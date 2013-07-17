package com.bprocessor.ui.commands;

import com.bprocessor.Surface;
import com.bprocessor.util.ModifyGeometry;

public class EraseSurface extends ModifyGeometry<Surface> {
	public EraseSurface(Surface geometry) {
		super(geometry);
	}
	public void apply() {
		geometry.setVisible(false);
	}
	public String description() {
		return "Erase Surface";
	}
}
