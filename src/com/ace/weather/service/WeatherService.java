package com.ace.weather.service;

import com.ace.weather.MainActivity;
import com.ace.weather.R;
import com.ace.weather.bean.Result;
import com.ace.weather.bean.Weather;
import com.ace.weather.constant.Constant;
import com.ace.weather.listener.OnCallBack;
import com.ace.weather.util.SPUtil;
import com.ace.weather.util.ScheduleUtil;
import com.ace.weather.util.WeatherUtil;
import com.ace.weather.util.YxUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.text.Html;
import android.widget.RemoteViews;

public class WeatherService extends Service {

	Handler handler = new Handler();
	Context context;

	private static Long last_showd_time = 0l;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// 开启屏幕
			if (Intent.ACTION_SCREEN_ON.equals(action)) {
				intent = new Intent(context, WeatherService.class);
				startService(intent);
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
		WeatherUtil.init(context);
		ScheduleUtil.init(context);

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(receiver, filter);

		last_showd_time = (Long) SPUtil.get(context, "last_showd_time", 0l);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// 循环间隔
				handler.postDelayed(this, 40 * 1000);

				ScheduleUtil.run();

				WeatherUtil.getWeather(new OnCallBack<Result>() {

					@Override
					public void onCallBack(Result result) {
						try {
							switch (result.code) {
							// OK
							case Result.CODE_OK:

								// 刚刚请求的, 更新界面
								if ((System.currentTimeMillis() - WeatherUtil.getLastRequetTime()) < 1000) {
									WeatherUtil.sendUpdateViewBroadcast();
								}

								// 显示通知间隔
								long curr = System.currentTimeMillis();
								boolean show = (curr - last_showd_time) > WeatherUtil.noti_show_interval;
								if (last_showd_time == 0l) {
									show = false;
									last_showd_time = curr;
								}

								if (!show)
									return;

								setLastShowTime(curr);

								Notification noti = new Notification();
								noti.tickerText = result.today.tickerText;

								if (noti.tickerText != null) {
									noti.icon = R.drawable.ic_launcher;
									RemoteViews views = new RemoteViews(getPackageName(), R.layout.noti_main);

									String str = WeatherUtil.getCurrNotiWeather(result);
									views.setCharSequence(R.id.tv_weather, "setText", Html.fromHtml(str));

									str = WeatherUtil.getWeatherStr(result.weather_data.get(1));
									views.setCharSequence(R.id.tv_weather_tomorrow, "setText", Html.fromHtml(str));
									noti.contentView = views;
									noti.when = curr + 100;
									noti.flags = Notification.FLAG_AUTO_CANCEL;
									Intent intent = new Intent(context, MainActivity.class);
									PendingIntent pi = PendingIntent.getActivity(context, 123, intent, PendingIntent.FLAG_UPDATE_CURRENT);
									noti.contentIntent = pi;
									NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
									nm.notify(R.string.app_name, noti);

									Service service = (Service) context;
									Vibrator mVibrator01 = (Vibrator) service.getApplication().getSystemService(Service.VIBRATOR_SERVICE);
									mVibrator01.vibrate(new long[] { 100, 300, 200, 300 }, -1);

								}
								break;
							}
						} catch (Exception e) {
							YxUtil.showToast(context, e.toString());
						}
					}

				});
			}
		}, 10);

		return START_STICKY;
	}

	private void setLastShowTime(long curr) {
		last_showd_time = curr;
		SPUtil.put(context, "last_showd_time", last_showd_time);
	}

}
