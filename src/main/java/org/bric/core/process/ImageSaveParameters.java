package org.bric.core.process;

import org.bric.core.model.output.OutputType;

public class ImageSaveParameters {

    public final String filepath;
    public final Float quality;
    public final OutputType outputType;

    public ImageSaveParameters(String filepath, Float quality, OutputType outputType) {
        this.filepath = filepath;
        this.quality = quality;
        this.outputType = outputType;
    }
}
