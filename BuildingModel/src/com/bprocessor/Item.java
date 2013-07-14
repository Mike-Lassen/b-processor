package com.bprocessor;

public abstract class Item extends Geometry {
    protected String name;

    public Item() {
    }
    public Item(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public abstract void accept(ItemVisitor visitor);
}

