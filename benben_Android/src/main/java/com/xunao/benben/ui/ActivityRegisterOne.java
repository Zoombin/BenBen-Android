package com.xunao.benben.ui;

import org.json.JSONObject;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.RegexUtils;
import com.xunao.benben.utils.ToastUtils;

public class ActivityRegisterOne extends BaseActivity implements
		OnClickListener {

	private EditText phone_num;
	private TextView register_point;

	private String phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_register_one);

		initTitle_Right_Left_bar("注册", "", "下一步",
				R.drawable.icon_com_title_left, 0);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		phone_num = (EditText) findViewById(R.id.phone_num);
		register_point = (TextView) findViewById(R.id.register_point);

		register_point.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startAnimActivity2Obj(
						ActivityWeb.class,
						"url",
						"http://benben.xun-ao.com/index.php/v1/setting/registerprotocol/key/iphone/type/1",
						"title", "奔犇使用协议");
			}
		});
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		// 修改部分自体颜色
		SpannableStringBuilder builder = new SpannableStringBuilder(
				register_point.getText().toString());
		ForegroundColorSpan greenSpan = new ForegroundColorSpan(
				Color.parseColor("#3b96ca"));
		builder.setSpan(greenSpan, 42, 51, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		register_point.setText(builder);

	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(this);
		setOnRightClickLinester(this);

	}

	@Override
	protected void onHttpStart() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		try {
			SuccessMsg successMsg = new SuccessMsg();
			successMsg.parseJSON(jsonObject);
			String code = jsonObject.optString("ret_code");
			ToastUtils.Infotoast(mContext, "短信已发送，请注意查收！");
			startAnimActivity2Obj(ActivityRegisterTwo.class, "phone", phone,
					"code", code);
		} catch (NetRequestException e) {
			e.getError().print(mContext);
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
			phone = phone_num.getText().toString();
			if (RegexUtils.checkMobile(phone)) {
				getRegisterCode(phone);
			} else {
				ToastUtils.Errortoast(mContext, "请正确输入手机号码！");
			}
			break;

		default:
			break;
		}

	}

	public void getRegisterCode(String phoneNum) {
		InteNetUtils.getInstance(mContext).getPhoneCode(phoneNum,
				mRequestCallBack);
	}

}
