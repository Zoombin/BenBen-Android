package com.xunao.benben.bean;

import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class SuccessMsg extends BaseBean<SuccessMsg> {

	private String msg;
	
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SuccessMsg parseJSON(JSONObject jsonObj) throws NetRequestException {
		checkJson(jsonObj);
		msg = jsonObj.optString("msg");
		return this;
		
	}

}
