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
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageProcessHandler {

    DefaultListModel model;
    
    ResizeParameters resizeParameters;
    RotateParameters rotateParameters;
    WatermarkParameters watermarkParameters;
    OutputParameters outputParameters;
    ProgressBarFrame progressBar;
    
    int modelSize;
    
    Stack<Integer> numsStack;
    
    static boolean preview = false;
    static int duplicateAction;
    
    int numberingIndex;
    String outputExtension;
    String outputPath;
    
    //pdf
    Document document;
    
    public ImageProcessHandler(DefaultListModel model) {
        this.model = model;
        modelSize = model.size();
        
        duplicateAction = Utils.NOT_SET;
        
    }
    
    //preview
    public ImageProcessHandler(ImportedImage image) {
        
        duplicateAction = Utils.NOT_SET;
        model = new DefaultListModel();
        model.addElement(image);
        
        preview = true;
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

    public void setOutputParameters(OutputParameters outputParameters) {
        this.outputParameters = outputParameters;
        
        numberingIndex = outputParameters.getNumberingStartIndex();
        outputExtension = outputParameters.getOutputFormat().toLowerCase();
        outputPath = outputParameters.getOutputPath();
    }

    public void start() {
        if(preview){
            if (((ImportedImage) model.get(0)).getImageType().equalsIgnoreCase("pdf")) {
                JOptionPane.showMessageDialog(null, "PDF preview is not supported yet!");
                return;
            }
        }
        
        progressBar = new ProgressBarFrame();
        progressBar.setVisible(true);
        progressBar.setImagesCount(modelSize);
        numsStack = new Stack<Integer>();
        if(outputPath.contains("#")){
            HashSet<Integer> existingNumsHash = Utils.getExistingNumsHash(outputPath);
            for(int i = outputParameters.getNumberingStartIndex(); true; i++){
                if(!existingNumsHash.contains(i)){
                    numsStack.add(i);
                }
                if(numsStack.size() == modelSize){
                    break;
                }
            }
        }

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                ResizeProcessor resizer = new ResizeProcessor(resizeParameters);
                RotateProcessor rotator = new RotateProcessor(rotateParameters);
                WatermarkProcessor watermarker = new WatermarkProcessor(watermarkParameters);

                for (int i = from; i < from + step; i++) {
                    if (!progressBar.isVisible() || i >= modelSize) {
                        return;
                    }
                    String inputExtension = ((ImportedImage) model.get(i)).getImageType();

                    if (inputExtension.equalsIgnoreCase("pdf")) {
                        if(outputExtension.equalsIgnoreCase("same as first")){
                            generateSeparatePDF(i);
                        } else {
                            pdfProcess(resizer, rotator, watermarker, i, true);
                        }
                    } else {
                        bufferedImageProcess(resizer, rotator, watermarker, i, null, false);
                    }

                    progressBar.showProgress(((ImportedImage) model.get(i)).getPath());
                    progressBar.updateValue(true);
                }
            }
        }).start();
    }

    private void bufferedImageProcess(ResizeProcessor resizer, RotateProcessor rotator, WatermarkProcessor watermarker, int imageNumber, BufferedImage currentImage, boolean pdfInput) {     
        if (currentImage == null) {
            currentImage = Utils.loadImage(((ImportedImage) model.get(imageNumber)).getPath());
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
            save(currentImage, applyFileNameMasks(outputPath, ((ImportedImage) model.get(imageNumber))));
        }
    }

    private void pdfProcess(ResizeProcessor resizer, RotateProcessor rotator, WatermarkProcessor watermarker, int imageNumber, boolean pdfInput) {
        ArrayList<BufferedImage> extractedImages = (ArrayList<BufferedImage>) PDFToImage.getBImagesFromPDF(((ImportedImage) model.get(imageNumber)).getPath(), 1, Integer.MAX_VALUE);
        for (BufferedImage currentImage : extractedImages) {
            bufferedImageProcess(resizer, rotator, watermarker, imageNumber, currentImage, pdfInput);
        }
    }
    
    public void generateSeparatePDF(int i){
        ResizeProcessor resizer = new ResizeProcessor(resizeParameters);
        RotateProcessor rotator = new RotateProcessor(rotateParameters);
        WatermarkProcessor watermarker = new WatermarkProcessor(watermarkParameters);
        
        openDocument(applyFileNameMasks(outputPath, ((ImportedImage) model.get(i))));
        
        pdfProcess(resizer, rotator, watermarker, i, true);
        
        document.close();
    }

    public void generatePDF() {
        ResizeProcessor resizer = new ResizeProcessor(resizeParameters);
        RotateProcessor rotator = new RotateProcessor(rotateParameters);
        WatermarkProcessor watermarker = new WatermarkProcessor(watermarkParameters);
        
        openDocument(applyFileNameMasks(outputPath, ((ImportedImage) model.get(0))));

        String inputExtension;
        for (int i = 0; i < modelSize; i++) {
            inputExtension = getExtension(((ImportedImage) model.get(i)).getPath());
            if (inputExtension.equalsIgnoreCase("pdf")) {
                pdfProcess(resizer, rotator, watermarker, i, true);
            } else {
                bufferedImageProcess(resizer, rotator, watermarker, i, null, false);
            }
            progressBar.showProgress(((ImportedImage) model.get(i)).getPath());
            progressBar.updateValue(true);
        }
        document.close();
    }

    public void openDocument(String filepath) {
        try {
            document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(new File(filepath)));
            document.open();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ImageProcessHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
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
        } catch (DocumentException ex) {
            Logger.getLogger(ImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String getExtension(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1);
        return extension;
    }
    
    public String applyFileNameMasks(String filepath, ImportedImage currentImage) {

        if (outputPath.substring(outputPath.lastIndexOf(Utils.FS) + 1).equals("")) {
            filepath = (filepath.lastIndexOf(Utils.FS) == filepath.length() - 1) ? filepath : filepath + Utils.FS;
            filepath = filepath + "%F";
        }

        if (filepath.contains("^P")) {
            if (!filepath.contains("^P" + Utils.FS)) {
                filepath = filepath.replace("^P", "^P" + Utils.FS);
            }
            if (!filepath.contains("%") && !filepath.contains("*") && !filepath.contains("#")) {
                filepath = filepath + "%F";
            }
        }

        filepath = filepath.replaceAll("\\*", Integer.toString(numberingIndex));

        Calendar cal = Calendar.getInstance();
        filepath = filepath.replaceAll("%D", cal.get(Calendar.DATE) + "");
        filepath = filepath.replaceAll("%M", (cal.get(Calendar.MONTH) + 1) + "");
        filepath = filepath.replaceAll("%Y", cal.get(Calendar.YEAR) + "");

        filepath = filepath.replaceAll("%F", currentImage.getName());
        filepath = filepath.replace("^P", currentImage.getPath().substring(0, currentImage.getPath().lastIndexOf(Utils.FS)));

        String extension = outputExtension;
        if (outputExtension.equals("same as first")) {
            for(String ext : Utils.supportedOutputExtensions){
                if(ext.equalsIgnoreCase(currentImage.getImageType())){
                    extension = currentImage.getImageType();
                    break;
                } else {
                    extension = Utils.prefs.get("defaultFileType", "jpg");
                }
            }
        }
        
        if (filepath.contains("#")) {
//            filepath = applySpecialFileMask(filepath, extension);
            filepath = filepath.replaceAll("#", String.valueOf(numsStack.pop()));
        }
        
        filepath += '.' + extension;

        return filepath;
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

    synchronized public static void duplicatePane(String file) {

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
                case 4:
                    duplicateAction = Utils.ADD;
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
        if(duplicateAction == Utils.SKIP || duplicateAction == Utils.SKIP_ALL){
            return true;
        }
        
        return false;
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
        
        numberingIndex++;

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
        }
    }
    
    private void saveJPG(BufferedImage imageForSave, File outputfile){
        try {
            Iterator iter = ImageIO.getImageWritersByFormatName("jpeg");
            ImageWriter writer = (ImageWriter) iter.next();
            ImageWriteParam iwp = writer.getDefaultWriteParam();
            iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            iwp.setCompressionQuality((float) outputParameters.getQuality());   // a float between 0 and 1
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
