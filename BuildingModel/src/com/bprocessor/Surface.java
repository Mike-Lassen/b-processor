package com.bprocessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.bprocessor.util.Plane;


public class Surface extends Geometry {
	protected List<Edge> edges;
	protected Surface exterior;
	protected Set<Surface> holes;
	protected boolean visible;

	public Surface() {}
	public Surface(Surface prototype) {
		super(prototype);
		edges = new LinkedList<Edge>(prototype.edges);
		exterior = prototype.exterior;
		if (prototype.holes != null) {
			holes = new HashSet<Surface>(prototype.holes);
		}
		visible = prototype.visible;
	}
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
	public Plane plane() {
		double d = 0;
		Vertex n = normal();
		if (n != null) {
			double a = n.getX();
			double b = n.getY();
			double c = n.getZ();
			Edge e1 = (Edge) edges.get(0);
			Vertex v1 = e1.getFrom();
			d = -(a * v1.getX() + b * v1.getY() + c * v1.getZ());
			return new Plane(a, b, c, d);
		} else {
			return null;
		}
	}

	public boolean contains(Edge edge) {
		return edges.contains(edge);
	}
	
	public Surface extrude(Vertex normal, double delta, Collection<Surface> sides) {
		normal = normal.scale(delta);
		List<Vertex> vertices = getVertices();
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
			List<Edge> newEdges = new LinkedList<Edge>();
			newEdges.add(r);
			newEdges.add(t);
			newEdges.add(l);
			newEdges.add(b);
			facemap[i] = new Surface(newEdges);
			sides.add(facemap[i]);
		}

		{
			List<Edge> newEdges = new LinkedList<Edge>();
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
	public boolean surrounds(Vertex vertex, Coord X, Coord Y) {
		Plane plane = plane();
		if (!plane.contains(vertex)) {
			return false;
		}

		int count = 0;
		for (Edge edge : edges) {
			Vertex from = edge.getFrom();
			Vertex to = edge.getTo();
			Vertex A = null;
			Vertex B = null;
			if (from.get(Y) < to.get(Y)) {
				A = from;
				B = to;
			} else {
				A = to;
				B = from;
			}
			Vertex C = vertex;
			if (A.get(Y) <= C.get(Y) && C.get(Y) < B.get(Y)) {
				double d = (B.get(X) - A.get(X)) * (C.get(Y) - A.get(Y)) - (B.get(Y) - A.get(Y)) * (C.get(X) - A.get(X));
				if (d >= 0) {
					count++;
				}
			}
		}
		return !(count % 2 == 0);
	}
	public boolean surrounds(Surface hole) {
		Vertex n = normal();
		Coord X = Coord.X;
		Coord Y = Coord.Y;
		double x = Math.abs(n.x);
		double y = Math.abs(n.y);
		double z = Math.abs(n.z);
		if (y > x) {
			if (y > z) {
				Y = Coord.Z;
			}
		} else {
			if (x > z) {
				X = Coord.Z;
			}
		}
		for (Vertex vertex : hole.getVertices()) {
			if (!surrounds(vertex, X, Y)) {
				return false;
			}
		}
		return true;
	}
	
	public void delete() {
		if (owner instanceof Group) {
			if (exterior != null) {
				exterior.remove(this);
			}
			for (Surface current : getHoles()) {
				remove(current);
			}
			Group group = (Group) owner;
			group.remove(this);
		}
	}
	protected void applySurface(Surface prototype) {
		super.applyGeometry(prototype);
		edges = new LinkedList<Edge>(prototype.edges);
		exterior = prototype.exterior;
		holes = prototype.holes;
		visible = prototype.visible;
	}
	
	public Memento memento() {
		return new SurfaceMemento(this);
	}
	
	private static class SurfaceMemento implements Memento {
		private Surface surface;
		private Surface copy;
		public SurfaceMemento(Surface surface) {
			this.surface = surface;
			copy = new Surface(surface);
		}
		public void restore() {
			surface.applySurface(copy);
		}
	}
}
