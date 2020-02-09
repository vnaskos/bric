/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bric.imageEditParameters;

import org.bric.core.model.TabParameters;

/**
 *
 * @author vasilis
 */
public class ResizeParameters implements TabParameters {
    private boolean enabled;
    private boolean maintainAspectRatio;
    private boolean considerOrientation;
    private int width;
    private int height;
    private int units;
    private boolean antialising;
    private int rendering;
    private String filter;
    private String sharpen;

    public int getRendering() {
        return rendering;
    }

    public void setRendering(int rendering) {
        this.rendering = rendering;
    }
    
    public String getSharpen() {
        return sharpen;
    }

    public void setSharpen(String sharpen) {
        this.sharpen = sharpen;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String interpolation) {
        this.filter = interpolation;
    }

    public boolean isAntialising() {
        return antialising;
    }

    public void setAntialising(boolean antialising) {
        this.antialising = antialising;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public boolean isConsiderOrientation() {
        return considerOrientation;
    }

    public void setConsiderOrientation(boolean considerOrientation) {
        this.considerOrientation = considerOrientation;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isMaintainAspectRatio() {
        return maintainAspectRatio;
    }

    public void setMaintainAspectRatio(boolean maintainAspectRatio) {
        this.maintainAspectRatio = maintainAspectRatio;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
