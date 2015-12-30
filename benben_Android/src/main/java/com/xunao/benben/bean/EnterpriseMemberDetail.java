package com.xunao.benben.bean;

import org.json.JSONObject;

import com.lidroid.xutils.db.annotation.Id;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class EnterpriseMemberDetail extends BaseBean<EnterpriseMemberDetail> {
    @Id
    private int a_id;
	private String id;
	private String contactId;
	private String memberId;
	private String shortPhone;
	private String remark;
	private String phone;
	private String name;
	private String createdTime;
	private String eGroupId;
	private String eMemberId;
	private String groupName;
	private String number;
	private String common;
	private boolean checked = false;
	private String pinyin;
	private boolean hasPinYin; // 记录拼音出现的位置
    private String eid;
    private String benben_id;

	public String getNumber() {
		return number;
	}

	public String getCommon() {
		return common;
	}

	public void setCommon(String common) {
		this.common = common;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getShortPhone() {
		return shortPhone;
	}

	public void setShortPhone(String shortPhone) {
		this.shortPhone = shortPhone;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public String geteGroupId() {
		return eGroupId;
	}

	public void seteGroupId(String eGroupId) {
		this.eGroupId = eGroupId;
	}

	public String geteMemberId() {
		return eMemberId;
	}

	public void seteMemberId(String eMemberId) {
		this.eMemberId = eMemberId;
	}


	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public EnterpriseMemberDetail parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		id = jsonObj.optString("id");
		contactId = jsonObj.optString("contact_id");
		memberId = jsonObj.optString("member_id");
		shortPhone = jsonObj.optString("short_phone");
		remark = jsonObj.optString("remark_name");
		phone = jsonObj.optString("phone");
		name = jsonObj.optString("name");
		createdTime = jsonObj.optString("created_time");
		eGroupId = jsonObj.optString("egroup_id");
		eMemberId = jsonObj.optString("emember_id");
		common = jsonObj.optString("common");
		pinyin = jsonObj.optString("pinyin");
		return this;
	}

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getBenben_id() {
        return benben_id;
    }

    public void setBenben_id(String benben_id) {
        this.benben_id = benben_id;
    }
}
