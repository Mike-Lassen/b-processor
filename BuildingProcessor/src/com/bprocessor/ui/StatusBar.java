package com.bprocessor.ui;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;

@SuppressWarnings("serial")
public class StatusBar extends JPanel {
	private JToolBar toolBar;
	public StatusBar() {
		toolBar = new JToolBar();
        toolBar.setFloatable(false);
        add(toolBar);
	}
	
	public void register(JComponent component) {
		toolBar.add(component);
		toolBar.invalidate();
		toolBar.revalidate();
	}
	public void deregister(JComponent component) {
		toolBar.remove(component);
		toolBar.invalidate();
		toolBar.revalidate();
	}
}
