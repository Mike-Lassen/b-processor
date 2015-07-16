package com.bprocessor.ui.tools;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.bprocessor.Color;
import com.bprocessor.GuideLayer;
import com.bprocessor.Handle;
import com.bprocessor.Line;
import com.bprocessor.Edge;
import com.bprocessor.Group;
import com.bprocessor.Surface;
import com.bprocessor.Vertex;
import com.bprocessor.ui.Intersection;
import com.bprocessor.ui.SketchView;
import com.bprocessor.ui.StandardTool;
import com.bprocessor.ui.StatusBar;
import com.bprocessor.ui.commands.InsertSurface;
import com.bprocessor.util.Command;
import com.bprocessor.util.CommandManager;
import com.bprocessor.util.CoordinateSystem;

public class PencilTool extends StandardTool {
	private LinkedList<Vertex> vertices;
	private LinkedList<Edge> edges;
	private List<Line> lines;

	private Group editing;

	private GuideLayer feedback;
	private Vertex currentVertex;
	private Handle mark;

	private StringBuffer buffer;
	

	private void makeSurface() {
		Surface surface = new Surface(edges);
		editing.clear();
		Command command = new InsertSurface(view.getSketch().getGroup(), surface);
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
		try {
			double length = Double.valueOf(value);
			for (Surface current : view.getSketch().getGroup().getSurfaces()) {
				if (current.getExterior() == null) {
					List<Surface> sides = new LinkedList<Surface>();
					List<Surface> tops = new LinkedList<Surface>();
					current.extrudeAll(new Vertex(0, 0, 1), length, sides, tops);
					for (Surface side: sides) {
						view.getSketch().getGroup().insert(side);
					}
					for (Surface surface : tops) {
						view.getSketch().getGroup().insert(surface);
					}
				}
			}
		} catch (Exception error) {

		}
		view.repaint();
	}

	public void prepare() {
		view.setSelected(null);
		editing = new Group("editing");
		view.addOverlay(editing);
		feedback = new GuideLayer("guides");
		view.addOverlay(feedback);
		lines = new LinkedList<Line>();
		buffer = new StringBuffer();
		view.requestFocus();
		view.repaint();
	}
	public void finish() {
		view.repaint();
		setLines(Collections.<Line>emptyList());
		view.removeOverlay(editing);
		view.removeOverlay(feedback);
		lines = null;
		editing = null;
		vertices = null;
		edges = null;
		currentVertex = null;
	}


	public Line createLine(Vertex origin, Vertex direction, Color color) {
		Vertex from = origin.copy();
		Vertex to = from.add(direction);
		Line line = new Line(from, to, color);
		return line;
	}

	public void setLines(List<Line> objects) {
		for (Line current : lines) {
			feedback.remove(current);
		}
		lines.clear();
		for (Line current : objects) {
			feedback.add(current);
			lines.add(current);
		}
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
					setLines(Collections.<Line>emptyList());
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

				{
					CoordinateSystem system = view.getCoordinateSystem();					
					List<Line> lst = new LinkedList<Line>();
					Color green = new Color(0.1, 0.8, 0.1);
					Color red = new Color(0.8, 0.1, 0.1);
					Color blue = new Color(0.1, 0.1, 0.8);
					
					Color bluish = new Color(0.3, 0.6, 1.0);
					Vertex i = system.getI();
					Vertex j = system.getJ();
					Vertex n = j.cross(i);
					lst.add(createLine(currentVertex, i, green));
					lst.add(createLine(currentVertex, j, red));
					if (!view.getRestrictToPlane()) {
						lst.add(createLine(currentVertex, n, blue));
					}
					if (vertices.size() > 1) {
						Vertex first = vertices.getFirst();
						lst.add(createLine(first, i, bluish));
						lst.add(createLine(first, j, bluish));
						if (!view.getRestrictToPlane()) {
							lst.add(createLine(first, n, bluish));
						}
					}
					setLines(lst);
				}
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
