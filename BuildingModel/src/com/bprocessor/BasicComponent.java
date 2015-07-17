package com.bprocessor;

import java.util.List;

public class BasicComponent extends Mesh {
    protected List<PolyFace> groups;
    protected List<Vertex> vertices;

    public BasicComponent() {}
    public BasicComponent(BasicComponent prototype) {
    	super(prototype);
    	groups = prototype.groups;
    	vertices = prototype.vertices;
    }
    public BasicComponent(String name, List<PolyFace> groups, List<Vertex> vertices) {
        super(name);
        this.groups = groups;
        this.vertices = vertices;
    }

    public List<PolyFace> getGroups() {
        return groups;
    }
    public void setGroup(List<PolyFace> groups) {
        this.groups = groups;
    }

    public void scaleIt(double factor) {
        for (Vertex vertex : vertices) {
            vertex.scaleIt(factor);
        }
    }

    @Override
    public void accept(ItemVisitor visitor) {
        visitor.visit(this);
    }
    protected void applyBasicComponent(BasicComponent prototype) {
    	super.applyItem(prototype);
    	groups = prototype.groups;
    	vertices = prototype.vertices;
    }
}
