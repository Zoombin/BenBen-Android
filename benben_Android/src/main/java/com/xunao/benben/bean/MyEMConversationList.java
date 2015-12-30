package com.xunao.benben.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.lidroid.xutils.exception.DbException;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.exception.NetRequestException;

public class MyEMConversationList extends BaseBean {

	public HashMap<String, MyEMConversation> MyEMConversationMap=new HashMap<String, MyEMConversation>();

	@Override
	public Object parseJSON(JSONObject jsonObj) throws NetRequestException {

		checkJson(jsonObj);

		JSONArray optJSONArray = jsonObj.optJSONArray("member_info");

		if (optJSONArray != null && optJSONArray.length() > 0) {
			int length = optJSONArray.length();
			MyEMConversation conversation = null;
			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					conversation = new MyEMConversation();
					conversation.parseJSON(optJSONObject);
					try {
						CrashApplication.getInstance().getDb()
								.save(conversation);
					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					MyEMConversationMap.put(conversation.getHuanxin_username(),
							conversation);
				}
			}
		}

		return this;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

}
