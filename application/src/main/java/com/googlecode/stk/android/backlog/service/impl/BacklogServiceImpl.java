package com.googlecode.stk.android.backlog.service.impl;

import java.util.List;
import java.util.Map;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import roboguice.util.Ln;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.googlecode.stk.android.backlog.Const;
import com.googlecode.stk.android.backlog.db.entity.Comment;
import com.googlecode.stk.android.backlog.db.entity.Component;
import com.googlecode.stk.android.backlog.db.entity.Issue;
import com.googlecode.stk.android.backlog.db.entity.IssueType;
import com.googlecode.stk.android.backlog.db.entity.Priority;
import com.googlecode.stk.android.backlog.db.entity.Project;
import com.googlecode.stk.android.backlog.db.entity.ProjectUserRelation;
import com.googlecode.stk.android.backlog.db.entity.Resolution;
import com.googlecode.stk.android.backlog.db.entity.Status;
import com.googlecode.stk.android.backlog.db.entity.Timeline;
import com.googlecode.stk.android.backlog.db.entity.User;
import com.googlecode.stk.android.backlog.db.entity.UserIcon;
import com.googlecode.stk.android.backlog.db.entity.Version;
import com.googlecode.stk.android.backlog.service.BacklogService;
import com.googlecode.stk.android.backlog.util.Util;

public class BacklogServiceImpl implements BacklogService {

	private static String TAG = "backlog-android-client";

	private String account;

	private String password;

	private String scopeId;

	@Inject
	Context context;

	private final SharedPreferences sp;

	@Inject
	public BacklogServiceImpl(@Named("default") SharedPreferences sp) {
		this.sp = sp;
		reload();
	}

	public void reload() {

		scopeId = sp.getString(Const.KEY_SPACE_ID, null);
		account = sp.getString(Const.KEY_ACCOUNT, null);
		password = sp.getString(Const.KEY_PASSWORD, null);
	}

	private String getBacklogEndPoint() {
		return String.format("https://%s.backlog.jp/XML-RPC", scopeId);
	}

	public boolean isSetuped() {
		return account != null && password != null && scopeId != null;
	}

	public void setBasicAuthentication(String account, String password) {
		this.account = account;
		this.password = password;
		reload();
	}

	@Override
	public List<Component> getComponents(Integer projectId) throws XMLRPCException {

		Object[] components = call("backlog.getComponents", projectId);

		List<Component> list = Util.convertList(components, Component.class);

		for (Component component : list) {
			component.projectId = projectId;
		}

		return list;
	}

	@Override
	public List<Project> getProjects() throws XMLRPCException {
		Object[] projects = call("backlog.getProjects");

		return Util.convertList(projects, Project.class);
	}

	@Override
	public List<IssueType> getIssueTypes(Integer id) throws XMLRPCException {
		Object[] issueTypes = call("backlog.getIssueTypes", id);
		List<IssueType> list = Util.convertList(issueTypes, IssueType.class);

		for (IssueType issueType : list) {
			issueType.projectId = id;
		}

		return list;
	}

	@Override
	public List<Timeline> getTimeline() throws XMLRPCException {
		Object[] timelines = call("backlog.getTimeline");
		return Util.convertList(timelines, Timeline.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public UserIcon getUserIcon(Integer id) throws XMLRPCException {
		Object userIcon = call("backlog.getUserIcon", id);
		return UserIcon.create((Map<String, Object>) userIcon);
	}

	@Override
	public List<Version> getVersions(Integer projectId) throws XMLRPCException {

		Object[] versions = call("backlog.getVersions", projectId);

		List<Version> list = Util.convertList(versions, Version.class);

		for (Version version : list) {
			version.projectId = projectId;
		}

		return list;
	}

	@Override
	public List<User> getUsers(Integer projectId) throws XMLRPCException {

		Object[] versions = call("backlog.getUsers", projectId);

		List<User> list = Util.convertList(versions, User.class);

		return list;
	}

	@SuppressWarnings("unchecked")
	public <T> T call(String methodName, Object... params) throws XMLRPCException {

		if (!isSetuped()) {

			throw new RuntimeException("not initialized");
		}

		Ln.d("call %s %s", getBacklogEndPoint(), methodName);
		XMLRPCClient client = new XMLRPCClient(getBacklogEndPoint(), account, password);

		Object object = client.callEx(methodName, params);

		Log.i(TAG, String.valueOf(object));

		if (object == null) {

			return null;
		}

		return (T) object;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Issue getIssue(int issueId) throws XMLRPCException {

		Object object = call("backlog.getIssue", issueId);

		if (object == null) {
			return null;
		}

		return Issue.create((Map<String, Object>) object);
	}

	@Override
	public List<Status> getStatuses() throws XMLRPCException {

		Object[] statuses = call("backlog.getStatuses");

		if (statuses == null) {
			return null;
		}

		List<Status> list = Util.convertList(statuses, Status.class);

		return list;
	}

	@Override
	public List<Priority> getPriorities() throws XMLRPCException {

		Object[] priorites = call("backlog.getPriorities");

		if (priorites == null) {
			return null;
		}

		List<Priority> list = Util.convertList(priorites, Priority.class);

		return list;
	}

	@Override
	public List<Resolution> getResolutions() throws XMLRPCException {

		Object[] resolutions = call("backlog.getResolutions");

		if (resolutions == null) {
			return null;
		}

		List<Resolution> list = Util.convertList(resolutions, Resolution.class);

		return list;
	}

	@Override
	public List<Comment> getComments(Integer issueId) throws XMLRPCException {

		Object[] comments = call("backlog.getComments", issueId);

		if (comments == null) {
			return null;
		}

		List<Comment> list = Util.convertList(comments, Comment.class);

		return list;
	}

	@Override
	public Comment addComment(String issueKey, String comment) throws XMLRPCException {

		Map<String, Object> map = Maps.newHashMap();

		map.put("key", issueKey);
		map.put("content", comment);

		Map<String, Object> result = call("backlog.addComment", map);

		if (result == null) {
			return null;
		}
		Comment ret = Comment.create(result);
		ret.issueKey = issueKey;
		return ret;
	}

	@Override
	public Issue switchStatus(String key, Status selectedStatus, Resolution selectedResolution, ProjectUserRelation selectedAssigner, String commentText) throws XMLRPCException {

		Map<String, Object> map = Maps.newHashMap();
		
		map.put("key", key);
		map.put("statusId", selectedStatus.id);
		
		if(selectedAssigner != null) {
			map.put("assignerId", selectedAssigner.user.id);
		}
		
		if(selectedResolution != null) {
			map.put("resolutionId", selectedResolution.id);
		}
		
		if(Strings.isNullOrEmpty(commentText)) {
			map.put("comment", commentText);
		}
		
		Map<String,Object> result = call("backlog.switchStatus", map);

		return Issue.create(result);
	}

}
