package com.bprocessor.util;

import com.bprocessor.Vertex;

public class CoordinateSystem {
	private static CoordinateSystem xy;
	
	public static CoordinateSystem xy() {
		if (xy == null) {
			Vertex origin = new Vertex(0, 0, 0);
			Vertex i = new Vertex(1, 0, 0);
			Vertex j = new Vertex(0, 1, 0);
			xy = new CoordinateSystem(origin, i, j);
		}
		return xy;
	}
	
	private Vertex origin;
	private Vertex i;
	private Vertex j;

	public CoordinateSystem(Vertex origin, Vertex i, Vertex j) {
		this.origin = origin;
		this.i = i;
		this.j = j;
	}

	public Vertex getOrigin() {
		return origin;
	}
	public void setOrigin(Vertex origin) {
		this.origin = origin;
	}
	public Vertex getI() {
		return i;
	}
	public void setI(Vertex i) {
		this.i = i;
	}
	public Vertex getJ() {
		return j;
	}
	public void setJ(Vertex j) {
		this.j = j;
	}
	public Plane plane() {
		Vertex n = i.cross(j);
		double d = 0;
		double a = n.getX();
		double b = n.getY();
		double c = n.getZ();
		double x0 = origin.getX();
		double y0 = origin.getY();
		double z0 = origin.getZ();
		d = -(a * x0 + b * y0 + c * z0);
		return new Plane(a, b, c, d);
	}
}
