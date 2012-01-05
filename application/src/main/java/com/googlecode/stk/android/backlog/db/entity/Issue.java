package com.googlecode.stk.android.backlog.db.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import roboguice.util.Ln;

import com.google.common.base.Strings;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Issue extends BaseEntity implements Convertable {

	@DatabaseField
	public String key;

	@DatabaseField
	public String summary;

	@DatabaseField
	public String description;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "priority_id")
	public Priority priority;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "issue_type_id")
	public IssueType issueType;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "status_id")
	public Status status;

	@DatabaseField
	public String startDate;

	@DatabaseField
	public String createdOn;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "assigner_id")
	public User assigner;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "created_user_id")
	public User createdUser;

	public Date getCreatedOn() {
		if (Strings.isNullOrEmpty(createdOn)) {
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

		try {
			return sdf.parse(createdOn);
		} catch (ParseException e) {
			Ln.e(e, "パースに失敗");
			return null;
		}

	}

	public Date getStartDate() {
		if (Strings.isNullOrEmpty(startDate)) {
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

		try {
			return sdf.parse(startDate);
		} catch (ParseException e) {
			Ln.e(e, "パースに失敗");
			return null;
		}
	}

	public static Issue create(Map<String, Object> map) {

		Issue issue = new Issue();

		issue.set(map);

		return issue;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void set(Map<String, Object> map) {
		this.id = (Integer) map.get("id");

		this.key = (String) map.get("key");

		this.summary = (String) map.get("summary");

		this.description = (String) map.get("description");

		this.priority = Priority.create((Map<String, Object>) map.get("priority"));

		if (map.containsKey("created_on")) {
			this.createdOn = (String) map.get("created_on");
		}

		if (map.containsKey("issueType")) {
			this.issueType = IssueType.create((Map<String, Object>) map.get("issueType"));
		}

		if (map.containsKey("status")) {

			this.status = Status.create((Map<String, Object>) map.get("status"));
		}

		if (map.containsKey("assigner")) {

			this.assigner = User.create((Map<String, Object>) map.get("assigner"));
		}

		if (map.containsKey("created_user")) {
			this.createdUser = User.create((Map<String, Object>) map.get("created_user"));
		}
	}

}
