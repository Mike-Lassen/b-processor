package com.bprocessor;

public class Constructor extends Edge {
    protected Color color;

    public Constructor() {

    }

    public Constructor(Vertex from, Vertex to, Color color) {
        super(from, to);
        this.color = color;
    }
    public Color getColor() {
        return color;
    }
    public void setColor(Color value) {
        color = value;
    }
    
    public String toString() {
        return "[constructor " + from + " - " + to + "]"; 
    }
}
