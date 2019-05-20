package infovis.diagram;

import infovis.debug.Debug;
import infovis.diagram.elements.DrawingEdge;
import infovis.diagram.elements.Edge;
import infovis.diagram.elements.Element;
import infovis.diagram.elements.GroupingRectangle;
import infovis.diagram.elements.None;
import infovis.diagram.elements.Vertex;
import infovis.diagram.layout.Fisheye;
import infovis.diagram.layout.Layout;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MouseController implements MouseListener,MouseMotionListener {
	 private Model model;
	 private View view;
	 private Element selectedElement = new None();
	 private double mouseOffsetX;
	 private double mouseOffsetY;
	 private boolean edgeDrawMode = false;
	 private DrawingEdge drawingEdge = null;
	 private boolean fisheyeMode;
	 private GroupingRectangle groupRectangle;

	 // checker if mouse in marker rectangle
	 private boolean inMarker = false;

	 // fisheye instance
	 private Layout fisheye = new Fisheye();

	/*
	 * Getter And Setter
	 */
	 public Element getSelectedElement(){
		 return selectedElement;
	 }
    public Model getModel() {
		return model;
	}
	public void setModel(Model diagramModel) {
		this.model = diagramModel;
	}
	public View getView() {
		return view;
	}
	public void setView(View diagramView) {
		this.view = diagramView;
	}
	/*
     * Implements MouseListener
     */
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		double scale = view.getScale();

		if (e.getButton() == MouseEvent.BUTTON3){
			/*
			 * add grouped elements to the model
			 */
			Vertex groupVertex = (Vertex)getElementContainingPosition(x/scale,y/scale);
			for (Iterator<Vertex> iter = groupVertex.getGroupedElements().iteratorVertices();iter.hasNext();){
				model.addVertex(iter.next());
			}
			for (Iterator<Edge> iter = groupVertex.getGroupedElements().iteratorEdges();iter.hasNext();){
				model.addEdge(iter.next());
			}
			/*
			 * remove elements
			 */
			List<Edge> edgesToRemove = new ArrayList<Edge>();
			for (Iterator<Edge> iter = model.iteratorEdges(); iter.hasNext();){
				Edge edge = iter.next();
				if (edge.getSource() == groupVertex || edge.getTarget() == groupVertex){
					edgesToRemove.add(edge);
				}
			}
			model.removeEdges(edgesToRemove);
			model.removeElement(groupVertex);
		}
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		double scale = view.getScale();

		// marker scaled position, with 0.25 we constant speed
		// this has to be 0.25 just like the g2D is 1/4 of the original size
		int x_scale = (int) (x / 0.25);
		int y_scale = (int) (y / 0.25);

		// check if the mouse is in the marker area
		if (view.markerContains(x_scale, y_scale)) {
			inMarker = true;
		} else {
			inMarker = false;
		}

	   if (edgeDrawMode){
			drawingEdge = new DrawingEdge((Vertex)getElementContainingPosition(x/scale,y/scale));
			model.addElement(drawingEdge);
		} else if (fisheyeMode){
			/*
			 * do handle interactions in fisheye mode
			 */
			view.repaint();
		} else {
			
			selectedElement = getElementContainingPosition(x/scale,y/scale);
			/*
			 * calculate offset
			 */
			mouseOffsetX = x - selectedElement.getX() * scale ;
			mouseOffsetY = y - selectedElement.getY() * scale ;	
		}
		
	}
	public void mouseReleased(MouseEvent arg0){
		int x = arg0.getX();
		int y = arg0.getY();
		
		if (drawingEdge != null){
			Element to = getElementContainingPosition(x, y);
			model.addEdge(new Edge(drawingEdge.getFrom(),(Vertex)to));
			model.removeElement(drawingEdge);
			drawingEdge = null;
		}
		if (groupRectangle != null){
		    Model groupedElements = new Model();
			for (Iterator<Vertex> iter = model.iteratorVertices(); iter.hasNext();) {
				Vertex vertex = iter.next();
				if (groupRectangle.contains(vertex.getShape().getBounds2D())){
					Debug.p("Vertex found");
					groupedElements.addVertex(vertex);	
				}
			}
			if (!groupedElements.isEmpty()){
				model.removeVertices(groupedElements.getVertices());
				
				Vertex groupVertex = new Vertex(groupRectangle.getCenterX(),groupRectangle.getCenterX());
				groupVertex.setColor(Color.ORANGE);
				groupVertex.setGroupedElements(groupedElements);
				model.addVertex(groupVertex);
				
				List<Edge> newEdges = new ArrayList(); 
				for (Iterator<Edge> iter = model.iteratorEdges(); iter.hasNext();) {
					Edge edge =  iter.next();
				    if (groupRectangle.contains(edge.getSource().getShape().getBounds2D()) 
				    	&& groupRectangle.contains(edge.getTarget().getShape().getBounds2D())){
				    		groupVertex.getGroupedElements().addEdge(edge);
                            Debug.p("add Edge to groupedElements");	
                            //iter.remove(); // Warum geht das nicht!
				    } else if (groupRectangle.contains(edge.getSource().getShape().getBounds2D())){
				    	groupVertex.getGroupedElements().addEdge(edge);
				    	newEdges.add(new Edge(groupVertex,edge.getTarget()));
				    } else if (groupRectangle.contains(edge.getTarget().getShape().getBounds2D())){
				    	groupVertex.getGroupedElements().addEdge(edge);
				    	newEdges.add(new Edge(edge.getSource(),groupVertex));
				    }
				}
				model.addEdges(newEdges);
				model.removeEdges(groupedElements.getEdges());
			}
			model.removeElement(groupRectangle);
			groupRectangle = null;
		}
		view.repaint();
	}
	
	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		double scale = view.getScale();
		/*
		 * Aufgabe 1.2
		 */
		if (fisheyeMode){
			Model reset = new Model();
			reset.generateTestValues();
			view.setModel(reset);
			fisheye.setMouseCoords(e.getX(), e.getY(), view);
			view.repaint();
		} else if (edgeDrawMode){
			drawingEdge.setX(e.getX());
			drawingEdge.setY(e.getY());
		}else if(selectedElement != null){
			selectedElement.updatePosition((e.getX()-mouseOffsetX)/scale, (e.getY()-mouseOffsetY) /scale);
			int x_offset = (int) ((e.getX()-mouseOffsetX) / 0.25);
			int y_offset = (int) ((e.getY()-mouseOffsetY) / 0.25);
			// if we are dragging and are in the marker rectangle
			if (inMarker) {
				// forward position into the view to adjust g2D
				view.updateTranslation(x_offset, y_offset);
			}
		}
		view.repaint();
	}
	public void mouseMoved(MouseEvent e) {
	}
	public boolean isDrawingEdges() {
		return edgeDrawMode;
	}
	public void setDrawingEdges(boolean drawingEdges) {
		this.edgeDrawMode = drawingEdges;
	}
	
	public void setFisheyeMode(boolean b) {
		fisheyeMode = b;
		if (b){
			Debug.p("new Fisheye Layout");
			view.repaint();
		} else {
			Debug.p("new Normal Layout");
			view.setModel(model);
			view.repaint();
		}
	}
	
	/*
	 * private Methods
	 */
	private Element getElementContainingPosition(double x,double y){
		Element currentElement = new None();
		Iterator<Element> iter = getModel().iterator();
		while (iter.hasNext()) {
		  Element element =  iter.next();
		  if (element.contains(x, y)) currentElement = element;  
		}
		return currentElement;
	}
	
    
}
