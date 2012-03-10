package com.googlecode.stk.android.backlog.db.entity;

import java.util.Map;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class IssueType extends BaseEntity implements Convertable, HasName {

	@DatabaseField
	public String name;

	@DatabaseField
	public String color;

	@DatabaseField
	public Integer projectId;

	public static IssueType create(Map<String, Object> map) {

		IssueType issueType = new IssueType();

		issueType.set(map);

		return issueType;
	}

	@Override
	public void set(Map<String, Object> map) {
		this.id = (Integer)map.get("id");
		this.name = (String)map.get("name");
		this.color = (String)map.get("color");
	}

	@Override
	public String getName() {
		return name;
	}
}
