/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UIControls;

import Controllers.mainController;
import Shapes.sscEllipse;
import Shapes.sscRectangle;
import javafx.scene.control.Tab;
import Shapes.sscShape;
import Shapes.sscText;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 *
 * @author Guillem
 */
public class sscTab extends Tab{
    
    //List of all images drawed inside this tab
    private ArrayList<sscShape> shapesDrawed;
    
    //The tab UI control to bind in TabPane
    private Tab tab;
    
    //First child node inside tab that contains all other nodes and permits to navigate across contents if it
    //depases edges from control
    private ScrollPane scrPane;
    
    //First child pane inside ScrolPane
    private AnchorPane anchorPane;
    
    //First child node inside AnchorPane that contains all nodes (ImageViewand sscShapes). His function is offer
    //a container for draw all elements inside.
    private Pane pane;
    
    //Node that contains capture image
    private ImageView iViewer;
    
    //Main Controller instance
    private final mainController maincontroller;
    
    //Used for naming shapes
    private Integer shapeCounter = 0;
    
    //Background color for the canvas
    private String backgroundCanvasColor = "#00cc00";
    
    private File fileForSave;
    
    /* Selection area variables */
    private boolean mObjectSelection = false;
    private boolean mEditabeSelection = false;
    private Boolean modeSelection = false;
    private double origX, origY;
    private double origSceneX, origSceneY;
    private double origTranslateX, origTranslateY;
    protected double origWidth, origHeight;
    private boolean isTranslating = false;
    private boolean isSelectionAreaDrawed = false;
    private boolean isDragEditting = false;
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
    private Rectangle selectionArea;
    
    public sscTab(String name, mainController mc)
    {
        super(name);
        
        //Main Controller instance
        maincontroller = mc;
        
        tab = this;
        
        shapesDrawed = new ArrayList<>();
        
        scrPane = new ScrollPane();
        anchorPane = new AnchorPane();
        pane = new Pane();
        
        iViewer = new ImageView();
        
        pane.setStyle(""
                + "-fx-background-color: " + backgroundCanvasColor + ";"
                + "-fx-border-color: #A6A6A6;"
                + "-fx-border-width: 3px;"
                + "-fx-border-style: solid outside"
        );
        
        pane.getChildren().add(iViewer);
        
        AnchorPane.setBottomAnchor(pane, 25d);
        AnchorPane.setLeftAnchor(pane, 25d);
        AnchorPane.setRightAnchor(pane, 25d);
        AnchorPane.setTopAnchor(pane, 25d);
        anchorPane.getChildren().add(pane);
        
        scrPane.setContent(anchorPane);
        tab.setContent(scrPane);
        
        maincontroller.getMenuItemPaste().addEventHandler(ActionEvent.ACTION, onContextMenuItemPasteActionEvent);
        maincontroller.getContextMenuShapeOptions().setOnShowing(this::onContextMenuShowingEvent);
        
        /** Events for selection mode **/
        //Creating and configuring selection area rectangle
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
        
        //Assigning myPane event handlers
        pane.addEventHandler(MouseEvent.MOUSE_PRESSED, onPaneMouseButtonPressedEvent);
        pane.addEventHandler(MouseEvent.MOUSE_DRAGGED, onPaneMouseDraggedEvent);
        pane.addEventHandler(MouseEvent.MOUSE_RELEASED, onPaneMouseButtonReleasedEvent);
              
        //Assigning maincontroller event handlers
        maincontroller.getMenuItemCancelSelection().addEventHandler(ActionEvent.ACTION, onCMICancelSelection);
    }
    
     public void onContextMenuShowingEvent(Event event) {
        if(maincontroller.getImageWriterImageCopied() != null)
        {
            maincontroller.getMenuItemPaste().setDisable(false);
        }
    }
    
    EventHandler<ActionEvent> onContextMenuItemPasteActionEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            PerformPasteActionFromEvent();
        }
    };
    
    /**
     * This methods paste an image that has copied in the image viewer of the selected tab
     */
    private void PerformPasteActionFromEvent()
    {
        if(maincontroller.getImageWriterImageCopied() != null && maincontroller.getTabPane().getSelectionModel().getSelectedItem() == this)
        {
            iViewer.setImage(maincontroller.getImageWriterImageCopied());
        }
    }
    
    /**
     * Start a selection mode that permits to select objects in the canvas
     */
    public void StartSelectionMode()
    {
        if(!modeSelection){
            modeSelection = true;
        
            mObjectSelection = true;
            mEditabeSelection = false;
            
            selectionArea.setStroke(Color.CORNFLOWERBLUE);
            selectionArea.setStrokeWidth(2);
        }
    }
    
    /**
     * Select all objects in canvas that are contained in the area defined by params
     * 
     * @param x
     * @param y
     * @param width
     * @param height 
     */
    public void SelectObjects(Double x, Double y, Double width, Double height)
    {
        for(int i = 0; i < shapesDrawed.size(); i++)
        {
            sscShape shape = (sscShape) shapesDrawed.get(i);
            
            HashMap<String, Double> dimensions = shape.getShapeDimensions();
            
            Double shapeX = dimensions.get("x") + dimensions.get("translateX");
            Double shapeY = dimensions.get("y") + dimensions.get("translateY");
            
            if((shapeX >= x && shapeX <= (x + width)) && (shapeY >= y && shapeY <= (y + height)))
            {
                System.out.println("Dins");
                shape.setShapeSelected(true);
            }
            
            System.out.println("Pasa");
        }
    }
    
    /**
     * Start a selection mode that permits to crop the background image
     */
    public void StartCropMode()
    {
        if(!modeSelection){
            modeSelection = true;
        
            mObjectSelection = false;
            mEditabeSelection = true;
            
            selectionArea.setStroke(Color.CORNFLOWERBLUE);
            selectionArea.setStrokeWidth(2);
            
            maincontroller.getMenuItemCopy().addEventHandler(ActionEvent.ACTION, onCMICopyActionEvent);
        }
    }
    
    public void CreateNewRectangle()
    {
        if(modeSelection){
            //Finish selection mode
            unsetModeSelection();
        }
        sscRectangle rt = new sscRectangle(70, 70, this, maincontroller, "Rectangle " + shapeCounter.toString());
        
        shapeCounter++;
        
        shapesDrawed.add(rt);
    }
    
    public void CreateNewText()
    {
        if(modeSelection){
            //Finish selection mode
            unsetModeSelection();
        }
        
        sscText tx = new sscText(this, maincontroller);
        
        shapeCounter++;
        
        shapesDrawed.add(tx);
    }
    
    public void CreateNewEllipse()
    {
        if(modeSelection){
            //Finish selection mode
            unsetModeSelection();
        }
        
        sscEllipse cr = new sscEllipse(70, 70, this, maincontroller, "Test");
        
        shapeCounter++;
        
        shapesDrawed.add(cr);
    }
    
    
    public void ModifyShapesOrderInPane(Shape shape, Shape selection, String op)
    {
        int childrensSize = pane.getChildren().size();
        int shapeIndex = pane.getChildren().indexOf(shape);
        int selectionIndex = pane.getChildren().indexOf(selection);
        int newShapeIndex = 0;
        int newSelectionIndex = 0;
        
        if(shapeIndex > 0 && selectionIndex > 1 && shapeIndex < (childrensSize - 1) && selectionIndex < childrensSize)
        {
            switch(op)
            {
                //Send backward
                case "backward":
                    newShapeIndex = shapeIndex - 2;
                    newSelectionIndex = selectionIndex - 2;
                    break;
                //Bring forward
                case "forward":
                    newShapeIndex = shapeIndex + 2;
                    newSelectionIndex = selectionIndex + 2;
                    break;
                //Bring to front
                case "front":
                    newShapeIndex = childrensSize - 2;
                    newSelectionIndex = childrensSize - 1;
                    break;
                //Send to back
                case "back":
                    newShapeIndex = 1;
                    newSelectionIndex = 2;
                    break;
                default:
                    return;
            }
            
            if(newShapeIndex > 0 && newSelectionIndex > 1 && newShapeIndex < (childrensSize - 1) && newSelectionIndex < childrensSize)
            {
                pane.getChildren().removeAll(shape, selection);
                ArrayList<Node> newOrdered = new ArrayList<>();
                
                if(pane.getChildren().size() == (childrensSize - 2))
                {
                    //Copy node 0 that corresponds with the background image.
                    newOrdered.add(pane.getChildren().get(0));
                    int mod = 0;
                    int counter = 1;
                    
                    for(int i = 1; i < childrensSize; i++)
                    {
                        mod = i % 2;
                        
                        if(mod != 0 && newShapeIndex == i)
                        {
                            newOrdered.add(shape);
                        }
                        else if(mod == 0 && newSelectionIndex == i)
                        {
                            newOrdered.add(selection);
                        }
                        else
                        {
                            if(counter < pane.getChildren().size())
                            {
                                newOrdered.add(pane.getChildren().get(counter));
                            }
                            
                            counter++;
                        }
                    }
                    
                    if(newOrdered.size() == childrensSize)
                    {
                        if(newOrdered.indexOf(shape) == newShapeIndex && newOrdered.indexOf(selection) == newSelectionIndex)
                        {
                            pane.getChildren().setAll(newOrdered);
                        }
                        else
                        {
                            System.out.println("Error, shape and selection indexes don't match");
                            return;
                        }
                    }
                    else
                    {
                        System.out.println("Error, size of arrays don't match");
                        return;
                    }
                }
                else
                {
                    System.out.println("Error removing elements from array");
                    return;
                }
                
            }
            else
            {
                System.out.println("Out of bounds 2nd");
                return;
            }
            
        }
        else
        {
            System.out.println("Out of bounds 1st");
            return;
        }
    }
    
    /**
     * Get the ImageView property that contains capture image
     * @return 
     */
    public ImageView getImageView(){ return iViewer; }
    
    /**
     * Get the Pane object that contains all drawed objects. It is included inside the ScrollPane.
     * @return 
     */
    public Pane getPane (){ return pane; }
    
    /**
     * Get the tab containing all nodes inside. It is the root node of this component.
     * @return 
     */
    public Tab getTab(){ return tab; }
    
    /**
     * Get the Scroll Pane that contains all the nodes inside. It is included inside the tab.
     * @return 
     */
    public ScrollPane getScrollPane(){ return scrPane; }
    
    /**
     * Get an array list containin all the shapes drawed in the pane
     * @return 
     */
    public ArrayList<sscShape> getShapesDrawed(){ return shapesDrawed; }
    
    public boolean isInSelectionMode(){ return modeSelection; }
    
    public void setImageViewerImage(WritableImage wi){ if(wi != null){ iViewer.setImage(wi); }}
    public String getBackgroundCanvasColor(){ return backgroundCanvasColor; }
    
    EventHandler<MouseEvent> onPaneMouseButtonPressedEvent = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent ev) 
        {
            if(ev.getButton() == MouseButton.PRIMARY)
            {
                panelMouseButtonPresed(ev);
            }
        }
    };
    
    private void panelMouseButtonPresed(MouseEvent ev){
        if(modeSelection && !isSelectionAreaDrawed)
        {
            //Creating and configuring the rectangle that performs the selection area
            origX = ev.getX();
            origY = ev.getY();

            selectionArea.setX(origX);
            selectionArea.setY(origY);


            pane.getChildren().add(selectionArea);
        }
    }    
    EventHandler<MouseEvent> onPaneMouseDraggedEvent = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent ev) 
        {
            if(ev.getButton() == MouseButton.PRIMARY)
            {
                if(modeSelection && !isSelectionAreaDrawed)
                {
                    double width = ev.getX() - origX;
                    double height = ev.getY() - origY;

                    selectionArea.setWidth(width);
                    selectionArea.setHeight(height);
                }
            }
        }
    };
    
    EventHandler<MouseEvent> onPaneMouseButtonReleasedEvent = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if(modeSelection && !isSelectionAreaDrawed)
            {
                if(mEditabeSelection){
                    myPaneButtonReleasedEditableAreaMode();
                } else {
                    myPaneButtonReleasedObjectSelectionMode();
                }
            }
        }
    };
    
    EventHandler<ActionEvent> onCMICancelSelection = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            //Finish selection mode
            unsetModeSelection();
        }
    };
    
    private void myPaneButtonReleasedEditableAreaMode(){
        selectionArea.getStrokeDashArray().setAll(4d,4d);
                
        isSelectionAreaDrawed = true;

        maincontroller.getMenuItemCopy().setDisable(false);
        maincontroller.getMenuItemCancelSelection().setDisable(false);
    }
    
    private void myPaneButtonReleasedObjectSelectionMode(){
        Double x = selectionArea.getX();
        Double y = selectionArea.getY();
        Double width = selectionArea.getWidth();
        Double height = selectionArea.getHeight();

        //Finish selection mode
        unsetModeSelection();
        
        SelectObjects(x, y, width, height);
    }
    
    EventHandler<ActionEvent> onCMICopyActionEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if(modeSelection && mEditabeSelection && isSelectionAreaDrawed)
            {
                PixelReader pr = iViewer.getImage().getPixelReader();
                
                int x = ((Double) selectionArea.getX()).intValue() + ((Double) selectionArea.getTranslateX()).intValue();
                int y = ((Double) selectionArea.getY()).intValue() + ((Double) selectionArea.getTranslateY()).intValue();
                int width = ((Double) selectionArea.getWidth()).intValue();
                int height = ((Double) selectionArea.getHeight()).intValue();
                
                WritableImage cropedIm = new WritableImage(pr, x, y, width, height);
                
                maincontroller.setImageWriterImageCopied(cropedIm);
                
                //Finish selection mode
                unsetModeSelection();
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
            if(!isDragEditting && mEditabeSelection && !isTranslating)
            {
                SelectionArea(event);
            }
        }
    };
    
    EventHandler<MouseEvent> onSelectionAreaMouseExit = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent ev) {
            if(!isDragEditting && mEditabeSelection && !isTranslating)
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
            if(!isDragEditting && mEditabeSelection && !isTranslating)
            {
                SelectionArea(event);
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
    
    EventHandler<MouseEvent> onSelectionAreaMouseDragged = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent ev) {
            
            if(modeSelection && mEditabeSelection && isSelectionAreaDrawed)
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
    
    EventHandler<MouseEvent> onSelectionAreaMouseButtonPressed = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent ev) {
            if(ev.getButton() == MouseButton.PRIMARY)
            {
                if(modeSelection && mEditabeSelection && isSelectionAreaDrawed)
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
    
    /**
     * Unset all configurations and values of the selection mode, selection area
     * and puts the tab in draw mode
     */
    private void unsetModeSelection(){
        
        pane.getChildren().remove(selectionArea);

        //Unset all selection area configs
        selectionArea.setX(0);
        selectionArea.setY(0);
        selectionArea.setTranslateX(0);
        selectionArea.setTranslateY(0);
        selectionArea.setWidth(0);
        selectionArea.setHeight(0);
        selectionArea.getStrokeDashArray().clear();

        isSelectionAreaDrawed = false;
        modeSelection = false;

        mObjectSelection = false;
        mEditabeSelection = false;

        isDragEditting = false;
        isTranslating = false;

        //Unselect buttons
        maincontroller.getButtonSelectionTool().setSelected(false);
        maincontroller.getButtonCropImage().setSelected(false);
    }
    
    /**
     * Return the file that is used for save the snapshot image of the canvas.
     * Formerly the image that user had drawed and saved to disk. Return a valid
     * File object or null if is not set yet.
     * @return 
     */
    public File getFileForSave() { return fileForSave; }
    
    /**
     * Set the value for the fileForSave atribute that is used for store the location
     * of the image that is drawed by the user. Is the exported image.
     * @param fileForSave 
     */
    public void setFileForSave(File fileForSave) { this.fileForSave = fileForSave; }
}
