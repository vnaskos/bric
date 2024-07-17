package org.bric.gui.input;

import org.bric.core.input.DirectoryScanner;
import org.bric.core.input.model.GenerationMethod;
import org.bric.core.input.model.ImportedImage;
import org.bric.core.model.DuplicateAction;
import org.bric.gui.dialog.ProgressBarFrame;
import org.bric.gui.swing.ArrayListTransferHandler;
import org.bric.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InputTab extends JPanel {

    private final ResourceBundle bundle;
    private final ListModel<ImportedImage> model;
    private final JList<ImportedImage> inputList;
    private final JLabel itemsCountLabel;
    private final InputDetailsPanel inputDetailsPanel;

    private DuplicateAction duplicateAction = DuplicateAction.NOT_SET;
    private String lastOpenedDirectory = "";

    public InputTab() {
        bundle = ResourceBundle.getBundle("lang/gui/BricUI");

        JScrollPane inputListScrollPane = new JScrollPane();
        model = new ListModel<>();
        inputList = new javax.swing.JList<>(model);
        JButton addButton = new JButton();
        JButton removeButton = new JButton();
        JButton clearButton = new JButton();
        itemsCountLabel = new JLabel();
        inputDetailsPanel = new InputDetailsPanel();

        this.setMinimumSize(new java.awt.Dimension(355, 480));

        addButton.setIcon(new javax.swing.ImageIcon(Objects.requireNonNull(getClass().getResource("/resource/icons/add.png")))); // NOI18N
        addButton.setToolTipText(bundle.getString("BricUI.addButton.toolTipText")); // NOI18N
        addButton.setBorderPainted(false);
        addButton.setContentAreaFilled(false);
        addButton.setDoubleBuffered(true);
        addButton.setRolloverIcon(new javax.swing.ImageIcon(Objects.requireNonNull(getClass().getResource("/resource/icons/add_p.png")))); // NOI18N
        addButton.addActionListener(evt -> importImages());

        removeButton.setIcon(new javax.swing.ImageIcon(Objects.requireNonNull(getClass().getResource("/resource/icons/remove.png")))); // NOI18N
        removeButton.setToolTipText(bundle.getString("BricUI.removeButton.toolTipText")); // NOI18N
        removeButton.setBorderPainted(false);
        removeButton.setContentAreaFilled(false);
        removeButton.setRolloverIcon(new javax.swing.ImageIcon(Objects.requireNonNull(getClass().getResource("/resource/icons/remove_p.png")))); // NOI18N
        removeButton.addActionListener(evt -> removeButtonActionPerformed());

        clearButton.setIcon(new javax.swing.ImageIcon(Objects.requireNonNull(getClass().getResource("/resource/icons/error.png")))); // NOI18N
        clearButton.setToolTipText(bundle.getString("BricUI.clearButton.toolTipText")); // NOI18N
        clearButton.setBorderPainted(false);
        clearButton.setContentAreaFilled(false);
        clearButton.setRolloverIcon(new javax.swing.ImageIcon(Objects.requireNonNull(getClass().getResource("/resource/icons/error_p.png")))); // NOI18N
        clearButton.addActionListener(evt -> clearButtonActionPerformed());

        itemsCountLabel.setFont(new java.awt.Font("DejaVu Sans Light", Font.PLAIN, 14)); // NOI18N
        itemsCountLabel.setText(bundle.getString("BricUI.itemsCountLabel.text")); // NOI18N

        inputList.setTransferHandler(new ArrayListTransferHandler());
        inputList.setDragEnabled(true);
        inputList.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        inputList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
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
                Logger.getLogger(InputTab.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void inputListValueChanged() {
        try {
            ImportedImage selectedItem = model.get(inputList.getSelectedIndex());

            inputDetailsPanel.updateIcon(selectedItem.getThumbnail()
                    .map(ImageIcon::new)
                    .orElseThrow(Exception::new));
            inputDetailsPanel.updateDetails(selectedItem.getName(), selectedItem.getDimensions(), selectedItem.getSize());
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
            duplicateAction = DuplicateAction.NOT_SET;
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

    public void importImages(){
        final List<String> imagesList = readImages();

        if (imagesList.isEmpty()){
            return;
        }

        final ProgressBarFrame importer = new ProgressBarFrame();
        importer.setImagesCount(imagesList.size());
        importer.setVisible(true);

        for (String s : imagesList) {
            Utils.getExecutorService().submit(() -> importFile(importer, s));
        }
    }

    private void importFile(final ProgressBarFrame progressBar, final String path) {
        if (model.contains(img -> img.getPath().equals(path))) {
            duplicatePane(path);
        }

        if (duplicateAction == DuplicateAction.SKIP ||
                duplicateAction == DuplicateAction.ALWAYS_SKIP) {
            progressBar.updateValue(true);
            progressBar.showProgress(path);
            return;
        }

        ImportedImage im = new ImportedImage(path, GenerationMethod::thumbnail, GenerationMethod::metadata);

        if (im.isNotCorrupted()) {
            addToModel(im);
        }

        progressBar.updateValue(im.isNotCorrupted());
        progressBar.showProgress(path);
    }

    private java.util.List<String> readImages(){
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

    public synchronized void duplicatePane(String file) {
        if (duplicateAction == DuplicateAction.NOT_SET ||
                duplicateAction == DuplicateAction.ADD ||
                duplicateAction == DuplicateAction.SKIP) {

            Object[] selectionValues = {
                    DuplicateAction.ALWAYS_ADD,
                    DuplicateAction.ADD,
                    DuplicateAction.ALWAYS_SKIP,
                    DuplicateAction.SKIP};

            Object selection;

            do{
                selection = JOptionPane.showInputDialog(
                        null, String.format(bundle.getString("BricUI.duplicate.text"), "\n"+file+"\n"),
                        bundle.getString("BricUI.duplicate.title"), JOptionPane.QUESTION_MESSAGE,
                        null, selectionValues, DuplicateAction.ALWAYS_ADD);
            } while(selection == null);

            duplicateAction = (DuplicateAction) selection;
        }
    }
}
