package com.bprocessor;

public class Color {
	protected float red;
	protected float green;
	protected float blue;
	
	public Color() {
		
	}
	
	public Color(double r, double g, double b) {
		red = (float) r;
		green = (float) g;
		blue = (float) b;
	}
	
	public float getRed() {
		return red;
	}
	public void setRed(float red) {
		this.red = red;
	}
	public float getGreen() {
		return green;
	}
	public void setGreen(float green) {
		this.green = green;
	}
	public float getBlue() {
		return blue;
	}
	public void setBlue(float blue) {
		this.blue = blue;
	}
	
	public float[] values() {
		return new float[]{red, green, blue};
	}
}
