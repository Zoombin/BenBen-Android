package com.xunao.benben.ui.item;

import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.Happy;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityLogin;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

public class ActivityHappy extends BaseActivity implements OnClickListener {
	private TextView tv_happy;
	private LinearLayout ll_next;
	private String createdTime = "";
	private Happy happy;

	private boolean isGoodOrBad = false;
	private LinearLayout ll_good;
	private LinearLayout ll_bad;
	private ImageView iv_good;
	private ImageView iv_bad;
	private String status = "";
	private String times = "";
	
	private boolean isLast = false;

	private SharedPreferences mySharedPreferences;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_happy);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("开心一刻", "", "",
				R.drawable.icon_com_title_left, 0);

		if (!CommonUtils.isNetworkAvailable(mContext)) {
			AnimFinsh();
		}

		tv_happy = (TextView) findViewById(R.id.tv_happy);
		ll_next = (LinearLayout) findViewById(R.id.ll_next);

		ll_bad = (LinearLayout) findViewById(R.id.ll_bad);
		ll_good = (LinearLayout) findViewById(R.id.ll_good);
		iv_bad = (ImageView) findViewById(R.id.iv_bad);
		iv_good = (ImageView) findViewById(R.id.iv_good);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		mySharedPreferences = getSharedPreferences("happy",
				mContext.MODE_PRIVATE);
		
		String lastid = mySharedPreferences.getString("lastid", "");
		createdTime = mySharedPreferences.getString("number", "");
		times = mySharedPreferences.getString("number", "");
		
		if(lastid != null && !"".equals(lastid)){
			if (CommonUtils.isNetworkAvailable(mContext)) {
				int id = Integer.parseInt(lastid) - 1;
				InteNetUtils.getInstance(mContext).getHappy(id+"", "",
						mRequestCallBack);
			}
		}else{
			if (CommonUtils.isNetworkAvailable(mContext)) {
				InteNetUtils.getInstance(mContext).getHappy("0", createdTime,
						mRequestCallBack);
			}
		}

	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mySharedPreferences = getSharedPreferences("happy",
						mContext.MODE_PRIVATE);
				SharedPreferences.Editor editor = mySharedPreferences.edit();
				editor.putString("lastid", happy.getId());
				editor.commit();
				AnimFinsh();
			}
		});

		ll_next.setOnClickListener(this);
		ll_bad.setOnClickListener(this);
		ll_good.setOnClickListener(this);
	}

	@Override
	protected void onHttpStart() {

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		String optString = jsonObject.optString("ret_num");

		if (optString != null) {
			if (!optString.equals("0")) {
				String errorMsg = jsonObject.optString("ret_msg");
				
				if(jsonObject.optString("ret_num").equals("2015") ){
					final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(
							mContext, R.style.MyDialog1);
					hint.setContent("奔犇账号在其他手机登录");
					hint.setBtnContent("确定");
					hint.show();
					hint.setOKListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							hint.dismiss();
						}
					});

					hint.show();
					CrashApplication.getInstance().logout();
					startActivity(new Intent(mContext, ActivityLogin.class));
				}else{
					ToastUtils.Infotoast(mContext, errorMsg);
				}
				
				isLast  = true;
				return;
			} else {
				try {
					happy = new Happy();
					JSONObject object = jsonObject.optJSONObject("happy");
					happy.parseJSON(object);
					
					if("".equals(happy.getDescription())){
						ToastUtils.Errortoast(mContext, "最后一条了，明天继续");
						return;
					}
					
					tv_happy.setText(happy.getDescription());
					createdTime = happy.getCreatedTime();

					if (happy.getStatus().equals("1")) {
						iv_good.setImageResource(R.drawable.icon_good_02);
					} else if (happy.getStatus().equals("2")) {
						iv_bad.setImageResource(R.drawable.icon_bad_02);
					} else {
						iv_good.setImageResource(R.drawable.icon_good_01);
						iv_bad.setImageResource(R.drawable.icon_bad_01);
					}

					status = happy.getStatus();
					
				} catch (NetRequestException e) {
					e.getError().print(mContext);
					e.printStackTrace();

				}
			}
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Infotoast(mContext, "网络不可用,请重试!");
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.ll_next:
			if (CommonUtils.isNetworkAvailable(mContext)){
				if(!isLast){
					times = createdTime;
					if(!"".equals(happy.getId())){
						InteNetUtils.getInstance(mContext).getHappy(happy.getId(),
								createdTime, mRequestCallBack);
					}else{
						ToastUtils.Errortoast(mContext, "最后一条了，明天继续");
					}
				}else{
					ToastUtils.Errortoast(mContext, "最后一条了，明天继续");
				}
			}
			break;
		case R.id.ll_good:
			if (CommonUtils.isNetworkAvailable(mContext)) {
				if (status.equals("0")) {
					if (CommonUtils.isNetworkAvailable(mContext)) {
						InteNetUtils.getInstance(mContext).setHappyGoodOrBad(
								"1", happy.getId(), requestCallBack);
						isGoodOrBad = false;
					}
				}
			}
			break;
		case R.id.ll_bad:
			if (CommonUtils.isNetworkAvailable(mContext)) {
				if (status.equals("0")) {
					if (CommonUtils.isNetworkAvailable(mContext)) {
						InteNetUtils.getInstance(mContext).setHappyGoodOrBad(
								"2", happy.getId(), requestCallBack);
						isGoodOrBad = true;
					}
				}
			}
			break;
		}
	}

	private RequestCallBack<String> requestCallBack = new RequestCallBack<String>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			if (isGoodOrBad) {
				ToastUtils.Infotoast(mContext, "吐槽失败!");
			} else {
				ToastUtils.Infotoast(mContext, "点赞失败!");
			}

		}

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			if (isGoodOrBad) {
				ToastUtils.Infotoast(mContext, "吐槽成功!");
				iv_bad.setImageResource(R.drawable.icon_bad_02);
				status = "1";
			} else {
				ToastUtils.Infotoast(mContext, "点赞成功!");
				iv_good.setImageResource(R.drawable.icon_good_02);
				status = "2";
			}
		}

	};
	
	public void onBackPressed() {
		mySharedPreferences = getSharedPreferences("happy",
				mContext.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString("lastid", happy.getId());
		editor.commit();
		AnimFinsh();
	};
	
}
