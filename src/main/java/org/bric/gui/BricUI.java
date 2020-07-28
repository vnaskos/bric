package org.bric.gui;

import org.bric.core.input.model.ImportedImage;
import org.bric.core.input.model.InputType;
import org.bric.core.model.DuplicateAction;
import org.bric.core.model.output.OutputParameters;
import org.bric.core.model.output.OutputType;
import org.bric.core.process.*;
import org.bric.gui.dialog.PreferencesFrame;
import org.bric.gui.dialog.ProgressBarFrame;
import org.bric.gui.input.InputTab;
import org.bric.gui.state.StateManager;
import org.bric.gui.tabs.OutputTab;
import org.bric.gui.tabs.ResizeJPanel;
import org.bric.gui.tabs.RotateJPanel;
import org.bric.gui.tabs.WatermarkJPanel;
import org.bric.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BricUI extends JFrame {

    static ResourceBundle bundle;

    private javax.swing.JToggleButton alwaysOnTopButton;

    private final PreferencesFrame preferencesFrame;
    private final About aboutFrame;

    private final StateManager stateManager;

    private final InputTab inputTab;
    private final OutputTab outputTab;
    private final ResizeJPanel resizeTab;
    private final RotateJPanel rotateTab;
    private final WatermarkJPanel watermarkTab;

    public BricUI() {
        Locale defaultLocale;
        if (Utils.prefs.getInt("locale", 0) == 0) {
            defaultLocale = Locale.ENGLISH;
        } else {
            defaultLocale = Utils.GREEK;
        }
        Locale.setDefault(defaultLocale);

        bundle = ResourceBundle.getBundle("lang/gui/BricUI");

        preferencesFrame = new PreferencesFrame();
        aboutFrame = new About();

        inputTab = new InputTab();
        outputTab = new OutputTab();
        resizeTab = new ResizeJPanel();
        rotateTab = new RotateJPanel();
        watermarkTab = new WatermarkJPanel();
        stateManager = new StateManager(outputTab, resizeTab, rotateTab, watermarkTab);

        initComponents();
        
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resource/logo.png")));
    }
    
    private void initComponents() {
        JPanel toolBar = new JPanel();
        alwaysOnTopButton = new javax.swing.JToggleButton();
        JButton hideDetailsButton = new JButton();
        JButton preferencesButton = new JButton();
        JButton startButton = new JButton();
        JButton previewButton = new JButton();
        JButton saveButton = new JButton();
        JButton loadButton = new JButton();
        JButton aboutButton = new JButton();
        JSplitPane workspace = new JSplitPane();
        JTabbedPane editPane = new JTabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("lang/gui/BricUI"); // NOI18N
        setTitle(bundle.getString("BricUI.title")); // NOI18N

        toolBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        alwaysOnTopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/clasp.png"))); // NOI18N
        alwaysOnTopButton.setToolTipText(bundle.getString("BricUI.alwaysOnTopButton.toolTipText")); // NOI18N
        alwaysOnTopButton.setBorderPainted(false);
        alwaysOnTopButton.setContentAreaFilled(false);
        alwaysOnTopButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/clasp_r.png"))); // NOI18N
        alwaysOnTopButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/clasp_r.png"))); // NOI18N
        alwaysOnTopButton.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/clasp_r.png"))); // NOI18N
        alwaysOnTopButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/clasp_p.png"))); // NOI18N
        alwaysOnTopButton.addActionListener(evt -> alwaysOnTopButtonActionPerformed());

        hideDetailsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/refresh.png"))); // NOI18N
        hideDetailsButton.setToolTipText(bundle.getString("BricUI.hideDetailsButton.toolTipText")); // NOI18N
        hideDetailsButton.setBorderPainted(false);
        hideDetailsButton.setContentAreaFilled(false);
        hideDetailsButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/refresh_p.png"))); // NOI18N
        hideDetailsButton.addActionListener(evt -> inputTab.changeDetailsView());

        preferencesButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/configuration.png"))); // NOI18N
        preferencesButton.setToolTipText(bundle.getString("BricUI.preferencesButton.toolTipText")); // NOI18N
        preferencesButton.setBorderPainted(false);
        preferencesButton.setContentAreaFilled(false);
        preferencesButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/configuration_p.png"))); // NOI18N
        preferencesButton.addActionListener(evt -> preferencesFrame.setVisible(true));

        startButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/tick.png"))); // NOI18N
        startButton.setToolTipText(bundle.getString("BricUI.startButton.toolTipText")); // NOI18N
        startButton.setBorderPainted(false);
        startButton.setContentAreaFilled(false);
        startButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/tick_p.png"))); // NOI18N
        startButton.addActionListener(evt -> startButtonActionPerformed());

        previewButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/photo-camera.png"))); // NOI18N
        previewButton.setToolTipText(bundle.getString("BricUI.previewButton.toolTipText")); // NOI18N
        previewButton.setBorderPainted(false);
        previewButton.setContentAreaFilled(false);
        previewButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/photo-camera_p.png"))); // NOI18N
        previewButton.addActionListener( evt -> previewButtonActionPerformed());

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/disc-floopy.png"))); // NOI18N
        saveButton.setToolTipText(bundle.getString("BricUI.saveButton.toolTipText")); // NOI18N
        saveButton.setBorderPainted(false);
        saveButton.setContentAreaFilled(false);
        saveButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/disc-floopy_p.png"))); // NOI18N
        saveButton.addActionListener(evt -> stateManager.saveState());

        loadButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/disc-cd.png"))); // NOI18N
        loadButton.setToolTipText(bundle.getString("BricUI.loadButton.toolTipText")); // NOI18N
        loadButton.setBorderPainted(false);
        loadButton.setContentAreaFilled(false);
        loadButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/disc-cd_p.png"))); // NOI18N
        loadButton.addActionListener(evt -> stateManager.restoreState());

        aboutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/info.png"))); // NOI18N
        aboutButton.setToolTipText(bundle.getString("BricUI.aboutButton.toolTipText")); // NOI18N
        aboutButton.setBorderPainted(false);
        aboutButton.setContentAreaFilled(false);
        aboutButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/info_p.png"))); // NOI18N
        aboutButton.addActionListener(evt -> aboutFrame.setVisible(true));

        javax.swing.GroupLayout toolBarLayout = new javax.swing.GroupLayout(toolBar);
        toolBar.setLayout(toolBarLayout);
        toolBarLayout.setHorizontalGroup(
                toolBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(toolBarLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(preferencesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(aboutButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(alwaysOnTopButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(hideDetailsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(loadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(210, 210, 210)
                                .addComponent(previewButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(9, 9, 9))
        );

        toolBarLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, aboutButton, hideDetailsButton);

        toolBarLayout.setVerticalGroup(
                toolBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(toolBarLayout.createSequentialGroup()
                                .addGap(0, 0, 0)
                                .addGroup(toolBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(alwaysOnTopButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(toolBarLayout.createSequentialGroup()
                                                .addGroup(toolBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(hideDetailsButton)
                                                        .addGroup(toolBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                .addComponent(previewButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(loadButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(saveButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(preferencesButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(startButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(aboutButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                                .addGap(0, 0, Short.MAX_VALUE))))
        );

        workspace.setDividerLocation(350);
        workspace.setResizeWeight(0.3);

        editPane.setMinimumSize(new java.awt.Dimension(370, 480));
        workspace.setRightComponent(editPane);
        editPane.add(bundle.getString("BricUI.outputTab.name"), outputTab);
        editPane.add(bundle.getString("BricUI.resizeTab.name"), resizeTab);
        editPane.add(bundle.getString("BricUI.rotateTab.name"), rotateTab);
        editPane.add(bundle.getString("BricUI.watermarkTab.name"), watermarkTab);

        workspace.setLeftComponent(inputTab);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(workspace, javax.swing.GroupLayout.PREFERRED_SIZE, 731, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(workspace)
                                .addGap(0, 0, 0)
                                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }

    private void alwaysOnTopButtonActionPerformed() {
        this.setAlwaysOnTop(alwaysOnTopButton.isSelected());
    }

    private void startButtonActionPerformed() {
        startProcess(inputTab.getInputItems());
    }

    private void previewButtonActionPerformed() {
        if (inputTab.getSelectedItem().getType() == InputType.PDF) {
            JOptionPane.showMessageDialog(null, "PDF preview is not supported yet!");
            return;
        }

        File temporary;
        try {
            temporary = File.createTempFile("preview", ".jpg");
            temporary.deleteOnExit();
        } catch (IOException ex) {
            Logger.getLogger(ImageProcessHandler.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        OutputParameters outputParameters = new OutputParameters(
                temporary.getAbsolutePath().replace(".jpg", ""),
                OutputType.JPG, 1, 1);
        FileNameService fileNameService = new FileNameService(
                outputParameters.getOutputPath(), outputParameters.getOutputType(),
                outputParameters.getNumberingStartIndex(), 1);
        ImageProcessHandler handler = new ImageProcessHandler(fileNameService, outputParameters,
                Collections.singletonList(inputTab.getSelectedItem()));

        handler.addProcessors(
                new ResizeProcessor(resizeTab.getImageEditParameters()),
                new RotateProcessor(rotateTab.getImageEditParameters()),
                new WatermarkProcessor(watermarkTab.getImageEditParameters()));

        fileNameService.setDuplicateAction(DuplicateAction.ALWAYS_OVERWRITE);

        CompletableFuture.allOf(handler.start().toArray(new CompletableFuture[1]))
                .thenAccept(v -> {
                    try {
                        Desktop.getDesktop().open(temporary);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException ex) {
            java.util.logging.Logger.getLogger(BricUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        SwingUtilities.invokeLater(() -> {
            JFrame bricUI = new BricUI();
            Utils.centerWindow(bricUI);
            bricUI.setVisible(true);
        });
    }
    
    public void startProcess(List<ImportedImage> inputItems) {
        if (inputItems == null || inputItems.isEmpty()) {
            return;
        }

        OutputParameters outputParameters = outputTab.getImageEditParameters();

        FileNameService fileNameService = new FileNameService(outputParameters.getOutputPath(),
                outputParameters.getOutputType(), outputParameters.getNumberingStartIndex(), inputItems.size());
        ImageProcessHandler mainProcess = new ImageProcessHandler(fileNameService, outputParameters, inputItems);

        mainProcess.addProcessors(
                new ResizeProcessor(resizeTab.getImageEditParameters()),
                new RotateProcessor(rotateTab.getImageEditParameters()),
                new WatermarkProcessor(watermarkTab.getImageEditParameters()));

        ProgressBarFrame progress = new ProgressBarFrame();
        progress.setImagesCount(inputItems.size());
        progress.setVisible(true);
        mainProcess.setProgressListener(path -> {
            progress.showProgress(path);
            progress.updateValue(true);
        });

        List<CompletableFuture<String>> processes = mainProcess.start();

        CompletableFuture.allOf(processes.toArray(new CompletableFuture[0]))
                .thenAccept(v -> progress.dispose());

        progress.setCancelListener(() -> processes.forEach(p -> p.cancel(true)));
    }

    public interface ProgressListener {
        void finished(String path);
    }

    public interface CancelListener {
        void canceled();
    }
}