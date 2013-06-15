package com.bprocessor.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.bprocessor.Sketch;

@SuppressWarnings("serial")
public class BuildingHierarchy extends JTree {

	private SketchController controller;
	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;

	private DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer() {  

		public Color getBackgroundNonSelectionColor() {  
			return(null);  
		}  
		public Color getBackgroundSelectionColor() {  
			return(null);  
		}
		public Color getBorderSelectionColor() {  
			return(null);  
		}  
		public Color getBackground() {  
			return(null);
		}
		protected void paintComponent(Graphics g) {
			if (getSelectionCount()>0) {
				for(int i: getSelectionRows()) {
					Rectangle r = getRowBounds(i);
					g.setColor(new Color(0.3f, 0.5f, 0.9f));
					g.fillRect(0, r.y - 1, BuildingHierarchy.this.getWidth(), r.height);
				}
			}
			super.paintComponent(g);
		}
		public Dimension getPreferredSize() {
			Dimension d1 = super.getPreferredSize();
			if (d1 != null) {
				return new Dimension(319, d1.height);
			} else {
				return null;
			}
		}
	};  


	public BuildingHierarchy(SketchController controller) {
		this.controller = controller;
		setEditable(false);
		Font font = new Font("Verdana", Font.PLAIN, 12);
		this.setFont(font);
		setRootVisible(false);
		setCellRenderer(renderer);
		setRowHeight(32);
		model = (DefaultTreeModel) getModel();
		root = (DefaultMutableTreeNode) model.getRoot();
		root.removeAllChildren();
		model.nodeStructureChanged(root);
	}
	
	public void sketchChanged(Object initiator) {
		root.removeAllChildren();
		for (Sketch current : controller.getSketches()) {
			String name = current.getName();
			root.add(new DefaultMutableTreeNode(name));
		}
		model.nodeStructureChanged(root);
		TreePath path = getPathForRow(0);
		setSelectionPath(path);  
		repaint();
	}
}
