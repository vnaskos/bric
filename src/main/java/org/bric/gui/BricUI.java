package org.bric.gui;

import org.bric.core.input.DirectoryScanner;
import org.bric.core.model.ImportedImage;
import org.bric.core.model.output.OutputParameters;
import org.bric.gui.input.ListModel;
import org.bric.gui.inputOutput.ProgressBarFrame;
import org.bric.gui.output.OutputTab;
import org.bric.gui.preferences.PreferencesFrame;
import org.bric.gui.swing.ArrayListTransferHandler;
import org.bric.gui.tabs.ResizeJPanel;
import org.bric.gui.tabs.RotateJPanel;
import org.bric.gui.tabs.WatermarkJPanel;
import org.bric.imageEditParameters.ResizeParameters;
import org.bric.imageEditParameters.RotateParameters;
import org.bric.imageEditParameters.WatermarkParameters;
import org.bric.processor.ImageProcessHandler;
import org.bric.utils.Utils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BricUI extends JFrame {

    static ResourceBundle bundle;

    private final ImageIcon DEFAULT_ICON = new javax.swing.ImageIcon(getClass().getResource("/resource/preview.png"));

    private javax.swing.JToggleButton alwaysOnTopButton;
    private javax.swing.JPanel detailsPanel;
    private javax.swing.JList<ImportedImage> inputList;
    private javax.swing.JLabel itemsCountLabel;
    private javax.swing.JTextPane metadataPane;
    private javax.swing.JLabel previewIcon;

    private final PreferencesFrame preferencesFrame;
    private final About aboutFrame;

    private final OutputTab outputTab;
    private final ResizeJPanel resizeTab;
    private final RotateJPanel rotateTab;
    private final WatermarkJPanel watermarkTab;

    private ListModel<ImportedImage> model;
    static int duplicateAction = Utils.NOT_SET;
    int previewState;
    

    public static String lastOpenedDirectory = "";

    JFileChooser propertiesChooser;
    Properties properties;
    
    /**
     * Creates new form Main
     */
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
        outputTab = new OutputTab();
        resizeTab = new ResizeJPanel();
        rotateTab = new RotateJPanel();
        watermarkTab = new WatermarkJPanel();

        initComponents();

        properties = new Properties();
        
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resource/logo.png")));
        initializeProperties();
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
        JPanel inputPane = new JPanel();
        JButton addButton = new JButton();
        JButton removeButton = new JButton();
        JButton clearButton = new JButton();
        itemsCountLabel = new javax.swing.JLabel();
        detailsPanel = new javax.swing.JPanel();
        previewIcon = new javax.swing.JLabel();
        JScrollPane metadataScrollPane = new JScrollPane();
        metadataPane = new javax.swing.JTextPane();
        JScrollPane inputListScrollPane = new JScrollPane();
        model = new ListModel<>();
        inputList = new javax.swing.JList<>(model);

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
        hideDetailsButton.addActionListener(evt -> hideDetailsButtonActionPerformed());

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
        saveButton.addActionListener(evt -> saveSettings());

        loadButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/disc-cd.png"))); // NOI18N
        loadButton.setToolTipText(bundle.getString("BricUI.loadButton.toolTipText")); // NOI18N
        loadButton.setBorderPainted(false);
        loadButton.setContentAreaFilled(false);
        loadButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/disc-cd_p.png"))); // NOI18N
        loadButton.addActionListener(evt -> loadSettings());

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

        inputPane.setMinimumSize(new java.awt.Dimension(355, 480));

        addButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/add.png"))); // NOI18N
        addButton.setToolTipText(bundle.getString("BricUI.addButton.toolTipText")); // NOI18N
        addButton.setBorderPainted(false);
        addButton.setContentAreaFilled(false);
        addButton.setDoubleBuffered(true);
        addButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/add_p.png"))); // NOI18N
        addButton.addActionListener(evt -> importImages());

        removeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/remove.png"))); // NOI18N
        removeButton.setToolTipText(bundle.getString("BricUI.removeButton.toolTipText")); // NOI18N
        removeButton.setBorderPainted(false);
        removeButton.setContentAreaFilled(false);
        removeButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/remove_p.png"))); // NOI18N
        removeButton.addActionListener(evt -> removeButtonActionPerformed());

        clearButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/error.png"))); // NOI18N
        clearButton.setToolTipText(bundle.getString("BricUI.clearButton.toolTipText")); // NOI18N
        clearButton.setBorderPainted(false);
        clearButton.setContentAreaFilled(false);
        clearButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/error_p.png"))); // NOI18N
        clearButton.addActionListener(evt -> clearButtonActionPerformed());

        itemsCountLabel.setFont(new java.awt.Font("DejaVu Sans Light", Font.PLAIN, 14)); // NOI18N
        itemsCountLabel.setText(bundle.getString("BricUI.itemsCountLabel.text")); // NOI18N

        detailsPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        detailsPanel.setMinimumSize(new java.awt.Dimension(167, 136));

        previewIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        previewIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/preview.png"))); // NOI18N
        previewIcon.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        previewIcon.setMaximumSize(new java.awt.Dimension(130, 130));
        previewIcon.setMinimumSize(new java.awt.Dimension(130, 130));
        previewIcon.setPreferredSize(new java.awt.Dimension(130, 130));

        metadataPane.setEditable(false);
        metadataPane.setContentType("text/html"); // NOI18N
        metadataPane.setMinimumSize(new java.awt.Dimension(6, 128));
        metadataPane.setPreferredSize(new java.awt.Dimension(6, 128));
        metadataScrollPane.setViewportView(metadataPane);

        javax.swing.GroupLayout detailsPanelLayout = new javax.swing.GroupLayout(detailsPanel);
        detailsPanel.setLayout(detailsPanelLayout);
        detailsPanelLayout.setHorizontalGroup(
                detailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(detailsPanelLayout.createSequentialGroup()
                                .addComponent(previewIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(metadataScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE))
        );
        detailsPanelLayout.setVerticalGroup(
                detailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, detailsPanelLayout.createSequentialGroup()
                                .addGap(0, 0, 0)
                                .addGroup(detailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(metadataScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                                        .addComponent(previewIcon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        inputList.setTransferHandler(new ArrayListTransferHandler());
        inputList.setDragEnabled(true);
        inputList.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        inputList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                inputListMouseClicked(evt);
            }
        });
        inputList.addListSelectionListener(evt -> inputListValueChanged());
        inputListScrollPane.setViewportView(inputList);

        javax.swing.GroupLayout inputPaneLayout = new javax.swing.GroupLayout(inputPane);
        inputPane.setLayout(inputPaneLayout);
        inputPaneLayout.setHorizontalGroup(
                inputPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(inputPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(removeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(clearButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(itemsCountLabel)
                                .addContainerGap())
                        .addComponent(inputListScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
                        .addComponent(detailsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        inputPaneLayout.setVerticalGroup(
                inputPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(inputPaneLayout.createSequentialGroup()
                                .addComponent(inputListScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(inputPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(inputPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(removeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(clearButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(itemsCountLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(detailsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(2, 2, 2))
        );

        workspace.setLeftComponent(inputPane);

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

    private void removeButtonActionPerformed() {
        try {
            removeImages();
        } catch (Exception e) {
            previewIcon.setIcon(DEFAULT_ICON);
        }
    }

    private void clearButtonActionPerformed() {
        try {
            clearAll();
        } catch (Exception e) {
            previewIcon.setIcon(DEFAULT_ICON);
        }
    }

    private void alwaysOnTopButtonActionPerformed() {
        this.setAlwaysOnTop(alwaysOnTopButton.isSelected());
    }

    private void hideDetailsButtonActionPerformed() {
        previewState++;
        switch (previewState) {
            case 0:
                previewIcon.setVisible(true);
                detailsPanel.setVisible(true);
                break;
            case 1:
                previewIcon.setVisible(false);
                break;
            case 2:
                previewIcon.setVisible(false);
                detailsPanel.setVisible(false);
                break;
            case 3:
                previewState = 0;
            default:
                previewIcon.setVisible(true);
                detailsPanel.setVisible(true);
        }
    }

    private void startButtonActionPerformed() {
        if (model.isEmpty()) {
            return;
        }

        startProcess(false);
    }


    private void inputListValueChanged() {
        try {
            ImportedImage importedImage = model.get(inputList.getSelectedIndex());
            
            generateThumbnailMetadataOnDemand(importedImage);
            
            previewIcon.setIcon(importedImage.getThumbnailImageIcon());
            previewInfo(importedImage.getPath(), importedImage.getDimensions(), importedImage.getSize());
        } catch (Exception e) {
            previewIcon.setIcon(DEFAULT_ICON);
        }
    }

    private void previewButtonActionPerformed() {
        if (inputList.getSelectedValue() == null) {
            return;
        }

        startProcess(true);
    }

    private void inputListMouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            try {
                if(model.isEmpty()){
                   return; 
                }
                ImportedImage image = model.get(inputList.getSelectedIndex());
                Desktop.getDesktop().open(new File(image.getPath()));
            } catch (IOException ex) {
                Logger.getLogger(BricUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
        java.awt.EventQueue.invokeLater(() -> {
            JFrame bricUI = new BricUI();
            Utils.centerWindow(bricUI);
            bricUI.setVisible(true);
        });
    }

    synchronized public static void duplicatePane(String file) {

        if (duplicateAction == Utils.NOT_SET || duplicateAction == Utils.REPLACE || duplicateAction == Utils.SKIP) {

            Object[] selectionValues = {BricUI.bundle.getString("BricUI.duplicate.replaceAll"), 
                BricUI.bundle.getString("BricUI.duplicate.replace"),
                BricUI.bundle.getString("BricUI.duplicate.skipAll"),
                BricUI.bundle.getString("BricUI.duplicate.skip")};

            String initialSelection = selectionValues[0].toString();

            Object selection;

            do{
            selection = JOptionPane.showInputDialog(
                    null, String.format(BricUI.bundle.getString("BricUI.duplicate.text"), "\n"+file+"\n"),
                    BricUI.bundle.getString("BricUI.duplicate.title"), JOptionPane.QUESTION_MESSAGE,
                    null, selectionValues, initialSelection);
            } while(selection == null);
            
            int answer = 0;
            int i = 0;

            for (Object value : selectionValues) {
                if (selection == value.toString()) {
                    answer = i;
                }
                i++;
            }
            switch (answer) {
                case 0:
                    duplicateAction = Utils.REPLACE_ALL;
                    break;
                case 1:
                    duplicateAction = Utils.REPLACE;
                    break;
                case 2:
                    duplicateAction = Utils.SKIP_ALL;
                    break;
                case 3:
                    duplicateAction = Utils.SKIP;
                    break;
                default:
                    duplicateAction = Utils.NOT_SET;
            }
        }
    }

    private void removeImages() {
        model.remove(inputList.getSelectedIndices());
        metadataPane.setText("");
        updateItemsLabel();
    }

    private void updateItemsLabel() {
        itemsCountLabel.setText(bundle.getString("BricUI.itemsCountLabel.text") + model.getSize());

        if (model.getSize() == 0) {
            duplicateAction = Utils.NOT_SET;
        }
    }

    private void clearAll() {
        model.clear();
        metadataPane.setText("");
        updateItemsLabel();
    }

    private void previewInfo(String listSelected, String dimensions, long filesize) {
        String text = "<html><body>";
        text += "<b>" + bundle.getString("BricUI.metadata.name") + "</b><br />";
        text += listSelected.substring(listSelected.lastIndexOf(Utils.FS) + 1) + "<br /><br />";
        text += "<b>" + bundle.getString("BricUI.metadata.dimensions") +" </b><br />" + dimensions + "<br />";
        if (filesize != 0) {
            text += "<br /><b>" + bundle.getString("BricUI.metadata.filesize") + " </b><br />" + filesize / 1024 + "KB<br />";
        }
        text += "</body></html>";
        metadataPane.setText(text);
        metadataPane.setCaretPosition(0);
    }
    
    public void importImages(){
        final List<String> imagesList = readImages();
        
        if(imagesList.isEmpty()){
            return;
        }

        ExecutorService executorService;
        if (Utils.prefs.getInt("importNumThreads", 0) == 0) {
            executorService = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());
        } else {
            executorService = Executors.newWorkStealingPool(Utils.prefs.getInt("importNumThreads", 1));
        }

        final ProgressBarFrame importer = new ProgressBarFrame();
        importer.setImagesCount(imagesList.size());
        importer.setVisible(true);

        for (String s : imagesList) {
            executorService.submit(importFile(importer, s));
        }
    }

    private Callable<Void> importFile(final ProgressBarFrame progressBar, final String path) {
        return () -> {
            if (model.contains(img -> img.getPath().equals(path))) {
                duplicatePane(path);
            }

            if (duplicateAction == Utils.SKIP ||
                    duplicateAction == Utils.SKIP_ALL) {
                progressBar.updateValue(true);
                progressBar.showProgress(path);
                return null;
            }

            ImportedImage im = new ImportedImage(path);

            if (!im.isCorrupted()) {
                addToModel(im);
            }

            progressBar.updateValue(!im.isCorrupted());
            progressBar.showProgress(path);
            return null;
        };
    }
    
    public void addToModel(final ImportedImage im) {
        SwingUtilities.invokeLater(() -> {
            model.addElement(im);
            updateItemsLabel();
        });
    }
    
    public List<String> readImages(){
        JFileChooser chooser = new JFileChooser(lastOpenedDirectory);
        Utils.setFileChooserProperties(chooser);
        //Open the dialog
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return Collections.emptyList();
        }
        lastOpenedDirectory = chooser.getSelectedFile().getParent();

        List<String> imagePaths = new ArrayList<>();
        for (File source : chooser.getSelectedFiles()) {
            imagePaths.addAll(DirectoryScanner.listFiles(source));
        }
        return imagePaths;
    }
    
    private void generateThumbnailMetadataOnDemand(ImportedImage importedImage){
        boolean thumbnail = Utils.prefs.getBoolean("thumbnail", true)
                && Utils.prefs.getInt("thumbWay", 0) == 1
                && importedImage.getThumbnailImageIcon() == null;

        boolean metadata = Utils.prefs.getBoolean("metadata", true)
                && Utils.prefs.getInt("metaWay", 0) == 1
                && importedImage.getDimensions().equals("unknown");

        Utils.setMetadataThumbnail(importedImage, metadata, thumbnail);
    }
    
    public void startProcess(final boolean preview){
        OutputParameters outputParameters = outputTab.getImageEditParameters();
        ResizeParameters resizeParameters = resizeTab.getImageEditParameters();
        RotateParameters rotateParameters = rotateTab.getImageEditParameters();
        WatermarkParameters watermarkParameters = watermarkTab.getImageEditParameters();

        ImageProcessHandler mainProcess;
        if(preview){
            ImportedImage imageToPreview = model.get(inputList.getSelectedIndex());
            mainProcess = ImageProcessHandler.createPreviewProcess(outputParameters, imageToPreview);
        } else {
            mainProcess = new ImageProcessHandler(outputParameters, model.getElements());
        }
        mainProcess.setResizeParameters(resizeParameters);
        mainProcess.setRotateParameters(rotateParameters);
        mainProcess.setWatermarkParameters(watermarkParameters);

        mainProcess.start();
    }
    
    private void initializeProperties(){
        propertiesChooser = new JFileChooser();
        propertiesChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        propertiesChooser.setFileFilter(new FileNameExtensionFilter("properties file(*.properties)", "PROPERTIES"));
    }
    
    private void saveSettings() {
        FileOutputStream out = null;
        try {
            propertiesChooser.setDialogTitle("Save properties");
            
            if (propertiesChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            String file = propertiesChooser.getSelectedFile().getPath();
            if(!file.matches(".+\\.properties")){
                file = propertiesChooser.getSelectedFile().getPath()+".properties";
            }
            out = new FileOutputStream(new File(file));

            properties.setProperty("fileTypeCombo", Integer.toString(outputTab.getFileTypeComboIndex()));
            properties.setProperty("outputPathText", outputTab.getOutputPathText());
            properties.setProperty("qualityValue", Integer.toString(outputTab.getQualitySliderValue()));
            properties.setProperty("startIndexValue", Integer.toString(outputTab.getStartIndexSpinnerValue()));

            properties.setProperty("resizeAntialising", resizeTab.getAntialisingCheckBox() ? "1" : "0");
            properties.setProperty("resizeAspect", resizeTab.getAspectCheckBox() ? "1" : "0");
            properties.setProperty("resizeHeight", resizeTab.getHeightSpinner());
            properties.setProperty("resizeOrientation", resizeTab.getOrientationCheckBox() ? "1" : "0");
            properties.setProperty("resizeRendering", Integer.toString(resizeTab.getRenderingComboBox()));
            properties.setProperty("resizeEnable", resizeTab.getResizeEnableCheckBox() ? "1" : "0");
            properties.setProperty("resizeFilter", Integer.toString(resizeTab.getResizeFilterComboBox()));
            properties.setProperty("resizeSharpen", Integer.toString(resizeTab.getSharpenComboBox()));
            properties.setProperty("resizeUnits", Integer.toString(resizeTab.getUnitCombo()));
            properties.setProperty("resizeWidth", resizeTab.getWidthSpinner());

            properties.setProperty("rotateEnable", rotateTab.getRotateEnableCheckBox() ? "1" : "0");
            properties.setProperty("rotateAction", Integer.toString(rotateTab.getActionsComboBox()));
            properties.setProperty("rotateAngle", rotateTab.getAngleSlider());
            properties.setProperty("rotateCustom", rotateTab.getCustomRadioButton() ? "1" : "0");
            properties.setProperty("rotateDifferentValue", rotateTab.getDifferentValueCheckBox() ? "1" : "0");
            properties.setProperty("rotateMinLimit", rotateTab.getFromSpinner());
            properties.setProperty("rotateLimit", rotateTab.getLimitCheckBox() ? "1" : "0");
            properties.setProperty("rotatePredifiend", rotateTab.getPredefinedRadioButton() ? "1" : "0");
            properties.setProperty("rotateRandom", rotateTab.getRandomCheckBox() ? "1" : "0");
            properties.setProperty("rotateMaxLimit", rotateTab.getToSpinner());

            properties.setProperty("watermarkColumns", watermarkTab.getColoumnsSpinner());
            properties.setProperty("watermarkText", watermarkTab.getEditorTextPane());
            properties.setProperty("watermarkMode", Integer.toString(watermarkTab.getModeComboBox()));
            properties.setProperty("watermarkOpacity", watermarkTab.getOpacitySlider());
            properties.setProperty("watermarkPattern", Integer.toString(watermarkTab.getPatternComboBox()));
            properties.setProperty("watermarkRows", watermarkTab.getRowsSlidder());
            properties.setProperty("watermarkEnable", watermarkTab.getWatermarkEnableCheckBox() ? "1" : "0");
            properties.setProperty("watermarkImage", watermarkTab.getWatermarkImageText());
            
            properties.store(out, "");
        } catch (IOException ex) {
            Logger.getLogger(BricUI.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if(out != null){
                    out.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(BricUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void loadSettings() {
        FileInputStream fileInput = null;
        try {
            propertiesChooser.setDialogTitle("Load properties");

            if (propertiesChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            
            File file = propertiesChooser.getSelectedFile();
            fileInput = new FileInputStream(file);
            properties.load(fileInput);

            outputTab.setFileTypeComboIndex(properties.getProperty("fileTypeCombo"));
            outputTab.setOutputPathText(properties.getProperty("outputPathText"));
            outputTab.setQualitySliderValue(properties.getProperty("qualityValue"));
            outputTab.setStartIndexSpinnerValue(Integer.parseInt(properties.getProperty("startIndexValue")));

            resizeTab.setAntialisingCheckBox(properties.getProperty("resizeAntialising").equals("1"));
            resizeTab.setAspectCheckBox(properties.getProperty("resizeAspect").equals("1"));
            resizeTab.setHeightSpinner(properties.getProperty("resizeHeight"));
            resizeTab.setOrientationCheckBox(properties.getProperty("resizeOrientation").equals("1"));
            resizeTab.setRenderingComboBox(Integer.parseInt(properties.getProperty("resizeRendering")));
            resizeTab.setResizeEnableCheckBox(properties.getProperty("resizeEnable").equals("1"));
            resizeTab.setResizeFilterComboBox(Integer.parseInt(properties.getProperty("resizeFilter")));
            resizeTab.setSharpenComboBox(Integer.parseInt(properties.getProperty("resizeSharpen")));
            resizeTab.setUnitCombo(Integer.parseInt(properties.getProperty("resizeUnits")));
            resizeTab.setWidthSpinner(Integer.parseInt(properties.getProperty("resizeWidth")));

            rotateTab.setRotateEnableCheckBox(properties.getProperty("rotateEnable").equals("1"));
            rotateTab.setActionsComboBox(Integer.parseInt(properties.getProperty("rotateAction")));
            rotateTab.setAngleSlider(Integer.parseInt(properties.getProperty("rotateAngle")));
            rotateTab.setCustomRadioButton(properties.getProperty("rotateCustom").equals("1"));
            rotateTab.setDifferentValueCheckBox(properties.getProperty("rotateDifferentValue").equals("1"));
            rotateTab.setFromSpinner(Integer.parseInt(properties.getProperty("rotateMinLimit")));
            rotateTab.setLimitCheckBox(properties.getProperty("rotateLimit").equals("1"));
            rotateTab.setPredefinedRadioButton(properties.getProperty("rotatePredifiend").equals("1"));
            rotateTab.setRandomCheckBox(properties.getProperty("rotateRandom").equals("1"));
            rotateTab.setToSpinner(Integer.parseInt(properties.getProperty("rotateMaxLimit")));

            watermarkTab.setColoumnsSpinner(Integer.parseInt(properties.getProperty("watermarkColumns")));
            watermarkTab.setEditorTextPane(properties.getProperty("watermarkText"));
            watermarkTab.setModeComboBox(Integer.parseInt(properties.getProperty("watermarkMode")));
            watermarkTab.setOpacitySlider(Integer.parseInt(properties.getProperty("watermarkOpacity")));
            watermarkTab.setPatternComboBox(Integer.parseInt(properties.getProperty("watermarkPattern")));
            watermarkTab.setRowsSlider(Integer.parseInt(properties.getProperty("watermarkRows")));
            watermarkTab.setWatermarkEnableCheckBox(properties.getProperty("watermarkEnable").equals("1"));
            watermarkTab.setWatermarkImageText(properties.getProperty("watermarkImage"));
            
        } catch (IOException ex) {
            Logger.getLogger(BricUI.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if(fileInput != null){
                    fileInput.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(BricUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}