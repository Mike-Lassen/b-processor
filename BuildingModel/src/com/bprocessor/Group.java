package com.bprocessor;

import java.util.LinkedList;
import java.util.List;

public class Group extends Component {
	private Mesh mesh;
	public Group() { }

	public Group(Entity prototype) {
		super(prototype);
	}
	
	public Group(String name, Mesh mesh) {
		super(name);
		this.mesh = mesh;
	}

	@Override
	public Mesh display() {
		return mesh;
	}
	
	public List<Attribute> getAttributes() {
    	List<Attribute> attributes = super.getAttributes();
    	List<Attribute> section = new LinkedList<Attribute>();
    	section.add(new Attribute("Mesh", mesh));
    	attributes.add(new Attribute("Group", section));
    	return attributes;
    }
	
}
