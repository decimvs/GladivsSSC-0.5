/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Shapes;

import Controllers.mainController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
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
    
    private double origSceneX, origSceneY;
    private double origTranslateX, origTranslateY;
    
    private boolean isMouseOver = false;
    private boolean isSelected = false;
    private boolean wasDragged = false;
    
    public sscText (Pane container, mainController mc) 
    {
        myContainer = container;
        maincontroller = mc;
        
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
        
        container.getChildren().add(textShape);
        container.getChildren().add(textSelection);
    }
    
    
    
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
                textShape.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));
                textSelection.setFont(Font.font((String) maincontroller.getComboBoxFontChooser().getSelectionModel().getSelectedItem(), Double.parseDouble((String) maincontroller.getComboBoxFontSize().getValue())));
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
    
        origSceneX = ev.getSceneX();
        origSceneY = ev.getSceneY();
        origTranslateX = textShape.getTranslateX();
        origTranslateY = textShape.getTranslateY();
        
        wasDragged = false;
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
