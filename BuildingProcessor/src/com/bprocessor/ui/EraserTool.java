package com.bprocessor.ui;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import com.bprocessor.Surface;

public class EraserTool extends Tool {
    public EraserTool(BuildingEditor editor) {
        super(editor);
    }

    public void prepare() {
    	editor.selected = null;
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
        Surface surface = editor.selectSurface(event.getX(), event.getY(), null, null);
        if (surface != null) {
            if (surface.getExterior() != null) {
                surface.setVisible(false);
            }
        }
        editor.checkpoint();
        editor.repaint();
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
