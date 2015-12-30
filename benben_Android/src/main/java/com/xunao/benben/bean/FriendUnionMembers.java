package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class FriendUnionMembers extends BaseBean<FriendUnionMembers> {
	private FriendUnionChief friendUnionChief;
	private ArrayList<FriendUnionMember> friendUnionMember;
	private ArrayList<FriendUnionOtherChief> friendUnionOtherChief;
	private String chief_member_count;
    private int remain_chief;
    private int remain_num;

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public FriendUnionMembers parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		checkJson(jsonObj);
		chief_member_count = jsonObj.optString("chief_member_count");
        remain_chief = jsonObj.optInt("remain_chief");
        remain_num = jsonObj.optInt("remain_num");

		friendUnionChief = new FriendUnionChief();
		friendUnionChief.parseJSON(jsonObj.optJSONObject("chief"));
		
		JSONArray optJSONArray = jsonObj.optJSONArray("chief_member");
		JSONArray optJSONArray2 = jsonObj.optJSONArray("other_chief");
		friendUnionOtherChief = new ArrayList<FriendUnionOtherChief>();
		
		if (optJSONArray != null && optJSONArray.length() >= 0) {
			friendUnionMember = new ArrayList<FriendUnionMember>();
			FriendUnionMember friendUser = null;
			int length = optJSONArray.length();
			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);
				if (optJSONObject != null) {
					friendUser = new FriendUnionMember();
					friendUser.parseJSON(optJSONObject);
					friendUnionMember.add(friendUser);
				}
			}
		}
		
		FriendUnionOtherChief otherChieUnionOtherChief = new FriendUnionOtherChief();
		otherChieUnionOtherChief.setType(1);
		otherChieUnionOtherChief.setMembers(friendUnionMember);
		otherChieUnionOtherChief.setName("盟主成员");
		friendUnionOtherChief.add(otherChieUnionOtherChief);
		
		FriendUnionOtherChief otherChief = new FriendUnionOtherChief();
		otherChief.setType(2);
		ArrayList<FriendUnionMember> members = new ArrayList<FriendUnionMember>();
		otherChief.setMembers(members);
		friendUnionOtherChief.add(otherChief);
		
		
		if (optJSONArray2 != null && optJSONArray2.length() >= 0) {
			FriendUnionOtherChief friendUser = null;
			int length = optJSONArray2.length();
			for (int i = 0; i < length; i++) {
				JSONObject optJSONObject = optJSONArray2.optJSONObject(i);
				if (optJSONObject != null) {
					friendUser = new FriendUnionOtherChief();
					friendUser.parseJSON(optJSONObject);
					friendUser.setType(3);
					friendUnionOtherChief.add(friendUser);
				}
			}
		}
		
		
		return this;
	}


    public FriendUnionMembers parseJSON1(JSONObject jsonObj)
            throws NetRequestException {
        checkJson(jsonObj);
        chief_member_count = jsonObj.optString("chief_member_count");
        remain_chief = jsonObj.optInt("remain_chief");
        remain_num = jsonObj.optInt("remain_num");

        friendUnionChief = new FriendUnionChief();
        friendUnionChief.parseJSON(jsonObj.optJSONObject("chief"));

        JSONArray optJSONArray = jsonObj.optJSONArray("chief_member");
        JSONArray optJSONArray2 = jsonObj.optJSONArray("other_chief");
        friendUnionOtherChief = new ArrayList<FriendUnionOtherChief>();

        if (optJSONArray != null && optJSONArray.length() >= 0) {
            friendUnionMember = new ArrayList<FriendUnionMember>();
            FriendUnionMember friendUser = null;
            int length = optJSONArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject optJSONObject = optJSONArray.optJSONObject(i);
                if (optJSONObject != null) {
                    friendUser = new FriendUnionMember();
                    friendUser.parseJSON(optJSONObject);
                    friendUnionMember.add(friendUser);
                }
            }
        }

        FriendUnionOtherChief otherChieUnionOtherChief = new FriendUnionOtherChief();
        otherChieUnionOtherChief.setType(1);
        otherChieUnionOtherChief.setMembers(friendUnionMember);
        otherChieUnionOtherChief.setName("盟主成员");
        friendUnionOtherChief.add(otherChieUnionOtherChief);

//        FriendUnionOtherChief otherChief = new FriendUnionOtherChief();
//        otherChief.setType(2);
//        ArrayList<FriendUnionMember> members = new ArrayList<FriendUnionMember>();
//        otherChief.setMembers(members);
//        friendUnionOtherChief.add(otherChief);


        if (optJSONArray2 != null && optJSONArray2.length() >= 0) {
            FriendUnionOtherChief friendUser = null;
            int length = optJSONArray2.length();
            for (int i = 0; i < length; i++) {
                JSONObject optJSONObject = optJSONArray2.optJSONObject(i);
                if (optJSONObject != null) {
                    friendUser = new FriendUnionOtherChief();
                    friendUser.parseJSON(optJSONObject);
                    friendUser.setType(3);
                    friendUnionOtherChief.add(friendUser);
                }
            }
        }


        return this;
    }

	public FriendUnionChief getFriendUnionChief() {
		return friendUnionChief;
	}

	public void setFriendUnionChief(FriendUnionChief friendUnionChief) {
		this.friendUnionChief = friendUnionChief;
	}

	public ArrayList<FriendUnionMember> getFriendUnionMember() {
		return friendUnionMember;
	}

	public void setFriendUnionMember(
			ArrayList<FriendUnionMember> friendUnionMember) {
		this.friendUnionMember = friendUnionMember;
	}

	public ArrayList<FriendUnionOtherChief> getFriendUnionOtherChief() {
		return friendUnionOtherChief;
	}

	public void setFriendUnionOtherChief(
			ArrayList<FriendUnionOtherChief> friendUnionOtherChief) {
		this.friendUnionOtherChief = friendUnionOtherChief;
	}

	public String getChief_member_count() {
		return chief_member_count;
	}

	public void setChief_member_count(String chief_member_count) {
		this.chief_member_count = chief_member_count;
	}

    public int getRemain_chief() {
        return remain_chief;
    }

    public void setRemain_chief(int remain_chief) {
        this.remain_chief = remain_chief;
    }

    public int getRemain_num() {
        return remain_num;
    }

    public void setRemain_num(int remain_num) {
        this.remain_num = remain_num;
    }
}
