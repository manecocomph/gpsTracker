package com.tianxiaohui.aipaobu;

import android.provider.BaseColumns;

public class AiPaoBuContract {
	private AiPaoBuContract() {
	};
	
	private static final String TEXT_TYPE = " TEXT";
	private static final String INTEGER_TYPE = " INTEGER";
	private static final String REAL_TYPE = " REAL";
	private static final String COMMA_SEP = ",";
	
	/* Inner class that defines the table contents */
    public static abstract class AiPaoBuRun implements BaseColumns {
        public static final String TABLE_NAME = "AiPaoBuRun";
        //public static final String COLUMN_NAME_HISTORY_ID = "runId";
        public static final String COLUMN_NAME_START_TIME = "startTime";
        public static final String COLUMN_NAME_END_TIME = "endTime";
        public static final String COLUMN_NAME_TIME_USED = "timeUsed";
        public static final String COLUMN_NAME_AVG_SPEED = "avgSpeed";
        public static final String COLUMN_NAME_SYNCED = "synced";  // 0 not, 1, syncing, 2 success
        
        public static final String SQL_CREATE_HISTORY =
        	    "CREATE TABLE " + AiPaoBuRun.TABLE_NAME + " (" +
        	    AiPaoBuRun._ID + " INTEGER PRIMARY KEY," +
        	    AiPaoBuRun.COLUMN_NAME_START_TIME + INTEGER_TYPE + COMMA_SEP +
        	    AiPaoBuRun.COLUMN_NAME_END_TIME + INTEGER_TYPE + COMMA_SEP +
        	    AiPaoBuRun.COLUMN_NAME_TIME_USED + INTEGER_TYPE + COMMA_SEP +
        	    AiPaoBuRun.COLUMN_NAME_AVG_SPEED + REAL_TYPE + COMMA_SEP +
        	    AiPaoBuRun.COLUMN_NAME_SYNCED + INTEGER_TYPE + 
        	    " )";

        public static final String SQL_DELETE_HISTORY =
        	    "DROP TABLE IF EXISTS " + AiPaoBuRun.TABLE_NAME;
    }
    
    public static abstract class AiPaoBuSection implements BaseColumns {
        public static final String TABLE_NAME = "AiPaoBuSection";
        //public static final String COLUMN_NAME_SECTION_ID = "sectionId";
        public static final String COLUMN_NAME_RUN_ID = "runId";
        public static final String COLUMN_NAME_START_TIME = "startTime";
        public static final String COLUMN_NAME_END_TIME = "endTime";
        
        public static final String SQL_CREATE_SECTION =
        	    "CREATE TABLE " + AiPaoBuSection.TABLE_NAME + " (" +
        	    AiPaoBuSection._ID + " INTEGER PRIMARY KEY," +
        	    AiPaoBuSection.COLUMN_NAME_RUN_ID + INTEGER_TYPE + COMMA_SEP +
        	    AiPaoBuSection.COLUMN_NAME_START_TIME + INTEGER_TYPE + COMMA_SEP +
        	    AiPaoBuSection.COLUMN_NAME_END_TIME + INTEGER_TYPE +
        	    " )";

        public static final String SQL_DELETE_SECTION =
        	    "DROP TABLE IF EXISTS " + AiPaoBuSection.TABLE_NAME;
    }
    
    public static abstract class AiPaoBuPoint implements BaseColumns {
        public static final String TABLE_NAME = "AiPaoBuPoint";
        //public static final String COLUMN_NAME_POINT_ID = "pointId";
        public static final String COLUMN_NAME_SECTION_ID = "sectionId";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        
        public static final String SQL_CREATE_POINT =
        	    "CREATE TABLE " + AiPaoBuPoint.TABLE_NAME + " (" +
        	    AiPaoBuPoint._ID + " INTEGER PRIMARY KEY," +
        	    AiPaoBuPoint.COLUMN_NAME_SECTION_ID + INTEGER_TYPE + COMMA_SEP +
        	    AiPaoBuPoint.COLUMN_NAME_TIME + INTEGER_TYPE + COMMA_SEP +
        	    AiPaoBuPoint.COLUMN_NAME_LATITUDE + REAL_TYPE + COMMA_SEP +
        	    AiPaoBuPoint.COLUMN_NAME_LONGITUDE + REAL_TYPE +
        	    " )";

        public static final String SQL_DELETE_POINT =
        	    "DROP TABLE IF EXISTS " + AiPaoBuPoint.TABLE_NAME;
    }
}
