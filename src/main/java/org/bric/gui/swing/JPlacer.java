package org.bric.gui.swing;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JPlacer extends JComponent implements MouseListener, MouseMotionListener, ComponentListener {

    private static final int DEFAULT_WIDTH = 360;
    private static final int DEFAULT_HEIGHT = 215;
    private static final Color DEFAULT_COLOR = Color.WHITE;
    private static final String DEFAULT_TEXT = "watermark";

    private static final int GRID_SIZE = 3;
    private final Rectangle[] gridCells = new Rectangle[GRID_SIZE * GRID_SIZE];

    private final transient Image backgroundImage;
    private final Rectangle windowBounds;

    private final ShadowLabel textRenderer = new ShadowLabel(DEFAULT_TEXT, 12);
    private final Rectangle textBox;
    private Dimension textBoxDimensions;
    private int lastXAxisMousePress;
    private int lastYAxisMousePress;
    private double xAxisTextBoxCenter;
    private double yAxisTextBoxCenter;

    private boolean mousePressed = false;

    private int componentWidth = DEFAULT_WIDTH;
    private int componentHeight = DEFAULT_HEIGHT;

    private double angleInRadians = 0;
    private float alpha = 1;

    public JPlacer() {
        backgroundImage = getBackgroundImage();
        setSize(backgroundImage.getWidth(this), backgroundImage.getHeight(this));
        setMinimumSize(new Dimension(backgroundImage.getWidth(this), backgroundImage.getHeight(this)));
        windowBounds = new Rectangle(0, 0, this.getWidth(), this.getHeight());

        textRenderer.setForeground(DEFAULT_COLOR);
        textRenderer.setVerticalTextPosition(SwingConstants.TOP);
        textRenderer.setHorizontalTextPosition(SwingConstants.LEFT);

        textBoxDimensions = textRenderer.getPreferredSize();
        int startX = getWidth() / 2 - textBoxDimensions.width / 2;
        int startY = getHeight() / 2 - textBoxDimensions.height / 2;
        textBox = new Rectangle(startX, startY, textBoxDimensions.width, textBoxDimensions.height);
        moveText(startX, startY);

        addListeners();
    }

    @Override
    public void paintComponent(Graphics g) {
        Image doubleBufferedImage = createImage(this.getSize().width, this.getSize().height);
        Graphics2D g2d = (Graphics2D) doubleBufferedImage.getGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        windowBounds.setSize(getWidth(), getHeight());
        g2d.fill(windowBounds);
        g2d.drawImage(backgroundImage, getWidth() / 2 - backgroundImage.getWidth(this) / 2, getHeight() / 2 - backgroundImage.getHeight(this) / 2, this);

        g2d.setColor(Color.GRAY);
        int gridCellWidth = getWidth() / GRID_SIZE;
        int gridCellHeight = getHeight() / GRID_SIZE;
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                gridCells[(y * GRID_SIZE) + x] = new Rectangle(gridCellWidth * x, gridCellHeight * y, gridCellWidth, gridCellHeight);
                g2d.draw(gridCells[(y * GRID_SIZE) + x]);
            }
        }

        int type = AlphaComposite.SRC_OVER;
        CellRendererPane crp = new CellRendererPane();

        g2d.setColor(DEFAULT_COLOR);
        g2d.rotate(angleInRadians, xAxisTextBoxCenter, yAxisTextBoxCenter);
        g2d.setComposite(AlphaComposite.getInstance(type, alpha));
        crp.paintComponent(g2d, textRenderer, this, textBox.x, textBox.y, textBoxDimensions.width, textBoxDimensions.height);
        g2d.rotate(-angleInRadians, xAxisTextBoxCenter, yAxisTextBoxCenter);

        if (!isEnabled()) {
            g2d.setColor(new Color(0, 0, 0, 120));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        g2d.dispose();

        g.drawImage(doubleBufferedImage, 0, 0, this);
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        if (!isEnabled()) {
            return;
        }

        for (Rectangle cell : gridCells) {
            if (cell.contains(me.getPoint())) {
                moveText(
                    cell.x + (cell.width / 2) - textBox.width / 2,
                    cell.y + (cell.height / 2) - textBox.height / 2);
                return;
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {
        if (!textBox.contains(me.getPoint())) {
            return;
        }

        lastXAxisMousePress = textBox.x - me.getX();
        lastYAxisMousePress = textBox.y - me.getY();

        mousePressed = true;
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if (mousePressed) {
            mousePressed = false;
        } else {
            mouseClicked(me);
        }
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        if (!isEnabled()) {
            return;
        }

        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent me) {
        if (!isEnabled()) {
            return;
        }

        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        if (!isEnabled()) {
            return;
        }

        if (!mousePressed) {
            return;
        }

        moveText(lastXAxisMousePress + me.getX(), lastYAxisMousePress + me.getY());
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        if (!isEnabled()) {
            return;
        }

        boolean mouseOnTextBox = textBox.contains(me.getPoint());
        setCursor(mouseOnTextBox
            ? new Cursor(Cursor.MOVE_CURSOR)
            : new Cursor(Cursor.HAND_CURSOR));
    }

    public void setAlpha(int value) {
        alpha = (100 - value) / 100f;
        repaint();
    }

    public float getAlpha() {
        return alpha;
    }

    public Color getColor() {
        return textRenderer.getForeground();
    }

    public void setAngle(int angle) {
        angleInRadians = Math.toRadians(angle);
        updateLimits();
    }

    public int getLabelCenterX() {
        return textBox.x + textBox.width / 2;
    }

    public int getLabelCenterY() {
        return textBox.y + textBox.height / 2;
    }

    public String getText() {
        return textRenderer.getText();
    }

    @Override
    public void componentResized(ComponentEvent ce) {
        double oldWidth = componentWidth;
        double oldHeight = componentHeight;
        componentWidth = ce.getComponent().getWidth();
        componentHeight = ce.getComponent().getHeight();

        xAxisTextBoxCenter = (componentWidth * xAxisTextBoxCenter) / oldWidth;
        yAxisTextBoxCenter = (componentHeight * yAxisTextBoxCenter) / oldHeight;

        textBox.setLocation(
            (int) (xAxisTextBoxCenter - (textBox.width / 2.0)),
            (int) (yAxisTextBoxCenter - (textBox.height / 2.0)));

        repaint();
    }

    @Override
    public void componentMoved(ComponentEvent ce) {
        // do nothing
    }

    @Override
    public void componentShown(ComponentEvent ce) {
        // do nothing
    }

    @Override
    public void componentHidden(ComponentEvent ce) {
        // do nothing
    }

    private void addListeners() {
        addMouseMotionListener(this);
        addMouseListener(this);
        addComponentListener(this);
    }

    private Image getBackgroundImage() {
        try {
            return ImageIO.read(Objects.requireNonNull(getClass().getResource("/resource/watermark/background.png")));
        } catch (IOException ex) {
            Logger.getLogger(JPlacer.class.getName()).log(Level.SEVERE, null, ex);
        }

        BufferedImage fallbackBackground = new BufferedImage(DEFAULT_WIDTH, DEFAULT_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = fallbackBackground.createGraphics();
        g2d.setBackground(Color.BLACK);
        g2d.clearRect(0, 0, fallbackBackground.getWidth(), fallbackBackground.getHeight());
        g2d.dispose();
        return fallbackBackground;
    }

    private void moveText(final int x, final int y) {
        textBox.setLocation(x, y);

        xAxisTextBoxCenter = textBox.x + (textBox.width / 2.0);
        yAxisTextBoxCenter = textBox.y + (textBox.height / 2.0);

        preventTextGoingOutOfBounds();
        repaint();
    }

    private void preventTextGoingOutOfBounds() {
        if (windowBounds.contains(textBox)) {
            return;
        }

        if (textBox.x + textBox.width / 2.0 >= windowBounds.getWidth() + windowBounds.getX()) {
            int newX = (int) ((windowBounds.getWidth() - textBox.width / 2.0) + windowBounds.getX());
            textBox.setLocation(newX, textBox.y);
        }
        if (textBox.x < windowBounds.getX() - textBox.width / 2.0) {
            int newX = (int) windowBounds.getX() - textBox.width / 2;
            textBox.setLocation(newX, textBox.y);
        }
        if (textBox.y + textBox.height / 2.0 > windowBounds.getHeight() + windowBounds.getY()) {
            int newY = (int) ((windowBounds.getHeight() - textBox.height / 2.0) + windowBounds.getY());
            textBox.setLocation(textBox.x, newY);
        }
        if (textBox.y < windowBounds.getY() - textBox.height / 2.0) {
            int newY = (int) windowBounds.getY() - textBox.height / 2;
            textBox.setLocation(textBox.x, newY);
        }
    }

    private void updateLimits() {
        textBoxDimensions = textRenderer.getPreferredSize();
        preventTextGoingOutOfBounds();
        repaint();
    }
}
