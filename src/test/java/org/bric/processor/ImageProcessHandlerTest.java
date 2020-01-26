package org.bric.processor;

import org.bric.input.ImportedImage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.Stack;

public class ImageProcessHandlerTest {

    private static final String JPG_EXTENSION = "jpg";
    private static final String AN_OUTPUT_PATH = "/output";
    private static final ImportedImage AN_IMAGE = new ImportedImage("/an/image/path/imported-image.jpg");

    private ImageProcessHandler imageProcessHandler;

    @BeforeEach
    public void setup() {
        DefaultListModel<ImportedImage> fakeModel = new DefaultListModel<>();
        imageProcessHandler = new ImageProcessHandler(fakeModel);
        imageProcessHandler.outputPath = AN_OUTPUT_PATH;
        imageProcessHandler.outputExtension = JPG_EXTENSION;
    }

    @Test
    public void applyFileNameMask_GivenFixedPath_ShouldOnlyApplyExtension() {
        String filepath = "/test/123";
        String actual = imageProcessHandler.applyFileNameMasks(filepath, AN_IMAGE);

        Assertions.assertEquals(filepath.concat(".jpg"), actual);
    }

    @Test
    public void applyFileNameMask_GivenRespectiveNumberingModifier_ShouldReplaceModifierWithNumber() {
        imageProcessHandler.numsStack = new Stack<>();
        imageProcessHandler.numsStack.push(0); // TODO: unsafe

        String filepath = "/test/123_#";
        String expected = "/test/123_0.jpg";
        String actual = imageProcessHandler.applyFileNameMasks(filepath, AN_IMAGE);

        Assertions.assertEquals(expected, actual);
    }
}
