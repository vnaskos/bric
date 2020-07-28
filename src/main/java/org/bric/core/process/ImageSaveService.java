package org.bric.core.process;

import ij.ImagePlus;
import ij.io.FileSaver;
import org.bric.core.model.output.OutputType;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageSaveService implements SaveService<BufferedImage, ImageSaveService.Parameters> {

    @Override
    public void save(Supplier<BufferedImage> supplier, ImageSaveService.Parameters parameters) {
        if (parameters.outputType == OutputType.JPG || parameters.outputType == OutputType.JPEG) {
            saveJPG(supplier.get(), parameters.filepath, parameters.quality);
        } else {
            save(supplier.get(), parameters.filepath, parameters.outputType);
        }
    }

    private void save(BufferedImage imageForSave, String newFilepath, OutputType outputType) {
        try {
            ImagePlus img = new ImagePlus("bric-image", imageForSave);
            FileSaver fileSaver = new FileSaver(img);

            switch (outputType) {
                case GIF:
                    fileSaver.saveAsGif(newFilepath);
                    break;
                case TIF:
                case TIFF:
                    fileSaver.saveAsTiff(newFilepath);
                    break;
                case PGM:
                    fileSaver.saveAsPgm(newFilepath);
                    break;
                case BMP:
                    fileSaver.saveAsBmp(newFilepath);
                    break;
                case PNG:
                    fileSaver.saveAsPng(newFilepath);
                    break;
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageProcessHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Save an image in JPG JPEG format
     *
     * @param image image to save
     * @param filepath path where image file will be saved
     * @param quality float between 0 and 1
     */
    private void saveJPG(BufferedImage image, String filepath, float quality){
        try {
            Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
            ImageWriter writer = iter.next();
            ImageWriteParam iwp = writer.getDefaultWriteParam();
            iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            iwp.setCompressionQuality(quality);
            FileImageOutputStream output = new FileImageOutputStream(new File(filepath));
            writer.setOutput(output);

            IIOImage image2 = new IIOImage(image, null, null);
            writer.write(null, image2, iwp);
            output.flush();
            writer.dispose();
            output.close();
        } catch (IOException ex) {
            Logger.getLogger(ImageProcessHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static class Parameters implements SaveService.Parameters {
        public final String filepath;
        public final Float quality;
        public final OutputType outputType;

        public Parameters(String filepath, Float quality, OutputType outputType) {
            this.filepath = filepath;
            this.quality = quality;
            this.outputType = outputType;
        }
    }
}
