package org.bric.processor;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.Sanselan;
import org.bric.gui.output.ProgressBarFrame;
import org.bric.imageEditParameters.OutputParameters;
import org.bric.imageEditParameters.ResizeParameters;
import org.bric.imageEditParameters.RotateParameters;
import org.bric.imageEditParameters.WatermarkParameters;
import org.bric.input.ImportedImage;
import org.bric.utils.PDFToImage;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageProcessHandler {

    private FileNameService fileNameService;

    DefaultListModel<ImportedImage> model;

    ResizeParameters resizeParameters;
    RotateParameters rotateParameters;
    WatermarkParameters watermarkParameters;
    private final OutputParameters outputParameters;
    ProgressBarFrame progressBar;

    int modelSize;

    private boolean preview = false;
    private int duplicateAction = Utils.NOT_SET;

    AtomicInteger numberingIndex;
    String outputExtension;
    String outputPath;

    //pdf
    Document document;

    public ImageProcessHandler(OutputParameters outputParameters, DefaultListModel<ImportedImage> model) {
        this.outputParameters = outputParameters;
        numberingIndex = new AtomicInteger(outputParameters.getNumberingStartIndex());
        outputExtension = outputParameters.getOutputFormat().toLowerCase();
        outputPath = outputParameters.getOutputPath();

        this.model = model;
        modelSize = model.size();

        this.fileNameService = new FileNameService(outputPath, outputExtension, outputParameters.getNumberingStartIndex(), model.size());
    }

    public static ImageProcessHandler createPreviewProcess(OutputParameters outputParameters, ImportedImage imageToPreview) {
        DefaultListModel<ImportedImage> previewModel = new DefaultListModel<>();
        previewModel.addElement(imageToPreview);

        ImageProcessHandler process = new ImageProcessHandler(outputParameters, previewModel);
        process.setPreview();

        return process;
    }

    private void setPreview() {
        this.preview = true;
    }

    public void setResizeParameters(ResizeParameters resizeParameters) {
        this.resizeParameters = resizeParameters;
    }

    public void setRotateParameters(RotateParameters rotateParameters) {
        this.rotateParameters = rotateParameters;
    }

    public void setWatermarkParameters(WatermarkParameters watermarkParameters) {
        this.watermarkParameters = watermarkParameters;
    }

    public void start() {
        if(preview){
            if (model.get(0).getImageType().equalsIgnoreCase("pdf")) {
                JOptionPane.showMessageDialog(null, "PDF preview is not supported yet!");
                return;
            }
        }

        progressBar = new ProgressBarFrame();
        progressBar.setVisible(true);
        progressBar.setImagesCount(modelSize);

        int processors;
        if (Utils.prefs.getInt("exportNumThreads", 0) == 0) {
            processors = Runtime.getRuntime().availableProcessors();
        } else {
            processors = Utils.prefs.getInt("exportNumThreads", 1);
        }

        final int step = (int) Math.ceil((double) modelSize / processors);

        if (outputExtension.equalsIgnoreCase("pdf")) {
            generatePDF();
        } else {
            for (int j = 0; j < modelSize; j += step) {
                startNewThread(progressBar, j, step);
            }
        }
    }



    public void startNewThread(final ProgressBarFrame progressBar, final int from, final int step) {
        new Thread(() -> {
            ResizeProcessor resizer = new ResizeProcessor(resizeParameters);
            RotateProcessor rotator = new RotateProcessor(rotateParameters);
            WatermarkProcessor watermarker = new WatermarkProcessor(watermarkParameters);

            for (int i = from; i < from + step; i++) {
                if (!progressBar.isVisible() || i >= modelSize) {
                    return;
                }
                String inputExtension = model.get(i).getImageType();

                if (inputExtension.equalsIgnoreCase("pdf")) {
                    if(outputExtension.equalsIgnoreCase("same as first")){
                        generateSeparatePDF(i);
                    } else {
                        pdfProcess(resizer, rotator, watermarker, i);
                    }
                } else {
                    bufferedImageProcess(resizer, rotator, watermarker, i, null, false);
                }

                progressBar.showProgress(model.get(i).getPath());
                progressBar.updateValue(true);
            }
        }).start();
    }

    private void bufferedImageProcess(ResizeProcessor resizer, RotateProcessor rotator, WatermarkProcessor watermarker, int imageNumber, BufferedImage currentImage, boolean pdfInput) {
        if (currentImage == null) {
            currentImage = Utils.loadImage(model.get(imageNumber).getPath());
        }
        if (resizeParameters.isEnabled()) {
            currentImage = resizer.process(currentImage);
        }

        if (rotateParameters.isEnabled()) {
            currentImage = rotator.process(currentImage);
        }

        if (watermarkParameters.isEnabled()) {
            currentImage = watermarker.process(currentImage);
        }

        if (outputExtension.equalsIgnoreCase("pdf") || pdfInput) {
            addImageToPDF(currentImage);
        } else {
            save(currentImage, fileNameService.generateFilePath(model.get(imageNumber)));
        }
    }

    private void pdfProcess(ResizeProcessor resizer, RotateProcessor rotator, WatermarkProcessor watermarker, int imageNumber) {
        ArrayList<BufferedImage> extractedImages = (ArrayList<BufferedImage>) PDFToImage.getBImagesFromPDF(model.get(imageNumber).getPath(), 1, Integer.MAX_VALUE);
        for (BufferedImage currentImage : extractedImages) {
            bufferedImageProcess(resizer, rotator, watermarker, imageNumber, currentImage, true);
        }
    }

    public void generateSeparatePDF(int i){
        ResizeProcessor resizer = new ResizeProcessor(resizeParameters);
        RotateProcessor rotator = new RotateProcessor(rotateParameters);
        WatermarkProcessor watermarker = new WatermarkProcessor(watermarkParameters);

        openDocument(fileNameService.generateFilePath(model.get(i)));

        pdfProcess(resizer, rotator, watermarker, i);

        document.close();
    }

    public void generatePDF() {
        ResizeProcessor resizer = new ResizeProcessor(resizeParameters);
        RotateProcessor rotator = new RotateProcessor(rotateParameters);
        WatermarkProcessor watermarker = new WatermarkProcessor(watermarkParameters);

        openDocument(fileNameService.generateFilePath(model.get(0)));

        String inputExtension;
        for (int i = 0; i < modelSize; i++) {
            inputExtension = getExtension(model.get(i).getPath());
            if (inputExtension.equalsIgnoreCase("pdf")) {
                pdfProcess(resizer, rotator, watermarker, i);
            } else {
                bufferedImageProcess(resizer, rotator, watermarker, i, null, false);
            }
            progressBar.showProgress(model.get(i).getPath());
            progressBar.updateValue(true);
        }
        document.close();
    }

    public void openDocument(String filepath) {
        try {
            document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(new File(filepath)));
            document.open();
        } catch (FileNotFoundException | DocumentException ex) {
            Logger.getLogger(ImageProcessHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addImageToPDF(BufferedImage image) {
        try {
            Rectangle pageSize = new Rectangle(image.getWidth(), image.getHeight());
            document.setPageSize(pageSize);
            document.newPage();
            Image iTextImage = Image.getInstance(image, null);
            Image pdfImage = Image.getInstance(iTextImage);
            pdfImage.setAbsolutePosition(0, 0);
            document.add(pdfImage);
        } catch (DocumentException | IOException ex) {
            Logger.getLogger(ImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
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

            //DPI
//            IIOMetadata data = writer.getDefaultImageMetadata(new ImageTypeSpecifier(imageForSave), iwp);
//            Element tree = (Element) data.getAsTree("javax_imageio_jpeg_image_1.0");
//            Element jfif = (Element) tree.getElementsByTagName("app0JFIF").item(0);
//            jfif.setAttribute("Xdensity", Integer.toString(outputParameters.getDpiHorizontal()));
//            jfif.setAttribute("Ydensity", Integer.toString(outputParameters.getDpiVertical()));
//            jfif.setAttribute("resUnits", "1"); // density is dots per inch	
//            data.setFromTree("javax_imageio_jpeg_image_1.0", tree);

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
