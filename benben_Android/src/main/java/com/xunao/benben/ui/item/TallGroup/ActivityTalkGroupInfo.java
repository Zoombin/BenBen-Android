package com.xunao.benben.ui.item.TallGroup;

import java.io.IOException;
import java.io.Serializable;

import in.srain.cube.image.CubeImageView;

import org.jivesoftware.smack.ChatManager;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.ContactsGroup;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.bean.TalkGroup;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoMsgHint;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.dialog.InputDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.hx.chatuidemo.activity.RecorderVideoActivity;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.item.ActivityChoseGroupLeader;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

public class ActivityTalkGroupInfo extends BaseActivity implements
		OnClickListener {

	private TalkGroup mTalkGroup;
	private String mTalkGroupID;
	private InputDialog inputDialog;
	private CubeImageView talk_group_poster;
	private TextView talk_group_name;
	private TextView talk_group_level;
	private TextView talk_group_address;

	private TextView talk_group_info;
	private TextView talk_group_count;

	private TextView send_message_blue; // 加入
	private TextView send_message_red; // 退出

	private View talk_group_count_arr;

	private View talk_group_arr;

	private String name;
    private RelativeLayout rl_switch_message;
    private ImageView iv_switch_open_message,iv_switch_close_message;
    private LinearLayout group_manage_change;
    private LinearLayout group_my_contact;


	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_talkgroup_info);
		initdefaultImage(R.drawable.ic_group_poster);
		mGroupInfoBroadCast = new GroupInfoBroadCast();
		registerReceiver(mGroupInfoBroadCast, new IntentFilter(
				AndroidConfig.refreshGroupInfo));
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("群资料", "", "", R.drawable.icon_com_title_left,
				0);
		wite = findViewById(R.id.wite);
		talk_group_poster = (CubeImageView) findViewById(R.id.talk_group_poster);
		talk_group_name = (TextView) findViewById(R.id.talk_group_name);
		talk_group_level = (TextView) findViewById(R.id.talk_group_level);
		talk_group_address = (TextView) findViewById(R.id.talk_group_address);
		group_add = findViewById(R.id.group_add);

		talk_group_count_arr = findViewById(R.id.talk_group_count_arr);
		talk_group_arr = findViewById(R.id.talk_group_arr);
		group_count = (LinearLayout) findViewById(R.id.group_count);
		group_info = (RelativeLayout) findViewById(R.id.group_info);
		talk_group_myname_box = (LinearLayout) findViewById(R.id.talk_group_myname_box);

		talk_group_info = (TextView) findViewById(R.id.talk_group_info);
		talk_group_myname = (TextView) findViewById(R.id.talk_group_myname);
		talk_group_count = (TextView) findViewById(R.id.talk_group_count);

		send_message_blue = (TextView) findViewById(R.id.send_message_blue);
		send_message_red = (TextView) findViewById(R.id.send_message_red);

		talk_group_poster.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				CommonUtils.showPoster(mContext, mTalkGroup.getPoster(),
						cubeimageLoader);
			}
		});

        rl_switch_message = (RelativeLayout) findViewById(R.id.rl_switch_message);
        rl_switch_message.setOnClickListener(this);
        iv_switch_open_message = (ImageView) findViewById(R.id.iv_switch_open_message);
        iv_switch_close_message = (ImageView) findViewById(R.id.iv_switch_close_message);

        group_manage_change = (LinearLayout) findViewById(R.id.group_manage_change);
        group_manage_change.setOnClickListener(this);
        group_my_contact = (LinearLayout) findViewById(R.id.group_my_contact);
        group_my_contact.setOnClickListener(this);
	}

	int type;

	private LinearLayout group_count;

	private RelativeLayout group_info;

	private LinearLayout talk_group_myname_box;

	@Override
	public void initDate(Bundle savedInstanceState) {
		mTalkGroupID = (String) getIntent().getSerializableExtra("FIND");
		if (!CommonUtils.isEmpty(mTalkGroupID)) {
			type = 1;
		} else {
			mTalkGroupID = (String) getIntent().getSerializableExtra(
					"TalkGroupID");
			type = 0;
		}

		showLoding("请稍后...");
		refreshGroupInfo();
	}

	protected void refreshGroupInfo() {
		//InteNetUtils.getInstance(mContext).getSingleGroupInfo(mTalkGroupID,
		InteNetUtils.getInstance(mContext).getSingleGroupInfo(mTalkGroupID,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						dissLoding();
						try {
							mTalkGroup = new TalkGroup();
							JSONObject jsonObj = new JSONObject(arg0.result);
							mTalkGroup.checkJson(jsonObj);

							JSONObject optJSONObject = jsonObj
									.optJSONObject("group_info");

							mTalkGroup.parseJSON(optJSONObject);
							wite.setVisibility(View.GONE);
							addData(mTalkGroup);
						} catch (JSONException e) {
							ToastUtils.Errortoast(mContext, "当前网络不可用");
							e.printStackTrace();
						} catch (NetRequestException e) {
							e.printStackTrace();
							e.getError().print(mContext);
						}
						//

					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						dissLoding();
						ToastUtils.Errortoast(mContext, "当前网络不可用");
					}
				});
	}

	protected void addData(final TalkGroup mTalkGroup) {

		if (type == 0) {
			talk_group_count_arr.setVisibility(View.VISIBLE);
			talk_group_myname_box.setVisibility(View.VISIBLE);
			talk_group_myname.setText(mTalkGroup.getGroup_nick_name());
			if (Integer.parseInt(mTalkGroup.getIs_admin()) == 1) {
				talk_group_arr.setVisibility(View.VISIBLE);

				group_info
						.setBackgroundResource(R.drawable.fragment_private_bg);
				group_info.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 修改群信息
						startAnimActivityForResult(
								ActivityUpdateTallGroup.class, "TalkGroup",
								mTalkGroup,
								AndroidConfig.writeFriendRequestCode);

					}
				});

			} else {
				talk_group_arr.setVisibility(View.GONE);
			}
			group_count.setBackgroundResource(R.drawable.fragment_private_bg);
			group_count.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 查看人员
					startAnimActivity2Obj(ActivityTalkGroupMember.class, "TG",
							mTalkGroup);
				}
			});

			talk_group_myname_box.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					inputDialog = new InputDialog(mContext,
							R.style.MyDialogStyle);
					inputDialog.setEditContent(mTalkGroup.getGroup_nick_name());
					inputDialog.setContent("修改群名片", "请输入新的群名片", "确认", "取消");
					inputDialog.setCancleListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							inputDialog.dismiss();
						}
					});
					inputDialog.setOKListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							name = inputDialog.getInputText();
							
							if (!CommonUtils.StringIsSurpass(name, 0, 12)) {
								ToastUtils.Infotoast(mContext, "我的名片不可超过12字");
								return;
							}
							
							if (CommonUtils.isNetworkAvailable(mContext))
								InteNetUtils.getInstance(mContext)
										.EditGroupNmae(name,
												mTalkGroup.getId(), changeName);
						}
					});
					inputDialog.show();
				}
			});

		} else {
			talk_group_myname_box.setVisibility(View.GONE);
			talk_group_arr.setVisibility(View.GONE);
			talk_group_count_arr.setVisibility(View.GONE);
			group_add.setVisibility(View.GONE);
		}
		CommonUtils.startImageLoader(cubeimageLoader, mTalkGroup.getPoster(),
				talk_group_poster);
		talk_group_name.setText(mTalkGroup.getName());
		talk_group_level.setText("LV" + mTalkGroup.getLevel());
		talk_group_address.setText(mTalkGroup.getShow_id() + "    "
				+ mTalkGroup.getPro_city());
		talk_group_info.setText(mTalkGroup.getDescription());
		talk_group_count.setText("成员（" + mTalkGroup.getNumber() + "人/"
				+ mTalkGroup.getMaxuser() + "人）");

		if (mApplication.mTalkGroupMap.get(mTalkGroup.getHuanxin_groupid()) == null) {
			send_message_blue.setVisibility(View.VISIBLE);
			send_message_red.setVisibility(View.GONE);

		} else {
			send_message_blue.setVisibility(View.GONE);
			send_message_red.setVisibility(View.VISIBLE);

			group_add.setVisibility(View.VISIBLE);
			if (Integer.parseInt(mTalkGroup.getIs_admin()) == 1) {
				send_message_red.setTag(0);
				send_message_red.setText("解散群组");
                rl_switch_message.setVisibility(View.GONE);
                group_manage_change.setVisibility(View.VISIBLE);
                group_my_contact.setVisibility(View.GONE);
			} else {
				// group_add.setVisibility(View.GONE);
                group_manage_change.setVisibility(View.GONE);
                group_my_contact.setVisibility(View.VISIBLE);
				send_message_red.setTag(1);
				send_message_red.setText("退出群组");
                rl_switch_message.setVisibility(View.GONE);
                EMGroup emGroup =EMGroupManager.getInstance().getGroup(mTalkGroup.getHuanxin_groupid());
                if(emGroup.isMsgBlocked()){
                    iv_switch_open_message.setVisibility(View.VISIBLE);
                    iv_switch_close_message.setVisibility(View.GONE);
                }else{
                    iv_switch_open_message.setVisibility(View.GONE);
                    iv_switch_close_message.setVisibility(View.VISIBLE);
                }
			}

		}



	}

	/**
	 * 修改群名片
	 */
	public RequestCallBack<String> changeName = new RequestCallBack<String>() {

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			inputDialog.dismiss();
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(arg0.result);
				SuccessMsg msg = new SuccessMsg();
				msg.checkJson(jsonObject);
				mTalkGroup.setGroup_nick_name(name);
				talk_group_myname.setText(name);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NetRequestException e) {
				e.getError().print(mContext);
				e.printStackTrace();
			}

		}

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			ToastUtils.Errortoast(mContext, arg1);
			inputDialog.dismiss();
		}
	};
	private TextView talk_group_myname;
	private View decorView;
	private View wite;
	private GroupInfoBroadCast mGroupInfoBroadCast;
	private View group_add;

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {

		if (arg1 == AndroidConfig.writeFriendRefreshResultCode) {
			// 刷新界面
			mTalkGroup = (TalkGroup) arg2.getSerializableExtra("TG");
			addData(mTalkGroup);
			mApplication.mTalkGroupMap.put(mTalkGroup.getHuanxin_groupid(),
					mTalkGroup);
			setResult(AndroidConfig.writeFriendRefreshResultCode, arg2);
		}

	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(this);

		group_add.setOnClickListener(this);
		send_message_blue.setOnClickListener(this);
		send_message_red.setOnClickListener(this);
	}

	@Override
	protected void onHttpStart() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {

		SuccessMsg successMsg = new SuccessMsg();

		try {

			successMsg.parseJSON(jsonObject);
			setResult(AndroidConfig.exitActivity);
			sendBroadcast(new Intent(AndroidConfig.RefreshTalkGroup));
			sendBroadcast(new Intent(AndroidConfig.ContactsRefresh));
			AnimFinsh();
			// 操作成功
		} catch (NetRequestException e) {
			e.printStackTrace();
			e.getError().print(mContext);
		}

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 头部左侧点击
		case R.id.com_title_bar_left_bt:
		case R.id.com_title_bar_left_tv:
			AnimFinsh();
			break;

		case R.id.send_message_blue:
			// if (CommonUtils.isNetworkAvailable(mContext))
			// InteNetUtils.getInstance(mContext).joinTalkGroup(
			// mTalkGroup.getHuanxin_groupid(),
			// user.getHuanxin_username(), mRequestCallBack);
			if (CommonUtils.isNetworkAvailable(mContext)) {
				// 需要申请和验证才能加入的，即group.isMembersOnly()为true，调用下面方法
				new Thread() {

					public void run() {
						try {
							EMGroupManager.getInstance().applyJoinToGroup(
									mTalkGroup.getHuanxin_groupid(),
									user.getUserNickname());

							mContext.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(
											mContext, R.style.MyDialog1);
									hint.setContent("申请加入群组成功");
									hint.setOKListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											hint.dismiss();
										}
									});
									hint.show();
								}
							});
						} catch (EaseMobException e) {
							e.printStackTrace();
                            mContext.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    ToastUtils.Errortoast(mContext, "申请加入失败");
                                }
                            });

						}
					};
				}.start();
			}
			break;
		case R.id.send_message_red:

			Integer type = (Integer) send_message_red.getTag();
			String content = "";
			switch (type) {
			case 0:// 解散群组
				content = "确定解散群组";
				break;
			case 1:// 退出群组
				content = "确定退出群组";
				break;

			}

			final InfoMsgHint hint = new InfoMsgHint(mContext,
					R.style.MyDialog1);
			hint.setContent(content, "", "确定", "取消");
			hint.setCancleListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					hint.dismiss();
				}
			});
			hint.setOKListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (CommonUtils.isNetworkAvailable(mContext)) {
						hint.dismiss();
						// EMConversation conversation = EMChatManager
						// .getInstance().getConversation(
						// mTalkGroup.getHuanxin_groupid());
						// // 创建一条文本消息
						// EMMessage message = EMMessage
						// .createSendMessage(EMMessage.Type.TXT);
						// message.setChatType(ChatType.GroupChat);
						// TextMessageBody txtBody = new TextMessageBody(user
						// .getUserNickname() + "退出该群&XUNAOEXIT");
						// message.addBody(txtBody);
						// // 设置接收人
						// message.setReceipt(mTalkGroup.getHuanxin_groupid());
						// // 把消息加入到此会话对象中
						// conversation.addMessage(message);
						// // 发送消息
						// EMChatManager.getInstance().sendMessage(message,
						// new EMCallBack() {
						//
						// @Override
						// public void onError(int arg0, String arg1) {
						// }
						//
						// @Override
						// public void onProgress(int arg0, String arg1) {
						//
						// }
						//
						// @Override
						// public void onSuccess() {
						// }
						// });

						InteNetUtils.getInstance(mContext).quitTalkGroup(
								mTalkGroup.getId(), mRequestCallBack);
					}
				}
			});
			hint.show();

			break;
		case R.id.group_add:// 邀请新成员
			startAnimActivity2Obj(ActivityAddGroupMember.class, "GROUPID",
					mTalkGroup.getId());
			break;
            case R.id.rl_switch_message:
                if (iv_switch_open_message.getVisibility() == View.VISIBLE) {
                    showLoding("");
                    InteNetUtils.getInstance(mContext).setFreeMode(
                            mTalkGroup.getId(), 0, user.getToken(), new RequestCallBack<String>() {
                                @Override
                                public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                                    new Thread(new Runnable() {
                                        public void run() {
                                            try {
                                                EMGroupManager.getInstance().unblockGroupMessage(mTalkGroup.getHuanxin_groupid());
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        iv_switch_open_message.setVisibility(View.INVISIBLE);
                                                        iv_switch_close_message.setVisibility(View.VISIBLE);
                                                        dissLoding();
                                                    }
                                                });
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        dissLoding();
                                                        ToastUtils.Errortoast(mContext,"解除屏蔽失败");
                                                    }
                                                });

                                            }
                                        }
                                    }).start();

                                }

                                @Override
                                public void onFailure(HttpException e, String s) {
                                    ToastUtils.Errortoast(mContext,"解除屏蔽失败");
                                }
                            });

                }else{
                    showLoding("");
                    InteNetUtils.getInstance(mContext).setFreeMode(
                            mTalkGroup.getId(), 1, user.getToken(), new RequestCallBack<String>() {
                                @Override
                                public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                                    new Thread(new Runnable() {
                                        public void run() {
                                            try {
                                                EMGroupManager.getInstance().blockGroupMessage(mTalkGroup.getHuanxin_groupid());
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        iv_switch_open_message.setVisibility(View.VISIBLE);
                                                        iv_switch_close_message.setVisibility(View.INVISIBLE);
                                                        dissLoding();
                                                    }
                                                });
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        dissLoding();
                                                        ToastUtils.Errortoast(mContext,"屏蔽群组失败");
                                                    }
                                                });

                                            }
                                        }
                                    }).start();

                                }

                                @Override
                                public void onFailure(HttpException e, String s) {
                                    ToastUtils.Errortoast(mContext,"屏蔽群组失败");
                                }
                            });
                }



                break;

            case R.id.group_manage_change:
                Intent intent = new Intent(this,ActivityChoseGroupLeader.class);
                intent.putExtra("mTalkGroupID", mTalkGroup.getId());
                startActivityForResult(intent,
                        AndroidConfig.ContactsFragmentRequestCode);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
            case R.id.group_my_contact:
                startAnimActivity2Obj(ActivityMyContactMember.class, "TG",
                        mTalkGroup);


                break;
            default:
                break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mGroupInfoBroadCast);

	}

	class GroupInfoBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			refreshGroupInfo();
			sendBroadcast(new Intent(AndroidConfig.refreshGroup));
		}

	}

}
