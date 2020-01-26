/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bric.imageEditParameters;

/**
 *
 * @author vasilis
 */
public class OutputParameters implements ImageEditParameters{
    private String outputPath;
    private String outputFormat;
    private int numberingStartIndex;
    private float quality;
//    private int dpiHorizontal;
//    private int dpiVertical;
    
//    public int getDpiHorizontal() {
//        return dpiHorizontal;
//    }
//
//    public void setDpiHorizontal(int dpiHorizontal) {
//        this.dpiHorizontal = dpiHorizontal;
//    }
//
//    public int getDpiVertical() {
//        return dpiVertical;
//    }
//
//    public void setDpiVertical(int dpiVertical) {
//        this.dpiVertical = dpiVertical;
//    }
    
    public int getNumberingStartIndex() {
        return numberingStartIndex;
    }

    public void setNumberingStartIndex(int numberingStartIndex) {
        this.numberingStartIndex = numberingStartIndex;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public float getQuality() {
        return quality;
    }

    public void setQuality(float quality) {
        this.quality = quality;
    }
    
}
