package org.bric.gui.inputOutput;

import org.bric.gui.BricUI;
import org.bric.utils.Utils;

import javax.swing.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProgressBarFrame extends javax.swing.JFrame {

    private javax.swing.JLabel elapsedTimeLabel;
    private javax.swing.JLabel errorsLabel;
    private javax.swing.JButton hideListButton;
    private javax.swing.JScrollPane inputOutputScrollPane;
    private javax.swing.JLabel processedLabel;
    private javax.swing.JProgressBar progressBar;
    
    private final DefaultListModel<String> model;
    
    private int total = 0;
    private AtomicInteger processed;
    private AtomicInteger errors;
    private long startTime;
    private int sleepValue;

    private BricUI.CancelListener cancelListener;
    
    public ProgressBarFrame() {
        processed = new AtomicInteger(0);
        errors = new AtomicInteger(0);
        startTime = System.currentTimeMillis();
        sleepValue = Utils.prefs.getInt("sleepValue", 500);
        
        model = new DefaultListModel<>();

        initComponents();
    }
    
    private void initComponents() {

        JPanel jPanel1 = new JPanel();
        progressBar = new javax.swing.JProgressBar();
        inputOutputScrollPane = new javax.swing.JScrollPane();
        JList<String> inputOutputList = new JList<>(model);
        JPanel detailsPanel = new JPanel();
        errorsLabel = new javax.swing.JLabel();
        processedLabel = new javax.swing.JLabel();
        JButton cancelButton = new JButton();
        hideListButton = new javax.swing.JButton();
        elapsedTimeLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("lang/gui/inputOutput/ProgressBarFrame"); // NOI18N
        setTitle(bundle.getString("ProgressBarFrame.title")); // NOI18N

        inputOutputScrollPane.setViewportView(inputOutputList);

        detailsPanel.setBackground(new java.awt.Color(230, 230, 240));
        detailsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), bundle.getString("ProgressBarFrame.detailsPanel.border.title"))); // NOI18N

        errorsLabel.setText(bundle.getString("ProgressBarFrame.errorsLabel.text")); // NOI18N

        processedLabel.setText(bundle.getString("ProgressBarFrame.processedLabel.text")); // NOI18N

        cancelButton.setText(bundle.getString("ProgressBarFrame.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(evt -> cancelButtonActionPerformed());

        hideListButton.setText(bundle.getString("ProgressBarFrame.hideListButton.text")); // NOI18N
        hideListButton.addActionListener(evt -> hideListButtonActionPerformed());

        elapsedTimeLabel.setText(bundle.getString("ProgressBarFrame.elapsedTimeLabel.text")); // NOI18N

        javax.swing.GroupLayout detailsPanelLayout = new javax.swing.GroupLayout(detailsPanel);
        detailsPanel.setLayout(detailsPanelLayout);
        detailsPanelLayout.setHorizontalGroup(
            detailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(detailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(elapsedTimeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                    .addComponent(processedLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(errorsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 74, Short.MAX_VALUE)
                .addGroup(detailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(hideListButton)
                    .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        detailsPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, cancelButton, hideListButton);

        detailsPanelLayout.setVerticalGroup(
            detailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailsPanelLayout.createSequentialGroup()
                .addGroup(detailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(detailsPanelLayout.createSequentialGroup()
                        .addGroup(detailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cancelButton)
                            .addGroup(detailsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(errorsLabel)))
                        .addGap(2, 2, 2)
                        .addComponent(hideListButton))
                    .addGroup(detailsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(processedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(elapsedTimeLabel)))
                .addGap(0, 6, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(detailsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(inputOutputScrollPane)
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(detailsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inputOutputScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }

    private void cancelButtonActionPerformed() {
        if (cancelListener != null) {
            cancelListener.canceled();
        }
        this.dispose();
    }

    private void hideListButtonActionPerformed() {
        inputOutputScrollPane.setVisible(!inputOutputScrollPane.isVisible());
        hideListButton.setText(hideListButton.getText().contains("Hide") ? "Show List" : "Hide List");
        this.setResizable(!this.isResizable());
        this.pack();
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        Utils.centerWindow(this);
    }
     
    public void updateValue(boolean succeeded){
        int updatedProcessedValue = processed.incrementAndGet();
        progressBar.setValue((updatedProcessedValue*100) / total);
        updateLabel();
        
        if(!succeeded){
            updateErrors();
        }
        
        if (processed.get() == total) {
            close();
        }
    }
    
    private void updateLabel(){
        processedLabel.setText("Processed: " + processed + "/" + total);
        
        DateFormat formatter = new SimpleDateFormat("mm:ss:SSS");
        String dateFormatted = formatter.format(System.currentTimeMillis() - startTime);
        elapsedTimeLabel.setText("Elapsed Time: " + dateFormatted);
    }
    
    public void showProgress(final String file){
        SwingUtilities.invokeLater(() -> model.addElement(file));
    }
    
    public void setImagesCount(int imagesCount){
        total = imagesCount;
        processedLabel.setText("Processed: " + processed + "/" + total);
    }
    
    public void updateErrors(){
        errors.incrementAndGet();
        errorsLabel.setText("Errors: " + errors);
    }
    
    public void close(){
        try {
            Thread.sleep(sleepValue);
        } catch (InterruptedException ex) {
            Logger.getLogger(ProgressBarFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (cancelListener != null) {
            cancelListener.canceled();
        }
        this.dispose();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public void setCancelListener(BricUI.CancelListener cancelListener) {
        this.cancelListener = cancelListener;
    }
}
