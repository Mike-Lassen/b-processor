package com.bprocessor;

import java.util.LinkedList;
import java.util.List;

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
    	attributes.add(new Attribute("Component", section));
    	return attributes;
    }
	
	public String label() {
		return name;
	}
}
