package com.xunao.benben.bean;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

import org.json.JSONObject;

import java.util.ArrayList;

public class OrderCommentReply extends BaseBean<OrderCommentReply>{
	private int id;
    private int comment_id;
    private String content;
    private int is_seller;
    private int comment_rank;
    private String huanxin_username;
    private long add_time;
    private int comment_type;
    private int promotion_id;

	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrderCommentReply parseJSON(JSONObject jsonObj)
			throws NetRequestException {
        comment_id = jsonObj.optInt("comment_id");
        promotion_id = jsonObj.optInt("promotion_id");
        content = jsonObj.optString("content");
        huanxin_username = jsonObj.optString("huanxin_username");
        is_seller = jsonObj.optInt("is_seller");
        comment_rank = jsonObj.optInt("comment_rank");
        add_time = jsonObj.optLong("add_time");
        comment_type = jsonObj.optInt("comment_type");
        return this;
	}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIs_seller() {
        return is_seller;
    }

    public void setIs_seller(int is_seller) {
        this.is_seller = is_seller;
    }

    public int getComment_rank() {
        return comment_rank;
    }

    public void setComment_rank(int comment_rank) {
        this.comment_rank = comment_rank;
    }

    public String getHuanxin_username() {
        return huanxin_username;
    }

    public void setHuanxin_username(String huanxin_username) {
        this.huanxin_username = huanxin_username;
    }

    public long getAdd_time() {
        return add_time;
    }

    public void setAdd_time(long add_time) {
        this.add_time = add_time;
    }

    public int getComment_type() {
        return comment_type;
    }

    public void setComment_type(int comment_type) {
        this.comment_type = comment_type;
    }

    public int getPromotion_id() {
        return promotion_id;
    }

    public void setPromotion_id(int promotion_id) {
        this.promotion_id = promotion_id;
    }
}
