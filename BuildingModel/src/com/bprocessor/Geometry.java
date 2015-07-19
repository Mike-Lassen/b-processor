package com.bprocessor;

import java.util.LinkedList;
import java.util.List;

public abstract class Geometry {
	public static final double EPSILON = 0.0000001;
    protected int id;
    protected Mesh owner;

    public Geometry() { }
    public Geometry(Geometry prototype) {
    }
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Mesh getOwner() {
        return owner;
    }
    public void setOwner(Mesh owner) {
        this.owner = owner;
    }
    public void delete() {
    	
    }
    public Memento memento() {
    	return null;
    }
    protected void applyGeometry(Geometry prototype) {
    }
    
    public List<Attribute> getAttributes() {
    	List<Attribute> attributes = new LinkedList<Attribute>();
    	List<Attribute> section = new LinkedList<Attribute>();
    	section.add(new Attribute("Owner", owner));
    	attributes.add(new Attribute("Geometry", section));
    	return attributes;
    }
}
