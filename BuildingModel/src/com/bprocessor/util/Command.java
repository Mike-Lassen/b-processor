package com.bprocessor.util;

public interface Command {
	void prepare();
	void apply();
	void finish();
	void undo();
	void redo();
	String description();
}
