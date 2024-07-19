package org.bric.core.process;

import java.awt.image.BufferedImage;
import java.util.function.Supplier;

public interface ImageService {

    BufferedImage load(String fullPath);

    void save(Supplier<BufferedImage> supplier, ImageSaveParameters parameters);
}
