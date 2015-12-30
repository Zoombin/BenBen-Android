package com.xunao.benben.ui.item;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.anim;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.BroadCasting;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityLogin;
import com.xunao.benben.utils.ToastUtils;

public class ActivityBroadCasting extends BaseActivity {
	protected static final int ZAIFAYITIAO = 1110;
	private BroadCasting broadCast;
	private TextView tv_time;
	private TextView tv_content;
	private TextView tv_sender;
	private Button btn_new_broadCasting;

	private MsgDialog inputDialog;
	private int lastNum;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_broad_casting);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("小喇叭详情", "", "",
				R.drawable.icon_com_title_left, R.drawable.icon_delete_01);

		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_content = (TextView) findViewById(R.id.tv_content);
		tv_sender = (TextView) findViewById(R.id.tv_sender);
		btn_new_broadCasting = (Button) findViewById(R.id.btn_new_broadCasting);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		broadCast = (BroadCasting) getIntent().getSerializableExtra(
				"broadCasting");

		tv_time.setText(broadCast.getCreatedTime());
		tv_sender.setText(broadCast.getDescription());
		tv_content.setText(broadCast.getContent());
		lastNum = getIntent().getIntExtra("lastNum", 0);
		btn_new_broadCasting.setText("再发一条(" + lastNum + ")");
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
				inputDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
				inputDialog.setContent("删除小喇叭", "是否删除本条小喇叭", "确认", "取消");
				inputDialog.setCancleListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						inputDialog.dismiss();
					}
				});
				inputDialog.setOKListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						InteNetUtils.getInstance(mContext).deleteBroadCasting(
								broadCast.getId(), "0",
								new RequestCallBack<String>() {
									@Override
									public void onSuccess(
											ResponseInfo<String> arg0) {
										String result = arg0.result;
										try {
											JSONObject jsonObject = new JSONObject(
													result);
											String ret_num = jsonObject
													.optString("ret_num");
											String ret_msg = jsonObject
													.optString("ret_msg");

											if ("0".equals(ret_num)) {
												ToastUtils.Infotoast(mContext,
														"小喇叭删除成功");
												new Handler().postDelayed(
														new Runnable() {

															@Override
															public void run() {
																sendBroadcast(new Intent(
																		AndroidConfig.refrashBroadCasting));
																AnimFinsh();
															}
														}, 300);
												return;
											} else {
												
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
													ToastUtils.Infotoast(mContext,
															ret_msg);
												}
												return;
											}
										} catch (JSONException e) {
											e.printStackTrace();
											ToastUtils.Infotoast(mContext,
													"小喇叭删除失败!");
											return;
										}
									}

									@Override
									public void onFailure(HttpException arg0,
											String arg1) {
										ToastUtils.Errortoast(mContext,
												"网络不可用!");
									}
								});
						inputDialog.dismiss();
					}
				});
				inputDialog.show();
			}
		});

		btn_new_broadCasting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (lastNum <= 0) {
					final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(
							ActivityBroadCasting.this, R.style.MyDialog1);
					hint.setContent("本月小喇叭已经用完");
					hint.setBtnContent("知道了");
					hint.show();
					hint.setOKListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							hint.dismiss();
						}
					});

					hint.show();
				} else {
					startAnimActivityForResult(ActivitySmallPublic.class,
							"sendContacts", broadCast, ZAIFAYITIAO);

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

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		switch (arg0) {
		case ZAIFAYITIAO:
			if (arg2 != null) {
				AnimFinsh();
			}
			break;

		default:
			break;
		}
		super.onActivityResult(arg0, arg1, arg2);
	}
}
