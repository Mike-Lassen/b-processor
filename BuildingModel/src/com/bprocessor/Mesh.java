package com.bprocessor;

import java.util.HashSet;
import java.util.Set;

public abstract class Mesh extends Geometry {
    protected String name;

    public Mesh() {}
    public Mesh(Mesh prototype) {
    	super(prototype);
    	name = prototype.name;
    }
    public Mesh(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public abstract void accept(ItemVisitor visitor);
    
    public void delete() {
    	if (owner instanceof Composite) {
    		Composite composite = (Composite) owner;
    		composite.remove(this);
    	}
    }
    protected void applyItem(Mesh prototype) {
    	super.applyGeometry(prototype);
    	name = prototype.name;
    }
    
    public void collectVertices(Set<Vertex> vertices) {
    	
    }
    
    public void scaleIt(double factor) {
    	Set<Vertex> vertices = new HashSet<Vertex>();
    	collectVertices(vertices);
        for (Vertex vertex : vertices) {
            vertex.scaleIt(factor);
        }
    }
    
}

