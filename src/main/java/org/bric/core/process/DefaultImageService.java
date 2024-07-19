package org.bric.core.process;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import org.bric.core.model.output.OutputType;
import org.bric.utils.Utils;

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

public class DefaultImageService implements ImageService {

    @Override
    public BufferedImage load(final String fullPath) {
        BufferedImage image = librarySwitch(fullPath, 1);

        if (image == null) {
            return librarySwitch(fullPath, 2);
        }

        return image;
    }

    @Override
    public void save(Supplier<BufferedImage> supplier, ImageSaveParameters parameters) {
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

    private BufferedImage librarySwitch(String filename, int library) {
        try {
            if (library == 1) {
                return ImageIO.read(new File(filename));
            } else if (library == 2) {
                return IJ.openImage(filename).getBufferedImage();
            }
        } catch (Exception ex) {
            return null;
        } catch (OutOfMemoryError ex) {
            if (library == 2) {
                Utils.outOfMemoryErrorMessage();
            }
            return null;
        }
        return null;
    }
}
