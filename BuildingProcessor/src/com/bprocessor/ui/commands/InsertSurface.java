package com.bprocessor.ui.commands;

import com.bprocessor.Group;
import com.bprocessor.Surface;
import com.bprocessor.util.ModifyGeometry;

public class InsertSurface extends ModifyGeometry<Group> {
	private Surface surface;
	public InsertSurface(Group group, Surface surface) {
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
