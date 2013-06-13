package com.bprocessor;

import java.util.Collection;


public class Camera {
    protected Vertex center;
    protected Vertex eye;
    protected Vertex up;

    public Camera() {
    }

    public Camera(Vertex center, Vertex eye, Vertex up) {
        this.center = center;
        this.eye = eye;
        this.up = up;
    }

    public Vertex getCenter() {
        return center;
    }
    public void setCenter(Vertex center) {
        this.center = center;
    }
    public Vertex getEye() {
        return eye;
    }
    public void setEye(Vertex eye) {
        this.eye = eye;
    }
    public Vertex getUp() {
        return up;
    }
    public void setUp(Vertex up) {
        this.up = up;
    }

    public void translate(Vertex vector) {
        center.set(center.add(vector));
        eye.set(eye.add(vector));
    }

    public void rotateVertically(double angle) {
        double x = center.getX() - eye.getX();
        double y = center.getY() - eye.getY();
        double z = center.getZ() - eye.getZ();
        Vertex forward = new Vertex(x, y, z);

        Vertex sidewards = up.cross(forward);
        sidewards.scaleIt(1 / sidewards.length());

        GeometryMath.rotate(angle, sidewards.getX(), sidewards.getY(), sidewards.getZ(), 
                eye, center);
        GeometryMath.rotate(angle,  sidewards.getX(), sidewards.getY(), sidewards.getZ(),
                up, new Vertex(0, 0, 0));
    }

    public void rotateHorizontally(double angle) {
        double turn = 1;
        if (up.getZ() < 0) {
            turn = -1;
        }
        GeometryMath.rotate(angle, 0, 0, turn, eye, center);
        GeometryMath.rotate(angle, 0, 0, turn, up, new Vertex(0, 0, 0));
    }

    public void zoomOut() {
        Vertex direction = eye.minus(center);
        direction.scaleIt(0.1);
        eye.set(eye.add(direction));
    }

    public void zoomIn() {
        Vertex direction = eye.minus(center);
        if (direction.length() > 0.1) {
            direction.scaleIt(0.1);
            eye.set(eye.minus(direction));
        }
    }

    public void focusOn(Collection<Vertex> vertices) {
        GeometryMath.BoundingSphere sphere = new GeometryMath.BoundingSphere(vertices);
        //double radius = sphere.radius();
        center.set(sphere.center());
    }
}
