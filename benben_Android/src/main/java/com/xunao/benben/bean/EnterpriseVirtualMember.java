package com.xunao.benben.bean;

import org.json.JSONObject;

import android.R.bool;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class EnterpriseVirtualMember extends BaseBean<EnterpriseVirtualMember> {
	private String id;
	private String phone;
	private String name;
	private String pinyin;
	private String poster;
	private boolean ischecked = false;
	private String remarkName;
	
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

	public boolean isIschecked() {
		return ischecked;
	}

	public void setIschecked(boolean ischecked) {
		this.ischecked = ischecked;
	}

	public String getRemarkName() {
		return remarkName;
	}

	public void setRemarkName(String remarkName) {
		this.remarkName = remarkName;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public EnterpriseVirtualMember parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		id = jsonObj.optString("id");
		phone = jsonObj.optString("phone");
		name = jsonObj.optString("name");
		pinyin = jsonObj.optString("pinyin");
		poster = jsonObj.optString("poster");
		return this;
	}

}
