package org.bric.core.input.model;

import org.bric.utils.Utils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;

public class Metadata {

    private final String dimensions;
    private final long size;

    public Metadata(String dimensions, long size) {
        this.dimensions = dimensions;
        this.size = size;
    }

    public String getDimensions() {
        return dimensions;
    }

    public long getSize() {
        return size;
    }

    public static Metadata generate(ImportedImage image) {
        String dimensions = "unknown";
        if (image.getType() != InputType.PDF) {
            dimensions = getImageDimensions(image.getPath());
            if (dimensions == null) {
                image.setCorrupted();
            }
        }

        long size = getFileSize(image.getPath());

        return new Metadata(dimensions, size);
    }

    private static long getFileSize(String filepath) {
        try {
            return new File(filepath).length();
        } catch (Exception ex) {
            return 0;
        }
    }

    private static String getImageDimensions(String filepath) {
        String dimensions = null;

        try (ImageInputStream in = ImageIO.createImageInputStream(new File(filepath))) {
            final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
            while (readers.hasNext()) {
                ImageReader reader = readers.next();
                try {
                    reader.setInput(in);
                    dimensions = reader.getWidth(0) + "x" + reader.getHeight(0);
                } finally {
                    reader.dispose();
                }
            }
            if (dimensions == null) {
                throw new Exception();
            }
        } catch (Exception e) {
            BufferedImage importImage = Utils.loadImage(filepath);
            if (importImage != null) {
                dimensions = importImage.getWidth() + "x" + importImage.getHeight();
            }
        }

        return dimensions;
    }
}
