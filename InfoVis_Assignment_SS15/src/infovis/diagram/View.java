package infovis.diagram;

import infovis.diagram.elements.Element;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.JPanel;



public class View extends JPanel{
	// Restrictions for overview square
	private final static int  MAX_WIDTH  = 800;
	private final static int MAX_HEIGHT = 720;

	private int markerPosX = 0;
	private int markerPosY = 0;

	private Model model = null;
	private Color color = Color.BLUE;
	private double scale = 1;
	private double translateX;
	private double translateY;
	private Rectangle2D marker = new Rectangle2D.Double();
	private Rectangle2D overviewRect = new Rectangle2D.Double();

	public Model getModel() {
		return model;
	}
	public void setModel(Model model) {
		this.model = model;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}

	
	public void paint(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.clearRect(0, 0, getWidth(), getHeight());

		// use scale member to zoom in and out.
		g2D.scale(getScale(), getScale());
		// move window according to coordinates
		g2D.translate(-translateX, -translateY);
		// paint the scene like it is
		paintDiagram(g2D);
		//move the object back for the overview rectangle
		g2D.translate(translateX, translateY);

		// paint the Overview diagram
		drawOverview(g2D);
		updateMarker((int) translateX,(int) translateY, getScale());
	}


	private void paintDiagram(Graphics2D g2D){
		for (Element element: model.getElements()){
			element.paint(g2D);
		}
	}

	// draw overview rectangle and marker rectangle
	private void drawOverview(Graphics2D g2D){
		// adjust size of graphic object for the overview. This window will be 1/4 the size of the normal scene
		g2D.scale(0.25 / getScale(), 0.25/getScale());
		// size of the rectangle equals the resized graphic object
		overviewRect.setRect(0,0, MAX_WIDTH, MAX_HEIGHT);
		// clean area over/under the rectangle
		g2D.clearRect(0,0, MAX_WIDTH, MAX_HEIGHT);
		// set frame color
		g2D.setColor(Color.BLACK);
		// draw rectangle
		g2D.draw(overviewRect);
		// draw the resized scene on its place
		paintDiagram(g2D);

		// set marker rectangle (current view)
		g2D.setColor(Color.RED);
		updateMarker(markerPosX, markerPosY, getScale());
		g2D.draw((marker));
	}
	
	public void setScale(double scale) {
		this.scale = scale;
	}
	public double getScale(){
		return scale;
	}
	public double getTranslateX() {
		return translateX;
	}
	public void setTranslateX(double translateX) {
		this.translateX = translateX;
	}
	public double getTranslateY() {
		return translateY;
	}
	public void setTranslateY(double tansslateY) {
		this.translateY = tansslateY;
	}
	public void updateTranslation(double x, double y){
		setTranslateX(x);
		setTranslateY(y);
	}

	public void updateMarker(int x, int y, double scale) {
		double width  = getWidth() / scale;
		double height = getHeight() / scale;
		markerPosX = x;
		markerPosY = y;
		//translate = true;

		if(x + width > MAX_WIDTH) {
			x = MAX_WIDTH - (int) width;
		}
		if(y + height > MAX_HEIGHT) {
			y = MAX_HEIGHT - (int) height;
		}
		// stop the marker from going off stream.
		if(x < 0){
			x = 0;
		}
		if(y < 0){
			y = 0;
		}
		marker.setRect(x, y, width, height); // 16, 10
	}

	public void updateOverview(Graphics2D g2D) {
		g2D.translate(translateX, translateY);
		drawOverview(g2D);
		g2D.translate(-translateX, -translateY);
	}

	public Rectangle2D getMarker(){
		return marker;
	}

	public boolean markerContains(int x, int y){
		return marker.contains(x, y);
	}

	public boolean overviewContains(int x, int y){
		return overviewRect.contains(x, y);
	}
}
 