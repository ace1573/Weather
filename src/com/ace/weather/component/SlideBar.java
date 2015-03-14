package com.ace.weather.component;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class SlideBar extends View {
	private String[] l;
	private SectionIndexer sectionIndexter = null;
	private ListView list;
	private TextView mDialogText;
	private int m_nItemHeight = 25;

	public SlideBar(Context context) {
		super(context);
		init();
	}

	public SlideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		if (sectionIndexter == null)
			l = new String[] { "" };
		else
			l = (String[]) sectionIndexter.getSections();
		invalidate();
	}

	public SlideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void setSectionIndexer(SectionIndexer adapter) {
		this.sectionIndexter = adapter;
		init();
	}

	public void setListView(ListView _list) {
		try {
			list = _list;
			// ListAdapter adpter = _list.getAdapter();
			// sectionIndexter = (SectionIndexer) _list.getAdapter();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setTextView(TextView mDialogText) {
		this.mDialogText = mDialogText;
	}

	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		try {

			if (sectionIndexter == null)
				return true;
			int i = (int) event.getY();
			int idx = i / m_nItemHeight;
			if (idx >= l.length) {
				idx = l.length - 1;
			} else if (idx < 0) {
				idx = 0;
			}
			if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
				mDialogText.setVisibility(View.VISIBLE);
				mDialogText.setText("" + l[idx]);
				// if (sectionIndexter == null) {
				// sectionIndexter = (SectionIndexer) list.getAdapter();
				// }
				int position = sectionIndexter.getPositionForSection(idx);
				if (position == -1) {
					return true;
				}
				list.setSelection(position);
			} else {
				mDialogText.setVisibility(View.INVISIBLE);
			}
		} catch (Exception e) {
			Log.d("", e.getMessage());
		}
		return true;
	}

	protected void onDraw(Canvas canvas) {
		Rect rect = new Rect();
		this.getGlobalVisibleRect(rect);
		m_nItemHeight = rect.height() / l.length;
		Paint paint = new Paint();
		paint.setColor(0xff595c61);

		DisplayMetrics dm = new DisplayMetrics();
		((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
		float density = dm.density;
		paint.setTextSize(14 * density);

		paint.setTextAlign(Paint.Align.CENTER);
		float widthCenter = getMeasuredWidth() / 2;
		for (int i = 0; i < l.length; i++) {
			canvas.drawText(String.valueOf(l[i]), widthCenter, m_nItemHeight + (i * m_nItemHeight), paint);
		}
		super.onDraw(canvas);
	}

}
