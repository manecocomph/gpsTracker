package com.tianxiaohui.aipaobu;

import android.app.Application;
import android.content.Context;

public class AiPaoBuApp extends Application {
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		AiPaoBuApp.context = this.getApplicationContext();
	}
	
	public static Context getAppContext() {
        return AiPaoBuApp.context;
    }
}
