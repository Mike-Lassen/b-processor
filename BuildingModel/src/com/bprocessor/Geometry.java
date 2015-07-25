package com.bprocessor;

import java.util.LinkedList;
import java.util.List;

public abstract class Geometry extends Entity {
	public static final double EPSILON = 0.0000001;
    protected Mesh owner;

    public Geometry() { }
    public Geometry(Geometry prototype) {
    }
    
    public Mesh getOwner() {
        return owner;
    }
    public void setOwner(Mesh owner) {
        this.owner = owner;
    }
    public void delete() {
    	
    }
    protected void applyGeometry(Geometry prototype) {
    	super.applyEntity(prototype);
    }
    
    public List<Attribute> getAttributes() {
    	List<Attribute> attributes = super.getAttributes();
    	List<Attribute> section = new LinkedList<Attribute>();
    	section.add(new Attribute("Owner", owner));
    	section.add(new Attribute("ID", id));
    	attributes.add(new Attribute("Geometry", section));
    	return attributes;
    }
}
