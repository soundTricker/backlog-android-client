package com.googlecode.stk.android.backlog.db;

import java.sql.SQLException;

import roboguice.util.Ln;

import com.google.inject.Provider;
import com.googlecode.stk.android.backlog.db.entity.BaseEntity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;


public class DaoProvider<T extends BaseEntity> implements Provider<Dao<T, Integer>> {

	private ConnectionSource con;
	private Class<T> clazz;

	public DaoProvider(ConnectionSource con, Class<T> clazz) {
		this.con = con;
		this.clazz = clazz;
	}
	
	@Override
	public Dao<T, Integer> get() {
		try {
			return DaoManager.createDao(con, clazz);
		} catch (SQLException e) {
			Ln.e(e);
			return null;
		}
	}

}
