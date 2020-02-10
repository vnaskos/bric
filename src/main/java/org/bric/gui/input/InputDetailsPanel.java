package org.bric.gui.input;

import org.bric.utils.Utils;

import javax.swing.*;
import java.util.ResourceBundle;

public class InputDetailsPanel extends JPanel {

    static ResourceBundle bundle;

    private final ImageIcon DEFAULT_ICON = new javax.swing.ImageIcon(getClass().getResource("/resource/preview.png"));

    private javax.swing.JTextPane metadataPane;
    private javax.swing.JLabel previewIcon;

    private int previewState;

    public InputDetailsPanel() {
        bundle = ResourceBundle.getBundle("lang/gui/BricUI");

        javax.swing.GroupLayout detailsPanelLayout = new javax.swing.GroupLayout(this);
        JScrollPane metadataScrollPane = new JScrollPane();
        metadataPane = new javax.swing.JTextPane();
        previewIcon = new javax.swing.JLabel();

        this.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        this.setMinimumSize(new java.awt.Dimension(167, 136));
        this.setLayout(detailsPanelLayout);

        previewIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        previewIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/preview.png")));
        previewIcon.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        previewIcon.setMaximumSize(new java.awt.Dimension(130, 130));
        previewIcon.setMinimumSize(new java.awt.Dimension(130, 130));
        previewIcon.setPreferredSize(new java.awt.Dimension(130, 130));

        metadataPane.setEditable(false);
        metadataPane.setContentType("text/html");
        metadataPane.setMinimumSize(new java.awt.Dimension(6, 128));
        metadataPane.setPreferredSize(new java.awt.Dimension(6, 128));
        metadataScrollPane.setViewportView(metadataPane);

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
    }

    public void clear() {
        clearPreview();
        metadataPane.setText("");
    }

    public void clearPreview() {
        previewIcon.setIcon(DEFAULT_ICON);
    }

    public void updateIcon(ImageIcon icon) {
        previewIcon.setIcon(icon);
    }

    public void updateViewState() {
        previewState++;
        switch (previewState) {
            case 0:
                previewIcon.setVisible(true);
                this.setVisible(true);
                break;
            case 1:
                previewIcon.setVisible(false);
                break;
            case 2:
                previewIcon.setVisible(false);
                this.setVisible(false);
                break;
            case 3:
                previewState = 0;
            default:
                previewIcon.setVisible(true);
                this.setVisible(true);
        }
    }

    public void updateDetails(String itemSource, String dimensions, long fileSize) {
        String text = "<html><body>";
        text += "<b>" + bundle.getString("BricUI.metadata.name") + "</b><br />";
        text += itemSource.substring(itemSource.lastIndexOf(Utils.FS) + 1) + "<br /><br />";
        text += "<b>" + bundle.getString("BricUI.metadata.dimensions") +" </b><br />" + dimensions + "<br />";
        if (fileSize != 0) {
            text += "<br /><b>" + bundle.getString("BricUI.metadata.filesize") + " </b><br />" + fileSize / 1024 + "KB<br />";
        }
        text += "</body></html>";
        metadataPane.setText(text);
        metadataPane.setCaretPosition(0);
    }
}
