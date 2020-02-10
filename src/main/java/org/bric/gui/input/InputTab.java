package org.bric.gui.input;

import org.bric.core.input.DirectoryScanner;
import org.bric.core.model.ImportedImage;
import org.bric.gui.BricUI;
import org.bric.gui.inputOutput.ProgressBarFrame;
import org.bric.gui.swing.ArrayListTransferHandler;
import org.bric.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InputTab extends JPanel {

    private ResourceBundle bundle;

    private ListModel<ImportedImage> model;
    private javax.swing.JList<ImportedImage> inputList;
    private javax.swing.JLabel itemsCountLabel;
    private final InputDetailsPanel inputDetailsPanel;

    private int duplicateAction = Utils.NOT_SET;
    private String lastOpenedDirectory = "";

    public InputTab() {
        bundle = ResourceBundle.getBundle("lang/gui/BricUI");

        JScrollPane inputListScrollPane = new JScrollPane();
        model = new ListModel<>();
        inputList = new javax.swing.JList<>(model);
        JButton addButton = new JButton();
        JButton removeButton = new JButton();
        JButton clearButton = new JButton();
        itemsCountLabel = new javax.swing.JLabel();
        inputDetailsPanel = new InputDetailsPanel();

        this.setMinimumSize(new java.awt.Dimension(355, 480));

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

        javax.swing.GroupLayout inputPaneLayout = new javax.swing.GroupLayout(this);
        this.setLayout(inputPaneLayout);
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
                        .addComponent(inputDetailsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                                .addComponent(inputDetailsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(2, 2, 2))
        );
    }

    public List<ImportedImage> getInputItems() {
        return model.getElements();
    }

    public ImportedImage getSelectedItem() {
        return model.getElementAt(inputList.getSelectedIndex());
    }

    public void changeDetailsView() {
        inputDetailsPanel.updateViewState();
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

    private void inputListValueChanged() {
        try {
            ImportedImage selectedItem = model.get(inputList.getSelectedIndex());

            generateThumbnailMetadataOnDemand(selectedItem);

            inputDetailsPanel.updateIcon(selectedItem.getThumbnailImageIcon());
            inputDetailsPanel.updateDetails(selectedItem.getPath(), selectedItem.getDimensions(), selectedItem.getSize());
        } catch (Exception e) {
            inputDetailsPanel.clearPreview();
        }
    }

    public void addToModel(final ImportedImage im) {
        SwingUtilities.invokeLater(() -> {
            model.addElement(im);
            updateItemsLabel();
        });
    }

    private void removeImages() {
        model.remove(inputList.getSelectedIndices());
        inputDetailsPanel.clear();
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
        inputDetailsPanel.clear();
        updateItemsLabel();
    }

    private void removeButtonActionPerformed() {
        try {
            removeImages();
        } catch (Exception e) {
            inputDetailsPanel.clearPreview();
        }
    }

    private void clearButtonActionPerformed() {
        try {
            clearAll();
        } catch (Exception e) {
            inputDetailsPanel.clearPreview();
        }
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


    public void importImages(){
        final java.util.List<String> imagesList = readImages();

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

    public java.util.List<String> readImages(){
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

    synchronized public void duplicatePane(String file) {

        if (duplicateAction == Utils.NOT_SET || duplicateAction == Utils.REPLACE || duplicateAction == Utils.SKIP) {

            Object[] selectionValues = {bundle.getString("BricUI.duplicate.replaceAll"),
                    bundle.getString("BricUI.duplicate.replace"),
                    bundle.getString("BricUI.duplicate.skipAll"),
                    bundle.getString("BricUI.duplicate.skip")};

            String initialSelection = selectionValues[0].toString();

            Object selection;

            do{
                selection = JOptionPane.showInputDialog(
                        null, String.format(bundle.getString("BricUI.duplicate.text"), "\n"+file+"\n"),
                        bundle.getString("BricUI.duplicate.title"), JOptionPane.QUESTION_MESSAGE,
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
}
