package org.bric.processor;

import ij.ImagePlus;
import ij.io.FileSaver;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.bric.core.input.model.ImportedImage;
import org.bric.core.input.model.InputType;
import org.bric.core.model.DuplicateAction;
import org.bric.core.model.output.OutputParameters;
import org.bric.core.model.output.OutputType;
import org.bric.gui.BricUI;
import org.bric.imageEditParameters.ResizeParameters;
import org.bric.imageEditParameters.RotateParameters;
import org.bric.imageEditParameters.WatermarkParameters;
import org.bric.utils.Utils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ImageProcessHandler {

    private final List<ImportedImage> inputQueue;
    private final OutputParameters outputParameters;
    private final List<ImageProcessor<?>> processors;

    private final FileNameService fileNameService;

    private DuplicateAction duplicateAction = DuplicateAction.NOT_SET;
    private BricUI.ProgressListener progressListener;

    public ImageProcessHandler(FileNameService fileNameService, OutputParameters outputParameters, List<ImportedImage> inputList) {
        this.outputParameters = outputParameters;
        this.processors = new ArrayList<>();

        inputQueue = inputList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        this.fileNameService = fileNameService;

    }

    public static void preview(ImportedImage imageToPreview, ImageProcessor<?>... processors) {
        File temporary;
        try {
            temporary = File.createTempFile("preview", ".jpg");
            temporary.deleteOnExit();
        } catch (IOException ex) {
            Logger.getLogger(ImageProcessHandler.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        OutputParameters outputParameters = new OutputParameters(
                temporary.getAbsolutePath().replace(".jpg", ""),
                OutputType.JPG, 1, 1);
        FileNameService fileNameService = new FileNameService(
                outputParameters.getOutputPath(), outputParameters.getOutputType(),
                outputParameters.getNumberingStartIndex(), 1);
        ImageProcessHandler handler = new ImageProcessHandler(fileNameService, outputParameters,
                Collections.singletonList(imageToPreview));
        handler.addProcessors(processors);
        handler.preview(temporary);
    }

    private void preview(File temporary) {
        if (inputQueue.isEmpty()) {
            return;
        }

        ImportedImage item = inputQueue.get(0);

        if (item.getType() == InputType.PDF) {
            JOptionPane.showMessageDialog(null, "PDF preview is not supported yet!");
            return;
        }

        duplicateAction = DuplicateAction.ALWAYS_OVERWRITE;

        try {
            task(item);
            Desktop.getDesktop().open(temporary);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addProcessors(ImageProcessor<?>... processors) {
        Collections.addAll(this.processors, processors);
    }

    public void setResizeParameters(ResizeParameters resizeParameters) {
        processors.add(new ResizeProcessor(resizeParameters));
    }

    public void setRotateParameters(RotateParameters rotateParameters) {
        processors.add(new RotateProcessor(rotateParameters));
    }

    public void setWatermarkParameters(WatermarkParameters watermarkParameters) {
        processors.add(new WatermarkProcessor(watermarkParameters));
    }

    public List<CompletableFuture<String>> start() {
        List<CompletableFuture<String>> futures = new ArrayList<>();
        if (outputParameters.getOutputType() == OutputType.PDF) {
            generatePDF(inputQueue);
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
                generatePDF(Collections.singletonList(item));
            } else {
                pdfProcess(null, item);
            }
        } else {
            bufferedImageProcess(null, item, null);
        }

        notifyFileProcessed(item);
        return item.getPath();
    }

    private void bufferedImageProcess(PDDocument doc, ImportedImage importedImage, BufferedImage currentImage) {
        if (currentImage == null) {
            currentImage = Utils.loadImage(importedImage.getPath());
        }

        for (ImageProcessor<?> processor : processors) {
            if (processor.isEnabled()) {
                currentImage = processor.process(currentImage);
            }
        }

        if (outputParameters.getOutputType() == OutputType.PDF ||
                (outputParameters.getOutputType() == OutputType.SAME_AS_FIRST && importedImage.getType() == InputType.PDF)) {
            addImageToPDF(doc, currentImage);
        } else {
            save(currentImage, fileNameService.generateFilePath(importedImage));
        }
    }

    private void pdfProcess(PDDocument document, ImportedImage importedImage) {
        try (PDDocument doc = PDDocument.load(new File(importedImage.getPath()))) {

            PDFRenderer pdfRenderer = new PDFRenderer(doc);
            for (int page = 0; page < doc.getNumberOfPages(); page++) {
                BufferedImage image = pdfRenderer.renderImageWithDPI(page, 300);
                bufferedImageProcess(document, importedImage, image);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generatePDF(List<ImportedImage> input) {
        PDDocument document = new PDDocument();

        if (input.isEmpty()) {
            return;
        }

        final ImportedImage firstItem = input.get(0);

        for (ImportedImage importedImage : input) {
            if (importedImage.getType() == InputType.PDF) {
                pdfProcess(document, importedImage);
            } else {
                bufferedImageProcess(document, importedImage, null);
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

    private String getExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    private String applySpecialFileMask(String filepath, String extension){
        int index = outputParameters.getNumberingStartIndex();
        String filepathBackup = filepath;
        do {
            filepath = filepathBackup;
            filepath = filepath.replaceAll("#", Integer.toString(index));
            index++;
        } while (new File(filepath + '.' + extension).exists());
        return filepath;
    }

    synchronized public void duplicatePane(String file) {

        if (duplicateAction == DuplicateAction.NOT_SET ||
                duplicateAction == DuplicateAction.OVERWRITE ||
                duplicateAction == DuplicateAction.SKIP ||
                duplicateAction == DuplicateAction.RENAME) {

            Object[] selectionValues = {
                    DuplicateAction.OVERWRITE, DuplicateAction.ALWAYS_OVERWRITE,
                    DuplicateAction.SKIP, DuplicateAction.ALWAYS_SKIP,
                    DuplicateAction.RENAME, DuplicateAction.ALWAYS_RENAME};

            Object selection;

            do {
            selection = JOptionPane.showInputDialog(
                    null, String.format("This image\n%s\n already exists on the output folder", file),
                    "Warning Duplicate", JOptionPane.QUESTION_MESSAGE,
                    null, selectionValues, DuplicateAction.RENAME);
            } while(selection == null);

            duplicateAction = (DuplicateAction) selection;
        }
    }
    
    private String duplicateAddAction(String filename) {
        String file = filename.substring(0, filename.lastIndexOf('.'));
        file += "(#)";
        String extension = getExtension(filename);
        file = applySpecialFileMask(file, extension);
        return file + "." + extension;
    }
    
    public boolean fileExistsCheck(String newFilepath) {
        File virtualFile = new File(newFilepath);
        if (virtualFile.exists()) {
            duplicatePane(newFilepath);
        }
        return duplicateAction == DuplicateAction.SKIP || duplicateAction == DuplicateAction.ALWAYS_SKIP;
    }

    private void save(BufferedImage imageForSave, String newFilepath) {
        if (fileExistsCheck(newFilepath)) {
            return;
        }

        if( (duplicateAction == DuplicateAction.ADD || duplicateAction == DuplicateAction.ALWAYS_RENAME)
                && new File(newFilepath).exists()) {
            newFilepath = duplicateAddAction(newFilepath);
        }
        
        File outputFile = new File(newFilepath);
        String extension = getExtension(outputFile.getPath());
        try {
            ImagePlus img = new ImagePlus("bric-image", imageForSave);
            FileSaver fileSaver = new FileSaver(img);

            if ((extension.equalsIgnoreCase("jpg")) || (extension.equalsIgnoreCase("jpeg"))) {
                saveJPG(imageForSave, outputFile);
            } else if (extension.equalsIgnoreCase("gif")) {
                fileSaver.saveAsGif(newFilepath);
            } else if (extension.equalsIgnoreCase("tif") || extension.equalsIgnoreCase("tiff")) {
                fileSaver.saveAsTiff(newFilepath);
            } else if(extension.equalsIgnoreCase("pgm")){
                fileSaver.saveAsPgm(newFilepath);
            } else if (extension.equalsIgnoreCase("bmp")) {
                fileSaver.saveAsBmp(newFilepath);
            } else if(extension.equalsIgnoreCase("png")){
                fileSaver.saveAsPng(newFilepath);
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageProcessHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void saveJPG(BufferedImage imageForSave, File outputfile){
        try {
            Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
            ImageWriter writer = iter.next();
            ImageWriteParam iwp = writer.getDefaultWriteParam();
            iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            iwp.setCompressionQuality(outputParameters.getQuality());   // a float between 0 and 1
            FileImageOutputStream output = new FileImageOutputStream(outputfile);
            writer.setOutput(output);

            IIOImage image2 = new IIOImage(imageForSave, null, null);
            writer.write(null, image2, iwp);
            output.flush();
            writer.dispose();
            output.close();
        } catch (IOException ex) {
            Logger.getLogger(ImageProcessHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setProgressListener(BricUI.ProgressListener progressListener) {
        this.progressListener = progressListener;
    }
}
