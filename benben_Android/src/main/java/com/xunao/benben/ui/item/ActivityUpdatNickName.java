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
import com.xunao.benben.bean.User;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.LodingDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

public class ActivityUpdatNickName extends BaseActivity {
	private EditText et_new_nick_name;
	private LodingDialog lodingDialog;
	protected static final int UPDATE_NICK_NAME = 4;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_update_nick_name);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("修改昵称", "", "提交",
				R.drawable.icon_com_title_left, 0);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		et_new_nick_name = (EditText) findViewById(R.id.et_new_nick_name);

		Intent intent = getIntent();
		if (intent.getStringExtra("nick_name") != null) {
			et_new_nick_name.setText(intent.getStringExtra("nick_name"));
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
				String nick_name = et_new_nick_name.getText().toString().trim();
				if (TextUtils.isEmpty(nick_name)) {
					ToastUtils.Infotoast(mContext, "昵称不能为空!");
				} else {
					if (!CommonUtils.StringIsSurpass(nick_name, 1, 12)) {
						ToastUtils.Infotoast(mContext, "昵称不可超过12字");
						return;
					}
					
					if (CommonUtils.isNetworkAvailable(mContext))
						InteNetUtils.getInstance(mContext).updateMemberInfo("",
								nick_name, "", "", mRequestCallBack);

				}
			}
		});
	}

	@Override
	protected void onHttpStart() {
		if (isShowLoding) {
			if (lodingDialog == null) {
				lodingDialog = new LodingDialog(mContext);
			}
			lodingDialog.show();
		}
	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		String ret_num = jsonObject.optString("ret_num");
		String ret_msg = jsonObject.optString("ret_msg");

		if (ret_num.equals("0")) {
			ToastUtils.Infotoast(mContext, "修改昵称成功！");
			User users = new User();
			try {
				users.parseJSON(jsonObject);
				Intent intent = new Intent();
				intent.putExtra("nick_name", users.getUserNickname());
				setResult(UPDATE_NICK_NAME, intent);
				user.setUserNickname(users.getUserNickname());
				sendBroadcast(new Intent(AndroidConfig.refrashMyFragment));
				AnimFinsh();
			} catch (NetRequestException e) {
				e.printStackTrace();
				ToastUtils.Infotoast(mContext, e.getError().toString());
			}
		} else {
			ToastUtils.Infotoast(mContext, "修改昵称失败！");
			return;
		}

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {

	}

}
