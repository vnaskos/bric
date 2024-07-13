package org.bric.core.process;

public enum FilePathModifier {

    NUMBERING("*");

    public final String modifier;

    FilePathModifier(String modifier) {
        this.modifier = modifier;
    }
}
