package com.xunao.benben.bean;

import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class AddressInfo extends BaseBean<AddressInfo> {
	
	private String id;
	private String bid;
	private String parent_bid;
	private String level;
	private String area_name;

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AddressInfo parseJSON(JSONObject jsonObj) throws NetRequestException {
		
		id = jsonObj.optString("id");
		bid = jsonObj.optString("bid");
		parent_bid = jsonObj.optString("parent_bid");
		level = jsonObj.optString("level");
		area_name = jsonObj.optString("area_name");
		
		
		return this;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public String getParent_bid() {
		return parent_bid;
	}

	public void setParent_bid(String parent_bid) {
		this.parent_bid = parent_bid;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getArea_name() {
		return area_name;
	}

	public void setArea_name(String area_name) {
		this.area_name = area_name;
	}

}
