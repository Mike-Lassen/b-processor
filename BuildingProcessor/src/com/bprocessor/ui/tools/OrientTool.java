package com.bprocessor.ui.tools;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import com.bprocessor.Color;
import com.bprocessor.GuideLayer;
import com.bprocessor.Handle;
import com.bprocessor.Line;
import com.bprocessor.Vertex;
import com.bprocessor.ui.SketchView;
import com.bprocessor.ui.StandardTool;
import com.bprocessor.ui.StatusBar;
import com.bprocessor.util.CoordinateSystem;


public class OrientTool extends StandardTool {

	public enum Mode {
		PLACE_ORIGIN,
		PLACE_X,
		PLACE_Y
	}

	private GuideLayer feedback;
	private Handle mark;
	private Mode mode;
	private Vertex origin;
	private Handle origin_mark;
	private Line x_axis;
	private Line y_axis;

	public OrientTool(SketchView view, StatusBar statusbar) {
		super(view, statusbar);
	}

	public void prepare() {
		feedback = new GuideLayer("orient");
		view.addOverlay(feedback);
		mode = Mode.PLACE_ORIGIN;
	}

	public void finish() {
		view.removeOverlay(feedback);
		feedback = null;
		view.repaint();
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent event) {
		if (mark != null) {
			Vertex vertex = new Vertex(mark.getX(), mark.getY(), mark.getZ());
			switch (mode) {
			case PLACE_ORIGIN:
				if (origin_mark != null) {
					feedback.remove(origin_mark);
					origin_mark = null;
				}
				if (x_axis != null) {
					feedback.remove(x_axis);
					x_axis = null;
				}
				if (y_axis != null) {
					feedback.remove(y_axis);
					y_axis = null;
				}
				origin = vertex;
				mode = Mode.PLACE_X;
				origin_mark = new Handle(origin.getX(), origin.getY(), origin.getZ(), new Color(0, 0, 0));
				feedback.add(origin_mark);
				break;
			case PLACE_X:
				if (!origin.coincides(vertex)) {
					x_axis = new Line(origin, vertex, new Color(0.2, 0.7, 0.1));
					feedback.add(x_axis);
					mode = Mode.PLACE_Y;
				}
				break;
			case PLACE_Y:
				if (!origin.coincides(vertex)) {
					y_axis = new Line(origin, vertex, new Color(0.7, 0.1, 0.2));
					feedback.add(y_axis);
					mode = Mode.PLACE_ORIGIN;
					Vertex i = x_axis.direction();
					i.normalize();
					Vertex j = y_axis.direction();
					j.normalize();
					CoordinateSystem system = new CoordinateSystem(origin, i, j);
					view.setCoordinateSystem(system);
				}
			}
		}
		view.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent event) {
		if (mark != null) {
			feedback.remove(mark);
			mark = null;
		}
		Vertex vertex = view.getIntersection(event.getX(), event.getY(), null).getVertex();
		if (vertex != null) {
			mark = new Handle(vertex.getX(), vertex.getY(), vertex.getZ(), new Color(0.7, 0.2, 0.1));
			feedback.add(mark);
		}
		view.repaint();
	}

	@Override
	public void keyPressed(KeyEvent event) {
		if (event.getKeyChar() == KeyEvent.VK_SPACE) {
			view.setCoordinateSystem(null);
			if (origin_mark != null) {
				feedback.remove(origin_mark);
				origin_mark = null;
			}
			if (x_axis != null) {
				feedback.remove(x_axis);
				x_axis = null;
			}
			if (y_axis != null) {
				feedback.remove(y_axis);
				y_axis = null;
			}
			mode = Mode.PLACE_ORIGIN;
			view.repaint();
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {
		// TODO Auto-generated method stub

	}

}
