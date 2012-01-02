package com.googlecode.stk.android.backlog.db.entity;

import java.util.Map;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Priority extends BaseEntity implements Convertable{

	@DatabaseField
	public String name;

	public static Priority create(Map<String, Object> map) {

		Priority priority = new Priority();

		priority.set(map);
		
		return priority;
	}

	@Override
	public void set(Map<String, Object> map) {
		this.id = (Integer)map.get("id");
		this.name = (String)map.get("name");
	}
}
