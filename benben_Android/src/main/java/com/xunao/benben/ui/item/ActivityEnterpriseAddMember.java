package com.xunao.benben.ui.item;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.RegexUtils;
import com.xunao.benben.utils.ToastUtils;

public class ActivityEnterpriseAddMember extends BaseActivity {
	private EditText tv_phone;
	private EditText tv_name;
	private String enterpriseId;
	private String enterpriseType;
	private String short_length;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_enterprise_add_member);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("添加成员", "", "添加",
				R.drawable.icon_com_title_left, 0);

		tv_phone = (EditText) findViewById(R.id.tv_phone);
		tv_name = (EditText) findViewById(R.id.tv_name);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		enterpriseId = getIntent().getStringExtra("enterpriseId");
		enterpriseType = getIntent().getStringExtra("enterpriseType");
		short_length = getIntent().getStringExtra("short_length");
		
		if(enterpriseType.equals("2")){
			tv_phone.setHint("请填写好友短号");
		}
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
				String phone = tv_phone.getText().toString().trim();
				String name = tv_name.getText().toString().trim();
                phone = phone.replaceAll("\\+86", "")
                        .replaceAll(" ", "").replaceAll("-", "");
				if ("".equals(phone)) {
					if(enterpriseType.equals("2")){
						ToastUtils.Errortoast(mContext, "请填写好友短号");
					}else{
						ToastUtils.Errortoast(mContext, "请填写好友手机号");
					}
					
					return;
				}
				
				if ("1".equals(enterpriseType)) {
					if (!RegexUtils.checkMobile(phone)) {
						ToastUtils.Errortoast(mContext, "请输入正确手机号");
						return;
					}
				}else{
					if (!short_length.equals(phone.length() + "")) {
						ToastUtils.Errortoast(mContext, "短号长度不匹配");
						return;
					}
				}

				if ("".equals(name)) {
					ToastUtils.Errortoast(mContext, "请输入好友真实姓名");
					return;
				}
				
				if (!CommonUtils.StringIsSurpass2(name, 2, 10)) {
					ToastUtils.Infotoast(mContext, "名字限制在1-10字之间");
					return;
				}
				
//				if(name.length() < 2 || name.length() > 20){
//					ToastUtils.Errortoast(mContext, "真实姓名限制在2-20个字符之间");
//					return;
//				}
				
				

				if ("1".equals(enterpriseType)) {
					String contacts = "0:" + name + ":" + phone;
					if (CommonUtils.isNetworkAvailable(mContext)) {
						InteNetUtils.getInstance(mContext)
								.enterpriseVInviteFriend(enterpriseId,
										contacts, "1", mRequestCallBack);
					}
				} else {
					String contacts = name + ":" + phone;
					if (CommonUtils.isNetworkAvailable(mContext)) {
						InteNetUtils.getInstance(mContext)
								.enterpriseVInviteFriendshort(enterpriseId,
										contacts, "1", mRequestCallBack);
					}
				}

			}
		});
	}

	@Override
	protected void onHttpStart() {

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		if (jsonObject.optString("ret_num").equals("0")) {
			ToastUtils.Infotoast(mContext, "添加新成员成功!");

			sendBroadcast(new Intent(AndroidConfig.refreshEnterpriseMember));
			sendBroadcast(new Intent(AndroidConfig.refreshEnterpriseList));
			sendBroadcast(new Intent(AndroidConfig.refreshEnterpriseDetail));
			
			Intent intent = new Intent();
			intent.putExtra("success", "success");
			setResult(2, intent);
			AnimFinsh();
		} else {
			ToastUtils.Infotoast(mContext, jsonObject.optString("ret_msg"));
		}

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Infotoast(mContext, "网络不可用,请重试!");
	}

}
