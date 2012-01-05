package com.googlecode.stk.android.backlog.db.entity;

import java.util.Map;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Component extends BaseEntity implements Convertable {

	@DatabaseField
	public String name;
	
	@DatabaseField
	public Integer projectId;

	public void set(Map<String, Object> map) {

		this.id = (Integer)map.get("id");

		this.name = (String)map.get("name");
	}

}
