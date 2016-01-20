package com.xunao.test.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.xunao.test.base.BaseBean;
import com.xunao.test.exception.NetRequestException;

public class BuyInfo extends BaseBean {

	private ArrayList<Quote> mQuotes;

	@Id
	@NoAutoIncrement
	private int id;
	private int memberId;// 作者ID
	private int is_close;// 是否关闭 0未关闭  1已关闭
	private String name;// 姓名
	private String Poster;// 头像
	private String province;// 省代码
	private String city;// 城市代码
	private String title;// 标题
	private String Amount;// 数量
	private String description;// 详情
	private String pro_city;// 地区
	private long deadline;// 截止日期
	private long createdTime;// 创建日期
	private int quotedNumber;// 报价人数
	private int haveQuote; 
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIs_close() {
		return is_close;
	}

	public void setIs_close(int is_close) {
		this.is_close = is_close;
	}

	public ArrayList<Quote> getmQuotes() {
		return mQuotes;
	}

	public void setmQuotes(ArrayList<Quote> mQuotes) {
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


	public String getAmount() {
		return Amount;
	}

	public void setAmount(String amount) {
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

	public int getHaveQuote() {
		return haveQuote;
	}

	public void setHaveQuote(int haveQuote) {
		this.haveQuote = haveQuote;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public BuyInfo parseJSON(JSONObject jsonObj) throws NetRequestException {

		id = jsonObj.optInt("Id");
		memberId = jsonObj.optInt("MemberId");
		is_close = jsonObj.optInt("is_close");
		Amount = jsonObj.optString("Amount");
		Poster = jsonObj.optString("Poster");
		name = jsonObj.optString("Name");
		title = jsonObj.optString("Title");
		description = jsonObj.optString("Description");
		pro_city = jsonObj.optString("pro_city");
		deadline = jsonObj.optLong("Deadline");
		createdTime = jsonObj.optLong("CreatedTime");
		quotedNumber = jsonObj.optInt("QuotedNumber");
		haveQuote = jsonObj.optInt("haveQuote");

		JSONArray optJSONArray = jsonObj.optJSONArray("Quote");
		if (optJSONArray != null) {
			int length = optJSONArray.length();
			mQuotes = new ArrayList<Quote>();
			Quote quote = null;
			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					quote = new Quote();
					quote.parseJSON(optJSONObject);
					mQuotes.add(quote);
				}
			}
		}
		return this;
	}
}
