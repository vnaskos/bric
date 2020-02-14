package org.bric.core.model;

public enum DuplicateAction {

    NOT_SET,
    OVERWRITE, SKIP, RENAME, ADD,
    ALWAYS_OVERWRITE, ALWAYS_SKIP, ALWAYS_RENAME, ALWAYS_ADD
}
