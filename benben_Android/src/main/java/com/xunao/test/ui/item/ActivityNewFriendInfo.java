package com.xunao.test.ui.item;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.test.R;
import com.xunao.test.base.*;
import com.xunao.test.bean.PublicMessage;
import com.xunao.test.config.AndroidConfig;
import com.xunao.test.net.InteNetUtils;
import com.xunao.test.utils.CommonUtils;
import com.xunao.test.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import in.srain.cube.image.CubeImageView;

/**
 * Created by ltf on 2015/11/5.
 */
public class ActivityNewFriendInfo extends BaseActivity implements View.OnClickListener {
    private CubeImageView contacts_poster;
    private TextView tv_contacts_name,tv_contacts_benben,tv_contacts_message;
    private TextView tv_reason;
    private LinearLayout ll_ztc,ll_friend_union;
    private CubeImageView iv_ztc,iv_friend_union;
    private TextView tv_short_name,tv_tag,tv_friend_union_name,tv_friend_union_area;
    private LinearLayout ll_result_select,ll_result;
    private TextView tv_refuse,tv_agree,tv_state;
    private String train_id="";
    private String legid="";
    private PublicMessage publicMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_new_friend_info);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("申请与通知", "", "",
                R.drawable.icon_com_title_left, 0);
        contacts_poster = (CubeImageView) findViewById(R.id.contacts_poster);
        tv_contacts_name = (TextView) findViewById(R.id.tv_contacts_name);
        tv_contacts_benben = (TextView) findViewById(R.id.tv_contacts_benben);
        tv_contacts_message = (TextView) findViewById(R.id.tv_contacts_message);
        tv_reason = (TextView) findViewById(R.id.tv_reason);
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
        ll_result_select = (LinearLayout) findViewById(R.id.ll_result_select);
        ll_result = (LinearLayout) findViewById(R.id.ll_result);
        tv_refuse = (TextView) findViewById(R.id.tv_refuse);
        tv_refuse.setOnClickListener(this);
        tv_agree = (TextView) findViewById(R.id.tv_agree);
        tv_agree.setOnClickListener(this);
        tv_state = (TextView) findViewById(R.id.tv_state);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        publicMessage = (PublicMessage) getIntent().getSerializableExtra("publicMessage");
        InteNetUtils.getInstance(mContext).newFriendInfo(publicMessage.getSid(),user.getToken(),mRequestCallBack);
    }

    @Override
    public void initLinstener(Bundle savedInstanceState) {
        setOnLeftClickLinester(this);
    }

    @Override
    protected void onHttpStart() {

    }

    @Override
    protected void onLoading(long count, long current, boolean isUploading) {

    }

    @Override
    protected void onSuccess(JSONObject jsonObject) {
        if (jsonObject.optString("ret_num").equals("0")) {
            CommonUtils.startImageLoader(cubeimageLoader, publicMessage.getPoster(),contacts_poster);
            tv_contacts_name.setText(jsonObject.optString("nick_name"));
            tv_contacts_benben.setText("奔犇号："+jsonObject.optString("benben_id"));
            String sex="";
            if(jsonObject.optInt("sex")==1){
                sex = "男 ";
            }else{
                sex = "女 ";
            }
            tv_contacts_message.setText(sex+jsonObject.optString("age")+"岁 "+jsonObject.optString("district"));
            tv_reason.setText(publicMessage.getReason());
            train_id=jsonObject.optString("train_id");
            if (train_id!=null && !train_id.equals("") && !train_id.equals("0")&& !train_id.equals("null")) {
                ll_ztc.setVisibility(View.VISIBLE);
                CommonUtils.startImageLoader(cubeimageLoader, jsonObject.optString("pic"), iv_ztc);
                tv_short_name.setText(jsonObject.optString("short_name"));
                tv_tag.setText(jsonObject.optString("tag"));
            } else {
                ll_ztc.setVisibility(View.GONE);
            }

            legid = jsonObject.optString("legid");
            if (legid != null && !legid.equals("")&& !legid.equals("0")&& !legid.equals("null")) {
                ll_friend_union.setVisibility(View.VISIBLE);
                CommonUtils.startImageLoader(cubeimageLoader, jsonObject.optString("leg_poster"),
                        iv_friend_union);
                tv_friend_union_name.setText(jsonObject.optString("leg_name"));
                tv_friend_union_area.setText(jsonObject.optString("leg_district"));
            } else {
                ll_friend_union.setVisibility(View.GONE);
            }
            switch (publicMessage.getStatus()){
                case PublicMessage.UNAGREE:
                    ll_result_select.setVisibility(View.VISIBLE);
                    ll_result.setVisibility(View.GONE);
                    break;
                case PublicMessage.AGREE:
                    ll_result_select.setVisibility(View.GONE);
                    tv_state.setText("已接受");
                    ll_result.setVisibility(View.VISIBLE);
                    break;
                case PublicMessage.REFUSE:
                    ll_result_select.setVisibility(View.GONE);
                    tv_state.setText("已拒绝");
                    ll_result.setVisibility(View.VISIBLE);
                    break;
            }

        }else{
            ToastUtils.Errortoast(this,"获取信息失败");
        }
    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        ToastUtils.Errortoast(this,"获取信息失败");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            // 头部左侧点击
            case R.id.com_title_bar_left_bt:
            case R.id.com_title_bar_left_tv:
                AnimFinsh();
                break;
            case R.id.ll_ztc:
                startAnimActivity3Obj(ActivityNumberTrainDetail.class,
                        "id", train_id, "kil", "");
                break;
            case R.id.tv_refuse:
                if (CommonUtils.isNetworkAvailable(mContext)) {
                    showLoding("正在处理...");
                    InteNetUtils.getInstance(mContext).rejectFriend(
                            publicMessage.getHuanxin_username(),user.getToken(),
                            new RequestCallBack<String>() {
                                @Override
                                public void onSuccess(ResponseInfo<String> arg0) {
                                    dissLoding();
                                    try {
                                        JSONObject jsonObject = new JSONObject(
                                                arg0.result);
                                        if (jsonObject.optString("ret_num").equals("0")) {
                                            try {
                                                EMChatManager
                                                        .getInstance()
                                                        .refuseInvitation(
                                                                publicMessage.getHuanxin_username());
                                            } catch (EaseMobException e) {
                                                // TODO Auto-generated
                                                // catch
                                                // block
                                                e.printStackTrace();
                                            }
                                            publicMessage.setStatus(PublicMessage.REFUSE);
                                            dbUtil.saveOrUpdate(publicMessage);
                                            setResult(AndroidConfig.writeFriendResultCode);
                                            sendBroadcast(new Intent(
                                                    AndroidConfig.ContactsRefresh));
                                            AnimFinsh();

                                        }else{
                                            ToastUtils.Errortoast(mContext,"操作失败");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        ToastUtils.Errortoast(mContext,"操作失败");
                                    } catch (DbException e) {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void onFailure(HttpException e, String s) {
                                    dissLoding();
                                    ToastUtils.Errortoast(mContext,"操作失败");
                                }
                            }
                    );


                }else{
                    ToastUtils.Errortoast(this, "当前网络不可用");
                }


                break;
            case R.id.tv_agree:
                // 同意加好友
                Intent intentFriend = new Intent(mContext, ActivityAddNewHxFriend.class);
                mContext.overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
                intentFriend.putExtra("publicMessage", publicMessage);
                startActivityForResult(intentFriend, AndroidConfig.writeFriendRequestCode);

//                if (CommonUtils.isNetworkAvailable(mContext)) {
//                    showLoding("正在处理...");
//                    InteNetUtils.getInstance(mContext).addFirend(
//                            publicMessage.getHuanxin_username(),
//                            new RequestCallBack<String>() {
//
//                                @Override
//                                public void onSuccess(
//                                        ResponseInfo<String> arg0) {
//                                    dissLoding();
//                                    try {
//                                        JSONObject jsonObject = new JSONObject(
//                                                arg0.result);
//
//                                        Contacts contacts = new Contacts();
//                                        contacts.parseJSONSingle2(jsonObject);
//
//                                        dbUtil.saveOrUpdate(contacts);
//                                        ArrayList<PhoneInfo> phones = contacts
//                                                .getPhones();
//                                        if (phones != null)
//                                            dbUtil.saveOrUpdateAll(phones);
//                                        publicMessage.setStatus(PublicMessage.AGREE);
//                                        dbUtil.saveOrUpdate(publicMessage);
//
//                                        try {
//                                            EMChatManager
//                                                    .getInstance()
//                                                    .refuseInvitation(
//                                                            publicMessage.getHuanxin_username());
//                                        } catch (EaseMobException e) {
//                                            // TODO Auto-generated
//                                            // catch
//                                            // block
//                                            e.printStackTrace();
//                                        }
//
//                                        // 获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
//                                        EMConversation conversation = EMChatManager
//                                                .getInstance()
//                                                .getConversation(
//                                                        contacts.getHuanxin_username());
//                                        // 创建一条文本消息
//                                        final EMMessage message = EMMessage
//                                                .createSendMessage(EMMessage.Type.TXT);
//                                        // 如果是群聊，设置chattype,默认是单聊
//                                        // 设置消息body
//                                        TextMessageBody txtBody = new TextMessageBody(
//                                                "我们已是好友,开始聊天吧");
//
//                                        message.setAttribute(
//                                                "friend_contact_new_id",
//                                                jsonObject
//                                                        .optString("targetContactID"));
//                                        message.addBody(txtBody);
//                                        // 设置接收人
//                                        message.setReceipt(contacts
//                                                .getHuanxin_username());
//                                        // 把消息加入到此会话对象中
//                                        conversation
//                                                .addMessage(message);
//                                        // 发送消息
//
//                                        new Handler().postDelayed(
//                                                new Runnable() {
//
//                                                    @Override
//                                                    public void run() {
//                                                        try {
//                                                            EMChatManager
//                                                                    .getInstance()
//                                                                    .sendMessage(
//                                                                            message);
//                                                        } catch (EaseMobException e) {
//                                                            e.printStackTrace();
//                                                        }
//                                                    }
//                                                }, 1000);
//                                        setResult(AndroidConfig.writeFriendResultCode);
//                                        sendBroadcast(new Intent(
//                                                AndroidConfig.ContactsRefresh));
//                                        AnimFinsh();
//                                        return;
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    } catch (NetRequestException e) {
//                                        com.xunao.benben.base.Error error = e.getError();
//                                        error.print(mContext);
//                                        if (error.getErrorId() == 5218
//                                                || error.getErrorId() == 5208) {
//
//                                            try {
//                                                EMChatManager
//                                                        .getInstance()
//                                                        .refuseInvitation(
//                                                                publicMessage.getHuanxin_username());
//                                            } catch (EaseMobException e1) {
//                                                // TODO
//                                                // Auto-generated
//                                                // catch
//                                                // block
//                                                e.printStackTrace();
//                                            }
//
//                                            publicMessage.setStatus(PublicMessage.AGREE);
//
//                                            try {
//                                                dbUtil.saveOrUpdate(publicMessage);
//                                            } catch (DbException e1) {
//                                                // TODO
//                                                // Auto-generated
//                                                // catch block
//                                                e1.printStackTrace();
//                                            }
//
//                                            // 获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
//                                            EMConversation conversation = EMChatManager
//                                                    .getInstance()
//                                                    .getConversation(
//                                                            publicMessage.getHuanxin_username());
//                                            // 创建一条文本消息
//                                            final EMMessage message = EMMessage
//                                                    .createSendMessage(EMMessage.Type.TXT);
//                                            // 如果是群聊，设置chattype,默认是单聊
//                                            // 设置消息body
//                                            TextMessageBody txtBody = new TextMessageBody(
//                                                    "我们已是好友,开始聊天吧");
//                                            message.addBody(txtBody);
//
//                                            // 设置接收人
//                                            message.setReceipt(publicMessage
//                                                    .getHuanxin_username());
//
//                                            // 把消息加入到此会话对象中
//                                            conversation
//                                                    .addMessage(message);
//                                            // 发送消息
//
//                                            new Handler()
//                                                    .postDelayed(
//                                                            new Runnable() {
//
//                                                                @Override
//                                                                public void run() {
//                                                                    try {
//                                                                        EMChatManager
//                                                                                .getInstance()
//                                                                                .sendMessage(
//                                                                                        message);
//                                                                    } catch (EaseMobException e) {
//                                                                        e.printStackTrace();
//                                                                    }
//                                                                }
//                                                            }, 1000);
//                                            setResult(AndroidConfig.writeFriendResultCode);
//                                            sendBroadcast(new Intent(
//                                                    AndroidConfig.ContactsRefresh));
//                                            AnimFinsh();
//                                            return;
//                                        }
//                                        e.printStackTrace();
//                                    } catch (DbException e) {
//                                        // TODO Auto-generated catch
//                                        // block
//                                        e.printStackTrace();
//                                    }
//                                    ToastUtils.Errortoast(mContext,
//                                            "同意添加失败");
//                                }
//
//                                @Override
//                                public void onFailure(
//                                        HttpException arg0,
//                                        String arg1) {
//                                    dissLoding();
//                                    ToastUtils.Errortoast(mContext,
//                                            "同意添加失败");
//                                }
//                            });
//                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==AndroidConfig.writeFriendResultCode){
            setResult(AndroidConfig.writeFriendResultCode);
            sendBroadcast(new Intent(
                    AndroidConfig.ContactsRefresh));
            AnimFinsh();
        }
    }
}
