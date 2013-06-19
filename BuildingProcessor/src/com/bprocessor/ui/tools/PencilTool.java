package com.bprocessor.ui.tools;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.bprocessor.Color;
import com.bprocessor.Constructor;
import com.bprocessor.Edge;
import com.bprocessor.Group;
import com.bprocessor.Surface;
import com.bprocessor.Vertex;
import com.bprocessor.ui.BuildingEditor;
import com.bprocessor.ui.Tool;
import com.bprocessor.util.Plane;

public class PencilTool extends Tool {
    private LinkedList<Vertex> vertices;
    private LinkedList<Edge> edges;
    private List<Constructor> constructors;
    private Group editing;
    private StringBuffer buffer;

    private void makeSurface() {
        Surface surface = new Surface(edges);
        editing.clear();
        editor.getSketch().getGroup().addAll(surface);
        for (Surface exterior : editor.getSketch().getGroup().getSurfaces()) {
            if (exterior != surface) {
                if (exterior.surrounds(surface)) {
                    exterior.add(surface);
                }
            }
        }
        editor.checkpoint();
        vertices = null;
        edges = null;
        editor.repaint();
    }

    public PencilTool(BuildingEditor editor) {
        super(editor);
    }

    public void evaluate(String value) {
        try {
            double length = Double.valueOf(value);
            for (Surface current : editor.getSketch().getGroup().getSurfaces()) {
                if (current.getExterior() == null) {
                    List<Surface> sides = new LinkedList<Surface>();
                    List<Surface> tops = new LinkedList<Surface>();
                    current.extrudeAll(new Vertex(0, 0, 1), length, sides, tops);
                    for (Surface side: sides) {
                    	editor.getSketch().getGroup().addAll(side);
                    }
                    for (Surface surface : tops) {
                    	editor.getSketch().getGroup().addAll(surface);
                    }
                }
            }
        } catch (Exception error) {

        }
        editor.repaint();
    }

    public void prepare() {
    	editor.setSelected(null);
        editing = new Group("editing");
        editor.addOverlay(editing);
        editor.setRestriction(new Plane(0, 0, 1, 0));
        constructors = new LinkedList<Constructor>();
        buffer = new StringBuffer();
        editor.requestFocus();
        editor.repaint();
    }
    public void finish() {
        editor.setRestriction(null);
        editor.repaint();
        setConstructors(Collections.<Constructor>emptyList());
        editor.removeOverlay(editing);
        constructors = null;
        editing = null;
        vertices = null;
        edges = null;
    }


    public Constructor createConstrutor(Vertex origin, Vertex direction, Color color) {
        Vertex from = origin.copy();
        Vertex to = from.add(direction);
        Constructor constructor = new Constructor(from, to, color);
        return constructor;
    }

    public void setConstructors(List<Constructor> objects) {
        for (Constructor current : constructors) {
            editor.removeConstructor(current);
        }
        constructors.clear();
        for (Constructor current : objects) {
            editor.addConstructor(current);
            constructors.add(current);
        }
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
        Plane plane = editor.getRestriction();
        Vertex original = editor.getPlaneIntersection(event.getX(), event.getY(), plane);
        roundIt(original);

        Vertex existing = editor.selectVertex(event.getX(), event.getY(), plane, original);

        Vertex vertex = null;

        if (existing != null) {
            if (existing.getOwner() == null) {
                roundIt(existing);
            }
            if (existing.getOwner() == editing) {
                if ((vertices.size() > 2) && (existing == vertices.getFirst())) {
                    if (vertices.size() > 0) {
                        Vertex previous = vertices.getLast();
                        Edge edge = new Edge(previous, existing);
                        edges.add(edge);
                        editing.add(edge);
                    }
                    makeSurface();
                    setConstructors(Collections.<Constructor>emptyList());
                } else {
                    vertex = existing;
                }
            } else {
                vertex = existing.copy();
            }
        } else {
            vertex = original;
        }

        if (vertex != null) {
            editing.add(vertex);
            if (vertices.size() > 0) {
                Vertex previous = vertices.getLast();
                Edge edge = new Edge(previous, vertex);
                edges.add(edge);
                editing.add(edge);
            }
            vertices.add(vertex);

            {
                List<Constructor> lst = new LinkedList<Constructor>();
                Color green = new Color(0.1, 0.8, 0.1);
                Color red = new Color(0.8, 0.1, 0.1);
                Color blue = new Color(0.3, 0.6, 1.0);
                Vertex i = new Vertex(1, 0, 0);
                Vertex j = new Vertex(0, 1, 0);
                lst.add(createConstrutor(vertex, i, green));
                lst.add(createConstrutor(vertex, j, red));

                if (vertices.size() > 1) {
                    Vertex first = vertices.getFirst();
                    lst.add(createConstrutor(first, i, blue));
                    lst.add(createConstrutor(first, j, blue));
                }
                setConstructors(lst);
            }
        }
        editor.repaint();
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
        // TODO Auto-generated method stub

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