package org.bric.gui.dialog;

import org.bric.core.model.DuplicateAction;
import org.bric.core.process.NamingCollisionResolver;

import javax.swing.*;

public class DialogNamingCollisionResolver implements NamingCollisionResolver {

    @Override
    public DuplicateAction resolve(final String file) {
        return (DuplicateAction) JOptionPane.showInputDialog(
            null, String.format("This image%n%s%n already exists on the output folder", file),
            "Warning Duplicate", JOptionPane.QUESTION_MESSAGE,
            null, availableActions, DuplicateAction.RENAME);
    }
}
