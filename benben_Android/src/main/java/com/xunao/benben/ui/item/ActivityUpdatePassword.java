package com.xunao.benben.ui.item;

import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityLogin;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

public class ActivityUpdatePassword extends BaseActivity {
	private EditText et_old_password;
	private EditText et_new_password;
	private EditText et_new_repassword;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_update_password);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("修改密码", "", "提交",
				R.drawable.icon_com_title_left, 0);

		et_old_password = (EditText) findViewById(R.id.et_old_password);
		et_new_password = (EditText) findViewById(R.id.et_new_password);
		et_new_repassword = (EditText) findViewById(R.id.et_new_repassword);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {

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
				String oldPwd = et_old_password.getText().toString().trim();
				String newPwd = et_new_password.getText().toString().trim();
				String newRePwd = et_new_repassword.getText().toString().trim();

				if (TextUtils.isEmpty(oldPwd) || TextUtils.isEmpty(newPwd)) {
					ToastUtils.Infotoast(mContext, "密码不能为空!");
					return;
				} else if (!newPwd.equals(newRePwd)) {
					ToastUtils.Infotoast(mContext, "两次密码输入不一致!");
					return;
				} else if (!newPwd.matches("^\\w{6,16}$")) {
					ToastUtils.Errortoast(mContext, "密码输入为6-16位");
					return;
				}
				if (CommonUtils.isNetworkAvailable(mContext))
					InteNetUtils.getInstance(mContext).updatePassword(oldPwd,
							newPwd, mRequestCallBack);
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
			ToastUtils.Infotoast(mContext, "修改密码成功,请重新登录!");
			mApplication.logout();
			startAnimActivity(ActivityLogin.class);
		} else {
			ToastUtils.Infotoast(mContext, ret_msg);
		}

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {

	}

}
