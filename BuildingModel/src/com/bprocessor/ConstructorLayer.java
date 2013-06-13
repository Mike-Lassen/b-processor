package com.bprocessor;

import java.util.LinkedList;
import java.util.List;

public class ConstructorLayer extends Item {
	protected List<Constructor> constructors;
	
	public ConstructorLayer() {
		
	}
	public ConstructorLayer(String name) {
		super(name);
		constructors = new LinkedList<Constructor>();
	}
	
	public List<Constructor> getConstructors() {
		return constructors;
	}
	public void setConstructors(List<Constructor> constructors) {
		this.constructors = constructors;
	}
	
	public void add(Constructor constructor) {
		constructors.add(constructor);
	}
	public void remove(Constructor constructor) {
		constructors.remove(constructor);
	}
	public void clear() {
		constructors.clear();
	}

	@Override
	public void visit(ItemVisitor visitor) {
		visitor.onConstructorLayer(this);
	}
}
