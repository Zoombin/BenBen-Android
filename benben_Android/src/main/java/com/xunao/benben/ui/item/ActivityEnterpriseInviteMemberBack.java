package com.xunao.benben.ui.item;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import u.aly.db;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.BxContacts;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.ContactsEnterprise;
import com.xunao.benben.bean.ContactsGroup;
import com.xunao.benben.bean.ContactsGroupEnterprise;
import com.xunao.benben.bean.Enterprise;
import com.xunao.benben.bean.EnterpriseMemberDetail;
import com.xunao.benben.bean.EnterpriseVirtualMember;
import com.xunao.benben.bean.EnterpriseVirtualMemberGroup;
import com.xunao.benben.bean.EnterpriseVirtualMemberList;
import com.xunao.benben.bean.GroupAddMember;
import com.xunao.benben.bean.PhoneInfo;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.item.ActivityChoiceFriend.ItemHolder;
import com.xunao.benben.ui.item.ActivityFriendUnionMember.HeaderViewHolder;
import com.xunao.benben.ui.item.ActivityFriendUnionMember.myAdapter;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

public class ActivityEnterpriseInviteMemberBack extends BaseActivity implements
		OnClickListener {

	private static final int ADD_REMARKS = 1000;
	private FloatingGroupExpandableListView listView;
	private myAdapter adapter;
	private ArrayList<ContactsEnterprise> contacts = new ArrayList<ContactsEnterprise>();
	private ArrayList<ContactsEnterprise> groupContacts = new ArrayList<ContactsEnterprise>();
	private ArrayList<ContactsEnterprise> contacts2 = new ArrayList<ContactsEnterprise>();
	private TextView tv_invite_content;
	private Button btn_invite;
	private String enterpriseId;
	private TextView tv_invite_number;
	private LinearLayout no_data;
	private Enterprise enterprise;
	private String enterpriseType = "";
	private ArrayList<EnterpriseMemberDetail> memberDetails = new ArrayList<EnterpriseMemberDetail>();
	private ArrayList<EnterpriseVirtualMember> groupVirtualMembers = new ArrayList<EnterpriseVirtualMember>();

	ArrayList<EnterpriseVirtualMemberGroup> membersGroups = new ArrayList<EnterpriseVirtualMemberGroup>();

	private FloatingGroupExpandableListView listView2;

	private ArrayList<ContactsGroupEnterprise> contactsGroups = new ArrayList<ContactsGroupEnterprise>();

	private vMyAdapter vAdapter;
	private WrapperExpandableListAdapter wrapperAdapter;

	// 虚拟网
	private ArrayList<EnterpriseVirtualMember> virtualMembers = new ArrayList<EnterpriseVirtualMember>();
	private EnterpriseVirtualMemberList virtualMemberList;

	private CheckBox all_checkbox;
	private TextView tv_all;

	private EditText search_edittext;
	private LinearLayout ll_seach_icon;

	private ImageView iv_search_content_delect;
	private boolean isAllChecked = false;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_enterprise_invite_member);
		initdefaultImage(R.drawable.ic_group_poster);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("添加成员", "", "",
				R.drawable.icon_com_title_left, 0);

		tv_invite_content = (TextView) findViewById(R.id.tv_invite_content);
		btn_invite = (Button) findViewById(R.id.btn_invite);

		search_edittext = (EditText) findViewById(R.id.search_edittext);
		ll_seach_icon = (LinearLayout) findViewById(R.id.ll_seach_icon);
		iv_search_content_delect = (ImageView) findViewById(R.id.iv_search_content_delect);
		tv_invite_number = (TextView) findViewById(R.id.tv_invite_number);
		no_data = (LinearLayout) findViewById(R.id.no_data);
		all_checkbox = (CheckBox) findViewById(R.id.all_checkbox);
		tv_all = (TextView) findViewById(R.id.tv_all);

		all_checkbox.setChecked(false);

		listView = (FloatingGroupExpandableListView) findViewById(R.id.listview);
		listView.setGroupIndicator(null);

		all_checkbox.setOnCheckedChangeListener(changeListener);

		iv_search_content_delect.setOnClickListener(this);

		listView2 = (FloatingGroupExpandableListView) findViewById(R.id.listView2);
		listView2.setVisibility(View.GONE);
		listView2.setGroupIndicator(null);

	}

	OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean checked) {
			if (enterpriseType.equals("1")) {
				if (checked) {
					String str = "";
					for (ContactsGroupEnterprise group : contactsGroups) {
						if (group.getmContacts() != null
								&& group.getmContacts().size() > 0) {
							for (ContactsEnterprise contact : group
									.getmContacts()) {
								if (!groupContacts.contains(contact)) {
									groupContacts.add(contact);
									str += contact.getName() + ",";
									contact.setChecked(true);
								}
							}
						}
					}
					if (groupContacts.size() > 0) {
						tv_invite_content.setText(str.substring(0,
								str.length() - 1));
						tv_invite_number.setText("等" + groupContacts.size()
								+ "人");
					}
				} else {
					tv_all.setText("全选");
					for (ContactsGroupEnterprise group : contactsGroups) {
						for (ContactsEnterprise contact : group.getmContacts()) {
							if (groupContacts.contains(contact)) {
								groupContacts.remove(contact);
								contact.setChecked(false);
							}
						}
					}
					tv_invite_content.setText("");
					tv_invite_number.setText("");
				}

				adapter.notifyDataSetChanged();
			} else {
				if (checked) {
					// tv_all.setText("取消");
					String str = "";
					for (EnterpriseVirtualMemberGroup member : membersGroups) {
						for (EnterpriseVirtualMember virtualMember : member
								.getVirtualMembers()) {
							if (!groupVirtualMembers.contains(virtualMember)) {
								groupVirtualMembers.add(virtualMember);
								str += virtualMember.getName() + ",";
								virtualMember.setIschecked(true);
							}

						}
					}
					//
					// for (EnterpriseVirtualMember member : virtualMembers) {
					// if (!groupVirtualMembers.contains(member)) {
					// groupVirtualMembers.add(member);
					// str += member.getName() + ",";
					// member.setIschecked(true);
					// }
					// }
					tv_invite_content
							.setText(str.substring(0, str.length() - 1));
					tv_invite_number.setText("等" + groupVirtualMembers.size()
							+ "人");

				} else {
					tv_all.setText("全选");

					for (EnterpriseVirtualMemberGroup member : membersGroups) {
						for (EnterpriseVirtualMember virtualMember : member
								.getVirtualMembers()) {
							if (groupVirtualMembers.contains(virtualMember)) {
								groupVirtualMembers.remove(virtualMember);
								virtualMember.setIschecked(false);
							}

						}
					}

					// for (EnterpriseVirtualMember member :
					// groupVirtualMembers) {
					// member.setIschecked(false);
					// }
					// groupVirtualMembers.removeAll(virtualMembers);
					tv_invite_content.setText("");
					tv_invite_number.setText("");

				}
				vAdapter.notifyDataSetChanged();
			}

		}
	};
	private WrapperExpandableListAdapter wrapperAdapter2;

	@Override
	public void initDate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		enterprise = (Enterprise) intent.getSerializableExtra("enterprise");
		enterpriseId = enterprise.getId();
		enterpriseType = enterprise.getType();
		memberDetails = (ArrayList<EnterpriseMemberDetail>) intent
				.getSerializableExtra("member");

//		if (enterprise.getType().equals("1")) {
			btn_invite.setText("添加");

			InteNetUtils.getInstance(mContext).getEnterpriseM(enterpriseId,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							ToastUtils.Errortoast(mContext, "当前网络不可用,");
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {

							try {
								JSONObject jsonObject = new JSONObject(
										arg0.result);

								jsonObject.toString();

								SuccessMsg msg = new SuccessMsg();

								msg.checkJson(jsonObject);

								JSONArray optJSONArray = jsonObject
										.optJSONArray("member_ginfo");

								ArrayList<ContactsEnterprise> arrayList = new ArrayList<ContactsEnterprise>();
								if (optJSONArray != null) {
									int length = optJSONArray.length();
									for (int i = 0; i < length; i++) {
										JSONObject optJSONObject = optJSONArray
												.optJSONObject(i);
										ContactsEnterprise ce = new ContactsEnterprise();
										ce.setId(optJSONObject.optInt("id")
												+ "");
										ce.setName(optJSONObject
												.optString("name"));
										ce.setPhone(optJSONObject
												.optString("phone"));
										arrayList.add(ce);

									}
								}

								try {
									List conGroupList = dbUtil.findAll(Selector
											.from(ContactsGroup.class).where(
													WhereBuilder.b("id", "<>",
															"10000")));

									ContactsGroupEnterprise cge = null;
									for (ContactsGroup cg : (ArrayList<ContactsGroup>) conGroupList) {
										cge = new ContactsGroupEnterprise();
										cge.setId(cg.getId());
										cge.setName(cg.getName());
										cge.setOpen(cg.isOpen());
										cge.setProportion(cg.getProportion());
										cge.setSelect(cg.isSelect());
										cge.setCreated_time(cg
												.getCreated_time());
										contactsGroups.add(cge);
									}

									groupOrderBy();

									for (ContactsGroupEnterprise group : contactsGroups) {
										List contact = dbUtil.findAll(Selector
												.from(Contacts.class)
												.where(WhereBuilder.b(
														"group_id", "=",
														group.getId()))
												.orderBy("is_benben", true));

										ArrayList<ContactsEnterprise> contacts = new ArrayList<ContactsEnterprise>();
										ArrayList<ContactsEnterprise> contactsArrayList = new ArrayList<ContactsEnterprise>();

										ContactsEnterprise ce = null;
										for (Contacts c : (ArrayList<Contacts>) contact) {
											ce = new ContactsEnterprise();
											ce.setId(c.getId() + "");
											ce.setName(c.getName());
											ce.setChecked(c.isChecked());
											ce.setGroup_id(c.getGroup_id());
											ce.setHasPinYin(c.isHasPinYin());
											ce.setHuanxin_username(c
													.getHuanxin_username());
											ce.setIs_baixing(c.getIs_baixing());
											ce.setIs_benben(c.getIs_benben());
											ce.setIs_friend(c.getIs_friend());
											ce.setPhone(c.getPhone());
											ce.setPhones(c.getPhones());
											ce.setPinyin(c.getPinyin());
											ce.setPoster(c.getPoster());
											ce.setRemark(c.getRemark());
											contacts.add(ce);
										}

										for (ContactsEnterprise con : contacts) {
											List phoneList = dbUtil.findAll(Selector
													.from(PhoneInfo.class)
													.where(WhereBuilder.b(
															"contacts_id", "=",
															con.getId())));

											ArrayList<PhoneInfo> phoneInfos = new ArrayList<PhoneInfo>();
											phoneInfos = (ArrayList<PhoneInfo>) phoneList;

											for (PhoneInfo info : phoneInfos) {
												ContactsEnterprise contacts2 = new ContactsEnterprise();
												contacts2.setGroup_id(con
														.getGroup_id());
												contacts2.setId(con.getId());
												contacts2.setPhone(info
														.getPhone());
												contacts2.setChecked(con
														.isChecked());
												contacts2.setPoster(con
														.getPoster());
												contacts2.setName(con.getName());

												contactsArrayList
														.add(contacts2);
											}
											contactsArrayList
													.removeAll(arrayList);
										}
										group.setmContacts(contactsArrayList);
									}
								} catch (DbException e) {
									e.printStackTrace();
									ToastUtils.Infotoast(mContext,
											"获取数据失败,请重试!");
								}

								adapter = new myAdapter();
								wrapperAdapter2 = new WrapperExpandableListAdapter(
										adapter);
								listView.setAdapter(wrapperAdapter2);

								for (int i = 0; i < contactsGroups.size(); i++) {
									listView.expandGroup(i);
								}

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (NetRequestException e) {
								e.printStackTrace();
								e.getError().print(mContext);
							}

						}
					});

//		} else {
//			btn_invite.setText("添加");
//			search_edittext.setVisibility(View.GONE);
//			ll_seach_icon.setVisibility(View.GONE);
//			iv_search_content_delect.setVisibility(View.GONE);
//			ll_search_item.setVisibility(View.GONE);
//			if (CommonUtils.isNetworkAvailable(mContext)) {
//				InteNetUtils.getInstance(mContext).getEnterpriseMemberList(
//						enterpriseId, mRequestCallBack);
//			}
//		}
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
					String searchKey = search_edittext.getText().toString()
							.trim();
					try {
						groupContacts.clear();
						tv_invite_content.setText("");
						tv_all.setText("全选");
						List contact = dbUtil.findAll(Selector
								.from(Contacts.class)
								.where(WhereBuilder
										.b("group_id", "<>", "10000"))
								.and(WhereBuilder.b("name", "like", "%"
										+ searchKey + "%"))
								.orderBy("is_benben", true));
						ArrayList<ContactsEnterprise> contactsList = new ArrayList<ContactsEnterprise>();
						ArrayList<ContactsEnterprise> contactsArrayList = new ArrayList<ContactsEnterprise>();

						contactsList = (ArrayList<ContactsEnterprise>) contact;
						for (ContactsEnterprise con : contactsList) {

							List list = dbUtil.findAll(Selector.from(
									PhoneInfo.class).where(
									WhereBuilder.b("contacts_id", "=",
											con.getId())));

							ArrayList<PhoneInfo> phoneInfos = (ArrayList<PhoneInfo>) list;

							for (PhoneInfo info : phoneInfos) {
								ContactsEnterprise contacts2 = new ContactsEnterprise();
								contacts2.setGroup_id(con.getGroup_id());
								contacts2.setId(con.getId());
								contacts2.setPhone(info.getPhone());
								contacts2.setChecked(con.isChecked());
								contacts2.setPoster(con.getPoster());
								contacts2.setName(con.getName());

								contactsArrayList.add(contacts2);
							}
						}

						for (ContactsGroupEnterprise group : contactsGroups) {
							group.getmContacts().clear();
							ArrayList<ContactsEnterprise> arrayList = new ArrayList<ContactsEnterprise>();
							for (ContactsEnterprise con : contactsArrayList) {
								if ((group.getId() + "").equals(con
										.getGroup_id())) {
									arrayList.add(con);
								}
							}
							group.setmContacts(arrayList);

						}

						adapter.notifyDataSetChanged();

					} catch (DbException e) {
						e.printStackTrace();
						ToastUtils.Infotoast(mContext, "获取数据失败,请重试!");
					}

					return true;
				}
				return false;
			}
		});

		btn_invite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (enterpriseType.equals("1")) {
					if (groupContacts.size() <= 0) {
						ToastUtils.Infotoast(mContext, "请选择要添加的好友!");
					} else {
						startAnimActivityForResult5(ActivityAddRemarks.class,
								"enterpriseId", enterpriseId, "contacts",
								groupContacts, ADD_REMARKS);
					}
				} else {
					if (groupContacts.size() <= 0) {
						ToastUtils.Infotoast(mContext, "请选择要添加的好友!");
					} else {
						startAnimActivityForResult5(ActivityAddRemarks.class,
								"enterpriseId", enterpriseId, "virtualMembers",
								groupContacts, ADD_REMARKS);
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
		virtualMemberList = new EnterpriseVirtualMemberList();
		try {
			virtualMemberList = virtualMemberList.parseJSON(jsonObject);

			if (virtualMemberList.getMembers() != null) {
				membersGroups = virtualMemberList.getMembers();
				if (membersGroups == null || membersGroups.size() <= 0) {
					no_data.setVisibility(View.VISIBLE);
					listView.setVisibility(View.GONE);
				} else {
					no_data.setVisibility(View.GONE);
					listView.setVisibility(View.VISIBLE);
				}

				listView.setVisibility(View.GONE);
			}
			listView.setVisibility(View.VISIBLE);
			vAdapter = new vMyAdapter();
			wrapperAdapter = new WrapperExpandableListAdapter(vAdapter);
			listView.setAdapter(wrapperAdapter);

			for (int i = 0; i < membersGroups.size(); i++) {
				listView.expandGroup(i);
			}

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
		public ContactsEnterprise getChild(int arg0, int arg1) {
			return contactsGroups.get(arg0).getmContacts().get(arg1);
		}

		@Override
		public long getChildId(int arg0, int arg1) {
			return arg1;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			final ContactsEnterprise contacts = getChild(groupPosition,
					childPosition);
			final ItemHolder itemHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.activity_packet_info_item, null);

				itemHolder = new ItemHolder();
				itemHolder.item_pinyin = (TextView) convertView
						.findViewById(R.id.item_pinyin);
				itemHolder.item_phone_checkbox = (CheckBox) convertView
						.findViewById(R.id.item_phone_checkbox);
				itemHolder.item_phone_poster = (RoundedImageView) convertView
						.findViewById(R.id.item_phone_poster);
				itemHolder.item_phone_name = (TextView) convertView
						.findViewById(R.id.item_phone_name);
				itemHolder.item_phone_phone = (TextView) convertView
						.findViewById(R.id.item_phone_phone);
				convertView.setTag(itemHolder);
			} else {
				itemHolder = (ItemHolder) convertView.getTag();
			}

			itemHolder.item_pinyin.setVisibility(View.GONE);
			itemHolder.item_phone_checkbox.setVisibility(View.VISIBLE);
			itemHolder.item_phone_checkbox.setChecked(contacts.isChecked());
			CommonUtils.startImageLoader(cubeimageLoader, contacts.getPoster(),
					itemHolder.item_phone_poster);
			itemHolder.item_phone_name.setText(contacts.getName() + "  "
					+ contacts.getPhone());
			// itemHolder.item_phone_phone.setText(contacts.getPhone());

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {

					if (itemHolder.item_phone_checkbox.isChecked()) {
						itemHolder.item_phone_checkbox.setChecked(false);
						if (groupContacts.contains(contacts)) {
							groupContacts.remove(contacts);
							contacts.setChecked(false);
							int size = 0;
							for (int i = 0; i < contactsGroups.size(); i++) {
								size += contactsGroups.get(i).getmContacts()
										.size();
							}
							if (groupContacts.size() < size) {
								all_checkbox.setOnCheckedChangeListener(null);
								all_checkbox.setChecked(false);
								tv_all.setText("全选");
								all_checkbox
										.setOnCheckedChangeListener(changeListener);
							}

						}

						if (groupContacts.size() <= 0) {
							tv_invite_content.setText("");
							tv_invite_number.setText("");
						} else {

							tv_invite_content.setText(tv_invite_content
									.getText().toString()
									.replaceAll(contacts.getName() + ",", ""));
							tv_invite_number.setText("等" + groupContacts.size()
									+ "人");
						}
					} else {
						itemHolder.item_phone_checkbox.setChecked(true);
						if (!groupContacts.contains(contacts)) {
							groupContacts.add(contacts);
							contacts.setChecked(true);
							int size = 0;

							for (int i = 0; i < contactsGroups.size(); i++) {

								size += contactsGroups.get(i).getmContacts()
										.size();
							}

							if (groupContacts.size() >= size) {
								all_checkbox.setOnCheckedChangeListener(null);
								all_checkbox.setChecked(true);
								// tv_all.setText("取消");
								all_checkbox
										.setOnCheckedChangeListener(changeListener);
							}
						}

						tv_invite_content.setText(tv_invite_content.getText()
								.length() <= 0 ? contacts.getName()
								: tv_invite_content.getText() + ","
										+ contacts.getName());
						tv_invite_number.setText("等" + groupContacts.size()
								+ "人");
					}
				}

			});

			return convertView;
		}

		@Override
		public int getChildrenCount(int arg0) {
			return contactsGroups.get(arg0).getmContacts().size();
		}

		@Override
		public ContactsGroupEnterprise getGroup(int arg0) {
			return contactsGroups.get(arg0);
		}

		@Override
		public int getGroupCount() {
			return contactsGroups.size();
		}

		@Override
		public long getGroupId(int arg0) {
			return arg0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup arg3) {

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

			holder.iv_edit.setVisibility(View.GONE);
			holder.tv_title.setVisibility(View.GONE);

			holder.group_num.setText("");
			holder.group_name.setText(getGroup(groupPosition).getName() + "  ("
					+ getGroup(groupPosition).getmContacts().size() + ")");

			if (listView.isGroupExpanded(groupPosition)) {
				holder.status_img
						.setImageResource(R.drawable.icon_contacts_single_down);
			} else {
				holder.status_img
						.setImageResource(R.drawable.icon_contacts_single_right);
			}

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

	class ItemHolder {
		TextView item_pinyin;
		CheckBox item_phone_checkbox;
		RoundedImageView item_phone_poster;
		TextView item_phone_name;
		TextView item_phone_phone;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_search_content_delect:
			iv_search_content_delect.setVisibility(View.GONE);
			ll_seach_icon.setVisibility(View.VISIBLE);
			search_edittext.setText("");
			// 影藏键盘
			((InputMethodManager) search_edittext.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(mContext.getCurrentFocus()
							.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
			contacts.clear();
			contacts2.clear();
			try {
				groupContacts.clear();
				tv_invite_content.setText("");
				tv_all.setText("全选");
				List contact = dbUtil.findAll(Selector.from(Contacts.class)
						.where(WhereBuilder.b("group_id", "<>", "10000"))
						.orderBy("is_benben", true));
				ArrayList<ContactsEnterprise> contactsList = new ArrayList<>();
				ArrayList<ContactsEnterprise> contactsArrayList = new ArrayList<ContactsEnterprise>();

				contactsList = (ArrayList<ContactsEnterprise>) contact;
				for (ContactsEnterprise con : contactsList) {

					List list = dbUtil.findAll(Selector.from(PhoneInfo.class)
							.where(WhereBuilder.b("contacts_id", "=",
									con.getId())));

					ArrayList<PhoneInfo> phoneInfos = (ArrayList<PhoneInfo>) list;

					for (PhoneInfo info : phoneInfos) {
						ContactsEnterprise contacts2 = new ContactsEnterprise();
						contacts2.setGroup_id(con.getGroup_id());
						contacts2.setId(con.getId());
						contacts2.setPhone(info.getPhone());
						contacts2.setChecked(con.isChecked());
						contacts2.setPoster(con.getPoster());
						contacts2.setName(con.getName());

						contactsArrayList.add(contacts2);
					}
				}

				for (ContactsGroupEnterprise group : contactsGroups) {
					group.getmContacts().clear();
					ArrayList<ContactsEnterprise> arrayList = new ArrayList<ContactsEnterprise>();
					for (ContactsEnterprise con : contactsArrayList) {
						if ((group.getId() + "").equals(con.getGroup_id())) {
							arrayList.add(con);
						}
					}
					group.setmContacts(arrayList);

				}

				adapter.notifyDataSetChanged();

			} catch (DbException e) {
				e.printStackTrace();
				ToastUtils.Infotoast(mContext, "获取数据失败,请重试!");
			}
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if (arg0 == ADD_REMARKS) {
			if (arg2 != null) {
				Intent intent = new Intent();
				setResult(2, intent);
				AnimFinsh();
			}
		}
		super.onActivityResult(arg0, arg1, arg2);
	}

	class vMyAdapter extends BaseExpandableListAdapter {

		@Override
		public EnterpriseVirtualMember getChild(int arg0, int arg1) {
			return membersGroups.get(arg0).getVirtualMembers().get(arg1);
		}

		@Override
		public long getChildId(int arg0, int arg1) {
			return arg1;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			final EnterpriseVirtualMember virtualMember = getChild(
					groupPosition, childPosition);
			final ItemHolder itemHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.activity_packet_info_item, null);

				itemHolder = new ItemHolder();
				itemHolder.item_pinyin = (TextView) convertView
						.findViewById(R.id.item_pinyin);
				itemHolder.item_phone_checkbox = (CheckBox) convertView
						.findViewById(R.id.item_phone_checkbox);
				itemHolder.item_phone_poster = (RoundedImageView) convertView
						.findViewById(R.id.item_phone_poster);
				itemHolder.item_phone_name = (TextView) convertView
						.findViewById(R.id.item_phone_name);
				itemHolder.item_phone_phone = (TextView) convertView
						.findViewById(R.id.item_phone_phone);
				convertView.setTag(itemHolder);
			} else {
				itemHolder = (ItemHolder) convertView.getTag();
			}

			itemHolder.item_pinyin.setVisibility(View.GONE);
			itemHolder.item_phone_checkbox.setVisibility(View.VISIBLE);
			itemHolder.item_phone_checkbox.setChecked(virtualMember
					.isIschecked());
			CommonUtils.startImageLoader(cubeimageLoader,
					virtualMember.getPoster(), itemHolder.item_phone_poster);
			itemHolder.item_phone_name.setText(virtualMember.getName() + "  "
					+ virtualMember.getPhone());
			// itemHolder.item_phone_phone.setText(virtualMember.getPhone());

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {

					if (itemHolder.item_phone_checkbox.isChecked()) {
						itemHolder.item_phone_checkbox.setChecked(false);
						if (groupVirtualMembers.contains(virtualMember)) {
							groupVirtualMembers.remove(virtualMember);
							virtualMember.setIschecked(false);
							int size = 0;
							for (int i = 0; i < membersGroups.size(); i++) {
								size += membersGroups.get(i)
										.getVirtualMembers().size();
							}
							if (groupVirtualMembers.size() < size) {
								all_checkbox.setOnCheckedChangeListener(null);
								all_checkbox.setChecked(false);
								tv_all.setText("全选");
								all_checkbox
										.setOnCheckedChangeListener(changeListener);
							}

						}

						if (groupVirtualMembers.size() <= 0) {
							tv_invite_content.setText("");
							tv_invite_number.setText("");
						} else {

							tv_invite_content.setText(tv_invite_content
									.getText().subSequence(
											0,
											tv_invite_content.getText()
													.length()
													- (virtualMember.getName()
															.length() + 1)));
							tv_invite_number.setText("等"
									+ groupVirtualMembers.size() + "人");
						}
					} else {
						itemHolder.item_phone_checkbox.setChecked(true);
						if (!groupVirtualMembers.contains(virtualMember)) {
							groupVirtualMembers.add(virtualMember);
							virtualMember.setIschecked(true);
							int size = 0;

							for (int i = 0; i < membersGroups.size(); i++) {

								size += membersGroups.get(i)
										.getVirtualMembers().size();
							}

							if (groupVirtualMembers.size() >= size) {
								all_checkbox.setOnCheckedChangeListener(null);
								all_checkbox.setChecked(true);
								// tv_all.setText("取消");
								all_checkbox
										.setOnCheckedChangeListener(changeListener);
							}
						}

						tv_invite_content.setText(tv_invite_content.getText()
								.length() <= 0 ? virtualMember.getName()
								: tv_invite_content.getText() + ","
										+ virtualMember.getName());
						tv_invite_number.setText("等"
								+ groupVirtualMembers.size() + "人");
					}
				}

			});

			return convertView;
		}

		@Override
		public int getChildrenCount(int arg0) {
			return membersGroups.get(arg0).getVirtualMembers().size();
		}

		@Override
		public EnterpriseVirtualMemberGroup getGroup(int arg0) {
			return membersGroups.get(arg0);
		}

		@Override
		public int getGroupCount() {
			return membersGroups.size();
		}

		@Override
		public long getGroupId(int arg0) {
			return arg0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup arg3) {

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

			holder.iv_edit.setVisibility(View.GONE);
			holder.tv_title.setVisibility(View.GONE);

			holder.group_num.setText("");
			holder.group_name.setText(getGroup(groupPosition).getName());

			if (listView.isGroupExpanded(groupPosition)) {
				holder.status_img
						.setImageResource(R.drawable.icon_contacts_single_down);
			} else {
				holder.status_img
						.setImageResource(R.drawable.icon_contacts_single_right);
			}

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

	private void groupOrderBy() {
		int size = contactsGroups.size();
		// 对组群排序,未分组放在后面
		ContactsGroupEnterprise unGroup = null;
		ContactsGroupEnterprise unGroup2 = null;
		for (int i = 0; i < size; i++) {
			if (contactsGroups.get(i).getName().equalsIgnoreCase("未分组")) {
				unGroup = contactsGroups.remove(i);
				break;
			}

		}
		if (unGroup != null) {
			contactsGroups.add(unGroup);
		}

		for (int i = 0; i < size; i++) {
			if (contactsGroups.get(i).getName().equalsIgnoreCase("常用号码直通车")) {
				unGroup2 = contactsGroups.remove(i);
				break;
			}
		}

		if (unGroup2 != null) {
			contactsGroups.add(unGroup2);
		}

	}

}
