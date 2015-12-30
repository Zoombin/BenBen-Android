package com.xunao.benben.base;

import java.io.Serializable;

import org.json.JSONObject;

import com.lidroid.xutils.db.annotation.Transient;
import com.xunao.benben.base.Error;
import com.xunao.benben.exception.NetRequestException;

public abstract class BaseBean<T> implements Serializable {

	@Transient
	public boolean isCancle = true;

	public boolean checkJson(JSONObject jsonObject) throws NetRequestException {
		int ret_num = jsonObject.optInt("ret_num");

		if (ret_num == 0) {
			return true;
		} else {
			String errorMsg = jsonObject.optString("ret_msg");
			Error error = new Error();
			error.parseJSON(jsonObject);
			
			throw new NetRequestException(error);
		}
	}

	/**
	 * 将Bean实例转化为json对象
	 * 
	 * @return
	 */
	public abstract JSONObject toJSON();

	/**
	 * 将json对象转化为Bean实例
	 * 
	 * @param jsonObj
	 * @return
	 */
	public abstract Object parseJSON(JSONObject jsonObj) throws NetRequestException;

}
