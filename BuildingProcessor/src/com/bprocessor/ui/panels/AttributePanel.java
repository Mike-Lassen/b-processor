package com.bprocessor.ui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.bprocessor.Attribute;
import com.bprocessor.Geometry;

@SuppressWarnings("serial")
public class AttributePanel extends JPanel {
	private Geometry target;
	private Font headerFont;
	private Font labelFont;
	private Font valueFont;

	public AttributePanel() {
		headerFont = new Font("Arial", Font.BOLD, 16);
		labelFont = new Font("Verdana", Font.BOLD, 12);
		valueFont = new Font("Verdana", Font.PLAIN, 12);
		setLayout(new BorderLayout());
	}

	public void setTarget(Geometry value) {
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

	JLabel renderValue(Object value) {
		JLabel label = new JLabel("" + value);
		label.setFont(valueFont);
		return label;
	}

	JLabel renderHeader(Object value) {
		JLabel label = new JLabel("" + value);
		label.setFont(headerFont);
		return label;
	}

}
