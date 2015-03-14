package com.ace.weather.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.ace.weather.R;
import com.ace.weather.bean.Result;
import com.ace.weather.component.YxAlertBuilder;
import com.ace.weather.listener.OnCallBack;
import com.ace.weather.util.ViewUtil;
import com.ace.weather.util.WeatherUtil;
import com.ace.weather.util.YxUtil;

public class InputCityActivity extends Activity {

	EditText edt_name;
	Button btn_set;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_activity);
		context = this;

		ViewUtil.findView(this);
		setTitle("设置城市");

		edt_name.setText(WeatherUtil.location);
		ViewUtil.moveCursor2End(edt_name);
		// 设置
		btn_set.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setBtnDisable();
				
				final String old_city = WeatherUtil.location;
				String new_city = edt_name.getText().toString().trim();
				if(TextUtils.isEmpty(new_city))
					return;
				WeatherUtil.setLocation(new_city);
				WeatherUtil.requestWeather(new OnCallBack<Result>() {

					@Override
					public void onCallBack(Result t) {
						setBtnEnable();
						switch (t.code) {
						case Result.CODE_OK:
							WeatherUtil.sendUpdateViewBroadcast();
							finish();
							break;
						case Result.CODE_NO_SUCH_CITY:
							WeatherUtil.setLocation(old_city);
							YxUtil.showToast(context, "没有该城市的天气数据");
							break;
						default:
							break;
						}
					}
				});
			}
		});
	}
	
	
	private void setBtnDisable(){
		btn_set.setText("正在请求");
		btn_set.setEnabled(false);
	}
	
	
	private void setBtnEnable(){
		btn_set.setText("设置");
		btn_set.setEnabled(true);
	}

}
