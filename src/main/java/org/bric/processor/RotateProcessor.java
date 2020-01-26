/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bric.processor;

import com.jhlabs.image.FlipFilter;
import com.jhlabs.image.RotateFilter;
import java.awt.image.BufferedImage;
import java.util.Random;
import org.bric.imageEditParameters.RotateParameters;

/**
 *
 * @author vasilis
 */
public class RotateProcessor implements ImageProcessor {

    RotateParameters rotateParameters;

    public RotateProcessor(RotateParameters rotateParameters) {
        this.rotateParameters = rotateParameters;
    }
    
    @Override
    public BufferedImage process(BufferedImage image) {
        if(rotateParameters.isCustom()){
            return customRotate(image);
        } else {
            return predefinedRotate(image);
        }
    }
    
    
    private BufferedImage predefinedRotate(BufferedImage image) {
        int action = rotateParameters.getAction();
        switch(action){
            case 0:
            case 4:
                action = FlipFilter.FLIP_180;
                break;
            case 1:
                action = FlipFilter.FLIP_90CCW;
                break;
            case 2:
                action = FlipFilter.FLIP_90CW;
                break;
            case 3:
                action = FlipFilter.FLIP_H;
                break;
            case 5:
                action = FlipFilter.FLIP_V;
                break;
        }
        FlipFilter flipFilter = new FlipFilter(action);
        return flipFilter.filter(image, null);
    }

    public BufferedImage customRotate(BufferedImage image) {
        int angle = rotateParameters.getAngle();
        if(rotateParameters.isDifferentValues()){
            Random rand = new Random();
            int max = rotateParameters.getTo();
            int min = rotateParameters.getFrom();
            if(rotateParameters.isLimit()) {
                angle = rand.nextInt(max-min+1)+min;
            } else {
                angle = rand.nextInt(360);
            }
        }else if(rotateParameters.isRandom()){
            angle = rotateParameters.getRandomAngle();
        }
        
        float radAngle = (float) (((360-angle)*Math.PI)/180);
        RotateFilter rotateFilter = new RotateFilter(radAngle);
        return rotateFilter.filter(image, null);
    }
    
}
