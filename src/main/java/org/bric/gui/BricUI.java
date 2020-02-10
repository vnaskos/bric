package org.bric.gui;

import org.bric.core.model.ImportedImage;
import org.bric.core.model.TabParameters;
import org.bric.core.model.output.OutputParameters;
import org.bric.gui.inputOutput.ProgressBarFrame;
import org.bric.gui.output.OutputTab;
import org.bric.gui.preferences.PreferencesFrame;
import org.bric.gui.swing.ArrayListTransferHandler;
import org.bric.gui.tabs.ImageEditTab;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class BricUI extends JFrame {

    static ResourceBundle bundle;

    private final ImageIcon DEFAULT_ICON = new javax.swing.ImageIcon(getClass().getResource("/resource/preview.png"));

    private javax.swing.JButton aboutButton;
    private javax.swing.JButton addButton;
    private javax.swing.JToggleButton alwaysOnTopButton;
    private javax.swing.JButton clearButton;
    private javax.swing.JPanel detailsPanel;
    private javax.swing.JTabbedPane editPane;
    private javax.swing.JButton hideDetailsButton;
    private javax.swing.JList inputList;
    private javax.swing.JScrollPane inputListScrollPane;
    private javax.swing.JPanel inputPane;
    private javax.swing.JLabel itemsCountLabel;
    private javax.swing.JButton loadButton;
    private javax.swing.JTextPane metadataPane;
    private javax.swing.JScrollPane metadataScrollPane;
    private javax.swing.JButton preferencesButton;
    private javax.swing.JButton previewButton;
    private javax.swing.JLabel previewIcon;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton startButton;
    private javax.swing.JPanel toolBar;
    private javax.swing.JSplitPane workspace;

    DefaultListModel model = new DefaultListModel();
    static int duplicateAction = Utils.NOT_SET;
    int previewState;
    
    private PreferencesFrame preferencesFrame = new PreferencesFrame();
    private About aboutFrame = new About();
    public static String lastOpenedDirectory = "";
    
    List<String> imagesList;
    ArrayListTransferHandler arrayListHandler;
    
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
             
        initComponents();
        arrayListHandler = new ArrayListTransferHandler();
        inputList.setModel(model);
        inputList.setTransferHandler(arrayListHandler);
        inputList.setDragEnabled(true);
        editPane.add(bundle.getString("BricUI.outputTab.name"), new OutputTab());
        editPane.add(bundle.getString("BricUI.resizeTab.name"), new ResizeJPanel());
        editPane.add(bundle.getString("BricUI.rotateTab.name"), new RotateJPanel());
        editPane.add(bundle.getString("BricUI.watermarkTab.name"), new WatermarkJPanel());
        properties = new Properties();
        
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resource/logo.png")));
        initializeProperties();
    }
    
    private void initComponents() {
        toolBar = new javax.swing.JPanel();
        alwaysOnTopButton = new javax.swing.JToggleButton();
        hideDetailsButton = new javax.swing.JButton();
        preferencesButton = new javax.swing.JButton();
        startButton = new javax.swing.JButton();
        previewButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        loadButton = new javax.swing.JButton();
        aboutButton = new javax.swing.JButton();
        workspace = new javax.swing.JSplitPane();
        editPane = new javax.swing.JTabbedPane();
        inputPane = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        itemsCountLabel = new javax.swing.JLabel();
        detailsPanel = new javax.swing.JPanel();
        previewIcon = new javax.swing.JLabel();
        metadataScrollPane = new javax.swing.JScrollPane();
        metadataPane = new javax.swing.JTextPane();
        inputListScrollPane = new javax.swing.JScrollPane();
        inputList = new javax.swing.JList();

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
        alwaysOnTopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alwaysOnTopButtonActionPerformed(evt);
            }
        });

        hideDetailsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/refresh.png"))); // NOI18N
        hideDetailsButton.setToolTipText(bundle.getString("BricUI.hideDetailsButton.toolTipText")); // NOI18N
        hideDetailsButton.setBorderPainted(false);
        hideDetailsButton.setContentAreaFilled(false);
        hideDetailsButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/refresh_p.png"))); // NOI18N
        hideDetailsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hideDetailsButtonActionPerformed(evt);
            }
        });

        preferencesButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/configuration.png"))); // NOI18N
        preferencesButton.setToolTipText(bundle.getString("BricUI.preferencesButton.toolTipText")); // NOI18N
        preferencesButton.setBorderPainted(false);
        preferencesButton.setContentAreaFilled(false);
        preferencesButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/configuration_p.png"))); // NOI18N
        preferencesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preferencesButtonActionPerformed(evt);
            }
        });

        startButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/tick.png"))); // NOI18N
        startButton.setToolTipText(bundle.getString("BricUI.startButton.toolTipText")); // NOI18N
        startButton.setBorderPainted(false);
        startButton.setContentAreaFilled(false);
        startButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/tick_p.png"))); // NOI18N
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        previewButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/photo-camera.png"))); // NOI18N
        previewButton.setToolTipText(bundle.getString("BricUI.previewButton.toolTipText")); // NOI18N
        previewButton.setBorderPainted(false);
        previewButton.setContentAreaFilled(false);
        previewButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/photo-camera_p.png"))); // NOI18N
        previewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previewButtonActionPerformed(evt);
            }
        });

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/disc-floopy.png"))); // NOI18N
        saveButton.setToolTipText(bundle.getString("BricUI.saveButton.toolTipText")); // NOI18N
        saveButton.setBorderPainted(false);
        saveButton.setContentAreaFilled(false);
        saveButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/disc-floopy_p.png"))); // NOI18N
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        loadButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/disc-cd.png"))); // NOI18N
        loadButton.setToolTipText(bundle.getString("BricUI.loadButton.toolTipText")); // NOI18N
        loadButton.setBorderPainted(false);
        loadButton.setContentAreaFilled(false);
        loadButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/disc-cd_p.png"))); // NOI18N
        loadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadButtonActionPerformed(evt);
            }
        });

        aboutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/info.png"))); // NOI18N
        aboutButton.setToolTipText(bundle.getString("BricUI.aboutButton.toolTipText")); // NOI18N
        aboutButton.setBorderPainted(false);
        aboutButton.setContentAreaFilled(false);
        aboutButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/info_p.png"))); // NOI18N
        aboutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutButtonActionPerformed(evt);
            }
        });

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

        toolBarLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[]{aboutButton, hideDetailsButton});

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

        inputPane.setMinimumSize(new java.awt.Dimension(355, 480));

        addButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/add.png"))); // NOI18N
        addButton.setToolTipText(bundle.getString("BricUI.addButton.toolTipText")); // NOI18N
        addButton.setBorderPainted(false);
        addButton.setContentAreaFilled(false);
        addButton.setDoubleBuffered(true);
        addButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/add_p.png"))); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        removeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/remove.png"))); // NOI18N
        removeButton.setToolTipText(bundle.getString("BricUI.removeButton.toolTipText")); // NOI18N
        removeButton.setBorderPainted(false);
        removeButton.setContentAreaFilled(false);
        removeButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/remove_p.png"))); // NOI18N
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        clearButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/error.png"))); // NOI18N
        clearButton.setToolTipText(bundle.getString("BricUI.clearButton.toolTipText")); // NOI18N
        clearButton.setBorderPainted(false);
        clearButton.setContentAreaFilled(false);
        clearButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/error_p.png"))); // NOI18N
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });

        itemsCountLabel.setFont(new java.awt.Font("DejaVu Sans Light", 0, 14)); // NOI18N
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

        inputList.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        inputList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                inputListMouseClicked(evt);
            }
        });
        inputList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                inputListValueChanged(evt);
            }
        });
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

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {
        importImages();
    }

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            removeImages();
        } catch (Exception e) {
            previewIcon.setIcon(DEFAULT_ICON);
        }
    }

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            clearAll();
        } catch (Exception e) {
            previewIcon.setIcon(DEFAULT_ICON);
        }
    }

    private void preferencesButtonActionPerformed(java.awt.event.ActionEvent evt) {
        preferencesFrame.setVisible(true);
    }

    private void alwaysOnTopButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.setAlwaysOnTop(alwaysOnTopButton.isSelected());
    }

    private void hideDetailsButtonActionPerformed(java.awt.event.ActionEvent evt) {
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

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if(!model.isEmpty()){
            startProcess(false);
        }
    }


    private void inputListValueChanged(javax.swing.event.ListSelectionEvent evt) {
        try {
            ImportedImage importedImage = (ImportedImage) model.get(inputList.getSelectedIndex());
            
            generateThumbnailMetadataOnDemand(importedImage);
            
            previewIcon.setIcon(importedImage.getThumbnailImageIcon());
            previewInfo(importedImage.getPath(), importedImage.getDimensions(), importedImage.getSize());
        } catch (Exception e) {
            previewIcon.setIcon(DEFAULT_ICON);
        }
    }

    private void previewButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if(inputList.getSelectedValue() != null){
            startProcess(true);
        }
    }

    private void inputListMouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            try {
                if(model.isEmpty()){
                   return; 
                }
                ImportedImage image = (ImportedImage) model.get(inputList.getSelectedIndex());
                Desktop.getDesktop().open(new File(image.getPath()));
            } catch (IOException ex) {
                Logger.getLogger(BricUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {
        saveSettings();
    }

    private void loadButtonActionPerformed(java.awt.event.ActionEvent evt) {
        loadSettings();
    }

    private void aboutButtonActionPerformed(java.awt.event.ActionEvent evt) {
        aboutFrame.setVisible(true);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(BricUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BricUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BricUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BricUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame bricUI = new BricUI();
                Utils.centerWindow(bricUI);
                bricUI.setVisible(true);
            }
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
        ArrayList<Integer> toBeDeleted = new ArrayList<Integer>();

        for (int number : inputList.getSelectedIndices()) {
            toBeDeleted.add(number);
        }

        Collections.reverse(toBeDeleted);

        for (int value : toBeDeleted) {
            removeFromHash(((ImportedImage)model.get(value)).getPath());
            model.remove(value);
            
        }

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
        imagesList.clear();
        clearHash();
        System.gc();
        metadataPane.setText("");
        updateItemsLabel();
    }

    private void previewInfo(String listSelected, String dimensions, long filesize) {
        String text = "<html><body>";
        text += "<b>" + bundle.getString("BricUI.metadata.name") + "</b><br />";
        text += listSelected.substring(listSelected.lastIndexOf(Utils.FS) + 1, listSelected.length()) + "<br /><br />";
        text += "<b>" + bundle.getString("BricUI.metadata.dimensions") +" </b><br />" + dimensions + "<br />";
        if (filesize != 0) {
            text += "<br /><b>" + bundle.getString("BricUI.metadata.filesize") + " </b><br />" + filesize / 1024 + "KB<br />";
        }
        text += "</body></html>";
        metadataPane.setText(text);
        metadataPane.setCaretPosition(0);
    }
    
    public void importImages(){
        imagesList = readImages();
        
        if(imagesList == Collections.EMPTY_LIST){
            return;
        }
        
        Thread a  = new Thread(new Runnable() {

            @Override
            public void run() {
                final ProgressBarFrame importer = new ProgressBarFrame();
                importer.setImagesCount(imagesList.size());
                importer.setVisible(true);

                int processors;
                if(Utils.prefs.getInt("importNumThreads", 0) == 0){
                    processors = Runtime.getRuntime().availableProcessors();
                } else {
                    processors = Utils.prefs.getInt("importNumThreads", 1);
                }
                final int step = (int) Math.ceil((double) imagesList.size() / processors);

                for (int j = 0; j < imagesList.size(); j += step) {
                    importImageThread(importer, j, step);
                }
            }
        });
        a.start();
    }
    
    private void importImageThread(final ProgressBarFrame importer, final int from, final int step) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i = from; i < from + step; i++) {
                    if (!importer.isVisible()) {
                        return;
                    }
                    if (i < imagesList.size()) {
                        
                        if (hash.contains(imagesList.get(i))) {
                            duplicatePane(imagesList.get(i));
                        }

                        if (duplicateAction == Utils.REPLACE || duplicateAction == Utils.REPLACE_ALL || !hash.contains(imagesList.get(i))) {
                            ImportedImage im = new ImportedImage( imagesList.get(i) );
                            if(!im.isCorrupted()){
                                addToModel(im);
                                importer.updateValue(true);
                            }else{
                                importer.updateValue(false);
                            }
                        } else {
                            importer.updateValue(true);
                        }
                        
                        importer.showProgress(imagesList.get(i));
                    } else {
                        break;
                    }
                }
                
            }
        }).start();
    }
    
    private static HashSet<String> hash = new HashSet<String>();
    
    public static void removeFromHash(String path){
        hash.remove(path);
    }
    
    public static void clearHash(){
        hash.clear();
    }
    
    public synchronized void addToModel(final ImportedImage im) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(hash.contains(im.getPath())){
                    model.removeElement(im);
                } else {
                    hash.add(im.getPath());
                }
                model.addElement(im);
                updateItemsLabel();
            }
        });
    }
    
    public List<String> readImages(){
        ArrayList<String> imagePaths = new ArrayList<String>();
        JFileChooser chooser = new JFileChooser(lastOpenedDirectory);
        Utils.setFileChooserProperties(chooser);
        //Open the dialog
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return Collections.EMPTY_LIST;
        }
        lastOpenedDirectory = chooser.getSelectedFile().getParent();
        
        for(File path : chooser.getSelectedFiles()){
            if(path.isDirectory()){
                ArrayList<String> directoryChildren = new ArrayList<String>();
                scanDirectory(path, directoryChildren);
                imagePaths.addAll(directoryChildren);
            }else{
                imagePaths.add(path.getPath());
            }
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
    
    private void scanDirectory(File file, ArrayList<String> list) {
        File[] children = file.listFiles();

        if (children == null) {
            return;
        }

        for (File child : children) {
            if (child.isFile() && child.getName().contains(".")) {
                String extension = child.toString().substring(child.toString().lastIndexOf('.') + 1);
                for(String ext : Utils.supportedInputExtensions){
                    if(ext.equalsIgnoreCase(extension)){
                        list.add(child.getPath());
                    }
                }
            } else if (child.isDirectory()) {
                scanDirectory(child, list);
            }
        }
    }
    
    public void startProcess(final boolean preview){
        new Thread(new Runnable() {

            @Override
            public void run() {
                OutputParameters outputParameters = null;
                ResizeParameters resizeParameters = null;
                RotateParameters rotateParameters = null;
                WatermarkParameters watermarkParameters = null;

                for (int i = 0; i < editPane.getComponentCount(); i++) {
                    TabParameters tabParameters = ((ImageEditTab) editPane.getComponentAt(i)).getImageEditParameters();
                    if (tabParameters instanceof OutputParameters) {
                        outputParameters = (OutputParameters) tabParameters;
                    } else if (tabParameters instanceof ResizeParameters) {
                        resizeParameters = (ResizeParameters) tabParameters;
                    } else if (tabParameters instanceof RotateParameters) {
                        rotateParameters = (RotateParameters) tabParameters;
                    } else if (tabParameters instanceof WatermarkParameters) {
                        watermarkParameters = (WatermarkParameters) tabParameters;
                    }
                }

                ImageProcessHandler mainProcess;
                if(preview){
                    ImportedImage imageToPreview = (ImportedImage) model.get(inputList.getSelectedIndex());
                    mainProcess = ImageProcessHandler.createPreviewProcess(outputParameters, imageToPreview);
                } else {
                    mainProcess = new ImageProcessHandler(outputParameters, model);
                }
                mainProcess.setResizeParameters(resizeParameters);
                mainProcess.setRotateParameters(rotateParameters);
                mainProcess.setWatermarkParameters(watermarkParameters);

                mainProcess.start();
                
            }
        }).start();
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
            
            OutputTab outputTab = (OutputTab) editPane.getComponentAt(0);
            properties.setProperty("fileTypeCombo", Integer.toString(outputTab.getFileTypeComboIndex()));
            properties.setProperty("outputPathText", outputTab.getOutputPathText());
            properties.setProperty("qualityValue", Integer.toString(outputTab.getQualitySliderValue()));
            properties.setProperty("startIndexValue", Integer.toString(outputTab.getStartIndexSpinnerValue()));
            
            ResizeJPanel resizeJPanel = (ResizeJPanel) editPane.getComponentAt(1);
            properties.setProperty("resizeAntialising", resizeJPanel.getAntialisingCheckBox() == true ? "1" : "0");
            properties.setProperty("resizeAspect", resizeJPanel.getAspectCheckBox() == true ? "1" : "0");
            properties.setProperty("resizeHeight", resizeJPanel.getHeightSpinner());
            properties.setProperty("resizeOrientation", resizeJPanel.getOrientationCheckBox() == true ? "1" : "0");
            properties.setProperty("resizeRendering", Integer.toString(resizeJPanel.getRenderingComboBox()));
            properties.setProperty("resizeEnable", resizeJPanel.getResizeEnableCheckBox() == true ? "1" : "0");
            properties.setProperty("resizeFilter", Integer.toString(resizeJPanel.getResizeFilterComboBox()));
            properties.setProperty("resizeSharpen", Integer.toString(resizeJPanel.getSharpenComboBox()));
            properties.setProperty("resizeUnits", Integer.toString(resizeJPanel.getUnitCombo()));
            properties.setProperty("resizeWidth", resizeJPanel.getWidthSpinner());
            
            RotateJPanel rotateJPanel = (RotateJPanel) editPane.getComponentAt(2);
            properties.setProperty("rotateEnable", rotateJPanel.getRotateEnableCheckBox() == true ? "1" : "0");
            properties.setProperty("rotateAction", Integer.toString(rotateJPanel.getActionsComboBox()));
            properties.setProperty("rotateAngle", rotateJPanel.getAngleSlider());
            properties.setProperty("rotateCustom", rotateJPanel.getCustomRadioButton() == true ? "1" : "0");
            properties.setProperty("rotateDifferentValue", rotateJPanel.getDifferentValueCheckBox() == true ? "1" : "0");
            properties.setProperty("rotateMinLimit", rotateJPanel.getFromSpinner());
            properties.setProperty("rotateLimit", rotateJPanel.getLimitCheckBox() == true ? "1" : "0");
            properties.setProperty("rotatePredifiend", rotateJPanel.getPredefinedRadioButton() == true ? "1" : "0");
            properties.setProperty("rotateRandom", rotateJPanel.getRandomCheckBox() == true ? "1" : "0");
            properties.setProperty("rotateMaxLimit", rotateJPanel.getToSpinner());
            
            WatermarkJPanel watermarkJPanel = (WatermarkJPanel) editPane.getComponentAt(3);
            properties.setProperty("watermarkColoumns", watermarkJPanel.getColoumnsSpinner());
            properties.setProperty("watermarkText", watermarkJPanel.getEditorTextPane());
            properties.setProperty("watermarkMode", Integer.toString(watermarkJPanel.getModeComboBox()));
            properties.setProperty("watermarkOpacity", watermarkJPanel.getOpacitySlider());
            properties.setProperty("watermarkPattern", Integer.toString(watermarkJPanel.getPatternComboBox()));
            properties.setProperty("watermarkRows", watermarkJPanel.getRowsSlidder());
            properties.setProperty("watermarkEnable", watermarkJPanel.getWatermarkEnableCheckBox() == true ? "1" : "0");
            properties.setProperty("watermarkImage", watermarkJPanel.getWatermarkImageText());
            
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
            
            OutputTab outputTab = (OutputTab) editPane.getComponentAt(0);
            outputTab.setFileTypeComboIndex(properties.getProperty("fileTypeCombo"));
            outputTab.setOutputPathText(properties.getProperty("outputPathText"));
            outputTab.setQualitySliderValue(properties.getProperty("qualityValue"));
            outputTab.setStartIndexSpinnerValue(Integer.parseInt(properties.getProperty("startIndexValue")));
            
            ResizeJPanel resizeJPanel = (ResizeJPanel) editPane.getComponentAt(1);
            resizeJPanel.setAntialisingCheckBox(properties.getProperty("resizeAntialising").equals("1") ? true : false);
            resizeJPanel.setAspectCheckBox(properties.getProperty("resizeAspect").equals("1") ? true : false);
            resizeJPanel.setHeightSpinner(properties.getProperty("resizeHeight"));
            resizeJPanel.setOrientationCheckBox(properties.getProperty("resizeOrientation").equals("1") ? true : false);
            resizeJPanel.setRenderingComboBox(Integer.parseInt(properties.getProperty("resizeRendering")));
            resizeJPanel.setResizeEnableCheckBox(properties.getProperty("resizeEnable").equals("1") ? true : false);
            resizeJPanel.setResizeFilterComboBox(Integer.parseInt(properties.getProperty("resizeFilter")));
            resizeJPanel.setSharpenComboBox(Integer.parseInt(properties.getProperty("resizeSharpen")));
            resizeJPanel.setUnitCombo(Integer.parseInt(properties.getProperty("resizeUnits")));
            resizeJPanel.setWidthSpinner(Integer.parseInt(properties.getProperty("resizeWidth")));
                    
            RotateJPanel rotateJPanel = (RotateJPanel) editPane.getComponentAt(2);
            rotateJPanel.setRotateEnableCheckBox(properties.getProperty("rotateEnable").equals("1") ? true : false);
            rotateJPanel.setActionsComboBox(Integer.parseInt(properties.getProperty("rotateAction")));
            rotateJPanel.setAngleSlider(Integer.parseInt(properties.getProperty("rotateAngle")));
            rotateJPanel.setCustomRadioButton(properties.getProperty("rotateCustom").equals("1") ? true : false);
            rotateJPanel.setDifferentValueCheckBox(properties.getProperty("rotateDifferentValue").equals("1") ? true : false);
            rotateJPanel.setFromSpinner(Integer.parseInt(properties.getProperty("rotateMinLimit")));
            rotateJPanel.setLimitCheckBox(properties.getProperty("rotateLimit").equals("1") ? true : false);
            rotateJPanel.setPredefinedRadioButton(properties.getProperty("rotatePredifiend").equals("1") ? true : false);
            rotateJPanel.setRandomCheckBox(properties.getProperty("rotateRandom").equals("1") ? true : false);
            rotateJPanel.setToSpinner(Integer.parseInt(properties.getProperty("rotateMaxLimit")));
            
            WatermarkJPanel watermarkJPanel = (WatermarkJPanel) editPane.getComponentAt(3);
            watermarkJPanel.setColoumnsSpinner(Integer.parseInt(properties.getProperty("watermarkColoumns")));
            watermarkJPanel.setEditorTextPane(properties.getProperty("watermarkText"));
            watermarkJPanel.setModeComboBox(Integer.parseInt(properties.getProperty("watermarkMode")));
            watermarkJPanel.setOpacitySlider(Integer.parseInt(properties.getProperty("watermarkOpacity")));
            watermarkJPanel.setPatternComboBox(Integer.parseInt(properties.getProperty("watermarkPattern")));
            watermarkJPanel.setRowsSlider(Integer.parseInt(properties.getProperty("watermarkRows")));
            watermarkJPanel.setWatermarkEnableCheckBox(properties.getProperty("watermarkEnable").equals("1") ? true : false);
            watermarkJPanel.setWatermarkImageText(properties.getProperty("watermarkImage"));
            
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