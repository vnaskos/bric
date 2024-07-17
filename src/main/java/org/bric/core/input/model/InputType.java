package org.bric.core.input.model;

public enum InputType {

    JPG("jpg"),
    JPEG("jpeg"),
    PNG("png"),
    BMP("bmp"),
    TIFF("tiff"),
    TIF("tif"),
    GIF("gif"),
    PSD("psd"),
    PNM("pnm"),
    PPM("ppm"),
    PGM("pgm"),
    PBM("pbm"),
    WBMP("wbmp"),
    PDF("pdf");

    public final String type;

    InputType(String type) {
        this.type = type;
    }

    public static InputType from(String filepath) {
        if (filepath == null) {
            throw new IllegalArgumentException("Null filepath is not allowed");
        }

        String extension = filepath.substring(filepath.lastIndexOf('.')+1);

        return InputType.valueOf(extension.toUpperCase());
    }

    public static boolean isSupported(String filepath) {
        try {
            InputType.from(filepath);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    @Override
    public String toString() {
        return type;
    }
}
