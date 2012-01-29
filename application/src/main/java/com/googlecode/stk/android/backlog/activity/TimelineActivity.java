package com.googlecode.stk.android.backlog.activity;

import static com.googlecode.stk.android.backlog.Const.*;

import java.sql.SQLException;
import java.util.List;

import org.xmlrpc.android.XMLRPCException;

import roboguice.util.Ln;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.inject.Inject;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.RoboGuice;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.stk.android.backlog.R;
import com.googlecode.stk.android.backlog.adapter.TimelineAdapter;
import com.googlecode.stk.android.backlog.db.entity.Timeline;
import com.googlecode.stk.android.backlog.db.entity.UserIcon;
import com.googlecode.stk.android.backlog.service.BacklogService;
import com.j256.ormlite.dao.Dao;

@EActivity(R.layout.timeline_list)
@RoboGuice
public class TimelineActivity extends ListActivity {

	@Inject
	BacklogService backlogService;

	@Inject
	TimelineAdapter timelineAdapter;

	@Inject
	Dao<UserIcon , Integer> userIconDao;

	ListMultimap<Integer, Timeline> userIconQueue = ArrayListMultimap.create();

	ProgressDialog dialog;
	
	@AfterViews
	public void initViews() {
		if(!backlogService.isSetuped()) {
			gotoSetting();
			return;
		}

		setListAdapter(timelineAdapter);
		reloadTimeline();
	}
	
	

	protected void reloadTimeline(){
		Log.i(TAG, "load timeline");
		
		dialog = new ProgressDialog(this);
		
		dialog.setMessage("タイムラインを読み込み中...");
		dialog.show();
		timelineAdapter.clear();
		reloadTimelineAsync();
	}

	@Background
	protected void reloadTimelineAsync() {
		List<Timeline> timelineList = null;
		try {
			timelineList = backlogService.getTimeline();
			Ln.d("タイムライン取得 %d個", timelineList.size());
		} catch (XMLRPCException e) {
			Ln.e(e);
			failThread("タイムラインの取得に失敗しました。");
			return;
		}
		
		for (Timeline timeline : timelineList) {

			UserIcon userIcon = null;
			try {
				userIcon = userIconDao.queryForId(timeline.user.id);
				if(userIcon != null && userIcon.data != null && userIcon.data.length > 0) {
					timeline.icon = BitmapFactory.decodeByteArray(userIcon.data , 0 ,userIcon.data.length);
				}
			} catch (SQLException e) {
				Ln.e(e , "can't get UserIcon id:%d" , timeline.user.id);
			}
			userIconQueue.put(timeline.user.id, timeline);
		}
		
		set2Adapter(timelineList);
		
		for (final Integer id : userIconQueue.keySet()) {
			loadUserIcon(id);
		}
	}

	@Background
	protected void loadUserIcon(final Integer id) {
		UserIcon result = null;
		try {
			result = backlogService.getUserIcon(id);
		} catch (XMLRPCException e) {
			Ln.e(e , "can't get userIcon id : %d" , id);
			return;
		}
		
		try {
			userIconDao.createOrUpdate(result);
		} catch (SQLException e) {
			Ln.e(e , "fail db access.");
		}
		List<Timeline> list = userIconQueue.get(id);
		for (Timeline t : list) {
			t.icon = BitmapFactory.decodeByteArray(result.data, 0 , result.data.length);
		}
		userIconQueue.removeAll(id);
		notifyUpdateIcon();
	}

	@UiThread
	protected void notifyUpdateIcon() {
		timelineAdapter.notifyDataSetChanged();
	}

	@UiThread
	protected void failThread(String message) {
		if(dialog != null) {
			dialog.dismiss();
		}
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	@UiThread
	protected void set2Adapter(List<Timeline> timelineList) {
		if(dialog != null) {
			dialog.dismiss();
		}
		for (Timeline timeline : timelineList) {
			timelineAdapter.add(timeline);
		}
		timelineAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		Intent intent = new Intent(this, IssueDetailActivity_.class);

		Timeline item = timelineAdapter.getItem(position);

		intent.putExtra("issueId", item.issue.id);

		startActivity(intent);
	}

	private void gotoSetting() {
		Intent intent = new Intent(this , SettingActivity_.class);

		startActivityForResult(intent , 0);
	}
	
	private void back() {
		finish();
	}
	
	@Click(R.id.backHomeImage)
	public void onBackHomeIconClick(View icon){
		back();
	}
	
}
