package org.bric.core.process;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PdfboxPdfService implements PdfService {

    private static final Logger logger = LoggerFactory.getLogger(PdfboxPdfService.class);

    @Override
    public List<BufferedImage> readAsImages(final String fullPath, final int startPage, final int endPage) {
        List<BufferedImage> images = new ArrayList<>();

        try (PDDocument document = Loader.loadPDF(new RandomAccessReadBufferedFile(fullPath))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            int finalPage = Math.min(document.getNumberOfPages(), endPage);
            for (int page = startPage; page < finalPage; page++) {
                images.add(pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB));
            }
        } catch (IOException e) {
            logger.error("Failed to read PDF [{}] pages [{}-{}] as images", fullPath, startPage, endPage, e);
        }

        return images;
    }

    @Override
    public void mergeToPdf(final Stream<BufferedImage> input, final String outputFullPath) {
        try(PDDocument document = new PDDocument()) {
            input.forEach(i -> addImageToPDF(document, i));
            document.save(outputFullPath);
        }  catch (IOException e) {
            logger.error("Failed to save PDF [{}]", outputFullPath, e);
        }
    }

    private void addImageToPDF(PDDocument doc, BufferedImage image) {
        try {
            PDPage page = new PDPage(new PDRectangle(image.getWidth(), image.getHeight()));
            doc.addPage(page);

            PDImageXObject pdImage = LosslessFactory.createFromImage(doc, image);

            PDPageContentStream contents = new PDPageContentStream(doc, page);

            contents.drawImage(pdImage, 0, 0);

            contents.close();
        } catch (Exception e){
            logger.error("Failed to convert image to PDF page", e);
        }
    }
}
