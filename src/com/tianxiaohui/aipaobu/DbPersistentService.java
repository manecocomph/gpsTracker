package com.tianxiaohui.aipaobu;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.tianxiaohui.aipaobu.AiPaoBuContract.AiPaoBuPoint;
import com.tianxiaohui.aipaobu.AiPaoBuContract.AiPaoBuRun;
import com.tianxiaohui.aipaobu.AiPaoBuContract.AiPaoBuSection;
import com.tianxiaohui.aipaobu.vo.Point;
import com.tianxiaohui.aipaobu.vo.Run;
import com.tianxiaohui.aipaobu.vo.Section;

public class DbPersistentService extends IntentService {
	private AiPaoBuDbHelper dbHelper = null;
	public DbPersistentService() {
		super(DbPersistentService.class.toString());
		this.dbHelper = new AiPaoBuDbHelper(AiPaoBuApp.getAppContext());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.e("test", "in my db persis service");
		
		Bundle pointListBundle = intent.getBundleExtra("runInfoBundle");
		if (null == pointListBundle) {
			return;
		}
		Run run = (Run) pointListBundle.getSerializable("runInfo");
		if (null == run) {
			return;
		}
		
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(AiPaoBuRun.COLUMN_NAME_START_TIME, run.getStartTime());
		values.put(AiPaoBuRun.COLUMN_NAME_END_TIME, run.getEndTime());
		values.put(AiPaoBuRun.COLUMN_NAME_TIME_USED, run.getTimeUsed());
		values.put(AiPaoBuRun.COLUMN_NAME_AVG_SPEED, run.getSvgSpeed());
		
		long runId = db.insert(AiPaoBuRun.TABLE_NAME, null, values);
		
		if (null == run.getSectionList()) {
			return;
		}
		
		for (Section section : run.getSectionList()) {
			values = new ContentValues();
			values.put(AiPaoBuSection.COLUMN_NAME_RUN_ID, runId);
			values.put(AiPaoBuSection.COLUMN_NAME_START_TIME, section.getStartTime());
			values.put(AiPaoBuSection.COLUMN_NAME_END_TIME, section.getEndTime());
			
			long sectionId = db.insert(AiPaoBuSection.TABLE_NAME, null, values);
			
			if (null != section.getPointList()) {
				for (Point p : section.getPointList()) {
					values = new ContentValues();
					values.put(AiPaoBuPoint.COLUMN_NAME_SECTION_ID, sectionId);
					values.put(AiPaoBuPoint.COLUMN_NAME_TIME, p.getTime());
					values.put(AiPaoBuPoint.COLUMN_NAME_LATITUDE, p.getLatitude());
					values.put(AiPaoBuPoint.COLUMN_NAME_LONGITUDE, p.getLongitude());
					
					db.insert(AiPaoBuPoint.TABLE_NAME, null, values);
				}
			}
		}
		
		db.close();
		Log.e("test", "after my db persis service");
	}

}
