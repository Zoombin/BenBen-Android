package com.xunao.benben.bean;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ltf on 2015/12/14.
 */
public class PayMethod extends BaseBean {
    private int id;
    private String pay_id;
    private String pay_name;
    private boolean isChecked=false;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPay_id() {
        return pay_id;
    }

    public void setPay_id(String pay_id) {
        this.pay_id = pay_id;
    }

    public String getPay_name() {
        return pay_name;
    }

    public void setPay_name(String pay_name) {
        this.pay_name = pay_name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public Object parseJSON(JSONObject jsonObj) throws NetRequestException {
        pay_id = jsonObj.optString("pay_id");
        pay_name = jsonObj.optString("pay_name");
        int is_chosen = jsonObj.optInt("is_chosen");
        if(is_chosen==0){
            isChecked = false;
        }else {
            isChecked = true;
        }
        return this;
    }
}
