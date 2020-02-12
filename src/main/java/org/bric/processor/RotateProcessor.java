package org.bric.processor;

import com.jhlabs.image.FlipFilter;
import com.jhlabs.image.RotateFilter;
import org.bric.imageEditParameters.RotateParameters;

import java.awt.image.BufferedImage;
import java.util.Random;

public class RotateProcessor extends ImageProcessor<RotateParameters> {

    public RotateProcessor(RotateParameters params) {
        super(params);
    }

    @Override
    public BufferedImage process(BufferedImage image) {
        if(params.isCustom()){
            return customRotate(image);
        } else {
            return predefinedRotate(image);
        }
    }
    
    
    private BufferedImage predefinedRotate(BufferedImage image) {
        int action = params.getAction();
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
        int angle = params.getAngle();
        if(params.isDifferentValues()){
            Random rand = new Random();
            int max = params.getTo();
            int min = params.getFrom();
            if(params.isLimit()) {
                angle = rand.nextInt(max-min+1)+min;
            } else {
                angle = rand.nextInt(360);
            }
        }else if(params.isRandom()){
            angle = params.getRandomAngle();
        }
        
        float radAngle = (float) (((360-angle)*Math.PI)/180);
        RotateFilter rotateFilter = new RotateFilter(radAngle);
        return rotateFilter.filter(image, null);
    }
    
}
