package com.xunao.benben.ui.item;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.Enterprise;
import com.xunao.benben.bean.EnterpriseMemberDetail;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.dialog.InputDialog;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityLogin;
import com.xunao.benben.ui.ActivityMyWeb;
import com.xunao.benben.ui.ActivityWeb;
import com.xunao.benben.ui.item.ActivityEnterpriseInviteMember.MyBroadcastReceiver;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.RegexUtils;
import com.xunao.benben.utils.ToastUtils;

public class ActivityEnterpriseDetail extends BaseActivity implements
		OnClickListener {
	private String id;
	private Enterprise enterprise;
	private TextView tv_name;
	private TextView tv_number;
	private TextView tv_info;
	private TextView tv_phone;
	private TextView tv_mp;
	private Button btn_tuichu;
	private RelativeLayout rl_name;
	private RelativeLayout rl_inivite;
	private RelativeLayout rl_fenzi;
	private RelativeLayout rl_phone;
	private RelativeLayout rl_mp;
	private RelativeLayout rl_add_common;
	private RelativeLayout rl_my_phone;
	private TextView tv_my_phone;
	private View line_group;
	private ImageView iv_other_phone;

	private TextView other_phone;
	
	private View line_5;

	private ArrayList<EnterpriseMemberDetail> memberDetails = new ArrayList<EnterpriseMemberDetail>();

	private final static int UPDATE_ENTERPRISE = 1;
	private final static int INVITE_FRIEND = 2;
	private final static int ENTERPRISE_GROUP = 3;

	private InputDialog inputDialog;
	private String pecketName;
	private boolean updatePhoneOrName = false;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_enterprise_detail);
		myBroadcastReceiver = new MyBroadcastReceiver();
		registerReceiver(myBroadcastReceiver, new IntentFilter(
				AndroidConfig.refreshEnterpriseDetail));
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("政企通讯录详情", "", "",
				R.drawable.icon_com_title_left, 0);

		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_number = (TextView) findViewById(R.id.tv_number);
		tv_info = (TextView) findViewById(R.id.tv_info);
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		name = (TextView) findViewById(R.id.name);
		tv_mp = (TextView) findViewById(R.id.tv_mp);
		btn_tuichu = (Button) findViewById(R.id.btn_tuichu);
		rl_name = (RelativeLayout) findViewById(R.id.rl_name);
		rl_inivite = (RelativeLayout) findViewById(R.id.rl_inivite);
		addBut = (LinearLayout) findViewById(R.id.addBut);
		rl_fenzi = (RelativeLayout) findViewById(R.id.rl_fenzi);
		rl_phone = (RelativeLayout) findViewById(R.id.rl_phone);
		rl_mp = (RelativeLayout) findViewById(R.id.rl_mp);
		rl_add_common = (RelativeLayout) findViewById(R.id.rl_add_common);
		tv_my_phone = (TextView) findViewById(R.id.tv_my_phone);
		iv_other_phone = (ImageView) findViewById(R.id.iv_other_phone);
		rl_my_phone = (RelativeLayout) findViewById(R.id.rl_my_phone);
		line_5 = findViewById(R.id.line_5);
		
		line_group = findViewById(R.id.line_group);

		other_phone = (TextView) findViewById(R.id.other_phone);

		zz = findViewById(R.id.zz);
		
		btn_tuichu.setVisibility(View.GONE);
		rl_add_common.setVisibility(View.GONE);
		line_group.setVisibility(View.GONE);
		
		other_phone.setText("我的短号");
		rl_name.setOnClickListener(this);
		btn_tuichu.setOnClickListener(this);
		rl_inivite.setOnClickListener(this);
		rl_fenzi.setOnClickListener(this);
		rl_phone.setOnClickListener(this);
		rl_mp.setOnClickListener(this);
		rl_add_common.setOnClickListener(this);
	}

	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(myBroadcastReceiver);
	};

	@Override
	public void initDate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		id = intent.getStringExtra("id");
		memberDetails = (ArrayList<EnterpriseMemberDetail>) intent
				.getSerializableExtra("member");

		InteNetUtils.getInstance(mContext).enterprisesDetail(id,
				mRequestCallBack);
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
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ActivityMyWeb.class);
                intent.putExtra("title", "大喇叭");
                intent.putExtra("url", AndroidConfig.NETHOST4 + AndroidConfig.CreateBroadcast + "?enterprise_id="+id+"&token="+user.getToken());
                startActivity(intent);


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
        Log.d("ltf","jsonObject==========="+jsonObject);
		String optString = jsonObject.optString("ret_num");

		if (optString != null) {
			if (!optString.equals("0")) {
				String errorMsg = jsonObject.optString("ret_msg");
				ToastUtils.Infotoast(mContext, errorMsg);
				return;
			}

			try {
				enterprise = new Enterprise();
				JSONObject object = jsonObject.optJSONObject("enterprise_info");
				enterprise.parseJSON(object);
				zz.setVisibility(View.GONE);

                if(enterprise.getIs_admin()==1){
                    initTitle_Right_Left_bar("政企通讯录详情", "", "大喇叭",
                            R.drawable.icon_com_title_left, 0);
                }


				if (enterprise.getOrigin() == 2 && "2".equals(enterprise.getType())) {
                    rl_my_phone.setClickable(false);
					btn_tuichu.setVisibility(View.GONE);
					btn_tuichu.setOnClickListener(null);
                    addBut.setVisibility(View.GONE);
                    rl_inivite.setOnClickListener(null);
                    rl_add_common.setOnClickListener(null);
                    rl_add_common.setVisibility(View.GONE);
                    rl_inivite.setClickable(false);

					
				}else{
                    if(enterprise.getIs_guard()==1 && enterprise.getIs_admin()!=1){
                        addBut.setVisibility(View.GONE);
                        rl_inivite.setOnClickListener(null);
                        rl_add_common.setOnClickListener(null);
                        rl_add_common.setVisibility(View.GONE);
                        rl_inivite.setClickable(false);

                    }else{
                        line_group.setVisibility(View.VISIBLE);
                        addBut.setVisibility(View.VISIBLE);
                        rl_add_common.setVisibility(View.VISIBLE);
                    }

                    line_5.setVisibility(View.VISIBLE);
                    rl_phone.setVisibility(View.VISIBLE);
					btn_tuichu.setVisibility(View.VISIBLE);


				}
				tv_name.setText(enterprise.getName());
				tv_number.setText("通讯录成员 ( " + enterprise.getNumber() + "人)");
				tv_info.setText(enterprise.getDescription());
				tv_phone.setText(enterprise.getPhone());
				tv_mp.setText(enterprise.getRemark());
				tv_my_phone.setText(enterprise.getMobliePhone());
				if ("2".equals(enterprise.getType())) {
					name.setText("虚拟网通讯录");
					other_phone.setText("我的短号");
					iv_other_phone.setVisibility(View.GONE);
					rl_phone.setClickable(false);
					rl_my_phone.setClickable(false);
				} else {
					other_phone.setText("我的其它号码");
					name.setText("企业通讯录");
					rl_my_phone.setClickable(false);
					
				}
                if(enterprise.getFirstin().equals("1")){
                    inputDialog = new InputDialog(mContext, R.style.MyDialogStyle);
                    inputDialog.setContent("通讯录名片", "请输入新的通讯录名片", "确认", "取消");
                    inputDialog.setEditContent(enterprise.getRemark());
                    inputDialog.setCancleListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            inputDialog.dismiss();
                        }
                    });
                    inputDialog.setOKListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pecketName = "";
                            pecketName = inputDialog.getInputText();

                            if (!CommonUtils.StringIsSurpass(pecketName, 0, 10)) {
                                ToastUtils.Infotoast(mContext, "通讯录名片限制在0-10个字数");
                                return;
                            }

                            if (CommonUtils.isNetworkAvailable(mContext)) {
                                updatePhoneOrName = false;
                                InteNetUtils.getInstance(mContext)
                                        .updateEnterpriseRemarkName(id, pecketName,
                                                requestCallBack2);
                                sendBroadcast(new Intent(
                                        AndroidConfig.refreshEnterpriseList));
                            }
                        }
                    });
                    inputDialog.show();
                }


			} catch (NetRequestException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Infotoast(mContext, "网络不可用,请重试!");
	}

	MsgDialog msgDialog;

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_tuichu:
			msgDialog = new MsgDialog(mContext, R.style.MyDialog1);
			msgDialog.setContent("退出政企通讯录", "确定要退出该政企通讯录", "确认", "取消");
			msgDialog.setCancleListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					msgDialog.dismiss();
				}
			});
			msgDialog.setOKListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					InteNetUtils.getInstance(mContext).enterprisesExit("",
							enterprise.getId(), requestCallBack);
					msgDialog.dismiss();
				}
			});
			msgDialog.show();
			break;
		case R.id.rl_name:
			// startAnimActivityForResult(ActivityCreatedEnterpriseContacts.class,
			// "enterprise", enterprise, UPDATE_ENTERPRISE);
			break;
		case R.id.rl_inivite:
			startAnimActivityForResult6(ActivityEnterpriseInviteMember.class,
					INVITE_FRIEND, "enterprise", enterprise, "member",
					memberDetails);
			break;
		case R.id.rl_fenzi:
			startAnimActivityForResult51(ActivityEnterpriseGroup.class,
					"enterpriseId", id, "type", enterprise.getType(), "member",
					memberDetails, ENTERPRISE_GROUP);
			break;
		case R.id.rl_phone:
			if (!"2".equals(enterprise.getType())) {
				inputDialog = new InputDialog(mContext, R.style.MyDialogStyle);
				inputDialog.setContent("其它号码", "请输入新的号码", "确认", "取消");
				inputDialog.setEditContent(enterprise.getPhone());
				inputDialog.setInputType(enterprise.getPhone());
				inputDialog.setCancleListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						inputDialog.dismiss();
					}
				});
				inputDialog.setOKListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						pecketName = "";
						pecketName = inputDialog.getInputText().trim();
						if (TextUtils.isEmpty(pecketName)) {
							if (CommonUtils.isNetworkAvailable(mContext)) {
								updatePhoneOrName = true;
								InteNetUtils.getInstance(mContext)
										.updateEnterpriseShortPhone(id, "", "",
												requestCallBack2);
								sendBroadcast(new Intent(
										AndroidConfig.refreshEnterpriseMember));
							}
							return;
						}
						if (!RegexUtils.checkDigit(pecketName)) {
							ToastUtils.Infotoast(mContext, "请输入数字");
							return;
						}
						if (pecketName.length() > 12) {
							ToastUtils.Errortoast(mContext, "号码长度不能超过12位!");
                            return;
						}

						if (CommonUtils.isNetworkAvailable(mContext)) {
							updatePhoneOrName = true;
							InteNetUtils.getInstance(mContext)
									.updateEnterpriseShortPhone(id, "",
											pecketName, requestCallBack2);
							sendBroadcast(new Intent(
									AndroidConfig.refreshEnterpriseMember));
						}
					}
				});
				inputDialog.show();
			}
			break;
		case R.id.rl_mp:
			inputDialog = new InputDialog(mContext, R.style.MyDialogStyle);
			inputDialog.setContent("通讯录名片", "请输入新的通讯录名片", "确认", "取消");
			inputDialog.setEditContent(enterprise.getRemark());
			inputDialog.setCancleListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					inputDialog.dismiss();
				}
			});
			inputDialog.setOKListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					pecketName = "";
					pecketName = inputDialog.getInputText();
					if (CommonUtils.isEmpty(pecketName)) {
						ToastUtils.Infotoast(mContext, "通讯录名片不能为空!");
						return;
					}

					if (!CommonUtils.StringIsSurpass(pecketName, 1, 10)) {
						ToastUtils.Infotoast(mContext, "通讯录名片限制在0-10个字数");
						return;
					}

					if (CommonUtils.isNetworkAvailable(mContext)) {
						updatePhoneOrName = false;
						InteNetUtils.getInstance(mContext)
								.updateEnterpriseRemarkName(id, pecketName,
										requestCallBack2);
						sendBroadcast(new Intent(
								AndroidConfig.refreshEnterpriseList));
					}
				}
			});
			inputDialog.show();
			break;
		case R.id.rl_add_common:

			startAnimActivity2Obj(ActivityEnterpriseMyAddContacts.class,
					"enterpriseId", id);
			break;
		}

	}

	private RequestCallBack<String> requestCallBack2 = new RequestCallBack<String>() {
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(arg0.result);
				if (jsonObject.optString("ret_num").equals("0")) {
					if (updatePhoneOrName) {
						ToastUtils.Infotoast(mContext, "修改其它号码成功!");
						enterprise.setPhone(pecketName);
						tv_phone.setText(pecketName);
					} else {
						ToastUtils.Infotoast(mContext, "修改通讯录名片成功!");
						tv_mp.setText(pecketName);
						enterprise.setRemark(pecketName);
						
						sendBroadcast(new Intent(
								AndroidConfig.refreshEnterpriseMember));
						sendBroadcast(new Intent(
								AndroidConfig.refreshEnterpriseList));
					}
					inputDialog.dismiss();
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
								jsonObject.optString("ret_msg"));
					}
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			ToastUtils.Infotoast(mContext, "网络不可用,请重试");
		}
	};

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent data) {
		switch (arg0) {
		case UPDATE_ENTERPRISE:
			if (data != null) {
				Enterprise enterprise2 = (Enterprise) data
						.getSerializableExtra("enterprise");
				tv_name.setText(enterprise2.getName());
				tv_info.setText(enterprise2.getDescription());
				tv_phone.setText(enterprise2.getPhone());
				user.setUpdate(true);
			}
			break;
		case INVITE_FRIEND:
			if (data != null) {
				user.setUpdate(true);
				AnimFinsh();
			}
			break;
		}
		super.onActivityResult(arg0, arg1, data);
	}

	private RequestCallBack<String> requestCallBack = new RequestCallBack<String>() {

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(arg0.result);
				if (jsonObject.optString("ret_num").equals("0")) {
					ToastUtils.Infotoast(mContext, "退出政企通讯录成功!");
					Intent intent = new Intent();
					intent.putExtra("exit", "exit");
					user.setUpdate(true);
					setResult(1000, intent);
					AnimFinsh();
				} else {
					ToastUtils.Infotoast(mContext, "退出政企通讯录失败!");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			ToastUtils.Infotoast(mContext, "网络不可用,请重试!");
		}
	};
	private TextView name;
	private View zz;
	private MyBroadcastReceiver myBroadcastReceiver;
	private LinearLayout addBut;

	class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			InteNetUtils.getInstance(mContext).enterprisesDetail(id,
					mRequestCallBack);
		}

	}
}
