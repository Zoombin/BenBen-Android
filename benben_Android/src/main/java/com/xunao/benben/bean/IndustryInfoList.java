package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class IndustryInfoList extends BaseBean<IndustryInfoList> {
	
	private ArrayList<IndustryInfo> industryInfos;

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IndustryInfoList parseJSON(JSONObject jsonObj) throws NetRequestException {
		
		checkJson(jsonObj);
		
		JSONArray area = jsonObj.optJSONArray("industry");
		if(area!=null&&area.length()>0){
			industryInfos = new ArrayList<IndustryInfo>();
			JSONObject jsonObject ;
			for (int i = 0; i < area.length(); i++) {
				jsonObject = area.optJSONObject(i);
				IndustryInfo industryInfo = new IndustryInfo();
				industryInfo.parseJSON(jsonObject);
				industryInfos.add(industryInfo);
			}
		}
		
		return this;
	}

	public ArrayList<IndustryInfo> getIndustryInfos() {
		return industryInfos;
	}

	public void setIndustryInfos(ArrayList<IndustryInfo> industryInfos) {
		this.industryInfos = industryInfos;
	}

}
