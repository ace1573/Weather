package com.ace.weather.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import com.ace.weather.R;
import com.ace.weather.component.YxAlertBuilder;
import com.ace.weather.util.ScheduleUtil;
import com.ace.weather.util.ViewUtil;
import com.ace.weather.util.WeatherUtil;
import com.ace.weather.util.YxUtil;

public class InputSchedultIntervalActivity extends Activity {

	EditText edt_name;
	Button btn_set;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_activity);
		context = this;

		ViewUtil.findView(this);
		setTitle("���ô��������ظ����(����)");

		edt_name.setText((((double) ScheduleUtil.schedule_noti_show_interval) / 1000 / 60) + "");
		edt_name.setHint("�����ظ����ʱ��(����)");
		edt_name.setInputType(EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
		ViewUtil.moveCursor2End(edt_name);
		// ����
		btn_set.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				YxAlertBuilder.create(context, "��ʾ", "֪ͨ���ʱ����Ϊ" + edt_name.getText().toString() + "����?", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String str = edt_name.getText().toString().trim();
						try {
							double minute = Double.parseDouble(str);
							ScheduleUtil.setNotiShowInterval(minute);
							YxUtil.showToast(context, "���óɹ�");
							finish();
						} catch (Exception e) {
							YxUtil.showToast(context, "��������");
						}
					}
				}).show();
			}
		});
	}

}
