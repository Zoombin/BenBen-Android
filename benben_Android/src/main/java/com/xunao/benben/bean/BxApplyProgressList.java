package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class BxApplyProgressList extends BaseBean{
	private ArrayList<BxApplyProgress> bxaProgresses;
	
	public ArrayList<BxApplyProgress> getBxaProgresses() {
		return bxaProgresses;
	}

	public void setBxaProgresses(ArrayList<BxApplyProgress> bxaProgresses) {
		this.bxaProgresses = bxaProgresses;
	}

	@Override
	public JSONObject toJSON() {
		
		return null;
	}

	@Override
	public Object parseJSON(JSONObject jsonObj) throws NetRequestException {
		checkJson(jsonObj);
		JSONArray optJSONArray = jsonObj.optJSONArray("status");
		
		if (optJSONArray != null) {
			int length = optJSONArray.length();
			bxaProgresses = new ArrayList<BxApplyProgress>();
			BxApplyProgress applyProgress;
			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					applyProgress = new BxApplyProgress();
					applyProgress.parseJSON(optJSONObject);
					
					bxaProgresses.add(applyProgress);
				}
			}

		}

		return this;
	}

}
