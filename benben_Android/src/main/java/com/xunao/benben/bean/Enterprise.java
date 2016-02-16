package com.xunao.benben.bean;

import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class Enterprise extends BaseBean<Enterprise> {

	private String id;
	private String name;
	private String number;
	private String phone;
	private int origin;// 1表示客户端创建的，2表示后台创建的，后台创建的不能添加成员
	private String description;
	private String type;
	private String area;
	private String inA;
	private String remark;
	private String memberId;
	private String mobliePhone;
	private int short_length;
	private int status;
    private String tag;
    private String firstin;
    private String enterprise_id;
    private int sort;
    private String benben_id;
    private int enterprise_apply;

	public String getId() {
		return id;
	}

	public String getArea() {
		return area;
	}

	public int getOrigin() {
		return origin;
	}

	public void setOrigin(int origin) {
		this.origin = origin;
	}

	public void setArea(String area) {
		this.area = area;
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

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDescription() {
		return description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getMobliePhone() {
		return mobliePhone;
	}

	public void setMobliePhone(String mobliePhone) {
		this.mobliePhone = mobliePhone;
	}

    public String getEnterprise_id() {
        return enterprise_id;
    }

    public void setEnterprise_id(String enterprise_id) {
        this.enterprise_id = enterprise_id;
    }

    @Override
	public JSONObject toJSON() {
		return null;
	}

	public String getInA() {
		return inA;
	}

	public void setInA(String inA) {
		this.inA = inA;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public int getShort_length() {
		return short_length;
	}

	public void setShort_length(int short_length) {
		this.short_length = short_length;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getFirstin() {
        return firstin;
    }

    public void setFirstin(String firstin) {
        this.firstin = firstin;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getBenben_id() {
        return benben_id;
    }

    public void setBenben_id(String benben_id) {
        this.benben_id = benben_id;
    }

    public int getEnterprise_apply() {
        return enterprise_apply;
    }

    public void setEnterprise_apply(int enterprise_apply) {
        this.enterprise_apply = enterprise_apply;
    }

    @Override
	public Enterprise parseJSON(JSONObject jsonObj) throws NetRequestException {

		id = jsonObj.optString("id");
        sort = jsonObj.optInt("sort");
		name = jsonObj.optString("name");
		number = jsonObj.optString("number");
		phone = jsonObj.optString("short_phone");
		origin = jsonObj.optInt("origin");
		short_length = jsonObj.optInt("short_length");
		mobliePhone = jsonObj.optString("phone");
		description = jsonObj.optString("description");
		type = jsonObj.optString("type");
		area = jsonObj.optString("ProCity");
		inA = jsonObj.optString("in");
		remark = jsonObj.optString("remark_name");
		memberId = jsonObj.optString("member_id");
		status = jsonObj.optInt("status");
        tag = jsonObj.optString("tag");
        firstin = jsonObj.optString("firstin");
        enterprise_id = jsonObj.optString("enterprise_id");
        enterprise_apply = jsonObj.optInt("enterprise_apply");
		return this;
	}

}
