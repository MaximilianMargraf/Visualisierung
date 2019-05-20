package infovis.scatterplot;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseController implements MouseListener, MouseMotionListener {

	private Model model = null;
	private View view = null;
	private int x = 0;
	private int y = 0;

	public void mouseClicked(MouseEvent arg0) {
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
		x = arg0.getX();
		y = arg0.getY();
	}

	public void mouseReleased(MouseEvent arg0) {
		int xl = arg0.getX();
		int yl = arg0.getY();

		view.getMarkerRectangle().setRect(x, y, xl-x, yl-y);

		view.repaint();
		for (Data d: model.getList()) {
			d.setColor(Color.BLACK);
		}
	}

	public void mouseDragged(MouseEvent arg0) {
		//view.repaint();
	}

	public void mouseMoved(MouseEvent arg0) {
	}

	public void setModel(Model model) {
		this.model  = model;	
	}

	public void setView(View view) {
		this.view  = view;
	}

}
