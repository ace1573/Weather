package com.ace.weather.util;

import java.util.ArrayList;

import android.content.Context;

public class SmsUtil {

	public static ArrayList<String> sended_numbers = new ArrayList<String>();

	private static final String FILE_SENDED_NUMBERS = "KEY_SENDED_NUMBERS";

	private static Context context;

	public static void init(Context ctx) {
		context = ctx;

		if (sended_numbers.size() == 0) {
			new Thread(){
				public void run() {
					Object obj = ObjectWriter.read(context, FILE_SENDED_NUMBERS);
					try {
						if (obj != null)
							sended_numbers = (ArrayList<String>) obj;
					} catch (Exception e) {
					}
				}
			}.start();
		}
	}

	public static void saveSendedNumber(final ArrayList<String> phone_numbers) {
		new Thread() {
			public void run() {
				for (String phone_number : phone_numbers) {
					if (!sended_numbers.contains(phone_number))
						sended_numbers.add(phone_number);
				}
				ObjectWriter.write(context, sended_numbers, FILE_SENDED_NUMBERS);
			}
		}.start();
	}
}
