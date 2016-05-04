package com.xunao.benben.bean;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

import org.json.JSONObject;

/**
 * Created by ltf on 2016/5/3.
 */
public class TrainPublic extends BaseBean{
    private String id;
    private String type;
    private int sender_id;
    private String content;
    private String identity1;
    private String created_time;
    private String pic1;
    private String pic2;
    private String pic3;
    private String Images="";// 使用||分割

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public Object parseJSON(JSONObject jsonObj) throws NetRequestException {
        id = jsonObj.optString("id");
        type = jsonObj.optString("type");
        sender_id = jsonObj.optInt("sender_id");
        content = jsonObj.optString("content");
        identity1 = jsonObj.optString("identity1");
        created_time = jsonObj.optString("created_time");
        pic1 = jsonObj.optString("pic1");
        pic2 = jsonObj.optString("pic2");
        pic3 = jsonObj.optString("pic3");

        if(!pic1.equals("")){
            Images += pic1+"^";
        }
        if(!pic2.equals("")){
            Images += pic2+"^";
        }
        if(!pic3.equals("")){
            Images += pic3+"^";
        }
        if(!Images.equals("")){
            Images = Images.substring(0,Images.length()-1);
        }
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIdentity1() {
        return identity1;
    }

    public void setIdentity1(String identity1) {
        this.identity1 = identity1;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public String getPic1() {
        return pic1;
    }

    public void setPic1(String pic1) {
        this.pic1 = pic1;
    }

    public String getPic2() {
        return pic2;
    }

    public void setPic2(String pic2) {
        this.pic2 = pic2;
    }

    public String getPic3() {
        return pic3;
    }

    public void setPic3(String pic3) {
        this.pic3 = pic3;
    }

    public String getImages() {
        return Images;
    }

    public void setImages(String images) {
        Images = images;
    }
}
