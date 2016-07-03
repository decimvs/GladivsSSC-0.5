/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ImageIO;

import UIControls.sscTab;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javax.imageio.ImageIO;

/**
 *
 * @author Guillem
 */
public class SaveImage {
    
    public static boolean save(sscTab tab, String filename)
    {
        int indexImageView = -1;
        
        ScrollPane tabContent = (ScrollPane) tab.getContent();

        Pane paneContent = (Pane) tabContent.getContent();

        for(int i = 0; paneContent.getChildren().size() > i; i++)
        {
            if(paneContent.getChildren().get(i) instanceof ImageView)
            {
                System.out.println("Trobat el ImageView en l'index " + i);
                indexImageView = i;
            }
        }
        
        if(indexImageView > -1 && paneContent.getChildren().get(indexImageView) != null)
        {
            ImageView iv1 = (ImageView) paneContent.getChildren().get(indexImageView);
                
            Image img1 = iv1.getImage();

            int niWidth = ((Double) img1.getWidth()).intValue();
            int niHeight = ((Double) img1.getHeight()).intValue();

            WritableImage wi = new WritableImage(niWidth, niHeight);
            
            //Get a snapshot of the Pane and save it to a file
            tab.getPane().snapshot(null, wi);

            File filePng = new File(filename + ".png");

            try {
                ImageIO.write(SwingFXUtils.fromFXImage(wi, null), "png", filePng);
            } catch (IOException ex) {
                Logger.getLogger(SaveImage.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error when write image");
                return false;
            }
        }
        
        return true;
    }
}
