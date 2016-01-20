package com.xunao.test.ui;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.test.R;
import com.xunao.test.base.BaseActivity;
import com.xunao.test.net.InteNetUtils;
import com.xunao.test.utils.ToastUtils;
import com.xunao.test.view.MyEditView;

public class ActivityForgetPasswordThree extends BaseActivity {
	private MyEditView phone_password;
	private MyEditView phone_repassword;
	private String code;
	private String phone;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_forget_password_02);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("找回密码", "", "完成",
				R.drawable.icon_com_title_left, 0);

		phone_password = (MyEditView) findViewById(R.id.phone_password);
		phone_repassword = (MyEditView) findViewById(R.id.phone_repassword);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		phone = intent.getStringExtra("phone");
		code = intent.getStringExtra("code");
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
				String password = phone_password.getText().toString().trim();
				String repassword = phone_repassword.getText().toString()
						.trim();
				if (TextUtils.isEmpty(password)
						|| TextUtils.isEmpty(repassword)) {
					ToastUtils.Infotoast(mContext, "请输入新密码!");
					return;
				}

				if (!password.equals(repassword)) {
					ToastUtils.Infotoast(mContext, "两次密码输入不一致!");
					return;
				} else if (!password.matches("^\\w{6,16}$")) {
					ToastUtils.Errortoast(mContext, "密码输入为6-16位");
					return;
				}

				InteNetUtils.getInstance(mContext).updateForgetPhoneCode(phone,
						password, repassword, code, mRequestCallBack);
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
		if (jsonObject.optString("ret_num").equals("0")) {
			ToastUtils.Infotoast(mContext, "密码重置成功,请重新登录!");
			startAnimActivity(ActivityLogin.class);
			AnimFinsh();
		} else {
			ToastUtils.Infotoast(mContext, "找回密码失败!");
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Infotoast(mContext, "网络不可用!");
	}

}
