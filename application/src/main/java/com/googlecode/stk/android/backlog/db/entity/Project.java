package com.googlecode.stk.android.backlog.db.entity;

import java.util.Map;

import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Project extends BaseEntity implements Convertable {

	public String name;

	public String key;

	public String url;

	public Boolean archived;

	@Override
	public void set(Map<String, Object> map) {
		this.id = (Integer)map.get("id");

		this.name = (String)map.get("name");

		this.key = (String)map.get("key");

		this.url = (String)map.get("url");

		this.archived = (Boolean)map.get("archived");
	}

}
