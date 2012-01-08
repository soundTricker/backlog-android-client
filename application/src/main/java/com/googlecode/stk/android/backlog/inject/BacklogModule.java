package com.googlecode.stk.android.backlog.inject;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.InjectorProvider;
import roboguice.inject.SharedPreferencesName;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.googlecode.stk.android.backlog.adapter.CommentAdapter;
import com.googlecode.stk.android.backlog.adapter.TimelineAdapter;
import com.googlecode.stk.android.backlog.db.DaoProvider;
import com.googlecode.stk.android.backlog.db.entity.Component;
import com.googlecode.stk.android.backlog.db.entity.Issue;
import com.googlecode.stk.android.backlog.db.entity.IssueType;
import com.googlecode.stk.android.backlog.db.entity.Priority;
import com.googlecode.stk.android.backlog.db.entity.Project;
import com.googlecode.stk.android.backlog.db.entity.Resolution;
import com.googlecode.stk.android.backlog.db.entity.Status;
import com.googlecode.stk.android.backlog.db.entity.User;
import com.googlecode.stk.android.backlog.db.entity.UserIcon;
import com.googlecode.stk.android.backlog.db.entity.Version;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;

public class BacklogModule extends AbstractAndroidModule {

	private final OrmLiteSqliteOpenHelper helper;

	public BacklogModule(OrmLiteSqliteOpenHelper helper) {
		this.helper = helper;
	}

	@Override
	protected void configure() {
		bind(new TypeLiteral<Dao<Project, Integer>>() {})
		.toProvider(new DaoProvider<Project>(helper.getConnectionSource(), Project.class))
		.in(Singleton.class);
		bind(new TypeLiteral<Dao<UserIcon, Integer>>() {})
		.toProvider(new DaoProvider<UserIcon>(helper.getConnectionSource(), UserIcon.class))
		.in(Singleton.class);
		bind(new TypeLiteral<Dao<Issue, Integer>>() {})
		.toProvider(new DaoProvider<Issue>(helper.getConnectionSource(), Issue.class))
		.in(Singleton.class);
		bind(new TypeLiteral<Dao<Component, Integer>>() {})
		.toProvider(new DaoProvider<Component>(helper.getConnectionSource(), Component.class))
		.in(Singleton.class);
		bind(new TypeLiteral<Dao<IssueType, Integer>>() {})
		.toProvider(new DaoProvider<IssueType>(helper.getConnectionSource(), IssueType.class))
		.in(Singleton.class);
		bind(new TypeLiteral<Dao<Priority, Integer>>() {})
		.toProvider(new DaoProvider<Priority>(helper.getConnectionSource(), Priority.class))
		.in(Singleton.class);
		bind(new TypeLiteral<Dao<Status, Integer>>() {})
		.toProvider(new DaoProvider<Status>(helper.getConnectionSource(), Status.class))
		.in(Singleton.class);
		bind(new TypeLiteral<Dao<User, Integer>>() {})
		.toProvider(new DaoProvider<User>(helper.getConnectionSource(), User.class))
		.in(Singleton.class);
		bind(new TypeLiteral<Dao<Version, Integer>>() {})
		.toProvider(new DaoProvider<Version>(helper.getConnectionSource(), Version.class))
		.in(Singleton.class);
		bind(new TypeLiteral<Dao<Resolution, Integer>>() {})
		.toProvider(new DaoProvider<Resolution>(helper.getConnectionSource(), Resolution.class))
		.in(Singleton.class);
		
		bindConstant().annotatedWith(SharedPreferencesName.class).to("default");
	}
	
	@Provides
	@Named("default")
	public SharedPreferences providesDefaultSharedPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	@Provides
	public TimelineAdapter timelineAdapter(Context context, LayoutInflater mInflater) {

		TimelineAdapter timelineAdapter = new TimelineAdapter(context, 0);
		((InjectorProvider) context).getInjector().injectMembers(timelineAdapter);

		return timelineAdapter;
	}
	
	@Provides
	public CommentAdapter commentAdapter(Context context, LayoutInflater mInflater) {
		CommentAdapter commentAdapter = new CommentAdapter(context, 0);
		
		((InjectorProvider) context).getInjector().injectMembers(commentAdapter);

		return commentAdapter;
		
	}
}
