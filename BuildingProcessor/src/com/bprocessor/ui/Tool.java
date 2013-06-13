package com.bprocessor.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import com.bprocessor.Camera;
import com.bprocessor.Vertex;
import com.bprocessor.util.Plane;

public abstract class Tool implements MouseListener, MouseMotionListener, KeyListener, MouseWheelListener {
    protected BuildingEditor editor;


    public Tool(BuildingEditor editor) {
        this.editor = editor;
    }

    public void prepare() {

    }
    public void finish() {

    }

    public static class CameraDrag extends Tool {
        private int x;
        private int y;

        public CameraDrag(BuildingEditor editor) {
            super(editor);
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
            Camera camera = editor.camera;
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

            Vertex first = editor.getPlaneIntersection(x, y, new Plane(dx, dy, dz, d));
            Vertex second = editor.getPlaneIntersection(event.getX(),
                    event.getY(), 
                    new Plane(dx, dy, dz, d));
            Vertex vector = first.minus(second);
            camera.translate(vector);

            x = event.getX();
            y = event.getY();
            editor.repaint();
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

        public CameraRotation(BuildingEditor editor) {
            super(editor);
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

            editor.camera.rotateHorizontally(-angleX);
            editor.camera.rotateVertically(angleY);

            x = event.getX();
            y = event.getY();
            editor.repaint();
        }

        @Override
        public void mouseMoved(MouseEvent event) {}
        @Override
        public void keyPressed(KeyEvent event) {
            if (event.getKeyChar() == ' ') {
                System.out.println("camera.eye " + editor.camera.getEye());
                System.out.println("camera.center " + editor.camera.getCenter());
                System.out.println("camera.up " + editor.camera.getUp());
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

        public CameraZoom(BuildingEditor editor) {
            super(editor);
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
                editor.camera.zoomOut();
                y = event.getY();
            }
            if (dy < -8) {
                editor.camera.zoomIn();
                y = event.getY();
            }
            editor.repaint();
        }

        @Override
        public void mouseMoved(MouseEvent event) {
            // TODO Auto-generated method stub

        }

        @Override
        public void keyPressed(KeyEvent event) {
            if (event.getKeyChar() == ' ') {
                editor.camera.focusOn(null);
                editor.repaint();
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
}
