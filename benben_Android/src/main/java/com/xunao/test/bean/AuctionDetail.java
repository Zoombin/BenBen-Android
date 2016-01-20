package com.xunao.test.bean;

import com.xunao.test.base.BaseBean;
import com.xunao.test.exception.NetRequestException;

import org.json.JSONObject;

/**
 * Created by ltf on 2016/1/8.
 */
public class AuctionDetail extends BaseBean<AuctionDetail>{
    private int id;
    private int num;
    private int auction_id;
    private String now_price;
    private String from;
    private int place;


    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public Object parseJSON(JSONObject jsonObj) throws NetRequestException {
        num = jsonObj.optInt("num");
        auction_id = jsonObj.optInt("auction_id");
        place = jsonObj.optInt("place");
        now_price = jsonObj.optString("now_price");
        from = jsonObj.optString("from");


        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getAuction_id() {
        return auction_id;
    }

    public void setAuction_id(int auction_id) {
        this.auction_id = auction_id;
    }

    public String getNow_price() {
        return now_price;
    }

    public void setNow_price(String now_price) {
        this.now_price = now_price;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }
}
