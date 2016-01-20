package com.xunao.test.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.test.base.BaseBean;
import com.xunao.test.exception.NetRequestException;

public class MyBuyInfolist extends BaseBean {

	private ArrayList<MyBuyInfo> mBuyInfos;

	public ArrayList<MyBuyInfo> getmQuotes() {
		return mBuyInfos;
	}

	public void setmQuotes(ArrayList<MyBuyInfo> mBuyInfos) {
		this.mBuyInfos = mBuyInfos;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public MyBuyInfolist parseJSON(JSONObject jsonObj) throws NetRequestException {

		checkJson(jsonObj);

		JSONArray optJSONArray = jsonObj.optJSONArray("number_info");

		if (optJSONArray != null) {
			int length = optJSONArray.length();
			mBuyInfos = new ArrayList<MyBuyInfo>();
			MyBuyInfo mBuyInfo = null;
			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					mBuyInfo = new MyBuyInfo();
					mBuyInfo.parseJSON(optJSONObject);
					mBuyInfos.add(mBuyInfo);
				}
			}
		}
		return this;
	}
}
