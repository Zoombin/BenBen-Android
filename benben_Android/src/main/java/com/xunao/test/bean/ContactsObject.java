package com.xunao.test.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.test.base.BaseBean;
import com.xunao.test.exception.NetRequestException;

public class ContactsObject extends BaseBean<ContactsObject> implements
		Cloneable {

	private ArrayList<ContactsGroup> mContactsGroups = new ArrayList<ContactsGroup>();
	private ArrayList<Contacts> mContactss = new ArrayList<Contacts>();

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
	public ContactsObject parseJSON(JSONObject jsonObj)
			throws NetRequestException {

		checkJson(jsonObj);

		JSONArray contactGroup = jsonObj.optJSONArray("group");
		JSONArray contact = jsonObj.optJSONArray("contact");

		if (contactGroup != null && contactGroup.length() > 0) {
			ContactsGroup contg = null;
			JSONObject jsonObject;
			for (int i = 0; i < contactGroup.length(); i++) {
				contg = new ContactsGroup();
				jsonObject = contactGroup.optJSONObject(i);
				contg.parseJSON(jsonObject);
				mContactsGroups.add(contg);
			}
		}

		if (contact != null && contact.length() > 0) {
			Contacts cont = null;
			JSONObject jsonObject;
			for (int i = 0; i < contact.length(); i++) {
				cont = new Contacts();
				jsonObject = contact.optJSONObject(i);
				cont.parseJSON(jsonObject);
                mContactss.add(cont);
				for (ContactsGroup cg : mContactsGroups) {
					if ((cg.getId() + "").equalsIgnoreCase(cont.getGroup_id())) {
						cg.getmContacts().add(cont);

						break;
					}
				}

			}
		}

		return this;
	}

	public ContactsObject contactsSynchroparseJSON(JSONObject jsonObj)
			throws NetRequestException {

		checkJson(jsonObj);

		JSONArray contact = jsonObj.optJSONArray("contact");

		if (contact != null && contact.length() > 0) {
			Contacts cont = null;
			JSONObject jsonObject;
			for (int i = 0; i < contact.length(); i++) {
				cont = new Contacts();
				jsonObject = contact.optJSONObject(i);
				cont.parseJSON(jsonObject);
				mContactss.add(cont);
			}
		}

		return this;
	}

	public ArrayList<Contacts> getmContactss() {
		return mContactss;
	}

	public void setmContactss(ArrayList<Contacts> mContactss) {
		this.mContactss = mContactss;
	}

	public ArrayList<ContactsGroup> getmContactsGroups() {
		return mContactsGroups;
	}

	public void setmContactsGroups(ArrayList<ContactsGroup> mContactsGroups) {
		this.mContactsGroups = mContactsGroups;
	}

}
