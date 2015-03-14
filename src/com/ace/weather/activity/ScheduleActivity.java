package com.ace.weather.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ace.weather.R;
import com.ace.weather.bean.ScheduleItem;
import com.ace.weather.component.BeanAdapter;
import com.ace.weather.component.OnPositive;
import com.ace.weather.component.YxInputBuilder;
import com.ace.weather.util.ScheduleUtil;
import com.ace.weather.util.ViewUtil;
import com.ace.weather.util.YxUtil;

public class ScheduleActivity extends Activity {

	Context context;

	ListView listview;

	BeanAdapter adapter;

	ArrayList<ScheduleItem> mList;

	private TextView tv_message;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_activity);
		context = this;

		if (ScheduleUtil.context == null)
			ScheduleUtil.init(context);
		mList = ScheduleUtil.get();

		ViewUtil.findView(this);
		setTitle("代办事项");

	}

	@Override
	protected void onStop() {
		super.onStop();
		new Thread() {
			public void run() {
				ScheduleUtil.write();
			};
		}.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("添加").setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				YxInputBuilder.create(context, "添加", null, null, new OnPositive() {

					@Override
					public void onClick(Dialog dialog, EditText edt_name, EditText edt_time) {

						ScheduleItem item = createScheItem(edt_name.getText().toString(), edt_time.getText().toString());

						if (item != null) {
							mList.add(item);
							adapter.notifyDataSetChanged();

							dialog.dismiss();
						}
					}

				}).show();
				return true;
			}
		}).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add("设置重复时间").setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(context, InputSchedultIntervalActivity.class);
				startActivity(intent);
				return true;
			}
		});
		return true;
	}

	private ScheduleItem createScheItem(String name, String time) {
		ScheduleItem item = new ScheduleItem();
		item.name = name;
		item.time = time;

		if (!time.matches("^[0-9]{1,2}\\.[0-9]{1,2}$")) {
			YxUtil.showToast(context, "时间格式应为 12.30");
			return null;
		}

		try {
			String hourStr = time.substring(0, time.indexOf("."));
			String minuteStr = time.substring(time.indexOf(".") + 1);

			int hour = Integer.parseInt(hourStr);
			int minute = Integer.parseInt(minuteStr);

			if (hour > 23 || minute > 59) {
				YxUtil.showToast(context, "小时不能大于23, 分钟不能大于59");
				return null;
			}
			item.hour = hour;
			item.minute = minute;

			if (hour < 10)
				hourStr = "0" + hour;
			if (minute < 10)
				minuteStr = "0" + minute;
			item.time = hourStr + "." + minuteStr;

			Date add_time = new Date();
			int add_minutes = add_time.getHours() * 60 + add_time.getMinutes();
			int noti_minutes = item.hour * 60 + item.minute;

			long noti_time_mills = add_time.getTime();
			noti_time_mills += (noti_minutes-add_minutes+1)*60*1000;
			//设为明天
			if (noti_minutes <= add_minutes) {
				noti_time_mills += 24*3600*1000l;
			}
			item.noti_time = noti_time_mills;

			return item;
		} catch (Exception e) {
			YxUtil.showToast(context, "时间格式应为 12.30");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		adapter = new BeanAdapter(context, R.layout.schedule_item, mList, ViewHolder.class) {

			@Override
			public void notifyDataSetChanged() {
				super.notifyDataSetChanged();
				setMessage();
			}

			@Override
			public void initView(int position, Object item, View view, Object viewHolder) {
				ViewHolder holder = (ViewHolder) viewHolder;
				ScheduleItem sche = (ScheduleItem) item;
				holder.tv_name.setText(sche.name);
				holder.tv_time.setText(sche.time);
			}
		};

		listview.setAdapter(adapter);
		setMessage();

		listview.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				menu.setHeaderTitle("操作");
				menu.add(0, 0, 0, "编辑");
				menu.add(0, 1, 1, "删除");
			}
		});
	}

	private void setMessage() {
		if (mList.size() == 0) {
			tv_message.setVisibility(View.VISIBLE);
		} else
			tv_message.setVisibility(View.GONE);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch ((int) item.getItemId()) {
		// 编辑
		case 0:
			final ScheduleItem sche = mList.get(info.position);
			YxInputBuilder.create(context, "编辑", sche.name, sche.time, new OnPositive() {

				@Override
				public void onClick(Dialog dialog, EditText edt_name, EditText edt_time) {

					ScheduleItem item = createScheItem(edt_name.getText().toString(), edt_time.getText().toString());

					if (item != null) {
						mList.set(info.position, item);
						adapter.notifyDataSetChanged();
						dialog.dismiss();
					}
				}
			}).show();
			break;
		// 删除
		case 1:
			mList.remove(info.position);
			adapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
		return true;
	}

	static class ViewHolder {
		public TextView tv_time;
		public TextView tv_name;
	}

}
