package com.bprocessor.ui;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class GlobalMenuBar extends MenuBar {
		private FileMenu fileMenu;
		private SketchController controller;
		private JFrame parent;
		

		public void sketchChanged(Object initiator) {
			fileMenu.newItem.setEnabled(controller.isNewEnabled());
			fileMenu.openItem.setEnabled(controller.isOpenEnabled());
			fileMenu.closeItem.setEnabled(controller.isCloseEnabled());
			fileMenu.saveItem.setEnabled(controller.isSaveEnabled());
			fileMenu.saveAsItem.setEnabled(controller.isSaveAsEnabled());
			
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
			}
		}
		public GlobalMenuBar(SketchController controller, JFrame parent) {
			this.controller = controller;
			this.parent = parent;
			fileMenu = new FileMenu("File");
			this.add(fileMenu);
		}
	}
