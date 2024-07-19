package org.bric.utils;

import org.bric.core.input.model.InputType;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.prefs.Preferences;

public class Utils {

    private static final ExecutorService EXECUTOR_SERVICE =
            Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());

    public static final Locale GREEK = new Locale("el", "GR");

    public static final String FS = System.getProperty("file.separator");

    public static Preferences prefs = Preferences.userRoot();

    public static void setFileChooserProperties(JFileChooser chooser) {
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        FileNameExtensionFilter[] filters = {
                extensionFilter("JPEG Image(*.jpg, *.jpeg)", InputType.JPG, InputType.JPEG),
                extensionFilter("PNG Images(*.png)", InputType.PNG),
                extensionFilter("BMP Images(*.bmp)", InputType.BMP),
                extensionFilter("TIFF Images(*.tiff, *.tif)", InputType.TIFF, InputType.TIF),
                extensionFilter("GIF Images(*.gif)", InputType.GIF),
                extensionFilter("Photoshop Files(*.psd)", InputType.PSD),
                extensionFilter("PNM Images(*.pnm)", InputType.PNM),
                extensionFilter("PPM Images(*.ppm)", InputType.PPM),
                extensionFilter("PGM Images(*.pgm)", InputType.PGM),
                extensionFilter("PBM Images(*.pbm)", InputType.PBM),
                extensionFilter("WBMP Images(*.wbmp)", InputType.WBMP),
                extensionFilter("PDF Files(*.pdf)", InputType.PDF),
                extensionFilter("All supported file types", InputType.values())
        };

        for (FileNameExtensionFilter filter : filters) {
            chooser.setFileFilter(filter);
        }
    }

    private static FileNameExtensionFilter extensionFilter(String description, InputType... types) {
        return new FileNameExtensionFilter(description,
                Arrays.stream(types)
                      .map(t -> t.type.toUpperCase())
                      .toArray(String[]::new));
    }

    public static void outOfMemoryErrorMessage(){
        JOptionPane.showMessageDialog(null,
                "Out of memory!\nRerun the program with -Xmx argument, using more than " + Runtime.getRuntime().maxMemory()/1000000 + "m",
                "Out of memory error.",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void centerWindow(Window frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }

    public static ExecutorService getExecutorService() {
        return EXECUTOR_SERVICE;
    }
}
