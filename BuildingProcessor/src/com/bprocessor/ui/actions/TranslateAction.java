package com.bprocessor.ui.actions;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.bprocessor.Attribute;
import com.bprocessor.Format;
import com.bprocessor.Mesh;
import com.bprocessor.Vertex;

public class TranslateAction extends Action {
	private Mesh target;
	private double x;
	private double y;
	private double z;

	public TranslateAction() { }

	public TranslateAction(TranslateAction prototype) {
		super(prototype);
	}

	public TranslateAction(Mesh target, double x, double y, double z) {
		this.target = target;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void evaluate() {

		HashSet<Vertex> vertices = new HashSet<Vertex>();
		target.collectVertices(vertices);
		Vertex center = null;
		Vertex vector = new Vertex(x, y, z);
		for (Vertex current : vertices) {
			if (center == null) {
				center = current.copy();
			} else {
				center.set(center.add(current));
			}
		}
		center.scaleIt(1.0 / vertices.size());
		for (Vertex current : vertices) {
			Vertex translated = current.add(vector);
			current.set(translated);
		}
	}

	public List<Attribute> getAttributes() {
		List<Attribute> attributes = super.getAttributes();
		List<Attribute> section = new LinkedList<Attribute>();
		section.add(new Attribute("Mesh", target));
		section.add(new Attribute("X", new Format() {
			@Override
			public String format() {
				return String.valueOf(x);
			}
			@Override
			public void apply(String value) {
				x = Double.valueOf(value);
			}
			@Override
			public List<String> values() {
				return null;
			}
		}));
		section.add(new Attribute("Y", new Format() {
			@Override
			public String format() {
				return String.valueOf(y);
			}
			@Override
			public void apply(String value) {
				y = Double.valueOf(value);
			}
			@Override
			public List<String> values() {
				return null;
			}
		}));
		section.add(new Attribute("Z", new Format() {
			@Override
			public String format() {
				return String.valueOf(z);
			}
			@Override
			public void apply(String value) {
				z = Double.valueOf(value);
			}
			@Override
			public List<String> values() {
				return null;
			}
		}));
		attributes.add(new Attribute("Translate Mesh", section));
		return attributes;
	}
}
