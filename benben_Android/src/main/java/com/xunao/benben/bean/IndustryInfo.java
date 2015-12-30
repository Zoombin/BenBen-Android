package com.xunao.benben.bean;

import org.json.JSONObject;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class IndustryInfo extends BaseBean<IndustryInfo> {
    @Id
    @NoAutoIncrement
	private String id;
	private String name;
	private String parentId;
	private String level;
    private String last;
	
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    @Override
	public JSONObject toJSON() {
		
		return null;
	}

	@Override
	public IndustryInfo parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		id = jsonObj.optString("id");
		name = jsonObj.optString("name");
		parentId = jsonObj.optString("parent_id");
		level = jsonObj.optString("level");
        last = jsonObj.optString("last");
		return this;
	}

}
