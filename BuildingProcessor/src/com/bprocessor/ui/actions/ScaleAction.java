package com.bprocessor.ui.actions;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.bprocessor.Attribute;
import com.bprocessor.Format;
import com.bprocessor.Mesh;
import com.bprocessor.Vertex;

public class ScaleAction extends Action {
	private Mesh target;
	private double factor;

	public ScaleAction() { }

	public ScaleAction(ScaleAction prototype) {
		super(prototype);
	}

	public ScaleAction(Mesh target, double factor) {
		this.target = target;
		this.factor = factor;
	}

	@Override
	public void evaluate() {
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
			Vertex scaled = translated.scale(factor);
			current.set(scaled.add(center));
		}
	}

	public List<Attribute> getAttributes() {
		List<Attribute> attributes = super.getAttributes();
		List<Attribute> section = new LinkedList<Attribute>();
		section.add(new Attribute("Mesh", target));
		section.add(new Attribute("Factor", new Format() {
			@Override
			public String format() {
				return String.valueOf(factor);
			}
			@Override
			public void apply(String value) {
				factor = Double.valueOf(value);
			}
		}));
		attributes.add(new Attribute("Scale Mesh", section));
		return attributes;
	}
}
