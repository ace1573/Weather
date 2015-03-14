package com.ace.weather.util;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;

import com.ace.weather.bean.Result;
import com.ace.weather.bean.Weather;
import com.ace.weather.constant.Constant;
import com.ace.weather.listener.OnCallBack;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HttpUtil {

	private static AsyncHttpClient client = new AsyncHttpClient();
	private static final String ak = "xC2CfDM87cxAjuPuL5sMUyj6";
	private static final String url = "http://api.map.baidu.com/telematics/v3/weather";

	public static void post(final Context context, String location, final OnCallBack<Result> onCallBack) {
		RequestParams params = new RequestParams();
		params.put("location", location);
		params.put("output", "json");
		params.put("ak", ak);
		client.get(context, url, params, new JsonHttpResponseHandler("utf-8") {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				Result bean = new Result();
				try {
					String status = response.getString("status");
					if ("success".equals(status)) {
						JSONArray results = response.getJSONArray("results");
						JSONObject result = results.getJSONObject(0);
						bean = ConvertUtil.json2Bean(result, Result.class);
						try {
							Weather w = bean.weather_data.get(0);
							bean.today = w;
							bean.tomorrow = bean.weather_data.get(1);
							bean.tomorrow.date = "明天";

							try {
								bean.currentDegree = w.date.substring(w.date.indexOf("(") + 4, w.date.lastIndexOf(")"));
							} catch (Exception e) {
								bean.currentDegree = "无";
							}
							try {
								if (w.date.startsWith("周"))
									w.date = "今天";
								else
									w.date = w.date.substring(0, w.date.indexOf(" "));
							} catch (Exception e) {
							}
							WeatherUtil.setTickerText(bean);

						} catch (Exception e) {
						}
						onCallBack.onCallBack(bean);
					} else {
						bean.code = Result.CODE_NO_SUCH_CITY;
						onCallBack.onCallBack(bean);
					}
				} catch (Exception e) {
					bean.code = Result.CODE_PARSE_JSON_ERROR;
					onCallBack.onCallBack(bean);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
				Result bean = new Result();
				bean.code = Result.CODE_NETWORK_ERROR;
				onCallBack.onCallBack(bean);
			}

		});
	}
}
