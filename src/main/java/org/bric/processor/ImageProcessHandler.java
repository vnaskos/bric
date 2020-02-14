package org.bric.processor;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.bric.core.input.model.ImportedImage;
import org.bric.core.input.model.InputType;
import org.bric.core.model.output.OutputParameters;
import org.bric.core.model.output.OutputType;
import org.bric.core.process.ImageSaveService;
import org.bric.gui.BricUI;
import org.bric.utils.Utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ImageProcessHandler {

    private final List<ImportedImage> inputQueue;
    private final OutputParameters outputParameters;
    private final List<ImageProcessor<?>> processors;

    private final FileNameService fileNameService;
    private final ImageSaveService imageSaveService;

    private BricUI.ProgressListener progressListener;

    public ImageProcessHandler(FileNameService fileNameService, OutputParameters outputParameters,
                               List<ImportedImage> inputList) {
        this(fileNameService, new ImageSaveService(), outputParameters, inputList);
    }

    public ImageProcessHandler(FileNameService fileNameService, ImageSaveService imageSaveService,
                               OutputParameters outputParameters, List<ImportedImage> inputList) {
        this.outputParameters = outputParameters;
        this.processors = new ArrayList<>();

        inputQueue = inputList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        this.fileNameService = fileNameService;
        this.imageSaveService = imageSaveService;
    }

    public void addProcessors(ImageProcessor<?>... processors) {
        Collections.addAll(this.processors, processors);
    }

    public List<CompletableFuture<String>> start() {
        List<CompletableFuture<String>> futures = new ArrayList<>();
        if (outputParameters.getOutputType() == OutputType.PDF) {
            futures.add(CompletableFuture.supplyAsync(() -> mergeInputToSinglePdf(inputQueue), Utils.getExecutorService()));
        } else {
            for (ImportedImage item : inputQueue) {
                futures.add(CompletableFuture.supplyAsync(() -> task(item), Utils.getExecutorService()));
            }
        }
        return futures;
    }

    private String task(final ImportedImage item) {
        if (item.getType() == InputType.PDF) {
            if (outputParameters.getOutputType() == OutputType.SAME_AS_FIRST) {
                mergeInputToSinglePdf(Collections.singletonList(item));
            } else {
                loadPdfPages(item.getPath(),
                        page -> saveImage(item, applyProcessors(page)));
            }
        } else {
            BufferedImage image = Utils.loadImage(item.getPath());
            saveImage(item, applyProcessors(image));
        }

        notifyFileProcessed(item);
        return item.getPath();
    }

    private BufferedImage applyProcessors(BufferedImage source) {
        BufferedImage currentImage = source;
        for (ImageProcessor<?> processor : processors) {
            if (processor.isEnabled()) {
                currentImage = processor.process(currentImage);
            }
        }
        return currentImage;
    }

    private void loadPdfPages(String pdfPath, Consumer<BufferedImage> consumer) {
        try (PDDocument doc = PDDocument.load(new File(pdfPath))) {
            PDFRenderer pdfRenderer = new PDFRenderer(doc);
            for (int page = 0; page < doc.getNumberOfPages(); page++) {
                consumer.accept(pdfRenderer.renderImageWithDPI(page, 300));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String mergeInputToSinglePdf(List<ImportedImage> input) {
        PDDocument document = new PDDocument();

        if (input.isEmpty()) {
            return "";
        }

        final ImportedImage firstItem = input.get(0);

        for (ImportedImage importedImage : input) {
            if (importedImage.getType() == InputType.PDF) {
                loadPdfPages(importedImage.getPath(),
                        page -> addImageToPDF(document, applyProcessors(page)));
            } else {
                BufferedImage image = Utils.loadImage(importedImage.getPath());
                image = applyProcessors(image);
                addImageToPDF(document, image);
            }
            notifyFileProcessed(importedImage);
        }

        try {
            document.save(fileNameService.generateFilePath(firstItem));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return firstItem.getPath();
    }

    private void notifyFileProcessed(ImportedImage importedImage) {
        if (progressListener == null) {
            return;
        }
        progressListener.finished(importedImage.getPath());
    }

    public void addImageToPDF(PDDocument doc, BufferedImage image) {
        try {
            PDPage page = new PDPage(new PDRectangle(image.getWidth(), image.getHeight()));
            doc.addPage(page);

            PDImageXObject pdImage = LosslessFactory.createFromImage(doc, image);

            PDPageContentStream contents = new PDPageContentStream(doc, page);

            contents.drawImage(pdImage, 0, 0);

            contents.close();
        } catch (IOException e){
            System.err.println("Exception while trying to create pdf document - " + e);
        }
    }

    private void saveImage(ImportedImage item, BufferedImage image) {
        String filename = fileNameService.generateFilePath(item);

        String nonCollidingFilename = fileNameService.preventNamingCollision(filename);
        if (nonCollidingFilename == null) {
            return;
        }

        imageSaveService.save(
                () -> image,
                new ImageSaveService.Parameters(
                        nonCollidingFilename,
                        outputParameters.getQuality(),
                        outputParameters.getOutputType() == OutputType.SAME_AS_FIRST
                                ? OutputType.valueOf(item.getType().name.toUpperCase())
                                : outputParameters.getOutputType()));
    }

    public void setProgressListener(BricUI.ProgressListener progressListener) {
        this.progressListener = progressListener;
    }
}
