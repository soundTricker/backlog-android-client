package com.googlecode.stk.android.backlog.db.entity;

import java.util.Map;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class UserIcon extends BaseEntity implements Convertable {

	@DatabaseField
	public String contentType;

	@DatabaseField(dataType = DataType.BYTE_ARRAY)
	public byte[] data;

	@DatabaseField
	public String updatedOn;

	@DatabaseField
	public String dataPath;

	public static UserIcon create(Map<String,Object> map) {
		UserIcon userIcon = new UserIcon();

		userIcon.set(map);

		return userIcon;
	}

	@Override
	public void set(Map<String, Object> map) {
		this.id = (Integer)map.get("id");

		this.contentType = (String)map.get("contentType");

		this.data = (byte[])map.get("data");

		this.updatedOn = (String)map.get("updated_on");
	}

}
