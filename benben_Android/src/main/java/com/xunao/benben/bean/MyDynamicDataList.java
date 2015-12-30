package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class MyDynamicDataList extends BaseBean {

	private ArrayList<MyDynamic> myDynamics;

	public ArrayList<MyDynamic> getMyDynamics() {
		return myDynamics;
	}

	public void setMyDynamics(ArrayList<MyDynamic> myDynamics) {
		this.myDynamics = myDynamics;
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
			myDynamics = new ArrayList<MyDynamic>();
			MyDynamic myDynamic;
			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					myDynamic = new MyDynamic();
					myDynamic.parseJSON(optJSONObject);
					myDynamics.add(myDynamic);
				}
			}

		}

		return this;
	}

}
