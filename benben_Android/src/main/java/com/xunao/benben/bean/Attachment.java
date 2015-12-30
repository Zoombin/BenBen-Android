package com.xunao.benben.bean;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

import org.json.JSONObject;

public class Attachment extends BaseBean<Attachment> {
	private int id;
	private int type;
	private int broadcast_id;
	private String attachment;
    private String height;
    private String width;
    private String thumb;


	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Attachment parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		id = jsonObj.optInt("id");
        type = jsonObj.optInt("type");
        broadcast_id = jsonObj.optInt("broadcast_id");
        attachment = jsonObj.optString("attachment");
        height = jsonObj.optString("height");
        width = jsonObj.optString("width");
        thumb = jsonObj.optString("thumb");
		return this;
	}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getBroadcast_id() {
        return broadcast_id;
    }

    public void setBroadcast_id(int broadcast_id) {
        this.broadcast_id = broadcast_id;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
}
