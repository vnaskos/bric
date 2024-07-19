package org.bric.core.process;

import org.bric.core.model.TabParameters;

import java.awt.image.BufferedImage;
import java.util.function.Supplier;

public abstract class ImageProcessor<T extends TabParameters> {

    protected T params;

    ImageProcessor(T params) {
        this.params = params;
    }

    public Supplier<BufferedImage> process(Supplier<BufferedImage> src) {
        return () -> process(src.get());
    }

    public abstract BufferedImage process(BufferedImage src);

    public abstract boolean isEnabled();
}
