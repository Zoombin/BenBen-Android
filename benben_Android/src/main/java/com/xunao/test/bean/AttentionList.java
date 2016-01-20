package com.xunao.test.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.test.base.BaseBean;
import com.xunao.test.exception.NetRequestException;

public class AttentionList extends BaseBean {

	private ArrayList<Attention> mAttentions;

	public ArrayList<Attention> getmAttentions() {
		return mAttentions;
	}

	public void setmAttentions(ArrayList<Attention> mAttentions) {
		this.mAttentions = mAttentions;
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

		if (optJSONArray != null && optJSONArray.length() > 0) {
			mAttentions = new ArrayList<Attention>();
			Attention attention;
			for (int i = 0; i < optJSONArray.length(); i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					attention = new Attention();
					attention.parseJSON(optJSONObject);
					mAttentions.add(attention);
				}
			}
		}
		return this;
	}

}
