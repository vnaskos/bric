package org.bric.core.process;

import org.bric.core.input.model.ImportedImage;
import org.bric.core.model.output.OutputParameters;
import org.bric.core.model.output.OutputType;
import org.bric.core.test.ImportedImageTestFactory;
import org.bric.gui.dialog.DialogNamingCollisionResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

class ImageProcessHandlerIT {

    private static final String DUCK_JPG = "duck.jpg";
    private static final String DOG_JPEG = "dog.jpeg";
    private static final String TWO_PAGE_PDF = "two-pages.pdf";

    @TempDir
    public Path outputPath;

    @Test
    void exportsImageToImage() {
        List<ImportedImage> input = Collections.singletonList(file(DUCK_JPG));
        OutputParameters output = new OutputParameters(outputPath.resolve("out*").toString(), OutputType.JPG, 1, 1);

        ImageProcessHandler handler = new ImageProcessHandler(getFileNameService(output), output, input);

        Assertions.assertTimeout(Duration.ofSeconds(3), () -> {
            CompletableFuture.allOf(handler.start().toArray(new CompletableFuture[0])).get();
            Assertions.assertTrue(outputPath.resolve("out1.jpg").toFile().exists());
        });
    }

    @Test
    void exportsImageToPdf() {
        List<ImportedImage> input = Collections.singletonList(file(DUCK_JPG));
        OutputParameters output = new OutputParameters(outputPath.resolve("out*").toString(), OutputType.PDF, 1, 1);

        ImageProcessHandler handler = new ImageProcessHandler(getFileNameService(output), output, input);

        Assertions.assertTimeout(Duration.ofSeconds(3), () -> {
            CompletableFuture.allOf(handler.start().toArray(new CompletableFuture[0])).get();
            Assertions.assertTrue(outputPath.resolve("out1.pdf").toFile().exists());
        });
    }

    @Test
    void exportsEachPdfPageToImage() {
        List<ImportedImage> input = Collections.singletonList(file(TWO_PAGE_PDF));
        OutputParameters output = new OutputParameters(outputPath.resolve("page*").toString(), OutputType.JPG, 1, 1);

        ImageProcessHandler handler = new ImageProcessHandler(getFileNameService(output), output, input);

        Assertions.assertTimeout(Duration.ofSeconds(3), () -> {
            CompletableFuture.allOf(handler.start().toArray(new CompletableFuture[0])).get();
            Assertions.assertTrue(outputPath.resolve("page1.jpg").toFile().exists());
            Assertions.assertTrue(outputPath.resolve("page2.jpg").toFile().exists());
        });
    }

    @Test
    void exportsEachPdfToItsOwnPdf() {
        List<ImportedImage> input = Arrays.asList(file(TWO_PAGE_PDF), file(TWO_PAGE_PDF));
        OutputParameters output = new OutputParameters(outputPath.resolve("out*").toString(), OutputType.SAME_AS_FIRST, 1, 1);

        ImageProcessHandler handler = new ImageProcessHandler(getFileNameService(output), output, input);

        Assertions.assertTimeout(Duration.ofSeconds(3), () -> {
            CompletableFuture.allOf(handler.start().toArray(new CompletableFuture[0])).get();
            Assertions.assertTrue(outputPath.resolve("out1.pdf").toFile().exists());
            Assertions.assertTrue(outputPath.resolve("out2.pdf").toFile().exists());
        });
    }

    @Test
    void mergesImagesToASinglePdf() {
        List<ImportedImage> input = Arrays.asList(file(DUCK_JPG), file(DOG_JPEG));
        OutputParameters output = new OutputParameters(outputPath.resolve("out*").toString(), OutputType.PDF, 1, 1);

        ImageProcessHandler handler = new ImageProcessHandler(getFileNameService(output), output, input);


        Assertions.assertTimeout(Duration.ofSeconds(3), () -> {
            CompletableFuture.allOf(handler.start().toArray(new CompletableFuture[0])).get();
            Assertions.assertTrue(outputPath.resolve("out1.pdf").toFile().exists());
        });
    }

    @Test
    void mergesPdfPagesToASinglePdf() {
        List<ImportedImage> input = Collections.singletonList(file(TWO_PAGE_PDF));
        OutputParameters output = new OutputParameters(outputPath.resolve("out*").toString(), OutputType.PDF, 1, 1);

        ImageProcessHandler handler = new ImageProcessHandler(getFileNameService(output), output, input);

        Assertions.assertTimeout(Duration.ofSeconds(3), () -> {
            CompletableFuture.allOf(handler.start().toArray(new CompletableFuture[0])).get();
            Assertions.assertTrue(outputPath.resolve("out1.pdf").toFile().exists());
            Assertions.assertFalse(outputPath.resolve("out2.pdf").toFile().exists());
        });
    }

    @Test
    void mergesPdfAndImagesToASinglePdf() {
        List<ImportedImage> input = Arrays.asList(file(DUCK_JPG), file(TWO_PAGE_PDF));
        OutputParameters output = new OutputParameters(outputPath.resolve("out*").toString(), OutputType.PDF, 1, 1);

        ImageProcessHandler handler = new ImageProcessHandler(getFileNameService(output), output, input);

        Assertions.assertTimeout(Duration.ofSeconds(3), () -> {
            CompletableFuture.allOf(handler.start().toArray(new CompletableFuture[0])).get();
            Assertions.assertTrue(outputPath.resolve("out1.pdf").toFile().exists());
            Assertions.assertFalse(outputPath.resolve("out2.pdf").toFile().exists());
        });
    }

    private ImportedImage file(String name) {
        URL url = this.getClass().getResource(File.separator + name);
        assert url != null;

        File testImageFile = new File(url.getFile());
        return ImportedImageTestFactory.anImage(testImageFile.getAbsolutePath());
    }

    private FileNameServiceImpl getFileNameService(OutputParameters output) {
        return new FileNameServiceImpl(output.getOutputPath(), output.getOutputType(),
                output.getNumberingStartIndex(), new DefaultFileService(), new CalendarDateProvider(), new DialogNamingCollisionResolver());
    }

}
