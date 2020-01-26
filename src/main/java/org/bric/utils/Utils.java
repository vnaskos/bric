/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bric.utils;

import ij.IJ;
import ij.ImagePlus;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.bric.input.GenerateThumbnail;
import org.bric.input.ImportedImage;

/**
 *
 * @author vasilis
 */
public class Utils {

    public static final String FS = System.getProperty("file.separator");
    public static final int REPLACE_ALL = 0,
            REPLACE = 1,
            SKIP_ALL = 2,
            SKIP = 3,
            NOT_SET = 4,
            OVERWRITE = 5,
            OVERWRITE_ALL = 6,
            ADD = 1,
            ADD_ALL = 0;
    
    public static Preferences prefs = Preferences.userRoot();
    
    public static String[] supportedOutputExtensions = 
            {"jpg", "jpeg", "png", "bmp", "pnm", "ppm", "pgm", "pbm", "tif", "tiff", "gif", "pdf"};
    
    public static String[] supportedInputExtensions = 
            {"jpg", "jpeg", "png", "bmp", "tiff", "tif", "gif", "psd", "pnm", "ppm", "pgm", "pbm", "wbmp", "pdf"};
    
    public static void setFileChooserProperties(JFileChooser chooser) {
        chooser.setMultiSelectionEnabled(true);
        //jFileChooser1.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        FileNameExtensionFilter[] filters = {
            new FileNameExtensionFilter("JPEG Image(*.jpg, *.jpeg)", "JPG", "JPEG"),
            new FileNameExtensionFilter("PNG Images(*.png)", "PNG"),
            new FileNameExtensionFilter("BMP Images(*.bmp)", "BMP"),
            new FileNameExtensionFilter("TIFF Images(*.tiff, *.tif)", "TIFF", "TIF"),
            new FileNameExtensionFilter("GIF Images(*.gif)", "GIF"),
            new FileNameExtensionFilter("Photoshop Files(*.psd)", "PSD"),
            new FileNameExtensionFilter("PNM Images(*.pnm)", "PNM"),
            new FileNameExtensionFilter("PPM Images(*.ppm)", "PPM"),
            new FileNameExtensionFilter("PGM Images(*.pgm)", "PGM"),
            new FileNameExtensionFilter("PBM Images(*.pbm)", "PBM"),
            new FileNameExtensionFilter("WBMP Images(*.wbmp)", "WBMP"),
            new FileNameExtensionFilter("PDF Files(*.pdf)", "PDF"),
            new FileNameExtensionFilter("All supported file types", "JPG", "JPEG", "PNG", "GIF", "BMP", "TIFF", "TIF", "PNM", "PPM", "PGM", "PBM", "WBMP", "PSD", "PDF")
        };

        for (FileNameExtensionFilter filter : filters) {
            chooser.setFileFilter(filter);
        }
    }
    
    public static void outOfMemoryErrorMessage(){
        JOptionPane.showMessageDialog(null, 
                "Out of memory!\nRerun the program with -Xmx argument, using more than " + Runtime.getRuntime().maxMemory()/1000000 + "m",
                "Out of memory error.",
                JOptionPane.ERROR_MESSAGE);
    }
    
    public static BufferedImage loadImage(String filename) {
        BufferedImage image = librarySwitch(filename, 0);
        if(image == null){
            image = librarySwitch(filename, 1);
        }
        if(image == null){
            image = librarySwitch(filename, 2);
        }
        return image;
    }
    
    public static BufferedImage librarySwitch(String filename, int library) {
        try {
            switch (library){
                case 0:{    
                    BufferedImage newImage = Sanselan.getBufferedImage(new File(filename));
                    return newImage;
                } case 1:{
                    BufferedImage newImage = ImageIO.read(new File(filename));
                    return newImage;
                } case 2:{
                    ImagePlus imp = IJ.openImage(filename);
                    BufferedImage newImage = BufferedImageCreator.create(imp, 0);
//                    ColorProcessor cp = (ColorProcessor) imp.getProcessor();
//                    int[] pixels = (int[]) cp.getPixels();
//                    BufferedImage bimg = new BufferedImage(cp.getWidth(), cp.getHeight(), BufferedImage.TYPE_INT_RGB);
//                    bimg.setRGB(0, 0, cp.getWidth(), cp.getHeight(), pixels, 0, cp.getWidth());
                    return newImage;
                } 
            }
        } catch (ImageReadException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        } catch (OutOfMemoryError ex) {
            if(library == 2) {
                outOfMemoryErrorMessage();
            }
            System.gc();
            return null;
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static void centerWindow(Window frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }
    
    public static void setMetadataThumbnail(ImportedImage image, boolean metadata, boolean thumbnail){
        if (thumbnail) {
            if (image.getImageType().equalsIgnoreCase("PDF")) {
                image.setThumbnailImageIcon(new ImageIcon(GenerateThumbnail.generate(image.getPath(), 125, 125)));
            } else {
                BufferedImage importImage = Utils.loadImage(image.getPath());
                if (importImage != null) {
                    image.setThumbnailImageIcon(new ImageIcon(GenerateThumbnail.generate(importImage, 125, 125)));
                } else {
                    image.setCorrupted(true);
                }
            }
        }

        if (metadata) {
            try {
                image.setSize(new File(image.getPath()).length());
                if (image.getImageType().equalsIgnoreCase("PDF")) {
                    image.setDimensions("unknown");
                } else {
                    ImageInputStream in = ImageIO.createImageInputStream(new File(image.getPath()));
                    try {
                        final Iterator readers = ImageIO.getImageReaders(in);
                        if (readers.hasNext()) {
                            ImageReader reader = (ImageReader) readers.next();
                            try {
                                reader.setInput(in);
                                image.setDimensions(reader.getWidth(0) + "x" + reader.getHeight(0));
                            } finally {
                                reader.dispose();
                            }
                        }
                        if (image.getDimensions() == null || image.getDimensions().equals("unknown")) {
                            throw new Exception();
                        }
                    } finally {
                        if (in != null) {
                            in.close();
                        }
                    }
                }
            } catch (Exception e) {
                BufferedImage importImage = Utils.loadImage(image.getPath());
                if (importImage != null) {
                    image.setDimensions(importImage.getWidth() + "x" + importImage.getHeight());
                } else {
                    image.setCorrupted(true);
                }
            }
        }

        if (image.getDimensions() == null) {
            image.setDimensions("unknown");
        }
    }
    
    public static HashSet<Integer> getExistingNumsHash(String outputPath) {
        String[] outputPathArray = outputPath.split(Pattern.quote(File.separator));
        int outputPathSize = outputPathArray.length;
        String outputName = outputPathArray[outputPathSize-1];
        HashSet<Integer> numsHashset = new HashSet<Integer>();
        File outputDir = new File(outputPath.substring(0, outputPath.lastIndexOf(System.getProperty("file.separator"))));
        List<String> list = Arrays.asList(outputDir.list());
        for (String file : list) {
            if (file.matches(createRegex(outputName))) {
                Pattern pattern = Pattern.compile("[0-9]+");
                Matcher m = pattern.matcher(file);
                while (m.find()) {
                    numsHashset.add(Integer.parseInt(m.group()));
                }
            }
        }
        return numsHashset;
    }
    
    public static String createRegex(String s) {  
        StringBuilder b = new StringBuilder();  
        for(int i=0; i<s.length(); ++i) {  
            char ch = s.charAt(i);  
            if ("\\.^$|?*+[]{}()-_".indexOf(ch) != -1){
                b.append('\\').append(ch);    
            }
            else {  
                b.append(ch);
            }
        }  
        b.append("\\.(jpg|jpeg|png|gif|tif|tiff|bmp|pdf|wbmp|pbm|pgm|ppm|pnm|psd|JPG|JPEG|PNG|GIF|TIF|TIFF|BMP|PDF|WBMP|PBM|PGM|PPM|PNM|PSD)");
        return b.toString().replaceAll("#", "[0-9]+");  
    }  
}
