package com.bprocessor;

public class Sketch extends Mesh {
    private int uid;
    private Polyhedron polyhedron;
    private boolean modified;
    private String path;

    public Sketch() {}
    public Sketch(Sketch prototype) {
    	super(prototype);
    	polyhedron = prototype.polyhedron;
    }
    public Sketch(String name) {
        super(name);
        this.polyhedron = new Polyhedron("Top");
        this.polyhedron.owner = this;
    }

    public int getUid() {
        return uid;
    }
    public void setUid(int uid) {
        this.uid = uid;
    }
    public Polyhedron getPolyhedron() {
        return polyhedron;
    }
    public void setPolyhedron(Polyhedron value) {
        this.polyhedron = value;
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
    	super.applyItem(prototype);
    	polyhedron = prototype.polyhedron;
    }
	@Override
	public void accept(ItemVisitor visitor) {
		
	}
  }
