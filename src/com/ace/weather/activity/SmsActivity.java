package com.ace.weather.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ace.weather.R;
import com.ace.weather.bean.Contact;
import com.ace.weather.bean.ScheduleItem;
import com.ace.weather.component.YxAlertBuilder;
import com.ace.weather.util.SmsUtil;
import com.ace.weather.util.ViewUtil;
import com.ace.weather.util.WeatherUtil;
import com.ace.weather.util.YxUtil;

public class SmsActivity extends Activity {

	Context context;

	private EditText edt_sms_content;
	private Button btn_next_sms, btn_choose_contacts, btn_send_sms;
	private TextView tv_contacts;

	private ArrayList<Contact> contacts;

	private ArrayList<String> phone_numbers = new ArrayList<String>();

	private String[] sms_list;
	private int curr_sms_index = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms_activity);
		context = this;

		ViewUtil.findView(this);
		setTitle("群发短信");
		
		SmsUtil.init(context);
		contacts = getContacts();

		sms_list = getResources().getStringArray(R.array.sms_list);
		if (sms_list == null)
			return;

		edt_sms_content.setText(sms_list[curr_sms_index]);
		ViewUtil.moveCursor2End(edt_sms_content);

		btn_next_sms.setOnClickListener(onClick);
		btn_choose_contacts.setOnClickListener(onClick);
		btn_send_sms.setOnClickListener(onClick);

		IntentFilter filter = new IntentFilter();
		filter.addAction(INTENT_SEND_STATE);
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		phone_numbers = new ArrayList<String>();

		StringBuilder sb = new StringBuilder();
		for (Contact contact : contacts) {
			if (contact.check) {
				String phontStr = WeatherUtil.getFontHtml("f08080", contact.phone);
				sb.append(contact.name).append(phontStr).append(",&nbsp;&nbsp;");
				phone_numbers.add(contact.phone);
			}
		}
		if (sb.lastIndexOf(",") > 0) {
			String str = sb.substring(0, sb.lastIndexOf(","));
			tv_contacts.setText(Html.fromHtml(str));
		} else
			tv_contacts.setText("");

	}

	private OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent;
			switch (v.getId()) {
			// 切换信息
			case R.id.btn_next_sms:
				curr_sms_index = (curr_sms_index + 1) % sms_list.length;
				edt_sms_content.setText(sms_list[curr_sms_index]);
				ViewUtil.moveCursor2End(edt_sms_content);
				break;
			// 选联系人
			case R.id.btn_choose_contacts:
				intent = new Intent(context, PickContactActivity.class);
				startActivity(intent);
				break;
			// 发送
			case R.id.btn_send_sms:
				if (phone_numbers.size() == 0) {
					showToast("你还没选择联系人");
					return;
				}

				YxAlertBuilder.create(context, "提示", "确定发送" + phone_numbers.size() + "条信息?", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						new Thread() {
							public void run() {

								success_count = 0;
								fail_count = 0;
								sendSms(phone_numbers, edt_sms_content.getText().toString());
							}
						}.start();
					}
				}).show();
				break;
			default:
				break;
			}
		}
	};

	protected void onDestroy() {
		super.onDestroy();
		try {
			unregisterReceiver(receiver);
		} catch (Exception e) {
		}
	}

	private void showToast(final String message) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				YxUtil.showToast(context, message);
			}
		});
	}

	private void sendSms(ArrayList<String> phone_numbers, String content) {
		try {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					setSendTitle(success_count, fail_count);
				}
			});
			showToast("短信发送中,请稍等");
			for (String phone_number : phone_numbers) {
				sendSingleSms(phone_number, content);
			}
		} catch (Exception e) {
			showToast("获取权限失败");
		}
	}

	private void sendSingleSms(String phone, String content) {
		SmsManager smsManager = SmsManager.getDefault();
		List<String> texts = smsManager.divideMessage(content);
		// 发送之前检查短信内容是否为空
		for (int i = 0; i < texts.size(); i++) {
			String text = texts.get(i);

			Intent itSend = new Intent(INTENT_SEND_STATE);
			PendingIntent mSendPI = PendingIntent.getBroadcast(getApplicationContext(), i, itSend, 0);// 这里requestCode和flag的设置很重要，影响数据KEY_PHONENUM的传递。
			smsManager.sendTextMessage(phone, null, content, mSendPI, null);
		}
	}

	private ArrayList<Contact> getContacts() {
		return PickContactActivity.mList;
	}

	private static final String INTENT_SEND_STATE = "INTENT_SEND_STATE";
	private int success_count = 0;
	private int fail_count = 0;
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (getResultCode() == RESULT_OK)
				success_count++;
			else
				fail_count++;

			setSendTitle(success_count, fail_count);

			// 保存已经发送的号码
			SmsUtil.saveSendedNumber(phone_numbers);

			if ((success_count + fail_count) == phone_numbers.size()) {
				setCompleteTitle(success_count, fail_count);
				YxAlertBuilder.create(context, "提示", "短信全部发送完毕, 成功:" + success_count + "条, 失败:" + fail_count + "条", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
			}
		}
	};

	private void setCompleteTitle(int success_count, int fail_count) {
		setSmsTitle("已完成", success_count, fail_count);
	}

	private void setSendTitle(int success_count, int fail_count) {
		setSmsTitle("发送中", success_count, fail_count);
	}

	private void setSmsTitle(String state, int success_count, int fail_count) {
		StringBuilder sb = new StringBuilder();
		sb.append(state).append("&nbsp;&nbsp;");
		state = WeatherUtil.getFontHtml("00BCD4", "成功:" + success_count + "个");
		sb.append(state);
		state = WeatherUtil.getFontHtml("FF313B", "失败:" + fail_count + "个");
		sb.append(state);
		setTitle(Html.fromHtml(sb.toString()));
	}

}
