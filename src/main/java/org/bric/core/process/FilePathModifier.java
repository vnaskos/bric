package org.bric.core.process;

public enum FilePathModifier {

    NUMBERING("*"),
    DAY("%D"),
    MONTH("%M"),
    YEAR("%Y"),
    ORIGINAL_FILENAME("%F"),
    ORIGINAL_FILEPATH("^P");

    public final String modifier;

    FilePathModifier(String modifier) {
        this.modifier = modifier;
    }
}
