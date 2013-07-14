package com.bprocessor.ui;

import java.awt.CheckboxMenuItem;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JFrame;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class GlobalMenuBar extends MenuBar {
	private FileMenu fileMenu;
	private EditMenu editMenu;
	private ViewMenu viewMenu;
	
	private SketchController controller;
	private SketchView view;
	
	private JFrame parent;
	private final boolean SERVER_ITEMS = false;

	public void sketchChanged(Object initiator) {
		fileMenu.newItem.setEnabled(controller.isNewEnabled());
		fileMenu.openItem.setEnabled(controller.isOpenEnabled());
		fileMenu.closeItem.setEnabled(controller.isCloseEnabled());
		fileMenu.saveItem.setEnabled(controller.isSaveEnabled());
		fileMenu.saveAsItem.setEnabled(controller.isSaveAsEnabled());

		if (SERVER_ITEMS) {
			fileMenu.uploadItem.setEnabled(controller.isUploadEnabled());
			fileMenu.downloadItem.setEnabled(controller.isDownloadEnabled());
			fileMenu.deleteItem.setEnabled(controller.isDeleteEnabled());
			fileMenu.downloadAllItem.setEnabled(controller.isDownloadAllEnabled());
		}
	}
	public void setup() {
		editMenu.undoItem.setEnabled(false);
		editMenu.redoItem.setEnabled(false);
		editMenu.cutItem.setEnabled(false);
		editMenu.copyItem.setEnabled(false);
		editMenu.pasteItem.setEnabled(false);
		editMenu.deleteItem.setEnabled(view.canDeleteSelection());
	}

	public class FileMenu extends Menu {
		public MenuItem newItem;
		public MenuItem openItem;
		public MenuItem closeItem;
		public MenuItem saveItem;
		public MenuItem saveAsItem;
		public MenuItem uploadItem;
		public MenuItem downloadItem;
		public MenuItem deleteItem;
		public MenuItem downloadAllItem;


		public FileMenu(String title) {
			super(title);
			{ 
				newItem = new MenuItem("New Sketch");
				newItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						controller.userNew(parent);
						controller.changed();
					}
				});
				add(newItem);
			}
			{ 
				openItem = new MenuItem("Open Sketch...");
				openItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						controller.userOpen(parent);
						controller.changed();
					}
				});
				add(openItem);
			}
			addSeparator();
			{ 
				closeItem = new MenuItem("Close");
				closeItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						controller.userClose(parent);
						controller.changed();
					}
				});
				add(closeItem);
			}
			{ 
				saveItem = new MenuItem("Save");
				saveItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						controller.userSave(parent);
						controller.changed();
					}
				});
				add(saveItem);
			}
			{ 
				saveAsItem = new MenuItem("Save as...");
				saveAsItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						controller.userSaveAs(parent);
						controller.changed();
					}
				});
				add(saveAsItem);
			}

			if (SERVER_ITEMS) {
				addSeparator();
				{ 
					uploadItem = new MenuItem("Upload To Server");
					uploadItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							controller.userUpload(parent);
							controller.changed();
						}
					});
					add(uploadItem);
				}
				{ 
					downloadItem = new MenuItem("Download From Server");
					downloadItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							controller.userDownload(parent);
							controller.changed();
						}
					});
					add(downloadItem);
				}
				{ 
					deleteItem = new MenuItem("Delete From Server");
					deleteItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							controller.userDelete(parent);
							controller.changed();
						}
					});
					add(deleteItem);
				}
				addSeparator();
				{ 
					downloadAllItem = new MenuItem("Download All From Server");
					downloadAllItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							controller.userDownloadAll(parent);
							controller.changed();
						}
					});
					add(downloadAllItem);
				}
			}
		}
	}

	public class EditMenu extends Menu {
		public MenuItem undoItem;
		public MenuItem redoItem;
		public MenuItem cutItem;
		public MenuItem copyItem;
		public MenuItem pasteItem;
		public MenuItem deleteItem;
		
		public EditMenu(String title) {
			super(title);
			{
				undoItem = new MenuItem("Undo");
				add(undoItem);
			}
			{
				redoItem = new MenuItem("Redo");
				add(redoItem);
			}
			addSeparator();
			{
				cutItem = new MenuItem("Cut");
				add(cutItem);
			}
			{
				copyItem = new MenuItem("Copy");
				add(copyItem);
			}
			{
				pasteItem = new MenuItem("Paste");
				add(pasteItem);
			}
			addSeparator();
			{
				deleteItem = new MenuItem("Delete");
				deleteItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						view.deleteSelection();
					}
				});
				add(deleteItem);
			}
		}
	}
	public class ViewMenu extends Menu {
		public CheckboxMenuItem showCoordinateSystem;
		public CheckboxMenuItem restrictToPlaneItem;
		public MenuItem resetCoordinateSystem;
		public CheckboxMenuItem showGrid;
		public CheckboxMenuItem snapToGrid;
		public MenuItem clearGuidesItem;
		
		public ViewMenu(String title) {
			super(title);
			{
				showCoordinateSystem = new CheckboxMenuItem("Show CoordinateSystem");
				showCoordinateSystem.setState(view.isCoordinateSystemVisible());
				showCoordinateSystem.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent event) {
						view.setCoordinateSystemVisible(showCoordinateSystem.getState());
						view.repaint();
					}
				});
				add(showCoordinateSystem);
			}
			{
				restrictToPlaneItem = new CheckboxMenuItem("Restrict to Plane");
				restrictToPlaneItem.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent event) {
						view.setRestrictToPlane(restrictToPlaneItem.getState());
						view.repaint();
					}
				});
				add(restrictToPlaneItem);
			}
			{ 
				resetCoordinateSystem = new MenuItem("Reset CoordinateSystem");
				resetCoordinateSystem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						view.setCoordinateSystem(null);
						view.repaint();
					}
				});
				add(resetCoordinateSystem);
			}
			addSeparator();
			{
				showGrid = new CheckboxMenuItem("Show Grid");
				showGrid.setState(view.isGridVisible());
				showGrid.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent event) {
						view.setGridVisible(showGrid.getState());
						view.repaint();
					}
				});
				add(showGrid);
			}
			{
				snapToGrid = new CheckboxMenuItem("Snap to Grid");
				snapToGrid.setState(view.getSnapToGrid());
				snapToGrid.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent event) {
						view.setSnapToGrid(snapToGrid.getState());
						view.repaint();
					}
				});
				add(snapToGrid);
			}
			addSeparator();
			{ 
				clearGuidesItem = new MenuItem("Clear Guides");
				clearGuidesItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						view.guideLayer().clear();
						view.repaint();
					}
				});
				add(clearGuidesItem);
			}
		}
	}
	public GlobalMenuBar(SketchController controller, SketchView view, JFrame parent) {
		this.controller = controller;
		this.view = view;
		this.parent = parent;
		fileMenu = new FileMenu("File");
		add(fileMenu);
		editMenu = new EditMenu("Edit");
		add(editMenu);
		viewMenu = new ViewMenu("View");
		add(viewMenu);
		Timer timer = new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				setup();
			}
		});
		timer.setDelay(100);
		timer.setRepeats(true);
		timer.start();
	}
}
