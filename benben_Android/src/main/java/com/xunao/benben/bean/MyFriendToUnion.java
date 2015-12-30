package com.xunao.benben.bean;

import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class MyFriendToUnion extends BaseBean<MyFriendToUnion> {
	private String id;
	private String groupId;
	private String name;
	private String poster;
	private String groupName;
	private boolean isGroup; // 记录分组出现的位置
	
	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public MyFriendToUnion parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		return null;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public boolean isGroup() {
		return isGroup;
	}

	public void setGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}

}
