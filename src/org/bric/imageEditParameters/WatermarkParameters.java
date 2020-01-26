/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bric.imageEditParameters;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;

/**
 *
 * @author vasilis
 */
public class WatermarkParameters implements ImageEditParameters {
    private boolean enabled;
    private boolean custom;
    private int pattern;    
    private boolean watermarkTextButton;
    private boolean watermarkImageButton;
    private String watermarkText;
    private String watermarkImagePath;
    private int tiledRows;
    private int tiledColumns;
    private Font font;
    private Color color;
    private int x;
    private int y;
    private BufferedImage watermarkImage;
    private int watermarkWidth;
    private int watermarkHeight;
    private int componentWidth;
    private int componentHeight;

    public int getCenterX() {
        return x;
    }

    public void setCenterX(int x) {
        this.x = x;
    }

    public int getCenterY() {
        return y;
    }

    public void setCenterY(int y) {
        this.y = y;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public int getTiledColumns() {
        return tiledColumns;
    }

    public void setTiledColumns(int tiledColumns) {
        this.tiledColumns = tiledColumns;
    }

    public int getTiledRows() {
        return tiledRows;
    }

    public void setTiledRows(int tiledRows) {
        this.tiledRows = tiledRows;
    }

    public boolean isWatermarkImageButton() {
        return watermarkImageButton;
    }

    public void setWatermarkImageButton(boolean watermarkImageButton) {
        this.watermarkImageButton = watermarkImageButton;
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getPattern() {
        return pattern;
    }

    public void setPattern(int pattern) {
        this.pattern = pattern;
    }

    public String getWatermarkImagePath() {
        return watermarkImagePath;
    }

    public void setWatermarkImagePath(String watermarkImagePath) {
        this.watermarkImagePath = watermarkImagePath;
    }

    public String getWatermarkText() {
        return watermarkText;
    }

    public void setWatermarkText(String watermarkText) {
        this.watermarkText = watermarkText;
    }

    public boolean isWatermarkTextButton() {
        return watermarkTextButton;
    }

    public void setWatermarkTextButton(boolean watermarkTextButton) {
        this.watermarkTextButton = watermarkTextButton;
    }
    
    public void setWatermarkImage(BufferedImage watermark){
        this.watermarkImage = watermark;
    }
    
    public BufferedImage getWatermarkImage(){
        return watermarkImage;
    }
    
    public int getWatermarkHeight() {
        return watermarkHeight;
    }

    public void setWatermarkHeight(int watermarkHeight) {
        this.watermarkHeight = watermarkHeight;
    }

    public int getWatermarkWidth() {
        return watermarkWidth;
    }

    public void setWatermarkWidth(int watermarkWidth) {
        this.watermarkWidth = watermarkWidth;
    }
    
    public int getComponentHeight() {
        return componentHeight;
    }

    public void setComponentHeight(int height) {
        this.componentHeight = height;
    }

    public int getComponentWidth() {
        return componentWidth;
    }

    public void setComponentWidth(int width) {
        this.componentWidth = width;
    }
    
    public int getX(int width){
        return (int)( ( (double) width / (getComponentWidth()) ) * getCenterX());
    }
    
    public int getY(int height){
        return (int) ( ( (double) height / getComponentHeight() ) * getCenterY());
    }
}
