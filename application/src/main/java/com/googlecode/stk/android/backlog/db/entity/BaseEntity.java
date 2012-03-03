package com.googlecode.stk.android.backlog.db.entity;

import com.j256.ormlite.field.DatabaseField;


public abstract class BaseEntity {

	@DatabaseField(id=true , columnName="_ID")
	public Integer id;

	@Override
	public boolean equals(Object o) {
		if(o == null) {
			return false;
		}
		
		if(!(o instanceof BaseEntity)) {
			return false;
		}
		
		BaseEntity baseEntity = (BaseEntity)o;
		
		if(baseEntity.id == this.id) {
			return true;
		}
		
		if(baseEntity.id == null) {
			return this.id == null;
		}
		return baseEntity.id.equals(this.id);
	}
}
