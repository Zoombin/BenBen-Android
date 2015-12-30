package com.xunao.benben.ui;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.RegexUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.MyEditView;
import com.xunao.benben.view.MyTextView;

public class ActivityForgetPassWord extends BaseActivity {
	private MyEditView phone_num;
	private String phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_forget_password_01);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("找回密码", "", "下一步",
				R.drawable.icon_com_title_left, 0);

		phone_num = (MyEditView) findViewById(R.id.phone_num);
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
				phone = phone_num.getText().toString();
				if (RegexUtils.checkMobile(phone)) {
					getRegisterCode(phone);
				} else {
					ToastUtils.Errortoast(mContext, "请正确输入手机号码！");
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
		try {
			SuccessMsg successMsg = new SuccessMsg();
			successMsg.parseJSON(jsonObject);
			String code = jsonObject.optString("ret_code");
			ToastUtils.Infotoast(mContext, "短信已发送，请注意查收！");
			startAnimActivity2Obj(ActivityForgetPasswordTwo.class, "phone",
					phone, "code", code);
		} catch (NetRequestException e) {
			e.getError().print(mContext);
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Infotoast(mContext, "网络不可用!");
	}

	public void getRegisterCode(String phoneNum) {
		if (CommonUtils.isNetworkAvailable(mContext)) {
			InteNetUtils.getInstance(mContext).getForgetPhoneCode(phoneNum,
					mRequestCallBack);
		}
	}

}
