package com.bprocessor;

import java.util.LinkedList;
import java.util.List;

public class Line extends Edge {
    protected Color color;

    public Line() {}
    public Line(Line prototype) {
    	super(prototype);
    	color = prototype.color;
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
