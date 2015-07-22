package com.bprocessor;

import java.util.LinkedList;
import java.util.List;

public class Entity {
	protected int id;
	
	public Entity() {}
	public Entity(Entity prototype) {
    }
	
	
	public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    public Memento memento() {
    	return null;
    }
    protected void applyEntity(Entity prototype) {
    }
    
    public List<Attribute> getAttributes() {
    	List<Attribute> attributes = new LinkedList<Attribute>();
    	return attributes;
    }
}
