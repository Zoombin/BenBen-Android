package com.xunao.benben.bean;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

import org.json.JSONObject;

/**
 * Created by ltf on 2016/1/7.
 */
public class AuctionRecord extends BaseBean<AuctionRecord>{
    private int id;
    private String auction_price;
    private int place;
    private String top_period;
    private String industry;
    private String district;
    private long auction_time;


    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public Object parseJSON(JSONObject jsonObj) throws NetRequestException {
        place = jsonObj.optInt("place");
        auction_time = jsonObj.optLong("auction_time");
        top_period = jsonObj.optString("top_period");
        industry = jsonObj.optString("industry");
        district = jsonObj.optString("district");
        auction_price = jsonObj.optString("auction_price");
        return this;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuction_price() {
        return auction_price;
    }

    public void setAuction_price(String auction_price) {
        this.auction_price = auction_price;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public String getTop_period() {
        return top_period;
    }

    public void setTop_period(String top_period) {
        this.top_period = top_period;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public long getAuction_time() {
        return auction_time;
    }

    public void setAuction_time(long auction_time) {
        this.auction_time = auction_time;
    }
}
