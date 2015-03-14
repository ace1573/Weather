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

public class AboutActivity extends Activity {

	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);
		context = this;

		ViewUtil.findView(this);
		setTitle("¹ØÓÚ");

		
	}
	
	

}
