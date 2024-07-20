package org.bric.core.process;

import org.bric.core.input.model.ImportedImage;
import org.bric.core.input.model.InputType;
import org.bric.core.model.output.OutputParameters;
import org.bric.core.model.output.OutputType;
import org.bric.gui.BricUI;
import org.bric.utils.Utils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImageProcessHandler {

    private final List<ImportedImage> inputQueue;
    private final OutputParameters outputParameters;
    private final List<ImageProcessor<?>> processors;

    private final FileNameService fileNameService;
    private final ImageService imageService;
    private final PdfService pdfService;

    private BricUI.ProgressListener progressListener;

    public ImageProcessHandler(FileNameService fileNameService, OutputParameters outputParameters, List<ImportedImage> inputList) {
        this(fileNameService, outputParameters, new DefaultImageService(), new PdfboxPdfService(), inputList);
    }

    public ImageProcessHandler(FileNameService fileNameService, OutputParameters outputParameters, ImageService imageService, PdfService pdfService,
                               List<ImportedImage> inputList) {
        this.outputParameters = outputParameters;
        this.imageService = imageService;
        this.pdfService = pdfService;
        this.processors = new ArrayList<>();

        inputQueue = inputList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        this.fileNameService = fileNameService;
    }

    public void addProcessors(ImageProcessor<?>... processors) {
        Collections.addAll(this.processors, processors);
    }

    public List<CompletableFuture<Void>> start() {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        if (outputParameters.getOutputType() == OutputType.PDF) {
            futures.add(CompletableFuture.runAsync(() -> mergeInputToSinglePdf(inputQueue), Utils.getExecutorService()));
        } else {
            for (ImportedImage item : inputQueue) {
                futures.add(CompletableFuture.runAsync(() -> task(item), Utils.getExecutorService()));
            }
        }
        return futures;
    }

    private void task(final ImportedImage item) {
        if (item.getType() == InputType.PDF && outputParameters.getOutputType() == OutputType.SAME_AS_FIRST) {
            mergeInputToSinglePdf(Collections.singletonList(item));
        } else if (item.getType() == InputType.PDF) {
            pdfService.readAsImages(item.getPath(), 0, Integer.MAX_VALUE).stream()
                .map(this::applyProcessors)
                .forEach(image -> saveImage(item, image));
        } else {
            saveImage(item, applyProcessors(imageService.load(item.getPath())));
        }

        notifyFileProcessed(item);
    }

    private void mergeInputToSinglePdf(List<ImportedImage> inputList) {
        String filePath = fileNameService.generateFilePath(inputList.get(0));
        String nonCollidingFilename = fileNameService.preventNamingCollision(filePath);
        if (nonCollidingFilename == null) {
            return;
        }

        Stream<BufferedImage> inputStream = inputList.stream().flatMap(importedImage -> {
            List<BufferedImage> images = new ArrayList<>();
            if (importedImage.getType() == InputType.PDF) {
                pdfService.readAsImages(importedImage.getPath(), 0, Integer.MAX_VALUE).stream()
                    .map(this::applyProcessors)
                    .forEach(images::add);
            } else {
                BufferedImage image = imageService.load(importedImage.getPath());
                images.add(applyProcessors(image));
            }
            notifyFileProcessed(importedImage);
            return images.stream();
        });

        pdfService.mergeToPdf(inputStream, nonCollidingFilename);
    }

    private BufferedImage applyProcessors(BufferedImage source) {
        Supplier<BufferedImage> resultSupplier = processors.stream()
            .filter(ImageProcessor::isEnabled)
            .reduce(
                () -> source,
                (supplier, processor) -> processor.process(supplier),
                (s1, s2) -> s1);

        return resultSupplier.get();
    }

    private void notifyFileProcessed(ImportedImage importedImage) {
        if (progressListener == null) {
            return;
        }
        progressListener.finished(importedImage.getPath());
    }

    private void saveImage(ImportedImage item, BufferedImage image) {
        String filename = fileNameService.generateFilePath(item);

        String nonCollidingFilename = fileNameService.preventNamingCollision(filename);
        if (nonCollidingFilename == null) {
            return;
        }

        imageService.save(
                () -> image,
                new ImageSaveParameters(
                        nonCollidingFilename,
                        outputParameters.getQuality(),
                        outputParameters.getOutputType() == OutputType.SAME_AS_FIRST
                                ? OutputType.valueOf(item.getType().type.toUpperCase())
                                : outputParameters.getOutputType()));
    }

    public void setProgressListener(BricUI.ProgressListener progressListener) {
        this.progressListener = progressListener;
    }
}
