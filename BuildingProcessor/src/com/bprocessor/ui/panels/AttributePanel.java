package com.bprocessor.ui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.bprocessor.Attribute;
import com.bprocessor.Entity;
import com.bprocessor.Geometry;
import com.bprocessor.ui.SketchView;

@SuppressWarnings("serial")
public class AttributePanel extends JPanel {
	private SketchView view;
	private Entity target;
	private Font headerFont;
	private Font labelFont;
	private Font valueFont;

	public AttributePanel() {
		headerFont = new Font("Arial", Font.BOLD, 16);
		labelFont = new Font("Verdana", Font.BOLD, 12);
		valueFont = new Font("Verdana", Font.PLAIN, 12);
		setLayout(new BorderLayout());
	}
	
	public void setSketchView(SketchView view) {
		this.view = view;
	}

	public void setTarget(Entity value) {
		if (target != value) {
			this.removeAll();
			target = value;
			if (target != null) {
				List<Attribute> attributes = target.getAttributes();
				Box column = renderList(attributes, 5);
				this.add(column, BorderLayout.NORTH);
			}	
			this.invalidate();
			this.revalidate();
			this.repaint();
		}
	}

	Box renderList(List<?> list, int indent) {
		Box column = Box.createVerticalBox();
		for (Object current : list) {
			Box row = render(current, indent);
			column.add(row);
		}
		return column;
	}

	Box render(Object object, int indent) {
		if (object instanceof Attribute) {
			return renderAttribute((Attribute) object, indent);
		} else {
			Box row = Box.createHorizontalBox();
			row.add(Box.createRigidArea(new Dimension(0, 20)));
			row.add(Box.createHorizontalStrut(indent));
			row.add(renderValue(object));
			row.add(Box.createHorizontalGlue());
			return row;
		}
	}
	Box renderAttribute(Attribute attribute, int indent) {
		Box row = Box.createHorizontalBox();
		row.add(Box.createRigidArea(new Dimension(0, 20)));
		String name = attribute.getName();
		Object value = attribute.getValue();

		if (value instanceof List) {
			row.add(Box.createHorizontalStrut(indent));
			row.add(renderName(name));
			row.add(Box.createHorizontalGlue());
			Box column = Box.createVerticalBox();
			column.add(row);
			column.add(renderList((List<?>) value, indent + 15));
			return column;
		} else {
			row.add(Box.createHorizontalStrut(indent));
			row.add(renderName(name + ":"));
			row.add(Box.createHorizontalGlue());
			row.add(renderValue(value));
			row.add(Box.createHorizontalStrut(5));
			return row;
		}

	}

	JLabel renderName(String name) {
		JLabel label = new JLabel(name);
		label.setFont(labelFont);
		return label;
	}

	JComponent renderValue(Object value) {
		if (value instanceof Entity) {
			return renderGeometry((Entity) value);
		} else {
			JLabel label = new JLabel("" + value);
			label.setFont(valueFont);
			return label;
		}
	}
	JComponent renderGeometry(Entity value) {
		JLabel label = new JLabel("" + value);
		label.setFont(valueFont);
		label.addMouseListener(new FollowReference(value));
		return label;
	}

	JLabel renderHeader(Object value) {
		JLabel label = new JLabel("" + value);
		label.setFont(headerFont);
		return label;
	}

	class FollowReference implements MouseListener {
		private Entity target;
		
		public FollowReference(Entity target) {
			this.target = target;
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			view.setSelected(target);
			view.repaint();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
