package org.bric.core.model;

import org.bric.core.model.input.GenerationMethod;
import org.bric.core.model.input.InputType;
import org.bric.gui.inputOutput.Thumbnail;
import org.bric.utils.Utils;

import java.awt.image.BufferedImage;
import java.util.Optional;

public class ImportedImage {

    private Thumbnail thumbnail;
    private String path;
    private String dimensions;
    private String name;
    private InputType type;
    private long size;
    private boolean corrupted = false;

    public ImportedImage(String path) {
        this.path = path;
        this.name = path.substring(path.lastIndexOf(Utils.FS)+1);

        if(this.name.contains(".")){
            this.name = name.substring(0, name.lastIndexOf("."));
        }
        this.type = InputType.from(path);
        
        Utils.setMetadataThumbnail(this, GenerationMethod.metadata() == GenerationMethod.ON_IMPORT);

        if (GenerationMethod.thumbnail() == GenerationMethod.ON_IMPORT) {
            generateThumbnail();
        }
//        if (GenerationMethod.metadata() == GenerationMethod.ON_IMPORT) {
//            generateMetadata();
//        }
    }

    private void generateThumbnail() {
        this.thumbnail = Thumbnail.generate(this);
    }
    
    public Optional<BufferedImage> getThumbnail() {
        if (!corrupted && thumbnail == null && GenerationMethod.thumbnail() == GenerationMethod.ON_DEMAND) {
            generateThumbnail();
        }
        return Optional.ofNullable(thumbnail.get());
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
    
    public InputType getType(){
        return type;
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
}
