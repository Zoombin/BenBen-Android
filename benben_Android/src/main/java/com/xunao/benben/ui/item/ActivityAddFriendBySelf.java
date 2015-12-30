package com.xunao.benben.ui.item;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.PhoneInfo;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityLogin;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.RegexUtils;
import com.xunao.benben.utils.ToastUtils;

public class ActivityAddFriendBySelf extends BaseActivity {
	protected static final int CHOICE_GROUP = 1000;
	private EditText et_phone;
	private EditText et_name;
	private RelativeLayout rl_add_group;
	private String groupId;
	private TextView tv_group;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_add_friend_byself);
		initTitle_Right_Left_bar("添加好友", "", "添加",
				R.drawable.icon_com_title_left, 0);

		et_phone = (EditText) findViewById(R.id.et_phone);
		et_name = (EditText) findViewById(R.id.et_name);
		rl_add_group = (RelativeLayout) findViewById(R.id.rl_add_group);
		tv_group = (TextView) findViewById(R.id.tv_group);
		rl_add_group.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startAnimActivityForResult(ActivityAddFriendChoiceGroup.class,
						CHOICE_GROUP);
			}
		});
	}

	@Override
	public void initView(Bundle savedInstanceState) {

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
				String phone = et_phone.getText().toString().trim();
				String name = et_name.getText().toString().trim();
				String group = tv_group.getText().toString().trim();
				if (TextUtils.isEmpty(phone) || !RegexUtils.checkNum(phone)) {
					ToastUtils.Errortoast(mContext, "手机号限制在3-16位!");
					return;
				}

				if (TextUtils.isEmpty(name)) {
					ToastUtils.Errortoast(mContext, "请输入好友姓名!");
					return;
				}
				
				if (!CommonUtils.StringIsSurpass2(name, 2, 12)) {
					ToastUtils.Errortoast(mContext, "好友姓名限制在1-12个字之间!");
					return;
				}

				if (group.equals("添加到分组")) {
					ToastUtils.Errortoast(mContext, "请选择分组!");
					return;
				}
				if (CommonUtils.isNetworkAvailable(mContext)) {
					InteNetUtils.getInstance(mContext).addContactsBySelf(phone,
							name, groupId, mRequestCallBack);
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
		String ret_msg = jsonObject.optString("ret_msg");

		if (ret_num.equals("0")) {

			try {
				Contacts contacts = new Contacts();
				JSONObject optJSONObject = jsonObject.optJSONObject("user");
				if(optJSONObject!=null){
				contacts.parseJSON(optJSONObject);
				dbUtil.saveOrUpdate(contacts);
                if(contacts.getPhones()!=null && contacts.getPhones().size()>0)
                    dbUtil.saveOrUpdateAll(contacts.getPhones());

//
//				PhoneInfo info=new PhoneInfo();
//				info.setContacts_id(contacts.getId());
//				info.setHuanxin_username(contacts.getHuanxin_username());
//				info.setIs_baixing(contacts.getIs_baixing());
//				info.setIs_benben(contacts.getIs_benben());
//				info.setName(contacts.getName());
//				info.setPoster(contacts.getPoster());
//				info.setNick_name(optJSONObject.optString("nick_name"));
//				info.setPhone(contacts.getPhone());
//				dbUtil.saveOrUpdate(info);
				
				ToastUtils.Infotoast(mContext, "添加好友成功!");
				sendBroadcast(new Intent(AndroidConfig.ContactsRefresh));
				AnimFinsh();
				}else{
					ToastUtils.Errortoast(mContext, "好友添加失败");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

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
				CrashApplication.getInstance().logout();
				startActivity(new Intent(mContext, ActivityLogin.class));
			}else{
				ToastUtils.Errortoast(mContext, ret_msg);
			}
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Errortoast(mContext, "好友添加失败");
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		switch (arg0) {
		case CHOICE_GROUP:
			if (arg2 != null) {
				groupId = arg2.getStringExtra("groupId");
				tv_group.setText(arg2.getStringExtra("groupName"));
			}
			break;

		default:
			break;
		}
		super.onActivityResult(arg0, arg1, arg2);
	}

}
