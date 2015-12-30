package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Transient;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class ContactsTemp extends BaseBean<ContactsTemp> implements Comparable {

	@Id
	private String id;

	private String group_id;
	private String name;
	private String is_benben = "0";
	private String is_baixing = "0";
	private String pinyin;
	private String huanxin_username;
	private String poster;
	private String is_friend;
	private boolean isChecked = true;
	// private boolean isCollection;
	@Transient
	private boolean hasPinYin; // 记录拼音出现的位置

	@Transient
	private ArrayList<PhoneInfo> phones = new ArrayList<PhoneInfo>();

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContactsTemp parseJSON(JSONObject jsonObj)
			throws NetRequestException {

		id = jsonObj.optString("id");
		group_id = jsonObj.optString("group_id");
		name = jsonObj.optString("name");
		pinyin = jsonObj.optString("pinyin");
		huanxin_username = jsonObj.optString("huanxin_username");
		poster = jsonObj.optString("poster");
		// isCollection = jsonObj.opt("isCollection");

		JSONArray phoneArray = jsonObj.optJSONArray("phone");
		if (phoneArray != null && phoneArray.length() > 0) {
			for (int i = 0; i < phoneArray.length(); i++) {
				JSONObject jsonObject = phoneArray.optJSONObject(i);
				PhoneInfo phone = new PhoneInfo(name);
				phone.setContacts_id(Integer.parseInt(id));
				phone.parseJSON(jsonObject);
				phones.add(phone);
			}
		}

		return this;
	}

	public ContactsTemp parseJSONSingle(JSONObject jsonObj)
			throws NetRequestException {
		checkJson(jsonObj);
		jsonObj = jsonObj.optJSONObject("user");
		id = jsonObj.optString("contact_info_id");
		group_id = jsonObj.optString("group_id");
		name = jsonObj.optString("name");
		// is_benben = jsonObj.optString("is_benben");
		// is_baixing = jsonObj.optString("is_baixing");
		pinyin = jsonObj.optString("pinyin");
		huanxin_username = jsonObj.optString("huanxin_username");
		poster = jsonObj.optString("poster");
		is_friend = jsonObj.optString("is_friend");
		// isCollection = jsonObj.opt("isCollection");

		JSONArray phoneArray = jsonObj.optJSONArray("phone");
		if (phoneArray != null && phoneArray.length() > 0) {
			for (int i = 0; i < phoneArray.length(); i++) {
				JSONObject jsonObject = phoneArray.optJSONObject(i);
				PhoneInfo phone = new PhoneInfo(name);
				phone.parseJSON(jsonObject);
				// phone.setContacts_id(Integer.parseInt(id));
				phone.setIs_baixing(is_baixing);
				phones.add(phone);
			}
		}

		return this;
	}

	public ContactsTemp parseJSONSingle2(JSONObject jsonObj)
			throws NetRequestException {
		checkJson(jsonObj);
		jsonObj = jsonObj.optJSONObject("friend_info");
		id = jsonObj.optString("id");
		group_id = jsonObj.optString("group_id");
		name = jsonObj.optString("name");
		pinyin = jsonObj.optString("pinyin");
		huanxin_username = jsonObj.optString("huanxin_username");
		poster = jsonObj.optString("poster");
		// isCollection = jsonObj.opt("isCollection");

		JSONArray phoneArray = jsonObj.optJSONArray("phone");
		if (phoneArray != null && phoneArray.length() > 0) {
			for (int i = 0; i < phoneArray.length(); i++) {
				JSONObject jsonObject = phoneArray.optJSONObject(i);
				PhoneInfo phone = new PhoneInfo(name);
				phone.parseJSON(jsonObject);
				phone.setContacts_id(Integer.parseInt(id));
				phone.setIs_baixing(is_baixing);
				phones.add(phone);
			}
		}

		return this;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	// public boolean isCollection() {
	// return isCollection;
	// }
	//
	// public void setCollection(boolean isCollection) {
	// this.isCollection = isCollection;
	// }

	public String getGroup_id() {
		return group_id;
	}

	public String getIs_friend() {
		return is_friend;
	}

	public void setIs_friend(String is_friend) {
		this.is_friend = is_friend;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIs_benben() {
		return is_benben;
	}

	public void setIs_benben(String is_benben) {
		this.is_benben = is_benben;
	}

	public String getIs_baixing() {
		return is_baixing;
	}

	public void setIs_baixing(String is_baixing) {
		this.is_baixing = is_baixing;
	}

	public ArrayList<PhoneInfo> getPhones() {
		return phones;
	}

	public void setPhones(ArrayList<PhoneInfo> phones) {
		this.phones = phones;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public boolean isHasPinYin() {
		return hasPinYin;
	}

	public void setHasPinYin(boolean hasPinYin) {
		this.hasPinYin = hasPinYin;
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

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	// 按照拼音排序用的
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		ContactsTemp s = (ContactsTemp) o;
		// return num > s.num ? 1 : (num == s.num ? 0 : -1);
		return pinyin.charAt(0) > s.getPinyin().charAt(0) ? 1 : (pinyin
				.charAt(0) == s.getPinyin().charAt(0) ? (pinyin.charAt(1) > s
				.getPinyin().charAt(1) ? 1 : (pinyin.charAt(1) == s.getPinyin()
				.charAt(1) ? 0 : -1)) : -1);
	}

	// 复写比较方法 用于删除
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		// return super.equals(o);
		ContactsTemp contacts = (ContactsTemp) o;
		if (this.getId().equals(contacts.getId())) {
			return true;
		} else {
			return false;
		}
	}
}
