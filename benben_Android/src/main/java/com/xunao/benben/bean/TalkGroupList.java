package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class TalkGroupList extends BaseBean<TalkGroupList> {
	
	private ArrayList<TalkGroup> talkGroups ;

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TalkGroupList parseJSON(JSONObject jsonObj) throws NetRequestException {
		
		checkJson(jsonObj);
		JSONArray group_list = jsonObj.optJSONArray("group_list");
		if(group_list!=null&&group_list.length()>0){
			talkGroups = new ArrayList<TalkGroup>();
			TalkGroup  talkGroup;
			JSONObject jsonObject;
			for (int i = 0; i < group_list.length(); i++) {
				talkGroup = new TalkGroup();
				jsonObject = group_list.optJSONObject(i);
				
				talkGroup.parseJSON(jsonObject);
				talkGroups.add(talkGroup);
			}
		}
		
		return this;
	}

	public ArrayList<TalkGroup> getTalkGroups() {
		return talkGroups;
	}

	public void setTalkGroups(ArrayList<TalkGroup> talkGroups) {
		this.talkGroups = talkGroups;
	}

}
