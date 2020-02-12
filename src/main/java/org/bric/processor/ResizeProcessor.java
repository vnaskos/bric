package org.bric.processor;

import com.mortennobel.imagescaling.*;
import org.bric.imageEditParameters.ResizeParameters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ResizeProcessor extends ImageProcessor<ResizeParameters>  {

    private int width;
    private int height;

    public ResizeProcessor(ResizeParameters params) {
        super(params);
    }

    @Override
    public BufferedImage process(BufferedImage image) {
        computeWidthHeight(image);

        ResampleOp resampleOp = new ResampleOp(width, height);
        resampleOp.setUnsharpenMask(getSharpen());

        BufferedImage resizedImage;

        if (params.getFilter().equalsIgnoreCase("auto")) {
            if (image.getWidth() < width || image.getHeight() < height) {
                resizedImage = graphicsFilters(image, "bicubic", width, height);
            } else {
                resizedImage = graphicsFilters(image, "bilenear", width, height);
            }
        } else if (params.getFilter().equalsIgnoreCase("bicubic")) {
            resampleOp.setFilter(ResampleFilters.getBiCubicFilter());
            resizedImage = resampleOp.filter(image, null);
        } else if (params.getFilter().equalsIgnoreCase("bicubichighfreqresponse")) {
            resampleOp.setFilter(ResampleFilters.getBiCubicHighFreqResponse());
            resizedImage = resampleOp.filter(image, null);
        } else if (params.getFilter().equalsIgnoreCase("lanczos3")) {
            resampleOp.setFilter(ResampleFilters.getLanczos3Filter());
            resizedImage = resampleOp.filter(image, null);
        } else if (params.getFilter().equalsIgnoreCase("multistep")) {
            MultiStepRescaleOp multiStepRescaleOp = new MultiStepRescaleOp(width, height);
            multiStepRescaleOp.setUnsharpenMask(getSharpen());
            resizedImage = multiStepRescaleOp.filter(image, null);
        } else if (params.getFilter().equalsIgnoreCase("bell")) {
            resampleOp.setFilter(ResampleFilters.getBellFilter());
            resizedImage = resampleOp.filter(image, null);
        } else if (params.getFilter().equalsIgnoreCase("box")) {
            resampleOp.setFilter(ResampleFilters.getBoxFilter());
            resizedImage = resampleOp.filter(image, null);
        } else if (params.getFilter().equalsIgnoreCase("hermite")) {
            resampleOp.setFilter(ResampleFilters.getHermiteFilter());
            resizedImage = resampleOp.filter(image, null);
        } else if (params.getFilter().equalsIgnoreCase("mitchell")) {
            resampleOp.setFilter(ResampleFilters.getMitchellFilter());
            resizedImage = resampleOp.filter(image, null);
        } else if (params.getFilter().equalsIgnoreCase("triangle")) {
            resampleOp.setFilter(ResampleFilters.getTriangleFilter());
            resizedImage = resampleOp.filter(image, null);
        } else if (params.getFilter().equalsIgnoreCase("thumpnail")) {
            ThumpnailRescaleOp thumpnailRescaleOp = new ThumpnailRescaleOp(width, height);
            thumpnailRescaleOp.setUnsharpenMask(getSharpen());
            resizedImage = thumpnailRescaleOp.filter(image, null);
        } else {
            if (params.getFilter().equalsIgnoreCase("nearest_neighbor")) {
                resizedImage = graphicsFilters(image, "nearest_neighbor", width, height);
            } else if(params.getFilter().equalsIgnoreCase("bilinear")) {
                resizedImage = graphicsFilters(image, "bilinear", width, height);
            } else {
                resizedImage = graphicsFilters(image, "bicubic", width, height);
            }
        }

        return resizedImage;
    }

    public AdvancedResizeOp.UnsharpenMask getSharpen(){
        switch (params.getSharpen()) {
            case "none":
                return AdvancedResizeOp.UnsharpenMask.None;
            case "normal":
                return AdvancedResizeOp.UnsharpenMask.Normal;
            case "oversharpened":
                return AdvancedResizeOp.UnsharpenMask.Oversharpened;
            case "soft":
                return AdvancedResizeOp.UnsharpenMask.Soft;
            default:
                return AdvancedResizeOp.UnsharpenMask.VerySharp;
        }
    }

    public BufferedImage graphicsFilters(BufferedImage srcImage, String filter, int width, int height) {
        int type = srcImage.getType() == BufferedImage.TYPE_CUSTOM ? BufferedImage.TYPE_INT_ARGB : srcImage.getType();
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.setComposite(AlphaComposite.Src);

        if (params.getRendering() == 0) {
            g.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
        } else {
            g.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_SPEED);
        }

        if (params.isAntialising()) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
        }

        if (filter.equalsIgnoreCase("nearest_neighbor")) {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        } else if(filter.equalsIgnoreCase("bilinear")) {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        } else { //bicubic
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        }
        g.drawImage(srcImage, 0, 0, width, height, null);
        g.dispose();

        return resizedImage;
    }

    private void computeWidthHeight(BufferedImage image){
        width = params.getWidth();
        height = params.getHeight();
        boolean maintain = params.isMaintainAspectRatio();
        boolean consider = params.isConsiderOrientation();
        if (params.getUnits() == 0) {
            if (maintain && !consider) {
                if (height == 0) {
                    height = (width * image.getHeight()) / image.getWidth();
                } else {
                    width = (height * image.getWidth()) / image.getHeight();
                }
            } else if (!maintain && consider) {
                if (image.getWidth() < image.getHeight()) { //portrait
                    width = params.getHeight();
                    height = params.getWidth();
                }
            } else if (maintain) {
                if (image.getWidth() >= image.getHeight()) { //landscape
                    height = (width * image.getHeight()) / image.getWidth();
                } else { //portrait
                    width = (height * image.getWidth()) / image.getHeight();
                }
            }
        } else if (params.getUnits() == 1) {
            width = (image.getWidth() * width) / 100;
            height = (image.getHeight() * height) / 100;
        }
    }
    
    
}
