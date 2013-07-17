package com.bprocessor;

public abstract class Item extends Geometry {
    protected String name;

    public Item() {}
    public Item(Item prototype) {
    	super(prototype);
    	name = prototype.name;
    }
    public Item(String name) {
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
    	if (owner instanceof Group) {
    		Group group = (Group) owner;
    		group.remove(this);
    	}
    }
    protected void applyItem(Item prototype) {
    	super.applyGeometry(prototype);
    	name = prototype.name;
    }
}

