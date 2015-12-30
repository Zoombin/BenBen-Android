package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;
import com.lidroid.xutils.db.annotation.Id;

public class EnterpriseMemberGroup extends BaseBean<EnterpriseMemberGroup> {
    @Id
    private int a_id;
	private String id;
	private String groupName;
	private String number;
    private String eid;
    private String benben_id;

	private boolean isClicked = false;
	
	private ArrayList<EnterpriseMemberDetail> memberDetails;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public ArrayList<EnterpriseMemberDetail> getMemberDetails() {
		return memberDetails;
	}

	public void setMemberDetails(ArrayList<EnterpriseMemberDetail> memberDetails) {
		this.memberDetails = memberDetails;
	}

	public boolean isClicked() {
		return isClicked;
	}

	public void setClicked(boolean isClicked) {
		this.isClicked = isClicked;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public EnterpriseMemberGroup parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		id = jsonObj.optString("id");
		groupName = jsonObj.optString("groupname");
		number = jsonObj.optString("number");
		JSONArray optJSONArray = jsonObj.optJSONArray("member_info");

		if (optJSONArray != null) {
			int length = optJSONArray.length();
			memberDetails = new ArrayList<EnterpriseMemberDetail>();
			EnterpriseMemberDetail memberDetail = null;

			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					memberDetail = new EnterpriseMemberDetail();
					memberDetail.parseJSON(optJSONObject);
					memberDetails.add(memberDetail);
				}
			}
		}
		return this;
	}

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getBenben_id() {
        return benben_id;
    }

    public void setBenben_id(String benben_id) {
        this.benben_id = benben_id;
    }
}
