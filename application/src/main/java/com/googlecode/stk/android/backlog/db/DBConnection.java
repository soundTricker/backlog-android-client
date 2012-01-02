package com.googlecode.stk.android.backlog.db;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.googlecode.stk.android.backlog.db.entity.Component;
import com.googlecode.stk.android.backlog.db.entity.Issue;
import com.googlecode.stk.android.backlog.db.entity.IssueType;
import com.googlecode.stk.android.backlog.db.entity.Priority;
import com.googlecode.stk.android.backlog.db.entity.Project;
import com.googlecode.stk.android.backlog.db.entity.User;
import com.googlecode.stk.android.backlog.db.entity.UserIcon;
import com.googlecode.stk.android.backlog.db.entity.Version;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DBConnection extends OrmLiteSqliteOpenHelper {

	@Inject
	public DBConnection(Context context) {
		super(context, "backlog2.sqlite", null, 1);
	}
	
	protected List<Class<?>> getDatabaseTableList() {
		
		List<Class<?>> databaseTableList = Lists.<Class<?>>newArrayList(
											Project.class,
											Component.class,
											Issue.class,
											IssueType.class,
											Priority.class,
											User.class,
											UserIcon.class,
											Version.class
											);
		return databaseTableList;
	}

	@Override
	public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
		try {
			for (Class<?> clazz : getDatabaseTableList()) {
				TableUtils.createTable(connectionSource, clazz);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			for (Class<?> clazz : getDatabaseTableList()) {
					TableUtils.dropTable(connectionSource, clazz, true);
			}
			onCreate(database, connectionSource);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
