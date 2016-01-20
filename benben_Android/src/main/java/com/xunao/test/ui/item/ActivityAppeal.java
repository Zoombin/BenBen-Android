package com.xunao.test.ui.item;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.test.R;
import com.xunao.test.base.BaseActivity;
import com.xunao.test.net.InteNetUtils;
import com.xunao.test.utils.CommonUtils;
import com.xunao.test.utils.ToastUtils;

public class ActivityAppeal extends BaseActivity {

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
		initTitle_Right_Left_bar("我要举报", "", "提交",
				R.drawable.icon_com_title_left, 0);
		et_content = (EditText) findViewById(R.id.et_content);
		et_content.setHint("请输入您要举报的内容...");
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		iD = getIntent().getStringExtra("ID");
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
					ToastUtils.Infotoast(mContext, "请输入申诉内容!");
					return;
				} else {
					if (CommonUtils.isNetworkAvailable(mContext))
						InteNetUtils.getInstance(mContext).doBuyComplain(iD,
								content, requestCallBack);
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
					ToastUtils.Infotoast(mContext, "您的举报我们已经收到会尽快处理!!");
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							AnimFinsh();

						}
					}, 300);
					return;
				} else {
					ToastUtils.Infotoast(mContext, ret_msg);
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				ToastUtils.Infotoast(mContext, "举报失败!");
				return;
			}
		}

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			ToastUtils.Infotoast(mContext, "举报失败!");
		}
	};
	private String iD;

}