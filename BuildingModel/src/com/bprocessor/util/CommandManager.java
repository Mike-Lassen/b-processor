package com.bprocessor.util;

import java.util.LinkedList;


public class CommandManager {
	private static CommandManager instance;
	public static CommandManager instance() {
		if (instance == null) {
			instance = new CommandManager();
		}
		return instance;
	}
	private LinkedList<Command> undoStack;
	private LinkedList<Command> redoStack;
	
	public CommandManager() {
		undoStack = new LinkedList<Command>();
		redoStack = new LinkedList<Command>();
	}
	
	public void apply(Command command) {
		command.prepare();
		command.apply();
		command.finish();
		redoStack.clear();
		undoStack.addLast(command);
	}
	
	public boolean canUndo() {
		return !undoStack.isEmpty();
	}
	public void undo() {
		if (!undoStack.isEmpty()) {
			Command command = undoStack.removeLast();
			command.undo();
			redoStack.addLast(command);
		}
	}
	public boolean canRedo() {
		return !redoStack.isEmpty();
	}
	public void redo() {
		if (!redoStack.isEmpty()) {
			Command command = redoStack.removeLast();
			command.redo();
			undoStack.addLast(command);
		}
	}
	public void clear() {
		undoStack.clear();
		redoStack.clear();
	}
	public LinkedList<Command> undoStack() {
		return undoStack;
	}
	public LinkedList<Command> redoStack() {
		return redoStack;
	}
}
