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
 * �����˶�view��һЩ����
 * 
 */
public class ViewUtil {

	public static void setHeightInPx(View view, int height) {
		LayoutParams params = view.getLayoutParams();
		params.height = height;
		view.setLayoutParams(params);
	}

	/**
	 * �õ��Զ����AlertDialog
	 * 
	 * @param context
	 * @param msg
	 * @return
	 */
	public static Dialog createAlertDialog(Context context, CharSequence title, CharSequence msg, CharSequence positionText, CharSequence negativeText,
			final DialogInterface.OnClickListener onPositive, final DialogInterface.OnClickListener onNegative) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.n_alert_dialog, null);// �õ�����view
		final Dialog dialog = new Dialog(context, R.style.alert_dialog);// �����Զ�����ʽdialog

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

		dialog.setCancelable(true);// �����á����ؼ���ȡ��
		dialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));// ���ò���
		return dialog;

	}

	/**
	 * �õ��Զ����AlertDialog
	 * 
	 * @param context
	 * @param name
	 * @return
	 */
	public static Dialog createInputDialog(Context context, CharSequence title, CharSequence name, CharSequence time, CharSequence positionText, CharSequence negativeText,
			final OnPositive onPositive, final DialogInterface.OnClickListener onNegative) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.input_dialog, null);// �õ�����view
		final Dialog dialog = new Dialog(context, R.style.alert_dialog);// �����Զ�����ʽdialog

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

		dialog.setCancelable(true);// �����á����ؼ���ȡ��
		dialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));// ���ò���
		return dialog;

	}

	/**
	 * �ƶ���굽ĩβ
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
	 * ����������ֹ���
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

					// �������0
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
	 * ��ȡmeasure��height
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
	 * ����text
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
	 * �����������ñ�����ͼ��Ŀ��
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
	 * ���������������ʱ����
	 * 
	 * @param edt_search
	 *            ���������
	 * @param listener
	 *            ��ʱ�¼�
	 * @param btn_clear
	 *            �����ť
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
					// ���������ʱ��
					latestTime = System.currentTimeMillis();

					// ���Ͳ�ѯ����
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							long currTime = System.currentTimeMillis();
							// ����������ӳ�ʱ�仹û��������������
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
	 * �������Ʋ���view
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
	 * ����class����view
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
				// �ж������Ƿ�һ��
				if (view.getClass().getName().equals(targetView.getName()))
					views.add((T) view);

				// ��������� �ݹ�
				if (view instanceof ViewGroup) {
					findViewsByClass((ViewGroup) view, views, targetView);
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * �Զ�ע�� ����activity�г�Ա����������ע��view
	 * 
	 * @param activity
	 */
	public static void findView(Activity activity) {
		try {
			Class clazz = activity.getClass();
			Field[] fields = clazz.getDeclaredFields();
			String fieldName = null;
			// ���������ֶ�
			for (Field field : fields) {
				fieldName = field.getName();
				// ����_
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
	 * �Զ�ע�� ����viewHolder�г�Ա����������ע��view����ͨ��setTag()���ø�view
	 * 
	 * @param viewGroup
	 */
	public static void setViewHolder(View view, Object viewHolder) {
		try {
			ViewGroup viewGroup = (ViewGroup) view;
			Class clazz = viewHolder.getClass();
			Field[] fields = clazz.getDeclaredFields();
			String fieldName = null;
			// ���������ֶ�
			for (Field field : fields) {
				fieldName = field.getName();
				// ����_
				try {
					Field idField = R.id.class.getField(fieldName);
					int id = (Integer) idField.get(null);
					view = viewGroup.findViewById(id);
					field.setAccessible(true);
					// ���ø�viewHolder
					field.set(viewHolder, view);
				} catch (Exception e) {
				}
			}
			// viewHolder���ø�viewGroup
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
		// ���봰�������ļ�
		ViewGroup group = (ViewGroup) inflater.inflate(layoutRes, null);
		// ����PopupWindow����
		PopupWindow pop = new PopupWindow(group, width, height, false);
		// ��Ҫ����һ�´˲����������߿���ʧ
		pop.setBackgroundDrawable(new BitmapDrawable());
		// ���õ��������ߴ�����ʧ
		pop.setOutsideTouchable(true);
		// ���ô˲�����ý��㣬�����޷����
		pop.setFocusable(true);
		return pop;
	}

	/**
	 * �����ֻ��ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����)
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