package com.xunao.benben.ui.item;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.baidu.platform.comapi.map.m;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.EnterpriseMember;
import com.xunao.benben.bean.EnterpriseMemberDetail;
import com.xunao.benben.bean.EnterpriseMemberDetailList;
import com.xunao.benben.bean.EnterpriseMemberGroup;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoMsgHint;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PhoneUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.utils.ViewHolderUtil;
import com.xunao.benben.view.MyTextView;

public class ActivityAddCommon extends BaseActivity {
	private String enterpriseId;
	private EditText search_edittext;
    private TextView searchName;
	private LinearLayout ll_seach_icon;
	private ImageView iv_search_content_delect;
	private ListView listView;
	private LinearLayout no_data;
	private String searchKey = "";
	private ArrayList<EnterpriseMemberDetail> memberDetails = new ArrayList<EnterpriseMemberDetail>();
	private EnterpriseMemberDetailList member;
	private myAdapter adapter;
	private boolean isCommon = false;
	private boolean change = false;
	private int commonNum = 0;
    private String type;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_enterprise_choice_member);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("查找通讯录成员", "", "",
				R.drawable.icon_com_title_left, 0);

		search_edittext = (EditText) findViewById(R.id.search_edittext);
        searchName = (TextView) findViewById(R.id.searchName);
        searchName.setText("搜索手机号/姓名");

		ll_seach_icon = (LinearLayout) findViewById(R.id.ll_seach_icon);
		iv_search_content_delect = (ImageView) findViewById(R.id.iv_search_content_delect);

		listView = (ListView) findViewById(R.id.listview);
		no_data = (LinearLayout) findViewById(R.id.no_data);
		no_data.setVisibility(View.VISIBLE);

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

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.bottomMargin = 0;
		listView.setLayoutParams(lp);

		adapter = new myAdapter();
		listView.setAdapter(adapter);

	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		enterpriseId = getIntent().getStringExtra("enterpriseId");
		commonNum = getIntent().getIntExtra("commonNum", 0);
        type = getIntent().getStringExtra("type");
		// if (CommonUtils.isNetworkAvailable(mContext)) {
		// InteNetUtils.getInstance(mContext).searchEnterprisesMember(
		// enterpriseId, "", mRequestCallBack);
		// }

	}

	@Override
	public void onBackPressed() {
		if (change) {
			sendBroadcast(new Intent(AndroidConfig.refreshEnterpriseMember));
		}
		AnimFinsh();

	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});

		// setOnRightClickLinester(new OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// onBackPressed();
		// }
		// });

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

					// 更新关键字
					searchKey = search_edittext.getText().toString().trim();
					if (CommonUtils.isNetworkAvailable(mContext)
							&& !CommonUtils.isEmpty(searchKey)) {
						InteNetUtils.getInstance(mContext)
								.searchEnterprisesMember(enterpriseId,
										searchKey, mRequestCallBack);
					}
					return true;
				}
				return false;
			}
		});

		iv_search_content_delect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				iv_search_content_delect.setVisibility(View.GONE);
				ll_seach_icon.setVisibility(View.VISIBLE);
				search_edittext.setText("");
				// 影藏键盘
				((InputMethodManager) search_edittext.getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(mContext.getCurrentFocus()
								.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);

//				if (CommonUtils.isNetworkAvailable(mContext)) {
//					InteNetUtils.getInstance(mContext).searchEnterprisesMember(
//							enterpriseId, "", mRequestCallBack);
//				}
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
			member = new EnterpriseMemberDetailList();
			member = member.parseJSON(jsonObject);
			memberDetails.clear();
			if (member.getEnterpriseMemberDetails() != null) {
				ArrayList<EnterpriseMemberDetail> memberGroups = member
						.getEnterpriseMemberDetails();
				memberDetails.addAll(member.getEnterpriseMemberDetails());
				no_data.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);

			} else {
				no_data.setVisibility(View.VISIBLE);
				listView.setVisibility(View.GONE);
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

	class myAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return memberDetails.size();
		}

		@Override
		public Object getItem(int arg0) {
			return memberDetails.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			final EnterpriseMemberDetail detail = memberDetails.get(position);
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.activity_enterprise_member_item, null);
			}
			View shortBox = ViewHolderUtil.get(convertView, R.id.shortBox);
			LinearLayout ll_enterprise_member = ViewHolderUtil.get(convertView,
					R.id.ll_enterprise_member);
			// TextView tv_enterprise_group = ViewHolderUtil.get(convertView,
			// R.id.tv_enterprise_group);
			TextView tv_enterprise_name = ViewHolderUtil.get(convertView,
					R.id.tv_enterprise_name);
			LinearLayout longBox = ViewHolderUtil
					.get(convertView, R.id.longBox);
			TextView tv_enterprise_phone = ViewHolderUtil.get(convertView,
					R.id.tv_enterprise_phone);
			TextView tv_enterprise_shortphone = ViewHolderUtil.get(convertView,
					R.id.tv_enterprise_shortphone);

			ImageView iv_shortmessage = ViewHolderUtil.get(convertView,
					R.id.iv_shortmessage);

			ImageView iv_message = ViewHolderUtil.get(convertView,
					R.id.iv_message);
			ImageView iv_make_phone = ViewHolderUtil.get(convertView,
					R.id.iv_make_phone);
			ImageView iv_make_shortphone = ViewHolderUtil.get(convertView,
					R.id.iv_make_shortphone);

			// tv_enterprise_group.setVisibility(View.GONE);
			tv_enterprise_name.setText(detail.getName());
			tv_enterprise_phone.setText(detail.getPhone());

			if (TextUtils.isEmpty(detail.getShortPhone())) {
				tv_enterprise_shortphone.setVisibility(View.GONE);
			} else {
				tv_enterprise_shortphone.setVisibility(View.VISIBLE);
			}

			if (detail.getCommon().equals("0")) {
				isCommon = false;
				tv_enterprise_name.setTextColor(Color.parseColor("#000000"));
				tv_enterprise_phone.setTextColor(Color.parseColor("#000000"));
				tv_enterprise_shortphone.setTextColor(Color
						.parseColor("#000000"));
			} else {
				isCommon = true;
				tv_enterprise_name.setTextColor(Color.RED);
				tv_enterprise_phone.setTextColor(Color.RED);
				tv_enterprise_shortphone.setTextColor(Color.RED);
			}

			// if (detail.getPhone().equals(user.getPhone())) {
			// iv_message.setVisibility(View.GONE);
			// iv_make_phone.setVisibility(View.GONE);
			// } else {
			// iv_message.setVisibility(View.VISIBLE);
			// iv_make_phone.setVisibility(View.VISIBLE);
			// }

			if (TextUtils.isEmpty(detail.getShortPhone()) || detail.getShortPhone().equals("0")) {
				shortBox.setVisibility(View.GONE);
			} else {
				shortBox.setVisibility(View.VISIBLE);
			}
			if (type.equals("3") || TextUtils.isEmpty(detail.getPhone()) || detail.getPhone().equals("0")) {
				longBox.setVisibility(View.GONE);
			} else {
				longBox.setVisibility(View.VISIBLE);
			}

			tv_enterprise_shortphone.setText(detail.getShortPhone());

			iv_make_phone.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
                    InteNetUtils.getInstance(mContext).AddTelNum(detail.getId(), new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                        }
                    });
					PhoneUtils.makeCall(Integer.parseInt(detail.getId()),detail.getName(), detail.getPhone(),
							mContext);
				}
			});

			iv_message.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					PhoneUtils.sendSMS(detail.getPhone(), "", mContext);
				}
			});

			iv_make_shortphone.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
                    InteNetUtils.getInstance(mContext).AddTelNum(detail.getId(), new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                        }
                    });
					PhoneUtils.makeCall(Integer.parseInt(detail.getId()),detail.getName(),
							detail.getShortPhone(), mContext);
				}
			});

			iv_shortmessage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					PhoneUtils.sendSMS(detail.getShortPhone(), "", mContext);
				}
			});

			convertView.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View arg0) {
					if (detail.getCommon().equals("1")) {
						showInfoMsgDialog("删除常用联系人", "是否将" + detail.getName()
								+ "从常用联系人中删除", detail.getId(), true, position);
					} else {
						if (commonNum >= 50) {
							ToastUtils.Errortoast(mContext, "常用联系人已满");
						} else {
							showInfoMsgDialog("添加常用联系人",
									"是否将" + detail.getName() + "设为常用联系人",
									detail.getId(), false, position);
						}
					}

					return true;
				}
			});

			return convertView;
		}
	}

	private void showInfoMsgDialog(String msg, String msg2,
			final String memberId, final boolean status, final int positions) {
		hint = new InfoMsgHint(mContext, R.style.MyDialog1);
		hint.setContent(msg, msg2, "完成", "取消");
		hint.setOKListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!status) {
					if (CommonUtils.isNetworkAvailable(mContext)) {
						InteNetUtils.getInstance(mContext).addCommon(
								enterpriseId, memberId,
								new RequestCallBack<String>() {

									@Override
									public void onFailure(HttpException arg0,
											String arg1) {
										ToastUtils
												.Infotoast(mContext, "网络不可用!");
									}

									@Override
									public void onSuccess(
											ResponseInfo<String> arg0) {
										try {
											JSONObject jsonObject = new JSONObject(
													arg0.result);
											
											if (jsonObject.optString("ret_num")
													.equals("0")) {
												ToastUtils.Infotoast(mContext,
														"添加常用联系人成功!");
												memberDetails.get(positions)
														.setCommon("1");
												change = true;
												commonNum += 1;
											} else {
												
												ToastUtils
														.Infotoast(
																mContext,
																jsonObject
																		.optString("ret_msg"));
											}
										} catch (JSONException e) {
											e.printStackTrace();
											ToastUtils.Infotoast(mContext,
													"操作失败!");
										}

										adapter.notifyDataSetChanged();
									}
								});
					}
				} else {
					if (CommonUtils.isNetworkAvailable(mContext)) {
						InteNetUtils.getInstance(mContext).delCommon(
								enterpriseId, memberId,
								new RequestCallBack<String>() {

									@Override
									public void onFailure(HttpException arg0,
											String arg1) {
										ToastUtils
												.Infotoast(mContext, "网络不可用!");
									}

									@Override
									public void onSuccess(
											ResponseInfo<String> arg0) {
										try {
											JSONObject jsonObject = new JSONObject(
													arg0.result);
											if (jsonObject.optString("ret_num")
													.equals("0")) {
												ToastUtils.Infotoast(mContext,
														"删除常用联系人成功!");
												memberDetails.get(positions)
														.setCommon("0");
												change = true;
												commonNum -= 1;
											} else {
												ToastUtils
														.Infotoast(
																mContext,
																jsonObject
																		.optString("ret_msgd"));
											}
										} catch (JSONException e) {
											e.printStackTrace();
											ToastUtils.Infotoast(mContext,
													"操作失败!");
										}

										adapter.notifyDataSetChanged();
									}
								});
					}

				}
				hint.dismiss();
			}
		});
		hint.setCancleListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				hint.dismiss();
			}
		});
		hint.show();
	}

}
