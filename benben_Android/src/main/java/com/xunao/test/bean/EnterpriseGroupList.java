package com.xunao.test.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.test.base.BaseBean;
import com.xunao.test.exception.NetRequestException;

public class EnterpriseGroupList extends BaseBean<EnterpriseGroupList> {
	private ArrayList<EnterpriseGroup> enterpriseGroups;

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public EnterpriseGroupList parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		checkJson(jsonObj);
		JSONArray optJSONArray = jsonObj.optJSONArray("member_group");
		if (optJSONArray != null) {
			int length = optJSONArray.length();
			enterpriseGroups = new ArrayList<EnterpriseGroup>();
			EnterpriseGroup group;

			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					group = new EnterpriseGroup();
					group.parseJSON(optJSONObject);

					enterpriseGroups.add(group);
				}
			}

		}
		return this;
	}

	public ArrayList<EnterpriseGroup> getEnterpriseGroups() {
		return enterpriseGroups;
	}

	public void setEnterpriseGroups(ArrayList<EnterpriseGroup> enterpriseGroups) {
		this.enterpriseGroups = enterpriseGroups;
	}

}
