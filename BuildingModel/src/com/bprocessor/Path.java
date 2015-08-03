package com.bprocessor;

import java.util.LinkedList;

public class Path<T> {
	private LinkedList<Component> path;
	private T target;
	public Path(LinkedList<Component> path, T target) {
		this.path = path;
		this.target = target;
	}
	
	public LinkedList<Component> path() {
		return path;
	}
	public T target() {
		return target;
	}
	
	public String toString() {
		return path + " : " + target;
	}
}
