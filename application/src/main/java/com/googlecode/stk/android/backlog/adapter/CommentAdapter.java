package com.googlecode.stk.android.backlog.adapter;

import java.text.SimpleDateFormat;

import roboguice.adapter.IterableAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.inject.Inject;
import com.googlecode.stk.android.backlog.R;
import com.googlecode.stk.android.backlog.db.entity.Comment;

public class CommentAdapter extends IterableAdapter<Comment> {

	@Inject
	Context context;

	@Inject
	LayoutInflater mInflater;

	public CommentAdapter(Context context,
		int resource,
		int textViewResourceId,
		Iterable<Comment> objects) {
		super(context, resource, textViewResourceId, objects);
	}

	public CommentAdapter(Context context,
		int resource,
		int textViewResourceId) {
		super(context, resource, textViewResourceId);
	}

	public CommentAdapter(Context context,
		int textViewResourceId,
		Iterable<Comment> objects) {
		super(context, textViewResourceId, objects);
	}

	public CommentAdapter(Context context,
		int textViewResourceId) {
		super(context, textViewResourceId);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Comment item = this.getItem(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.comment_row, null);
		}

		setText(convertView, R.id.user, item.createdUser.name);
		
		ImageView userIcon = (ImageView)convertView.findViewById(R.id.userIcon);

		if(item.icon != null) {
			userIcon.setImageBitmap(item.icon);
			((ProgressBar)convertView.findViewById(R.id.progressBar1)).setVisibility(View.INVISIBLE);
//			((ProgressBar)convertView.findViewById(R.id.progressBar1)).setLayoutParams(new LayoutParams(0,0));
			userIcon.setVisibility(View.VISIBLE);
		}
		setText(convertView, R.id.updatedOn, new SimpleDateFormat("yyyy/MM/dd HH:mm").format(item.getUpdatedOnAsDate()));
		setText(convertView, R.id.content, item.content);

		return convertView;
	}

	private void setText(View convertView, int id , String value) {
		TextView view = (TextView)convertView.findViewById(id);

		view.setText(value);
	}

}
