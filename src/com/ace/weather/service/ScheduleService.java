package com.ace.weather.service;

import com.ace.weather.util.ScheduleUtil;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;

public class ScheduleService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Long id = intent.getLongExtra("id", -1l);
		if (id != -1) {
			ScheduleUtil.openDeleteConfirm(id);
			return START_STICKY;
		}
		
		return super.onStartCommand(intent, flags, startId);
	}

}
