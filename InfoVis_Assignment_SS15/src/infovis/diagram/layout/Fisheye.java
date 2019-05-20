package infovis.diagram.layout;

import infovis.diagram.Model;
import infovis.diagram.View;
import infovis.diagram.elements.Vertex;

// ftp://ftp.cs.brown.edu/pub/techreports/93/cs93-40.pdf
public class Fisheye implements Layout{

	private double fishEyeCenterX = 250;
	private double fishEyeCenterY = 240;
	private double d = 4;

	public void setMouseCoords(int x, int y, View view) {
		// start thy fisheye
		fishEyeCenterX = x;
		fishEyeCenterY = y;
		transform(view.getModel(), view);
	}

	public Model transform(Model model, View view) {
		for(int i = 0; i < model.getVertices().size(); ++i) {
			Vertex current = model.getVertices().get(i);

			// each vertex gets an individual position in the fisheye view, depending on its position in
			// normal coordinates and the position of the focus
			double fishX = calcFish(view.getWidth(), fishEyeCenterX, current.getX());
			double fishY = calcFish(view.getHeight(), fishEyeCenterY, current.getY());

			// the size of each vertex depends on the vertex's size, its position in normal coordinates
			// and the position of the focus
			double qNormX = current.getX() + current.getWidth()/2;
			double qNormY = current.getY() + current.getHeight()/2;

			 // position in fisheye mode, x and y are mapped independently
			double qFishX = calcFish(view.getWidth(), fishEyeCenterX, qNormX);
			double qFishY = calcFish(view.getHeight(), fishEyeCenterY, qNormY);

			// computing the size of the vertex
			double sGeom = 2 * Math.min(Math.abs(qFishX - fishX), Math.abs(qFishY - fishY)) * 0.05;

			// edit the vertex being iterated on
			current.setFrame(fishX, fishY, current.getWidth() * sGeom, current.getHeight() * sGeom);
			model.getVertices().set(i, current);
		}
		view.setModel(model);
		view.repaint();
		return model;
	}

	// "F_1 of the fisheye paper, returning the position for a vertex in fisheye mode
	public double calcFish(double pBorder, double pFocus, double pNorm) {
		double dMax = calcDMax(pBorder, pFocus, pNorm);

		// dNorm is the horizontal distance between the point being transformed and the focus in normal coordinates
		// the meanings of dMax and dNorm are similiar in vertical dimension
		double dNorm = pNorm - pFocus;
		double g = calcG(dNorm / dMax);

		// Adding the focus to g(x) * dMax yields the fisheye coordinates
		return pFocus + g * dMax;
	}

	// calculate the horizontal distance between the boundry of the screen and focus in normal coordinates
	public double calcDMax(double pBorder, double pFocus, double pNorm) {
		double dMax = 0;
		if (pNorm > pFocus)
			dMax = pBorder - pFocus;
		else
			dMax = 0 - pFocus;
		return dMax;
	}

	// calculate G(x)
	// d is the distortion factor
	public double calcG(double x) {
		return ((d + 1) * (x)) / ((d * x) + 1);
	}
	
}
