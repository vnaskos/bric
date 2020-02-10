package org.bric.core.input;

import org.bric.core.model.input.InputType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DirectoryScanner {

    public static List<String> listFiles(File source) {
        if (source.isFile()) {
            return Collections.singletonList(source.getAbsolutePath());
        } else if (source.isDirectory()) {
            List<String> scannedFiles = new ArrayList<>();
            scanDirectory(source, scannedFiles);
            return scannedFiles;
        }
        return Collections.emptyList();
    }

    private static void scanDirectory(File file, List<String> list) {
        File[] children = file.listFiles();

        if (children == null) {
            return;
        }

        for (File child : children) {
            if (child.isFile() && child.getName().contains(".")) {
                if (InputType.isSupported(child.getAbsolutePath())) {
                    list.add(child.getPath());
                }
            } else if (child.isDirectory()) {
                scanDirectory(child, list);
            }
        }
    }
}
