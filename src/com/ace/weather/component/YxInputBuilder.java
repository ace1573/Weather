package com.ace.weather.component;

import com.ace.weather.util.ViewUtil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.view.WindowManager;

/**
 * @author ACE
 * 
 */
public class YxInputBuilder {

	private Context context;
	public CharSequence title;
	public CharSequence msg;
	public CharSequence positiveText;
	public CharSequence negativeText;
	public OnPositive onPositive;
	public DialogInterface.OnClickListener onNegative;
	public boolean cancelable = true;

	public YxInputBuilder(Context context) {
		this.context = context;
	}

	public Dialog create() {
		Dialog dialog = ViewUtil.createInputDialog(context, title, msg, null, positiveText, negativeText, onPositive, onNegative);

		dialog.setCancelable(cancelable);
		return dialog;
	}

	/**
	 * 创建可以从service开启的dialog
	 * 
	 * @return
	 */
	public Dialog createServiceDialog() {
		Dialog dialog = create();
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		return dialog;
	}

	/**
	 * 创建对话框
	 * 
	 * @param context
	 * @param title
	 *            标题
	 * @param name
	 *            消息提示
	 * @param onPositive
	 *            点击确定
	 * @return
	 */
	public static Dialog create(Context context, String title, String name, String time, OnPositive onPositive) {
		Dialog dialog = ViewUtil.createInputDialog(context, title, name, time, null, null, onPositive, null);
		return dialog;
	}

	public YxInputBuilder setTitle(CharSequence title) {
		this.title = title;
		return this;
	}

	public YxInputBuilder setMessage(CharSequence message) {
		this.msg = message;
		return this;
	}

	public YxInputBuilder setPositiveButton(CharSequence text, OnPositive listener) {
		this.positiveText = text;
		this.onPositive = listener;
		return this;
	}

	public YxInputBuilder setNegativeButton(CharSequence text, OnClickListener listener) {
		this.negativeText = text;
		this.onNegative = listener;
		return this;
	}

	public YxInputBuilder setCancelable(boolean cancelable) {
		this.cancelable = cancelable;
		return this;
	}

	public void show() {
		create().show();
	}

	public void showServiceDialog() {
		createServiceDialog().show();
	}

	public static boolean isPerssionGranted(Context context) {
		// boolean permission = false;
		// try {
		// PackageManager pm = context.getPackageManager();
		// permission = (PackageManager.PERMISSION_GRANTED ==
		// pm.checkPermission("android.permission.SYSTEM_ALERT_WINDOW",
		// context.getPackageName()));
		// YxUtil.showToast(context, permission+"");
		// } catch (Exception e) {
		// }
		// return permission;

		return (PackageManager.PERMISSION_GRANTED == context.checkCallingPermission("android.permission.SYSTEM_ALERT_WINDOW"));

	}

}
