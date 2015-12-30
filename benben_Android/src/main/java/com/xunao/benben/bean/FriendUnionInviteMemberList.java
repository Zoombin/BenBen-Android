package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class FriendUnionInviteMemberList extends
		BaseBean<FriendUnionInviteMemberList> {
	private ArrayList<FriendUnionMemberGroup> members;

	public ArrayList<FriendUnionMemberGroup> getMembers() {
		return members;
	}

	public void setMembers(ArrayList<FriendUnionMemberGroup> members) {
		this.members = members;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FriendUnionInviteMemberList parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		checkJson(jsonObj);
		JSONArray optJSONArray = jsonObj.optJSONArray("member_list");
		if (optJSONArray != null) {
			int length = optJSONArray.length();
			members = new ArrayList<FriendUnionMemberGroup>();
			FriendUnionMemberGroup member;

			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					member = new FriendUnionMemberGroup();
					member.parseJSON(optJSONObject);
					members.add(member);
				}
			}

		}
		return this;
	}

}
