/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bric.utils;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

/**
 *
 * @author vasilis
 */
public class PDFToImage {
    
    public static List<BufferedImage> getBImagesFromPDF(String pdfFile, int startPage, int endPage){
        PDDocument document = null;
        try {
            document = PDDocument.load(pdfFile);
            
            int resolution;
            try {
                resolution = Toolkit.getDefaultToolkit().getScreenResolution();
            } catch (HeadlessException e) {
                resolution = 96;
            }
            
            ArrayList<BufferedImage> bufferedImagesList = new ArrayList<BufferedImage>();
            List pages = document.getDocumentCatalog().getAllPages();
            for (int i = startPage - 1; i < endPage && i < pages.size(); i++) {
                PDPage page = (PDPage) pages.get(i);
                bufferedImagesList.add(page.convertToImage(BufferedImage.TYPE_INT_ARGB, resolution));
            }
            return bufferedImagesList;
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            if(document != null){
                try {
                    document.close();
                } catch (IOException ex) {
                    Logger.getLogger(PDFToImage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return Collections.EMPTY_LIST;
    }
}
