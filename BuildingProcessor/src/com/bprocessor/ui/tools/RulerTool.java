package com.bprocessor.ui.tools;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JLabel;

import com.bprocessor.Color;
import com.bprocessor.Line;
import com.bprocessor.Edge;
import com.bprocessor.Geometry;
import com.bprocessor.Vertex;
import com.bprocessor.ui.SketchView;
import com.bprocessor.ui.StandardTool;
import com.bprocessor.ui.StatusBar;
import com.bprocessor.util.Filter;

public class RulerTool extends StandardTool {
    private int startx;
    private int starty;
    private boolean moving;

    StringBuffer buffer;
    private Edge prototype;
    private Line line;
    private JLabel legendFld;
    private JLabel distanceFld;

    public RulerTool(SketchView view, StatusBar statusbar) {
        super(view, statusbar);
    }


    public void evaluate(String value) {
        try {
            double length = Double.valueOf(value);
            if (prototype != null) {
                Vertex from = line.getFrom();
                Vertex to = line.getTo();
                Vertex v = to.minus(from);
                Vertex p = prototype.intersection(from);
                Vertex u = from.minus(p);
                u.normalize();
                u.scaleIt(length);
                from.set(p.add(u));
                to.set(from.add(v));
                view.repaint();
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
    	view.setSelected(null);
        view.repaint();
        view.requestFocus();
        buffer = new StringBuffer();
        legendFld = new JLabel("Distance: ");
        legendFld.setFont(new Font("Dialog", Font.BOLD, 12));
        statusbar.register(legendFld);
        distanceFld = new JLabel("n/a");
        distanceFld.setFont(new Font("Dialog", Font.PLAIN, 12));
        statusbar.register(distanceFld);
    }
    public void finish() {
        view.repaint();
        buffer = null;
        line = null;
        prototype = null;
        statusbar.deregister(legendFld);
        legendFld = null;
        statusbar.deregister(distanceFld);
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
        Geometry result = view.pickObject(event.getX(), event.getY(), null);
        if (result instanceof Edge) {
            Edge edge = (Edge) result;
            Vertex from = edge.getFrom().copy();
            Vertex to = edge.getTo().copy();
            Color blue = new Color(0.3, 0.6, 1.0);
            line = new Line(from, to, blue);
            
            adjust(line);
			
            prototype = edge;
            view.guideLayer().add(line);
            updateDistance(0.0);
            view.repaint();
        }
    }
    
    private void adjust(Line line) {
    	Vertex from = line.getFrom();
    	Vertex to = line.getTo();
    	Vertex direction = line.direction();
        direction.normalize();
		direction.scaleIt(50);
		to.set(from.add(direction));
		from.set(from.minus(direction));
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (line != null) {
            if (!moving) {
                int dx = event.getX() - startx;
                int dy = event.getY() - starty;
                if ((dx*dx + dy*dy) > 10) {
                    moving = true;
                }
            }
            if (moving) {
                Vertex vertex = view.getIntersection(event.getX(), event.getY(), new Filter<Geometry>() {
					public boolean evaluate(Geometry object) {
						return object != line;
					}
				}).getVertex();
                if (vertex != null) {
                    vertex.setX(round(vertex.getX()));
                    vertex.setY(round(vertex.getY()));
                    vertex.setZ(round(vertex.getZ()));
                    Vertex from = line.getFrom();
                    Vertex to = line.getTo();
                    Vertex v = to.minus(from);
                    from.set(vertex);
                    to.set(from.add(v));
                    {
                        Vertex p = prototype.intersection(from);
                        updateDistance(p.distance(from));
                    }
                    adjust(line);
                    view.repaint();
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
            view.guideLayer().clear();
            line = null;
            prototype = null;
            buffer = new StringBuffer();
            updateDistance();
            view.repaint();
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
