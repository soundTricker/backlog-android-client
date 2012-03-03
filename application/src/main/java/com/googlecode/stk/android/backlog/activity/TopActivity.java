package com.googlecode.stk.android.backlog.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.googlecode.androidannotations.annotations.BeforeCreate;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.RoboGuice;
import com.googlecode.stk.android.backlog.R;
import com.googlecode.stk.android.backlog.service.BacklogService;

@EActivity(R.layout.main)
@RoboGuice
public class TopActivity extends Activity {

	private static final int MENU_ID_SETTING = Menu.FIRST + 1;

	@Inject
	@Named("default")
	public SharedPreferences sp;

	@Inject
	public BacklogService backlogService;

	@BeforeCreate
	public void initViews() {
		if(!backlogService.isSetuped()) {
			Toast.makeText(this, "初期設定を行います。", Toast.LENGTH_SHORT).show();
			gotoSetting();
			return;
		}
	}

	private void gotoSetting() {
		Intent intent = new Intent(this , SettingActivity_.class);

		startActivityForResult(intent , 0);
	}

	private void gotoTimeline() {
		Intent intent = new Intent(this, TimelineActivity_.class);


		startActivityForResult(intent, 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_ID_SETTING, Menu.NONE,R.string.setting);

		return super.onCreateOptionsMenu(menu);
	}

	@Click(R.id.settingImage)
	public void onSettingIconClick() {
		gotoSetting();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case MENU_ID_SETTING:
			gotoSetting();
			return true;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Click(R.id.timelineButton)
	public void onTimelineButtonClick() {
		gotoTimeline();
	}

}
