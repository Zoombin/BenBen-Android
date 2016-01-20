package com.xunao.test.bean.tx;

import org.json.JSONObject;

import com.easemob.chat.EMConversation;
import com.xunao.test.base.BaseBean;
import com.xunao.test.exception.NetRequestException;

public class BenbenEMConversation extends BaseBean {

	public static int PUBLIC = 0;
	public static int NOMAL = 1;

	private int type;
	private int publicNum;
    private int buyNum;
	EMConversation mEMConversation;

	public int getType() {
		return type;
	}

	public int getPublicNum() {
		return publicNum;
	}

	public void setPublicNum(int publicNum) {
		this.publicNum = publicNum;
	}

    public int getBuyNum() {
        return buyNum;
    }

    public void setBuyNum(int buyNum) {
        this.buyNum = buyNum;
    }

    public void setType(int type) {
		this.type = type;
	}

	public EMConversation getmEMConversation() {
		return mEMConversation;
	}

	public void setmEMConversation(EMConversation mEMConversation) {
		this.mEMConversation = mEMConversation;
	}

	public BenbenEMConversation(int type, EMConversation mEMConversation) {
		super();
		this.type = type;
		this.mEMConversation = mEMConversation;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object parseJSON(JSONObject jsonObj) throws NetRequestException {
		// TODO Auto-generated method stub
		return null;
	}

}
