package com.bprocessor;

import java.util.LinkedList;
import java.util.List;

public class Composite<T extends Item> extends Item {
    protected List<T> items;

    public Composite() {		
    }

    public Composite(String name) {
        super(name);
        items = new LinkedList<T>();
    }

    public void add(T child) {
        items.add(child);
    }
    public void remove(T child) {
        items.remove(child);
    }

    public List<T> getItems() {
        return items;
    }
    public void setItems(List<T> items) {
        this.items = items;
    }

    @Override
    public void visit(ItemVisitor visitor) {
        for (Item current : items) {
            current.visit(visitor);
        }
    }
}
