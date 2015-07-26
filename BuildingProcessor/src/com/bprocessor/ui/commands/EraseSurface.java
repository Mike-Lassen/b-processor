package com.bprocessor.ui.commands;

import com.bprocessor.Surface;
import com.bprocessor.util.ModifyEntity;

public class EraseSurface extends ModifyEntity<Surface> {
	public EraseSurface(Surface geometry) {
		super(geometry);
	}
	public void apply() {
		entity.setVisible(false);
	}
	public String description() {
		return "Erase Surface";
	}
}
