/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bric.processor;

import java.awt.image.BufferedImage;

/**
 *
 * @author vasilis
 */
public interface ImageProcessor {
    public BufferedImage process(BufferedImage src);
}
