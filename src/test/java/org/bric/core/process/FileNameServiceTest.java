package org.bric.core.process;

import org.bric.core.input.model.ImportedImage;
import org.bric.core.model.output.OutputType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class FileNameServiceTest {

    private static final int A_COUPLE_OF_ITEMS = 2;
    private static final int A_NUMBERING_VALUE = 20;
    private static final ImportedImage A_JPG_IMAGE = new ImportedImage("/an/image/path/imported-image.jpg");
    private static final ImportedImage IMAGE_UNDER_BLUE_TREES_DIR = new ImportedImage("/blue/trees/img.png");

    @Test
    public void generateFilepath_GivenFixedPath_ShouldOnlyAppendExtension() {
        String outputFilepath = "/test/123";

        FileNameService fileNameService = new FileNameService(outputFilepath, OutputType.JPG,
                A_NUMBERING_VALUE, A_COUPLE_OF_ITEMS);

        String actual = fileNameService.generateFilePath(A_JPG_IMAGE);

        Assertions.assertEquals("/test/123.jpg", actual);
    }

    @Test
    public void generateFilepath_GivenSameAsFirstOutputType_ShouldOnlyAppendInitialExtension() {
        String outputFilepath = "/test/123";
        FileNameService fileNameService = new FileNameService(outputFilepath, OutputType.SAME_AS_FIRST,
                A_NUMBERING_VALUE, A_COUPLE_OF_ITEMS);

        String actual = fileNameService.generateFilePath(A_JPG_IMAGE);

        Assertions.assertEquals("/test/123.jpg", actual);
    }

    @Test
    public void generateFilepath_GivenSameAsFirstOutputTypeWithUnsupportedExtension_ShouldAppendDefaultExtension() {
        String outputFilepath = "/test/123";
        ImportedImage anUnsupportedOutputExtensionImage = new ImportedImage("/image/path/img.psd");
        FileNameService fileNameService = new FileNameService(outputFilepath, OutputType.SAME_AS_FIRST,
                A_NUMBERING_VALUE, A_COUPLE_OF_ITEMS);

        String actual = fileNameService.generateFilePath(anUnsupportedOutputExtensionImage);

        String defaultExtension = "." + OutputType.DEFAULT.name.toLowerCase();
        Assertions.assertEquals(outputFilepath + defaultExtension, actual);
    }

    @Test
    public void generateFilepath_GivenRespectiveNumberingModifier_ShouldReplaceModifierWithNumber() {
        String outputFilepath = System.getProperty("java.io.tmpdir") + File.separator + "123_#";
        int anInitialNumberingValue = 12;
        FileNameService fileNameService = new FileNameService(outputFilepath, OutputType.JPG,
                anInitialNumberingValue, A_COUPLE_OF_ITEMS);

        String actual = fileNameService.generateFilePath(A_JPG_IMAGE);

        Assertions.assertTrue(actual.endsWith("123_12.jpg"));
    }

    @Test
    public void generateFilepath_GivenOutputPathWithoutFilenameMask_ShouldPlaceOriginalImageName() {
        String outputFilepath = "/test/";
        ImportedImage imageNamedHouses = new ImportedImage("/path/houses.png");
        FileNameService fileNameService = new FileNameService(outputFilepath, OutputType.JPG,
                A_NUMBERING_VALUE, A_COUPLE_OF_ITEMS);

        String actual = fileNameService.generateFilePath(imageNamedHouses);

        Assertions.assertEquals("/test/houses.jpg", actual);
    }

    @Test
    public void generateFilepath_GivenOriginalDirectoryModifier_ShouldReturnOriginalImagePathAndNameWithOutputExtension() {
        String outputFilepath = "^P";
        FileNameService fileNameService = new FileNameService(outputFilepath, OutputType.JPG,
                A_NUMBERING_VALUE, A_COUPLE_OF_ITEMS);

        String actual = fileNameService.generateFilePath(IMAGE_UNDER_BLUE_TREES_DIR);

        Assertions.assertEquals("/blue/trees/img.jpg", actual);
    }

    @Test
    public void generateFilepath_GivenOriginalDirectoryModifierAndSlash_ShouldReturnOriginalImagePathAndNameWithOutputExtension() {
        String outputFilepath = "^P/";
        FileNameService fileNameService = new FileNameService(outputFilepath, OutputType.JPG,
                A_NUMBERING_VALUE, A_COUPLE_OF_ITEMS);

        String actual = fileNameService.generateFilePath(IMAGE_UNDER_BLUE_TREES_DIR);

        Assertions.assertEquals("/blue/trees/img.jpg", actual);
    }

    @Test
    public void generateFilepath_GivenOriginalDirectoryAndNumberingModifier_ShouldReturnOriginalImagePathWithNumberingAndOutputExtension() {
        String outputFilepath = "^P*";
        int anInitialNumberingValue = 12;
        FileNameService fileNameService = new FileNameService(outputFilepath, OutputType.JPG,
                anInitialNumberingValue, A_COUPLE_OF_ITEMS);

        String actual = fileNameService.generateFilePath(IMAGE_UNDER_BLUE_TREES_DIR);

        Assertions.assertEquals("/blue/trees12.jpg", actual);
    }

    @Test
    public void generateFilepath_GivenOriginalDirectoryAndSlashAndNumberingModifier_ShouldReturnOriginalImagePathWithNumberingAndOutputExtension() {
        String outputFilepath = "^P/*";
        int anInitialNumberingValue = 12;
        FileNameService fileNameService = new FileNameService(outputFilepath, OutputType.JPG,
                anInitialNumberingValue, A_COUPLE_OF_ITEMS);

        String actual = fileNameService.generateFilePath(IMAGE_UNDER_BLUE_TREES_DIR);

        Assertions.assertEquals("/blue/trees/12.jpg", actual);
    }

    @Test
    public void generateFilepath_GivenMultipleNumberingModifiers_ShouldReplaceThemAll() {
        String outputFilepath = "/path/*_*";
        int anInitialNumberingValue = 11;
        FileNameService fileNameService = new FileNameService(outputFilepath, OutputType.JPG,
                anInitialNumberingValue, A_COUPLE_OF_ITEMS);

        String actual = fileNameService.generateFilePath(A_JPG_IMAGE);

        Assertions.assertEquals("/path/11_11.jpg", actual);
    }

}
