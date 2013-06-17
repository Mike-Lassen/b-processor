package com.bprocessor;

import java.util.LinkedList;
import java.util.List;

public class SketchInfoList {
	private String name;
	private List<SketchInfo> infos;
	
	public SketchInfoList() {}
	public SketchInfoList(String name) {
		this.name = name;
		infos = new LinkedList<SketchInfo>();
	}
	
	public String getName() {
		return  name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<SketchInfo> getInfos() {
		return infos;
	}
	public void setInfos(List<SketchInfo> infos) {
		this.infos = infos;
	}
}
