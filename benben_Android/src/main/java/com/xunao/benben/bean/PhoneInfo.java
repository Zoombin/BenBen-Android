package com.xunao.benben.bean;

import org.json.JSONObject;

import com.lidroid.xutils.db.annotation.Transient;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.exception.NetRequestException;

public class PhoneInfo extends BaseBean<PhoneInfo> {

	private int id;
	private String phone;
	private String is_benben;
	private String is_baixing;
	private String poster;
	private String name;
	private String nick_name;
	private int contacts_id;
	private String huanxin_username;
    private String is_active;
    private String pid;

    private String tag;
    private String train_id;
    private String pic;
    private String short_name;
    private String leg_name;
    private String leg_poster;
    private String leg_district;
    private String legid;
    private String type;




	@Transient
	private boolean isSeleck;

	public boolean isSeleck() {
		return isSeleck;
	}

	public void setSeleck(boolean isSeleck) {
		this.isSeleck = isSeleck;
	}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHuanxin_username() {
		return huanxin_username;
	}

	public void setHuanxin_username(String huanxin_username) {
		this.huanxin_username = huanxin_username;
	}

	public PhoneInfo() {
		super();
	}

	public PhoneInfo(String name) {
		super();
		this.name = name;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PhoneInfo parseJSON(JSONObject jsonObj) throws NetRequestException {
        pid = jsonObj.optString("id");
		phone = jsonObj.optString("phone");
		is_benben = jsonObj.optString("is_benben");
		is_baixing = jsonObj.optString("is_baixing");
		poster = jsonObj.optString("poster");
		nick_name = jsonObj.optString("nick_name");
		huanxin_username = jsonObj.optString("huanxin_username");
        is_active = jsonObj.optString("is_active");

        tag = jsonObj.optString("tag");
        train_id = jsonObj.optString("train_id");
        pic = jsonObj.optString("pic");
        short_name = jsonObj.optString("short_name");
        leg_name = jsonObj.optString("leg_name");
        leg_poster = jsonObj.optString("leg_poster");
        leg_district = jsonObj.optString("leg_district");
        legid = jsonObj.optString("legid");
        type =  jsonObj.optString("type");
		return this;
	}

	public PhoneInfo parseJSONAddNum(JSONObject jsonObj)
			throws NetRequestException {
//        id = jsonObj.optInt("id");
		phone = jsonObj.optString("phone");
		is_benben = jsonObj.optString("is_benben");
		is_baixing = jsonObj.optString("is_baixing");
		poster = jsonObj.optString("poster");
		nick_name = jsonObj.optString("nick_name");
		huanxin_username = jsonObj.optString("huanxin_username");
		return this;
	}

    public PhoneInfo parseUpdateJSON(JSONObject jsonObj) throws NetRequestException {
        pid = jsonObj.optString("id");
        phone = jsonObj.optString("phone");
        is_benben = jsonObj.optString("is_benben");
        is_baixing = jsonObj.optString("is_baixing");
        poster = jsonObj.optString("poster");
        nick_name = jsonObj.optString("nick_name");
        huanxin_username = jsonObj.optString("huanxin_username");
        is_active = jsonObj.optString("is_active");
        contacts_id = jsonObj.optInt("contact_info_id");
        tag = jsonObj.optString("tag");
        train_id = jsonObj.optString("train_id");
        pic = jsonObj.optString("pic");
        short_name = jsonObj.optString("short_name");
        leg_name = jsonObj.optString("leg_name");
        leg_poster = jsonObj.optString("leg_poster");
        leg_district = jsonObj.optString("leg_district");
        legid = jsonObj.optString("legid");
        type =  jsonObj.optString("type");
        return this;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getIs_benben() {
		return is_benben;
	}

	public void setIs_benben(String is_benben) {
		this.is_benben = is_benben;
	}

	public String getIs_baixing() {
		return is_baixing;
	}

	public void setIs_baixing(String is_baixing) {
		this.is_baixing = is_baixing;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

    public int getContacts_id() {
        return contacts_id;
    }

    public void setContacts_id(int contacts_id) {
        this.contacts_id = contacts_id;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTrain_id() {
        return train_id;
    }

    public void setTrain_id(String train_id) {
        this.train_id = train_id;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public String getLeg_name() {
        return leg_name;
    }

    public void setLeg_name(String leg_name) {
        this.leg_name = leg_name;
    }

    public String getLeg_poster() {
        return leg_poster;
    }

    public void setLeg_poster(String leg_poster) {
        this.leg_poster = leg_poster;
    }

    public String getLeg_district() {
        return leg_district;
    }

    public void setLeg_district(String leg_district) {
        this.leg_district = leg_district;
    }

    public String getLegid() {
        return legid;
    }

    public void setLegid(String legid) {
        this.legid = legid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
	public boolean equals(Object o) {
		PhoneInfo contacts = (PhoneInfo) o;
		if (this.getPhone().equals(contacts.getPhone())) {
			return true;
		} else {
			return false;
		}
	}

}
