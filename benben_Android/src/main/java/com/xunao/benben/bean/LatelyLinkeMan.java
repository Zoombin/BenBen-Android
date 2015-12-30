package com.xunao.benben.bean;

import org.json.JSONObject;

import com.lidroid.xutils.db.annotation.Id;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class LatelyLinkeMan extends BaseBean {

	private int id;
    private int cid;
	private String linkeManName;
	private String linkeManPhone;
	private long latelyTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LatelyLinkeMan() {
		super();
	}

	public LatelyLinkeMan(int cid,String linkeManName, String linkeManPhone,
			long latelyTime) {
		super();
        this.cid = cid;
		this.linkeManName = linkeManName;
		this.linkeManPhone = linkeManPhone;
		this.latelyTime = latelyTime;
	}

	public String getLinkeManName() {
		return linkeManName;
	}

	public void setLinkeManName(String linkeManName) {
		this.linkeManName = linkeManName;
	}

	public String getLinkeManPhone() {
		return linkeManPhone;
	}

	public void setLinkeManPhone(String linkeManPhone) {
		this.linkeManPhone = linkeManPhone;
	}

	public long getLatelyTime() {
		return latelyTime;
	}

	public void setLatelyTime(long latelyTime) {
		this.latelyTime = latelyTime;
	}

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    @Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public Object parseJSON(JSONObject jsonObj) throws NetRequestException {
		return null;
	}

}
