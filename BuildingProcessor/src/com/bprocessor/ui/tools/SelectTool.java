package com.bprocessor.ui.tools;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import com.bprocessor.Geometry;
import com.bprocessor.ui.SketchView;
import com.bprocessor.ui.Tool;

public class SelectTool extends Tool {

    public SelectTool(SketchView view) {
        super(view);
    }

    @Override
    public void mouseClicked(MouseEvent event) {

    }

    @Override
    public void mouseEntered(MouseEvent event) {

    }

    @Override
    public void mouseExited(MouseEvent event) {

    }

    @Override
    public void mousePressed(MouseEvent event) {
        Geometry geometry = view.selectObject(event.getX(), event.getY());
        view.setSelected(geometry);
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
        // TODO Auto-generated method stub

    }

    @Override
    public void keyPressed(KeyEvent event) {
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
