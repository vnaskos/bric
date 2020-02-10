package org.bric.core.model.input;

import org.bric.utils.Utils;

public enum GenerationMethod {
    ON_IMPORT, ON_DEMAND, NEVER;

    public static GenerationMethod thumbnail() {
        boolean disabled = ! Utils.prefs.getBoolean("thumbnail", true);
        boolean onImport = Utils.prefs.getInt("thumbWay", 0) == 0;
        return disabled ? GenerationMethod.NEVER
                        : onImport ? GenerationMethod.ON_IMPORT
                                   : GenerationMethod.ON_DEMAND;
    }

    public static GenerationMethod metadata() {
        boolean disabled = ! Utils.prefs.getBoolean("metadata", true);
        boolean onImport = Utils.prefs.getInt("metaWay", 0) == 0;
        return disabled ? GenerationMethod.NEVER
                        : onImport ? GenerationMethod.ON_IMPORT
                                   : GenerationMethod.ON_DEMAND;
    }
}
