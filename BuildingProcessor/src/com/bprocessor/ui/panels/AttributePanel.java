package com.bprocessor.ui.panels;

import javax.swing.JPanel;

import com.bprocessor.Entity;
import com.bprocessor.ui.SketchView;

@SuppressWarnings("serial")
public class AttributePanel extends JPanel {
	protected SketchView view;

	public AttributePanel() {
	}

	public void setSketchView(SketchView view) {
		this.view = view;
	}

	public void setTarget(Entity value) {
		
	}
}
