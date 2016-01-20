package com.xunao.test.bean;

import org.json.JSONObject;

import com.xunao.test.base.BaseBean;
import com.xunao.test.exception.NetRequestException;

public class News extends BaseBean {

	private String id;
	private int type;
	private String sender;
	private String poster;
	private String content;
	private int identity1;
	private int identity2;
	private long created_time;
	private int status;
	private int senderId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getSenderId() {
		return senderId;
	}

	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}

	/***
	 * 类型:1公告,2通知,3小喇叭,4消息
	 * 
	 * @return
	 */
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getCreated_time() {
		return created_time;
	}

	public void setCreated_time(long created_time) {
		this.created_time = created_time;
	}

	public int getIdentity1() {
		return identity1;
	}

	public void setIdentity1(int identity1) {
		this.identity1 = identity1;
	}

	public int getIdentity2() {
		return identity2;
	}

	public void setIdentity2(int identity2) {
		this.identity2 = identity2;
	}

	/**
	 * 0 未读 1已读 2政企已同意
	 * 
	 * @return
	 */
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object parseJSON(JSONObject jsonObj) throws NetRequestException {

		id = jsonObj.optString("id");
		type = jsonObj.optInt("type");
		sender = jsonObj.optString("sender");
		senderId = jsonObj.optInt("sender_id");
		poster = jsonObj.optString("poster");
		content = jsonObj.optString("content");
		identity1 = jsonObj.optInt("identity1");
		identity2 = jsonObj.optInt("identity2");
		created_time = jsonObj.optLong("created_time");
		status = jsonObj.optInt("status");

		return this;
	}

}
