package com.xunao.test.bean;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.xunao.test.base.BaseBean;
import com.xunao.test.exception.NetRequestException;

import org.json.JSONObject;

public class NewMsgAboutMe extends BaseBean<NewMsgAboutMe> {

	@Id @NoAutoIncrement
	private String id;
    private int mid;
	private String member_id;
	private String description;
	private long created_time;
    private String huanxin_username;
    private String nick_name;
    private String poster;
    private int replier;
    private String review;
    private int type;
    private boolean is_new;
    private String benben_id;



    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreated_time() {
        return created_time;
    }

    public void setCreated_time(long created_time) {
        this.created_time = created_time;
    }

    public String getHuanxin_username() {
        return huanxin_username;
    }

    public void setHuanxin_username(String huanxin_username) {
        this.huanxin_username = huanxin_username;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public int getReplier() {
        return replier;
    }

    public void setReplier(int replier) {
        this.replier = replier;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isIs_new() {
        return is_new;
    }

    public void setIs_new(boolean is_new) {
        this.is_new = is_new;
    }

    public String getBenben_id() {
        return benben_id;
    }

    public void setBenben_id(String benben_id) {
        this.benben_id = benben_id;
    }

    @Override
	public JSONObject toJSON() {
		return null;
	}



	// Comment
	@Override
	public Object parseJSON(JSONObject jsonObj) throws NetRequestException {

		return null;
	}
}
