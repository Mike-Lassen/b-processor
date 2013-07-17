package com.bprocessor;

import java.util.LinkedList;
import java.util.List;

public class Composite extends Item {
    protected List<Item> items;

    public Composite() {}
    public Composite(Composite prototype) {
    	super(prototype);
    	items = new LinkedList<Item>(prototype.items);
    }
    public Composite(String name) {
        super(name);
        items = new LinkedList<Item>();
    }

    public void add(Item child) {
        items.add(child);
        child.owner = this;
    }
    public void remove(Item child) {
        items.remove(child);
        child.owner = null;
    }

    public List<Item> getItems() {
        return items;
    }
    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public void accept(ItemVisitor visitor) {
        for (Item current : items) {
            current.accept(visitor);
        }
    }
    protected void applyComposite(Composite prototype) {
    	super.applyItem(prototype);
    	items = prototype.items;
    }
}
