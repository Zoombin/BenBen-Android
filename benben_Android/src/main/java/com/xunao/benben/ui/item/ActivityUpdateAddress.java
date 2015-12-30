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

public class ActivityUpdateAddress extends BaseActivity {
	private EditText et_address;
	private String address;
	private final static int UPDATE_ADDRESS = 1;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_update_address);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("修改地址", "", "提交",
				R.drawable.icon_com_title_left, 0);

		et_address = (EditText) findViewById(R.id.et_address);

		Intent intent = getIntent();
		if (intent.getStringExtra("address") != null) {
			et_address.setText(intent.getStringExtra("address"));
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
				address = et_address.getText().toString().trim();

				if (TextUtils.isEmpty(address)) {
					ToastUtils.Infotoast(mContext, "地址不能为空！");
					return;
				}
                if (!CommonUtils.StringIsSurpass2(address, 1, 30)) {
                    ToastUtils.Errortoast(mContext, "地址限制在30个字之间!");
                    return;
                }
				if (CommonUtils.isNetworkAvailable(mContext))
					InteNetUtils.getInstance(mContext).updateMemberInfo("", "",
							"", address, mRequestCallBack);
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
			ToastUtils.Infotoast(mContext, "修改地址成功！");
			user.setAddress(address);
			Intent intent = new Intent();
			intent.putExtra("address", address);
			setResult(UPDATE_ADDRESS, intent);
			AnimFinsh();
		} else {
			ToastUtils.Infotoast(mContext, "修改地址失败！");
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {

	}

}
