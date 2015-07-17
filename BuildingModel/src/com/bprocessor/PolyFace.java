package com.bprocessor;

import java.util.LinkedList;
import java.util.List;

public class PolyFace extends Item {
    protected List<Face> faces;
    protected Material material;

    public PolyFace() {

    }
    public PolyFace(String name) {
    	super(name);
        faces = new LinkedList<Face>();
    }
    public PolyFace(List<Face> faces) {
        this.faces = faces;
    }

    public void add(Face face) {
        faces.add(face);
    }
    public void remove(Face face){
        faces.remove(face);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<Face> getFaces() {
        return faces;
    }
    public void setFaces(List<Face> faces) {
        this.faces = faces;
    }
    public Material getMaterial() {
        return material;
    }
    public void setMaterial(Material material) {
        this.material = material;
    }
	@Override
	public void accept(ItemVisitor visitor) {
	}
}
