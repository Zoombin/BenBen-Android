package com.xunao.benben.ui.item;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.FriendUnion;
import com.xunao.benben.bean.FriendUnionChief;
import com.xunao.benben.bean.FriendUnionGroup;
import com.xunao.benben.bean.FriendUnionMember;
import com.xunao.benben.bean.FriendUnionMembers;
import com.xunao.benben.bean.FriendUnionOtherChief;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InputDialog;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.MyTextView;
import com.xunao.benben.view.ActionSheet.ActionSheetListener;

public class ActivityFriendUnionMember extends BaseActivity implements
		ActionSheetListener {
	private FriendUnion friendUnion;
	private RoundedImageView chief_poster;
	private MyTextView chief_name;
	private MyTextView chief_types;
	private MyTextView chief_ids_heard;
	private FloatingGroupExpandableListView listView;
	private refrashBroadcast broadcast;
	private myAdapter adapter;
	private TextView friend_union_other_chief;
	private FloatingGroupExpandableListView listView2;

	private FriendUnionChief friendUnionChief;
	private ArrayList<FriendUnionMember> friendUnionMember;
	private FriendUnionMembers friendUnionMembers;
	private ArrayList<FriendUnionOtherChief> friendUnionOtherChiefs = new ArrayList<>();

	private TextView wx_message;

	private WrapperExpandableListAdapter wrapperAdapter;
	private WrapperExpandableListAdapter wrapperAdapter2;

	private boolean isDeleted = false;

	private int groupNumber;
	private View heard;

	private ArrayList<FriendUnionGroup> group = new ArrayList<>();
	private InputDialog inputDialog;
	private MsgDialog msgDialog;

	private int delPosition = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		broadcast = new refrashBroadcast();
		registerReceiver(broadcast, new IntentFilter(
				AndroidConfig.refrashFriendUnionMember));
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_friend_union_member);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("联盟成员", "", "",
				R.drawable.icon_com_title_left, R.drawable.icon_com_title_more);
		setShowLoding(false);

		friendUnion = (FriendUnion) getIntent().getSerializableExtra(
				"friendUnion");
		heard = View.inflate(mContext,
				R.layout.activity_friend_union_member_header, null);
		chief_poster = (RoundedImageView) heard.findViewById(R.id.chief_poster);
		chief_name = (MyTextView) heard.findViewById(R.id.chief_name);
		chief_types = (MyTextView) heard.findViewById(R.id.chief_type);
		chief_ids_heard = (MyTextView) heard.findViewById(R.id.chief_id);

		friend_union_other_chief = (TextView) findViewById(R.id.friend_union_other_chief);
		wx_message = (TextView) findViewById(R.id.wx_message);

		listView = (FloatingGroupExpandableListView) findViewById(R.id.listView);
		listView.addHeaderView(heard);
		listView.setGroupIndicator(null);

		SharedPreferences preferences = getSharedPreferences("alert",
				mContext.MODE_PRIVATE);

		String isShowWx = preferences.getString("friend", "");

		if (isShowWx.equals("1")) {
			wx_message.setVisibility(View.GONE);

			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			lp.bottomMargin = 0;
			listView.setLayoutParams(lp);
		} else {
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("friend", "1");
			editor.commit();
		}

		// chief_name.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// updateBz();
		// }
		// });
		chief_types.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				updateBz();
			}
		});

	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		if (CommonUtils.isNetworkAvailable(mContext)) {
			InteNetUtils.getInstance(mContext).getFriendUnionMember(
					friendUnion.getId(), mRequestCallBack);
		}

		if ((!friendUnion.getType().equals("1"))
				&& (!friendUnion.getType().equals("0"))) {
			initTitle_Right_Left_bar("联盟成员", "", "",
					R.drawable.icon_com_title_left, 0);
		}

		if (friendUnion.getType().equals("0")
				|| friendUnion.getType().equals("1")) {
			wx_message.setVisibility(View.VISIBLE);

			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			lp.bottomMargin = 30;
			listView.setLayoutParams(lp);
		}

	}

	private void updateBz() {
		if (friendUnionChief.getBenbenId().equals(user.getBenbenId())) {
			inputDialog = new InputDialog(mContext, R.style.MyDialogStyle);
			inputDialog.setContent("修改备注", "请输入新的备注名", "确认", "取消");
			inputDialog.setEditContent(friendUnionChief.getRemarkName());
			inputDialog.setCancleListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					inputDialog.dismiss();
				}
			});
			inputDialog.setOKListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					final String pecketName = inputDialog.getInputText();

					// if ("".equals(pecketName)) {
					// ToastUtils.Errortoast(mContext, "备注不能为空");
					// return;
					// }

					// if (pecketName.length() > 12) {
					// ToastUtils.Errortoast(mContext, "备注不能超过12个字");
					// return;
					// }

					if (!CommonUtils.StringIsSurpass2(pecketName, 2, 12)) {
						ToastUtils.Errortoast(mContext, "备注限制在1-12个字之间");
						return;
					}

					if (CommonUtils.isNetworkAvailable(mContext)) {
						InteNetUtils.getInstance(mContext)
								.updateFriendUnionInfo(pecketName, "",
										friendUnion.getId(),
										friendUnionChief.getMemberId(),
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
														ToastUtils.Infotoast(
																mContext,
																"修改备注成功!");
														inputDialog.dismiss();
														friendUnionChief
																.setRemarkName(pecketName);
														chief_types
																.setText(pecketName);
													} else {
														ToastUtils.Errortoast(
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
												ToastUtils.Errortoast(mContext,
														"网络不可用!");
											}
										});
					}
				}
			});
			inputDialog.show();
		}
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.putExtra("member_number", friendUnion.getNumber());
				setResult(1100, intent);
				AnimFinsh();
			}
		});

		setOnRightClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showActionSheet();
			}
		});

	}

	protected void showActionSheet() {
		setTheme(R.style.ActionSheetStyleIOS7);

		if (friendUnion.getType().equals("0")) {
			ActionSheet.createBuilder(this, getSupportFragmentManager())
					.setCancelButtonTitle("取消")
					.setOtherButtonTitles("添加成员", "邀请堂主")
					// 设置颜色 必须一一对应
					.setOtherButtonTitlesColor("#1E82FF", "#1E82FF")
					.setCancelableOnTouchOutside(true).setListener(this).show();
		} else if (friendUnion.getType().equals("1")) {
			ActionSheet.createBuilder(this, getSupportFragmentManager())
					.setCancelButtonTitle("取消")
					.setOtherButtonTitles("添加成员")
					// 设置颜色 必须一一对应
					.setOtherButtonTitlesColor("#1E82FF")
					.setCancelableOnTouchOutside(true)
					.setListener(new ActionSheetListener() {
						@Override
						public void onOtherButtonClick(ActionSheet actionSheet,
								int index) {
							switch (index) {
							case 0:
//								startAnimActivity6Obj(
//										ActivityFriendUnionInviteMember.class,
//										"friendUnionId", friendUnion.getId(),
//										"type", "0", "friendUnionType",
//										friendUnion.getCategory());
                                Intent intent = new Intent(ActivityFriendUnionMember.this, ActivityFriendUnionInviteMember.class);
                                intent.putExtra("friendUnionId", friendUnion.getId());
                                intent.putExtra("type", "0");
                                intent.putExtra("friendUnionType", friendUnion.getCategory());
                                intent.putExtra("remain_chief", friendUnionMembers.getRemain_chief());
                                intent.putExtra("remain_num", friendUnionMembers.getRemain_num());
                                ActivityFriendUnionMember.this.startActivity(intent);
                                ActivityFriendUnionMember.this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
								break;
							}
						}

						@Override
						public void onDismiss(ActionSheet actionSheet,
								boolean isCancel) {

						}
					}).show();
		}

	}

	@Override
	protected void onHttpStart() {
	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {
	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
        Log.d("ltf","jsonObject============="+jsonObject);
		try {
			friendUnionOtherChiefs.clear();
			JSONObject object = jsonObject.optJSONObject("member_info");
			friendUnionMembers = new FriendUnionMembers();
			friendUnionMembers = friendUnionMembers.parseJSON(object);
			friendUnionChief = new FriendUnionChief();
			friendUnionChief = friendUnionMembers.getFriendUnionChief();
			friendUnionOtherChiefs = friendUnionMembers
					.getFriendUnionOtherChief();
			initData();
		} catch (NetRequestException e) {
			e.printStackTrace();
		}
	}

	private void initData() {
		CommonUtils.startImageLoader(cubeimageLoader,
				friendUnionChief.getPoster(), chief_poster);

		chief_poster.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mContext,
                        ActivityContactsInfo.class);
				intent.putExtra("username",
						friendUnionChief.getHuanxinUserName());
				startActivity(intent);
			}
		});

		String nickName = "";
		if (friendUnionChief.getNickName().length() >= 8) {
			String[] nickNameArr = friendUnionChief.getNickName().split("");
			for (int i = 0; i < nickNameArr.length; i++) {
				if (i == 6) {
					nickName += nickNameArr[i] + "\n";
				} else {
					nickName += nickNameArr[i];
				}
			}
		} else {
			nickName = friendUnionChief.getNickName();
		}

		// chief_name.setText(friendUnionChief.getNickName());
		chief_name.setText(nickName);
		chief_types.setText(friendUnionChief.getRemarkName());
		chief_ids_heard.setText("奔犇号：" + friendUnionChief.getBenbenId());

		if (friendUnionOtherChiefs != null && friendUnionOtherChiefs.size() > 0) {
			int number = 0;

			adapter = new myAdapter();
			wrapperAdapter = new WrapperExpandableListAdapter(adapter);
			listView.setAdapter(wrapperAdapter);

			for (int i = 0; i < friendUnionOtherChiefs.size(); i++) {
//				number += friendUnionOtherChiefs.get(i).getMembers().size();
				number += friendUnionOtherChiefs.get(i).getMemberCount();
				if (isDeleted) {
					if (i == groupNumber) {
						listView.expandGroup(i);
					}
				}
			}

			friendUnion.setNumber(number
					+ 1
					+ Integer.parseInt(friendUnionMembers
							.getChief_member_count()));
		}
		if (delPosition != 0) {
			listView.setSelection(delPosition + 1);
		}
		adapter.notifyDataSetChanged();

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Errortoast(mContext, "网络不可用!");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcast);
	}

	class refrashBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if (CommonUtils.isNetworkAvailable(mContext)) {
				InteNetUtils.getInstance(mContext).getFriendUnionMember(
						friendUnion.getId(), mRequestCallBack);
			}
		}

	}

	@Override
	public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

	}

	@Override
	public void onOtherButtonClick(ActionSheet actionSheet, int index) {
		switch (index) {
		case 0:
//			startAnimActivity6Obj(ActivityFriendUnionInviteMember.class,
//					"friendUnionId", friendUnion.getId(), "type", "0",
//					"friendUnionType", friendUnion.getCategory());

            Intent intent = new Intent(this, ActivityFriendUnionInviteMember.class);
            intent.putExtra("friendUnionId", friendUnion.getId());
            intent.putExtra("type", "0");
            intent.putExtra("friendUnionType", friendUnion.getCategory());
            intent.putExtra("remain_chief", friendUnionMembers.getRemain_chief());
            intent.putExtra("remain_num", friendUnionMembers.getRemain_num());
            this.startActivity(intent);
            this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;

		case 1:
//			startAnimActivity6Obj(ActivityFriendUnionInviteMember.class,
//					"friendUnionId", friendUnion.getId(), "type", "1",
//					"friendUnionType", friendUnion.getCategory());
            Intent intent2 = new Intent(this, ActivityFriendUnionInviteMember.class);
            intent2.putExtra("friendUnionId", friendUnion.getId());
            intent2.putExtra("type", "1");
            intent2.putExtra("friendUnionType", friendUnion.getCategory());
            intent2.putExtra("remain_chief", friendUnionMembers.getRemain_chief());
            intent2.putExtra("remain_num", friendUnionMembers.getRemain_num());
            this.startActivity(intent2);
            this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		}
	}

	class myAdapter extends BaseExpandableListAdapter {

		@Override
		public FriendUnionMember getChild(int arg0, int arg1) {
			return friendUnionOtherChiefs.get(arg0).getMembers().get(arg1);
		}

		@Override
		public long getChildId(int arg0, int arg1) {
			return arg1;
		}

		@Override
		public View getChildView(final int groupPosition,
				final int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			final FriendUnionMember unionMember = getChild(groupPosition,
					childPosition);
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.activity_friend_union_member_content, null);
			}

			RoundedImageView chief_poster = (RoundedImageView) convertView
					.findViewById(R.id.chief_poster);
			MyTextView chief_name = (MyTextView) convertView
					.findViewById(R.id.chief_name);
			convertView.findViewById(R.id.item_all).setVisibility(View.GONE);
			;
			final MyTextView chief_type = (MyTextView) convertView
					.findViewById(R.id.chief_type);
			MyTextView chief_ids = (MyTextView) convertView
					.findViewById(R.id.chief_id);

			CommonUtils.startImageLoader(cubeimageLoader,
					unionMember.getPoster(), chief_poster);

			String nickName = "";
			if (unionMember.getNickName().length() >= 8) {
				String[] nickNameArr = unionMember.getNickName().split("");
				for (int i = 0; i < nickNameArr.length; i++) {
					if (i == 6) {
						nickName += nickNameArr[i] + "\n";
					} else {
						nickName += nickNameArr[i];
					}
				}
				
			} else {
				nickName = unionMember.getNickName();
			}

			String remark = "";
			if (unionMember.getRemark().length() >= 8) {
//				String[] remarkArr = unionMember.getRemark().trim().split("");
//				for (int i = 0; i < remarkArr.length; i++) {
//					if (!"".equals(remarkArr[i])) {
//						if (i == 6) {
//							remark += remarkArr[i] + "\n";
//						} else {
//							remark += remarkArr[i];
//						}
//					}
//				}
				remark = unionMember.getRemark();
				chief_type.setSingleLine(false);
				chief_type.setMaxLines(2);
			} else {
				chief_type.setSingleLine(true);
				remark = unionMember.getRemark();
			}

			chief_name.setText(nickName);
			chief_type.setText(remark + " "); 
			chief_ids.setText("奔犇号：" + unionMember.getBenbenId());

			if (TextUtils.isEmpty(unionMember.getRemark())) {
				chief_type.setVisibility(View.GONE);
			} else {
				chief_type.setVisibility(View.VISIBLE);
			}

			chief_poster.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(mContext,
                            ActivityContactsInfo.class);
					intent.putExtra("username",
							unionMember.getHuanxinUserName());
					startActivity(intent);
				}
			});

			chief_type.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (unionMember.getBenbenId().equals(user.getBenbenId())
							|| (friendUnion.getType().equals("0") && (unionMember
									.getType().equals("1")))) {
						inputDialog = new InputDialog(mContext,
								R.style.MyDialogStyle);
						inputDialog.setContent("修改备注", "请输入新的备注名", "确认", "取消");
						inputDialog.setEditContent(unionMember.getRemark());
						inputDialog.setCancleListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								inputDialog.dismiss();
							}
						});
						inputDialog.setOKListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								final String pecketName = inputDialog
										.getInputText();

								// if ("".equals(pecketName)) {
								// ToastUtils.Errortoast(mContext, "备注不能为空");
								// return;
								// }

								if (!CommonUtils.StringIsSurpass2(pecketName,
										2, 12)) {
									ToastUtils.Errortoast(mContext,
											"备注限制在1-12个字之间");
									return;
								}

								// if (pecketName.length() > 12) {
								// ToastUtils.Errortoast(mContext,
								// "备注不能超过12个字");
								// return;
								// }

								if (CommonUtils.isNetworkAvailable(mContext)) {
									InteNetUtils
											.getInstance(mContext)
											.updateFriendUnionInfo(
													pecketName,
													"",
													friendUnion.getId(),
													unionMember.getMemberId(),
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

																if (ret_num
																		.equals("0")) {

																	// String
																	// nickName
																	// = "";
																	// if
																	// (pecketName.length()
																	// >= 8) {
																	// String[]
																	// nickNameArr
																	// =
																	// pecketName.split("");
																	// for (int
																	// i = 0; i
																	// <
																	// nickNameArr.length;
																	// i++) {
																	// if (i ==
																	// 6) {
																	// nickName
																	// +=
																	// nickNameArr[i]
																	// + "\n";
																	// } else {
																	// nickName
																	// +=
																	// nickNameArr[i];
																	// }
																	// }
																	// } else {
																	// nickName
																	// =
																	// pecketName;
																	// }

																	ToastUtils
																			.Infotoast(
																					mContext,
																					ret_msg);
																	inputDialog
																			.dismiss();
																	unionMember
																			.setRemark(pecketName);
																	chief_type
																			.setText(pecketName);
																	chief_type
																			.setVisibility(View.VISIBLE);
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
															ToastUtils
																	.Errortoast(
																			mContext,
																			"网络不可用!");
														}
													});
								}
							}
						});
						inputDialog.show();
					}
				}
			});

			convertView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View arg0) {
					if (friendUnion.getType().equals("0")) {
						delPosition = childPosition;
						deleteMember(unionMember, groupPosition);
					} else {
						if (friendUnion.getType().equals("1")) {
							if (friendUnionOtherChiefs.get(groupPosition)
									.getRight() == 1) {
								if (!unionMember.getBenbenId().equals(
										user.getBenbenId())) {
									delPosition = childPosition;
									deleteMember(unionMember, groupPosition);
									groupNumber = groupPosition;
								}
							}
						}
					}
					return false;
				}
			});

			return convertView;
		}

		@Override
		public int getChildrenCount(int arg0) {
			return friendUnionOtherChiefs.get(arg0).getMembers().size();
		}

		@Override
		public FriendUnionOtherChief getGroup(int arg0) {
			return friendUnionOtherChiefs.get(arg0);
		}

		@Override
		public int getGroupCount() {
			return friendUnionOtherChiefs.size();
		}

		@Override
		public long getGroupId(int arg0) {
			return arg0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			final FriendUnionOtherChief otherChief = getGroup(groupPosition);
			final HeaderViewHolder holder;

			if (convertView == null) {
				holder = new HeaderViewHolder();
				convertView = View.inflate(mContext,
						R.layout.activity_friend_union_hader, null);
				holder.group_name = (TextView) convertView
						.findViewById(R.id.group_name);
				holder.status_img = (ImageView) convertView
						.findViewById(R.id.status_img);
				holder.group_num = (TextView) convertView
						.findViewById(R.id.group_num);
				holder.tv_title = (TextView) convertView
						.findViewById(R.id.tv_title);
				holder.ll_hader = (LinearLayout) convertView
						.findViewById(R.id.ll_hader);
				holder.iv_edit = (ImageView) convertView
						.findViewById(R.id.iv_edit);

				convertView.setTag(holder);
			} else {
				holder = (HeaderViewHolder) convertView.getTag();
			}

			if (otherChief.getType() == 1 || otherChief.getType() == 3) {
				holder.tv_title.setVisibility(View.GONE);
				holder.ll_hader.setVisibility(View.VISIBLE);

			} else {
				holder.tv_title.setVisibility(View.VISIBLE);
				holder.ll_hader.setVisibility(View.GONE);

			}

			holder.group_name.setText(otherChief.getName());
			if ("盟主成员".equals(otherChief.getName())) {
				holder.group_num.setText(friendUnionMembers
						.getChief_member_count() + "人");
			} else {
//				holder.group_num.setText(otherChief.getMembers().size() + "人");
				holder.group_num.setText(otherChief.getMemberCount()+ "人");
			}

			if (listView.isGroupExpanded(groupPosition)) {
				holder.status_img
						.setImageResource(R.drawable.icon_contacts_single_down);
			} else {
				holder.status_img
						.setImageResource(R.drawable.icon_contacts_single_right);
			}

			if (friendUnion.getType().equals("0")) {
				if (otherChief.getRight() == 1) {
					holder.iv_edit.setVisibility(View.VISIBLE);
				} else {
					holder.iv_edit.setVisibility(View.GONE);
				}
			} else if (friendUnion.getType().equals("1")) {
				holder.iv_edit.setVisibility(View.GONE);
				if (otherChief.getRight() == 1) {
					holder.iv_edit.setVisibility(View.VISIBLE);
				}
			} else {
				holder.iv_edit.setVisibility(View.VISIBLE);
			}

			if (otherChief.getRight() == 1) {
				holder.iv_edit.setVisibility(View.VISIBLE);
			} else {
				holder.iv_edit.setVisibility(View.GONE);
			}

			holder.iv_edit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (otherChief.getRight() == 1) {
						inputDialog = new InputDialog(mContext,
								R.style.MyDialogStyle);
						inputDialog.setContent("修改堂名", "请输入新的堂名", "确认", "取消");
						inputDialog.setEditContent(otherChief.getName());
						inputDialog.setCancleListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								inputDialog.dismiss();
							}
						});
						inputDialog.setOKListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								final String pecketName = inputDialog
										.getInputText();

								if ("".equals(pecketName)) {
									ToastUtils.Errortoast(mContext, "备注不能为空");
									return;
								}

								// if (pecketName.length() > 12) {
								// ToastUtils.Errortoast(mContext,
								// "备注限制在1-12个字之间");
								// return;
								// }

								if (!CommonUtils.StringIsSurpass2(pecketName,
										2, 12)) {
									ToastUtils.Errortoast(mContext,
											"备注限制在1-12个字之间");
									return;
								}

								if (CommonUtils.isNetworkAvailable(mContext)) {
									InteNetUtils
											.getInstance(mContext)
											.updateFriendUnionInfo(
													"",
													pecketName,
													friendUnion.getId(),
													otherChief.getId(),
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

																if (ret_num
																		.equals("0")) {
																	ToastUtils
																			.Infotoast(
																					mContext,
																					"修改堂名称成功!");
																	inputDialog
																			.dismiss();
																	otherChief
																			.setName(pecketName);
																	holder.group_name
																			.setText(pecketName);

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
															ToastUtils
																	.Errortoast(
																			mContext,
																			"网络不可用!");
														}
													});
								}
							}
						});
						inputDialog.show();
					} else {
						ToastUtils.Errortoast(mContext, "无法更改此堂名!");
					}
				}
			});

			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
			return false;
		}

	}

	class HeaderViewHolder {
		TextView group_name;
		TextView group_num;
		ImageView status_img;
		TextView tv_title;
		LinearLayout ll_hader;
		ImageView iv_edit;
	}

	private void deleteMember(final FriendUnionMember member,
			final int GroupPostion) {
		msgDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
		msgDialog.setContent("删除成员", "是否删除该成员", "确认", "取消");
		msgDialog.setCancleListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				msgDialog.dismiss();
			}
		});
		msgDialog.setOKListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InteNetUtils.getInstance(mContext).exitFriendUnionMember(
						friendUnion.getId(), member.getMemberId(), "1",
						new RequestCallBack<String>() {

							@Override
							public void onSuccess(ResponseInfo<String> arg0) {
								String result = arg0.result;

								JSONObject jsonObject;
								try {
									jsonObject = new JSONObject(result);
									String ret_num = jsonObject
											.optString("ret_num");
									String ret_msg = jsonObject
											.optString("ret_msg");

									if (ret_num.equals("0")) {
										ToastUtils.Infotoast(mContext,
												"删除成员成功!");

										listView.expandGroup(GroupPostion);
										msgDialog.dismiss();

										friendUnion.setNumber(friendUnion
												.getNumber() - 1);
										isDeleted = true;
										sendBroadcast(new Intent(
												AndroidConfig.refreshFriendUnion));

										if (CommonUtils
												.isNetworkAvailable(mContext)) {
											InteNetUtils
													.getInstance(mContext)
													.getFriendUnionMember(
															friendUnion.getId(),
															mRequestCallBack);
										}

									} else {
										ToastUtils
												.Errortoast(mContext, ret_msg);
									}

								} catch (JSONException e) {
									e.printStackTrace();
								}

							}

							@Override
							public void onFailure(HttpException arg0,
									String arg1) {
								ToastUtils.Errortoast(mContext, "网络不可用!");
							}
						});
				msgDialog.dismiss();
			}
		});

		listView.expandGroup(GroupPostion);
		msgDialog.show();
	}

}
