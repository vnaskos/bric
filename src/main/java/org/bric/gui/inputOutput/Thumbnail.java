package org.bric.gui.inputOutput;

import org.bric.core.model.ImportedImage;
import org.bric.core.model.input.InputType;
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
                image = PDFToImage.getBImagesFromPDF(importedImage.getPath(), 1, 1).get(0);
            } else {
                image = Utils.loadImage(importedImage.getPath());
            }
            BufferedImage scaledImage = scaleImage(image, DEFAULT_WIDTH, DEFAULT_HEIGHT);
            return new Thumbnail(scaledImage);
        } catch (Exception ex) {
            importedImage.setCorrupted(true);
            return null;
        }
    }

    private static BufferedImage scaleImage(Image image, int thumbWidth, int thumbHeight) {
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
