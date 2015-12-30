package com.xunao.benben.bean;

import org.json.JSONObject;

import com.lidroid.xutils.db.annotation.Transient;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class FriendUnionMember extends BaseBean<FriendUnionMember> {
	private String id;
	private String memberId;
	private String nickName;
	private String poster;
	private String title;
	private String type;
	private String phone;
	private String remark;
	private int size = 0;
	private String benbenId;
	private String huanxinUserName;

    @Transient
    private boolean isChecked = false;
	

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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getBenbenId() {
		return benbenId;
	}

	public void setBenbenId(String benbenId) {
		this.benbenId = benbenId;
	}

	public String getHuanxinUserName() {
		return huanxinUserName;
	}

	public void setHuanxinUserName(String huanxinUserName) {
		this.huanxinUserName = huanxinUserName;
	}

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    @Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public FriendUnionMember parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		id = jsonObj.optString("id");
		memberId = jsonObj.optString("member_id");
		nickName = jsonObj.optString("nick_name");
		poster = jsonObj.optString("poster");
		phone = jsonObj.optString("phone");
		remark = jsonObj.optString("remark_name");
		benbenId = jsonObj.optString("benben_id");
		type = jsonObj.optString("type");
		huanxinUserName = jsonObj.optString("huanxin_username");
		return this;
	}

}
