package com.ace.weather.app;

import com.ace.weather.receiver.MyReceiver;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

public class MyApp extends Application{
	
	private static BroadcastReceiver receiver;
	@Override
	public void onCreate() {
		super.onCreate();
		
//		receiver = new MyReceiver();
//		IntentFilter filter = new IntentFilter();
//		filter.addAction(Intent.ACTION_TIME_TICK);
//		registerReceiver(receiver, filter);
	}
}
