package com.xunao.benben.ui.item.TallGroup;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.ContactsGroup;
import com.xunao.benben.bean.GroupAddMember;
import com.xunao.benben.bean.GroupAddMemberList;
import com.xunao.benben.bean.GroupAddMembers;
import com.xunao.benben.bean.PhoneInfo;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.MyTextView;

public class ActivityAddGroupMember extends BaseActivity implements
		OnClickListener {

	private static final int ADD_REMARKS = 1000;
	private FloatingGroupExpandableListView listView;
	private myAdapter adapter;
	private ArrayList<GroupAddMember> mGroupAddMember = new ArrayList<GroupAddMember>();
	private ArrayList<GroupAddMembers> allGroupAddMember = new ArrayList<GroupAddMembers>();
	private ArrayList<GroupAddMember> selectMember = new ArrayList<GroupAddMember>();
	private TextView tv_invite_content;
	private Button btn_invite;
	private String enterpriseId;
	private TextView tv_invite_number;
	private LinearLayout no_data;

	private CheckBox all_checkbox;
	private TextView tv_all;

	// private EditText search_edittext;
	private LinearLayout ll_seach_icon;

	// private ImageView iv_search_content_delect;
	private String groupId;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_group_addmember);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		setShowLoding(false);
		initTitle_Right_Left_bar("添加群成员", "", "",
				R.drawable.icon_com_title_left, 0);

		tv_invite_content = (TextView) findViewById(R.id.tv_invite_content);
		btn_invite = (Button) findViewById(R.id.btn_invite);

		// search_edittext = (EditText) findViewById(R.id.search_edittext);
		ll_seach_icon = (LinearLayout) findViewById(R.id.ll_seach_icon);
		// iv_search_content_delect = (ImageView)
		// findViewById(R.id.iv_search_content_delect);
		tv_invite_number = (TextView) findViewById(R.id.tv_invite_number);
		no_data = (LinearLayout) findViewById(R.id.no_data);
		all_checkbox = (CheckBox) findViewById(R.id.all_checkbox);
		tv_all = (TextView) findViewById(R.id.tv_all);
		tv_list = (TextView) findViewById(R.id.tv_list);
		tv_listnum = (TextView) findViewById(R.id.tv_listnum);

		all_checkbox.setChecked(false);

		listView = (FloatingGroupExpandableListView) findViewById(R.id.listview);
		listView.setGroupIndicator(null);
		if (wrapperAdapter == null) {
			adapter = new myAdapter();
			wrapperAdapter = new WrapperExpandableListAdapter(adapter);
			listView.setAdapter(wrapperAdapter);
		} else {
			wrapperAdapter.notifyDataSetChanged();
		}

	}

	private WrapperExpandableListAdapter wrapperAdapter;

	@Override
	public void initDate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		groupId = intent.getStringExtra("GROUPID");
		if (CommonUtils.isNetworkAvailable(mContext)) {
			showLoding("请稍后...");
			InteNetUtils.getInstance(mContext).getGroupMemberListAdd(groupId,
					new RequestCallBack<String>() {

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							dissLoding();

							try {
								JSONObject jsonObject = new JSONObject(
										arg0.result);
								GroupAddMemberList groupAddMemberList = new GroupAddMemberList();

								groupAddMemberList.parseJSON(jsonObject);
								addData(groupAddMemberList);

							} catch (JSONException e) {
								e.printStackTrace();
							} catch (NetRequestException e) {
								e.getError().print(mContext);
								e.printStackTrace();
							}
						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							dissLoding();
							ToastUtils.Errortoast(mContext, "网络不可用,请重试");
						}
					});
		}

	}

	private void addData(GroupAddMemberList groupAddMemberList) {

		if (groupAddMemberList != null
				&& groupAddMemberList.getmGroupAddMember() != null
				&& groupAddMemberList.getmGroupAddMember().size() > 0) {
			no_data.setVisibility(View.GONE);
			allGroupAddMember.addAll(groupAddMemberList.getmGroupAddMember());
			adapter.notifyDataSetChanged();

		} else {
			no_data.setVisibility(View.VISIBLE);
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

		all_checkbox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isAllCheck) {
					isAllCheck = false;
					tv_all.setText("全选");
				} else {
					isAllCheck = true;
				}
				for (GroupAddMembers c : allGroupAddMember) {
					if (c.getmGroupAddMember() != null
							&& c.getmGroupAddMember().size() > 0) {
						c.setSelect(isAllCheck);
						for (GroupAddMember con : c.getmGroupAddMember()) {
							con.setChecked(isAllCheck);
							if (isAllCheck) {
								selectMember.add(con);
							} else {
								selectMember.remove(con);
							}
						}
					}
				}
				tv_list.setText(getSelectName());
				tv_listnum.setText(getSelectNum());
				wrapperAdapter.notifyDataSetChanged();
			}
		});

		btn_invite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (selectMember.size() <= 0) {
					ToastUtils.Infotoast(mContext, "请选择要邀请的好友!");
				} else {
					if (CommonUtils.isNetworkAvailable(mContext)) {
						String ids = "";
						for (GroupAddMember g : selectMember) {
							ids += g.getId() + ",";
						}
						ids = ids.substring(0, ids.length() - 1);
						showLoding("请稍后...");
						InteNetUtils.getInstance(mContext).addGroupJoinMore(
								ids, groupId, mRequestCallBack);
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
		SuccessMsg msg = new SuccessMsg();

		try {
			msg.parseJSON(jsonObject);
			sendBroadcast(new Intent(AndroidConfig.refreshGroupInfo));
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					dissLoding();
					AnimFinsh();
				}
			}, 200);

		} catch (NetRequestException e) {
			dissLoding();
			e.getError().print(mContext);
			e.printStackTrace();
		}

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		dissLoding();
		ToastUtils.Infotoast(mContext, "网络不可用,请重试!");
	}

	private boolean isAllCheck;
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

	class myAdapter extends BaseExpandableListAdapter {

		@Override
		public GroupAddMember getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return allGroupAddMember.get(groupPosition).getmGroupAddMember()
					.get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getChildView(final int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			final GroupAddMember contact = getChild(groupPosition,
					childPosition);
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.item_groupaddmember, null);
			}

			final CheckBox checkBox = (CheckBox) convertView
					.findViewById(R.id.item_all);
			MyTextView chief_name = (MyTextView) convertView
					.findViewById(R.id.chiefNameAndPhone);
			RoundedImageView item_poster = (RoundedImageView) convertView
					.findViewById(R.id.item_poster);

			checkBox.setChecked(contact.isChecked());
			checkBox.setVisibility(View.VISIBLE);

			chief_name.setText(contact.getName() + "  " + contact.getPhone());

			CommonUtils.startImageLoader(cubeimageLoader, contact.getPoster(),
					item_poster);

			OnClickListener click = new OnClickListener() {
				@Override
				public void onClick(View arg0) {

					GroupAddMembers group = getGroup(groupPosition);

					checkBox.setChecked(!contact.isChecked());
					contact.setChecked(!contact.isChecked());

					if (!contact.isChecked()) {
						group.setSelect(false);
						selectMember.remove(contact);
						setIsAllCheck(false);
						myAdapter.this.notifyDataSetChanged();
					} else {
						selectMember.add(contact);
						boolean checkAllSelect = true;

						for (GroupAddMember c : group.getmGroupAddMember()) {
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
							for (GroupAddMembers c : allGroupAddMember) {
								if (!c.isSelect()
										&& c.getmGroupAddMember().size() > 0) {
									allSelect = false;
									break;
								}
							}
							setIsAllCheck(allSelect);
						}

					}
					tv_list.setText(getSelectName());
					tv_listnum.setText(getSelectNum());
				}
			};

			checkBox.setOnClickListener(click);
			convertView.setOnClickListener(click);
			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			return allGroupAddMember.get(groupPosition).getmGroupAddMember()
					.size();
		}

		@Override
		public GroupAddMembers getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return allGroupAddMember.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return allGroupAddMember.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return 0;
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
			final GroupAddMembers group = getGroup(groupPosition);
			holder.item_all.setVisibility(View.VISIBLE);
			holder.item_all.setChecked(group.isSelect());

			if (listView.isGroupExpanded(groupPosition)) {
				holder.status_img
						.setImageResource(R.drawable.icon_contacts_single_down);
			} else {
				holder.status_img
						.setImageResource(R.drawable.icon_contacts_single_right);
			}

			holder.item_group_name.setText(getGroup(groupPosition).getName());

			if (getGroup(groupPosition).getmGroupAddMember().size() > 0) {
				holder.item_all.setClickable(true);
				holder.item_all.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {

						if (group.isSelect()) {
							group.setSelect(false);
							setIsAllCheck(false);
							for (GroupAddMember c : group.getmGroupAddMember()) {
								c.setChecked(false);
								selectMember.remove(c);
							}
							myAdapter.this.notifyDataSetChanged();
						} else {
							group.setSelect(true);
							holder.item_all.setChecked(true);
							for (GroupAddMember c : group.getmGroupAddMember()) {
								c.setChecked(true);
								selectMember.add(c);
							}
							myAdapter.this.notifyDataSetChanged();
							boolean allSelect = true;
							for (GroupAddMembers c : allGroupAddMember) {
								if (!c.isSelect()
										&& c.getmGroupAddMember().size() > 0) {
									allSelect = false;
									break;
								}
							}
							setIsAllCheck(allSelect);

						}
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
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return false;
		}

	}

	class HeaderViewHolder {
		TextView item_group_name;
		ImageView status_img;
		CheckBox item_all;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	private String getSelectName() {
		if (selectMember != null) {
			StringBuffer buffer = new StringBuffer();
			for (GroupAddMember c : selectMember) {
				buffer.append(c.getName() + ",");
			}
			if (buffer.length() > 0) {
				return buffer.substring(0, buffer.length() - 1).toString();

			}
		}
		return "";
	}

	private String getSelectNum() {
		if (selectMember != null && selectMember.size() > 0) {
			return "等" + selectMember.size() + "人";
		}
		return "";
	}

}
