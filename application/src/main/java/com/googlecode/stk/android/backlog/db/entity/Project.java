package com.googlecode.stk.android.backlog.db.entity;

import java.util.Map;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Project extends BaseEntity implements Convertable ,HasName{

	@DatabaseField
	public String name;

	@DatabaseField
	public String key;

	@DatabaseField
	public String url;

	@DatabaseField(dataType=DataType.BOOLEAN)
	public Boolean archived;
	
	public static Project create(Map<String, Object> map) {
		Project project = new Project();
		
		project.set(map);
		
		return project;
	}

	@Override
	public void set(Map<String, Object> map) {
		this.id = (Integer)map.get("id");

		this.name = (String)map.get("name");

		this.key = (String)map.get("key");

		this.url = (String)map.get("url");

		this.archived = (Boolean)map.get("archived");
	}

	@Override
	public String getName() {
		return name;
	}
}
