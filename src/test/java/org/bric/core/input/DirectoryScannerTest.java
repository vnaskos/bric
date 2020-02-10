package org.bric.core.input;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class DirectoryScannerTest {

    private static final String DUCK_JPG = "duck.jpg";
    private static final String DOG_JPEG = "dog.jpeg";

    @Test
    public void listFiles_ShouldReturnEmpty_WhenInvalidSourceFile() {
        File nonExistingFile = new File("/does/not/exist");

        List<String> scannedFiles = DirectoryScanner.listFiles(nonExistingFile);

        assertEquals(Collections.emptyList(), scannedFiles);
    }

    @Test
    public void listFiles_ShouldReturnSingleFile_WhenSourceIsNotDirectory() {
        File anImageFile = file(DUCK_JPG);

        List<String> scannedFiles = DirectoryScanner.listFiles(anImageFile);
        List<String> expected = Collections.singletonList(anImageFile.getAbsolutePath());

        assertEquals(expected, scannedFiles);
    }

    @Test
    public void listFiles_ShouldReturnFiles_WhenSourceIsDirectory(@TempDir Path tempDir) {
        File duck = copyFile(DUCK_JPG, tempDir);
        File dog = copyFile(DOG_JPEG, tempDir);

        List<String> scannedFiles = DirectoryScanner.listFiles(tempDir.toFile());
        List<String> expected = Arrays.asList(duck.getAbsolutePath(), dog.getAbsolutePath());

        assertIterableEquals(expected, scannedFiles);
    }

    @Test
    public void listFiles_ShouldReturnFilesRecursively_WhenSourceDirectoryHasSubdirectories(@TempDir Path tempDir) {
        File duck = copyFile(DUCK_JPG, tempDir);
        File dog = copyFile(DOG_JPEG, tempDir.resolve("subdir"));

        List<String> scannedFiles = DirectoryScanner.listFiles(tempDir.toFile());
        List<String> expected = Arrays.asList(duck.getAbsolutePath(), dog.getAbsolutePath());

        assertIterableEquals(expected, scannedFiles);
    }

    @Test
    public void listFiles_ShouldReturnOnlySupportedFiles_WhenDirectoryHasMixedContent(@TempDir Path tempDir) throws IOException {
        File duck = copyFile(DUCK_JPG, tempDir);
        File notSupported = tempDir.resolve("not-supported-file.unsupported").toFile();
        Files.write(notSupported.toPath(), Collections::emptyListIterator);

        List<String> scannedFiles = DirectoryScanner.listFiles(tempDir.toFile());
        List<String> expected = Collections.singletonList(duck.getAbsolutePath());

        assertIterableEquals(expected, scannedFiles);
    }

    private File file(String name) {
        URL url = this.getClass().getResource(File.separator + name);
        return new File(url.getFile());
    }

    private File copyFile(String testFile, Path destDir) {
        try {
            if (!destDir.toFile().exists() && !destDir.toFile().mkdirs()) {
                throw new Exception("Couldn't create destination directory");
            }
            File copiedFile = Files.copy(file(testFile).toPath(),
                                         destDir.resolve(testFile),
                                         StandardCopyOption.REPLACE_EXISTING)
                                    .toFile();
            Objects.requireNonNull(copiedFile);
            return copiedFile;
        } catch (Exception e) {
            fail("Test file copy failed");
            return new File("");
        }
    }
}
