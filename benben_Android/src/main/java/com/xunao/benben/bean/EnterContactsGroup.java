package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Transient;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class EnterContactsGroup extends BaseBean<EnterContactsGroup> implements
		Cloneable {

	@Id
	@NoAutoIncrement
	private int id;
	private String groupname;
	private int number;
	@Transient
	private boolean isOpen; // 是否开闭合
	@Transient
	private boolean isSelect; // 是否选中
	@Transient
	private String proportion; // 头部的比例

	private ArrayList<EnterContacts> mContacts = new ArrayList<EnterContacts>();

	public ArrayList<EnterContacts> getmContacts() {
		return mContacts;
	}

	public void setmContacts(ArrayList<EnterContacts> mContacts) {
		this.mContacts = mContacts;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	@Override
	public EnterContactsGroup parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		id = jsonObj.optInt("id");
		groupname = jsonObj.optString("groupname");
		number = jsonObj.optInt("number");

		JSONArray optJSONArray = jsonObj.optJSONArray("member_info");
		if (optJSONArray != null) {
			EnterContacts contacts;
			for (int i = 0; i < optJSONArray.length(); i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					contacts = new EnterContacts();
					contacts.parseJSON(optJSONObject);
					contacts.setGroupid(id);
					mContacts.add(contacts);
				}
			}

		}

		return this;
	}

	// 复写比较方法 用于删除
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		// if (this == o) // 先检查是否其自反性，后比较other是否为空。这样效率高
		// return true;
		// if (o == null)
		// return false;

		final EnterContactsGroup contactsGroup = (EnterContactsGroup) o;
		if (contactsGroup.getId() == this.getId()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

	public String getProportion() {
		return proportion;
	}

	public void setProportion(String proportion) {
		this.proportion = proportion;
	}

}
