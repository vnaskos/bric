/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bric.gui.watermark;

/**
 *
 * @author vasilis
 */

import javax.swing.*;
import java.awt.*;

public class ShadowLabel extends JLabel {

    private String text;

    private Font font;

    public ShadowLabel() {
      super();
    }
    
    public ShadowLabel(String text, int size) {
        super();
        this.text = text;
        super.setText(text);
        font = super.getFont();
        this.setPreferredSize(super.getPreferredSize());
        super.setText("");
    }

    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2D = (Graphics2D) g;
        // ////////////////////////////////////////////////////////////////
        // antialiasing
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                 RenderingHints.VALUE_ANTIALIAS_ON);
        // ////////////////////////////////////////////////////////////////
        
        /**
         * draw text
         */
        
        g2D.setFont(font);
        g2D.setColor(new Color(0, 0, 0));
        g2D.drawString(this.text, 1, 11);
//          g2D.setColor(new Color(255, 255, 255, 230));
        g2D.setColor(super.getForeground());
        g2D.drawString(this.text, 0, 10);
        g2D.dispose();

    }

    @Override
    public void setText(String text) {
        this.text = text;
        repaint();
    }

    /**
     * Default UID
     */
    private static final long serialVersionUID = 1L;

}
