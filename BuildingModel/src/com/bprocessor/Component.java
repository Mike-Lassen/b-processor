package com.bprocessor;

public abstract class Component extends Entity {
	protected String name;
	public Component() {}

	public Component(Entity prototype) {
		super(prototype);
	}
	
	public Component(String name) {
		this.name = name;
	}

	public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
	public abstract Mesh display();

	public void applyComponent(Component prototype) {
		name = prototype.name;
	}
}
