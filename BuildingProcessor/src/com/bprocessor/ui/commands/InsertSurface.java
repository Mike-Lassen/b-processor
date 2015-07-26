package com.bprocessor.ui.commands;

import com.bprocessor.Polyhedron;
import com.bprocessor.Surface;
import com.bprocessor.util.ModifyEntity;

public class InsertSurface extends ModifyEntity<Polyhedron> {
	private Surface surface;
	public InsertSurface(Polyhedron group, Surface surface) {
		super(group);
		this.surface = surface;
	}
	public void apply() {
		entity.insert(surface);
	}
	public String description() {
		return "Insert Surface";
	}

}
