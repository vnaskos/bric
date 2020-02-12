package org.bric.processor;

import org.bric.imageEditParameters.WatermarkParameters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class WatermarkProcessor extends ImageProcessor<WatermarkParameters> {

    public WatermarkProcessor(WatermarkParameters params) {
        super(params);
    }

    @Override
    public BufferedImage process(BufferedImage image) {
        int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
        BufferedImage watermarkImage = new BufferedImage(image.getWidth(), image.getHeight(), type);
        Graphics2D g2 = watermarkImage.createGraphics();

        g2.drawImage(image, 0, 0, null);
        if( params.getPattern() == 0){
            int newX = params.getX(image.getWidth())- params.getWatermarkImage().getWidth()/2;
            int newY = params.getY(image.getHeight())- params.getWatermarkImage().getHeight()/2;
            g2.drawImage(params.getWatermarkImage(), newX, newY, null);
        } else {
            int columns = params.getTiledColumns();
            int rows = params.getTiledRows();
            
            if(columns == 0){
                columns = image.getWidth() / params.getWatermarkImage().getWidth();
            }
            
            if(rows == 0){
                rows = image.getHeight() / params.getWatermarkImage().getHeight();
            }
            
            int rowsStep = image.getHeight() / rows;
            int columnsStep = image.getWidth() / columns;
            
            if( rowsStep < params.getWatermarkHeight() / 2){
                rows = image.getHeight() / params.getWatermarkImage().getHeight();
                rowsStep = image.getHeight() / rows;
            }
            
            if( columnsStep < params.getWatermarkWidth() / 2) {
                columns = image.getWidth() / params.getWatermarkImage().getWidth();
                columnsStep = image.getWidth() / columns;
            }
            
            int row = ((image.getHeight() / rows) / 2) - params.getWatermarkImage().getHeight() / 2;
            int column = ((image.getWidth() / columns) / 2) - params.getWatermarkImage().getWidth() / 2;
            
            for(int c = 0; c < columns; c++){
                for(int r = 0; r < rows; r++){
                    g2.drawImage(params.getWatermarkImage(), column + (columnsStep*c), row + (rowsStep*r), null);
                }
            }
        }
        g2.dispose();
        return watermarkImage;
    }
    
}
