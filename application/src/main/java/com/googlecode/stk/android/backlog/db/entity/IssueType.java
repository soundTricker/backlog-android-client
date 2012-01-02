package com.googlecode.stk.android.backlog.db.entity;

import java.util.Map;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class IssueType extends BaseEntity implements Convertable {

	@DatabaseField
	public String name;

	@DatabaseField
	public String color;

	public static IssueType create(Map<String, Object> map) {

		IssueType user = new IssueType();

		user.id = (Integer)map.get("id");
		user.name = (String)map.get("name");
		user.color = (String)map.get("color");

		return user;
	}

	@Override
	public void set(Map<String, Object> map) {
		this.id = (Integer)map.get("id");
		this.name = (String)map.get("name");
		this.color = (String)map.get("color");
	}

}
