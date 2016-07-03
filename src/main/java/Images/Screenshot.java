/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Images;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 *
 * @author Guillem
 */
public class Screenshot {
    
    public WritableImage takeScreenshot()
    {
        //Obtenir la grandària de la imatge
        java.awt.Rectangle screenRect = new java.awt.Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        
        try
        {
            
            //Obtenir la captura 
            BufferedImage bf = new Robot().createScreenCapture(screenRect);

                        
            //Escriure la imatge amb format per al image viewer.
            WritableImage wr = null;
            if (bf != null)
            {
                wr = new WritableImage(bf.getWidth(), bf.getHeight());
                PixelWriter pw = wr.getPixelWriter();
                
                for (int x = 0; x < bf.getWidth(); x++) 
                {
                    for (int y = 0; y < bf.getHeight(); y++) 
                    {
                        pw.setArgb(x, y, bf.getRGB(x, y));
                    }
                }
            }
            
            return wr;
            
        }   catch (AWTException ex) {
                Logger.getLogger(Screenshot.class.getName()).log(Level.SEVERE, null, ex);
                return null;
        }
    }
    
}
