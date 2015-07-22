package com.bprocessor;

public class Sketch extends Component {
    private int uid;
    private Polyhedron polyhedron;
    private Net grid;
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
        this.grid = new Net("Main");
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
    public Net getGrid() {
    	return grid;
    }
    public void setGrid(Net value) {
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
		mesh.add(grid);
		mesh.add(polyhedron);
		return mesh;
	}
  }
