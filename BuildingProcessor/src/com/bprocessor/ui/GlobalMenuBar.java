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
	public GlobalMenuBar(SketchController controller, JFrame parent) {
		this.controller = controller;
		this.parent = parent;
		fileMenu = new FileMenu("File");
		this.add(fileMenu);
	}
}
