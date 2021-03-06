package org.bric.gui.tabs;

import org.bric.core.process.model.RotateParameters;
import org.bric.gui.state.StatefulComponent;
import org.bric.gui.swing.JRotator;

import java.util.Properties;
import java.util.Random;

public class RotateJPanel extends javax.swing.JPanel  implements ImageEditTab, StatefulComponent {

    private int rotationUpLimit = 360,
            rotationDownLimit = 0;
    private int randomAngle;
    private int min, max;
    private Random rand = new Random();
    
    /**
     * Creates new form RotateJPanel
     */
    public RotateJPanel() {
        initComponents();
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        customPredefinedGroup = new javax.swing.ButtonGroup();
        RotateEnableCheckBox = new javax.swing.JCheckBox();
        customRadioButton = new javax.swing.JRadioButton();
        predefinedRadioButton = new javax.swing.JRadioButton();
        angleLabel = new javax.swing.JLabel();
        angleField = new javax.swing.JTextField();
        angleSlider = new javax.swing.JSlider();
        randomCheckBox = new javax.swing.JCheckBox();
        differentValueCheckBox = new javax.swing.JCheckBox();
        limitCheckBox = new javax.swing.JCheckBox();
        fromSpinner = new javax.swing.JSpinner();
        fromLimitLabel = new javax.swing.JLabel();
        toSpinner = new javax.swing.JSpinner();
        toLimitLabel = new javax.swing.JLabel();
        actionsLabel = new javax.swing.JLabel();
        actionsComboBox = new javax.swing.JComboBox();
        rotator = new JRotator();
        jSeparator1 = new javax.swing.JSeparator();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("lang/gui/tabs/RotateJPanel"); // NOI18N
        RotateEnableCheckBox.setText(bundle.getString("RotateJPanel.RotateEnableCheckBox.text")); // NOI18N
        RotateEnableCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RotateEnableCheckBoxActionPerformed(evt);
            }
        });

        customPredefinedGroup.add(customRadioButton);
        customRadioButton.setSelected(true);
        customRadioButton.setText(bundle.getString("RotateJPanel.customRadioButton.text")); // NOI18N
        customRadioButton.setEnabled(false);
        customRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                customRadioButtonItemStateChanged(evt);
            }
        });

        customPredefinedGroup.add(predefinedRadioButton);
        predefinedRadioButton.setText(bundle.getString("RotateJPanel.predefinedRadioButton.text")); // NOI18N
        predefinedRadioButton.setEnabled(false);
        predefinedRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                predefinedRadioButtonItemStateChanged(evt);
            }
        });

        angleLabel.setText(bundle.getString("RotateJPanel.angleLabel.text")); // NOI18N
        angleLabel.setEnabled(false);

        angleField.setText("0°");
        angleField.setEnabled(false);
        angleField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                angleFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                angleFieldKeyTyped(evt);
            }
        });

        angleSlider.setMajorTickSpacing(45);
        angleSlider.setMaximum(360);
        angleSlider.setMinorTickSpacing(5);
        angleSlider.setPaintLabels(true);
        angleSlider.setPaintTicks(true);
        angleSlider.setValue(0);
        angleSlider.setEnabled(false);
        angleSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                angleSliderStateChanged(evt);
            }
        });

        randomCheckBox.setText(bundle.getString("RotateJPanel.randomCheckBox.text")); // NOI18N
        randomCheckBox.setEnabled(false);
        randomCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                randomCheckBoxItemStateChanged(evt);
            }
        });

        differentValueCheckBox.setText(bundle.getString("RotateJPanel.differentValueCheckBox.text")); // NOI18N
        differentValueCheckBox.setEnabled(false);

        limitCheckBox.setText(bundle.getString("RotateJPanel.limitCheckBox.text")); // NOI18N
        limitCheckBox.setEnabled(false);
        limitCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                limitCheckBoxItemStateChanged(evt);
            }
        });

        fromSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 360, 1));
        fromSpinner.setEnabled(false);
        fromSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fromSpinnerStateChanged(evt);
            }
        });

        fromLimitLabel.setText(bundle.getString("RotateJPanel.fromLimitLabel.text")); // NOI18N
        fromLimitLabel.setEnabled(false);

        toSpinner.setModel(new javax.swing.SpinnerNumberModel(360, 0, 360, 1));
        toSpinner.setEnabled(false);
        toSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                toSpinnerStateChanged(evt);
            }
        });

        toLimitLabel.setText(bundle.getString("RotateJPanel.toLimitLabel.text")); // NOI18N
        toLimitLabel.setEnabled(false);

        actionsLabel.setText(bundle.getString("RotateJPanel.actionsLabel.text")); // NOI18N
        actionsLabel.setEnabled(false);

        actionsComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "180° Upside Down", "90° Counter Clockwise", "90° Clockwise", "Horizontal Flip", "Horizontal + Vertical Flip", "Vertical Flip" }));
        actionsComboBox.setEnabled(false);

        rotator.setEnabled(false);
        rotator.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rotatorMouseClicked(evt);
            }
        });
        rotator.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                rotatorMouseDragged(evt);
            }
        });

        javax.swing.GroupLayout rotatorLayout = new javax.swing.GroupLayout(rotator);
        rotator.setLayout(rotatorLayout);
        rotatorLayout.setHorizontalGroup(
            rotatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 240, Short.MAX_VALUE)
        );
        rotatorLayout.setVerticalGroup(
            rotatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 240, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(angleLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rotator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(angleField, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(41, 41, 41))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(angleSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(randomCheckBox)
                                    .addComponent(limitCheckBox))
                                .addGap(51, 51, 51)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(fromLimitLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(fromSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(toLimitLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(toSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(differentValueCheckBox)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(customRadioButton)
                                .addGap(18, 18, 18)
                                .addComponent(predefinedRadioButton))
                            .addComponent(RotateEnableCheckBox)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(actionsLabel)
                                .addGap(18, 18, 18)
                                .addComponent(actionsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 28, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(RotateEnableCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(customRadioButton)
                    .addComponent(predefinedRadioButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rotator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(angleLabel)
                    .addComponent(angleField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(angleSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(randomCheckBox)
                    .addComponent(differentValueCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(limitCheckBox)
                    .addComponent(toLimitLabel)
                    .addComponent(toSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fromLimitLabel)
                    .addComponent(fromSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(actionsLabel)
                    .addComponent(actionsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(36, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void RotateEnableCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RotateEnableCheckBoxActionPerformed
        rotateTabEnable();
    }//GEN-LAST:event_RotateEnableCheckBoxActionPerformed

    private void customRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_customRadioButtonItemStateChanged
        rotateTabEnable();
    }//GEN-LAST:event_customRadioButtonItemStateChanged

    private void predefinedRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_predefinedRadioButtonItemStateChanged
        rotateTabEnable();
    }//GEN-LAST:event_predefinedRadioButtonItemStateChanged

    private void angleFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_angleFieldKeyTyped
        rotateFieldAction();
    }//GEN-LAST:event_angleFieldKeyTyped

    private void angleFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_angleFieldKeyReleased
        rotateFieldAction();
    }//GEN-LAST:event_angleFieldKeyReleased

    private void angleSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_angleSliderStateChanged
        rotator.setAngle(angleSlider.getValue());
        if (!angleField.hasFocus()) {
            angleField.setText(rotator.getAngle() + "°");
        }
    }//GEN-LAST:event_angleSliderStateChanged

    private void randomCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_randomCheckBoxItemStateChanged
        rotateTabEnable();
    }//GEN-LAST:event_randomCheckBoxItemStateChanged

    private void limitCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_limitCheckBoxItemStateChanged
        rotateTabEnable();
    }//GEN-LAST:event_limitCheckBoxItemStateChanged

    private void fromSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fromSpinnerStateChanged
        setRotationLimit();
    }//GEN-LAST:event_fromSpinnerStateChanged

    private void toSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_toSpinnerStateChanged
        setRotationLimit();
    }//GEN-LAST:event_toSpinnerStateChanged

    private void rotatorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rotatorMouseClicked
       angleSlider.setValue(rotator.getAngle());
       angleField.setText(rotator.getAngle() + "°");
    }//GEN-LAST:event_rotatorMouseClicked

    private void rotatorMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rotatorMouseDragged
        angleSlider.setValue(rotator.getAngle());
        angleField.setText(rotator.getAngle() + "°");
    }//GEN-LAST:event_rotatorMouseDragged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox RotateEnableCheckBox;
    private javax.swing.JComboBox actionsComboBox;
    private javax.swing.JLabel actionsLabel;
    private javax.swing.JTextField angleField;
    private javax.swing.JLabel angleLabel;
    private javax.swing.JSlider angleSlider;
    private javax.swing.ButtonGroup customPredefinedGroup;
    private javax.swing.JRadioButton customRadioButton;
    private javax.swing.JCheckBox differentValueCheckBox;
    private javax.swing.JLabel fromLimitLabel;
    private javax.swing.JSpinner fromSpinner;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JCheckBox limitCheckBox;
    private javax.swing.JRadioButton predefinedRadioButton;
    private javax.swing.JCheckBox randomCheckBox;
    private JRotator rotator;
    private javax.swing.JLabel toLimitLabel;
    private javax.swing.JSpinner toSpinner;
    // End of variables declaration//GEN-END:variables

    @Override
    public RotateParameters getImageEditParameters() {
        RotateParameters rotateParameters = new RotateParameters();
        rotateParameters.setEnabled(RotateEnableCheckBox.isSelected());
        rotateParameters.setCustom(customRadioButton.isSelected());
        rotateParameters.setPredefined(predefinedRadioButton.isSelected());
        int angle = rotator.getAngle();
        rotateParameters.setAngle(angle);
        rotateParameters.setRandom(randomCheckBox.isSelected());
        rotateParameters.setDifferentValues(differentValueCheckBox.isSelected());
        rotateParameters.setLimit(limitCheckBox.isSelected());
        rotateParameters.setFrom((Integer)fromSpinner.getValue());
        rotateParameters.setTo((Integer)toSpinner.getValue());
        rotateParameters.setAction(actionsComboBox.getSelectedIndex());
        rotateParameters.setRandomAngle(calculateRandomAngle());

        return rotateParameters;
    }
    
    private int calculateRandomAngle(){
        if(limitCheckBox.isSelected()){
            min = Integer.parseInt(fromSpinner.getValue().toString());
            max = Integer.parseInt(toSpinner.getValue().toString());
            
            if(min > max){
                int reminder = max;
                max = min;
                min = reminder;
            }
            
            randomAngle = rand.nextInt(max-min+1)+min;
        } else {
            randomAngle = rand.nextInt(360);
        }
        return randomAngle; 
    }
    
    private void rotateTabEnable() {
        boolean enable = RotateEnableCheckBox.isSelected();
        boolean predefined = predefinedRadioButton.isSelected();
        boolean random = randomCheckBox.isSelected();
        boolean limit = limitCheckBox.isSelected();
        customRadioButton.setEnabled(enable);
        predefinedRadioButton.setEnabled(enable);
        rotator.setEnabled(enable & !random & !predefined);
        angleLabel.setEnabled(enable & !random & !predefined);
        angleField.setEnabled(enable & !random & !predefined);
        angleSlider.setEnabled(enable & !random & !predefined);
        randomCheckBox.setEnabled(enable & !predefined);
        differentValueCheckBox.setEnabled(enable & random & !predefined);
        limitCheckBox.setEnabled(enable & random & !predefined);
        fromLimitLabel.setEnabled(enable & !predefined & limit & random);
        fromSpinner.setEnabled(enable & !predefined & limit & random);
        toLimitLabel.setEnabled(enable & !predefined & limit & random);
        toSpinner.setEnabled(enable & !predefined & limit & random);
        actionsLabel.setEnabled(enable & predefined);
        actionsComboBox.setEnabled(enable & predefined);
    }
    
    private void rotateFieldAction() {
        int symbol = angleField.getText().contains("°") ? 1 : 0;
        int fieldNumber = 0;
        try {
            fieldNumber = Integer.parseInt(angleField.getText(0, angleField.getText().length() - symbol));
        } catch (Exception ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (fieldNumber <= 360 && fieldNumber >= 0 && angleField.getText().length() >= 1) {
            rotator.setAngle(fieldNumber);
            angleSlider.setValue(fieldNumber);
        }
    }
    
    private void setRotationLimit() {
        rotationDownLimit = Integer.parseInt(fromSpinner.getValue().toString());
        rotationUpLimit = Integer.parseInt(toSpinner.getValue().toString());

        if (rotationDownLimit > rotationUpLimit) {
            int tempValue = rotationDownLimit;
            rotationDownLimit = rotationUpLimit;
            rotationUpLimit = tempValue;
        }
    }
    
    
    public boolean getRotateEnableCheckBox() {
        return RotateEnableCheckBox.isSelected();
    }

    public void setRotateEnableCheckBox(boolean value) {
        this.RotateEnableCheckBox.setSelected(value);
    }

    public int getActionsComboBox() {
        return actionsComboBox.getSelectedIndex();
    }

    public void setActionsComboBox(int index) {
        this.actionsComboBox.setSelectedIndex(index);
    }

    public String getAngleSlider() {
        return String.valueOf(angleSlider.getValue());
    }

    public void setAngleSlider(int angleSlider) {
        this.angleSlider.getModel().setValue(angleSlider);
    }

    public boolean getCustomRadioButton() {
        return customRadioButton.isSelected();
    }

    public void setCustomRadioButton(boolean value) {
        this.customRadioButton.setSelected(value);
    }

    public boolean getDifferentValueCheckBox() {
        return differentValueCheckBox.isSelected();
    }

    public void setDifferentValueCheckBox(boolean value) {
        this.differentValueCheckBox.setSelected(value);
    }

    public String getFromSpinner() {
        return fromSpinner.getValue().toString();
    }

    public void setFromSpinner(int fromSpinner) {
        this.fromSpinner.getModel().setValue(fromSpinner);
    }

    public boolean getLimitCheckBox() {
        return limitCheckBox.isSelected();
    }

    public void setLimitCheckBox(boolean value) {
        this.limitCheckBox.setSelected(value);
    }

    public boolean getPredefinedRadioButton() {
        return predefinedRadioButton.isSelected();
    }

    public void setPredefinedRadioButton(boolean value) {
        this.predefinedRadioButton.setSelected(value);
    }

    public boolean getRandomCheckBox() {
        return randomCheckBox.isSelected();
    }

    public void setRandomCheckBox(boolean value) {
        this.randomCheckBox.setSelected(value);
    }

    public String getToSpinner() {
        return toSpinner.getValue().toString();
    }

    public void setToSpinner(int toSpinner) {
        this.toSpinner.getModel().setValue(toSpinner);
    }

    @Override
    public Properties saveState(Properties properties) {
        properties.setProperty("rotateEnable", getRotateEnableCheckBox() ? "1" : "0");
        properties.setProperty("rotateAction", Integer.toString(getActionsComboBox()));
        properties.setProperty("rotateAngle", getAngleSlider());
        properties.setProperty("rotateCustom", getCustomRadioButton() ? "1" : "0");
        properties.setProperty("rotateDifferentValue", getDifferentValueCheckBox() ? "1" : "0");
        properties.setProperty("rotateMinLimit", getFromSpinner());
        properties.setProperty("rotateLimit", getLimitCheckBox() ? "1" : "0");
        properties.setProperty("rotatePredifiend", getPredefinedRadioButton() ? "1" : "0");
        properties.setProperty("rotateRandom", getRandomCheckBox() ? "1" : "0");
        properties.setProperty("rotateMaxLimit", getToSpinner());
        return properties;
    }

    @Override
    public void restoreState(Properties properties) {
        setRotateEnableCheckBox(properties.getProperty("rotateEnable").equals("1"));
        setActionsComboBox(Integer.parseInt(properties.getProperty("rotateAction")));
        setAngleSlider(Integer.parseInt(properties.getProperty("rotateAngle")));
        setCustomRadioButton(properties.getProperty("rotateCustom").equals("1"));
        setDifferentValueCheckBox(properties.getProperty("rotateDifferentValue").equals("1"));
        setFromSpinner(Integer.parseInt(properties.getProperty("rotateMinLimit")));
        setLimitCheckBox(properties.getProperty("rotateLimit").equals("1"));
        setPredefinedRadioButton(properties.getProperty("rotatePredifiend").equals("1"));
        setRandomCheckBox(properties.getProperty("rotateRandom").equals("1"));
        setToSpinner(Integer.parseInt(properties.getProperty("rotateMaxLimit")));
    }
}
