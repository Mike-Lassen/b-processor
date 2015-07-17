package com.bprocessor;

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
}

