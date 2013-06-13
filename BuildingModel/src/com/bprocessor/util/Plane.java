package com.bprocessor.util;

import com.bprocessor.Edge;
import com.bprocessor.Surface;
import com.bprocessor.Vertex;

/**
 * Utility
 */
public class Plane {
	 private double a;
	 private double b;
	 private double c;
	 private double d;

	 public Plane() {
		 
	 }
	 public Plane(double a, double b, double c, double d) {
		 this.a = a;
		 this.b = b;
		 this.c = c;
		 this.d = d;
	 }
	 	 
	 public Vertex intersection(Vertex origin, Vertex direction, boolean endless) {
		 double x0 = origin.getX();
		 double y0 = origin.getY();
		 double z0 = origin.getZ();
		 double xd = direction.getX();
		 double yd = direction.getY();
		 double zd = direction.getZ();
		 double vd = a * xd + b * yd + c * zd;
		 if (vd != 0) {
			 double v0 = -(a * x0 + b * y0 + c * z0 + d);
			 double t = v0 / vd;
			 if (!endless && (t < 0.001 || t > 1.001)) {
				 return null;
			 }
			 Vertex i = new Vertex();
			 i.setX(x0 + t * xd);
			 i.setY(y0 + t * yd);
			 i.setZ(z0 + t * zd);
			 return i;
		 } else {
			 return null;
		 }
	 }
	 public Vertex intersection(Edge ray) {
		 Vertex p0 = ray.getFrom();
		 Vertex p1 = ray.getTo();
		 p1 = p1.minus(p0);
		 return intersection(p0, p1, false);
	 }
	 
	 public double distance(Vertex vertex) {
		 return (a * vertex.getX() + b * vertex.getY() + c * vertex.getZ() + d);
	 }
	 public boolean contains(Vertex vertex) {
		 return Math.abs(distance(vertex)) < 0.0000001;
	 }
	 public boolean contains(Edge edge) {
		 return contains(edge.getFrom()) && contains(edge.getTo());
	 }
	 public boolean contains(Surface surface) {
		 for (Vertex current : surface.getVertices()) {
			 if (!contains(current)) {
				 return false;
			 }
		 }
		 return true;
	 }
}
