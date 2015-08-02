package com.bprocessor.ui.tools;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.LinkedList;

import com.bprocessor.Color;
import com.bprocessor.Net;
import com.bprocessor.Handle;
import com.bprocessor.Edge;
import com.bprocessor.Polyhedron;
import com.bprocessor.Surface;
import com.bprocessor.Vertex;
import com.bprocessor.ui.Intersection;
import com.bprocessor.ui.SketchView;
import com.bprocessor.ui.StandardTool;
import com.bprocessor.ui.StatusBar;
import com.bprocessor.ui.commands.InsertSurface;
import com.bprocessor.util.Command;
import com.bprocessor.util.CommandManager;

public class PencilTool extends StandardTool {
	private LinkedList<Vertex> vertices;
	private LinkedList<Edge> edges;
	
	private Polyhedron editing;

	private Net feedback;
	private Vertex currentVertex;
	private Handle mark;

	private StringBuffer buffer;
	

	private void makeSurface() {
		Surface surface = new Surface(edges);
		editing.clear();
		Command command = new InsertSurface(view.getSketch().getPolyhedron(), surface);
		CommandManager.instance().apply(command);
		view.checkpoint();
		vertices = null;
		edges = null;
		view.repaint();
	}

	public PencilTool(SketchView view, StatusBar statusbar) {
		super(view, statusbar);
	}
	
	public void evaluate(String value) {
	}

	public void prepare() {
		view.setSelected(null);
		view.setRestrictToPlane(true);
		editing = new Polyhedron("editing");
		view.addOverlay(editing);
		feedback = new Net("guides");
		feedback.clear();
		view.addOverlay(feedback);
		buffer = new StringBuffer();
		view.requestFocus();
		view.repaint();
	}
	public void finish() {
		view.setRestrictToPlane(false);
		view.repaint();
		view.removeOverlay(editing);
		view.removeOverlay(feedback);
		editing = null;
		vertices = null;
		edges = null;
		currentVertex = null;
	}

	public void selectVertex(int x, int y) {

		if (mark != null) {
			feedback.remove(mark);
			mark = null;
		}
		Color color = null;    	
		Intersection intersection = view.getIntersection(x, y, null);
		
		if (intersection != null) {
			Vertex vertex = intersection.getVertex();
			if (vertex.getOwner() == null) {
				roundIt(vertex);
			}
			Vertex existing = editing.findVertex(vertex);
			if (existing != null) {
				currentVertex = existing;
				color = new Color(0.7, 0.2, 0.1);
			} else {
				currentVertex = vertex;
				if (intersection.getType() == Intersection.EDGE) {
					color = new Color(0.1, 0.2, 0.7);
				} else if (intersection.getType() == Intersection.VERTEX) {
					color = new Color(0.7, 0.2, 0.1);
				}
			}
		}
		if (currentVertex != null && color != null) {
			mark = new Handle(currentVertex.getX(), currentVertex.getY(), currentVertex.getZ(), color);
			feedback.add(mark);
		};

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
		if (vertices == null) {
			vertices = new LinkedList<Vertex>();
			edges = new LinkedList<Edge>();
		}

		if (currentVertex != null) {
			if (currentVertex.getOwner() == editing) {
				if ((vertices.size() > 2) && (currentVertex == vertices.getFirst())) {
					if (vertices.size() > 0) {
						Vertex previous = vertices.getLast();
						Edge edge = new Edge(previous, currentVertex);
						edges.add(edge);
						editing.add(edge);
					}
					makeSurface();
				}
			} else {
				currentVertex = currentVertex.copy();
				editing.add(currentVertex);
				if (vertices.size() > 0) {
					Vertex previous = vertices.getLast();
					Edge edge = new Edge(previous, currentVertex);
					edges.add(edge);
					editing.add(edge);
				}
				vertices.add(currentVertex);
			}

			view.repaint();
		}
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
		selectVertex(event.getX(), event.getY());
		view.repaint();
	}

	@Override
	public void keyPressed(KeyEvent event) {
		char ch = event.getKeyChar();
		if (ch == KeyEvent.VK_ESCAPE) {
			buffer = new StringBuffer();
		} else if (ch == KeyEvent.VK_ENTER) {
			evaluate(buffer.toString());
			buffer = new StringBuffer();
		} else {
			buffer.append(ch);
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
