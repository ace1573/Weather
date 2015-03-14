package com.ace.weather.bean;

import java.io.Serializable;

public class ScheduleItem implements Serializable{
	public String name;
	public String time;
	
	public Integer hour;
	public Integer minute;
	
	public boolean show = false;
	public Long noti_time;
}
