package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class AddressInfoList extends BaseBean<AddressInfoList> {
	
	private ArrayList<AddressInfo> addressInfos;

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AddressInfoList parseJSON(JSONObject jsonObj) throws NetRequestException {
		
		checkJson(jsonObj);
		
		JSONArray area = jsonObj.optJSONArray("area");
		if(area!=null&&area.length()>0){
			addressInfos = new ArrayList<AddressInfo>();
			JSONObject jsonObject ;
			for (int i = 0; i < area.length(); i++) {
				jsonObject = area.optJSONObject(i);
				AddressInfo addressInfo = new AddressInfo();
				addressInfo.parseJSON(jsonObject);
				addressInfos.add(addressInfo);
			}
		}
		
		return this;
	}

	public ArrayList<AddressInfo> getAddressInfos() {
		return addressInfos;
	}

	public void setAddressInfos(ArrayList<AddressInfo> addressInfos) {
		this.addressInfos = addressInfos;
	}

}
