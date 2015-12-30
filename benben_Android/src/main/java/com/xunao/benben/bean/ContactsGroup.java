package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONObject;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Transient;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class ContactsGroup extends BaseBean<ContactsGroup> implements Cloneable {

	@Id
	@NoAutoIncrement
	private int id;
	private String name;
	private String created_time;
    private int sort;

	@Transient
	private boolean isOpen; // 是否开闭合
	@Transient
	private boolean isSelect  = false; // 是否选中
	@Transient
	private String proportion; // 头部的比例

	private ArrayList<Contacts> mContacts = new ArrayList<Contacts>();

	public ArrayList<Contacts> getmContacts() {
		return mContacts;
	}

	public void setmContacts(ArrayList<Contacts> mContacts) {
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

	@Override
	public ContactsGroup parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		id = jsonObj.optInt("id");
		name = jsonObj.optString("name");
        sort = jsonObj.optInt("sort");
		created_time = jsonObj.optString("created_time");
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

		final ContactsGroup contactsGroup = (ContactsGroup) o;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

	public String getCreated_time() {
		return created_time;
	}

	public void setCreated_time(String created_time) {
		this.created_time = created_time;
	}

	public String getProportion() {
		return proportion;
	}

	public void setProportion(String proportion) {
		this.proportion = proportion;
	}

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
