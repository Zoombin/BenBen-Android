package com.xunao.benben.ui.item;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.EnterpriseInMember;
import com.xunao.benben.bean.EnterpriseMemberDetail;
import com.xunao.benben.bean.EnterpriseMyMemberList;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.hx.chatuidemo.utils.CommonUtils;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.PhoneUtils;
import com.xunao.benben.utils.ToastUtils;

public class ActivityEnterpriseMyAddContacts extends BaseActivity {
	private ListView listview;
	private String enterpriseId;;
	private myAdapter adapter;
	private LinearLayout no_data;

	private MsgDialog inputDialog;

	private ArrayList<EnterpriseInMember> inMembers = new ArrayList<EnterpriseInMember>();
	private EnterpriseMyMemberList myMemberList;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_enterprise_my_add_contacts);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("我添加的联系人", "", "",
				R.drawable.icon_com_title_left, 0);

		listview = (ListView) findViewById(R.id.listview);
		adapter = new myAdapter();
		listview.setAdapter(adapter);

		no_data = (LinearLayout) findViewById(R.id.no_data);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		enterpriseId = getIntent().getStringExtra("enterpriseId");
		if (CommonUtils.isNetWorkConnected(mContext)) {
			InteNetUtils.getInstance(mContext).getMyInviteEnterpriseMember(
					enterpriseId, mRequestCallBack);
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
	}

	@Override
	protected void onHttpStart() {

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		inMembers.clear();
		myMemberList = new EnterpriseMyMemberList();
		try {
			myMemberList = myMemberList.parseJSON(jsonObject);
			inMembers = myMemberList.getMembers();
		} catch (NetRequestException e) {
			e.printStackTrace();
		}

		if (inMembers.size() > 0 && inMembers != null) {
			no_data.setVisibility(View.GONE);
			listview.setVisibility(View.VISIBLE);
			adapter.notifyDataSetChanged();
		} else {
			no_data.setVisibility(View.VISIBLE);
			listview.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Errortoast(mContext, "网络不可用!");
	}

	class myAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return inMembers.size();
		}

		@Override
		public EnterpriseInMember getItem(int arg0) {
			return inMembers.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			ViewHolder holder;
			holder = new ViewHolder();
			final EnterpriseInMember inMember = inMembers.get(position);

	//		if (convertView == null) {
				convertView = View.inflate(mContext,
						R.layout.activity_enterprise_member_item, null);
				holder.tv_enterprise_name = (TextView) convertView
						.findViewById(R.id.tv_enterprise_name);
				holder.iv_message = (ImageView) convertView
						.findViewById(R.id.iv_message);
				holder.iv_make_phone = (ImageView) convertView
						.findViewById(R.id.iv_make_phone);
				holder.tv_enterprise_phone = (TextView) convertView
						.findViewById(R.id.tv_enterprise_phone);
				holder.tv_enterprise_shortphone = (TextView) convertView
						.findViewById(R.id.tv_enterprise_shortphone);
				holder.iv_shortmessage = (ImageView) convertView
						.findViewById(R.id.iv_shortmessage);
				holder.iv_make_shortphone = (ImageView) convertView
						.findViewById(R.id.iv_make_shortphone);
				holder.shortBox = (LinearLayout) convertView
						.findViewById(R.id.shortBox);
				holder.longBox = (LinearLayout) convertView
						.findViewById(R.id.longBox);
				holder.item_select = (ImageView) convertView
						.findViewById(R.id.item_select);
				holder.ll_longphone = (LinearLayout) convertView.findViewById(R.id.ll_longphone);
				convertView.setTag(holder);
				
				holder.tv_enterprise_name.setText(inMember.getName());
				holder.tv_enterprise_phone.setText(inMember.getPhone());
				holder.tv_enterprise_shortphone.setText(inMember.getShortPhone());

				holder.item_select.setVisibility(View.VISIBLE);

				if (TextUtils.isEmpty(inMember.getPhone())
						|| (inMember.getPhone() == null)) {
					holder.tv_enterprise_phone.setVisibility(View.GONE);
					holder.iv_make_phone.setVisibility(View.GONE);
					holder.iv_message.setVisibility(View.GONE);
					
					holder.longBox.setVisibility(View.GONE);
				} else {
					holder.tv_enterprise_phone.setVisibility(View.VISIBLE);
					holder.iv_make_phone.setVisibility(View.VISIBLE);
					holder.iv_message.setVisibility(View.VISIBLE);
				}
				
				
				if (TextUtils.isEmpty(inMember.getShortPhone())
						|| (inMember.getShortPhone() == null)) {
					holder.tv_enterprise_shortphone.setVisibility(View.GONE);
					holder.iv_make_shortphone.setVisibility(View.GONE);
					holder.iv_shortmessage.setVisibility(View.GONE);
					holder.shortBox.setVisibility(View.GONE);
				} else {
					holder.tv_enterprise_shortphone.setVisibility(View.VISIBLE);
					holder.iv_make_shortphone.setVisibility(View.VISIBLE);
					holder.iv_shortmessage.setVisibility(View.VISIBLE);
					holder.shortBox.setVisibility(View.VISIBLE);
				}

				holder.iv_make_phone.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						PhoneUtils.makeCall(Integer.parseInt(inMember.getId()),inMember.getName(),
								inMember.getPhone(), mContext);
					}
				});
				
//			} else {
//				holder = (ViewHolder) convertView.getTag();
//			}


			holder.iv_message.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					PhoneUtils.sendSMS(inMember.getPhone(),"", mContext);
				}
			});

			holder.iv_make_shortphone.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					PhoneUtils.makeCall(Integer.parseInt(inMember.getId()),inMember.getName(),
							inMember.getShortPhone(), mContext);
				}
			});

			holder.iv_shortmessage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					PhoneUtils.sendSMS(inMember.getShortPhone(),"", mContext);
				}
			});
			
			

			holder.item_select.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					inputDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
					inputDialog.setContent("删除成员", "是否删除该成员", "确认", "取消");
					inputDialog.setCancleListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							inputDialog.dismiss();
						}
					});
					inputDialog.setOKListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							InteNetUtils.getInstance(mContext)
									.delMyAddEnterpriseMember(enterpriseId,
											inMember.getId(),
											new RequestCallBack<String>() {
												@Override
												public void onSuccess(
														ResponseInfo<String> arg0) {
													String result = arg0.result;
													JSONObject jsonObject;
													try {
														jsonObject = new JSONObject(
																result);
														String ret_num = jsonObject
																.optString("ret_num");
														String ret_msg = jsonObject
																.optString("ret_msg");

														if (ret_num.equals("0")) {
															ToastUtils
																	.Infotoast(
																			mContext,
																			"删除成员成功!");
															if (CommonUtils
																	.isNetWorkConnected(mContext)) {
																InteNetUtils
																		.getInstance(
																				mContext)
																		.getMyInviteEnterpriseMember(
																				enterpriseId,
																				mRequestCallBack);
															}
															
															sendBroadcast(new Intent(
																	AndroidConfig.refreshEnterpriseList));
															
															sendBroadcast(new Intent(
																	AndroidConfig.refreshEnterpriseDetail));
															sendBroadcast(new Intent(
																	AndroidConfig.refreshEnterpriseMember));
														} else {
															ToastUtils
																	.Errortoast(
																			mContext,
																			ret_msg);
														}

													} catch (JSONException e) {
														e.printStackTrace();
													}

												}

												@Override
												public void onFailure(
														HttpException arg0,
														String arg1) {
													ToastUtils.Errortoast(
															mContext, "网络不可用!");
												}
											});
							inputDialog.dismiss();
						}
					});
					inputDialog.show();
				}
			});

			return convertView;
		}
	}

	class ViewHolder {
		TextView tv_enterprise_name;
		TextView tv_enterprise_phone;
		ImageView iv_message;
		ImageView iv_make_phone;
		TextView tv_enterprise_shortphone;
		ImageView iv_shortmessage;
		ImageView iv_make_shortphone;
		ImageView item_select;
		LinearLayout shortBox;
		LinearLayout ll_longphone;
		LinearLayout longBox;

	}
}
