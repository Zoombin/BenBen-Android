package com.xunao.benben.bean;

import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class Happy extends BaseBean<Happy> {
	private String id;
	private String description;
	private String status;
	private String createdTime;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public Happy parseJSON(JSONObject jsonObj) throws NetRequestException {
		id = jsonObj.optString("id");
		description = jsonObj.optString("description");
		status = jsonObj.optString("status");
		createdTime = jsonObj.optString("created_time");
		return this;
	}

}
