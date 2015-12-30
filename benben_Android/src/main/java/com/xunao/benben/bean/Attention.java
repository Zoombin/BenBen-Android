package com.xunao.benben.bean;

import org.json.JSONObject;

import com.lidroid.xutils.db.annotation.Id;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class Attention extends BaseBean {

	@Id
	private String id;
	private String nickName;
	private String poster;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object parseJSON(JSONObject jsonObj) throws NetRequestException {
		id = jsonObj.optString("creation_auth_id");
		nickName = jsonObj.optString("nick_name");
		poster = jsonObj.optString("poster");
		return this;
	}

}
