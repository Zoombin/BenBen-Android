package com.xunao.benben.bean;

import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.utils.CommonUtils;

public class TalkGroup extends BaseBean<TalkGroup> {

	private String id;
	private String poster;
	private String name;
	private String province;
	private String city;
	private String area;
	private String street;
	private String description;
	private String bulletin;
	private String member_id;
	private String group_admin;
	private String is_admin;
	private String number;
	private String status;
	private String created_time;
	private String level;
	private String huanxin_groupid;
	private String pro_city;
	private String group_nick_name;
	private String memberUpperLimit = "0";
	private String show_id;
	private int maxuser;

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TalkGroup parseJSON(JSONObject jsonObj) throws NetRequestException {

		id = jsonObj.optString("id");
		poster = jsonObj.optString("poster");
		name = jsonObj.optString("name");
		province = jsonObj.optString("province");
		city = jsonObj.optString("city");
		area = jsonObj.optString("area");
		street = jsonObj.optString("street");
		group_admin = jsonObj.optString("group_admin");
		is_admin = jsonObj.optString("is_admin");
		description = jsonObj.optString("description");
		bulletin = jsonObj.optString("bulletin");
		member_id = jsonObj.optString("member_id");
		number = jsonObj.optString("number");
		status = jsonObj.optString("status");
		created_time = jsonObj.optString("created_time");
		maxuser = jsonObj.optInt("maxuser");
		level = jsonObj.optString("level");
		huanxin_groupid = jsonObj.optString("huanxin_groupid");
		show_id = jsonObj.optString("show_id");
		pro_city = jsonObj.optString("pro_city");
		if (CommonUtils.isEmpty(pro_city)) {
			pro_city = jsonObj.optString("pro-city");
		}

		group_nick_name = jsonObj.optString("group_nick_name");
		memberUpperLimit = jsonObj.optString("memberUpperLimit");

		return this;
	}

	public int getMaxuser() {
		return maxuser;
	}

	public void setMaxuser(int maxuser) {
		this.maxuser = maxuser;
	}

	public String getMemberUpperLimit() {
		return memberUpperLimit;
	}

	public void setMemberUpperLimit(String memberUpperLimit) {
		this.memberUpperLimit = memberUpperLimit;
	}

	public String getIs_admin() {
		return is_admin;
	}

	public void setIs_admin(String is_admin) {
		this.is_admin = is_admin;
	}

	public String getGroup_nick_name() {
		return group_nick_name;
	}

	public void setGroup_nick_name(String group_nick_name) {
		this.group_nick_name = group_nick_name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public String getName() {
		return name;
	}

	public String getGroup_admin() {
		return group_admin;
	}

	public void setGroup_admin(String group_admin) {
		this.group_admin = group_admin;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBulletin() {
		return bulletin;
	}

	public void setBulletin(String bulletin) {
		this.bulletin = bulletin;
	}

	public String getMember_id() {
		return member_id;
	}

	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreated_time() {
		return created_time;
	}

	public void setCreated_time(String created_time) {
		this.created_time = created_time;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getHuanxin_groupid() {
		return huanxin_groupid;
	}

	public void setHuanxin_groupid(String huanxin_groupid) {
		this.huanxin_groupid = huanxin_groupid;
	}

	public String getPro_city() {
		return pro_city;
	}

	public void setPro_city(String pro_city) {
		this.pro_city = pro_city;
	}

	public String getShow_id() {
		return show_id;
	}

	public void setShow_id(String show_id) {
		this.show_id = show_id;
	}

}
