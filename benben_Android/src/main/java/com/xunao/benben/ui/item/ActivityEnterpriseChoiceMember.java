package com.xunao.benben.ui.item;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.EnterpriseMemberDetail;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

public class ActivityEnterpriseChoiceMember extends BaseActivity {
	private ListView listview;
	private myAdapter adapter;
	private int maxShow;
	private String enterpriseId;
	private LinearLayout ll_search_item;
	private ArrayList<EnterpriseMemberDetail> memberDetails = new ArrayList<EnterpriseMemberDetail>();
	private ArrayList<EnterpriseMemberDetail> memberDetails2 = new ArrayList<EnterpriseMemberDetail>();

	private Button btn_invite;
	private TextView add_common;
	private RelativeLayout rl_add_common;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_enterprise_choice_member);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("选择常用联系人", "", "",
				0, 0);
		ll_search_item = (LinearLayout) findViewById(R.id.ll_search_item);
		listview = (ListView) findViewById(R.id.listview);

		btn_invite = (Button) findViewById(R.id.btn_invite);
		add_common = (TextView) findViewById(R.id.add_common);
		rl_add_common = (RelativeLayout) findViewById(R.id.rl_add_common);

		adapter = new myAdapter();
		listview.setAdapter(adapter);
		ll_search_item.setVisibility(View.GONE);
		add_common.setVisibility(View.GONE);
		rl_add_common.setVisibility(View.VISIBLE);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		memberDetails = (ArrayList<EnterpriseMemberDetail>) getIntent()
				.getSerializableExtra("members");
		maxShow = getIntent().getIntExtra("maxShow", 0);
		enterpriseId = getIntent().getStringExtra("enterpriseId");

		adapter.notifyDataSetChanged();
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AnimFinsh();
			}
		});

//		setOnRightClickLinester(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				if (memberDetails2.size() <= 0) {
//					ToastUtils.Infotoast(mContext, "请选择常用联系人!");
//					return;
//				} else if (memberDetails2.size() > maxShow) {
//					ToastUtils.Infotoast(mContext, "常用联系人最多为" + maxShow + "人!");
//				} else {
//					if (CommonUtils.isNetworkAvailable(mContext)) {
//						String contactsId = "";
//						for (EnterpriseMemberDetail detail : memberDetails2) {
//							contactsId += detail.getId() + ",";
//						}
//						contactsId = contactsId.substring(0,
//								contactsId.length() - 1);
//						InteNetUtils.getInstance(mContext).addCommon(
//								enterpriseId, contactsId, mRequestCallBack);
//					}
//				}
//			}
//		});
		
		btn_invite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (memberDetails2.size() <= 0) {
					ToastUtils.Infotoast(mContext, "请选择常用联系人!");
					return;
				} else if (memberDetails2.size() > maxShow) {
					ToastUtils.Infotoast(mContext, "常用联系人最多为" + maxShow + "人!");
				} else {
					if (CommonUtils.isNetworkAvailable(mContext)) {
						String contactsId = "";
						for (EnterpriseMemberDetail detail : memberDetails2) {
							contactsId += detail.getId() + ",";
						}
						contactsId = contactsId.substring(0,
								contactsId.length() - 1);
						InteNetUtils.getInstance(mContext).addCommon(
								enterpriseId, contactsId, mRequestCallBack);
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
		if (jsonObject.optString("ret_num").equals("0")) {
			ToastUtils.Infotoast(mContext, "设置常用联系人成功!");
			Intent intent = new Intent();
			intent.putExtra("success", "success");
			setResult(1000, intent);
			AnimFinsh();
		} else {
			ToastUtils.Infotoast(mContext, jsonObject.optString("ret_msg"));
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
			final EnterpriseMemberDetail memberDetail = memberDetails
					.get(position);
			final ItemHolder itemHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.activity_enterprise_add_common_item, null);

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

			itemHolder.item_pinyin.setVisibility(View.GONE);
			itemHolder.item_phone_checkbox.setVisibility(View.VISIBLE);
			itemHolder.item_phone_checkbox.setChecked(memberDetail.isChecked());
			itemHolder.item_phone_poster.setVisibility(View.GONE);
			itemHolder.item_phone_name.setText(memberDetail.getName());
			itemHolder.item_phone_phone.setText(memberDetail.getPhone());
			
			itemHolder.item_phone_phone.setVisibility(View.VISIBLE);

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (itemHolder.item_phone_checkbox.isChecked()) {
						itemHolder.item_phone_checkbox.setChecked(false);
						if (memberDetails2.contains(memberDetail)) {
							memberDetails2.remove(memberDetail);
						}

						memberDetail.setChecked(false);
					} else {
						if (!memberDetails2.contains(memberDetail)) {
							if(memberDetails2.size() >= 50){
								ToastUtils.Errortoast(mContext, "常用联系人只能选择50人");
								return;
							}else{
								itemHolder.item_phone_checkbox.setChecked(true);
								memberDetail.setChecked(true);
								memberDetails2.add(memberDetail);
							}
						}
					}

					btn_invite.setText("确定 (" + memberDetails2.size() + "/50)");
				}
			});

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
