package com.xunao.benben.ui.item;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Enterprise;
import com.xunao.benben.bean.EnterpriseList;
import com.xunao.benben.bean.EnterpriseMember;
import com.xunao.benben.bean.EnterpriseMemberDetail;
import com.xunao.benben.bean.EnterpriseMemberGroup;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.dialog.InputDialog;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.utils.ViewHolderUtil;

public class ActivitySearchEnterprise extends BaseActivity implements
		OnClickListener {
	private LinearLayout ll_seach_icon;
	private LinearLayout ll_search_item;
	private String searchKey = "";
	private EditText search_edittext;
	private ImageView iv_search_content_delect;
	private LinearLayout no_data;
	private ListView listView;
	private myAdapter adapter;
	private InputDialog inputDialog;
	private Enterprise enterpriseAdd;

	private ArrayList<Enterprise> enterprises = new ArrayList<>();
	private EnterpriseList enterpriseList;
	private String pecketName;
	private ArrayList<EnterpriseMemberDetail> enterpriseMemberDetails = new ArrayList<EnterpriseMemberDetail>();
	private ArrayList<EnterpriseMemberGroup> memberGroups = new ArrayList<>();

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_search_enterprise);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("政企通讯录", "", "取消",
				R.drawable.icon_com_title_left, 0);

		search_edittext = (EditText) findViewById(R.id.search_edittext);
		((TextView) findViewById(R.id.searchName)).setText("政企通讯录名称");
		iv_search_content_delect = (ImageView) findViewById(R.id.iv_search_content_delect);
		ll_seach_icon = (LinearLayout) findViewById(R.id.ll_seach_icon);
		no_data = (LinearLayout) findViewById(R.id.no_data);
		listView = (ListView) findViewById(R.id.listView);

		no_data.setVisibility(View.VISIBLE);

		iv_search_content_delect.setOnClickListener(this);

		adapter = new myAdapter();
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
			}
		});

		search_edittext.setFocusable(true);
		search_edittext.setFocusableInTouchMode(true);
		search_edittext.requestFocus();

		Timer timer = new Timer();

		timer.schedule(new TimerTask()

		{
			public void run()

			{
				InputMethodManager inputManager =

				(InputMethodManager) search_edittext.getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);

				inputManager.showSoftInput(search_edittext, 0);
			}

		}, 200);
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

		search_edittext.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				if (hasFocus) {
					InputMethodManager imm = (InputMethodManager) getApplicationContext()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					// 显示键盘
					imm.showSoftInput(search_edittext, 0);
				}
			}
		});

		search_edittext.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				if (arg0.length() > 0) {
					ll_seach_icon.setVisibility(View.GONE);
					iv_search_content_delect.setVisibility(View.VISIBLE);
				} else {
					ll_seach_icon.setVisibility(View.VISIBLE);
					iv_search_content_delect.setVisibility(View.GONE);
					searchKey = "";
				}
			}
		});

		search_edittext.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView arg0, int actionId,
					KeyEvent arg2) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					// 先隐藏键盘
					((InputMethodManager) search_edittext.getContext()
							.getSystemService(Context.INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(mContext.getCurrentFocus()
									.getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);

					initTitle_Right_Left_bar("政企通讯录", "", "取消",
							R.drawable.icon_com_title_left, 0);

					// 更新关键字
					searchKey = search_edittext.getText().toString().trim();
                    if(searchKey.equals("")){
                        ToastUtils.Infotoast(mContext,"请输入搜索内容");
                    }else if (CommonUtils.isNetworkAvailable(mContext)) {
                            InteNetUtils.getInstance(mContext).searchEnterprise(
                                    searchKey, mRequestCallBack);
                        }


					return true;
				}
				return false;
			}
		});

		setOnRightClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AnimFinsh();
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
			enterpriseList = new EnterpriseList();
			enterpriseList = enterpriseList.parseJSON(jsonObject);
            enterprises.clear();
			if (enterpriseList.getEnterprises().size() <= 0) {
				no_data.setVisibility(View.VISIBLE);
				listView.setVisibility(View.GONE);
			} else {
				no_data.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
				enterprises.addAll(enterpriseList.getEnterprises());
			}

			adapter.notifyDataSetChanged();
		} catch (NetRequestException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Infotoast(mContext, "网络不可用!");
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_search_content_delect:

			iv_search_content_delect.setVisibility(View.GONE);
			searchKey = "";
			ll_seach_icon.setVisibility(View.VISIBLE);
			search_edittext.setText("");
			enterprises.clear();
			adapter.notifyDataSetChanged();
			no_data.setVisibility(View.VISIBLE);
			// 影藏键盘
			((InputMethodManager) search_edittext.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(mContext.getCurrentFocus()
							.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);

			break;
		}
	}

	class myAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return enterprises.size();
		}

		@Override
		public Object getItem(int arg0) {
			return enterprises.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			convertView = getLayoutInflater().inflate(
					R.layout.activity_entrprises_contacts_item, null);
			TextView tv_enterprise_name = ViewHolderUtil.get(convertView,
					R.id.tv_enterprise_name);
			LinearLayout rl_add_enterprise = ViewHolderUtil.get(convertView,
					R.id.rl_add_enterprise);
			TextView tv_enterprise_number = ViewHolderUtil.get(convertView,
					R.id.tv_enterprise_number);
			ImageView iv_add = ViewHolderUtil.get(convertView, R.id.iv_add);
			TextView tv_add = ViewHolderUtil.get(convertView, R.id.tv_add);
            ImageView iv_tag = ViewHolderUtil.get(convertView,
                    R.id.iv_tag);
            TextView tv_introduce = ViewHolderUtil.get(convertView, R.id.tv_introduce);


            if(enterprises.get(position).getTag().equals("虚拟")){
                if(enterprises.get(position).getOrigin()==2){
                   iv_tag.setImageResource(R.drawable.icon_enterprises_xuni_back);
                }else {
                    iv_tag.setImageResource(R.drawable.icon_enterprises_xuni);
                }
            }else if(enterprises.get(position).getTag().equals("企业")){
                if(enterprises.get(position).getOrigin()==2){
                    iv_tag.setImageResource(R.drawable.icon_enterprises_company_back);
                }else {
                    iv_tag.setImageResource(R.drawable.icon_enterprises_company);
                }
            }else{
                iv_tag.setImageResource(R.drawable.icon_enterprises_baixing);
            }
			if (enterprises.get(position).getInA().equals("0")) {
				iv_add.setVisibility(View.VISIBLE);
				tv_add.setVisibility(View.VISIBLE);

				if (enterprises.get(position).getType().equals("3")) {
					iv_add.setVisibility(View.GONE);
					tv_add.setVisibility(View.GONE);
				} else {
                    iv_add.setVisibility(View.VISIBLE);
                    iv_add.setVisibility(View.VISIBLE);

				}

			} else {
				iv_add.setVisibility(View.GONE);
				tv_add.setVisibility(View.GONE);
			}

			tv_enterprise_name.setText(enterprises.get(position).getName());
			tv_enterprise_number.setText("("
					+ enterprises.get(position).getNumber() + "人)");

			OnClickListener add = new OnClickListener() {
				@Override
				public void onClick(View arg0) {
                    if (enterprises.get(position).getEnterprise_apply() == 3 && !enterprises.get(position).getType().equals("2")) {
                        ToastUtils.Infotoast(mContext, "该政企不允许加入");
                    } else {

                        if (enterprises.get(position).getType().equals("1")) {
                            if (CommonUtils.isNetworkAvailable(mContext)) {
                                enterpriseAdd = enterprises.get(position);
                                if (enterprises.get(position).getEnterprise_apply() == 2) {
                                    final MsgDialog msgDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
                                    msgDialog.setContent("确定加入吗?", "", "确定", "取消");
                                    msgDialog.setCancleListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            msgDialog.dismiss();
                                        }
                                    });
                                    msgDialog.setOKListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            msgDialog.dismiss();
                                            InteNetUtils.getInstance(mContext)
                                                    .EnterpriseApplyJoin(
                                                            enterprises.get(position).getId(),
                                                            "", new RequestCallBack<String>() {
                                                                @Override
                                                                public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                                                                    try {
                                                                        JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                                                                        if (jsonObject.optInt("ret_num") == 0) {
                                                                            ToastUtils.Errortoast(mContext, "提交申请成功!");
                                                                        } else {
                                                                            ToastUtils.Errortoast(mContext, jsonObject.optString("ret_msg"));
                                                                        }
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onFailure(HttpException e, String s) {
                                                                    ToastUtils.Errortoast(mContext, "提交申请失败!");
                                                                }
                                                            });
                                        }
                                    });
                                    msgDialog.show();

                                } else {
                                    InteNetUtils.getInstance(mContext)
                                            .enterpriseVInviteFriend(
                                                    enterprises.get(position).getId(),
                                                    "", "", requestCallBack);
                                }
                            }
                        } else {
                            inputDialog = new InputDialog(mContext,
                                    R.style.MyDialogStyle);
                            inputDialog.setContent("加入政企通讯录", "请输入短号", "完成", "取消");
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
                                    pecketName = "";
                                    pecketName = inputDialog.getInputText();
                                    enterpriseAdd = enterprises.get(position);

                                    if (CommonUtils.isNetworkAvailable(mContext)) {
                                        if (enterprises.get(position).getEnterprise_apply() == 2) {
                                            final MsgDialog msgDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
                                            msgDialog.setContent("确定申请加入吗?", "", "确定", "取消");
                                            msgDialog.setCancleListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    msgDialog.dismiss();
                                                }
                                            });
                                            msgDialog.setOKListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    msgDialog.dismiss();
                                                    InteNetUtils.getInstance(mContext)
                                                            .EnterpriseApplyJoin(
                                                                    enterprises.get(position).getId(),
                                                                    pecketName, new RequestCallBack<String>() {
                                                                        @Override
                                                                        public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                                                                            try {
                                                                                JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                                                                                if (jsonObject.optInt("ret_num") == 0) {
                                                                                    ToastUtils.Errortoast(mContext, "提交申请成功!");
                                                                                }else if (jsonObject.optInt("ret_num") == 1888) {
                                                                                    final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(
                                                                                            ActivitySearchEnterprise.this, R.style.MyDialog1);
                                                                                    hint.setContent("加入政企通讯录成功");
                                                                                    hint.setBtnContent("确定");
                                                                                    hint.show();
                                                                                    hint.setOKListener(new OnClickListener() {

                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            //user.setUpdate(true);
                                                                                            startAnimActivity2Obj(
                                                                                                    ActivityEnterpriseMember.class, "id",
                                                                                                    enterpriseAdd.getId(), "name",
                                                                                                    enterpriseAdd.getName());
                                                                                            sendBroadcast(new Intent(AndroidConfig.refreshEnterpriseList));
                                                                                            AnimFinsh();
                                                                                        }
                                                                                    });

                                                                                    hint.show();
                                                                                    user.setUpdate(false);
                                                                                } else {
                                                                                    ToastUtils.Errortoast(mContext, jsonObject.optString("ret_msg"));
                                                                                }
                                                                            } catch (JSONException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onFailure(HttpException e, String s) {
                                                                            ToastUtils.Errortoast(mContext, "提交申请失败!");
                                                                        }
                                                                    });


                                                }
                                            });
                                            msgDialog.show();

                                        } else {

                                            InteNetUtils
                                                    .getInstance(mContext)
                                                    .enterpriseAdd(
                                                            enterprises.get(position)
                                                                    .getId(),
                                                            pecketName, requestCallBack);
                                        }
                                    }
                                    inputDialog.dismiss();
                                }
                            });
                            inputDialog.show();
                        }
                    }
                }
			};

			iv_add.setOnClickListener(add);
			tv_add.setOnClickListener(add);

			rl_add_enterprise.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {

					final Enterprise enterprise = enterprises.get(position);
					if (enterprise.getInA().equals("1")) {
						if (CommonUtils.isNetworkAvailable(mContext)) {
							InteNetUtils.getInstance(mContext)
									.enterpriseMember(enterprise.getId(), "",
											new RequestCallBack<String>() {
												@Override
												public void onSuccess(
														ResponseInfo<String> arg0) {
													JSONObject jsonObject = null;
													try {
														jsonObject = new JSONObject(
																arg0.result);
														EnterpriseMember member = new EnterpriseMember();
														member = member
																.parseJSON(jsonObject);
														if (member == null
																&& member
																		.getEnterpriseMemberDetails()
																		.size() <= 0) {
														} else {
															memberGroups = member
																	.getEnterpriseMemberDetails();
															for (EnterpriseMemberGroup detail : memberGroups) {
																for (EnterpriseMemberDetail detail2 : detail
																		.getMemberDetails()) {
																	detail2.setGroupName(detail
																			.getGroupName());
																	detail2.setNumber(detail
																			.getNumber());
																	enterpriseMemberDetails
																			.add(detail2);
																}
															}
														}
													} catch (
															JSONException
															| NetRequestException e) {
														e.printStackTrace();
													}

													startAnimActivityForResult5(
															ActivityEnterpriseDetail.class,
															"id",
															enterprise.getId(),
															"member",
															enterpriseMemberDetails,
															1000);
												}

												@Override
												public void onFailure(
														HttpException arg0,
														String arg1) {
													ToastUtils.Infotoast(
															mContext, "网络不可用!");
												}
											});
						}
					}
				}
			});

            tv_introduce.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Enterprise enterprise = enterprises.get(position);
                    String content = "暂无简介";
                    if(!enterprise.getDescription().equals("")){
                        content = enterprise.getDescription();
                    }

                    final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(
                            mContext, R.style.MyDialog1);
                    hint.setContent(content);
                    hint.show();
                    hint.setOKListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            hint.dismiss();
                        }
                    });

                }
            });
			return convertView;
		}
	}

	private RequestCallBack<String> requestCallBack = new RequestCallBack<String>() {

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(arg0.result);

				if (jsonObject.optString("ret_num").equals("0") || jsonObject == null) {
					final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(
							ActivitySearchEnterprise.this, R.style.MyDialog1);
					hint.setContent("加入政企通讯录成功");
					hint.setBtnContent("确定");
					hint.show();
					hint.setOKListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							//user.setUpdate(true);
//							startAnimActivity2Obj(
//									ActivityEnterpriseMember.class, "id",
//									enterpriseAdd.getId(), "name",
//									enterpriseAdd.getName());
                            hint.dismiss();
                            sendBroadcast(new Intent(AndroidConfig.refreshEnterpriseList));

                            Intent intent = new Intent(mContext, ActivityEnterpriseMember.class);
                            intent.putExtra("id", enterpriseAdd.getId());
                            intent.putExtra("name", enterpriseAdd.getName());
                            intent.putExtra("origin", enterpriseAdd.getOrigin());
                            intent.putExtra("type", enterpriseAdd.getType());
                            startActivity(intent);
                            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);


							AnimFinsh();
						}
					});
					user.setUpdate(false);
					// AnimFinsh();
				} else {
					ToastUtils.Infotoast(mContext,
							jsonObject.optString("ret_msg"));
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			ToastUtils.Infotoast(mContext, "网络不可用!");
		}
	};

}
