package org.bric.processor;

import org.bric.core.input.model.ImportedImage;
import org.bric.core.model.output.OutputParameters;
import org.bric.core.model.output.OutputType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ImageProcessHandlerIT {

    private static final String DUCK_JPG = "duck.jpg";
    private static final String DOG_JPEG = "dog.jpeg";
    private static final String TWO_PAGE_PDF = "two-pages.pdf";

    @TempDir
    public Path outputPath;

    @Test
    public void exportsImageToImage() throws InterruptedException {
        List<ImportedImage> input = Collections.singletonList(new ImportedImage(file(DUCK_JPG).getAbsolutePath()));
        OutputParameters output = new OutputParameters(outputPath.resolve("out*").toString(), OutputType.JPG, 1, 1);

        new ImageProcessHandler(getFileNameService(output, input), output, input).start();

        Thread.sleep(1000);
        Assertions.assertTrue(outputPath.resolve("out1.jpg").toFile().exists());
    }

    @Test
    public void exportsImageToPdf() throws InterruptedException {
        List<ImportedImage> input = Collections.singletonList(new ImportedImage(file(DUCK_JPG).getAbsolutePath()));
        OutputParameters output = new OutputParameters(outputPath.resolve("out*").toString(), OutputType.PDF, 1, 1);

        new ImageProcessHandler(getFileNameService(output, input), output, input).start();

        Thread.sleep(1000);
        Assertions.assertTrue(outputPath.resolve("out1.pdf").toFile().exists());
    }

    @Test
    public void exportsEachPdfPageToImage() throws InterruptedException {
        List<ImportedImage> input = Collections.singletonList(new ImportedImage(file(TWO_PAGE_PDF).getAbsolutePath()));
        OutputParameters output = new OutputParameters(outputPath.resolve("page*").toString(), OutputType.JPG, 1, 1);

        new ImageProcessHandler(getFileNameService(output, input), output, input).start();

        Thread.sleep(2000);
        Assertions.assertTrue(outputPath.resolve("page1.jpg").toFile().exists());
        Assertions.assertTrue(outputPath.resolve("page2.jpg").toFile().exists());
    }

    @Test
    public void exportsEachPdfToItsOwnPdf() throws InterruptedException {
        List<ImportedImage> input = Arrays.asList(
                new ImportedImage(file(TWO_PAGE_PDF).getAbsolutePath()),
                new ImportedImage(file(TWO_PAGE_PDF).getAbsolutePath()));
        OutputParameters output = new OutputParameters(outputPath.resolve("out*").toString(), OutputType.SAME_AS_FIRST, 1, 1);

        new ImageProcessHandler(getFileNameService(output, input), output, input).start();

        Thread.sleep(2000);
        Assertions.assertTrue(outputPath.resolve("out1.pdf").toFile().exists());
        Assertions.assertTrue(outputPath.resolve("out2.pdf").toFile().exists());
    }

    @Test
    public void mergesImagesToASinglePdf() throws InterruptedException {
        List<ImportedImage> input = Arrays.asList(
                new ImportedImage(file(DUCK_JPG).getAbsolutePath()),
                new ImportedImage(file(DOG_JPEG).getAbsolutePath()));
        OutputParameters output = new OutputParameters(outputPath.resolve("out*").toString(), OutputType.PDF, 1, 1);

        new ImageProcessHandler(getFileNameService(output, input), output, input).start();

        Thread.sleep(1000);
        Assertions.assertTrue(outputPath.resolve("out1.pdf").toFile().exists());
    }

    @Test
    public void mergesPdfPagesToASinglePdf() throws InterruptedException {
        List<ImportedImage> input = Collections.singletonList(new ImportedImage(file(TWO_PAGE_PDF).getAbsolutePath()));
        OutputParameters output = new OutputParameters(outputPath.resolve("out*").toString(), OutputType.PDF, 1, 1);

        new ImageProcessHandler(getFileNameService(output, input), output, input).start();

        Thread.sleep(1000);
        Assertions.assertTrue(outputPath.resolve("out1.pdf").toFile().exists());
        Assertions.assertFalse(outputPath.resolve("out2.pdf").toFile().exists());
    }

    @Test
    public void mergesPdfAndImagesToASinglePdf() throws InterruptedException {
        List<ImportedImage> input = Arrays.asList(
                new ImportedImage(file(DUCK_JPG).getAbsolutePath()),
                new ImportedImage(file(TWO_PAGE_PDF).getAbsolutePath()));
        OutputParameters output = new OutputParameters(outputPath.resolve("out*").toString(), OutputType.PDF, 1, 1);

        new ImageProcessHandler(getFileNameService(output, input), output, input).start();

        Thread.sleep(1000);
        Assertions.assertTrue(outputPath.resolve("out1.pdf").toFile().exists());
        Assertions.assertFalse(outputPath.resolve("out2.pdf").toFile().exists());
    }

    private File file(String name) {
        URL url = this.getClass().getResource(File.separator + name);
        return new File(url.getFile());
    }

    private FileNameService getFileNameService(OutputParameters output, List<ImportedImage> input) {
        return new FileNameService(output.getOutputPath(), output.getOutputType(),
                output.getNumberingStartIndex(), input.size());
    }

}
