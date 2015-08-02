package com.bprocessor.ui.actions;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.bprocessor.Attribute;
import com.bprocessor.Format;
import com.bprocessor.Mesh;
import com.bprocessor.Vertex;
import com.bprocessor.util.Matrix;

public class RotateAction extends Action {
	private Mesh target;
	private double degrees;

	public RotateAction() { }

	public RotateAction(RotateAction prototype) {
		super(prototype);
	}

	public RotateAction(Mesh target, double degrees) {
		this.target = target;
		this.degrees = degrees;
	}

	@Override
	public void evaluate() {
		double angle = degrees * Math.PI / 180.0;

		Vertex axis = new Vertex(0, 0, 1);
		Matrix rotation = Matrix.rotation(angle, axis.getX(), axis.getY(), axis.getZ());
		HashSet<Vertex> vertices = new HashSet<Vertex>();
		target.collectVertices(vertices);
		Vertex center = null;
		for (Vertex current : vertices) {
			if (center == null) {
				center = current.copy();
			} else {
				center.set(center.add(current));
			}
		}
		center.scaleIt(1.0 / vertices.size());
		for (Vertex current : vertices) {
			Vertex translated = current.minus(center);
			Vertex rotated = rotation.multiply(translated);
			current.set(rotated.add(center));
		}
	}

	public List<Attribute> getAttributes() {
		List<Attribute> attributes = super.getAttributes();
		List<Attribute> section = new LinkedList<Attribute>();
		section.add(new Attribute("Mesh", target));
		section.add(new Attribute("Degrees", new Format() {
			@Override
			public String format() {
				return String.valueOf(degrees);
			}
			@Override
			public void apply(String value) {
				degrees = Double.valueOf(value);
			}
		}));
		attributes.add(new Attribute("Rotate Mesh", section));
		return attributes;
	}
}
