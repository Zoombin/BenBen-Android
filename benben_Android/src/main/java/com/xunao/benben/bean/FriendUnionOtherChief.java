package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lidroid.xutils.db.annotation.Transient;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class FriendUnionOtherChief extends BaseBean<FriendUnionOtherChief>{
	private String name;
	private ArrayList<FriendUnionMember> members;
	private int type;
	private int right;
	private String id;
	private int MemberCount;
    @Transient
    private boolean isSelect  = false; // 是否选中
	
	public int getRight() {
		return right;
	}

	public void setRight(int right) {
		this.right = right;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public FriendUnionOtherChief parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		name = jsonObj.optString("name");
		right = jsonObj.optInt("right");
		id = jsonObj.optString("id");
		MemberCount = jsonObj.optInt("member_count");
		
		JSONArray optJSONArray = jsonObj.optJSONArray("member");
		
		
		if (optJSONArray != null && optJSONArray.length() >= 0) {
			members = new ArrayList<FriendUnionMember>();
			FriendUnionMember friendUser = null;
			int length = optJSONArray.length();
			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					friendUser = new FriendUnionMember();
					friendUser.parseJSON(optJSONObject);
					members.add(friendUser);
				}
			}
		}
		
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<FriendUnionMember> getMembers() {
		return members;
	}

	public void setMembers(ArrayList<FriendUnionMember> members) {
		this.members = members;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getMemberCount() {
		return MemberCount;
	}

	public void setMemberCount(int memberCount) {
		MemberCount = memberCount;
	}

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }
}
