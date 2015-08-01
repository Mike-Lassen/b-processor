package com.bprocessor.ui.actions;

import java.util.LinkedList;
import java.util.List;

import com.bprocessor.Attribute;
import com.bprocessor.Entity;
import com.bprocessor.util.Operation;

public abstract class Action extends Entity {

	public Action() {
	}

	public Action(Entity prototype) {
		super(prototype);
	}

	public abstract void evaluate();
	
	public List<Attribute> getAttributes() {
    	List<Attribute> attributes = super.getAttributes();
    	List<Attribute> section = new LinkedList<Attribute>();
    	section.add(new Attribute("Confirm", new Operation("Confirm") {
			@Override
			public void evaluate() {
				Action.this.evaluate();
			}
		}));
    	attributes.add(new Attribute("Action", section));
    	return attributes;
    }
}
