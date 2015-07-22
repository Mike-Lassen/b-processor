package com.bprocessor;

import java.util.LinkedList;
import java.util.List;

import com.bprocessor.util.CoordinateSystem;

public class Net extends Mesh {
	protected List<Line> lines;
	protected List<Handle> handles;
	
	

	public Net() {}
	public Net(Net prototype) {
		super(prototype);
		lines = new LinkedList<Line>(prototype.lines);
		handles = new LinkedList<Handle>(prototype.handles);
	}
	public Net(String name) {
		super(name);
		lines = new LinkedList<Line>();
		handles = new LinkedList<Handle>();
		
		double gutter = 0.2;
		double width = 0.8;
		int n = 20;
		
		Color color = new Color(221.0f/255, 234.0f/255, 254.0f/ 255);
		double length = n * (gutter + width) + width;
		CoordinateSystem system = CoordinateSystem.xy();
		Vertex i = system.getI();
		Vertex j = system.getJ();
		Vertex origin = system.getOrigin();
		{
			Vertex from = origin;
			Vertex v = i.scale(length);
			for (int k = 0; k <= n; k++) {
				Vertex to1 = from.add(v);
				add(new Line(from, to1, color));
				from = from.add(j.scale(width));
				Vertex to2 = from.add(v);
				add(new Line(from, to2, color));
				from = from.add(j.scale(gutter));
			}
		}
		{
			Vertex from = origin;
			Vertex v = j.scale(length);
			for (int k = 0; k <= n; k++) {
				Vertex to1 = from.add(v);
				add(new Line(from, to1, color));
				from = from.add(i.scale(width));
				Vertex to2 = from.add(v);
				add(new Line(from, to2, color));
				from = from.add(i.scale(gutter));
			}
		}
		
	}

	public List<Line> getLines() {
		return lines;
	}
	public void setLines(List<Line> lines) {
		this.lines = lines;
	}

	public void add(Line line) {
		lines.add(line);
		line.owner = this;
	}
	public void remove(Line line) {
		lines.remove(line);
		line.owner = null;
	}

	public List<Handle> getHandles() {
		return handles;
	}
	public void setHandles(List<Handle> handles) {
		this.handles = handles;
	}

	public void add(Handle handle) {
		handles.add(handle);
		handle.owner = this;
	}
	public void remove(Handle handle) {
		handles.remove(handle);
		handle.owner = null;
	}

	public void clear() {
		for (Line current : lines) {
			current.owner = null;
		}
		lines.clear();
		for (Handle current : handles) {
			current.owner = null;
		}
		handles.clear();
	}

	@Override
	public void accept(ItemVisitor visitor) {
		visitor.visit(this);
	}
	protected void applyGuideLayer(Net prototype) {
		super.applyItem(prototype);
		lines = prototype.lines;
		handles = prototype.handles;
	}
}
