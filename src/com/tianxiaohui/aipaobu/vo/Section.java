package com.tianxiaohui.aipaobu.vo;

import java.io.Serializable;
import java.util.List;

public class Section implements Serializable {
	private static final long serialVersionUID = 1L;

	private long runId;
	private long startTime;
	private long endTime;
	private List<Point> pointList;
	
	public long getRunId() {
		return runId;
	}
	public void setRunId(long runId) {
		this.runId = runId;
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
	public List<Point> getPointList() {
		return pointList;
	}
	public void setPointList(List<Point> pointList) {
		this.pointList = pointList;
	}
}
