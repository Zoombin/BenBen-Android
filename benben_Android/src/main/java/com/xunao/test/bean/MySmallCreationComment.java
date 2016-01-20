package com.xunao.test.bean;

import org.json.JSONObject;

import com.xunao.test.base.BaseBean;
import com.xunao.test.exception.NetRequestException;

public class MySmallCreationComment extends BaseBean {

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private String circleId;
	private String nick_name;
	private String memberId;
	private String review;
	private String createdTime;

	public String getCircleId() {
		return circleId;
	}

	public void setCircleId(String circleId) {
		this.circleId = circleId;
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
		circleId = jsonObj.optString("circle_id");
		memberId = jsonObj.optString("member_id");
		review = jsonObj.optString("review");
		createdTime = jsonObj.optString("created_time");
		nick_name = jsonObj.optString("nick_name");

		return this;
	}

}
