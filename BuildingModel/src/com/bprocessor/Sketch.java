package com.bprocessor;

import java.util.LinkedList;
import java.util.List;

public class Sketch extends Component {
    private int uid;
    private Polyhedron polyhedron;
    private Group group;
    private Grid grid;
    private Camera camera;
    private boolean modified;
    private String path;

    public Sketch() {}
    public Sketch(String name) {
    	super(name);
        this.polyhedron = new Polyhedron("Main");
        this.group = new Group("Top", this.polyhedron);
        this.grid = new Grid("Grid");
        Vertex center = new Vertex(4, 2, 1.3);
		Vertex eye = new Vertex(6, -9, 8);
		Vertex up = new Vertex(0, 0, 1);
		camera = new Camera(center, eye, up);
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
    public Camera getCamera() {
    	return camera;
    }
    public void setCamera(Camera value) {
    	camera = value;
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
    	section.add(new Attribute("Camera", camera));
    	
    	attributes.add(new Attribute("Sketch", section));
    	return attributes;
    }
  }
