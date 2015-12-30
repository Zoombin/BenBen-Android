package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class FriendUnionMemberGroup extends BaseBean<FriendUnionMemberGroup> {
	private String id;
	private String groupName;
	private ArrayList<FriendUnionInviteMember> members;
	private boolean select;

	public boolean isSelect() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public ArrayList<FriendUnionInviteMember> getMember() {
		return members;
	}

	public void setMember(ArrayList<FriendUnionInviteMember> members) {
		this.members = members;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FriendUnionMemberGroup parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		checkJson(jsonObj);
		JSONArray optJSONArray = jsonObj.optJSONArray("member");
		groupName = jsonObj.optString("name");
		id = jsonObj.optString("id");

		if (optJSONArray != null) {
			int length = optJSONArray.length();
			members = new ArrayList<FriendUnionInviteMember>();
			FriendUnionInviteMember member;

			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					member = new FriendUnionInviteMember();
					member.parseJSON(optJSONObject);
					members.add(member);
				}
			}

		}
		return this;
	}

}
