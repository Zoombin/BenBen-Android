package com.xunao.benben.bean;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

import org.json.JSONObject;

/**
 * Created by ltf on 2016/3/17.
 */
public class BaiXin extends BaseBean{
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public Object parseJSON(JSONObject jsonObj) throws NetRequestException {
        id = jsonObj.optInt("id");
        name = jsonObj.optString("name");
        return this;
    }
}
