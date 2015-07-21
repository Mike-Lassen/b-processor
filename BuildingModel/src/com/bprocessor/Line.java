package com.bprocessor;

import java.util.LinkedList;
import java.util.List;

public class Line extends Edge {
    protected Color color;
    protected boolean stippled;
    protected float width;

    public Line() {}
    public Line(Line prototype) {
    	super(prototype);
    	color = prototype.color;
    }
    public Line(Vertex from, Vertex to, Color color) {
        super(from, to);
        this.color = color;
        this.width = 1.0f;
        this.stippled = false;
    }
    
    public Color getColor() {
        return color;
    }
    public void setColor(Color value) {
        color = value;
    }
    public float getWidth() {
    	return width;
    }
    public void setWidth(float value) {
    	width = value;
    }
    public boolean getStippled() {
    	return stippled;
    }
    public void setStippled(boolean value) {
    	stippled = value;
    }
    
    public Vertex direction() {
    	return to.minus(from);
    }
    
    public String toString() {
        return "[line " + from + " - " + to + "]"; 
    }
    
    public List<Attribute> getAttributes() {
    	List<Attribute> attributes = super.getAttributes();
    	List<Attribute> section = new LinkedList<Attribute>();
    	section.add(new Attribute("Color", color));
    	attributes.add(new Attribute("Line", section));
    	return attributes;
    }
    
    public void applyLine(Line prototype) {
    	super.applyEdge(prototype);
    	color = prototype.color;
    }
}
