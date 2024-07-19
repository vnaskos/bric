package org.bric.core.process;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.Stream;

public interface PdfService {

    List<BufferedImage> readAsImages(String fullPath, int startPage, int endPage);

    void mergeToPdf(Stream<BufferedImage> input, String outputFullPath);
}
