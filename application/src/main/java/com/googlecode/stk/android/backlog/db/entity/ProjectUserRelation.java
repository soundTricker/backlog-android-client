package com.googlecode.stk.android.backlog.db.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class ProjectUserRelation extends BaseEntity {
	
	@DatabaseField(foreign=true,foreignAutoRefresh=true,columnName="project_id")
	public Project project;
	
	@DatabaseField(foreign=true,foreignAutoRefresh=true,columnName="user_id")
	public User user;
}
