package com.xunao.benben.bean;

import org.json.JSONObject;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class BxApplyProgress extends BaseBean<BxApplyProgress> {

	@Id
	@NoAutoIncrement
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private String name;
	private String phone;
	private String address;
	private String createdTime;
	private String status;
	private String short_phone;

	public String getShort_phone() {
		return short_phone;
	}

	public void setShort_phone(String short_phone) {
		this.short_phone = short_phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public BxApplyProgress parseJSON(JSONObject jsonObj)
			throws NetRequestException {

		id = jsonObj.optInt("id");
		name = jsonObj.optString("name");
		phone = jsonObj.optString("phone");
		address = jsonObj.optString("address");
		short_phone = jsonObj.optString("short_phone");
		createdTime = jsonObj.optString("date");
		status = jsonObj.optString("status");

		return this;

	}

}
