package com.xunao.benben.bean;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

import org.json.JSONObject;

/**
 * Created by ltf on 2015/12/2.
 */
public class Promotion extends BaseBean {
    private int id;
    private int promotionid;
    private String poster;
    private String small_poster;
    private String vip_time;
    private String name;
    private String description;
    private int is_overtime;
    private int is_down;

    private int is_close;
    private String valid_left;
    private String valid_right;
    private String poster_st;
    private String small_poster_st;
    private int pm_id;
    private String poster_nd;
    private String small_poster_nd;
    private String poster_rd;
    private String small_poster_rd;
    private double origion_price;
    private double promotion_price;
    private int sellcount;
    private String model;
    private String mustknow;
    private String shipping_fee;
    private String price;

    private boolean isChecked = false;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPromotionid() {
        return promotionid;
    }

    public void setPromotionid(int promotionid) {
        this.promotionid = promotionid;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getSmall_poster() {
        return small_poster;
    }

    public void setSmall_poster(String small_poster) {
        this.small_poster = small_poster;
    }

    public String getVip_time() {
        return vip_time;
    }

    public void setVip_time(String vip_time) {
        this.vip_time = vip_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIs_overtime() {
        return is_overtime;
    }

    public void setIs_overtime(int is_overtime) {
        this.is_overtime = is_overtime;
    }

    public int getIs_close() {
        return is_close;
    }

    public void setIs_close(int is_close) {
        this.is_close = is_close;
    }

    public String getValid_left() {
        return valid_left;
    }

    public void setValid_left(String valid_left) {
        this.valid_left = valid_left;
    }

    public String getValid_right() {
        return valid_right;
    }

    public void setValid_right(String valid_right) {
        this.valid_right = valid_right;
    }

    public String getPoster_st() {
        return poster_st;
    }

    public void setPoster_st(String poster_st) {
        this.poster_st = poster_st;
    }

    public int getPm_id() {
        return pm_id;
    }

    public void setPm_id(int pm_id) {
        this.pm_id = pm_id;
    }

    public String getSmall_poster_st() {
        return small_poster_st;
    }

    public void setSmall_poster_st(String small_poster_st) {
        this.small_poster_st = small_poster_st;
    }

    public String getPoster_nd() {
        return poster_nd;
    }

    public void setPoster_nd(String poster_nd) {
        this.poster_nd = poster_nd;
    }

    public String getSmall_poster_nd() {
        return small_poster_nd;
    }

    public void setSmall_poster_nd(String small_poster_nd) {
        this.small_poster_nd = small_poster_nd;
    }

    public String getPoster_rd() {
        return poster_rd;
    }

    public void setPoster_rd(String poster_rd) {
        this.poster_rd = poster_rd;
    }

    public String getSmall_poster_rd() {
        return small_poster_rd;
    }

    public void setSmall_poster_rd(String small_poster_rd) {
        this.small_poster_rd = small_poster_rd;
    }

    public double getOrigion_price() {
        return origion_price;
    }

    public void setOrigion_price(double origion_price) {
        this.origion_price = origion_price;
    }

    public double getPromotion_price() {
        return promotion_price;
    }

    public void setPromotion_price(double promotion_price) {
        this.promotion_price = promotion_price;
    }

    public int getIs_down() {
        return is_down;
    }

    public void setIs_down(int is_down) {
        this.is_down = is_down;
    }

    public int getSellcount() {
        return sellcount;
    }

    public void setSellcount(int sellcount) {
        this.sellcount = sellcount;
    }

    public String getShipping_fee() {
        return shipping_fee;
    }

    public void setShipping_fee(String shipping_fee) {
        this.shipping_fee = shipping_fee;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getMustknow() {
        return mustknow;
    }

    public void setMustknow(String mustknow) {
        this.mustknow = mustknow;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public Object parseJSON(JSONObject jsonObj) throws NetRequestException {
        promotionid = jsonObj.optInt("promotionid");
        poster = jsonObj.optString("poster");
        small_poster = jsonObj.optString("small_poster");
        vip_time = jsonObj.optString("vip_time");
        name = jsonObj.optString("name");
        description = jsonObj.optString("description");
        is_overtime = jsonObj.optInt("is_overtime");
        is_down = jsonObj.optInt("is_down");

        is_close = jsonObj.optInt("is_close");
        valid_left = jsonObj.optString("valid_left");
        valid_right = jsonObj.optString("valid_right");
        poster_st = jsonObj.optString("poster_st");
        pm_id = jsonObj.optInt("pm_id");

        small_poster_st = jsonObj.optString("small_poster_st");
        poster_nd = jsonObj.optString("poster_nd");
        small_poster_nd = jsonObj.optString("small_poster_nd");
        poster_rd = jsonObj.optString("poster_rd");
        small_poster_rd = jsonObj.optString("small_poster_rd");
        origion_price = jsonObj.optDouble("origion_price");
        promotion_price = jsonObj.optDouble("promotion_price");
        sellcount = jsonObj.optInt("sellcount");
        model = jsonObj.optString("model");
        mustknow = jsonObj.optString("mustknow");
        shipping_fee = jsonObj.optString("shipping_fee");
        return this;
    }

    public Object parseJSONCollection(JSONObject jsonObj) throws NetRequestException {
        promotionid = jsonObj.optInt("promotion_id");
        poster = jsonObj.optString("poster");
        small_poster = jsonObj.optString("small_poster");
        vip_time = jsonObj.optString("vip_time");
        name = jsonObj.optString("name");
        description = jsonObj.optString("description");
        is_overtime = jsonObj.optInt("is_overtime");
        is_down = jsonObj.optInt("is_down");

        is_close = jsonObj.optInt("is_close");
        valid_left = jsonObj.optString("valid_left");
        valid_right = jsonObj.optString("valid_right");
        poster_st = jsonObj.optString("poster_st");
        pm_id = jsonObj.optInt("pm_id");

        small_poster_st = jsonObj.optString("small_poster_st");
        poster_nd = jsonObj.optString("poster_nd");
        small_poster_nd = jsonObj.optString("small_poster_nd");
        poster_rd = jsonObj.optString("poster_rd");
        small_poster_rd = jsonObj.optString("small_poster_rd");
        origion_price = jsonObj.optDouble("origion_price");
        promotion_price = jsonObj.optDouble("promotion_price");
        sellcount = jsonObj.optInt("sellcount");
        model = jsonObj.optString("model");
        mustknow = jsonObj.optString("mustknow");
        shipping_fee = jsonObj.optString("shipping_fee");
        price = jsonObj.optString("price");
        return this;
    }
}
