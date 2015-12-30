package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class FriendUnionList extends BaseBean {
	private ArrayList<FriendUnion> friendUnions;
	
	public ArrayList<FriendUnion> getFriendUnions() {
		return friendUnions;
	}

	public void setFriendUnions(ArrayList<FriendUnion> friendUnions) {
		this.friendUnions = friendUnions;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public Object parseJSON(JSONObject jsonObj) throws NetRequestException {
		checkJson(jsonObj);

		JSONArray optJSONArray = jsonObj.optJSONArray("enterprise_list");

		if (optJSONArray != null) {
			int length = optJSONArray.length();
			friendUnions = new ArrayList<FriendUnion>();
			FriendUnion friendData;
			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					friendData = new FriendUnion();
					friendData.parseJSON(optJSONObject);
					friendUnions.add(friendData);
				}
			}

		}

		return this;
	}

}
