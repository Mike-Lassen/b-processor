package com.bprocessor;

import java.util.LinkedList;
import java.util.List;

public class Edge  extends Geometry {
    protected Vertex from;
    protected Vertex to;

    public Edge() {}
    public Edge(Edge prototype) {
    	super(prototype);
    	from = prototype.from;
    	to = prototype.to;
    }
    public Edge(Vertex from, Vertex to) {
        this.from = from;
        this.to = to;
    }

    public void setFrom(Vertex vertex) {
        from = vertex;
    }
    public Vertex getFrom() {
        return from;
    }

    public void setTo(Vertex vertex) {
        to = vertex;
    }
    public Vertex getTo() {
        return to;
    }

    public boolean contains(Vertex vertex) {
        return (vertex == from) || (vertex == to);
    }
    public Vertex otherVertex(Vertex vertex) {
        if (vertex == from) {
            return to;
        }
        if (vertex == to) {
            return from;
        }
        return null;
    }

    public double length() {
        return from.distance(to);
    }

    public boolean intersects(Vertex vertex) {
        Vertex p0 = getFrom();
        Vertex p1 = getTo();
        Vertex r = p1.minus(p0);
        Vertex q = vertex;
        double t = 0.0;

        if (Math.abs(r.getX()) > EPSILON) {
            t = (q.getX() - p0.getX()) / r.getX();
        } else if (Math.abs(r.getY()) > EPSILON) {
            t = (q.getY() - p0.getY()) / r.getY();
        } else if (Math.abs(r.getZ()) > EPSILON) {
            t = (q.getZ() - p0.getZ()) / r.getZ();
        }
        r.scaleIt(t);
        Vertex p = p0.add(r).minus(q);
        if (p.length() < EPSILON) {
            return true;
        }
        return false;
    }

    public Vertex intersection(Vertex vertex) {
        if (intersects(vertex)) {
            return vertex;
        } else {
            Vertex v = to.minus(from);
            double length = length();
            Vertex w = vertex.minus(from);
            double t = v.dot(w);
            v.scaleIt(t / (length * length));
            return from.add(v);
        }
    }
    public Vertex closest(Edge ray) {
    	Edge edge = shortestEdge(ray);
    	if (edge != null) {
    		return edge.getFrom();
    	} else {
    		return null;
    	}
    }
    public Vertex intersection(Edge ray) {
    	Edge edge = shortestEdge(ray);
    	if (edge != null) {
    		if (edge.length() < EPSILON) {
    			return edge.getFrom();
    		} else {
    			return null;
    		}
    	} else {
    		return null;
    	}
    }
    
    public Edge shortestEdge(Edge other) {
        Vertex p1 = this.getFrom();
        Vertex p2 = this.getTo();
        Vertex p3 = other.getFrom();
        Vertex p4 = other.getTo();
        
        Vertex pa = new Vertex();
        Vertex pb = new Vertex();
        
        Vertex p13 = new Vertex();
        Vertex p43 = new Vertex();
        Vertex p21 = new Vertex();
        double d1343, d4321, d1321, d4343, d2121;
        double numer, denom;
        double mua, mub;
        double eps = EPSILON;
        p13.setX(p1.getX() - p3.getX());
        p13.setY(p1.getY() - p3.getY());
        p13.setZ(p1.getZ() - p3.getZ());
        p43.setX(p4.getX() - p3.getX());
        p43.setY(p4.getY() - p3.getY());
        p43.setZ(p4.getZ() - p3.getZ());
        if (Math.abs(p43.getX())  < eps && Math.abs(p43.getY())  < eps && Math.abs(p43.getZ())  < eps) {
          return null;
        }
        p21.setX(p2.getX() - p1.getX());
        p21.setY(p2.getY() - p1.getY());
        p21.setZ(p2.getZ() - p1.getZ());
        if (Math.abs(p21.getX())  < eps && Math.abs(p21.getY())  < eps && Math.abs(p21.getZ())  < eps) {
          return null;
        }

        d1343 = p13.getX() * p43.getX() + p13.getY() * p43.getY() + p13.getZ() * p43.getZ();
        d4321 = p43.getX() * p21.getX() + p43.getY() * p21.getY() + p43.getZ() * p21.getZ();
        d1321 = p13.getX() * p21.getX() + p13.getY() * p21.getY() + p13.getZ() * p21.getZ();
        d4343 = p43.getX() * p43.getX() + p43.getY() * p43.getY() + p43.getZ() * p43.getZ();
        d2121 = p21.getX() * p21.getX() + p21.getY() * p21.getY() + p21.getZ() * p21.getZ();

        denom = d2121 * d4343 - d4321 * d4321;
        if (Math.abs(denom) < eps) {
          return null;
        }
        numer = d1343 * d4321 - d1321 * d4343;

        mua = numer / denom;
        mub = (d1343 + d4321 * (mua)) / d4343;

        pa.setX(p1.getX() + mua * p21.getX());
        pa.setY(p1.getY() + mua * p21.getY());
        pa.setZ(p1.getZ() + mua * p21.getZ());
        pb.setX(p3.getX() + mub * p43.getX());
        pb.setY(p3.getY() + mub * p43.getY());
        pb.setZ(p3.getZ() + mub * p43.getZ());

        return new Edge(pa, pb);
      }
      

    public boolean orthogonal(Edge other) {
        Vertex v1 = to.minus(from);
        Vertex v2 = other.to.minus(other.from);
        double t = Math.abs(v1.dot(v2));
        return (t < EPSILON);
    }
    public boolean parallel(Edge other) {
    	Vertex v1 = to.minus(from);
        Vertex v2 = other.to.minus(other.from);
        Vertex cross = v1.cross(v2);
        return cross.length() < EPSILON;
    }

    public List<Surface> connectedSurfaces() {
    	List<Surface> surfaces = new LinkedList<Surface>();
    	if (owner instanceof Polyhedron) {
    		Polyhedron group = (Polyhedron) owner;
    		for (Surface current : group.surfaces) {
    			if (current.contains(this)) {
    				surfaces.add(current);
    			}
    		}
    	}
    	return surfaces;
    }
    
    public void delete() {
    	if (owner instanceof Polyhedron) {
    		List<Surface> surfaces = connectedSurfaces();
    		Polyhedron group = (Polyhedron) owner;
    		group.remove(this);
    		for (Surface current : surfaces) {
    			current.delete();
    		}
    	}
    }
    
    public String toString() {
        return "[edge " + from + " - " + to + "]"; 
    }
    
    protected void applyEdge(Edge prototype) {
    	super.applyGeometry(prototype);
    	from = prototype.from;
    	to = prototype.to;
    }
    
    public Memento memento() {
		return new EdgeMemento(this);
	}
	
	private static class EdgeMemento implements Memento {
		private Edge edge;
		private Edge copy;
		public EdgeMemento(Edge edge) {
			this.edge = edge;
			copy = new Edge(edge);
		}
		public void restore() {
			edge.applyEdge(copy);
		}
	}
}
