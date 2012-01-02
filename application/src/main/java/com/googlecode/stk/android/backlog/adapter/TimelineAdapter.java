package com.googlecode.stk.android.backlog.adapter;

import java.text.SimpleDateFormat;

import roboguice.adapter.IterableAdapter;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.inject.Inject;
import com.googlecode.stk.android.backlog.R;
import com.googlecode.stk.android.backlog.db.entity.Timeline;

public class TimelineAdapter extends IterableAdapter<Timeline> {

	@Inject Context context;

	@Inject
	private LayoutInflater mInflater;


	public TimelineAdapter(Context context, int resource, int textViewResourceId, Iterable<Timeline> objects) {
		super(context, resource, textViewResourceId, objects);
	}

	public TimelineAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);
	}

	public TimelineAdapter(Context context, int textViewResourceId,
			Iterable<Timeline> objects) {
		super(context, textViewResourceId, objects);
	}

	public TimelineAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Timeline item = this.getItem(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.timeline_row, null);
		}

		setText(convertView, R.id.type, item.type.name);
		setText(convertView, R.id.issueKey, item.issue.key);
		setText(convertView, R.id.user, item.user.name);
		setText(convertView, R.id.updatedOn, new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(item.getUpdatedOnAsDate()));
		setText(convertView, R.id.summary, item.issue.summary);
		setText(convertView, R.id.content, item.content);

		LinearLayout timelineLabel = (LinearLayout)convertView.findViewById(R.id.timeline_label);

		switch(item.type.id) {
			case 1:
				timelineLabel.setBackgroundColor(Color.rgb(99, 0, 0));
				break;
			case 2:
				timelineLabel.setBackgroundColor(Color.rgb(39, 121, 202));
				break;
			case 3:
				timelineLabel.setBackgroundColor(Color.rgb(126, 168, 0));
				break;
		}

		final ImageView userIcon = (ImageView)convertView.findViewById(R.id.userIcon);
		if(item.icon != null) {
			userIcon.setImageBitmap(item.icon);
		} else {

		}
		return convertView;

	}

	private void setText(View convertView, int id , String value) {
		TextView view = (TextView)convertView.findViewById(id);

		view.setText(value);
	}
}
