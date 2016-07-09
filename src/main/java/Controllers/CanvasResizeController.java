/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import UIControls.sscTab;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Guillem
 */
public class CanvasResizeController implements Initializable {
    
    @FXML
    TextField txtCanvasWidth, txtCanvasHeight;
    @FXML
    Button btnChange;
    
    private sscTab myTab;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        //Force field to only accept numeric values
        txtCanvasWidth.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    txtCanvasWidth.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
            
        });
        
        //Force field to only accept numeric values
        txtCanvasHeight.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    txtCanvasHeight.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
            
        });
        
        btnChange.addEventHandler(ActionEvent.ACTION, onButtonChangeActionEvent);
    }    
    
    
    public void setMyTabContainer(sscTab tab) 
    { 
        if(tab != null)
        { 
            myTab = tab;
            
            Pane myPane = myTab.getPane();
            
            txtCanvasWidth.setText(
                    ((Integer)
                            ((Double) myPane.getWidth()).intValue())
                            .toString()
                    );
            
            txtCanvasHeight.setText(
                    ((Integer)
                            ((Double) myPane.getHeight()).intValue())
                            .toString()
                    );
        }
    }
    
    EventHandler<ActionEvent> onButtonChangeActionEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Pane pane = myTab.getPane();
            
            Double width = new Double(txtCanvasWidth.getText());
            Double height = new Double(txtCanvasHeight.getText());
            
            pane.setMaxSize(width, height);
            pane.setMinSize(width, height);
            
            //Form closing
            Stage stage = (Stage) btnChange.getScene().getWindow();
            stage.close();
        }
    };
}
