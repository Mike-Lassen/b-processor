package com.bprocessor.ui;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import com.bprocessor.Camera;
import com.bprocessor.Vertex;
import com.bprocessor.util.Plane;

public abstract class StandardTool extends Tool {
	protected StatusBar statusbar;
	
	
	public static class CameraDrag extends Tool {
	    private int x;
	    private int y;
	
	    public CameraDrag(SketchView view) {
	        super(view);
	    }
	
	    @Override
	    public void mouseClicked(MouseEvent event) {}
	    @Override
	    public void mouseEntered(MouseEvent event) {}
	    @Override
	    public void mouseExited(MouseEvent event) {}
	
	    @Override
	    public void mousePressed(MouseEvent event) {
	        x = event.getX();
	        y = event.getY();
	    }
	
	    @Override
	    public void mouseReleased(MouseEvent event) {}
	
	    @Override
	    public void mouseDragged(MouseEvent event) {
	        Camera camera = view.getCamera();
	        Vertex center = camera.getCenter();
	        Vertex eye = camera.getEye();
	
	        double dx = eye.getX() - center.getX();
	        double dy = eye.getY() - center.getY();
	        double dz = eye.getZ() - center.getZ();
	        double sqr = Math.sqrt(dx * dx  + dy * dy + dz * dz);
	        if (sqr < 1) {
	            dx *= 1 / sqr;
	            dy *= 1 / sqr;
	            dz *= 1 / sqr;
	        }
	        double d = -dx * center.getX() - dy * center.getY() - dz * center.getZ();
	        
	        Plane plane = new Plane(dx, dy, dz, d);
	        Vertex first = plane.intersection(view.getRay(x, y));
	        Vertex second = plane.intersection(view.getRay(event.getX(), event.getY()));
	        Vertex vector = first.minus(second);
	        camera.translate(vector);
	
	        x = event.getX();
	        y = event.getY();
	        view.repaint();
	    }
	
	    @Override
	    public void mouseMoved(MouseEvent event) {}
	    @Override
	    public void keyPressed(KeyEvent event) {}
	    @Override
	    public void keyReleased(KeyEvent event) {}
	    @Override
	    public void keyTyped(KeyEvent event) {}
	    @Override
	    public void mouseWheelMoved(MouseWheelEvent event) {}
	}

	public static class CameraRotation extends Tool {
	    private int x;
	    private int y;
	
	    public CameraRotation(SketchView view) {
	        super(view);
	    }
	
	    @Override
	    public void mouseClicked(MouseEvent event) {}
	    @Override
	    public void mouseEntered(MouseEvent event) {}
	    @Override
	    public void mouseExited(MouseEvent event) {}
	
	    @Override
	    public void mousePressed(MouseEvent event) {
	        x = event.getX();
	        y = event.getY();
	    }
	
	    @Override
	    public void mouseReleased(MouseEvent event) {}
	
	    @Override
	    public void mouseDragged(MouseEvent event) {
	        double angleX = ((double)(event.getX() - x) / 360) * Math.PI;
	        double angleY = ((double)(event.getY() - y) / 360) * Math.PI;
	
	        view.getCamera().rotateHorizontally(-angleX);
	        view.getCamera().rotateVertically(angleY);
	
	        x = event.getX();
	        y = event.getY();
	        view.repaint();
	    }
	
	    @Override
	    public void mouseMoved(MouseEvent event) {}
	    @Override
	    public void keyPressed(KeyEvent event) {
	        if (event.getKeyChar() == ' ') {
	            System.out.println("camera.eye " + view.getCamera().getEye());
	            System.out.println("camera.center " + view.getCamera().getCenter());
	            System.out.println("camera.up " + view.getCamera().getUp());
	        }
	    }
	    @Override
	    public void keyReleased(KeyEvent event) {}
	    @Override
	    public void keyTyped(KeyEvent event) {}
	    @Override
	    public void mouseWheelMoved(MouseWheelEvent event) {}
	}

	public static class CameraZoom extends Tool {
	    private int y;
	
	    public CameraZoom(SketchView view) {
	        super(view);
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
	        y = event.getY();
	    }
	
	    @Override
	    public void mouseReleased(MouseEvent event) {
	        // TODO Auto-generated method stub
	
	    }
	
	    @Override
	    public void mouseDragged(MouseEvent event) {
	        int dy = event.getY() - y;
	        if (dy > 8) {
	            view.getCamera().zoomOut();
	            y = event.getY();
	        }
	        if (dy < -8) {
	            view.getCamera().zoomIn();
	            y = event.getY();
	        }
	        view.repaint();
	    }
	
	    @Override
	    public void mouseMoved(MouseEvent event) {
	        // TODO Auto-generated method stub
	
	    }
	
	    @Override
	    public void keyPressed(KeyEvent event) {
	        if (event.getKeyChar() == ' ') {
	            view.getCamera().focusOn(null);
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

	public StandardTool(SketchView view, StatusBar statusbar) {
		super(view);
		this.statusbar = statusbar;
	}

	public double round(double value) {
	    long i = Math.round((value * 1000));
	    double result = (double) (i / 1000.0);
	    return result;
	}

	public void roundIt(Vertex vertex) {
	    vertex.setX(round(vertex.getX()));
	    vertex.setY(round(vertex.getY()));
	    vertex.setZ(round(vertex.getZ()));
	}
	
}
