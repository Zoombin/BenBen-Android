package com.xunao.benben.ui.item;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Enterprise;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

public class ActivityCreatedEnterpriseContacts extends BaseActivity {
	private EditText et_enterprise_name;
	private EditText et_enterprise_phone;
	private EditText et_enterprise_intro;
	private TextView tv_enterprise_area;
	private RadioButton rbn_1;
	private RadioButton rbn_2;

	private boolean isUpdate = false;
	private Enterprise enterprise;
	private Enterprise enterprise2;

	private String[] addressId;
	private String addressName;
	private final static int CHOICE_ADDRESS = 1;

	private RelativeLayout tv_enterprice_rl;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_created_enterprise_contacts);
	}

	@Override
	public void initView(Bundle savedInstanceState) {

		if (isUpdate) {
			initTitle_Right_Left_bar("修改政企通讯录", "", "完成",
					R.drawable.icon_com_title_left, 0);
		} else {
			initTitle_Right_Left_bar("新建政企通讯录", "", "完成",
					R.drawable.icon_com_title_left, 0);
		}
		et_enterprise_name = (EditText) findViewById(R.id.et_enterprise_name);
		et_enterprise_phone = (EditText) findViewById(R.id.et_enterprise_phone);
		et_enterprise_intro = (EditText) findViewById(R.id.et_enterprise_intro);
		tv_enterprise_area = (TextView) findViewById(R.id.tv_enterprise_area);

		tv_enterprice_rl = (RelativeLayout) findViewById(R.id.tv_enterprice_rl);
		rbn_1 = (RadioButton) findViewById(R.id.rbn_1);
		rbn_2 = (RadioButton) findViewById(R.id.rbn_2);

		rbn_1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					et_enterprise_phone.setVisibility(View.GONE);
				} else {
					et_enterprise_phone.setVisibility(View.VISIBLE);
				}
			}
		});

		rbn_2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					et_enterprise_phone.setVisibility(View.VISIBLE);
				} else {
					et_enterprise_phone.setVisibility(View.GONE);
				}
			}
		});

		if (rbn_1.isChecked()) {
			et_enterprise_phone.setVisibility(View.GONE);
		} else {
			et_enterprise_phone.setVisibility(View.VISIBLE);
		}

		tv_enterprice_rl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startAnimActivityForResult2(ActivityChoiceAddress.class,
						CHOICE_ADDRESS, "level", "3");
			}
		});

	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		enterprise = new Enterprise();
		enterprise = (Enterprise) intent.getSerializableExtra("enterprise");

		if (enterprise != null) {
			isUpdate = true;
			et_enterprise_name.setText(enterprise.getName());
			et_enterprise_phone.setText(enterprise.getPhone());
			et_enterprise_intro.setText(enterprise.getDescription());
			tv_enterprise_area.setText(enterprise.getArea());
			et_enterprise_name.setFocusable(false);

			if (enterprise.getType().equals("1")) {
				rbn_1.setChecked(true);
				et_enterprise_phone.setVisibility(View.GONE);
			} else {
				rbn_2.setChecked(true);
				et_enterprise_phone.setVisibility(View.VISIBLE);
			}
			initTitle_Right_Left_bar("修改通讯录", "", "完成",
					R.drawable.icon_com_title_left, 0);
		} else {
			isUpdate = false;
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
				String name = et_enterprise_name.getText().toString().trim();
				String phone = et_enterprise_phone.getText().toString().trim();
				String intro = et_enterprise_intro.getText().toString().trim();
				int type = 1;

				if (CommonUtils.isEmpty(name)) {
					ToastUtils.Infotoast(mContext, "请输入通讯录名称!");
					return;
				}

				if (!CommonUtils.StringIsSurpass2(name, 2, 20)) {
					ToastUtils.Infotoast(mContext, "名称限制在1-20字之间");
					return;
				}

//				if (name.length() > 20) {
//					ToastUtils.Infotoast(mContext, "名称限制在1-20字数之间");
//					return;
//				}
				if (addressId == null) {
					ToastUtils.Infotoast(mContext, "请选择地址");
					return;
				}
				if (CommonUtils.isEmpty(intro)) {
					ToastUtils.Infotoast(mContext, "请输入通讯录介绍!");
					return;
				}

				if (rbn_2.isChecked()) {
					type = 2;
					if (TextUtils.isEmpty(phone)) {
						ToastUtils.Infotoast(mContext, "请输入短号!");
						return;
					}
//					if (!CommonUtils.StringIsSurpass(name, 3, 6)) {
//						ToastUtils.Errortoast(mContext, "短号限制在3—6字");
//						return;
//					}
					
					if(phone.length() < 3 || phone.length() > 6){
						ToastUtils.Errortoast(mContext, "短号限制在3—6个数字");
						return;
					}

				} else {
					type = 1;
				}

				if (isUpdate) {
					enterprise2 = new Enterprise();
					enterprise2.setName(name);
					enterprise2.setDescription(intro);
					enterprise2.setPhone(phone);

					InteNetUtils.getInstance(mContext).updateEnterprises(
							enterprise.getId(), name, type, phone, intro,
							addressId, mRequestCallBack);
				} else {
					InteNetUtils.getInstance(mContext).addEnterprises(name,
							type, phone, intro, addressId, mRequestCallBack);
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
			if (isUpdate) {
				ToastUtils.Infotoast(mContext, "修改政企通讯录成功!");
				Intent intent = new Intent();
				intent.putExtra("enterprise", enterprise2);
				setResult(1, intent);
				AnimFinsh();
			} else {
				ToastUtils.Infotoast(mContext, "新建政企通讯录成功!");
				user.setUpdate(true);
				AnimFinsh();
			}

		} else {
			ToastUtils.Infotoast(mContext, jsonObject.optString("ret_msg"));
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Infotoast(mContext, "网络不可用,请重试");
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent data) {
		switch (arg0) {
		case CHOICE_ADDRESS:
			if (data != null) {
				addressId = null;
				tv_enterprise_area.setText(data.getStringExtra("address"));
				addressId = data.getStringArrayExtra("addressId");
			}
			break;
		}
		super.onActivityResult(arg0, arg1, data);
	}

}
