/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UIControls;

import Controllers.mainController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Guillem
 */
public class SelectionArea {
    
    private Rectangle selectionArea;
    private sscTab myTab;
    private Pane myPane;
    private double origX, origY;
    private double origSceneX, origSceneY;
    private double origTranslateX, origTranslateY;
    protected double origWidth, origHeight;
    private boolean isTranslating = false;
    private boolean isSelectionAreaDrawed = false;
    private boolean isDragEditting = false;
    
    mainController maincontroller;
    
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
    
    public SelectionArea(sscTab tab, mainController maincontroller)
    {
        myTab = tab;
        myPane = tab.getPane();
        
        this.maincontroller = maincontroller;
        
        selectionArea = new Rectangle(0,0,0,0);
        
        selectionArea.setFill(new Color(1, 1 ,1, 0.2));
        selectionArea.setStroke(Color.CORNFLOWERBLUE);
        selectionArea.setStrokeWidth(2);

        //Assigning selectionArea event handlers
        selectionArea.addEventFilter(MouseEvent.MOUSE_ENTERED, onSelectionAreaMouseEnterEvent);
        selectionArea.addEventFilter(MouseEvent.MOUSE_MOVED, onSelectionAreaMouseMoveEvent);
        selectionArea.addEventFilter(MouseEvent.MOUSE_RELEASED, onSelectionAreaButtonReleasedEvent);
        selectionArea.addEventFilter(MouseEvent.MOUSE_DRAGGED, onSelectionAreaMouseDragged);
        selectionArea.addEventFilter(MouseEvent.MOUSE_PRESSED, onSelectionAreaMouseButtonPressed);
        
        //Event handler assign
        myPane.addEventHandler(MouseEvent.MOUSE_PRESSED, onPaneMouseButtonPressedEvent);
        myPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, onPaneMouseDraggedEvent);
        myPane.addEventHandler(MouseEvent.MOUSE_RELEASED, onPaneMouseButtonReleasedEvent);
        
        maincontroller.getMenuItemCopy().addEventHandler(ActionEvent.ACTION, onCMICopyActionEvent);
        maincontroller.getMenuItemCancelSelection().addEventHandler(ActionEvent.ACTION, onCMICancelSelection);
    }
    
    EventHandler<ActionEvent> onCMICancelSelection = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            myPane.getChildren().remove(selectionArea);
            
            selectionArea.setX(0);
            selectionArea.setY(0);
            selectionArea.setWidth(0);
            selectionArea.setHeight(0);
            
            isSelectionAreaDrawed = false;
            myTab.setSelectionMode(false);
            
            isDragEditting = false;
            isTranslating = false;
        }
    };
    
    EventHandler<ActionEvent> onCMICopyActionEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if(myTab.isInSelectionMode() && isSelectionAreaDrawed)
            {
                PixelReader pr = myTab.getImageView().getImage().getPixelReader();
                
                int x = ((Double) selectionArea.getX()).intValue();
                int y = ((Double) selectionArea.getY()).intValue();
                int width = ((Double) selectionArea.getWidth()).intValue();
                int height = ((Double) selectionArea.getHeight()).intValue();
                
                WritableImage cropedIm = new WritableImage(pr, x, y, width, height);
                
                maincontroller.setImageWriterImageCopied(cropedIm);
            }
        }
    };
    
    /**
     * Calculates the edges for the selection area to respond to mouse over events. 
     * Stores the edege that the mouse is over.
     * 
     * Return true if the mouse pointer is over the selection area, false if it's out
     * of the selection area.
     * 
     * @param sp Shape that get the calculated area
     * @param ev Mouse Event relationed with this operation
     * @return 
     */
    public boolean SelectionArea(MouseEvent ev) {
        
        //Definim un àrea de 5px cap a l'interior del quadre que suposa l'area de edició per arrossegament
        boolean costatDret = (ev.getX() >= (selectionArea.getX() + selectionArea.getWidth() - 5) && ev.getX() <= selectionArea.getX() + selectionArea.getWidth());
        boolean costatEsquerre = (ev.getX() >= selectionArea.getX() && ev.getX() <= selectionArea.getX() + 5);
        boolean costatSuperior = (ev.getY() >= selectionArea.getY() && ev.getY() <= selectionArea.getY() + 5);
        boolean costatInferior = (ev.getY() >= (selectionArea.getY() + selectionArea.getHeight()- 5) && ev.getY() <= selectionArea.getY() + selectionArea.getHeight());
        
        
            if(costatDret && costatInferior && !isTranslating)
            {
                selectionArea.setCursor(Cursor.SE_RESIZE);
                selectedEditionEdge = asSE;
                return true;
            }
            else if(costatDret && costatSuperior && !isTranslating)
            {
                selectionArea.setCursor(Cursor.NE_RESIZE);
                selectedEditionEdge = asNE;
                return true;
            }
            else if(costatEsquerre && costatInferior && !isTranslating)
            {
                selectionArea.setCursor(Cursor.SW_RESIZE);
                selectedEditionEdge = asSW;
                return true;
            }
            else if(costatEsquerre && costatSuperior && !isTranslating)
            {
                selectionArea.setCursor(Cursor.NW_RESIZE);
                selectedEditionEdge = asNW;
                return true;
            }
            else
            {
                if(costatDret && !isTranslating)
                {
                    selectionArea.setCursor(Cursor.E_RESIZE);
                    selectedEditionEdge = asE;
                    return true;
                }
                else if (costatEsquerre && !isTranslating)
                {
                    selectionArea.setCursor(Cursor.W_RESIZE);
                    selectedEditionEdge = asW;
                    return true;
                }
                else if(costatSuperior && !isTranslating)
                {
                    selectionArea.setCursor(Cursor.N_RESIZE);
                    selectedEditionEdge = asN;
                    return true;
                }
                else if(costatInferior && !isTranslating)
                {
                    selectionArea.setCursor(Cursor.S_RESIZE);
                    selectedEditionEdge = asS;
                    return true;
                }
                else
                {
                    selectedEditionEdge = -1;
                    selectionArea.setCursor(Cursor.OPEN_HAND);
                }
            }
            
            return false;
    }
    
    EventHandler<MouseEvent> onSelectionAreaMouseMoveEvent = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) 
        {
            if(!isDragEditting && !isTranslating)
            {
                SelectionArea(event);
            }
        }
    };
    
    EventHandler<MouseEvent> onSelectionAreaMouseExit = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent ev) {
            if(!isDragEditting && !isTranslating)
            {
                //Actualitzar estat del cursor del ratolí
                SelectionArea(ev);
            }
        }
    };
    
    EventHandler<MouseEvent> onSelectionAreaMouseEnterEvent = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) 
        {
            if(!isDragEditting && !isTranslating)
            {
                SelectionArea(event);
            }
        }
    };
    
    EventHandler<MouseEvent> onPaneMouseButtonReleasedEvent = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if(myTab.isInSelectionMode() && !isSelectionAreaDrawed)
            {
                selectionArea.getStrokeDashArray().setAll(4d,4d);
                
                isSelectionAreaDrawed = true;
                
                maincontroller.getMenuItemCopy().setDisable(false);
                maincontroller.getMenuItemCancelSelection().setDisable(false);
            }
        }
    };
    
    EventHandler<MouseEvent> onSelectionAreaButtonReleasedEvent = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            isDragEditting = false;
            isTranslating = false;
            selectedEditionEdge = -1;
        }
    };
    
    EventHandler<MouseEvent> onPaneMouseDraggedEvent = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent ev) 
        {
            if(ev.getButton() == MouseButton.PRIMARY)
            {
                if(myTab.isInSelectionMode() && !isSelectionAreaDrawed)
                {
                    double width = ev.getX() - origX;
                    double height = ev.getY() - origY;

                    selectionArea.setWidth(width);
                    selectionArea.setHeight(height);
                }
            }
        }
    };
    
    EventHandler<MouseEvent> onSelectionAreaMouseDragged = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent ev) {
            
            if(myTab.isInSelectionMode() && isSelectionAreaDrawed)
            {
                double offsetX = ev.getSceneX() - origSceneX;
                double offsetY = ev.getSceneY() - origSceneY;
                double newTranslateX = origTranslateX + offsetX;
                double newTranslateY = origTranslateY + offsetY;
                double newWidth = origWidth + offsetX;
                double newHeight = origHeight + offsetY;

                if(!isDragEditting && isTranslating)
                {
                    selectionArea.setCursor(Cursor.CLOSED_HAND);
                    selectionArea.setTranslateX(newTranslateX);
                    selectionArea.setTranslateY(newTranslateY);
                }
                else
                {
                    SetShapeDimensions(ev, newWidth, newHeight, newTranslateX, newTranslateY, offsetX, offsetY);
                }
            }
        }
        
    };
    
    EventHandler<MouseEvent> onPaneMouseButtonPressedEvent = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent ev) 
        {
            if(ev.getButton() == MouseButton.PRIMARY)
            {
                if(myTab.isInSelectionMode() && !isSelectionAreaDrawed)
                {
                    origX = ev.getX();
                    origY = ev.getY();

                    selectionArea.setX(origX);
                    selectionArea.setY(origY);
                    

                    myPane.getChildren().add(selectionArea);
                }
            }
        }
    };
    
    EventHandler<MouseEvent> onSelectionAreaMouseButtonPressed = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent ev) {
            if(ev.getButton() == MouseButton.PRIMARY)
            {
                if(myTab.isInSelectionMode() && isSelectionAreaDrawed)
                {
                    origSceneX = ev.getSceneX();
                    origSceneY = ev.getSceneY();
                    origTranslateX = selectionArea.getTranslateX();
                    origTranslateY = selectionArea.getTranslateY();
                    origWidth = selectionArea.getWidth();
                    origHeight = selectionArea.getHeight();
                    
                    if(SelectionArea(ev))
                    {
                        isDragEditting = true;
                    }
                    else
                    {
                        isTranslating = true;
                        selectionArea.setCursor(Cursor.CLOSED_HAND);
                    }
                }
            }
        }
    };
    
    public void SetShapeDimensions(MouseEvent ev, double width, double height, double translateX, double translateY, double offsetX, double offsetY) 
    {
        //La modificació de tamany és realitza per mitjà del valor que estiga assignat al camp selectedEditonEdge
        //De esta forma el composelectionAreaament del redimensionament ve unit a la situació de operació del ratolí, encara que el 
        //punter no estiga dins la figura. Només finalitza la operació, quant l'usuari solta el botó del ratolí.
        
        if(selectedEditionEdge == asSE && isDragEditting && !isTranslating)//Cantó inferior dret, arrossegament cap a la dreta i cap a baix
        {
            //Incrementar el tamany amb l'increment de la diferència amb el punt d'inici de l'arrossegament
            selectionArea.setWidth(width); //Axe X
            selectionArea.setHeight(height); //Axe Y
        }
        else if(selectedEditionEdge == asSW && isDragEditting && !isTranslating)
        {
            //Axe X
            double newWidth = width + (offsetX * (-2));
            selectionArea.setWidth(newWidth);
            selectionArea.setTranslateX(translateX);
            
            //Axe Y
            selectionArea.setHeight(height);
        }
        else if(selectedEditionEdge == asNW && isDragEditting && !isTranslating)
        {
            //Axe X
            double newWidth = width + (offsetX * (-2));
            selectionArea.setWidth(newWidth);
            selectionArea.setTranslateX(translateX);
            
            //Axe Y
           double newHeight = height +(offsetY * (-2));
           selectionArea.setHeight(newHeight);
           selectionArea.setTranslateY(translateY);
        }
        else if(selectedEditionEdge == asNE && isDragEditting && !isTranslating)
        {
            //Axe X
            selectionArea.setWidth(width);
            
            //Axe Y
            double newHeight = height +(offsetY * (-2));
           selectionArea.setHeight(newHeight);
           selectionArea.setTranslateY(translateY);
        }
        else if(selectedEditionEdge == asE && isDragEditting && !isTranslating) //�rea dreta, arrossegament lateral dret
        {
            selectionArea.setWidth(width);
        }
        else if(selectedEditionEdge == asS && isDragEditting && !isTranslating)//�rea inferior, arrossegament cap a baix
        {
            selectionArea.setHeight(height);
        }
        else if(selectedEditionEdge == asW && isDragEditting && !isTranslating)
        {
            //Càlcul per a eixamplar el recuadre per la dreta (rebem valors negatius)
            double newWidth = width + (offsetX * (-2));
            
            //Establim la nova amplària per la dreta i desplacem el recuadre cap a la esquerra
            //per a compensar el despla�ament que provoca el augment de grand�ria
            selectionArea.setWidth(newWidth);
            selectionArea.setTranslateX(translateX);
        }
        else if(selectedEditionEdge == asN && isDragEditting && !isTranslating)
        {
            //Mateix principi que per al cas de l'axe X però adaptat a l'axe Y
            double newHeight = height +(offsetY * (-2));
            
            selectionArea.setHeight(newHeight);
            selectionArea.setTranslateY(translateY);
        }
    }
}
