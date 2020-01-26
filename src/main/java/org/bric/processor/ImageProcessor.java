/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bric.processor;

import java.awt.image.BufferedImage;
import org.bric.imageEditParameters.ImageEditParameters;

/**
 *
 * @author vasilis
 */
public interface ImageProcessor {
    public BufferedImage process(BufferedImage src);
}
