package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class MySmallCreationList extends BaseBean {


	private ArrayList<MySmallCreationData> mCreationDatas;

	public ArrayList<MySmallCreationData> getmCreationDatas() {
		return mCreationDatas;
	}

	public void setmCreationDatas(ArrayList<MySmallCreationData> mCreationDatas) {
		this.mCreationDatas = mCreationDatas;
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
			mCreationDatas = new ArrayList<MySmallCreationData>();
			MySmallCreationData friendData;
			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					friendData = new MySmallCreationData();
					friendData.parseJSON(optJSONObject);
					mCreationDatas.add(friendData);
				}
			}

		}

		return this;
	}

}
