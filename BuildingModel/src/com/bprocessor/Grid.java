package com.bprocessor;

import java.util.LinkedList;
import java.util.List;

import com.bprocessor.util.CoordinateSystem;
import com.bprocessor.util.Operation;

public class Grid extends Component {
	private CoordinateSystem system;
	private double hsize;
	private double hgutter;
	private int hcount;
	private double vsize;
	private double vgutter;
	private int vcount;

	private Net net;

	public Grid() {}

	public Grid(Grid prototype) {
		super(prototype);
	}

	public Grid(String name) {
		super(name);
		system = CoordinateSystem.xy();
		hsize = 0.8;
		hgutter = 0.2;
		hcount = 20;
		vsize = 0.8;
		vgutter = 0.2;
		vcount = 20;
		net = new Net("Grid Net");
		net.setSelectable(false);
		setup();
	}
	
	public void apply(CoordinateSystem value) {
		system = value;
		setup();
	}

	public void setNet(Net value) {
		net = value;
	}
	public Net getNet() {
		return net;
	}

	@Override
	public Mesh display() {
		return net;
	}

	private void setup() {
		net.clear();
		Color color = new Color(221.0f/255, 234.0f/255, 254.0f/ 255);
		Vertex i = system.getI();
		Vertex j = system.getJ();
		Vertex origin = system.getOrigin();
		{
			double length = vcount * (vsize + vgutter) - vgutter;
			Vertex from = origin;
			Vertex v = j.scale(length);
			for (int k = 0; k < hcount; k++) {
				Vertex to1 = from.add(v);
				net.add(new Line(from, to1, color));
				from = from.add(i.scale(hsize));
				if (hgutter > 0) {
					Vertex to2 = from.add(v);
					net.add(new Line(from, to2, color));
					from = from.add(i.scale(hgutter));
				}
			}
		}
		{
			double length = hcount * (hsize + hgutter) - hgutter;
			Vertex from = origin;
			Vertex v = i.scale(length);
			int n = vcount;
			if (vgutter == 0) {
				n++;
			}
			for (int k = 0; k < n; k++) {
				Vertex to1 = from.add(v);
				net.add(new Line(from, to1, color));
				from = from.add(j.scale(vsize));
				if (vgutter > 0) {
					Vertex to2 = from.add(v);
					net.add(new Line(from, to2, color));
					from = from.add(j.scale(vgutter));
				}
			}
		}
	}

	public List<Attribute> getAttributes() {
		List<Attribute> attributes = super.getAttributes();
		List<Attribute> section = new LinkedList<Attribute>();

		section.add(new Attribute("Col Width", new Format() {
			@Override
			public String format() {
				return String.valueOf(hsize);
			}
			@Override
			public void apply(String value) {
				hsize = Double.valueOf(value);
			}
			@Override
			public List<String> values() {
				return null;
			}
		}));
		section.add(new Attribute("Col Gutter", new Format() {
			@Override
			public String format() {
				return String.valueOf(hgutter);
			}
			@Override
			public void apply(String value) {
				hgutter = Double.valueOf(value);
			}
			@Override
			public List<String> values() {
				return null;
			}
		}));
		section.add(new Attribute("Columns", new Format() {
			@Override
			public String format() {
				return String.valueOf(hcount);
			}
			@Override
			public void apply(String value) {
				hcount = Integer.valueOf(value);
			}
			@Override
			public List<String> values() {
				return null;
			}
		}));
		section.add(new Attribute("Row Height", new Format() {
			@Override
			public String format() {
				return String.valueOf(vsize);
			}
			@Override
			public void apply(String value) {
				vsize = Double.valueOf(value);
			}
			@Override
			public List<String> values() {
				return null;
			}
		}));
		section.add(new Attribute("Row Gutter", new Format() {
			@Override
			public String format() {
				return String.valueOf(vgutter);
			}
			@Override
			public void apply(String value) {
				vgutter = Double.valueOf(value);
			}
			@Override
			public List<String> values() {
				return null;
			}
		}));
		section.add(new Attribute("Rows", new Format() {
			@Override
			public String format() {
				return String.valueOf(vcount);
			}
			@Override
			public void apply(String value) {
				vcount = Integer.valueOf(value);
			}
			@Override
			public List<String> values() {
				return null;
			}
		}));
		section.add(new Attribute("Net", net));
		section.add(new Attribute("Redraw", new Operation() {
			@Override
			public void evaluate() {
				setup();
			}
		}));
		attributes.add(new Attribute("Grid", section));
		return attributes;
	}


}
