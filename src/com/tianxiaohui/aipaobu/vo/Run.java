package com.tianxiaohui.aipaobu.vo;

import java.io.Serializable;
import java.util.List;

public class Run implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private long id;
	private long startTime;
	private long endTime;
	private long timeUsed;
	private float svgSpeed;
	private List<Section> sectionList;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public long getTimeUsed() {
		return timeUsed;
	}
	public void setTimeUsed(long timeUsed) {
		this.timeUsed = timeUsed;
	}
	public float getSvgSpeed() {
		return svgSpeed;
	}
	public void setSvgSpeed(float svgSpeed) {
		this.svgSpeed = svgSpeed;
	}
	public List<Section> getSectionList() {
		return sectionList;
	}
	public void setSectionList(List<Section> sectionList) {
		this.sectionList = sectionList;
	}
	
	
}
