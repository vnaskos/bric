package org.bric.processor;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.Sanselan;
import org.bric.core.input.model.ImportedImage;
import org.bric.core.input.model.InputType;
import org.bric.core.model.output.OutputParameters;
import org.bric.core.model.output.OutputType;
import org.bric.gui.inputOutput.ProgressBarFrame;
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
import java.util.Queue;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ImageProcessHandler {

    private final FileNameService fileNameService;

    private Queue<ImportedImage> inputQueue;

    ResizeParameters resizeParameters;
    RotateParameters rotateParameters;
    WatermarkParameters watermarkParameters;
    private final OutputParameters outputParameters;
    ProgressBarFrame progressBar;

    private ResizeProcessor resizeProcessor;
    private RotateProcessor rotateProcessor;
    private WatermarkProcessor watermarkProcessor;

    int modelSize;

    private boolean preview = false;
    private int duplicateAction = Utils.NOT_SET;

    public ImageProcessHandler(FileNameService fileNameService, OutputParameters outputParameters, List<ImportedImage> inputList) {
        this.outputParameters = outputParameters;

        inputQueue = inputList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ConcurrentLinkedQueue::new));

        modelSize = inputQueue.size();

        this.fileNameService = fileNameService;
    }

    public static ImageProcessHandler createPreviewProcess(OutputParameters outputParameters, ImportedImage imageToPreview) {
        FileNameService fileNameService = new FileNameService(outputParameters.getOutputPath(),
                outputParameters.getOutputType(), outputParameters.getNumberingStartIndex(), 1);
        ImageProcessHandler process = new ImageProcessHandler(fileNameService, outputParameters,
                Collections.singletonList(imageToPreview));
        process.setPreview();

        return process;
    }

    private void setPreview() {
        this.preview = true;
    }

    public void setResizeParameters(ResizeParameters resizeParameters) {
        this.resizeParameters = resizeParameters;
        this.resizeProcessor = new ResizeProcessor(resizeParameters);
    }

    public void setRotateParameters(RotateParameters rotateParameters) {
        this.rotateParameters = rotateParameters;
        this.rotateProcessor = new RotateProcessor(rotateParameters);
    }

    public void setWatermarkParameters(WatermarkParameters watermarkParameters) {
        this.watermarkParameters = watermarkParameters;
        this.watermarkProcessor = new WatermarkProcessor(watermarkParameters);
    }

    public void start() {
        if(preview){
            ImportedImage firstItem = inputQueue.poll();
            if (firstItem == null) {
                return;
            }
            if (firstItem.getType() == InputType.PDF) {
                JOptionPane.showMessageDialog(null, "PDF preview is not supported yet!");
                return;
            }
        }

        progressBar = new ProgressBarFrame();
        progressBar.setVisible(true);
        progressBar.setImagesCount(modelSize);

        if (outputParameters.getOutputType() == OutputType.PDF) {
            generatePDF();
        } else {
            ExecutorService executorService = getExecutorService();

            while (!inputQueue.isEmpty()) {
                executorService.submit(task(inputQueue.poll()));
            }
        }
    }

    private ExecutorService getExecutorService() {
        ExecutorService executorService;
        if (Utils.prefs.getInt("exportNumThreads", 0) == 0) {
            executorService = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());
        } else {
            executorService = Executors.newWorkStealingPool(Utils.prefs.getInt("exportNumThreads", 1));
        }
        return executorService;
    }

    private Callable<Void> task(final ImportedImage item) {
        return () -> {
            if (!progressBar.isVisible()) {
                return null;
            }

            if (item.getType() == InputType.PDF) {
                if(outputParameters.getOutputType() == OutputType.SAME_AS_FIRST) {
                    generateSeparatePDF(item);
                } else {
                    pdfProcess(null, item);
                }
            } else {
                bufferedImageProcess(null, item, null);
            }

            progressBar.showProgress(item.getPath());
            progressBar.updateValue(true);

            return null;
        };
    }

    private void bufferedImageProcess(PDDocument doc, ImportedImage importedImage, BufferedImage currentImage) {
        if (currentImage == null) {
            currentImage = Utils.loadImage(importedImage.getPath());
        }
        if (resizeParameters != null && resizeParameters.isEnabled()) {
            currentImage = resizeProcessor.process(currentImage);
        }

        if (rotateParameters != null && rotateParameters.isEnabled()) {
            currentImage = rotateProcessor.process(currentImage);
        }

        if (watermarkParameters != null && watermarkParameters.isEnabled()) {
            currentImage = watermarkProcessor.process(currentImage);
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

    public void generateSeparatePDF(ImportedImage importedImage){
        PDDocument document = new PDDocument();

        pdfProcess(document, importedImage);

        try {
            document.save(fileNameService.generateFilePath(Objects.requireNonNull(importedImage)));
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

    public void generatePDF() {
        PDDocument document = new PDDocument();

        final ImportedImage firstItem = inputQueue.peek();

        if (firstItem == null) {
            return;
        }

        for (ImportedImage importedImage : inputQueue) {
            if (importedImage.getType() == InputType.PDF) {
                pdfProcess(document, importedImage);
            } else {
                bufferedImageProcess(document, importedImage, null);
            }
            progressBar.showProgress(importedImage.getPath());
            progressBar.updateValue(true);
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

        if (duplicateAction == Utils.NOT_SET || duplicateAction == Utils.OVERWRITE || duplicateAction == Utils.SKIP || duplicateAction == Utils.ADD) {

            Object[] selectionValues = {"Overwrite", "Overwrite All", "Skip", "Skip All", "Add", "Add All"};

            String initialSelection = selectionValues[4].toString();

            Object selection;

            do {
            selection = JOptionPane.showInputDialog(
                    null, String.format("This image\n%s\n already exists on the output folder", file),
                    "Warning Duplicate", JOptionPane.QUESTION_MESSAGE, null, selectionValues, initialSelection);
            }while(selection == null);
            int answer = 0;
            int i = 0;

            for (Object value : selectionValues) {
                if (selection == value.toString()) {
                    answer = i;
                }
                i++;
            }
            
            switch (answer) {
                case 0:
                    duplicateAction = Utils.OVERWRITE;
                    break;
                case 1:
                    duplicateAction = Utils.OVERWRITE_ALL;
                    break;
                case 2:
                    duplicateAction = Utils.SKIP;
                    break;
                case 3:
                    duplicateAction = Utils.SKIP_ALL;
                    break;
                case 5:
                    duplicateAction = Utils.ADD_ALL;
                    break;
                default:
                    duplicateAction = Utils.ADD;
            }
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
        return duplicateAction == Utils.SKIP || duplicateAction == Utils.SKIP_ALL;
    }
    
    public void previewProcess(BufferedImage image){
        try {
            File temporary = File.createTempFile("preview", ".jpg");
            temporary.deleteOnExit();
            ImageIO.write(image,"jpg",temporary);
            Desktop.getDesktop().open(temporary);
        } catch (IOException ex) {
            Logger.getLogger(ImageProcessHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public void save(BufferedImage imageForSave, String newFilepath) {
        if(preview){
            previewProcess(imageForSave);
            return;
        }

        if (fileExistsCheck(newFilepath)) {
            return;
        }

        if( (duplicateAction == Utils.ADD || duplicateAction == Utils.ADD_ALL) && new File(newFilepath).exists()){
            newFilepath = duplicateAddAction(newFilepath);
        }
        
        File outputFile = new File(newFilepath);
        String extension = getExtension(outputFile.getPath());
        try {
            if ((extension.equalsIgnoreCase("jpg")) || (extension.equalsIgnoreCase("jpeg"))) {
                saveJPG(imageForSave, outputFile);
            } else if (extension.equalsIgnoreCase("gif")) {
                ImageFormat format = ImageFormat.IMAGE_FORMAT_GIF;
                Sanselan.writeImage(imageForSave, outputFile, format, null);
            } else if (extension.equalsIgnoreCase("tif")) {
                ImageFormat format = ImageFormat.IMAGE_FORMAT_TIFF;
                Sanselan.writeImage(imageForSave, outputFile, format, null);
            } else if(extension.equalsIgnoreCase("pgm")){
                ImageFormat format = ImageFormat.IMAGE_FORMAT_PGM;
                Sanselan.writeImage(imageForSave, outputFile, format, null);
            } else if(extension.equalsIgnoreCase("ppm")){
                ImageFormat format = ImageFormat.IMAGE_FORMAT_PPM;
                Sanselan.writeImage(imageForSave, outputFile, format, null);
            } else if(extension.equalsIgnoreCase("pnm")){
                ImageFormat format = ImageFormat.IMAGE_FORMAT_PNM;
                Sanselan.writeImage(imageForSave, outputFile, format, null);
            } else if (extension.equalsIgnoreCase("pbm")) {
                ImageFormat format = ImageFormat.IMAGE_FORMAT_PBM;
                Sanselan.writeImage(imageForSave, outputFile, format, null);
            } else if (extension.equalsIgnoreCase("bmp")) {
                ImageFormat format = ImageFormat.IMAGE_FORMAT_BMP;
                Sanselan.writeImage(imageForSave, outputFile, format, null);
            } else if(extension.equalsIgnoreCase("png")){
                ImageFormat format = ImageFormat.IMAGE_FORMAT_PNG;
                Sanselan.writeImage(imageForSave, outputFile, format, null);
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
}
