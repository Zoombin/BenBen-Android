/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xunao.benben.ui.item;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.Error;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.PhoneInfo;
import com.xunao.benben.bean.PublicMessage;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.utils.ViewHolderUtil;
import com.xunao.benben.view.MyTextView;

/**
 * 申请与通知
 *
 */
public class NewFriendsMsgActivity extends BaseActivity {
	private ListView listView;
	private List<PublicMessage> mPublicMessages;
	private NewFriendsMsgAdapter adapter;
	private NewsBrocastReceiver brocastReceiver;
    private RelativeLayout nodota;
    private MsgDialog msgDialog;

	@Override
	public void onBackPressed() {
		AnimFinsh();
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_new_friends_msg);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
        nodota = (RelativeLayout) findViewById(R.id.nodota);
		listView = (ListView) findViewById(R.id.list);

		initTitleView();
		TitleMode mode = new TitleMode("#068cd9", "", R.drawable.icon_delete_01, new OnClickListener() {
			@Override
			public void onClick(View v) {
                if(mPublicMessages!=null && mPublicMessages.size()>0){
                    msgDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
                    msgDialog.setContent("确定清空吗", "", "确认", "取消");
                    msgDialog.setCancleListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            msgDialog.dismiss();
                        }
                    });
                    msgDialog.setOKListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            msgDialog.dismiss();
                            try {
                                dbUtil.deleteAll(PublicMessage.class);
                                mPublicMessages = new ArrayList<>();
                                nodota.setVisibility(View.VISIBLE);

                            } catch (DbException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            adapter.notifyDataSetChanged();
                            mApplication.getBme().setPublicNum(0);
                            sendBroadcast(new Intent(AndroidConfig.ContactsRefresh));
                        }
                    });
                    msgDialog.show();
                }else{
                    ToastUtils.Errortoast(mContext,"当前无申请与通知消息");
                }
			}
		}, "", R.drawable.ic_back, new OnClickListener() {

			@Override
			public void onClick(View v) {
				mContext.AnimFinsh();
			}
		}, "申请与通知", 0);
		chanageTitle(mode);

		brocastReceiver = new NewsBrocastReceiver();

		registerReceiver(brocastReceiver, new IntentFilter("hasNews"));

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(brocastReceiver);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {

		try {
            mPublicMessages = new ArrayList<>();
            List<PublicMessage> unAgreeMessages = dbUtil.findAll(Selector.from(PublicMessage.class).where("status","=",PublicMessage.UNAGREE)
					.orderBy("id", true));
            if(unAgreeMessages!=null && unAgreeMessages.size()!=0){
                mPublicMessages.addAll(unAgreeMessages);
            }
            List<PublicMessage> publicMessages = dbUtil.findAll(Selector.from(PublicMessage.class).where("status","!=",PublicMessage.UNAGREE)
                    .orderBy("id", true));
            if(publicMessages!=null && publicMessages.size()!=0){
                mPublicMessages.addAll(publicMessages);
            }
            if(mPublicMessages!=null && mPublicMessages.size()>0){
                nodota.setVisibility(View.GONE);
            }else{
                nodota.setVisibility(View.VISIBLE);
            }

		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		adapter = new NewFriendsMsgAdapter();
		listView.setAdapter(adapter);
		mApplication.getBme().setPublicNum(0);
		sendBroadcast(new Intent(AndroidConfig.ContactsRefresh));
	}



    @Override
	public void initLinstener(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		// TODO Auto-generated method stub

	}

	class NewsBrocastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
//				mPublicMessages = dbUtil.findAll(PublicMessage.class);
                mPublicMessages = new ArrayList<>();
                List<PublicMessage> unAgreeMessages = dbUtil.findAll(Selector.from(PublicMessage.class).where("status","=",PublicMessage.UNAGREE)
                        .orderBy("id", true));
                if(unAgreeMessages!=null && unAgreeMessages.size()!=0){
                    mPublicMessages.addAll(unAgreeMessages);
                }
                List<PublicMessage> publicMessages = dbUtil.findAll(Selector.from(PublicMessage.class).where("status","!=",PublicMessage.UNAGREE)
                        .orderBy("id", true));
                if(publicMessages!=null && publicMessages.size()!=0){
                    mPublicMessages.addAll(publicMessages);
                }
                if(mPublicMessages!=null && mPublicMessages.size()>0){
                    nodota.setVisibility(View.GONE);
                }else{
                    nodota.setVisibility(View.VISIBLE);
                }

			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 设置adapter
			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}
		}

	}

	class NewFriendsMsgAdapter extends BaseAdapter {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(NewFriendsMsgActivity.this,
						R.layout.row_invite_msg, null);
			}

			final PublicMessage item = getItem(position);
			item.setIsLook(PublicMessage.LOOKED);
			try {
				dbUtil.saveOrUpdate(item);
			} catch (DbException e) {
				// TODO
				// Auto-generated
				// catch
				// block
				e.printStackTrace();
			}

			RoundedImageView item_iv = ViewHolderUtil.get(convertView,
					R.id.item_iv);
			MyTextView item_name = ViewHolderUtil.get(convertView,
					R.id.item_name);
            MyTextView item_user_name = ViewHolderUtil.get(convertView,
                    R.id.item_user_name);

			MyTextView item_content = ViewHolderUtil.get(convertView,
					R.id.item_friend_content);

			final Button addFriend = ViewHolderUtil.get(convertView,
					R.id.addFriend);

			CommonUtils.startImageLoader(cubeimageLoader, item.getPoster(),
					item_iv);

//			item_iv.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					Intent intent = new Intent(mContext,
//                            ActivityContactsInfo.class);
//					mContext.overridePendingTransition(R.anim.in_from_right,
//							R.anim.out_to_left);
//					intent.putExtra("username", item.getHuanxin_username());
//					startActivity(intent);
//				}
//			});

			switch (item.getStatus()) {
			// 同意
			case PublicMessage.AGREE:
				addFriend.setText("已同意");
				addFriend.setBackgroundResource(R.drawable.but_bg_public_agree);
				switch (item.getClassType()) {
				case PublicMessage.GROUP:
					item_name.setText(item.getName());
					item_content.setText(item.getName() + "请求加入【"
							+ item.getNick_name() + "】");
                    item_user_name.setText("");
					break;
				case PublicMessage.FRIEND:
                    item_name.setText(item.getNick_name());
					item_content.setText(item.getReason());
                    item_user_name.setText("");
					break;
                case PublicMessage.Union:
                    item_name.setText(item.getName());
                    item_user_name.setText(Html.fromHtml("<u>"+item.getNick_name()+"</u>"));
                    item_content.setText(item.getReason());
                    break;
                case PublicMessage.GROUPCHANGE:
                    item_name.setText(item.getName());
                    item_user_name.setText("");
                    item_content.setText(item.getReason());
                    break;
				}


				break;
            case PublicMessage.REFUSE:
                addFriend.setText("已拒绝");
                addFriend.setBackgroundResource(R.drawable.but_bg_public_agree);
                switch (item.getClassType()) {
                    case PublicMessage.GROUP:
                        item_name.setText(item.getName());
                        item_user_name.setText("");
                        item_content.setText(item.getName() + "请求加入【"
                                + item.getNick_name() + "】");
                        break;
                    case PublicMessage.FRIEND:
                        item_name.setText(item.getNick_name());
                        item_user_name.setText("");
                        item_content.setText(item.getReason());
                        break;
                    case PublicMessage.Union:
                        if(item.getSid() == 0){
                            item_name.setText(item.getNick_name());
                            item_user_name.setText("");
                        }else {
                            item_name.setText(item.getName());
                            item_user_name.setText(Html.fromHtml("<u>" + item.getNick_name() + "</u>"));
                        }
                        item_content.setText(item.getReason());
                        break;
                    case PublicMessage.GROUPCHANGE:
                        item_name.setText(item.getName());
                        item_user_name.setText("");
                        item_content.setText(item.getReason());
                        break;
                }

                break;
			// 未同意
			case PublicMessage.UNAGREE:
				addFriend.setText("同意");
				addFriend
						.setBackgroundResource(R.drawable.but_bg_public_unagree);
				switch (item.getClassType()) {
				case PublicMessage.GROUP:
					item_name.setText(item.getName());
                    item_user_name.setText("");
					item_content.setText(item.getName() + "请求加入【"
							+ item.getNick_name() + "】");
					addFriend.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (CommonUtils.isNetworkAvailable(mContext)) {
								showLoding("正在处理...");
								InteNetUtils
										.getInstance(mContext)
										.joinTalkGroup(
												item.getHuanxin_username(),
												item.getHuanxin_username_joiner(),
												new RequestCallBack<String>() {

													@Override
													public void onFailure(
															HttpException arg0,
															String arg1) {
														dissLoding();
														ToastUtils.Errortoast(
																mContext,
																"同意加入失败");
													}

													@Override
													public void onSuccess(
															ResponseInfo<String> arg0) {
														dissLoding();
														JSONObject jsonObj;
														try {
															jsonObj = new JSONObject(
																	arg0.result);
															SuccessMsg successMsg = new SuccessMsg();
															successMsg
																	.parseJSON(jsonObj);
															addFriend
																	.setText("已同意");
															addFriend
																	.setBackgroundResource(R.drawable.but_bg_public_agree);

															item.setStatus(PublicMessage.AGREE);

															try {
																dbUtil.saveOrUpdate(item);
															} catch (DbException e) {
																// TODO
																// Auto-generated
																// catch
																// block
																e.printStackTrace();
															}
															sendBroadcast(new Intent(
																	AndroidConfig.ContactsRefresh));
															return;
														} catch (JSONException e) {
															// TODO
															// Auto-generated
															// catch
															// block
															e.printStackTrace();
														} catch (NetRequestException e) {

															e.printStackTrace();
														}
														ToastUtils.Errortoast(
																mContext,
																"同意加入失败");
													}
												});

							}
						}
					});
					break;
				case PublicMessage.FRIEND:
					item_name.setText(item.getNick_name());
                    item_user_name.setText("");
					item_content.setText(item.getReason());
					addFriend.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
                            Intent intentFriend = new Intent(mContext, ActivityAddNewHxFriend.class);
                            mContext.overridePendingTransition(R.anim.in_from_right,
                                    R.anim.out_to_left);
                            intentFriend.putExtra("publicMessage", item);
                            startActivityForResult(intentFriend, AndroidConfig.writeFriendRequestCode);
							// 同意加好友
//							if (CommonUtils.isNetworkAvailable(mContext)) {
//								showLoding("正在处理...");
//								InteNetUtils.getInstance(mContext).addFirend(
//										item.getHuanxin_username(),
//										new RequestCallBack<String>() {
//
//											@Override
//											public void onSuccess(
//													ResponseInfo<String> arg0) {
//												dissLoding();
//												try {
//													JSONObject jsonObject = new JSONObject(
//															arg0.result);
//
//													Contacts contacts = new Contacts();
//													contacts.parseJSONSingle2(jsonObject);
//
//													dbUtil.saveOrUpdate(contacts);
//													ArrayList<PhoneInfo> phones = contacts
//															.getPhones();
//													if (phones != null)
//														dbUtil.saveOrUpdateAll(phones);
//													item.setStatus(PublicMessage.AGREE);
//													dbUtil.saveOrUpdate(item);
//
//													try {
//														EMChatManager
//																.getInstance()
//																.refuseInvitation(
//																		item.getHuanxin_username());
//													} catch (EaseMobException e) {
//														// TODO Auto-generated
//														// catch
//														// block
//														e.printStackTrace();
//													}
//
//													addFriend.setText("已同意");
//													addFriend
//															.setBackgroundResource(R.drawable.but_bg_public_agree);
//
//													// 获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
//													EMConversation conversation = EMChatManager
//															.getInstance()
//															.getConversation(
//																	contacts.getHuanxin_username());
//													// 创建一条文本消息
//													final EMMessage message = EMMessage
//															.createSendMessage(EMMessage.Type.TXT);
//													// 如果是群聊，设置chattype,默认是单聊
//													// 设置消息body
//													TextMessageBody txtBody = new TextMessageBody(
//															"我们已是好友,开始聊天吧");
//
//													message.setAttribute(
//															"friend_contact_new_id",
//															jsonObject
//																	.optString("targetContactID"));
//													message.addBody(txtBody);
//													// 设置接收人
//													message.setReceipt(contacts
//															.getHuanxin_username());
//													// 把消息加入到此会话对象中
//													conversation
//															.addMessage(message);
//													// 发送消息
//
//													new Handler().postDelayed(
//															new Runnable() {
//
//																@Override
//																public void run() {
//																	try {
//																		EMChatManager
//																				.getInstance()
//																				.sendMessage(
//																						message);
//																	} catch (EaseMobException e) {
//																		e.printStackTrace();
//																	}
//																}
//															}, 1000);
//
//													sendBroadcast(new Intent(
//															AndroidConfig.ContactsRefresh));
//													return;
//												} catch (JSONException e) {
//													e.printStackTrace();
//												} catch (NetRequestException e) {
//													Error error = e.getError();
//													error.print(mContext);
//													if (error.getErrorId() == 5218
//															|| error.getErrorId() == 5208) {
//
//														try {
//															EMChatManager
//																	.getInstance()
//																	.refuseInvitation(
//																			item.getHuanxin_username());
//														} catch (EaseMobException e1) {
//															// TODO
//															// Auto-generated
//															// catch
//															// block
//															e.printStackTrace();
//														}
//
//														item.setStatus(PublicMessage.AGREE);
//
//														try {
//															dbUtil.saveOrUpdate(item);
//														} catch (DbException e1) {
//															// TODO
//															// Auto-generated
//															// catch block
//															e1.printStackTrace();
//														}
//														addFriend
//																.setText("已同意");
//														addFriend
//																.setBackgroundResource(R.drawable.but_bg_public_agree);
//
//														// 获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
//														EMConversation conversation = EMChatManager
//																.getInstance()
//																.getConversation(
//																		item.getHuanxin_username());
//														// 创建一条文本消息
//														final EMMessage message = EMMessage
//																.createSendMessage(EMMessage.Type.TXT);
//														// 如果是群聊，设置chattype,默认是单聊
//														// 设置消息body
//														TextMessageBody txtBody = new TextMessageBody(
//																"我们已是好友,开始聊天吧");
//														message.addBody(txtBody);
//
//														// 设置接收人
//														message.setReceipt(item
//																.getHuanxin_username());
//
//														// 把消息加入到此会话对象中
//														conversation
//																.addMessage(message);
//														// 发送消息
//
//														new Handler()
//																.postDelayed(
//																		new Runnable() {
//
//																			@Override
//																			public void run() {
//																				try {
//																					EMChatManager
//																							.getInstance()
//																							.sendMessage(
//																									message);
//																				} catch (EaseMobException e) {
//																					e.printStackTrace();
//																				}
//																			}
//																		}, 1000);
//
//														sendBroadcast(new Intent(
//																AndroidConfig.ContactsRefresh));
//
//														return;
//													}
//													e.printStackTrace();
//												} catch (DbException e) {
//													// TODO Auto-generated catch
//													// block
//													e.printStackTrace();
//												}
//												ToastUtils.Errortoast(mContext,
//														"同意添加失败");
//											}
//
//											@Override
//											public void onFailure(
//													HttpException arg0,
//													String arg1) {
//												dissLoding();
//												ToastUtils.Errortoast(mContext,
//														"同意添加失败");
//											}
//										});

//							}
						}
					});

					break;
                case PublicMessage.Union:
                    item_name.setText(item.getName());
                    item_user_name.setText(Html.fromHtml("<u>" + item.getNick_name() + "</u>"));
                    item_content.setText(item.getReason());
                    addFriend.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (CommonUtils.isNetworkAvailable(mContext)) {
                                showLoding("正在处理...");
                                // 加入好友联盟
                                InteNetUtils.getInstance(mContext).acceptFriendUN(item.getNews_id(), new RequestCallBack<String>() {

                                    @Override
                                    public void onFailure(HttpException arg0, String arg1) {
                                        dissLoding();
                                        ToastUtils.Errortoast(mContext, "加入联盟失败，请稍后再试");
                                    }

                                    @Override
                                    public void onSuccess(ResponseInfo<String> arg0) {
                                        dissLoding();
                                        try {
                                            JSONObject jsonObject = new JSONObject(arg0.result);

                                            SuccessMsg msg = new SuccessMsg();

                                            msg.parseJSON(jsonObject);
                                            ToastUtils.Errortoast(mContext, "加入成功");
                                            item.setStatus(PublicMessage.AGREE);
                                            try {
                                                dbUtil.saveOrUpdate(item);
                                            } catch (DbException e1) {
                                                // TODO
                                                // Auto-generated
                                                // catch block
                                                e1.printStackTrace();
                                            }
                                            adapter.notifyDataSetChanged();

                                        } catch (JSONException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        } catch (NetRequestException e) {
                                            e.getError().print(mContext);
                                            e.printStackTrace();
                                        }

                                    }
                                });
                            }else{
                                ToastUtils.Errortoast(mContext,"当前网络不可用");
                            }
                        }
                    });

                    break;
                case PublicMessage.GROUPCHANGE:
                    item_name.setText(item.getName());
                    item_user_name.setText("");
                    item_content.setText(item.getReason());
                    addFriend.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (CommonUtils.isNetworkAvailable(mContext)) {
                                showLoding("正在处理...");
                                // 加入好友联盟
                                InteNetUtils.getInstance(mContext).acceptTransfer(item.getNews_id(),user.getToken(), new RequestCallBack<String>() {

                                    @Override
                                    public void onFailure(HttpException arg0, String arg1) {
                                        dissLoding();
                                        ToastUtils.Errortoast(mContext, "接受转让失败，请稍后再试");
                                    }

                                    @Override
                                    public void onSuccess(ResponseInfo<String> arg0) {
                                        dissLoding();
                                        try {
                                            JSONObject jsonObject = new JSONObject(arg0.result);

                                            SuccessMsg msg = new SuccessMsg();

                                            msg.parseJSON(jsonObject);
                                            ToastUtils.Errortoast(mContext, "接受转让成功");
                                            item.setStatus(PublicMessage.AGREE);
                                            try {
                                                dbUtil.saveOrUpdate(item);
                                            } catch (DbException e1) {
                                                // TODO
                                                // Auto-generated
                                                // catch block
                                                e1.printStackTrace();
                                            }
                                            adapter.notifyDataSetChanged();

                                        } catch (JSONException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        } catch (NetRequestException e) {
                                            e.getError().print(mContext);
                                            e.printStackTrace();
                                        }

                                    }
                                });
                            }else{
                                ToastUtils.Errortoast(mContext,"当前网络不可用");
                            }
                        }
                    });
                    break;
					case PublicMessage.NUMBERTRAIN_CHANGE:
						item_name.setText(item.getNick_name());
						item_user_name.setText("");
						item_content.setText(item.getReason());
						addFriend.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								if (CommonUtils.isNetworkAvailable(mContext)) {
									showLoding("正在处理...");
									// 号码直通车转让
									InteNetUtils.getInstance(mContext).storeAgreeTransfer(item.getNews_id(),user.getToken(), new RequestCallBack<String>() {

										@Override
										public void onFailure(HttpException arg0, String arg1) {
											dissLoding();
											ToastUtils.Errortoast(mContext, "接受转让失败，请稍后再试");
										}

										@Override
										public void onSuccess(ResponseInfo<String> arg0) {
											dissLoding();
											try {
												JSONObject jsonObject = new JSONObject(arg0.result);

												SuccessMsg msg = new SuccessMsg();

												msg.parseJSON(jsonObject);
												ToastUtils.Errortoast(mContext, "接受转让成功");
												item.setStatus(PublicMessage.AGREE);
												try {
													dbUtil.saveOrUpdate(item);
												} catch (DbException e1) {
													// TODO
													// Auto-generated
													// catch block
													e1.printStackTrace();
												}
												adapter.notifyDataSetChanged();

											} catch (JSONException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											} catch (NetRequestException e) {
												e.getError().print(mContext);
												e.printStackTrace();
											}

										}
									});
								}else{
									ToastUtils.Errortoast(mContext,"当前网络不可用");
								}
							}
						});
						break;
				}

				break;
			}

            item_user_name.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(NewFriendsMsgActivity.this, ActivityContactsInfo.class);
                    intent.putExtra("username", item.getHuanxin_username());
                    NewFriendsMsgActivity.this.startActivityForResult(intent, AndroidConfig.ContactsFragmentRequestCode);
                    NewFriendsMsgActivity.this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

                }
            });

            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (item.getClassType()) {
                        case PublicMessage.GROUP:
                            if(item.getHuanxin_username_joiner()!=null && !item.getHuanxin_username_joiner().equals("")) {
                                Intent intentGroup = new Intent(mContext, ActivityNewGroupInfo.class);
                                mContext.overridePendingTransition(R.anim.in_from_right,
                                        R.anim.out_to_left);
                                intentGroup.putExtra("publicMessage", item);
                                startActivityForResult(intentGroup, AndroidConfig.writeFriendRequestCode);
                            }
                            break;
                        case PublicMessage.FRIEND:
                            if(item.getSid()!=0) {
                                Intent intentFriend = new Intent(mContext, ActivityNewFriendInfo.class);
                                mContext.overridePendingTransition(R.anim.in_from_right,
                                        R.anim.out_to_left);
                                intentFriend.putExtra("publicMessage", item);
                                startActivityForResult(intentFriend, AndroidConfig.writeFriendRequestCode);
                            }
                            break;
                        case PublicMessage.Union:
                            if(item.getSid()!=0) {
                                Intent intentUnion = new Intent(mContext, ActivityNewUnionInfo.class);
                                mContext.overridePendingTransition(R.anim.in_from_right,
                                        R.anim.out_to_left);
                                intentUnion.putExtra("publicMessage", item);
                                startActivityForResult(intentUnion, AndroidConfig.writeFriendRequestCode);
                            }
                            break;
                        case PublicMessage.GROUPCHANGE:
                            if(!item.getNews_id().equals("")) {
                                Intent intentUnion = new Intent(mContext, ActivityNewGroupChangeInfo.class);
                                mContext.overridePendingTransition(R.anim.in_from_right,
                                        R.anim.out_to_left);
                                intentUnion.putExtra("publicMessage", item);
                                startActivityForResult(intentUnion, AndroidConfig.writeFriendRequestCode);
                            }
                            break;
						case PublicMessage.NUMBERTRAIN_CHANGE:
							//号码直通车转让
							if(!item.getNews_id().equals("")) {
								Intent intentUnion = new Intent(mContext, ActivityTransferDetails.class);
								mContext.overridePendingTransition(R.anim.in_from_right,
										R.anim.out_to_left);
								intentUnion.putExtra("publicMessage", item);
								startActivityForResult(intentUnion, AndroidConfig.writeFriendRequestCode);
							}
							break;
                    }
                }
            });

			return convertView;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
            if(mPublicMessages!=null){
                return mPublicMessages.size();
            }else{
                return 0;
            }
		}

		@Override
		public PublicMessage getItem(int position) {
            if(mPublicMessages!=null){
                return mPublicMessages.get(position);
            }else{
                return null;
            }
//			return mPublicMessages.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case AndroidConfig.writeFriendResultCode:
                try {
//                    mPublicMessages = dbUtil.findAll(Selector.from(PublicMessage.class)
//                            .orderBy("id", true));
                    mPublicMessages = new ArrayList<>();
                    List<PublicMessage> unAgreeMessages = dbUtil.findAll(Selector.from(PublicMessage.class).where("status","=",PublicMessage.UNAGREE)
                            .orderBy("id", true));
                    if(unAgreeMessages!=null && unAgreeMessages.size()!=0){
                        mPublicMessages.addAll(unAgreeMessages);
                    }
                    List<PublicMessage> publicMessages = dbUtil.findAll(Selector.from(PublicMessage.class).where("status","!=",PublicMessage.UNAGREE)
                            .orderBy("id", true));
                    if(publicMessages!=null && publicMessages.size()!=0){
                        mPublicMessages.addAll(publicMessages);
                    }
                    if(mPublicMessages!=null && mPublicMessages.size()>0){
                        nodota.setVisibility(View.GONE);
                    }else{
                        nodota.setVisibility(View.VISIBLE);
                    }
                } catch (DbException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
                break;
        }

    }
}
