package com.xunao.benben.ui.item;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.PhoneInfo;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityAddFriendDetail extends BaseActivity {
	protected static final int CHOICE_GROUP = 1000;
	private EditText et_phone;
	private EditText et_name;
    private EditText et_reason;
	private RelativeLayout rl_add_group;
	private String groupId;
	private TextView tv_group;
    private String from_huanxin;
    private String to_huanxin;
    String reason;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_add_friend_byself);
		initTitle_Right_Left_bar("好友邀请", "", "保存",
				R.drawable.icon_com_title_left, 0);

		et_phone = (EditText) findViewById(R.id.et_phone);
        et_phone.setVisibility(View.GONE);
		et_name = (EditText) findViewById(R.id.et_name);
        et_name.setHint("请设置备注");
        et_reason = (EditText) findViewById(R.id.et_reason);
        et_reason.setVisibility(View.VISIBLE);
		rl_add_group = (RelativeLayout) findViewById(R.id.rl_add_group);
		tv_group = (TextView) findViewById(R.id.tv_group);
		rl_add_group.setOnClickListener(new View.OnClickListener() {
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
//        publicMessage = (PublicMessage) getIntent().getSerializableExtra("publicMessage");
        from_huanxin = getIntent().getStringExtra("from_huanxin");
        to_huanxin = getIntent().getStringExtra("to_huanxin");
    }

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AnimFinsh();
			}
		});

		setOnRightClickLinester(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String name = et_name.getText().toString().trim();
                reason = String.valueOf(et_reason.getText()).trim();
				String group = tv_group.getText().toString().trim();
				if (TextUtils.isEmpty(name)) {
					ToastUtils.Errortoast(mContext, "请设置好友备注!");
					return;
				}
				
				if (!CommonUtils.StringIsSurpass2(name, 2, 12)) {
					ToastUtils.Errortoast(mContext, "备注限制在1-12个字之间!");
					return;
				}

                if (!CommonUtils.StringIsSurpass2(reason, 0, 25)) {
                    ToastUtils.Errortoast(mContext, "验证信息限制在0-25个字之间!");
                    return;
                }

				if (group.equals("添加到分组")) {
					ToastUtils.Errortoast(mContext, "请选择分组!");
					return;
				}
				if (CommonUtils.isNetworkAvailable(mContext)) {
                    InteNetUtils.getInstance(mContext).applyFriend(
                            from_huanxin, to_huanxin, name, groupId, mRequestCallBack);
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
        if(jsonObject.optInt("ret_num")==0){
            try {
                EMContactManager.getInstance().addContact(
                        to_huanxin, reason);
                final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(
                        mContext, R.style.MyDialog1);
                hint.show();
                hint.setOKListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        hint.dismiss();
                        AnimFinsh();
                    }
                });
            } catch (EaseMobException e) {
                ToastUtils.Errortoast(mContext, "好友邀请发送失败");
                e.printStackTrace();
            }
        }else{
            ToastUtils.Errortoast(mContext, "好友邀请发送失败");
        }


    }

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Errortoast(mContext, "邀请失败");
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
