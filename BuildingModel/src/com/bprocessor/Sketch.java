package com.bprocessor;

public class Sketch extends Mesh {
    private int uid;
    private Polyhedron group;
    private boolean modified;
    private String path;

    public Sketch() {}
    public Sketch(Sketch prototype) {
    	super(prototype);
    	group = prototype.group;
    }
    public Sketch(String name) {
        super(name);
        this.group = new Polyhedron("Top");
        this.group.owner = this;
    }

    public int getUid() {
        return uid;
    }
    public void setUid(int uid) {
        this.uid = uid;
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
    	super.applyItem(prototype);
    	group = prototype.group;
    }
	@Override
	public void accept(ItemVisitor visitor) {
		
	}
  }
