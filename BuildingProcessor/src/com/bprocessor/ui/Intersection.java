package com.bprocessor.ui;

import com.bprocessor.Vertex;

public class Intersection {
	public static final String PLANE = "plane";
	public static final String VERTEX = "vertex";
	public static final String EDGE = "edge";
	public static final String SURFACE = "surface";
	
	private Vertex vertex;
	private String type;
	
	public Intersection(Vertex vertex, String type) {
		this.vertex = vertex;
		this.type = type;
	}
	
	public void setVertex(Vertex value) {
		vertex = value;
	}
	public Vertex getVertex() {
		return vertex;
	}
	
	public void setType(String value) {
		type = value;
	}
	public String getType() {
		return type;
	}
}
