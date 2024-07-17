package org.bric.core.input.model;

import org.bric.core.test.ImportedImageTestFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ImportedImageTest {

    @Test
    void constructor_GivenNullPath_ThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> ImportedImageTestFactory.anImage(null));
    }

    @ParameterizedTest
    @CsvSource(value = {
        "image.png,image",
        "/tmp/image.jpg,image",
        "/longer/path/to/image/file.gif,file"
    })
    void getName_GivenPath_ReturnsNameWithoutExtension(String path, String expectedName) {
        ImportedImage image = ImportedImageTestFactory.anImage(path);

        String actual = image.getName();

        Assertions.assertEquals(expectedName, actual);
    }
}
