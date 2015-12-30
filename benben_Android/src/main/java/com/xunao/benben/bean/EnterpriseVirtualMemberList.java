package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class EnterpriseVirtualMemberList extends
		BaseBean<EnterpriseVirtualMemberList> {
	private ArrayList<EnterpriseVirtualMemberGroup> members;

	public ArrayList<EnterpriseVirtualMemberGroup> getMembers() {
		return members;
	}

	public void setMembers(ArrayList<EnterpriseVirtualMemberGroup> members) {
		this.members = members;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public EnterpriseVirtualMemberList parseJSON(JSONObject jsonObj)
			throws NetRequestException {

		checkJson(jsonObj);
		JSONArray optJSONArray = jsonObj.optJSONArray("member_list");

		if (optJSONArray != null) {
			int length = optJSONArray.length();
			members = new ArrayList<EnterpriseVirtualMemberGroup>();
			EnterpriseVirtualMemberGroup enterprise;
			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					enterprise = new EnterpriseVirtualMemberGroup();
					enterprise.parseJSON(optJSONObject);
					members.add(enterprise);
				}
			}

		}
		return this;
	}

}
