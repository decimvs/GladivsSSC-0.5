/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import ImageIO.SaveImage;
import Images.Screenshot;
import UIControls.sscTab;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 *
 * @author Guillem
 */
public class mainController implements Initializable {
    
    @FXML
    private TabPane tabPane;
    @FXML
    private Button btnRectangle, btnSave, btnScreenshot, btnText, btnCircle;
    @FXML
    private ColorPicker cpFillColor, cpBorderColor;
    @FXML
    private ComboBox cbStrokeType, cbxFontSize, cbxFontChooser;
    @FXML
    private Slider slStrokeWidth, slDashSpace, slDashWidth;
    @FXML
    private TextField txtStrokeWidth, txtDashSpace, txtDashWidth, txtText;
    @FXML
    private ToggleButton btnTextUnderline, btnItalicText, btnBoldText;
    @FXML
    private ContextMenu cmShapeOptions;
    @FXML
    private MenuItem cmiBringToFront, cmiSendToBack, cmiBringForward, cmiSendBackward, cmiDeleteShape;
    
    //Counter for autonaming files saved
    private int filesSaveCounter = 1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        //Event handlers assignement
        btnRectangle.setOnAction(this::buttonRectangleActionPerformed);
        btnSave.setOnAction(this::buttonSaveActionPerformed);
        btnScreenshot.setOnAction(this::buttonTakeScreenshotActionPerformed);
        btnText.setOnAction(this::btnTextOnActionEvent);
        btnCircle.setOnAction(this::btnCircleOnActionEvent);
        
        //Setting first element selected in the border type combo box
        cbStrokeType.getSelectionModel().selectFirst();
        slStrokeWidth.valueProperty().addListener(onSlideStrokeWidthValueChange);
        slDashSpace.valueProperty().addListener(onSlideDashSpaceValueChange);
        slDashWidth.valueProperty().addListener(onSlideDashWidthValueChange);
        
        //Getting, showing fonts and selecting first one 
        List<String> fonts = Font.getFamilies();
        cbxFontChooser.getItems().addAll(fonts);
        cbxFontChooser.getSelectionModel().selectFirst();
        
        //Setting default colors in color pickers
        cpBorderColor.setValue(Color.BLACK);
    }
    
    /**
     * Performs a new Screenshot capture
     * This methods take the screnshot and shows it in a new tab
     */
    public void takeNewScreenshot()
    {
        //Take the screenshot and write it to an WritableImage
        Screenshot ssh = new Screenshot();
        WritableImage wi = ssh.takeScreenshot();
        
        if(wi != null)
        {
            //Create a new tab and assigning a new name
            sscTab tab = new sscTab("Untitled-" + (tabPane.getTabs().size() + 1), this);
            
            
            //Set the image on imageview
            tab.getImageView().setImage(wi);
            
            //Assign tab in tabPane
            tabPane.getTabs().add(tab);
            
            //Setting the new tab as the tab selected
            tabPane.getSelectionModel().select(tab);
        }
    }
    
    public void btnCircleOnActionEvent(ActionEvent ev)
    {
        if(tabPane.getTabs().size() > 0)
        {
            sscTab selTab = (sscTab) tabPane.getSelectionModel().getSelectedItem();
            selTab.CreateNewEllipse();
        }
    }
    
    public void btnTextOnActionEvent(ActionEvent ev)
    {
        if(tabPane.getTabs().size() > 0)
        {
            sscTab selTab = (sscTab) tabPane.getSelectionModel().getSelectedItem();
            selTab.CreateNewText();
        }
    }
    
    ChangeListener<Number> onSlideDashWidthValueChange = new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            
                    txtDashWidth.setText(Integer.toString(((Double) newValue).intValue()));
        }
    };
    
    ChangeListener<Number> onSlideDashSpaceValueChange = new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            
                    txtDashSpace.setText(Integer.toString(((Double) newValue).intValue()));
        }
    };
    
    ChangeListener<Number> onSlideStrokeWidthValueChange = new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            
                    txtStrokeWidth.setText(Integer.toString(((Double) newValue).intValue()));
        }
    };
    
    public void buttonTakeScreenshotActionPerformed(ActionEvent ev)
    {
        takeNewScreenshot();
    }
    
    public void buttonRectangleActionPerformed(ActionEvent ev)
    {
        if(tabPane.getTabs().size() > 0)
        {
            sscTab selTab = (sscTab) tabPane.getSelectionModel().getSelectedItem();
            selTab.CreateNewRectangle();
        }
    }
    
    public void buttonSaveActionPerformed(ActionEvent ev)
    {
        if(tabPane.getTabs().size() > 0)
        {
            sscTab selTab = (sscTab) tabPane.getSelectionModel().getSelectedItem();
            
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose image name and format");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("PNG", "*.png"),
                    new FileChooser.ExtensionFilter("JPEG", "*.jpg"),
                    new FileChooser.ExtensionFilter("GIF", "*.gif")
            );
            
            File file = fileChooser.showSaveDialog(cmShapeOptions);
            
            if(file != null)
            {
                
                if(SaveImage.save(selTab, file))
                {
                    System.out.println("File saved sucessfully");
                }
                else
                {
                    System.out.println("Error when saving the image");
                }
            }
        }
    }
    
    public ColorPicker getFillColorPicker(){ return cpFillColor; }
    public ColorPicker getBorderColorPicker(){ return cpBorderColor; }
    public ComboBox getStrokeTypeComboBox(){ return cbStrokeType; }
    public Slider getSliderStrokeWidth() { return slStrokeWidth; }
    public TextField getTextFieldStrokeWidth() { return txtStrokeWidth; }
    public Slider getSliderDashSpace(){ return slDashSpace; }
    public TextField getTextFieldDashSpace(){ return txtDashSpace; }
    public Slider getSliderDashWidth(){ return slDashWidth; }
    public TextField getTextFieldDashWidth(){ return txtDashWidth; }
    public TextField getTextFieldText() { return txtText; }
    public ComboBox getComboBoxFontSize() { return cbxFontSize; }
    public ComboBox getComboBoxFontChooser() { return cbxFontChooser; }
    public ToggleButton getButtonTextUnderline() { return btnTextUnderline; }
    public ToggleButton getButtonBoldText() { return btnBoldText; }
    public ToggleButton getButtonItalicText() { return btnItalicText; }
    public ContextMenu getContextMenuShapeOptions() { return cmShapeOptions; }
    public MenuItem getMenuItemBringToFront() { return cmiBringToFront; }
    public MenuItem getMenuItemSendToBack() { return cmiSendToBack; }
    public MenuItem getMenuItemBringForward() { return cmiBringForward; }
    public MenuItem getMenuItemSendBackward() { return cmiSendBackward; }
    public MenuItem getMenuItemDeleteShape() { return cmiDeleteShape; }
}
