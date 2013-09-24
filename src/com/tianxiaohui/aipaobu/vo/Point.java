package com.tianxiaohui.aipaobu.vo;

import java.io.Serializable;

public class Point implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private long runid;
	private long time;
	private double latitude;
	private double longitude;
	
	public long getRunid() {
		return runid;
	}
	public void setRunid(long runid) {
		this.runid = runid;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
}
