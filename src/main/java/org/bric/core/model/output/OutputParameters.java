package org.bric.core.model.output;

import org.bric.core.model.TabParameters;

public final class OutputParameters implements TabParameters {

    private final String outputPath;
    private final String outputFormat;
    private final int numberingStartIndex;
    private final float quality;

    public OutputParameters(String outputPath, String outputFormat, int numberingStartIndex, float quality) {
        this.outputPath = outputPath;
        this.outputFormat = outputFormat;
        this.numberingStartIndex = numberingStartIndex;
        this.quality = quality;
    }

    public int getNumberingStartIndex() {
        return numberingStartIndex;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public float getQuality() {
        return quality;
    }
    
}
