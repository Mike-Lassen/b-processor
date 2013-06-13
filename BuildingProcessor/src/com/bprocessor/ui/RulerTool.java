package com.bprocessor.ui;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import com.bprocessor.Color;
import com.bprocessor.Constructor;
import com.bprocessor.Edge;
import com.bprocessor.Geometry;
import com.bprocessor.Vertex;
import com.bprocessor.util.Plane;

public class RulerTool extends Tool {
    private int startx;
    private int starty;
    private boolean moving;

    StringBuffer buffer;
    private Edge prototype;
    private Constructor constructor;

    public RulerTool(BuildingEditor editor) {
        super(editor);
    }


    public void evaluate(String value) {
        try {
            double length = Double.valueOf(value);
            if (prototype != null) {
                Vertex from = constructor.getFrom();
                Vertex to = constructor.getTo();
                Vertex v = to.minus(from);
                Vertex p = prototype.intersection(from);
                Vertex u = from.minus(p);
                u.normalize();
                u.scaleIt(length);
                from.set(p.add(u));
                to.set(from.add(v));
                editor.repaint();
            }
        } catch (Exception error) {

        }

    }

    public void prepare() {
        editor.restriction = new Plane(0, 0, 1, 0);
        editor.repaint();
        editor.requestFocus();
        buffer = new StringBuffer();
    }
    public void finish() {
        editor.restriction = null;
        editor.repaint();
        buffer = null;
        constructor = null;
        prototype = null;
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

    public double round(double value) {
        long i = (long) (value * 1000);
        double result = (double) (i / 1000.0);
        return result;
    }

    @Override
    public void mousePressed(MouseEvent event) {
        startx = event.getX();
        starty = event.getY();
        moving = false;

        Plane plane = editor.restriction;
        Vertex original = editor.getPlaneIntersection(event.getX(), event.getY(), plane);
        original.setX(round(original.getX()));
        original.setY(round(original.getY()));
        original.setZ(round(original.getZ()));

        Geometry result = editor.selectGeometry(event.getX(), event.getY(), plane, original);
        if (result instanceof Edge) {
            Edge edge = (Edge) result;
            Vertex from = edge.getFrom().copy();
            Vertex to = edge.getTo().copy();
            Color blue = new Color(0.3, 0.6, 1.0);
            constructor = new Constructor(from, to, blue);
            prototype = edge;
            editor.constructorLayer.add(constructor);
            editor.repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (constructor != null) {
            if (!moving) {
                int dx = event.getX() - startx;
                int dy = event.getY() - starty;
                if ((dx*dx + dy*dy) > 10) {
                    moving = true;
                }
            }
            if (moving) {
                Plane plane = editor.restriction;
                Vertex original = editor.getPlaneIntersection(event.getX(), event.getY(), plane);
                if (original != null) {
                    original.setX(round(original.getX()));
                    original.setY(round(original.getY()));
                    original.setZ(round(original.getZ()));
                    Vertex from = constructor.getFrom();
                    Vertex to = constructor.getTo();
                    Vertex v = to.minus(from);
                    from.set(original);
                    to.set(from.add(v));
                    editor.repaint();
                }
            }
        }
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
        } else if (ch == KeyEvent.VK_SPACE) {
            editor.constructorLayer.clear();
            constructor = null;
            prototype = null;
            buffer = new StringBuffer();
            editor.repaint();
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
