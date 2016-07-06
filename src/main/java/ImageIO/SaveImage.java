/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ImageIO;

import UIControls.sscTab;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javax.imageio.ImageIO;

/**
 *
 * @author Guillem
 */
public class SaveImage {
    
    private static final int[] RGB_MASKS = {0xFF0000, 0xFF00, 0xFF};
    private static final ColorModel RGB_OPAQUE = new DirectColorModel(32, RGB_MASKS[0], RGB_MASKS[1], RGB_MASKS[2]);
    
    public static boolean save(sscTab tab, File file)
    {
        //int indexImageView = -1;
        
        ScrollPane tabContent = (ScrollPane) tab.getContent();

        Pane paneContent = (Pane) tabContent.getContent();

        WritableImage wi = new WritableImage(((Double) paneContent.getWidth()).intValue(), ((Double) paneContent.getHeight()).intValue());

        //Get a snapshot of the Pane and save it to a file
        tab.getPane().snapshot(null, wi);

        String extension = file.getName();
        String format;

        if(extension.length() > 0 && extension.lastIndexOf(".") > 0)
        {
            String[] ext = extension.split("\\.");
            int index = ext.length - 1;
            format = ext[index];

            if(format.equals("png") || format.equals("gif"))
            {
                return savePngGifFormats(format, file, wi);
            }
            else if(format.equals("jpg"))
            {
                return saveJpgFormat(file, wi);
            }                
        }
        else
        {
            return false;
        }
        
        return false;
    }
    
    private static boolean savePngGifFormats(String format, File file, WritableImage wi)
    {
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(wi, null), format, file);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(SaveImage.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error when write image");
            return false;
        }
    }
    
    private static boolean saveJpgFormat(File file, WritableImage wi)
    {
        java.awt.Image img = SwingFXUtils.fromFXImage(wi, null);
        PixelGrabber pg = new PixelGrabber(img, 0, 0, -1, -1, true);
        
        try {
            pg.grabPixels();
            
            int width = pg.getWidth(), height = pg.getHeight();

            DataBuffer buffer = new DataBufferInt((int[]) pg.getPixels(), pg.getWidth() * pg.getHeight());
            WritableRaster raster = Raster.createPackedRaster(buffer, width, height, width, RGB_MASKS, null);
            BufferedImage bi = new BufferedImage(RGB_OPAQUE, raster, false, null);

            ImageIO.write(bi, "jpg", file);
            
            return true;
            
        } catch (InterruptedException ex) {
            return false;
        } catch (IOException ex) {
            return false;
        }
    }
}
