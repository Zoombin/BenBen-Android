package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class EnterpriseList extends BaseBean<EnterpriseList>{
	
	private ArrayList<Enterprise> enterprises;

	
	public ArrayList<Enterprise> getEnterprises() {
		return enterprises;
	}

	public void setEnterprises(ArrayList<Enterprise> enterprises) {
		this.enterprises = enterprises;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public EnterpriseList parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		checkJson(jsonObj);
		JSONArray optJSONArray = jsonObj.optJSONArray("enterprise_list");
		
		if (optJSONArray != null) {
			int length = optJSONArray.length();
			enterprises = new ArrayList<Enterprise>();
			Enterprise enterprise;
			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					enterprise = new Enterprise();
					enterprise.parseJSON(optJSONObject);
					enterprises.add(enterprise);
				}
			}

		}
		
		
		return this;
	}

}
