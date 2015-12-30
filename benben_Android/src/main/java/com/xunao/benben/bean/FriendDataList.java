package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class FriendDataList extends BaseBean {

	private ArrayList<FriendData> mFriendDatas;

	public ArrayList<FriendData> getmFriendDatas() {
		return mFriendDatas;
	}

	public void setmFriendDatas(ArrayList<FriendData> mFriendDatas) {
		this.mFriendDatas = mFriendDatas;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object parseJSON(JSONObject jsonObj) throws NetRequestException {

		checkJson(jsonObj);

		JSONArray optJSONArray = jsonObj.optJSONArray("number_info");

		if (optJSONArray != null) {
			int length = optJSONArray.length();
			mFriendDatas = new ArrayList<FriendData>();
			FriendData friendData;
			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					friendData = new FriendData();
					friendData.parseJSON(optJSONObject);
					mFriendDatas.add(friendData);
				}
			}

		}

		return this;
	}

}
