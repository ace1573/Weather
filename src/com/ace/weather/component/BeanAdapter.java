package com.ace.weather.component;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.ace.weather.util.ViewUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class BeanAdapter extends BaseAdapter {

	List mList;
	Context context;
	int layoutId;
	Class wiewHolder;

	public BeanAdapter(Context context, int layoutId, List list, Class wiewHolder) {
		this.mList = list;
		this.context = context;
		this.layoutId = layoutId;
		this.wiewHolder = wiewHolder;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null) {
			view = LayoutInflater.from(context).inflate(layoutId, parent, false);

			try {
				Constructor constructor = wiewHolder.getDeclaredConstructor();
				constructor.setAccessible(true);
				Object obj = constructor.newInstance();
				ViewUtil.setViewHolder(view, obj);

			} catch (Exception e) {
			}
		}

		initView(position, getItem(position), view, view.getTag());
		return view;
	}

	public abstract void initView(int position, Object item, View view, Object viewHolder);

}
