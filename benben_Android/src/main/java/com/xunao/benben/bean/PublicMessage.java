package com.xunao.benben.bean;

import org.json.JSONObject;

import android.text.TextUtils;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Transient;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class PublicMessage extends BaseBean {

	// 请求同意
	@Transient
	public static final int AGREE = 0;
	// 请求未同意
	@Transient
	public static final int UNAGREE = 1;
    // 请求未同意
    @Transient
    public static final int REFUSE = 2;

	@Transient
	public static final int LOOKED = 1;
	@Transient
	public static final int UNLOOK = 0;

	// 好友请求
	@Transient
	public static final int FRIEND = 0;
	// 群组请求
	@Transient
	public static final int GROUP = 1;
    @Transient
    public static final int Union = 2;
    @Transient
    public static final int GROUPCHANGE = 3;
	@Transient
	public static final int NUMBERTRAIN_CHANGE = 6;
	@Id
	private int id;
	private int sid;
	private int classType;
	private int isFriend;
	private String name;
	private String nick_name;
	private String poster;
	private String phone;
	private String huanxin_username;
	private String huanxin_username_joiner;
	private int status;
	private int isLook;
	private long creatTime;
    private String reason;
    private String news_id;

	//号码直通车转让信息
	String store_id;
	String store_name;
	String vip_account;

	public int getIsLook() {
		return isLook;
	}

	public void setIsLook(int isLook) {
		this.isLook = isLook;
	}

	public long getCreatTime() {
		return creatTime;
	}

	public void setCreatTime(long creatTime) {
		this.creatTime = creatTime;
	}

	public String getHuanxin_username_joiner() {
		return huanxin_username_joiner;
	}

	public void setHuanxin_username_joiner(String huanxin_username_joiner) {
		this.huanxin_username_joiner = huanxin_username_joiner;
	}

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public int getClassType() {
		return classType;
	}

	public void setClassType(int classType) {
		this.classType = classType;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIsFriend() {
		return isFriend;
	}

	public void setIsFriend(int isFriend) {
		this.isFriend = isFriend;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getHuanxin_username() {
		return huanxin_username;
	}

	public void setHuanxin_username(String huanxin_username) {
		this.huanxin_username = huanxin_username;
	}

    public String getNews_id() {
        return news_id;
    }

    public void setNews_id(String news_id) {
        this.news_id = news_id;
    }

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public Object parseJSON(JSONObject jsonObj) throws NetRequestException {

		sid = jsonObj.optInt("id");
		isFriend = jsonObj.optInt("is_friend");
		name = jsonObj.optString("name");
		nick_name = jsonObj.optString("nick_name");
		poster = jsonObj.optString("poster");
		phone = jsonObj.optString("phone");
		huanxin_username = jsonObj.optString("huanxin_username");
		status = UNAGREE;
		isLook = UNLOOK;
		if (TextUtils.isEmpty(huanxin_username)) {
			classType = GROUP;
		} else {
			classType = FRIEND;
		}

		return this;
	}

	@Override
	public boolean equals(Object o) {

		PublicMessage oth = (PublicMessage) o;

		return this.getHuanxin_username().equals(oth.getHuanxin_username());
	}

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

	public String getStore_id() {
		return store_id;
	}

	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}

	public String getStore_name() {
		return store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	public String getVip_account() {
		return vip_account;
	}

	public void setVip_account(String vip_account) {
		this.vip_account = vip_account;
	}

}
