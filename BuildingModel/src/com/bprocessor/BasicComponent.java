package com.bprocessor;

import java.util.List;

public class BasicComponent extends Item {
    protected List<FaceGroup> groups;
    protected List<Vertex> vertices;

    public BasicComponent() {
    }

    public BasicComponent(String name, List<FaceGroup> groups, List<Vertex> vertices) {
        super(name);
        this.groups = groups;
        this.vertices = vertices;
    }

    public List<FaceGroup> getGroups() {
        return groups;
    }
    public void setGroup(List<FaceGroup> groups) {
        this.groups = groups;
    }
    public List<Vertex> getVertices() {
        return vertices;
    }
    public void setVertices(List<Vertex> vertices) {
        this.vertices = vertices;
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
}
