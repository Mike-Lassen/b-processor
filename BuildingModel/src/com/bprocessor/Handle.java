package com.bprocessor;

public class Handle extends Vertex {
	private Color color;
	
	public Handle(double x, double y, double z, Color color) {
		super(x, y, z);
		this.color = color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	public Color getColor() {
		return color;
	}
	
}
