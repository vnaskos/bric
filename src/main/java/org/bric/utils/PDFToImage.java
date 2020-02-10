package org.bric.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFToImage {
    
    public static List<BufferedImage> getBImagesFromPDF(String pdfFile, int startPage, int endPage){
        List<BufferedImage> images = new ArrayList<>();

        try (PDDocument document = PDDocument.load(new File(pdfFile))) {

            PDFRenderer pdfRenderer = new PDFRenderer(document);
            for (int page = startPage; page < Math.min(document.getNumberOfPages(), endPage); page++) {
                images.add(pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return images;
    }
}
