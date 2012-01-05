package com.googlecode.stk.android.backlog.activity;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.xmlrpc.android.XMLRPCException;

import roboguice.util.Ln;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.widget.TextView;

import com.google.inject.Inject;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.RoboGuice;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.stk.android.backlog.R;
import com.googlecode.stk.android.backlog.db.entity.Issue;
import com.googlecode.stk.android.backlog.service.BacklogService;
import com.j256.ormlite.dao.Dao;

@EActivity(R.layout.issue_detail)
@RoboGuice
public class IssueDetailActivity extends Activity {

	@Inject
	BacklogService backlogService;
	
	@Inject
	Dao<Issue,Integer> issueDao;
	
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
	
	Issue issue;
	
	ProgressDialog dialog;
	
	@AfterViews
	public void initView() {
		
		setVisible(false);
		Ln.d("issueId %d",issueId);
		
		dialog = new ProgressDialog(this);
		
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		
		dialog.setMessage("課題を取得中");
		
		dialog.show();
		
		loadIssue();
	}
	
	@Background
	public void loadIssue() {
		
		try {
			issue = issueDao.queryForId(issueId);
			if(issue != null) {
				setIssue(issue);
			}
		} catch (SQLException e) {
			Ln.e(e);
		}
		
		try {
			issue = backlogService.getIssue(issueId);
			setIssue(issue);
			issueDao.createOrUpdate(issue);
		} catch (NumberFormatException e) {
			throw new RuntimeException(e);
		} catch (XMLRPCException e) {
			Ln.e(e);
		} catch (SQLException e) {
			Ln.e(e);
		}
	}
	
	@UiThread
	public void setIssue(Issue issue) {

		if(issue.issueType != null) {
			
			type.setText(issue.issueType.name);
			
			if(issue.issueType.color != null) {
				
				type.setBackgroundColor(Color.parseColor(issue.issueType.color));
			}
		}
		
		status.setText(issue.status.name);
		
		issueKey.setText(issue.key);
		
		createdAt.setText(new SimpleDateFormat("yyyy/MM/dd HH:mm").format(issue.getCreatedOn()));
		
		title.setText(issue.summary);
		
		priority.setText(issue.priority.name);
		
		if(issue.createdUser != null) {
			
			userName.setText(issue.createdUser.name);
		}
		
		description.setText(issue.description);

		if(dialog != null) {
			dialog.dismiss();
		}
		setVisible(true);
		
	}
}
