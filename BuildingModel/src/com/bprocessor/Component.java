package com.bprocessor;

public abstract class Component extends Entity {
	public Component() {}

	public Component(Entity prototype) {
		super(prototype);
	}
	
	public abstract Mesh display();

}
