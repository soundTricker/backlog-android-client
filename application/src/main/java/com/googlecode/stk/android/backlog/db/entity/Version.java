package com.googlecode.stk.android.backlog.db.entity;

import java.util.Date;
import java.util.Map;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Version extends BaseEntity implements Convertable{

	@DatabaseField
	public String name;

	@DatabaseField
	public Date date;

	public static Version create(Map<String, Object> map) {

		Version bean = new Version();

		bean.set(map);

		return bean;
	}

	@Override
	public void set(Map<String, Object> map) {
		this.id = (Integer)map.get("id");
		this.name = (String)map.get("name");
		this.date = (Date)map.get("date");
		
	}

}
