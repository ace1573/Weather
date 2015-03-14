package com.ace.weather.receiver;

import com.ace.weather.service.WeatherService;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("com.ace.weather")) {
			
			System.out.println("receive");

			// ¼ì²éService×´Ì¬
			boolean isServiceRunning = false;
			ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
				if ("com.ace.weather.service.WeatherService".equals(service.service.getClassName()))

				{
					isServiceRunning = true;
				}

			}
			if (!isServiceRunning) {
				Intent i = new Intent(context, WeatherService.class);
				context.startService(i);
			}
		}
	}

}
