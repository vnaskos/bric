package org.bric.core.model.output;

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
    PDF("PDF");

    public static final OutputType DEFAULT = JPG;
    public final String name;

    OutputType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
