package com.bprocessor;

public class Edge  extends Geometry {
    protected Vertex from;
    protected Vertex to;

    public Edge() {}
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

        if (Math.abs(r.getX()) > 0.0000001) {
            t = (q.getX() - p0.getX()) / r.getX();
        } else if (Math.abs(r.getY()) > 0.0000001) {
            t = (q.getY() - p0.getY()) / r.getY();
        } else if (Math.abs(r.getZ()) > 0.0000001) {
            t = (q.getZ() - p0.getZ()) / r.getZ();
        }
        r.scaleIt(t);
        Vertex p = p0.add(r).minus(q);
        if (p.length() < 0.0000001) {
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

    public boolean orthogonal(Edge other) {
        Vertex v1 = to.minus(from);
        Vertex v2 = other.to.minus(other.from);
        double t = Math.abs(v1.dot(v2));
        return (t < 0.0000001);
    }

    public String toString() {
        return "[constructor " + from + " - " + to + "]"; 
    }
}
