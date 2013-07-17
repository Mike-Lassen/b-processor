package com.bprocessor;

import java.util.LinkedList;
import java.util.List;

import com.bprocessor.util.Function;
import com.bprocessor.util.Util;

public class Group extends Composite {
    protected List<Surface> surfaces;
    protected List<Edge> edges;
    protected List<Vertex> vertices;

    public Group(){}
    public Group(Group prototype) {
    	super(prototype);
    	surfaces = new LinkedList<Surface>(prototype.surfaces);
    	edges = new LinkedList<Edge>(prototype.edges);
    	vertices = new LinkedList<Vertex>(prototype.vertices);
    }
    public Group(String name) {
        super(name);
        this.surfaces = new LinkedList<Surface>();
        this.edges = new LinkedList<Edge>();
        this.vertices = new LinkedList<Vertex>();
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
    public void remove(Vertex vertex) {
    	if (vertex.owner == this) {
    		vertices.remove(vertex);
    		vertex.owner = null;
    	}
    }
    public void add(Edge edge) {
        if (edge.owner == null) {
            edges.add(edge);
            edge.owner = this;
        }
    }
    public void remove(Edge edge) {
        if (edge.owner == this) {
            edges.remove(edge);
            edge.owner = null;
        }
    }
    public void add(Surface surface) {
        if (surface.owner == null) {
            surfaces.add(surface);
            surface.owner = this;
        }
    }
    public void remove(Surface surface) {
        if (surface.owner == this) {
            surfaces.remove(surface);
            surface.owner = null;
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

    public void insert(Edge edge) {
        add(edge);
        add(edge.from);
        add(edge.to);
    }
    public void insert(Surface surface) {
        add(surface);
        for (Edge current : surface.edges) {
            insert(current);
        }
        for (Surface exterior : surfaces) {
			if (exterior != surface) {
				if (exterior.surrounds(surface)) {
					exterior.add(surface);
				}
			}
		}
    }

    @Override
    public void accept(ItemVisitor visitor) {
        visitor.visit(this);
        super.accept(visitor);
    }

    public String toString() {
        return "[group " + name + "]";
    }
    
    public Memento memento() {
    	return new GroupMemento(this);
    }
    
    protected void applyGroup(Group prototype) {
    	super.applyComposite(prototype);
    	surfaces = prototype.surfaces;
    	for (Surface current : surfaces) {
    		current.owner = this;
    	}
    	edges = prototype.edges;
    	for (Edge current : edges) {
    		current.owner = this;
    	}
    	vertices = prototype.vertices;
    	for (Vertex current : vertices) {
    		current.owner = this;
    	}
    }
    
    
    private static class GroupMemento implements Memento {
    	protected Group group;
    	protected Group copy;
    	protected List<Memento> mementos;
    	
    	public GroupMemento(Group group) {
    		this.group = group;
    		copy = new Group(group);
    		mementos = new LinkedList<Memento>();
			List<Geometry> geometry = new LinkedList<Geometry>();
			geometry.addAll(group.vertices);
			geometry.addAll(group.edges);
			geometry.addAll(group.surfaces);
			Util.map(mementos, geometry, new Function<Memento, Geometry>() {
				public Memento apply(Geometry value) {
					return value.memento();
				}
			});
    	}
    	
    	public void restore() {
    		group.applyGroup(copy);
    		for (Memento current : mementos) {
    			current.restore();
    		}
    	}
    }
}
