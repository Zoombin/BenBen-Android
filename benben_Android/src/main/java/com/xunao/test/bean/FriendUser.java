package com.xunao.test.bean;

import org.json.JSONObject;

import com.xunao.test.base.BaseBean;
import com.xunao.test.exception.NetRequestException;

public class FriendUser extends BaseBean {

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	private String nickName;
	private String poster;
	private String huanxin_username;
	private long created_time;
	private String benben_id;


	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public String getHuanxin_username() {
		return huanxin_username;
	}

	public void setHuanxin_username(String huanxin_username) {
		this.huanxin_username = huanxin_username;
	}

	public long getCreated_time() {
		return created_time;
	}

	public void setCreated_time(long created_time) {
		this.created_time = created_time;
	}

	public String getBenben_id() {
		return benben_id;
	}

	public void setBenben_id(String benben_id) {
		this.benben_id = benben_id;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object parseJSON(JSONObject jsonObj) throws NetRequestException {

		id = jsonObj.optString("id");
		created_time = jsonObj.optLong("created_time");
		nickName = jsonObj.optString("nick_name");
		poster = jsonObj.optString("poster");
		benben_id = jsonObj.optString("benben_id");
		huanxin_username = jsonObj.optString("huanxin_username");

		return this;
	}

}
