package com.xunao.benben.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class BuyInfoContent extends BaseBean {

	private ArrayList<QuoteContent> mQuoteContents;

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private String title;// 标题
	private String Amount;// 数量
	private int is_close;// 是否 关闭
	private long left_time;// 剩余时间
	private String description;// 详情
	private long deadline;// 截止日期
	private int quotedNumber;// 报价人数
	private int memberId;// 作者ID
	private String name;// 姓名
	private String nickName;// 姓名
	private String Poster;// 头像
	private String province;// 省代码
	private String city;// 城市代码
	private String pro_city;// 地区
	private String address;// 地区

	private long created_time;

    private List<BuyInfoPic> infoPics = new ArrayList<>();

	public ArrayList<QuoteContent> getmQuoteContents() {
		return mQuoteContents;
	}

	public void setmQuoteContents(ArrayList<QuoteContent> mQuoteContents) {
		this.mQuoteContents = mQuoteContents;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public int getIs_close() {
		return is_close;
	}

	public void setIs_close(int is_close) {
		this.is_close = is_close;
	}

	public long getLeft_time() {
		return left_time;
	}

	public void setLeft_time(long left_time) {
		this.left_time = left_time;
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

	public int getQuotedNumber() {
		return quotedNumber;
	}

	public void setQuotedNumber(int quotedNumber) {
		this.quotedNumber = quotedNumber;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	public long getCreated_time() {
		return created_time;
	}

	public void setCreated_time(long created_time) {
		this.created_time = created_time;
	}

    public List<BuyInfoPic> getInfoPics() {
        return infoPics;
    }

    public void setInfoPics(List<BuyInfoPic> infoPics) {
        this.infoPics = infoPics;
    }

    @Override
	public BuyInfoContent parseJSON(JSONObject jsonObj)
			throws NetRequestException {

		checkJson(jsonObj);

		jsonObj = jsonObj.optJSONObject("buy_info");
		if (jsonObj != null) {
			id = jsonObj.optString("id");
			title = jsonObj.optString("title");
			Amount = jsonObj.optString("amount");
			is_close = jsonObj.optInt("is_close");
			left_time = jsonObj.optLong("left_time");
			description = jsonObj.optString("description");
			deadline = jsonObj.optLong("deadline");
			quotedNumber = jsonObj.optInt("quoted_number");
			memberId = jsonObj.optInt("member_id");
			name = jsonObj.optString("name");
			nickName = jsonObj.optString("nick_name");
			Poster = jsonObj.optString("poster");
			pro_city = jsonObj.optString("pro_city");
			address = jsonObj.optString("address");
			created_time = jsonObj.optLong("created_time");

			JSONArray optJSONArray = jsonObj.optJSONArray("quoted_info");
			if (optJSONArray != null) {
				int length = optJSONArray.length();
				mQuoteContents = new ArrayList<QuoteContent>();
				QuoteContent quote = null;
				for (int i = 0; i < length; i++) {
					JSONObject optJSONObject = optJSONArray.optJSONObject(i);
					if (optJSONObject != null) {
						quote = new QuoteContent();
						quote.parseJSON(optJSONObject);
						mQuoteContents.add(quote);
					}
				}
			}


            JSONArray picJSONArray = jsonObj.optJSONArray("sell_pic");
            if (picJSONArray != null && picJSONArray.length()>0) {
                int length = picJSONArray.length();
                BuyInfoPic buyInfoPic = null;
                for (int i = 0; i < length; i++) {
                    JSONObject optJSONObject = picJSONArray.optJSONObject(i);
                    if (optJSONObject != null) {
                        buyInfoPic = new BuyInfoPic();
                        buyInfoPic.parseJSON(optJSONObject);
                        infoPics.add(buyInfoPic);
                    }
                }
            }
			return this;
		}
		return null;
	}
}
