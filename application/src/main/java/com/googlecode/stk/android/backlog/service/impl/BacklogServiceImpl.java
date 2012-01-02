package com.googlecode.stk.android.backlog.service.impl;

import java.util.List;
import java.util.Map;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import roboguice.util.Ln;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.googlecode.stk.android.backlog.Const;
import com.googlecode.stk.android.backlog.db.entity.Component;
import com.googlecode.stk.android.backlog.db.entity.IssueType;
import com.googlecode.stk.android.backlog.db.entity.Project;
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

		Map<String, Object> params = Maps.newHashMap();

		params.put("projectId", projectId);

		Object[] components = call("backlog.getComponents", params);

		return Util.convertList(components, Component.class);
	}

	@Override
	public List<Project> getProjects() throws XMLRPCException {
		Object[] projects = call("backlog.getProjects");

		return Util.convertList(projects, Project.class);
	}

	@Override
	public List<IssueType> getIssueTypes(Integer id) throws XMLRPCException {
		Object[] issueTypes = call("backlog.getIssueTypes", id);
		return Util.convertList(issueTypes, IssueType.class);
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
	public List<Version> getVersions(Integer projectId) {
		
		return null;
	}

	@Override
	public List<User> getUsers(Integer projectId) throws XMLRPCException {

		return null;
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
