package org.bric.core.process;

import org.bric.core.model.DuplicateAction;

public interface NamingCollisionResolver {

    DuplicateAction[] availableActions = {
        DuplicateAction.OVERWRITE, DuplicateAction.ALWAYS_OVERWRITE,
        DuplicateAction.SKIP, DuplicateAction.ALWAYS_SKIP,
        DuplicateAction.RENAME, DuplicateAction.ALWAYS_RENAME
    };

    DuplicateAction resolve(String file);

}
