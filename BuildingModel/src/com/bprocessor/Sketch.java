package com.bprocessor;

import java.util.LinkedList;
import java.util.List;

public class Sketch extends Component {
    private int uid;
    private Polyhedron polyhedron;
    private Group group;
    private Grid grid;
    private boolean modified;
    private String path;

    public Sketch() {}
    public Sketch(String name) {
    	super(name);
        this.polyhedron = new Polyhedron("Main");
        this.group = new Group("Top", this.polyhedron);
        this.grid = new Grid("Grid");
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
    public Grid getGrid() {
    	return grid;
    }
    public void setGrid(Grid value) {
    	grid = value;
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
    	super.applyComponent(prototype);
    	polyhedron = prototype.polyhedron;
    }
    
	@Override
	public Mesh display() {
		Composite mesh = new Composite("Constructed");
		mesh.add(grid.display());
		mesh.add(group.display());
		return mesh;
	}
	
	public List<Attribute> getAttributes() {
    	List<Attribute> attributes = super.getAttributes();
    	List<Attribute> section = new LinkedList<Attribute>();
    	section.add(new Attribute("Content", group));
    	section.add(new Attribute("Grid", grid));
    	
    	attributes.add(new Attribute("Sketch", section));
    	return attributes;
    }
  }
