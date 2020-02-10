/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bric.gui.swing;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vasilis
 */
public class JRotator extends JComponent implements MouseListener, MouseMotionListener {
    
    private static final int IMAGE_WIDTH = 240;//square
    private static final int IMAGE_HEIGHT = 240;
    private static final double SCALE_CONSTANT = (IMAGE_WIDTH*190)/280;
    private static boolean enabled = true;
    
    private double radAngle;
    private Image mainImage;
    private Image backgroundEnabled;
    private Image backgroundDisabled;
    
    private String mainImagePath = "/resource/rotate/mainImage.png";
    
    private Image doubleBufferedImage;
    private Graphics dbg;
    
    public JRotator() {
        initialize();
        mainImage = scaleImage(mainImagePath);
    }
    
    private void initialize(){
        addMouseMotionListener(this);
        addMouseListener(this);
        this.setSize(IMAGE_WIDTH, IMAGE_HEIGHT);
        try {
            backgroundEnabled = scaleBackground(ImageIO.read(getClass().getResource("/resource/rotate/background.png")));
            backgroundDisabled = scaleBackground(ImageIO.read(getClass().getResource("/resource/rotate/backgroundDisabled.png")));
        } catch (IOException ex) {
            Logger.getLogger(JRotator.class.getName()).log(Level.SEVERE, null, ex);
        }
        //setEnabled(this.isEnabled());
    }
    

    private Image scaleImage(String image){
        BufferedImage scaledImage = null;
        try {
            scaledImage = ImageIO.read(getClass().getResource(image));
            double xScale = 0;
            double yScale = 0;
            try{
                xScale = (scaledImage.getWidth()*SCALE_CONSTANT)/scaledImage.getHeight();
                yScale = (scaledImage.getHeight()*SCALE_CONSTANT)/scaledImage.getWidth();
            }catch(Exception e){
                
            }
            if(scaledImage.getWidth() <= scaledImage.getHeight()){
                return scaledImage.getScaledInstance((int) xScale, -1, Image.SCALE_SMOOTH);
            }else{
                return scaledImage.getScaledInstance(-1, (int) yScale, Image.SCALE_SMOOTH);
            }
        } catch (IOException ex) {
            Logger.getLogger(JRotator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return scaledImage;
    }
    
    private Image scaleBackground(Image image){
        return image.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);
    }
    
    @Override
    public void paintComponent(Graphics g){
        doubleBufferedImage = createImage (this.getSize().width, this.getSize().height);	
        dbg = doubleBufferedImage.getGraphics ();
        int w = (int) this.getWidth();
        int h = (int) this.getHeight();

        Graphics2D g2 = (Graphics2D) dbg;
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //G2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        if(this.isEnabled()) {
            g2.drawImage(backgroundEnabled, 0,0,this);
        } else {
            g2.drawImage(backgroundDisabled, 0,0,this);
        }
        g2.rotate(radAngle, w/2, h/2);
        g2.drawImage(mainImage, (IMAGE_WIDTH/2)-(mainImage.getWidth(this) /2), (IMAGE_HEIGHT/2)-(mainImage.getHeight(this)/2), this);
        g2.rotate(-radAngle, w/2, h/2);

        g2.dispose();
	
	g.drawImage (doubleBufferedImage, 0, 0, this);
    }
    
    public void setAngle(int angle){
        radAngle = (angle*Math.PI)/180;
        repaint();
    }
    
    public int getAngle(){
        return (int) ((180*radAngle)/Math.PI);
    }
    
    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
        updateAngle(me);
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        updateAngle(me);
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        updateAngle(me);
    }

    @Override
    public void mouseMoved(MouseEvent me) {
    }
    
    @Override
    public void setEnabled(boolean value){
        enabled = value;
        super.setEnabled(value);
    }
    
    public void updateAngle(MouseEvent evt){
        if(!enabled){
           return; 
        }
        double Xd = evt.getX();
        double Yd = evt.getY();
//        double Xd = evt.getX()-120;
//        double Yd = evt.getY()-120;
//        radAngle = Math.atan2(Yd,Xd);
        
        if(Xd < 120 && Yd < 120){ // 1o tetartimorio
            Yd = 120 - Yd;
            Xd = -(120 - Xd);
        } else if(Xd >= 120 && Yd < 120){ // 2o teartimorio
            Yd = 120 - Yd;
            Xd = Xd - 120;
        } else if(Xd >= 120 && Yd >= 120){ // 3o teartimorio
            Yd = -(Yd - 120);
            Xd = Xd - 120;
        } else if(Xd < 120 && Yd >= 120){ // 4o teartimorio
            Yd = -(Yd - 120);
            Xd = -(120 - Xd);
        }
        
        radAngle = Math.atan2(Xd, Yd);
        
        if(((180/Math.PI)*radAngle) < 0){
            radAngle = radAngle+(2*Math.PI);
        }
        repaint();
    }
    
}
