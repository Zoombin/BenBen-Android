package com.xunao.benben.ui.item;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

public class ActivityUpdateRealName extends BaseActivity {
	private EditText et_real_name;
	private String name;
	private final static int UPDATE_REAL_NAME = 1;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_update_real_name);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("真实姓名", "", "提交",
				R.drawable.icon_com_title_left, 0);

		et_real_name = (EditText) findViewById(R.id.et_real_name);

		Intent intent = getIntent();
		if (intent.getStringExtra("name") != null) {
			et_real_name.setText(intent.getStringExtra("name"));
		}

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
				name = et_real_name.getText().toString().trim();

				if (TextUtils.isEmpty(name)) {
					ToastUtils.Infotoast(mContext, "真实姓名不能为空！");
					return;
				}
				if (CommonUtils.isNetworkAvailable(mContext))
					InteNetUtils.getInstance(mContext).updateMemberInfo("", "",
							name, "", mRequestCallBack);
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

		if (ret_num.equals("0")) {
			ToastUtils.Infotoast(mContext, "修改真实姓名成功");
			user.setName(name);
			Intent intent = new Intent();
			intent.putExtra("name", name);
			setResult(UPDATE_REAL_NAME, intent);
			AnimFinsh();
		} else {
			ToastUtils.Infotoast(mContext, "修改真实姓名失败");
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Infotoast(mContext, "修改真实姓名失败！");
	}

}
