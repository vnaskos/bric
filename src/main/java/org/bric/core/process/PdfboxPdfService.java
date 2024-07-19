package org.bric.core.process;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PdfboxPdfService implements PdfService {

    @Override
    public List<BufferedImage> readAsImages(final String fullPath, final int startPage, final int endPage) {
        List<BufferedImage> images = new ArrayList<>();

        try (PDDocument document = PDDocument.load(new File(fullPath))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            for (int page = startPage; page < Math.min(document.getNumberOfPages(), endPage); page++) {
                images.add(pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return images;
    }

    @Override
    public void mergeToPdf(final Stream<BufferedImage> input, final String outputFullPath) {
        try(PDDocument document = new PDDocument()) {
            input.forEach(i -> addImageToPDF(document, i));
            document.save(outputFullPath);
        }  catch (IOException e) {
            e.printStackTrace();
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
        } catch (IOException e){
            System.err.println("Exception while trying to create pdf document - " + e);
        }
    }
}
