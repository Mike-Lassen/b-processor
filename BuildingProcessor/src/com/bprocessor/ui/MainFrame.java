package com.bprocessor.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import javax.swing.event.MouseInputAdapter;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
	public static MainFrame instance;
	private SketchController controller;

	private GlobalMenuBar menubar;
	private ToolBar toolbar;
	private StatusBar statusbar;
	private BuildingEditor editor;
	
	public void setup() {
		menubar.setup();
	}
	
	public class GlobalMenuBar extends MenuBar {
		public FileMenu fileMenu;

		public void setup() {
			MainFrame.this.setTitle(controller.title());
			fileMenu.newItem.setEnabled(controller.isNewEnabled());
			fileMenu.openItem.setEnabled(controller.isOpenEnabled());
			fileMenu.closeItem.setEnabled(controller.isCloseEnabled());
			fileMenu.saveItem.setEnabled(controller.isSaveEnabled());
			fileMenu.saveAsItem.setEnabled(controller.isSaveAsEnabled());
			editor.setSketch(controller.getActiveSketch());
		}

		public class FileMenu extends Menu {
			public MenuItem newItem;
			public MenuItem openItem;
			public MenuItem closeItem;
			public MenuItem saveItem;
			public MenuItem saveAsItem;

			public FileMenu(String title) {
				super(title);
				{ 
					newItem = new MenuItem("New Sketch");
					newItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							controller.userNew(MainFrame.this);
							setup();
						}
					});
					add(newItem);
				}
				{ 
					openItem = new MenuItem("Open Sketch...");
					openItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							controller.userOpen(MainFrame.this);
							setup();
						}
					});
					add(openItem);
				}
				addSeparator();
				{ 
					closeItem = new MenuItem("Close");
					closeItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							controller.userClose(MainFrame.this);
							setup();
						}
					});
					add(closeItem);
				}
				{ 
					saveItem = new MenuItem("Save");
					saveItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							controller.userSave(MainFrame.this);
							setup();
						}
					});
					add(saveItem);
				}
				{ 
					saveAsItem = new MenuItem("Save as...");
					saveAsItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							controller.userSaveAs(MainFrame.this);
							setup();
						}
					});
					add(saveAsItem);
				}
			}
		}
		public GlobalMenuBar() {
			fileMenu = new FileMenu("File");
			this.add(fileMenu);
		}
	}

	public class TreeArea extends JPanel {
		public TreeArea() {
			this.addMouseListener(dragger);
			setLayout(new BorderLayout());
			JPanel top = new JPanel();
			top.setPreferredSize(new Dimension(320, 24));
			top.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.GRAY));
			add(top, BorderLayout.NORTH);

			JPanel center = new JPanel();
			Color ligth = new Color(0xF9, 0xF9, 0xF9);
			center.setBackground(ligth);
			JScrollPane scroll = new JScrollPane(center);
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
			editor = new BuildingEditor();
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
		instance = this;
		controller = new SketchController();
		this.setTitle("B-Processor");
		menubar = new GlobalMenuBar();
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
		menubar.setup();
		setVisible(true);
		pack();
	}

	public static void main(String[] args) {
		new MainFrame();
	}
}
