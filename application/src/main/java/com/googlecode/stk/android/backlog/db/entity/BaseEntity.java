package com.googlecode.stk.android.backlog.db.entity;

import com.j256.ormlite.field.DatabaseField;


public abstract class BaseEntity {

	@DatabaseField(id=true , columnName="_ID")
	public Integer id;
	
}
