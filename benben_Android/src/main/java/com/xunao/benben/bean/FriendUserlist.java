package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class FriendUserlist extends BaseBean {

	ArrayList<FriendUser> mFriendUser;

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<FriendUser> getmFriendUser() {
		return mFriendUser;
	}

	public void setmFriendUser(ArrayList<FriendUser> mFriendUser) {
		this.mFriendUser = mFriendUser;
	}

	@Override
	public Object parseJSON(JSONObject jsonObj) throws NetRequestException {

		checkJson(jsonObj);

		JSONArray optJSONArray = jsonObj.optJSONArray("user");

		if (optJSONArray != null && optJSONArray.length() >= 0) {
			mFriendUser = new ArrayList<FriendUser>();
			FriendUser friendUser = null;
			int length = optJSONArray.length();
			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					friendUser = new FriendUser();
					friendUser.parseJSON(optJSONObject);
					mFriendUser.add(friendUser);
				}
			}
		}
		return this;
	}

}
