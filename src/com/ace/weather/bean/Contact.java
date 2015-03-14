package com.ace.weather.bean;

public class Contact {
	public Contact(String name, String phone) {
		this.name = name;
		this.phone = phone;
	}

	public String name;
	public String phone;
	public boolean check = false;
	
	public boolean sended = false;
}
