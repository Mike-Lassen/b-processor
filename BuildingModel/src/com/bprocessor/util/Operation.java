package com.bprocessor.util;

public abstract class Operation {
	private String name;
	public Operation() {
	}

	public Operation(String name) {
		this.name = name;
	}
	
	public void setName(String value) {
		this.name = value;
	}
	public String getName() {
		return name;
	}
	
	public abstract void evaluate();
	
}
