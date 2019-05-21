package infovis.paracoords;

import infovis.scatterplot.Data;
import infovis.scatterplot.Model;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseController implements MouseListener, MouseMotionListener {
	private View view = null;
	private Model model = null;
	private int x = 0;
	private int y = 0;
	
	public void mouseClicked(MouseEvent e) {
		
	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {
		view.getMarkerRectangle().setRect(0,0,0,0);
		view.repaint();
	}

	public void mousePressed(MouseEvent e) {
		x = e.getX();
		y = e.getY();
	}

	public void mouseReleased(MouseEvent e) {
		int xl = e.getX();
		int yl = e.getY();

		view.getMarkerRectangle().setRect(0,0,0,0);
		view.repaint();
	}

	public void mouseDragged(MouseEvent e) {
		int xl = e.getX();
		int yl = e.getY();

		view.getMarkerRectangle().setRect(x, y, xl-x, yl-y);

		view.repaint();
		// set models back to normal for next rectangle
		for (Data d: model.getList()) {
			d.setColor(Color.BLACK);
		}
	}

	public void mouseMoved(MouseEvent e) {

	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

}
