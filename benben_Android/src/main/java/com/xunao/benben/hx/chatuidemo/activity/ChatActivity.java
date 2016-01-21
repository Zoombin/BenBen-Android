/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xunao.benben.hx.chatuidemo.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.GroupReomveListener;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.NormalFileMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VideoMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;
import com.easemob.util.VoiceRecorder;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseBean;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.ContactsTemp;
import com.xunao.benben.bean.MyEMConversation;
import com.xunao.benben.bean.NumberTrain;
import com.xunao.benben.bean.TalkGroup;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.hx.applib.controller.HXSDKHelper;
import com.xunao.benben.hx.chatuidemo.adapter.ExpressionAdapter;
import com.xunao.benben.hx.chatuidemo.adapter.ExpressionPagerAdapter;
import com.xunao.benben.hx.chatuidemo.adapter.MessageAdapter;
import com.xunao.benben.hx.chatuidemo.adapter.VoicePlayClickListener;
import com.xunao.benben.hx.chatuidemo.utils.CommonUtils;
import com.xunao.benben.hx.chatuidemo.utils.ImageUtils;
import com.xunao.benben.hx.chatuidemo.utils.SmileUtils;
import com.xunao.benben.hx.chatuidemo.widget.ExpandGridView;
import com.xunao.benben.hx.chatuidemo.widget.PasteEditText;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityNumberTrain;
import com.xunao.benben.ui.item.ActivityContactsInfo;
import com.xunao.benben.ui.item.ImageFile;
import com.xunao.benben.ui.item.TallGroup.ActivityGroupNoticeDetails;
import com.xunao.benben.ui.item.TallGroup.ActivityTalkGroupInfo;
import com.xunao.benben.ui.shareselect.ActivityShareSelectFriend;
import com.xunao.benben.ui.shareselect.ActivityShareSelectTalkGroup;
import com.xunao.benben.utils.Bimp;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.PublicWay;
import com.xunao.benben.utils.Res;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.MyTextView;

/**
 * 聊天页面
 * 
 */
public class ChatActivity extends BaseActivity implements OnClickListener, EMEventListener {

	// 头部
	private ImageView com_title_bar_left_bt;
	private MyTextView com_title_bar_left_tv;
	private ImageView com_title_bar_right_bt;
	private MyTextView com_title_bar_right_tv;
	private MyTextView com_title_bar_content;

	private static final int REQUEST_CODE_EMPTY_HISTORY = 2;
	public static final int REQUEST_CODE_CONTEXT_MENU = 3;
	private static final int REQUEST_CODE_MAP = 4;
	public static final int REQUEST_CODE_TEXT = 5;
	public static final int REQUEST_CODE_VOICE = 6;
	public static final int REQUEST_CODE_PICTURE = 7;
	public static final int REQUEST_CODE_LOCATION = 8;
	public static final int REQUEST_CODE_NET_DISK = 9;
	public static final int REQUEST_CODE_FILE = 10;
	public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
	public static final int REQUEST_CODE_PICK_VIDEO = 12;
	public static final int REQUEST_CODE_DOWNLOAD_VIDEO = 13;
	public static final int REQUEST_CODE_VIDEO = 14;
	public static final int REQUEST_CODE_DOWNLOAD_VOICE = 15;
	public static final int REQUEST_CODE_SELECT_USER_CARD = 16;
	public static final int REQUEST_CODE_SEND_USER_CARD = 17;
	public static final int REQUEST_CODE_CAMERA = 18;
	public static final int REQUEST_CODE_LOCAL = 19;
	public static final int REQUEST_CODE_CLICK_DESTORY_IMG = 20;
	public static final int REQUEST_CODE_GROUP_DETAIL = 21;
	public static final int REQUEST_CODE_SELECT_VIDEO = 23;
	public static final int REQUEST_CODE_SELECT_FILE = 24;
	public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;
    public static final int REQUEST_CODE_SELECT_ZTC = 26;

	public static final int RESULT_CODE_COPY = 1;
	public static final int RESULT_CODE_DELETE = 2;
	public static final int RESULT_CODE_FORWARD = 3;
	public static final int RESULT_CODE_OPEN = 4;
	public static final int RESULT_CODE_DWONLOAD = 5;
	public static final int RESULT_CODE_TO_CLOUD = 6;
	public static final int RESULT_CODE_EXIT_GROUP = 7;

	public static final int CHATTYPE_SINGLE = 1;
	public static final int CHATTYPE_GROUP = 2;
    public static boolean isMySend=false;

	public static final String COPY_IMAGE = "EASEMOBIMG";
	private View recordingContainer;
	private ImageView micImage;
	private TextView recordingHint;
	private ListView listView;
	private PasteEditText mEditTextContent;
	private View buttonSetModeKeyboard;
	private View buttonSetModeVoice;
	private View buttonSend;
	private View buttonPressToSpeak;
	// private ViewPager expressionViewpager;
	private LinearLayout emojiIconContainer;
	private LinearLayout btnContainer;
	private ImageView locationImgview;
	private View more;
	private int position;
	private ClipboardManager clipboard;
	private ViewPager expressionViewpager;
	private InputMethodManager manager;
	private List<String> reslist;
	private Drawable[] micImages;
	private int chatType;
	private EMConversation conversation;
//	private NewMessageBroadcastReceiver receiver;
	public static ChatActivity activityInstance = null;
	// 给谁发送消息
	private String toChatUsername;
	private VoiceRecorder voiceRecorder;
	private MessageAdapter adapter;
	private File cameraFile;
	static int resendPos;

	private GroupListener groupListener;

	private ImageView iv_emoticons_normal;
	private ImageView iv_emoticons_checked;
	private RelativeLayout edittext_layout;
	private ProgressBar loadmorePB;
	private boolean isloading;
	private final int pagesize = 20;
	private boolean haveMoreData = true;
	private Button btnMore;
	public String playMsgId;
	private refreshGroupBroadcast broadcast;
    private TextView tv_record_time;
    private LinearLayout bar_bottom;
    public static Bitmap bimap;

	private Handler micImageHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			// 切换msg切换图片
			micImage.setImageDrawable(micImages[msg.what]);
		}
	};
	// private EMGroup group;
	private String groupName;

    public static SensorManager mSensorManager=null;
    public static Sensor mSensor=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		initdefaultImage(R.drawable.default_face);
		broadcast = new refreshGroupBroadcast();
		registerReceiver(broadcast,
				new IntentFilter(AndroidConfig.refreshGroup));
		initView();
		setUpView();
		mFinshBroadCast = new finshBroadCast();
		registerReceiver(mFinshBroadCast, new IntentFilter(AndroidConfig.Finsh));
        Res.init(this);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
//        mScreenWidth = metric.widthPixels;
//        mScreenHeight = metric.heightPixels;
        CrashApplication.getInstance().addActivity(this);
        bimap = BitmapFactory.decodeResource(getResources(),
                R.drawable.icon_addpic_unfocused);
        PublicWay.activityList.add(this);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
	}

	/**
	 * initView
	 */
	protected void initView() {

        bar_bottom = (LinearLayout) findViewById(R.id.bar_bottom);
		// 头部
		com_title_bar_left_bt = (ImageView) findViewById(R.id.com_title_bar_left_bt);
		com_title_bar_left_tv = (MyTextView) findViewById(R.id.com_title_bar_left_tv);
		com_title_bar_right_bt = (ImageView) findViewById(R.id.com_title_bar_right_bt);
		com_title_bar_right_tv = (MyTextView) findViewById(R.id.com_title_bar_right_tv);
		com_title_bar_content = (MyTextView) findViewById(R.id.com_title_bar_content);
        com_title_bar_content.setMaxWidth(PixelUtil.dp2px(150));

		//处理公告
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) com_title_bar_right_tv.getLayoutParams();
		layoutParams.setMargins(0, 0, PixelUtil.dp2px(40), 0);
		com_title_bar_right_tv.setLayoutParams(layoutParams);
		com_title_bar_right_tv.setText("公告");

		recordingContainer = findViewById(R.id.recording_container);
        tv_record_time = (TextView)findViewById(R.id.tv_record_time);
		micImage = (ImageView) findViewById(R.id.mic_image);
		recordingHint = (TextView) findViewById(R.id.recording_hint);
		listView = (ListView) findViewById(R.id.list);
		mEditTextContent = (PasteEditText) findViewById(R.id.et_sendmessage);
		buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
		edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
		buttonSetModeVoice = findViewById(R.id.btn_set_mode_voice);
		buttonSend = findViewById(R.id.btn_send);
		buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
		expressionViewpager = (ViewPager) findViewById(R.id.vPager);
		emojiIconContainer = (LinearLayout) findViewById(R.id.ll_face_container);
		btnContainer = (LinearLayout) findViewById(R.id.ll_btn_container);
		locationImgview = (ImageView) findViewById(R.id.btn_location);
		iv_emoticons_normal = (ImageView) findViewById(R.id.iv_emoticons_normal);
		iv_emoticons_checked = (ImageView) findViewById(R.id.iv_emoticons_checked);
		loadmorePB = (ProgressBar) findViewById(R.id.pb_load_more);
		btnMore = (Button) findViewById(R.id.btn_more);
		iv_emoticons_normal.setVisibility(View.VISIBLE);
		iv_emoticons_checked.setVisibility(View.INVISIBLE);
		more = findViewById(R.id.more);
		// edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);

		// 动画资源文件,用于录制语音时
		micImages = new Drawable[] {
				getResources().getDrawable(R.drawable.record_animate_01),
				getResources().getDrawable(R.drawable.record_animate_02),
				getResources().getDrawable(R.drawable.record_animate_03),
				getResources().getDrawable(R.drawable.record_animate_04),
				getResources().getDrawable(R.drawable.record_animate_05),
				getResources().getDrawable(R.drawable.record_animate_06),
				getResources().getDrawable(R.drawable.record_animate_07),
				getResources().getDrawable(R.drawable.record_animate_08),
				getResources().getDrawable(R.drawable.record_animate_09),
				getResources().getDrawable(R.drawable.record_animate_10),
				getResources().getDrawable(R.drawable.record_animate_11),
				getResources().getDrawable(R.drawable.record_animate_12),
				getResources().getDrawable(R.drawable.record_animate_13),
				getResources().getDrawable(R.drawable.record_animate_14), };

		// 表情list
		reslist = getExpressionRes(35);
		// 初始化表情viewpager
		List<View> views = new ArrayList<View>();
		View gv1 = getGridChildView(1);
		View gv2 = getGridChildView(2);
		views.add(gv1);
		views.add(gv2);
		expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));
		edittext_layout.requestFocus();
		voiceRecorder = new VoiceRecorder(micImageHandler);
		buttonPressToSpeak.setOnTouchListener(new PressToSpeakListen());
		mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// if (hasFocus) {
				// edittext_layout
				// .setBackgroundResource(R.drawable.input_bar_bg_active);
				// } else {
				// edittext_layout
				// .setBackgroundResource(R.drawable.input_bar_bg_normal);
				// }

			}
		});
		mEditTextContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// edittext_layout
				// .setBackgroundResource(R.drawable.input_bar_bg_active);
				more.setVisibility(View.GONE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.INVISIBLE);
				emojiIconContainer.setVisibility(View.GONE);
				btnContainer.setVisibility(View.GONE);
			}
		});
		// 监听文字框
		mEditTextContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!TextUtils.isEmpty(s)) {
					btnMore.setVisibility(View.GONE);
					buttonSend.setVisibility(View.VISIBLE);
				} else {
					btnMore.setVisibility(View.VISIBLE);
					buttonSend.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

	}

	private void setUpView() {

		// 头部
		com_title_bar_left_bt.setOnClickListener(this);
		com_title_bar_left_tv.setOnClickListener(this);
		com_title_bar_right_bt.setOnClickListener(this);
		com_title_bar_right_tv.setOnClickListener(this);

		com_title_bar_left_bt.setImageResource(R.drawable.icon_com_title_left);
		com_title_bar_right_bt
				.setImageResource(R.drawable.icon_com_title_more2);

		activityInstance = this;
		iv_emoticons_normal.setOnClickListener(this);
		iv_emoticons_checked.setOnClickListener(this);
		// position = getIntent().getIntExtra("position", -1);
		clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
				.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "xunao");
		// 判断单聊还是群聊 默认的是单聊
		chatType = getIntent().getIntExtra("chatType", CHATTYPE_SINGLE);

		if (chatType == CHATTYPE_SINGLE) { // 单聊

			com_title_bar_right_bt.setVisibility(View.VISIBLE);
			com_title_bar_right_tv.setVisibility(View.INVISIBLE);

			toChatUsername = getIntent().getStringExtra("userId");
			// ((TextView) findViewById(R.id.name)).setText(toChatUsername);
			Contacts contacts = (Contacts) CrashApplication.getInstance().mContactsMap
					.get(toChatUsername);

			initdefaultImage(R.drawable.default_face);
			if (contacts != null) {
				com_title_bar_content.setText(contacts.getName());
			} else {
				try {
					ContactsTemp findFirst = CrashApplication
							.getInstance()
							.getDb()
							.findFirst(
									Selector.from(ContactsTemp.class).where(
											"huanxin_username", "=",
											toChatUsername));
					if (findFirst != null) {
						com_title_bar_content.setText(findFirst.getName());
					}
					InteNetUtils.getInstance(CrashApplication.getInstance())
							.getContactInfoFromHXg(toChatUsername,
									new RequestCallBack<String>() {
										@Override
										public void onFailure(
												HttpException arg0, String arg1) {
										}

										@Override
										public void onSuccess(
												ResponseInfo<String> arg0) {
											try {
												JSONObject jsonObject = new JSONObject(
														arg0.result);
												ContactsTemp contacts = new ContactsTemp();
												contacts.parseJSONSingle(jsonObject);
												com_title_bar_content
														.setText(contacts
																.getName());
                                                if(contacts.getName()!=null && !contacts.getName().equals("")) {
                                                    bar_bottom.setVisibility(View.VISIBLE);
                                                    com_title_bar_right_bt.setVisibility(View.VISIBLE);
                                                }

												try {
													CrashApplication
															.getInstance()
															.getDb()
															.delete(ContactsTemp.class,
																	WhereBuilder
																			.b("huanxin_username",
																					"=",
																					toChatUsername));
													CrashApplication
															.getInstance()
															.getDb()
															.saveOrUpdate(
																	contacts);
													adapter.notifyDataSetChanged();
												} catch (DbException e) {
													// TODO Auto-generated
													// catch block
													e.printStackTrace();
												}
											} catch (JSONException e) {
												e.printStackTrace();
											} catch (NetRequestException e) {
												e.printStackTrace();
											}

										}
									});

				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			conversation = EMChatManager.getInstance().getConversation(
					toChatUsername, false);

            if(String.valueOf(com_title_bar_content.getText()).equals("")){
                com_title_bar_content.setText(toChatUsername);
                bar_bottom.setVisibility(View.GONE);
                com_title_bar_right_bt.setVisibility(View.GONE);
            }
		} else {
			com_title_bar_right_bt.setVisibility(View.VISIBLE);
			com_title_bar_right_tv.setVisibility(View.VISIBLE);
			mTalkGroup = (TalkGroup) getIntent().getSerializableExtra("tG");


			// 群聊
			findViewById(R.id.container_to_group).setVisibility(View.VISIBLE);
			findViewById(R.id.container_remove).setVisibility(View.GONE);
			findViewById(R.id.container_voice_call).setVisibility(View.GONE);
			findViewById(R.id.container_video_call).setVisibility(View.GONE);
			if (mTalkGroup != null) {
				toChatUsername = mTalkGroup.getHuanxin_groupid();
			} else {
				toChatUsername = getIntent().getStringExtra("groupId");
			}
			if (!TextUtils.isEmpty(toChatUsername)) {
				group = EMGroupManager.getInstance().getGroup(toChatUsername);
				// TalkGroup talkGroup = (TalkGroup)
				// CrashApplication.getInstance().getHuanXinMap().get(toChatUsername);
				// groupName = talkGroup.getName();
				if (mTalkGroup != null) {
					com_title_bar_content.setText(mTalkGroup.getName());
				} else {
					if (group == null) {
						refreshGroup();
					} else {
						com_title_bar_content.setText(group.getGroupName());
//								+ "(" + group.getMembers().size() + ")");
					}
				}
				// ((TextView)
				// findViewById(R.id.name)).setText(group.getGroupName());

				conversation = EMChatManager.getInstance().getConversation(
						toChatUsername, true);
			}

		}
		// 把此会话的未读数置为0
		conversation.resetUnreadMsgCount();
		adapter = new MessageAdapter(this, toChatUsername, chatType,
				cubeImageLoader, user);
		// 显示消息
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new ListScrollListener());
		int count = listView.getCount();
		if (count > 0) {
			listView.setSelection(count - 1);
		}

		listView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideKeyboard();
				more.setVisibility(View.GONE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.INVISIBLE);
				emojiIconContainer.setVisibility(View.GONE);
				btnContainer.setVisibility(View.GONE);
				return false;
			}
		});
//		// 注册接收消息广播
//		receiver = new NewMessageBroadcastReceiver();
//		IntentFilter intentFilter = new IntentFilter(EMChatManager
//				.getInstance().getNewMessageBroadcastAction());
//		// 设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
//		intentFilter.setPriority(5);
//		registerReceiver(receiver, intentFilter);

        EMChatManager.getInstance().registerEventListener(this);

//		// 注册一个ack回执消息的BroadcastReceiver
//		IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager
//				.getInstance().getAckMessageBroadcastAction());
//		ackMessageIntentFilter.setPriority(5);
//		registerReceiver(ackMessageReceiver, ackMessageIntentFilter);
//
//		// 注册一个消息送达的BroadcastReceiver
//		IntentFilter deliveryAckMessageIntentFilter = new IntentFilter(
//				EMChatManager.getInstance()
//						.getDeliveryAckMessageBroadcastAction());
//		deliveryAckMessageIntentFilter.setPriority(5);
//		registerReceiver(deliveryAckMessageReceiver,
//				deliveryAckMessageIntentFilter);

		// 监听当前会话的群聊解散被T事件
		groupListener = new GroupListener();
		EMGroupManager.getInstance().addGroupChangeListener(groupListener);

		// show forward message if the message is not null
		String forward_msg_id = getIntent().getStringExtra("forward_msg_id");
		if (forward_msg_id != null) {
			// 显示发送要转发的消息
			forwardMessage(forward_msg_id);
		}

	}

    @Override
    public void onEvent(EMNotifierEvent event) {
        EMMessage message = (EMMessage) event.getData();
        String username = message.getFrom();
        String msgid = message.getMsgId();
        switch (event.getEvent()){
            case EventNewMessage: // 普通消息

                // 如果是群聊消息，获取到group id
                if (message.getChatType() == ChatType.GroupChat) {
                    username = message.getTo();
                    // message.setFrom(mTalkGroup.getName());
                }
                if (!username.equals(toChatUsername)) {
                    // 消息不是发给当前会话，return
                    HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
                    notifyNewMessage(message);
                    return;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        conversation = EMChatManager.getInstance().getConversation(
                                toChatUsername);
                        // 通知adapter有新消息，更新ui
                        adapter.refresh();
                    }
                });

                break;

            case EventReadAck:

                EMConversation conversation = EMChatManager.getInstance()
                        .getConversation(username);
                if (conversation != null) {
                    // 把message设为已读
                    EMMessage msg = conversation.getMessage(msgid);
                    if (msg != null) {
                        msg.isAcked = true;
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

                break;
            case EventDeliveryAck:
                EMConversation conversation1 = EMChatManager.getInstance()
                        .getConversation(username);
                if (conversation1 != null) {
                    // 把message设为已读
                    EMMessage msg = conversation1.getMessage(msgid);
                    if (msg != null) {
                        msg.isDelivered = true;
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
                break;

        }
    }

    class refreshGroupBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			refreshGroup();
		}

	}

	private void refreshGroup() {
		new Thread() {
			public void run() {
				try {
					group = EMGroupManager.getInstance().getGroupFromServer(
							toChatUsername);

					ChatActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							com_title_bar_content.setText(group.getGroupName());
//									+ "(" + group.getMembers().size() + ")");
						}
					});
					if (group != null) {
						EMGroupManager.getInstance().createOrUpdateLocalGroup(
								group);
					}

				} catch (EaseMobException e) {
					e.printStackTrace();
					ChatActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							com_title_bar_content.setText("未知");
						}
					});
				}

			};
		}.start();
	}

	/**
	 * onActivityResult
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_LOCAL){
            isMySend = true;
            if (Bimp.tempSelectBitmap.size() > 0) {
                for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
                    sendPicture(Bimp.tempSelectBitmap.get(i).getImagePath());
                }
                Bimp.tempSelectBitmap.clear();
            }
            return;
        }

        if (requestCode == REQUEST_CODE_SELECT_ZTC) {
            if(resultCode==RESULT_OK){
                NumberTrain numberTrain = (NumberTrain) data.getSerializableExtra("numberTrain");
                sendNumberTrain(numberTrain);

            }
            return;
        }

		if (resultCode == RESULT_CODE_EXIT_GROUP) {
			setResult(RESULT_OK);
			finish();
			return;
		}
		if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
			switch (resultCode) {
			case RESULT_CODE_COPY: // 复制消息
				EMMessage copyMsg = ((EMMessage) adapter.getItem(data
						.getIntExtra("position", -1)));
				// clipboard.setText(SmileUtils.getSmiledText(ChatActivity.this,
				// ((TextMessageBody) copyMsg.getBody()).getMessage()));
				clipboard.setText(((TextMessageBody) copyMsg.getBody())
						.getMessage());
				break;
			case RESULT_CODE_DELETE: // 删除消息
				EMMessage deleteMsg = (EMMessage) adapter.getItem(data
						.getIntExtra("position", -1));
				conversation.removeMessage(deleteMsg.getMsgId());
				adapter.Deleterefresh();
				listView.setSelection(data.getIntExtra("position",
						adapter.getCount()) - 1);
				break;

				case RESULT_CODE_FORWARD: // 转发消息
					// case RESULT_CODE_FORWARD: // 转发消息
					// EMMessage forwardMsg = (EMMessage) adapter.getItem(data
					// .getIntExtra("position", 0));
					// Intent intent = new Intent(this, ForwardMessageActivity.class);
					// intent.putExtra("forward_msg_id", forwardMsg.getMsgId());
					// startActivity(intent);
					//
					// break;


					EMMessage forwardMsg = (EMMessage) adapter.getItem(data
					.getIntExtra("position", 0));
					setTheme(R.style.ActionSheetStyleIOS7);
					showShareActionSheet(forwardMsg);
					break;
			default:
				break;
			}
		}
		if (resultCode == RESULT_OK) { // 清空消息
			if (requestCode == REQUEST_CODE_EMPTY_HISTORY) {
				// 清空会话
				EMChatManager.getInstance().clearConversation(toChatUsername);
				adapter.refresh();
			} else if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
				if (cameraFile != null && cameraFile.exists()) {
                    sendPicture(cameraFile.getAbsolutePath());
                    isMySend=true;
                }
			} else if (requestCode == REQUEST_CODE_SELECT_VIDEO) { // 发送本地选择的视频

				int duration = data.getIntExtra("dur", 0);
				String videoPath = data.getStringExtra("path");
				File file = new File(PathUtil.getInstance().getImagePath(),
						"thvideo" + System.currentTimeMillis());
				Bitmap bitmap = null;
				FileOutputStream fos = null;
				try {
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
					if (bitmap == null) {
						EMLog.d("chatactivity",
								"problem load video thumbnail bitmap,use default icon");
						bitmap = BitmapFactory.decodeResource(getResources(),
								R.drawable.app_panel_video_icon);
					}
					fos = new FileOutputStream(file);

					bitmap.compress(CompressFormat.JPEG, 100, fos);

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (fos != null) {
						try {
							fos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						fos = null;
					}
					if (bitmap != null) {
						bitmap.recycle();
						bitmap = null;
					}

				}
				sendVideo(videoPath, file.getAbsolutePath(), duration / 1000);

//			} else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
//                if(resultCode==RESULT_OK){
//                    ArrayList<String> paths = data.getStringArrayListExtra("code");
//                    for(int i=0;i<paths.size();i++) {
//                        sendPicture(paths.get(i));
//                        isMySend=true;
//                    }
//                }

//				if (data != null) {
//					Uri selectedImage = data.getData();
//					if (selectedImage != null) {
//						sendPicByUri(selectedImage);
//					}
//				}
			} else if (requestCode == REQUEST_CODE_SELECT_FILE) { // 发送选择的文件
				if (data != null) {
					Uri uri = data.getData();
					if (uri != null) {
						sendFile(uri);
					}
				}

			} else if (requestCode == REQUEST_CODE_MAP) { // 地图
				double latitude = data.getDoubleExtra("latitude", 0);
				double longitude = data.getDoubleExtra("longitude", 0);
				String locationAddress = data.getStringExtra("address");
				if (locationAddress != null && !locationAddress.equals("")) {
					more(more);
					sendLocationMsg(latitude, longitude, "", locationAddress);
				} else {
					String st = getResources().getString(
							R.string.unable_to_get_loaction);
					Toast.makeText(this, st, Toast.LENGTH_SHORT).show();
				}
				// 重发消息
			} else if (requestCode == REQUEST_CODE_TEXT
					|| requestCode == REQUEST_CODE_VOICE
					|| requestCode == REQUEST_CODE_PICTURE
					|| requestCode == REQUEST_CODE_LOCATION
					|| requestCode == REQUEST_CODE_VIDEO
					|| requestCode == REQUEST_CODE_FILE) {
				resendMessage();
			} else if (requestCode == REQUEST_CODE_COPY_AND_PASTE) {
				// 粘贴
				if (!TextUtils.isEmpty(clipboard.getText())) {
					String pasteText = clipboard.getText().toString();
					if (pasteText.startsWith(COPY_IMAGE)) {
						// 把图片前缀去掉，还原成正常的path
                        isMySend = true;
						sendPicture(pasteText.replace(COPY_IMAGE, ""));
					}

				}
			} else if (requestCode == REQUEST_CODE_ADD_TO_BLACKLIST) { // 移入黑名单
				EMMessage deleteMsg = (EMMessage) adapter.getItem(data
						.getIntExtra("position", -1));
				addUserToBlacklist(deleteMsg.getFrom());
			} else if (conversation.getMsgCount() > 0) {
				adapter.refresh();
				setResult(RESULT_OK);
			} else if (requestCode == REQUEST_CODE_GROUP_DETAIL) {
				adapter.refresh();
			}
		}

		if (resultCode == AndroidConfig.writeFriendRefreshResultCode) {
            if (chatType == CHATTYPE_GROUP) {
                mTalkGroup = (TalkGroup) data.getSerializableExtra("TG");
                com_title_bar_content.setText(mTalkGroup.getName() + "("
                        + mTalkGroup.getNumber() + ")");
            }
			setResult(AndroidConfig.writeFriendRefreshResultCode);
		}
		if (resultCode == AndroidConfig.exitActivity) {
			AnimFinsh();
			setResult(AndroidConfig.writeFriendRefreshResultCode);
		}

	}

	/**
	 * 消息图标点击事件
	 * 
	 * @param view
	 */
	@Override
	public void onClick(View view) {
		String st1 = getResources().getString(R.string.not_connect_to_server);
		int id = view.getId();
		if (id == R.id.btn_send) {// 点击发送按钮(发文字和表情)
			String s = mEditTextContent.getText().toString();
			sendText(s);
		} else if (id == R.id.btn_take_picture) {
			selectPicFromCamera();// 点击照相图标
		} else if (id == R.id.btn_picture) {
//			selectPicFromLocal(); // 点击图片图标
//            Intent intent = new Intent(ChatActivity.this,
//                    PhotoWallActivity.class);
//            startActivityForResult(intent, REQUEST_CODE_LOCAL);

            Intent intent = new Intent(
                    ChatActivity.this,
                    ImageFile.class);
            intent.putExtra("isClear",true);
            startActivityForResult(intent, REQUEST_CODE_LOCAL);
            overridePendingTransition(
                    R.anim.activity_translate_in,
                    R.anim.activity_translate_out);

		} else if (id == R.id.btn_location) { // 位置
			startActivityForResult(new Intent(this, BaiduMapActivity.class),
					REQUEST_CODE_MAP);
		} else if (id == R.id.iv_emoticons_normal) { // 点击显示表情框
			more.setVisibility(View.VISIBLE);
			iv_emoticons_normal.setVisibility(View.INVISIBLE);
			iv_emoticons_checked.setVisibility(View.VISIBLE);
			btnContainer.setVisibility(View.GONE);
			emojiIconContainer.setVisibility(View.VISIBLE);
			hideKeyboard();
		} else if (id == R.id.iv_emoticons_checked) { // 点击隐藏表情框
			iv_emoticons_normal.setVisibility(View.VISIBLE);
			iv_emoticons_checked.setVisibility(View.INVISIBLE);
			btnContainer.setVisibility(View.VISIBLE);
			emojiIconContainer.setVisibility(View.GONE);
			more.setVisibility(View.GONE);

		} else if (id == R.id.btn_video) {
			// 点击摄像图标
			Intent intent = new Intent(ChatActivity.this,
					ImageGridActivity.class);
			startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
		} else if (id == R.id.btn_file) { // 点击文件图标
			selectFileFromLocal();
		}
        else if (id == R.id.btn_ztc) { // 点击文件图标
            Intent intent = new Intent();
            intent.setClass(ChatActivity.this,ActivityNumberTrain.class);
            intent.putExtra("from","chat");
            startActivityForResult(intent,REQUEST_CODE_SELECT_ZTC);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

        }
		// else if (id == R.id.btn_voice_call) { // 点击语音电话图标
		// if (!EMChatManager.getInstance().isConnected())
		// Toast.makeText(this, st1, 0).show();
		// else
		// startActivity(new Intent(ChatActivity.this,
		// VoiceCallActivity.class).putExtra("username", toChatUsername)
		// .putExtra("isComingCall", false));
		// }else if (id == R.id.btn_video_call) { //视频通话
		// if (!EMChatManager.getInstance().isConnected())
		// Toast.makeText(this, st1, 0).show();
		// else
		// startActivity(new Intent(this,
		// VideoCallActivity.class).putExtra("username", toChatUsername)
		// .putExtra("isComingCall", false));
		// }
		else if (id == R.id.com_title_bar_left_bt
				|| id == R.id.com_title_bar_left_tv) {
			back(view);
		} else if (id == R.id.com_title_bar_right_bt) {
			// 如果是群组聊天进入群组详细
			// emptyHistory(view);

			if (chatType == CHATTYPE_SINGLE) { // 单聊
                Contacts mContacts = (Contacts) getIntent().getSerializableExtra("contacts");
                String name = toChatUsername;
                Intent intent = new Intent(this, ActivityContactsInfo.class);
//                intent.putExtra("contacts", mContacts);
                intent.putExtra("username", name);
                this.startActivityForResult(intent, AndroidConfig.ContactsFragmentRequestCode);
                this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);


//				String name = toChatUsername;

				// if (contacts != null) {
				// Intent intent = new Intent(context,
				// ActivityContactsInfo.class);
				// intent.putExtra("contacts", contacts);
				// ((BaseActivity) context).startActivityForResult(intent,
				// AndroidConfig.ContactsFragmentRequestCode);
				// ((BaseActivity) context).overridePendingTransition(
				// R.anim.in_from_right, R.anim.out_to_left);
				// } else {
//				Intent intent = new Intent(ChatActivity.this,
//						ActivityCaptureContactsInfo.class);
//				intent.putExtra("username", name);
//				(ChatActivity.this).startActivityForResult(intent,
//						AndroidConfig.ContactsFragmentRequestCode);
//				(ChatActivity.this).overridePendingTransition(
//						R.anim.in_from_right, R.anim.out_to_left);
			} else {
				startAnimActivityForResult(ActivityTalkGroupInfo.class,
						"TalkGroupID", toChatUsername,
						AndroidConfig.writeFriendResultCode);
			}
		}else if(id == R.id.com_title_bar_right_tv){
			//查看群公告
			startActivity(new Intent(ChatActivity.this, ActivityGroupNoticeDetails.class).putExtra("hx_groupid",toChatUsername));
		}
	}

	public void startAnimActivityForResult(Class<?> cla, String key,
			String baseBean, int requestCode) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, baseBean);
		this.startActivityForResult(intent, requestCode);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	/**
	 * 照相获取图片
	 */
	public void selectPicFromCamera() {
		if (!CommonUtils.isExitsSdcard()) {
			String st = getResources().getString(
					R.string.sd_card_does_not_exist);
			Toast.makeText(getApplicationContext(), st, Toast.LENGTH_SHORT).show();
			return;
		}

		cameraFile = new File(PathUtil.getInstance().getImagePath(),
				CrashApplication.getInstance().getUserName()
						+ System.currentTimeMillis() + ".jpg");
		cameraFile.getParentFile().mkdirs();
		startActivityForResult(
				new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
						MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
				REQUEST_CODE_CAMERA);
	}

	/**
	 * 选择文件
	 */
	private void selectFileFromLocal() {
		Intent intent = null;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("*/*");
			intent.addCategory(Intent.CATEGORY_OPENABLE);

		} else {
			intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
	}

	/**
	 * 从图库获取图片
	 */
	public void selectPicFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");

		} else {
			intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, REQUEST_CODE_LOCAL);
	}

	/**
	 * 发送文本消息
	 * 
	 * @param content
	 *            message content
	 */
	private void sendText(String content) {

		if (content.length() > 0 && content.length()<=500) {
			EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
			// 如果是群聊，设置chattype,默认是单聊
			if (chatType == CHATTYPE_GROUP)
				message.setChatType(ChatType.GroupChat);
			TextMessageBody txtBody = new TextMessageBody(content);
			// 设置消息body
			message.addBody(txtBody);
			// 设置要发给谁,用户username或者群聊groupid
			message.setReceipt(toChatUsername);
			// 把messgage加到conversation中
			conversation.addMessage(message);
			// 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
			adapter.refresh();
			listView.setSelection(listView.getCount() - 1);
			mEditTextContent.setText("");

			setResult(RESULT_OK);

		}else{
            ToastUtils.Infotoast(this,"聊天内容最多500字");
        }
	}

    /**
     * 发送号码直通车
     */
    private void sendNumberTrain(NumberTrain numberTrain) {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        // 如果是群聊，设置chattype,默认是单聊
        if (chatType == CHATTYPE_GROUP)
            message.setChatType(ChatType.GroupChat);
        TextMessageBody txtBody = new TextMessageBody("号码直通车");
        // 设置消息body
        message.addBody(txtBody);
        message.setAttribute("train_id", numberTrain.getId());
        message.setAttribute("train_name", numberTrain.getShortName());
        message.setAttribute("train_tag", numberTrain.getTag());
        message.setAttribute("train_poster", numberTrain.getPoster());
        message.setAttribute("shop", numberTrain.getShop());
        // 设置要发给谁,用户username或者群聊groupid
        message.setReceipt(toChatUsername);
        // 把messgage加到conversation中
        conversation.addMessage(message);
        // 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
        adapter.refresh();
        listView.setSelection(listView.getCount() - 1);
        mEditTextContent.setText("");

        setResult(RESULT_OK);

    }

	/**
	 * 发送语音
	 * 
	 * @param filePath
	 * @param fileName
	 * @param length
	 * @param isResend
	 */
	private void sendVoice(String filePath, String fileName, String length,
			boolean isResend) {
		if (!(new File(filePath).exists())) {
			return;
		}
		try {
			final EMMessage message = EMMessage
					.createSendMessage(EMMessage.Type.VOICE);
			// 如果是群聊，设置chattype,默认是单聊
			if (chatType == CHATTYPE_GROUP)
				message.setChatType(ChatType.GroupChat);
			message.setReceipt(toChatUsername);
			int len = Integer.parseInt(length);
			VoiceMessageBody body = new VoiceMessageBody(new File(filePath),
					len);
			message.addBody(body);

			conversation.addMessage(message);
			adapter.refresh();
			listView.setSelection(listView.getCount() - 1);
			setResult(RESULT_OK);
			// send file
			// sendVoiceSub(filePath, fileName, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送图片
	 *
	 * @param filePath
	 */
	private void sendPicture(final String filePath) {
		String to = toChatUsername;
		// create and add image message in view
		final EMMessage message = EMMessage
				.createSendMessage(EMMessage.Type.IMAGE);
		// 如果是群聊，设置chattype,默认是单聊
		if (chatType == CHATTYPE_GROUP)
			message.setChatType(ChatType.GroupChat);

		message.setReceipt(to);
		ImageMessageBody body = new ImageMessageBody(new File(filePath));
		// 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
		// body.setSendOriginalImage(true);
		message.addBody(body);
		conversation.addMessage(message);

		listView.setAdapter(adapter);
		adapter.refresh();
		listView.setSelection(listView.getCount() - 1);
		setResult(RESULT_OK);
		// more(more);
	}

//    /**
//     * 发送图片
//     *
//     */
//    private void sendPicture(ArrayList<String> paths) {
//        String to = toChatUsername;
//        // create and add image message in view
//        final EMMessage message = EMMessage
//                .createSendMessage(EMMessage.Type.IMAGE);
//        // 如果是群聊，设置chattype,默认是单聊
//        if (chatType == CHATTYPE_GROUP)
//            message.setChatType(ChatType.GroupChat);
//
//        message.setReceipt(to);
//        for(int i=0;i< paths.size();i++){
//            ImageMessageBody body = new ImageMessageBody(new File(paths.get(i)));
//            // 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
//            // body.setSendOriginalImage(true);
//            message.addBody(body);
//            conversation.addMessage(message);
//        }
////        ImageMessageBody body = new ImageMessageBody(new File(filePath));
////        // 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
////        // body.setSendOriginalImage(true);
////        message.addBody(body);
////        conversation.addMessage(message);
//
//        listView.setAdapter(adapter);
//        adapter.refresh();
//        listView.setSelection(listView.getCount() - 1);
//        setResult(RESULT_OK);
//        // more(more);
//    }



	/**
	 * 发送视频消息
	 */
	private void sendVideo(final String filePath, final String thumbPath,
			final int length) {
		final File videoFile = new File(filePath);
		if (!videoFile.exists()) {
			return;
		}
		try {
			EMMessage message = EMMessage
					.createSendMessage(EMMessage.Type.VIDEO);
			// 如果是群聊，设置chattype,默认是单聊
			if (chatType == CHATTYPE_GROUP)
				message.setChatType(ChatType.GroupChat);
			String to = toChatUsername;
			message.setReceipt(to);
			VideoMessageBody body = new VideoMessageBody(videoFile, thumbPath,
					length, videoFile.length());
			message.addBody(body);
			conversation.addMessage(message);
			listView.setAdapter(adapter);
			adapter.refresh();
			listView.setSelection(listView.getCount() - 1);
			setResult(RESULT_OK);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

//	/**
//	 * 根据图库图片uri发送图片
//	 *
//	 * @param selectedImage
//	 */
//	private void sendPicByUri(Uri selectedImage) {
//		// String[] filePathColumn = { MediaStore.Images.Media.DATA };
//		Cursor cursor = getContentResolver().query(selectedImage, null, null,
//				null, null);
//		String st8 = getResources().getString(R.string.cant_find_pictures);
//		if (cursor != null) {
//			cursor.moveToFirst();
//			int columnIndex = cursor.getColumnIndex("_data");
//			String picturePath = cursor.getString(columnIndex);
//			cursor.close();
//			cursor = null;
//
//			if (picturePath == null || picturePath.equals("null")) {
//				Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
//				toast.setGravity(Gravity.CENTER, 0, 0);
//				toast.show();
//				return;
//			}
//			sendPicture(picturePath);
//		} else {
//			File file = new File(selectedImage.getPath());
//			if (!file.exists()) {
//				Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
//				toast.setGravity(Gravity.CENTER, 0, 0);
//				toast.show();
//				return;
//
//			}
//			sendPicture(file.getAbsolutePath());
//		}
//
//	}

	/**
	 * 发送位置信息
	 * 
	 * @param latitude
	 * @param longitude
	 * @param imagePath
	 * @param locationAddress
	 */
	private void sendLocationMsg(double latitude, double longitude,
			String imagePath, String locationAddress) {
		EMMessage message = EMMessage
				.createSendMessage(EMMessage.Type.LOCATION);
		// 如果是群聊，设置chattype,默认是单聊
		if (chatType == CHATTYPE_GROUP)
			message.setChatType(ChatType.GroupChat);
		LocationMessageBody locBody = new LocationMessageBody(locationAddress,
				latitude, longitude);
		message.addBody(locBody);
		message.setReceipt(toChatUsername);
		conversation.addMessage(message);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		listView.setSelection(listView.getCount() - 1);
		setResult(RESULT_OK);

	}

	/**
	 * 发送文件
	 * 
	 * @param uri
	 */
	private void sendFile(Uri uri) {
		String filePath = null;
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor = null;

			try {
				cursor = getContentResolver().query(uri, projection, null,
						null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					filePath = cursor.getString(column_index);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			filePath = uri.getPath();
		}
		File file = new File(filePath);
		if (file == null || !file.exists()) {
			String st7 = getResources().getString(R.string.File_does_not_exist);
			Toast.makeText(getApplicationContext(), st7, Toast.LENGTH_SHORT).show();
			return;
		}
		if (file.length() > 10 * 1024 * 1024) {
			String st6 = getResources().getString(
					R.string.The_file_is_not_greater_than_10_m);
			Toast.makeText(getApplicationContext(), st6,Toast.LENGTH_SHORT).show();
			return;
		}

		// 创建一个文件消息
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.FILE);
		// 如果是群聊，设置chattype,默认是单聊
		if (chatType == CHATTYPE_GROUP)
			message.setChatType(ChatType.GroupChat);

		message.setReceipt(toChatUsername);
		// add message body
		NormalFileMessageBody body = new NormalFileMessageBody(new File(
				filePath));
		message.addBody(body);
		conversation.addMessage(message);
		listView.setAdapter(adapter);
		adapter.refresh();
		listView.setSelection(listView.getCount() - 1);
		setResult(RESULT_OK);
	}

	/**
	 * 重发消息
	 */
	private void resendMessage() {
		EMMessage msg = null;
		msg = conversation.getMessage(resendPos);
		// msg.setBackSend(true);
		msg.status = EMMessage.Status.CREATE;

		adapter.refresh();
		listView.setSelection(resendPos);
	}

	/**
	 * 显示语音图标按钮
	 * 
	 * @param view
	 */
	public void setModeVoice(View view) {
		hideKeyboard();
		edittext_layout.setVisibility(View.GONE);
		more.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
		buttonSetModeKeyboard.setVisibility(View.VISIBLE);
		buttonSend.setVisibility(View.GONE);
		btnMore.setVisibility(View.VISIBLE);
		buttonPressToSpeak.setVisibility(View.VISIBLE);
		iv_emoticons_normal.setVisibility(View.VISIBLE);
		iv_emoticons_checked.setVisibility(View.INVISIBLE);
		btnContainer.setVisibility(View.VISIBLE);
		emojiIconContainer.setVisibility(View.GONE);

	}

	/**
	 * 显示键盘图标
	 * 
	 * @param view
	 */
	public void setModeKeyboard(View view) {
		// mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener()
		// {
		// @Override
		// public void onFocusChange(View v, boolean hasFocus) {
		// if(hasFocus){
		// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		// }
		// }
		// });
		edittext_layout.setVisibility(View.VISIBLE);
		more.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
		buttonSetModeVoice.setVisibility(View.VISIBLE);
		// mEditTextContent.setVisibility(View.VISIBLE);
		mEditTextContent.requestFocus();
		// buttonSend.setVisibility(View.VISIBLE);
		buttonPressToSpeak.setVisibility(View.GONE);
		if (TextUtils.isEmpty(mEditTextContent.getText())) {
			btnMore.setVisibility(View.VISIBLE);
			buttonSend.setVisibility(View.GONE);
		} else {
			btnMore.setVisibility(View.GONE);
			buttonSend.setVisibility(View.VISIBLE);
		}

	}

	/**
	 * 点击清空聊天记录
	 * 
	 * @param view
	 */
	public void emptyHistory(View view) {
		String st5 = getResources().getString(
				R.string.Whether_to_empty_all_chats);
		startActivityForResult(
				new Intent(this, AlertDialog.class)
						.putExtra("titleIsCancel", true).putExtra("msg", st5)
						.putExtra("cancel", true), REQUEST_CODE_EMPTY_HISTORY);
	}

	/**
	 * 点击进入群组详情
	 * 
	 * @param view
	 */
	public void toGroupDetails(View view) {
		// startActivityForResult((new Intent(this,
		// GroupDetailsActivity.class).putExtra("groupId", toChatUsername)),
		// REQUEST_CODE_GROUP_DETAIL);
	}

	/**
	 * 显示或隐藏图标按钮页
	 * 
	 * @param view
	 */
	public void more(View view) {
		if (more.getVisibility() == View.GONE) {
			System.out.println("more gone");
			hideKeyboard();
			more.setVisibility(View.VISIBLE);
			btnContainer.setVisibility(View.VISIBLE);
			emojiIconContainer.setVisibility(View.GONE);
		} else {
			if (emojiIconContainer.getVisibility() == View.VISIBLE) {
				emojiIconContainer.setVisibility(View.GONE);
				btnContainer.setVisibility(View.VISIBLE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.INVISIBLE);
			} else {
				more.setVisibility(View.GONE);
			}

		}

	}

	/**
	 * 点击文字输入框
	 * 
	 * @param v
	 */
	public void editClick(View v) {
		listView.setSelection(listView.getCount() - 1);
		if (more.getVisibility() == View.VISIBLE) {
			more.setVisibility(View.GONE);
			iv_emoticons_normal.setVisibility(View.VISIBLE);
			iv_emoticons_checked.setVisibility(View.INVISIBLE);
		}

	}

//	/**
//	 * 消息广播接收者
//	 *
//	 */
//	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			// 记得把广播给终结掉
//
//			// abortBroadcast();
//
//			String username = intent.getStringExtra("from");
//			String msgid = intent.getStringExtra("msgid");
//			// 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
//			EMMessage message = EMChatManager.getInstance().getMessage(msgid);
//			// 如果是群聊消息，获取到group id
//			if (message.getChatType() == ChatType.GroupChat) {
//				username = message.getTo();
//				// message.setFrom(mTalkGroup.getName());
//			}
//			if (!username.equals(toChatUsername)) {
//				// 消息不是发给当前会话，return
//				notifyNewMessage(message);
//				return;
//			} else {
//				abortBroadcast();
//			}
//			conversation = EMChatManager.getInstance().getConversation(
//					toChatUsername);
//			// 通知adapter有新消息，更新ui
//			adapter.refresh();
////			listView.setSelection(listView.getCount() - 1);
//
//		}
//	}

//	/**
//	 * 消息回执BroadcastReceiver
//	 */
//	private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {
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
//				if (msg != null) {
//					msg.isAcked = true;
//				}
//			}
//			adapter.notifyDataSetChanged();
//
//		}
//	};

//	/**
//            * 消息送达BroadcastReceiver
//	 */
//	private BroadcastReceiver deliveryAckMessageReceiver = new BroadcastReceiver() {
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
//				if (msg != null) {
//					msg.isDelivered = true;
//				}
//			}
//
//			adapter.notifyDataSetChanged();
//		}
//	};
	private PowerManager.WakeLock wakeLock;
	private TalkGroup mTalkGroup;
	private finshBroadCast mFinshBroadCast;
	private EMGroup group;

	/**
	 * 按住说话listener
	 * 
	 */
	class PressToSpeakListen implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!CommonUtils.isExitsSdcard()) {
					String st4 = getResources().getString(
							R.string.Send_voice_need_sdcard_support);
					Toast.makeText(ChatActivity.this, st4, Toast.LENGTH_SHORT)
							.show();
					return false;
				}
				try {
					v.setPressed(true);
					wakeLock.acquire();
					if (VoicePlayClickListener.isPlaying)
                        VoicePlayClickListener.currentPlayListener.clearMedia();
//						VoicePlayClickListener.currentPlayListener
//								.stopPlayVoice();
					recordingContainer.setVisibility(View.VISIBLE);
					recordingHint
							.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
					voiceRecorder.startRecording(null, toChatUsername,
							getApplicationContext());
					timer.start();
					isLength = false;
					
				} catch (Exception e) {
					e.printStackTrace();
					v.setPressed(false);
					if (wakeLock.isHeld())
						wakeLock.release();
					if (voiceRecorder != null)
						voiceRecorder.discardRecording();
					recordingContainer.setVisibility(View.INVISIBLE);
					Toast.makeText(ChatActivity.this, R.string.recoding_fail,
							Toast.LENGTH_SHORT).show();
					return false;
				}

				return true;
			case MotionEvent.ACTION_MOVE: {
				if (event.getY() < 0) {
					recordingHint
							.setText(getString(R.string.release_to_cancel));
					recordingHint
							.setBackgroundResource(R.drawable.recording_text_hint_bg);
				} else {
					recordingHint
							.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
				}
				return true;
			}
			case MotionEvent.ACTION_UP:
				v.setPressed(false);
				recordingContainer.setVisibility(View.INVISIBLE);
				if (wakeLock.isHeld())
					wakeLock.release();
				if (event.getY() < 0) {
					// discard the recorded audio.
					voiceRecorder.discardRecording();

				} else {
					// stop recording and send voice file
					String st1 = getResources().getString(
							R.string.Recording_without_permission);
					String st2 = getResources().getString(
							R.string.The_recording_time_is_too_short);
					String st3 = getResources().getString(
							R.string.send_failure_please);
					try {
						int length = voiceRecorder.stopRecoding();
						if (length > 0) {
//							if (length >= 60) {
//								Toast.makeText(getApplicationContext(),
//										"录音时间不能超过60秒", Toast.LENGTH_SHORT).show();
//							}else{
								sendVoice(voiceRecorder.getVoiceFilePath(),
										voiceRecorder
										.getVoiceFileName(toChatUsername),
										Integer.toString(length), false);
								
					//		}
						} else if (length == EMError.INVALID_FILE) {
							if (!isLength) {
								Toast.makeText(getApplicationContext(), st1,
										Toast.LENGTH_SHORT).show();
							}
						}  else {
							if(!isLength){
								Toast.makeText(getApplicationContext(), st2,
										Toast.LENGTH_SHORT).show();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						if(!isLength){
							Toast.makeText(getApplicationContext(), st3,
									Toast.LENGTH_SHORT).show();
						}
					}

				}
				return true;
			default:
				recordingContainer.setVisibility(View.INVISIBLE);
				if (voiceRecorder != null)
					voiceRecorder.discardRecording();
				return false;
			}
		}
	}

	/**
	 * 获取表情的gridview的子view
	 * 
	 * @param i
	 * @return
	 */
	private View getGridChildView(int i) {
		View view = View.inflate(this, R.layout.expression_gridview, null);
		ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
		List<String> list = new ArrayList<String>();
		if (i == 1) {
			List<String> list1 = reslist.subList(0, 20);
			list.addAll(list1);
		} else if (i == 2) {
			list.addAll(reslist.subList(20, reslist.size()));
		}
		list.add("delete_expression");
		final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this,
				1, list);
		gv.setAdapter(expressionAdapter);
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String filename = expressionAdapter.getItem(position);
				try {
					// 文字输入框可见时，才可输入表情
					// 按住说话可见，不让输入表情
					if (buttonSetModeKeyboard.getVisibility() != View.VISIBLE) {

						if (filename != "delete_expression") { // 不是删除键，显示表情
							// 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
							Class clz = Class
									.forName("com.xunao.benben.hx.chatuidemo.utils.SmileUtils");
							Field field = clz.getField(filename);
							mEditTextContent.append(SmileUtils.getSmiledText(
									ChatActivity.this, (String) field.get(null)));
						} else { // 删除文字或者表情
							if (!TextUtils.isEmpty(mEditTextContent.getText())) {

								int selectionStart = mEditTextContent
										.getSelectionStart();// 获取光标的位置
								if (selectionStart > 0) {
									String body = mEditTextContent.getText()
											.toString();
									String tempStr = body.substring(0,
											selectionStart);
									int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
									if (i != -1) {
										CharSequence cs = tempStr.substring(i,
												selectionStart);
										if (SmileUtils.containsKey(cs
												.toString()))
											mEditTextContent.getEditableText()
													.delete(i, selectionStart);
										else
											mEditTextContent.getEditableText()
													.delete(selectionStart - 1,
															selectionStart);
									} else {
										mEditTextContent.getEditableText()
												.delete(selectionStart - 1,
														selectionStart);
									}
								}
							}

						}
					}
				} catch (Exception e) {
				}

			}
		});
		return view;
	}

	public List<String> getExpressionRes(int getSum) {
		List<String> reslist = new ArrayList<String>();
		for (int x = 1; x <= getSum; x++) {
			String filename = "ee_" + x;

			reslist.add(filename);

		}
		return reslist;

	}

    @Override
    protected void onStop() {
        super.onStop();
        EMChatManager.getInstance().unregisterEventListener(this);
    }

    @Override
	protected void onDestroy() {
		super.onDestroy();
        Bimp.tempSelectBitmap.clear();
        CrashApplication.getInstance().removeActivity(this);
		activityInstance = null;
		EMGroupManager.getInstance().removeGroupChangeListener(groupListener);
		// 注销广播
		try {
//			unregisterReceiver(receiver);
			unregisterReceiver(broadcast);
//			receiver = null;
		} catch (Exception e) {
		}
		try {
			unregisterReceiver(mFinshBroadCast);
			mFinshBroadCast = null;
//			unregisterReceiver(ackMessageReceiver);
//			ackMessageReceiver = null;
//			unregisterReceiver(deliveryAckMessageReceiver);
//			deliveryAckMessageReceiver = null;
		} catch (Exception e) {
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (chatType == CHATTYPE_GROUP) {
			refreshGroup();
		}
		adapter.refresh();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (wakeLock.isHeld())
			wakeLock.release();
		if (VoicePlayClickListener.isPlaying
				&& VoicePlayClickListener.currentPlayListener != null) {
			// 停止语音播放
            VoicePlayClickListener.currentPlayListener.clearMedia();
//			VoicePlayClickListener.currentPlayListener.stopPlayVoice();
		}

		try {
			// 停止录音
			if (voiceRecorder.isRecording()) {
				voiceRecorder.discardRecording();
				recordingContainer.setVisibility(View.INVISIBLE);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 加入到黑名单
	 * 
	 * @param username
	 */
	private void addUserToBlacklist(String username) {
		String st11 = getResources().getString(
				R.string.Move_into_blacklist_success);
		String st12 = getResources().getString(
				R.string.Move_into_blacklist_failure);
		try {
			EMContactManager.getInstance().addUserToBlackList(username, false);
			Toast.makeText(getApplicationContext(), st11, Toast.LENGTH_SHORT).show();
		} catch (EaseMobException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), st12,Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 返回
	 * 
	 * @param view
	 */
	public void back(View view) {
		hideKeyboard();
		AnimFinsh();
	}

	/**
	 * 覆盖手机返回键
	 */
	@Override
	public void onBackPressed() {
		if (more.getVisibility() == View.VISIBLE) {
			more.setVisibility(View.GONE);
			iv_emoticons_normal.setVisibility(View.VISIBLE);
			iv_emoticons_checked.setVisibility(View.INVISIBLE);
		} else {
			hideKeyboard();
			AnimFinsh();
		}
	}

	/**
	 * listview滑动监听listener
	 * 
	 */
	private class ListScrollListener implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_IDLE:
				if (view.getFirstVisiblePosition() == 0 && !isloading
						&& haveMoreData) {
					loadmorePB.setVisibility(View.VISIBLE);
					// sdk初始化加载的聊天记录为20条，到顶时去db里获取更多
					List<EMMessage> messages;
					try {
						// 获取更多messges，调用此方法的时候从db获取的messages
						// sdk会自动存入到此conversation中
						if (chatType == CHATTYPE_SINGLE)
							messages = conversation.loadMoreMsgFromDB(adapter
									.getItem(0).getMsgId(), pagesize);
						else
							messages = conversation.loadMoreGroupMsgFromDB(
									adapter.getItem(0).getMsgId(), pagesize);
					} catch (Exception e1) {
						loadmorePB.setVisibility(View.GONE);
						return;
					}
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
					}
					if (messages.size() != 0) {
						// 刷新ui
						adapter.notifyDataSetChanged();
						listView.setSelection(messages.size() - 1);
						if (messages.size() != pagesize)
							haveMoreData = false;
					} else {
						haveMoreData = false;
					}
					loadmorePB.setVisibility(View.GONE);
					isloading = false;

				}
				break;
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		// 点击notification bar进入聊天页面，保证只有一个聊天页面
		String username = intent.getStringExtra("userId");
		if (toChatUsername.equals(username))
			super.onNewIntent(intent);
		else {
			finish();
			startActivity(intent);
		}

	}

	/**
	 * 转发消息
	 * 
	 * @param forward_msg_id
	 */
	protected void forwardMessage(String forward_msg_id) {
		EMMessage forward_msg = EMChatManager.getInstance().getMessage(
				forward_msg_id);
		EMMessage.Type type = forward_msg.getType();
		switch (type) {
		case TXT:
			// 获取消息内容，发送消息
			String content = ((TextMessageBody) forward_msg.getBody())
					.getMessage();
			sendText(content);
			break;
		case IMAGE:
			// 发送图片
			String filePath = ((ImageMessageBody) forward_msg.getBody())
					.getLocalUrl();
			if (filePath != null) {
				File file = new File(filePath);
				if (!file.exists()) {
					// 不存在大图发送缩略图
					filePath = ImageUtils.getThumbnailImagePath(filePath);
				}
				sendPicture(filePath);
			}
			break;
		default:
			break;
		}
	}

	private void showShareActionSheet(final EMMessage forwardMsg) {
		ActionSheet
				.createBuilder(this, getSupportFragmentManager())
				.setCancelButtonTitle("取消")
				.setOtherButtonTitles("分享给好友", "分享到群组")
						// 设置颜色 必须一一对应
				.setOtherButtonTitlesColor("#1E82FF", "#1E82FF")
				.setCancelableOnTouchOutside(true)
				.setListener(new ActionSheet.ActionSheetListener() {
					@Override
					public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
					}

					@Override
					public void onOtherButtonClick(ActionSheet actionSheet, int index) {
						switch (index) {
							case 0:
								Intent intent = new Intent(ChatActivity.this, ActivityShareSelectFriend.class);
								if (forwardMsg.getType() == EMMessage.Type.IMAGE) {
									intent.putExtra("type","Forward_img");
								}else if (forwardMsg.getType() == EMMessage.Type.VIDEO){
									intent.putExtra("type","Forward_video");
								}
								intent.putExtra("msg_id", forwardMsg.getMsgId());
								startActivity(intent);
								overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
								break;
							case 1:
								Intent groupintent = new Intent(ChatActivity.this, ActivityShareSelectTalkGroup.class);
								if (forwardMsg.getType() == EMMessage.Type.IMAGE) {
									groupintent.putExtra("type","Forward_img");
								}else if (forwardMsg.getType() == EMMessage.Type.VIDEO){
									groupintent.putExtra("type","Forward_video");
								}
								groupintent.putExtra("msg_id", forwardMsg.getMsgId());
								startActivity(groupintent);
								overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
								break;
						}
					}
				}).show();
	}

	/**
	 * 监测群组解散或者被T事件
	 * 
	 */
	class GroupListener extends GroupReomveListener {

		@Override
		public void onUserRemoved(final String groupId, String groupName) {
			runOnUiThread(new Runnable() {
				String st13 = getResources().getString(R.string.you_are_group);

				public void run() {
					if (toChatUsername.equals(groupId)) {
						// Toast.makeText(ChatActivity.this, st13, 1).show();
						// TODO
						// 如果群组为空 finish
						// if (GroupDetailsActivity.instance != null)
						// GroupDetailsActivity.instance.finish();
						setResult(AndroidConfig.writeFriendRefreshResultCode);
						AnimFinsh();
					}
				}
			});
		}

		@Override
		public void onGroupDestroy(final String groupId, String groupName) {
			// 群组解散正好在此页面，提示群组被解散，并finish此页面
			runOnUiThread(new Runnable() {
				String st14 = getResources().getString(
						R.string.the_current_group);

				public void run() {
					if (toChatUsername.equals(groupId)) {
						setResult(AndroidConfig.writeFriendRefreshResultCode);
						AnimFinsh();
					}
				}
			});
		}

	}

	public String getToChatUsername() {
		return toChatUsername;
	}

	class finshBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			ChatActivity.this.AnimFinsh();
		}

	}
	boolean isLength = false;
	private CountDownTimer timer = new CountDownTimer(60000, 1000) {
		@Override
		public void onTick(long millisUntilFinished) {
//            int last = 60-length;
            int last = (int) (millisUntilFinished/1000);
            tv_record_time.setText("剩余"+last+"s");
		}

		@Override
		public void onFinish() {
			int length = voiceRecorder.stopRecoding();

			if(length >= 60){
                recordingContainer.setVisibility(View.INVISIBLE);
				isLength = true;
				sendVoice(voiceRecorder.getVoiceFilePath(),
						voiceRecorder
						.getVoiceFileName(toChatUsername),
						Integer.toString(length), false);
			}
			return;
		}
	};
	
}
