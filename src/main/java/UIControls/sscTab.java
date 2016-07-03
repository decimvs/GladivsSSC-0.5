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
import java.util.ArrayList;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Guillem
 */
public class sscTab extends Tab{
    
    //List of all images drawed inside this tab
    private ArrayList<sscShape> shapesDrawed;
    
    //List of all shapes selected
    private ArrayList<sscShape> selectedShapes;
    
    //The tab UI control to bind in TabPane
    private Tab tab;
    
    //First child node inside tab that contains all other nodes and permits to navigate across contents if it
    //depases edges from control
    ScrollPane scrPane;
    
    //First child node inside ScrollPane that contains all nodes (ImageViewand sscShapes). His function is offer
    //a container for draw all elements inside.
    private Pane pane;
    
    //Node that contains capture image
    private ImageView iViewer;
    
    //Main Controller instance
    private final mainController maincontroller;
    
    private double origX, origY;
    
    private Rectangle selectionArea;
    
    private Integer contadorFiguras = 0;
    
    public sscTab(String name, mainController mc)
    {
        super(name);
        
        //Main Controller instance
        maincontroller = mc;
        
        tab = this;
        
        shapesDrawed = new ArrayList<>();
        
        scrPane = new ScrollPane();
        
        pane = new Pane();
        
        iViewer = new ImageView();
        
        pane.getChildren().add(iViewer);
        scrPane.setContent(pane);
        tab.setContent(scrPane);
        
        pane.setOnMousePressed(this::paneOnMouseButtonPressed);
        pane.setOnMouseDragged(this::paneOnMouseDragged);
        pane.setOnMouseReleased(this::paneOnMouseButtonReleased);
    }
    
    public void CreateNewRectangle()
    {
        sscRectangle rt = new sscRectangle(70, 70, this.getPane(), maincontroller, "Rectangle " + contadorFiguras.toString());
        
        contadorFiguras++;
        
        this.getShapesDrawed().add(rt);
    }
    
    public void CreateNewText()
    {
        sscText tx = new sscText(this.pane, maincontroller);
        
        contadorFiguras++;
        
        getShapesDrawed().add(tx);
    }
    
    public void CreateNewEllipse()
    {
        sscEllipse cr = new sscEllipse(70, 70, pane, maincontroller, "Test");
        
        contadorFiguras++;
        
        getShapesDrawed().add(cr);
    }
    
    /**
     * Get the ImageView property that contains capture image
     * @return 
     */
    public ImageView getImageView()
    {
        return iViewer;
    }
    
    /**
     * Get the Pane object that contains all drawed objects. It is included inside the ScrollPane.
     * @return 
     */
    public Pane getPane ()
    {
        return pane;
    }
    
    /**
     * Get the tab containing all nodes inside. It is the root node of this component.
     * @return 
     */
    public Tab getTab()
    {
        return tab;
    }
    
    /**
     * Get the Scroll Pane that contains all the nodes inside. It is included inside the tab.
     * @return 
     */
    public ScrollPane getScrollPane()
    {
        return scrPane;
    }
    
    /**
     * Get an array list containin all the shapes drawed in the pane
     * @return 
     */
    public ArrayList<sscShape> getShapesDrawed()
    {
        return shapesDrawed;
    }
    
    /**
     * Get a list that contains all the selected shapes
     * @return 
     */
    public ArrayList<sscShape> getSelectedShapes()
    {
        return selectedShapes;
    }
    
    public void paneOnMouseButtonPressed(MouseEvent ev)
    {
        //origX = ev.getX();
        //origY = ev.getY();
        
        //selectionArea = new Rectangle(origX, origY, 0 , 0);
        //selectionArea.setFill(new Color(0, 0 ,0, 0));
        //selectionArea.setStroke(Color.BLACK);
        //selectionArea.setStrokeWidth(2);
        
        //pane.getChildren().add(selectionArea);
    }
    
    public void paneOnMouseDragged(MouseEvent ev)
    {
        //double width = ev.getX() - origX;
        double height = ev.getY() - origY;
        
        //selectionArea.setWidth(width);
        //selectionArea.setHeight(height);
    }
    
    public void paneOnMouseButtonReleased(MouseEvent ev)
    {
        //pane.getChildren().remove(selectionArea);
    }
}
