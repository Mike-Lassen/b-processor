package com.bprocessor.ui.tools;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import com.bprocessor.Surface;
import com.bprocessor.ui.SketchView;
import com.bprocessor.ui.StandardTool;
import com.bprocessor.ui.StatusBar;

public class EraserTool extends StandardTool {
    public EraserTool(SketchView view, StatusBar statusbar) {
        super(view, statusbar);
    }

    public void prepare() {
    	view.setSelected(null);
    }
    public void finish() {
    }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent event) {
        Surface surface = view.selectSurface(event.getX(), event.getY(), null, null);
        if (surface != null) {
            if (surface.getExterior() != null) {
                surface.setVisible(false);
            }
        }
        view.checkpoint();
        view.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseDragged(MouseEvent e) { }

    @Override
    public void mouseMoved(MouseEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) { }

    @Override
    public void keyReleased(KeyEvent e) { }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) { }

}
