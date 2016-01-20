package com.xunao.test.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.xunao.test.base.BaseBean;
import com.xunao.test.exception.NetRequestException;

public class BxContactsAll extends BaseBean<BxContactsAll> {

	@Id
	@NoAutoIncrement
	private int id;
	private boolean checked = false;
	private String name;
	private ArrayList<BxContacts> BxContactslist=new ArrayList<BxContacts>();

	public ArrayList<BxContacts> getBxContactslist() {
		return BxContactslist;
	}

	public void setBxContactslist(ArrayList<BxContacts> bxContactslist) {
		BxContactslist = bxContactslist;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public JSONObject toJSON() {

		return null;
	}

	@Override
	public BxContactsAll parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		name = jsonObj.optString("name");
		id = jsonObj.optInt("id");
		JSONArray optJSONArray = jsonObj.optJSONArray("member");

		if (optJSONArray != null) {
			int length = optJSONArray.length();
			BxContactslist = new ArrayList<BxContacts>();
			BxContacts bxContacts;
			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					bxContacts = new BxContacts();
					bxContacts.parseJSON(optJSONObject);
					BxContactslist.add(bxContacts);
				}
			}

		}
		return this;
	}

}
