package com.xunao.benben.bean;

import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class EnterpriseGroup extends BaseBean<EnterpriseGroup> {
	private String id;
	private String groupName;
	private String createdTime;
	private boolean isSelect;
	private String allNum;
	
	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public EnterpriseGroup parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		checkJson(jsonObj);
		id = jsonObj.optString("id");
		groupName = jsonObj.optString("groupname");
		createdTime = jsonObj.optString("created_time");
		allNum = jsonObj.optString("all_num");
		return this;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public String getAllNum() {
		return allNum;
	}

	public void setAllNum(String allNum) {
		this.allNum = allNum;
	}

}
