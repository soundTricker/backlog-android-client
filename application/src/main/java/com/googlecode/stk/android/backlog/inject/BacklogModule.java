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
import com.googlecode.stk.android.backlog.adapter.TimelineAdapter;
import com.googlecode.stk.android.backlog.db.DaoProvider;
import com.googlecode.stk.android.backlog.db.entity.UserIcon;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;

public class BacklogModule extends AbstractAndroidModule {

	private final OrmLiteSqliteOpenHelper helper;

	public BacklogModule(OrmLiteSqliteOpenHelper helper) {
		this.helper = helper;
	}

	@Override
	protected void configure() {
		bind(new TypeLiteral<Dao<UserIcon , Integer>>(){}).toProvider(new DaoProvider<UserIcon>(helper.getConnectionSource(), UserIcon.class)).in(Singleton.class);
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
		((InjectorProvider)context).getInjector().injectMembers(timelineAdapter);

		return timelineAdapter;

	}
}
