package com.bprocessor;

import java.util.LinkedList;
import java.util.List;

public class Group extends Component {
	private Mesh mesh;
	private List<Component> children;
	public Group() { }

	public Group(Group prototype) {
		super(prototype);
	}
	
	public Group(String name, Mesh mesh) {
		super(name);
		this.mesh = mesh;
		this.children = new LinkedList<Component>();
	}

	@Override
	public Mesh display() {
		Composite composite = new Composite("");
		composite.add(mesh);
		composite.setTag(this);
		for (Component current : children) {
			composite.add(current.display());
		}
		return composite;
	}
	
	public List<Attribute> getAttributes() {
    	List<Attribute> attributes = super.getAttributes();
    	List<Attribute> section = new LinkedList<Attribute>();
    	section.add(new Attribute("Mesh", mesh));
    	attributes.add(new Attribute("Group", section));
    	return attributes;
    }
	
}
