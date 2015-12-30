package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class BroadCastingList extends BaseBean<BroadCastingList> {
	private ArrayList<BroadCasting> broadCasting;
	private int authority;
	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public BroadCastingList parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		checkJson(jsonObj);
		authority = jsonObj.optInt("authority");
		JSONArray optJSONArray = jsonObj.optJSONArray("lists");

		if (optJSONArray != null) {
			int length = optJSONArray.length();
			broadCasting = new ArrayList<BroadCasting>();
			BroadCasting mBuyInfo = null;
			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					mBuyInfo = new BroadCasting();
					mBuyInfo.parseJSON(optJSONObject);
					broadCasting.add(mBuyInfo);
				}
			}
		}
		
		return this;
	}

	public ArrayList<BroadCasting> getBroadCasting() {
		return broadCasting;
	}

	public void setBroadCasting(ArrayList<BroadCasting> broadCasting) {
		this.broadCasting = broadCasting;
	}

	public int getAuthority() {
		return authority;
	}

	public void setAuthority(int authority) {
		this.authority = authority;
	}

}
