package com.xunao.benben.ui.item;

import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMError;
import com.easemob.util.VoiceRecorder;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.BroadCasting;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.FriendUnion;
import com.xunao.benben.bean.FriendUnionMember;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityLogin;
import com.xunao.benben.utils.Bimp;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.FileUtils;
import com.xunao.benben.utils.ImageItem;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.PublicWay;
import com.xunao.benben.utils.Res;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.utils.click.XunAoVoicePlayClickListener;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.MyTextView;

public class ActivitySmallPublic extends BaseActivity implements OnClickListener {

	protected static final int CHOICE_FRIEND_UNION = 1000;
	protected static final int CHOICE_BENBEN_FRIEND = 1001;
	private EditText et_content;
	private LinearLayout ll_choice_friend_union;
	private RelativeLayout rl_choice_friend_union;
	private ArrayList<FriendUnion> friendUnions = new ArrayList<>();
	private String friendUnionId = "";
	private TextView tv_friend_union_name;
	private RelativeLayout rl_choice_friend;
	private TextView tv_friend;
	private TextView tv_receivers;
	private ArrayList<Contacts> contacts = new ArrayList<Contacts>();
    private ArrayList<FriendUnionMember>  friendUnionContacts = new ArrayList<FriendUnionMember>();
	private CheckBox item_all;
    private ImageView iv_arrow;
	private boolean isSelectUnion = false;
	private FriendUnion friendUnion;
	private String numberTrain;
	private MyTextView com_title_bar_right_tv;
    private RelativeLayout prerecord_tab_one;
    private RelativeLayout prerecord_tab_three;
    private View[] but;
    private static final int RECORD = 1;
    private static final int TEXTIMG = 0;
    private int statueType = TEXTIMG;;
	private int  send = 1;
	private BroadCasting sendContacts;

    private GridView noScrollgridview;
    private GridAdapter adapter;
    private static final int TAKE_PICTURE = 0x000001;
    public static Bitmap bimap;
    // 录音
    private TextView recordingHint;
    private ImageView micImage;
    private Drawable[] micImages;
    View recordingContainer;
    private VoiceRecorder voiceRecorder;
    // 录音界面
    private View vedio_box;
    // 录音文件
    private View vedio;
    // 删除录音文件
    private View delete;
    // 录音按钮
    private View record_vedio;
    private MyTextView record_vedio_text;
    private Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 切换msg切换图片
            micImage.setImageDrawable(micImages[msg.what]);
        }
    };
    private MsgDialog msgDialog;
    private TextView tv_record_time;
    private String promotion;
    private String url="";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Res.init(this);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;
        CrashApplication.getInstance().addActivity(this);
        bimap = BitmapFactory.decodeResource(getResources(),
                R.drawable.icon_addpic_unfocused);
        PublicWay.activityList.add(this);
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_send_broadcast);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("发送小喇叭", "", "发送",
				R.drawable.icon_com_title_left, 0);

        but = new View[2];
        prerecord_tab_one = (RelativeLayout) findViewById(R.id.prerecord_tab_one);
        prerecord_tab_three = (RelativeLayout) findViewById(R.id.prerecord_tab_three);
        but[0] = prerecord_tab_one;
        but[1] = prerecord_tab_three;
        prerecord_tab_one.setOnClickListener(this);
        prerecord_tab_three.setOnClickListener(this);
        initBox();
		et_content = (EditText) findViewById(R.id.et_content);
		et_content.setBackgroundColor(Color.WHITE);
		et_content.setHint("说点什么吧...");
        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        adapter.update();
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                if (numberTrain != null) {
                    ToastUtils.Infotoast(mContext,"不能同时发送图片");
                }else {

                    if (arg2 == Bimp.tempSelectBitmap.size()) {
                        if (Bimp.tempSelectBitmap.size() < 6) {
                            changeImage();
                        }
                    } else {
                        Intent intent = new Intent(ActivitySmallPublic.this,
                                GalleryActivity.class);
                        intent.putExtra("position", "1");
                        intent.putExtra("ID", arg2);
                        startActivity(intent);
                    }
                }
            }

        });
        prerecord_tab_one.performClick();

		ll_choice_friend_union = (LinearLayout) findViewById(R.id.ll_choice_friend_union);
		rl_choice_friend_union = (RelativeLayout) findViewById(R.id.rl_choice_friend_union);
		tv_friend_union_name = (TextView) findViewById(R.id.tv_friend_union_name);

		com_title_bar_right_tv = (MyTextView) findViewById(R.id.com_title_bar_right_tv);
		
		rl_choice_friend = (RelativeLayout) findViewById(R.id.rl_choice_friend);
		tv_friend = (TextView) findViewById(R.id.tv_friend);
		item_all = (CheckBox) findViewById(R.id.item_all);
        iv_arrow = (ImageView) findViewById(R.id.iv_arrow);
		tv_receivers = (TextView) findViewById(R.id.tv_receivers);

		ll_choice_friend_union.setVisibility(View.VISIBLE);
		rl_choice_friend_union.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
                if (friendUnion.getType().equals("1")) {
                    Intent intent = new Intent(ActivitySmallPublic.this, ActivityChoiceBroadUnion.class);
                    intent.putExtra("contactsList",friendUnionContacts);
                    intent.putExtra("friendUnion", friendUnion);
                    startActivityForResult(intent, CHOICE_FRIEND_UNION);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

                } else {

                    if (item_all.isChecked()) {
                        item_all.setChecked(false);
                        isSelectUnion = false;
                        tv_friend_union_name.setText("发送给好友联盟(0人)");
                    } else {
                        item_all.setChecked(true);
                        isSelectUnion = true;
                        tv_friend_union_name.setText("发送给好友联盟("
                                + friendUnion.getNumber() + "人)");
                    }

                    int size = 0;
                    if (contacts.size() > 0) {
                        if (contacts.size() >= 3) {
                            size = 3;
                        } else {
                            size = contacts.size();
                        }
                        String content = "";
                        if (isSelectUnion) {
                            int size2 = contacts.size() + friendUnion.getNumber();
                            content = size2 + "位收件人：英雄联盟,";
                        } else {
                            content = contacts.size() + "位收件人：";
                        }

                        for (int i = 0; i < size; i++) {
                            content += contacts.get(i).getName() + ",";
                        }
                        content = content.substring(0, content.length() - 1);
                        content += "等好友";

                        tv_receivers.setText(content);
                    } else {
                        if (item_all.isChecked()) {
                            tv_receivers.setText(friendUnion.getNumber()
                                    + "位收件人：英雄联盟");
                        } else {
                            tv_receivers.setText("0位收件人");
                        }
                    }
                }
            }
		});

		rl_choice_friend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
                startAnimActivityForResult(ActivityChoiceBroadFriend.class,
                        "contactsList", contacts, "typeWhat", typeWhat,
                        CHOICE_BENBEN_FRIEND);
			}
		});

		if (user.getSysLeague() != 2) {
			rl_choice_friend_union.setVisibility(View.GONE);
		} else {
			if (CommonUtils.isNetworkAvailable(mContext)) {
				InteNetUtils.getInstance(mContext).getMyFriendUnionInfo(
						mRequestCallBack);
			}
		}
        initrecord();
	}

    private void initBox() {

        vedio_box = findViewById(R.id.vedio_box);
        vedio = findViewById(R.id.vedio);
        delete = findViewById(R.id.delete);
        record_vedio = findViewById(R.id.record_vedio);
        tv_record_time = (TextView)findViewById(R.id.tv_record_time);
        record_vedio_text = (MyTextView) findViewById(R.id.record_vedio_text);
        record_vedio.setOnTouchListener(new PressToSpeakListen());

        delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                vedio.setVisibility(View.INVISIBLE);
                if (voiceRecorder != null) {
                    voiceRecorder.discardRecording();
                    voiceRecorder = null;
                    isrecordOk = false;
                }
                if (XunAoVoicePlayClickListener.isPlaying)
                    XunAoVoicePlayClickListener.currentPlayListener
                            .stopPlayVoice();
                record_vedio_text.setText("按住按钮添加录音");

            }
        });

    }

    private void initrecord() {
        recordingHint = (TextView) findViewById(R.id.recording_hint);
        wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "xunao");
        micImage = (ImageView) findViewById(R.id.mic_image);
        recordingContainer = findViewById(R.id.recording_container);
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

    }

	@Override
	public void initDate(Bundle savedInstanceState) {
//        AidPic aicpic = new AidPic();
//        picList.add(aicpic);
//        adapter.notifyDataSetChanged();

		iD = getIntent().getStringExtra("ID");

		numberTrain = getIntent().getStringExtra("numberTrain");
        promotion = getIntent().getStringExtra("promotion");
        url = getIntent().getStringExtra("url");

		sendContacts = (BroadCasting) getIntent().getSerializableExtra(
				"sendContacts");

		if (sendContacts != null) {
			if (!sendContacts.getLeague_id().equals("0")) {
				item_all.setChecked(true);
				friendUnionId = sendContacts.getLeague_id();
				tv_friend_union_name.setText("发送给好友联盟(0人)");
				isSelectUnion = true;
			}

			if (!TextUtils.isEmpty(sendContacts.getPhone())) {
				String[] phone = sendContacts.getPhone().split(",");
				String[] isBenBen = sendContacts.getIs_benben().split(",");
				contacts.clear();
				for (int i = 0; i < phone.length; i++) {
					Contacts contact = new Contacts();
					contact.setPhone(phone[i]);
					contact.setIs_benben(isBenBen[i]);
					try {
						List list = dbUtil.findAll(Selector
								.from(Contacts.class).where(
										WhereBuilder.b("is_benben", "=",
												isBenBen[i])));
						ArrayList<Contacts> arrayList = new ArrayList<Contacts>();
						arrayList = (ArrayList<Contacts>) list;

						if (arrayList.size() > 0) {
							contact.setName(arrayList.get(0).getName());
						} else {
							contact.setName(phone[i]);
						}

					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					contacts.add(contact);
				}
			}
			tv_receivers.setText(sendContacts.getDescription() + "等好友");
			tv_friend.setText("发送给奔犇好友("
					+ sendContacts.getIs_benben().split(",").length + "人)");
		} else {
			tv_friend.setText("发送给奔犇好友(0人)");
		}

		if (numberTrain != null) {
			et_content.setText(numberTrain);
		}
        if (promotion != null) {
            et_content.setText(promotion);
        }
		
	}

    // 显示拍照选照片 弹窗
    private void changeImage() {
        setTheme(R.style.ActionSheetStyleIOS7);
        showActionSheet();
    }
    public void showActionSheet() {
        ActionSheet
                .createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle("取消")
                .setOtherButtonTitles("拍摄新图片", "从相册选择")
                        // 设置颜色 必须一一对应
                .setOtherButtonTitlesColor("#1E82FF", "#1E82FF")
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet,
                                                   int index) {
                        switch (index) {
                            case 0:
                                photo();
                                break;
                            case 1:
                                Intent intent = new Intent(
                                        ActivitySmallPublic.this,
                                        ImageFile.class);
                                startActivity(intent);
                                overridePendingTransition(
                                        R.anim.activity_translate_in,
                                        R.anim.activity_translate_out);
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onDismiss(ActionSheet actionSheet,
                                          boolean isCancel) {
                        // TODO Auto-generated method stub

                    }
                }).show();
    }

    public void photo() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }


	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		setOnRightClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String content = et_content.getText().toString().trim();

                switch (statueType) {
                    case RECORD:
                        if (TextUtils.isEmpty(content) && !isrecordOk) {
                            ToastUtils.Errortoast(mContext, "内容不可为空");
                            return;
                        }
                        if (!CommonUtils.StringIsSurpass2(content, 0, 200)) {
                            ToastUtils.Errortoast(mContext, "文字内容限制在200个字之间!");
                            return;
                        }
                        String videoPath = "";
                        if(voiceRecorder!=null) {
                            videoPath =voiceRecorder.getVoiceFilePath();
                            Log.d("ltf","videoPath============"+videoPath);
                        }
                        if (user.getSysLeague() == 2) {
                            if (!item_all.isChecked() && contacts.size() <= 0 && friendUnionContacts.size() <=0) {
                                ToastUtils.Errortoast(mContext, "请选择要发送的好友联盟或好友!");
                                return;
                            }

                            String description = "";
                            if (item_all.isChecked()) {
                                friendUnionId = friendUnion.getId();
                                if(friendUnion.getType().equals("1")){
                                    description = "工作联盟,";
                                }else{
                                    description = "英雄联盟,";
                                }
                            } else {
                                friendUnionId = "";
                            }
                            String legphone = "";
                            if (friendUnionContacts.size() > 0) {
                                friendUnionId = friendUnion.getId();
                                for (FriendUnionMember contact : friendUnionContacts) {
                                    legphone += contact.getBenbenId() + ",";
                                }
                                legphone = legphone.substring(0, legphone.length() - 1);
                            }
                            String phone = "";
                            if (contacts.size() > 0) {
                                for (Contacts contact : contacts) {
                                    phone += contact.getIs_benben() + ",";
                                    description += contact.getName()+",";
                                }
                                phone = phone.substring(0, phone.length() - 1);
                                description = description.substring(0, description.length() - 1);
                            }
                            int num=0;
                            if(friendUnion.getType().equals("1")) {
                                num = contacts.size() + friendUnionContacts.size();
                            }else{
                                num = contacts.size() + friendUnion.getNumber();
                            }
                            description = num+"位收件人:"+description;

                            if (CommonUtils.isNetworkAvailable(mContext)) {
                                com_title_bar_right_tv.setClickable(false);
                                if (numberTrain != null) {
                                    if (send == 1) {
                                        send = 2;
                                        if (promotion != null && !promotion.equals("")){
                                            content += url;
                                        }

                                        InteNetUtils.getInstance(mContext).doPublicVoice(
                                                friendUnionId, content, phone,legphone, "1", videoPath,recordLength,
                                                requestCallBack);
                                    }
                                } else {
                                    if (send == 1) {
                                        send = 2;
                                        if (promotion != null && !promotion.equals("")){
                                            content += url;
                                        }
                                        InteNetUtils.getInstance(mContext).doPublicVoice(
                                                friendUnionId, content, phone,legphone, "0", videoPath,recordLength,
                                                requestCallBack);
                                    }
                                }
                                ToastUtils.Infotoast(mContext, "小喇叭已发送");
                                BroadCasting message = new BroadCasting();
                                message.setId("");
                                message.setContentdetail(content);
                                message.setCreatedTime(simpleDateFormat.format(new Date()));
                                message.setShortDescription(tv_receivers.getText().toString());
                                message.setDescription(description);
                                message.setType(2);
                                message.setImages(videoPath);
                                Intent intent = new Intent();
                                intent.putExtra("message",message);
                                setResult(RESULT_OK,intent);
                                AnimFinsh();
                            }else{
                                ToastUtils.Errortoast(mContext, "当前网络不可用!");
                            }

                        } else {
                            String phone = "";
                            String description="";
                            if (contacts.size() > 0) {
                                for (Contacts contact : contacts) {
                                    phone += contact.getIs_benben() + ",";
                                    description += contact.getName() + ",";
                                }
                                phone = phone.substring(0, phone.length() - 1);
                                description = description.substring(0, description.length() - 1);
                            } else {
                                ToastUtils.Errortoast(mContext, "请选择要发送的好友!");
                                return;
                            }

                            if (CommonUtils.isNetworkAvailable(mContext)) {
                                com_title_bar_right_tv.setClickable(false);
                                if (numberTrain != null) {
                                    if (send == 1) {
                                        send = 2;
                                        if (promotion != null && !promotion.equals("")){
                                            content += url;
                                        }
                                        InteNetUtils.getInstance(mContext).doPublicVoice("",
                                                content, phone,"", "1", videoPath,recordLength, requestCallBack);
                                    }
                                } else {
                                    if (send == 1) {
                                        send = 2;
                                        if (promotion != null && !promotion.equals("")){
                                            content += url;
                                        }
                                        InteNetUtils.getInstance(mContext).doPublicVoice("",
                                                content, phone,"", "0", videoPath,recordLength, requestCallBack);
                                    }
                                }
                                ToastUtils.Infotoast(mContext, "小喇叭已发送");
                                BroadCasting message = new BroadCasting();
                                message.setId("");
                                message.setContentdetail(content);
                                message.setCreatedTime(simpleDateFormat.format(new Date()));
                                message.setShortDescription(tv_receivers.getText().toString());
                                message.setDescription(description);
                                message.setType(2);
                                message.setImages(videoPath);
                                Intent intent = new Intent();
                                intent.putExtra("message",message);
                                setResult(RESULT_OK,intent);
                                AnimFinsh();
                            }else{
                                ToastUtils.Errortoast(mContext, "当前网络不可用!");
                            }
                        }
                        break;
                    case TEXTIMG:
                        if (TextUtils.isEmpty(content) && Bimp.tempSelectBitmap.size() <= 0) {
                            ToastUtils.Errortoast(mContext, "小喇叭内容不可为空");
                            return;
                        }
                        if (!CommonUtils.StringIsSurpass2(content, 0, 200)) {
                            ToastUtils.Errortoast(mContext, "文字内容限制在200个字之间!");
                            return;
                        }
                        int size = Bimp.tempSelectBitmap.size();
                        String[] images = new String[size];
                        String Images = null;

                        for (int i = 0; i < size; i++) {
                            images[i] = Bimp.tempSelectBitmap.get(i).getImagePath();
                            if (Images != null) {
                                Images += "^" + Bimp.tempSelectBitmap.get(i).getImagePath();
                            } else {
                                Images = Bimp.tempSelectBitmap.get(i).getImagePath();
                            }
                        }

                        if (user.getSysLeague() == 2) {
                            if (!item_all.isChecked() && contacts.size() <= 0 && friendUnionContacts.size() <=0) {
                                ToastUtils.Errortoast(mContext, "请选择要发送的好友联盟或好友!");
                                return;
                            }
                            String description = "";
                            if (item_all.isChecked()) {
                                friendUnionId = friendUnion.getId();
                                if(friendUnion.getType().equals("1")){
                                    description = "工作联盟,";
                                }else{
                                    description = "英雄联盟,";
                                }
                            } else {
                                friendUnionId = "";
                            }

                            String legphone = "";
                            if (friendUnionContacts.size() > 0) {
                                friendUnionId = friendUnion.getId();
                                for (FriendUnionMember contact : friendUnionContacts) {
                                    legphone += contact.getBenbenId() + ",";
                                }
                                legphone = legphone.substring(0, legphone.length() - 1);
                            }


                            String phone = "";

                            if (contacts.size() > 0) {
                                for (Contacts contact : contacts) {
                                    phone += contact.getIs_benben() + ",";
                                    description += contact.getName()+",";
                                }
                                phone = phone.substring(0, phone.length() - 1);
                                description = description.substring(0, description.length() - 1);
                            }
                            int num=0;
                            if(friendUnion.getType().equals("1")) {
                                num = contacts.size() + friendUnionContacts.size();
                            }else{
                                num = contacts.size() + friendUnion.getNumber();
                            }
                            description = num+"位收件人:"+description;

                            if (CommonUtils.isNetworkAvailable(mContext)) {
                                com_title_bar_right_tv.setClickable(false);
                                if (numberTrain != null) {
                                    if (send == 1) {
                                        send = 2;
                                        if (promotion != null && !promotion.equals("")){
                                            content += url;
                                        }
                                        InteNetUtils.getInstance(mContext).doPublic(
                                                friendUnionId, content, phone,legphone, "1", images,
                                                requestCallBack);
                                    }
                                } else {
                                    if (send == 1) {
                                        send = 2;
                                        if (promotion != null && !promotion.equals("")){
                                            content += url;
                                        }
                                        InteNetUtils.getInstance(mContext).doPublic(
                                                friendUnionId, content, phone,legphone, "0", images,
                                                requestCallBack);
                                    }
                                }
                                ToastUtils.Infotoast(mContext, "小喇叭已发送");
                                BroadCasting message = new BroadCasting();
                                message.setId("");
                                message.setContentdetail(content);
                                message.setCreatedTime(simpleDateFormat.format(new Date()));
                                message.setShortDescription(tv_receivers.getText().toString());
                                message.setDescription(description);
                                message.setType(1);
                                message.setImages(Images);
                                Intent intent = new Intent();
                                intent.putExtra("message",message);
                                setResult(RESULT_OK,intent);

                                AnimFinsh();
                            }else{
                                ToastUtils.Errortoast(mContext, "当前网络不可用!");
                            }

                        } else {
                            String phone = "";
                            String description="";
                            if (contacts.size() > 0) {
                                for (Contacts contact : contacts) {
                                    phone += contact.getIs_benben() + ",";
                                    description += contact.getName() + ",";
                                }
                                phone = phone.substring(0, phone.length() - 1);
                                description = description.substring(0, description.length() - 1);
                            } else {
                                ToastUtils.Errortoast(mContext, "请选择要发送的好友!");
                                return;
                            }

                            if (CommonUtils.isNetworkAvailable(mContext)) {
                                com_title_bar_right_tv.setClickable(false);
                                if (numberTrain != null) {
                                    if (send == 1) {
                                        send = 2;
                                        if (promotion != null && !promotion.equals("")){
                                            content += url;
                                        }
                                        InteNetUtils.getInstance(mContext).doPublic("",
                                                content, phone,"", "1", images, requestCallBack);
                                    }
                                } else {
                                    if (send == 1) {
                                        send = 2;
                                        if (promotion != null && !promotion.equals("")){
                                            content += url;
                                        }
                                        InteNetUtils.getInstance(mContext).doPublic("",
                                                content, phone, "","0", images, requestCallBack);
                                    }
                                }
                                ToastUtils.Infotoast(mContext, "小喇叭已发送");
                                BroadCasting message = new BroadCasting();
                                message.setId("");
                                message.setContentdetail(content);
                                message.setCreatedTime(simpleDateFormat.format(new Date()));
                                message.setShortDescription(tv_receivers.getText().toString());
                                message.setDescription(description);
                                message.setType(1);
                                message.setImages(Images);
                                Intent intent = new Intent();
                                intent.putExtra("message",message);
                                setResult(RESULT_OK,intent);
                                AnimFinsh();
                            }else{
                                ToastUtils.Errortoast(mContext, "当前网络不可用!");
                            }
                        }
                        break;
                }
			}
		});
	}

	@Override
	protected void onHttpStart() {

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		friendUnion = new FriendUnion();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("info");
            if(jsonArray==null || jsonArray.length()==0){
                rl_choice_friend_union.setVisibility(View.GONE);
            }else {
                JSONObject job = jsonArray.getJSONObject(0);
                friendUnion = friendUnion.parseJSON(job);
                if(friendUnion.getType().equals("1")){
                    iv_arrow.setVisibility(View.VISIBLE);
                    item_all.setVisibility(View.GONE);
                }else if(friendUnion.getType().equals("2")){
                    iv_arrow.setVisibility(View.GONE);
                    item_all.setVisibility(View.VISIBLE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NetRequestException e) {
            e.printStackTrace();
        }
//        JSONObject object = jsonObject.optJSONObject("info");
//		try {
//			if(object != null){
//				friendUnion = friendUnion.parseJSON(object);
//                if(friendUnion.getCategory().equals("1")){
//                    iv_arrow.setVisibility(View.VISIBLE);
//                    item_all.setVisibility(View.GONE);
//                }else if(friendUnion.getCategory().equals("2")){
//                    iv_arrow.setVisibility(View.GONE);
//                    item_all.setVisibility(View.VISIBLE);
//                }
//
//			}else{
//				rl_choice_friend_union.setVisibility(View.GONE);
//			}
//
//		} catch (NetRequestException e) {
//			e.printStackTrace();
//		}

		tv_friend_union_name
				.setText("发送给好友联盟("
						+ (item_all.isChecked() ? friendUnion.getNumber() : "0")
						+ "人)");
		
		
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Errortoast(mContext, "网络不可用!");
	}

	private RequestCallBack<String> requestCallBack = new RequestCallBack<String>() {

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			String result = arg0.result;
			try {
				JSONObject jsonObject = new JSONObject(result);
				String ret_num = jsonObject.optString("ret_num");
				String ret_msg = jsonObject.optString("ret_msg");

				if ("0".equals(ret_num)) {
//					ToastUtils.Infotoast(mContext, "小喇叭已发送成功");
//					com_title_bar_right_tv.setClickable(true);
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							sendBroadcast(new Intent(
									AndroidConfig.refrashBroadCasting));

							if (numberTrain != null) {
								sendBroadcast(new Intent(
										AndroidConfig.refreshNumberTrain));
							}
						}
					}, 300);
					
//					Intent intent = new Intent();
//					intent.putExtra("ccc", "11");
//					setResult(111, intent);
//					AnimFinsh();
					return;
				} else {
					
					if(jsonObject.optString("ret_num").equals("2015") ){
						final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(
								mContext, R.style.MyDialog1);
						hint.setContent("奔犇账号在其他手机登录");
						hint.setBtnContent("确定");
						hint.show();
						hint.setOKListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								hint.dismiss();
							}
						});

						hint.show();
						CrashApplication.getInstance().logout();
						startActivity(new Intent(mContext, ActivityLogin.class));
//                        com_title_bar_right_tv.setClickable(true);
					}else{
//                        com_title_bar_right_tv.setClickable(true);
//						ToastUtils.Infotoast(mContext, ret_msg);
					}
					return;
				}
			} catch (JSONException e) {
//                com_title_bar_right_tv.setClickable(true);
				e.printStackTrace();
//				ToastUtils.Infotoast(mContext, "小喇叭发送失败!");
				return;
			}
		}

		@Override
		public void onFailure(HttpException arg0, String arg1) {
//            com_title_bar_right_tv.setClickable(true);
//			ToastUtils.Infotoast(mContext, "小喇叭发送失败!");
		}
	};
	private String iD;
	private String typeWhat;

	@Override
	protected void onActivityResult(int arg0, int resultCode, Intent data) {
		switch (arg0) {
		case CHOICE_FRIEND_UNION:
            if (data != null) {
                friendUnionContacts.clear();
                friendUnionContacts = (ArrayList<FriendUnionMember>) data
                        .getSerializableExtra("contacts");

                typeWhat = data.getStringExtra("typeWhat");
                tv_friend_union_name.setText("发送给好友联盟(" + friendUnionContacts.size() + "人)");
                int size = 0;
                if (friendUnionContacts.size() >= 3) {
                    size = 3;
                } else {
                    size = friendUnionContacts.size();
                }
                String content = "";
                int size2 = contacts.size() + friendUnionContacts.size();
                content = size2 + "位收件人";


//                for (int i = 0; i < size; i++) {
//                    content += contacts.get(i).getName() + "、";
//                }
//                content = content.substring(0, content.length() - 1);
//                content += "等好友";

                tv_receivers.setText(content);

            }
			break;
		case CHOICE_BENBEN_FRIEND:
			if (data != null) {
                contacts.clear();
                contacts = (ArrayList<Contacts>) data
                        .getSerializableExtra("contacts");

                typeWhat = data.getStringExtra("typeWhat");
                tv_friend.setText("发送给奔犇好友(" + contacts.size() + "人)");

                if (friendUnion!=null && friendUnion.getType().equals("1")) {
                    String content = "";
                    int size2 = contacts.size() + friendUnionContacts.size();
                    content = size2 + "位收件人";
                    tv_receivers.setText(content);
                } else{
                    int size = 0;
                    if (contacts.size() >= 3) {
                        size = 3;
                    } else {
                        size = contacts.size();
                    }
                    String content = "";
                    if (isSelectUnion && friendUnion != null) {
                        int size2 = contacts.size() + friendUnion.getNumber();
                        if(friendUnion.getType().equals("1")){
                            content = size2 + "位收件人：工作联盟,";
                        }else {
                            content = size2 + "位收件人：英雄联盟,";
                        }
                    } else {
                        content = contacts.size() + "位收件人：";
                    }

                    for (int i = 0; i < size; i++) {
                        content += contacts.get(i).getName() + ",";
                    }
                    content = content.substring(0, content.length() - 1);
                    content += "等好友";

                    tv_receivers.setText(content);

                    if (contacts.size() <= 0 && !isSelectUnion) {
                        tv_receivers.setText("");
                    }
                }
			}
            break;
            case TAKE_PICTURE:
                if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {
                    String fileName = String.valueOf(System.currentTimeMillis());
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    String saveBitmap = FileUtils.saveBitmapPhoto(bm, fileName);
                    ImageItem takePhoto = new ImageItem();
                    takePhoto.setBitmap(new SoftReference<Bitmap>(bm));
                    takePhoto.setImagePath(saveBitmap);

                    Bimp.tempSelectBitmap.add(takePhoto);
                }
                break;

		}
		super.onActivityResult(arg0, resultCode, data);
	}

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.prerecord_tab_one:// 图文
                if(statueType != TEXTIMG) {
                    if(isrecordOk) {
                        msgDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
                        msgDialog.setContent("确定放弃编辑音频吗？", "", "确认", "取消");
                        msgDialog.setCancleListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                msgDialog.dismiss();
                            }
                        });
                        msgDialog.setOKListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                msgDialog.dismiss();
                                statueType = TEXTIMG;
                                setChecked(prerecord_tab_one, false);
                                setChecked(prerecord_tab_three, false);
//                curTouchTab = prerecord_tab_one;
                                setChecked(prerecord_tab_one, true);
                                noScrollgridview.setVisibility(View.VISIBLE);
                                vedio_box.setVisibility(View.GONE);
                                // 删除选择图片
                                Bimp.tempSelectBitmap.clear();
                                adapter.notifyDataSetChanged();
                            }
                        });
                        msgDialog.show();
                    }else{
                        statueType = TEXTIMG;
                        setChecked(prerecord_tab_one, false);
                        setChecked(prerecord_tab_three, false);
//                curTouchTab = prerecord_tab_one;
                        setChecked(prerecord_tab_one, true);
                        noScrollgridview.setVisibility(View.VISIBLE);
                        vedio_box.setVisibility(View.GONE);
                        // 删除选择图片
                        Bimp.tempSelectBitmap.clear();
                        adapter.notifyDataSetChanged();
                    }
                }

                break;
            case R.id.prerecord_tab_three:// 音频
                if(statueType != RECORD) {
                    if(Bimp.tempSelectBitmap.size() > 0) {
                        msgDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
                        msgDialog.setContent("确定放弃编辑图片吗？", "", "确认", "取消");
                        msgDialog.setCancleListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                msgDialog.dismiss();
                            }
                        });
                        msgDialog.setOKListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                msgDialog.dismiss();
                                statueType = RECORD;
                                setChecked(prerecord_tab_one, false);
                                setChecked(prerecord_tab_three, false);
//                curTouchTab = prerecord_tab_three;
                                setChecked(prerecord_tab_three, true);
                                noScrollgridview.setVisibility(View.GONE);
                                vedio_box.setVisibility(View.VISIBLE);
                                isrecordOk = false;
                                vedio.setVisibility(View.INVISIBLE);
                                if (voiceRecorder != null) {
                                    voiceRecorder.discardRecording();
                                    voiceRecorder = null;
                                }
                                record_vedio_text.setText("按住按钮添加录音");
                            }
                        });
                        msgDialog.show();
                    }else{
                        statueType = RECORD;
                        setChecked(prerecord_tab_one, false);
                        setChecked(prerecord_tab_three, false);
//                curTouchTab = prerecord_tab_three;
                        setChecked(prerecord_tab_three, true);
                        noScrollgridview.setVisibility(View.GONE);
                        vedio_box.setVisibility(View.VISIBLE);
                        isrecordOk = false;
                        vedio.setVisibility(View.INVISIBLE);
                        if (voiceRecorder != null) {
                            voiceRecorder.discardRecording();
                            voiceRecorder = null;
                        }
                        record_vedio_text.setText("按住按钮添加录音");
                    }
                }

                break;
        }
    }


    @SuppressLint("HandlerLeak")
    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int selectedPosition = -1;
        private boolean shape;
        private int mWidth = (mScreenWidth - PixelUtil.dp2px(100)) / 4;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        public int getCount() {
            if (Bimp.tempSelectBitmap.size() == 6) {
                return 6;
            }
            return (Bimp.tempSelectBitmap.size() + 1);
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                View v = convertView.findViewById(R.id.box);
                holder.delete = (ImageView) convertView
                        .findViewById(R.id.delete);
                convertView.setTag(holder);

                v.setLayoutParams(new RelativeLayout.LayoutParams(mWidth
                        + PixelUtil.dp2px(10), mWidth + PixelUtil.dp2px(10)));
                holder.image.getLayoutParams().width = mWidth;
                holder.image.getLayoutParams().height = mWidth;

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == Bimp.tempSelectBitmap.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.icon_addpic_unfocused));

//				decodeFile = PhotoUtils.rotaingImageView(
//						PhotoUtils.readPictureDegree(image.getAbsolutePath()),
//						image.getAbsolutePath());

                holder.delete.setVisibility(View.GONE);
                if (position == 6) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position)
                        .getBitmap());
                holder.delete.setVisibility(View.VISIBLE);
                holder.delete.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Bimp.tempSelectBitmap.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
            public ImageView delete;
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        public void loading() {
            adapter.notifyDataSetChanged();
        }
    }

    protected void onRestart() {
        adapter.update();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Bimp.tempSelectBitmap.clear();
        CrashApplication.getInstance().removeActivity(this);
        if (XunAoVoicePlayClickListener.isPlaying)
            XunAoVoicePlayClickListener.currentPlayListener
                    .stopPlayVoice();
    }

    private PowerManager.WakeLock wakeLock;
    private String TAG = "record";
    private boolean isrecordOk;
    private int recordLength;


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
                        ToastUtils.Infotoast(ActivitySmallPublic.this, st4);
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        wakeLock.acquire();
                        if (XunAoVoicePlayClickListener.isPlaying)
                            XunAoVoicePlayClickListener.currentPlayListener
                                    .stopPlayVoice();
                        recordingContainer.setVisibility(View.VISIBLE);
                        recordingHint
                                .setText(getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);

                        if (voiceRecorder == null) {
                            voiceRecorder = new VoiceRecorder(micImageHandler);
                        }

                        voiceRecorder.startRecording(null, TAG,
                                getApplicationContext());
                        timer.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                        v.setPressed(false);
                        if (wakeLock.isHeld())
                            wakeLock.release();
                        if (voiceRecorder != null)
                            voiceRecorder.discardRecording();
                        recordingContainer.setVisibility(View.INVISIBLE);
                        ToastUtils.Infotoast(ActivitySmallPublic.this,
                                "录音失败，请重试！");
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
                                if (length >= 60) {
                                    Toast.makeText(getApplicationContext(),
                                            "录音时间不能超过60秒", Toast.LENGTH_SHORT)
                                            .show();
                                } else {
                                    recordLength = length;
                                    vedio.setVisibility(View.VISIBLE);
                                    vedio.setOnClickListener(new XunAoVoicePlayClickListener(
                                            voiceRecorder.getVoiceFilePath(),
                                            ActivitySmallPublic.this));
                                    record_vedio_text.setText("重新录制");
                                    isrecordOk = true;
                                }
                                // sendVoice(voiceRecorder.getVoiceFilePath(),
                                // voiceRecorder
                                // .getVoiceFileName(TAG),
                                // Integer.toString(length), false);
                            } else if (length == EMError.INVALID_FILE) {
                                Toast.makeText(getApplicationContext(), st1,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), st2,
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtils.Errortoast(ActivitySmallPublic.this, st3);
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

    private void setChecked(RelativeLayout view, boolean isCheck) {
        RadioButton tab_RB = (RadioButton) view.findViewById(R.id.tab_RB);
        tab_RB.setChecked(isCheck);

    }

    private CountDownTimer timer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
//            int last = 60-length;
            int last = (int) (millisUntilFinished/1000);
            tv_record_time.setText("剩余"+last+"s");
        }

        @Override
        public void onFinish() {
            record_vedio.setPressed(false);
            recordingContainer.setVisibility(View.INVISIBLE);
            if (wakeLock.isHeld())
                wakeLock.release();

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
                    recordLength = length;
                    vedio.setVisibility(View.VISIBLE);
                    vedio.setOnClickListener(new XunAoVoicePlayClickListener(
                            voiceRecorder.getVoiceFilePath(),
                            ActivitySmallPublic.this));
                    record_vedio_text.setText("重新录制");
                    isrecordOk = true;

                } else if (length == EMError.INVALID_FILE) {
                    Toast.makeText(getApplicationContext(), st1,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), st2,
                            Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.Errortoast(ActivitySmallPublic.this, st3);
            }
        }
    };
}