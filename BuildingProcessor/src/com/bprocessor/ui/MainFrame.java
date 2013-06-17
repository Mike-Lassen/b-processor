package com.bprocessor.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import javax.swing.event.MouseInputAdapter;

@SuppressWarnings("serial")
public class MainFrame extends JFrame implements SketchObserver {
	private SketchController controller;

	private GlobalMenuBar menubar;
	private ToolBar toolbar;
	private StatusBar statusbar;
	private BuildingEditor editor;
	private BuildingHierarchy hierarchy;
	
	public void setup() {
		menubar.sketchChanged(this);
	}
	
	
	public class TreeArea extends JPanel {
		public TreeArea() {
			this.addMouseListener(dragger);
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
		public EditorArea() {
			this.addMouseListener(dragger);
			setLayout(new BorderLayout());
			JPanel top = new JPanel();
			top.setPreferredSize(new Dimension(320, 24));
			top.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.GRAY));
			add(top, BorderLayout.NORTH);
			editor = new BuildingEditor(controller);
			editor.addMouseListener(new Blocker());
			add(editor, BorderLayout.CENTER);
		}
	}
	public class AttributesArea extends JPanel {
		public AttributesArea() {
			this.addMouseListener(dragger);
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

	public class Dragger extends MouseInputAdapter {
		public void mousePressed(MouseEvent e) {
		}
	}
	public class Blocker extends MouseInputAdapter {
	}

	Dragger dragger;

	public class MainArea extends JPanel {
		public MainArea() {
			this.addMouseListener(dragger);
			setLayout(new BorderLayout());
			JPanel left = new TreeArea();
			left.setPreferredSize(new Dimension(320, 672));
			left.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.GRAY));
			add(left, BorderLayout.WEST);

			JPanel center = new EditorArea();
			center.setPreferredSize(new Dimension(640, 672));
			center.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.GRAY));
			add(center, BorderLayout.CENTER);

			JPanel right = new AttributesArea();
			right.setPreferredSize(new Dimension(320, 672));
			right.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.GRAY));
			add(right, BorderLayout.EAST);
		}
	}

	public MainFrame() {
		controller = new SketchController(this, "sketches.lst");
		this.setTitle("B-Processor");
		menubar = new GlobalMenuBar(controller, MainFrame.this);
		this.setMenuBar(menubar);
		dragger = new Dragger();
		setLayout(new BorderLayout());

		toolbar = new ToolBar();
		toolbar.setPreferredSize(new Dimension(1280, 40));
		toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
		add(toolbar, BorderLayout.NORTH);

		JPanel center = new MainArea();
		center.setPreferredSize(new Dimension(1280, 672));
		center.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.WHITE));
		add(center, BorderLayout.CENTER);

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

		toolbar.editor = editor;
		editor.toolbar = toolbar;
		editor.statusbar = statusbar;

		editor.setup();
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
