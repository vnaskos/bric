package org.bric.processor;

import org.bric.input.ImportedImage;
import org.bric.utils.Utils;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class FileNameService {

    public static final String DEFAULT_OUTPUT_TYPE = "jpg";

    private final String outputFilepath;
    private final String outputType;
    private final AtomicInteger numberingIndex;

    private Queue<Integer> availableNumberingIndices;

    public FileNameService(String outputFilepath, String outputType, int initialNumberingIndex, int totalItems) {
        this.outputFilepath = outputFilepath;
        this.outputType = outputType;
        this.numberingIndex = new AtomicInteger(initialNumberingIndex);

        initNumsStack(initialNumberingIndex, totalItems);
    }

    private void initNumsStack(int initialNumberingIndex, int totalItems) {
        availableNumberingIndices = new ConcurrentLinkedQueue<>();
        if (outputFilepath.contains("#")) {
            HashSet<Integer> existingNumsHash = Utils.getExistingNumsHash(outputFilepath);
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

    public String applyFileNameMasks(ImportedImage currentImage) {
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

        String extension = outputType;
        if (outputType.equals("same as first")) {
            for(String ext : Utils.supportedOutputExtensions){
                if(ext.equalsIgnoreCase(currentImage.getImageType())){
                    extension = currentImage.getImageType();
                    break;
                } else {
                    extension = Utils.prefs.get("defaultFileType", DEFAULT_OUTPUT_TYPE);
                }
            }
        }

        generatedFilepath += '.' + extension;

        if (generatedFilepath.contains("#")) {
            generatedFilepath = generatedFilepath.replaceAll("#", String.valueOf(availableNumberingIndices.poll()));
        }

        return generatedFilepath;
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
}
