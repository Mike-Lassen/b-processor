package com.bprocessor.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

@SuppressWarnings("serial")
public class MainFrame extends JFrame implements SketchObserver {
	private SketchController controller;

	private GlobalMenuBar menubar;
	private ToolBar toolbar;
	private StatusBar statusbar;
	private BuildingEditor editor;
	private BuildingHierarchy hierarchy;
	
	
	public class TreeArea extends JPanel {
		public TreeArea() {
			setLayout(new BorderLayout());
			JPanel top = new JPanel();
			top.setPreferredSize(new Dimension(320, 24));
			top.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.GRAY));
			add(top, BorderLayout.NORTH);

			hierarchy = new BuildingHierarchy(controller);
			Color ligth = new Color(0xF9, 0xF9, 0xF9);
			hierarchy.setBackground(ligth);
			JScrollPane scroll = new JScrollPane(hierarchy);
			scroll.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
			add(scroll, BorderLayout.CENTER);
		}
	}
	public class EditorArea extends JPanel {
		public EditorArea(SketchController controller, ToolBar toolbar, StatusBar statusbar) {
			setLayout(new BorderLayout());
			JPanel top = new JPanel();
			top.setPreferredSize(new Dimension(320, 24));
			top.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.GRAY));
			add(top, BorderLayout.NORTH);
			editor = new BuildingEditor(controller, toolbar, statusbar);
			add(editor, BorderLayout.CENTER);
		}
	}
	public class AttributesArea extends JPanel {
		public AttributesArea() {
			setLayout(new BorderLayout());
			JPanel top = new JPanel();
			top.setPreferredSize(new Dimension(320, 24));
			top.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 0, Color.GRAY));
			add(top, BorderLayout.NORTH);

			JPanel center = new JPanel();
			center.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));
			Color ligth = new Color(0xF9, 0xF9, 0xF9);
			center.setBackground(ligth);
			add(center, BorderLayout.CENTER);
		}
	}

	public class MainArea extends JPanel {
		public MainArea(SketchController controller, ToolBar toolbar, StatusBar statusbar) {
			setLayout(new BorderLayout());
			JPanel treeArea = new TreeArea();
			treeArea.setPreferredSize(new Dimension(320, 672));
			treeArea.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.GRAY));
			add(treeArea, BorderLayout.WEST);

			JPanel editorArea = new EditorArea(controller, toolbar, statusbar);
			editorArea.setPreferredSize(new Dimension(640, 672));
			editorArea.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.GRAY));
			add(editorArea, BorderLayout.CENTER);

			JPanel attributesArea = new AttributesArea();
			attributesArea.setPreferredSize(new Dimension(320, 672));
			attributesArea.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.GRAY));
			add(attributesArea, BorderLayout.EAST);
		}
	}

	public MainFrame() {
		controller = new SketchController(this, "sketches.lst");
		
		setLayout(new BorderLayout());
		setTitle("B-Processor");
		
		menubar = new GlobalMenuBar(controller, MainFrame.this);
		setMenuBar(menubar);
		

		toolbar = new ToolBar();
		toolbar.setPreferredSize(new Dimension(1280, 40));
		toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
		add(toolbar, BorderLayout.NORTH);
		
		statusbar = new StatusBar();
		statusbar.setPreferredSize(new Dimension(1280, 40));
		statusbar.setBorder(new EtchedBorder() {
			public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
				g.setColor(getShadowColor(c));
				g.drawLine(x, y, x + width, y);
				g.setColor(getHighlightColor(c));
				g.drawLine(x, y + 1, x + width, y + 1);
			}
		});
		add(statusbar, BorderLayout.SOUTH);

		JPanel mainArea = new MainArea(controller, toolbar, statusbar);
		mainArea.setPreferredSize(new Dimension(1280, 672));
		mainArea.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.WHITE));
		add(mainArea, BorderLayout.CENTER);

		sketchChanged(this);
		setVisible(true);
		pack();
	}

	public static void main(String[] args) {
		new MainFrame();
	}

	@Override
	public void sketchChanged(Object initiator) {
		setTitle(controller.title());
		editor.setSketch(controller.getActiveSketch());
		menubar.sketchChanged(initiator);
		hierarchy.sketchChanged(initiator);
		editor.repaint();
	}
}
