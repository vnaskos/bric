package org.bric.core.model.input;

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

    public final String name;

    InputType(String name) {
        this.name = name;
    }

    public static InputType from(String filepath) {
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
        return name;
    }
}
