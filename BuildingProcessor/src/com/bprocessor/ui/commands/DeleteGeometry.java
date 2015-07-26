package com.bprocessor.ui.commands;

import com.bprocessor.Geometry;
import com.bprocessor.Polyhedron;
import com.bprocessor.util.ModifyEntity;

public class DeleteGeometry extends ModifyEntity<Polyhedron> {
	private Geometry target;
	
	public DeleteGeometry(Geometry target) {
		super((Polyhedron)target.getOwner());
		this.target = target;
	}
	public void apply() {
		target.delete();
	}
	public String description() {
		return "Delete " + target.getClass().getName();
	}
}
