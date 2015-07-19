package com.bprocessor;

public class Sketch extends Geometry {
    private int uid;
    private String name;
    private Polyhedron group;
    private boolean modified;
    private String path;

    public Sketch() {}
    public Sketch(Sketch prototype) {
    	super(prototype);
    	name = prototype.name;
    	group = prototype.group;
    }
    public Sketch(String name) {
        this.name = name;
        this.group = new Polyhedron("Top");
    }

    public int getUid() {
        return uid;
    }
    public void setUid(int uid) {
        this.uid = uid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Polyhedron getGroup() {
        return group;
    }
    public void setGroup(Polyhedron group) {
        this.group = group;
    }
    public boolean isModified() {
        return modified;
    }
    public void setModified(boolean modified) {
        this.modified = modified;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    protected void applySketch(Sketch prototype) {
    	super.applyGeometry(prototype);
    	name = prototype.name;
    	group = prototype.group;
    }
  }
