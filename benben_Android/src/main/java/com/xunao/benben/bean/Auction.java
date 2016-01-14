package com.xunao.benben.bean;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

import org.json.JSONObject;

/**
 * Created by ltf on 2016/1/7.
 */
public class Auction extends BaseBean<Auction>{
    private int id;
    private int num;
    private int auction_id;
    private long rest_time;
    private int is_close;
    private long end_time;
    private double add_step;
    private int is_start;
    private double guarantee;
    private long start_time;
    private String top_period;
    private String industry;
    private String district;
    private double start_price;

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public Object parseJSON(JSONObject jsonObj) throws NetRequestException {
        num = jsonObj.optInt("num");
        auction_id = jsonObj.optInt("auction_id");
        rest_time = jsonObj.optLong("rest_time");
        is_close = jsonObj.optInt("is_close");
        end_time = jsonObj.optLong("end_time");
        add_step = jsonObj.optDouble("add_step");
        is_start = jsonObj.optInt("is_start");
        guarantee = jsonObj.optDouble("guarantee");
        start_time = jsonObj.optLong("start_time");
        top_period = jsonObj.optString("top_period");
        industry = jsonObj.optString("industry");
        district = jsonObj.optString("district");
        start_price = jsonObj.optDouble("start_price");
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

    public long getRest_time() {
        return rest_time;
    }

    public void setRest_time(long rest_time) {
        this.rest_time = rest_time;
    }

    public int getIs_close() {
        return is_close;
    }

    public void setIs_close(int is_close) {
        this.is_close = is_close;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public double getAdd_step() {
        return add_step;
    }

    public void setAdd_step(double add_step) {
        this.add_step = add_step;
    }

    public int getIs_start() {
        return is_start;
    }

    public void setIs_start(int is_start) {
        this.is_start = is_start;
    }

    public double getGuarantee() {
        return guarantee;
    }

    public void setGuarantee(double guarantee) {
        this.guarantee = guarantee;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
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

    public double getStart_price() {
        return start_price;
    }

    public void setStart_price(double start_price) {
        this.start_price = start_price;
    }
}
