package com.googlecode.stk.android.backlog.activity;

import java.sql.SQLException;
import java.util.List;

import org.xmlrpc.android.XMLRPCException;

import roboguice.event.Observes;
import roboguice.util.Ln;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
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
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.RoboGuice;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.UiThreadDelayed;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.stk.android.backlog.R;
import com.googlecode.stk.android.backlog.adapter.CommentAdapter;
import com.googlecode.stk.android.backlog.db.entity.Comment;
import com.googlecode.stk.android.backlog.db.entity.UserIcon;
import com.googlecode.stk.android.backlog.dialog.CommentDialog;
import com.googlecode.stk.android.backlog.dialog.CommentDialog.OnAddCommentSuccessEvent;
import com.googlecode.stk.android.backlog.service.BacklogService;
import com.j256.ormlite.dao.Dao;

@EActivity(R.layout.comment_list)
@OptionsMenu(R.menu.comment_menu)
@RoboGuice
public class CommentActivity extends ListActivity {

	@Inject
	BacklogService backlogService;

	@Extra("issueId")
	Integer issueId;

	@Extra("issueKey")
	String issueKey;

	@ViewById(android.R.id.list)
	ListView commentList;
	
	@Inject
	Dao<Comment,Integer> commentDao;

	@Inject
	Dao<UserIcon, Integer> userIconDao;

	@Inject
	CommentAdapter commentAdapter;

	ProgressDialog progressDialog;
	
	ListMultimap<Integer, Comment> userIconQueue = ArrayListMultimap.create();
	
	@AfterViews
	public void initView() {

		setVisible(false);
		Ln.d("issueId %d", issueId);
		setListAdapter(commentAdapter);
		
		progressDialog = new ProgressDialog(this);

		progressDialog.setMessage("コメントを取得中");

		progressDialog.show();
		
		loadComment();
	}

	@Background
	protected void loadComment() {
		try {
			
			List<Comment> comments = commentDao.queryBuilder().where().eq("issueKey", issueKey).query();
			
			if(comments != null && !comments.isEmpty()) {
				reloadCommentList(comments);
			}
			
			comments = backlogService.getComments(issueId);
			
			if(comments == null || comments.isEmpty()) {
				failFinish("コメントが存在しませんでした。");
				
				return;
			}
			
			for (Comment comment : comments) {
				UserIcon icon = userIconDao.queryForId(comment.createdUser.id);
				
				if(icon != null && icon.data != null && icon.data.length > 0) {
					comment.icon = BitmapFactory.decodeByteArray(icon.data, 0, icon.data.length);
					continue;
				}
				
				userIconQueue.put(comment.createdUser.id, comment);
			}
			
			reloadCommentList(comments);
			for (final Integer id : userIconQueue.keySet()) {
				loadUserIcon(id);
			}

			for (Comment comment : comments) {
				commentDao.createOrUpdate(comment);
			}
		} catch (XMLRPCException e) {
			Ln.e(e, "コメントの取得に失敗");
			failFinish("コメントの取得に失敗しました。 (ネットワークエラー)");
			delayFinish();
		} catch (SQLException e) {
			Ln.e(e, "コメントの取得に失敗");
			failFinish("コメントの取得に失敗しました。 (DBエラー)");
			delayFinish();
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
		
		commentAdapter.clear();
		
		for (Comment comment : comments) {
			commentAdapter.add(comment);
		}

		commentAdapter.notifyDataSetChanged();
		
		commentList.setVisibility(View.VISIBLE);
		
		if(progressDialog != null) {
			progressDialog.dismiss();
			
		}
		
		setVisible(true);
	}

	@UiThread
	public void failFinish(String message) {
		
		if (progressDialog != null) {
			progressDialog.dismiss();
		}

		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		setVisible(true);
	}

	@UiThreadDelayed(1000)
	public void delayFinish() {

		if (progressDialog != null) {
			progressDialog.dismiss();
		}

		setVisible(true);
		finish();
	}
	private void back() {
		finish();
		setVisible(true);
	}
	
	@Click(R.id.backHomeImage)
	public void onBackHomeIconClick(View icon){
		back();
	}
	
	@OptionsItem(R.id.addComment)
	public void addComment() {
		CommentDialog.createDialog(issueKey, this).show();
	}
	
	public void onAddedCommentSuccess(@Observes OnAddCommentSuccessEvent e) {
		commentAdapter.add(e.comment);
		commentAdapter.notifyDataSetChanged();
		
		try {
			commentDao.createOrUpdate(e.comment);
		} catch (SQLException e1) {
		}
	}
}
