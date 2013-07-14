package com.bprocessor;

public class Line extends Edge {
    protected Color color;

    public Line() {

    }

    public Line(Vertex from, Vertex to, Color color) {
        super(from, to);
        this.color = color;
    }
    public Color getColor() {
        return color;
    }
    public void setColor(Color value) {
        color = value;
    }
    
    public Vertex direction() {
    	return to.minus(from);
    }
    
    public String toString() {
        return "[line " + from + " - " + to + "]"; 
    }
}
