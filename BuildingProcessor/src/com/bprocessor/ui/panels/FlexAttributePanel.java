package com.bprocessor.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.bprocessor.Attribute;
import com.bprocessor.Entity;
import com.bprocessor.Format;

@SuppressWarnings("serial")
public class FlexAttributePanel extends AttributePanel {
	private Entity target;
	private Font headingFont;
	private Font labelFont;
	private JTextField defaultField;
	private int width;
	
	public FlexAttributePanel() {
		setLayout(new BorderLayout());
		headingFont = new Font("Helvetica Neue", Font.BOLD, 12);
		labelFont = new Font("Helvetica Neue", Font.PLAIN, 12);
	}
	
	public void setTarget(Entity value) {
		if (value != target) {
			target = value;
			this.removeAll();
			defaultField = null;
			target = value;
			if (target != null) {
				List<Attribute> attributes = target.getAttributes();
				width = this.getWidth();
				JComponent body = renderSections(attributes, 5, 0);
				this.add(body, BorderLayout.CENTER);
				if (defaultField != null) {
					defaultField.requestFocus();
				}
			}
			this.invalidate();
			this.revalidate();
			this.repaint();
		}
	}

	public JComponent renderSections(List<Attribute> list, int x, int y) {
		JPanel body = new JPanel();
		body.setLayout(null);
		for (Attribute attribute : list) {
			int height = renderSection(body, attribute, x, y);
			y = y + height; 
		}
		return body;
	}
	
	public int renderSection(JComponent body, Attribute attribute, int x, int y) {
		int height = renderHeader(body, attribute.getName(), x, y);
		y += height;
		List<Object> list = (List<Object>) attribute.getValue();
		for (Object current : list) {
			if (current instanceof Attribute) {
				int delta = renderAtribute(body, (Attribute) current, x, y);
				y += delta;
				height += delta;
			}
		}
		JPanel line = new JPanel();
		line.setBounds(4, y + 1, width - 8, 1);
		line.setBackground(Color.LIGHT_GRAY);
		line.setOpaque(true);
		body.add(line);
		return height;
	}
	
	public int renderHeader(JComponent body, String name, int x, int y) {
		JLabel label = new JLabel(name, SwingConstants.LEFT);
		label.setBounds(x, y, 120, 24);
		label.setFont(headingFont);
		label.setVerticalAlignment(SwingConstants.CENTER);
		body.add(label);
		return 24;
	}
	
	public int renderAtribute(JComponent body, Attribute attribute, int x, int y) {
		renderLabel(body, attribute.getName(), x, y);
		int height = renderObject(body, attribute.getValue(), x + 77, y);
		return height;
	}
	
	public void renderLabel(JComponent body, String name, int x, int y) {
		JLabel label = new JLabel(name, SwingConstants.RIGHT);
		label.setBounds(x, y, 70, 24);
		label.setFont(labelFont);
		label.setVerticalAlignment(SwingConstants.CENTER);
		label.setForeground(new Color(0X505050));
		body.add(label);
	}
	
	public int renderObject(JComponent body, Object value, int x, int y) {
		int height;
		if (value instanceof Format) {
			height = renderFormat(body, (Format) value, x, y);
		} else if (value instanceof Entity) {
			height = renderEntity(body, (Entity) value, x, y);
		} else {
			height = renderString(body, value + "", x, y);
		}
		return height;
	}
	
	public int renderString(JComponent body, String name, int x, int y) {
		JLabel label = new JLabel(name);
		label.setFont(labelFont);
		Dimension size = label.getPreferredSize();
		label.setBounds(x, y, size.width, 24);
		label.setVerticalAlignment(SwingConstants.CENTER);
		body.add(label);
		return 24;
	}
	
	public int renderEntity(JComponent body, Entity entity, int x, int y) {
		JLabel label = new JLabel(entity + "");
		label.setFont(labelFont);
		Dimension size = label.getPreferredSize();
		label.setBounds(x, y, size.width, 24);
		label.setVerticalAlignment(SwingConstants.CENTER);
		label.addMouseListener(new FollowReference(entity));
		body.add(label);
		return 24;
	}
	
	public int renderFormat(JComponent body, Format format, int x, int y) {
		JTextField field = new JTextField(format.format());
		field.setFont(labelFont);
		field.setBounds(x - 4, y + 1, 230, 20);
		body.add(field);
		if (defaultField == null) {
			defaultField = field;
		}
		field.addActionListener(new ApplyValue(format, field));
		return 24;
	}
	
	class ApplyValue implements ActionListener {
		private Format format;
		private JTextField field;
		public ApplyValue(Format format, JTextField field) {
			this.format = format;
			this.field = field;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			format.apply(field.getText());
			field.select(0, Integer.MAX_VALUE);
		}
		
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
