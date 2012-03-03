package com.googlecode.stk.android.backlog.activity;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.xmlrpc.android.XMLRPCException;

import roboguice.event.Observes;
import roboguice.util.Ln;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.RoboGuice;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.UiThreadDelayed;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.stk.android.backlog.R;
import com.googlecode.stk.android.backlog.db.entity.Issue;
import com.googlecode.stk.android.backlog.db.entity.UserIcon;
import com.googlecode.stk.android.backlog.dialog.SwitchStatusDialog;
import com.googlecode.stk.android.backlog.service.BacklogService;
import com.j256.ormlite.dao.Dao;

@EActivity(R.layout.issue_detail)
@OptionsMenu(R.menu.issue_detail)
@RoboGuice
public class IssueDetailActivity extends Activity {

	@Inject
	BacklogService backlogService;

	@Inject
	Dao<Issue, Integer> issueDao;

	@ViewById(R.id.type)
	TextView type;

	@ViewById(R.id.status)
	TextView status;

	@ViewById(R.id.issueKey)
	TextView issueKey;

	@ViewById(R.id.priority)
	TextView priority;

	@ViewById(R.id.createdAt)
	TextView createdAt;

	@ViewById(R.id.title)
	TextView title;

	@ViewById(R.id.user_name)
	TextView userName;

	@ViewById(R.id.description)
	TextView description;

	@Extra("issueId")
	Integer issueId;

	@ViewById(R.id.progressBar1)
	ProgressBar progressBar;

	@ViewById(R.id.userIcon)
	ImageView userIcon;

	@Inject
	Dao<UserIcon, Integer> userIconDao;

	Issue issue;

	ProgressDialog dialog;

	@AfterViews
	public void initView() {

		setVisible(false);
		Ln.d("issueId %d", issueId);

		dialog = new ProgressDialog(this);

		dialog.setMessage("課題を取得中");

		dialog.show();

		loadIssue();

	}

	@Background
	public void loadIssue() {

		try {
			Issue dbIssue = issueDao.queryForId(issueId);
			if (dbIssue != null) {
				setIssue(dbIssue);
			}
		} catch (SQLException e) {
			Ln.e(e);
		}

		try {
			Issue serverIssue = backlogService.getIssue(issueId);
			setIssue(serverIssue);
			issueDao.createOrUpdate(serverIssue);
		} catch (XMLRPCException e) {

			Ln.e(e);

			if (issue == null) {
				failFinish("ネットワークエラーが発生しました。");

				delayFinish();
			}

		} catch (SQLException e) {
			Ln.e(e);
		}
	}

	@UiThread
	public void failFinish(String message) {

		if (dialog != null) {
			dialog.dismiss();
		}
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	@UiThreadDelayed(1000)
	public void delayFinish() {

		if (dialog != null) {
			dialog.dismiss();
		}

		finish();
	}

	@UiThread
	public void setIssue(Issue issue) {

		this.issue = issue;

		this.issueId = issue.id;

		if (issue.issueType != null) {

			type.setText(issue.issueType.name);

			if (issue.issueType.color != null) {

				type.setBackgroundColor(Color.parseColor(issue.issueType.color));
			}
		}

		status.setText(issue.status.name);

		issueKey.setText(issue.key);

		createdAt.setText(new SimpleDateFormat("yyyy/MM/dd HH:mm").format(issue.getCreatedOn()));

		title.setText(issue.summary);

		priority.setText(issue.priority.name);

		if (issue.createdUser != null) {

			try {
				UserIcon icon = userIconDao.queryForId(issue.createdUser.id);
				if (icon == null) {
					icon = backlogService.getUserIcon(issue.createdUser.id);
				}

				userIcon.setImageBitmap(BitmapFactory.decodeByteArray(icon.data, 0, icon.data.length));
				userIcon.setVisibility(View.VISIBLE);
			} catch (SQLException e) {
				Ln.e(e);
			} catch (XMLRPCException e) {
				Ln.e(e);
			} finally {
				progressBar.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
				progressBar.setVisibility(View.INVISIBLE);
			}
			userName.setText(issue.createdUser.name);
		}

		description.setText(issue.description);

		if (dialog != null) {
			dialog.dismiss();
		}
		setVisible(true);
	}

	@Click(R.id.showComment)
	public void showComment() {
		Intent intent = new Intent(this, CommentActivity_.class);

		intent.putExtra("issueId", issueId);
		intent.putExtra("issueKey", issue.key);

		startActivity(intent);
	}

	private void back() {
		finish();
	}

	@Click(R.id.backHomeImage)
	public void onBackHomeIconClick(View icon) {
		back();
	}

	@OptionsItem(R.id.changeStatus)
	public void onSelectChangeStatus() {
		SwitchStatusDialog.createDialog(this, issue).show();
	}

	public void onSuccessSwitchStatus(@Observes SwitchStatusDialog.OnSwitchStatusSuccessEvent e) {
		setIssue(issue);

		try {
			issueDao.update(issue);
		} catch (SQLException e1) {
			Ln.e(e1, "issueの更新に失敗しました");
		}
	}

}
