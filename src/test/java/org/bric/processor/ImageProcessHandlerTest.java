package org.bric.processor;

import org.bric.input.ImportedImage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

public class ImageProcessHandlerTest {

    private static final String JPG_EXTENSION = "jpg";
    private static final String AN_OUTPUT_PATH = "/output";
    private static final ImportedImage AN_IMAGE = new ImportedImage("/an/image/path/imported-image.jpg");

    private ImageProcessHandler imageProcessHandler;

    @BeforeEach
    public void setup() {
        DefaultListModel<ImportedImage> fakeModel = new DefaultListModel<>();
        imageProcessHandler = new ImageProcessHandler(fakeModel);
    }

    @Test
    public void applyFileNameMask_GivenFixedPath_ShouldOnlyApplyExtension() {
        imageProcessHandler.outputPath = AN_OUTPUT_PATH;
        imageProcessHandler.outputExtension = JPG_EXTENSION;

        String filepath = "/test/123";
        String actual = imageProcessHandler.applyFileNameMasks(filepath, AN_IMAGE);

        Assertions.assertEquals(filepath.concat(".jpg"), actual);
    }
}
