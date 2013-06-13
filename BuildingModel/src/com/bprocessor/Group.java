package com.bprocessor;

import java.util.LinkedList;
import java.util.List;

public class Group extends Composite {
	protected List<Surface> surfaces;
	protected List<Edge> edges;
	protected List<Vertex> vertices;
	
	public Group(){
		
	}
	public Group(String name) {
		super(name);
		this.surfaces = new LinkedList();
		this.edges = new LinkedList();
		this.vertices = new LinkedList();
	}
	
	public List<Vertex> getVertices() {
		return vertices;
	}
	public void setVertices(List<Vertex> vertices) {
		this.vertices = vertices;
	}
	public List<Edge> getEdges() {
		return edges;
	}
	public void setEdges(List<Edge> edges) {
		this.edges = edges;
	}
	public List<Surface> getSurfaces() {
		return surfaces;
	}
	public void setSurfaces(List<Surface> surfaces) {
		this.surfaces = surfaces;
	}
	
	public void add(Vertex vertex) {
		if (vertex.owner == null) {
			vertices.add(vertex);
			vertex.owner = this;
		}
	}
	public void add(Edge edge) {
		if (edge.owner == null) {
			edges.add(edge);
			edge.owner = this;
		}
	}
	public void add(Surface surface) {
		if (surface.owner == null) {
			surfaces.add(surface);
			surface.owner = this;
		}
	}
	
	public void clear() {
		for (Surface current : surfaces) {
			current.owner = null;
		}
		surfaces.clear();
		for (Edge current : edges) {
			current.owner = null;
		}
		edges.clear();
		for (Vertex current : vertices) {
			current.owner = null;
		}
		vertices.clear();
	}
	
	public Vertex findVertex(Vertex vertex) {
		return Vertex.findVertex(vertices, vertex);
	}
	
	public Vertex insert(Vertex vertex) {
		Vertex actual = findVertex(vertex);
		if (actual == null) {
			add(vertex);
			return vertex;
		} else {
			return actual;
		}
	}
	
	public void addAll(Edge edge) {
		add(edge);
		add(edge.from);
		add(edge.to);
	}
	public void addAll(Surface surface) {
		add(surface);
		for (Edge current : surface.edges) {
			addAll(current);
		}
	}
	
	@Override
	public void visit(ItemVisitor visitor) {
		visitor.onGroup(this);
		super.visit(visitor);
	}
	
	public String toString() {
		return "[group " + name + "]";
	}
}
