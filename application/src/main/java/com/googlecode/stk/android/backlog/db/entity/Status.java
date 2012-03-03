package com.googlecode.stk.android.backlog.db.entity;

import java.util.Map;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Status extends BaseEntity implements Convertable {

	public static final int FINISH_ID = 4;

	@DatabaseField
	public String name;

	public static Status create(Map<String, Object> map) {
		Status status = new Status();

		status.set(map);

		return status;
	}

	@Override
	public void set(Map<String, Object> map) {
		this.id = (Integer) map.get("id");
		this.name = (String) map.get("name");
	}

}
