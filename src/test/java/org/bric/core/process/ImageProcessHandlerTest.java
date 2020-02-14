package org.bric.core.process;

import org.bric.core.input.model.ImportedImage;
import org.bric.core.model.output.OutputParameters;
import org.bric.core.model.output.OutputType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

public class ImageProcessHandlerTest {

    private static final int ANY_NUMBERING = 1;
    private static final int ANY_QUALITY = 1;

    @Test
    public void startProcess_ShouldFilterOutNullInputs_WhenOutputImageInputContainsOnlyNull() {
        List<ImportedImage> input = Arrays.asList(null, null);
        OutputParameters output = new OutputParameters(null, OutputType.JPG, ANY_NUMBERING, ANY_QUALITY);
        FileNameService fakeFileNameService = Mockito.mock(FileNameService.class);

        new ImageProcessHandler(fakeFileNameService, output, input).start();

        Mockito.verify(fakeFileNameService, Mockito.never()).generateFilePath(Mockito.any());
    }

    @Test
    public void startProcess_ShouldFilterOutNullInputs_WhenOutputPdfAndInputContainsOnlyNull() {
        List<ImportedImage> input = Arrays.asList(null, null);
        OutputParameters output = new OutputParameters(null, OutputType.PDF, ANY_NUMBERING, ANY_QUALITY);
        FileNameService fakeFileNameService = Mockito.mock(FileNameService.class);

        new ImageProcessHandler(fakeFileNameService, output, input).start();

        Mockito.verify(fakeFileNameService, Mockito.never()).generateFilePath(Mockito.any());
    }
}
