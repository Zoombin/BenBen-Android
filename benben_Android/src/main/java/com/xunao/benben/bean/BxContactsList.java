package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class BxContactsList extends BaseBean {
	private ArrayList<BxContactsAll> arrayList = new ArrayList<BxContactsAll>();

	public ArrayList<BxContactsAll> getBxContacts() {
		return arrayList;
	}

	public void setBxContacts(ArrayList<BxContactsAll> arrayList) {
		this.arrayList = arrayList;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public Object parseJSON(JSONObject jsonObj) throws NetRequestException {
		checkJson(jsonObj);
		JSONArray optJSONArray = jsonObj.optJSONArray("contact");

		if (optJSONArray != null) {
			int length = optJSONArray.length();
			arrayList = new ArrayList<BxContactsAll>();
			BxContactsAll bxContacts;

			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					bxContacts = new BxContactsAll();
					bxContacts.parseJSON(optJSONObject);
					arrayList.add(bxContacts);
				}
			}

		}

		return this;
	}

}
