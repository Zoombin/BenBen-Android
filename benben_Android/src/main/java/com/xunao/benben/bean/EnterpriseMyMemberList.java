package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class EnterpriseMyMemberList extends BaseBean<EnterpriseMyMemberList> {
	private ArrayList<EnterpriseInMember> members;
	
	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EnterpriseMyMemberList parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		checkJson(jsonObj);

		JSONArray optJSONArray = jsonObj.optJSONArray("member_list");
		
		if (optJSONArray != null) {
			int length = optJSONArray.length();
			members = new ArrayList<EnterpriseInMember>();
			EnterpriseInMember friendData;
			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					friendData = new EnterpriseInMember();
					friendData.parseJSON(optJSONObject);
					members.add(friendData);
				}
			}

		}
		
		return this;
	}

	public ArrayList<EnterpriseInMember> getMembers() {
		return members;
	}

	public void setMembers(ArrayList<EnterpriseInMember> members) {
		this.members = members;
	}

}
