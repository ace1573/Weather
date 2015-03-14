package com.ace.weather.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Vibrator;
import android.text.Html;
import android.view.WindowManager;
import android.widget.RemoteViews;

import com.ace.weather.R;
import com.ace.weather.bean.ScheduleItem;
import com.ace.weather.component.YxAlertBuilder;
import com.ace.weather.service.ScheduleService;

public class ScheduleUtil {
	public static Context context;

	public static final String FILE_SCHEDULES = "FILE_LIST_SCHEDULES";

	public static ArrayList<ScheduleItem> sches;

	private static SimpleDateFormat format;

	private static Long lastNotiShowTime = 0l;
	public static Long schedule_noti_show_interval = 20 * 60 * 1000l;

	public static void setNotiShowInterval(double minute) {
		schedule_noti_show_interval = (long) minute * 60 * 1000;
		SPUtil.put(context, "schedule_noti_show_interval", schedule_noti_show_interval);
	}

	public static void init(Context ctx) {
		context = ctx;
		if (sches == null)
			sches = (ArrayList<ScheduleItem>) ObjectWriter.read(ctx, FILE_SCHEDULES);
		if (sches == null)
			sches = new ArrayList<ScheduleItem>();

		schedule_noti_show_interval = (Long) SPUtil.get(context, "schedule_noti_show_interval", schedule_noti_show_interval);
		format = new SimpleDateFormat("HH.mm");
	}

	public static void write() {
		ObjectWriter.write(context, sches, FILE_SCHEDULES);
	}

	public static ArrayList<ScheduleItem> get() {

		Collections.sort(sches, new Comparator<ScheduleItem>() {

			@Override
			public int compare(ScheduleItem lhs, ScheduleItem rhs) {
				int l = lhs.hour * 60 + lhs.minute;
				int r = rhs.hour * 60 + rhs.minute;
				return l - r;
			}
		});
		return sches;
	}

	public static void run() {

		try {

			if (sches.size() == 0)
				return;

			ArrayList<ScheduleItem> first_shows = new ArrayList<ScheduleItem>();
			ArrayList<ScheduleItem> repeat_shows = new ArrayList<ScheduleItem>();
			long now_time = System.currentTimeMillis();
			for (ScheduleItem item : sches) {
				if (item.show) {
					repeat_shows.add(item);
					continue;
				}

				//到时间了
				if(now_time>=item.noti_time){
					item.show = true;
					first_shows.add(item);
				}
			}

			boolean vibrate = false;
			if (first_shows.size() != 0) {
				write();
				showNoti(first_shows);
				vibrate = true;
			}

			if (repeat_shows.size() != 0) {
				if ((now_time - lastNotiShowTime) > schedule_noti_show_interval) {
					showNoti(repeat_shows);
					vibrate = true;
				}
			}

			if (vibrate) {
				Service service = (Service) context;
				Vibrator mVibrator01 = (Vibrator) service.getApplication().getSystemService(Service.VIBRATOR_SERVICE);
				mVibrator01.vibrate(new long[] { 100, 300, 200, 300 }, -1);
			}
		} catch (Exception e) {
			YxUtil.showToast(context, e.toString());
		}

	}

	public static void showNoti(ArrayList<ScheduleItem> shows) {
		for (ScheduleItem item : shows) {
			
			lastNotiShowTime = System.currentTimeMillis();
			
			Notification noti = new Notification();
			noti.tickerText = null;
			noti.tickerText = "待办事项";

			noti.icon = R.drawable.ic_launcher;
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.noti_sche);

			StringBuilder sb = new StringBuilder();
			String str = WeatherUtil.getFontHtml("999999", item.time.replace(".", ":"));
			sb.append(str);
			str = WeatherUtil.getFontHtml("00BCD4", item.name);
			sb.append(str);

			views.setCharSequence(R.id.tv_sche, "setText", Html.fromHtml(sb.toString()));
			noti.contentView = views;
			noti.when = System.currentTimeMillis() + 100;
			noti.flags = Notification.FLAG_AUTO_CANCEL;
			Intent intent = new Intent(context, ScheduleService.class);
			intent.putExtra("id", item.noti_time);
			PendingIntent pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			noti.contentIntent = pi;
			noti.flags |= Notification.FLAG_AUTO_CANCEL;

			// noti.ledARGB = 0xff00ff00;
			// noti.ledOnMS = 1000;
			// noti.ledOffMS = 1000;

			NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			long id = item.noti_time % Integer.MAX_VALUE;
			nm.notify((int) id, noti);
		}
	}

	public static void openDeleteConfirm(final Long id) {
		Dialog dialog = YxAlertBuilder.create(context, "提示", "删除该代办事项?", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Iterator<ScheduleItem> it = sches.iterator();
				while (it.hasNext()) {
					ScheduleItem item = it.next();
					if (id.equals(item.noti_time)) {
						it.remove();
						break;
					}
				}
				write();
			}
		});
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();

	}
}
