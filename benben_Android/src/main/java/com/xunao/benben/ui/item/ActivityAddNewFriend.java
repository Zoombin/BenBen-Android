package com.xunao.benben.ui.item;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.ContactsObject;
import com.xunao.benben.bean.LatelyLinkeMan;
import com.xunao.benben.bean.PhoneInfo;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityAddNewFriend extends BaseActivity {
	protected static final int CHOICE_GROUP = 1000;
	private EditText et_phone;
	private EditText et_name;
	private RelativeLayout rl_add_group;
	private String groupId;
	private TextView tv_group;
    private LatelyLinkeMan latelyLinkeMan;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_add_friend_byself);
		initTitle_Right_Left_bar("新建联系人", "", "新建",
				R.drawable.icon_com_title_left, 0);

		et_phone = (EditText) findViewById(R.id.et_phone);
        et_phone.setVisibility(View.GONE);
		et_name = (EditText) findViewById(R.id.et_name);
        et_name.setHint("请设置备注");
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
        latelyLinkeMan = (LatelyLinkeMan) getIntent().getSerializableExtra("latelyLinkeMan");
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
				String name = et_name.getText().toString().trim();
				String group = tv_group.getText().toString().trim();
				if (TextUtils.isEmpty(name)) {
					ToastUtils.Errortoast(mContext, "请设置好友备注!");
					return;
				}
				
				if (!CommonUtils.StringIsSurpass2(name, 2, 12)) {
					ToastUtils.Errortoast(mContext, "备注限制在1-12个字之间!");
					return;
				}

				if (group.equals("添加到分组")) {
					ToastUtils.Errortoast(mContext, "请选择分组!");
					return;
				}
				if (CommonUtils.isNetworkAvailable(mContext)) {
                    InteNetUtils.getInstance(mContext).Addcontact(name, latelyLinkeMan.getLinkeManPhone(), groupId, mRequestCallBack);
				}else{
                    ToastUtils.Errortoast(mContext, "网络不可用!");
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
        try {
            if(jsonObject.optInt("ret_num") == 0) {
                Contacts contacts  = new Contacts();
                contacts.parseJSON(jsonObject.getJSONObject("user"));
                ContactsObject contactsObject = new ContactsObject();
                contactsObject = contactsObject
                        .contactsSynchroparseJSON(jsonObject);

                latelyLinkeMan.setCid(contacts.getId());
                latelyLinkeMan.setLinkeManName(contacts.getName());

                dbUtil.update(latelyLinkeMan,
                        WhereBuilder.b("linkeManPhone", "=", latelyLinkeMan.getLinkeManPhone()), "cid", "linkeManName");

                dbUtil.saveOrUpdate(contacts);
                dbUtil.saveOrUpdateAll(contacts.getPhones());


                ToastUtils.Infotoast(mContext, "新建联系人成功!");
                sendBroadcast(new Intent(AndroidConfig.ContactsRefresh));
                setResult(AndroidConfig.writeFriendRefreshResultCode);
                AnimFinsh();
            }else{
                ToastUtils.Infotoast(mContext, jsonObject.optString("ret_msg"));

            }
        } catch (NetRequestException e) {
            ToastUtils.Infotoast(mContext, "新建联系人失败!");
            e.printStackTrace();
        } catch (DbException e) {
            ToastUtils.Infotoast(mContext, "新建联系人失败!");
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Errortoast(mContext, "新建联系人失败");
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
