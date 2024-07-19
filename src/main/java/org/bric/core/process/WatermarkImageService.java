package org.bric.core.process;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface WatermarkImageService {

    BufferedImage textToImage(String content, Color color, Font font, float alpha, boolean isPlainText);

}
