package org.bric.core.model;

import org.bric.core.model.input.GenerationMethod;
import org.bric.core.model.input.InputType;
import org.bric.core.model.input.Metadata;
import org.bric.core.model.input.Thumbnail;

import java.awt.image.BufferedImage;
import java.util.Optional;

public class ImportedImage {

    private String path;
    private InputType type;
    private Metadata metadata;
    private Thumbnail thumbnail;

    private boolean corrupted = false;

    public ImportedImage(String path) {
        this.path = path;
        this.type = InputType.from(path);

        if (GenerationMethod.thumbnail() == GenerationMethod.ON_IMPORT) {
            generateThumbnail();
        }
        if (GenerationMethod.metadata() == GenerationMethod.ON_IMPORT) {
            generateMetadata();
        }
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

    public void generateMetadata() {
        this.metadata = Metadata.generate(this);
    }

    public Optional<Metadata> getMetadata() {
        if (!corrupted && metadata == null && GenerationMethod.metadata() == GenerationMethod.ON_DEMAND) {
            generateMetadata();
        }
        return Optional.ofNullable(metadata);
    }
    
    public String getPath(){
        return path;
    }

    public InputType getType(){
        return type;
    }

    public String getName() {
        return getMetadata().map(Metadata::getName).orElse(path);
    }

    public String getDimensions() {
        return getMetadata().map(Metadata::getDimensions).orElse("");
    }

    public long getSize() {
        return getMetadata().map(Metadata::getSize).orElse(0L);
    }

    public boolean isNotCorrupted() {
        return !corrupted;
    }

    public void setCorrupted() {
        this.corrupted = true;
    }

    @Override
    public String toString() {
        return path;
    }
}
