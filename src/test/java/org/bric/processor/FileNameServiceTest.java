package org.bric.processor;

import org.bric.input.ImportedImage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class FileNameServiceTest {

    private static final String JPG_OUTPUT_TYPE = "jpg";
    private static final String SAME_AS_FIRST_OUTPUT_TYPE = "same as first";
    private static final int A_COUPLE_OF_ITEMS = 2;
    private static final int A_NUMBERING_VALUE = 20;
    private static final ImportedImage A_JPG_IMAGE = new ImportedImage("/an/image/path/imported-image.jpg");
    private static final ImportedImage IMAGE_UNDER_BLUE_TREES_DIR = new ImportedImage("/blue/trees/img.png");

    @Test
    public void applyFileNameMask_GivenFixedPath_ShouldOnlyAppendExtension() {
        String outputFilepath = "/test/123";
        FileNameService fileNameService = new FileNameService(outputFilepath, JPG_OUTPUT_TYPE,
                A_NUMBERING_VALUE, A_COUPLE_OF_ITEMS);

        String actual = fileNameService.applyFileNameMasks(A_JPG_IMAGE);

        Assertions.assertEquals("/test/123.jpg", actual);
    }

    @Test
    public void applyFileNameMask_GivenSameAsFirstOutputType_ShouldOnlyAppendInitialExtension() {
        String outputFilepath = "/test/123";
        FileNameService fileNameService = new FileNameService(outputFilepath, SAME_AS_FIRST_OUTPUT_TYPE,
                A_NUMBERING_VALUE, A_COUPLE_OF_ITEMS);

        String actual = fileNameService.applyFileNameMasks(A_JPG_IMAGE);

        Assertions.assertEquals("/test/123.jpg", actual);
    }

    @Test
    public void applyFileNameMask_GivenSameAsFirstOutputTypeWithUnsupportedExtension_ShouldAppendDefaultExtension() {
        String outputFilepath = "/test/123";
        ImportedImage anUnsupportedImage = new ImportedImage("/image/path/img.UNSUPPORTED_EXT");
        FileNameService fileNameService = new FileNameService(outputFilepath, SAME_AS_FIRST_OUTPUT_TYPE,
                A_NUMBERING_VALUE, A_COUPLE_OF_ITEMS);

        String actual = fileNameService.applyFileNameMasks(anUnsupportedImage);

        String defaultExtension = "." + FileNameService.DEFAULT_OUTPUT_TYPE;
        Assertions.assertEquals(outputFilepath + defaultExtension, actual);
    }

    @Test
    public void applyFileNameMask_GivenRespectiveNumberingModifier_ShouldReplaceModifierWithNumber() {
        String outputFilepath = System.getProperty("java.io.tmpdir") + File.separator + "123_#";
        int anInitialNumberingValue = 12;
        FileNameService fileNameService = new FileNameService(outputFilepath, JPG_OUTPUT_TYPE,
                anInitialNumberingValue, A_COUPLE_OF_ITEMS);

        String actual = fileNameService.applyFileNameMasks(A_JPG_IMAGE);

        Assertions.assertTrue(actual.endsWith("123_12.jpg"));
    }

    @Test
    public void applyFileNameMask_GivenOutputPathWithoutFilenameMask_ShouldPlaceOriginalImageName() {
        String outputFilepath = "/test/";
        ImportedImage imageNamedHouses = new ImportedImage("/path/houses.png");
        FileNameService fileNameService = new FileNameService(outputFilepath, JPG_OUTPUT_TYPE,
                A_NUMBERING_VALUE, A_COUPLE_OF_ITEMS);

        String actual = fileNameService.applyFileNameMasks(imageNamedHouses);

        Assertions.assertEquals("/test/houses.jpg", actual);
    }

    @Test
    public void applyFileNameMask_GivenOriginalDirectoryModifier_ShouldReturnOriginalImagePathAndNameWithOutputExtension() {
        String outputFilepath = "^P";
        FileNameService fileNameService = new FileNameService(outputFilepath, JPG_OUTPUT_TYPE,
                A_NUMBERING_VALUE, A_COUPLE_OF_ITEMS);

        String actual = fileNameService.applyFileNameMasks(IMAGE_UNDER_BLUE_TREES_DIR);

        Assertions.assertEquals("/blue/trees/img.jpg", actual);
    }

    @Test
    public void applyFileNameMask_GivenOriginalDirectoryModifierAndSlash_ShouldReturnOriginalImagePathAndNameWithOutputExtension() {
        String outputFilepath = "^P/";
        FileNameService fileNameService = new FileNameService(outputFilepath, JPG_OUTPUT_TYPE,
                A_NUMBERING_VALUE, A_COUPLE_OF_ITEMS);

        String actual = fileNameService.applyFileNameMasks(IMAGE_UNDER_BLUE_TREES_DIR);

        Assertions.assertEquals("/blue/trees/img.jpg", actual);
    }

    @Test
    public void applyFileNameMask_GivenOriginalDirectoryAndNumberingModifier_ShouldReturnOriginalImagePathWithNumberingAndOutputExtension() {
        String outputFilepath = "^P*";
        int anInitialNumberingValue = 12;
        FileNameService fileNameService = new FileNameService(outputFilepath, JPG_OUTPUT_TYPE,
                anInitialNumberingValue, A_COUPLE_OF_ITEMS);

        String actual = fileNameService.applyFileNameMasks(IMAGE_UNDER_BLUE_TREES_DIR);

        Assertions.assertEquals("/blue/trees12.jpg", actual);
    }

    @Test
    public void applyFileNameMask_GivenOriginalDirectoryAndSlashAndNumberingModifier_ShouldReturnOriginalImagePathWithNumberingAndOutputExtension() {
        String outputFilepath = "^P/*";
        int anInitialNumberingValue = 12;
        FileNameService fileNameService = new FileNameService(outputFilepath, JPG_OUTPUT_TYPE,
                anInitialNumberingValue, A_COUPLE_OF_ITEMS);

        String actual = fileNameService.applyFileNameMasks(IMAGE_UNDER_BLUE_TREES_DIR);

        Assertions.assertEquals("/blue/trees/12.jpg", actual);
    }

    @Test
    public void applyFileNameMask_GivenMultipleNumberingModifiers_ShouldReplaceThemAll() {
        String outputFilepath = "/path/*_*";
        int anInitialNumberingValue = 11;
        FileNameService fileNameService = new FileNameService(outputFilepath, JPG_OUTPUT_TYPE,
                anInitialNumberingValue, A_COUPLE_OF_ITEMS);

        String actual = fileNameService.applyFileNameMasks(A_JPG_IMAGE);

        Assertions.assertEquals("/path/11_11.jpg", actual);
    }

}
