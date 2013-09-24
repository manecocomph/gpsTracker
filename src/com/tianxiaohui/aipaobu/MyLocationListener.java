package com.tianxiaohui.aipaobu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.Symbol;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.tianxiaohui.aipaobu.vo.Point;
import com.tianxiaohui.aipaobu.vo.Run;
import com.tianxiaohui.aipaobu.vo.Section;

public class MyLocationListener implements BDLocationListener {
	private Run run = null;
	private Section curSection = null;
	private List<Point> curPointList = null;
	private MainActivity ma = null;

	public MyLocationListener(MainActivity ma) {
		this.ma = ma;
	}
	
	@Override
	public void onReceiveLocation(BDLocation location) {
		if (location == null) {
			return;
		}
		
		Point p = new Point();
		p.setTime(System.currentTimeMillis());
		p.setLatitude(location.getLatitude());
		p.setLongitude(location.getLongitude());
		
		this.curPointList.add(p);
		
		MapController mMapController = this.ma.mapView.getController();  
	    // 得到mMapView的控制权,可以用它控制和驱动平移和缩放  
		int latitude = (int) (location.getLatitude() * 1E6);
    	int longitude = (int) (location.getLongitude() * 1E6);
	    GeoPoint point =new GeoPoint(latitude, longitude);  
	    //用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)  
	    mMapController.setCenter(point);//设置地图中心点 
		
		//this.addPoint(p, false);
		/*
		StringBuffer sb = new StringBuffer(256);
		sb.append("time : ");
		sb.append(location.getTime());
		sb.append("\nerror code : ");
		sb.append(location.getLocType());
		sb.append("\nlatitude : ");
		sb.append(location.getLatitude());
		sb.append("\nlontitude : ");
		sb.append(location.getLongitude());
		sb.append("\nradius : ");
		sb.append(location.getRadius());
		if (location.getLocType() == BDLocation.TypeGpsLocation) {
			sb.append("\nspeed : ");
			sb.append(location.getSpeed());
			sb.append("\nsatellite : ");
			sb.append(location.getSatelliteNumber());
		} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
			sb.append("\naddr : ");
			sb.append(location.getAddrStr());
		}

		//logMsg(sb.toString());
		 */
	}

	public void onReceivePoi(BDLocation poiLocation) {
		if (poiLocation == null) {
			return;
		}
		StringBuffer sb = new StringBuffer(256);
		sb.append("Poi time : ");
		sb.append(poiLocation.getTime());
		sb.append("\nerror code : ");
		sb.append(poiLocation.getLocType());
		sb.append("\nlatitude : ");
		sb.append(poiLocation.getLatitude());
		sb.append("\nlontitude : ");
		sb.append(poiLocation.getLongitude());
		sb.append("\nradius : ");
		sb.append(poiLocation.getRadius());
		if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
			sb.append("\naddr : ");
			sb.append(poiLocation.getAddrStr());
		}
		if (poiLocation.hasPoi()) {
			sb.append("\nPoi:");
			sb.append(poiLocation.getPoi());
		} else {
			sb.append("noPoi information");
		}
		//logMsg(sb.toString());
	}
	
	private synchronized void addPoint(Point p, boolean forcePersist) {
		if (null != p) {
			this.curPointList.add(p);
		}
		
		if (this.curPointList.size() > Config.MAX_POINTS_IN_MEMO || forcePersist) {
			// send to service and persist
			Intent service = new Intent(AiPaoBuApp.getAppContext(), DbPersistentService.class);
			Bundle pointListBundle = new Bundle();
			pointListBundle.putSerializable("pointList", (Serializable) this.curPointList);
			service.putExtra("pointListBundle", pointListBundle);
			
			AiPaoBuApp.getAppContext().startService(service);
			//this.pointList.clear();  // used by multi-thread
			//TOTO need to check, if use clear should be ok or not!
			this.curPointList = Collections.synchronizedList(new ArrayList<Point>(Config.MAX_POINTS_IN_MEMO + 2));
		}
	}
	
	public void startRun() {
		this.run = new Run();
		this.run.setStartTime(System.currentTimeMillis());
		this.run.setSectionList(new ArrayList<Section>());
	}
	
	public void endRun() {
		this.run.setEndTime(System.currentTimeMillis());
		// leave complex compute to service class
		// draw the map line
		List<Point> pointList = new ArrayList<Point>();
		for (Section sec : this.run.getSectionList() ){
			if (null != sec.getPointList()) {
				for (Point p : sec.getPointList()) {
					pointList.add(p);
				}
			}
		}
		
		if (pointList.size() < 6) {
			return;
		}
		
		Intent service = new Intent(AiPaoBuApp.getAppContext(), DbPersistentService.class);
		Bundle pointListBundle = new Bundle();
		pointListBundle.putSerializable("runInfo", (Serializable) this.run);
		service.putExtra("runInfoBundle", pointListBundle);
		
		Log.i("test", "in listener end run");
		AiPaoBuApp.getAppContext().startService(service);
		Log.i("test", "after listener end run");
		
		this.ma.drawPath(pointList);
	}
	
	public void startSection() {
		this.curSection = new Section();
		this.curSection.setStartTime(System.currentTimeMillis());
		
		this.curPointList = Collections.synchronizedList(new ArrayList<Point>(Config.MAX_POINTS_IN_MEMO + 2));
	}
	
	public void endSection() {
		this.curSection.setEndTime(System.currentTimeMillis());
		this.curSection.setPointList(this.curPointList);
		
		this.run.getSectionList().add(this.curSection);
	}
}