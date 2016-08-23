package com.gladivs.gladivsssc3;

import Controllers.mainController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

/**
 *
 * @author Guillem
 */
public class MainApp extends Application implements NativeKeyListener {
    
    private mainController mController;
    
    @Override
    public void start(Stage primaryStage) {
        
        try {
                GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }
        
        final FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/mainFormB.fxml")
        );
        
        Parent root;
        
        try {
            root = (Parent) loader.load();
        } catch (IOException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
            
            System.out.println(ex.getMessage());
            System.out.println(ex.getStackTrace().toString());
            
            return;
        }
        
        mController = loader.getController();
        
        Scene scene = new Scene(root);
        
        primaryStage.setTitle("Gladivs - Image Editor");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/icona_sense_sombra.png")));
        primaryStage.setScene(scene);
        primaryStage.show();
        
        GlobalScreen.addNativeKeyListener(this);
        
        // Get the logger for "org.jnativehook" and set the level to warning.
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.WARNING);

        // Don't forget to disable the parent handlers.
        logger.setUseParentHandlers(false);
    }
    
    /**
     *Esta funció definida en Application es invocada quant l'aplicació rep l'ordre de tancar o finalitzar
     * Finalitza tots els fils de la aplicació.
     */
    @Override
    public void stop()
    {
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.exit(1);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException ex) {
                Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {    
        //Esta sentencia permet de executar el codi dins del fil de la aplicació javaFX
        Platform.runLater( () -> {
            if(e.getKeyCode() == NativeKeyEvent.VC_PRINTSCREEN)
            {
                mController.takeNewScreenshot();
                System.out.println("Screenshot Taked");
            }
        });
    }

    public void nativeKeyTyped(NativeKeyEvent e) {
        //Nothing to do
    }
    
}
