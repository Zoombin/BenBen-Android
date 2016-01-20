package com.xunao.test.ui.item;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.xunao.test.R;
import com.xunao.test.base.BaseActivity;
import com.xunao.test.bean.Contacts;
import com.xunao.test.bean.PhoneInfo;
import com.xunao.test.bean.PublicMessage;
import com.xunao.test.config.AndroidConfig;
import com.xunao.test.exception.NetRequestException;
import com.xunao.test.net.InteNetUtils;
import com.xunao.test.utils.CommonUtils;
import com.xunao.test.utils.ToastUtils;

import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityAddNewHxFriend extends BaseActivity {
	protected static final int CHOICE_GROUP = 1000;
	private EditText et_phone;
	private EditText et_name;
	private RelativeLayout rl_add_group;
	private String groupId;
	private TextView tv_group;
    private PublicMessage publicMessage;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_add_friend_byself);
		initTitle_Right_Left_bar("联系人", "", "保存",
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
        publicMessage = (PublicMessage) getIntent().getSerializableExtra("publicMessage");
        et_name.setText(publicMessage.getNick_name());
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
                    InteNetUtils.getInstance(mContext).addFirend(
                            publicMessage.getHuanxin_username(),name,groupId, mRequestCallBack);
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
            Contacts contacts = new Contacts();
			contacts.parseJSONSingle2(jsonObject);

            dbUtil.saveOrUpdate(contacts);
            ArrayList<PhoneInfo> phones = contacts
                    .getPhones();
            if (phones != null)
                dbUtil.saveOrUpdateAll(phones);
            publicMessage.setStatus(PublicMessage.AGREE);
            dbUtil.saveOrUpdate(publicMessage);

            try {
                EMChatManager
                        .getInstance()
                        .refuseInvitation(
                                publicMessage.getHuanxin_username());
            } catch (EaseMobException e) {
                // TODO Auto-generated
                // catch
                // block
                e.printStackTrace();
            }

            // 获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
            EMConversation conversation = EMChatManager
                    .getInstance()
                    .getConversation(
                            contacts.getHuanxin_username());

            // 创建一条文本消息
            final EMMessage message = EMMessage
                    .createSendMessage(EMMessage.Type.TXT);
            // 如果是群聊，设置chattype,默认是单聊
            // 设置消息body
            TextMessageBody txtBody = new TextMessageBody(
                    "我们已是好友,开始聊天吧");

            message.setAttribute("t1",2);
            message.setAttribute(
                    "friend_contact_new_id",
                    jsonObject
                            .optString("targetContactID"));
            message.addBody(txtBody);
            // 设置接收人
            message.setReceipt(contacts
                    .getHuanxin_username());
            // 把消息加入到此会话对象中
            conversation
                    .addMessage(message);
            // 发送消息

            new Handler().postDelayed(
                    new Runnable() {

                        @Override
                        public void run() {
                            try {
                                EMChatManager
                                        .getInstance()
                                        .sendMessage(
                                                message);
                            } catch (EaseMobException e) {
                                e.printStackTrace();
                            }
                        }
                    }, 1000);

            setResult(AndroidConfig.writeFriendResultCode);
            sendBroadcast(new Intent(
                    AndroidConfig.ContactsRefresh));
            AnimFinsh();
        } catch (NetRequestException e) {
            com.xunao.test.base.Error error = e.getError();
            error.print(mContext);
            if (error.getErrorId() == 5218
                    || error.getErrorId() == 5208) {

                try {
                    EMChatManager
                            .getInstance()
                            .refuseInvitation(
                                    publicMessage.getHuanxin_username());
                } catch (EaseMobException e1) {
                    // TODO
                    // Auto-generated
                    // catch
                    // block
                    e.printStackTrace();
                }

                publicMessage.setStatus(PublicMessage.AGREE);

                try {
                    dbUtil.saveOrUpdate(publicMessage);
                } catch (DbException e1) {
                    // TODO
                    // Auto-generated
                    // catch block
                    e1.printStackTrace();
                }

                // 获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
                EMConversation conversation = EMChatManager
                        .getInstance()
                        .getConversation(
                                publicMessage.getHuanxin_username());
                // 创建一条文本消息
                final EMMessage message = EMMessage
                        .createSendMessage(EMMessage.Type.TXT);
                // 如果是群聊，设置chattype,默认是单聊
                // 设置消息body
                TextMessageBody txtBody = new TextMessageBody(
                        "我们已是好友,开始聊天吧");
                message.addBody(txtBody);

                message.setAttribute("t1",2);

                // 设置接收人
                message.setReceipt(publicMessage
                        .getHuanxin_username());

                // 把消息加入到此会话对象中
                conversation
                        .addMessage(message);
                // 发送消息

                new Handler()
                        .postDelayed(
                                new Runnable() {

                                    @Override
                                    public void run() {
                                        try {
                                            EMChatManager
                                                    .getInstance()
                                                    .sendMessage(
                                                            message);
                                        } catch (EaseMobException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, 1000);
                setResult(AndroidConfig.writeFriendResultCode);
                sendBroadcast(new Intent(
                        AndroidConfig.ContactsRefresh));
                ToastUtils.Errortoast(mContext, "同意成功");
                AnimFinsh();
                return;
            }
            e.printStackTrace();
        } catch (DbException e) {
            // TODO Auto-generated catch
            // block
            e.printStackTrace();
        }

    }

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Errortoast(mContext, "同意失败");
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
