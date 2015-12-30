package com.xunao.benben.ui.item;

import in.srain.cube.image.CubeImageView;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.BaseActivity.TitleMode;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.ContactsGroup;
import com.xunao.benben.bean.PhoneInfo;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.AddPhoneDialog;
import com.xunao.benben.dialog.InfoMsgHint;
import com.xunao.benben.dialog.InputDialog;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.dialog.UpdateVersionDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PhoneUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.RegexUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.utils.ViewHolderUtil;
import com.xunao.benben.utils.XunaoLog;
import com.xunao.benben.view.MyTextView;

public class ActivityContactsEditInfoContent extends BaseActivity implements
		OnClickListener {
	private changeBroadCast changeBroadCast;
	private CubeImageView contacts_poster;
	private TextView contacts_name;
	private TextView contacts_group_name;
    private TextView  tv_nick_name;
	private TextView contacts_benben;
	private ImageView iv_edit_name;

	private ListView listview;
	private LinearLayout delete;
	private Contacts mContacts;
	private ArrayList<List_Bean> list_Beans = new ArrayList<List_Bean>();
	private View layout_addPhone;
	private MyAdapter myAdapter;
	private InputDialog inputDialog;
	private String pecketName;
	private RelativeLayout rl_contacts_group;
	private TextView tv_contacts_group;
	private List<PhoneInfo> phones;

    private LinearLayout ll_delete;
    private ArrayList<PhoneInfo> phoneBenbenList = new ArrayList<PhoneInfo>();
    private int benPosition=0;
    private String benBenId="";
    private int infoId;
    private String phone="";
    private MsgDialog msgDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		changeBroadCast = new changeBroadCast();
		IntentFilter intentFilter = new IntentFilter(
				AndroidConfig.refreshContactsGroup);
		intentFilter.setPriority(100);
		registerReceiver(changeBroadCast, intentFilter);

	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_cpntacts_edit_info_content);
	}

	@Override
	public void initView(Bundle savedInstanceState) {

		initTitleView();
		TitleMode mode = new TitleMode("#068cd9", "", 0, new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		}, "", R.drawable.ic_back, new OnClickListener() {

			@Override
			public void onClick(View v) {
				mContext.AnimFinsh();
			}
		}, "编辑联系人", 0);
		chanageTitle(mode);

		contacts_poster = (CubeImageView) findViewById(R.id.contacts_poster);
		contacts_name = (TextView) findViewById(R.id.contacts_name);
		contacts_group_name = (TextView) findViewById(R.id.contacts_group_name);
        tv_nick_name = (TextView) findViewById(R.id.tv_nick_name);
		contacts_benben = (TextView) findViewById(R.id.contacts_benben);
		layout_addPhone = findViewById(R.id.layout_addPhone);
		rl_contacts_group = (RelativeLayout) findViewById(R.id.rl_contacts_group);
		tv_contacts_group = (TextView) findViewById(R.id.tv_contacts_group);
		iv_edit_name = (ImageView) findViewById(R.id.iv_edit_name);
		iv_edit_name.setVisibility(View.VISIBLE);
		listview = (ListView) findViewById(R.id.listview);
		delete = (LinearLayout) findViewById(R.id.delete);
		contacts_name.setOnClickListener(this);
		iv_edit_name.setOnClickListener(this);
		rl_contacts_group.setOnClickListener(this);
        ll_delete = (LinearLayout) findViewById(R.id.ll_delete);
        ll_delete.setOnClickListener(this);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		mContacts = (Contacts) getIntent().getSerializableExtra("contacts");

		initSelfData();
	}

	private void initSelfData() {
		contacts_name.setText(mContacts.getName());
		ContactsGroup grouop = null;
		phones = null;
		list_Beans.clear();
        phoneBenbenList.clear();
        benPosition=0;
		try {
			// 获得组名
			grouop = dbUtil.findById(ContactsGroup.class,
					mContacts.getGroup_id());
			// 获得联系人下的 phoneInfo
			phones = dbUtil.findAll(Selector.from(PhoneInfo.class).where(
					"contacts_id", "=", mContacts.getId()));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if(phones!=null) {
            int i=0;
            for (PhoneInfo phoneInfo : phones) {
                if(!phoneInfo.getIs_benben().equals("0")){
                    phoneBenbenList.add(phoneInfo);
                    if(phoneInfo.getIs_active().equals("1")){
                        benPosition = i;
                    }
                    i++;
                }


                if(!phoneInfo.getPhone().equals("")) {
                    List_Bean bean = null;
                    if ("0".equals(phoneInfo.getIs_baixing())) {
                        bean = new List_Bean(phoneInfo.getPid(), phoneInfo.getPhone(), false,
                                phoneInfo.getIs_baixing(), phoneInfo.getIs_benben());
                    } else {
                        bean = new List_Bean(phoneInfo.getPid(), phoneInfo.getPhone(), true,
                                phoneInfo.getIs_baixing(), phoneInfo.getIs_benben());
                    }
                    list_Beans.add(bean);
                }
            }
        }



        String groupName = "";
        if(grouop!=null && grouop.getName()!=null){
            groupName = grouop.getName();
        }

		contacts_group_name.setText(groupName);
		contacts_group_name.setVisibility(View.GONE);
		tv_contacts_group.setText(groupName);
        if(phoneBenbenList!=null && phoneBenbenList.size()!=0){
            CommonUtils.startImageLoader(cubeimageLoader, mContacts.getPoster(),
                    contacts_poster);
            tv_nick_name.setText("昵称："+phoneBenbenList.get(benPosition).getNick_name());
            benBenId = phoneBenbenList.get(benPosition).getIs_benben();
            infoId = phoneBenbenList.get(benPosition).getContacts_id();
            phone = phoneBenbenList.get(benPosition).getPhone();
        }else{
            tv_nick_name.setText("昵称："+mContacts.getNick_name());
            benBenId = mContacts.getIs_benben();
            infoId = mContacts.getId();
        }
		if ("0".equals(benBenId)) {
			contacts_benben.setVisibility(View.GONE);
            ll_delete.setVisibility(View.GONE);
		} else {
            contacts_benben.setVisibility(View.VISIBLE);
            ll_delete.setVisibility(View.VISIBLE);
			contacts_benben.setText("奔犇号：" + benBenId);
		}

		myAdapter = new MyAdapter();
		listview.setAdapter(myAdapter);
		refrshHeight();

	}

	protected void refrshHeight() {
		int height = 0;
		for (List_Bean lb : list_Beans) {
			if (lb.addBaixing) {
				height += PixelUtil.dp2px(91);
			} else {
				height += PixelUtil.dp2px(56);
			}
		}

		listview.setLayoutParams(new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, height));
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {

		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				hint = new InfoMsgHint(mContext, R.style.MyDialog1);
				hint.setContent("确定删除联系人?", "", "确定", "取消");
				hint.setCancleListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						hint.dismiss();
					}
				});
				hint.setOKListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 删除联系人
						if (CommonUtils.isNetworkAvailable(mContext)) {
							showLoding("请稍后...");
							InteNetUtils.getInstance(mContext).deleteContact(
									mContacts.getId() + "",
									new RequestCallBack<String>() {

										@Override
										public void onFailure(
												HttpException arg0, String arg1) {
											dissLoding();
											ToastUtils.Errortoast(mContext,
													"删除失败");
										}

										@Override
										public void onSuccess(
												ResponseInfo<String> arg0) {
											dissLoding();
											try {
												JSONObject jsonObject = new JSONObject(
														arg0.result);
												SuccessMsg msg = new SuccessMsg();
												try {
													msg.parseJSON(jsonObject);
													try {
														hint.dismiss();
                                                        EMChatManager.getInstance().clearConversation(mContacts.getHuanxin_username());
														dbUtil.delete(
																Contacts.class,
																WhereBuilder
																		.b("id",
																				"=",
																				mContacts
																						.getId()));
														dbUtil.delete(
																PhoneInfo.class,
																WhereBuilder
																		.b("contacts_id",
																				"=",
																				mContacts
																						.getId()));
														// setResult(AndroidConfig.writeFriendRefreshResultCode);
														mApplication.mContactsMap
																.remove(mContacts
																		.getHuanxin_username());
														EMChatManager.getInstance().deleteConversation(mContacts
																		.getHuanxin_username(), true);
														Intent intent = new Intent();
														intent.setAction(AndroidConfig.Finsh);

														intent.putExtra("type",
																100);
														sendBroadcast(intent);
														sendBroadcast(new Intent(
																AndroidConfig.ContactsRefresh));
														sendBroadcast(new Intent(
																AndroidConfig.refreshActivityCaptureContactsInfo));
														AnimFinsh();
													} catch (DbException e) {
														e.printStackTrace();
													}
												} catch (NetRequestException e) {
													e.getError()
															.print(mContext);
												}
											} catch (JSONException e) {
												ToastUtils.Errortoast(mContext,
														"删除失败");
											}

										}
									});
						}
					}
				});
				hint.show();
			}

		});

		layout_addPhone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (list_Beans.size() >= 5) {
					ToastUtils.Errortoast(mContext, "只能输入5组号码!");
					return;
				}
				inputDialog = new InputDialog(mContext, R.style.MyDialogStyle);
				inputDialog.setContent("添加号码", "请输入新的号码", "添加", "取消");
				inputDialog.setInputType("");
				inputDialog.setCancleListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						inputDialog.dismiss();
					}
				});
				inputDialog.setOKListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						final String string = inputDialog.getInputText();

						if (RegexUtils.checkNum(string)) {
							InteNetUtils.getInstance(mContext).addPhone(string,
									mContacts.getId() + "",user.getToken(),
									new RequestCallBack<String>() {

										@Override
										public void onFailure(
												HttpException arg0, String arg1) {
											ToastUtils.Errortoast(mContext,
													"添加号码失败");
										}

										@Override
										public void onSuccess(
												ResponseInfo<String> arg0) {
											inputDialog.dismiss();
											try {
												JSONObject jsonObject = new JSONObject(
														arg0.result);
												SuccessMsg msg = new SuccessMsg();
												try {
													msg.parseJSON(jsonObject);
													try {
														JSONObject optJSONObject = jsonObject
																.optJSONObject("phone_info");
														PhoneInfo phoneInfo = new PhoneInfo();
														phoneInfo
																.parseJSON(optJSONObject);

														JSONObject opt = jsonObject
																.optJSONObject("contact_info");

//														mContacts.setPoster(opt
//																.optString("poster"));
//														mContacts
//																.setHuanxin_username(opt
//																		.optString("huanxin_username"));
//														mContacts
//																.setIs_benben(opt
//																		.optString("is_benben"));
//														mContacts
//																.setIs_baixing(opt
//																		.optString("is_baixing"));

//														dbUtil.saveOrUpdate(mContacts);
														sendBroadcast(new Intent(
																AndroidConfig.ContactsRefresh));

														phoneInfo
																.setContacts_id(mContacts
																		.getId());
														dbUtil.saveOrUpdate(phoneInfo);
														if (CommonUtils
																.isEmpty(phoneInfo
																		.getIs_baixing())
																|| phoneInfo
																		.getIs_baixing()
																		.equals("0")) {
															list_Beans
																	.add(new List_Bean(
                                                                            phoneInfo.getPid(),
																			string,
																			false,
																			"0",
                                                                            phoneInfo.getIs_benben()));
														} else {
															list_Beans
																	.add(new List_Bean(
                                                                            phoneInfo.getPid(),
																			string,
																			true,
																			phoneInfo.getIs_baixing(),
                                                                            phoneInfo.getIs_benben()));
														}
														// myAdapter
														// .notifyDataSetChanged();
														// refrshHeight();
														initSelfData();
														setResult(AndroidConfig.writeFriendRefreshResultCode);
													} catch (DbException e) {
														e.printStackTrace();
													}
												} catch (NetRequestException e) {
													e.getError()
															.print(mContext);
												}
											} catch (JSONException e) {
												ToastUtils.Errortoast(mContext,
														"添加失败");
											}
										}
									});
						} else {
							ToastUtils.Errortoast(mContext, "号码最少3位，最多16位");
						}
					}
				});
				inputDialog.show();
			}
		});

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

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list_Beans.size();
		}

		@Override
		public List_Bean getItem(int arg0) {
			// TODO Auto-generated method stub
			return list_Beans.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			return getItem(position).addBaixing ? 0 : 1;
		}

		@Override
		public View getView(int position, View converView, ViewGroup parents) {
			if (converView == null) {
				switch (getItemViewType(position)) {
				case 0:// 有短号
					converView = LayoutInflater.from(mContext).inflate(
							R.layout.item_edit_contacts_short, null);
					break;
				case 1:// 没有短号
					converView = LayoutInflater.from(mContext).inflate(
							R.layout.item_edit_contacts_unshort, null);
					break;
				}
			}

			final List_Bean item = getItem(position);

			switch (getItemViewType(position)) {
			case 0:// 有短号
				ImageView delete_but = ViewHolderUtil.get(converView,
						R.id.delete_but);
				MyTextView item_phone_name = ViewHolderUtil.get(converView,
						R.id.item_phone_name);
				MyTextView item_phone_name_short = ViewHolderUtil.get(
						converView, R.id.item_phone_name_short);

				item_phone_name.setText(item.getPone());
				item_phone_name_short.setText(item.getBaixingPhone());

				delete_but.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						deleteNumber(item, MyAdapter.this);
					}

				});
				break;
			case 1:// 没有短号

				ImageView nodelete_but = ViewHolderUtil.get(converView,
						R.id.delete_but);
				MyTextView noitem_phone_name = ViewHolderUtil.get(converView,
						R.id.item_phone_name);

				noitem_phone_name.setText(item.getPone());

				nodelete_but.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						deleteNumber(item, MyAdapter.this);
					}
				});

				break;
			}

			return converView;
		}

	}

	// 删除联系人号码
	private void deleteNumber(final List_Bean item, final MyAdapter adapter) {
		InteNetUtils.getInstance(mContext).deleteNumber(item.getPid(),
				user.getToken() + "", new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						ToastUtils.Errortoast(mContext, "删除失败");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						try {
							JSONObject jsonObject = new JSONObject(arg0.result);
							SuccessMsg msg = new SuccessMsg();
							try {
								msg.parseJSON(jsonObject);
								try {
                                    if(list_Beans.size()==1 && mContacts.getIs_benben().equals("0")){
                                        EMChatManager.getInstance().clearConversation(mContacts.getHuanxin_username());
                                        dbUtil.delete(
                                                Contacts.class,
                                                WhereBuilder
                                                        .b("id",
                                                                "=",
                                                                mContacts
                                                                        .getId()));
                                        dbUtil.delete(
                                                PhoneInfo.class,
                                                WhereBuilder
                                                        .b("contacts_id",
                                                                "=",
                                                                mContacts
                                                                        .getId()));
                                        // setResult(AndroidConfig.writeFriendRefreshResultCode);
                                        mApplication.mContactsMap
                                                .remove(mContacts
                                                        .getHuanxin_username());
                                        EMChatManager.getInstance().deleteConversation(mContacts
                                                .getHuanxin_username(), true);
                                        Intent intent = new Intent();
                                        intent.setAction(AndroidConfig.Finsh);

                                        intent.putExtra("type",
                                                100);
                                        sendBroadcast(intent);
                                        sendBroadcast(new Intent(
                                                AndroidConfig.ContactsRefresh));
                                        sendBroadcast(new Intent(
                                                AndroidConfig.refreshActivityCaptureContactsInfo));
                                        AnimFinsh();
                                    }else {
                                        if(item.getIsBenben().equals("0")) {
                                            dbUtil.delete(
                                                    PhoneInfo.class,
                                                    WhereBuilder.b("pid", "=",
                                                            item.getPid()));
                                        }else{
                                            PhoneInfo phoneInfo = new PhoneInfo();
                                            phoneInfo.setPhone("");
                                            dbUtil.update(phoneInfo,
                                                    WhereBuilder.b("pid","=",item.getPid()),"phone");
                                        }
                                        list_Beans.remove(item);

//									mContacts.setIs_benben("0");
//									mContacts.setHuanxin_username("");
//									mContacts.setPoster("");
//									mContacts.setIs_baixing("0");
//									for (PhoneInfo p : phones) {
//										if (!p.getPhone()
//												.equals(item.getPone())) {
//											if (!p.getIs_benben().equals("0")) {
//												mContacts.setIs_benben(p
//														.getIs_benben());
//												mContacts.setHuanxin_username(p
//														.getHuanxin_username());
//												mContacts.setPoster(p
//														.getPoster());
//												mContacts.setIs_baixing(p
//														.getIs_baixing());
//											}
//										}
//									}
//									dbUtil.saveOrUpdate(mContacts);


                                        initSelfData();
                                        setResult(AndroidConfig.writeFriendRefreshResultCode);
                                        sendBroadcast(new Intent(
                                                AndroidConfig.ContactsRefresh));
                                        sendBroadcast(new Intent(
                                                AndroidConfig.refreshActivityCaptureContactsInfo));
                                        refrshHeight();
                                    }
								} catch (DbException e) {
									e.printStackTrace();
								}
							} catch (NetRequestException e) {
								e.getError().print(mContext);
							}
						} catch (JSONException e) {
							ToastUtils.Errortoast(mContext, "删除失败");
						}

					}
				});

	}

	class List_Bean {

		public List_Bean(String pid,String phone, boolean addBaixing, String baixingPhone,String isBenben) {
            this.pid = pid;
			this.phone = phone;
			this.addBaixing = addBaixing;
			this.baixingPhone = baixingPhone;
            this.isBenben = isBenben;
		}

        String pid;
		String phone;
		boolean addBaixing;
		String baixingPhone;
        String isBenben;

        public String getPid() {
            return pid;
        }
        public String getPone() {
			return phone;
		}

		public boolean isBaiXing() {
			return addBaixing;
		}

		public String getBaixingPhone() {
			return baixingPhone;
		}

        public String getIsBenben() {
            return isBenben;
        }
    }

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
            case R.id.rl_contacts_group:
                startAnimActivity2Obj(ActivityChoiceContactsGroup.class, "groupId",
                        mContacts.getGroup_id(), "contacts", mContacts);
                break;
            case R.id.iv_edit_name:
            case R.id.contacts_name:
                inputDialog = new InputDialog(mContext, R.style.MyDialogStyle);
                inputDialog.setContent("修改联系人姓名", "请输入姓名", "确认", "取消");
                inputDialog.setEditContent(mContacts.getName());
                inputDialog.setCancleListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inputDialog.dismiss();
                    }
                });
                inputDialog.setOKListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pecketName = inputDialog.getInputText();
                        if (CommonUtils.isEmpty(pecketName)) {
                            ToastUtils.Infotoast(mContext, "请输入联系人备注");
                            return;
                        }
                        if (!CommonUtils.StringIsSurpass2(pecketName, 2, 12)) {
                            ToastUtils.Errortoast(mContext, "联系人备注限制在1—12字之内");
                            return;
                        }

                        if (CommonUtils.isNetworkAvailable(mContext)) {
                            InteNetUtils.getInstance(mContext).updateContactsName(
                                    mContacts.getId() + "", pecketName,
                                    new RequestCallBack<String>() {
                                        @Override
                                        public void onSuccess(
                                                ResponseInfo<String> arg0) {
                                            JSONObject jsonObject = null;
                                            try {
                                                jsonObject = new JSONObject(
                                                        arg0.result);
                                                if (jsonObject.optString("ret_num")
                                                        .equals("0")) {
                                                    ToastUtils.Infotoast(mContext,
                                                            "修改成功");
                                                    contacts_name
                                                            .setText(pecketName);
                                                    mContacts.setName(pecketName);
                                                    mContacts.setPinyin(jsonObject.optString("pinyin"));
                                                    dbUtil.update(
                                                            mContacts,
                                                            WhereBuilder.b(
                                                                    "id",
                                                                    "=",
                                                                    mContacts
                                                                            .getId()),
                                                            "name","pinyin");
                                                    Intent intent = new Intent();
                                                    intent.setAction(AndroidConfig.Finsh);
                                                    intent.putExtra("type", 101);
                                                    intent.putExtra("contacts",
                                                            mContacts);
                                                    sendBroadcast(intent);
                                                    sendBroadcast(new Intent(
                                                            AndroidConfig.ContactsRefresh));
                                                    sendBroadcast(new Intent(
                                                            AndroidConfig.refreshActivityCaptureContactsInfo));
                                                    inputDialog.dismiss();
                                                } else {
                                                    ToastUtils
                                                            .Infotoast(
                                                                    mContext,
                                                                    jsonObject
                                                                            .optString("ret_msg"));
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onFailure(HttpException arg0,
                                                String arg1) {
                                            ToastUtils
                                                    .Infotoast(mContext, "网络不可用!");
                                        }
                                    });
                        }
                    }
                });
                inputDialog.show();
                break;
            case R.id.ll_delete:
                //删除奔犇号
                msgDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
                msgDialog.setContent("确定删除该奔犇号", "", "确认", "取消");
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
                          InteNetUtils.getInstance(mContext).deleteBenBen(benBenId,
                                  user.getToken(), infoId, new RequestCallBack<String>() {

                                      @Override
                                      public void onFailure(HttpException arg0, String arg1) {
                                          ToastUtils.Errortoast(mContext, "删除失败");
                                      }

                                      @Override
                                      public void onSuccess(ResponseInfo<String> arg0) {
                                          try {
                                              JSONObject jsonObject = new JSONObject(arg0.result);
                                              SuccessMsg msg = new SuccessMsg();
                                              try {
                                                  msg.parseJSON(jsonObject);
                                                  try {
                                                      if (phoneBenbenList.size() == 1) {
                                                          if (list_Beans.size() == 0 || list_Beans.size() == 1 && phone.equals(list_Beans.get(0).getPone())) {
                                                              EMChatManager.getInstance().clearConversation(mContacts.getHuanxin_username());
                                                              dbUtil.delete(
                                                                      Contacts.class,
                                                                      WhereBuilder
                                                                              .b("id",
                                                                                      "=",
                                                                                      mContacts
                                                                                              .getId()));
                                                              dbUtil.delete(
                                                                      PhoneInfo.class,
                                                                      WhereBuilder
                                                                              .b("contacts_id",
                                                                                      "=",
                                                                                      mContacts
                                                                                              .getId()));
                                                              mApplication.mContactsMap
                                                                      .remove(mContacts
                                                                              .getHuanxin_username());
                                                              EMChatManager.getInstance().deleteConversation(mContacts
                                                                      .getHuanxin_username(), true);
                                                              Intent intent = new Intent();
                                                              intent.setAction(AndroidConfig.Finsh);

                                                              intent.putExtra("type",
                                                                      100);
                                                              sendBroadcast(intent);
                                                              sendBroadcast(new Intent(
                                                                      AndroidConfig.ContactsRefresh));
                                                              sendBroadcast(new Intent(
                                                                      AndroidConfig.refreshActivityCaptureContactsInfo));
                                                              AnimFinsh();
                                                          } else {
                                                              dbUtil.delete(
                                                                      PhoneInfo.class,
                                                                      WhereBuilder.b("is_benben", "=",
                                                                              benBenId));
                                                              mContacts.setIs_benben("0");
                                                              mContacts.setHuanxin_username("");
                                                              for (PhoneInfo p : phones) {
                                                                  if (p.getIs_benben().equals("0")) {
                                                                      mContacts.setIs_benben("0");
                                                                      mContacts.setPoster(p.getPoster());
                                                                      mContacts.setIs_baixing(p.getIs_baixing());
                                                                      mContacts.setNick_name(p.getNick_name());
                                                                      break;
                                                                  }
                                                              }
                                                              dbUtil.saveOrUpdate(mContacts);
                                                              sendBroadcast(new Intent(
                                                                      AndroidConfig.ContactsRefresh));
                                                              setResult(AndroidConfig.writeFriendRefreshResultCode);
                                                              sendBroadcast(new Intent(
                                                                      AndroidConfig.refreshActivityCaptureContactsInfo));
                                                              AnimFinsh();
                                                          }
                                                      } else {
                                                          dbUtil.delete(
                                                                  PhoneInfo.class,
                                                                  WhereBuilder.b("is_benben", "=",
                                                                          benBenId));
                                                          setResult(AndroidConfig.writeFriendRefreshResultCode);
                                                          sendBroadcast(new Intent(
                                                                  AndroidConfig.ContactsRefresh));
                                                          sendBroadcast(new Intent(
                                                                  AndroidConfig.refreshActivityCaptureContactsInfo));
                                                          AnimFinsh();
                                                      }
                                                  } catch (DbException e) {
                                                      e.printStackTrace();
                                                  }
                                              } catch (NetRequestException e) {
                                                  e.getError().print(mContext);
                                              }
                                          } catch (
                                                  JSONException e
                                                  )

                                          {
                                              ToastUtils.Errortoast(mContext, "删除失败");
                                          }

                                      }
                                  });
                      }
                  });
                msgDialog.show();


                break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(changeBroadCast);
	}

	class changeBroadCast extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			mContacts = (Contacts) arg1.getSerializableExtra("group");
			initSelfData();
			Intent intent = new Intent();
			intent.setAction(AndroidConfig.Finsh);
			intent.putExtra("type", 101);
			intent.putExtra("contacts", mContacts);
			sendBroadcast(intent);
			sendBroadcast(new Intent(AndroidConfig.ContactsRefresh));
			sendBroadcast(new Intent(
					AndroidConfig.refreshActivityCaptureContactsInfo));
		}

	}

}
