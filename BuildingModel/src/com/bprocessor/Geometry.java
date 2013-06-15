package com.bprocessor;

public class Geometry {
    protected int id;
    protected Item owner;

    public Geometry() {

    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Item getOwner() {
        return owner;
    }
    public void setOwner(Item owner) {
        this.owner = owner;
    }
}
