package org.bric.core.process;

import java.io.File;

public class DefaultFileService implements FileService {

    @Override
    public boolean exists(final String filepath) {
        return new File(filepath).exists();
    }
}
