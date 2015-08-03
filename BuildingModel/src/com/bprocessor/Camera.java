package com.bprocessor;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


public class Camera extends Entity {
    protected Vertex center;
    protected Vertex eye;
    protected Vertex up;
    protected double fov;
    protected double near;
    protected double far;
    

    public Camera() {
    }

    public Camera(Vertex center, Vertex eye, Vertex up) {
        this.center = center;
        this.eye = eye;
        this.up = up;
        fov = 45.0;
        near = 0.1;
        far = 1000.0;
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
    public double getFov() {
    	return fov;
    }
    public void setFov(double value) {
    	fov = value;
    }
    public double getNear() {
    	return near;
    }
    public void setNear(double value) {
    	near = value;
    }
    public double getFar() {
    	return far;
    }
    public void setFar(float value) {
    	far = value;
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
    
    public List<Attribute> getAttributes() {
		List<Attribute> attributes = super.getAttributes();
		List<Attribute> section = new LinkedList<Attribute>();

		section.add(new Attribute("FOV", new Format() {
			@Override
			public String format() {
				return String.valueOf(fov);
			}
			@Override
			public void apply(String value) {
				fov = Double.valueOf(value);
			}
		}));
		section.add(new Attribute("Near", new Format() {
			@Override
			public String format() {
				return String.valueOf(near);
			}
			@Override
			public void apply(String value) {
				near = Double.valueOf(value);
			}
		}));
		section.add(new Attribute("Far", new Format() {
			@Override
			public String format() {
				return String.valueOf(far);
			}
			@Override
			public void apply(String value) {
				far = Double.valueOf(value);
			}
		}));
		attributes.add(new Attribute("Camera", section));
		return attributes;
	}
}
