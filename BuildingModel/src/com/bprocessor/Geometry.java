package com.bprocessor;

public class Geometry {
	protected long id;
	protected Item owner;
	
	public Geometry() {
		
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Item getOwner() {
		return owner;
	}
	public void setOwner(Item owner) {
		this.owner = owner;
	}
}
