package com.googlecode.stk.android.backlog.db.entity;

import java.util.Map;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class User extends BaseEntity implements Convertable,HasName{

	@DatabaseField
	public String name;
	
	public static User create(Map<String, Object> map) {

		User user = new User();

		user.set(map);

		return user;
	}

	@Override
	public void set(Map<String, Object> map) {
		this.id = (Integer)map.get("id");
		this.name = (String)map.get("name");
	}

	@Override
	public String getName() {
		return name;
	}
}
