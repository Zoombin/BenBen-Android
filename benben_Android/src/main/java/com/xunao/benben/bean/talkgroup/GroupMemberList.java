package com.xunao.benben.bean.talkgroup;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class GroupMemberList extends BaseBean {

	private ArrayList<GroupMember> mGroupMembers;

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<GroupMember> getmGroupMembers() {
		return mGroupMembers;
	}

	public void setmGroupMembers(ArrayList<GroupMember> mGroupMembers) {
		this.mGroupMembers = mGroupMembers;
	}

	@Override
	public Object parseJSON(JSONObject jsonObj) throws NetRequestException {

		checkJson(jsonObj);

		JSONArray optJSONArray = jsonObj.optJSONArray("member_info");
		if (optJSONArray != null) {
			int length = optJSONArray.length();
			mGroupMembers = new ArrayList<GroupMember>();
			GroupMember member = null;
			for (int i = 0; i < length; i++) {

				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					member = new GroupMember();
					member.parseJSON(optJSONObject);
					mGroupMembers.add(member);
				}
			}
		}
		return this;
	}
}
