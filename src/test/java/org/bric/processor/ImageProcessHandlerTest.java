package org.bric.processor;

import org.bric.imageEditParameters.OutputParameters;
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
    private static final int A_NUMBERING_VALUE = 20;
    private static final ImportedImage A_JPG_IMAGE_PATH = new ImportedImage("/an/image/path/imported-image.jpg");

    private ImageProcessHandler imageProcessHandler;

    @BeforeEach
    public void setup() {
        OutputParameters fakeOutputParameters = new OutputParameters();
        fakeOutputParameters.setOutputFormat("FAKE");
        fakeOutputParameters.setOutputPath("FAKE");
        DefaultListModel<ImportedImage> fakeModel = new DefaultListModel<>();
        imageProcessHandler = new ImageProcessHandler(fakeOutputParameters, fakeModel);
        imageProcessHandler.outputPath = AN_OUTPUT_PATH;
        imageProcessHandler.outputExtension = JPG_OUTPUT_TYPE;
    }

    @Test
    public void applyFileNameMask_GivenFixedPath_ShouldOnlyAppendExtension() {
        String filepath = "/test/123";

        String actual = imageProcessHandler.applyFileNameMasks(filepath, A_JPG_IMAGE_PATH, A_NUMBERING_VALUE, JPG_OUTPUT_TYPE);

        Assertions.assertEquals("/test/123.jpg", actual);
    }

    @Test
    public void applyFileNameMask_GivenSameAsFirstOutputType_ShouldOnlyAppendInitialExtension() {
        String filepath = "/test/123";

        String actual = imageProcessHandler.applyFileNameMasks(filepath, A_JPG_IMAGE_PATH, A_NUMBERING_VALUE, SAME_AS_FIRST_OUTPUT_TYPE);

        Assertions.assertEquals("/test/123.jpg", actual);
    }

    @Test
    public void applyFileNameMask_GivenSameAsFirstOutputTypeWithUnsupportedExtension_ShouldAppendDefaultExtension() {
        String filepath = "/test/123";

        String actual = imageProcessHandler.applyFileNameMasks(filepath,
                new ImportedImage("/image/path/img.UNSUPPORTED_EXT"), A_NUMBERING_VALUE, SAME_AS_FIRST_OUTPUT_TYPE);

        String defaultExtension = "." + ImageProcessHandler.DEFAULT_OUTPUT_TYPE;
        Assertions.assertEquals(filepath.concat(defaultExtension), actual);
    }

    @Test
    public void applyFileNameMask_GivenRespectiveNumberingModifier_ShouldReplaceModifierWithNumber() {
        imageProcessHandler.numsStack = new Stack<>();
        imageProcessHandler.numsStack.push(0); // TODO: unsafe
        String filepath = "/test/123_#";

        String actual = imageProcessHandler.applyFileNameMasks(filepath, A_JPG_IMAGE_PATH, A_NUMBERING_VALUE, JPG_OUTPUT_TYPE);

        Assertions.assertEquals("/test/123_0.jpg", actual);
    }

    @Test
    public void applyFileNameMask_GivenOutputPathWithoutFilenameMask_ShouldPlaceOriginalImageName() {
        String filepath = "/test/";
        String originalImagePath = "/path/original.png";

        String actual = imageProcessHandler.applyFileNameMasks(filepath, new ImportedImage(originalImagePath), A_NUMBERING_VALUE, JPG_OUTPUT_TYPE);

        Assertions.assertEquals("/test/original.jpg", actual);
    }

    @Test
    public void applyFileNameMask_GivenOriginalDirectoryModifier_ShouldReturnOriginalImagePathAndNameWithOutputExtension() {
        String filepath = "^P";
        String originalImagePath = "/original/path/img.png";

        String actual = imageProcessHandler.applyFileNameMasks(filepath,
                new ImportedImage(originalImagePath), A_NUMBERING_VALUE, JPG_OUTPUT_TYPE);

        Assertions.assertEquals("/original/path/img.jpg", actual);
    }

    @Test
    public void applyFileNameMask_GivenOriginalDirectoryModifierAndSlash_ShouldReturnOriginalImagePathAndNameWithOutputExtension() {
        String filepath = "^P/";
        String originalImagePath = "/original/path/img.png";

        String actual = imageProcessHandler.applyFileNameMasks(filepath,
                new ImportedImage(originalImagePath), A_NUMBERING_VALUE, JPG_OUTPUT_TYPE);

        Assertions.assertEquals("/original/path/img.jpg", actual);
    }

    @Test
    public void applyFileNameMask_GivenMultipleNumberingModifiers_ShouldReplaceThemAll() {
        String filepath = "/path/*_*";
        int anInitialNumberingValue = 11;

        String actual = imageProcessHandler.applyFileNameMasks(filepath, A_JPG_IMAGE_PATH,
                anInitialNumberingValue, JPG_OUTPUT_TYPE);

        Assertions.assertEquals("/path/11_11.jpg", actual);
    }

    @Test
    public void applyFileNameMask_GivenOriginalDirectoryAndNumberingModifier_ShouldReturnOriginalImagePathWithNumberingAndOutputExtension() {
        String filepath = "^P*";
        String originalImagePath = "/original/path/img.png";
        int anInitialNumberingValue = 12;

        String actual = imageProcessHandler.applyFileNameMasks(filepath,
                new ImportedImage(originalImagePath), anInitialNumberingValue, JPG_OUTPUT_TYPE);

        Assertions.assertEquals("/original/path12.jpg", actual);
    }

    @Test
    public void applyFileNameMask_GivenOriginalDirectoryAndSlashAndNumberingModifier_ShouldReturnOriginalImagePathWithNumberingAndOutputExtension() {
        String filepath = "^P/*";
        String originalImagePath = "/original/path/img.png";
        int anInitialNumberingValue = 12;

        String actual = imageProcessHandler.applyFileNameMasks(filepath,
                new ImportedImage(originalImagePath), anInitialNumberingValue, JPG_OUTPUT_TYPE);

        Assertions.assertEquals("/original/path/12.jpg", actual);
    }
}
