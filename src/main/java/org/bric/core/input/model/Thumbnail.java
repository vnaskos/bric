package org.bric.core.input.model;

import org.bric.utils.PDFToImage;
import org.bric.utils.Utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Thumbnail {

    public static final int DEFAULT_WIDTH = 125;
    public static final int DEFAULT_HEIGHT = 125;

    private final BufferedImage image;

    private Thumbnail(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage get() {
        return image;
    }

    public static Thumbnail generate(ImportedImage importedImage) {
        try {
            BufferedImage image;
            if (importedImage.getType() == InputType.PDF) {
                image = PDFToImage.getBImagesFromPDF(importedImage.getPath(), 0, 1).get(0);
            } else {
                image = Utils.loadImage(importedImage.getPath());
            }
            BufferedImage scaledImage = scaleImage(image);
            return new Thumbnail(scaledImage);
        } catch (Exception ex) {
            importedImage.setCorrupted();
            return null;
        }
    }

    private static BufferedImage scaleImage(Image image) {
        int thumbWidth = DEFAULT_WIDTH;
        int thumbHeight = DEFAULT_HEIGHT;

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
