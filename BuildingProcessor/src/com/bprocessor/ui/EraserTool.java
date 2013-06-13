package com.bprocessor.ui;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import com.bprocessor.Surface;
import com.bprocessor.Vertex;
import com.bprocessor.util.Plane;

public class EraserTool extends Tool {
    public EraserTool(BuildingEditor editor) {
        super(editor);
    }

    public void prepare() {
        editor.restriction = new Plane(0, 0, 1, 0);
    }
    public void finish() {
        editor.restriction = null;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent event) {
        Plane plane = editor.restriction;
        Vertex original = editor.getPlaneIntersection(event.getX(), event.getY(), plane);		
        Surface surface = editor.selectSurface(event.getX(), event.getY(), plane, original);
        if (surface != null) {
            if (surface.getExterior() != null) {
                surface.setVisible(false);
            }
        }
        editor.checkpoint();
        editor.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        // TODO Auto-generated method stub

    }

}
