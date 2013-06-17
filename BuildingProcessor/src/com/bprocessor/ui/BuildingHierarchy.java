package com.bprocessor.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.bprocessor.Sketch;

@SuppressWarnings("serial")
public class BuildingHierarchy extends JTree  implements TreeSelectionListener {

	private SketchController controller;
	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;

	private DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer() {  

		private boolean selected;

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
			if (selected) {
				g.setColor(new Color(0.3f, 0.5f, 0.9f));
				g.fillRect(0, 0, BuildingHierarchy.this.getWidth(), 32);
			}
			super.paintComponent(g);
		}
		public java.awt.Component getTreeCellRendererComponent(
				JTree tree,
				Object value,
				boolean sel,
				boolean expanded,
				boolean leaf,
				int row,
				boolean hasFocus) {
			super.getTreeCellRendererComponent(
					tree, value, sel,
					expanded, leaf, row,
					hasFocus);
			this.selected = sel;
			return this;
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
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.addTreeSelectionListener(this);
		model = (DefaultTreeModel) getModel();
		root = (DefaultMutableTreeNode) model.getRoot();
	}

	@SuppressWarnings("rawtypes")
	public TreePath search(DefaultMutableTreeNode node, Object object) {
		if (node.getUserObject() == object) {
			return new TreePath(node.getPath());
		} else {
			Enumeration enumerator = node.children();
			while (enumerator.hasMoreElements()) {
				DefaultMutableTreeNode current = (DefaultMutableTreeNode) enumerator.nextElement();
				TreePath result = search(current, object);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}

	public class SketchNode extends DefaultMutableTreeNode {
		private Sketch sketch;
		
		public SketchNode(Sketch sketch) {
			super(sketch);
			this.sketch = sketch;
		}
		
		public String toString() {
			if (sketch.getUid() != 0) {
				return  sketch.getName() + " [server id " + sketch.getUid() + "]";
			} else {
				return sketch.getName();
			}
		}
	}

	public void update() {
		updating = true;
		root.removeAllChildren();
		for (Sketch current : controller.getSketches()) {
			root.add(new SketchNode(current));
		}
		model.nodeStructureChanged(root);

		TreePath path = search(root, controller.getActiveSketch());
		if (path != null) {
			setSelectionPath(path); 
		}
		repaint();
		updating = false;
	}
	
	private boolean updating;
	
	public void sketchChanged(Object initiator) {
		if (initiator != this) {
			update();
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		if (!updating) {
			TreePath[] paths = getSelectionPaths();
			if (paths != null) {
				Object object = paths[0].getLastPathComponent();
				if (object instanceof SketchNode) {
					SketchNode node = (SketchNode) object;
					Sketch sketch = (Sketch) node.getUserObject();
					controller.setActiveSketch(sketch);
					controller.changed(this);
				}
			}
		}
	}
}
