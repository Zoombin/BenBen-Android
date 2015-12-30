package com.xunao.benben.bean;

import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class FriendUnionChief extends BaseBean<FriendUnionChief> {
	private String id;
	private String memberId;
	private String nickName;
	private String poster;
	private String benbenId;
	private String type;
	private String remarkName;
	private String huanxinUserName;

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public FriendUnionChief parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		id = jsonObj.optString("id");
		memberId = jsonObj.optString("member_id");
		nickName = jsonObj.optString("nick_name");
		poster = jsonObj.optString("poster");
		benbenId = jsonObj.optString("benben_id");
		type = jsonObj.optString("type");
		remarkName = jsonObj.optString("remark_name");
		huanxinUserName = jsonObj.optString("huanxin_username");
		return this;
	}

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

	public String getBenbenId() {
		return benbenId;
	}

	public void setBenbenId(String benbenId) {
		this.benbenId = benbenId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRemarkName() {
		return remarkName;
	}

	public void setRemarkName(String remarkName) {
		this.remarkName = remarkName;
	}

	public String getHuanxinUserName() {
		return huanxinUserName;
	}

	public void setHuanxinUserName(String huanxinUserName) {
		this.huanxinUserName = huanxinUserName;
	}

}
