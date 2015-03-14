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
public class YxAlertBuilder {

	private Context context;
	public CharSequence title;
	public CharSequence msg;
	public CharSequence positiveText;
	public CharSequence negativeText;
	public DialogInterface.OnClickListener onPositive;
	public DialogInterface.OnClickListener onNegative;
	public boolean cancelable = true;

	public YxAlertBuilder(Context context) {
		this.context = context;
	}

	public Dialog create() {
		Dialog dialog = ViewUtil.createAlertDialog(context, title, msg, positiveText, negativeText, onPositive, onNegative);
		dialog.setCancelable(cancelable);
		return dialog;
	}
	
	/**
	 * 创建可以从service开启的dialog
	 * @return
	 */
	public Dialog createServiceDialog(){
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
	 * @param msg
	 *            消息提示
	 * @param onPositive
	 *            点击确定
	 * @return
	 */
	public static Dialog create(Context context, String title, String msg, DialogInterface.OnClickListener onPositive) {
		Dialog dialog = ViewUtil.createAlertDialog(context, title, msg, null, null, onPositive, null);
		return dialog;
	}

	public YxAlertBuilder setTitle(CharSequence title) {
		this.title = title;
		return this;
	}

	public YxAlertBuilder setMessage(CharSequence message) {
		this.msg = message;
		return this;
	}

	public YxAlertBuilder setPositiveButton(CharSequence text, OnClickListener listener) {
		this.positiveText = text;
		this.onPositive = listener;
		return this;
	}

	public YxAlertBuilder setNegativeButton(CharSequence text, OnClickListener listener) {
		this.negativeText = text;
		this.onNegative = listener;
		return this;
	}

	public YxAlertBuilder setCancelable(boolean cancelable) {
		this.cancelable = cancelable;
		return this;
	}

	public void show() {
		create().show();
	}
	
	public void showServiceDialog(){
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
