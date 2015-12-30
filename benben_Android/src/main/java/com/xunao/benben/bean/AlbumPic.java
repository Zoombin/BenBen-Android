package com.xunao.benben.bean;

import com.lidroid.xutils.db.annotation.Transient;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

import org.json.JSONObject;

/**
 * Created by ltf on 2015/12/4.
 */
public class AlbumPic extends BaseBean {
    private int id;
    private int picid;
    private int activity_id;
    private String small_poster;
    private String poster;
    @Transient
    private boolean isChecked = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPicid() {
        return picid;
    }

    public void setPicid(int picid) {
        this.picid = picid;
    }

    public int getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(int activity_id) {
        this.activity_id = activity_id;
    }

    public String getSmall_poster() {
        return small_poster;
    }

    public void setSmall_poster(String small_poster) {
        this.small_poster = small_poster;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
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
        picid = jsonObj.optInt("picid");
        activity_id = jsonObj.optInt("activity_id");
        small_poster = jsonObj.optString("small_poster");
        poster = jsonObj.optString("poster");
        return this;
    }
}
