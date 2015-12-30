package com.xunao.benben.bean;

import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class MyQuote extends BaseBean<MyQuote> {

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	private String name;
	private int member_id;
	private String price;
	private String description;
	private long created_time;

	public MyQuote() {
		super();
	}

	public MyQuote(String name, String price, String description) {
		super();
		this.name = name;
		this.price = price;
		this.description = description;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMember_id() {
		return member_id;
	}

	public void setMember_id(int member_id) {
		this.member_id = member_id;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getCreated_time() {
		return created_time;
	}

	public void setCreated_time(long created_time) {
		this.created_time = created_time;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MyQuote parseJSON(JSONObject jsonObj) throws NetRequestException {

		id = jsonObj.optString("item_id");
		member_id = jsonObj.optInt("member_id");
		name = jsonObj.optString("name");
		created_time = jsonObj.optLong("created_time");
		price = jsonObj.optString("price");
		description = jsonObj.optString("description");

		return this;
	}

}
