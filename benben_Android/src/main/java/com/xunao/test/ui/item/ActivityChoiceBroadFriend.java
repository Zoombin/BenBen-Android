package com.xunao.test.ui.item;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.test.R;
import com.xunao.test.base.BaseActivity;
import com.xunao.test.bean.Contacts;
import com.xunao.test.bean.ContactsGroup;
import com.xunao.test.config.AndroidConfig;
import com.xunao.test.utils.CommonUtils;
import com.xunao.test.view.MyTextView;

public class ActivityChoiceBroadFriend extends BaseActivity {
	protected static final int CHOICE_FRIEND_BY_AREA = 10002;
	private FloatingGroupExpandableListView listView;
	private CheckBox all_checkbox;
	private TextView tv_all;
	private Button btn_invite;
	private WrapperExpandableListAdapter wrapperAdapter;
	private myAdapter adapter;
	private LinearLayout no_data;
	private ArrayList<ContactsGroup> contactsGroup = new ArrayList<>();
	private ArrayList<Contacts> groupContacts = new ArrayList<>();
//	private ArrayList<Contacts> contactsList = new ArrayList<Contacts>();
	private ArrayList<Contacts> selectContacts = new ArrayList<Contacts>();
    private LinearLayout ll_select_message;
    private TextView tv_select_num;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_friend_union_invite_member);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("按分组选", "", "",
				R.drawable.icon_com_title_left, R.drawable.icon_by_area);

		listView = (FloatingGroupExpandableListView) findViewById(R.id.listView);
		no_data = (LinearLayout) findViewById(R.id.no_data);
		all_checkbox = (CheckBox) findViewById(R.id.all_checkbox);
		tv_all = (TextView) findViewById(R.id.tv_all);
		btn_invite = (Button) findViewById(R.id.btn_invite);
        tv_select_num = (TextView) findViewById(R.id.tv_select_num);
		tv_list = (TextView) findViewById(R.id.tv_list);
		tv_listnum = (TextView) findViewById(R.id.tv_listnum);
        ll_select_message = (LinearLayout) findViewById(R.id.ll_select_message);
        all_checkbox.setVisibility(View.VISIBLE);
        tv_all.setVisibility(View.VISIBLE);
        ll_select_message.setVisibility(View.VISIBLE);
        tv_select_num.setVisibility(View.GONE);

		listView.setGroupIndicator(null);
		adapter = new myAdapter();
		wrapperAdapter = new WrapperExpandableListAdapter(adapter);
		listView.setAdapter(wrapperAdapter);

		btn_invite.setText("添加");
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AnimFinsh();
			}
		});

		all_checkbox.setOnCheckedChangeListener(changeListener);

		btn_invite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.putExtra("contacts", groupContacts);
				intent.putExtra("typeWhat", "1");
				setResult(1000, intent);
				AnimFinsh();
			}
		});

		setOnRightClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startAnimActivityForResult(
						ActivityChoiceBroadCastFriendByArea.class,
						"selectContacts", groupContacts, CHOICE_FRIEND_BY_AREA);
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

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {

	}

	OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean checked) {
			if (checked) {
				for (ContactsGroup memberGroup : contactsGroup) {
					if (memberGroup.getmContacts().size() > 0) {
						memberGroup.setSelect(true);
						for (Contacts contact : memberGroup.getmContacts()) {
							if (!groupContacts.contains(contact)) {
								contact.setChecked(true);
								groupContacts.add(contact);
							}
						}
					}
				}

			} else {

				for (ContactsGroup memberGroup : contactsGroup) {
					memberGroup.setSelect(false);
					for (Contacts contact : memberGroup.getmContacts()) {
						contact.setChecked(false);
						groupContacts.remove(contact);
					}
				}

				tv_all.setText("全选");
			}
			tv_list.setText(getSelectName());
			tv_listnum.setText(getSelectNum());
			adapter.notifyDataSetChanged();
		}
	};
	private TextView tv_list;
	private TextView tv_listnum;

	@Override
	public void initDate(Bundle savedInstanceState) {
		String typeWhat = getIntent().getStringExtra("typeWhat");

		selectContacts = (ArrayList<Contacts>) getIntent()
				.getSerializableExtra("contactsList");
		initLocalData();
		tv_list.setText(getSelectName());
		tv_listnum.setText(getSelectNum());
		// if (typeWhat != null) {
		// if (typeWhat.equals("2")) {
		// startAnimActivityForResult(
		// ActivityChoiceBroadCastFriendByArea.class, "contacts",
		// selectContacts, CHOICE_FRIEND_BY_AREA);
		// }else{
		// initLocalData();
		// }
		// }else{
		// initLocalData();
		// }

	}

	private void groupOrderBy() {
		int size = contactsGroup.size();
		// 对组群排序,未分组放在后面
		ContactsGroup unGroup = null;
		ContactsGroup unGroup2 = null;
		for (int i = 0; i < size; i++) {
			if (contactsGroup.get(i).getName().equalsIgnoreCase("未分组")) {
				unGroup = contactsGroup.remove(i);
				break;
			}

		}
		if (unGroup != null) {
			contactsGroup.add(unGroup);
		}

		for (int i = 0; i < size; i++) {
			if (contactsGroup.get(i).getName().equalsIgnoreCase("常用号码直通车")) {
				unGroup2 = contactsGroup.remove(i);
				break;
			}
		}

		if (unGroup2 != null) {
			contactsGroup.add(unGroup2);
		}

	}

	private void initLocalData() {
		try {
			List list = dbUtil.findAll(Selector.from(ContactsGroup.class)
					.where(WhereBuilder.b("id", "!=", "10000")));
			contactsGroup = (ArrayList<ContactsGroup>) list;
			groupOrderBy();
			for (ContactsGroup group : contactsGroup) {
				ArrayList<Contacts> arrayList = new ArrayList<Contacts>();
				List cList = dbUtil.findAll(Selector.from(Contacts.class)
						.where(WhereBuilder.b("group_id", "=", group.getId())
								.and("is_benben", "!=", "0")));

				if (cList != null && cList.size() > 0  ) {
					arrayList = (ArrayList<Contacts>) cList;
				}

				if (selectContacts.size() > 0 && selectContacts != null) {
					for (Contacts c : arrayList) {
						for (Contacts contact : selectContacts) {
							if ((c.getIs_benben() != null)
									&& (contact.getIs_benben() != null)) {
								if (c.getIs_benben().equals(
										contact.getIs_benben())) {
									c.setChecked(true);
                                    if (!groupContacts.contains(c)) {
                                        groupContacts.add(c);
                                    }
								}
							}

//							if ((c.getPhone() != null)
//									&& (contact.getPhone() != null)) {
//								if (c.getPhone().equals(contact.getPhone())) {
//									if (!groupContacts.contains(contact)) {
//										groupContacts.add(c);
//										c.setChecked(true);
//
//									}
//								}
//							}
						}
					}
				}

				ArrayList<Contacts> contactsLArrayList = new ArrayList<Contacts>();
				
				
				for (Contacts contacts : arrayList) {
//					List phones = dbUtil.findAll(Selector.from(PhoneInfo.class)
//							.where(WhereBuilder.b("contacts_id", "=",
//									contacts.getId())));
//
//					ArrayList<PhoneInfo> phoneInfos = new ArrayList<PhoneInfo>();
//					if (phones != null) {
//						phoneInfos = (ArrayList<PhoneInfo>) phones;
//						for (PhoneInfo info : phoneInfos) {
//							contacts.setPhone(info.getPhone());
//							contactsLArrayList.add(contacts);
//						}
//					}
					contactsLArrayList.add(contacts);
				}

				group.setmContacts(contactsLArrayList);

			}

			for (int i = 0; i < contactsGroup.size(); i++) {
				int index = 0;
				for (Contacts c : contactsGroup.get(i).getmContacts()) {
					if (c.isChecked()) {
						index += 1;
						if (index == contactsGroup.get(i).getmContacts().size()) {
							contactsGroup.get(i).setSelect(true);
						}
					}
				}
				// listView.expandGroup(i);
			}

		} catch (DbException e) {
			e.printStackTrace();
		}

		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		switch (arg0) {
		case CHOICE_FRIEND_BY_AREA:
			if (arg2 != null) {
				String result = arg2.getStringExtra("result");
				if (result != null) {
					if (result.equals(AndroidConfig.CLOSE)) {
						AnimFinsh();
						return;
					}
				}
				
				ArrayList<Contacts> contactsList = new ArrayList<Contacts>();
				contactsList = (ArrayList<Contacts>) arg2
				 .getSerializableExtra("contacts");
				 
				 if(contactsList != null && contactsList.size() > 0){
					 Intent intent = new Intent();
					 intent.putExtra("contacts", contactsList);
					 intent.putExtra("typeWhat", "2");
					 setResult(1000, intent);
					 AnimFinsh();
				 }else{
					 
					 ArrayList<Contacts> list = (ArrayList<Contacts>) arg2
							 .getSerializableExtra("groupContacts");
					 
					 ArrayList<Contacts> areaArrayList = new ArrayList<Contacts>();
					 
					 if (list != null && list.size() > 0) {
						 for (Contacts contacts : list) {
							 for (Contacts contacts2 : groupContacts) {
								 if (contacts.getName().equals(contacts2.getName())) {
									 if (!areaArrayList.contains(contacts2))
										 areaArrayList.add(contacts2);
								 }
							 }
						 }
					 }
					 
					 groupContacts.clear();
					 groupContacts.addAll(areaArrayList);
					 
					 int selectSize = 0;
					 for (ContactsGroup contactGroup : contactsGroup) {
						 int size = 0;
						 for (Contacts contacts : contactGroup.getmContacts()) {
							 contacts.setChecked(false);
							 for (Contacts contacts2 : list) {
								 if (contacts.getIs_benben().equals(
										 contacts2.getIs_benben())) {
									 contacts.setChecked(true);
									 size += 1;
								 }
							 }
						 }
						 
						 selectSize += size;
						 if (size == contactGroup.getmContacts().size()) {
                             contactGroup.setSelect(true);
						 } else {
                             contactGroup.setSelect(false);
						 }
						 
					 }
					 
					 int allSize = 0;
					 for (ContactsGroup contactGroup : contactsGroup) {
						 allSize += contactGroup.getmContacts().size();
					 }
					 
					 if (selectSize >= allSize) {
						 all_checkbox.setOnCheckedChangeListener(null);
						 all_checkbox.setChecked(true);
						 all_checkbox.setOnCheckedChangeListener(changeListener);
					 } else {
						 all_checkbox.setOnCheckedChangeListener(null);
						 all_checkbox.setChecked(false);
						 all_checkbox.setOnCheckedChangeListener(changeListener);
					 }
					 
					 // if(selectSize == contactsList.size()){
					 // all_checkbox.setChecked(true);
					 // }else{
					 // all_checkbox.setChecked(false);
					 // }
					 
					 adapter.notifyDataSetChanged();
					 
					 tv_list.setText(getSelectName());
					 tv_listnum.setText(getSelectNum());
				 }
				 
				 
				


				// String result = arg2.getStringExtra("result");
				// if (result.equals(AndroidConfig.CLOSE)) {
				// AnimFinsh();
				// } else {
				// Intent intent = new Intent();
				// groupContacts.clear();
				// groupContacts = (ArrayList<Contacts>) arg2
				// .getSerializableExtra("groupContacts");
				// intent.putExtra("contacts", groupContacts);
				// intent.putExtra("typeWhat", "2");
				// setResult(1000, intent);
				// AnimFinsh();
				// }

			}
			break;
		}
		super.onActivityResult(arg0, arg1, arg2);
	}

	class myAdapter extends BaseExpandableListAdapter {

		boolean isFrist = true;

		@Override
		public Contacts getChild(int arg0, int arg1) {
			return contactsGroup.get(arg0).getmContacts().get(arg1);
		}

		@Override
		public long getChildId(int arg0, int arg1) {
			return arg1;
		}

		@Override
		public View getChildView(final int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			final Contacts contact = getChild(groupPosition, childPosition);
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.activity_friend_union_member_content, null);
			}

			final CheckBox checkBox = (CheckBox) convertView
					.findViewById(R.id.item_all);
			RoundedImageView item_phone_poster = (RoundedImageView) convertView
					.findViewById(R.id.chief_poster);
			MyTextView chief_id = (MyTextView) convertView
					.findViewById(R.id.chief_id);
			MyTextView chief_name = (MyTextView) convertView
					.findViewById(R.id.chief_name);

			checkBox.setChecked(contact.isChecked());
			checkBox.setVisibility(View.VISIBLE);

			chief_id.setText("奔犇号：" + contact.getIs_benben());
			chief_name.setText(contact.getName());

			CommonUtils.startImageLoader(cubeimageLoader, contact.getPoster(),
					item_phone_poster);

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (checkBox.isChecked()) {
						checkBox.setChecked(false);
						contact.setChecked(false);

						groupContacts.remove(contact);

						getGroup(groupPosition).setSelect(false);
						adapter.notifyDataSetChanged();

						tv_all.setText("全选");
						all_checkbox.setOnCheckedChangeListener(null);
						all_checkbox.setChecked(false);
						all_checkbox.setOnCheckedChangeListener(changeListener);
					} else {
						checkBox.setChecked(true);
						contact.setChecked(true);
						if (!groupContacts.contains(contact)) {
							groupContacts.add(contact);
						}

						int gsize = 0;
						for (Contacts contacts : getGroup(groupPosition)
								.getmContacts()) {
							if (contacts.isChecked()) {
								gsize += 1;
							}
						}

						if (gsize == getChildrenCount(groupPosition)) {
							getGroup(groupPosition).setSelect(true);
							adapter.notifyDataSetChanged();
						}

						int size = 0;
						for (int i = 0; i < getGroupCount(); i++) {
							size += contactsGroup.get(i).getmContacts().size();
						}

						if (groupContacts.size() >= size) {
							all_checkbox.setOnCheckedChangeListener(null);
							all_checkbox.setChecked(true);
							all_checkbox
									.setOnCheckedChangeListener(changeListener);
						}
					}
					tv_list.setText(getSelectName());
					tv_listnum.setText(getSelectNum());
				}
			});

			return convertView;
		}

		@Override
		public int getChildrenCount(int arg0) {
			return contactsGroup.get(arg0).getmContacts().size();
		}

		@Override
		public ContactsGroup getGroup(int arg0) {
			return contactsGroup.get(arg0);
		}

		@Override
		public int getGroupCount() {
			return contactsGroup.size();
		}

		@Override
		public long getGroupId(int arg0) {
			return arg0;
		}

		@Override
		public View getGroupView(final int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			HeaderViewHolder holder;

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

			holder.item_all.setVisibility(View.VISIBLE);

			if (getGroup(groupPosition).getmContacts().size() <= 0) {
				holder.item_all.setChecked(false);
			} else {
				holder.item_all.setChecked(getGroup(groupPosition).isSelect());
			}

			holder.item_group_name.setText(getGroup(groupPosition).getName()
					+ "(" + getGroup(groupPosition).getmContacts().size()
					+ "人)");

			holder.item_all.setClickable(getGroup(groupPosition).getmContacts()
					.size() > 0);

			holder.item_all.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (getGroup(groupPosition).isSelect()) {
						for (Contacts contacts : getGroup(groupPosition)
								.getmContacts()) {
							contacts.setChecked(false);
							groupContacts.remove(contacts);
						}

						getGroup(groupPosition).setSelect(false);
						adapter.notifyDataSetChanged();

						all_checkbox.setOnCheckedChangeListener(null);
						tv_all.setText("全选");
						all_checkbox.setChecked(false);
						all_checkbox.setOnCheckedChangeListener(changeListener);

					} else {
						for (Contacts contacts : getGroup(groupPosition)
								.getmContacts()) {
							contacts.setChecked(true);
							if (!groupContacts.contains(contacts)) {
								groupContacts.add(contacts);
							}
						}

						getGroup(groupPosition).setSelect(true);
						adapter.notifyDataSetChanged();

						int size = 0;
						for (int i = 0; i < getGroupCount(); i++) {
							size += contactsGroup.get(i).getmContacts().size();
						}

						if (groupContacts.size() >= size) {
							all_checkbox.setOnCheckedChangeListener(null);
							all_checkbox.setChecked(true);
							all_checkbox
									.setOnCheckedChangeListener(changeListener);
						}

					}
					tv_list.setText(getSelectName());
					tv_listnum.setText(getSelectNum());
				}
			});

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

			if (listView.isGroupExpanded(groupPosition)) {
				holder.status_img
						.setImageResource(R.drawable.icon_contacts_single_down);
			} else {
				holder.status_img
						.setImageResource(R.drawable.icon_contacts_single_right);
			}

			// if (isFrist && groupPosition == getGroupCount() - 1) {
			// isFrist = false;
			// for (int i = 0; i < getGroupCount(); i++) {
			// listView.collapseGroup(i);
			// }
			// this.notifyDataSetChanged();
			// }

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
		TextView item_group_name;
		ImageView status_img;
		CheckBox item_all;
	}

	private String getSelectName() {
		if (groupContacts != null) {
			StringBuffer buffer = new StringBuffer();
			for (Contacts c : groupContacts) {
				buffer.append(c.getName() + ",");
			}
			if (buffer.length() > 0) {
				return buffer.substring(0, buffer.length() - 1).toString();

			}
		}
		return "";
	}

	private String getSelectNum() {
		if (groupContacts != null && groupContacts.size() > 0) {
			return "等" + groupContacts.size() + "人";
		}
		return "";
	}
}
