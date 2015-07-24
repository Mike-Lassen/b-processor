package com.bprocessor;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class Mesh extends Entity {
    protected String name;
    protected boolean selectable;
    
    public Mesh() {}
    public Mesh(Mesh prototype) {
    	super(prototype);
    	name = prototype.name;
    }
    public Mesh(String name) {
        this.name = name;
        this.selectable = true;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isSelectable() {
    	return selectable;
    }
    public void setSelectable(boolean value) {
    	selectable = value;
    }
    public abstract void accept(ItemVisitor visitor);
    
    public void delete() {
    }
    protected void applyItem(Mesh prototype) {
    	super.applyEntity(prototype);
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
    public void moveIt(double x, double y, double z) {
    	Set<Vertex> vertices = new HashSet<Vertex>();
    	collectVertices(vertices);
        for (Vertex vertex : vertices) {
            vertex.setX(vertex.x + x);
            vertex.setY(vertex.y + y);
            vertex.setZ(vertex.z + z);
        }
    }
    
    public List<Attribute> getAttributes() {
    	List<Attribute> attributes = super.getAttributes();
    	List<Attribute> section = new LinkedList<Attribute>();
    	section.add(new Attribute("Name", new Format() {
			@Override
			public String format() {
				return name;
			}
			
			@Override
			public void apply(String value) {
				name = value;
			}
		}));
    	section.add(new Attribute("Selectable", selectable));
    	attributes.add(new Attribute("Mesh", section));
    	return attributes;
    }
    
    
}

