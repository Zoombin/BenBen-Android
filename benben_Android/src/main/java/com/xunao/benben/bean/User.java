package com.xunao.benben.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Transient;
import com.lidroid.xutils.exception.DbException;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.exception.NetRequestException;

public class User extends BaseBean<User> {

	private ArrayList<TalkGroup> talkGroups;

	@Id
	@NoAutoIncrement
	private int id;
	private int league;
	private String Name;
	private String UserNickname;
	private String BenbenId;
	private String Poster;
	private String Address;
	private String ProCity;
	private String BaiXing;
	private String Cornet;
	private String Integral;
	private String Coin;
	private String UserSex;
	private String Age;
	private String Phone;
	private String Token;
	private String huanxin_username;
	private String huanxin_password;
	private String huanxin_uuid;
	private long birthday;
	private String userInfo;
	private String userQrCode;
	private String level;
	private String appellation;
	private int sysLeague;
	
	private int league_disable;
	private int creation_disable;
	private int buy_disable;
	private int enterprise_disable;
	private int group_disable;
	private int store_disable;


	
	public int getSysLeague() {
		return sysLeague;
	}

	public void setSysLeague(int sysLeague) {
		this.sysLeague = sysLeague;
	}

	private int zhiId;
	private String zhiName;
	private String zhiShortName;
	private boolean isRegLogin = false;
	public boolean isRegLogin() {
		return isRegLogin;
	}

	public void setRegLogin(boolean isRegLogin) {
		this.isRegLogin = isRegLogin;
	}

	private boolean isUpdate = false;

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	public String parseJSONPoster(JSONObject jsonObj)
			throws NetRequestException {
		checkJson(jsonObj);
		Poster = jsonObj.optString("poster");
		try {
			CrashApplication.getInstance().getDb().saveOrUpdate(this);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Poster;
	}

	@Override
	public User parseJSON(JSONObject jsonObj) throws NetRequestException {

		checkJson(jsonObj);

		JSONObject jsonObject = jsonObj.optJSONObject("user");
		JSONObject jsonObjectZhitongche = jsonObject
				.optJSONObject("ZhiTongChe");

		id = jsonObject.optInt("UserId");
		league = jsonObject.optInt("league");
		Name = jsonObject.optString("name");
		
		sysLeague = jsonObject.optInt("league");

		BenbenId = jsonObject.optString("BenbenId");
		Poster = jsonObject.optString("Poster");

		if (TextUtils.isEmpty(Poster)) {
			Poster = jsonObject.optString("UserPoster");
		}

		Address = jsonObject.optString("Address");
		ProCity = jsonObject.optString("ProCity");
		BaiXing = jsonObject.optString("BaiXing");
		Cornet = jsonObject.optString("Cornet");
		Integral = jsonObject.optString("integral");
		Coin = jsonObject.optString("Coin");
		appellation = jsonObject.optString("appellation");
		
		league_disable = jsonObject.optInt("league_disable");
		creation_disable = jsonObject.optInt("creation_disable");
		buy_disable = jsonObject.optInt("buy_disable");
		enterprise_disable = jsonObject.optInt("enterprise_disable");
		group_disable = jsonObject.optInt("group_disable");
		store_disable = jsonObject.optInt("store_disable");

		UserNickname = jsonObject.optString("UserNickname");
		UserSex = jsonObject.optString("UserSex");
		Age = jsonObject.optString("Age");
		birthday = jsonObject.optLong("Age");
		Phone = jsonObject.optString("Phone");
		Token = jsonObject.optString("Token");
		userInfo = jsonObject.optString("UserInfo");
		level = jsonObject.optString("level");

		userQrCode = jsonObject.optString("UserQrcode");

		huanxin_username = jsonObject.optString("huanxin_username");
		huanxin_password = jsonObject.optString("huanxin_password");
		huanxin_uuid = jsonObject.optString("huanxin_uuid");

		if (jsonObjectZhitongche != null) {
			zhiId = jsonObjectZhitongche.optInt("Id");
			zhiName = jsonObjectZhitongche.optString("Name");
			zhiShortName = jsonObjectZhitongche.optString("ShortName");
		}

		JSONArray group_list = jsonObject.optJSONArray("group_list");
		if (group_list != null && group_list.length() > 0) {
			talkGroups = new ArrayList<TalkGroup>();
			TalkGroup talkGroup;
			JSONObject jsonGroup;
			for (int i = 0; i < group_list.length(); i++) {
				talkGroup = new TalkGroup();
				jsonGroup = group_list.optJSONObject(i);

				talkGroup.parseJSON(jsonGroup);
				talkGroups.add(talkGroup);
			}
		}

		try {
			CrashApplication.getInstance().getDb().deleteAll(TalkGroup.class);
			CrashApplication.getInstance().getDb().saveOrUpdate(this);
			CrashApplication.getInstance().getDb().saveOrUpdateAll(talkGroups);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO Auto-generated method stub
		return this;
	}

	public String getUserQrCode() {
		return userQrCode;
	}

	public void setUserQrCode(String userQrCode) {
		this.userQrCode = userQrCode;
	}

	public String getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public long getBirthday() {
		return birthday;
	}

	public void setBirthday(long birthday) {
		this.birthday = birthday;
	}

	public int getZhiId() {
		return zhiId;
	}

	public void setZhiId(int zhiId) {
		this.zhiId = zhiId;
	}

	public String getZhiName() {
		return zhiName;
	}

	public void setZhiName(String zhiName) {
		this.zhiName = zhiName;
	}

	public String getZhiShortName() {
		return zhiShortName;
	}

	public void setZhiShortName(String zhiShortName) {
		this.zhiShortName = zhiShortName;
	}

	public boolean isUpdate() {
		return isUpdate;
	}

	public void setUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getBenbenId() {
		return BenbenId;
	}

	public void setBenbenId(String benbenId) {
		BenbenId = benbenId;
	}

	public String getPoster() {
		return Poster;
	}

	public void setPoster(String poster) {
		Poster = poster;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public String getProCity() {
		return ProCity;
	}

	public void setProCity(String proCity) {
		ProCity = proCity;
	}

	public String getBaiXing() {
		return BaiXing;
	}

	public void setBaiXing(String baiXing) {
		BaiXing = baiXing;
	}

	public String getCornet() {
		return Cornet;
	}

	public void setCornet(String cornet) {
		Cornet = cornet;
	}

	public String getIntegral() {
		return Integral;
	}

	public void setIntegral(String integral) {
		Integral = integral;
	}

	public String getCoin() {
		return Coin;
	}

	public void setCoin(String coin) {
		Coin = coin;
	}

	public int getLeague() {
		return league;
	}

	public void setLeague(int league) {
		this.league = league;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserNickname() {
		return UserNickname;
	}

	public void setUserNickname(String userNickname) {
		UserNickname = userNickname;
	}

	public String getUserSex() {
		return UserSex;
	}

	public void setUserSex(String userSex) {
		UserSex = userSex;
	}

	public String getAge() {
		return Age;
	}

	public void setAge(String age) {
		Age = age;
	}

	public String getPhone() {
		return Phone;
	}

	public void setPhone(String phone) {
		Phone = phone;
	}

	public String getToken() {
		return Token;
	}

	public void setToken(String token) {
		Token = token;
	}

	public String getHuanxin_username() {
		return huanxin_username;
	}

	public void setHuanxin_username(String huanxin_username) {
		this.huanxin_username = huanxin_username;
	}

	public String getHuanxin_password() {
		return huanxin_password;
	}

	public void setHuanxin_password(String huanxin_password) {
		this.huanxin_password = huanxin_password;
	}

	public String getHuanxin_uuid() {
		return huanxin_uuid;
	}

	public void setHuanxin_uuid(String huanxin_uuid) {
		this.huanxin_uuid = huanxin_uuid;
	}

	public ArrayList<TalkGroup> getTalkGroups() {
		return talkGroups;
	}

	public void setTalkGroups(ArrayList<TalkGroup> talkGroups) {
		this.talkGroups = talkGroups;
	}

	public String getAppellation() {
		return appellation;
	}

	public void setAppellation(String appellation) {
		this.appellation = appellation;
	}

	public int getLeague_disable() {
		return league_disable;
	}

	public void setLeague_disable(int league_disable) {
		this.league_disable = league_disable;
	}

	public int getCreation_disable() {
		return creation_disable;
	}

	public void setCreation_disable(int creation_disable) {
		this.creation_disable = creation_disable;
	}

	public int getBuy_disable() {
		return buy_disable;
	}

	public void setBuy_disable(int buy_disable) {
		this.buy_disable = buy_disable;
	}

	public int getEnterprise_disable() {
		return enterprise_disable;
	}

	public void setEnterprise_disable(int enterprise_disable) {
		this.enterprise_disable = enterprise_disable;
	}

	public int getGroup_disable() {
		return group_disable;
	}

	public void setGroup_disable(int group_disable) {
		this.group_disable = group_disable;
	}

	public int getStore_disable() {
		return store_disable;
	}

	public void setStore_disable(int store_disable) {
		this.store_disable = store_disable;
	}


}
