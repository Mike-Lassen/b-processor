package com.bprocessor;

import java.util.Collection;

public class Vertex extends Geometry {
	protected double x;
	protected double y;
	protected double z;
	
	public static Vertex findVertex(Collection<Vertex> vertices, Vertex vertex) {
		for (Vertex current : vertices) {
			if (current.coincides(vertex)) {
				return current;
			}
		}
		return null;
	}

	public Vertex() {
		
	}
	public Vertex(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	
	public void setX(double value) {
		x = value;
	}
	public double getX() {
		return x;
	}
	public void setY(double value) {
		y = value;
	}
	public double getY() {
		return y;
	}
	public void setZ(double value) {
		z = value;
	}
	public double getZ() {
		return z;
	}

	public void set(Vertex vertex) {
		x = vertex.x;
		y = vertex.y;
		z = vertex.z;
	}
	public Vertex copy() {
		return new Vertex(x, y, z);
	}
	
	public double length() {
		return Math.sqrt(x * x + y * y + z * z);
	}
	public Vertex add(Vertex v) {
		return new Vertex(x + v.x, y + v.y, z + v.z);
	}
	public Vertex minus(Vertex v) {
		return new Vertex(x - v.x, y - v.y, z - v.z);
	}
	public Vertex scale(double scale) {
		return new Vertex(scale * x, scale * y, scale * z);
	}
	public void scaleIt(double scale) {
		x = getX() * scale;
		y = getY() * scale;
		z = getZ() * scale;
	}
	public Vertex cross(Vertex v) {
		Vertex cross = new Vertex();
		cross.x = y * v.z - v.y * z;
		cross.y = z * v.x - v.z * x;
		cross.z = x * v.y - v.x * y;
		return cross;
	}
	public double dot(Vertex v) {
		return x * v.x + y * v.y + z * v.z;
	}
	public void normalize() {
		double l = length();
		if (l > 0.0) {
			x = x / l;
			y = y / l;
			z = z / l;
		}
	}

	public double distance(Vertex v) {
		double dx = v.x - x;
		double dy = v.y - y;
		double dz = v.z - z;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	public boolean coincides(Vertex v) {
		double dx = v.x - x;
		double dy = v.y - y;
		double dz = v.z - z;
		if (Math.abs(dx) > 0.0000001) {
			return false;
		}
		if (Math.abs(dy) > 0.0000001) {
			return false;
		}
		if (Math.abs(dz) > 0.0000001) {
			return false;
		}
		return true;
	}
	
	public String toString() {
		return "[" + x + " " + y + " " + z + "]";
	}
}
