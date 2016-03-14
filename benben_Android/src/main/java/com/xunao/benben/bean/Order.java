package com.xunao.benben.bean;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

import org.json.JSONObject;

/**
 * Created by ltf on 2015/12/21.
 */
public class Order extends BaseBean<Order>{
    private int id;
    private int goods_number;
    private String train_id;
    private String goods_name;
    private String short_name;
    private String order_id;
    private String shipping_fee;
    private int order_status;
    private String promotion_pic;
    private String promotion_price;
    private int promotion_id;
    private int pay_status;
    private String goods_amount;
    private String order_amount;
    private String store_pic;
    private int shipping_status;
    private String order_sn;
    private int pay_id;
    private String pay_name;
    private long pay_time;
    private long add_time;
    private long confirm_time;
    private String consignee;
    private String mobile;
    private String address;
    private int back_status;
    private long back_apply_time;
    private long back_deal_time;
    private int extension_code;
    private String huanxin_username;
    private String qrcode;
    private int is_close;
    private int is_out;
    private int is_consume;

    private String user_poster;
    private String nick_name;
    private String shipping_sn;
    private Long consume_time;

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public Object parseJSON(JSONObject jsonObj) throws NetRequestException {
        goods_number = jsonObj.optInt("goods_number");
        train_id = jsonObj.optString("train_id");
        goods_name = jsonObj.optString("goods_name");
        short_name = jsonObj.optString("short_name");
        order_id = jsonObj.optString("order_id");
        order_status = jsonObj.optInt("order_status");
        shipping_fee = jsonObj.optString("shipping_fee");
        promotion_pic = jsonObj.optString("promotion_pic");
        promotion_price = jsonObj.optString("promotion_price");
        promotion_id = jsonObj.optInt("promotion_id");
        pay_status = jsonObj.optInt("pay_status");
        goods_amount = jsonObj.optString("goods_amount");
        order_amount = jsonObj.optString("order_amount");
        store_pic = jsonObj.optString("store_pic");
        shipping_status = jsonObj.optInt("shipping_status");
        order_sn = jsonObj.optString("order_sn");
        pay_id = jsonObj.optInt("pay_id");
        pay_name = jsonObj.optString("pay_name");
        pay_time = jsonObj.optLong("pay_time");
        add_time = jsonObj.optLong("add_time");
        confirm_time = jsonObj.optLong("confirm_time");
        consignee = jsonObj.optString("consignee");
        mobile = jsonObj.optString("mobile");
        address = jsonObj.optString("address");
        back_status = jsonObj.optInt("back_status");
        back_apply_time = jsonObj.optLong("back_apply_time");
        back_deal_time = jsonObj.optLong("back_deal_time");
        extension_code = jsonObj.optInt("extension_code");
        huanxin_username = jsonObj.optString("huanxin_username");
        qrcode = jsonObj.optString("qrcode");
        is_out = jsonObj.optInt("is_out");
        is_close = jsonObj.optInt("is_close");
        is_consume = jsonObj.optInt("is_consume");
        user_poster = jsonObj.optString("user_poster");
        nick_name = jsonObj.optString("nick_name");
        shipping_sn = jsonObj.optString("shipping_sn");
        consume_time = jsonObj.optLong("consume_time");
        return this;
    }

    public Object parseJSON1(JSONObject jsonObj) throws NetRequestException {
        goods_number = jsonObj.optInt("goods_number");
        train_id = jsonObj.optString("train_id");
        goods_name = jsonObj.optString("name");
        short_name = jsonObj.optString("short_name");
        order_id = jsonObj.optString("order_id");
        order_status = jsonObj.optInt("order_status");
        shipping_fee = jsonObj.optString("shipping_fee");
        promotion_pic = jsonObj.optString("promotion_pic");
        promotion_price = jsonObj.optString("promotion_price");
        promotion_id = jsonObj.optInt("promotion_id");
        pay_status = jsonObj.optInt("pay_status");
        goods_amount = jsonObj.optString("goods_amount");
        order_amount = jsonObj.optString("order_amount");
        store_pic = jsonObj.optString("store_pic");
        shipping_status = jsonObj.optInt("shipping_status");
        order_sn = jsonObj.optString("order_sn");
        pay_id = jsonObj.optInt("pay_id");
        pay_name = jsonObj.optString("pay_name");
        pay_time = jsonObj.optLong("pay_time");
        add_time = jsonObj.optLong("add_time");
        confirm_time = jsonObj.optLong("confirm_time");
        consignee = jsonObj.optString("consignee");
        mobile = jsonObj.optString("mobile");
        address = jsonObj.optString("address");
        back_status = jsonObj.optInt("back_status");
        back_apply_time = jsonObj.optLong("back_apply_time");
        back_deal_time = jsonObj.optLong("back_deal_time");
        extension_code = jsonObj.optInt("extension_code");
        huanxin_username = jsonObj.optString("huanxin_username");
        qrcode = jsonObj.optString("qrcode");
        is_out = jsonObj.optInt("is_out");
        is_close = jsonObj.optInt("is_close");
        is_consume = jsonObj.optInt("is_consume");
        user_poster = jsonObj.optString("user_poster");
        nick_name = jsonObj.optString("nick_name");
        shipping_sn = jsonObj.optString("shipping_sn");
        consume_time = jsonObj.optLong("consume_time");
        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGoods_number() {
        return goods_number;
    }

    public void setGoods_number(int goods_number) {
        this.goods_number = goods_number;
    }

    public String getTrain_id() {
        return train_id;
    }

    public void setTrain_id(String train_id) {
        this.train_id = train_id;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getShipping_fee() {
        return shipping_fee;
    }

    public void setShipping_fee(String shipping_fee) {
        this.shipping_fee = shipping_fee;
    }

    public String getPromotion_pic() {
        return promotion_pic;
    }

    public void setPromotion_pic(String promotion_pic) {
        this.promotion_pic = promotion_pic;
    }

    public String getPromotion_price() {
        return promotion_price;
    }

    public void setPromotion_price(String promotion_price) {
        this.promotion_price = promotion_price;
    }

    public int getPromotion_id() {
        return promotion_id;
    }

    public void setPromotion_id(int promotion_id) {
        this.promotion_id = promotion_id;
    }

    public int getPay_status() {
        return pay_status;
    }

    public void setPay_status(int pay_status) {
        this.pay_status = pay_status;
    }

    public String getGoods_amount() {
        return goods_amount;
    }

    public void setGoods_amount(String goods_amount) {
        this.goods_amount = goods_amount;
    }

    public String getOrder_amount() {
        return order_amount;
    }

    public void setOrder_amount(String order_amount) {
        this.order_amount = order_amount;
    }

    public String getStore_pic() {
        return store_pic;
    }

    public void setStore_pic(String store_pic) {
        this.store_pic = store_pic;
    }

    public int getShipping_status() {
        return shipping_status;
    }

    public void setShipping_status(int shipping_status) {
        this.shipping_status = shipping_status;
    }

    public String getOrder_sn() {
        return order_sn;
    }

    public void setOrder_sn(String order_sn) {
        this.order_sn = order_sn;
    }

    public int getPay_id() {
        return pay_id;
    }

    public void setPay_id(int pay_id) {
        this.pay_id = pay_id;
    }

    public String getPay_name() {
        return pay_name;
    }

    public void setPay_name(String pay_name) {
        this.pay_name = pay_name;
    }

    public int getOrder_status() {
        return order_status;
    }

    public void setOrder_status(int order_status) {
        this.order_status = order_status;
    }

    public long getPay_time() {
        return pay_time;
    }

    public void setPay_time(long pay_time) {
        this.pay_time = pay_time;
    }

    public long getConfirm_time() {
        return confirm_time;
    }

    public void setConfirm_time(long confirm_time) {
        this.confirm_time = confirm_time;
    }

    public long getAdd_time() {
        return add_time;
    }

    public void setAdd_time(long add_time) {
        this.add_time = add_time;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getBack_status() {
        return back_status;
    }

    public void setBack_status(int back_status) {
        this.back_status = back_status;
    }

    public long getBack_apply_time() {
        return back_apply_time;
    }

    public void setBack_apply_time(long back_apply_time) {
        this.back_apply_time = back_apply_time;
    }

    public long getBack_deal_time() {
        return back_deal_time;
    }

    public void setBack_deal_time(long back_deal_time) {
        this.back_deal_time = back_deal_time;
    }

    public int getExtension_code() {
        return extension_code;
    }

    public void setExtension_code(int extension_code) {
        this.extension_code = extension_code;
    }

    public String getHuanxin_username() {
        return huanxin_username;
    }

    public void setHuanxin_username(String huanxin_username) {
        this.huanxin_username = huanxin_username;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public int getIs_close() {
        return is_close;
    }

    public void setIs_close(int is_close) {
        this.is_close = is_close;
    }

    public int getIs_out() {
        return is_out;
    }

    public void setIs_out(int is_out) {
        this.is_out = is_out;
    }

    public int getIs_consume() {
        return is_consume;
    }

    public void setIs_consume(int is_consume) {
        this.is_consume = is_consume;
    }

    public String getUser_poster() {
        return user_poster;
    }

    public void setUser_poster(String user_poster) {
        this.user_poster = user_poster;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getShipping_sn() {
        return shipping_sn;
    }

    public void setShipping_sn(String shipping_sn) {
        this.shipping_sn = shipping_sn;
    }

    public Long getConsume_time() {
        return consume_time;
    }

    public void setConsume_time(Long consume_time) {
        this.consume_time = consume_time;
    }
}
