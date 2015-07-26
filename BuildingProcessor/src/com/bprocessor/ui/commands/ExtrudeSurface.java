package com.bprocessor.ui.commands;

import java.util.LinkedList;
import java.util.List;

import com.bprocessor.Polyhedron;
import com.bprocessor.Surface;
import com.bprocessor.Vertex;
import com.bprocessor.util.ModifyEntity;

public class ExtrudeSurface extends ModifyEntity<Polyhedron> {
	private Surface target;
	private Vertex direction;
	private double distance;
	
	public ExtrudeSurface(Surface target, Vertex direction, double distance) {
		super((Polyhedron) target.getOwner());
		this.target = target;
		this.direction = direction;
		this.distance = distance;
	}
	@Override
	public void apply() {
		List<Surface> sides = new LinkedList<Surface>();
		List<Surface> tops = new LinkedList<Surface>();
		target.extrudeAll(direction, distance, sides, tops);
		for (Surface side: sides) {
			entity.insert(side);
		}
		for (Surface surface : tops) {
			entity.insert(surface);
		}
	}

	@Override
	public String description() {
		return "Extrude Surface";
	}

}
