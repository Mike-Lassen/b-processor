package com.bprocessor;

import java.util.List;

public class Face extends Geometry {
    protected List<Vertex> vertices;
    protected List<Vertex> normals;

    public Face() {

    }
    public Face(List<Vertex> vertices) {
        this.vertices = vertices;
    }
    public Face(List<Vertex> vertices, List<Vertex> normals) {
        this.vertices = vertices;
        this.normals = normals;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }
    public void setVertices(List<Vertex> vertices) {
        this.vertices = vertices;
    }
    public List<Vertex> getNormals() {
        return normals;
    }
    public void setNormals(List<Vertex> normals) {
        this.normals = normals;
    }
}
