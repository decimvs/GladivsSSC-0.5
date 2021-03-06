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
import javafx.scene.Cursor;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;

/**
 *
 * @author Guillem
 */
public class sscEllipse implements sscShape {
    
    private Ellipse shape;        //Figura
    private Rectangle selection;   //Selecció
    private Pane container;         //Contenedor
    private mainController maincontroller; //Main Controller instance
    private sscTab rootTab;         //Root of node tab. Contents Pane container
    
    //Variables per a enregistrar les posicions de l'objecte i el ratolí quant es fa click
    private double origSceneX, origSceneY;
    private double origTranslateX, origTranslateY;
    
    //Variables per a enregistrar el tamany de l'objecte quant es fa click
    protected double origXRad, origYRad;
    
    //Variable de control per a la funció edició per arrossegament
    protected boolean isDragEditting = false;
    
    //Variable de control per a la operació desplaçar
    private boolean isTranslating = false;
    
    private boolean isSelected = false;
    private boolean wasDragged = false;
    private boolean isMouseOver = false;
    
    private int indexStrokeType = 0;
    
    private String myName;
    
    //Constants for edges control
    private final int asN = 0;
    private final int asS = 1;
    private final int asE = 2;
    private final int asW = 3;
    private final int asNE = 4;
    private final int asSE = 5;
    private final int asNW = 6;
    private final int asSW = 7;
    
    private int selectedEditionEdge = -1;
    
    public sscEllipse(double width, double  height, sscTab tab, mainController mc, String name)
    {
        container = tab.getPane();
        maincontroller = mc;
        myName = name;
        rootTab = tab;
        
        //Capa de selecció
        DrawSelectionArea(width, height);
        
        //Figura
        //+Width +Height per a que el centre es deplace la longitud del radi així l'extrem del radi coincidix amb el punt 0,0 del rectangle de selecció
        shape = new Ellipse( 70 + width, 70 + height, width, height);
        
        //Afegir figura i capa de selecció dins del contenidor
        container.getChildren().add(shape);
        container.getChildren().add(selection);
        
        /**
         * Stablish initial atributes for the shape: border, fill color, border width and border color.
         */
        shape.setFill(maincontroller.getFillColorPicker().getValue());
        shape.setStrokeType(StrokeType.INSIDE);
        
        switch(maincontroller.getStrokeTypeComboBox().getSelectionModel().getSelectedIndex())
        {
            case 0:     //Border none
                 shape.setStrokeWidth(0);
                 shape.setStroke(Color.WHITE);
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
        
        /**
         * Adding all event handlers and filters.
         */
        container.addEventFilter(MouseEvent.MOUSE_PRESSED, onPaneMouseButtonPressed);
        maincontroller.getFillColorPicker().addEventFilter(ActionEvent.ACTION, onFillColorPickerActionEvent);
        maincontroller.getSliderStrokeWidth().valueProperty().addListener(onSlideStrokeWidthValueChange);
        maincontroller.getStrokeTypeComboBox().addEventFilter(ActionEvent.ACTION, onStrokeTypeDDMChanged);
        maincontroller.getBorderColorPicker().addEventFilter(ActionEvent.ACTION, onStrokeColorPickerActionEvent);
        maincontroller.getSliderDashSpace().valueProperty().addListener(onSlideDashPropertiesValueChange);
        maincontroller.getSliderDashWidth().valueProperty().addListener(onSlideDashPropertiesValueChange);
        maincontroller.getMenuItemDeleteShape().addEventHandler(ActionEvent.ACTION, onMenuItemDeleteShapeActionEvent);
        maincontroller.getMenuItemSendBackward().addEventHandler(ActionEvent.ACTION, onMenuItemSendBackwardtActionEvent);
        maincontroller.getMenuItemBringForward().addEventHandler(ActionEvent.ACTION, onMenuItemBringForwardActionEvent);
        maincontroller.getMenuItemSendToBack().addEventHandler(ActionEvent.ACTION, onMenuItemSendToBackActionEvent);
        maincontroller.getMenuItemBringToFront().addEventHandler(ActionEvent.ACTION, onMenuItemBringToFrontActionEvent);
    }
    
    EventHandler<ActionEvent> onMenuItemDeleteShapeActionEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if(isSelected)
            {
///TODO CONFIRMATION PROMPT                
                container.getChildren().removeAll(selection, shape);
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
    
    EventHandler<ActionEvent> onStrokeColorPickerActionEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent ev) {
            if(isSelected)
            {
                ColorPicker cp = (ColorPicker) ev.getSource();
                
                shape.setStroke(cp.getValue());
            }
        }
    };
    
    EventHandler<ActionEvent> onStrokeTypeDDMChanged = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if(isSelected)
            {
                ComboBox cb = (ComboBox) event.getSource();

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
            }
        }
    };
    
    EventHandler<ActionEvent> onFillColorPickerActionEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent ev) {
            if(isSelected)
            {
                ColorPicker cp = (ColorPicker) ev.getSource();

                shape.setFill(cp.getValue());
            }
        }
    };
    
    EventHandler<MouseEvent> onPaneMouseButtonPressed = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if(!isMouseOver && isSelected)
            {
                isSelected = false;
                
                selection.setStroke(null); 
                selection.setStrokeWidth(0);
            }
        }
    };
    
    private void updateShapePropertiesOnPropertiesWindow()
    {
        Slider slider = maincontroller.getSliderStrokeWidth();
        TextField txt = maincontroller.getTextFieldStrokeWidth();
        ComboBox cbStroke = maincontroller.getStrokeTypeComboBox();
        ColorPicker cpShape = maincontroller.getFillColorPicker();
        ColorPicker cpStroke = maincontroller.getBorderColorPicker();
        Slider sliderDash = maincontroller.getSliderDashSpace();
        TextField txtDash = maincontroller.getTextFieldDashSpace();
        Slider dashWidth = maincontroller.getSliderDashWidth();
        TextField txtDashWidth = maincontroller.getTextFieldDashWidth();
        
        txt.setText(Integer.toString(((Double) shape.getStrokeWidth()).intValue()));
        slider.setValue(shape.getStrokeWidth());
        cbStroke.getSelectionModel().select(indexStrokeType);
        cpShape.setValue((Color) shape.getFill());
        cpStroke.setValue((Color) shape.getStroke());
        
        if(indexStrokeType == 2 && shape.getStrokeDashArray().size() > 1)
        {
            sliderDash.setValue(shape.getStrokeDashArray().get(1));
            txtDash.setText(Integer.toString(shape.getStrokeDashArray().get(1).intValue()));
            dashWidth.setValue(shape.getStrokeDashArray().get(0));
            txtDashWidth.setText(Integer.toString(shape.getStrokeDashArray().get(0).intValue()));
        }
    }
    
    @Override
    public void SelectionOnMousePressedEventHandler(MouseEvent ev) {
        
        if(ev.getButton() == MouseButton.PRIMARY)
        {
            //Enregistrament dels valors de la posició de l'objecte prèvia a l'arrossegament

           origSceneX = ev.getSceneX();
           origSceneY = ev.getSceneY();
           origTranslateX = selection.getTranslateX();
           origTranslateY = selection.getTranslateY();

           //Establir les dimensions originals de l'objecte
           SaveShapeDimensions(selection);

           //Comprobació si click en la zona de edició o en el centre de la figura
           if(SelectionArea(shape, ev))
           {
               isDragEditting = true;
           }
           else
           {
               isTranslating = true;
               selection.setCursor(Cursor.CLOSED_HAND);
           }
        }
    }

    @Override
    public void SelectionOnMouseDraggedEventHandler(MouseEvent ev) {
        //Variables i càlculs per al desplaçament de l'objecte
            double offsetX = ev.getSceneX() - origSceneX;
            double offsetY = ev.getSceneY() - origSceneY;
            double newTranslateX = origTranslateX + offsetX;
            double newTranslateY = origTranslateY + offsetY;
            double newWidth = origXRad + offsetX;
            double newHeight = origYRad + offsetY;
            
            //Comprobació entre desplaçament o edició de tamany
            if(!isDragEditting && isTranslating)
            {
                selection.setCursor(Cursor.CLOSED_HAND);
                selection.setTranslateX(newTranslateX);
                shape.setTranslateX(newTranslateX);
                selection.setTranslateY(newTranslateY);
                shape.setTranslateY(newTranslateY);
            }
            else
            {
                SetShapeDimensions(selection, ev, newWidth, newHeight, newTranslateX, newTranslateY, offsetX, offsetY);
            }
            
            wasDragged = true;
    }

    @Override
    public void SelectionOnMouseButtonReleasedEventHandler(MouseEvent ev) {
        if(ev.getButton() == MouseButton.PRIMARY)
        {
            if(!wasDragged && !isSelected || wasDragged && !isSelected || wasDragged && isSelected)
            {
                isSelected = true;
                updateShapePropertiesOnPropertiesWindow();
            }
            else if(!wasDragged && isSelected)
            {
                isSelected = false;
            }

            //Cambiar el valor a false per a desbloquejar el mode edició en el cas de estar activat.
            isDragEditting = false;
            isTranslating = false;
            selectedEditionEdge = -1;
            wasDragged = false;
        }
    }

    @Override
    public void SelectionOnMouseOverEventHandler(MouseEvent ev) {
        if(!isDragEditting && !isTranslating)
        {
            selection.setStroke(Color.CORNFLOWERBLUE);  
            selection.setStrokeWidth(2);

            //Actualitzar estat del cursor del ratolí
            SelectionArea(selection, ev);
        }
        
        isMouseOver = true;
    }

    @Override
    public void SelectionOnMouseMoveEventHandler(MouseEvent ev) {
        if(!isDragEditting && !isTranslating)
        {
            //Actualitzar estat del cursor del ratolí
            SelectionArea(selection, ev);
        }
    }

    @Override
    public void SelectionOnMouseOutEventHandler(MouseEvent ev) {
        if(!isDragEditting && !isTranslating && !isSelected)
        {
            selection.setStroke(null); 
            selection.setStrokeWidth(0);
            
            //Actualitzar estat del cursor del ratolí
            SelectionArea(selection, ev);
        }
        
        isMouseOver = false;
    }

    @Override
    public boolean SelectionArea(Shape sp, MouseEvent ev) {
        //Definim un àrea de 5px cap a l'interior del quadre que suposa l'area de edició per arrossegament
        boolean costatDret = (ev.getX() >= (selection.getX() + selection.getWidth() - 5) && ev.getX() <= selection.getX() + selection.getWidth());
        boolean costatEsquerre = (ev.getX() >= selection.getX() && ev.getX() <= selection.getX() + 5);
        boolean costatSuperior = (ev.getY() >= selection.getY() && ev.getY() <= selection.getY() + 5);
        boolean costatInferior = (ev.getY() >= (selection.getY() + selection.getHeight()- 5) && ev.getY() <= selection.getY() + selection.getHeight());
        
        
            if(costatDret && costatInferior && !isTranslating)
            {
                selection.setCursor(Cursor.SE_RESIZE);
                selectedEditionEdge = asSE;
                return true;
            }
            else if(costatDret && costatSuperior && !isTranslating)
            {
                selection.setCursor(Cursor.NE_RESIZE);
                selectedEditionEdge = asNE;
                return true;
            }
            else if(costatEsquerre && costatInferior && !isTranslating)
            {
                selection.setCursor(Cursor.SW_RESIZE);
                selectedEditionEdge = asSW;
                return true;
            }
            else if(costatEsquerre && costatSuperior && !isTranslating)
            {
                selection.setCursor(Cursor.NW_RESIZE);
                selectedEditionEdge = asNW;
                return true;
            }
            else
            {
                if(costatDret && !isTranslating)
                {
                    selection.setCursor(Cursor.E_RESIZE);
                    selectedEditionEdge = asE;
                    return true;
                }
                else if (costatEsquerre && !isTranslating)
                {
                    selection.setCursor(Cursor.W_RESIZE);
                    selectedEditionEdge = asW;
                    return true;
                }
                else if(costatSuperior && !isTranslating)
                {
                    selection.setCursor(Cursor.N_RESIZE);
                    selectedEditionEdge = asN;
                    return true;
                }
                else if(costatInferior && !isTranslating)
                {
                    selection.setCursor(Cursor.S_RESIZE);
                    selectedEditionEdge = asS;
                    return true;
                }
                else
                {
                    selectedEditionEdge = -1;
                    selection.setCursor(Cursor.OPEN_HAND);
                }
            }

            return false;
    }

    @Override
    public void SaveShapeDimensions(Shape sp) {       
        origXRad = shape.getRadiusX();
        origYRad = shape.getRadiusY();
    }

    @Override
    public void SetShapeDimensions(Shape sp, MouseEvent ev, double width, double height, double translateX, double translateY, double offsetX, double offsetY) {
        //La modificació de tamany és realitza per mitjà del valor que estiga assignat al camp selectedEditonEdge
        //De esta forma el comportament del redimensionament ve unit a la situació de operació del ratolí, encara que el 
        //punter no estiga dins la figura. Només finalitza la operació, quant l'usuari solta el botó del ratolí.
        
        if(selectedEditionEdge == asSE && isDragEditting && !isTranslating)//Cantó inferior dret, arrossegament cap a la dreta i cap a baix
        {
            //Incrementar el tamany amb l'increment de la diferència amb el punt d'inici de l'arrossegament
            selection.setWidth(width * 2); //Axe X
            shape.setRadiusX(width); //Axe X
            shape.setCenterX(selection.getX() + (selection.getWidth() / 2));
            selection.setHeight(height * 2); //Axe Y
            shape.setRadiusY(height); //Axe Y
            shape.setCenterY(selection.getY() + (selection.getHeight() / 2));
        }
        else if(selectedEditionEdge == asSW && isDragEditting && !isTranslating)
        {
            //Axe X
            double newWidth = width + (offsetX * (-2));
            selection.setWidth(newWidth * 2);
            shape.setRadiusX(newWidth);
            selection.setTranslateX(translateX);
            shape.setCenterX(selection.getX() + (selection.getWidth() / 2));
            shape.setTranslateX(translateX);
            
            //Axe Y
            selection.setHeight(height * 2);
            shape.setRadiusY(height);
            shape.setCenterY(selection.getY() + (selection.getHeight() / 2));
        }
        else if(selectedEditionEdge == asNW && isDragEditting && !isTranslating)
        {
            //Axe X
            double newWidth = width + (offsetX * (-2));
            selection.setWidth(newWidth * 2);
            shape.setRadiusX(newWidth);
            selection.setTranslateX(translateX);
            shape.setCenterX(selection.getX() + (selection.getWidth() / 2));
            shape.setTranslateX(translateX);
            
            //Axe Y
           double newHeight = height +(offsetY * (-2));
           selection.setHeight(newHeight * 2);
           shape.setRadiusY(newHeight);
           selection.setTranslateY(translateY);
           shape.setCenterY(selection.getY() + (selection.getHeight() / 2));
           shape.setTranslateY(translateY);
        }
        else if(selectedEditionEdge == asNE && isDragEditting && !isTranslating)
        {
            //Axe X
            selection.setWidth(width * 2);
            shape.setRadiusX(width);
            shape.setCenterX(selection.getX() + (selection.getWidth() / 2));
            
            //Axe Y
            double newHeight = height +(offsetY * (-2));
           selection.setHeight(newHeight * 2);
           shape.setRadiusY(newHeight);
           selection.setTranslateY(translateY);
           shape.setCenterY(selection.getY() + (selection.getHeight() / 2));
           shape.setTranslateY(translateY);
        }
        else if(selectedEditionEdge == asE && isDragEditting && !isTranslating) //Àrea dreta, arrossegament lateral dret
        {
            selection.setWidth(width * 2);
            shape.setRadiusX(width);
            shape.setCenterX(selection.getX() + (selection.getWidth() / 2));
        }
        else if(selectedEditionEdge == asS && isDragEditting && !isTranslating)//Àrea inferior, arrossegament cap a baix
        {
            selection.setHeight(height * 2);
            shape.setRadiusY(height);
            shape.setCenterY(selection.getY() + (selection.getHeight() / 2));
        }
        else if(selectedEditionEdge == asW && isDragEditting && !isTranslating)
        {
            //Càlcul per a eixamplar el recuadre per la dreta (rebem valors negatius)
            double newWidth = width + (offsetX * (-2));
            
            //Establim la nova amplària per la dreta i desplacem el recuadre cap a la esquerra
            //per a compensar el despla�ament que provoca el augment de grand�ria
            selection.setWidth(newWidth * 2);
            shape.setRadiusX(newWidth);
            selection.setTranslateX(translateX);
            shape.setCenterX(selection.getX() + (selection.getWidth() / 2));
            shape.setTranslateX(translateX);
        }
        else if(selectedEditionEdge == asN && isDragEditting && !isTranslating)
        {
            //Mateix principi que per al cas de l'axe X però adaptat a l'axe Y
            double newHeight = height +(offsetY * (-2));
            
            selection.setHeight(newHeight * 2);
            shape.setRadiusY(newHeight);
            selection.setTranslateY(translateY);
            shape.setCenterY(selection.getY() + (selection.getHeight() / 2));
            shape.setTranslateY(translateY);
        }
    }

    @Override
    public void DrawSelectionArea(double width, double height) {
        selection = new Rectangle( 70, 70, width * 2, height * 2); //Color transparent
        selection.setFill(new Color(0,0,0,0));
        
        //Assignació dels events a la capa de selecció
        selection.setOnMousePressed(this::SelectionOnMousePressedEventHandler);
        selection.setOnMouseDragged(this::SelectionOnMouseDraggedEventHandler);
        selection.setOnMouseEntered(this::SelectionOnMouseOverEventHandler);
        selection.setOnMouseExited(this::SelectionOnMouseOutEventHandler);
        selection.setOnMouseReleased(this::SelectionOnMouseButtonReleasedEventHandler);
        selection.setOnMouseMoved(this::SelectionOnMouseMoveEventHandler);
    }

    @Override
    public Shape getShape() {
        return shape;
    }
    
    public int getIndexStrokeType() { return indexStrokeType; }
    public void setIndexStrokeType(int index){ indexStrokeType = index; }
    
    @Override
    public HashMap<String, Double> getShapeDimensions() 
    {
        HashMap<String, Double> mhm = new HashMap<>();
        
        mhm.put("x", shape.getCenterX() - shape.getRadiusX());
        mhm.put("y", shape.getCenterY() - shape.getRadiusY());
        mhm.put("translateX", shape.getTranslateX());
        mhm.put("translateY", shape.getTranslateY());
        
        return mhm;
    }
    
    @Override
    public void setShapeSelected(boolean selected) {
        isSelected = selected;
    }
}
