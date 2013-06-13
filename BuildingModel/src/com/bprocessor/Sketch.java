package com.bprocessor;

public class Sketch extends Geometry {
	private long uid;
	private String name;
	private Group group;
	private boolean modified;
	private String path;
	
	public Sketch() {
	}
	public Sketch(String name) {
		this.name = name;
		this.group = new Group("group");
	}
	
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Group getGroup() {
		return group;
	}
	public void setGroup(Group group) {
		this.group = group;
	}
	public boolean isModified() {
		return modified;
	}
	public void setModified(boolean modified) {
		this.modified = modified;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
}
