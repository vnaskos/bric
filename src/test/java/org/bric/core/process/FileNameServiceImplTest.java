package org.bric.core.process;

import org.bric.core.input.model.ImportedImage;
import org.bric.core.model.DuplicateAction;
import org.bric.core.model.output.OutputType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.stream.Stream;

import static org.bric.core.test.ImportedImageTestFactory.anImage;

class FileNameServiceImplTest {

    private static final int A_NUMBERING_VALUE = 20;
    private static final ImportedImage A_JPG_IMAGE = anImage();
    private final FileService fakeFileService = Mockito.mock(FileService.class);
    private final DateProvider fakeDateProvider = Mockito.mock(DateProvider.class);
    private final NamingCollisionResolver fakeNamingCollisionResolver = Mockito.mock(NamingCollisionResolver.class);

    @Test
    void generateFilepath_GivenFixedPath_ShouldOnlyAppendExtension() {
        String outputFilepath = "/test/123";
        FileNameServiceImpl fileNameService = createFileNameService(outputFilepath, OutputType.JPG, A_NUMBERING_VALUE);

        String actual = fileNameService.generateFilePath(A_JPG_IMAGE);

        Assertions.assertEquals("/test/123.jpg", actual);
    }

    @ParameterizedTest
    @CsvSource({"/img.jpg,/test/123.jpg", "/img.png,/test/123.png", "/img.pdf,/test/123.pdf"})
    void generateFilepath_GivenSameAsFirstOutputType_ShouldOnlyAppendInitialExtension(String imagePath, String expectedPath) {
        String outputFilepath = "/test/123";
        FileNameServiceImpl fileNameService = createFileNameService(outputFilepath, OutputType.SAME_AS_FIRST, A_NUMBERING_VALUE);

        String actual = fileNameService.generateFilePath(anImage(imagePath));

        Assertions.assertEquals(expectedPath, actual);
    }

    @Test
    void generateFilepath_GivenSameAsFirstOutputTypeWithUnsupportedExtension_ShouldAppendDefaultExtension() {
        String outputFilepath = "/test/123";
        ImportedImage anUnsupportedOutputExtensionImage = anImage("/image/path/img.psd");
        FileNameServiceImpl fileNameService = createFileNameService(outputFilepath, OutputType.SAME_AS_FIRST, A_NUMBERING_VALUE);

        String actual = fileNameService.generateFilePath(anUnsupportedOutputExtensionImage);

        Assertions.assertEquals("/test/123.jpg", actual);
    }

    @Test
    void generateFilepath_GivenRespectiveNumberingModifier_ShouldReplaceModifierWithNumber() {
        String outputFilepath = "/tmp/123_#";
        int anInitialNumberingValue = 12;
        Mockito.when(fakeFileService.exists("/tmp/123_12.jpg")).thenReturn(true);
        FileNameServiceImpl fileNameService = createFileNameService(outputFilepath, OutputType.JPG, anInitialNumberingValue);

        String actual = fileNameService.generateFilePath(A_JPG_IMAGE);

        Assertions.assertEquals("/tmp/123_13.jpg", actual);
    }

    @ParameterizedTest
    @CsvSource({"-4,-4.jpg,-3.jpg", "0,0.jpg,1.jpg", "23,23.jpg,24.jpg"})
    void generateFilepath_GivenNumberingPathModifier_ShouldReturnIncrementalNumberingPaths(int initialNumbering, String expectedFirstIteration, String expectedSecondIteration) {
        String outputFilepath = "*";
        FileNameServiceImpl fileNameService = createFileNameService(outputFilepath, OutputType.JPG, initialNumbering);

        String firstIteration = fileNameService.generateFilePath(A_JPG_IMAGE);
        Assertions.assertEquals(expectedFirstIteration, firstIteration);

        String secondIteration = fileNameService.generateFilePath(A_JPG_IMAGE);
        Assertions.assertEquals(expectedSecondIteration, secondIteration);
    }

    @ParameterizedTest
    @CsvSource({"1,1.jpg", "31,31.jpg"})
    void generateFilepath_GivenDayPathModifier_ShouldAppendDayOfMonthOnOutputPath(int day, String expectedPath) {
        String outputFilepath = "%D";
        Mockito.when(fakeDateProvider.day()).thenReturn(day);
        FileNameServiceImpl fileNameService = createFileNameService(outputFilepath, OutputType.JPG, A_NUMBERING_VALUE);

        String actual = fileNameService.generateFilePath(A_JPG_IMAGE);

        Assertions.assertEquals(expectedPath, actual);
    }

    @ParameterizedTest
    @CsvSource({"1,1.jpg", "12,12.jpg"})
    void generateFilepath_GivenMonthPathModifier_ShouldAppendDayOfMonthOnOutputPath(int month, String expectedPath) {
        String outputFilepath = "%M";
        Mockito.when(fakeDateProvider.month()).thenReturn(month);
        FileNameServiceImpl fileNameService = createFileNameService(outputFilepath, OutputType.JPG, A_NUMBERING_VALUE);

        String actual = fileNameService.generateFilePath(A_JPG_IMAGE);

        Assertions.assertEquals(expectedPath, actual);
    }

    @ParameterizedTest
    @CsvSource({"1970,1970.jpg", "2024,2024.jpg"})
    void generateFilepath_GivenYearPathModifier_ShouldAppendDayOfMonthOnOutputPath(int year, String expectedPath) {
        String outputFilepath = "%Y";
        Mockito.when(fakeDateProvider.year()).thenReturn(year);
        FileNameServiceImpl fileNameService = createFileNameService(outputFilepath, OutputType.JPG, A_NUMBERING_VALUE);

        String actual = fileNameService.generateFilePath(A_JPG_IMAGE);

        Assertions.assertEquals(expectedPath, actual);
    }

    @Test
    void generateFilepath_GivenOriginalFilenameModifier_ShouldAppendOriginalFilename() {
        String outputFilepath = "%F";
        ImportedImage housesJpg = anImage("/path/houses.png");
        FileNameServiceImpl fileNameService = createFileNameService(outputFilepath, OutputType.JPG, A_NUMBERING_VALUE);

        String actual = fileNameService.generateFilePath(housesJpg);

        Assertions.assertEquals("houses.jpg", actual);
    }

    @Test
    void generateFilepath_GivenOriginalFilepathModifier_ShouldPrependOriginalFilepath() {
        String outputFilepath = "^P/test";
        ImportedImage anImageUnderBlueTreesDir = anImage("/blue/trees/test.jpg");
        FileNameServiceImpl fileNameService = createFileNameService(outputFilepath, OutputType.JPG, A_NUMBERING_VALUE);

        String actual = fileNameService.generateFilePath(anImageUnderBlueTreesDir);

        Assertions.assertEquals("/blue/trees/test.jpg", actual);
    }

    @ParameterizedTest
    @CsvSource({"^P", "^P/"})
    void generateFilepath_GivenOriginalFilepathModifierWithoutFilename_ShouldPrependOriginalFilepathAndFilename(String outputFilepath) {
        ImportedImage anImageUnderBlueTreesDir = anImage("/blue/trees/img.jpg");
        FileNameServiceImpl fileNameService = createFileNameService(outputFilepath, OutputType.JPG, A_NUMBERING_VALUE);

        String actual = fileNameService.generateFilePath(anImageUnderBlueTreesDir);

        Assertions.assertEquals("/blue/trees/img.jpg", actual);
    }


    @Test
    void generateFilepath_WithoutFilename_ShouldPlaceOriginalImageName() {
        ImportedImage housesJpg = anImage("/path/houses.png");
        String outputFilepath = "/test/";
        FileNameServiceImpl fileNameService = createFileNameService(outputFilepath, OutputType.JPG, A_NUMBERING_VALUE);

        String actual = fileNameService.generateFilePath(housesJpg);

        Assertions.assertEquals("/test/houses.jpg", actual);
    }

    @Test
    void generateFilepath_GivenMultipleNumberingModifiers_ShouldReplaceThemAll() {
        String outputFilepath = "/path/*_*";
        int anInitialNumberingValue = 11;
        FileNameServiceImpl fileNameService = createFileNameService(outputFilepath, OutputType.JPG, anInitialNumberingValue);

        String actual = fileNameService.generateFilePath(A_JPG_IMAGE);

        Assertions.assertEquals("/path/11_11.jpg", actual);
    }

    @ParameterizedTest
    @MethodSource("provideNamingCollisionCases")
    void preventNamingCollision_GivenAction_ShouldResolveCollision(String outputFilepath, DuplicateAction action, String resolvedFilepath) {
        Mockito.when(fakeFileService.exists(outputFilepath)).thenReturn(true);
        Mockito.when(fakeNamingCollisionResolver.resolve(Mockito.anyString())).thenReturn(action);
        FileNameServiceImpl fileNameService = createFileNameService("/tmp/%F", OutputType.JPG, 1);

        String actual = fileNameService.preventNamingCollision(outputFilepath);

        Assertions.assertEquals(resolvedFilepath, actual);
    }

    static Stream<Arguments> provideNamingCollisionCases() {
        return Stream.of(
            Arguments.of("/tmp/out.jpg", DuplicateAction.SKIP, null),
            Arguments.of("/tmp/out.jpg", DuplicateAction.ALWAYS_SKIP, null),
            Arguments.of("/tmp/out.jpg", DuplicateAction.RENAME, "/tmp/out(1).jpg"),
            Arguments.of("/tmp/out.jpg", DuplicateAction.ALWAYS_RENAME, "/tmp/out(1).jpg"),
            Arguments.of("/tmp/out.jpg", DuplicateAction.OVERWRITE, "/tmp/out.jpg"),
            Arguments.of("/tmp/out.jpg", DuplicateAction.ALWAYS_OVERWRITE, "/tmp/out.jpg")
        );
    }

    @ParameterizedTest
    @MethodSource("provideNamingCollisionInvalidCases")
    void preventNamingCollision_GivenInvalidAction_ShouldTryAgainUntilValidAction(DuplicateAction invalidAction) {
        Mockito.when(fakeFileService.exists("/tmp/out.jpg")).thenReturn(true);
        Mockito.when(fakeNamingCollisionResolver.resolve(Mockito.anyString()))
            .thenReturn(invalidAction)
            .thenReturn(DuplicateAction.SKIP);
        FileNameServiceImpl fileNameService = createFileNameService("/tmp/%F", OutputType.JPG, 1);

        String actual = fileNameService.preventNamingCollision("/tmp/out.jpg");

        Assertions.assertNull(actual);
    }

    static Stream<Arguments> provideNamingCollisionInvalidCases() {
        return Stream.of(
            Arguments.of((DuplicateAction) null),
            Arguments.of(DuplicateAction.NOT_SET),
            Arguments.of(DuplicateAction.ADD),
            Arguments.of(DuplicateAction.ALWAYS_ADD)
        );
    }

    private FileNameServiceImpl createFileNameService(String outputFilepath, OutputType outputType, int initialNumbering) {
        return new FileNameServiceImpl(
            outputFilepath,
            outputType,
            initialNumbering,
            fakeFileService,
            fakeDateProvider,
            fakeNamingCollisionResolver);
    }

}
