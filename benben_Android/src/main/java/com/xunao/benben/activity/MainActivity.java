package com.xunao.benben.activity;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageTask;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMContact;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMNotifier;
import com.easemob.chat.GroupChangeListener;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.HanziToPinyin;
import com.easemob.util.NetUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.BaseFragment;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.base.service.UpdateInfoService;
import com.xunao.benben.bean.AndriodVersion;
import com.xunao.benben.bean.BuyNews;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.NewMsgAboutMe;
import com.xunao.benben.bean.News;
import com.xunao.benben.bean.NewsList;
import com.xunao.benben.bean.PublicMessage;
import com.xunao.benben.bean.TalkGroup;
import com.xunao.benben.bean.tx.BenbenEMConversation;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.fragment.ContactsFragment;
import com.xunao.benben.fragment.MyFragment;
import com.xunao.benben.fragment.PlayPhoneFragment;
import com.xunao.benben.fragment.PrivateFragment;
import com.xunao.benben.hx.applib.controller.HXSDKHelper;
import com.xunao.benben.hx.chatuidemo.Constant;
import com.xunao.benben.hx.chatuidemo.DemoHXSDKHelper;
import com.xunao.benben.hx.chatuidemo.activity.ChatActivity;
import com.xunao.benben.hx.chatuidemo.activity.ChatAllHistoryFragment;
import com.xunao.benben.hx.chatuidemo.db.InviteMessgeDao;
import com.xunao.benben.hx.chatuidemo.db.User;
import com.xunao.benben.hx.chatuidemo.db.UserDao;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityLogin;
import com.xunao.benben.ui.item.ActivityAddFriend;
import com.xunao.benben.ui.item.ActivityBenBenFriend;
import com.xunao.benben.ui.item.ActivityNews;
import com.xunao.benben.ui.order.ActivityMyOrder;
import com.xunao.benben.ui.order.ActivityOrderCheck;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.HintUtil;
import com.xunao.benben.utils.PlayPhoneUtils;
import com.xunao.benben.utils.SharePreferenceUtil;
import com.xunao.benben.utils.TimeUtil;
import com.xunao.benben.utils.ToastUtils;

public class MainActivity extends BaseActivity implements EMEventListener, UmengUpdateListener {

	public static final int UPDATA = 0;
	public static final int NOUPDATA = 1;
	private ExecutorService executorService = Executors.newFixedThreadPool(2);
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case UPDATA:// 消息中心消息
				tab_num.setVisibility(View.VISIBLE);
				break;
			case NOUPDATA:// 消息中心没消息public abstract void onRefresh();
				tab_num.setVisibility(View.GONE);
				break;
			}

		}
	};
	private String path;
	private MyConnectionListener myConnectionListener;
	private MyContactListener myContactListener;
	private MyGroupChangeListener myGroupChangeListener;
	// 环信
//	private NewMessageBroadcastReceiver msgReceiver;
	private myFragmentBroadcast fragmentBroadcast;
	private MyBroadcastReceiver msgCenterReceiver;
	private ChatAllHistoryFragment huanXinFragment;
	// 账号在别处登录
	public boolean isConflict = false;
	// 账号被移除
	private boolean isCurrentAccountRemoved = false;

	// 记录从哪个页面跳转来的 默认为登录
	public String from = "login";
	private refreshBrocast mrefreshBrocast;
	// 底部按钮
	private RelativeLayout tab_one;
	public RelativeLayout tab_two;
	private RelativeLayout tab_three;
	private RelativeLayout tab_four;
	private RelativeLayout tab_five;
	// 底部红点
	private TextView tab_two_num,tab_four_num,tab_four_hint;

	private int[] nomalImg = { R.drawable.tab_one_nomal,
			R.drawable.tab_two_nomal, R.drawable.tab_three_nomal,
			R.drawable.tab_four_nomal, R.drawable.tab_five_nomal };
	private int[] touchImg = { R.drawable.tab_one_touch,
			R.drawable.tab_two_touch, R.drawable.tab_three_touch,
			R.drawable.tab_four_touch, R.drawable.tab_five_touch };

    public static Bitmap bimap;

	// 定义Fragment数组
	public Fragment[] fragments;

	public void showBlack() {
		if (black_box != null) {
			black_box.setVisibility(View.VISIBLE);
		}
	}

	public void hineBlack() {
		if (black_box != null) {
			black_box.setVisibility(View.GONE);
		}
	}

	// 下载展示图片
	public void downloadImg() {
		final CubeImageView temp = (CubeImageView) findViewById(R.id.temp);

		InteNetUtils.getInstance(mContext).downloadSplashImg(mScreenWidth,
				mScreenHeight, new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// 下载成功
						// {"ret_num":0,"ret_msg":"OK","splash":"http:\/\/benben.xun-ao.com\/uploads\/2015-05-27\/pcM4VzrK2JZ7UtzN.jpg"}
						try {
							JSONObject jsonObject = new JSONObject(arg0.result);

							path = jsonObject.optString("splash");
							temp.setTag(R.string.type, "noshow");
							CommonUtils.startImageLoader(cubeimageLoader, path,
									temp);
							// if(CommonUtils.isEmpty(path)){
							// temp.loadImage(cubeimageLoader, path,
							// mScreenWidth, mScreenHeight);
							// }

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// 下载失败
					}
				});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        UmengUpdateAgent.setUpdateOnlyWifi(false); //false 则非wifi也检测更新
        UmengUpdateAgent.update(this);
        UmengUpdateAgent.setUpdateListener(this);


        bimap = BitmapFactory.decodeResource(getResources(),
                R.drawable.error);

		fragmentBroadcast = new myFragmentBroadcast();
		registerReceiver(fragmentBroadcast, new IntentFilter(
				AndroidConfig.refrashMyFragment));
		try {
			mApplication.user = dbUtil
					.findFirst(com.xunao.benben.bean.User.class);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		try {
			List<PublicMessage> findAll = dbUtil.findAll(Selector.from(
					PublicMessage.class).orderBy("creatTime", true));
			if (findAll != null) {
				mApplication.mPublicMessage.addAll(findAll);
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Intent intent = new Intent(this, UpdateInfoService.class);
		intent.setPackage(getPackageName());
		intent.setAction("com.xunao.benben.base.service.UpdateInfoService");
		startService(intent);

		cubeimageLoader.setImageLoadHandler(new DefaultImageLoadHandler(
				mContext) {
			@Override
			public void onLoading(ImageTask imageTask,
					CubeImageView cubeImageView) {
				if (cubeImageView != null) {
					String tag = (String) cubeImageView.getTag(R.string.type);
					if ("group".equals(tag)) {
						cubeImageView
								.setImageResource(R.drawable.ic_group_poster);
					} else if ("twocode".equals(tag)) {
					} else {
						cubeImageView.setImageResource(R.drawable.default_face);
					}

				}

			}

			@Override
			public void onLoadFinish(ImageTask imageTask,
					CubeImageView cubeImageView, BitmapDrawable drawable) {
				if (cubeImageView != null) {
					if (imageTask.getIdentityUrl().equalsIgnoreCase(
							(String) cubeImageView.getTag())) {
						String tag = (String) cubeImageView
								.getTag(R.string.type);
						if ("noshow".equals(tag)) {
							spUtil.setPath(path);
						}

						cubeImageView.setVisibility(View.VISIBLE);
						cubeImageView.setImageDrawable(drawable);
					}
				}
			}

			@Override
			public void onLoadError(ImageTask imageTask,
					CubeImageView imageView, int errorCode) {
				if (imageView != null) {
					String tag = (String) imageView.getTag(R.string.type);
					if ("group".equals(tag)) {
						imageView.setImageResource(R.drawable.ic_group_poster);
					} else if ("twocode".equals(tag)) {
					} else {
						imageView.setImageResource(R.drawable.default_face);
					}
				}
			}
		});

		downloadImg();
		if (user != null) {
			try {
				user = dbUtil.findFirst(com.xunao.benben.bean.User.class);
				List<TalkGroup> findAll = dbUtil.findAll(Selector
						.from(TalkGroup.class));
				if (findAll != null) {
					user.setTalkGroups((ArrayList<TalkGroup>) findAll);
					for (TalkGroup tg : findAll) {
						mApplication.mTalkGroupMap.put(tg.getHuanxin_groupid(),
								tg);
					}
				}
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

        if (CommonUtils.isNetworkAvailable(mContext)) {
            long friendTime = spUtil.getLastFriendTime();
            long creationTime = spUtil.getLastCreationTime();
            long friendRushTime = spUtil.getLastFriendRushTime();
            long creationRushTime = spUtil.getLastCreationRushTime();
            if(friendTime==0){
                friendTime = new Date().getTime()/1000;
                spUtil.setLastFriendTime(friendTime);
            }
            if(creationTime==0){
                creationTime = new Date().getTime()/1000;
                spUtil.setLastCreationTime(creationTime);
            }
            if(friendRushTime==0){
                friendRushTime = new Date().getTime()/1000;
                spUtil.setLastFriendRushTime(friendRushTime);
            }
            if(creationRushTime==0){
                creationRushTime = new Date().getTime()/1000;
                spUtil.setLastCreationRushTime(creationRushTime);
            }


            InteNetUtils.getInstance(mContext).Remind(friendTime, creationTime,friendRushTime, creationRushTime,user.getToken(), mRequestCallBack);
        }else{
            ToastUtils.Errortoast(mContext, "网络不可用!");
        }

		if (savedInstanceState != null
				&& savedInstanceState.getBoolean(Constant.ACCOUNT_REMOVED,
						false)) {
			// 防止被移除后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			mApplication.logout();
			finish();
			startActivity(new Intent(this, ActivityLogin.class));
			return;
		} else if (savedInstanceState != null
				&& savedInstanceState.getBoolean("isConflict", false)) {
			// 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			finish();
			startActivity(new Intent(this, ActivityLogin.class));
			return;
		}

        DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();


        EMChatManager.getInstance().registerEventListener(this);



        new Thread() {

			public void run() {



				// 注册一个接收消息的BroadcastReceiver
//				msgReceiver = new NewMessageBroadcastReceiver();
//				IntentFilter intentFilter = new IntentFilter(EMChatManager
//						.getInstance().getNewMessageBroadcastAction());
//				intentFilter.setPriority(3);
//				registerReceiver(msgReceiver, intentFilter);
//
//				// 注册一个ack回执消息的BroadcastReceiver
//				IntentFilter ackMessageIntentFilter = new IntentFilter(
//						EMChatManager.getInstance()
//								.getAckMessageBroadcastAction());
//				ackMessageIntentFilter.setPriority(3);
//				registerReceiver(ackMessageReceiver, ackMessageIntentFilter);
//
//				// 注册一个透传消息的BroadcastReceiver
//				IntentFilter cmdMessageIntentFilter = new IntentFilter(
//						EMChatManager.getInstance()
//								.getCmdMessageBroadcastAction());
//				cmdMessageIntentFilter.setPriority(3);
//				registerReceiver(cmdMessageReceiver, cmdMessageIntentFilter);

				// 注册一个消息中心的BroadcastReceiver
				IntentFilter messageintentFilter = new IntentFilter(
						"hasMessage");
				msgCenterReceiver = new MyBroadcastReceiver();
				registerReceiver(msgCenterReceiver, messageintentFilter);
				mrefreshBrocast = new refreshBrocast();
				registerReceiver(mrefreshBrocast, new IntentFilter(
						AndroidConfig.ContactsRefresh));

//				注册一个离线消息的BroadcastReceiver
//				IntentFilter offlineMessageIntentFilter = new
//				IntentFilter(EMChatManager.getInstance()
//				.getOfflineMessageBroadcastAction());
//				registerReceiver(offlineMessageReceiver,
//				offlineMessageIntentFilter);

				myContactListener = new MyContactListener();
				EMContactManager.getInstance().setContactListener(
						myContactListener);

				myConnectionListener = new MyConnectionListener();
				EMChatManager.getInstance().addConnectionListener(
						myConnectionListener);
				myGroupChangeListener = new MyGroupChangeListener();
				EMGroupManager.getInstance().addGroupChangeListener(
						myGroupChangeListener);

				registerReceiver(mHomeKeyEventReceiver, new IntentFilter(
						Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
				registerReceiver(mLockScrennEventReceiver, new IntentFilter(
						Intent.ACTION_SCREEN_OFF));

				// 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
				EMChat.getInstance().setAppInited();
			};
		}.start();
        try {
            List<BuyNews> findBuyAll = dbUtil.findAll(Selector
                    .from(BuyNews.class).where("benben_id", "=",user.getBenbenId()));
//                    .where(WhereBuilder.b("benben_id", "=",user.getBenbenId())));
            if(findBuyAll!=null && findBuyAll.size()>0){
                for(int i=0;i<findBuyAll.size();i++){
                    mApplication.buyNewses.add(0, findBuyAll.get(i));
                }
            }


        } catch (DbException e) {
            e.printStackTrace();
        }
        saveInviteMsg();
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);
		// 检查更新
		checkUpdate();
	}

	// 检查更新
	private void checkUpdate() {
		setShowLoding(false);
		if (CommonUtils.isNetworkAvailable(mContext)) {
			InteNetUtils.getInstance(mContext).checkVersion(
					new RequestCallBack<String>() {
						@Override
						public void onSuccess(ResponseInfo<String> json) {
							AndriodVersion andriodVersion = new AndriodVersion();
							JSONObject object = null;
							try {
								object = new JSONObject(json.result);
								andriodVersion.parseJSON(object);
							} catch (Exception e) {
								e.printStackTrace();
								ToastUtils.Errortoast(mContext, "服务器数据有误!");
								return;
							}

							boolean versionCompare = CommonUtils
									.versionCompare(mContext,
											andriodVersion.getAndriodVersion());

							if (!versionCompare) {
								CommonUtils.showUpdateDialog(mContext,
										andriodVersion.getUrl(),
										andriodVersion.getUpdateContent(),
										new Handler());
							}
						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							// TODO Auto-generated method stub

						}
					});
		}
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitleView();
		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mContext.startAnimActivity(ActivityNews.class);
			}
		});

		comritlebar = findViewById(R.id.comritlebar);
		black_box = findViewById(R.id.black_box);

		tab_one = (RelativeLayout) findViewById(R.id.tab_one);
		tab_two = (RelativeLayout) findViewById(R.id.tab_two);
		tab_three = (RelativeLayout) findViewById(R.id.tab_three);
		tab_four = (RelativeLayout) findViewById(R.id.tab_four);
		tab_five = (RelativeLayout) findViewById(R.id.tab_five);
		tab_num = findViewById(R.id.tab_num);

		tab_two_num = (TextView) findViewById(R.id.tab_two_num);
        tab_four_num = (TextView) findViewById(R.id.tab_four_num);
        tab_four_hint = (TextView) findViewById(R.id.tab_four_hint);

		ContactsFragment mainFragment = new ContactsFragment();
		huanXinFragment = new ChatAllHistoryFragment();
		PlayPhoneFragment mPlayPhoneFragment = new PlayPhoneFragment();
		PrivateFragment mPrivateFragment = new PrivateFragment();
		MyFragment fiveFragment = new MyFragment();

		mApplication.fragments = fragments = new Fragment[] { mainFragment,
				huanXinFragment, mPlayPhoneFragment, mPrivateFragment,
				fiveFragment };

		List<Fragment> fragments2 = getSupportFragmentManager().getFragments();
		if (fragments2 != null) {
			for (Fragment f : fragments2) {
				getSupportFragmentManager().beginTransaction().hide(f)
						.remove(f).commit();
			}
		}
		getSupportFragmentManager().beginTransaction()
				.add(R.id.main_fragment_content, mainFragment)
				.add(R.id.main_fragment_content, huanXinFragment)
				.hide(huanXinFragment).show(mainFragment).commit();
		changeTab(R.id.tab_one);
		changeFragment(R.id.tab_one);

	}

	@Override
	public void initDate(Bundle savedInstanceState) {

		Intent intent = getIntent();
		from = intent.getStringExtra("source");

		inviteMessgeDao = new InviteMessgeDao(this);

	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onHttpStart() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSuccess(JSONObject jsonObject) {
		// TODO Auto-generated method stub
        if(jsonObject.optInt("ret_num")==0){
            try {
                JSONObject jsonObjectCreation = jsonObject.getJSONObject("creation");
                int creationNum = jsonObjectCreation.optInt("applaud")+jsonObjectCreation.optInt("watcher_num");
//                int creationAboutMe = jsonObjectCreation.optInt("publish")+jsonObjectCreation.optInt("hit_me");
                JSONObject jsonObjectFriend = jsonObject.getJSONObject("friend");
                int friendNum = jsonObjectFriend.optInt("applaud")+jsonObjectFriend.optInt("friend_num");
//                int friendAboutMe =jsonObjectFriend.optInt("publish")+jsonObjectFriend.optInt("hit_me");
                CrashApplication.getInstance().creationNum = creationNum;
                CrashApplication.getInstance().friendNum = friendNum;
//                CrashApplication.getInstance().creationAboutMe = creationAboutMe;
//                CrashApplication.getInstance().friendAboutMe = friendAboutMe;
                int friendAboutMe=0;
                int creationAboutMe=0;
                try {

                    List<NewMsgAboutMe> friendAboutMeLists =dbUtil.findAll(Selector.from(NewMsgAboutMe.class).where("type","=",1).and("is_new","=",true).and("benben_id","=",CrashApplication.getInstance().user.getBenbenId()));
                    if(friendAboutMeLists!=null){
                        friendAboutMe = friendAboutMeLists.size();
                    }
                    List<NewMsgAboutMe> creationAboutMeLists =dbUtil.findAll(Selector.from(NewMsgAboutMe.class).where("type","=",2).and("is_new","=",true).and("benben_id","=",CrashApplication.getInstance().user.getBenbenId()));
                    if(creationAboutMeLists!=null){
                        creationAboutMe = creationAboutMeLists.size();
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }


                int allNum =creationNum+friendNum;
                int allNumAboutMe =creationAboutMe+friendAboutMe;
                if(allNumAboutMe<=0){
                    tab_four_num.setVisibility(View.GONE);
                    if(allNum<=0){
                        tab_four_hint.setVisibility(View.GONE);
                    }else{
                        tab_four_hint.setVisibility(View.VISIBLE);
                    }
                }else if(allNumAboutMe<=99){
                    tab_four_num.setText(allNumAboutMe + "");
                    tab_four_num.setVisibility(View.VISIBLE);
                }else{
                    tab_four_num.setText("99+");
                    tab_four_num.setVisibility(View.VISIBLE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		// TODO Auto-generated method stub
	}

	private int curTouchTab;
	private int lastTouchTab;
	public View comritlebar;
	private View black_box;

	// 底部按钮点击
	public void touchTab(View v) {
		int id = v.getId();

		changeTab(id);
		changeFragment(id);
	}

	private void setTabStyle(RelativeLayout view, int positon, boolean isTouch) {
		ImageView tab_img = (ImageView) view.findViewById(R.id.tab_img);
		if (isTouch) {
			tab_img.setImageResource(touchImg[positon]);
		} else {
			tab_img.setImageResource(nomalImg[positon]);
		}
	}

	public void changeTab(int id) {
		setTabStyle(tab_one, 0, false);
		setTabStyle(tab_two, 1, false);
		setTabStyle(tab_three, 2, false);
		setTabStyle(tab_four, 3, false);
		setTabStyle(tab_five, 4, false);

		if (id != R.id.tab_three) {
			PlayPhoneUtils.hinePlayNumBox();
		}

		switch (id) {
		case R.id.tab_one:
			setTabStyle(tab_one, 0, true);
			break;
		case R.id.tab_two:
			setTabStyle(tab_two, 1, true);
			break;
		case R.id.tab_three:
			setTabStyle(tab_three, 2, true);
			break;
		case R.id.tab_four:
			setTabStyle(tab_four, 3, true);
			break;
		case R.id.tab_five:
			setTabStyle(tab_five, 4, true);
            ((MyFragment) fragments[4]).refreshIntegral();

			break;
		}
	}

	private void changeFragment(int id) {
		switch (id) {
		case R.id.tab_one:
			chanageTitle(new TitleMode("#068cd9", "添加", 0,
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							// // 添加好友
							// InfoSimpleMsgHint hint = new InfoSimpleMsgHint(
							// mContext, R.style.MyDialog1);
							//
							// hint.show();
							startAnimActivity(ActivityAddFriend.class);
						}
					}, "消息", 0, null, "通讯录", 0));
			curTouchTab = 0;
			break;
		case R.id.tab_two:
			count1 = 0;
			chanageTitle(new TitleMode("#068cd9", "", R.drawable.ic_addwihte,
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							startAnimActivity(ActivityBenBenFriend.class);
						}
					}, "消息", 0, null, "聊天", 0));
			curTouchTab = 1;
			break;
		case R.id.tab_three:
			chanageTitle(new TitleMode("#068cd9", "", 0, null, "消息", 0, null,
					"拨号", 0));
			curTouchTab = 2;
			break;
		case R.id.tab_four:
			chanageTitle(new TitleMode("#068cd9", "", 0, null, "消息", 0, null,
					"发现", 0));
			curTouchTab = 3;
			break;
		case R.id.tab_five:
			chanageTitle(new TitleMode("#068cd9", "", R.drawable.icon_order_check, new OnClickListener() {
                @Override
                public void onClick(View view) {
                    startAnimActivity(ActivityOrderCheck.class);
                }
            }, "消息", 0, null,"我的", 0));
			curTouchTab = 4;
			break;

		}
		int size = fragments.length;

		if (curTouchTab != lastTouchTab) {
			FragmentTransaction trx = getSupportFragmentManager()
					.beginTransaction();
			trx.hide(fragments[lastTouchTab]);
			if (!fragments[curTouchTab].isAdded()) {
				trx.add(R.id.main_fragment_content, fragments[curTouchTab]);
			}
			if (fragments[curTouchTab].isResumed()
					&& fragments[curTouchTab] instanceof BaseFragment) {
				((BaseFragment) fragments[curTouchTab]).onRefresh();
			}
			trx.show(fragments[curTouchTab]).commit();
		}

		// for (int i = 0; i < size; i++) {
		// if (i == curTouchTab) {
		// continue;
		// }
		// if (fragments.get(i).isResumed()) {
		// beginTransaction.hide(fragments.get(i));
		// fragments.get(i).onPause();
		// }
		// }
		// if (addFragments != null) {
		// boolean contains = addFragments
		// .contains(fragments.get(curTouchTab));
		// if (contains) {
		// beginTransaction.show(fragments.get(curTouchTab));
		// if (fragments.get(curTouchTab).isResumed()) {
		// fragments.get(curTouchTab).onResume();
		// }
		// } else {
		// beginTransaction.add(R.id.main_fragment_content,
		// fragments.get(curTouchTab));
		// beginTransaction.show(fragments.get(curTouchTab));
		// if (fragments.get(curTouchTab).isResumed()) {
		// fragments.get(curTouchTab).onResume();
		// }
		// }
		// } else {
		// beginTransaction.add(R.id.main_fragment_content,
		// fragments.get(curTouchTab));
		// beginTransaction.show(fragments.get(curTouchTab));
		// if (fragments.get(curTouchTab).isResumed()) {
		// fragments.get(curTouchTab).onResume();
		// }
		// }
		//
		// beginTransaction.commit();
		lastTouchTab = curTouchTab;
	}

	// 提供给fragment
	public String getFrom() {
		return from;
	}

	// 点击俩次退出
	@Override
	public void onBackPressed() {
		mApplication.shutDown();
	}

	public boolean isConflict() {
		return isConflict;
	}

	public void setConflict(boolean isConflict) {
		this.isConflict = isConflict;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 注销广播接收者
		try {
            EMChatManager.getInstance().unregisterEventListener(this);
			unregisterReceiver(mrefreshBrocast);
			unregisterReceiver(msgCenterReceiver);
//			unregisterReceiver(msgReceiver);
			unregisterReceiver(fragmentBroadcast);
			unregisterReceiver(mHomeKeyEventReceiver);
			unregisterReceiver(mLockScrennEventReceiver);
		} catch (Exception e) {
		}
//		try {
//			unregisterReceiver(ackMessageReceiver);
//		} catch (Exception e) {
//		}
//		try {
//			unregisterReceiver(cmdMessageReceiver);
//		} catch (Exception e) {
//		}

		EMChatManager.getInstance().removeConnectionListener(
				myConnectionListener);
		EMContactManager.getInstance().removeContactListener();
		EMGroupManager.getInstance().removeGroupChangeListener(
				myGroupChangeListener);
//		try {
//		unregisterReceiver(offlineMessageReceiver);
//		} catch (Exception e) {
//		}

		if (conflictBuilder != null) {
			conflictBuilder.create().dismiss();
			conflictBuilder = null;
		}

	}

	private int count1 = 0;
	private int count2 = 0;

	/**
	 * 刷新未读消息数
	 */
	public void updateUnreadLabel() {
		count1 = getUnreadMsgCountTotal();
		count2 = getUnreadAddressCountTotal();
		if (count1 > 0) {
			tab_two_num.setText(String.valueOf(count1 + count2));
			tab_two_num.setVisibility(View.VISIBLE);
		} else {
			if (count2 <= 0) {
				tab_two_num.setVisibility(View.INVISIBLE);
			}
		}
	}

	/**
	 * 刷新申请与通知消息数
	 */
	public void updateUnreadAddressLable() {
		runOnUiThread(new Runnable() {
			public void run() {
				count2 = getUnreadAddressCountTotal();
				count1 = getUnreadMsgCountTotal();
				if (count2 > 0) {
					tab_two_num.setText(String.valueOf(count1 + count2));
					tab_two_num.setVisibility(View.VISIBLE);
				} else {
					if (count1 <= 0) {
						tab_two_num.setVisibility(View.INVISIBLE);
					}
				}
			}
		});

	}

	/**
	 * 获取未读申请与通知消息
	 * 
	 * @return
	 */
	public int getUnreadAddressCountTotal() {
		int unreadAddressCountTotal = 0;
        int unreadBuyCountTotal = 0;
		if (mApplication.getBme() != null) {
            unreadAddressCountTotal = mApplication.getBme().getPublicNum();
            unreadBuyCountTotal = mApplication.getBme().getBuyNum();
        }
		return unreadAddressCountTotal+unreadBuyCountTotal;
	}

	/**
	 * 获取未读消息数
	 * 
	 * @return
	 */
	private CrashApplication mCrashApplication;

	public int getUnreadMsgCountTotal() {
		SharedPreferences mySharedPreferences= getSharedPreferences("benben",
				Activity.MODE_PRIVATE);
//		int unreadMsgCountTotal = 0;
//		int pbNum = 0;
//		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
//		Hashtable<String, EMConversation> conversations = EMChatManager
//				.getInstance().getAllConversations();
//		List<BenbenEMConversation> list = new ArrayList<BenbenEMConversation>();
//		for (EMConversation conversation : conversations.values()) {
//			if (conversation.getAllMessages().size() != 0) {
//				BenbenEMConversation benbenEMConversation = new BenbenEMConversation(
//						BenbenEMConversation.NOMAL, conversation);
//				list.add(benbenEMConversation);
//			}
//		}
//		List<BenbenEMConversation> emConversations = new ArrayList<BenbenEMConversation>();
		mCrashApplication = CrashApplication.getInstance();
//		for (BenbenEMConversation emConversation : list) {
//			if (emConversation.getType() == BenbenEMConversation.NOMAL) {
//				EMConversation conversation = emConversation
//						.getmEMConversation();
//				// 获取用户username或者群组groupid
//				final String username = conversation.getUserName();
//				EMContact contact = null;
//				boolean isGroup = false;
//				if (conversation.isGroup()) {
//					isGroup = true;
//				}
//
//				if (isGroup) {
//					TalkGroup contactsGroup = mCrashApplication.mTalkGroupMap
//							.get(username);
//
//					if (contactsGroup != null
//							&& contactsGroup.getStatus().equals("1")) {
//						if (list.contains(emConversation)) {
//							pbNum += emConversation.getmEMConversation()
//									.getUnreadMsgCount();
//							// unreadMsgCountTotal -= emConversation
//							// .getmEMConversation().getUnreadMsgCount();
//						}
//					}
//				}
//			}
//		}


        int unreadMsgCountTotal = 0;
        int chatroomUnreadMsgCount = 0;
        unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
//        pbNum += unreadMsgCountTotal;
        for(EMConversation conversation:EMChatManager.getInstance().getAllConversations().values()){
            if(conversation.getType() == EMConversation.EMConversationType.ChatRoom)
                chatroomUnreadMsgCount=chatroomUnreadMsgCount+conversation.getUnreadMsgCount();
        }

//		if(mySharedPreferences.getInt("pbNum", 0) > pbNum){
//			pbNum =mySharedPreferences.getInt("pbNum", 0);
//		}
//
//
//		SharedPreferences.Editor editor = mySharedPreferences.edit();
//		editor.putInt("pbNum", pbNum);
//		editor.commit();
		
//		unreadMsgCountTotal -= pbNum;
		
		// unreadMsgCountTotal =
		// EMChatManager.getInstance().getUnreadMsgsCount()
		// - emConversations.size();
		return unreadMsgCountTotal-chatroomUnreadMsgCount;
	}

    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage: // 普通消息
            {
                EMMessage message = (EMMessage) event.getData();
               // 提示新消息
				initNewMessage(message);
                HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
                break;
            }
            case EventReadAck:
            {
                EMMessage message = (EMMessage) event.getData();
                String msgid = message.getMsgId();
                String from = message.getFrom();
                EMConversation conversation = EMChatManager.getInstance()
                        .getConversation(from);
                if (conversation != null) {
                    // 把message设为已读
                    EMMessage msg = conversation.getMessage(msgid);

                    if (msg != null) {

                        // 2014-11-5 修复在某些机器上，在聊天页面对方发送已读回执时不立即显示已读的bug
                        if (ChatActivity.activityInstance != null) {
                            if (msg.getChatType() == ChatType.Chat) {
                                if (from.equals(ChatActivity.activityInstance
                                        .getToChatUsername()))
                                    return;
                            }
                        }

                        msg.isAcked = true;
                    }
                }
                break;
            }
            case EventNewCMDMessage:{
                EMMessage message = (EMMessage) event.getData();
                initNewCMDMessage(message);
                break;
            }

            case EventOfflineMessage: {
				List<EMMessage> messages = (List<EMMessage>) event.getData();
				for(int i=0;i<messages.size();i++){
					EMMessage message = messages.get(i);
					initNewMessage(message);
//					HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
				}
                break;
            }

            case EventConversationListChanged: {
                break;
            }


            default:
                break;
        }

    }

    private void initNewCMDMessage(EMMessage message) {
        long created_time = message.getIntAttribute("created_time",0);
        int creation_id = message.getIntAttribute("creation_id",0);
        int friend_id = message.getIntAttribute("friend_id",0);
        String id = message.getStringAttribute("id","");
        String member_id = message.getStringAttribute("member_id","");
        String description = message.getStringAttribute("description","");
        String huanxin_username = message.getStringAttribute("huanxin_username","");
        String nick_name = message.getStringAttribute("nick_name","");
        String poster = message.getStringAttribute("poster","");
        int replier = message.getIntAttribute("replier",0);
        String review = message.getStringAttribute("review","");
        NewMsgAboutMe newMsgAboutMe = new NewMsgAboutMe();
        newMsgAboutMe.setId(id);
        newMsgAboutMe.setCreated_time(created_time);
        newMsgAboutMe.setDescription(description);
        newMsgAboutMe.setHuanxin_username(huanxin_username);
        newMsgAboutMe.setMember_id(member_id);
        newMsgAboutMe.setNick_name(nick_name);
        newMsgAboutMe.setPoster(poster);
        newMsgAboutMe.setReplier(replier);
        newMsgAboutMe.setReview(review);
        if(creation_id!=0){
            newMsgAboutMe.setMid(creation_id);
            newMsgAboutMe.setType(2);//微创作
        }else{
            newMsgAboutMe.setMid(friend_id);
            newMsgAboutMe.setType(1);//朋友圈
        }
        newMsgAboutMe.setIs_new(true);
        newMsgAboutMe.setBenben_id(user.getBenbenId());
        try {
            dbUtil.saveOrUpdate(newMsgAboutMe);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if(creation_id!=0) {
            sendBroadcast(new Intent("hasCreationNews"));
        }else{
            sendBroadcast(new Intent("hasFriendNews"));
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int friendAboutMe=0;
                int creationAboutMe=0;
                try {

                    List<NewMsgAboutMe> friendAboutMeLists =dbUtil.findAll(Selector.from(NewMsgAboutMe.class).where("type","=",1).and("is_new","=",true).and("benben_id","=",CrashApplication.getInstance().user.getBenbenId()));
                    if(friendAboutMeLists!=null){
                        friendAboutMe = friendAboutMeLists.size();
                    }
                    List<NewMsgAboutMe> creationAboutMeLists =dbUtil.findAll(Selector.from(NewMsgAboutMe.class).where("type","=",2).and("is_new","=",true).and("benben_id","=",CrashApplication.getInstance().user.getBenbenId()));
                    if(creationAboutMeLists!=null){
                        creationAboutMe = creationAboutMeLists.size();
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }

                int allNumAboutMe =creationAboutMe+friendAboutMe;
                if(allNumAboutMe<=0){
                    tab_four_num.setVisibility(View.GONE);
                }else if(allNumAboutMe<=99){
                    tab_four_num.setText(allNumAboutMe + "");
                    tab_four_num.setVisibility(View.VISIBLE);
                }else{
                    tab_four_num.setText("99+");
                    tab_four_num.setVisibility(View.VISIBLE);
                }


                ((PrivateFragment) fragments[3]).refresh();
            }
        });



    }

    private void initNewMessage(EMMessage message){
// 主页面收到消息后，主要为了提示未读，实际消息内容需要到chat页面查看
        String from = message.getFrom();
        // 消息id
        String msgId = message.getMsgId();

        int t1 = message.getIntAttribute("t1",1);
        int t4 = message.getIntAttribute("t4",1);
        if(t1==1){
            int t2 = message.getIntAttribute("t2",0);
            if(t2==0){
                if(t4==4){
                    try {
                        int buyid = message.getIntAttribute("buyid", 0);
                        BuyNews buyNews = new BuyNews();
                        buyNews.setBid(buyid+"");
                        buyNews.setContent(((TextMessageBody) message.getBody()).getMessage());
                        buyNews.setTime(message.getMsgTime());
                        buyNews.setStatus(0);
                        buyNews.setBenben_id(user.getBenbenId());
                        mApplication.buyNewses.add(0, buyNews);
                        dbUtil.save(buyNews);
//                            sendBroadcast(new Intent("hasNews"));
                    } catch (DbException e) {
                        e.printStackTrace();
                    }

                    EMConversation conversation = EMChatManager.getInstance().getConversation(from);
                    conversation.removeMessage(msgId);
                    notifyNewIviteMessage();

                }else {
                    // 2014-10-22 修复在某些机器上，在聊天页面对方发消息过来时不立即显示内容的bug
                    if (ChatActivity.activityInstance != null) {
                        if (message.getChatType() == ChatType.GroupChat) {
                            if (message.getTo().equals(
                                    ChatActivity.activityInstance.getToChatUsername()))
                                return;
                        } else {
                            if (from.equals(ChatActivity.activityInstance
                                    .getToChatUsername()))
                                return;
                        }
                    }
//                    // 状态栏提示
//                    notifyNewMessage(message);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 刷新bottom bar消息未读数
                            updateUnreadLabel();
                            if (curTouchTab == 1) {
                                // 当前页面如果为聊天历史页面，刷新此页面
                                if (huanXinFragment != null) {
                                    huanXinFragment.refresh();
                                }
                            }
                        }
                    });
//                    if (curTouchTab == 1) {
//                        // 当前页面如果为聊天历史页面，刷新此页面
//                        if (huanXinFragment != null) {
//                            huanXinFragment.refresh();
//                        }
//                    }
                }
            }else if(t2==1){
				// 注销广播接收者，否则在ChatActivity中会收到这个广播
                String nick_name = message.getStringAttribute("nick_name", "");
                String huanxin_username = message.getStringAttribute("hxname","");
                int leg_id = message.getIntAttribute("leg_id", 0);
                String leg_name = message.getStringAttribute("leg_name","");
                String leg_poster = message.getStringAttribute("leg_poster","");
                String news_id = message.getStringAttribute("news_id","");
                int t3 = message.getIntAttribute("t3",0);
                String user_poster = message.getStringAttribute("user_poster","");
                String group_name = message.getStringAttribute("group_name","");
                String group_poster = message.getStringAttribute("group_poster","");
                int type = PublicMessage.UNAGREE;
                if(t3==1){
                    type = PublicMessage.AGREE;
                }else if(t3==2){
                    type = PublicMessage.REFUSE;
                }

                if(t4==1){
                    try {
                        PublicMessage mPublicMessage = dbUtil.findFirst(Selector.from(
                                PublicMessage.class).where("huanxin_username", "=",
                                huanxin_username).and("sid","=",leg_id).and("classType","=",PublicMessage.Union));
                        if (mPublicMessage == null) {
                            mPublicMessage = new PublicMessage();
                            mPublicMessage.setSid(leg_id);
                            mPublicMessage.setClassType(PublicMessage.Union);
                            mPublicMessage.setHuanxin_username(huanxin_username);
                            mPublicMessage.setStatus(type);
                            mPublicMessage.setName(leg_name);
                            mPublicMessage.setReason(((TextMessageBody) message.getBody()).getMessage());
                            mPublicMessage.setCreatTime(TimeUtil.now());
                            mPublicMessage.setNick_name(nick_name);
                            mPublicMessage.setPoster(leg_poster);
                            mPublicMessage.setNews_id(news_id);

                            if (!mApplication.mPublicMessage.contains(mPublicMessage)) {
                                mApplication.mPublicMessage.add(0, mPublicMessage);
                            }
                            try {
                                dbUtil.saveOrUpdate(mPublicMessage);
                            } catch (DbException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            sendBroadcast(new Intent("hasNews"));
                        } else {
                            mPublicMessage.setIsLook(PublicMessage.UNLOOK);
                            mPublicMessage.setStatus(type);
                            mPublicMessage.setCreatTime(TimeUtil.now());
                            mPublicMessage.setReason(((TextMessageBody) message.getBody()).getMessage());
                            mApplication.mPublicMessage.add(0, mPublicMessage);
                            dbUtil.update(mPublicMessage);
                        }

                    } catch (DbException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }else if(t4==2){
                    PublicMessage mPublicMessage = new PublicMessage();
                    mPublicMessage.setHuanxin_username("");
                    mPublicMessage.setClassType(PublicMessage.GROUP);
                    mPublicMessage.setStatus(type);
                    mPublicMessage.setReason(((TextMessageBody) message.getBody()).getMessage());
                    mPublicMessage.setCreatTime(TimeUtil.now());
                    mPublicMessage.setName(group_name);
                    mPublicMessage.setPoster(group_poster);
                    if (!mApplication.mPublicMessage
                            .contains(mPublicMessage)) {
                        mApplication.mPublicMessage.add(0,
                                mPublicMessage);
                    }

                    try {
                        dbUtil.saveOrUpdate(mPublicMessage);
                    } catch (DbException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }else if(t4==3){
                    PublicMessage mPublicMessage = new PublicMessage();
                    mPublicMessage.setHuanxin_username("");
                    mPublicMessage.setClassType(PublicMessage.FRIEND);
                    mPublicMessage.setStatus(type);
                    mPublicMessage.setReason(((TextMessageBody) message.getBody()).getMessage());
                    mPublicMessage.setCreatTime(TimeUtil.now());
                    mPublicMessage.setNick_name(nick_name);
                    mPublicMessage.setPoster(user_poster);
                    if (!mApplication.mPublicMessage
                            .contains(mPublicMessage)) {
                        mApplication.mPublicMessage.add(0,
                                mPublicMessage);
                    }

                    try {
                        dbUtil.saveOrUpdate(mPublicMessage);
                    } catch (DbException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }else if(t4==5){
                    String huanxin_groupid = message.getStringAttribute("huanxin_groupid", "");
                    String transfer_id = message.getStringAttribute("transfer_id", "");
                    try {
                        PublicMessage mPublicMessage = dbUtil.findFirst(Selector.from(
                                PublicMessage.class).where("huanxin_username", "=",
                                huanxin_groupid).and("classType","=",PublicMessage.GROUPCHANGE));
                        if (mPublicMessage == null) {
                            mPublicMessage = new PublicMessage();
                            mPublicMessage.setName(group_name);
                            mPublicMessage.setHuanxin_username(huanxin_groupid);
                            mPublicMessage.setNews_id(transfer_id);
                            mPublicMessage.setClassType(PublicMessage.GROUPCHANGE);
                            mPublicMessage.setStatus(type);
                            mPublicMessage.setReason(((TextMessageBody) message.getBody()).getMessage());
                            mPublicMessage.setCreatTime(TimeUtil.now());
                            mPublicMessage.setPoster(group_poster);
                            if (!mApplication.mPublicMessage
                                    .contains(mPublicMessage)) {
                                mApplication.mPublicMessage.add(0,
                                        mPublicMessage);
                            }

                            try {
                                dbUtil.saveOrUpdate(mPublicMessage);
                            } catch (DbException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }else{
                            mPublicMessage.setName(group_name);
                            mPublicMessage.setNews_id(transfer_id);
                            mPublicMessage.setIsLook(PublicMessage.UNLOOK);
                            mPublicMessage.setStatus(type);
                            mPublicMessage.setCreatTime(TimeUtil.now());
                            mPublicMessage.setReason(((TextMessageBody) message.getBody()).getMessage());
                            mApplication.mPublicMessage.add(0, mPublicMessage);
                            dbUtil.update(mPublicMessage);
                        }
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }else if(t4==6){
					//直通车转让
					String transfer_id = message.getStringAttribute("transfer_id", "");
					String apply_nickname = message.getStringAttribute("apply_nickname", "");
					String apply_poster = message.getStringAttribute("apply_poster", "");
					String store_id = message.getStringAttribute("store_id", "");
					String store_name = message.getStringAttribute("store_name", "");
					String vip_account = message.getStringAttribute("vip_account", "");
					try {
						PublicMessage mPublicMessage = dbUtil.findFirst(Selector.from(
								PublicMessage.class).where("news_id", "=",transfer_id).and("classType","=",PublicMessage.NUMBERTRAIN_CHANGE));
						if (mPublicMessage == null) {
							mPublicMessage = new PublicMessage();
							mPublicMessage.setHuanxin_username("");
							mPublicMessage.setNews_id(transfer_id);
							mPublicMessage.setNick_name(apply_nickname);
							mPublicMessage.setPoster(apply_poster);
							mPublicMessage.setStore_id(store_id);
							mPublicMessage.setStore_name(store_name);
							mPublicMessage.setReason(((TextMessageBody) message.getBody()).getMessage());
							mPublicMessage.setVip_account(vip_account);
							mPublicMessage.setClassType(PublicMessage.NUMBERTRAIN_CHANGE);
							mPublicMessage.setStatus(type);
							mPublicMessage.setCreatTime(TimeUtil.now());
							if (!mApplication.mPublicMessage.contains(mPublicMessage)) {
								mApplication.mPublicMessage.add(0, mPublicMessage);
							}

							try {
								dbUtil.saveOrUpdate(mPublicMessage);
							} catch (DbException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else{
							mPublicMessage.setHuanxin_username("");
							mPublicMessage.setNews_id(transfer_id);
							mPublicMessage.setStore_id(store_id);
							mPublicMessage.setStore_name(store_name);
							mPublicMessage.setReason(((TextMessageBody) message.getBody()).getMessage());
							mPublicMessage.setVip_account(vip_account);
							mPublicMessage.setClassType(PublicMessage.NUMBERTRAIN_CHANGE);
							mPublicMessage.setStatus(type);
							mPublicMessage.setIsLook(PublicMessage.UNLOOK);
							mPublicMessage.setCreatTime(TimeUtil.now());
							mApplication.mPublicMessage.add(0, mPublicMessage);
							dbUtil.update(mPublicMessage);
						}
					} catch (DbException e) {
						e.printStackTrace();
					}
				}

                EMConversation conversation = EMChatManager.getInstance().getConversation(from);
                conversation.removeMessage(msgId);
                notifyNewIviteMessage();
            }

        }else if(t1==2){
            InteNetUtils.getInstance(mContext).getContactInfoFromQR(null,
                    from, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                            JSONObject jsonObject = null;
                            Contacts mContacts = new Contacts();
                            try {
                                jsonObject = new JSONObject(stringResponseInfo.result);
                                mContacts.parseJSONSingle4(jsonObject);
                                if(mContacts!=null){
                                    dbUtil.saveOrUpdate(mContacts);
                                    if (mContacts.getPhones() != null)
                                        dbUtil.saveOrUpdateAll(mContacts.getPhones());
                                }
                                ((ContactsFragment) fragments[0]).initlocakData();
                                ((ChatAllHistoryFragment) fragments[1]).refresh();
                                // 刷新bottom bar消息未读数
                                updateUnreadLabel();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (NetRequestException e) {
                                e.printStackTrace();
                            } catch (DbException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(HttpException e, String s) {

                        }
                    });
        }else{
            EMConversation conversation = EMChatManager.getInstance().getConversation(from);
            conversation.removeMessage(msgId);
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(UPDATA);
                }
            });
        }
    }

//    /**
//	 * 新消息广播接收者
//	 */
//	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			// 主页面收到消息后，主要为了提示未读，实际消息内容需要到chat页面查看
//			String from = intent.getStringExtra("from");
//			// 消息id
//			String msgId = intent.getStringExtra("msgid");
//			EMMessage message = EMChatManager.getInstance().getMessage(msgId);
//            int t1 = message.getIntAttribute("t1",1);
//            int t4 = message.getIntAttribute("t4",1);
//            if(t1==1){
//                int t2 = message.getIntAttribute("t2",0);
//                if(t2==0){
//                    if(t4==4){
//                        try {
//                            int buyid = message.getIntAttribute("buyid", 0);
//                            BuyNews buyNews = new BuyNews();
//                            buyNews.setBid(buyid+"");
//                            buyNews.setContent(((TextMessageBody) message.getBody()).getMessage());
//                            buyNews.setTime(message.getMsgTime());
//                            buyNews.setStatus(0);
//                            buyNews.setBenben_id(user.getBenbenId());
//                            mApplication.buyNewses.add(0, buyNews);
//                            dbUtil.save(buyNews);
////                            sendBroadcast(new Intent("hasNews"));
//                        } catch (DbException e) {
//                            e.printStackTrace();
//                        }
//
//                        EMConversation conversation = EMChatManager.getInstance().getConversation(from);
//                        conversation.removeMessage(msgId);
//                        notifyNewIviteMessage();
//
//                    }else {
//                        // 2014-10-22 修复在某些机器上，在聊天页面对方发消息过来时不立即显示内容的bug
//                        if (ChatActivity.activityInstance != null) {
//                            if (message.getChatType() == ChatType.GroupChat) {
//                                if (message.getTo().equals(
//                                        ChatActivity.activityInstance.getToChatUsername()))
//                                    return;
//                            } else {
//                                if (from.equals(ChatActivity.activityInstance
//                                        .getToChatUsername()))
//                                    return;
//                            }
//                        }
//
//                        // 注销广播接收者，否则在ChatActivity中会收到这个广播
//                        abortBroadcast();
//
//                        // 状态栏提示
//                        // notifyNewMessage(message);
//
//                        // 刷新bottom bar消息未读数
//                        updateUnreadLabel();
//                        if (curTouchTab == 1) {
//                            // 当前页面如果为聊天历史页面，刷新此页面
//                            if (huanXinFragment != null) {
//                                huanXinFragment.refresh();
//                            }
//                        }
//                    }
//                }else if(t2==1){
//                    // 注销广播接收者，否则在ChatActivity中会收到这个广播
//                    abortBroadcast();
//                    String nick_name = message.getStringAttribute("nick_name", "");
//                    String huanxin_username = message.getStringAttribute("hxname","");
//                    int leg_id = message.getIntAttribute("leg_id", 0);
//                    String leg_name = message.getStringAttribute("leg_name","");
//                    String leg_poster = message.getStringAttribute("leg_poster","");
//                    String news_id = message.getStringAttribute("news_id","");
//                    int t3 = message.getIntAttribute("t3",0);
//                    String user_poster = message.getStringAttribute("user_poster","");
//                    String group_name = message.getStringAttribute("group_name","");
//                    String group_poster = message.getStringAttribute("group_poster","");
//                    int type = PublicMessage.UNAGREE;
//                    if(t3==1){
//                        type = PublicMessage.AGREE;
//                    }else if(t3==2){
//                        type = PublicMessage.REFUSE;
//                    }
//
//                    if(t4==1){
//                        try {
//                            PublicMessage mPublicMessage = dbUtil.findFirst(Selector.from(
//                                    PublicMessage.class).where("huanxin_username", "=",
//                                    huanxin_username).and("sid","=",leg_id).and("classType","=",PublicMessage.Union));
//                            if (mPublicMessage == null) {
//                                mPublicMessage = new PublicMessage();
//                                mPublicMessage.setSid(leg_id);
//                                mPublicMessage.setClassType(PublicMessage.Union);
//                                mPublicMessage.setHuanxin_username(huanxin_username);
//                                mPublicMessage.setStatus(type);
//                                mPublicMessage.setName(leg_name);
//                                mPublicMessage.setReason(((TextMessageBody) message.getBody()).getMessage());
//                                mPublicMessage.setCreatTime(TimeUtil.now());
//                                mPublicMessage.setNick_name(nick_name);
//                                mPublicMessage.setPoster(leg_poster);
//                                mPublicMessage.setNews_id(news_id);
//
//                                if (!mApplication.mPublicMessage.contains(mPublicMessage)) {
//                                    mApplication.mPublicMessage.add(0, mPublicMessage);
//                                }
//                                try {
//                                    dbUtil.saveOrUpdate(mPublicMessage);
//                                } catch (DbException e) {
//                                    // TODO Auto-generated catch block
//                                    e.printStackTrace();
//                                }
//                                sendBroadcast(new Intent("hasNews"));
//                            } else {
//                                mPublicMessage.setIsLook(PublicMessage.UNLOOK);
//                                mPublicMessage.setStatus(type);
//                                mPublicMessage.setCreatTime(TimeUtil.now());
//                                mPublicMessage.setReason(((TextMessageBody) message.getBody()).getMessage());
//                                mApplication.mPublicMessage.add(0, mPublicMessage);
//                                dbUtil.update(mPublicMessage);
//                            }
//
//                        } catch (DbException e1) {
//                            // TODO Auto-generated catch block
//                            e1.printStackTrace();
//                        }
//                    }else if(t4==2){
//                        PublicMessage mPublicMessage = new PublicMessage();
//                        mPublicMessage.setHuanxin_username("");
//                        mPublicMessage.setClassType(PublicMessage.GROUP);
//                        mPublicMessage.setStatus(type);
//                        mPublicMessage.setReason(((TextMessageBody) message.getBody()).getMessage());
//                        mPublicMessage.setCreatTime(TimeUtil.now());
//                        mPublicMessage.setName(group_name);
//                        mPublicMessage.setPoster(group_poster);
//                        if (!mApplication.mPublicMessage
//                                .contains(mPublicMessage)) {
//                            mApplication.mPublicMessage.add(0,
//                                    mPublicMessage);
//                        }
//
//                        try {
//                            dbUtil.saveOrUpdate(mPublicMessage);
//                        } catch (DbException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//
//                    }else if(t4==3){
//                        PublicMessage mPublicMessage = new PublicMessage();
//                        mPublicMessage.setHuanxin_username("");
//                        mPublicMessage.setClassType(PublicMessage.FRIEND);
//                        mPublicMessage.setStatus(type);
//                        mPublicMessage.setReason(((TextMessageBody) message.getBody()).getMessage());
//                        mPublicMessage.setCreatTime(TimeUtil.now());
//                        mPublicMessage.setNick_name(nick_name);
//                        mPublicMessage.setPoster(user_poster);
//                        if (!mApplication.mPublicMessage
//                                .contains(mPublicMessage)) {
//                            mApplication.mPublicMessage.add(0,
//                                    mPublicMessage);
//                        }
//
//                        try {
//                            dbUtil.saveOrUpdate(mPublicMessage);
//                        } catch (DbException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    }else if(t4==5){
//                        String huanxin_groupid = message.getStringAttribute("huanxin_groupid", "");
//                        String transfer_id = message.getStringAttribute("transfer_id", "");
//                        try {
//                            PublicMessage mPublicMessage = dbUtil.findFirst(Selector.from(
//                                    PublicMessage.class).where("huanxin_username", "=",
//                                    huanxin_groupid).and("classType","=",PublicMessage.GROUPCHANGE));
//                            if (mPublicMessage == null) {
//                                mPublicMessage = new PublicMessage();
//                                mPublicMessage.setName(group_name);
//                                mPublicMessage.setHuanxin_username(huanxin_groupid);
//                                mPublicMessage.setNews_id(transfer_id);
//                                mPublicMessage.setClassType(PublicMessage.GROUPCHANGE);
//                                mPublicMessage.setStatus(type);
//                                mPublicMessage.setReason(((TextMessageBody) message.getBody()).getMessage());
//                                mPublicMessage.setCreatTime(TimeUtil.now());
//                                mPublicMessage.setPoster(group_poster);
//                                if (!mApplication.mPublicMessage
//                                        .contains(mPublicMessage)) {
//                                    mApplication.mPublicMessage.add(0,
//                                            mPublicMessage);
//                                }
//
//                                try {
//                                    dbUtil.saveOrUpdate(mPublicMessage);
//                                } catch (DbException e) {
//                                    // TODO Auto-generated catch block
//                                    e.printStackTrace();
//                                }
//                            }else{
//                                mPublicMessage.setName(group_name);
//                                mPublicMessage.setNews_id(transfer_id);
//                                mPublicMessage.setIsLook(PublicMessage.UNLOOK);
//                                mPublicMessage.setStatus(type);
//                                mPublicMessage.setCreatTime(TimeUtil.now());
//                                mPublicMessage.setReason(((TextMessageBody) message.getBody()).getMessage());
//                                mApplication.mPublicMessage.add(0, mPublicMessage);
//                                dbUtil.update(mPublicMessage);
//                            }
//                        } catch (DbException e) {
//                            e.printStackTrace();
//                        }
//
//
//
//                    }
//
//                    EMConversation conversation = EMChatManager.getInstance().getConversation(from);
//                    conversation.removeMessage(msgId);
//                    notifyNewIviteMessage();
//                }
//
//            }else if(t1==2){
//                InteNetUtils.getInstance(mContext).getContactInfoFromQR(null,
//                        from, new RequestCallBack<String>() {
//                            @Override
//                            public void onSuccess(ResponseInfo<String> stringResponseInfo) {
//                                JSONObject jsonObject = null;
//                                Contacts mContacts = new Contacts();
//                                try {
//                                    jsonObject = new JSONObject(stringResponseInfo.result);
//                                    mContacts.parseJSONSingle4(jsonObject);
//                                    if(mContacts!=null){
//                                        dbUtil.saveOrUpdate(mContacts);
//                                        if (mContacts.getPhones() != null)
//                                            dbUtil.saveOrUpdateAll(mContacts.getPhones());
//                                    }
//                                    ((ContactsFragment) fragments[0]).initlocakData();
//                                    ((ChatAllHistoryFragment) fragments[1]).refresh();
//                                    abortBroadcast();
//                                    // 刷新bottom bar消息未读数
//                                    updateUnreadLabel();
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                } catch (NetRequestException e) {
//                                    e.printStackTrace();
//                                } catch (DbException e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//
//                            @Override
//                            public void onFailure(HttpException e, String s) {
//
//                            }
//                        });
//            }else{
//                EMConversation conversation = EMChatManager.getInstance().getConversation(from);
//                conversation.removeMessage(msgId);
//                executorService.submit(new Runnable() {
//                    @Override
//                    public void run() {
//                        handler.sendEmptyMessage(UPDATA);
//                    }
//                });
//            }
//
//
//
//
//		}
//	}

//	/**
//	 * 消息回执BroadcastReceiver
//	 */
//	private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			abortBroadcast();
//
//			String msgid = intent.getStringExtra("msgid");
//			String from = intent.getStringExtra("from");
//			EMConversation conversation = EMChatManager.getInstance()
//					.getConversation(from);
//			if (conversation != null) {
//				// 把message设为已读
//				EMMessage msg = conversation.getMessage(msgid);
//
//				if (msg != null) {
//
//					// 2014-11-5 修复在某些机器上，在聊天页面对方发送已读回执时不立即显示已读的bug
//					if (ChatActivity.activityInstance != null) {
//						if (msg.getChatType() == ChatType.Chat) {
//							if (from.equals(ChatActivity.activityInstance
//									.getToChatUsername()))
//								return;
//						}
//					}
//
//					msg.isAcked = true;
//				}
//			}
//
//		}
//	};
//
//	/**
//	 * 透传消息BroadcastReceiver
//	 */
//	private BroadcastReceiver cmdMessageReceiver = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			abortBroadcast();
////			// EMLog.d(TAG, "收到透传消息");
////			// 获取cmd message对象
////			String msgId = intent.getStringExtra("msgid");
////			EMMessage message = intent.getParcelableExtra("message");
////			// 获取消息body
////			CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
////			String action = cmdMsgBody.action;// 获取自定义action
//            EMMessage message = intent.getParcelableExtra("message");
//            long created_time = message.getIntAttribute("created_time",0);
//            int creation_id = message.getIntAttribute("creation_id",0);
//            int friend_id = message.getIntAttribute("friend_id",0);
//            String id = message.getStringAttribute("id","");
//            String member_id = message.getStringAttribute("member_id","");
//            String description = message.getStringAttribute("description","");
//            String huanxin_username = message.getStringAttribute("huanxin_username","");
//            String nick_name = message.getStringAttribute("nick_name","");
//            String poster = message.getStringAttribute("poster","");
//            int replier = message.getIntAttribute("replier",0);
//            String review = message.getStringAttribute("review","");
//            NewMsgAboutMe newMsgAboutMe = new NewMsgAboutMe();
//            newMsgAboutMe.setId(id);
//            newMsgAboutMe.setCreated_time(created_time);
//            newMsgAboutMe.setDescription(description);
//            newMsgAboutMe.setHuanxin_username(huanxin_username);
//            newMsgAboutMe.setMember_id(member_id);
//            newMsgAboutMe.setNick_name(nick_name);
//            newMsgAboutMe.setPoster(poster);
//            newMsgAboutMe.setReplier(replier);
//            newMsgAboutMe.setReview(review);
//            if(creation_id!=0){
//                newMsgAboutMe.setMid(creation_id);
//                newMsgAboutMe.setType(2);//微创作
//            }else{
//                newMsgAboutMe.setMid(friend_id);
//                newMsgAboutMe.setType(1);//朋友圈
//            }
//            newMsgAboutMe.setIs_new(true);
//            newMsgAboutMe.setBenben_id(user.getBenbenId());
//            try {
//                dbUtil.saveOrUpdate(newMsgAboutMe);
//            } catch (DbException e) {
//                e.printStackTrace();
//            }
//            if(creation_id!=0) {
//                sendBroadcast(new Intent("hasCreationNews"));
//            }else{
//                sendBroadcast(new Intent("hasFriendNews"));
//            }
//
//            int friendAboutMe=0;
//            int creationAboutMe=0;
//            try {
//
//                List<NewMsgAboutMe> friendAboutMeLists =dbUtil.findAll(Selector.from(NewMsgAboutMe.class).where("type","=",1).and("is_new","=",true).and("benben_id","=",CrashApplication.getInstance().user.getBenbenId()));
//                if(friendAboutMeLists!=null){
//                    friendAboutMe = friendAboutMeLists.size();
//                }
//                List<NewMsgAboutMe> creationAboutMeLists =dbUtil.findAll(Selector.from(NewMsgAboutMe.class).where("type","=",2).and("is_new","=",true).and("benben_id","=",CrashApplication.getInstance().user.getBenbenId()));
//                if(creationAboutMeLists!=null){
//                    creationAboutMe = creationAboutMeLists.size();
//                }
//            } catch (DbException e) {
//                e.printStackTrace();
//            }
//
//            int allNumAboutMe =creationAboutMe+friendAboutMe;
//            if(allNumAboutMe<=0){
//                tab_four_num.setVisibility(View.GONE);
//            }else if(allNumAboutMe<=99){
//                tab_four_num.setText(allNumAboutMe + "");
//                tab_four_num.setVisibility(View.VISIBLE);
//            }else{
//                tab_four_num.setText("99+");
//                tab_four_num.setVisibility(View.VISIBLE);
//            }
//
//
//            ((PrivateFragment) fragments[3]).refresh();
//
//            // 获取扩展属性 此处省略
//			// message.getStringAttribute("");
//			// EMLog.d(TAG,
//			// String.format("透传消息：action:%s,message:%s", action,
//			// message.toString()));
//			String st9 = getResources().getString(
//					R.string.receive_the_passthrough);
//			// Toast.makeText(MainActivity.this, st9 + action,
//			// Toast.LENGTH_SHORT)
//			// .show();
//		}
//	};

//	/**
//	 * 离线消息BroadcastReceiver sdk 登录后，服务器会推送离线消息到client，这个receiver，是通知UI
//	 * 有哪些人发来了离线消息 UI 可以做相应的操作，比如下载用户信息
//	 */
//	private BroadcastReceiver offlineMessageReceiver = new BroadcastReceiver() {
//
//	@Override
//	public void onReceive(Context context, Intent intent) {
//        String[] users = intent.getStringArrayExtra("fromuser");
//        String[] groups = intent.getStringArrayExtra("fromgroup");
//        if (users != null) {
//            for (String user : users) {
//                System.out.println("收到user离线消息：" + user);
//            }
//        }
//        if (groups != null) {
//            for (String group : groups) {
//                System.out.println("收到group离线消息：" + group);
//            }
//        }
//        }
//	};

	private InviteMessgeDao inviteMessgeDao;
	private UserDao userDao;

    @Override
    public void onUpdateReturned(int i, UpdateResponse updateResponse) {
        if(updateResponse!=null && updateResponse.hasUpdate){
            CrashApplication.getInstance().updateFlag = true;
        }
    }

    /***
	 * 好友变化listener
	 * 
	 */
	private class MyContactListener implements EMContactListener {

		@Override
		public void onContactAdded(List<String> usernameList) {

		}

		@Override
		public void onContactDeleted(final List<String> usernameList) {
		}

		@Override
		public void onContactInvited(String username, final String reason) {
			// 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
			try {
				PublicMessage mPublicMessage = dbUtil.findFirst(Selector.from(
						PublicMessage.class).where("huanxin_username", "=",
						username).and("sid","!=",0).and("classType","=",PublicMessage.FRIEND));
				if (mPublicMessage == null) {
					InteNetUtils.getInstance(mContext).GetContacformNamet(
							new String[] { username },
							new RequestCallBack<String>() {
								@Override
								public void onFailure(HttpException arg0,
										String arg1) {

								}

								@Override
								public void onSuccess(ResponseInfo<String> arg0) {

									try {
										JSONObject jsonObject = new JSONObject(
												arg0.result);
										PublicMessage contant = new PublicMessage();

										contant.checkJson(jsonObject);

										JSONArray optJSONObject = jsonObject
												.optJSONArray("user");

										contant.parseJSON(optJSONObject
												.getJSONObject(0));
                                        contant.setReason(reason);
										contant.setCreatTime(TimeUtil.now());
										if (!mApplication.mPublicMessage
												.contains(contant)) {
											mApplication.mPublicMessage.add(0,
													contant);
										}

										try {
											dbUtil.saveOrUpdate(contant);
										} catch (DbException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

										notifyNewIviteMessage();

									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (NetRequestException e) {
										e.printStackTrace();
										e.getError().print(mContext);
									}

								}
							});

				} else {

					mPublicMessage.setStatus(PublicMessage.UNAGREE);
                    mPublicMessage.setReason(reason);
					mPublicMessage.setCreatTime(TimeUtil.now());
					mApplication.mPublicMessage.add(0, mPublicMessage);
					dbUtil.update(mPublicMessage);
					notifyNewIviteMessage();
				}

			} catch (DbException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		@Override
		public void onContactAgreed(String username) {
			// List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
			// for (InviteMessage inviteMessage : msgs) {
			// if (inviteMessage.getFrom().equals(username)) {
			// return;
			// }
			// }
			// // 自己封装的javabean
			// InviteMessage msg = new InviteMessage();
			// msg.setFrom(username);
			// msg.setTime(System.currentTimeMillis());
			// // Log.d(TAG, username + "同意了你的好友请求");
			// msg.setStatus(InviteMesageStatus.BEAGREED);
			// notifyNewIviteMessage(msg);

		}

		@Override
		public void onContactRefused(String username) {
//			// // // 保存增加的联系人
//			final Map<String, Contacts> localUsers = mApplication.mContactsMap;
//			// 添加好友时可能会回调added方法两次
//			if (!localUsers.containsKey(username)) {
//				localUsers.put(username, null);
//				((ContactsFragment) fragments[0]).refreshData();
//				sendBroadcast(new Intent("hasNews"));
//				// InteNetUtils.getInstance(mContext).getContactInfoFromHX(
//				// username, new RequestCallBack<String>() {
//				// @Override
//				// public void onFailure(HttpException arg0,
//				// String arg1) {
//				// }
//				//
//				// @Override
//				// public void onSuccess(ResponseInfo<String> arg0) {
//				// try {
//				// JSONObject jsonObject = new JSONObject(
//				// arg0.result);
//				//
//				// Contacts contacts = new Contacts();
//				// contacts.parseJSONSingle(jsonObject);
//				//
//				// contacts.setGroup_id(mApplication.contactsObject
//				// .getmContactsGroups()
//				// .get(mApplication.contactsObject
//				// .getmContactsGroups()
//				// .size() - 1).getId());
//				//
//				// dbUtil.saveOrUpdate(contacts);
//				// dbUtil.saveOrUpdateAll(contacts.getPhones());
//				// ((ContactsFragment) fragments[0])
//				// .refreshData();
//				// ;
//				// sendBroadcast(new Intent("hasNews"));
//				// } catch (JSONException e) {
//				// e.printStackTrace();
//				// } catch (NetRequestException e) {
//				// e.getError().print(mContext);
//				// e.printStackTrace();
//				// } catch (DbException e) {
//				// // TODO Auto-generated catch
//				// // block
//				// e.printStackTrace();
//				// }
//				//
//				// }
//				// });
//			}

		}

	}

	/**
	 * 保存提示新消息
	 *
	 */
	private void notifyNewIviteMessage() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				saveInviteMsg();
				// 提示有新消息
//				EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();
				// 刷新bottom bar消息未读数
				updateUnreadAddressLable();
				// 刷新好友页面ui
				if (curTouchTab == 1)
					huanXinFragment.refresh();
			}
		});

	}

	/**
	 * 保存邀请等msg
	 *
	 */
	private void saveInviteMsg() {
		// 未读数加1
		BenbenEMConversation bem = mApplication.getBme();
		if (bem != null) {
			try {
				List<Object> findAll = dbUtil.findAll(Selector.from(
						PublicMessage.class).where("isLook", "=",
						PublicMessage.UNLOOK));
				if (findAll != null) {
					bem.setPublicNum(findAll.size());
				}

                List<Object> findBuyAll = dbUtil.findAll(Selector.from(
                        BuyNews.class).where("status", "=",0).and("benben_id","=",user.getBenbenId()));
                if (findBuyAll != null) {
                    bem.setBuyNum(findBuyAll.size());
                }
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * set head
	 * 
	 * @param username
	 * @return
	 */
	User setUserHead(String username) {
		User user = new User();
		user.setUsername(username);
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
			headerName = user.getUsername();
		}
		if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance()
					.get(headerName.substring(0, 1)).get(0).target.substring(0,
					1).toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
		return user;
	}

	/**
	 * 连接监听listener
	 * 
	 */
	private class MyConnectionListener implements EMConnectionListener {

		@Override
		public void onConnected() {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (huanXinFragment.errorItem != null) {
						huanXinFragment.errorItem.setVisibility(View.GONE);
					}
				}

			});
		}

		@Override
		public void onDisconnected(final int error) {
			final String st1 = getResources().getString(
					R.string.Less_than_chat_server_connection);
			final String st2 = getResources().getString(
					R.string.the_current_network);
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (error == EMError.USER_REMOVED) {
						// 显示帐号已经被移除
						showAccountRemovedDialog();
					} else if (error == EMError.CONNECTION_CONFLICT) {
						// 显示帐号在其他设备登陆dialog
						showConflictDialog();
					} else {
						huanXinFragment.errorItem.setVisibility(View.VISIBLE);
						if (NetUtils.hasNetwork(MainActivity.this)) {
							// huanXinFragment.errorText.setText(st1);
							startActivity(new Intent(mContext,
									ActivityLogin.class));
							CrashApplication.getInstance().logout();
							ToastUtils.Errortoast(mContext, "用户登录失败,请重新登录");
						} else
							huanXinFragment.errorText.setText(st2);

					}
				}

			});
		}
	}

	/**
	 * MyGroupChangeListener
	 */
	private class MyGroupChangeListener implements GroupChangeListener {

		@Override
		public void onInvitationReceived(String groupId, String groupName,
				String inviter, String reason) {
			boolean hasGroup = false;

		}

		@Override
		public void onInvitationAccpted(String groupId, String inviter,
				String reason) {

		}

		@Override
		public void onInvitationDeclined(String groupId, String invitee,
				String reason) {

		}

		@Override
		public void onUserRemoved(String groupId, String groupName) {
			// 提示用户被T了，demo省略此步骤
			// 刷新ui
			runOnUiThread(new Runnable() {
				public void run() {
					try {
						updateUnreadLabel();
						if (curTouchTab == 1)
							huanXinFragment.refresh();
						// if (CommonUtils.getTopActivity(MainActivity.this)
						// .equals(GroupsActivity.class.getName())) {
						// GroupsActivity.instance.onResume();
						// }
					} catch (Exception e) {
						// EMLog.e(TAG, "refresh exception " + e.getMessage());
					}
				}
			});
		}

		@Override
		public void onGroupDestroy(String groupId, String groupName) {
			// 群被解散
			// 提示用户群被解散,demo省略
			// 刷新ui
			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					huanXinFragment.refresh();
					// if (CommonUtils.getTopActivity(MainActivity.this).equals(
					// GroupsActivity.class.getName())) {
					// GroupsActivity.instance.onResume();
					// }
				}
			});

		}

		@Override
		public void onApplicationReceived(String groupId, String groupName,
				String applyer, String reason) {
			try {
				PublicMessage mPublicMessage = dbUtil.findFirst(Selector.from(
						PublicMessage.class).where("huanxin_username_joiner",
						"=", applyer).and("huanxin_username","=",groupId));
				if (mPublicMessage == null) {
					TalkGroup talkGroup = mApplication.mTalkGroupMap
							.get(groupId);

					mPublicMessage = new PublicMessage();
					mPublicMessage.setClassType(PublicMessage.GROUP);
					mPublicMessage.setHuanxin_username(groupId);
					mPublicMessage.setStatus(PublicMessage.UNAGREE);
					mPublicMessage.setName(reason);
                    mPublicMessage.setReason(reason);
					mPublicMessage.setCreatTime(TimeUtil.now());
                    TalkGroup group = dbUtil.findFirst(Selector.from(TalkGroup.class).where("huanxin_groupid","=",groupId));
					mPublicMessage.setNick_name(group.getName());
					mPublicMessage.setPoster(talkGroup.getPoster());
					mPublicMessage.setHuanxin_username_joiner(applyer);
					if (!mApplication.mPublicMessage.contains(mPublicMessage)) {
						mApplication.mPublicMessage.add(0, mPublicMessage);
					}
					try {
						dbUtil.saveOrUpdate(mPublicMessage);
					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					sendBroadcast(new Intent("hasNews"));
					notifyNewIviteMessage();
				} else {
					mPublicMessage.setIsLook(PublicMessage.UNLOOK);
					mPublicMessage.setStatus(PublicMessage.UNAGREE);
					mPublicMessage.setCreatTime(TimeUtil.now());
                    mPublicMessage.setHuanxin_username(groupId);
                    TalkGroup group = dbUtil.findFirst(Selector.from(TalkGroup.class).where("huanxin_groupid","=",groupId));
                    mPublicMessage.setNick_name(group.getName());
					mApplication.mPublicMessage.add(0, mPublicMessage);
					dbUtil.update(mPublicMessage);
					notifyNewIviteMessage();
				}

			} catch (DbException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

		@Override
		public void onApplicationAccept(String groupId, String groupName,
				String accepter) {
			// String st4 = getResources().getString(
			// R.string.Agreed_to_your_group_chat_application);
			// // 加群申请被同意
			// EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
			// msg.setChatType(ChatType.GroupChat);
			// msg.setFrom(accepter);
			// msg.setTo(groupId);
			// msg.setMsgId(UUID.randomUUID().toString());
			// msg.addBody(new TextMessageBody(accepter + st4));
			// // 保存同意消息
			// EMChatManager.getInstance().saveMessage(msg);
			// // 提醒新消息
			// EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();
			//
			// runOnUiThread(new Runnable() {
			// public void run() {
			// updateUnreadLabel();
			// // 刷新ui
			// if (curTouchTab == 1)
			// huanXinFragment.refresh();
			// // if (CommonUtils.getTopActivity(MainActivity.this).equals(
			// // GroupsActivity.class.getName())) {
			// // GroupsActivity.instance.onResume();
			// // }
			// }
			// });
		}

		@Override
		public void onApplicationDeclined(String groupId, String groupName,
				String decliner, String reason) {
			// 加群申请被拒绝，demo未实现
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		isOpen = false;
		isRun = false;
		setUpdateInfo();
		if (!isConflict || !isCurrentAccountRemoved) {
			updateUnreadLabel();
			updateUnreadAddressLable();
			EMChatManager.getInstance().activityResumed();
		}
		MobclickAgent.onPageStart("main");
		MobclickAgent.onResume(mContext);
		sendBadgeNumber(0);
        HXSDKHelper.getInstance().getNotifier().reset();
	}

	// 必须使用，Activity启动页
	private final static String lancherActivityClassName = SplashActivity.class
			.getName();

	private void sendBadgeNumber(int number) {

        if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")){
            HintUtil.resetBadgeCount(mContext, HintUtil.Platform.mi);
        }else if(Build.MANUFACTURER.equalsIgnoreCase("samsung")){
            HintUtil.resetBadgeCount(mContext, HintUtil.Platform.samsung);
        }else if(Build.MANUFACTURER.equalsIgnoreCase("htc")){
            HintUtil.resetBadgeCount(mContext,  HintUtil.Platform.htc);
        }else if(Build.MANUFACTURER.equalsIgnoreCase("lg")){
            HintUtil.resetBadgeCount(mContext,  HintUtil.Platform.lg);
        }else if(Build.MANUFACTURER.equalsIgnoreCase("sony")){
            HintUtil.resetBadgeCount(mContext, HintUtil.Platform.sony);
        }

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
			// NotificationCompat.Builder builder = new
			// NotificationCompat.Builder(
			// this);
			// builder.setContentTitle("您有" + number + "未读消息");
			// builder.setTicker("您有" + number + "未读消息");
			// builder.setAutoCancel(true);
			// builder.setSmallIcon(R.drawable.ic_icon);
			// builder.setDefaults(Notification.DEFAULT_LIGHTS);
			// notification = builder.build();
			// Class miuiNotificationClass = Class
			// .forName("android.app.MiuiNotification");
			// Object miuiNotification = miuiNotificationClass.newInstance();
			// Field field = miuiNotification.getClass().getDeclaredField(
			// "messageCount");
			// field.setAccessible(true);
			// field.set(miuiNotification, number);// 设置信息数
			// field = notification.getClass().getField("extraNotification");
			// field.setAccessible(true);
			// field.set(notification, miuiNotification);
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

	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("main");
		MobclickAgent.onPause(mContext);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("isConflict", isConflict);
		outState.putBoolean(Constant.ACCOUNT_REMOVED, isCurrentAccountRemoved);
		super.onSaveInstanceState(outState);
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// moveTaskToBack(false);
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	private android.app.AlertDialog.Builder conflictBuilder;
	private android.app.AlertDialog.Builder accountRemovedBuilder;
	private boolean isConflictDialogShow;
	private boolean isAccountRemovedDialogShow;
	private View tab_num;

	/**
	 * 显示帐号在别处登录dialog
	 */
	private void showConflictDialog() {
		isConflictDialogShow = true;
		final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(mContext,
				R.style.MyDialog1);

		// hint.setContent("犇犇账号在其他手机登录");
		// hint.show();
		// hint.setOKListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// hint.dismiss();
		// }
		// });
		// hint.setOnDismissListener(new OnDismissListener() {
		//
		// @Override
		// public void onDismiss(DialogInterface dialog) {

		// ToastUtils.InfotoastLong(mContext, "奔犇账号在其他手机登录");
		mApplication.logout();
		mApplication.setExit(true);
		startActivity(new Intent(MainActivity.this, ActivityLogin.class));
		// }

		// });

	}

	/**
	 * 帐号被移除的dialog
	 */
	private void showAccountRemovedDialog() {
		isAccountRemovedDialogShow = true;
		// mApplication.logout();
		// String st5 =
		// getResources().getString(R.string.Remove_the_notification);

		ToastUtils.InfotoastLong(mContext, "奔犇账号被移除");
		mApplication.logout();
		startActivity(new Intent(MainActivity.this, ActivityLogin.class));

		// if (!MainActivity.this.isFinishing()) {
		// // clear up global variables
		// try {
		// if (accountRemovedBuilder == null)
		// accountRemovedBuilder = new android.app.AlertDialog.Builder(
		// MainActivity.this);
		// accountRemovedBuilder.setTitle(st5);
		// accountRemovedBuilder.setMessage(R.string.em_user_remove);
		// accountRemovedBuilder.setPositiveButton(R.string.ok,
		// new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog,
		// int which) {
		// dialog.dismiss();
		// accountRemovedBuilder = null;
		// AnimFinsh();
		// startActivity(new Intent(MainActivity.this,
		// ActivityLogin.class));
		// }
		// });
		// accountRemovedBuilder.setCancelable(false);
		// accountRemovedBuilder.create().show();
		// isCurrentAccountRemoved = true;
		// } catch (Exception e) {
		// // EMLog.e(TAG,
		// // "---------color userRemovedBuilder error"
		// // + e.getMessage());
		// }
		// }

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (getIntent().getBooleanExtra("conflict", false)
				&& !isConflictDialogShow) {
			showConflictDialog();
		} else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false)
				&& !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}
	}

	/**
	 * 检查当前用户是否被删除
	 */
	public boolean getCurrentAccountRemoved() {
		return isCurrentAccountRemoved;
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {

		private NewsList all;

		@Override
		public void onReceive(Context context, Intent intent) {
			setUpdateInfo();
		}
	}

	// private class BroadcastReceiver extends BroadcastReceiver {
	//
	// private NewsList all;
	//
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// setUpdateInfo();
	// }
	// }

	// 通讯录刷新
	class refreshBrocast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			((ContactsFragment) fragments[0]).initlocakData();
			((ChatAllHistoryFragment) fragments[1]).refresh();
		}

	}

	public void refreshPlayPhone() {
		((PlayPhoneFragment) fragments[2]).onRefresh();
	}

	public void setUpdateInfo() {
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					List<News> findAll = dbUtil.findAll(Selector.from(
							News.class).where("status", "=", 0));

					if (findAll != null && findAll.size() > 0) {
						handler.sendEmptyMessage(UPDATA);
					} else {
						handler.sendEmptyMessage(NOUPDATA);
					}

				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

	class myFragmentBroadcast extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			((MyFragment) fragments[4]).refrashUser();
		}

	}

	private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
		String SYSTEM_REASON = "reason";
		String SYSTEM_HOME_KEY = "homekey";
		String SYSTEM_HOME_KEY_LONG = "recentapps";

		@Override
		public void onReceive(Context context, Intent intent) {
			// String action = intent.getAction();
			// if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
			// String reason = intent.getStringExtra(SYSTEM_REASON);
			// if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
			// // 表示按了home键,程序到了后台
			// // 点击home贱,需要拉取是否有推送
			// isRun = true;
			// if (!isOpen)
			// new Thread(new MyRunable()).start();
			// }
			// }
		}

	};

	private BroadcastReceiver mLockScrennEventReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// // 监听用户锁屏幕
			// isRun = true;
			// if (!isOpen) {
			// ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
			// if (!cn.getPackageName().equals(getPackageName()))
			// new Thread(new MyRunable()).start();
			// }
		}
	};

	boolean isRun = false;
	boolean isOpen = false;
	long time = 0;
	private ActivityManager am;

	class MyRunable implements Runnable {

		@Override
		public void run() {
			isOpen = true;
			while (isRun) {
				time++;
				if (time > 20 * 60) {
					mContext.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							isRun = false;
							isOpen = false;
							startAnimActivity(ActivityLogin.class);
							mApplication.logout();
						}
					});
				}
				SystemClock.sleep(1000);
			}
		}

	}

}
