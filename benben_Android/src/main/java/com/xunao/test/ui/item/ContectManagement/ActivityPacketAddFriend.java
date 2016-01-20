package com.xunao.test.ui.item.ContectManagement;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.test.R;
import com.xunao.test.base.BaseActivity;
import com.xunao.test.bean.Contacts;
import com.xunao.test.bean.ContactsGroup;
import com.xunao.test.bean.ContactsObject;
import com.xunao.test.bean.PhoneInfo;
import com.xunao.test.bean.SuccessMsg;
import com.xunao.test.config.AndroidConfig;
import com.xunao.test.exception.NetRequestException;
import com.xunao.test.net.InteNetUtils;
import com.xunao.test.utils.CommonUtils;
import com.xunao.test.utils.ToastUtils;
import com.xunao.test.utils.ViewHolderUtil;
import com.xunao.test.view.MyTextView;

public class ActivityPacketAddFriend extends BaseActivity implements
		OnClickListener {

	private FloatingGroupExpandableListView unlistView;
	private View selectBox;
	private View unSelectBox;
	private MyTextView tab_right;
	private MyTextView tab_left;
	private ContactsGroup curContactsGroup;
	private ArrayList<ContactsGroup> mContactsGroups;
	private WrapperExpandableListAdapter wrapperAdapter;

	private ArrayList<Contacts> selectContacts;
	private ArrayList<Contacts> curAllContactsBack;
	private CheckBox all_checkbox;
	private TextView tv_all;
	private boolean isAllCheck;
	private SelectAdapter selectAdapter;
	private ListView selectListview;
	private int noGroupId;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_packet_addfriend);
		initdefaultImage(R.drawable.default_face);
	}

	@Override
	public void initView(Bundle savedInstanceState) {

		initTitleView();

		tab_left = (MyTextView) findViewById(R.id.tab_left);
		tab_right = (MyTextView) findViewById(R.id.tab_right);
		unSelectBox = findViewById(R.id.unSelectBox);
		selectBox = findViewById(R.id.SelectBox);
		all_checkbox = (CheckBox) findViewById(R.id.all_checkbox);
		tv_all = (TextView) findViewById(R.id.tv_all);

		unlistView = (FloatingGroupExpandableListView) findViewById(R.id.unlistView);
		unlistView.setGroupIndicator(null);

		selectListview = (ListView) findViewById(R.id.selectListview);
	}

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

	@Override
	public void initDate(Bundle savedInstanceState) {
		selectContacts = new ArrayList<Contacts>();
		curAllContactsBack = new ArrayList<Contacts>();
		Intent intent = getIntent();
		curContactsGroup = (ContactsGroup) intent
				.getSerializableExtra("contactsGroup");
		if (curContactsGroup.getmContacts() != null)
			curAllContactsBack.addAll(curContactsGroup.getmContacts());

		TitleMode mode = new TitleMode("#068cd9", "完成", 0,
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (selectContacts != null) {
							String user_id = "";
							selectContacts.addAll(curContactsGroup
									.getmContacts());
							for (Contacts cs : selectContacts) {
								user_id += cs.getId() + ",";
							}
							if (user_id.length() > 0) {
								user_id = user_id.substring(0,
										user_id.length() - 1);
							}
							if (CommonUtils.isNetworkAvailable(mContext)) {
								InteNetUtils.getInstance(mContext)
										.EditPacketInfo(
												curContactsGroup.getId() + "",
												user_id, mRequestCallBack);
							}
						}

					}
				}, "", R.drawable.ic_back, new OnClickListener() {

					@Override
					public void onClick(View v) {

						mContext.AnimFinsh();
					}
				}, curContactsGroup.getName(), 0);
		chanageTitle(mode);
		initlocakData();
	}

	// 读取本地数据库数据
	public void initlocakData() {
		showLoding("请稍后...");
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					List<ContactsGroup> findAll2 = dbUtil.findAll(Selector
							.from(ContactsGroup.class).where("id", "!=", 10000)
							.and("id", "!=", curContactsGroup.getId()));

					mContactsGroups = (ArrayList<ContactsGroup>) findAll2;
					ArrayList<Contacts> getmContacts = null;
					ArrayList<Contacts> com = new ArrayList<Contacts>();
					ArrayList<Contacts> benben = new ArrayList<Contacts>();
					for (ContactsGroup cg : mContactsGroups) {

						for (ContactsGroup contactsGroup : mContactsGroups) {
							if (("未分组").equals(contactsGroup.getName())) {
								noGroupId = contactsGroup.getId();
							}
						}

						getmContacts = cg.getmContacts();
						getmContacts.clear();
						List<Contacts> findAll = dbUtil.findAll(Selector
								.from(Contacts.class)
								.where("group_id", "=", cg.getId())
								.orderBy("pinyin", false));
						if (findAll != null) {
							getmContacts.addAll(findAll);
							com.clear();
							benben.clear();

							for (Contacts contacts : getmContacts) {

								List<PhoneInfo> findAll3 = dbUtil
										.findAll(Selector.from(PhoneInfo.class)
												.where("contacts_id", "=",
														contacts.getId()));

								contacts.setPhones((ArrayList<PhoneInfo>) findAll3);

								if (!contacts.getIs_benben().equals("0")) {
									benben.add(contacts);
								} else {
									com.add(contacts);
								}
							}
						}
						getmContacts.clear();
						getmContacts.addAll(benben);
						getmContacts.addAll(com);
						cg.setProportion(benben.size() + "/"
								+ getmContacts.size());
					}
					ContactsObject contactsObject = new ContactsObject();
					contactsObject.setmContactsGroups(mContactsGroups);

					groupOrderBy();

					mContext.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mContext.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									mContext.dissLoding();
								}
							});
							if (wrapperAdapter == null) {
								myAdapter adapter = new myAdapter();
								wrapperAdapter = new WrapperExpandableListAdapter(
										adapter);
								unlistView.setAdapter(wrapperAdapter);
							} else {
								wrapperAdapter.notifyDataSetChanged();
							}

							selectAdapter = new SelectAdapter();
							selectListview.setAdapter(selectAdapter);
						}
					});

				} catch (NumberFormatException e) {
					e.printStackTrace();
					mContext.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mContext.dissLoding();
						}
					});
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					mContext.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mContext.dissLoding();
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					mContext.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mContext.dissLoding();
						}
					});
				}
			}
		}).start();
	}

	private void groupOrderBy() {
		int size = mContactsGroups.size();
		// 对组群排序,未分组放在后面
		ContactsGroup unGroup = null;
		ContactsGroup unGroup2 = null;
		for (int i = 0; i < size; i++) {
			if (mContactsGroups.get(i).getName().equalsIgnoreCase("未分组")) {
				unGroup = mContactsGroups.remove(i);
				break;
			}

		}
		if (unGroup != null) {
			mContactsGroups.add(unGroup);
		}

	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		tab_left.setOnClickListener(this);
		tab_right.setOnClickListener(this);
		tab_left.performClick();

		all_checkbox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isAllCheck) {
					isAllCheck = false;
					tv_all.setText("全选");
				} else {
					isAllCheck = true;
				}
				for (ContactsGroup c : mContactsGroups) {
					if (c.getmContacts() != null && c.getmContacts().size() > 0) {
						c.setSelect(isAllCheck);
						for (Contacts con : c.getmContacts()) {
							if (isAllCheck) {
								if (!con.isChecked())
									selectContacts.add(con);
							} else {
								selectContacts.remove(con);
							}
							con.setChecked(isAllCheck);
						}
					}
				}
				tab_right.setText("已选择(" + selectContacts.size() + ")");
				wrapperAdapter.notifyDataSetChanged();
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
		SuccessMsg msg = new SuccessMsg();
		try {
			msg.parseJSON(jsonObject);
			for (Contacts c : selectContacts) {
				curAllContactsBack.remove(c);
				c.setGroup_id(curContactsGroup.getId() + "");
			}
			for (Contacts c : curAllContactsBack) {
				c.setGroup_id(noGroupId + "");
			}

			// 存储本地数据
			dbUtil.saveOrUpdateAll(selectContacts);
			dbUtil.saveOrUpdateAll(curAllContactsBack);
			// nContacts.removeAll(groupListContacts);
			// nContacts.addAll(groupListContacts);
			sendBroadcast(new Intent(AndroidConfig.ContactsRefresh));
			setResult(AndroidConfig.PacketManagementResultCodeInfo);
			AnimFinsh();

		} catch (NetRequestException e) {
			// TODO Auto-generated catch block
			e.getError().print(mContext);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Errortoast(mContext, "当前网络不可用");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tab_left:
			tab_left.setBackgroundResource(R.drawable.bac_bule_left);
			tab_left.setTextColor(Color.parseColor("#ffffff"));

			tab_right.setBackgroundResource(R.drawable.bac_white_right);
			tab_right.setTextColor(R.color.top_bar_color);

			unSelectBox.setVisibility(View.VISIBLE);
			selectBox.setVisibility(View.GONE);
			break;
		case R.id.tab_right:

			tab_left.setBackgroundResource(R.drawable.bac_white_left);
			tab_left.setTextColor(R.color.top_bar_color);

			tab_right.setBackgroundResource(R.drawable.bac_bule_right);
			tab_right.setTextColor(Color.parseColor("#ffffff"));

			unSelectBox.setVisibility(View.GONE);
			selectBox.setVisibility(View.VISIBLE);
			getNewContacts(selectContacts);
			selectAdapter.notifyDataSetChanged();

			break;
		}

	}

	class myAdapter extends BaseExpandableListAdapter {

		@Override
		public Contacts getChild(int arg0, int arg1) {
			return mContactsGroups.get(arg0).getmContacts().get(arg1);
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
						R.layout.item_addfriend_item, null);
			}

			final CheckBox checkBox = (CheckBox) ViewHolderUtil.get(
					convertView, R.id.item_all);
			MyTextView chief_name = (MyTextView) ViewHolderUtil.get(
					convertView, R.id.item_name);
			RoundedImageView item_poster = (RoundedImageView) ViewHolderUtil
					.get(convertView, R.id.item_poster);

			checkBox.setChecked(contact.isChecked());
			checkBox.setVisibility(View.VISIBLE);

			CommonUtils.startImageLoader(cubeimageLoader, contact.getPoster(),
					item_poster);

			ArrayList<PhoneInfo> phones = contact.getPhones();

			if (phones != null) {
				StringBuffer phone = new StringBuffer();
				for (PhoneInfo p : phones) {
					phone.append(p.getPhone() + ",");
				}
				if (phone.length() > 0) {
					chief_name.setText(contact.getName() + " "
							+ phone.substring(0, phone.length() - 1));
				} else {
					chief_name.setText(contact.getName());
				}
			}

			OnClickListener click = new OnClickListener() {
				@Override
				public void onClick(View arg0) {

					ContactsGroup group = getGroup(groupPosition);

					checkBox.setChecked(!contact.isChecked());
					contact.setChecked(!contact.isChecked());

					if (!contact.isChecked()) {
						group.setSelect(false);
						selectContacts.remove(contact);
						setIsAllCheck(false);
						tab_right.setText("已选择(" + selectContacts.size() + ")");
						myAdapter.this.notifyDataSetChanged();
					} else {
						selectContacts.add(contact);
						boolean checkAllSelect = true;

						for (Contacts c : group.getmContacts()) {
							if (!c.isChecked()) {
								checkAllSelect = false;
								break;
							}
						}
						group.setSelect(checkAllSelect);
						group.setSelect(checkAllSelect);
						myAdapter.this.notifyDataSetChanged();
						tab_right.setText("已选择(" + selectContacts.size() + ")");
						if (checkAllSelect) {
							boolean allSelect = true;
							for (ContactsGroup c : mContactsGroups) {
								if (!c.isSelect()&&c.getmContacts().size()>0) {
									allSelect = false;
									break;
								}
							}
							setIsAllCheck(allSelect);
						}

					}

				}
			};

			convertView.setOnClickListener(click);
			checkBox.setOnClickListener(click);

			return convertView;
		}

		@Override
		public int getChildrenCount(int arg0) {
			return mContactsGroups.get(arg0).getmContacts().size();
		}

		@Override
		public ContactsGroup getGroup(int arg0) {
			return mContactsGroups.get(arg0);
		}

		@Override
		public int getGroupCount() {
			return mContactsGroups.size();
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
			final ContactsGroup group = getGroup(groupPosition);
			holder.item_all.setVisibility(View.VISIBLE);
			holder.item_all.setChecked(group.isSelect());

			if (unlistView.isGroupExpanded(groupPosition)) {
				holder.status_img
						.setImageResource(R.drawable.icon_contacts_single_down);
			} else {
				holder.status_img
						.setImageResource(R.drawable.icon_contacts_single_right);
			}

			holder.item_group_name.setText(getGroup(groupPosition).getName()
					+ "  (" + group.getmContacts().size() + ")");

			if(group.getmContacts().size()>0){
				holder.item_all.setClickable(true);
				holder.item_all.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {

						if (group.isSelect()) {
							group.setSelect(false);
							setIsAllCheck(false);
							for (Contacts c : group.getmContacts()) {
								c.setChecked(false);
								selectContacts.remove(c);
							}
							tab_right.setText("已选择(" + selectContacts.size() + ")");
							myAdapter.this.notifyDataSetChanged();
						} else {
							group.setSelect(true);
							holder.item_all.setChecked(true);
							for (Contacts c : group.getmContacts()) {
								if (!c.isChecked()) {
									c.setChecked(true);
									selectContacts.add(c);
								}
							}
							tab_right.setText("已选择(" + selectContacts.size() + ")");
							myAdapter.this.notifyDataSetChanged();
							boolean allSelect = true;
							for (ContactsGroup c : mContactsGroups) {
								if (!c.isSelect()&&c.getmContacts().size()>0) {
									allSelect = false;
									break;
								}
							}
							setIsAllCheck(allSelect);
						}

					}
				});

			}else{
				holder.item_all.setClickable(false);
			}
			
		
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (unlistView.isGroupExpanded(groupPosition)) {
						unlistView.collapseGroup(groupPosition);
					} else {
						unlistView.expandGroup(groupPosition);
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
		TextView item_group_name;
		ImageView status_img;
		CheckBox item_all;
	}

	class SelectAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return selectContacts.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return selectContacts.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {

			final Contacts contacts = selectContacts.get(position);
			final ItemHolder itemHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.activity_packet_info_item, null);
				itemHolder = new ItemHolder();
				itemHolder.item_pinyin = (TextView) convertView
						.findViewById(R.id.item_pinyin);
				itemHolder.item_phone_checkbox = (CheckBox) convertView
						.findViewById(R.id.item_phone_checkbox);
				itemHolder.item_phone_name = (TextView) convertView
						.findViewById(R.id.item_phone_name);
				itemHolder.item_phone_phone = (TextView) convertView
						.findViewById(R.id.item_phone_phone);
				convertView.setTag(itemHolder);
			} else {
				itemHolder = (ItemHolder) convertView.getTag();
			}

			String poster = contacts.getPoster();

			itemHolder.item_phone_name.setText(contacts.getName());
			ArrayList<PhoneInfo> phones = contacts.getPhones();
			if (phones != null) {
				StringBuffer phone = new StringBuffer();
				for (PhoneInfo p : phones) {
					phone.append(p.getPhone() + ",");
				}
				itemHolder.item_phone_phone.setText(phone.substring(0,
						phone.length() - 1));
			}

			itemHolder.item_pinyin.setText(contacts.getPinyin().charAt(0) + "");
			if (contacts.isHasPinYin()) {
				itemHolder.item_pinyin.setVisibility(View.VISIBLE);
			} else {
				itemHolder.item_pinyin.setVisibility(View.GONE);
			}

			return convertView;
		}
	}

	/**
	 * 序列化 找出位置
	 */
	public void getNewContacts(ArrayList<Contacts> contacts) {
		// 根据拼音序列化
		// Collections.sort(contacts);

		// 找出有拼音出现的位置
		if (contacts != null && contacts.size() > 0) {
			// 这样写是为了第一个一定会出现
			int pinyin = -1;
			// int pinyin = contacts.get(0).getPinyin().charAt(0);
			for (int i = 0; i < contacts.size(); i++) {
				int j = contacts.get(i).getPinyin().charAt(0);
				contacts.get(i).setHasPinYin(false);
				if (j != pinyin) {
					pinyin = j;
					contacts.get(i).setHasPinYin(true);
				}
			}
		}
	}

	class ItemHolder {
		public TextView item_phone_phone;
		TextView item_pinyin;
		CheckBox item_phone_checkbox;
		TextView item_phone_name;
	}

}
