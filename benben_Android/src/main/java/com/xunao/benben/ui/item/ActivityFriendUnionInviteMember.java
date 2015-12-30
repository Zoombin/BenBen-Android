package com.xunao.benben.ui.item;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.array;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.xunao.benben.bean.FriendUnionInviteMember;
import com.xunao.benben.bean.FriendUnionInviteMemberList;
import com.xunao.benben.bean.FriendUnionMemberGroup;
import com.xunao.benben.bean.GroupAddMember;
import com.xunao.benben.bean.GroupAddMembers;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.item.ActivityEnterpriseMember.HeaderViewHolder;
import com.xunao.benben.ui.item.ActivityEnterpriseMember.myAdapter;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.MyTextView;

public class ActivityFriendUnionInviteMember extends BaseActivity {
	private FloatingGroupExpandableListView listView;
	private LinearLayout no_data;
	private String friendUnionId;
	private CheckBox all_checkbox;
	private TextView tv_all;
	private Button btn_invite;
	private WrapperExpandableListAdapter wrapperAdapter;

	private ArrayList<FriendUnionInviteMember> members = new ArrayList<>();
	private FriendUnionInviteMemberList inviteMemberList;
	private ArrayList<FriendUnionInviteMember> inviteMembers = new ArrayList<>();
	private ArrayList<FriendUnionMemberGroup> memberGroups = new ArrayList<>();

	private myAdapter adapter;
	private String type;
	private String category;
    private int max_num;
    private TextView tv_select_num;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_friend_union_invite_member);
		initdefaultImage(R.drawable.ic_group_poster);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
        category = getIntent().getStringExtra("friendUnionType");
        if(category.equals("2")) {
            initTitle_Right_Left_bar("添加成员", "", "",
                    R.drawable.icon_com_title_left, 0);
        }else{
            initTitle_Right_Left_bar("添加成员", "", "手动添加",
                    R.drawable.icon_com_title_left, 0);
        }

		listView = (FloatingGroupExpandableListView) findViewById(R.id.listView);
		no_data = (LinearLayout) findViewById(R.id.no_data);
		all_checkbox = (CheckBox) findViewById(R.id.all_checkbox);
		tv_all = (TextView) findViewById(R.id.tv_all);
		tv_list = (TextView) findViewById(R.id.tv_list);
		tv_listnum = (TextView) findViewById(R.id.tv_listnum);
		btn_invite = (Button) findViewById(R.id.btn_invite);
        tv_select_num = (TextView) findViewById(R.id.tv_select_num);

		listView.setGroupIndicator(null);
		adapter = new myAdapter();
		wrapperAdapter = new WrapperExpandableListAdapter(adapter);
		listView.setAdapter(wrapperAdapter);
		btn_invite.setText("添加");

	}

	private void getData() {
		if (CommonUtils.isNetworkAvailable(mContext)) {
			InteNetUtils.getInstance(mContext).getInviteFriendUnionMember(
					friendUnionId, type, mRequestCallBack);
		}
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		friendUnionId = getIntent().getStringExtra("friendUnionId");
		type = getIntent().getStringExtra("type");
		getData();

		if ("1".equals(type)) {
			initTitle_Right_Left_bar("邀请堂主", "", "",
					R.drawable.icon_com_title_left, 0);
			btn_invite.setText("邀请");
            max_num = getIntent().getIntExtra("remain_chief",0);
		}else{
            max_num = getIntent().getIntExtra("remain_num",0);
        }

        tv_select_num.setText("(0/"+max_num+")");
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
            public void onClick(View view) {
                if(max_num>0) {
//                    startAnimActivity2Obj(ActivityFriendUnionInviteMemberBySelf.class, "friendUnionId", friendUnionId);

                    Intent intent = new Intent(ActivityFriendUnionInviteMember.this, ActivityFriendUnionInviteMemberBySelf.class);
                    intent.putExtra("friendUnionId", friendUnionId);
                    ActivityFriendUnionInviteMember.this.startActivityForResult(intent, 1);
                    ActivityFriendUnionInviteMember.this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }else{
                    ToastUtils.Infotoast(mContext,"添加成员已达上限");
                }
            }
        });


		all_checkbox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isAllCheck) {
					isAllCheck = false;
					tv_all.setText("全选");
				} else {
					isAllCheck = true;
				}

				for (FriendUnionMemberGroup c : memberGroups) {
					if (c.getMember() != null && c.getMember().size() > 0) {
						c.setSelect(isAllCheck);
						for (FriendUnionInviteMember con : c.getMember()) {
							con.setChecked(isAllCheck);
							if (isAllCheck) {
								if (!inviteMembers.contains(con)) {
									inviteMembers.add(con);
								}
							} else {
								if (inviteMembers.contains(con)) {
									inviteMembers.remove(con);
								}
							}
						}
					}
				}
                tv_select_num.setText("("+inviteMembers.size()+"/"+max_num+")");
				tv_list.setText(getSelectName());
				tv_listnum.setText(getSelectNum());
				wrapperAdapter.notifyDataSetChanged();
			}
		});

		btn_invite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (inviteMembers.size() <= 0) {
                    if ("1".equals(type)) {
                        ToastUtils.Infotoast(mContext, "请选择邀请的成员!");
                    }else{
                        ToastUtils.Infotoast(mContext, "请选择添加的成员!");
                    }
					return;
				}else if(inviteMembers.size() > max_num){
                    if ("1".equals(type)) {
                        ToastUtils.Infotoast(mContext, "邀请的成员超过上限!");
                    }else{
                        ToastUtils.Infotoast(mContext, "添加的成员超过上限!");
                    }
                    return;
                }else {
					String memberId = "";
					for (FriendUnionInviteMember member : inviteMembers) {
						memberId += member.getBenbenId() + ",";
					}
					if (CommonUtils.isNetworkAvailable(mContext)) {
						InteNetUtils.getInstance(mContext)
								.inviteFriendUnionMember(
										friendUnionId,
										memberId.substring(0,
												memberId.length() - 1), type,
										new RequestCallBack<String>() {

											@Override
											public void onSuccess(
													ResponseInfo<String> arg0) {
												JSONObject jsonObject;
												try {
													jsonObject = new JSONObject(
															arg0.result);
													String ret_num = jsonObject
															.optString("ret_num");
													if (ret_num.equals("0")) {
                                                        if ("1".equals(type)) {
                                                            ToastUtils.Infotoast(mContext, "邀请成功!");
                                                        }else{
                                                            ToastUtils.Infotoast(mContext, "添加成功!");
                                                        }
														sendBroadcast(new Intent(
																AndroidConfig.refrashFriendUnionMember));
														sendBroadcast(new Intent(
																AndroidConfig.refreshFriendUnion));
														AnimFinsh();

													} else {
														ToastUtils
																.Infotoast(
																		mContext,
																		jsonObject
																				.optString("ret_msg"));
													}
												} catch (JSONException e) {
													e.printStackTrace();
												}
											}

											@Override
											public void onFailure(
													HttpException arg0,
													String arg1) {
												ToastUtils.Infotoast(mContext,
														"网络不可用,请重试!");
											}
										});
					}
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
		inviteMemberList = new FriendUnionInviteMemberList();
		try {
			inviteMemberList = inviteMemberList.parseJSON(jsonObject);
			memberGroups = inviteMemberList.getMembers();
			if (memberGroups == null || memberGroups.size() <= 0) {
				no_data.setVisibility(View.VISIBLE);
				listView.setVisibility(View.GONE);
			} else {
				no_data.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
			}

			for (int i = 0; i < memberGroups.size(); i++) {
				// listView.expandGroup(i);
				for (FriendUnionInviteMember unionInviteMember : memberGroups
						.get(i).getMember()) {
					if (category.equals("2")) {
						if(!type.equals("0")){
							unionInviteMember.setChecked(false);
						}else{
							unionInviteMember.setChecked(true);
							memberGroups.get(i).setSelect(true);
							inviteMembers.add(unionInviteMember);
						}
						// inviteMembers.add(unionInviteMember);
					}
				}
				

			}

			adapter.notifyDataSetChanged();
		} catch (NetRequestException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Infotoast(mContext, "网络不可用,请重试!");
	}

	class myAdapter extends BaseExpandableListAdapter {

		@Override
		public FriendUnionInviteMember getChild(int arg0, int arg1) {
			return memberGroups.get(arg0).getMember().get(arg1);
		}

		@Override
		public long getChildId(int arg0, int arg1) {
			return arg1;
		}

		@Override
		public View getChildView(final int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			final FriendUnionInviteMember member = getChild(groupPosition,
					childPosition);
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.activity_friend_union_invite_member_item,
						null);
			}

			final CheckBox checkBox = (CheckBox) convertView
					.findViewById(R.id.item_phone_checkbox);
			RoundedImageView item_phone_poster = (RoundedImageView) convertView
					.findViewById(R.id.item_phone_poster);
			MyTextView item_phone_name = (MyTextView) convertView
					.findViewById(R.id.item_phone_name);
//			MyTextView item_phone_phone = (MyTextView) convertView
//					.findViewById(R.id.item_phone_phone);
			MyTextView item_phone_benben = (MyTextView) convertView
					.findViewById(R.id.item_phone_benben);

			item_phone_name.setText(member.getName());
			checkBox.setChecked(member.isChecked());
			checkBox.setVisibility(View.VISIBLE);
		//	item_phone_phone.setText(member.getPhone());
			CommonUtils.startImageLoader(cubeimageLoader, member.getPoster(),
					item_phone_poster);

			item_phone_benben.setText("奔犇号: " + member.getBenbenId());

//			item_phone_phone.setVisibility(View.GONE);

			OnClickListener click = new OnClickListener() {
				@Override
				public void onClick(View arg0) {

					FriendUnionMemberGroup group = getGroup(groupPosition);

					checkBox.setChecked(!member.isChecked());
					member.setChecked(!member.isChecked());

					if (!member.isChecked()) {
						group.setSelect(false);
						inviteMembers.remove(member);
						setIsAllCheck(false);
						myAdapter.this.notifyDataSetChanged();
					} else {
                        if(inviteMembers.size()<max_num) {

                            inviteMembers.add(member);
                            boolean checkAllSelect = true;

                            for (FriendUnionInviteMember c : group.getMember()) {
                                if (!c.isChecked()) {
                                    checkAllSelect = false;
                                    break;
                                }
                            }
                            group.setSelect(checkAllSelect);
                            group.setSelect(checkAllSelect);
                            myAdapter.this.notifyDataSetChanged();
                            if (checkAllSelect) {
                                boolean allSelect = true;
                                for (FriendUnionMemberGroup c : memberGroups) {
                                    if (!c.isSelect() && c.getMember().size() > 0) {
                                        allSelect = false;
                                        break;
                                    }
                                }
                                setIsAllCheck(allSelect);
                            }
                        }else{
                            checkBox.setChecked(!member.isChecked());
                            member.setChecked(!member.isChecked());
                            ToastUtils.Infotoast(mContext,"选择成员已达上限");
                        }

					}
                    tv_select_num.setText("("+inviteMembers.size()+"/"+max_num+")");
					tv_list.setText(getSelectName());
					tv_listnum.setText(getSelectNum());
				}
			};

			checkBox.setOnClickListener(click);
			convertView.setOnClickListener(click);

			return convertView;
		}

		@Override
		public int getChildrenCount(int arg0) {
			return memberGroups.get(arg0).getMember().size();
		}

		@Override
		public FriendUnionMemberGroup getGroup(int arg0) {
			return memberGroups.get(arg0);
		}

		@Override
		public int getGroupCount() {
			return memberGroups.size();
		}

		@Override
		public long getGroupId(int arg0) {
			return arg0;
		}

		@Override
		public View getGroupView(final int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {

			final HeaderViewHolder holder;

			if (convertView == null) {
				holder = new HeaderViewHolder();
				convertView = View.inflate(mContext,
						R.layout.activity_friend_union_member_group_head, null);
				holder.item_group_name = (TextView) convertView
						.findViewById(R.id.item_group_name);
				holder.status_img = (ImageView) convertView
						.findViewById(R.id.status_img);
				holder.item_all = (CheckBox) convertView
						.findViewById(R.id.item_all);
				convertView.setTag(holder);
			} else {
				holder = (HeaderViewHolder) convertView.getTag();
			}
			final FriendUnionMemberGroup group = getGroup(groupPosition);
			holder.item_all.setVisibility(View.VISIBLE);
			holder.item_all.setChecked(group.isSelect());

			if (listView.isGroupExpanded(groupPosition)) {
				holder.status_img
						.setImageResource(R.drawable.icon_contacts_single_down);
			} else {
				holder.status_img
						.setImageResource(R.drawable.icon_contacts_single_right);
			}

			holder.item_group_name.setText(getGroup(groupPosition)
					.getGroupName());

			if (getGroup(groupPosition).getMember().size() > 0) {
				holder.item_all.setClickable(true);
				holder.item_all.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {

						if (group.isSelect()) {
							group.setSelect(false);
							setIsAllCheck(false);
							for (FriendUnionInviteMember c : group.getMember()) {
								c.setChecked(false);
								inviteMembers.remove(c);
							}
							myAdapter.this.notifyDataSetChanged();
						} else {

                            if((inviteMembers.size()+group.getMember().size())<=max_num) {

                                group.setSelect(true);
                                holder.item_all.setChecked(true);
                                for (FriendUnionInviteMember c : group.getMember()) {
                                    c.setChecked(true);
                                    inviteMembers.add(c);
                                }
                                myAdapter.this.notifyDataSetChanged();
                                boolean allSelect = true;
                                for (FriendUnionMemberGroup c : memberGroups) {
                                    if (!c.isSelect() && c.getMember().size() > 0) {
                                        allSelect = false;
                                        break;
                                    }
                                }
                                setIsAllCheck(allSelect);
                            }else{
                                holder.item_all.setChecked(false);
                                ToastUtils.Infotoast(mContext,"选择人员超过上限");
                            }

						}
                        tv_select_num.setText("("+inviteMembers.size()+"/"+max_num+")");
						tv_list.setText(getSelectName());
						tv_listnum.setText(getSelectNum());
					}
				});
			} else {
				holder.item_all.setClickable(false);
			}

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (listView.isGroupExpanded(groupPosition)) {
						listView.collapseGroup(groupPosition);
					} else {
						listView.expandGroup(groupPosition);
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

	boolean isAllCheck;
	private TextView tv_list;
	private TextView tv_listnum;

	private void setIsAllCheck(boolean allCheck) {

		if (allCheck) {
			isAllCheck = true;
			all_checkbox.setChecked(true);
		} else {
			isAllCheck = false;
			all_checkbox.setChecked(false);
			tv_all.setText("全选");
		}
	}

	class HeaderViewHolder {
		TextView item_group_name;
		ImageView status_img;
		CheckBox item_all;
	}

	private String getSelectName() {
		if (inviteMembers != null) {
			StringBuffer buffer = new StringBuffer();
			for (FriendUnionInviteMember c : inviteMembers) {
				buffer.append(c.getName() + ",");
			}
			if (buffer.length() > 0) {
				return buffer.substring(0, buffer.length() - 1).toString();

			}
		}
		return "";
	}

	private String getSelectNum() {
		if (inviteMembers != null && inviteMembers.size() > 0) {
			return "等" + inviteMembers.size() + "人";
		}
		return "";
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    sendBroadcast(new Intent(
                            AndroidConfig.refrashFriendUnionMember));
                    sendBroadcast(new Intent(
                            AndroidConfig.refreshFriendUnion));
                    AnimFinsh();
                }

                break;
        }
    }
}
