package com.xunao.test.ui.item;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.test.R;
import com.xunao.test.base.BaseActivity;
import com.xunao.test.bean.FriendUnion;
import com.xunao.test.net.InteNetUtils;
import com.xunao.test.utils.CommonUtils;
import com.xunao.test.utils.ToastUtils;

public class ActivityFriendUnionAddBulletin extends BaseActivity {
	private FriendUnion friendUnion;
	private EditText et_content;
	protected String annocement;
	private RelativeLayout rl_choice_friend_union;
	private RelativeLayout rl_choice_friend;
	private View line;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_complaint);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("好友联盟公告", "", "提交",
				R.drawable.icon_com_title_left, 0);

		et_content = (EditText) findViewById(R.id.et_content);
		et_content.setHint("请输入联盟公告");
		
		rl_choice_friend_union = (RelativeLayout) findViewById(R.id.rl_choice_friend_union);
		rl_choice_friend = (RelativeLayout) findViewById(R.id.rl_choice_friend);
		line = findViewById(R.id.line);
		
		rl_choice_friend_union.setVisibility(View.GONE);
		rl_choice_friend.setVisibility(View.GONE);
		line.setVisibility(View.GONE);
		
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		friendUnion = (FriendUnion) getIntent().getSerializableExtra(
				"friendUnion");
		et_content.setText(friendUnion.getAnnocement());
		et_content.setSelection(friendUnion.getAnnocement().length());
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
				annocement = et_content.getText().toString().trim();
				if (TextUtils.isEmpty(annocement)) {
					ToastUtils.Infotoast(mContext, "请输入联盟公告!");
					return;
				}
                if (!CommonUtils.StringIsSurpass2(annocement, 1, 150)) {
                    ToastUtils.Errortoast(mContext, "公告限制在150个字之间!");
                    return;
                }
				if (CommonUtils.isNetworkAvailable(mContext)) {
					InteNetUtils.getInstance(mContext).updateFriendUnion(
							friendUnion.getId(), "", "", null, annocement,
							null, mRequestCallBack);
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

		if (ret_num.equals("0")) {
			ToastUtils.Infotoast(mContext, "发布好友联盟公告成功!");
			Intent intent = new Intent();
			friendUnion.setAnnocement(annocement);
			intent.putExtra("friendUnion", friendUnion);
			setResult(10000, intent);
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
