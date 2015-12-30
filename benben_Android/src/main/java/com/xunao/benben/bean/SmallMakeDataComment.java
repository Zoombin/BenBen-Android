package com.xunao.benben.bean;

import org.json.JSONObject;

import com.lidroid.xutils.db.annotation.Id;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class SmallMakeDataComment extends BaseBean {
	private String id;
	private String nick_name;
	private String memberId;
	private String review;
	private String createdTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public Object parseJSON(JSONObject jsonObj) throws NetRequestException {
		id = jsonObj.optString("circle_id");
		memberId = jsonObj.optString("member_id");
		review = jsonObj.optString("review");
		createdTime = jsonObj.optString("created_time");
		nick_name = jsonObj.optString("nick_name");

		return this;
	}

}
