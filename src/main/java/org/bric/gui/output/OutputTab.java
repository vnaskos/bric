package org.bric.gui.output;

import org.bric.core.model.output.OutputParameters;
import org.bric.core.model.output.OutputType;
import org.bric.gui.tabs.ImageEditTab;
import org.bric.gui.tabs.StatefulComponent;
import org.bric.utils.Utils;

import javax.swing.*;
import java.util.Properties;
import java.util.ResourceBundle;

public class OutputTab extends javax.swing.JPanel implements ImageEditTab, StatefulComponent {

    private javax.swing.JComboBox<OutputType> fileTypeCombo;
    private javax.swing.JTextField outputPathText;
    private javax.swing.JLabel qualityLabel;
    private javax.swing.JSlider qualitySlider;
    private javax.swing.JSpinner startIndexSpinner;

    private SpinnerNumberModel numberingModel;

    ResourceBundle bundle;

    private String lastOpenedDirectory = "";

    public OutputTab() {

        bundle = ResourceBundle.getBundle("lang/gui/tabs/OutputJPanel");

        this.numberingModel = new SpinnerNumberModel(1, null, null, 1);

        initComponents();
    }

    private void initComponents() {

        outputPathText = new javax.swing.JTextField();
        JButton browseButton = new JButton();
        JLabel formatLabel = new JLabel();
        fileTypeCombo = new javax.swing.JComboBox<>(OutputType.values());
        JLabel numberingLabel = new JLabel();
        startIndexSpinner = new javax.swing.JSpinner(numberingModel);
        qualityLabel = new javax.swing.JLabel();
        qualitySlider = new javax.swing.JSlider();
        JScrollPane fileNameMasksScrollPane = new JScrollPane();
        JTextPane fileNameMasksPane = new JTextPane();

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
                    .addComponent(fileNameMasksScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 83, Short.MAX_VALUE)
                .addComponent(qualityLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(qualitySlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(fileNameMasksScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }

    private void browseButtonActionPerformed() {
        JFileChooser chooser = new JFileChooser(lastOpenedDirectory);
        Utils.setFileChooserProperties(chooser);
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        outputPathText.setText(chooser.getSelectedFile().getPath() + Utils.FS);
        lastOpenedDirectory = chooser.getSelectedFile().getPath();
    }

    private void fileTypeComboItemStateChanged() {
        OutputType outputType = fileTypeCombo.getItemAt(fileTypeCombo.getSelectedIndex());
        boolean qualityEnable = outputType == OutputType.SAME_AS_FIRST ||
                                outputType == OutputType.JPG ||
                                outputType == OutputType.JPEG;
        qualityLabel.setEnabled(qualityEnable);
        qualitySlider.setEnabled(qualityEnable);
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
        return new OutputParameters(getOutputPathText(),
                fileTypeCombo.getItemAt(fileTypeCombo.getSelectedIndex()),
                numberingModel.getNumber().intValue(),
                qualitySlider.getValue()/100.0F);
    }

    @Override
    public Properties saveState(Properties properties) {
        properties.setProperty("fileTypeCombo", Integer.toString(getFileTypeComboIndex()));
        properties.setProperty("outputPathText", getOutputPathText());
        properties.setProperty("qualityValue", Integer.toString(getQualitySliderValue()));
        properties.setProperty("startIndexValue", Integer.toString(getStartIndexSpinnerValue()));
        return properties;
    }

    @Override
    public void restoreState(Properties properties) {
        setFileTypeComboIndex(properties.getProperty("fileTypeCombo"));
        setOutputPathText(properties.getProperty("outputPathText"));
        setQualitySliderValue(properties.getProperty("qualityValue"));
        setStartIndexSpinnerValue(Integer.parseInt(properties.getProperty("startIndexValue")));
    }
}
