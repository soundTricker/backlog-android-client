package com.googlecode.stk.android.backlog.db.entity;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import android.graphics.Bitmap;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Comment extends BaseEntity implements Convertable {

	@DatabaseField
	public String content;
	
	@DatabaseField(foreign = true , foreignAutoRefresh = true , columnName = "created_user_id")
	public User createdUser;
	
	@DatabaseField
	public String createdOn;
	
	@DatabaseField
	public String updatedOn;
	
	@DatabaseField(persisted=false)
	public Bitmap icon;
	
	public static Comment create(Map<String, Object> map) {
		Comment comment = new Comment();
		
		comment.set(map);
		
		return comment;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void set(Map<String, Object> map) {
		
		this.id = (Integer) map.get("id");
		
		this.content = (String) map.get("content");
		
		this.createdUser = User.create((Map<String,Object>)map.get("created_user"));
		
		this.createdOn = (String)map.get("created_on");
		
		this.updatedOn = (String)map.get("updated_on");
	}
	
	public Date getCreatedOnAsDate() {
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		
		return format.parse(createdOn, new ParsePosition(0));
	}

	public Date getUpdatedOnAsDate() {
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		
		return format.parse(updatedOn, new ParsePosition(0));
	}

}
