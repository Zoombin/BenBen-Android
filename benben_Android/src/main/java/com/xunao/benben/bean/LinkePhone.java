package com.xunao.benben.bean;

import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class LinkePhone extends BaseBean {

	private int id;
	private String phone;
	private String name;
	private String is_benben;
	private String is_baixing;
	private String poster;
	private String nick_name;
	private int contactId;

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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

	public int getContactId() {
		return contactId;
	}

	public void setContactId(int contactId) {
		this.contactId = contactId;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object parseJSON(JSONObject jsonObj) throws NetRequestException {
		// TODO Auto-generated method stub
		return null;
	}

}
