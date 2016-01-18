package com.xunao.benben.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

/**
 * 首选项管理
 * 
 * @ClassName: SharePreferenceUtil
 * @Description: TODO
 * @author smile
 * @date 2014-6-10 下午4:20:14
 */
@SuppressLint("CommitPrefEdits")
public class SharePreferenceUtil {
	private SharedPreferences mSharedPreferences;
	private static SharedPreferences.Editor editor;

	public SharePreferenceUtil(Context context, String name) {
		mSharedPreferences = context.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		editor = mSharedPreferences.edit();
	}

	// 第一次进入应用
	private String SHARED_KEY_FRIST = "shared_key_frist";
	// 下载展示图片地址
	private String SHARED_KEY_PATH = "shared_key_path";
	// 应用版本号
	private String SHARE_KEY_VERSION = "shared_key_version";
	// 用户信息
	private String SHARED_KEY_USER_ID = "shared_key_user_id";
	private String SHARED_KEY_USER_NAME = "shared_key_user_name";
	private String SHARED_KEY_USERLOGIN_NAME = "shared_key_userlogin_name";
	private String SHARED_KEY_USER_PWD = "shared_key_user_pwd";
	private String SHARED_KEY_USER_TOKEN = "shared_key_user_token";

	// 记录环信的通知数
	private String SHARED_KEY_USER_NOTICE = "shared_key_user_notice";
	private String SHARED_KEY_LASTTIMELOGIN = "shared_key_LastTimeLogin";
	private String SHARED_KEY_LSSTTIME = "shared_key_LastTime";

    //记录朋友圈的通知时间
    private String SHARED_FRIEND_NOTICE = "shared_friend_notice";
    private String SHARED_FRIEND_RUSH_NOTICE = "shared_friend_rush_notice";
    //记录μ创作的通知时间
    private String SHARED_CREATION_NOTICE = "shared_creation_notice";
    private String SHARED_CREATION_RUSH_NOTICE = "shared_creation_rush_notice";

    //通讯录版本号
    private String SHARED_SNAP_SHOT = "shared_snap_shot";

    //用户奔犇号
    private String SHARED_LOGIN_BENBEN = "shared_login_benben";

	// 消息最后请求时间
	public void setLastTime(long lastTime) {
		editor.putLong(SHARED_KEY_LSSTTIME, lastTime);
		editor.commit();
	}

	public long getLastTime() {
		return mSharedPreferences.getLong(SHARED_KEY_LSSTTIME, 0);
	}

	public void setUserNotice(String userNotice) {
		editor.putString(SHARED_KEY_USER_NOTICE, userNotice);
		editor.commit();
	}

	public String getUserNotice() {
		return mSharedPreferences.getString(SHARED_KEY_USER_NOTICE, "");
	}

	// 上次是否登录成功
	public boolean getLastTimeLogin() {
		return mSharedPreferences.getBoolean(SHARED_KEY_LASTTIMELOGIN, false);
	}

	public void setLastTimeLogin(boolean islogin) {
		editor.putBoolean(SHARED_KEY_LASTTIMELOGIN, islogin);
		editor.commit();
	}

	// 用户环信账号
	public void setUserAcount(String usernmae) {
		editor.putString(SHARED_KEY_USER_NAME, usernmae);
		editor.commit();
	}

	public String getUserAcount() {
		return mSharedPreferences.getString(SHARED_KEY_USER_NAME, "");
	}

	// 用户账号
	public void setUseLoginrAcount(String usernmae) {
		editor.putString(SHARED_KEY_USERLOGIN_NAME, usernmae);
		editor.commit();
	}

	public String getUserLoginAcount() {
		return mSharedPreferences.getString(SHARED_KEY_USERLOGIN_NAME, "");
	}

	// 用户密码
	public void setUserPWD(String usernmae) {
		editor.putString(SHARED_KEY_USER_PWD, usernmae);
		editor.commit();
	}

	public String getUserPWD() {
		return mSharedPreferences.getString(SHARED_KEY_USER_PWD, "");
	}

	// 环信
	private String SHARED_KEY_HUANXIN_USERNAME = "shared_key_huanxin_username";

	public void setHuanXinUserName(String huanxin_username) {
		editor.putString(SHARED_KEY_HUANXIN_USERNAME, huanxin_username);
		editor.commit();
	}

	public String getHuanXinUserName() {
		return mSharedPreferences.getString(SHARED_KEY_HUANXIN_USERNAME, "");
	}

	// 应用版本号
	public void setVersion(String version) {
		editor.putString(SHARE_KEY_VERSION, version);
		editor.commit();
	}

	public String getVersion() {
		return mSharedPreferences.getString(SHARE_KEY_VERSION, "");
	}

	// 是否第一次进入程序
	public boolean isFristComing() {
		return mSharedPreferences.getBoolean(SHARED_KEY_FRIST, true);
	}

	public void setFristComing(boolean isFrist) {
		editor.putBoolean(SHARED_KEY_FRIST, isFrist);
		editor.commit();
	}

	// 有没有下载的图片地址
	public String getPath() {
		return mSharedPreferences.getString(SHARED_KEY_PATH, "");
	}

	public void setPath(String path) {
		editor.putString(SHARED_KEY_PATH, path);
		editor.commit();
	}

	// 用户id
	public void setUserId(String userid) {
		editor.putString(SHARED_KEY_USER_ID, userid);
		editor.commit();
	}

	public String getUserId() {
		return mSharedPreferences.getString(SHARED_KEY_USER_ID, "");
	}

	// 用户token
	public void setUserToken(String usertoken) {
		editor.putString(SHARED_KEY_USER_TOKEN, usertoken);
		editor.commit();
	}

	public String getUserToken() {
		return mSharedPreferences.getString(SHARED_KEY_USER_TOKEN, "");
	}



    // 朋友圈最后请求时间
    public void setLastFriendTime(long lastTime) {
        editor.putLong(SHARED_FRIEND_NOTICE, lastTime);
        editor.commit();
    }

    public long getLastFriendTime() {
        return mSharedPreferences.getLong(SHARED_FRIEND_NOTICE, 0);
    }

    public void setLastFriendRushTime(long lastTime) {
        editor.putLong(SHARED_FRIEND_RUSH_NOTICE, lastTime);
        editor.commit();
    }

    public long getLastFriendRushTime() {
        return mSharedPreferences.getLong(SHARED_FRIEND_RUSH_NOTICE, 0);
    }


    // 微创作最后请求时间
    public void setLastCreationTime(long lastTime) {
        editor.putLong(SHARED_CREATION_NOTICE, lastTime);
        editor.commit();
    }

    public long getLastCreationTime() {
        return mSharedPreferences.getLong(SHARED_CREATION_NOTICE, 0);
    }

    public void setLastCreationRushTime(long lastTime) {
        editor.putLong(SHARED_CREATION_RUSH_NOTICE, lastTime);
        editor.commit();
    }

    public long getLastCreationRushTime() {
        return mSharedPreferences.getLong(SHARED_CREATION_RUSH_NOTICE, 0);
    }

	public void setObject(Object object, String key) {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			ObjectOutputStream oos = new ObjectOutputStream(bos);

			oos.writeObject(object);

			String objStr = new String(Base64.encode(bos.toByteArray(),
					Base64.DEFAULT));
			editor.putString(key, objStr);
			editor.commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public <T> T getObject(String key, Class<T> t) {

		try {

			String string = mSharedPreferences.getString(key, "");

			if (!string.equals("")) {

				byte[] decode = Base64
						.decode(string.getBytes(), Base64.DEFAULT);

				ByteArrayInputStream bos = new ByteArrayInputStream(decode);

				ObjectInputStream oos = new ObjectInputStream(bos);

				T readObject = (T) oos.readObject();
				return readObject;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

    // 获取通讯录版本号
    public String getSnapshot() {
        return mSharedPreferences.getString(SHARED_SNAP_SHOT, "1");
    }

    public void setSnapshot(String snapshot) {
        editor.putString(SHARED_SNAP_SHOT, snapshot);
        editor.commit();
    }


    // 获取上次登录用户信息
    public String getLastLoginUser() {
        return mSharedPreferences.getString(SHARED_LOGIN_BENBEN, "");
    }

    public void setLastLoginUser(String benbenid) {
        editor.putString(SHARED_LOGIN_BENBEN, benbenid);
        editor.commit();
    }

}
