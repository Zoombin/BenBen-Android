package com.xunao.benben.ui.item;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.PhoneInfo;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityLogin;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.RegexUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityFriendUnionInviteMemberBySelf extends BaseActivity {
	protected static final int CHOICE_GROUP = 1000;
	private EditText et_phone;
	private EditText et_benben;
    private String friendUnionId;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_friend_union_invite_byself);
		initTitle_Right_Left_bar("添加成员", "", "添加",
				R.drawable.icon_com_title_left, 0);

		et_phone = (EditText) findViewById(R.id.et_phone);
        et_benben = (EditText) findViewById(R.id.et_benben);

	}

	@Override
	public void initView(Bundle savedInstanceState) {

	}

	@Override
	public void initDate(Bundle savedInstanceState) {
        friendUnionId = getIntent().getStringExtra("friendUnionId");
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AnimFinsh();
			}
		});

		setOnRightClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String phone = et_phone.getText().toString().trim();
				String benben = et_benben.getText().toString().trim();
				if (TextUtils.isEmpty(phone) || !RegexUtils.checkNum(phone)) {
					ToastUtils.Errortoast(mContext, "手机号限制在3-16位!");
					return;
				}

				if (TextUtils.isEmpty(benben)) {
					ToastUtils.Errortoast(mContext, "请输入奔犇号");
					return;
				}

				if (CommonUtils.isNetworkAvailable(mContext)) {
                    InteNetUtils.getInstance(mContext)
                            .inviteFriendUnionMemberMySelf(
                                    friendUnionId,benben,"0",phone,"1",user.getToken(),
                                    new RequestCallBack<String>() {

                                        @Override
                                        public void onSuccess(
                                                ResponseInfo<String> arg0) {
                                            JSONObject jsonObject;
                                            try {
                                                jsonObject = new JSONObject(
                                                        arg0.result);
                                                String ret_num = jsonObject
                                                        .optString("ret_num");
                                                if (ret_num.equals("0")) {
                                                    ToastUtils.Infotoast(
                                                            mContext,
                                                            "添加成功!");
                                                    setResult(RESULT_OK,null);

                                                    sendBroadcast(new Intent(
                                                            AndroidConfig.refrashFriendUnionMember));
                                                    sendBroadcast(new Intent(
                                                            AndroidConfig.refreshFriendUnion));
                                                    AnimFinsh();

                                                } else {
                                                    ToastUtils
                                                            .Infotoast(
                                                                    mContext,
                                                                    jsonObject
                                                                            .optString("ret_msg"));
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onFailure(
                                                HttpException arg0,
                                                String arg1) {
                                            ToastUtils.Infotoast(mContext,
                                                    "网络不可用,请重试!");
                                        }
                                    });

				}else{
                    ToastUtils.Errortoast(mContext,"网络不可用");
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
		String ret_num = jsonObject.optString("ret_num");
		String ret_msg = jsonObject.optString("ret_msg");

		if (ret_num.equals("0")) {

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
				CrashApplication.getInstance().logout();
				startActivity(new Intent(mContext, ActivityLogin.class));
			}else{
				ToastUtils.Errortoast(mContext, ret_msg);
			}
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Errortoast(mContext, "添加成员失败");
	}


}
