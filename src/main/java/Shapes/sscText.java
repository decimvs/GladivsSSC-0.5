/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Shapes;

import Controllers.mainController;
import UIControls.sscTab;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
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
    private Text textShape;
    private Text textSelection;
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
        
        Text tx = new Text();
        Text txs = new Text();
        
        textShape = tx;
        textSelection = txs;
        
        textSelection.setFill(new Color(0,0,0,0));
        
        //Initialising text. Set text value, font family and size
        setText(maincontroller.getTextFieldText().getText());
        textShape.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));
        textSelection.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));
        
        textShape.setX(80);
        textShape.setY(80);
        
        textSelection.setX(80);
        textSelection.setY(80);
        
        textShape.setStrokeType(StrokeType.CENTERED);
        textSelection.setStrokeType(StrokeType.OUTSIDE);
        
        switch(maincontroller.getStrokeTypeComboBox().getSelectionModel().getSelectedIndex())
        {
            case 0:     //Border none
                 textShape.setStrokeWidth(0);
                 textShape.setStroke(Color.BLACK);
                 indexStrokeType = 0;
                break;
            case 1:     //Border lineal
                textShape.setStrokeWidth(maincontroller.getSliderStrokeWidth().getValue());
                textShape.setStroke(maincontroller.getBorderColorPicker().getValue());
                indexStrokeType = 1;
                break;
            case 2:     //Border dashed
                textShape.getStrokeDashArray().clear();
                textShape.getStrokeDashArray().addAll(maincontroller.getSliderDashWidth().getValue(), maincontroller.getSliderDashSpace().getValue());
                textShape.setStrokeWidth(maincontroller.getSliderStrokeWidth().getValue());
                textShape.setStroke(maincontroller.getBorderColorPicker().getValue());
                indexStrokeType = 2;
                break;
        }
        
        //Assigning event handlers
        textSelection.setOnMousePressed(this::SelectionOnMousePressedEventHandler);
        textSelection.setOnMouseDragged(this::SelectionOnMouseDraggedEventHandler);
        textSelection.setOnMouseReleased(this::SelectionOnMouseButtonReleasedEventHandler);
        textSelection.setOnMouseEntered(this::SelectionOnMouseOverEventHandler);
        textSelection.setOnMouseExited(this::SelectionOnMouseOutEventHandler);
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
        
        myContainer.getChildren().add(textShape);
        myContainer.getChildren().add(textSelection);
    }
    
    EventHandler<ActionEvent> onMenuItemDeleteShapeActionEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if(isSelected)
            {
///TODO CONFIRMATION PROMPT                
                myContainer.getChildren().removeAll(textSelection, textShape);
            }
        }
    };
    
    EventHandler<ActionEvent> onMenuItemSendBackwardtActionEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if(isSelected)
            {
                rootTab.ModifyShapesOrderInPane(textShape, textSelection, "backward");
            }
        }
    };
    
    EventHandler<ActionEvent> onMenuItemBringForwardActionEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if(isSelected)
            {
                rootTab.ModifyShapesOrderInPane(textShape, textSelection, "forward");
            }
        }
    };
    
    EventHandler<ActionEvent> onMenuItemSendToBackActionEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if(isSelected)
            {
                rootTab.ModifyShapesOrderInPane(textShape, textSelection, "back");
            }
        }
    };
    
    EventHandler<ActionEvent> onMenuItemBringToFrontActionEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if(isSelected)
            {
                rootTab.ModifyShapesOrderInPane(textShape, textSelection, "front");
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
                    textShape.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), FontWeight.BOLD, FontPosture.ITALIC, Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));
                    textSelection.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), FontWeight.BOLD, FontPosture.ITALIC, Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));
                }
                else if (btnbold.isSelected() && !btnitalic.isSelected())
                {
                    textShape.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), FontWeight.BOLD, FontPosture.REGULAR, Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));
                    textSelection.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), FontWeight.BOLD, FontPosture.REGULAR, Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));
                }
                else if (!btnbold.isSelected() && btnitalic.isSelected())
                {
                    textShape.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), FontWeight.NORMAL, FontPosture.ITALIC, Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));
                    textSelection.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), FontWeight.NORMAL, FontPosture.ITALIC, Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));
                }
                else
                {
                    textShape.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), FontWeight.NORMAL, FontPosture.REGULAR, Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));
                    textSelection.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), FontWeight.NORMAL, FontPosture.REGULAR, Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));
                }
                
                //Request focus in Pane container to unfocus this control.
                myContainer.requestFocus();
            }
        }
    };
    
    EventHandler<ActionEvent> onButtonTextUnderlineActionEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if(isSelected)
            {
                if(textShape.isUnderline())
                {
                    textShape.setUnderline(false);
                    textSelection.setUnderline(false);
                    maincontroller.getButtonTextUnderline().setSelected(false);
                    myContainer.requestFocus();
                }
                else
                {
                    textShape.setUnderline(true);
                    textSelection.setUnderline(true);
                    maincontroller.getButtonTextUnderline().setSelected(true);
                    myContainer.requestFocus();
                }
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
                    Double width = textShape.getStrokeWidth();

                    if(slider.getValue() != width)
                    {
                        textShape.setStrokeWidth(slider.getValue());
                    }
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
                
                textShape.getStrokeDashArray().clear();
                textShape.getStrokeDashArray().addAll(sliderWidth.getValue(), sliderSpace.getValue());
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
                        textShape.setStrokeWidth(0);
                        indexStrokeType = 0;
                        break;
                    case 1:     //lineal
                        textShape.getStrokeDashArray().clear();
                        textShape.setStrokeWidth(maincontroller.getSliderStrokeWidth().getValue());
                        indexStrokeType = 1;
                        break;
                    case 2:     //punts
                        textShape.getStrokeDashArray().clear();
                        textShape.getStrokeDashArray().addAll(maincontroller.getSliderDashWidth().getValue(), maincontroller.getSliderDashSpace().getValue());
                        textShape.setStrokeWidth(maincontroller.getSliderStrokeWidth().getValue());
                        indexStrokeType = 2;
                        break;
                }
            }
        }
    };
    
    EventHandler<ActionEvent> onStrokeColorPickerActionEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent ev) {
            if(isSelected)
            {
                textShape.setStroke(maincontroller.getBorderColorPicker().getValue());
            }
        }
    };
    
    EventHandler<ActionEvent> onFillColorPickerActionEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if(isSelected)
            {
                textShape.setFill(maincontroller.getFillColorPicker().getValue());
            }
        }
    };
    
    EventHandler<MouseEvent> onPaneMousePressedEvent = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if(!isMouseOver && isSelected)
            {
                textSelection.setStrokeWidth(0);
                
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
                    textShape.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), FontWeight.BOLD, FontPosture.ITALIC, Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));
                    textSelection.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), FontWeight.BOLD, FontPosture.ITALIC, Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));
                }
                else if (btnbold.isSelected() && !btnitalic.isSelected())
                {
                    textShape.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), FontWeight.BOLD, FontPosture.REGULAR, Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));
                    textSelection.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), FontWeight.BOLD, FontPosture.REGULAR, Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));
                }
                else if (!btnbold.isSelected() && btnitalic.isSelected())
                {
                    textShape.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), FontWeight.NORMAL, FontPosture.ITALIC, Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));
                    textSelection.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), FontWeight.NORMAL, FontPosture.ITALIC, Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));
                }
                else
                {
                    textShape.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), FontWeight.NORMAL, FontPosture.REGULAR, Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));
                    textSelection.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), FontWeight.NORMAL, FontPosture.REGULAR, Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));
                }
            }
        }
        
    };
    
    EventHandler<KeyEvent> onTextKeysTiped = new EventHandler<KeyEvent>(){
        @Override
        public void handle(KeyEvent ev) {
            if(isSelected)
            {
                textShape.setText(maincontroller.getTextFieldText().getText());
                textSelection.setText(maincontroller.getTextFieldText().getText());
            }
        }
        
    };
    
    public void setText (String text)
    {
            textShape.setText(text);
            textSelection.setText(text);
    }

    @Override
    public void SelectionOnMousePressedEventHandler(MouseEvent ev) {
    
        if(ev.getButton() == MouseButton.PRIMARY)
        {
            origSceneX = ev.getSceneX();
            origSceneY = ev.getSceneY();
            origTranslateX = textShape.getTranslateX();
            origTranslateY = textShape.getTranslateY();

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
        
        textShape.setTranslateX(newTranslateX);
        textSelection.setTranslateX(newTranslateX);
        textShape.setTranslateY(newTranslateY);
        textSelection.setTranslateY(newTranslateY);
        
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

                maincontroller.getTextFieldText().setText(textShape.getText());
                maincontroller.getComboBoxFontChooser().getSelectionModel().select(textShape.getFont().getFamily());
                maincontroller.getComboBoxFontSize().setValue(String.valueOf(new Double(textShape.getFont().getSize()).intValue()));
                maincontroller.getFillColorPicker().setValue((Color) textShape.getFill());
            }
        }
    }

    @Override
    public void SelectionOnMouseOverEventHandler(MouseEvent ev) {
        
        textSelection.setStroke(Color.CORNFLOWERBLUE);
        textSelection.setStrokeWidth(2);
        
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
            textSelection.setStrokeWidth(0);
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
   
    
    
}
