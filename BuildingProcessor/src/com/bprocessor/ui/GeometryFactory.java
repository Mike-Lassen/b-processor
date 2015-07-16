package com.bprocessor.ui;

import com.bprocessor.Vertex;

public class GeometryFactory {
	public Vertex vertex(double x, double y, double z) {
		return new Vertex(x, y, z);
	}
	public Vertex vertex(double x, double y) {
		return new Vertex(x, y, 0);
	}
}
