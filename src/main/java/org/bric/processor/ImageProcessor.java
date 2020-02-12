package org.bric.processor;

import org.bric.core.model.TabParameters;

import java.awt.image.BufferedImage;

public abstract class ImageProcessor<T extends TabParameters> {

    protected T params;

    public ImageProcessor(T params) {
        this.params = params;
    }

    public abstract BufferedImage process(BufferedImage src);

    public abstract boolean isEnabled();
}
