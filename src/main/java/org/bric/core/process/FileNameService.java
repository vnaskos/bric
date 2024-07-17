package org.bric.core.process;

import org.bric.core.input.model.ImportedImage;
import org.bric.core.model.DuplicateAction;

public interface FileNameService {

    void setDuplicateAction(DuplicateAction duplicateAction);

    String generateFilePath(ImportedImage currentImage);

    String preventNamingCollision(String filepath);

}
