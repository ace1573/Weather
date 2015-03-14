package com.ace.weather.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.ace.weather.R;
import com.ace.weather.bean.Contact;
import com.ace.weather.component.BeanAdapter;
import com.ace.weather.component.SlideBar;
import com.ace.weather.util.PinYinUtil;
import com.ace.weather.util.SmsUtil;
import com.ace.weather.util.ViewUtil;
import com.ace.weather.util.YxUtil;

public class PickContactActivity extends Activity {

	Context context;

	public static ArrayList<Contact> mList = new ArrayList<Contact>();

	private ListView listview;

	private TextView tv_message;

	private MyAdapter adapter;

	private SlideBar view_slidebar;

	private Button btn_ok;

	private ArrayList<String> sended_numbers;

	private static boolean init = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pick_contact_activity);
		context = this;

		ViewUtil.findView(this);
		setTitle("选择联系人");

		SmsUtil.init(context);
		sended_numbers = SmsUtil.sended_numbers;

		initSlidePop();

		adapter = new MyAdapter(context, R.layout.pick_contact_item, mList, ViewHolder.class);
		// 点击选中
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Contact contact = mList.get(position);
				contact.check = !contact.check;
				adapter.notifyDataSetChanged();
			}
		});

		btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		if (!init || mList.size() == 0) {
			showMessage();
			btn_ok.setEnabled(false);
			new Thread(new GetContacts()).start();
		} else {
			setAdapter();
			init = true;
		}

	}

	@Override
	public void onBackPressed() {
		if (!init)
			return;
		super.onBackPressed();
	}

	private void initSlidePop() {
		TextView mDialogText = (TextView) LayoutInflater.from(context).inflate(R.layout.pop_slidebar, null);
		mDialogText.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
		getWindowManager().addView(mDialogText, lp);
		view_slidebar.setTextView(mDialogText);
	}

	private void setAdapter() {
		adapter.setSections();
		listview.setAdapter(adapter);
		view_slidebar.setListView(listview);
		view_slidebar.setSectionIndexer(adapter);
	}

	private void showMessage() {
		tv_message.setVisibility(View.VISIBLE);
	}

	private void hideMessage() {
		tv_message.setVisibility(View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("全选").setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (item.getTitle().equals("全选")) {
					item.setTitle("全不选");

					for (Contact contact : mList) {
						contact.check = true;
						adapter.notifyDataSetChanged();
					}
				} else {
					item.setTitle("全选");

					for (Contact contact : mList) {
						contact.check = false;
						adapter.notifyDataSetChanged();
					}
				}
				return true;
			}
		}).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		menu.add("反选").setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {

				for (Contact contact : mList) {
					contact.check = !contact.check;
					adapter.notifyDataSetChanged();
				}
				return true;
			}
		}).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		final String not_show_sended = "不显示发送过的";
		return true;
	}

	class GetContacts implements Runnable {
		@Override
		public void run() {
			try {
				// TODO Auto-generated method stub
				Uri uri = ContactsContract.Contacts.CONTENT_URI;
				String[] projection = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.PHOTO_ID };
				String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '1'";
				String[] selectionArgs = null;
				String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
				Cursor cursor = managedQuery(uri, projection, selection, selectionArgs, sortOrder);
				Cursor phonecur = null;

				while (cursor.moveToNext()) {

					// 取得联系人名字
					int nameFieldColumnIndex = cursor.getColumnIndex(android.provider.ContactsContract.PhoneLookup.DISPLAY_NAME);
					String name = cursor.getString(nameFieldColumnIndex);
					// 取得联系人ID
					String contactId = cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.Contacts._ID));
					phonecur = managedQuery(android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
							+ contactId, null, null);
					// 取得电话号码(可能存在多个号码)
					while (phonecur.moveToNext()) {
						String strPhoneNumber = phonecur.getString(phonecur.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER));
						if (strPhoneNumber.length() > 4) {
							Contact contact = new Contact(name, strPhoneNumber);
							if (sended_numbers.contains(contact.phone))
								contact.sended = true;
							mList.add(contact);
						}
					}
				}
				if (phonecur != null)
					phonecur.close();
				cursor.close();

				// 更新数据
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						setAdapter();
						btn_ok.setEnabled(true);
						hideMessage();
						init = true;
					}
				});
			} catch (Exception e) {
				YxUtil.showToast(context, "权限获取失败");
			}
		}
	}

	class MyAdapter extends BeanAdapter implements SectionIndexer {

		private String[] sections;
		private HashMap<String, Integer> set;

		public MyAdapter(Context context, int layoutId, List list, Class wiewHolder) {
			super(context, layoutId, list, wiewHolder);
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		public void setSections() {
			set = new HashMap<String, Integer>();
			for (int i = getCount() - 1; i >= 0; i--) {
				Contact contact = (Contact) getItem(i);
				if (!TextUtils.isEmpty(contact.name)) {
					String ch = contact.name.substring(0, 1);
					ch = PinYinUtil.converterToFirstSpell(ch).toUpperCase();
					if (!ch.matches("[0-9]"))
						set.put(ch, i);
				}
			}
			Set<String> keySet = set.keySet();
			ArrayList<String> list = new ArrayList<String>(keySet);
			Collections.sort(list);

			sections = new String[list.size()];
			list.toArray(sections);
		}

		@Override
		public void initView(int position, Object item, View view, Object viewHolder) {
			ViewHolder holder = (ViewHolder) viewHolder;
			Contact contact = (Contact) item;

			holder.cb_check.setChecked(contact.check);
			holder.tv_contact_name.setText(contact.name);
			holder.tv_contact_phone.setText(contact.phone);

			if (contact.sended) {
				holder.tv_state.setVisibility(View.VISIBLE);
			} else
				holder.tv_state.setVisibility(View.GONE);
		}

		@Override
		public Object[] getSections() {
			return sections;
		}

		@Override
		public int getPositionForSection(int sectionIndex) {
			return set.get(sections[sectionIndex]);
		}

		@Override
		public int getSectionForPosition(int position) {
			return 1;
		}

	}

	static class ViewHolder {
		public CheckBox cb_check;
		public TextView tv_contact_phone;
		public TextView tv_contact_name;
		public TextView tv_state;
	}

	private boolean not_show_sended = false;

}
