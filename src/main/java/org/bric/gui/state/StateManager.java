package org.bric.gui.state;

import org.bric.gui.BricUI;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StateManager {

    private final List<StatefulComponent> statefulComponents;

    public StateManager(StatefulComponent... statefulComponents) {
        this.statefulComponents = Arrays.asList(statefulComponents);
    }

    public void saveState() {
        FileOutputStream out = null;
        try {
            JFileChooser propertiesChooser = propertiesFileChooser();
            propertiesChooser.setDialogTitle("Save properties");

            if (propertiesChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            String file = propertiesChooser.getSelectedFile().getPath();
            if(!file.matches(".+\\.properties")){
                file = propertiesChooser.getSelectedFile().getPath()+".properties";
            }
            out = new FileOutputStream(new File(file));

            final Properties properties = new Properties();
            statefulComponents.forEach(c -> c.saveState(properties));

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

    public void restoreState() {
        FileInputStream fileInput = null;
        try {
            JFileChooser propertiesChooser = propertiesFileChooser();
            propertiesChooser.setDialogTitle("Load properties");

            if (propertiesChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File file = propertiesChooser.getSelectedFile();
            fileInput = new FileInputStream(file);
            final Properties properties = new Properties();
            properties.load(fileInput);

            statefulComponents.forEach(c -> c.restoreState(properties));
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

    private static JFileChooser propertiesFileChooser() {
        JFileChooser propertiesChooser = new JFileChooser();
        propertiesChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        propertiesChooser.setFileFilter(new FileNameExtensionFilter("properties file(*.properties)", "PROPERTIES"));
        return propertiesChooser;
    }
}
