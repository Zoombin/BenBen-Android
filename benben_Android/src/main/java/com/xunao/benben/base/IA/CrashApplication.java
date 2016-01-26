package com.xunao.benben.base.IA;

import in.srain.cube.Cube;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.util.CubeDebug;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMMessage;
import com.easemob.chat.OnMessageNotifyListener;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtils.DbUpgradeListener;
import com.lidroid.xutils.exception.DbException;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.xunao.benben.R;
import com.xunao.benben.activity.SplashActivity;
import com.xunao.benben.base.service.UpdateInfoService;
import com.xunao.benben.bean.BuyNews;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.ContactsGroup;
import com.xunao.benben.bean.ContactsObject;
import com.xunao.benben.bean.PublicMessage;
import com.xunao.benben.bean.TalkGroup;
import com.xunao.benben.bean.User;
import com.xunao.benben.bean.tx.BenbenEMConversation;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.hx.chatuidemo.DemoHXSDKHelper;
import com.xunao.benben.utils.SharePreferenceUtil;

public class CrashApplication extends Application {
	// 必须使用，Activity启动页
	private final static String lancherActivityClassName = SplashActivity.class
			.getName();
	private static CrashApplication mInstance;

	private LinkedList<Activity> mBaseActivityList = new LinkedList<Activity>();
	public ArrayList<PublicMessage> mPublicMessage = new ArrayList<PublicMessage>();
    public ArrayList<BuyNews> buyNewses = new ArrayList<BuyNews>();
	private static SharePreferenceUtil shareUtils;

	/**
	 * 所有的分组,与所有的联系人
	 */
	public ContactsObject contactsObject;
	public Map<String, ContactsGroup> mContactsGroupMap = new HashMap<String, ContactsGroup>();
	public Map<String, Contacts> mContactsMap = new HashMap<String, Contacts>();
	public Map<String, TalkGroup> mTalkGroupMap = new HashMap<String, TalkGroup>();
    public int friendNum=0;
    public int creationNum=0;
//    public int creationAboutMe=0;
//    public int friendAboutMe=0;

    public boolean updateFlag = false;

	/**
	 * 专门给通讯录分组用 所有的分组,与所有的联系人
	 */
	public ContactsObject contactsObjectManagement;
	/**
	 * 当前操作的分组
	 */
	public ContactsGroup contactsGroup;

	private BenbenEMConversation bme = new BenbenEMConversation(
			BenbenEMConversation.PUBLIC, null);

	private boolean isExit = false;
	
	public boolean isExit() {
		return isExit;
	}

	public void setExit(boolean isExit) {
		this.isExit = isExit;
	}

	public BenbenEMConversation getBme() {
		return bme;
	}

	public void setBme(BenbenEMConversation bme) {
		this.bme = bme;
	}

	// 字体
	private Typeface tf;

	private DbUtils db;

	public DbUtils getDb() {
		return db;
	}

	// 持久化一个hashmap 用来存储环信name所对应的联系人资料 或 群组
	HashMap<String, Object> huanXinMap;

	// 环信
	public static Context applicationContext;
	// login user name
	public final String PREF_USERNAME = "username";
	/**
	 * 当前用户nickname,为了苹果推送不是userid而是昵称
	 */
	public static String currentUserNick = "";
	public static DemoHXSDKHelper hxSDKHelper = new DemoHXSDKHelper();

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
//		CrashHandler crashHandler = CrashHandler.getInstance();
//		crashHandler.init(this);
		applicationContext = this;
		mInstance = this;
		PushAgent mPushAgent = PushAgent.getInstance(this);
		mPushAgent.enable();
		MobclickAgent.setDebugMode(true);
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);

		// File cacheDir =
		// CommonUtils.getImageCachePath(getApplicationContext());
		// initImageLoader(this, cacheDir);
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(this);

		initImageLoader(this);

		AssetManager mgr = getAssets();
		tf = Typeface.createFromAsset(mgr, "nomal.TTF");
		createDB();
		// 环信初始化
		hxSDKHelper.onInit(applicationContext);
		initHuanXinData();
		EMChatOptions chatOptions = EMChatManager.getInstance()
				.getChatOptions();
		// 设置是否启用新消息提醒(打开或者关闭消息声音和震动提示)
		chatOptions.setNotifyBySoundAndVibrate(true); // 默认为true 开启新消息提醒
		// 设置是否启用新消息声音提醒
		chatOptions.setNoticeBySound(true); // 默认为true 开启声音提醒
		// 设置是否启用新消息震动提醒
		chatOptions.setNoticedByVibrate(true); // 默认为true 开启震动提醒
		// 设置语音消息播放是否设置为扬声器播放
		chatOptions.setUseSpeaker(true); // 默认为true 开启扬声器播放
		// 设置自定义的文字提示
		chatOptions.setNotifyText(new OnMessageNotifyListener() {

			@Override
			public String onNewMessageNotify(EMMessage message) {
				// 可以根据message的类型提示不同文字，这里为一个简单的示例
				// installShortCut();
				return "奔犇有一条消息哦";
			}

			@Override
			public String onLatestMessageNotify(EMMessage message,
					int fromUsersNum, int messageNum) {
				// installShortCut();
				sendBadgeNumber(messageNum);
				return "奔犇有" + messageNum + "条消息";
			}

			@Override
			public String onSetNotificationTitle(EMMessage arg0) {
				// installShortCut();
				return "奔犇有一条消息哦";
			}

			@Override
			public int onSetSmallIcon(EMMessage arg0) {
				// TODO Auto-generated method stub
				return 0;
			}
		});

	}

	private void sendBadgeNumber(int number) {

		if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
			sendToXiaoMi(number);
		} else if (Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
			sendToSamsumg(number);
		} else if (Build.MANUFACTURER.toLowerCase().contains("sony")) {
			sendToSony(number);
		} else {
			// Toast.makeText(this, "Not Support", Toast.LENGTH_LONG).show();
		}
	}

	Notification notification = null;

	private void sendToXiaoMi(int number) {
		final NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		boolean isMiUIV6 = true;
		try {
//			NotificationCompat.Builder builder = new NotificationCompat.Builder(
//					this);
//			builder.setContentTitle("您有" + number + "未读消息");
//			builder.setTicker("您有" + number + "未读消息");
//			builder.setAutoCancel(true);
//			builder.setSmallIcon(R.drawable.ic_icon);
//			builder.setDefaults(Notification.DEFAULT_LIGHTS);
//			notification = builder.build();
//			Class miuiNotificationClass = Class
//					.forName("android.app.MiuiNotification");
//			Object miuiNotification = miuiNotificationClass.newInstance();
//			Field field = miuiNotification.getClass().getDeclaredField(
//					"messageCount");
//			field.setAccessible(true);
//			field.set(miuiNotification, number);// 设置信息数
//			field = notification.getClass().getField("extraNotification");
//			field.setAccessible(true);
//			field.set(notification, miuiNotification);
//			Toast.makeText(this, "Xiaomi=>isSendOk=>1", Toast.LENGTH_LONG)
//					.show();
		} catch (Exception e) {
			e.printStackTrace();
			// miui 6之前的版本
			isMiUIV6 = false;
			Intent localIntent = new Intent(
					"android.intent.action.APPLICATION_MESSAGE_UPDATE");
			localIntent.putExtra(
					"android.intent.extra.update_application_component_name",
					getPackageName() + "/" + lancherActivityClassName);
			localIntent.putExtra(
					"android.intent.extra.update_application_message_text",
					number);
			sendBroadcast(localIntent);
		} finally {
			// if (notification != null && isMiUIV6) {
			// // miui6以上版本需要使用通知发送
			// new Handler().postDelayed(new Runnable() {
			//
			// @Override
			// public void run() {
			// // TODO Auto-generated method stub
			// nm.notify(101010, notification);
			// }
			// }, 2000);
			//
			// }
		}

	}

	private void sendToSony(int number) {
		boolean isShow = true;
		if ("0".equals(number)) {
			isShow = false;
		}
		Intent localIntent = new Intent();
		localIntent
				.putExtra(
						"com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE",
						isShow);// 是否显示
		localIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
		localIntent.putExtra(
				"com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME",
				lancherActivityClassName);// 启动页
		localIntent.putExtra(
				"com.sonyericsson.home.intent.extra.badge.MESSAGE", number);// 数字
		localIntent.putExtra(
				"com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME",
				getPackageName());// 包名
		sendBroadcast(localIntent);

		// Toast.makeText(this, "Sony," + "isSendOk", Toast.LENGTH_LONG).show();
	}

	private void sendToSamsumg(int number) {
		final NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Intent localIntent = new Intent(
				"android.intent.action.BADGE_COUNT_UPDATE");
		localIntent.putExtra("badge_count", number);// 数字
		localIntent.putExtra("badge_count_package_name", getPackageName());// 包名
		localIntent
				.putExtra("badge_count_class_name", lancherActivityClassName); // 启动页
		sendBroadcast(localIntent);

//		NotificationCompat.Builder builder = new NotificationCompat.Builder(
//				this);
//		builder.setContentTitle("您有" + number + "未读消息");
//		builder.setTicker("您有" + number + "未读消息");
//		builder.setAutoCancel(true);
//		builder.setSmallIcon(R.drawable.ic_icon);
//		builder.setDefaults(Notification.DEFAULT_LIGHTS);
//		notification = builder.build();
//		nm.notify(101010, notification);

		// Toast.makeText(this, "Samsumg," + "isSendOk",
		// Toast.LENGTH_LONG).show();
	}

	private void createDB() {
		db = DbUtils.create(this, "benben", 8, new DbUpgradeListener() {
			@Override
			public void onUpgrade(DbUtils arg0, int arg1, int arg2) {
                getInstance().getSpUtil().setSnapshot("1");
			}
		});

		db.configAllowTransaction(true);
		db.configDebug(AndroidConfig.isDebug);

	}

	public void initImageLoader(Context context) {

		CubeDebug.DEBUG_IMAGE = AndroidConfig.isDebug;
		ImageLoaderFactory.customizeCache(this,
		// memory size
				1024 * 10,
				// disk cache directory
				// path1.getAbsolutePath(),
				null,
				// disk cache size
				ImageLoaderFactory.DEFAULT_FILE_CACHE_SIZE_IN_KB);

		ImageLoaderFactory.setDefaultImageResizer(DemoDuiTangImageReSizer
				.getInstance());
		Cube.onCreate(this);

	}

	public static CrashApplication getInstance() {
		// TODO Auto-generated method stub
		return mInstance;
	}

	public void addActivity(Activity activity) {
		mBaseActivityList.add(activity);
	}

	public void removeActivity(Activity activity) {
		mBaseActivityList.remove(activity);
	}

	public Typeface getTf() {
		return tf;
	}

	public void setTf(Typeface tf) {
		this.tf = tf;
	}

	public SharePreferenceUtil getSpUtil() {

		if (shareUtils == null) {
			shareUtils = new SharePreferenceUtil(mInstance,
					AndroidConfig.SHARENAME);
		}

		return shareUtils;
	}

	public void exit() {

		stopService(new Intent(this, UpdateInfoService.class));
		for (Activity activity : mBaseActivityList) {
			if (activity != null) {
				activity.finish();
			}
		}
		EMChatManager.getInstance().logout();// 此方法为同步方法
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
//				System.exit(0);
			}
		}, 1000);
	}

	public void logout() {
		stopService(new Intent(this, UpdateInfoService.class));
        friendNum=0;
        creationNum=0;
//        creationAboutMe=0;
//        friendAboutMe=0;

		CrashApplication.getInstance().mContactsMap.clear();
		CrashApplication.getInstance().contactsGroup = null;
		CrashApplication.getInstance().contactsObject = null;
		CrashApplication.getInstance().mContactsGroupMap.clear();
		CrashApplication.getInstance().mPublicMessage.clear();
        CrashApplication.getInstance().buyNewses.clear();
		CrashApplication.getInstance().mTalkGroupMap.clear();

		int size = mBaseActivityList.size();
		for (int i = size - 1; i >= 0; i--) {
			mBaseActivityList.get(i).finish();
			mBaseActivityList.remove(i);
		}
		if (mPublicMessage != null) {
			mPublicMessage.clear();
		}
        if (buyNewses != null) {
            buyNewses.clear();
        }
		bme.setPublicNum(0);
        bme.setBuyNum(0);
		EMChatManager.getInstance().logout();// 此方法为同步方法
	}

	int i;
	long front;
	long later;

	public void shutDown() {
		i++;
		if (i < 2) {
			Toast.makeText(this, "再点一次退出程序", Toast.LENGTH_SHORT).show();
			front = System.currentTimeMillis();
			return;
		}
		if (i >= 2) {
			later = System.currentTimeMillis();
			if (later - front > 2000) {
				Toast.makeText(this, "再点一次退出程序", Toast.LENGTH_SHORT).show();
				front = System.currentTimeMillis();
				i = 1;
			} else {
				i = 0;
				exit();
			}
		}
	}

	private com.xunao.benben.bean.User mUser;

	public boolean isAttenRefresh;

	public boolean isAddContacts;

	public Fragment[] fragments;

	public int mScreenWidth;

	public int mScreenHeight;
	public User user;

	/**
	 * 获取当前登陆用户名
	 * 
	 * @return
	 */
	public String getUserName() {
		return hxSDKHelper.getHXId();
	}

	/**
	 * 获取密码
	 * 
	 * @return
	 */
	public String getPassword() {
		return hxSDKHelper.getPassword();
	}

	/**
	 * 设置用户名
	 *
	 */
	public void setUserName(String username) {
		hxSDKHelper.setHXId(username);
	}

	/**
	 * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
	 * 内部的自动登录需要的密码，已经加密存储了
	 * 
	 * @param pwd
	 */
	public void setPassword(String pwd) {
		hxSDKHelper.setPassword(pwd);
	}

	/**
	 * 创建环信id和本地数据之间的联系
	 */
	private void initHuanXinData() {
		// TODO Auto-generated method stub
		try {
			huanXinMap = new HashMap<String, Object>();
			ArrayList<Contacts> contacts = (ArrayList<Contacts>) db
					.findAll(Contacts.class);
			ArrayList<TalkGroup> talkGroups = (ArrayList<TalkGroup>) db
					.findAll(TalkGroup.class);
			if (contacts != null && contacts.size() > 0) {
				for (Contacts cs : contacts) {
					if (!"0".equals(cs.getIs_benben())) {
						huanXinMap.put(cs.getHuanxin_username(), cs);
					}
				}
			}

			if (talkGroups != null && talkGroups.size() > 0) {
				for (TalkGroup talkGroup : talkGroups) {
					huanXinMap.put(talkGroup.getHuanxin_groupid(), talkGroup);
				}
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public HashMap<String, Object> getHuanXinMap() {
		return huanXinMap;
	}

	public void setHuanXinMap(HashMap<String, Object> huanXinMap) {
		this.huanXinMap = huanXinMap;
	}

	public void setContactsObject(ContactsObject contactsObject) {
		this.contactsObject = contactsObject;
		ArrayList<ContactsGroup> getmContactsGroups = contactsObject
				.getmContactsGroups();
		mContactsGroupMap.clear();
		mContactsMap.clear();
		for (ContactsGroup cg : getmContactsGroups) {
			mContactsGroupMap.put(cg.getId() + "", cg);
			for (Contacts c : cg.getmContacts()) {
				if (!"0".equals(c.getIs_benben())) {
					mContactsMap.put(c.getHuanxin_username(), c);
				}
			}
		}
	}
}
