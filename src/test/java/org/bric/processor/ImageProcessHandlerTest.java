package org.bric.processor;

import org.bric.input.ImportedImage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.Stack;

public class ImageProcessHandlerTest {

    private static final String JPG_OUTPUT_TYPE = "jpg";
    private static final String SAME_AS_FIRST_OUTPUT_TYPE = "same as first";
    private static final String AN_OUTPUT_PATH = "/output";
    private static final ImportedImage A_JPG_IMAGE_PATH = new ImportedImage("/an/image/path/imported-image.jpg");

    private ImageProcessHandler imageProcessHandler;

    @BeforeEach
    public void setup() {
        DefaultListModel<ImportedImage> fakeModel = new DefaultListModel<>();
        imageProcessHandler = new ImageProcessHandler(fakeModel);
        imageProcessHandler.outputPath = AN_OUTPUT_PATH;
        imageProcessHandler.outputExtension = JPG_OUTPUT_TYPE;
    }

    @Test
    public void applyFileNameMask_GivenFixedPath_ShouldOnlyAppendExtension() {
        String filepath = "/test/123";
        String actual = imageProcessHandler.applyFileNameMasks(filepath, A_JPG_IMAGE_PATH);

        Assertions.assertEquals(filepath.concat(".jpg"), actual);
    }

    @Test
    public void applyFileNameMask_GivenSameAsFirstOutputType_ShouldOnlyAppendInitialExtension() {
        imageProcessHandler.outputExtension = SAME_AS_FIRST_OUTPUT_TYPE;

        String filepath = "/test/123";
        String actual = imageProcessHandler.applyFileNameMasks(filepath, A_JPG_IMAGE_PATH);

        Assertions.assertEquals(filepath.concat(".jpg"), actual);
    }

    @Test
    public void applyFileNameMask_GivenSameAsFirstOutputTypeWithUnsupportedExtension_ShouldAppendDefaultExtension() {
        imageProcessHandler.outputExtension = SAME_AS_FIRST_OUTPUT_TYPE;

        String filepath = "/test/123";
        String actual = imageProcessHandler.applyFileNameMasks(filepath,
                new ImportedImage("/image/path/img.UNSUPPORTED_EXT"));

        String defaultExtension = "." + ImageProcessHandler.DEFAULT_OUTPUT_TYPE;

        Assertions.assertEquals(filepath.concat(defaultExtension), actual);
    }

    @Test
    public void applyFileNameMask_GivenRespectiveNumberingModifier_ShouldReplaceModifierWithNumber() {
        imageProcessHandler.numsStack = new Stack<>();
        imageProcessHandler.numsStack.push(0); // TODO: unsafe

        String filepath = "/test/123_#";
        String expected = "/test/123_0.jpg";
        String actual = imageProcessHandler.applyFileNameMasks(filepath, A_JPG_IMAGE_PATH);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void applyFileNameMask_GivenOutputPathWithoutFilenameMask_ShouldPlaceOriginalImageName() {
        String filepath = "/test/";
        imageProcessHandler.outputPath = filepath;

        String actual = imageProcessHandler.applyFileNameMasks(filepath, new ImportedImage("/path/original.png"));

        Assertions.assertEquals(filepath.concat("original.jpg"), actual);
    }

    @Test
    public void applyFileNameMask_GivenOriginalDirectoryModifier_ShouldReturnOriginalImagePathAndNameWithOutputExtension() {
        String filepath = "^P";

        String actual = imageProcessHandler.applyFileNameMasks(filepath,
                new ImportedImage("/original/path/img.png"));

        Assertions.assertEquals("/original/path/img.jpg", actual);
    }

    @Test
    public void applyFileNameMask_GivenOriginalDirectoryModifierAndSlash_ShouldReturnOriginalImagePathAndNameWithOutputExtension() {
        String filepath = "^P/";

        String actual = imageProcessHandler.applyFileNameMasks(filepath,
                new ImportedImage("/original/path/img.png"));

        Assertions.assertEquals("/original/path/img.jpg", actual);
    }

    @Test
    public void applyFileNameMask_GivenOriginalDirectoryAndNumberingModifier_ShouldReturnOriginalImagePathWithNumberingAndOutputExtension() {
        String filepath = "^P*";

        String actual = imageProcessHandler.applyFileNameMasks(filepath,
                new ImportedImage("/original/path/img.png"));

        Assertions.assertEquals("/original/path/0.jpg", actual);
    }

    @Test
    public void applyFileNameMask_GivenOriginalDirectoryAndSlashAndNumberingModifier_ShouldReturnOriginalImagePathWithNumberingAndOutputExtension() {
        String filepath = "^P/*";

        String actual = imageProcessHandler.applyFileNameMasks(filepath,
                new ImportedImage("/original/path/img.png"));

        Assertions.assertEquals("/original/path/0.jpg", actual);
    }
}
