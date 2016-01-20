package com.xunao.test.bean;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.xunao.test.base.BaseBean;
import com.xunao.test.exception.NetRequestException;
import org.json.JSONObject;

public class AccountAddress extends BaseBean<AccountAddress> {
	@Id
	@NoAutoIncrement
	private int id;
	private String consignee;
	private String tel;
	private String zipcode;
	private String street;
	private int is_default;
	private String city;
	private String country;
	private String member_id;
    private String area;
    private String address;
    private String email;
    private String address_name;
    private String province;
    private String best_time;
    private String mobile;
	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccountAddress parseJSON(JSONObject jsonObj) throws NetRequestException {

		id = jsonObj.optInt("address_id");
        consignee = jsonObj.optString("consignee");
        tel = jsonObj.optString("tel");
        zipcode = jsonObj.optString("zipcode");
        street = jsonObj.optString("street");
        is_default = jsonObj.optInt("is_default");
        city = jsonObj.optString("city");
        country = jsonObj.optString("country");
        member_id = jsonObj.optString("member_id");
        area = jsonObj.optString("area");
        address = jsonObj.optString("address");
        email = jsonObj.optString("email");
        address_name = jsonObj.optString("address_name");
        province = jsonObj.optString("province");
        best_time = jsonObj.optString("best_time");
        mobile = jsonObj.optString("mobile");

		return this;
	}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getIs_default() {
        return is_default;
    }

    public void setIs_default(int is_default) {
        this.is_default = is_default;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress_name() {
        return address_name;
    }

    public void setAddress_name(String address_name) {
        this.address_name = address_name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getBest_time() {
        return best_time;
    }

    public void setBest_time(String best_time) {
        this.best_time = best_time;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}
