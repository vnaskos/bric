/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bric.gui.inputOutput;

import org.bric.utils.Utils;

import javax.swing.*;

/**
 *
 * @author vasilis
 */
public class ImportedImage {
    private ImageIcon thumbnailImageIcon;
    private String path;
    private String dimensions;
    private String name;
    private String imageType;
    private long size;
    private boolean corrupted = false;

    public ImportedImage() {
        
    }

    public ImportedImage(String path) {
        this.path = path;
        this.name = path.substring(path.lastIndexOf(Utils.FS)+1);
        
        boolean thumbnail = Utils.prefs.getBoolean("thumbnail", true) && Utils.prefs.getInt("thumbWay", 0) == 0;
        boolean metadata = Utils.prefs.getBoolean("metadata", true) && Utils.prefs.getInt("metaWay", 0) == 0;
        
        if(this.name.contains(".")){
            this.name = name.substring(0, name.lastIndexOf("."));
        }
        this.imageType = path.substring(path.lastIndexOf('.')+1);
        
        Utils.setMetadataThumbnail(this, metadata, thumbnail);
        
    }
    
    public void setThumbnailImageIcon(ImageIcon thumbnail){
        this.thumbnailImageIcon = thumbnail;
    }
    
    public ImageIcon getThumbnailImageIcon(){
        return thumbnailImageIcon;
    }
    
    public void setPath(String path){
        this.path = path;
    }
    
    public String getPath(){
        return path;
    }
    
    public void setDimensions(String dimensions){
        this.dimensions = dimensions;
    }
    
    public String getDimensions(){
        return dimensions;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public String getName(){
        return name;
    }
    
    public void setImageType(String imageType){
        this.imageType = imageType;
    }
    
    public String getImageType(){
        return imageType;
    }
    
    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
    
    public boolean isCorrupted() {
        return corrupted;
    }

    public void setCorrupted(boolean corrupted) {
        this.corrupted = corrupted;
    }

    @Override
    public String toString() {
        return path;
    }

    @Override
    public ImportedImage clone() throws CloneNotSupportedException {
        ImportedImage clone = new ImportedImage();
        clone.setPath(path);
        clone.setName(name);
        clone.setImageType(imageType);
        return clone;
    }
    
    
}
