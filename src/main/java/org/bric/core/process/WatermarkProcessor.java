package org.bric.core.process;

import org.bric.core.model.WatermarkPattern;
import org.bric.core.process.model.WatermarkParameters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class WatermarkProcessor extends ImageProcessor<WatermarkParameters> {

    public WatermarkProcessor(WatermarkParameters params) {
        super(params);
    }

    @Override
    public BufferedImage process(BufferedImage image) {
        if (params.getPattern() == WatermarkPattern.SINGLE) {
            return generateSingleWatermark(image);
        }

        return generateTiledWatermark(image);
    }

    @Override
    public boolean isEnabled() {
        return params.isEnabled();
    }

    private BufferedImage generateSingleWatermark(BufferedImage input) {
        BufferedImage watermarkImage = new BufferedImage(input.getWidth(), input.getHeight(), getImageType(input));
        Graphics2D g2d = watermarkImage.createGraphics();
        g2d.drawImage(input, 0, 0, null);
        int newX = params.getX(input.getWidth()) - params.getWatermarkImage().getWidth()/2;
        int newY = params.getY(input.getHeight()) - params.getWatermarkImage().getHeight()/2;
        g2d.drawImage(params.getWatermarkImage(), newX, newY, null);
        g2d.dispose();
        return watermarkImage;
    }

    private BufferedImage generateTiledWatermark(final BufferedImage image) {
        BufferedImage watermarkImage = new BufferedImage(image.getWidth(), image.getHeight(), getImageType(image));
        Graphics2D g2d = watermarkImage.createGraphics();

        g2d.drawImage(image, 0, 0, null);

        int columns = params.getTiledColumns();
        int rows = params.getTiledRows();

        if (columns == 0) {
            columns = image.getWidth() / params.getWatermarkImage().getWidth();
        }

        if (rows == 0) {
            rows = image.getHeight() / params.getWatermarkImage().getHeight();
        }

        int rowsStep = image.getHeight() / rows;
        int columnsStep = image.getWidth() / columns;

        if (rowsStep < params.getWatermarkHeight() / 2) {
            rows = image.getHeight() / params.getWatermarkImage().getHeight();
            rowsStep = image.getHeight() / rows;
        }

        if (columnsStep < params.getWatermarkWidth() / 2) {
            columns = image.getWidth() / params.getWatermarkImage().getWidth();
            columnsStep = image.getWidth() / columns;
        }

        int row = ((image.getHeight() / rows) / 2) - params.getWatermarkImage().getHeight() / 2;
        int column = ((image.getWidth() / columns) / 2) - params.getWatermarkImage().getWidth() / 2;

        for (int c = 0; c < columns; c++) {
            for(int r = 0; r < rows; r++) {
                g2d.drawImage(params.getWatermarkImage(), column + (columnsStep*c), row + (rowsStep*r), null);
            }
        }

        g2d.dispose();
        return watermarkImage;
    }

    private static int getImageType(final BufferedImage image) {
        return image.getType() == BufferedImage.TYPE_CUSTOM
            ? BufferedImage.TYPE_INT_ARGB
            : image.getType();
    }
}
