package com.xunao.benben.bean;

import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class FriendUnionInviteMember extends BaseBean<FriendUnionInviteMember> {
	private String id;
	private String phone;
	private String name;
	private String pinyin;
	private String poster;
	private String benbenId;
	private boolean isChecked = false;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public String getBenbenId() {
		return benbenId;
	}

	public void setBenbenId(String benbenId) {
		this.benbenId = benbenId;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public FriendUnionInviteMember parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		id = jsonObj.optString("id");
		phone = jsonObj.optString("phone");
		pinyin = jsonObj.optString("pinyin");
		poster = jsonObj.optString("poster");
		name = jsonObj.optString("name");
		benbenId = jsonObj.optString("is_benben");
		return this;
	}

}
