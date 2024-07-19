package org.bric.gui.swing;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JRotator extends JComponent implements MouseListener, MouseMotionListener {

    private static final int COMPONENT_DEFAULT_WIDTH = 240;
    private static final int COMPONENT_DEFAULT_HEIGHT = 240;

    private static final double X_AXIS_CENTER = COMPONENT_DEFAULT_WIDTH / 2.0;
    private static final double Y_AXIS_CENTER = COMPONENT_DEFAULT_HEIGHT / 2.0;

    private final transient Image enabledBackground;
    private final transient Image disabledBackground;
    private final transient Image controllerImage;

    private double angleInRadians;

    public JRotator() {
        setSize(COMPONENT_DEFAULT_WIDTH, COMPONENT_DEFAULT_HEIGHT);

        enabledBackground = scale("/resource/rotate/background.png", 1);
        disabledBackground = scale("/resource/rotate/backgroundDisabled.png", 1);
        controllerImage = scale("/resource/rotate/mainImage.png", 0.5);

        addListeners();
    }

    private void addListeners() {
        addMouseMotionListener(this);
        addMouseListener(this);
    }

    private Image scale(String image, double scaleFactor) {
        try {
            BufferedImage originalImage = ImageIO.read(Objects.requireNonNull(getClass().getResource(image)));
            double xScale = COMPONENT_DEFAULT_WIDTH * scaleFactor;
            double yScale = COMPONENT_DEFAULT_HEIGHT * scaleFactor;

            if (originalImage.getWidth() <= originalImage.getHeight()) {
                return originalImage.getScaledInstance((int) xScale, -1, Image.SCALE_SMOOTH);
            } else {
                return originalImage.getScaledInstance(-1, (int) yScale, Image.SCALE_SMOOTH);
            }
        } catch (IOException ex) {
            Logger.getLogger(JRotator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void paintComponent(Graphics g) {
        Image doubleBufferedImage = createImage(this.getSize().width, this.getSize().height);
        Graphics dbg = doubleBufferedImage.getGraphics();

        Graphics2D g2d = (Graphics2D) dbg;
        g2d.setRenderingHint(
            RenderingHints.KEY_RENDERING,
            RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        g2d.drawImage(isEnabled() ? enabledBackground : disabledBackground, 0, 0, this);

        double xAxisControllerCenter = controllerImage.getWidth(this) / 2.0;
        double yAxisControllerCenter = controllerImage.getHeight(this) / 2.0;

        g2d.rotate(angleInRadians, X_AXIS_CENTER, Y_AXIS_CENTER);
        g2d.translate(X_AXIS_CENTER - xAxisControllerCenter, Y_AXIS_CENTER - yAxisControllerCenter);
        g2d.drawImage(controllerImage, 0, 0, this);

        g2d.dispose();
        g.drawImage(doubleBufferedImage, 0, 0, this);
    }

    public void setAngle(int angle) {
        angleInRadians = Math.toRadians(angle);
        repaint();
    }

    public int getAngle() {
        return (int) ((180 * angleInRadians) / Math.PI);
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        // Do nothing
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
        // Do nothing
    }

    @Override
    public void mouseExited(MouseEvent me) {
        // Do nothing
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        updateAngle(me);
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        // Do nothing
    }

    private void updateAngle(MouseEvent evt) {
        if (!isEnabled()) {
            return;
        }

        double mouseX = evt.getX();
        double mouseY = evt.getY();

        if (isTopLeftQuadrant(mouseX, mouseY)) {
            mouseX = -(X_AXIS_CENTER - mouseX);
            mouseY = Y_AXIS_CENTER - mouseY;
        } else if (isTopRightQuadrant(mouseX, mouseY)) {
            mouseX = mouseX - X_AXIS_CENTER;
            mouseY = Y_AXIS_CENTER - mouseY;
        } else if (isBottomRightQuadrant(mouseX, mouseY)) {
            mouseX = mouseX - X_AXIS_CENTER;
            mouseY = -(mouseY - Y_AXIS_CENTER);
        } else if (isBottomLeftQuadrant(mouseX, mouseY)) {
            mouseX = -(X_AXIS_CENTER - mouseX);
            mouseY = -(mouseY - Y_AXIS_CENTER);
        }

        angleInRadians = Math.atan2(mouseX, mouseY);

        if (angleInRadians < 0) {
            angleInRadians = angleInRadians + (2 * Math.PI);
        }

        repaint();
    }

    private static boolean isTopLeftQuadrant(final double mouseX, final double mouseY) {
        return mouseX < X_AXIS_CENTER && mouseY < Y_AXIS_CENTER;
    }

    private static boolean isBottomLeftQuadrant(final double mouseX, final double mouseY) {
        return mouseX < X_AXIS_CENTER && mouseY >= Y_AXIS_CENTER;
    }

    private static boolean isBottomRightQuadrant(final double mouseX, final double mouseY) {
        return mouseX >= X_AXIS_CENTER && mouseY >= Y_AXIS_CENTER;
    }

    private static boolean isTopRightQuadrant(final double mouseX, final double mouseY) {
        return mouseX >= X_AXIS_CENTER && mouseY < Y_AXIS_CENTER;
    }
}
