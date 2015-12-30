package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class FriendUnionGroup extends BaseBean<FriendUnionGroup> {
	private String name;
	private ArrayList<FriendUnionMember> unionMembers;
	private String type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<FriendUnionMember> getUnionMembers() {
		return unionMembers;
	}

	public void setUnionMembers(ArrayList<FriendUnionMember> unionMembers) {
		this.unionMembers = unionMembers;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public FriendUnionGroup parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		name = jsonObj.optString("name");
		checkJson(jsonObj);
		JSONArray optJSONArray = jsonObj.optJSONArray("member");
		if (optJSONArray != null) {
			int length = optJSONArray.length();
			unionMembers = new ArrayList<FriendUnionMember>();
			FriendUnionMember unionMember = null;
			for (int i = 0; i <= length; i++) {
				unionMember = new FriendUnionMember();
				if (i == 0) {
					unionMember.setTitle(name);
					unionMember.setType("0");
					unionMember.isCancle = false;
					unionMembers.add(unionMember);
					unionMember.setSize(length);
				} else {
					JSONObject optJSONObject = optJSONArray
							.optJSONObject(i - 1);
					if (optJSONObject != null) {
						unionMember.setType("1");
						unionMember.setTitle(name);
						unionMember.isCancle = true;
						unionMember.parseJSON(optJSONObject);
					}
					unionMembers.add(unionMember);
				}

			}
		}
		return this;
	}

}
