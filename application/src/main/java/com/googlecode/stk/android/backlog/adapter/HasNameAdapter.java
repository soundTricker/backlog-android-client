package com.googlecode.stk.android.backlog.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;
import com.googlecode.stk.android.backlog.db.entity.HasName;
import roboguice.adapter.IterableAdapter;

/**
 * Created by IntelliJ IDEA.
 * User: ohashi keisuke
 * Date: 12/03/06
 * Time: 0:06
 * To change this template use File | Settings | File Templates.
 */
public class HasNameAdapter<T extends HasName> extends IterableAdapter<T>{

	public HasNameAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);    //To change body of overridden methods use File | Settings | File Templates.
	}

	public HasNameAdapter(Context context, int resource, int textViewResourceId, Iterable<T> objects) {
		super(context, resource, textViewResourceId, objects);    //To change body of overridden methods use File | Settings | File Templates.
	}

	public HasNameAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);    //To change body of overridden methods use File | Settings | File Templates.
	}

	public HasNameAdapter(Context context, int textViewResourceId, Iterable<T> objects) {
		super(context, textViewResourceId, objects);    //To change body of overridden methods use File | Settings | File Templates.
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if(convertView == null) {
			convertView = new TextView(getContext());
		}

		((TextView)convertView).setText(getItem(position).getName());

		return convertView;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {

		if(convertView == null) {
			convertView = new CheckedTextView(getContext());
		}

		((CheckedTextView)convertView).setText(getItem(position).getName());

		return convertView;
	}
}
