package com.googlecode.stk.android.backlog.db.entity;

import java.util.Map;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Issue extends BaseEntity implements Convertable{

	@DatabaseField
	public String key;

	@DatabaseField
	public String summary;

	@DatabaseField
	public String description;

	@DatabaseField(foreign=true , columnName="priority_id")
	public Priority priority;

	public static Issue create(Map<String, Object> map) {

		Issue issue = new Issue();

		issue.set(map);
		
		return issue;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void set(Map<String, Object> map) {
		this.id = (Integer)map.get("id");

		this.key = (String)map.get("key");

		this.summary = (String)map.get("summary");

		this.description = (String)map.get("description");

		this.priority = Priority.create((Map<String,Object>)map.get("priority"));
		
	}

}
