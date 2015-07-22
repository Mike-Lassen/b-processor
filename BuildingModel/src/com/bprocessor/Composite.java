package com.bprocessor;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Composite extends Mesh {
    protected List<Mesh> items;

    public Composite() {}
    public Composite(Composite prototype) {
    	super(prototype);
    	items = new LinkedList<Mesh>(prototype.items);
    }
    public Composite(String name) {
        super(name);
        items = new LinkedList<Mesh>();
    }

    public void add(Mesh child) {
        items.add(child);
    }
    public void remove(Mesh child) {
        items.remove(child);
    }

    public List<Mesh> getItems() {
        return items;
    }
    public void setItems(List<Mesh> items) {
        this.items = items;
    }

    @Override
    public void accept(ItemVisitor visitor) {
        for (Mesh current : items) {
            current.accept(visitor);
        }
    }
    protected void applyComposite(Composite prototype) {
    	super.applyItem(prototype);
    	items = prototype.items;
    }
    
    public void collectVertices(Set<Vertex> vertices) {
    	for (Mesh current : items) {
    		current.collectVertices(vertices);
    	}
    }
}
