package org.bric.core.test;

import org.bric.core.input.model.GenerationMethod;
import org.bric.core.input.model.ImportedImage;

import java.util.function.Supplier;

public class ImportedImageTestFactory {

    public static final Supplier<GenerationMethod> NO_GENERATION = () -> GenerationMethod.NEVER;

    public static ImportedImage anImage() {
        return anImage("/tmp/an_image.jpg");
    }

    /**
     * Create a mock object of ImportedImage
     *
     * @param fullPath the path that the fake file exists in, including path, name and extension
     * @return A valid ImportedImage object without side effects
     */
    public static ImportedImage anImage(String fullPath) {
        return new ImportedImage(fullPath, NO_GENERATION, NO_GENERATION);
    }
}
