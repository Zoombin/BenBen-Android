package com.xunao.benben.bean.talkgroup;

import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class GroupMember extends BaseBean {

	private int id;
	private int sex;
	private int age;
	private int isAdmin;
	private String phone;
	private String huanxin_username;
	private String group_nick_name;
	private String poster;
	private String nick_name;

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object parseJSON(JSONObject jsonObj) throws NetRequestException {
		
		id = jsonObj.optInt("id");
		sex = jsonObj.optInt("sex");
		age = jsonObj.optInt("age");
		phone = jsonObj.optString("phone");
		huanxin_username = jsonObj.optString("huanxin_username");
		group_nick_name = jsonObj.optString("group_nick_name");
		poster = jsonObj.optString("poster");
		nick_name = jsonObj.optString("nick_name");
		isAdmin = jsonObj.optInt("is_admin");

		return this;
	}

	public int getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(int isAdmin) {
		this.isAdmin = isAdmin;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
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

	public String getGroup_nick_name() {
		return group_nick_name;
	}

	public void setGroup_nick_name(String group_nick_name) {
		this.group_nick_name = group_nick_name;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

}
