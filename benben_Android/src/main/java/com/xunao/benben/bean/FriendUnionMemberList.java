package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class FriendUnionMemberList extends BaseBean<FriendUnionMemberList>{
	private ArrayList<FriendUnionGroup>friendUnionGroup;
	public ArrayList<FriendUnionGroup> getFriendUnionGroup() {
		return friendUnionGroup;
	}

	public void setFriendUnionGroup(ArrayList<FriendUnionGroup> friendUnionGroup) {
		this.friendUnionGroup = friendUnionGroup;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public FriendUnionMemberList parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		checkJson(jsonObj);

		JSONArray optJSONArray = jsonObj.optJSONArray("member_info");

		if (optJSONArray != null) {
			int length = optJSONArray.length();
			friendUnionGroup = new ArrayList<FriendUnionGroup>();
			FriendUnionGroup unionGroup = null;
			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					unionGroup = new FriendUnionGroup();
					unionGroup.parseJSON(optJSONObject);
					friendUnionGroup.add(unionGroup);
				}
			}
		}
		return this;
	}

}
