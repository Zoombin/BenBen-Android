package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Transient;
import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class EnterpriseMember extends BaseBean<EnterpriseMember> {
    @Id
    private int a_id;
	private String id;
	private String filter;
	private int maxShow;
	private ArrayList<EnterpriseMemberGroup> enterpriseMemberDetails=new ArrayList<>();
	private int commontCounts;
    private String eid;
    private String benben_id;
    @Transient
    private int firstin;
    @Transient
    private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public int getMaxShow() {
		return maxShow;
	}

	public void setMaxShow(int maxShow) {
		this.maxShow = maxShow;
	}

	public ArrayList<EnterpriseMemberGroup> getEnterpriseMemberDetails() {
		return enterpriseMemberDetails;
	}

	public void setEnterpriseMemberDetails(
			ArrayList<EnterpriseMemberGroup> enterpriseMemberDetails) {
		this.enterpriseMemberDetails = enterpriseMemberDetails;
	}

	public int getCommontCounts() {
		return commontCounts;
	}

	public void setCommontCounts(int commontCounts) {
		this.commontCounts = commontCounts;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public EnterpriseMember parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		id = jsonObj.optString("id");
		filter = jsonObj.optString("filter");
        name = jsonObj.optString("name");
		maxShow = jsonObj.optInt("max_show");
        firstin = jsonObj.optInt("firstin");
		commontCounts = jsonObj.optInt("common_count");
		JSONArray optJSONArray = jsonObj.optJSONArray("member_ginfo");

		if (optJSONArray != null) {
			int length = optJSONArray.length();
			enterpriseMemberDetails = new ArrayList<EnterpriseMemberGroup>();
			EnterpriseMemberGroup memberDetail = null;

			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					memberDetail = new EnterpriseMemberGroup();
					memberDetail.parseJSON(optJSONObject);
					enterpriseMemberDetails.add(memberDetail);
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

    public int getFirstin() {
        return firstin;
    }

    public void setFirstin(int firstin) {
        this.firstin = firstin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
