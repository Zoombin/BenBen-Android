package com.xunao.test.bean;

import org.json.JSONObject;

import com.xunao.test.base.BaseBean;
import com.xunao.test.exception.NetRequestException;

public class EnterpriseMemberInviteList extends
		BaseBean<EnterpriseMemberInviteList> {

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EnterpriseMemberInviteList parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		return this;
	}

}
