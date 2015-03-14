package com.ace.weather.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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
import org.htmlparser.lexer.Lexer;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Environment;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

import com.ace.weather.bean.Result;
import com.ace.weather.bean.Weather;
import com.ace.weather.constant.Constant;
import com.ace.weather.listener.OnCallBack;

public class WeatherUtil {
	public static final String FILE_RESULT = "FILE_RESULT";
	public static Long lastRequestTime;

	public static final String url = "http://m.weather.com.cn/mweather/101280101.shtml";

	private static Result result;

	private static long requestInterval = 3600 * 1000;

	public static Context context;

	public static String location = "广州";
	public static Long noti_show_interval = (long) (4 * 3600 * 1000);

	private static int[] pm25Int = new int[] { 50, 100, 150, 200, 300, Integer.MAX_VALUE };
	private static String[] pm25Str = new String[] { "优", "良", "轻度污染", "中度污染", "重度污染", "严重污染" };

	public static void init(Context ctx) {
		context = ctx;
		location = (String) SPUtil.get(context, "location", "广州");
		noti_show_interval = (Long) SPUtil.get(context, "noti_show_interval", noti_show_interval);
	}

	public static void setNotiInterval(double hour) {
		WeatherUtil.noti_show_interval = (long) (hour * 3600 * 1000);
		SPUtil.put(context, "noti_show_interval", noti_show_interval);
	}

	public static void setLocation(String location) {
		WeatherUtil.location = location;
		SPUtil.put(context, "location", location);
	}

	public static void setResult(Result bean) {
		result = bean;
		ObjectWriter.write(context, result, FILE_RESULT);
	}

	public static Result getLocalWeather() {
		if (result == null)
			result = (Result) ObjectWriter.read(context, FILE_RESULT);
		return result;
	}

	public static void sendUpdateViewBroadcast() {
		Intent intent = new Intent();
		intent.setAction(Constant.INTENT_UPDATE_VIEW);
		context.sendBroadcast(intent);
	}

	public static Long getLastRequetTime() {
		if (lastRequestTime == null)
			lastRequestTime = (Long) SPUtil.get(context, "lastRequestTime", 0l);
		return lastRequestTime;
	}

	public static void setLastRequestTime(Long time) {
		lastRequestTime = System.currentTimeMillis();
		SPUtil.put(context, "lastRequestTime", lastRequestTime);
	}

	public static void getWeather(OnCallBack<Result> onCallBack) {
		getWeather(requestInterval, onCallBack);
	}

	public static void getWeather(Long interval, final OnCallBack<Result> onCallBack) {
		// 返回本地
		if ((System.currentTimeMillis() - getLastRequetTime()) < interval) {
			onCallBack.onCallBack(getLocalWeather());
		} else {
			requestWeather(new OnCallBack<Result>() {

				@Override
				public void onCallBack(Result t) {
					onCallBack.onCallBack(t);
				}
			});
		}
	}

	public static void requestWeather(final OnCallBack<Result> onCallBack) {

		HttpUtil.post(context, location, new OnCallBack<Result>() {

			@Override
			public void onCallBack(Result bean) {
				switch (bean.code) {
				case Result.CODE_OK:
					setResult(bean);
					setLastRequestTime(System.currentTimeMillis());
					break;
				}
				onCallBack.onCallBack(bean);
			}
		});

	}

	public static String getTodayAndTomorrowWeather(Result result) {
		String str0 = getCurrWeather(result);
		String str1 = getWeatherStr(result.weather_data.get(1));
		return str0 + "<br>" + str1;
	}

	public static String getCurrWeather(Result result) {

		StringBuilder sb = new StringBuilder();
		sb.append("<font color='#999999'>" + "当前" + "</font> ");
		if (!TextUtils.isEmpty(result.currentDegree))
			sb.append(getFontHtml("00BCD4", result.currentDegree));

		try {
			int pm25 = Integer.parseInt(result.pm25);
			int i = 0;
			for (; i < pm25Int.length; i++) {
				if (pm25 < pm25Int[i])
					break;
			}
			sb.append(getFontHtml("F68F6C", pm25Str[i]));
		} catch (Exception e) {
		}
		return sb.toString();
	}

	public static String getCurrNotiWeather(Result result) {
		StringBuilder sb = new StringBuilder();
		sb.append("<font color='#999999'>" + "当前" + "</font> ");
		if (!TextUtils.isEmpty(result.currentDegree))
			sb.append(getFontHtml("00BCD4", result.currentDegree));
		if (!TextUtils.isEmpty(result.today.tickerText))
			sb.append(getFontHtml("48CFAE", result.today.tickerText));
		return sb.toString();
	}

	public static String getWeatherStr(Weather weather) {
		StringBuilder sb = new StringBuilder();
		sb.append("<font color='#999999'>" + weather.date + "</font> ");
		sb.append(getFontHtml("00BCD4", weather.temperature));
		sb.append(getFontHtml("f08080", weather.weather + ", " + weather.wind));

		return sb.toString();
	}

	public static String getAllWeather(Result result, boolean not_doday) {
		StringBuilder sb = new StringBuilder();
		for (Weather w : result.weather_data) {
			if (not_doday) {
				not_doday = false;
				continue;
			}
			sb.append(getWeatherStr(w)).append("<br><br>");
		}
		return sb.toString();
	}

	public static String getFontHtml(String font, String str) {
		return "<font color='#" + font + "'>" + "[" + str + "]</font>" + " ";
	}

	public static String getTickerText(Result result) {
		return "<font color='#F68F6C'>" + "(" + result.today.tickerText + ")</font>" + " ";
	}

	public static Degree getMaxAndMinDegree(Weather w) {
		String temp = w.temperature;
		Integer max = null;
		Integer min = null;
		try {
			String str = temp.substring(0, temp.indexOf(" "));
			max = Integer.parseInt(str);

			str = temp.substring(temp.lastIndexOf(" ") + 1, temp.lastIndexOf("℃"));
			min = Integer.parseInt(str);

			if (max < min) {
				int t = max;
				max = min;
				min = t;
			}
			return new Degree(max, min);
		} catch (Exception e) {
		}
		return new Degree(null, null);
	}

	static class Degree {
		public Degree(Integer max, Integer min) {
			this.max = max;
			this.min = min;
		}

		public Integer max;
		public Integer min;
	}

	private static String getNotiTickerText(Weather w0, Weather w1) {
		Degree d0 = getMaxAndMinDegree(w0);
		Degree d1 = getMaxAndMinDegree(w1);

		StringBuilder sb = new StringBuilder();
		if (w1.weather.contains("雨")) {
			return "明天下雨";
		}

		if (d0.max == null || d0.min == null || d1.max == null || d1.min == null || d0.max == d0.min || d1.max == d1.min) {
			return null;
		}

		int sub = d0.min - d1.min;
		if ((sub) > 2) {
			return "明天降温(" + sub + "℃)";
		}

		sub = d1.max - d0.max;
		if ((sub) > 2) {
			return "明天升温(" + sub + "℃)";
		}

		return null;
	}

	public static String setTickerText(Result result) {
		try {
			Weather w0 = result.today;
			Weather w1 = result.tomorrow;

			String tickerText = getNotiTickerText(w0, w1);
			w0.tickerText = tickerText;
			return tickerText;
		} catch (Exception e) {
			return null;
		}
	}

}
