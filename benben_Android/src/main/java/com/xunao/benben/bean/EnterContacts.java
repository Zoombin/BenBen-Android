package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Transient;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class EnterContacts extends BaseBean<EnterContacts> implements
		Comparable {
	@Id
	@NoAutoIncrement
	private int id;
	private int groupid;
	private String name;
	private int member_id;
	private String phone;
	private String shortPhone;
	private String pinyin;
	private boolean hasPinYin;

	@Transient
	private boolean isChecked = false;

	public boolean isHasPinYin() {
		return hasPinYin;
	}

	public void setHasPinYin(boolean hasPinYin) {
		this.hasPinYin = hasPinYin;
	}

	public int getGroupid() {
		return groupid;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
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

	public int getMember_id() {
		return member_id;
	}

	public void setMember_id(int member_id) {
		this.member_id = member_id;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getShortPhone() {
		return shortPhone;
	}

	public void setShortPhone(String shortPhone) {
		this.shortPhone = shortPhone;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EnterContacts parseJSON(JSONObject jsonObj)
			throws NetRequestException {

		id = jsonObj.optInt("id");
		member_id = jsonObj.optInt("member_id");
		name = jsonObj.optString("name");
		shortPhone = jsonObj.optString("short_phone");
		phone = jsonObj.optString("phone");
		pinyin = jsonObj.optString("pinyin");

		return this;
	}

	// 复写比较方法 用于删除
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		// return super.equals(o);
		EnterContacts contacts = (EnterContacts) o;
		if (this.getId() == contacts.getId()) {
			return true;
		} else {
			return false;
		}
	}

	// 按照拼音排序用的
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		Contacts s = (Contacts) o;
		// return num > s.num ? 1 : (num == s.num ? 0 : -1);
		return pinyin.charAt(0) - s.getPinyin().charAt(0);
	}

}
