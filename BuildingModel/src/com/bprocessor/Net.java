package com.bprocessor;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
	@Override
	public void collectVertices(Set<Vertex> vertices) {
		vertices.addAll(getHandles());
	}
}
