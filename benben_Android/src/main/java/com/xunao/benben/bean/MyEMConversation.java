package com.xunao.benben.bean;

import org.json.JSONObject;

import com.easemob.chat.EMMessage;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class MyEMConversation extends BaseBean {

	private String groupId;
	@Id
	@NoAutoIncrement
	private int id;
	private String name;
	private String nick_name;
	private String sex;
	private String age;
	private String group_nick_name;
	private String phone;
	private String huanxin_username;
	private String poster;

	public String getGroup_nick_name() {
		return group_nick_name;
	}

	public void setGroup_nick_name(String group_nick_name) {
		this.group_nick_name = group_nick_name;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
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
		groupId = jsonObj.optString("groupId");
		id = jsonObj.optInt("id");
		name = jsonObj.optString("name");
		nick_name = jsonObj.optString("nick_name");
		sex = jsonObj.optString("sex");
		age = jsonObj.optString("age");
		group_nick_name = jsonObj.optString("group_nick_name");
		phone = jsonObj.optString("phone");
		huanxin_username = jsonObj.optString("huanxin_username");
		poster = jsonObj.optString("poster");
		return this;
	}

	public Object parseSingleJSON(JSONObject jsonObj)
			throws NetRequestException {
		checkJson(jsonObj);
		jsonObj = jsonObj.optJSONObject("user");
		id = jsonObj.optInt("benben_id");
		name = jsonObj.optString("name");
		nick_name = jsonObj.optString("nick_name");
		sex = jsonObj.optString("sex");
		age = jsonObj.optString("age");
		group_nick_name = jsonObj.optString("nick_name");
		phone = jsonObj.optString("phone");
		huanxin_username = jsonObj.optString("huanxin_username");
		poster = jsonObj.optString("poster");
		return this;
	}

}
