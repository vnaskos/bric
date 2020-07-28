package org.bric.core.process.model;

import org.bric.core.model.TabParameters;

public class RotateParameters implements TabParameters {
    private boolean enabled;
    private boolean custom;
    private boolean predefined;
    private boolean random;
    private boolean differentValues;
    private boolean limit;
    private int from;
    private int to;
    private int angle;
    private int action;
    private int randomAngle;

    public int getRandomAngle() {
        return randomAngle;
    }

    public void setRandomAngle(int randomAngle) {
        this.randomAngle = randomAngle;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }
    
    public boolean isPredefined() {
        return predefined;
    }

    public void setPredefined(boolean predefined) {
        this.predefined = predefined;
    }

    public boolean isDifferentValues() {
        return differentValues;
    }

    public void setDifferentValues(boolean differentValues) {
        this.differentValues = differentValues;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public boolean isLimit() {
        return limit;
    }

    public void setLimit(boolean limit) {
        this.limit = limit;
    }

    public boolean isRandom() {
        return random;
    }

    public void setRandom(boolean random) {
        this.random = random;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }
    
}
