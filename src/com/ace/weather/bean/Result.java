package com.ace.weather.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class Result implements Serializable{
	public String currentCity;
	public String pm25;
	public ArrayList<Weather> weather_data = new ArrayList<Weather>();
	
	public String currentDegree;
	public Weather today;
	public Weather tomorrow;
	
	public Integer code = 200;
	
	
	public static final int CODE_OK = 200;
	public static final int CODE_NO_SUCH_CITY = 1;
	public static final int CODE_NETWORK_ERROR = 2;
	public static final int CODE_PARSE_JSON_ERROR = 3;
}
