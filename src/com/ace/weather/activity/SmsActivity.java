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
		setTitle("Ⱥ������");
		
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
			// �л���Ϣ
			case R.id.btn_next_sms:
				curr_sms_index = (curr_sms_index + 1) % sms_list.length;
				edt_sms_content.setText(sms_list[curr_sms_index]);
				ViewUtil.moveCursor2End(edt_sms_content);
				break;
			// ѡ��ϵ��
			case R.id.btn_choose_contacts:
				intent = new Intent(context, PickContactActivity.class);
				startActivity(intent);
				break;
			// ����
			case R.id.btn_send_sms:
				if (phone_numbers.size() == 0) {
					showToast("�㻹ûѡ����ϵ��");
					return;
				}

				YxAlertBuilder.create(context, "��ʾ", "ȷ������" + phone_numbers.size() + "����Ϣ?", new DialogInterface.OnClickListener() {

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
			showToast("���ŷ�����,���Ե�");
			for (String phone_number : phone_numbers) {
				sendSingleSms(phone_number, content);
			}
		} catch (Exception e) {
			showToast("��ȡȨ��ʧ��");
		}
	}

	private void sendSingleSms(String phone, String content) {
		SmsManager smsManager = SmsManager.getDefault();
		List<String> texts = smsManager.divideMessage(content);
		// ����֮ǰ�����������Ƿ�Ϊ��
		for (int i = 0; i < texts.size(); i++) {
			String text = texts.get(i);

			Intent itSend = new Intent(INTENT_SEND_STATE);
			PendingIntent mSendPI = PendingIntent.getBroadcast(getApplicationContext(), i, itSend, 0);// ����requestCode��flag�����ú���Ҫ��Ӱ������KEY_PHONENUM�Ĵ��ݡ�
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

			// �����Ѿ����͵ĺ���
			SmsUtil.saveSendedNumber(phone_numbers);

			if ((success_count + fail_count) == phone_numbers.size()) {
				setCompleteTitle(success_count, fail_count);
				YxAlertBuilder.create(context, "��ʾ", "����ȫ���������, �ɹ�:" + success_count + "��, ʧ��:" + fail_count + "��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
			}
		}
	};

	private void setCompleteTitle(int success_count, int fail_count) {
		setSmsTitle("�����", success_count, fail_count);
	}

	private void setSendTitle(int success_count, int fail_count) {
		setSmsTitle("������", success_count, fail_count);
	}

	private void setSmsTitle(String state, int success_count, int fail_count) {
		StringBuilder sb = new StringBuilder();
		sb.append(state).append("&nbsp;&nbsp;");
		state = WeatherUtil.getFontHtml("00BCD4", "�ɹ�:" + success_count + "��");
		sb.append(state);
		state = WeatherUtil.getFontHtml("FF313B", "ʧ��:" + fail_count + "��");
		sb.append(state);
		setTitle(Html.fromHtml(sb.toString()));
	}

}
