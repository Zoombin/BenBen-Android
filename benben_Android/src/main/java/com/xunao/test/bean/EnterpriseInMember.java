package com.xunao.test.bean;

import org.json.JSONObject;

import com.xunao.test.base.BaseBean;
import com.xunao.test.exception.NetRequestException;

public class EnterpriseInMember extends BaseBean<EnterpriseInMember> {
	private String id;
	private String contactsId;
	private String memberId;
	private String shortPhone;
	private String remarkName;
	private String createdTime;
	private String phone;
	private String name;
	private String inviteId;
	
	
	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public EnterpriseInMember parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		id = jsonObj.optString("id");
		contactsId = jsonObj.optString("contact_id");
		memberId = jsonObj.optString("member_id");
		shortPhone = jsonObj.optString("short_phone");
		remarkName = jsonObj.optString("remark_name");
		createdTime = jsonObj.optString("created_time");
		phone = jsonObj.optString("phone");
		name = jsonObj.optString("name");
		inviteId = jsonObj.optString("invite_id");
		return this;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContactsId() {
		return contactsId;
	}

	public void setContactsId(String contactsId) {
		this.contactsId = contactsId;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getShortPhone() {
		return shortPhone;
	}

	public void setShortPhone(String shortPhone) {
		this.shortPhone = shortPhone;
	}

	public String getRemarkName() {
		return remarkName;
	}

	public void setRemarkName(String remarkName) {
		this.remarkName = remarkName;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
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

	public String getInviteId() {
		return inviteId;
	}

	public void setInviteId(String inviteId) {
		this.inviteId = inviteId;
	}

}
