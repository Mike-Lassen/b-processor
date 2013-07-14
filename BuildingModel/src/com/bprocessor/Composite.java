package com.bprocessor;

import java.util.LinkedList;
import java.util.List;

public class Composite extends Item {
    protected List<Item> items;

    public Composite() {		
    }

    public Composite(String name) {
        super(name);
        items = new LinkedList<Item>();
    }

    public void add(Item child) {
        items.add(child);
    }
    public void remove(Item child) {
        items.remove(child);
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
}
