package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.R.bool;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class EnterpriseVirtualMemberGroup extends BaseBean<EnterpriseVirtualMemberGroup> {
	private String id;
	private String name;
	private ArrayList<EnterpriseVirtualMember> virtualMembers;
	
	
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

	@Override
	public JSONObject toJSON() {
		return null;
	}

	public ArrayList<EnterpriseVirtualMember> getVirtualMembers() {
		return virtualMembers;
	}

	public void setVirtualMembers(ArrayList<EnterpriseVirtualMember> virtualMembers) {
		this.virtualMembers = virtualMembers;
	}

	@Override
	public EnterpriseVirtualMemberGroup parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		id = jsonObj.optString("id");
		name = jsonObj.optString("name");
		
		JSONArray optJSONArray = jsonObj.optJSONArray("member");

		if (optJSONArray != null) {
			int length = optJSONArray.length();
			virtualMembers = new ArrayList<EnterpriseVirtualMember>();
			EnterpriseVirtualMember enterprise;
			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					enterprise = new EnterpriseVirtualMember();
					enterprise.parseJSON(optJSONObject);
					virtualMembers.add(enterprise);
				}
			}

		}
		
		return this;
	}

}
