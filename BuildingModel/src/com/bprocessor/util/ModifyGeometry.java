package com.bprocessor.util;

import com.bprocessor.Geometry;
import com.bprocessor.Memento;

public abstract class ModifyGeometry<T extends Geometry> implements Command {
	protected T geometry;
	private Memento memento;
	
	public ModifyGeometry(T geometry) {
		this.geometry = geometry;
	}
	public void prepare() {
		memento = geometry.memento();
	}
	public void finish() { }
	private void revert() {
		Memento next = geometry.memento();
		memento.restore();
		memento = next;
	}
	public void undo() {
		revert();
	}
	public void redo() {
		revert();
	}
}
