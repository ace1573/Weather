package com.ace.weather.util;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.ace.weather.R;
import com.ace.weather.component.OnPositive;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 
 * 集合了对view的一些操作
 * 
 */
public class ViewUtil {

	public static void setHeightInPx(View view, int height) {
		LayoutParams params = view.getLayoutParams();
		params.height = height;
		view.setLayoutParams(params);
	}

	/**
	 * 得到自定义的AlertDialog
	 * 
	 * @param context
	 * @param msg
	 * @return
	 */
	public static Dialog createAlertDialog(Context context, CharSequence title, CharSequence msg, CharSequence positionText, CharSequence negativeText,
			final DialogInterface.OnClickListener onPositive, final DialogInterface.OnClickListener onNegative) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.n_alert_dialog, null);// 得到加载view
		final Dialog dialog = new Dialog(context, R.style.alert_dialog);// 创建自定义样式dialog

		Window win = dialog.getWindow();
		win.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams lp = win.getAttributes();
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		win.setAttributes(lp);

		TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
		TextView tv_msg = (TextView) view.findViewById(R.id.tv_msg);
		Button btn_position = (Button) view.findViewById(R.id.btn_position);
		Button btn_negative = (Button) view.findViewById(R.id.btn_negative);

		if (title != null)
			tv_title.setText(title);
		if (msg != null)
			tv_msg.setText(msg);
		if (positionText != null)
			btn_position.setText(positionText);
		if (negativeText != null)
			btn_negative.setText(negativeText);

		final DialogInterface.OnClickListener defaultOnClick = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};

		if (onPositive != null) {
			btn_position.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onPositive.onClick(dialog, 0);
					dialog.dismiss();
				}
			});
		} else
			btn_position.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

		if (onNegative != null) {
			btn_negative.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onNegative.onClick(dialog, 0);
					dialog.dismiss();
				}
			});
		} else
			btn_negative.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

		dialog.setCancelable(true);// 可以用“返回键”取消
		dialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
		return dialog;

	}

	/**
	 * 得到自定义的AlertDialog
	 * 
	 * @param context
	 * @param name
	 * @return
	 */
	public static Dialog createInputDialog(Context context, CharSequence title, CharSequence name, CharSequence time, CharSequence positionText, CharSequence negativeText,
			final OnPositive onPositive, final DialogInterface.OnClickListener onNegative) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.input_dialog, null);// 得到加载view
		final Dialog dialog = new Dialog(context, R.style.alert_dialog);// 创建自定义样式dialog

		Window win = dialog.getWindow();
		win.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams lp = win.getAttributes();
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		win.setAttributes(lp);

		TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
		final EditText edt_name = (EditText) view.findViewById(R.id.edt_name);
		final EditText edt_time = (EditText) view.findViewById(R.id.edt_time);
		Button btn_position = (Button) view.findViewById(R.id.btn_position);
		Button btn_negative = (Button) view.findViewById(R.id.btn_negative);

		if (title != null)
			tv_title.setText(title);
		if (name != null)
			edt_name.setText(name);
		if (time != null)
			edt_time.setText(time);
		if (positionText != null)
			btn_position.setText(positionText);
		if (negativeText != null)
			btn_negative.setText(negativeText);

		final DialogInterface.OnClickListener defaultOnClick = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};

		if (onPositive != null) {
			btn_position.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onPositive.onClick(dialog, edt_name, edt_time);
				}
			});
		} else
			btn_position.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

		if (onNegative != null) {
			btn_negative.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onNegative.onClick(dialog, 0);
					dialog.dismiss();
				}
			});
		} else
			btn_negative.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

		dialog.setCancelable(true);// 可以用“返回键”取消
		dialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
		return dialog;

	}

	/**
	 * 移动光标到末尾
	 * 
	 * @param edt_content
	 */
	public static void moveCursor2End(EditText edt_content) {
		try {
			CharSequence text = edt_content.getText();
			if (text instanceof Spannable) {
				Spannable spanText = (Spannable) text;
				Selection.setSelection(spanText, text.length());
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 输入符合数字规则
	 * 
	 * @param edt_content
	 */
	public static void setEdtNoZeroPrefix(final EditText edt_content) {
		edt_content.addTextChangedListener(new TextWatcher() {
			private boolean mask = false;

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				try {
					if (mask) {
						mask = false;
						return;
					}

					String content = edt_content.getText().toString();

					// 输入的是0
					if (content.matches("^0+$")) {
						mask = true;
						edt_content.setText("0");
						moveCursor2End(edt_content);
						return;
					}

					StringBuilder sb = new StringBuilder(content);
					while (sb.length() > 0 && sb.charAt(0) == '0') {
						sb.delete(0, 1);
					}
					mask = true;
					edt_content.setText(sb.toString());
					moveCursor2End(edt_content);
				} catch (Exception e) {
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	/**
	 * 获取measure的height
	 * 
	 * @param view
	 * @return
	 */
	public static int getMeasureHeight(View view) {
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		view.measure(w, h);
		return view.getMeasuredHeight();
	}

	/**
	 * 设置text
	 * 
	 * @param tv
	 * @param text
	 * @param defaultText
	 */
	public static void setText(TextView tv, String text, String defaultText) {
		if (TextUtils.isEmpty(text))
			text = defaultText;
		tv.setText(text);
	}

	/**
	 * 根据字数设置标题栏图标的宽度
	 * 
	 * @param btn_more
	 */
	public static void setBtnWidth(Button btn_more) {
		try {
			double len = ((double) btn_more.getText().toString().length()) / 2;
			LayoutParams params = btn_more.getLayoutParams();
			int width = (int) (params.width * len);
			params.width = width;
			btn_more.setLayoutParams(params);

			ViewGroup parent = (ViewGroup) btn_more.getParent();
			params = parent.getLayoutParams();
			params.width = width;
			parent.setLayoutParams(params);
		} catch (Exception e) {
		}
	}

	/**
	 * 设置搜索输入框延时搜索
	 * 
	 * @param edt_search
	 *            搜索输入框
	 * @param listener
	 *            延时事件
	 * @param btn_clear
	 *            清除按钮
	 */
	public static void setSearchBarDelay(final EditText edt_search, final OnClickListener listener, View btn_clear) {
		try {

			if (edt_search == null)
				return;
			edt_search.addTextChangedListener(new TextWatcher() {
				private long latestTime = 0;
				private Handler handler = new Handler();
				private int delayMills = 1000;

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// 最新输入的时间
					latestTime = System.currentTimeMillis();

					// 发送查询请求
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							long currTime = System.currentTimeMillis();
							// 如果经过该延迟时间还没有新输入则搜索
							if ((currTime - latestTime) < (delayMills - 10))
								return;
							else {
								listener.onClick(null);
							}
						}
					}, delayMills);
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {

				}

				@Override
				public void afterTextChanged(Editable s) {

				}
			});

			if (btn_clear == null)
				return;
			btn_clear.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					edt_search.setText("");
				}
			});
		} catch (Exception e) {
		}
	}

	/**
	 * 根据名称查找view
	 * 
	 * @param root
	 * @param targetView
	 * @return
	 */
	public static <T> ArrayList<T> findViewsByName(ViewGroup root, Class<T> targetView) {
		ArrayList<T> views = new ArrayList<T>();
		findViewsByClass(root, views, targetView);
		return views;
	}

	/**
	 * 根据class找子view
	 * 
	 * @param root
	 * @param views
	 * @param targetView
	 */
	private static <T> void findViewsByClass(ViewGroup root, ArrayList<T> views, Class<T> targetView) {
		try {

			if (root == null)
				return;
			int count = root.getChildCount();
			View view;
			for (int i = 0; i < count; i++) {
				view = root.getChildAt(i);
				// 判断名称是否一致
				if (view.getClass().getName().equals(targetView.getName()))
					views.add((T) view);

				// 如果是容器 递归
				if (view instanceof ViewGroup) {
					findViewsByClass((ViewGroup) view, views, targetView);
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 自动注入 根据activity中成员变量的名称注入view
	 * 
	 * @param activity
	 */
	public static void findView(Activity activity) {
		try {
			Class clazz = activity.getClass();
			Field[] fields = clazz.getDeclaredFields();
			String fieldName = null;
			// 遍历所有字段
			for (Field field : fields) {
				fieldName = field.getName();
				// 包含_
				try {
					Field idField = R.id.class.getField(fieldName);
					int id = (Integer) idField.get(null);
					View view = activity.findViewById(id);
					field.setAccessible(true);
					field.set(activity, view);
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 自动注入 根据viewHolder中成员变量的名称注入view，并通过setTag()设置给view
	 * 
	 * @param viewGroup
	 */
	public static void setViewHolder(View view, Object viewHolder) {
		try {
			ViewGroup viewGroup = (ViewGroup) view;
			Class clazz = viewHolder.getClass();
			Field[] fields = clazz.getDeclaredFields();
			String fieldName = null;
			// 遍历所有字段
			for (Field field : fields) {
				fieldName = field.getName();
				// 包含_
				try {
					Field idField = R.id.class.getField(fieldName);
					int id = (Integer) idField.get(null);
					view = viewGroup.findViewById(id);
					field.setAccessible(true);
					// 设置给viewHolder
					field.set(viewHolder, view);
				} catch (Exception e) {
				}
			}
			// viewHolder设置给viewGroup
			viewGroup.setTag(viewHolder);
		} catch (Exception e) {
		}
	}

	public static void setOnClick(ArrayList views, OnClickListener listener) {
		if (views == null)
			return;
		View view;
		for (Object obj : views) {
			if (obj instanceof View) {
				view = (View) obj;
				view.setOnClickListener(listener);
			}
		}
	}

	public static void setOnCheckChange(ArrayList<? extends CheckBox> views, OnCheckedChangeListener listener) {
		if (views == null)
			return;
		CheckBox view;
		for (Object obj : views) {
			if (obj instanceof CheckBox) {
				view = (CheckBox) obj;
				view.setOnCheckedChangeListener(listener);
			}
		}
	}

	public static PopupWindow cretePopup(Context context, int layoutRes, int width, int height) {
		LayoutInflater inflater = LayoutInflater.from(context);
		// 引入窗口配置文件
		ViewGroup group = (ViewGroup) inflater.inflate(layoutRes, null);
		// 创建PopupWindow对象
		PopupWindow pop = new PopupWindow(group, width, height, false);
		// 需要设置一下此参数，点击外边可消失
		pop.setBackgroundDrawable(new BitmapDrawable());
		// 设置点击窗口外边窗口消失
		pop.setOutsideTouchable(true);
		// 设置此参数获得焦点，否则无法点击
		pop.setFocusable(true);
		return pop;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static Bitmap getBitmapFromView(View view) {
		Bitmap bitmap = null;
		try {
			int width = view.getWidth();
			int height = view.getHeight();
			if (width != 0 && height != 0) {
				bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(bitmap);
				view.layout(0, 0, width, height);
				view.draw(canvas);
			}
		} catch (Exception e) {
			bitmap = null;
		}
		return bitmap;
	}

}