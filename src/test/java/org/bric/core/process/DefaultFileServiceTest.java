package org.bric.core.process;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class DefaultFileServiceTest {

    private static final String DUCK_JPG = "duck.jpg";
    private static final String DOG_JPEG = "dog.jpeg";

    @Test
    void listFiles_GivenInvalidSourceFile_ShouldReturnEmpty() {
        File nonExistingFile = new File("/does/not/exist");

        List<String> scannedFiles = defaultFileService().listFiles(nonExistingFile);

        assertEquals(Collections.emptyList(), scannedFiles);
    }

    @Test
    void listFiles_GivenImageFile_ShouldReturnSingleFile() {
        File anImageFile = file(DUCK_JPG);

        List<String> scannedFiles = defaultFileService().listFiles(anImageFile);
        List<String> expected = Collections.singletonList(anImageFile.getAbsolutePath());

        assertEquals(expected, scannedFiles);
    }

    @Test
    void listFiles_GivenNullFile_ShouldReturnEmpty() {
        List<String> scannedFiles = defaultFileService().listFiles(null);

        assertEquals(Collections.emptyList(), scannedFiles);
    }

    @Test
    void listFiles_GivenCorruptedDirectory_ShouldReturnEmpty() {
        File dir = dir("corrupted");
        Mockito.when(dir.listFiles()).thenReturn(null);

        List<String> scannedFiles = defaultFileService().listFiles(dir);

        assertEquals(Collections.emptyList(), scannedFiles);
    }

    @Test
    void listFiles_GivenEmptyDirectory_ShouldReturnEmpty() {
        File dir = dir("/tmp/");

        List<String> scannedFiles = defaultFileService().listFiles(dir);

        assertEquals(Collections.emptyList(), scannedFiles);
    }

    @Test
    void listFiles_GivenDirectory_ShouldReturnAllSupportedContainedFiles() {
        File duck = file(DUCK_JPG);
        File dog = file(DOG_JPEG);
        File dir = dir("/tmp/", duck, dog);

        List<String> scannedFiles = defaultFileService().listFiles(dir);
        List<String> expected = Arrays.asList(duck.getAbsolutePath(), dog.getAbsolutePath());

        assertIterableEquals(expected, scannedFiles);
    }

    @Test
    void listFiles_GivenDirectoryWithSubdirectories_ShouldReturnAllSupportedContainedFiles() {
        File duck = file(DUCK_JPG);
        File dog = file(DOG_JPEG);
        File subDir = dir("/tmp/sub/", dog);
        File dir = dir("/tmp/", duck, subDir);

        List<String> scannedFiles = defaultFileService().listFiles(dir);
        List<String> expected = Arrays.asList(duck.getAbsolutePath(), dog.getAbsolutePath());

        assertIterableEquals(expected, scannedFiles);
    }

    @Test
    void listFiles_GivenDirectoryHasMixedContent_ShouldReturnOnlySupportedFiles() {
        File duck = file(DUCK_JPG);
        File unsupportedFile = file("not-supported-file.unsupported");
        File dir = dir("/tmp/", duck, unsupportedFile);

        List<String> scannedFiles = defaultFileService().listFiles(dir);
        List<String> expected = Collections.singletonList(duck.getAbsolutePath());

        assertIterableEquals(expected, scannedFiles);
    }

    private DefaultFileService defaultFileService() {
        return new DefaultFileService();
    }

    private File file(String path) {
        File fakeFile = Mockito.mock(File.class);
        Mockito.when(fakeFile.exists()).thenReturn(true);
        Mockito.when(fakeFile.isFile()).thenReturn(true);
        Mockito.when(fakeFile.isDirectory()).thenReturn(false);
        Mockito.when(fakeFile.getPath()).thenReturn(path);
        Mockito.when(fakeFile.getAbsolutePath()).thenReturn(path);
        return fakeFile;
    }

    private File dir(String path, File... containedFiles) {
        File fakeDir = Mockito.mock(File.class);
        Mockito.when(fakeDir.exists()).thenReturn(true);
        Mockito.when(fakeDir.isFile()).thenReturn(false);
        Mockito.when(fakeDir.isDirectory()).thenReturn(true);
        Mockito.when(fakeDir.getPath()).thenReturn(path);
        Mockito.when(fakeDir.getAbsolutePath()).thenReturn(path);
        Mockito.when(fakeDir.listFiles()).thenReturn(containedFiles);
        return fakeDir;
    }
}
