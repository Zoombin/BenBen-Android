package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class GroupAddMembers extends BaseBean {

	@Id
	@NoAutoIncrement
	private int id;
	private String name;
	private boolean select = false;

	ArrayList<GroupAddMember> mGroupAddMember = new ArrayList<GroupAddMember>();

	public ArrayList<GroupAddMember> getmGroupAddMember() {
		return mGroupAddMember;
	}

	public void setmGroupAddMember(ArrayList<GroupAddMember> mGroupAddMember) {
		this.mGroupAddMember = mGroupAddMember;
	}

	public boolean isSelect() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object parseJSON(JSONObject jsonObj) throws NetRequestException {

		id = jsonObj.optInt("id");
		name = jsonObj.optString("name");
		JSONArray optJSONArray = jsonObj.optJSONArray("member");
		if (optJSONArray != null && optJSONArray.length() > 0) {
			mGroupAddMember = new ArrayList<GroupAddMember>();
			GroupAddMember member = null;
			for (int i = 0; i < optJSONArray.length(); i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					member = new GroupAddMember();
					member.parseJSON(optJSONObject);
					mGroupAddMember.add(member);
				}
			}

		}
		return this;
	}

}
