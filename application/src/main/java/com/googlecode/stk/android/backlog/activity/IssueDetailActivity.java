package com.googlecode.stk.android.backlog.activity;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.xmlrpc.android.XMLRPCException;

import roboguice.util.Ln;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.inject.Inject;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.RoboGuice;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.UiThreadDelayed;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.stk.android.backlog.R;
import com.googlecode.stk.android.backlog.adapter.CommentAdapter;
import com.googlecode.stk.android.backlog.db.entity.Comment;
import com.googlecode.stk.android.backlog.db.entity.Issue;
import com.googlecode.stk.android.backlog.db.entity.UserIcon;
import com.googlecode.stk.android.backlog.service.BacklogService;
import com.j256.ormlite.dao.Dao;

@EActivity(R.layout.issue_detail)
@RoboGuice
public class IssueDetailActivity extends ListActivity {

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

	@ViewById(android.R.id.list)
	ListView commentList;

	@Inject
	Dao<UserIcon, Integer> userIconDao;

	@Inject
	CommentAdapter commentAdapter;

	Issue issue;

	ProgressDialog dialog;

	ListMultimap<Integer, Comment> userIconQueue = ArrayListMultimap.create();

	@AfterViews
	public void initView() {

		setVisible(false);
		Ln.d("issueId %d", issueId);
		setListAdapter(commentAdapter);

		dialog = new ProgressDialog(this);

		dialog.setMessage("課題を取得中");

		dialog.show();

		loadIssue();

	}

	@Background
	public void loadIssue() {

		boolean loadedFromDb = false;
		try {
			issue = issueDao.queryForId(issueId);
			if (issue != null) {
				setIssue(issue);
				loadedFromDb = true;
			}
		} catch (SQLException e) {
			Ln.e(e);
		}

		try {
			issue = backlogService.getIssue(issueId);
			setIssue(issue);
			issueDao.createOrUpdate(issue);
		} catch (XMLRPCException e) {

			Ln.e(e);

			if (!loadedFromDb) {
				failFinish("ネットワークエラーが発生しました。");

				failFinishDelay();
			}

		} catch (SQLException e) {
			Ln.e(e);
		}
	}

	@UiThread
	public void failFinish(String message) {
		if (dialog != null) {
			dialog.setMessage(message);
		}
	}

	@UiThreadDelayed(1000)
	public void failFinishDelay() {

		if (dialog != null) {
			dialog.dismiss();
		}

		finish();
	}

	@UiThread
	public void setIssue(Issue issue) {

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

//				progressBar.setLayoutParams((new LayoutParams(0, 0));
				progressBar.setVisibility(View.INVISIBLE);
				userIcon.setVisibility(View.VISIBLE);
			} catch (SQLException e) {
				Ln.e(e);
			} catch (XMLRPCException e) {
				Ln.e(e);
			} finally {
//				progressBar.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
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
	public void clickShowComment() {
		dialog = new ProgressDialog(this);

		dialog.setMessage("コメントを取得中");

		dialog.show();

		loadComment();

	}

	@Background
	protected void loadComment() {
		try {
			List<Comment> comments = backlogService.getComments(issueId);
			
			if(comments == null || comments.size() == 0) {
				failFinish("コメントが存在しませんでした。");
			}
			
			for (Comment comment : comments) {
				UserIcon icon = userIconDao.queryForId(comment.createdUser.id);
				
				if(icon == null || icon.data == null || icon.data.length <= 0) {
					userIconQueue.put(comment.createdUser.id, comment);
				} else {
					comment.icon = BitmapFactory.decodeByteArray(icon.data, 0, icon.data.length);
				}
			}
			
			reloadCommentList(comments);
			
			for (final Integer id : userIconQueue.keySet()) {
				loadUserIcon(id);
			}

		} catch (XMLRPCException e) {
			Ln.e(e, "コメントの取得に失敗");
			failFinish("コメントの取得に失敗しました。 (ネットワークエラー)");
		} catch (SQLException e) {
			Ln.e(e, "コメントの取得に失敗");
			failFinish("コメントの取得に失敗しました。 (DBエラー)");
		}
	}

	@Background
	protected void loadUserIcon(final Integer id) {
		UserIcon result = null;
		try {
			result = backlogService.getUserIcon(id);
		} catch (XMLRPCException e) {
			Ln.e(e, "can't get userIcon id : %d", id);
			return;
		}

		try {
			userIconDao.createOrUpdate(result);
		} catch (SQLException e) {
			Ln.e(e, "fail db access.");
		}
		List<Comment> list = userIconQueue.get(id);
		for (Comment t : list) {
			t.icon = BitmapFactory.decodeByteArray(result.data, 0, result.data.length);
		}
		userIconQueue.removeAll(id);
		notifyUpdateIcon();
	}

	@UiThread
	protected void notifyUpdateIcon() {
		commentAdapter.notifyDataSetChanged();
	}

	@UiThread
	public void reloadCommentList(List<Comment> comments) {
		for (Comment comment : comments) {
			commentAdapter.add(comment);
		}

		commentAdapter.notifyDataSetChanged();
		
		commentList.setVisibility(View.VISIBLE);
	}

}
