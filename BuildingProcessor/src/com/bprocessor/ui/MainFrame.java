package com.bprocessor.ui;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

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

import com.bprocessor.ui.panels.AttributePanel;
import com.bprocessor.ui.tools.EraserTool;
import com.bprocessor.ui.tools.OrientTool;
import com.bprocessor.ui.tools.PencilTool;
import com.bprocessor.ui.tools.RulerTool;
import com.bprocessor.ui.tools.SelectTool;

@SuppressWarnings("serial")
public class MainFrame extends JFrame implements SketchObserver {
	private SketchController controller;

	private Binding binding;
	private GroovyShell shell;
	
	private GlobalMenuBar menubar;
	private ToolBar toolbar;
	private StatusBar statusbar;
	private SketchView view;
	private SketchHierarchy hierarchy;
	private AttributesArea attributesArea;
	private AttributePanel attributesPanel;
	
	public class TreeArea extends JPanel {
		public TreeArea() {
			setLayout(new BorderLayout());
			JPanel top = new JPanel();
			top.setPreferredSize(new Dimension(320, 24));
			top.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.GRAY));
			add(top, BorderLayout.NORTH);

			hierarchy = new SketchHierarchy(controller);
			Color ligth = new Color(0xF9, 0xF9, 0xF9);
			hierarchy.setBackground(ligth);
			JScrollPane scroll = new JScrollPane(hierarchy);
			scroll.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
			add(scroll, BorderLayout.CENTER);
		}
	}
	public class ViewArea extends JPanel {
		public ViewArea(SketchView view) {
			setLayout(new BorderLayout());
			JPanel top = new JPanel();
			top.setPreferredSize(new Dimension(320, 24));
			top.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.GRAY));
			add(top, BorderLayout.NORTH);
			add(view, BorderLayout.CENTER);
		}
	}
	public class AttributesArea extends JPanel {
		private JPanel center;
		
		public void register(JPanel panel) {
			center.add(panel, BorderLayout.CENTER);
			center.invalidate();
			center.revalidate();
		}
		public void deregister(JPanel panel) {
			center.remove(panel);
			center.invalidate();
			center.revalidate();
		}
		
		public AttributesArea() {
			setLayout(new BorderLayout());
			JPanel top = new JPanel();
			top.setPreferredSize(new Dimension(320, 24));
			top.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 0, Color.GRAY));
			add(top, BorderLayout.NORTH);

			center = new JPanel();
			center.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));
			Color ligth = new Color(0xF9, 0xF9, 0xF9);
			center.setBackground(ligth);
			center.setLayout(new BorderLayout());
			add(center, BorderLayout.CENTER);
		}
	}

	public class MainArea extends JPanel {
		public MainArea(SketchView view) {
			setLayout(new BorderLayout());
			JPanel treeArea = new TreeArea();
			treeArea.setPreferredSize(new Dimension(320, 672));
			treeArea.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.GRAY));
			add(treeArea, BorderLayout.WEST);

			JPanel viewArea = new ViewArea(view);
			viewArea.setPreferredSize(new Dimension(640, 672));
			viewArea.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.GRAY));
			add(viewArea, BorderLayout.CENTER);

			attributesArea = new AttributesArea();
			attributesArea.setPreferredSize(new Dimension(320, 672));
			attributesArea.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.GRAY));
			add(attributesArea, BorderLayout.EAST);
		}
	}

	public MainFrame() {
		controller = new SketchController(this, "sketches.lst");

		setLayout(new BorderLayout());
		setTitle("B-Processor");

		
		binding = new Binding();
		binding.setVariable("controller", controller);
		shell = new GroovyShell(binding);

		toolbar = new ToolBar();
		toolbar.setPreferredSize(new Dimension(1280, 40));
		toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
		add(toolbar, BorderLayout.NORTH);

		view = new SketchView(controller);
		binding.setVariable("view", view);
		
		JPanel mainArea = new MainArea(view);
		mainArea.setPreferredSize(new Dimension(1280, 672));
		mainArea.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.WHITE));
		add(mainArea, BorderLayout.CENTER);

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

		
		view.setDelegate(toolbar.getInputListener());
		
		registerTools();
		toolbar.disableAll();
		
		
		menubar = new GlobalMenuBar(controller, view, shell, MainFrame.this);
		setMenuBar(menubar);
		
		registerPanels();
		
		sketchChanged(this);
		setVisible(true);
		pack();
	}

	public void registerTools() {
		Tool select = new SelectTool(view, statusbar);
		
		Tool pencil = new PencilTool(view, statusbar);
		Tool ruler = new RulerTool(view, statusbar);
		Tool eraser = new EraserTool(view, statusbar);

		Tool orient = new OrientTool(view, statusbar);
		
		Tool cameraDrag = new StandardTool.CameraDrag(view);
		Tool cameraRotation = new StandardTool.CameraRotation(view);
		Tool cameraZoom = new StandardTool.CameraZoom(view);

		

		toolbar.registerTool("select", "Biconselecttool.gif", select);
		toolbar.addSeperator(20);

		toolbar.registerTool("pencil", "Biconpentool.gif", pencil);
		toolbar.registerTool("ruler", "ruler-icon.png", ruler);
		toolbar.registerTool("eraser", "eraser-icon.png", eraser);
		
		toolbar.addSeperator(20);
		toolbar.registerTool("orient", "constructor-icon.png", orient);
		
		toolbar.addSeperator(40);

		toolbar.registerTool("camera-drag", "Bicondrag.gif", cameraDrag);
		toolbar.registerTool("camera-rotation", "Biconrotcam.png", cameraRotation);
		toolbar.registerTool("camera-zoom", "Biconzomeinout.gif", cameraZoom);
	}

	public void registerPanels() {
		attributesPanel = new AttributePanel();
		attributesArea.register(attributesPanel);
		view.setAttributePanel(attributesPanel);
	}
	
	public static void main(String[] args) {
		new MainFrame();
	}

	@Override
	public void sketchChanged(Object initiator) {
		setTitle(controller.title());
		if (view.getSketch() != controller.getActiveSketch()) {
			view.setSketch(controller.getActiveSketch());
			if (controller.getActiveSketch() != null) {
				toolbar.enableAll();
				toolbar.selectTool("select");
			} else {
				toolbar.disableAll();
				toolbar.selectTool(null);
			}
		}
		menubar.sketchChanged(initiator);
		hierarchy.sketchChanged(initiator);
		view.repaint();
	}
}
