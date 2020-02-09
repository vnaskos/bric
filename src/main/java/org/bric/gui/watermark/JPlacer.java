/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bric.gui.watermark;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vasilis
 */
public class JPlacer extends JComponent implements MouseListener, MouseMotionListener, ComponentListener {

    private Image doubleBufferedImage;
    private Graphics dbg;
    
    private Rectangle windowBounds;
    
    private Image background;
    private static final Color DEFAULT_COLOR = Color.WHITE;
    private Color fontColor = DEFAULT_COLOR;
    
    private double radAngle = 0;
    private double angle = 0;
    private float alpha = 1F;
    
    private int width, height;
    
    private static final String STANDARD_TEXT = "watermark";
    private String previewText = STANDARD_TEXT;
    
    private Rectangle rect;
    
    private int preX, preY;
    
    private boolean mouseReleased = false;
    
    private ShadowLabel renderer = new ShadowLabel(STANDARD_TEXT, 12);
    
    private CellRendererPane crp = new CellRendererPane();
    private Dimension dim;
    
    private int rotationCenterX, rotationCenterY;
    
    private Rectangle[] positions = new Rectangle[9];
    
    private int startX, startY;
    
    private int oldWidth, oldHeight, newWidth = 360, newHeight = 215, oldX, oldY;
    
    private boolean enabled;

    public JPlacer(){
        initialize();
    }
    
    private void initialize(){
        addMouseMotionListener(this);
        addMouseListener(this);
        addComponentListener(this);
        
        try {
            background = ImageIO.read(getClass().getResource("/resource/watermark/dokimi 6.png"));
        } catch (IOException ex) {
            Logger.getLogger(JPlacer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.setSize(background.getWidth(this), background.getHeight(this));
        this.setMinimumSize(new Dimension(background.getWidth(this), background.getHeight(this)));
        
        dim = renderer.getPreferredSize();
        width = dim.width;
        height = dim.height;
        
        startX = this.getWidth() / 2 - width/2;
        startY = this.getHeight() / 2 - height/2;
        
        rect = new Rectangle(startX, startY, width, height);
        
        windowBounds = new Rectangle(0, 0, this.getWidth(), this.getHeight());
        renderer.setForeground(fontColor);
        
        rotationCenterX = rect.x + (dim.width/2);
        rotationCenterY = rect.y + (dim.height/2);
        
        renderer.setVerticalTextPosition(JLabel.TOP);
        renderer.setHorizontalTextPosition(JLabel.LEFT);
    }
    
    @Override
    public void paintComponent(Graphics g){
        rotationCenterX = rect.x + (dim.width/2);
        rotationCenterY = rect.y + (dim.height/2);
        
        doubleBufferedImage = createImage (this.getSize().width, this.getSize().height);
        dbg = doubleBufferedImage.getGraphics();
        
        width = dim.width;
        height = dim.height;
        
        Graphics2D g2 = (Graphics2D) dbg;
        
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        windowBounds.setSize(this.getWidth(), this.getHeight());
        g2.fill(windowBounds);
        g2.drawImage(background, this.getWidth() / 2 - background.getWidth(this) /2, this.getHeight() / 2 - background.getHeight(this) /2, this);
        
        g2.setColor(Color.WHITE);
        int k=0;
        for(int y = 0; y < 3; y++){
            for(int x = 0; x < 3; x++){
                positions[k] = new Rectangle((this.getWidth()/3)*x, (this.getHeight()/3)*y, this.getWidth()/3, this.getHeight()/3);
                g2.draw(positions[k]);
                k++;
            }
        }
        
        g2.setColor(fontColor);
        
        g2.rotate(radAngle, rotationCenterX, rotationCenterY);
        g2.setComposite(makeComposite(alpha));
        
        crp.paintComponent(g2, renderer, this, rect.x, rect.y, dim.width, dim.height);
        
        width = dim.width;
        height = dim.height;
        rect.setSize(width, height);

        g2.rotate(-radAngle, rotationCenterX, rotationCenterY);
        
        g2.dispose();
	
	g.drawImage (doubleBufferedImage, 0, 0, this);
        
    }
    
    private AlphaComposite makeComposite(float alpha) {
        int type = AlphaComposite.SRC_OVER;
        return (AlphaComposite.getInstance(type, alpha));
    }
    
    private void checkRectangleCollision() {
        if (windowBounds.contains(rect)) {
            return;
        }
     
        if (rect.x+rect.width/2  >= windowBounds.getWidth()+windowBounds.getX()) {
            setLimitLocations((int) ((windowBounds.getWidth() - rect.width/2)+windowBounds.getX()), rect.y);
        }
        if (rect.x < windowBounds.getX()-rect.width/2){
            setLimitLocations((int) windowBounds.getX()-rect.width/2, rect.y);
        }
        if (rect.y+rect.height/2 > windowBounds.getHeight()+windowBounds.getY()) {
            setLimitLocations(rect.x, (int) ((windowBounds.getHeight() - rect.height/2)+windowBounds.getY()));
        }
        if (rect.y < windowBounds.getY()-rect.height/2) {
            setLimitLocations(rect.x, (int) windowBounds.getY()-rect.height/2);
        }
    }
    
    private void setLimitLocations(int newX, int newY){
        int centerX = (rect.width/2)-(rect.width/2);
        int centerY = (rect.height/2)-(rect.height/2);
        
        rect.setLocation(newX, newY);  
        rect.setLocation(newX-centerX, newY-centerY);
    }
    
    @Override
    public void mouseClicked(MouseEvent me) {
        if(!enabled){
            return;
        }
        for(int i = 0; i < positions.length; i++){
            if(positions[i].contains(me.getPoint())){
                rect.setLocation(((positions[i].width/2) + positions[i].x)-rect.width/2, ((positions[i].height/2) + positions[i].y)-rect.height/2);
                checkRectangleCollision();
                repaint();
                return;
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {
        preX = rect.x - me.getX();
        preY = rect.y - me.getY();

        if (rect.contains(me.getX(), me.getY())) {
            updateLocation(me);
        } else {
            mouseReleased = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if (rect.contains(me.getX(), me.getY())) {
            updateLocation(me);
        } else {
            mouseReleased = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        if(!enabled){
            return;
        }
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent me) {
        if(!enabled){
            return;
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        if (!mouseReleased) {
            updateLocation(me);
        }
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        if(!enabled){
            return;
        }
        if (rect.contains(me.getPoint())) {//bounded
            setCursor(new Cursor(Cursor.MOVE_CURSOR));
        } else {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
    }
    
    public void updateLocation(MouseEvent e) {
        if(!enabled){
            return;
        }
        rect.setLocation(preX + e.getX(), preY + e.getY());
        checkRectangleCollision();
        
        repaint();
    }
    
    public double getTextX(){
        return rect.getX();
    }
    
    public double getTextY(){
        return rect.getY();
    }
    
    public void setText(String text){
        previewText = text;
        renderer.setText(previewText);
        updateLimits();
    }
    
    public void setAlpha(int value){
        alpha = (float) ((100-value) * Math.pow(10,-2));
        repaint();
    }
    
    public float getAlpha(){
        return alpha;
    }
    
    public void setFonts(Font font){
        renderer.setFont(font);
        updateLimits();
    }
    
    public void setColor(Color color){
        fontColor = color;
        renderer.setForeground(color);
        repaint();
    }
    
    public Color getColor(){
        return renderer.getForeground();
    }
  
    public void setAngle(int angle){
        this.angle = angle;
        radAngle = (angle*Math.PI)/180;
        updateLimits();
    }
    
    public double getAngle(){
        return angle;
    }
    
    private void updateLimits(){
        dim = renderer.getPreferredSize();
        checkRectangleCollision();
        repaint();
    }
    
    public void setImage(ImageIcon image){
        renderer.setIcon(image);
        updateLimits();
    }
    
    public void setImageAlign(int value){
        switch(value) {
            case 0:
                renderer.setVerticalTextPosition(JLabel.TOP);
                renderer.setHorizontalTextPosition(JLabel.LEFT);
                break;
            case 1:
                renderer.setVerticalTextPosition(JLabel.TOP);
                renderer.setHorizontalTextPosition(JLabel.RIGHT);
                break;
            case 2:
                renderer.setVerticalTextPosition(JLabel.CENTER);
                renderer.setHorizontalTextPosition(JLabel.LEFT);
                break;
            case 3:
                renderer.setVerticalTextPosition(JLabel.CENTER);
                renderer.setHorizontalTextPosition(JLabel.RIGHT);
                break;
            case 4:
                renderer.setVerticalTextPosition(JLabel.TOP);
                renderer.setHorizontalTextPosition(JLabel.CENTER);
                break;
            case 5:
                renderer.setVerticalTextPosition(JLabel.BOTTOM);
                renderer.setHorizontalTextPosition(JLabel.CENTER);
                break;
            case 6:
                renderer.setVerticalTextPosition(JLabel.BOTTOM);
                renderer.setHorizontalTextPosition(JLabel.LEFT);
                break;
            case 7:
                renderer.setVerticalTextPosition(JLabel.BOTTOM);
                renderer.setHorizontalTextPosition(JLabel.RIGHT);
                break;
        }
        updateLimits();
    }
    
    public int getLabelCenterX(){
        return rect.x+rect.width/2;
    }
    
    public int getLabelCenterY(){
        return rect.y+rect.height/2;
    }
    
    public int getLabelWidth(){
        return dim.width;
    }
    
    public int getLabelHeight(){
        return dim.height;
    }
    
    public String getText(){
        return renderer.getText();
    }

    @Override
    public void componentResized(ComponentEvent ce) {
        oldWidth = newWidth;
        oldHeight = newHeight;
        newWidth = this.getWidth();
        newHeight = this.getHeight();
        oldX = rect.x+rect.width/2;
        oldY = rect.y+rect.height/2;
        rect.setLocation((int)(( (double)newWidth/oldWidth)*oldX-rect.width/2), (int)( ((double)newHeight/oldHeight)*oldY-rect.height/2));
        dim = renderer.getPreferredSize();
        repaint();
    }

    @Override
    public void componentMoved(ComponentEvent ce) {
    }

    @Override
    public void componentShown(ComponentEvent ce) {
    }

    @Override
    public void componentHidden(ComponentEvent ce) {
    }
    
    @Override
    public void setEnabled(boolean value){
        enabled = value;
        super.setEnabled(value);
    }

}
