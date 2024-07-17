package org.bric.core.process;

import org.bric.core.input.model.InputType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultFileService implements FileService {

    @Override
    public boolean exists(final String filepath) {
        return new File(filepath).exists();
    }

    @Override
    public List<String> listFiles(File source) {
        if (source == null) {
            return Collections.emptyList();
        }

        if (source.isFile()) {
            return Collections.singletonList(source.getAbsolutePath());
        }

        if (source.isDirectory()) {
            return scanDirectory(source);
        }

        return Collections.emptyList();
    }

    private List<String> scanDirectory(File file) {
        File[] children = file.listFiles();

        if (children == null) {
            return Collections.emptyList();
        }

        List<String> scannedFiles = new ArrayList<>();
        for (File child : children) {
            if (child.isFile() && InputType.isSupported(child.getAbsolutePath())) {
                scannedFiles.add(child.getPath());
            } else if (child.isDirectory()) {
                scannedFiles.addAll(scanDirectory(child));
            }
        }
        return scannedFiles;
    }
}
