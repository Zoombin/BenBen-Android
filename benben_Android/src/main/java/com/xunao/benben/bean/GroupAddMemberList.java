package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class GroupAddMemberList extends BaseBean {

	ArrayList<GroupAddMembers> mGroupAddMember = new ArrayList<GroupAddMembers>();

	public ArrayList<GroupAddMembers> getmGroupAddMember() {
		return mGroupAddMember;
	}

	public void setmGroupAddMember(ArrayList<GroupAddMembers> mGroupAddMember) {
		this.mGroupAddMember = mGroupAddMember;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object parseJSON(JSONObject jsonObj) throws NetRequestException {

		checkJson(jsonObj);

		JSONArray optJSONArray = jsonObj.optJSONArray("member_list");

		if (optJSONArray != null && optJSONArray.length() > 0) {
			mGroupAddMember = new ArrayList<GroupAddMembers>();
			GroupAddMembers member = null;
			for (int i = 0; i < optJSONArray.length(); i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					member = new GroupAddMembers();
					member.parseJSON(optJSONObject);
					mGroupAddMember.add(member);
				}
			}

		}

		return this;
	}

}
