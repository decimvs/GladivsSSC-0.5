/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Shapes;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape;

/**
 *
 * @author Guillem
 */
public interface sscShape {
    
    /**
     * TOTS ESTOS SON EVENTS PER A LA CAPA DE SELECCIÓ
     * Es produix quant el ratolí passa per damunt de l'objecte.
     * Deu definirse en el objecte selection.setOnMousePressed(this::SelectionOnMousePressedEventHandler);
     * 
     * El procediment és per a tots els eventhandlers de la mateixa forma adaptant-los a l'event que gestionen
     * @param ev 
     */
    public void SelectionOnMousePressedEventHandler(MouseEvent ev);
    
    public void SelectionOnMouseDraggedEventHandler(MouseEvent ev);
    
    public void SelectionOnMouseButtonReleasedEventHandler(MouseEvent ev);
    
    public void SelectionOnMouseOverEventHandler(MouseEvent ev);
    
    public void SelectionOnMouseMoveEventHandler(MouseEvent ev);
    
    public void SelectionOnMouseOutEventHandler(MouseEvent ev);
    
    public boolean SelectionArea(javafx.scene.shape.Shape sp, MouseEvent ev);
    
    public void SaveShapeDimensions(javafx.scene.shape.Shape sp);
    
    public void SetShapeDimensions(javafx.scene.shape.Shape sp, MouseEvent ev, double width, double height, double translateX, double translateY, double offsetX, double offsetY);
    
    public void DrawSelectionArea(double width, double height);
    
    public Shape getShape();
    
}
