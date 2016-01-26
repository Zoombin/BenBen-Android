package com.xunao.benben.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

import java.util.ArrayList;
import java.util.List;

public class QuoteContent extends BaseBean<QuoteContent> {
	@Id
	@NoAutoIncrement
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private int store_id;
	private int member_id;
	private String price;
	private String description;
	private long created_time;
	private String nickName;
	private String short_name;
	private String poster;
	private String huanxinUsername;
	private String name;
	private int accept;// 1代表接受他的保价

    private List<BuyInfoPic> infoPics = new ArrayList<>();
    private String Images;// 使用||分割

	public QuoteContent() {
		super();
	}

	public QuoteContent(String name, String price, String description) {
		super();
		this.name = name;
		this.price = price;
		this.description = description;
	}

	public int getAccept() {
		return accept;
	}

	public void setAccept(int accept) {
		this.accept = accept;
	}

	public int getStore_id() {
		return store_id;
	}

	public void setStore_id(int store_id) {
		this.store_id = store_id;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public String getHuanxinUsername() {
		return huanxinUsername;
	}

	public void setHuanxinUsername(String huanxinUsername) {
		this.huanxinUsername = huanxinUsername;
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

	public String getShort_name() {
		return short_name;
	}

	public void setShort_name(String short_name) {
		this.short_name = short_name;
	}

    public List<BuyInfoPic> getInfoPics() {
        return infoPics;
    }

    public void setInfoPics(List<BuyInfoPic> infoPics) {
        this.infoPics = infoPics;
    }

    public String getImages() {
        return Images;
    }

    public void setImages(String images) {
        Images = images;
    }

    @Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QuoteContent parseJSON(JSONObject jsonObj)
			throws NetRequestException {

		id = jsonObj.optInt("id");
		store_id = jsonObj.optInt("store_id");
		short_name = jsonObj.optString("short_name");
		member_id = jsonObj.optInt("member_id");
		accept = jsonObj.optInt("accept");
		price = jsonObj.optString("price");
		description = jsonObj.optString("description");
		created_time = jsonObj.optLong("created_time");
		nickName = jsonObj.optString("nick_name");
		poster = jsonObj.optString("poster");
		huanxinUsername = jsonObj.optString("huanxin_username");
		name = jsonObj.optString("name");

        JSONArray picJSONArray = jsonObj.optJSONArray("quote_pic");
        if (picJSONArray != null && picJSONArray.length()>0) {
            int length = picJSONArray.length();
            BuyInfoPic buyInfoPic = null;
            for (int i = 0; i < length; i++) {
                JSONObject optJSONObject = picJSONArray.optJSONObject(i);
                if (optJSONObject != null) {
                    buyInfoPic = new BuyInfoPic();
                    buyInfoPic.parseJSON(optJSONObject);
                    infoPics.add(buyInfoPic);
                    if (Images != null) {
                        Images += "^" + buyInfoPic.getPoster();
                    } else {
                        Images = buyInfoPic.getPoster();
                    }

                }
            }
        }



		return this;
	}

}
