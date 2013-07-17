package com.bprocessor.ui.commands;

import com.bprocessor.Geometry;
import com.bprocessor.Group;
import com.bprocessor.util.ModifyGeometry;

public class DeleteGeometry extends ModifyGeometry<Group> {
	private Geometry target;
	
	public DeleteGeometry(Geometry target) {
		super((Group)target.getOwner());
		this.target = target;
	}
	public void apply() {
		target.delete();
	}
	public String description() {
		return "Delete " + target.getClass().getName();
	}
}
