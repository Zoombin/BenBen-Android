package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.widget.Button;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class BuyInfolist extends BaseBean {

	private ArrayList<BuyInfo> mBuyInfos;

	public ArrayList<BuyInfo> getmQuotes() {
		return mBuyInfos;
	}

	public void setmQuotes(ArrayList<BuyInfo> mBuyInfos) {
		this.mBuyInfos = mBuyInfos;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public BuyInfolist parseJSON(JSONObject jsonObj) throws NetRequestException {

		checkJson(jsonObj);

		JSONArray optJSONArray = jsonObj.optJSONArray("number_info");

		if (optJSONArray != null) {
			int length = optJSONArray.length();
			mBuyInfos = new ArrayList<BuyInfo>();
			BuyInfo mBuyInfo = null;
			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					mBuyInfo = new BuyInfo();
					mBuyInfo.parseJSON(optJSONObject);
					mBuyInfos.add(mBuyInfo);
				}
			}
		}
		return this;
	}
}
