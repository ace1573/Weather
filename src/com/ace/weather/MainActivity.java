package com.ace.weather;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.util.NodeList;

import com.ace.weather.activity.AboutActivity;
import com.ace.weather.activity.InputCityActivity;
import com.ace.weather.activity.InputWeatherIntervalActivity;
import com.ace.weather.activity.ScheduleActivity;
import com.ace.weather.activity.SmsActivity;
import com.ace.weather.bean.Result;
import com.ace.weather.constant.Constant;
import com.ace.weather.listener.OnCallBack;
import com.ace.weather.receiver.MyReceiver;
import com.ace.weather.service.WeatherService;
import com.ace.weather.util.ObjectWriter;
import com.ace.weather.util.WeatherUtil;
import com.ace.weather.util.YxUtil;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private TextView tv_weather;
	private Button btn_get;
	private String[] days = new String[7];
	private Context context;

	private Result result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.INTENT_UPDATE_VIEW);
		registerReceiver(receiver, filter);

		Intent intent = new Intent(this, WeatherService.class);
		startService(intent);

		tv_weather = (TextView) findViewById(R.id.tv_weather);
		btn_get = (Button) findViewById(R.id.btn_get);
		context = this;

		if (WeatherUtil.context == null) {
			WeatherUtil.init(this);
		}

		result = (Result) WeatherUtil.getLocalWeather();
		if (result != null)
			initView(result, false);

		btn_get.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btn_get.setEnabled(false);
				get();
			}

		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			unregisterReceiver(receiver);
		} catch (Exception e) {
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("设置天气城市").setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(context, InputCityActivity.class);
				startActivity(intent);
				return true;
			}
		});

		menu.add("设置天气通知间隔").setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(context, InputWeatherIntervalActivity.class);
				startActivity(intent);
				return true;
			}
		});

		menu.add("待办事项").setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(context, ScheduleActivity.class);
				startActivity(intent);
				return true;
			}
		});
		
		
		menu.add("短信群发器").setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(context, SmsActivity.class);
				startActivity(intent);
				return true;
			}
		});
		
		menu.add("关于").setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(context, AboutActivity.class);
				startActivity(intent);
				return true;
			}
		});
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		Long lastRequetTime = WeatherUtil.getLastRequetTime();
		boolean enable = (System.currentTimeMillis() - lastRequetTime) > 10 * 60 * 1000;
		btn_get.setEnabled(enable);
	}

	private void get() {
		YxUtil.showToast(context, "请求中...");
		WeatherUtil.requestWeather(new OnCallBack<Result>() {

			@Override
			public void onCallBack(Result t) {
				switchResult(t);
			}
		});
	}

	private void initView(final Result result, final boolean show_toast) {
		try {

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					try {
						setTitle(result.currentCity + "天气");
						StringBuilder sb = new StringBuilder();

						// 当前
						String str = WeatherUtil.getCurrWeather(result);
						sb.append(str);
						sb.append("<br>");
						sb.append("<br>");
						String []s = {"1","2","3"};

						// 今天
						str = WeatherUtil.getWeatherStr(result.today);
						sb.append(str);

						// 后几天
						sb.append("<br><br><br><br>");
						str = WeatherUtil.getAllWeather(result, true);
						sb.append(str);

						tv_weather.setText(Html.fromHtml(sb.toString()));

						if (!TextUtils.isEmpty(result.today.tickerText))
							setTitle(Html.fromHtml(result.currentCity + "天气  " + WeatherUtil.getTickerText(result)));
						else
							setTitle(result.currentCity + "天气 ");

						if (show_toast)
							YxUtil.showToast(context, "天气已更新");
						
						
					} catch (Exception e) {
					}
				}
			});
		} catch (Exception e) {
		}
	}

	private void switchResult(Result t) {
		switch (t.code) {
		case Result.CODE_OK:
			initView(t, true);
			break;
		case Result.CODE_NETWORK_ERROR:
			YxUtil.showToast(context, "网络异常");
			btn_get.setEnabled(true);
			break;
		case Result.CODE_NO_SUCH_CITY:
			// YxUtil.showToast(context, "没有该城市的天气数据");
			break;
		default:
			break;
		}
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constant.INTENT_UPDATE_VIEW.equals(action)) {
				Result t = WeatherUtil.getLocalWeather();
				switchResult(t);
			}
		}
	};
}
