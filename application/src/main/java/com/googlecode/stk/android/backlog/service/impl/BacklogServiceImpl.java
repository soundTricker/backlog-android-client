package com.googlecode.stk.android.backlog.service.impl;

import java.util.List;
import java.util.Map;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import roboguice.util.Ln;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.googlecode.stk.android.backlog.Const;
import com.googlecode.stk.android.backlog.db.entity.Comment;
import com.googlecode.stk.android.backlog.db.entity.Component;
import com.googlecode.stk.android.backlog.db.entity.Issue;
import com.googlecode.stk.android.backlog.db.entity.IssueType;
import com.googlecode.stk.android.backlog.db.entity.Priority;
import com.googlecode.stk.android.backlog.db.entity.Project;
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
		
		Object[] versions = call("backlog.getVersions" , projectId);
		
		List<Version> list = Util.convertList(versions, Version.class);
		
		for (Version version : list) {
			version.projectId = projectId;
		}
		
		return list;
	}

	@Override
	public List<User> getUsers(Integer projectId) throws XMLRPCException {
		
		Object[] versions = call("backlog.getUsers" , projectId);
		
		List<User> list = Util.convertList(versions, User.class);
		
		return list;
	}

	@SuppressWarnings("unchecked")
	public <T> T call(String methodName, Object... params)
			throws XMLRPCException {

		if (!isSetuped()) {

			throw new RuntimeException("not initialized");
		}
		
		Ln.d("call %s %s", getBacklogEndPoint() , methodName);
		XMLRPCClient client = new XMLRPCClient(getBacklogEndPoint(), account,
				password);

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
		
		if(object == null) {
			return null;
		}
		
		return Issue.create((Map<String,Object>) object);
	}

	@Override
	public List<Status> getStatuses() throws XMLRPCException {
		
		Object[] statuses = call("backlog.getStatuses");
		
		if(statuses == null) {
			return null;
		}
		
		List<Status> list = Util.convertList(statuses, Status.class);
		
		return list;
	}

	@Override
	public List<Priority> getPriorities() throws XMLRPCException {
		
		Object[] priorites = call("backlog.getPriorities");
		
		if(priorites == null) {
			return null;
		}
		
		List<Priority> list = Util.convertList(priorites, Priority.class);
		
		return list;
	}

	@Override
	public List<Resolution> getResolutions() throws XMLRPCException {
		
		Object[] resolutions = call("backlog.getResolutions");
		
		if(resolutions == null) {
			return null;
		}
		
		List<Resolution> list = Util.convertList(resolutions, Resolution.class);
		
		return list;
	}
	
	@Override
	public List<Comment> getComments(Integer issueId) throws XMLRPCException {
		
		Object[] comments = call("backlog.getComments", issueId);
		
		if(comments == null) {
			return null;
		}
		
		List<Comment> list = Util.convertList(comments, Comment.class);
		
		return list;
	}

//	public static class XmlRpcAsyncTask<T, M> extends RoboAsyncTask<T> {
//
//		private final XmlRpcParam param;
//		private final AsyncTaskCallBack<M> callBack;
//		private final BacklogService backlogService;
//		private final Converter<T, M> converter;
//
//		public XmlRpcAsyncTask(Context context, XmlRpcParam param,
//				BacklogService backlogService, AsyncTaskCallBack<M> callback,
//				Converter<T, M> converter) {
//			ProgressDialog dialog = new ProgressDialog(context);
//			callback.setDialog(dialog);
//			((InjectorProvider) context).getInjector().injectMembers(this);
//			this.param = param;
//			this.backlogService = backlogService;
//			this.converter = converter;
//			this.callBack = callback;
//		}
//
//		@Override
//		protected void onPreExecute() throws Exception {
//			callBack.onPreExecute();
//		}
//
//		public T call() throws Exception {
//			return backlogService.call(param.methodName, param.params);
//		}
//
//		@Override
//		protected void onFinally() throws RuntimeException {
//			callBack.onFinally();
//		}
//
//		protected void onSuccess(T t) throws Exception {
//			callBack.onSuccess(converter.contert(t));
//		};
//
//		@Override
//		protected void onException(Exception e) throws RuntimeException {
//			callBack.onException(e);
//		}
//	}
//
//	public static class XmlRpcParam {
//
//		public final String methodName;
//		public final Object[] params;
//
//		public XmlRpcParam(String methodName, Object... params) {
//			this.methodName = methodName;
//			this.params = params;
//		}
//	}
//
//	abstract public static class NonDialogAsyncTaskCallBack<T> extends AsyncTaskCallBack<T> {
//		@Override
//		public void onPreExecute() {
//		}
//	}
//
//	abstract public static class AsyncTaskCallBack<T> {
//		private ProgressDialog dialog;
//
//		public void setDialog(ProgressDialog dialog) {
//			this.dialog = dialog;
//		}
//
//		public void onFinally() {
//			if (dialog.isShowing()) {
//				dialog.dismiss();
//			}
//		}
//
//		public void onPreExecute() {
//			dialog.setMessage("データを取得します");
//			dialog.show();
//		}
//
//		abstract public void onSuccess(T result);
//
//		public void onException(Exception e) {
//			Log.e(TAG, e.getMessage(), e);
//		}
//	}

}
