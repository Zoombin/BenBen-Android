package com.xunao.test.bean;

import org.json.JSONObject;

import com.xunao.test.base.BaseBean;
import com.xunao.test.exception.NetRequestException;

public class BxApplyInfo extends BaseBean {

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private String name;
	private int status;
	private String idCard;
	private String area;
	private String poster;
	private String poster2;
	private String reason;
	private String phone;
	private String short_phone;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public String getPoster2() {
		return poster2;
	}

	public void setPoster2(String poster2) {
		this.poster2 = poster2;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getShort_phone() {
		return short_phone;
	}

	public void setShort_phone(String short_phone) {
		this.short_phone = short_phone;
	}

	@Override
	public Object parseJSON(JSONObject jsonObj) throws NetRequestException {
		name = jsonObj.optString("name");
		status = jsonObj.optInt("status");
		short_phone = jsonObj.optString("short_phone");
		idCard = jsonObj.optString("id_card");
		phone = jsonObj.optString("phone");
		area = jsonObj.optString("pro_city");
		poster = jsonObj.optString("poster1");
		poster2 = jsonObj.optString("poster2");
		reason = jsonObj.optString("reason");

		return this;
	}

}
