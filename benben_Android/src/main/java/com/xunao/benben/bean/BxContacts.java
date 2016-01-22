package com.xunao.benben.bean;

import org.json.JSONObject;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Transient;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.utils.CommonUtils;

public class BxContacts extends BaseBean<BxContacts> implements Comparable {

	@Id
	@NoAutoIncrement
	private int id;
	private boolean checked = false;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	private String name;
	private String phone;
	private String[] addressId;
	private String address;
	private String pinyin;
	private String is_benben;
	private String poster;
	private String group_id;
	@Transient
	private boolean hasPinYin; // 记录拼音出现的位置
    @Transient
    private boolean isUpdate;
    @Transient
    private String allPhone;

	public String getIs_benben() {
		return is_benben;
	}

	public void setIs_benben(String is_benben) {
		this.is_benben = is_benben;
	}

	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
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

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String[] getAddressId() {
		return addressId;
	}

	public void setAddressId(String[] addressId) {
		this.addressId = addressId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public boolean isHasPinYin() {
		return hasPinYin;
	}

	public void setHasPinYin(boolean hasPinYin) {
		this.hasPinYin = hasPinYin;
	}

	@Override
	public JSONObject toJSON() {

		return null;
	}

	@Override
	public BxContacts parseJSON(JSONObject jsonObj) throws NetRequestException {
		name = jsonObj.optString("name");
		phone = jsonObj.optString("phone");
		is_benben = jsonObj.optString("is_benben");
		group_id = jsonObj.optString("group_id");
		poster = jsonObj.optString("poster");
        pinyin = jsonObj.optString("pinyin");
        if(pinyin!=null && pinyin.length()>0){
            pinyin = pinyin.substring(0, 1);
        }
		hasPinYin = true;
		return this;
	}

	// 按照拼音排序用的
	@Override
	public int compareTo(Object o) {
		BxContacts s = (BxContacts) o;
		return pinyin.charAt(0) - s.getPinyin().charAt(0);
	}

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    public String getAllPhone() {
        return allPhone;
    }

    public void setAllPhone(String allPhone) {
        this.allPhone = allPhone;
    }
}
