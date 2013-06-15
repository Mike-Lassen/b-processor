package com.bprocessor.ui;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JLabel;

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
    private JLabel legendFld;
    private JLabel distanceFld;

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

    private void updateDistance() {
    	if (buffer.length() > 0) {
    		distanceFld.setText(buffer.toString());
    	} else {
    		distanceFld.setText("n/a");
    	}
    }
    private void updateDistance(double distance) {
    	distanceFld.setText(String.valueOf(round(distance)));
    }
    
    public void prepare() {
        editor.restriction = new Plane(0, 0, 1, 0);
        editor.repaint();
        editor.requestFocus();
        buffer = new StringBuffer();
        legendFld = new JLabel("Distance: ");
        legendFld.setFont(new Font("Dialog", Font.BOLD, 12));
        editor.statusbar.register(legendFld);
        distanceFld = new JLabel("n/a");
        distanceFld.setFont(new Font("Dialog", Font.PLAIN, 12));
        editor.statusbar.register(distanceFld);
    }
    public void finish() {
        editor.restriction = null;
        editor.repaint();
        buffer = null;
        constructor = null;
        prototype = null;
        editor.statusbar.deregister(legendFld);
        legendFld = null;
        editor.statusbar.deregister(distanceFld);
        distanceFld = null;
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
            updateDistance(0.0);
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
                    {
                        Vertex p = prototype.intersection(from);
                        updateDistance(p.distance(from));
                    }
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
            updateDistance();
        } else if (ch == KeyEvent.VK_ENTER) {
            evaluate(buffer.toString());
            buffer = new StringBuffer();
        } else if (ch == KeyEvent.VK_SPACE) {
            editor.constructorLayer.clear();
            constructor = null;
            prototype = null;
            buffer = new StringBuffer();
            updateDistance();
            editor.repaint();
        } else {
            buffer.append(ch);
            updateDistance();
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
