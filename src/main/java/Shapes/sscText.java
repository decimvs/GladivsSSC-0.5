/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Shapes;

import Controllers.mainController;
import UIControls.sscTab;
import java.util.HashMap;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 *
 * @author Guillem
 */
public class sscText  implements sscShape {
    
    private Pane myContainer;
    private mainController maincontroller;
    private Text shape;
    private Rectangle selection;
    private sscTab rootTab;
    
    private double origSceneX, origSceneY;
    private double origTranslateX, origTranslateY;
    
    private boolean isMouseOver = false;
    private boolean isSelected = false;
    private boolean wasDragged = false;
    
    private int indexStrokeType = 0;
    
    public sscText (sscTab tab, mainController mc) 
    {
        myContainer = tab.getPane();
        maincontroller = mc;
        rootTab = tab;
        
        shape = new Text();
        selection = new Rectangle();
        
        selection.setFill(new Color(0,0,0,0));
        
        //Initialising text. Set text value, font family and size
        shape.setText("Text");
        shape.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));    
        
        //Setting text origin
        shape.setX(80);
        shape.setY(80);
        
        //Set selection origin
        Bounds shapeBounds = shape.getBoundsInParent();
        
        selection.setX(shapeBounds.getMinX());
        selection.setY(shapeBounds.getMinY());
        
        shape.setStrokeType(StrokeType.CENTERED);
        selection.setStrokeType(StrokeType.OUTSIDE);
        
        switch(maincontroller.getStrokeTypeComboBox().getSelectionModel().getSelectedIndex())
        {
            case 0:     //Border none
                 shape.setStrokeWidth(0);
                 shape.setStroke(Color.BLACK);
                 indexStrokeType = 0;
                break;
            case 1:     //Border lineal
                shape.setStrokeWidth(maincontroller.getSliderStrokeWidth().getValue());
                shape.setStroke(maincontroller.getBorderColorPicker().getValue());
                indexStrokeType = 1;
                break;
            case 2:     //Border dashed
                shape.getStrokeDashArray().clear();
                shape.getStrokeDashArray().addAll(maincontroller.getSliderDashWidth().getValue(), maincontroller.getSliderDashSpace().getValue());
                shape.setStrokeWidth(maincontroller.getSliderStrokeWidth().getValue());
                shape.setStroke(maincontroller.getBorderColorPicker().getValue());
                indexStrokeType = 2;
                break;
        }
        
        //Set selection area size
        selection.setWidth(shapeBounds.getWidth());
        selection.setHeight(shapeBounds.getHeight());
        
        //Assigning event handlers
        selection.setOnMousePressed(this::SelectionOnMousePressedEventHandler);
        selection.setOnMouseDragged(this::SelectionOnMouseDraggedEventHandler);
        selection.setOnMouseReleased(this::SelectionOnMouseButtonReleasedEventHandler);
        selection.setOnMouseEntered(this::SelectionOnMouseOverEventHandler);
        selection.setOnMouseExited(this::SelectionOnMouseOutEventHandler);
        maincontroller.getTextFieldText().addEventFilter(KeyEvent.KEY_RELEASED, onTextKeysTiped);
        maincontroller.getComboBoxFontSize().addEventFilter(ActionEvent.ACTION, onChangeFontFamilyOrSize);
        maincontroller.getComboBoxFontChooser().addEventFilter(ActionEvent.ACTION, onChangeFontFamilyOrSize);
        myContainer.addEventFilter(MouseEvent.MOUSE_PRESSED, onPaneMousePressedEvent);
        maincontroller.getFillColorPicker().addEventFilter(ActionEvent.ACTION, onFillColorPickerActionEvent);
        maincontroller.getBorderColorPicker().addEventFilter(ActionEvent.ACTION, onStrokeColorPickerActionEvent);
        maincontroller.getStrokeTypeComboBox().addEventFilter(ActionEvent.ACTION, onStrokeTypeDDMChanged);
        maincontroller.getSliderStrokeWidth().valueProperty().addListener(onSlideStrokeWidthValueChange);
        maincontroller.getSliderDashSpace().valueProperty().addListener(onSlideDashPropertiesValueChange);
        maincontroller.getSliderDashWidth().valueProperty().addListener(onSlideDashPropertiesValueChange);
        maincontroller.getButtonTextUnderline().addEventFilter(ActionEvent.ACTION, onButtonTextUnderlineActionEvent);
        maincontroller.getButtonItalicText().addEventFilter(ActionEvent.ACTION, onBoldItalicButtonsActionEvent);
        maincontroller.getButtonBoldText().addEventFilter(ActionEvent.ACTION, onBoldItalicButtonsActionEvent);
        maincontroller.getMenuItemDeleteShape().addEventHandler(ActionEvent.ACTION, onMenuItemDeleteShapeActionEvent);
        maincontroller.getMenuItemSendBackward().addEventHandler(ActionEvent.ACTION, onMenuItemSendBackwardtActionEvent);
        maincontroller.getMenuItemBringForward().addEventHandler(ActionEvent.ACTION, onMenuItemBringForwardActionEvent);
        maincontroller.getMenuItemSendToBack().addEventHandler(ActionEvent.ACTION, onMenuItemSendToBackActionEvent);
        maincontroller.getMenuItemBringToFront().addEventHandler(ActionEvent.ACTION, onMenuItemBringToFrontActionEvent);
        
        myContainer.getChildren().add(shape);
        myContainer.getChildren().add(selection);
    }
    
    EventHandler<ActionEvent> onMenuItemDeleteShapeActionEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if(isSelected)
            {
///TODO CONFIRMATION PROMPT                
                myContainer.getChildren().removeAll(selection, shape);
            }
        }
    };
    
    EventHandler<ActionEvent> onMenuItemSendBackwardtActionEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if(isSelected)
            {
                rootTab.ModifyShapesOrderInPane(shape, selection, "backward");
            }
        }
    };
    
    EventHandler<ActionEvent> onMenuItemBringForwardActionEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if(isSelected)
            {
                rootTab.ModifyShapesOrderInPane(shape, selection, "forward");
            }
        }
    };
    
    EventHandler<ActionEvent> onMenuItemSendToBackActionEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if(isSelected)
            {
                rootTab.ModifyShapesOrderInPane(shape, selection, "back");
            }
        }
    };
    
    EventHandler<ActionEvent> onMenuItemBringToFrontActionEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if(isSelected)
            {
                rootTab.ModifyShapesOrderInPane(shape, selection, "front");
            }
        }
    };
    
    EventHandler<ActionEvent> onBoldItalicButtonsActionEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if(isSelected)
            {
                ToggleButton btnbold = maincontroller.getButtonBoldText();
                ToggleButton btnitalic = maincontroller.getButtonItalicText();
                
                if(btnbold.isSelected() && btnitalic.isSelected())
                {
                    shape.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), FontWeight.BOLD, FontPosture.ITALIC, Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));
                }
                else if (btnbold.isSelected() && !btnitalic.isSelected())
                {
                    shape.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), FontWeight.BOLD, FontPosture.REGULAR, Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));
                }
                else if (!btnbold.isSelected() && btnitalic.isSelected())
                {
                    shape.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), FontWeight.NORMAL, FontPosture.ITALIC, Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));
                }
                else
                {
                    shape.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), FontWeight.NORMAL, FontPosture.REGULAR, Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));
                }
                
                //Request focus in Pane container to unfocus this control.
                myContainer.requestFocus();
                
                //Update selection area size
                updateSelectionSize();
            }
        }
    };
    
    EventHandler<ActionEvent> onButtonTextUnderlineActionEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if(isSelected)
            {
                if(shape.isUnderline())
                {
                    shape.setUnderline(false);
                    //selection.setUnderline(false);
                    maincontroller.getButtonTextUnderline().setSelected(false);
                    myContainer.requestFocus();
                }
                else
                {
                    shape.setUnderline(true);
                    //selection.setUnderline(true);
                    maincontroller.getButtonTextUnderline().setSelected(true);
                    myContainer.requestFocus();
                }
                
                //Update selection area size
                updateSelectionSize();
            }
        }
    };
    
    ChangeListener<Number> onSlideStrokeWidthValueChange = new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            if(isSelected)
            {
                //If border type is selected to None, here it hasn't anything to do
                if(maincontroller.getStrokeTypeComboBox().getSelectionModel().getSelectedIndex() > 0)
                {
                    Slider slider = maincontroller.getSliderStrokeWidth();
                    Double width = shape.getStrokeWidth();

                    if(slider.getValue() != width)
                    {
                        shape.setStrokeWidth(slider.getValue());
                    }
                    
                    //Update selection area size
                    updateSelectionSize();
                }
            }
        }
    };
    
    ChangeListener<Number> onSlideDashPropertiesValueChange = new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            if(isSelected)
            {
                Slider sliderSpace = maincontroller.getSliderDashSpace();
                Slider sliderWidth = maincontroller.getSliderDashWidth();
                
                shape.getStrokeDashArray().clear();
                shape.getStrokeDashArray().addAll(sliderWidth.getValue(), sliderSpace.getValue());
                
                //Update selection area size
                updateSelectionSize();
            }
        }
    };
    
    EventHandler<ActionEvent> onStrokeTypeDDMChanged = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if(isSelected)
            {
                ComboBox cb = maincontroller.getStrokeTypeComboBox();

                switch(cb.getSelectionModel().getSelectedIndex())
                {
                    case 0:     //none
                        shape.setStrokeWidth(0);
                        indexStrokeType = 0;
                        break;
                    case 1:     //lineal
                        shape.getStrokeDashArray().clear();
                        shape.setStrokeWidth(maincontroller.getSliderStrokeWidth().getValue());
                        indexStrokeType = 1;
                        break;
                    case 2:     //punts
                        shape.getStrokeDashArray().clear();
                        shape.getStrokeDashArray().addAll(maincontroller.getSliderDashWidth().getValue(), maincontroller.getSliderDashSpace().getValue());
                        shape.setStrokeWidth(maincontroller.getSliderStrokeWidth().getValue());
                        indexStrokeType = 2;
                        break;
                }
                
                //Update selection area size
                updateSelectionSize();
            }
        }
    };
    
    EventHandler<ActionEvent> onStrokeColorPickerActionEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent ev) {
            if(isSelected)
            {
                shape.setStroke(maincontroller.getBorderColorPicker().getValue());
            }
        }
    };
    
    EventHandler<ActionEvent> onFillColorPickerActionEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if(isSelected)
            {
                shape.setFill(maincontroller.getFillColorPicker().getValue());
            }
        }
    };
    
    EventHandler<MouseEvent> onPaneMousePressedEvent = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if(!isMouseOver && isSelected)
            {
                selection.setStrokeWidth(0);
                
                isSelected = false;
            }
        }
    };
    
    EventHandler<ActionEvent> onChangeFontFamilyOrSize = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent ev) {
            if(isSelected)
            {
                ToggleButton btnbold = maincontroller.getButtonBoldText();
                ToggleButton btnitalic = maincontroller.getButtonItalicText();
                
                if(btnbold.isSelected() && btnitalic.isSelected())
                {
                    shape.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), FontWeight.BOLD, FontPosture.ITALIC, Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));
                }
                else if (btnbold.isSelected() && !btnitalic.isSelected())
                {
                    shape.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), FontWeight.BOLD, FontPosture.REGULAR, Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));
                }
                else if (!btnbold.isSelected() && btnitalic.isSelected())
                {
                    shape.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), FontWeight.NORMAL, FontPosture.ITALIC, Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));
                }
                else
                {
                    shape.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), FontWeight.NORMAL, FontPosture.REGULAR, Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));//selection.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), FontWeight.NORMAL, FontPosture.REGULAR, Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));
                }
                
                shape.setText(maincontroller.getTextFieldText().getText());
                
                //Update selection area size
                updateSelectionSize();
            }
        }
        
    };
    
    EventHandler<KeyEvent> onTextKeysTiped = new EventHandler<KeyEvent>(){
        @Override
        public void handle(KeyEvent ev) {
            if(isSelected)
            {
                //Update text
                shape.setText(maincontroller.getTextFieldText().getText());
                
                //Update selection box size
                updateSelectionSize();
            }
        }
        
    };

    @Override
    public void SelectionOnMousePressedEventHandler(MouseEvent ev) {
    
        if(ev.getButton() == MouseButton.PRIMARY)
        {
            origSceneX = ev.getSceneX();
            origSceneY = ev.getSceneY();
            origTranslateX = shape.getTranslateX();
            origTranslateY = shape.getTranslateY();

            wasDragged = false;
        }
    }

    @Override
    public void SelectionOnMouseDraggedEventHandler(MouseEvent ev) {
        
        double offsetX = ev.getSceneX() - origSceneX;
        double offsetY = ev.getSceneY() - origSceneY;
        double newTranslateX = origTranslateX + offsetX;
        double newTranslateY = origTranslateY + offsetY;
        //double newWidth = origWidth + offsetX;
        //double newHeight = origHeight + offsetY;
        
        shape.setTranslateX(newTranslateX);
        selection.setTranslateX(newTranslateX);
        shape.setTranslateY(newTranslateY);
        selection.setTranslateY(newTranslateY);
        
        wasDragged = true;
    }

    @Override
    public void SelectionOnMouseButtonReleasedEventHandler(MouseEvent ev) {
        if(ev.getButton() == MouseButton.PRIMARY)
        {
            if(isSelected && !wasDragged)
            {
                isSelected = false;
            }
            else
            {
                isSelected = true;

                maincontroller.getTextFieldText().setText(shape.getText());
                maincontroller.getComboBoxFontChooser().getSelectionModel().select(shape.getFont().getFamily());
                maincontroller.getComboBoxFontSize().setValue(String.valueOf(new Double(shape.getFont().getSize()).intValue()));
                maincontroller.getFillColorPicker().setValue((Color) shape.getFill());
            }
        }
    }

    @Override
    public void SelectionOnMouseOverEventHandler(MouseEvent ev) {
        
        selection.setStroke(Color.CORNFLOWERBLUE);
        selection.setStrokeWidth(2);
        
        isMouseOver = true;
    }

    @Override
    public void SelectionOnMouseMoveEventHandler(MouseEvent ev) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void SelectionOnMouseOutEventHandler(MouseEvent ev) {
        if(!isSelected)
        {
            selection.setStrokeWidth(0);
        }
        
        isMouseOver = false;
    }

    @Override
    public boolean SelectionArea(Shape sp, MouseEvent ev) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void SaveShapeDimensions(Shape sp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void SetShapeDimensions(Shape sp, MouseEvent ev, double width, double height, double translateX, double translateY, double offsetX, double offsetY) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void DrawSelectionArea(double width, double height) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Shape getShape() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
   
    /**
     * Get the text bounds and aplies the text width and height to the selection area
     */
    private void updateSelectionSize()
    {
        Bounds text = shape.getBoundsInLocal();
        
        selection.setWidth(text.getWidth());
        selection.setHeight(text.getHeight());
        
        //Repositioning selection area
        selection.setY(text.getMinY());
    }
    
    @Override
    public HashMap<String, Double> getShapeDimensions() 
    {
        HashMap<String, Double> mhm = new HashMap<>();
        
        mhm.put("x", shape.getBoundsInLocal().getMinX());
        mhm.put("y", shape.getBoundsInLocal().getMinY());
        mhm.put("translateX", shape.getTranslateX());
        mhm.put("translateY", shape.getTranslateY());
        
        return mhm;
    }
    
    @Override
    public void setShapeSelected(boolean selected) {
        isSelected = selected;
    }
}
