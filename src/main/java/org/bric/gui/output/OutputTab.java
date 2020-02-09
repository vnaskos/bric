package org.bric.gui.output;

import org.bric.gui.BricUI;
import org.bric.gui.tabs.ImageEditTab;
import org.bric.imageEditParameters.OutputParameters;
import org.bric.utils.Utils;

import javax.swing.*;
import java.util.ResourceBundle;

public class OutputTab extends javax.swing.JPanel implements ImageEditTab {

    private javax.swing.JButton browseButton;
    private javax.swing.JLabel dpiLabel;
    private javax.swing.JTextPane fileNameMasksPane;
    private javax.swing.JScrollPane fileNameMasksScrollPane;
    private javax.swing.JComboBox<OutputType> fileTypeCombo;
    private javax.swing.JLabel formatLabel;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JSpinner jSpinner2;
    private javax.swing.JSpinner jSpinner3;
    private javax.swing.JLabel numberingLabel;
    private javax.swing.JTextField outputPathText;
    private javax.swing.JLabel qualityLabel;
    private javax.swing.JSlider qualitySlider;
    private javax.swing.JSpinner startIndexSpinner;

    ResourceBundle bundle;

    public OutputTab() {
        
        bundle = ResourceBundle.getBundle("lang/gui/tabs/OutputJPanel");
        
        initComponents();
        
        SpinnerModel numbering = new SpinnerNumberModel(1, null, null, 1);
        startIndexSpinner.setModel(numbering);
        
        SpinnerModel horizontal = new SpinnerNumberModel(300, 10, 1000, 1);
        SpinnerModel vertical = new SpinnerNumberModel(300, 10, 1000, 1);
        jSpinner2.setModel(horizontal);
        jSpinner3.setModel(vertical);
        
        dpiLabel.setVisible(false);
        jLabel7.setVisible(false);
        jLabel8.setVisible(false);
        jSpinner2.setVisible(false);
        jSpinner3.setVisible(false);
    }

    private void initComponents() {

        outputPathText = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        formatLabel = new javax.swing.JLabel();
        fileTypeCombo = new javax.swing.JComboBox<>(OutputType.values());
        numberingLabel = new javax.swing.JLabel();
        startIndexSpinner = new javax.swing.JSpinner();
        qualityLabel = new javax.swing.JLabel();
        qualitySlider = new javax.swing.JSlider();
        fileNameMasksScrollPane = new javax.swing.JScrollPane();
        fileNameMasksPane = new javax.swing.JTextPane();
        dpiLabel = new javax.swing.JLabel();
        jSpinner2 = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jSpinner3 = new javax.swing.JSpinner();

        outputPathText.setText(System.getProperty("user.home"));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("lang/gui/tabs/OutputJPanel"); // NOI18N
        browseButton.setText(bundle.getString("OutputJPanel.browseButton.text")); // NOI18N
        browseButton.setToolTipText(bundle.getString("OutputJPanel.browseButton.toolTipText")); // NOI18N
        browseButton.addActionListener(evt -> browseButtonActionPerformed());

        formatLabel.setText(bundle.getString("OutputJPanel.formatLabel.text")); // NOI18N

        fileTypeCombo.addItemListener(evt -> fileTypeComboItemStateChanged());

        numberingLabel.setText(bundle.getString("OutputJPanel.numberingLabel.text")); // NOI18N

        qualityLabel.setText(bundle.getString("OutputJPanel.qualityLabel.text") + "100%");

        qualitySlider.setMajorTickSpacing(10);
        qualitySlider.setMinorTickSpacing(5);
        qualitySlider.setPaintLabels(true);
        qualitySlider.setPaintTicks(true);
        qualitySlider.setValue(100);
        qualitySlider.addChangeListener(evt -> qualitySliderStateChanged());

        fileNameMasksPane.setEditable(false);
        fileNameMasksPane.setContentType("text/html"); // NOI18N
        fileNameMasksPane.setText(bundle.getString("OutputJpanel.fileNameMasks.text")); // NOI18N
        fileNameMasksScrollPane.setViewportView(fileNameMasksPane);

        dpiLabel.setText(bundle.getString("OutputJPanel.dpiLabel.text")); // NOI18N

        jLabel7.setText(bundle.getString("OutputJPanel.jLabel7.text")); // NOI18N

        jLabel8.setText(bundle.getString("OutputJPanel.jLabel8.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(outputPathText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(formatLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 191, Short.MAX_VALUE)
                        .addComponent(fileTypeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(numberingLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(startIndexSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(qualityLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(qualitySlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fileNameMasksScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(dpiLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(jSpinner3, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(outputPathText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(formatLabel)
                    .addComponent(fileTypeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(numberingLabel)
                    .addComponent(startIndexSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(dpiLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSpinner3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 83, Short.MAX_VALUE)
                .addComponent(qualityLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(qualitySlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(fileNameMasksScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }

    private void browseButtonActionPerformed() {
        JFileChooser chooser = new JFileChooser(BricUI.lastOpenedDirectory);
        Utils.setFileChooserProperties(chooser);
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        outputPathText.setText(chooser.getSelectedFile().getPath() + Utils.FS);
        BricUI.lastOpenedDirectory = chooser.getSelectedFile().getPath();
    }

    private void fileTypeComboItemStateChanged() {
        OutputType outputType = fileTypeCombo.getItemAt(fileTypeCombo.getSelectedIndex());
        boolean qualityEnable = outputType == OutputType.SAME_AS_FIRST ||
                                outputType == OutputType.JPG ||
                                outputType == OutputType.JPEG;
        qualityLabel.setEnabled(qualityEnable);
        qualitySlider.setEnabled(qualityEnable);
        dpiLabel.setEnabled(qualityEnable);
        jLabel7.setEnabled(qualityEnable);
        jLabel8.setEnabled(qualityEnable);
        jSpinner2.setEnabled(qualityEnable);
        jSpinner3.setEnabled(qualityEnable);
    }

    private void qualitySliderStateChanged() {
        qualityLabel.setText(String.format(bundle.getString("OutputJPanel.qualityLabel.text") + "%s%%", qualitySlider.getValue()));
    }

    public int getFileTypeComboIndex() {
        return fileTypeCombo.getSelectedIndex();
    }

    public void setFileTypeComboIndex(String index) {
        this.fileTypeCombo.setSelectedIndex(Integer.parseInt(index));
    }

    public String getOutputPathText() {
        return outputPathText.getText();
    }

    public void setOutputPathText(String outputPathText) {
        this.outputPathText.setText(outputPathText);
    }

    public int getQualitySliderValue() {
        return qualitySlider.getValue();
    }

    public void setQualitySliderValue(String value) {
        this.qualitySlider.setValue(Integer.parseInt(value));
    }

    public int getStartIndexSpinnerValue() {
        return Integer.parseInt(startIndexSpinner.getValue().toString());
    }

    public void setStartIndexSpinnerValue(int startIndex) {
        this.startIndexSpinner.getModel().setValue(startIndex);
    }

    @Override
    public OutputParameters getImageEditParameters() {
        OutputParameters outputParameters = new OutputParameters();
        outputParameters.setOutputPath(outputPathText.getText());
        outputParameters.setQuality((float)qualitySlider.getValue()/100);
        outputParameters.setOutputFormat(String.valueOf(fileTypeCombo.getSelectedItem()));
        outputParameters.setNumberingStartIndex((Integer)startIndexSpinner.getValue());
        return outputParameters;
    }
}
