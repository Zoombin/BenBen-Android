package com.xunao.benben.ui.item;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

public class ActivityComplain extends BaseActivity {
	private EditText et_content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_complaint);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("投诉建议", "", "提交",
				R.drawable.icon_com_title_left, 0);
		et_content = (EditText) findViewById(R.id.et_content);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {

	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		setOnRightClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String content = et_content.getText().toString().trim();
				if (TextUtils.isEmpty(content)) {
					ToastUtils.Infotoast(mContext, "请输入投诉建议内容!");
				}else if (!CommonUtils.StringIsSurpass2(content, 0, 200)) {
                    ToastUtils.Errortoast(mContext, "内容限制在200个字之间!");
                }else {
					InteNetUtils.getInstance(mContext).doComplain(content,
							requestCallBack);
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

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {

	}

	private RequestCallBack<String> requestCallBack = new RequestCallBack<String>() {

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			String result = arg0.result;
			try {
				JSONObject jsonObject = new JSONObject(result);

				String ret_num = jsonObject.optString("ret_num");
				String ret_msg = jsonObject.optString("ret_msg");

				if ("0".equals(ret_num)) {
					ToastUtils.Infotoast(mContext, "我们已收到您的举报，将会尽快处理！");
					finish();
					return;
				} else {
					ToastUtils.Infotoast(mContext, "提交投诉建议失败!");
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				ToastUtils.Infotoast(mContext, "提交投诉建议失败!");
				return;
			}
		}

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			ToastUtils.Infotoast(mContext, "网络不可用!");
		}
	};

}
