package com.tianxiaohui.aipaobu;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Symbol;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.tianxiaohui.aipaobu.AiPaoBuContract.AiPaoBuPoint;
import com.tianxiaohui.aipaobu.AiPaoBuContract.AiPaoBuRun;
import com.tianxiaohui.aipaobu.vo.Point;

public class MainActivity extends Activity {
	private static final int STATUS_NO_RUN = 0;
	private static final int STATUS_RUNNING = 1;
	private static final int STATUS_PAUSED = 2;
	private int status = 0;
	
	public LocationClient mLocationClient = null;
	public MyLocationListener myListener = new MyLocationListener(this);
	
	private BMapManager bMapMgr = null;  
	public MapView mapView = null;  
	
	private String labelKey = "startStop";
	private static final String DATE_FORMAT = "M月d日 hh:mm:ss";
	public Map<String, Long> runMap = new HashMap<String, Long>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
	    mLocationClient.registerLocationListener( myListener );    //注册监听函数
	    
	    BDLocation lastKnownLocation = mLocationClient.getLastKnownLocation();
	    int latitude = 39915000;
	    int longitude = 116404000;
	    if (null != lastKnownLocation) {
	    	latitude = (int) (lastKnownLocation.getLatitude() * 1E6);
	    	longitude = (int) (lastKnownLocation.getLongitude() * 1E6);
	    }
	    
	    // init map
	    bMapMgr = new BMapManager(getApplication());  
	    bMapMgr.init("40b94f51a3ae3c29388cd3db14820024", null); 
	    
	    //注意：请在试用setContentView前初始化BMapManager对象，否则会报错  
	    setContentView(R.layout.activity_main);  
	    
	    mapView = (MapView) findViewById(R.id.bmapsView);  
	    mapView.setBuiltInZoomControls(true);  
	    
	    //设置启用内置的缩放控件  
	    MapController mMapController = mapView.getController();  
	    // 得到mMapView的控制权,可以用它控制和驱动平移和缩放  
	    GeoPoint point =new GeoPoint(latitude, longitude);  
	    //用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)  
	    mMapController.setCenter(point);//设置地图中心点  
	    mMapController.setZoom(12);//设置地图zoom级别
	    
	    // which label to show start? pause? or stop
	    if (null != savedInstanceState) {
			String curLabel = savedInstanceState.getString(labelKey);
			if (null != curLabel) {
				Button btn = (Button) this.findViewById(R.id.startStop);
				//Toast.makeText(this, "value" + curLabel + " " + btn, Toast.LENGTH_SHORT).show();
				//btn.setText("Eric");
				btn.setText(curLabel);
			}
		}
		
		this.initRunDropdownList();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Button btn = (Button) this.findViewById(R.id.startStop);
		outState.putString(labelKey, btn.getText().toString());
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void startPauseStop(View view) {
		Button btn = (Button) view;

		if (MainActivity.STATUS_NO_RUN == this.status) {
			if (mLocationClient != null) {
				LocationClientOption option = new LocationClientOption();
				option.setOpenGps(true);
				option.setAddrType("all");//返回的定位结果包含地址信息
				option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
				option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
				option.disableCache(true);//禁止启用缓存定位
				option.setPoiNumber(5);	//最多返回POI个数	
				option.setPoiDistance(1000); //poi查询距离		
				option.setPoiExtraInfo(false); //是否需要POI的电话和地址等详细信息		
				mLocationClient.setLocOption(option);
	
				mLocationClient.start();
				mLocationClient.requestLocation();
				
				this.myListener.startRun();
				this.myListener.startSection();
			} else {
				//
			}
			
			this.status = MainActivity.STATUS_RUNNING;
			btn.setText(R.string.stop);
		} else if(MainActivity.STATUS_RUNNING == this.status) {
			Log.e("test", "start end run");
			
			if (mLocationClient != null && mLocationClient.isStarted()) {
				mLocationClient.stop();
			} else {
				Log.e("test", mLocationClient + " status " + mLocationClient.isStarted());
			}
			
			//Toast.makeText(this, "good " + this.myListener, Toast.LENGTH_SHORT).show();
			this.myListener.endSection();
			this.myListener.endRun();
			
			this.status = MainActivity.STATUS_NO_RUN;
			btn.setText(R.string.start);
		}
	}
	
	/*
	public void showInfo(View view) {
		AiPaoBuDbHelper dbHelper = new AiPaoBuDbHelper(this);
		String msg = "run: ";
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select count(1) as cnt from AiPaoBuRun";
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		msg += cursor.getInt(cursor.getColumnIndexOrThrow("cnt")) + "  point: ";
		
		sql = "select count(1) as cnt from AiPaoBuPoint";
		cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		db.close();
		msg += cursor.getInt(cursor.getColumnIndexOrThrow("cnt"));
				
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		
		
	}
	*/
	
	@Override  
	protected void onDestroy() {
		mapView.destroy();
		if (bMapMgr != null) {
			bMapMgr.destroy();
			bMapMgr = null;
		}
		super.onDestroy();
	}  
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override  
	protected void onPause(){  
		mapView.onPause();  
	        if(bMapMgr!=null){  
	        	bMapMgr.stop();  
	        }  
	        super.onPause();  
	}  
	@Override  
	protected void onResume(){  
		mapView.onResume();  
	        if(bMapMgr!=null){  
	        	bMapMgr.start();  
	        }  
	       super.onResume();  
	}  
	
	private void initRunDropdownList() {
		AiPaoBuDbHelper dbHelper = new AiPaoBuDbHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select * from AiPaoBuRun order by _id desc";
		
		SimpleDateFormat sdf = new SimpleDateFormat(MainActivity.DATE_FORMAT);
		Cursor cursor = db.rawQuery(sql, null);
		long time = 0l;
		List<String> timeList = new ArrayList<String>();
		String tmpDateStr = null;
		this.runMap.clear();
		while (cursor.moveToNext()) {
			time = cursor.getLong(cursor.getColumnIndexOrThrow(AiPaoBuRun.COLUMN_NAME_START_TIME));
			tmpDateStr = sdf.format(new Date(time));
			timeList.add(tmpDateStr);
			this.runMap.put(tmpDateStr, time);
		}
		db.close();
		
		Spinner runListSpinner = (Spinner) this.findViewById(R.id.spinner1);
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, timeList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		runListSpinner.setAdapter(dataAdapter);
		
		runListSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener(this));
	}
	
	private void pushToFile(List<Point> pList) {
		BufferedWriter bw = null;
		try {
			File myFile = this.getMyExtFile();
			if (null != myFile) {
				bw = new BufferedWriter(new FileWriter(myFile, true));
				
				for (Point p : pList) {
					bw.write(p.getTime() + ":" + p.getLatitude() + "," + p.getLongitude() + "\r\n");
				}
			} else {
				Toast.makeText(this, "Can't create data file!",
						Toast.LENGTH_SHORT).show();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != bw) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	private File getMyExtFile() throws IOException {
		File rootFile = Environment.getExternalStorageDirectory();
		File myDir = new File(rootFile, "com.tianxiaohui.first");
		
		if (!myDir.exists()) {
			if (!myDir.mkdir()) {
				return null;
			}
		}
		
		File myFile = new File(myDir, "gpsPoint.txt");
		
		if (!myFile.exists()) {
			if (!myFile.createNewFile()) {
				return null;
			}
		}
		
		return myFile;
	}
	
	public void drawPath(List<Point> pointList) {
		if (null == pointList) {
			return;
		}

		if (pointList.size() > 2) {
			//int before = pointList.size();
			
			
			double latSum = 0;
			double longSum = 0;
			Iterator<Point> iterator = pointList.iterator();
			Point tmpPoint = null;
			double errVal = 4.9E-324;
			while (iterator.hasNext()) {
				tmpPoint = iterator.next();
				if (errVal == tmpPoint.getLatitude() || errVal == tmpPoint.getLongitude()) {
					iterator.remove();
					continue;
				}
				if (Math.abs(tmpPoint.getLatitude()) > 90 || Math.abs(tmpPoint.getLongitude()) > 180) {
					iterator.remove();
					continue;
				}
				latSum += tmpPoint.getLatitude();
				longSum += tmpPoint.getLongitude();
			}
			double latAvg = latSum / pointList.size();
			double longAvg = longSum / pointList.size();
			
			// reduce noise
			List<GeoPoint> gpList = new ArrayList<GeoPoint>();
			for (Point p : pointList) {
				if (Math.abs(latAvg - p.getLatitude()) < 4 && Math.abs(longAvg - p.getLongitude()) < 10) {
					gpList.add(new GeoPoint((int) (p.getLatitude() * 1E6),
								(int) (p.getLongitude() * 1E6)));
				}
			}
			//int after = pointList.size();
			
			//Toast.makeText(this, "b: " + before + " a:" + after + " lv:" + latAvg + " av" + longAvg, Toast.LENGTH_SHORT).show();
			
			// draw the map line
			Geometry geometry = new Geometry();
			geometry.setPolyLine(gpList.toArray(new GeoPoint[gpList.size()]));

			Symbol palaceSymbol = new Symbol();// 创建样式
			Symbol.Color palaceColor = palaceSymbol.new Color();// 创建颜色
			palaceColor.red = 0;// 设置颜色的红色分量
			palaceColor.green = 0;// 设置颜色的绿色分量
			palaceColor.blue = 255;// 设置颜色的蓝色分量
			palaceColor.alpha = 126;// 设置颜色的alpha值
			palaceSymbol.setLineSymbol(palaceColor, 7);// 设置样式参数，颜色：palaceColor是否填充距形：是线

			Graphic palaceGraphic = new Graphic(geometry, palaceSymbol);

			GraphicsOverlay palaceOverlay = new GraphicsOverlay(this.mapView);

			long palaceId = palaceOverlay.setData(palaceGraphic);
			// 将overlay添加到mapview中
			this.mapView.getOverlays().clear();
			this.mapView.getOverlays().add(palaceOverlay);
			// 刷新地图使新添加的overlay生效
			this.mapView.refresh();
			// 移动，缩放地图到最视野
			this.mapView.getController().setZoom(16);
			// set center
			this.mapView.getController().setCenter(gpList.get(gpList.size() / 2));
			
		} else {
			Toast.makeText(this, "Too less data", Toast.LENGTH_SHORT).show();
		}

		//this.pushToFile(pointList);
	}
}

class MyOnItemSelectedListener implements OnItemSelectedListener {
	MainActivity ma = null;
	MyOnItemSelectedListener(MainActivity ma) {
		this.ma = ma;
	}
	
	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
		String selected = (String) adapterView.getSelectedItem();
		//Toast.makeText(adapterView.getContext(), view.getId() + " pos: " + adapterView.getItemAtPosition(pos) + " id: " + selected, Toast.LENGTH_SHORT).show();
		
		
		AiPaoBuDbHelper dbHelper = new AiPaoBuDbHelper(AiPaoBuApp.getAppContext());
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select * from AiPaoBuPoint where sectionId in (select _id from AiPaoBuSection where runId in (select _id from AiPaoBuRun where startTime = " + this.ma.runMap.get(selected) + "))";
		Cursor cursor = db.rawQuery(sql, null);
		List<Point> pointList = new ArrayList<Point>();
		Point p = null;
		while (cursor.moveToNext()) {
			p = new Point();
			p.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(AiPaoBuPoint.COLUMN_NAME_LATITUDE)));
			p.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(AiPaoBuPoint.COLUMN_NAME_LONGITUDE)));
			p.setTime(cursor.getLong(cursor.getColumnIndexOrThrow(AiPaoBuPoint.COLUMN_NAME_TIME)));
			pointList.add(p);
		}
		db.close();
		
		this.ma.drawPath(pointList);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
