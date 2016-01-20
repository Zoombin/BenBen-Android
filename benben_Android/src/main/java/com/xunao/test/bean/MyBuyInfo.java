package com.xunao.test.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.test.base.BaseBean;
import com.xunao.test.exception.NetRequestException;

public class MyBuyInfo extends BaseBean {

	private ArrayList<MyQuote> mQuotes;

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	private int memberId;// 作者ID
	private String name;// 姓名
	private String Poster;// 头像
	private String province;// 省代码
	private String city;// 城市代码
	private String title;// 标题
	private int Amount;// 数量
	private String description;// 详情
	private String pro_city;// 地区
	private long deadline;// 截止日期
	private long createdTime;// 创建日期
	private int quotedNumber;// 报价人数
	private String status;

	public ArrayList<MyQuote> getmQuotes() {
		return mQuotes;
	}

	public void setmQuotes(ArrayList<MyQuote> mQuotes) {
		this.mQuotes = mQuotes;
	}


	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPoster() {
		return Poster;
	}

	public void setPoster(String poster) {
		Poster = poster;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getAmount() {
		return Amount;
	}

	public void setAmount(int amount) {
		Amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPro_city() {
		return pro_city;
	}

	public void setPro_city(String pro_city) {
		this.pro_city = pro_city;
	}

	public long getDeadline() {
		return deadline;
	}

	public void setDeadline(long deadline) {
		this.deadline = deadline;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	public int getQuotedNumber() {
		return quotedNumber;
	}

	public void setQuotedNumber(int quotedNumber) {
		this.quotedNumber = quotedNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public MyBuyInfo parseJSON(JSONObject jsonObj) throws NetRequestException {

		id = jsonObj.optString("Id");
		memberId = jsonObj.optInt("MemberId");
		Amount = jsonObj.optInt("Amount");
		Poster = jsonObj.optString("Poster");
		name = jsonObj.optString("Name");
		title = jsonObj.optString("Title");
		description = jsonObj.optString("Description");
		pro_city = jsonObj.optString("pro_city");
		deadline = jsonObj.optLong("Deadline");
		createdTime = jsonObj.optLong("CreatedTime");
		quotedNumber = jsonObj.optInt("QuotedNumber");
		status = jsonObj.optString("status");

		JSONArray optJSONArray = jsonObj.optJSONArray("Quote");
		if (optJSONArray != null) {
			int length = optJSONArray.length();
			mQuotes = new ArrayList<MyQuote>();
			MyQuote quote = null;
			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					quote = new MyQuote();
					quote.parseJSON(optJSONObject);
					mQuotes.add(quote);
				}
			}
		}
		return this;
	}
}
