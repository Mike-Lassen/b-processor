package com.bprocessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class Surface extends Geometry {
    protected List<Edge> edges;
    protected Surface exterior;
    protected Set<Surface> holes;
    protected boolean visible;

    public Surface() {}
    public Surface(List<Edge> edges) {
        this.edges = edges;
        visible = true;
    }

    public List<Edge> getEdges() {
        return edges;
    }
    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public void add(Surface hole) {
        if(holes == null) {
            holes = new HashSet<Surface>();
        }
        holes.add(hole);
        hole.exterior = this;
    }
    public void remove(Surface hole) {
        if (hole.exterior == this) {
            holes.remove(hole);
            hole.exterior = null;
        }
    }
    public Set<Surface> getHoles() {
        if (holes != null) {
            return holes;
        } else {
            return Collections.emptySet();
        }
    }
    public void setHoles(Set<Surface> holes) {
        this.holes = holes;
    }
    public Surface getExterior() {
        return exterior;
    }
    public void setExterior(Surface exterior) {
        this.exterior = exterior;
    }
    public boolean isVisible() {
        return visible;
    }
    public void setVisible(boolean value) {
        visible = value;
    }

    public List<Vertex> getVertices() {
        List<Vertex> vertices = new ArrayList<Vertex>();
        Edge e1 = edges.get(0);
        Edge e2 = edges.get(1);
        Vertex current = null;
        if (e2.contains(e1.from)) {
            current = e1.to;
        } else {
            current = e1.from;
        }
        for (Edge edge : edges) {
            vertices.add(current);
            current = edge.otherVertex(current);
        }
        return vertices;
    }

    public Vertex normal() {
        /**
         * Newell's method
         */
        List<Vertex> vertices = getVertices();
        Vertex normal = new Vertex(0, 0, 0);
        for (int i = 0; i < vertices.size(); i++) {
            Vertex current = vertices.get(i);
            Vertex next = vertices.get((i + 1 ) % vertices.size());
            normal.x = normal.x + (current.y - next.y) * (current.z + next.z);
            normal.y = normal.y + (current.z - next.z) * (current.x + next.x);
            normal.z = normal.z + (current.x - next.x) * (current.y + next.y);
        }
        normal.normalize();
        return normal;
    }

    public Surface extrude(Vertex normal, double delta, Collection<Surface> sides) {
        normal = normal.scale(delta);
        List vertices = getVertices();
        int n = vertices.size();
        Vertex[] v = new Vertex[n];
        Edge[] e = new Edge[n];
        Vertex[] vmap = new Vertex[n];
        Edge[] topmap = new Edge[n];
        Edge[] sidemap = new Edge[n];
        Surface[] facemap = new Surface[n];
        Surface top = null;
        vertices.toArray(v);
        edges.toArray(e);

        for (int i = 0; i < n; i++) {
            vmap[i] = v[i].add(normal);
        }

        for (int i = 0; i < n; i++) {
            topmap[i] = new Edge(vmap[i], vmap[(i + 1) % n]);
        }

        for (int i = 0; i < n; i++) {
            sidemap[i] = new Edge(v[i], vmap[i]);
        }

        for (int i = 0; i < n; i++) {
            Edge b = e[i];
            Edge r = sidemap[i];
            Edge l = sidemap[(i + 1) % n];
            Edge t = topmap[i];
            List newEdges = new LinkedList();
            newEdges.add(r);
            newEdges.add(t);
            newEdges.add(l);
            newEdges.add(b);
            facemap[i] = new Surface(newEdges);
            sides.add(facemap[i]);
        }

        {
            List newEdges = new LinkedList();
            for (int i = 0; i < n; i++) {
                newEdges.add(topmap[n - i - 1]);
            }
            top = new Surface(newEdges);
        }
        return top;
    }
    public Surface extrudeAll(Vertex normal, double delta, Collection<Surface> sides, Collection<Surface> tops) {
        Surface top = extrude(normal, delta, sides);
        tops.add(top);
        for (Surface hole : getHoles()) {
            Surface holetop;
            holetop = hole.extrude(normal, delta, sides);
            top.add(holetop);
            tops.add(holetop);
        }
        return top;
    }
    public boolean surrounds(Vertex vertex) {
        int count = 0;
        for (Edge edge : edges) {
            Vertex from = edge.getFrom();
            Vertex to = edge.getTo();
            Vertex A = null;
            Vertex B = null;
            if (from.y < to.y) {
                A = from;
                B = to;
            } else {
                A = to;
                B = from;
            }
            Vertex C = vertex;
            if (A.y <= C.y && C.y < B.y) {
                double d = (B.x - A.x) * (C.y - A.y) - (B.y - A.y) * (C.x - A.x);
                if (d >= 0) {
                    count++;
                }
            }
        }
        return !(count % 2 == 0);
    }
    public boolean surrounds(Surface hole) {
        for (Vertex vertex : hole.getVertices()) {
            if (!surrounds(vertex)) {
                return false;
            }
        }
        return true;
    }
}