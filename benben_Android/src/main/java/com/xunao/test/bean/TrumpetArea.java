package com.xunao.test.bean;

import org.json.JSONObject;

import com.xunao.test.base.BaseBean;
import com.xunao.test.exception.NetRequestException;

public class TrumpetArea extends BaseBean<TrumpetArea> {
	private String [] addressId;
	private String addressName;
	
	public String[] getAddressId() {
		return addressId;
	}

	public void setAddressId(String[] addressId) {
		this.addressId = addressId;
	}

	public String getAddressName() {
		return addressName;
	}

	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public TrumpetArea parseJSON(JSONObject jsonObj) throws NetRequestException {
		return null;
	}

}
