package com.bprocessor.util;

import com.bprocessor.Entity;
import com.bprocessor.Memento;

public abstract class ModifyEntity<T extends Entity> implements Command {
	protected T entity;
	private Memento memento;
	
	public ModifyEntity(T entity) {
		this.entity = entity;
	}
	public void prepare() {
		memento = entity.memento();
	}
	public void finish() { }
	private void revert() {
		Memento next = entity.memento();
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
