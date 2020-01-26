/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bric.processor;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.bric.imageEditParameters.WatermarkParameters;

/**
 *
 * @author vasilis
 */
public class WatermarkProcessor implements ImageProcessor {

    WatermarkParameters watermarkParameter;
    
    public WatermarkProcessor(WatermarkParameters watermarkParameter) {
        this.watermarkParameter = watermarkParameter;
    }

    
    @Override
    public BufferedImage process(BufferedImage image) {
        int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
        BufferedImage watermarkImage = new BufferedImage(image.getWidth(), image.getHeight(), type);
        Graphics2D g2 = watermarkImage.createGraphics();

        g2.drawImage(image, 0, 0, null);
        if( watermarkParameter.getPattern() == 0){
            int newX = watermarkParameter.getX(image.getWidth())-watermarkParameter.getWatermarkImage().getWidth()/2;
            int newY = watermarkParameter.getY(image.getHeight())-watermarkParameter.getWatermarkImage().getHeight()/2;
            g2.drawImage(watermarkParameter.getWatermarkImage(), newX, newY, null);
        } else {
            int columns = watermarkParameter.getTiledColumns();
            int rows = watermarkParameter.getTiledRows();
            
            if(columns == 0){
                columns = image.getWidth() / watermarkParameter.getWatermarkImage().getWidth();
            }
            
            if(rows == 0){
                rows = image.getHeight() / watermarkParameter.getWatermarkImage().getHeight();
            }
            
            double rowsStep = image.getHeight() / rows;
            double columnsStep = image.getWidth() / columns;
            
            if( rowsStep < watermarkParameter.getWatermarkHeight() / 2){
                rows = image.getHeight() / watermarkParameter.getWatermarkImage().getHeight();
                rowsStep = image.getHeight() / rows;
            }
            
            if( columnsStep < watermarkParameter.getWatermarkWidth() / 2) {
                columns = image.getWidth() / watermarkParameter.getWatermarkImage().getWidth();
                columnsStep = image.getWidth() / columns;
            }
            
            int row = ((image.getHeight() / rows) / 2) - watermarkParameter.getWatermarkImage().getHeight() / 2;
            int column = ((image.getWidth() / columns) / 2) - watermarkParameter.getWatermarkImage().getWidth() / 2;
            
            for(int c = 0; c < columns; c++){
                for(int r = 0; r < rows; r++){
                    g2.drawImage(watermarkParameter.getWatermarkImage(),(int) (column + (columnsStep*c)), (int) (row + (rowsStep*r)) , null);
                }
            }
        }
        g2.dispose();
        return watermarkImage;
    }
    
}
