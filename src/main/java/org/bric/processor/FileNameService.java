package org.bric.processor;

import org.bric.core.input.model.ImportedImage;
import org.bric.core.model.output.OutputType;
import org.bric.utils.Utils;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileNameService {

    private final String outputFilepath;
    private final OutputType outputType;
    private final AtomicInteger numberingIndex;

    private Queue<Integer> availableNumberingIndices;

    public FileNameService(String outputFilepath, OutputType outputType, int initialNumberingIndex, int totalItems) {
        this.outputFilepath = outputFilepath;
        this.outputType =  outputType;
        this.numberingIndex = new AtomicInteger(initialNumberingIndex);

        initNumsStack(numberingIndex.get(), totalItems);
    }

    private void initNumsStack(int initialNumberingIndex, int totalItems) {
        availableNumberingIndices = new ConcurrentLinkedQueue<>();
        if (outputFilepath.contains("#")) {
            HashSet<Integer> existingNumsHash = getExistingNumsHash(outputFilepath);
            for (int i = initialNumberingIndex; true; i++) {
                if (!existingNumsHash.contains(i)) {
                    availableNumberingIndices.add(i);
                }
                if (availableNumberingIndices.size() == totalItems) {
                    break;
                }
            }
        }
    }

    public String generateFilePath(ImportedImage currentImage) {
        String generatedFilepath = outputFilepath;

        generatedFilepath = ensureFilepathContainsSlashIfEndsWithPathModifier(generatedFilepath);
        generatedFilepath = ensureFilepathContainsFilename(generatedFilepath);

        generatedFilepath = generatedFilepath.replace("*", Integer.toString(numberingIndex.getAndIncrement()));

        Calendar cal = Calendar.getInstance();
        generatedFilepath = generatedFilepath.replace("%D", cal.get(Calendar.DATE) + "");
        generatedFilepath = generatedFilepath.replace("%M", (cal.get(Calendar.MONTH) + 1) + "");
        generatedFilepath = generatedFilepath.replace("%Y", cal.get(Calendar.YEAR) + "");

        generatedFilepath = generatedFilepath.replace("%F", currentImage.getName());
        generatedFilepath = generatedFilepath.replace("^P", currentImage.getPath().substring(0, currentImage.getPath().lastIndexOf(Utils.FS)));

        generatedFilepath += '.' + getOutputExtension(currentImage);

        if (generatedFilepath.contains("#")) {
            generatedFilepath = generatedFilepath.replaceAll("#", String.valueOf(availableNumberingIndices.poll()));
        }

        return generatedFilepath;
    }

    private String getOutputExtension(ImportedImage currentImage) {
        if (outputType == OutputType.SAME_AS_FIRST) {
            try {
                return OutputType.valueOf(currentImage.getType().name.toUpperCase()).name.toLowerCase();
            } catch (IllegalArgumentException ex) {
                return Utils.prefs.get("defaultFileType", OutputType.DEFAULT.name.toLowerCase());
            }
        }

        return outputType.name.toLowerCase();
    }

    private String ensureFilepathContainsSlashIfEndsWithPathModifier(String filepath) {
        if (filepath.endsWith("^P")) {
            return filepath + Utils.FS;
        }
        return filepath;
    }

    private String ensureFilepathContainsFilename(String filepath) {
        if (filepath.endsWith(Utils.FS)) {
            return filepath + "%F";
        }
        return filepath;
    }

    @Deprecated
    private static HashSet<Integer> getExistingNumsHash(String outputPath) {
        String[] outputPathArray = outputPath.split(Pattern.quote(File.separator));
        int outputPathSize = outputPathArray.length;
        String outputName = outputPathArray[outputPathSize-1];
        HashSet<Integer> numsHashset = new HashSet<Integer>();
        File outputDir = new File(outputPath.substring(0, outputPath.lastIndexOf(System.getProperty("file.separator"))));
        List<String> list = Arrays.asList(outputDir.list());
        for (String file : list) {
            if (file.matches(createRegex(outputName))) {
                Pattern pattern = Pattern.compile("[0-9]+");
                Matcher m = pattern.matcher(file);
                while (m.find()) {
                    numsHashset.add(Integer.parseInt(m.group()));
                }
            }
        }
        return numsHashset;
    }

    @Deprecated
    public static String createRegex(String s) {
        StringBuilder b = new StringBuilder();
        for(int i=0; i<s.length(); ++i) {
            char ch = s.charAt(i);
            if ("\\.^$|?*+[]{}()-_".indexOf(ch) != -1){
                b.append('\\').append(ch);
            }
            else {
                b.append(ch);
            }
        }
        b.append("\\.(jpg|jpeg|png|gif|tif|tiff|bmp|pdf|wbmp|pbm|pgm|ppm|pnm|psd|JPG|JPEG|PNG|GIF|TIF|TIFF|BMP|PDF|WBMP|PBM|PGM|PPM|PNM|PSD)");
        return b.toString().replaceAll("#", "[0-9]+");
    }
}
