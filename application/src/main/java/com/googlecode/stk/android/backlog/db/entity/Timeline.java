package com.googlecode.stk.android.backlog.db.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.j256.ormlite.table.DatabaseTable;

import roboguice.util.Ln;
import android.graphics.Bitmap;

@DatabaseTable
public class Timeline implements Convertable {

	@Override
	public String toString() {
		return "Timeline [content=" + content + ", issue=" + issue + ", type="
				+ type + ", updatedOn=" + updatedOn + ", user=" + user + "]";
	}

	public Type type;

	public String content;

	public String updatedOn;

	public User user;

	public Issue issue;

	public Bitmap icon;

	public static class Type {
		public Integer id;

		public String name;

		public static Type create(Map<String, Object> t) {

			Type type = new Type();

			type.id = (Integer)t.get("id");

			type.name = (String)t.get("name");

			return type;
		}
	}

	public static Timeline create(Map<String, Object> m) {

		Timeline timeline = new Timeline();
		
		timeline.set(m);
		
		return timeline;
	}

	public Date getUpdatedOnAsDate() {
		if(updatedOn == null || "".equals(updatedOn)) {
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

		try {
			return sdf.parse(updatedOn);
		} catch (ParseException e) {
			Ln.e(e, "パースに失敗");
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void set(Map<String, Object> map) {
		Map<String, Object> t = (Map<String, Object>)map.get("type");

		Type type = Type.create(t);

		this.type = type;

		this.content = (String)map.get("content");

		this.issue = Issue.create((Map<String, Object>)map.get("issue"));

		this.updatedOn = (String)map.get("updated_on");

		this.user = User.create((Map<String,Object>)map.get("user"));
		
	}
}
