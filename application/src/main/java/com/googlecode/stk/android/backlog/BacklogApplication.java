package com.googlecode.stk.android.backlog;

import java.util.List;

import roboguice.application.RoboApplication;
import android.content.Context;

import com.google.inject.Module;
import com.googlecode.stk.android.backlog.db.DBConnection;
import com.googlecode.stk.android.backlog.inject.BacklogModule;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

public class BacklogApplication extends RoboApplication {

	private OrmLiteSqliteOpenHelper helper;

	public BacklogApplication() {
		super();
	}

	public BacklogApplication(Context context) {
		super();
		attachBaseContext(context);
	}

	@Override
	public void onCreate() {
		OpenHelperManager.setOpenHelperClass(DBConnection.class);
		
		helper = OpenHelperManager.getHelper(this , DBConnection.class);
		
		getInjector().injectMembers(helper);
		super.onCreate();
	}

	@Override
	protected void addApplicationModules(List<Module> modules) {
		modules.add(new BacklogModule(helper));
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		OpenHelperManager.releaseHelper();
	}

}
