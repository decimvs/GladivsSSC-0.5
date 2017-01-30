/*
 * Copyright (C) 2016 Guillermo Espert Carrasquer [gespert@yahoo.es]
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package Controllers;

import ImageIO.SaveImage;
import Images.Screenshot;
import UIControls.sscTab;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Guillem
 */
public class mainController implements Initializable {
    
    @FXML
    private TabPane tabPane;
    @FXML
    private Button btnRectangle, btnSave, btnScreenshot, btnText, btnCircle, btnExportImage, btnNewDocument;
    @FXML
    private ColorPicker cpFillColor, cpBorderColor;
    @FXML
    private ComboBox cbStrokeType, cbxFontSize, cbxFontChooser;
    @FXML
    private Slider slStrokeWidth, slDashSpace, slDashWidth;
    @FXML
    private TextField txtStrokeWidth, txtDashSpace, txtDashWidth;
    @FXML
    private ToggleButton btnTextUnderline, btnItalicText, btnBoldText, btnCrop, btnSelectionTool;
    @FXML
    private ContextMenu cmShapeOptions;
    @FXML
    private MenuItem cmiBringToFront, cmiSendToBack, cmiBringForward, cmiSendBackward, cmiDeleteShape, cmiCopy, cmiPaste, cmiCancelSelection;
    @FXML
    private MenuItem miNewDocument, miChangeCanvasSize, miExportImage, miQuickExportImage;
    @FXML
    private Menu menuFile;
    @FXML
    private TextArea txtText;
    
    //Counter for autonaming files saved
    private int filesSaveCounter = 1;
    
    private WritableImage copiedImage = null;
    private mainController maincontroller = this;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        //Event handlers assignement
        btnRectangle.setOnAction(this::buttonRectangleActionPerformed);
        btnSave.setOnAction(this::buttonSaveActionPerformed);
        btnScreenshot.setOnAction(this::buttonTakeScreenshotActionPerformed);
        btnText.setOnAction(this::btnTextOnActionEvent);
        btnCircle.setOnAction(this::btnCircleOnActionEvent);
        btnCrop.addEventFilter(ActionEvent.ACTION, onButtonCropActionEvent);
        btnSelectionTool.addEventFilter(ActionEvent.ACTION, onButtonSelectionToolActionEvent);
        miNewDocument.addEventHandler(ActionEvent.ACTION, onMenuItemNewActionEvent);
        btnNewDocument.addEventHandler(ActionEvent.ACTION, onMenuItemNewActionEvent);
        miChangeCanvasSize.addEventHandler(ActionEvent.ACTION, onMenuItemChangeCanvasSizeActionEvent);
        btnExportImage.setOnAction(this::exportImageActionPerformed);
        miExportImage.setOnAction(this::exportImageActionPerformed);
        miQuickExportImage.setOnAction(this::quickExportImageActionPerformed);
        
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
    
    EventHandler<ActionEvent> onMenuItemChangeCanvasSizeActionEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if(tabPane.getTabs().size() > 0)
            {
                sscTab tab = (sscTab) tabPane.getSelectionModel().getSelectedItem();
                
                Parent root;
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/canvasResizeForm.fxml"));
                    
                    root = fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.setTitle("Canvas resize");
                    stage.setScene(new Scene(root));
                    stage.initModality(Modality.APPLICATION_MODAL);
                    
                    
                    CanvasResizeController crc = fxmlLoader.<CanvasResizeController>getController();
                    crc.setMyTabContainer(tab);
                    
                    stage.show();
                    
                } catch (IOException e) {
                    e.printStackTrace();
                    
                    return;
                }
            }
        }
    };
    
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
            createNewTab(wi);
        }
    }
    
    /**
     * Event handler for crop button.
     * Starts the selection mode in the selected tab for performing a crop action.
     */
    EventHandler<ActionEvent> onButtonCropActionEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if(tabPane.getTabs().size() > 0)
            {
                sscTab selTab = (sscTab) tabPane.getSelectionModel().getSelectedItem();
                selTab.StartCropMode();
                
                btnCrop.setSelected(true);
            }
        }
    };
    
    /**
     * Event handler for selection tool button.
     * Starts the selection mode in the selected tab for performing a selection of objects inside the canvas
     */
    EventHandler<ActionEvent> onButtonSelectionToolActionEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if(tabPane.getTabs().size() > 0)
            {
                sscTab selTab = (sscTab) tabPane.getSelectionModel().getSelectedItem();
                selTab.StartSelectionMode();
                
                btnSelectionTool.setSelected(true);
            }
        }
    };
    
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
    
    /**
     * Event action performer for the Take Screen Shot action
     * @param ev 
     */
    public void buttonTakeScreenshotActionPerformed(ActionEvent ev)
    {
        takeNewScreenshot();
    }
    
    /**
     * Event action performer for the button Rectangle
     * @param ev 
     */
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
       
    }
    
    public void exportImageActionPerformed(ActionEvent ev){
        tabExportSnapshotToFile(false);
    }
    
    public void quickExportImageActionPerformed(ActionEvent ev){
        tabExportSnapshotToFile(true);
    }
    
    /**
     * Perform the action for the buttons and menu items: Export
     */
    private void tabExportSnapshotToFile(boolean quickSave){
        if(tabPane.getTabs().size() > 0)
        {
            sscTab selTab = (sscTab) tabPane.getSelectionModel().getSelectedItem();
            File file;
            File initialDirectory;
            
            //Verify if we need to show the file chooser dialog
            if(!quickSave || (quickSave && selTab.getFileForSave() == null)){
                if(selTab.getFileForSave() == null){
                    initialDirectory = new File(System.getProperty("user.home"));
                }
                else
                {                    
                    initialDirectory = new File(selTab.getFileForSave().getParent());
                }
                
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choose image name and format");
                fileChooser.setInitialDirectory(initialDirectory);
                fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("PNG", "*.png"),
                    new FileChooser.ExtensionFilter("JPEG", "*.jpg"),
                    new FileChooser.ExtensionFilter("GIF", "*.gif")
                );
            
                file = fileChooser.showSaveDialog(cmShapeOptions);
            } else {
                file = selTab.getFileForSave();
            }
            
            //Checking that is possible to write in the file or user canceled chosing file
            if(file != null){
                if(SaveImage.save(selTab, file))
                {
                    Alert alo = new Alert(AlertType.INFORMATION, "File saved sucessfully");
                    System.out.println("File saved sucessfully");

                    //Set the file location for quickly save later
                    selTab.setFileForSave(file);
                    //Show success alert
                    alo.show();
                }
                else
                {
                    Alert ale = new Alert(AlertType.ERROR, "Error when saving the image");
                    ale.show();
                    System.out.println("Error when saving the image");
                }
            }
        }
    }
    
    private void createNewTab(WritableImage wi)
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
    
    private void createNewTab()
    {
        //Create a new tab and assigning a new name
        sscTab tab = new sscTab("Untitled-" + (tabPane.getTabs().size() + 1), this);

        //Assign tab in tabPane
        tabPane.getTabs().add(tab);

        //Setting the new tab as the tab selected
        tabPane.getSelectionModel().select(tab);
    }
    
    EventHandler<ActionEvent> onMenuItemNewActionEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            createNewTab();
        }
    };
    
    public ColorPicker getFillColorPicker(){ return cpFillColor; }
    public ColorPicker getBorderColorPicker(){ return cpBorderColor; }
    public ComboBox getStrokeTypeComboBox(){ return cbStrokeType; }
    public Slider getSliderStrokeWidth() { return slStrokeWidth; }
    public TextField getTextFieldStrokeWidth() { return txtStrokeWidth; }
    public Slider getSliderDashSpace(){ return slDashSpace; }
    public TextField getTextFieldDashSpace(){ return txtDashSpace; }
    public Slider getSliderDashWidth(){ return slDashWidth; }
    public TextField getTextFieldDashWidth(){ return txtDashWidth; }
    public TextArea getTextFieldText() { return txtText; }
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
    public MenuItem getMenuItemCopy() { return cmiCopy; }
    public MenuItem getMenuItemPaste() { return cmiPaste; }
    public MenuItem getMenuItemCancelSelection() { return cmiCancelSelection; }
    public WritableImage getImageWriterImageCopied() { return copiedImage; }
    public void setImageWriterImageCopied(WritableImage wi) { if(wi != null){ copiedImage = wi; } }
    public ToggleButton getButtonCropImage() { return btnCrop; }
    public TabPane getTabPane() { return tabPane; }
    public ToggleButton getButtonSelectionTool() { return btnSelectionTool; }
}
