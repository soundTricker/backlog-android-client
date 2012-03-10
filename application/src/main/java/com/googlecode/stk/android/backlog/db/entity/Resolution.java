package com.googlecode.stk.android.backlog.db.entity;

import java.util.Map;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Resolution extends BaseEntity implements Convertable,HasName {

	@DatabaseField
	public String name;
	
	public static Resolution create(Map<String, Object> map) {
		
		Resolution resolution = new Resolution();
		
		resolution.set(map);
		
		return resolution;
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
