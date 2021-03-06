package com.bprocessor;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.bprocessor.util.Function;
import com.bprocessor.util.Util;

public class Polyhedron extends Mesh {
    protected List<Surface> surfaces;
    protected List<Edge> edges;
    protected List<Vertex> vertices;

    public Polyhedron(){}
    public Polyhedron(Polyhedron prototype) {
    	super(prototype);
    	surfaces = new LinkedList<Surface>(prototype.surfaces);
    	edges = new LinkedList<Edge>(prototype.edges);
    	vertices = new LinkedList<Vertex>(prototype.vertices);
    }
    public Polyhedron(String name) {
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
            if (vertex.getId() == 0) {
            	vertex.setId(Vertex.nextID());
            }
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
    }

    public String toString() {
        return "[Polyhedron " + name + "]";
    }
    
    public Memento memento() {
    	return new PolyhedronMemento(this);
    }
    
    protected void applyPolyhedron(Polyhedron prototype) {
    	super.applyItem(prototype);
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
    
    
    private static class PolyhedronMemento implements Memento {
    	protected Polyhedron poly;
    	protected Polyhedron copy;
    	protected List<Memento> mementos;
    	
    	public PolyhedronMemento(Polyhedron poly) {
    		this.poly = poly;
    		copy = new Polyhedron(poly);
    		mementos = new LinkedList<Memento>();
			List<Geometry> geometry = new LinkedList<Geometry>();
			geometry.addAll(poly.vertices);
			geometry.addAll(poly.edges);
			geometry.addAll(poly.surfaces);
			Util.map(mementos, geometry, new Function<Memento, Geometry>() {
				public Memento apply(Geometry value) {
					return value.memento();
				}
			});
    	}
    	
    	public void restore() {
    		poly.applyPolyhedron(copy);
    		for (Memento current : mementos) {
    			current.restore();
    		}
    	}
    }
    
    public List<Attribute> getAttributes() {
    	List<Attribute> attributes = super.getAttributes();
    	List<Attribute> section = new LinkedList<Attribute>();
    	section.add(new Attribute("Surfaces", surfaces));    	
    	attributes.add(new Attribute("Polyhedron", section));
    	return attributes;
    }
	@Override
	public void collectVertices(Set<Vertex> vertices) {
		vertices.addAll(getVertices());
	}
}
