package com.bprocessor;

public class Geometry {
	public static final double EPSILON = 0.0000001;
    protected int id;
    protected Item owner;

    public Geometry() {

    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Item getOwner() {
        return owner;
    }
    public void setOwner(Item owner) {
        this.owner = owner;
    }
    
    public void delete() {
    	
    }
}
