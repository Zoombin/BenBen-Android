package com.xunao.benben.bean;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

import org.json.JSONObject;

/**
 * Created by ltf on 2016/1/5.
 */
public class Bill extends BaseBean<Bill> {
    private int id;
    private String fee;
    private String content;
    private long time;
    private String order_type;
    private String remain;
    private String coin;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }

    public String getRemain() {
        return remain;
    }

    public void setRemain(String remain) {
        this.remain = remain;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public Object parseJSON(JSONObject jsonObj) throws NetRequestException {
        fee = jsonObj.optString("fee");
        content = jsonObj.optString("content");
        time = jsonObj.optLong("time");
        order_type = jsonObj.optString("order_type");
        remain = jsonObj.optString("remain");
        coin = jsonObj.optString("coin");
        return this;
    }

    public Object parseVipBillJSON(JSONObject jsonObj) throws NetRequestException {
        fee = jsonObj.optString("money");
        content = jsonObj.optString("name");
        time = jsonObj.optLong("time");
        return this;
    }


}
