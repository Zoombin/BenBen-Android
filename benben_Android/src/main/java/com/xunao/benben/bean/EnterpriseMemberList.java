package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class EnterpriseMemberList extends BaseBean<EnterpriseMemberList> {

	private ArrayList<EnterpriseMember> enterpriseMembers;
	private ArrayList<EnterpriseMemberDetail> enterpriseMemberDetails;
	
	
	public ArrayList<EnterpriseMember> getEnterpriseMembers() {
		return enterpriseMembers;
	}

	public void setEnterpriseMembers(ArrayList<EnterpriseMember> enterpriseMembers) {
		this.enterpriseMembers = enterpriseMembers;
	}

	public ArrayList<EnterpriseMemberDetail> getEnterpriseMemberDetails() {
		return enterpriseMemberDetails;
	}

	public void setEnterpriseMemberDetails(
			ArrayList<EnterpriseMemberDetail> enterpriseMemberDetails) {
		this.enterpriseMemberDetails = enterpriseMemberDetails;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public EnterpriseMemberList parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		checkJson(jsonObj);

		JSONArray optJSONArray = jsonObj.optJSONArray("member_info");

		if (optJSONArray != null && optJSONArray.length() > 0) {
			enterpriseMembers = new ArrayList<EnterpriseMember>();
			EnterpriseMember eMember;
			for (int i = 0; i < optJSONArray.length(); i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					eMember = new EnterpriseMember();
					eMember.parseJSON(optJSONObject);
					enterpriseMembers.add(eMember);
				}
			}
		}
		
		return this;
	}
	
	
	public EnterpriseMemberList parseJSON2(JSONObject jsonObj)
			throws NetRequestException {
		checkJson(jsonObj);

		JSONArray optJSONArray = jsonObj.optJSONArray("member_info");

		if (optJSONArray != null && optJSONArray.length() > 0) {
			enterpriseMemberDetails = new ArrayList<EnterpriseMemberDetail>();
			EnterpriseMemberDetail eMember;
			for (int i = 0; i < optJSONArray.length(); i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					eMember = new EnterpriseMemberDetail();
					eMember.parseJSON(optJSONObject);
					enterpriseMemberDetails.add(eMember);
				}
			}
		}
		
		return this;
	}

}
