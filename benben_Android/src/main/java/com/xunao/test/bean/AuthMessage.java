package com.xunao.test.bean;

import com.xunao.test.base.BaseBean;
import com.xunao.test.exception.NetRequestException;

import org.json.JSONObject;

/**
 * Created by ltf on 2015/12/1.
 */
public class AuthMessage extends BaseBean {
    private int id;
    private int authid;
    private String real_name;
    private String company;
    private String licence;
    private long time;
    private int memberid;
    private String reason;
    private int status;
    private String back;
    private String idcard;
    private String front;
    private int type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAuthid() {
        return authid;
    }

    public void setAuthid(int authid) {
        this.authid = authid;
    }

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getMemberid() {
        return memberid;
    }

    public void setMemberid(int memberid) {
        this.memberid = memberid;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getBack() {
        return back;
    }

    public void setBack(String back) {
        this.back = back;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getFront() {
        return front;
    }

    public void setFront(String front) {
        this.front = front;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public Object parseJSON(JSONObject jsonObj) throws NetRequestException {
        authid = jsonObj.optInt("authid");
        real_name = jsonObj.optString("real_name");
        licence = jsonObj.optString("licence");
        time = jsonObj.optLong("time");
        memberid = jsonObj.optInt("memberid");
        reason = jsonObj.optString("reason");
        status = jsonObj.optInt("status");
        back = jsonObj.optString("back");
        idcard = jsonObj.optString("idcard");
        front = jsonObj.optString("front");
        type = jsonObj.optInt("type");
        company = jsonObj.optString("company");
        return this;
    }
}
