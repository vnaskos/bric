package org.bric.gui.tabs;

import com.jhlabs.image.RotateFilter;
import org.bric.core.model.WatermarkMode;
import org.bric.core.model.WatermarkPattern;
import org.bric.core.process.ImageService;
import org.bric.core.process.WatermarkImageService;
import org.bric.core.process.model.WatermarkParameters;
import org.bric.gui.state.StatefulComponent;
import org.bric.gui.swing.JPlacer;
import org.bric.utils.Utils;
import say.swing.JFontChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Properties;
import java.util.ResourceBundle;

public class WatermarkJPanel extends JPanel implements ImageEditTab, StatefulComponent {

    private static final ResourceBundle bundle = ResourceBundle.getBundle("lang/gui/tabs/WatermarkJPanel");

    private final transient ImageService imageService;
    private final transient WatermarkImageService watermarkImageService;
    private final Color defaultColor;
    private final Font defaultFont;

    private JButton browseButton;
    private JButton colorButton;
    private JSpinner coloumnsSpinner;
    private JLabel columnsLabel;
    private JLabel editorJLabel;
    private JTextPane editorTextPane;
    private JButton fontButton;
    private JFontChooser fontChooser;
    private javax.swing.JScrollPane jScrollPane2;
    private JComboBox<WatermarkMode> modeComboBox;
    private javax.swing.JSlider opacitySlider;
    private JLabel opacityLabel;
    private JComboBox<WatermarkPattern> patternComboBox;
    private JLabel patternLabel;
    private JLabel rotateLabel;
    private javax.swing.JSlider rotateSlider;
    private JLabel rowsLabel;
    private JSpinner rowsSlidder;
    private JPanel settingsPanel;
    private javax.swing.JCheckBox watermarkEnableCheckBox;
    private javax.swing.JTextField watermarkImageText;
    private JPlacer watermarkPlacer;

    private Color color;
    private String plainText;
    private Font currentFont;
    private String lastOpenedDirectory = "";

    public WatermarkJPanel(ImageService imageService, WatermarkImageService watermarkImageService) {
        this.imageService = imageService;
        this.watermarkImageService = watermarkImageService;
        initComponents();
        defaultColor = rotateLabel.getForeground();
        watermarkImageText.setVisible(false);
        browseButton.setVisible(false);
        defaultFont = editorTextPane.getFont();
        currentFont = defaultFont;
    }

    private void initComponents() {
        javax.swing.JSplitPane watermarkSplitPane;

        fontChooser = new JFontChooser();
        watermarkEnableCheckBox = new javax.swing.JCheckBox();
        watermarkSplitPane = new JSplitPane();
        watermarkPlacer = new JPlacer();
        rotateSlider = new javax.swing.JSlider();
        opacitySlider = new javax.swing.JSlider();
        JPanel jPanel2 = new JPanel();
        settingsPanel = new JPanel();
        colorButton = new JButton();
        fontButton = new JButton();
        modeComboBox = new JComboBox<>();
        editorJLabel = new JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        editorTextPane = new JTextPane();
        browseButton = new JButton();
        watermarkImageText = new javax.swing.JTextField();
        JPanel jPanel3 = new JPanel();
        rowsSlidder = new JSpinner();
        patternComboBox = new JComboBox<>();
        columnsLabel = new JLabel();
        rowsLabel = new JLabel();
        coloumnsSpinner = new JSpinner();
        patternLabel = new JLabel();
        opacityLabel = new JLabel();
        rotateLabel = new JLabel();

        watermarkEnableCheckBox.setText(bundle.getString("WatermarkJPanel.watermarkEnableCheckBox.text"));
        watermarkEnableCheckBox.addItemListener(this::watermarkEnableCheckBoxItemStateChanged);

        watermarkSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

        watermarkPlacer.setEnabled(false);
        watermarkPlacer.setMinimumSize(new java.awt.Dimension(350, 215));

        rotateSlider.setMaximum(360);
        rotateSlider.setToolTipText(bundle.getString("WatermarkJPanel.rotateSlider.toolTipText"));
        rotateSlider.setValue(0);
        rotateSlider.setEnabled(false);
        rotateSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                rotateSliderMouseEntered();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                rotateSliderMouseExited();
            }
        });
        rotateSlider.addChangeListener(this::rotateSliderStateChanged);

        opacitySlider.setMajorTickSpacing(10);
        opacitySlider.setMinorTickSpacing(5);
        opacitySlider.setToolTipText(bundle.getString("WatermarkJPanel.opacity.toolTipText"));
        opacitySlider.setValue(0);
        opacitySlider.setEnabled(false);
        opacitySlider.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                opacitySliderMouseEntered();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                opacitySliderMouseExited();
            }
        });
        opacitySlider.addChangeListener(this::opacitySliderStateChanged);

        javax.swing.GroupLayout watermarkPlacerLayout = new javax.swing.GroupLayout(watermarkPlacer);
        watermarkPlacer.setLayout(watermarkPlacerLayout);
        watermarkPlacerLayout.setHorizontalGroup(
            watermarkPlacerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(opacitySlider, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
            .addComponent(rotateSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        watermarkPlacerLayout.setVerticalGroup(
            watermarkPlacerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, watermarkPlacerLayout.createSequentialGroup()
                .addComponent(opacitySlider, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 176, Short.MAX_VALUE)
                .addComponent(rotateSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        watermarkSplitPane.setLeftComponent(watermarkPlacer);

        settingsPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        colorButton.setText(bundle.getString("WatermarkJPanel.colorButton.text"));
        colorButton.setToolTipText(bundle.getString("WatermarkJPanel.colorButton.toolTipText"));
        colorButton.setEnabled(false);
        colorButton.addActionListener(this::colorButtonActionPerformed);

        fontButton.setText(bundle.getString("WatermarkJPanel.fontButton.text"));
        fontButton.setToolTipText(bundle.getString("WatermarkJPanel.fontButton.toolTipText"));
        fontButton.setEnabled(false);
        fontButton.addActionListener(this::fontButtonActionPerformed);

        modeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new WatermarkMode[] { WatermarkMode.TEXT_MODE, WatermarkMode.HTML_MODE, WatermarkMode.IMAGE_MODE }));
        modeComboBox.setEnabled(false);
        modeComboBox.addActionListener(this::modeComboBoxActionPerformed);

        editorJLabel.setText(bundle.getString("WatermarkJPanel.editorJLabel.text.text"));
        editorJLabel.setEnabled(false);

        editorTextPane.setContentType("text/html");
        editorTextPane.setText(bundle.getString("WatermarkJPanel.editorTextPane.text"));
        editorTextPane.setEnabled(false);
        jScrollPane2.setViewportView(editorTextPane);

        browseButton.setText(bundle.getString("WatermarkJPanel.browseButton.text"));
        browseButton.setEnabled(false);
        browseButton.addActionListener(this::browseButtonActionPerformed);

        watermarkImageText.setEnabled(false);

        javax.swing.GroupLayout settingsPanelLayout = new javax.swing.GroupLayout(settingsPanel);
        settingsPanel.setLayout(settingsPanelLayout);
        settingsPanelLayout.setHorizontalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(settingsPanelLayout.createSequentialGroup()
                        .addComponent(watermarkImageText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(settingsPanelLayout.createSequentialGroup()
                        .addComponent(editorJLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(fontButton)
                        .addGap(3, 3, 3)
                        .addComponent(colorButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(modeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        settingsPanelLayout.setVerticalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, settingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fontButton)
                    .addComponent(modeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(colorButton)
                    .addComponent(editorJLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(browseButton)
                    .addComponent(watermarkImageText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        rowsSlidder.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        rowsSlidder.setEnabled(false);

        patternComboBox.setModel(new DefaultComboBoxModel<>(new WatermarkPattern[] { WatermarkPattern.SINGLE, WatermarkPattern.TILED }));
        patternComboBox.setEnabled(false);
        patternComboBox.addItemListener(this::patternComboBoxItemStateChanged);

        columnsLabel.setText(bundle.getString("WatermarkJPanel.coulumnsLabel.text"));
        columnsLabel.setToolTipText(bundle.getString("WatermarkJPanel.columnsLabel.toolTipText"));
        columnsLabel.setEnabled(false);

        rowsLabel.setText(bundle.getString("WatermarkJPanel.rowsLabel.text"));
        rowsLabel.setToolTipText(bundle.getString("WatermarkJPanel.rowsLabel.toolTipText"));
        rowsLabel.setEnabled(false);

        coloumnsSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        coloumnsSpinner.setEnabled(false);

        patternLabel.setText(bundle.getString("WatermarkJPanel.patternLabel.text"));
        patternLabel.setEnabled(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(patternLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(patternComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(rowsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rowsSlidder, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(columnsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(coloumnsSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(patternComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rowsSlidder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(coloumnsSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(patternLabel)
                    .addComponent(rowsLabel)
                    .addComponent(columnsLabel))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(settingsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(settingsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        watermarkSplitPane.setRightComponent(jPanel2);

        opacityLabel.setText(String.format(bundle.getString("WatermarkJPanel.opacityLabel.text"), 0));
        opacityLabel.setEnabled(false);

        rotateLabel.setText(bundle.getString("WatermarkJPanel.rotateJLabel.text")+"0°");
        rotateLabel.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(watermarkSplitPane)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(watermarkEnableCheckBox)
                .addGap(47, 47, 47)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rotateLabel)
                    .addComponent(opacityLabel))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(watermarkEnableCheckBox)
                    .addComponent(opacityLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rotateLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(watermarkSplitPane))
        );
    }// </editor-fold>

    private void watermarkEnableCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        watermarkTabEnable();
    }

    private void rotateSliderMouseExited() {
        rotateLabel.setForeground(defaultColor);
    }

    private void rotateSliderMouseEntered() {
        rotateLabel.setForeground(Color.RED);
    }

    private void rotateSliderStateChanged(javax.swing.event.ChangeEvent evt) {
        watermarkPlacer.setAngle(rotateSlider.getValue());
        rotateLabel.setText(bundle.getString("WatermarkJPanel.rotateJLabel.text") + rotateSlider.getValue() + "°");
    }

    private void opacitySliderMouseExited() {
        opacityLabel.setForeground(defaultColor);
    }

    private void opacitySliderMouseEntered() {
        opacityLabel.setForeground(Color.RED);
    }

    private void opacitySliderStateChanged(javax.swing.event.ChangeEvent evt) {
        opacityLabel.setText(String.format(bundle.getString("WatermarkJPanel.opacityLabel.text"), opacitySlider.getValue()));
        watermarkPlacer.setAlpha(opacitySlider.getValue());
    }

    private void colorButtonActionPerformed(java.awt.event.ActionEvent evt) {
        color = JColorChooser.showDialog(null, "Choose Color", Color.BLACK);
        editorTextPane.setForeground(color);
    }

    private void fontButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (fontChooser.showDialog(this) != JFontChooser.OK_OPTION) {
            return;
        }
        editorTextPane.setFont(fontChooser.getSelectedFont());
        currentFont = fontChooser.getSelectedFont();
    }

    private void patternComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        watermarkTabEnable();
    }

    private void modeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
        setEditorMode();
        editorTextPane.setText(plainText);
    }

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser chooser = new JFileChooser(lastOpenedDirectory);
        Utils.setFileChooserProperties(chooser);
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        watermarkImageText.setText(chooser.getSelectedFile().getPath());
        lastOpenedDirectory = chooser.getSelectedFile().getPath();
    }

    private void setEditorMode(){
        plainText = editorTextPane.getText();
        if(modeComboBox.getSelectedItem() == WatermarkMode.TEXT_MODE) {
            jScrollPane2.setVisible(true);
            watermarkImageText.setVisible(false);
            browseButton.setVisible(false);
            fontButton.setVisible(true);
            colorButton.setVisible(true);
            editorJLabel.setText(bundle.getString("WatermarkJPanel.editorJLabel.text.text"));
            editorTextPane.setContentType("text/html");
            editorTextPane.setFont(currentFont);
            editorTextPane.setForeground(color);
        } else if(modeComboBox.getSelectedItem() == WatermarkMode.HTML_MODE) {
            fontButton.setVisible(false);
            colorButton.setVisible(false);
            jScrollPane2.setVisible(true);
            watermarkImageText.setVisible(false);
            browseButton.setVisible(false);
            editorJLabel.setText(bundle.getString("WatermarkJPanel.editorJLabel.text.html"));
            editorTextPane.setContentType("text/plain");
            editorTextPane.setFont(defaultFont);
            editorTextPane.setForeground(Color.BLACK);
        } else {
            fontButton.setVisible(false);
            colorButton.setVisible(false);
            jScrollPane2.setVisible(false);
            watermarkImageText.setVisible(true);
            browseButton.setVisible(true);
            editorJLabel.setText(bundle.getString("WatermarkJPanel.editorJLabel.text.image"));
        }
    }

    @Override
    public WatermarkParameters getImageEditParameters() {
        WatermarkParameters watermarkParameters = new WatermarkParameters();
        watermarkParameters.setEnabled(watermarkEnableCheckBox.isSelected());

        watermarkParameters.setWatermarkText(editorTextPane.getText());

        watermarkParameters.setPattern(getWatermarkPatternSelection());
        watermarkParameters.setTiledRows((Integer) rowsSlidder.getValue());
        watermarkParameters.setTiledColumns((Integer) coloumnsSpinner.getValue());

        watermarkParameters.setFont(fontChooser.getSelectedFont());
        watermarkParameters.setColor(watermarkPlacer.getColor());

        watermarkParameters.setComponentWidth(watermarkPlacer.getWidth());
        watermarkParameters.setComponentHeight(watermarkPlacer.getHeight());
        watermarkParameters.setCenterX(watermarkPlacer.getLabelCenterX());
        watermarkParameters.setCenterY(watermarkPlacer.getLabelCenterY());

        BufferedImage watermarkImage = computeWatermarkBufferedImage();

        watermarkParameters.setWatermarkImage(watermarkImage);
        watermarkParameters.setWatermarkWidth(watermarkImage.getWidth());
        watermarkParameters.setWatermarkHeight(watermarkImage.getHeight());
        return watermarkParameters;
    }

    private BufferedImage computeWatermarkBufferedImage() {
        BufferedImage watermark;

        if (modeComboBox.getSelectedItem() != WatermarkMode.IMAGE_MODE) {
            watermark = watermarkImageService.textToImage(
                editorTextPane.getText(),
                editorTextPane.getForeground(),
                editorTextPane.getFont(),
                watermarkPlacer.getAlpha(),
                modeComboBox.getSelectedItem() == WatermarkMode.TEXT_MODE);
        } else {
            watermark = imageService.load(watermarkImageText.getText());
        }

        float angle = (float) (((360-rotateSlider.getValue())*Math.PI)/180);
        RotateFilter rotateFilter = new RotateFilter(angle);
        return rotateFilter.filter(watermark, null);
    }

    private void watermarkTabEnable() {
        boolean enable = watermarkEnableCheckBox.isSelected();
        boolean tiled = patternComboBox.getSelectedItem() == WatermarkPattern.TILED;

        watermarkPlacer.setEnabled(enable);
        patternLabel.setEnabled(enable);
        patternComboBox.setEnabled(enable);
        opacityLabel.setEnabled(enable);
        opacitySlider.setEnabled(enable);

        rowsLabel.setEnabled(enable && tiled);
        rowsSlidder.setEnabled(enable && tiled);
        columnsLabel.setEnabled(enable && tiled);
        coloumnsSpinner.setEnabled(enable && tiled);
        rotateLabel.setEnabled(enable);
        rotateSlider.setEnabled(enable);
        for(Component comp : settingsPanel.getComponents()){
            comp.setEnabled(enable);
        }
        editorTextPane.setEnabled(enable);
    }

    public String getColoumnsSpinner() {
        return coloumnsSpinner.getValue().toString();
    }

    public void setColoumnsSpinner(int coloumnsSpinner) {
        this.coloumnsSpinner.getModel().setValue(coloumnsSpinner);
    }

    public String getEditorTextPane() {
        return editorTextPane.getText();
    }

    public void setEditorTextPane(String editorTextPane) {
        this.editorTextPane.setText(editorTextPane);
    }

    public WatermarkMode getModeComboBox() {
        return (WatermarkMode) modeComboBox.getSelectedItem();
    }

    public void setModeComboBox(WatermarkMode watermarkMode) {
        this.modeComboBox.setSelectedItem(watermarkMode);
    }

    public String getOpacitySlider() {
        return String.valueOf(opacitySlider.getValue());
    }

    public void setOpacitySlider(int opacitySlider) {
        this.opacitySlider.getModel().setValue(opacitySlider);
    }

    public WatermarkPattern getWatermarkPatternSelection() {
        return (WatermarkPattern) patternComboBox.getSelectedItem();
    }

    public void setWatermarkPatternSelection(WatermarkPattern patternComboBox) {
        this.patternComboBox.setSelectedItem(patternComboBox);
    }

    public String getRowsSlidder() {
        return String.valueOf(rowsSlidder.getValue());
    }

    public void setRowsSlider(int rowsSlidder) {
        this.rowsSlidder.getModel().setValue(rowsSlidder);
    }

    public boolean getWatermarkEnableCheckBox() {
        return watermarkEnableCheckBox.isSelected();
    }

    public void setWatermarkEnableCheckBox(boolean watermarkEnableCheckBox) {
        this.watermarkEnableCheckBox.setSelected(watermarkEnableCheckBox);
    }

    public String getWatermarkImageText() {
        return watermarkImageText.getText();
    }

    public void setWatermarkImageText(String watermarkImageText) {
        this.watermarkImageText.setText(watermarkImageText);
    }

    @Override
    public Properties saveState(Properties properties) {
        properties.setProperty("watermarkColumns", getColoumnsSpinner());
        properties.setProperty("watermarkText", getEditorTextPane());
        properties.setProperty("watermarkMode", getModeComboBox().name());
        properties.setProperty("watermarkOpacity", getOpacitySlider());
        properties.setProperty("watermarkPattern", getWatermarkPatternSelection().toString());
        properties.setProperty("watermarkRows", getRowsSlidder());
        properties.setProperty("watermarkEnable", getWatermarkEnableCheckBox() ? "1" : "0");
        properties.setProperty("watermarkImage", getWatermarkImageText());
        return properties;
    }

    @Override
    public void restoreState(Properties properties) {
        setColoumnsSpinner(Integer.parseInt(properties.getProperty("watermarkColumns")));
        setEditorTextPane(properties.getProperty("watermarkText"));
        setModeComboBox(WatermarkMode.valueOf(properties.getProperty("watermarkMode")));
        setOpacitySlider(Integer.parseInt(properties.getProperty("watermarkOpacity")));
        setWatermarkPatternSelection(WatermarkPattern.valueOf(properties.getProperty("watermarkPattern")));
        setRowsSlider(Integer.parseInt(properties.getProperty("watermarkRows")));
        setWatermarkEnableCheckBox(properties.getProperty("watermarkEnable").equals("1"));
        setWatermarkImageText(properties.getProperty("watermarkImage"));
    }
}
