package com.xunao.benben.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class NUmberTrainCollection extends BaseBean<NUmberTrainCollection>{
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

	@Override
	public JSONObject toJSON() {
	
		return null;
	}

	@Override
	public NUmberTrainCollection parseJSON(JSONObject jsonObj) throws NetRequestException {
	
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
		
		return this;
	}

}
