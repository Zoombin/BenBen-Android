package com.xunao.benben.ui.item;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.EnterpriseGroup;
import com.xunao.benben.bean.EnterpriseMemberDetail;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.ToastUtils;

public class ActivityDeleteEnterpriseGroup extends BaseActivity implements
		OnClickListener {
	private TextView point_info;
	private TextView btn_delete;
	private ListView listview;
	private EnterpriseGroup enterpriseGroup;
	private ArrayList<EnterpriseGroup> enterpriseGroups = new ArrayList<EnterpriseGroup>();
	private ArrayList<EnterpriseMemberDetail> memberDetails = new ArrayList<EnterpriseMemberDetail>();
	private myAdapter adapter;
	private String enterpriseId = "";
	private boolean isWfz = false;

	// 记录删除的groupid 默认为-1 即只删除不移动 这个是要移动到的组的id
	private String delete_id = "-1";

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_packet_delete);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("确认删除", "", "",
				R.drawable.icon_com_title_left, 0);
		point_info = (TextView) findViewById(R.id.point_info);
		btn_delete = (TextView) findViewById(R.id.btn_delete);

		listview = (ListView) findViewById(R.id.listview);

		btn_delete.setOnClickListener(this);

	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		enterpriseGroup = (EnterpriseGroup) intent
				.getSerializableExtra("group");

		enterpriseGroups = (ArrayList<EnterpriseGroup>) intent
				.getSerializableExtra("groupList");
		memberDetails = (ArrayList<EnterpriseMemberDetail>) intent
				.getSerializableExtra("member");

		enterpriseId = intent.getStringExtra("enterpriseId");

		EnterpriseGroup myGroup = new EnterpriseGroup();
		myGroup.setId("-1");
		myGroup.setGroupName("同时删除分组下所有成员");
		myGroup.setSelect(true);
		enterpriseGroups.add(myGroup);

		// 去除选中的group
		for (int i = 0; i < enterpriseGroups.size(); i++) {
			if (enterpriseGroups.get(i).getId().equals(enterpriseGroup.getId())) {
				if (enterpriseGroups.get(i).getGroupName().equals("未分组")) {
					isWfz = true;
				}

				enterpriseGroups.remove(enterpriseGroups.get(i));
				break;
			}
		}

		if (enterpriseGroups.size() > 0) {
			// 修改提示信息
			point_info.setText("将分组下成员移动到其他分组:");

		} else {
			point_info.setText("删除当前分组,分组下成员会移动到未分组:");
		}

		adapter = new myAdapter();
		listview.setAdapter(adapter);
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AnimFinsh();
			}
		});

		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				for (int i = 0; i < enterpriseGroups.size(); i++) {
					enterpriseGroups.get(i).setSelect(false);
				}
				enterpriseGroups.get(position).setSelect(true);

				// 更新deleteid
				delete_id = enterpriseGroups.get(position).getId();

				adapter.notifyDataSetChanged();
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
	public void onBackPressed() {
		AnimFinsh();
	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		if (jsonObject.optString("ret_num").equals("0")) {
			if (delete_id.equals("-1")) {
				if (isWfz) {
					ToastUtils.Infotoast(mContext, "删除分组成员成功!");
				} else {
					ToastUtils.Infotoast(mContext, "删除分组成功!");
				}
			} else {
				ToastUtils.Infotoast(mContext, "删除分组成功!");
			}
			Intent data = new Intent();
			setResult(200, data);
			AnimFinsh();
		} else {
			ToastUtils.Infotoast(mContext, jsonObject.optString("ret_msg"));
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Infotoast(mContext, "网络不可用,请重试!");
	}

	private itemHolder itHolder = null;

	class myAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return enterpriseGroups.size();
		}

		@Override
		public Object getItem(int arg0) {
			return enterpriseGroups.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final EnterpriseGroup enterpriseGroup = enterpriseGroups
					.get(position);
			if (convertView == null) {
				itHolder = new itemHolder();
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.activity_packet_item, null);
				itHolder.item_select = (ImageView) convertView
						.findViewById(R.id.item_select);
				itHolder.group_name = (TextView) convertView
						.findViewById(R.id.group_name);

				itHolder.changeNameBut = (ImageView) convertView
						.findViewById(R.id.changeNameBut);
				itHolder.addFriend = (LinearLayout) convertView
						.findViewById(R.id.addFriend);

				itHolder.changeNameBut.setVisibility(View.GONE);
				itHolder.addFriend.setVisibility(View.GONE);

				// convertView.findViewById(R.id.addFriend).setVisibility(
				// View.GONE);
				convertView.setTag(itHolder);
			} else {
				itHolder = (itemHolder) convertView.getTag();
			}

			if (enterpriseGroup.isSelect()) {
				itHolder.item_select
						.setImageResource(R.drawable.icon_checkbox_select);
			} else {
				itHolder.item_select
						.setImageResource(R.drawable.icon_checkbox_noselect);
			}

			if (position == enterpriseGroups.size() - 1) {
				convertView.setPadding(0, PixelUtil.dp2px(20), 0, 0);
				itHolder.group_name.setTextColor(Color.parseColor("#de3f3f"));
			} else {
				convertView.setPadding(0, 0, 0, 0);
				itHolder.group_name.setTextColor(Color.parseColor("#000000"));
			}

			itHolder.group_name.setText(enterpriseGroup.getGroupName());

			return convertView;
		}

	}

	class itemHolder {
		ImageView item_select;
		TextView group_name;
		ImageView changeNameBut;
		LinearLayout addFriend;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_delete:
//			if (memberDetails.size() <= 0) {
//				ToastUtils.Infotoast(mContext, "此通讯录中没有成员!");
//				return;
//			}
//			String id = "";
//			for (EnterpriseMemberDetail detail : memberDetails) {
//				id += detail.getId() + ",";
//			}
//			id = id.substring(0, id.length() - 1);
			if (CommonUtils.isNetworkAvailable(mContext)) {
				if (delete_id.equals("-1")) {
					InteNetUtils.getInstance(mContext)
							.delEnterpriseGroupMember("0",
									enterpriseGroup.getId(), enterpriseId,
									mRequestCallBack);
				} else {
					InteNetUtils.getInstance(mContext)
							.delEnterpriseGroupMember(delete_id,
									enterpriseGroup.getId(), enterpriseId,
									mRequestCallBack);
				}
			}
			break;

		default:
			break;
		}
	}

}
