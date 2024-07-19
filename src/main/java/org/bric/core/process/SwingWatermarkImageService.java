package org.bric.core.process;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SwingWatermarkImageService implements WatermarkImageService {

    @Override
    public BufferedImage textToImage(final String content, Color color, Font font, float alpha, boolean isPlainText) {
        JTextPane pane = new JTextPane();
        pane.setContentType("text/html");
        pane.setText(content);

        if (isPlainText) {
            pane.setForeground(color);
            pane.setFont(font);
        }

        pane.setBackground(new Color(255, 255, 255, 0));
        Dimension dim = pane.getPreferredSize();

        BufferedImage watermark = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = watermark.createGraphics();

        CellRendererPane crp = new CellRendererPane();
        crp.paintComponent(g2d, pane, null, 0, 0, dim.width, dim.height);

        g2d.setComposite(makeComposite(alpha));
        g2d.dispose();

        return watermark;
    }

    private AlphaComposite makeComposite(float alpha) {
        int type = AlphaComposite.SRC_OVER;
        return (AlphaComposite.getInstance(type, alpha));
    }
}
