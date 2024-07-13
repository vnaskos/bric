package org.bric.core.process;

import org.bric.core.input.model.ImportedImage;
import org.bric.core.model.output.OutputType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.io.File;

class FileNameServiceImplTest {

    private static final int A_COUPLE_OF_ITEMS = 2;
    private static final int A_NUMBERING_VALUE = 20;
    private static final ImportedImage A_JPG_IMAGE = new ImportedImage("/an/image/path/imported-image.jpg");
    private static final ImportedImage IMAGE_UNDER_BLUE_TREES_DIR = new ImportedImage("/blue/trees/img.png");

    @Test
    void generateFilepath_GivenFixedPath_ShouldOnlyAppendExtension() {
        String outputFilepath = "/test/123";

        FileNameServiceImpl fileNameService = new FileNameServiceImpl(outputFilepath, OutputType.JPG,
            A_NUMBERING_VALUE, A_COUPLE_OF_ITEMS, new CalendarDateProvider());

        String actual = fileNameService.generateFilePath(A_JPG_IMAGE);

        Assertions.assertEquals("/test/123.jpg", actual);
    }

    @ParameterizedTest
    @CsvSource({"/img.jpg,/test/123.jpg", "/img.png,/test/123.png", "/img.pdf,/test/123.pdf"})
    void generateFilepath_GivenSameAsFirstOutputType_ShouldOnlyAppendInitialExtension(String imagePath, String expectedPath) {
        String outputFilepath = "/test/123";
        FileNameServiceImpl fileNameService = new FileNameServiceImpl(outputFilepath, OutputType.SAME_AS_FIRST,
            A_NUMBERING_VALUE, A_COUPLE_OF_ITEMS, new CalendarDateProvider());

        String actual = fileNameService.generateFilePath(new ImportedImage(imagePath));

        Assertions.assertEquals(expectedPath, actual);
    }

    @Test
    void generateFilepath_GivenSameAsFirstOutputTypeWithUnsupportedExtension_ShouldAppendDefaultExtension() {
        String outputFilepath = "/test/123";
        ImportedImage anUnsupportedOutputExtensionImage = new ImportedImage("/image/path/img.psd");
        FileNameServiceImpl fileNameService = new FileNameServiceImpl(outputFilepath, OutputType.SAME_AS_FIRST,
            A_NUMBERING_VALUE, A_COUPLE_OF_ITEMS, new CalendarDateProvider());

        String actual = fileNameService.generateFilePath(anUnsupportedOutputExtensionImage);

        String defaultExtension = "." + OutputType.DEFAULT.name.toLowerCase();
        Assertions.assertEquals(outputFilepath + defaultExtension, actual);
    }

    @Test
    void generateFilepath_GivenRespectiveNumberingModifier_ShouldReplaceModifierWithNumber() {
        String outputFilepath = System.getProperty("java.io.tmpdir") + File.separator + "123_#";
        int anInitialNumberingValue = 12;
        FileNameServiceImpl fileNameService = new FileNameServiceImpl(outputFilepath, OutputType.JPG,
            anInitialNumberingValue, A_COUPLE_OF_ITEMS, new CalendarDateProvider());

        String actual = fileNameService.generateFilePath(A_JPG_IMAGE);

        Assertions.assertTrue(actual.endsWith("123_12.jpg"));
    }

    @ParameterizedTest
    @CsvSource({"-4,-4.jpg,-3.jpg", "0,0.jpg,1.jpg", "23,23.jpg,24.jpg"})
    void generateFilepath_GivenNumberingPathModifier_ShouldReturnIncrementalNumberingPaths(int initialNumbering, String expectedFirstIteration, String expectedSecondIteration) {
        String outputFilepath = "*";
        FileNameServiceImpl fileNameService = new FileNameServiceImpl(outputFilepath, OutputType.JPG, initialNumbering, A_COUPLE_OF_ITEMS, new CalendarDateProvider());

        String firstIteration = fileNameService.generateFilePath(A_JPG_IMAGE);
        Assertions.assertEquals(expectedFirstIteration, firstIteration);

        String secondIteration = fileNameService.generateFilePath(A_JPG_IMAGE);
        Assertions.assertEquals(expectedSecondIteration, secondIteration);
    }

    @ParameterizedTest
    @CsvSource({"1,1.jpg", "31,31.jpg"})
    void generateFilepath_GivenDayPathModifier_ShouldAppendDayOfMonthOnOutputPath(int day, String expectedPath) {
        String outputFilepath = "%D";
        DateProvider fakeDateProvider = Mockito.mock(DateProvider.class);
        Mockito.when(fakeDateProvider.day()).thenReturn(day);
        FileNameServiceImpl fileNameService = new FileNameServiceImpl(outputFilepath, OutputType.JPG, A_NUMBERING_VALUE, A_COUPLE_OF_ITEMS, fakeDateProvider);

        String actual = fileNameService.generateFilePath(A_JPG_IMAGE);

        Assertions.assertEquals(expectedPath, actual);
    }

    @ParameterizedTest
    @CsvSource({"1,1.jpg", "12,12.jpg"})
    void generateFilepath_GivenMonthPathModifier_ShouldAppendDayOfMonthOnOutputPath(int month, String expectedPath) {
        String outputFilepath = "%M";
        DateProvider fakeDateProvider = Mockito.mock(DateProvider.class);
        Mockito.when(fakeDateProvider.month()).thenReturn(month);
        FileNameServiceImpl fileNameService = new FileNameServiceImpl(outputFilepath, OutputType.JPG, A_NUMBERING_VALUE, A_COUPLE_OF_ITEMS, fakeDateProvider);

        String actual = fileNameService.generateFilePath(A_JPG_IMAGE);

        Assertions.assertEquals(expectedPath, actual);
    }

    @ParameterizedTest
    @CsvSource({"1970,1970.jpg", "2024,2024.jpg"})
    void generateFilepath_GivenYearPathModifier_ShouldAppendDayOfMonthOnOutputPath(int year, String expectedPath) {
        String outputFilepath = "%Y";
        DateProvider fakeDateProvider = Mockito.mock(DateProvider.class);
        Mockito.when(fakeDateProvider.year()).thenReturn(year);
        FileNameServiceImpl fileNameService = new FileNameServiceImpl(outputFilepath, OutputType.JPG, A_NUMBERING_VALUE, A_COUPLE_OF_ITEMS, fakeDateProvider);

        String actual = fileNameService.generateFilePath(A_JPG_IMAGE);

        Assertions.assertEquals(expectedPath, actual);
    }

    @Test
    void generateFilepath_GivenOriginalFilenameModifier_ShouldAppendOriginalFilename() {
        String outputFilepath = "%F";
        ImportedImage housesJpg = new ImportedImage("/path/houses.png");
        FileNameServiceImpl fileNameService = new FileNameServiceImpl(outputFilepath, OutputType.JPG,
            A_NUMBERING_VALUE, A_COUPLE_OF_ITEMS, new CalendarDateProvider());

        String actual = fileNameService.generateFilePath(housesJpg);

        Assertions.assertEquals("houses.jpg", actual);
    }

    @Test
    void generateFilepath_GivenOriginalFilepathModifier_ShouldPrependOriginalFilepath() {
        String outputFilepath = "^P/test";
        int anInitialNumberingValue = 12;
        FileNameServiceImpl fileNameService = new FileNameServiceImpl(outputFilepath, OutputType.JPG,
            anInitialNumberingValue, A_COUPLE_OF_ITEMS, new CalendarDateProvider());

        String actual = fileNameService.generateFilePath(IMAGE_UNDER_BLUE_TREES_DIR);

        Assertions.assertEquals("/blue/trees/test.jpg", actual);
    }

    @ParameterizedTest
    @CsvSource({"^P", "^P/"})
    void generateFilepath_GivenOriginalFilepathModifierWithoutFilename_ShouldPrependOriginalFilepathAndFilename(String outputFilepath) {
        FileNameServiceImpl fileNameService = new FileNameServiceImpl(outputFilepath, OutputType.JPG,
            A_NUMBERING_VALUE, A_COUPLE_OF_ITEMS, new CalendarDateProvider());

        String actual = fileNameService.generateFilePath(IMAGE_UNDER_BLUE_TREES_DIR);

        Assertions.assertEquals("/blue/trees/img.jpg", actual);
    }

    @Test
    void generateFilepath_WithoutFilename_ShouldPlaceOriginalImageName() {
        String outputFilepath = "/test/";
        ImportedImage housesJpg = new ImportedImage("/path/houses.png");
        FileNameServiceImpl fileNameService = new FileNameServiceImpl(outputFilepath, OutputType.JPG,
            A_NUMBERING_VALUE, A_COUPLE_OF_ITEMS, new CalendarDateProvider());

        String actual = fileNameService.generateFilePath(housesJpg);

        Assertions.assertEquals("/test/houses.jpg", actual);
    }

    @Test
    void generateFilepath_GivenMultipleNumberingModifiers_ShouldReplaceThemAll() {
        String outputFilepath = "/path/*_*";
        int anInitialNumberingValue = 11;
        FileNameServiceImpl fileNameService = new FileNameServiceImpl(outputFilepath, OutputType.JPG,
            anInitialNumberingValue, A_COUPLE_OF_ITEMS, new CalendarDateProvider());

        String actual = fileNameService.generateFilePath(A_JPG_IMAGE);

        Assertions.assertEquals("/path/11_11.jpg", actual);
    }

}
