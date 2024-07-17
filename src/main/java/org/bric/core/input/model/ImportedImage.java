package org.bric.core.input.model;

import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.function.Supplier;

public class ImportedImage {

    private final String path;
    private final InputType type;
    private final Supplier<GenerationMethod> thumbnailGenerationMethod;
    private final Supplier<GenerationMethod> metadataGenerationMethod;
    private Metadata metadata;
    private Thumbnail thumbnail;

    private boolean corrupted = false;

    public ImportedImage(String path,
                         Supplier<GenerationMethod> thumbnailGenerationMethod,
                         Supplier<GenerationMethod> metadataGenerationMethod) {
        this.path = path;
        this.type = InputType.from(path);
        this.thumbnailGenerationMethod = thumbnailGenerationMethod;
        this.metadataGenerationMethod = metadataGenerationMethod;

        if (thumbnailGenerationMethod.get() == GenerationMethod.ON_IMPORT) {
            generateThumbnail();
        }
        if (metadataGenerationMethod.get() == GenerationMethod.ON_IMPORT) {
            generateMetadata();
        }
    }

    private void generateThumbnail() {
        this.thumbnail = Thumbnail.generate(this);
    }

    public Optional<BufferedImage> getThumbnail() {
        if (!corrupted && thumbnail == null && thumbnailGenerationMethod.get() == GenerationMethod.ON_DEMAND) {
            generateThumbnail();
        }
        return Optional.ofNullable(thumbnail.get());
    }

    public void generateMetadata() {
        this.metadata = Metadata.generate(this);
    }

    public Optional<Metadata> getMetadata() {
        if (!corrupted && metadata == null && metadataGenerationMethod.get() == GenerationMethod.ON_DEMAND) {
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
