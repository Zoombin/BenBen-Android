package com.xunao.benben.ui.item.ContectManagement;

import in.srain.cube.image.CubeImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.ContactsGroup;
import com.xunao.benben.bean.ContactsObject;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.view.MyTextView;

@SuppressLint("ResourceAsColor")
public class ActivityPacketInfoManagement extends BaseActivity implements
		OnClickListener {

	private ImageView com_title_bar_left_bt;

	private MyTextView com_title_bar_left_tv;

	private ImageView com_title_bar_right_bt;

	private MyTextView com_title_bar_right_tv;
	private CheckBox all_checkbox;
	private TextView tv_all;

	private TextView tab_left;
	private TextView tab_right;

	private ListView all_listview;
	private ListView group_listview;

	// 选中的组
	private ContactsGroup contactsGroup;

	private ArrayList<Contacts> curAllContacts;
	private ArrayList<Contacts> curAllContactsBack = new ArrayList<Contacts>();// 备份

	// 未分组的id
	private String noGroupId = "";

	// 两个适配器
	private MyAdapter allAdapter;
	private MyAdapter groupAdapter;

	// 新建一个map来保存移动之前的groupid 防止别的分组移进去再移出去变成未分组
	private HashMap<String, String> beforMap = new HashMap<String, String>();
	/**
	 * 所有的联系人
	 */
	private ArrayList<Contacts> allContacts = new ArrayList<Contacts>();
	private boolean isAllChcked = false;

	private View all_Box;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_packet_info_management);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		tab_left = (TextView) findViewById(R.id.tab_left);
		tab_right = (TextView) findViewById(R.id.tab_right);

		all_Box = findViewById(R.id.all_Box);
		all_listview = (ListView) findViewById(R.id.all_listview);
		group_listview = (ListView) findViewById(R.id.group_listview);

		all_checkbox = (CheckBox) findViewById(R.id.all_checkbox);
		tv_all = (TextView) findViewById(R.id.tv_all);
		all_checkbox.setChecked(false);

	};

	@Override
	public void initDate(Bundle savedInstanceState) {
		// 初始化数据
		contactsGroup = mApplication.contactsGroup;
		curAllContacts = contactsGroup.getmContacts();
		if (contactsGroup.getmContacts() != null) {
			curAllContactsBack.addAll(contactsGroup.getmContacts());
		}

		// 获取所有
		if (mApplication.contactsObjectManagement.getmContactss() != null) {
			allContacts.addAll(mApplication.contactsObjectManagement
					.getmContactss());
		}

		all_checkbox.setChecked(curAllContacts != null
				&& curAllContacts.size() >= allContacts.size());
		all_checkbox.setOnCheckedChangeListener(changeListener);

		for (Contacts c : allContacts) {
			if (c.getGroup_id().equals(contactsGroup.getId())) {
				c.setChecked(true);
			} else {
				c.setChecked(false);
			}
		}

		getNewContacts(curAllContacts);
		getNewContacts(allContacts);

		// 获取未分组的groupid
		ArrayList<ContactsGroup> mContactsGroups = mApplication.contactsObjectManagement
				.getmContactsGroups();
		for (ContactsGroup contactsGroup : mContactsGroups) {
			if (("未分组").equals(contactsGroup.getName())) {
				noGroupId = contactsGroup.getId() + "";
			}
		}

		// 配置适配器
		allAdapter = new MyAdapter(true, allContacts);
		if (curAllContacts != null) {
			groupAdapter = new MyAdapter(false, curAllContacts);
			group_listview.setAdapter(groupAdapter);
		}

		all_listview.setAdapter(allAdapter);

		tab_right.setText("已选择("
				+ (curAllContacts != null ? curAllContacts.size() : "0") + ")");
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		tab_left.setOnClickListener(this);
		tab_right.setOnClickListener(this);

		com_title_bar_left_bt = (ImageView) findViewById(R.id.com_title_bar_left_bt);
		com_title_bar_left_tv = (MyTextView) findViewById(R.id.com_title_bar_left_tv);
		com_title_bar_right_bt = (ImageView) findViewById(R.id.com_title_bar_right_bt);
		com_title_bar_right_tv = (MyTextView) findViewById(R.id.com_title_bar_right_tv);

		com_title_bar_left_tv.setOnClickListener(this);
		com_title_bar_left_bt.setOnClickListener(this);
		com_title_bar_right_tv.setOnClickListener(this);
		com_title_bar_right_bt.setOnClickListener(this);
		tab_left.performClick();
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
			for (Contacts c : curAllContacts) {
				curAllContactsBack.remove(c);
				c.setGroup_id(contactsGroup.getId() + "");
			}
			for (Contacts c : curAllContactsBack) {
				c.setGroup_id(noGroupId);
			}

			// 存储本地数据
			dbUtil.saveOrUpdateAll(curAllContacts);
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
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tab_left:
			tab_left.setBackgroundResource(R.drawable.bac_white_left);
			tab_left.setTextColor(R.color.top_bar_color);

			tab_right.setBackgroundResource(R.drawable.bac_bule_right);
			tab_right.setTextColor(Color.parseColor("#ffffff"));

			all_Box.setVisibility(View.VISIBLE);
			group_listview.setVisibility(View.GONE);
			break;
		case R.id.tab_right:
			tab_left.setBackgroundResource(R.drawable.bac_bule_left);
			tab_left.setTextColor(Color.parseColor("#ffffff"));

			tab_right.setBackgroundResource(R.drawable.bac_white_right);
			tab_right.setTextColor(R.color.top_bar_color);

			all_Box.setVisibility(View.GONE);
			group_listview.setVisibility(View.VISIBLE);
			break;

		// 头部左侧点击
		case R.id.com_title_bar_left_bt:
		case R.id.com_title_bar_left_tv:
			AnimFinsh();
			break;
		// 右侧 完成
		case R.id.com_title_bar_right_bt:
		case R.id.com_title_bar_right_tv:

			if (curAllContacts != null) {
				String user_id = "";
				for (Contacts cs : curAllContacts) {
					user_id += cs.getId() + ",";
				}
				if (user_id.length() > 0) {
					user_id = user_id.substring(0, user_id.length() - 1);
				}
				if (CommonUtils.isNetworkAvailable(mContext)) {
					InteNetUtils.getInstance(mContext).EditPacketInfo(
							contactsGroup.getId() + "", user_id,
							mRequestCallBack);
				}
			}
			break;
		default:
			break;
		}

	}

	class MyAdapter extends BaseAdapter {
		private boolean isAll;
		private ArrayList<Contacts> adapterContacts;

		public MyAdapter(boolean isAll, ArrayList<Contacts> adapterContacts) {
			// TODO Auto-generated constructor stub
			this.isAll = isAll;
			this.adapterContacts = adapterContacts;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return adapterContacts.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return adapterContacts.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {

			final Contacts contacts = adapterContacts.get(position);
			final ItemHolder itemHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.activity_packet_info_item, null);
				itemHolder = new ItemHolder();
				itemHolder.item_pinyin = (TextView) convertView
						.findViewById(R.id.item_pinyin);
				itemHolder.item_phone_checkbox = (CheckBox) convertView
						.findViewById(R.id.item_phone_checkbox);
				itemHolder.item_phone_poster = (CubeImageView) convertView
						.findViewById(R.id.item_phone_poster);
				itemHolder.item_phone_name = (TextView) convertView
						.findViewById(R.id.item_phone_name);
				convertView.setTag(itemHolder);
			} else {
				itemHolder = (ItemHolder) convertView.getTag();
			}

			String poster = contacts.getPoster();
			CommonUtils.startImageLoader(cubeimageLoader, poster,
					itemHolder.item_phone_poster);

			if (isAll) {
				itemHolder.item_phone_checkbox.setVisibility(View.VISIBLE);
				// // checkbox的初始化
				// if (contactsGroup.getId().equals(contacts.getGroup_id())) {
				// contacts.setChecked(true);
				// itemHolder.item_phone_checkbox.setChecked(true);
				// } else {
				// contacts.setChecked(false);
				// itemHolder.item_phone_checkbox.setChecked(false);
				// }

				itemHolder.item_phone_checkbox.setChecked(contacts.isChecked());

				// item的点击事件
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (itemHolder.item_phone_checkbox.isChecked()) {

							if (beforMap.containsKey(contacts.getId())) {
								// 取消选择 将联系人移动到原来的分组
								contacts.setGroup_id(beforMap.get(contacts
										.getId()));
							}
							// else {
							// // 取消选择 将联系人移动到未分组
							// contacts.setGroup_id(noGroupId);
							// }
							contacts.setChecked(false);
							curAllContacts.remove(contacts);
						} else {
							contacts.setChecked(true);
							beforMap.put(contacts.getId() + "",
									contacts.getGroup_id());
							// // 点击勾选 将联系人移动到当前分组
							// contacts.setGroup_id(contactsGroup.getId());
							curAllContacts.add(contacts);
						}

						all_checkbox.setOnCheckedChangeListener(null);
						all_checkbox.setChecked(curAllContacts.size() >= allContacts
								.size());
						all_checkbox.setOnCheckedChangeListener(changeListener);

						itemHolder.item_phone_checkbox
								.setChecked(!itemHolder.item_phone_checkbox
										.isChecked());
						getNewContacts(curAllContacts);
						if (groupAdapter != null)
							groupAdapter.notifyDataSetChanged();
						tab_right.setText("已选择("
								+ (curAllContacts != null ? curAllContacts
										.size() : "0") + ")");
					}
				});

			}

			itemHolder.item_phone_name.setText(contacts.getName());
			itemHolder.item_pinyin.setText(contacts.getPinyin().charAt(0) + "");
			if (contacts.isHasPinYin()) {
				itemHolder.item_pinyin.setVisibility(View.VISIBLE);
			} else {
				itemHolder.item_pinyin.setVisibility(View.GONE);
			}

			return convertView;
		}
	}

	OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

			if (arg1) {// 全选
				for (Contacts c : allContacts) {
					c.setChecked(true);
					beforMap.put(c.getId() + "", c.getGroup_id());
					if (!curAllContacts.contains(c)) {
						curAllContacts.add(c);
					}
				}
				tab_right
						.setText("已选择("
								+ (curAllContacts != null ? curAllContacts
										.size() : "0") + ")");
				if (allAdapter != null)
					allAdapter.notifyDataSetChanged();
				if (groupAdapter != null)
					groupAdapter.notifyDataSetChanged();

			} else {// 取消全选
				for (Contacts c : allContacts) {
					if (beforMap.containsKey(c.getId())) {
						c.setGroup_id(beforMap.get(c.getId()));
					}
					c.setChecked(false);
					curAllContacts.remove(c);
				}
				tab_right
						.setText("已选择("
								+ (curAllContacts != null ? curAllContacts
										.size() : "0") + ")");
				if (allAdapter != null)
					allAdapter.notifyDataSetChanged();
				if (groupAdapter != null)
					groupAdapter.notifyDataSetChanged();
			}

		}
	};

	class ItemHolder {
		TextView item_pinyin;
		CheckBox item_phone_checkbox;
		CubeImageView item_phone_poster;
		TextView item_phone_name;
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

}
