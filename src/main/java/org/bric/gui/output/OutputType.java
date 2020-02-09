package org.bric.gui.output;

public enum OutputType {

    SAME_AS_FIRST("SAME AS FIRST"),
    JPG("JPG"),
    JPEG("JPEG"),
    PNG("PNG"),
    GIF("GIF"),
    BMP("BMP"),
    TIF("TIF"),
    TIFF("TIFF"),
    PGM("PGM"),
    PBM("PBM"),
    PNM("PNM"),
    PPM("PPM"),
    PDF("PDF");

    public final String name;

    OutputType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
