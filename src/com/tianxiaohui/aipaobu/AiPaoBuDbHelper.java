package com.tianxiaohui.aipaobu;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AiPaoBuDbHelper extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "AiPaoBu.db";
    
	public AiPaoBuDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(AiPaoBuContract.AiPaoBuRun.SQL_CREATE_HISTORY);
		db.execSQL(AiPaoBuContract.AiPaoBuSection.SQL_CREATE_SECTION);
		db.execSQL(AiPaoBuContract.AiPaoBuPoint.SQL_CREATE_POINT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(AiPaoBuContract.AiPaoBuRun.SQL_DELETE_HISTORY);
		db.execSQL(AiPaoBuContract.AiPaoBuSection.SQL_DELETE_SECTION);
		db.execSQL(AiPaoBuContract.AiPaoBuPoint.SQL_DELETE_POINT);
        onCreate(db);
	}

}
