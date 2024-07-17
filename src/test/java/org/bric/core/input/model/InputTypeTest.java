package org.bric.core.input.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class InputTypeTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "/img.jpg",
        "/img.Jpg",
        "/img.JPG",
        "/img.jpeg",
        "/img.png",
        "/img.bmp",
        "/img.tiff",
        "/img.tif",
        "/img.gif",
        "/img.psd",
        "/img.pnm",
        "/img.ppm",
        "/img.pgm",
        "/img.pbm",
        "/img.wbmp",
        "/img.pdf"
    })
    void isSupported_GivenValidInputType_ReturnsTrue(String filepath) {
        boolean actual = InputType.isSupported(filepath);

        Assertions.assertTrue(actual);
    }

    @Test
    void isSupported_GivenInvalidInputType_ReturnsFalse() {
        Assertions.assertFalse(InputType.isSupported(null));
        Assertions.assertFalse(InputType.isSupported("/not-an-img.ini"));
        Assertions.assertFalse(InputType.isSupported("/not-an-img"));
        Assertions.assertFalse(InputType.isSupported(""));
        Assertions.assertFalse(InputType.isSupported("/not-an-img.jpg.ini"));
    }
}
