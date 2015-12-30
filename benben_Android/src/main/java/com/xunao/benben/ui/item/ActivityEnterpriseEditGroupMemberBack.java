package com.xunao.benben.ui.item;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.BxContactsList;
import com.xunao.benben.bean.ContactsGroup;
import com.xunao.benben.bean.ContactsObject;
import com.xunao.benben.bean.EnterpriseGroup;
import com.xunao.benben.bean.EnterpriseMemberDetail;
import com.xunao.benben.bean.EnterpriseMemberList;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.MyTextView;

@SuppressLint("ResourceAsColor")
public class ActivityEnterpriseEditGroupMemberBack extends BaseActivity implements
		OnClickListener {

	private ImageView com_title_bar_left_bt;

	private MyTextView com_title_bar_left_tv;

	private ImageView com_title_bar_right_bt;

	private MyTextView com_title_bar_right_tv;

	private TextView tab_left;
	private TextView tab_right;

	private ListView all_listview;
	private ListView group_listview;

	// 选中的组
	private ContactsGroup contactsGroup;
	private ContactsObject contactsObject;

	private ArrayList<EnterpriseMemberDetail> groupListContacts = new ArrayList<EnterpriseMemberDetail>();
	private ArrayList<EnterpriseMemberDetail> enMemberDetails = new ArrayList<EnterpriseMemberDetail>();
	private EnterpriseGroup enterpriseGroup;
	private String enterpriseId;

	private EnterpriseMemberList enMemberList;

	private BxContactsList bxContactsList;
	private CheckBox all_checkbox;
	private TextView tv_all;
	// 未分组的id
	private String noGroupId = "";

	// 两个适配器
	private MyAdapter allAdapter;
	private MyAdapter groupAdapter;

	// 新建一个map来保存移动之前的groupid 防止别的分组移进去再移出去变成未分组
	private HashMap<String, String> beforMap = new HashMap<String, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_enterorise_member);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		tab_left = (TextView) findViewById(R.id.tab_left);
		tab_right = (TextView) findViewById(R.id.tab_right);

		all_listview = (ListView) findViewById(R.id.all_listview);
		group_listview = (ListView) findViewById(R.id.group_listview);
		all_Box = findViewById(R.id.all_Box);

		all_checkbox = (CheckBox) findViewById(R.id.all_checkbox);
		tv_all = (TextView) findViewById(R.id.tv_all);

		all_checkbox.setOnCheckedChangeListener(changeListener);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {

		enMemberDetails = (ArrayList<EnterpriseMemberDetail>) getIntent()
				.getSerializableExtra("member");
		enterpriseGroup = (EnterpriseGroup) getIntent().getSerializableExtra(
				"group");
		enterpriseId = getIntent().getStringExtra("enterpriseId");

		tab_right.setText("已选择(" + groupListContacts.size() + ")");

		allAdapter = new MyAdapter(true, enMemberDetails);
		groupAdapter = new MyAdapter(false, groupListContacts);

		all_listview.setAdapter(allAdapter);
		group_listview.setAdapter(groupAdapter);

		initData();
	}

	private void initData() {
		if (CommonUtils.isNetworkAvailable(mContext)) {
			InteNetUtils.getInstance(mContext).getEnterpriseGroupMember(
					enterpriseId, enterpriseGroup.getId(),
					new RequestCallBack<String>() {
						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							String result = arg0.result;
							JSONObject jsonObject;
							try {
								jsonObject = new JSONObject(result);
								enMemberList = new EnterpriseMemberList();
								enMemberList = enMemberList
										.parseJSON2(jsonObject);

								if (enMemberList.getEnterpriseMemberDetails() != null) {
									groupListContacts.clear();
									groupListContacts.addAll(enMemberList
											.getEnterpriseMemberDetails());
									if (groupListContacts.size() > 0) {
										for (EnterpriseMemberDetail detail : enMemberDetails) {
											for (EnterpriseMemberDetail memberDetail : groupListContacts) {
												if (!TextUtils
														.isEmpty(memberDetail
																.getPhone())) {
													if (memberDetail.getId()
															.equals(detail
																	.getId())) {
														detail.setChecked(true);
													}
												}
											}
										}
										tab_right.setText("已选择("
												+ groupListContacts.size()
												+ ")");
									}
								}

							} catch (Exception e) {
								e.printStackTrace();
							}

							allAdapter.notifyDataSetChanged();
							groupAdapter.notifyDataSetChanged();
						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							ToastUtils.Errortoast(mContext, "网络不可用!");
						}
					});
		}
	}

	OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean checked) {
			if (checked) {
				// tv_all.setText("取消");
				for (EnterpriseMemberDetail memberDetail : enMemberDetails) {
					if (!groupListContacts.contains(memberDetail)) {
						groupListContacts.add(memberDetail);
						memberDetail.setChecked(true);
					}
				}

				tab_right.setText("已选择(" + groupListContacts.size() + ")");
			} else {
				tv_all.setText("全选");
				for (EnterpriseMemberDetail memberDetail : enMemberDetails) {
					if (groupListContacts.contains(memberDetail)) {
						memberDetail.setChecked(false);
					}
				}
				groupListContacts.removeAll(enMemberDetails);
				tab_right.setText("已选择(" + groupListContacts.size() + ")");
			}
			allAdapter.notifyDataSetChanged();
			groupAdapter.notifyDataSetChanged();
		}
	};

	private View all_Box;

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

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		if (jsonObject.optString("ret_num").equals("0")) {
			ToastUtils.Infotoast(mContext, "编辑成员成功!");
			sendBroadcast(new Intent(AndroidConfig.refrashGroupMember));
			sendBroadcast(new Intent(AndroidConfig.refreshEnterpriseMember));
			finish();
		} else {
			ToastUtils.Infotoast(mContext, jsonObject.optString("ret_msg"));
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Infotoast(mContext, "网络不可用!");
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
			if (groupListContacts.size() > 0) {
				String userId = "";
				for (EnterpriseMemberDetail detail : groupListContacts) {
					userId += detail.getId() + ",";
				}
				if (CommonUtils.isNetworkAvailable(mContext)) {
					InteNetUtils.getInstance(mContext)
							.editEnterpriseGroupMember(enterpriseId,
									enterpriseGroup.getId(),
									userId.substring(0, userId.length() - 1),
									mRequestCallBack);
				}
			} else {
				ToastUtils.Infotoast(mContext, "请选择要邀请的好友!");
			}
			break;
		default:
			break;
		}

	}

	class MyAdapter extends BaseAdapter {
		private boolean isAll;
		private ArrayList<EnterpriseMemberDetail> adapterContacts;

		public MyAdapter(boolean isAll,
				ArrayList<EnterpriseMemberDetail> adapterContacts) {
			// TODO Auto-generated constructor stub
			this.isAll = isAll;
			this.adapterContacts = adapterContacts;
		}

		@Override
		public int getCount() {
			return adapterContacts.size();
		}

		@Override
		public Object getItem(int arg0) {
			return adapterContacts.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {

			final EnterpriseMemberDetail memberDetail = adapterContacts
					.get(position);
			final ItemHolder itemHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.activity_packet_info_item, null);
				itemHolder = new ItemHolder();
				itemHolder.item_pinyin = (TextView) convertView
						.findViewById(R.id.item_pinyin);
				itemHolder.item_phone_checkbox = (CheckBox) convertView
						.findViewById(R.id.item_phone_checkbox);
				itemHolder.item_phone_poster = (ImageView) convertView
						.findViewById(R.id.item_phone_poster);
				itemHolder.item_phone_name = (TextView) convertView
						.findViewById(R.id.item_phone_name);
				itemHolder.item_phone_phone = (TextView) convertView
						.findViewById(R.id.item_phone_phone);
				convertView.setTag(itemHolder);
			} else {
				itemHolder = (ItemHolder) convertView.getTag();
			}

			if (isAll) {
				itemHolder.item_phone_checkbox.setVisibility(View.VISIBLE);
				// checkbox的初始化

				itemHolder.item_phone_checkbox.setChecked(memberDetail
						.isChecked());

				// item的点击事件
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (itemHolder.item_phone_checkbox.isChecked()) {
							itemHolder.item_phone_checkbox.setChecked(false);

							if (groupListContacts.contains(memberDetail)) {
								groupListContacts.remove(memberDetail);
							}
							tv_all.setText("全选");
							all_checkbox.setOnCheckedChangeListener(null);
							all_checkbox.setChecked(false);
							memberDetail.setChecked(false);
							all_checkbox
									.setOnCheckedChangeListener(changeListener);
						} else {
							itemHolder.item_phone_checkbox.setChecked(true);
							memberDetail.setChecked(true);
							if (!groupListContacts.contains(memberDetail)) {
								groupListContacts.add(memberDetail);
							}

							if (groupListContacts.size() >= enMemberDetails
									.size()) {
								// tv_all.setText("取消");
								all_checkbox.setOnCheckedChangeListener(null);
								all_checkbox.setChecked(true);
								all_checkbox
										.setOnCheckedChangeListener(changeListener);
							}

						}
						groupAdapter.notifyDataSetChanged();
						tab_right.setText("已选择(" + groupListContacts.size()
								+ ")");
					}
				});

			}

			itemHolder.item_phone_name.setText(memberDetail.getName());
			itemHolder.item_phone_phone.setText(memberDetail.getPhone());
			String pinyin = CommonUtils.hanYuToPinyin(memberDetail.getName())
					.substring(0, 1);
			itemHolder.item_pinyin.setText(pinyin);

			if (position == 0) {
				itemHolder.item_pinyin.setVisibility(View.VISIBLE);
			} else {
				String pinyin2 = CommonUtils.hanYuToPinyin(
						adapterContacts.get(position - 1).getName()).substring(
						0, 1);
				if (pinyin.equals(pinyin2)) {
					itemHolder.item_pinyin.setVisibility(View.GONE);
				} else {
					itemHolder.item_pinyin.setVisibility(View.VISIBLE);
				}
			}

			return convertView;
		}
	}

	class ItemHolder {
		TextView item_pinyin;
		CheckBox item_phone_checkbox;
		ImageView item_phone_poster;
		TextView item_phone_name;
		TextView item_phone_phone;
	}
}
