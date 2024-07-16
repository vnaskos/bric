package org.bric.core.process;

import org.bric.core.input.model.ImportedImage;
import org.bric.core.model.DuplicateAction;
import org.bric.core.model.output.OutputType;
import org.bric.utils.Utils;

import javax.swing.*;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class FileNameServiceImpl implements FileNameService {

    private DuplicateAction duplicateAction = DuplicateAction.NOT_SET;

    private final String outputFilepath;
    private final OutputType outputType;
    private final AtomicInteger numberingIndex;
    private final FileService fileService;
    private final DateProvider dateProvider;

    public FileNameServiceImpl(String outputFilepath, OutputType outputType, int initialNumberingIndex, FileService fileService, DateProvider dateProvider) {
        this.outputFilepath = outputFilepath;
        this.outputType =  outputType;
        this.numberingIndex = new AtomicInteger(initialNumberingIndex);
        this.fileService = fileService;
        this.dateProvider = dateProvider;
    }

    @Override
    public void setDuplicateAction(DuplicateAction duplicateAction) {
        this.duplicateAction = duplicateAction;
    }

    @Override
    public String generateFilePath(ImportedImage currentImage) {
        String generatedFilepath = ensureFilepathContainsFilename(outputFilepath)
            .replace(FilePathModifier.DAY.modifier, String.valueOf(dateProvider.day()))
            .replace(FilePathModifier.MONTH.modifier, String.valueOf(dateProvider.month()))
            .replace(FilePathModifier.YEAR.modifier, String.valueOf(dateProvider.year()))
            .replace(FilePathModifier.ORIGINAL_FILENAME.modifier, currentImage.getName())
            .replace(FilePathModifier.ORIGINAL_FILEPATH.modifier, currentImage.getPath().substring(0, currentImage.getPath().lastIndexOf(Utils.FS)))
            + '.' + getOutputExtension(currentImage);

        if (generatedFilepath.contains(FilePathModifier.NUMBERING.modifier)) {
            generatedFilepath = generatedFilepath.replace(FilePathModifier.NUMBERING.modifier, Integer.toString(numberingIndex.getAndIncrement()));
        }

        if (generatedFilepath.contains(FilePathModifier.NEXT_AVAILABLE_NUMBERING.modifier)) {
            generatedFilepath = getNextAvailableFilepath(generatedFilepath);
        }

        return generatedFilepath;
    }

    @Override
    public String preventNamingCollision(String filepath) {
        File fileToBeSaved = new File(filepath);
        if (fileToBeSaved.exists()) {
            duplicatePane(filepath);
        }

        if (duplicateAction == DuplicateAction.SKIP || duplicateAction == DuplicateAction.ALWAYS_SKIP) {
            return null;
        }

        if( (duplicateAction == DuplicateAction.RENAME || duplicateAction == DuplicateAction.ALWAYS_RENAME)
                && new File(filepath).exists()) {
            return duplicateAddAction(filepath);
        }

        return filepath;
    }

    public String getNextAvailableFilepath(String filepathTemplate) {
        String candidate;

        do {
            candidate = filepathTemplate.replace(FilePathModifier.NEXT_AVAILABLE_NUMBERING.modifier, Integer.toString(numberingIndex.getAndIncrement()));
        } while (fileService.exists(candidate));

        return candidate;
    }

    private String getOutputExtension(ImportedImage currentImage) {
        if (outputType == OutputType.SAME_AS_FIRST) {
            try {
                return OutputType.valueOf(currentImage.getType().name.toUpperCase()).name.toLowerCase();
            } catch (IllegalArgumentException ex) {
                return Utils.prefs.get("defaultFileType", OutputType.DEFAULT.name).toLowerCase();
            }
        }

        return outputType.name.toLowerCase();
    }

    private String ensureFilepathContainsFilename(String filepath) {
        if (filepath.endsWith(FilePathModifier.ORIGINAL_FILEPATH.modifier)) {
            return filepath + Utils.FS + FilePathModifier.ORIGINAL_FILENAME.modifier;
        }

        if (filepath.endsWith(Utils.FS)) {
            return filepath + FilePathModifier.ORIGINAL_FILENAME.modifier;
        }

        return filepath;
    }

    private synchronized void duplicatePane(String file) {
        if (duplicateAction == DuplicateAction.NOT_SET ||
                duplicateAction == DuplicateAction.OVERWRITE ||
                duplicateAction == DuplicateAction.SKIP ||
                duplicateAction == DuplicateAction.RENAME) {

            Object[] selectionValues = {
                    DuplicateAction.OVERWRITE, DuplicateAction.ALWAYS_OVERWRITE,
                    DuplicateAction.SKIP, DuplicateAction.ALWAYS_SKIP,
                    DuplicateAction.RENAME, DuplicateAction.ALWAYS_RENAME};

            Object selection;

            do {
                selection = JOptionPane.showInputDialog(
                        null, String.format("This image%n%s%n already exists on the output folder", file),
                        "Warning Duplicate", JOptionPane.QUESTION_MESSAGE,
                        null, selectionValues, DuplicateAction.RENAME);
            } while(selection == null);

            duplicateAction = (DuplicateAction) selection;
        }
    }

    private String duplicateAddAction(String filename) {
        String file = filename.substring(0, filename.lastIndexOf('.'));
        file += String.format("(%s)", FilePathModifier.NEXT_AVAILABLE_NUMBERING.modifier);
        String extension = getExtension(filename);
        return getNextAvailableFilepath(file + "." + extension);
    }

    private String getExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
