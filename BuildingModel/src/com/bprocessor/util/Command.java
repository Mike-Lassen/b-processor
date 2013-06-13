package com.bprocessor.util;

import java.util.LinkedList;
import java.util.List;

import com.bprocessor.Edge;
import com.bprocessor.Group;
import com.bprocessor.Surface;
import com.bprocessor.Vertex;

/**
 * Utility
 */
public class Command {
	
	public static Surface surface(Edge ...objects) {
		List<Edge> edges = new LinkedList<Edge>();
		for (Edge current : objects) {
			edges.add(current);
		}
		return new Surface(edges);
	}
	
	public static Surface surface(List<Vertex> vertices) {
		List<Edge> edges = new LinkedList<Edge>();
		Vertex previous = vertices.get(vertices.size() - 1);
		for (Vertex current : vertices) {
			Edge edge = new Edge(previous, current);
			edges.add(edge);
			previous = current;
		}
		return new Surface(edges);
	}
	
	public static Surface square(double width) {
		Vertex v1 = new Vertex(-width, -width, 0);
		Vertex v2 = new Vertex(width, -width, 0);
		Vertex v3 = new Vertex(width, width, 0);
		Vertex v4 = new Vertex(-width, width, 0);
		Edge e1 = new Edge(v1, v2);
		Edge e2 = new Edge(v2, v3);
		Edge e3 = new Edge(v3, v4);
		Edge e4 = new Edge(v4, v1);
		return surface(e1, e2, e3, e4);
	}
	
	public static Group box() {
		Surface s1 = square(8);
		Surface s2 = square(7.7);
		s1.add(s2);
		s2.setVisible(false);
		Group result = new Group("Square");
		result.addAll(s1);
		result.addAll(s2);
		List<Surface> sides = new LinkedList<Surface>();
		List<Surface> tops = new LinkedList<Surface>();
		Surface top = s1.extrudeAll(new Vertex(0, 0, 1), 2.8, sides, tops);
		for (Surface side: sides) {
			result.addAll(side);
		}
		for (Surface current : tops) {
			if (current != top) {
				current.setVisible(false);
			}
			result.addAll(current);
		}
		return result;
	}
	
}
