package com.xunao.benben.bean;

import org.json.JSONObject;

import u.aly.ch;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class FriendUnion extends BaseBean<FriendUnion> {

	private String id;
	private String name;
	private String ninkName;
	private String info;
	private int number;
	private String poster;
	private String createdTime;
	private boolean isBitMap;
	private String createdId;
	private String imageName;
	private String area;
	private String annocement;
	private String type;
	private String remarkContent;
	private String category;
	private int status;
	private String fullArea;
    private String change_time;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNinkName() {
		return ninkName;
	}

	public void setNinkName(String ninkName) {
		this.ninkName = ninkName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public boolean isBitMap() {
		return isBitMap;
	}

	public void setBitMap(boolean isBitMap) {
		this.isBitMap = isBitMap;
	}

	public String getCreatedId() {
		return createdId;
	}

	public void setCreatedId(String createdId) {
		this.createdId = createdId;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getRemarkContent() {
		return remarkContent;
	}

	public void setRemarkContent(String remarkContent) {
		this.remarkContent = remarkContent;
	}

	public String getFullArea() {
		return fullArea;
	}

	public void setFullArea(String fullArea) {
		this.fullArea = fullArea;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAnnocement() {
		return annocement;
	}

	public void setAnnocement(String annocement) {
		this.annocement = annocement;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

    public String getChange_time() {
        return change_time;
    }

    public void setChange_time(String change_time) {
        this.change_time = change_time;
    }

    @Override
	public FriendUnion parseJSON(JSONObject jsonObj) throws NetRequestException {
		checkJson(jsonObj);
		id = jsonObj.optString("id");
		name = jsonObj.optString("name");
		poster = jsonObj.optString("poster");
		number = jsonObj.optInt("number");
		info = jsonObj.optString("description");
		createdTime = jsonObj.optString("created_time");
		createdId = jsonObj.optString("member_id");
		ninkName = jsonObj.optString("nickname");
		area = jsonObj.optString("area");
		isBitMap = false;
		annocement = jsonObj.optString("announcement");
		type = jsonObj.optString("type");
		remarkContent = jsonObj.optString("remark_content");
		category = jsonObj.optString("category");
		status = jsonObj.optInt("status");
		fullArea = jsonObj.optString("full_area");
        change_time = jsonObj.optString("change_time");
		return this;
	}

}
