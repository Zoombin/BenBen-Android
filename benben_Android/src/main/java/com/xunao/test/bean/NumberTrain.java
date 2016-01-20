package com.xunao.test.bean;

import org.json.JSONObject;

import com.xunao.test.base.BaseBean;
import com.xunao.test.exception.NetRequestException;

public class NumberTrain extends BaseBean<NumberTrain>{
	private String id;
	private String name;
	private String distance_kilometers;
	private String distance_meters;
	private String phone;
	private String industry;
	private String poster;
	private int created_time;
	private double lat;
	private double lng;
	private String isTop;
	private String tag;
	private String shortName;
	private String telePhone;
	private String description;
    private String shop;
    private int auth_grade;
    private int place;
    private int is_valid;
	

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getIsTop() {
		return isTop;
	}

	public void setIsTop(String isTop) {
		this.isTop = isTop;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDistance_kilometers() {
		return distance_kilometers;
	}

	public void setDistance_kilometers(String distance_kilometers) {
		this.distance_kilometers = distance_kilometers;
	}

	public String getDistance_meters() {
		return distance_meters;
	}

	public void setDistance_meters(String distance_meters) {
		this.distance_meters = distance_meters;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}
	

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public int getCreated_time() {
		return created_time;
	}

	public void setCreated_time(int created_time) {
		this.created_time = created_time;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getTelePhone() {
		return telePhone;
	}

	public void setTelePhone(String telePhone) {
		this.telePhone = telePhone;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public int getAuth_grade() {
        return auth_grade;
    }

    public void setAuth_grade(int auth_grade) {
        this.auth_grade = auth_grade;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public int getIs_valid() {
        return is_valid;
    }

    public void setIs_valid(int is_valid) {
        this.is_valid = is_valid;
    }

    @Override
	public JSONObject toJSON() {
	
		return null;
	}

	@Override
	public NumberTrain parseJSON(JSONObject jsonObj) throws NetRequestException {
	
		id = jsonObj.optString("id");
		name = jsonObj.optString("name");
		distance_kilometers = jsonObj.optString("distance_kilometers");
		distance_meters = jsonObj.optString("distance_meters");
		phone = jsonObj.optString("phone");
		industry = jsonObj.optString("industry");
		poster = jsonObj.optString("poster");
		lat = jsonObj.optDouble("lat");
		lng = jsonObj.optDouble("lng");
		isTop = jsonObj.optString("istop");
		created_time = jsonObj.optInt("created_time");
		tag = jsonObj.optString("tag");
		telePhone = jsonObj.optString("telephone");
		shortName = jsonObj.optString("short_name");
		description = jsonObj.optString("description");
        shop =  jsonObj.optString("shop");
        auth_grade =  jsonObj.optInt("auth_grade");
        place = jsonObj.optInt("place");
        is_valid = jsonObj.optInt("is_valid");
		return this;
	}

}
