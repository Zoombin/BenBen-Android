package com.xunao.benben.bean;

import org.json.JSONObject;

import com.lidroid.xutils.db.annotation.Id;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class Quote extends BaseBean<Quote> {

	private int id;
	private int item_id;
	private String name;
	private int member_id;
	private String price;
	private String description;
	private long created_time;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getItem_id() {
		return item_id;
	}

	public void setItem_id(int item_id) {
		this.item_id = item_id;
	}

	public Quote() {
		super();
	}

	public Quote(String name, String price, String description,int memberId) {
		super();
		this.name = name;
		this.price = price;
		this.member_id = memberId;
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
	public Quote parseJSON(JSONObject jsonObj) throws NetRequestException {

		item_id = jsonObj.optInt("item_id");
		member_id = jsonObj.optInt("member_id");
		name = jsonObj.optString("name");
		created_time = jsonObj.optLong("created_time");
		price = jsonObj.optString("price");
		description = jsonObj.optString("description");

		return this;
	}

}
