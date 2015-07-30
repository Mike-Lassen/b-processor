package com.bprocessor.ui.actions;

import java.util.LinkedList;
import java.util.List;

import com.bprocessor.Attribute;
import com.bprocessor.Entity;
import com.bprocessor.Format;
import com.bprocessor.Surface;
import com.bprocessor.Vertex;
import com.bprocessor.ui.commands.ExtrudeSurface;
import com.bprocessor.util.Command;
import com.bprocessor.util.CommandManager;

public class ExtrudeSufaceAction extends Action {
	private Surface target;
	private double distance;
	
	public ExtrudeSufaceAction() {
	}

	public ExtrudeSufaceAction(Entity prototype) {
		super(prototype);
	}
	
	public ExtrudeSufaceAction(Surface target, double distance) {
		this.target = target;
		this.distance = distance;
	}
	
	public void evaluate() {
		Vertex direction = new Vertex(0, 0, 1);
		Command command = new ExtrudeSurface(target, direction, distance);
		CommandManager.instance().apply(command);
	}
	
	public List<Attribute> getAttributes() {
    	List<Attribute> attributes = super.getAttributes();
    	List<Attribute> section = new LinkedList<Attribute>();
    	section.add(new Attribute("Distance", new Format() {
			@Override
			public String format() {
				return String.valueOf(distance);
			}
			@Override
			public void apply(String value) {
				distance = Double.valueOf(value);
			}
			@Override
			public List<String> values() {
				return null;
			}
		}));
    	section.add(new Attribute("Surface", target));
    	attributes.add(new Attribute("Extrude Surface", section));
    	return attributes;
    }
}
