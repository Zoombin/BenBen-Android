package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class SmallDataList extends BaseBean {


	private ArrayList<SmallMakeData> mSmallMakeData;

	public ArrayList<SmallMakeData> getmSmallMakeData() {
		return mSmallMakeData;
	}

	public void setmSmallMakeData(ArrayList<SmallMakeData> mSmallMakeData) {
		this.mSmallMakeData = mSmallMakeData;
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
			mSmallMakeData = new ArrayList<SmallMakeData>();
			SmallMakeData friendData;
			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					friendData = new SmallMakeData();
					friendData.parseJSON(optJSONObject);
					mSmallMakeData.add(friendData);
				}
			}

		}

		return this;
	}

}
