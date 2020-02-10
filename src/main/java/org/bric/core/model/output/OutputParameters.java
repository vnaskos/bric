package org.bric.core.model.output;

import org.bric.core.model.TabParameters;

public final class OutputParameters implements TabParameters {

    private final String outputPath;
    private final OutputType outputFormat;
    private final int numberingStartIndex;
    private final float quality;

    public OutputParameters(String outputPath, OutputType outputFormat, int numberingStartIndex, float quality) {
        this.outputPath = outputPath;
        this.outputFormat = outputFormat;
        this.numberingStartIndex = numberingStartIndex;
        this.quality = quality;
    }

    public int getNumberingStartIndex() {
        return numberingStartIndex;
    }

    public OutputType getOutputType() {
        return outputFormat;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public float getQuality() {
        return quality;
    }
    
}
