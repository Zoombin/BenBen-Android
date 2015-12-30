package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class MemberListByArea extends BaseBean<MemberListByArea> {
	private ArrayList<Contacts> contactsList;
	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public MemberListByArea parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		checkJson(jsonObj);

		JSONArray optJSONArray = jsonObj.optJSONArray("member_list");

		if (optJSONArray != null && optJSONArray.length() > 0) {
			contactsList = new ArrayList<Contacts>();
			Contacts eMember;
			for (int i = 0; i < optJSONArray.length(); i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					eMember = new Contacts();
					eMember.parseJSONBroadCast(optJSONObject);
					contactsList.add(eMember);
				}
			}
		}
		return this;
	}

	public ArrayList<Contacts> getContactsList() {
		return contactsList;
	}

	public void setContactsList(ArrayList<Contacts> contactsList) {
		this.contactsList = contactsList;
	}

}
