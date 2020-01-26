/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bric.input;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import org.bric.utils.PDFToImage;
import org.bric.utils.Utils;

/**
 *
 * @author vasilis
 */
public class GenerateThumbnail {
    
    public static BufferedImage generate(String filename, int thumbWidth, int thumbHeight){
        if(filename == null){
            return null;
        }
        Image image;
        if(filename.substring(filename.lastIndexOf(".")+1).equalsIgnoreCase("pdf")){
            image = PDFToImage.getBImagesFromPDF(filename, 1, 1).get(0);
        } else {
            image = Utils.loadImage(filename);
        }
        return generate(image, thumbWidth, thumbHeight);
    }
    
    public static BufferedImage generate(Image image, int thumbWidth, int thumbHeight){
        if(image.getWidth(null) >= image.getHeight(null)){
            thumbHeight = (thumbWidth * image.getHeight(null)) / image.getWidth(null); 
        } else {
            thumbWidth = (thumbHeight * image.getWidth(null)) / image.getHeight(null);
        }
        BufferedImage resizedImage = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);
        g.dispose();
        return resizedImage;
    }
    
}
