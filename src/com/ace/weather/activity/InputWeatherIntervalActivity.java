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
import com.ace.weather.util.ViewUtil;
import com.ace.weather.util.WeatherUtil;
import com.ace.weather.util.YxUtil;

public class InputWeatherIntervalActivity extends Activity {

	EditText edt_name;
	Button btn_set;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_activity);
		context = this;

		ViewUtil.findView(this);
		setTitle("��������֪ͨ���ʱ��(Сʱ)");

		edt_name.setText((((double) WeatherUtil.noti_show_interval) / 1000 / 3600) + "");
		edt_name.setHint("����֪ͨ���(Сʱ)");
		edt_name.setInputType(EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
		ViewUtil.moveCursor2End(edt_name);
		// ����
		btn_set.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				YxAlertBuilder.create(context, "��ʾ", "֪ͨ���ʱ����Ϊ" + edt_name.getText().toString() + "Сʱ?", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String str = edt_name.getText().toString().trim();
						try {
							double hour = Double.parseDouble(str);
							WeatherUtil.setNotiInterval(hour);
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
