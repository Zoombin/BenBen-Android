package com.xunao.benben.ui.item;

import in.srain.cube.image.CubeImageView;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.BxContacts;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.ContactsGroup;
import com.xunao.benben.bean.PhoneInfo;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.hx.chatuidemo.activity.ChatActivity;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PhoneUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.ActionSheet.ActionSheetListener;
import com.xunao.benben.view.MyTextView;

public class ActivityContactsInfo extends BaseActivity implements
		OnClickListener {

	private Contacts mContacts;

	private CubeImageView contacts_poster;
	private TextView contacts_name;
    private TextView tv_nick_name;
	private TextView contacts_group_name;
	private TextView contacts_benben;
    private MyTextView send_msg_or_add_friend;
	private LinearLayout contacts_add_benben;
	private ListView listview;
	private LinearLayout send_message;
	private ArrayList<PhoneInfo> phoneInfosArrayList = new ArrayList<PhoneInfo>();

	private ArrayList<List_Bean> list_Beans = new ArrayList<List_Bean>();
	private finshBroadCast mfinshBroadCast;
    private ArrayList<PhoneInfo> phoneBenbenList = new ArrayList<PhoneInfo>();
    private int benPosition=0;
    private LinearLayout ll_change;

    private LinearLayout ll_ztc,ll_friend_union;
    private CubeImageView iv_ztc,iv_friend_union;
    private TextView tv_short_name,tv_tag,tv_friend_union_name,tv_friend_union_area,tv_friend_union_type;
    private String train_id="";
    private String legid ="";
    private boolean isFriend = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mfinshBroadCast = new finshBroadCast();
		registerReceiver(mfinshBroadCast, new IntentFilter(AndroidConfig.Finsh));
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_contacts_info);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("联系人详情", "", "编辑",
				R.drawable.icon_com_title_left, 0);

		contacts_poster = (CubeImageView) findViewById(R.id.contacts_poster);
        tv_nick_name = (TextView) findViewById(R.id.tv_nick_name);
		contacts_name = (TextView) findViewById(R.id.contacts_name);
		contacts_group_name = (TextView) findViewById(R.id.contacts_group_name);
		contacts_benben = (TextView) findViewById(R.id.contacts_benben);
		contacts_add_benben = (LinearLayout) findViewById(R.id.contacts_add_benben);
        send_msg_or_add_friend = (MyTextView) findViewById(R.id.send_msg_or_add_friend);
		listview = (ListView) findViewById(R.id.listview);
		send_message = (LinearLayout) findViewById(R.id.send_message);
		contacts_poster.setOnClickListener(this);
        ll_change = (LinearLayout) findViewById(R.id.ll_change);
        ll_change.setOnClickListener(this);

        ll_ztc = (LinearLayout) findViewById(R.id.ll_ztc);
        ll_ztc.setOnClickListener(this);
        ll_friend_union = (LinearLayout) findViewById(R.id.ll_friend_union);
        ll_friend_union.setOnClickListener(this);
        iv_ztc = (CubeImageView) findViewById(R.id.iv_ztc);
        iv_friend_union = (CubeImageView) findViewById(R.id.iv_friend_union);
        tv_short_name = (TextView) findViewById(R.id.tv_short_name);
        tv_tag = (TextView) findViewById(R.id.tv_tag);
        tv_friend_union_name = (TextView) findViewById(R.id.tv_friend_union_name);
        tv_friend_union_area = (TextView) findViewById(R.id.tv_friend_union_area);
        tv_friend_union_type = (TextView) findViewById(R.id.tv_friend_union_type);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		mContacts = (Contacts) getIntent().getSerializableExtra("contacts");
        if(mContacts==null){
            String id = getIntent().getStringExtra("id");
            String username = getIntent().getStringExtra("username");
            getIntent().putExtra("username", "");
            setShowLoding(false);
            if (CommonUtils.isNetworkAvailable(mContext)) {
                showLoding("请稍后...");
                InteNetUtils.getInstance(mContext).getContactInfoFromQR(id,
                        username, mRequestCallBack);
            }
        }else{
            getData();
        }


	}

	private void getData() {
        contacts_name.setText(mContacts.getName());
        if (user.getHuanxin_username().equals(mContacts.getHuanxin_username())) {
            send_message.setVisibility(View.GONE);
        } else {
            send_message.setVisibility(View.VISIBLE);
        }
        if ("0".equals(mContacts.getIs_friend())) {
            isFriend = false;
            listview.setVisibility(View.INVISIBLE);
            send_msg_or_add_friend.setText("加好友");
            initTitle_Right_Left_bar("联系人详情", "", "",
                    R.drawable.icon_com_title_left, 0);
            contacts_add_benben.setVisibility(View.GONE);
            ll_change.setVisibility(View.GONE);
            ll_ztc.setVisibility(View.GONE);
            ll_friend_union.setVisibility(View.GONE);
            CommonUtils.startImageLoader(cubeimageLoader, mContacts.getPoster(),
                    contacts_poster);
            tv_nick_name.setText("昵称：" + mContacts.getNick_name());
            contacts_benben.setText("奔犇号：" + mContacts.getIs_benben());
            List<PhoneInfo> phonelists = null;
            try {
                if (!TextUtils.isEmpty(mContacts.getGroup_id())) {
                    ContactsGroup group = dbUtil.findById(ContactsGroup.class,
                            mContacts.getGroup_id());

                    contacts_group_name.setText(group.getName());
                    contacts_group_name.setVisibility(View.VISIBLE);
                } else {
                    contacts_group_name.setVisibility(View.GONE);
                }

                // 获得联系人下的 phoneInfo
                phonelists = dbUtil.findAll(Selector.from(PhoneInfo.class).where(
                        "contacts_id", "=", mContacts.getId()));

            } catch (DbException e) {
                e.printStackTrace();
            }
            phoneBenbenList.clear();
            if (phonelists != null && phonelists.size() > 0) {
                int i = 0;
                for (PhoneInfo phoneInfo : phonelists) {
                    if (!phoneInfo.getIs_benben().equals("") && !phoneInfo.getIs_benben().equals("0")) {
                        phoneBenbenList.add(phoneInfo);
                        if (phoneInfo.getIs_active().equals("1")) {
                            benPosition = i;
                        }
                        i++;
                    }
                }
            }

            if (phoneBenbenList != null && phoneBenbenList.size() != 0) {
                contacts_benben.setVisibility(View.VISIBLE);
                if (phoneBenbenList.size() > 1) {
                    ll_change.setVisibility(View.VISIBLE);
                } else {
                    ll_change.setVisibility(View.GONE);
                }

                CommonUtils.startImageLoader(cubeimageLoader, phoneBenbenList.get(benPosition).getPoster(),
                        contacts_poster);
//                tv_nick_name.setText("昵称：" + phoneBenbenList.get(benPosition).getNick_name());
//                contacts_benben.setText("奔犇号：" + phoneBenbenList.get(benPosition).getIs_benben());

                train_id = phoneBenbenList.get(benPosition).getTrain_id();
                if (!train_id.equals("") && !train_id.equals("0")) {
                    ll_ztc.setVisibility(View.VISIBLE);
                    CommonUtils.startImageLoader(cubeimageLoader, phoneBenbenList.get(benPosition).getPic(),
                            iv_ztc);

                    tv_short_name.setText(phoneBenbenList.get(benPosition).getShort_name());
                    tv_tag.setText(phoneBenbenList.get(benPosition).getTag());
                } else {
                    ll_ztc.setVisibility(View.GONE);
                }
                legid = phoneBenbenList.get(benPosition).getLegid();
                if (legid != null && !legid.equals("")) {
                    ll_friend_union.setVisibility(View.VISIBLE);
                    CommonUtils.startImageLoader(cubeimageLoader, phoneBenbenList.get(benPosition).getLeg_poster(),
                            iv_friend_union);
                    tv_friend_union_name.setText(phoneBenbenList.get(benPosition).getLeg_name());
                    String type = phoneBenbenList.get(benPosition).getType();
                    if(type.equals("英雄联盟")){
                        tv_friend_union_type.setText("英雄");
                        tv_friend_union_type.setTextColor(Color.rgb(33, 207, 213));
                        tv_friend_union_type.setBackgroundResource(R.drawable.textview_friend_union_2);
                    }else if(type.equals("工作联盟")){
                        tv_friend_union_type.setVisibility(View.VISIBLE);
                        tv_friend_union_type.setText("工作");
                        tv_friend_union_type.setTextColor(Color.rgb(233,81,135));
                        tv_friend_union_type.setBackgroundResource(R.drawable.textview_friend_union_1);
                    }
//                    tv_friend_union_type.setText(phoneBenbenList.get(benPosition).getType());

                    tv_friend_union_area.setText(phoneBenbenList.get(benPosition).getLeg_district());
                } else {
                    ll_friend_union.setVisibility(View.GONE);
                }

            }

        } else {
            isFriend = true;
            send_msg_or_add_friend.setText("发消息");
            initTitle_Right_Left_bar("联系人详情", "", "编辑",
                    R.drawable.icon_com_title_left, 0);
            isFriend = true;
            train_id = "0";
            ContactsGroup grouop = null;
            List<PhoneInfo> phones = null;
            try {
                mContacts = dbUtil.findById(Contacts.class,
                        mContacts.getId());
                // 获得组名
                grouop = dbUtil.findById(ContactsGroup.class,
                        mContacts.getGroup_id());
                // 获得联系人下的 phoneInfo
                phones = dbUtil.findAll(Selector.from(PhoneInfo.class).where(
                        "contacts_id", "=", mContacts.getId()));
                phoneInfosArrayList = (ArrayList<PhoneInfo>) phones;
            } catch (DbException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            final StringBuffer phoneNums = new StringBuffer();
            list_Beans.clear();
            phoneBenbenList.clear();
            benPosition = 0;
            if (phones != null && phones.size() > 0) {
                int i = 0;
                for (PhoneInfo phoneInfo : phones) {
                    if (!phoneInfo.getIs_benben().equals("") && !phoneInfo.getIs_benben().equals("0")) {
                        phoneBenbenList.add(phoneInfo);
                        if (phoneInfo.getIs_active().equals("1")) {
                            benPosition = i;
                        }
                        i++;
                    }

                    if (!phoneInfo.getPhone().equals("")) {
                        if ("0".equals(phoneInfo.getIs_baixing())) {
                            List_Bean list_Bean = new List_Bean(phoneInfo.getPhone(),
                                    false, false);
                            list_Beans.add(list_Bean);
                        } else {
                            List_Bean list_Bean = new List_Bean(phoneInfo.getPhone(),
                                    true, false);
                            List_Bean list_Bean2 = new List_Bean(
                                    phoneInfo.getIs_baixing(), true, true);
                            list_Beans.add(list_Bean);
                            list_Beans.add(list_Bean2);
                        }
                        phoneNums.append(phoneInfo.getPhone() + ";");
                    }
                }
            }
//		CommonUtils.startImageLoader(cubeimageLoader, mContacts.getPoster(),
//				contacts_poster);
            contacts_group_name.setText(grouop.getName());
            if (phoneBenbenList != null && phoneBenbenList.size() != 0) {
                contacts_benben.setVisibility(View.VISIBLE);
                if (phoneBenbenList.size() > 1) {
                    ll_change.setVisibility(View.VISIBLE);
                } else {
                    ll_change.setVisibility(View.GONE);
                }

                CommonUtils.startImageLoader(cubeimageLoader, phoneBenbenList.get(benPosition).getPoster(),
                        contacts_poster);
                tv_nick_name.setText("昵称：" + phoneBenbenList.get(benPosition).getNick_name());
                send_message.setVisibility(View.VISIBLE);
                contacts_add_benben.setVisibility(View.GONE);
                contacts_benben.setText("奔犇号：" + phoneBenbenList.get(benPosition).getIs_benben());

                train_id = phoneBenbenList.get(benPosition).getTrain_id();
                if (!train_id.equals("") && !train_id.equals("0")) {
                    ll_ztc.setVisibility(View.VISIBLE);
                    CommonUtils.startImageLoader(cubeimageLoader, phoneBenbenList.get(benPosition).getPic(),
                            iv_ztc);

                    tv_short_name.setText(phoneBenbenList.get(benPosition).getShort_name());
                    tv_tag.setText(phoneBenbenList.get(benPosition).getTag());
                } else {
                    ll_ztc.setVisibility(View.GONE);
                }
                legid = phoneBenbenList.get(benPosition).getLegid();
                if (legid != null && !legid.equals("")) {
                    ll_friend_union.setVisibility(View.VISIBLE);
                    CommonUtils.startImageLoader(cubeimageLoader, phoneBenbenList.get(benPosition).getLeg_poster(),
                            iv_friend_union);
                    tv_friend_union_name.setText(phoneBenbenList.get(benPosition).getLeg_name());
                    String type = phoneBenbenList.get(benPosition).getType();
                    if(type.equals("英雄联盟")){
                        tv_friend_union_type.setText("英雄");
                        tv_friend_union_type.setTextColor(Color.rgb(33, 207, 213));
                        tv_friend_union_type.setBackgroundResource(R.drawable.textview_friend_union_2);
                    }else if(type.equals("工作联盟")){
                        tv_friend_union_type.setVisibility(View.VISIBLE);
                        tv_friend_union_type.setText("工作");
                        tv_friend_union_type.setTextColor(Color.rgb(233,81,135));
                        tv_friend_union_type.setBackgroundResource(R.drawable.textview_friend_union_1);
                    }
//                    tv_friend_union_type.setText(phoneBenbenList.get(benPosition).getType());

                    tv_friend_union_area.setText(phoneBenbenList.get(benPosition).getLeg_district());
                } else {
                    ll_friend_union.setVisibility(View.GONE);
                }

            } else {
                ll_change.setVisibility(View.GONE);
                ll_ztc.setVisibility(View.GONE);
                ll_friend_union.setVisibility(View.GONE);
                CommonUtils.startImageLoader(cubeimageLoader, mContacts.getPoster(),
                        contacts_poster);
                tv_nick_name.setText("昵称：" + mContacts.getNick_name());
                if ("0".equals(mContacts.getIs_benben())) {
                    contacts_benben.setVisibility(View.GONE);
                    send_message.setVisibility(View.GONE);
                    contacts_add_benben.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            String[] phs = new String[phoneInfosArrayList.size()];
                            for (int i = 0; i < phs.length; i++) {
                                phs[i] = phoneInfosArrayList.get(i).getPhone();
                            }
                            showActionSheet(phs);
                        }
                    });

                } else {
                    send_message.setVisibility(View.VISIBLE);
                    contacts_add_benben.setVisibility(View.GONE);
                    contacts_benben.setText("奔犇号：" + mContacts.getIs_benben());
                }
            }
        }

		MyAdapter myAdapter = new MyAdapter();
		listview.setAdapter(myAdapter);
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(this);
		setOnRightClickLinester(this);

		contacts_add_benben.setOnClickListener(this);
		send_message.setOnClickListener(this);
	}

	@Override
	protected void onHttpStart() {

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
        Log.d("ltf","jsonObject============="+jsonObject);
        dissLoding();
        mContacts = new Contacts();
        try {
            mContacts.parseJSONSingle4(jsonObject);
            dbUtil.saveOrUpdate(mContacts);

            dbUtil.delete(PhoneInfo.class,WhereBuilder.b("contacts_id", "=", mContacts.getId()));
            if(mContacts.getPhones()!=null && mContacts.getPhones().size()>0) {
                dbUtil.saveOrUpdateAll(mContacts.getPhones());
            }

        } catch (NetRequestException e) {
            e.printStackTrace();
        } catch (DbException e) {
            e.printStackTrace();
        }
        getData();

    }

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		// TODO Auto-generated method stub
        dissLoding();
        ToastUtils.Infotoast(mContext, "网络不可用");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
            // 头部左侧点击
            case R.id.com_title_bar_left_bt:
            case R.id.com_title_bar_left_tv:
                AnimFinsh();
                break;
            // 右侧 编辑
            case R.id.com_title_bar_right_bt:
            case R.id.com_title_bar_right_tv:

                startAnimActivityForResult(ActivityContactsEditInfoContent.class,
                        "contacts", mContacts, AndroidConfig.writeFriendRequestCode);

                break;
            // 发送消息
            case R.id.send_message:
                if (isFriend) {
                    // 进入聊天页面
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    if(phoneBenbenList!=null && phoneBenbenList.size()>0){
                        intent.putExtra("userId", phoneBenbenList.get(benPosition).getHuanxin_username());
                    }else{
                        intent.putExtra("userId", mContacts.getHuanxin_username());
                    }
                    intent.putExtra("contacts", mContacts);
                    startActivityForResult(intent, AndroidConfig.writeFriendRequestCode);
                    addAnim();
                } else {
                    Intent intent = new Intent(mContext, ActivityAddFriendDetail.class);
                    mContext.overridePendingTransition(R.anim.in_from_right,
                            R.anim.out_to_left);
                    intent.putExtra("nick_name", mContacts.getNick_name());
                    intent.putExtra("from_huanxin", user.getHuanxin_username());
                    intent.putExtra("to_huanxin", mContacts.getHuanxin_username());
                    startActivityForResult(intent, AndroidConfig.writeFriendRequestCode);
//                    try {
//                        EMContactManager.getInstance().addContact(mContacts.getHuanxin_username(),
//                                "加个好友呗!");
//
//                        final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(
//                                mContext, R.style.MyDialog1);
//                        hint.show();
//                        hint.setOKListener(new OnClickListener() {
//
//                            @Override
//                            public void onClick(View v) {
//                                hint.dismiss();
//                            }
//                        });
//
//                    } catch (EaseMobException e) {
//                        ToastUtils.Errortoast(mContext, "添加好友失败");
//                        e.printStackTrace();
//                    }// 需异步处理
                }

                break;
            case R.id.contacts_poster:

                if (phoneBenbenList != null && phoneBenbenList.size() != 0) {
                    CommonUtils.showPoster(mContext, phoneBenbenList.get(benPosition).getPoster(),
                            cubeimageLoader);
                }else {
                    CommonUtils.showPoster(mContext, mContacts.getPoster(),
                            cubeimageLoader);
                }
                break;
            case R.id.ll_change:
                this.showLoding("切换中...");
                ll_change.setClickable(false);
                benPosition++;
                if(benPosition >= phoneBenbenList.size()){
                    benPosition=0;
                }
                final PhoneInfo phoneInfo = phoneBenbenList.get(benPosition);
                InteNetUtils.getInstance(mContext).setActive(phoneInfo.getPid(),
                    user.getToken(),phoneInfo.getContacts_id(), new RequestCallBack<String>() {

                        @Override
                        public void onFailure(HttpException arg0, String arg1) {
                            ActivityContactsInfo.this.dissLoding();
                            ToastUtils.Errortoast(mContext, "切换失败");
                            ll_change.setClickable(true);
                        }

                        @Override
                        public void onSuccess(ResponseInfo<String> arg0) {
                            try {
                                JSONObject jsonObject = new JSONObject(arg0.result);
                                phoneInfo.setIs_active("0");
                                dbUtil.update(phoneInfo,
                                    WhereBuilder.b("is_active","=","1"),
                                    "is_active");
                                phoneInfo.setIs_active("1");
                                dbUtil.update(phoneInfo,
                                        WhereBuilder.b("pid","=",phoneInfo.getPid()),"is_active");
                                ll_change.setClickable(true);
                                ActivityContactsInfo.this.dissLoding();
                                getData();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (DbException e) {
                                e.printStackTrace();
                            }


                        }
                    });


                break;
            case R.id.ll_ztc:
                String hxName = "";
                if(phoneBenbenList!=null && phoneBenbenList.size()>0){
                    hxName= phoneBenbenList.get(benPosition).getHuanxin_username();
                }else{
                    hxName = mContacts.getHuanxin_username();
                }

                if(hxName.equals(user.getHuanxin_username())){
                    startAnimActivity3Obj(ActivityMyNumberTrainDetail.class,
                            "id", train_id, "kil", "");
                }else {
                    startAnimActivity3Obj(ActivityNumberTrainDetail.class,
                            "id", train_id, "kil", "");
                }

                break;
            case R.id.ll_friend_union:
                Intent intentUnion = new Intent(mContext, ActivityContactsUnionInfo.class);
                mContext.overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
                intentUnion.putExtra("legid", legid);
                startActivityForResult(intentUnion, AndroidConfig.writeFriendRequestCode);

                break;

		default:
			break;
		}

	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list_Beans.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View converView, ViewGroup parents) {
            if (converView == null) {
                converView = LayoutInflater.from(mContext).inflate(
                        R.layout.activity_contacts_info_item, null);
            }
            List_Bean list_Bean = list_Beans.get(position);
            final String phone = list_Bean.getPone();
            if (list_Beans.get(position).isShort()) {
                ((TextView) converView.findViewById(R.id.item_phone_name))
                        .setTextColor(Color.parseColor("#5f9bbe"));
            } else {
                ((TextView) converView.findViewById(R.id.item_phone_name))
                        .setTextColor(Color.parseColor("#000000"));
            }

            ((TextView) converView.findViewById(R.id.item_phone_name))
                    .setText(phone);
            TextView item_baixing = (TextView) converView
                    .findViewById(R.id.item_baixing);

            if (!list_Bean.isBaiXing()) {
                item_baixing.setVisibility(View.VISIBLE);
                item_baixing.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if ((Integer.parseInt(mContext.user.getUserInfo()) & 2) > 0) {
                            // 进入邀请百姓网
                            ArrayList<BxContacts> groupListContacts = new ArrayList<BxContacts>();
                            BxContacts bxContacts = new BxContacts();
                            bxContacts.setPhone(phone);
                            bxContacts.setName(mContacts.getName());
                            bxContacts.setPinyin(mContacts.getPinyin());
                            bxContacts.setId(mContacts.getId());
                            groupListContacts.add(bxContacts);
                            startAnimActivity4Obj(
                                    ActivityInviteFriendToBx.class, "contacts",
                                    groupListContacts);
                        } else {
                            ToastUtils.Infotoast(mContext, "请先申请加入百姓网");
                        }

                    }
                });
            } else {
                item_baixing.setVisibility(View.GONE);
            }

            item_baixing.setVisibility(View.GONE);

            converView.findViewById(R.id.item_phone_single_sms)
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            PhoneUtils.sendSMS(phone, "", mContext);
                        }
                    });

            converView.findViewById(R.id.item_phone_single_call)
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            PhoneUtils.makeCall(mContacts.getId(),mContacts.getName(), phone,
                                    mContext);
                        }
                    });
            converView.findViewById(R.id.item_phone_name)
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            PhoneUtils.makeCall(mContacts.getId(),mContacts.getName(), phone,
                                    mContext);
                        }
                    });
			return converView;
		}
	}

	class List_Bean {

		public List_Bean(String phone, boolean addBaixing, boolean isShort) {
			this.phone = phone;
			this.addBaixing = addBaixing;
			this.isShort = isShort;
		}

		String phone;
		boolean addBaixing;
		boolean isShort;

		public String getPone() {
			return phone;
		}

		public boolean isBaiXing() {
			return addBaixing;
		}

		public boolean isShort() {
			return isShort;
		}

	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {

		if (arg1 == AndroidConfig.writeFriendRefreshResultCode) {
			try {
				Contacts mc = dbUtil.findFirst(Selector.from(Contacts.class)
						.where(WhereBuilder.b("id", "=", mContacts.getId())));
				if (mc != null) {
					mContacts = mc;
					getData();
					sendBroadcast(new Intent(AndroidConfig.refreshPackageInfo));
				}
				// else {
				// new Handler().postDelayed(new Runnable() {
				//
				// @Override
				// public void run() {
				// setResult(AndroidConfig.ContactsFragmentResultCode);
				// AnimFinsh();
				//
				// }
				// }, 300);
				//
				// }
			} catch (DbException e) {
				e.printStackTrace();
			}

		}
	}

	public void showActionSheet(final String[] phone) {
		setTheme(R.style.ActionSheetStyleIOS7);
		ActionSheet.createBuilder(this, getSupportFragmentManager())
				.setCancelButtonTitle("取消")
				.setOtherButtonTitles(phone)
				// 设置颜色 必须一一对应
				.setOtherButtonTitlesColor("#1E82FF", "#1E82FF")
				.setCancelableOnTouchOutside(true)
				.setListener(new ActionSheetListener() {

					@Override
					public void onOtherButtonClick(ActionSheet actionSheet,
							int index) {
						PhoneUtils.sendSMS(phone[index],
								AndroidConfig.smsContext, mContext);
					}

					@Override
					public void onDismiss(ActionSheet actionSheet,
							boolean isCancel) {
					}
				}).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mfinshBroadCast);
	}

	class finshBroadCast extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int type = intent.getIntExtra("type", 0);
			if (type == 100) {
				mContext.AnimFinsh();
			} else {
				mContacts = (Contacts) intent.getSerializableExtra("contacts");
				getData();
				sendBroadcast(new Intent(AndroidConfig.refreshPackageInfo));
			}

		}

	}

}
