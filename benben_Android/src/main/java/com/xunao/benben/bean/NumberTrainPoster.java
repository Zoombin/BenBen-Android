package com.xunao.benben.bean;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

import org.json.JSONObject;

public class NumberTrainPoster extends BaseBean<NumberTrainPoster>{
	private int id;
    private int pid;
    private String poster;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    @Override
	public JSONObject toJSON() {
	
		return null;
	}

	@Override
	public NumberTrainPoster parseJSON(JSONObject jsonObj) throws NetRequestException {
	
		pid = jsonObj.optInt("id");
        poster = jsonObj.optString("pic");
		return this;
	}

}
