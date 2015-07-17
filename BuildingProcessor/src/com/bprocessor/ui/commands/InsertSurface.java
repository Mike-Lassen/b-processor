package com.bprocessor.ui.commands;

import com.bprocessor.Polyhedron;
import com.bprocessor.Surface;
import com.bprocessor.util.ModifyGeometry;

public class InsertSurface extends ModifyGeometry<Polyhedron> {
	private Surface surface;
	public InsertSurface(Polyhedron group, Surface surface) {
		super(group);
		this.surface = surface;
	}
	public void apply() {
		geometry.insert(surface);
	}
	public String description() {
		return "Insert Surface";
	}

}
