package org.bric.core.process;

import java.io.File;
import java.util.List;

public interface FileService {

    boolean exists(String filepath);

    List<String> listFiles(File source);
}
