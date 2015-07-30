package com.bprocessor;

import java.util.List;

public class Path<T> {
	private List<Component> path;
	private T target;
	public Path(List<Component> path, T target) {
		this.path = path;
		this.target = target;
	}
	
	public List<Component> path() {
		return path;
	}
	public T target() {
		return target;
	}
}
