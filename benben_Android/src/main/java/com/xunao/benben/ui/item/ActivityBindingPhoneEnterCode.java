package com.xunao.benben.ui.item;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityLogin;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

public class ActivityBindingPhoneEnterCode extends BaseActivity implements
		OnClickListener {

	private String phone_num;
	private String phone_code;

	private TextView phone_info;
	private Button no_phone_code;
	private EditText input_phone_code;

	// 倒计时
	private CountDownTimer timer = new CountDownTimer(60000, 1000) {
		@Override
		public void onTick(long millisUntilFinished) {
			no_phone_code.setEnabled(false);
			no_phone_code.setText((millisUntilFinished / 1000) + "");
		}

		@Override
		public void onFinish() {
			no_phone_code.setEnabled(true);
			no_phone_code.setText("没收到？");
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_register_two);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("变更手机号", "", "完成",
				R.drawable.icon_com_title_left, 0);

		phone_info = (TextView) findViewById(R.id.phone_info);
		no_phone_code = (Button) findViewById(R.id.no_phone_code);
		input_phone_code = (EditText) findViewById(R.id.input_phone_code);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		phone_num = intent.getStringExtra("phone");
		phone_code = intent.getStringExtra("code");

		phone_info.setText("验证码已发送至：" + phone_num);

		if (phone_code != null && phone_code.length() > 0) {
			timer.start();
		}
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(this);
		setOnRightClickLinester(this);

		no_phone_code.setOnClickListener(this);
	}

	@Override
	protected void onHttpStart() {
	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {
	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		if(jsonObject.optString("ret_num").equals("0")){
			ToastUtils.Infotoast(mContext, "变更手机号成功,请重新登录!");
			mApplication.logout();
			startAnimActivity(ActivityLogin.class);
		}else{
			ToastUtils.Infotoast(mContext, jsonObject.optString("ret_msg"));
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Errortoast(mContext, "网络请求失败，请重试！");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 头部左侧点击
		case R.id.com_title_bar_left_bt:
		case R.id.com_title_bar_left_tv:
			AnimFinsh();
			break;

		// 头部右侧点击
		case R.id.com_title_bar_right_bt:
		case R.id.com_title_bar_right_tv:
			String code = input_phone_code.getText().toString();
			if(code.equals(phone_code)){
				InteNetUtils.getInstance(mContext).updatePhone(phone_num, code, mRequestCallBack);
			}else{
				ToastUtils.Errortoast(mContext, "请输入正确验证码！");
			}
			break;
		case R.id.no_phone_code:
			if (phone_num != null && phone_num.length() > 0) {
				getRegisterCode(phone_num);
			}
			break;
		default:
			break;
		}
	}

	public void getRegisterCode(String phoneNum) {
		InteNetUtils.getInstance(mContext).getPhoneCode(phoneNum, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				ToastUtils.Errortoast(mContext, "短信已发送，请注意查收！");
			}
			
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				ToastUtils.Errortoast(mContext, "网络请求失败，请重试！");
			}
		});
	}

}
