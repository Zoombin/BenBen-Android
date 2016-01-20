package com.xunao.test.ui.item.ContectManagement;

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
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.xunao.test.R;
import com.xunao.test.base.BaseActivity;
import com.xunao.test.bean.Contacts;
import com.xunao.test.bean.ContactsGroup;
import com.xunao.test.bean.ContactsObject;
import com.xunao.test.bean.SuccessMsg;
import com.xunao.test.config.AndroidConfig;
import com.xunao.test.exception.NetRequestException;
import com.xunao.test.net.InteNetUtils;
import com.xunao.test.utils.CommonUtils;
import com.xunao.test.utils.PixelUtil;
import com.xunao.test.utils.ToastUtils;

public class ActivityPacketDelete extends BaseActivity implements
		OnClickListener {
	// 这个是上个页面传递过来的group信息
	private ContactsGroup contactsGroup;

	private ContactsObject contactsObject;
	private ListView listview;

	// 记录删除的groupid 默认为-1 即只删除不移动 这个是要移动到的组的id
	private String delete_id = "-1";

	private ArrayList<ContactsGroup> mContactsGroups = new ArrayList<ContactsGroup>();
	private ContactsGroup myGroup;
	private MyAdapter myAdapter;

	private TextView point_info;

	private TextView btn_delete;
	private String delGroupName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_packet_delete);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		initTitle_Right_Left_bar("确认删除", "", "",
				R.drawable.icon_com_title_left, 0);

		point_info = (TextView) findViewById(R.id.point_info);
		btn_delete = (TextView) findViewById(R.id.btn_delete);

		listview = (ListView) findViewById(R.id.listview);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		contactsGroup = (ContactsGroup) getIntent().getSerializableExtra(
				"contactsGroup");
		contactsObject = mApplication.contactsObject;

		// 修改提示信息
		point_info.setText("将分组下成员移动到其他分组:");
		mContactsGroups.clear();

		for (ContactsGroup c : contactsObject.getmContactsGroups()) {
			if (c.getId() != 10000) {
				c.setSelect(false);
				mContactsGroups.add(c);
			}
		}

		// 去除选中的group
		mContactsGroups.remove(contactsGroup);
		ContactsGroup noGroup = new ContactsGroup();
		// for (int i = 0; i < mContactsGroups.size(); i++) {
		// if (mContactsGroups.get(i).getName().equals("未分组")) {
		// noGroup = mContactsGroups.remove(i);
		// break;
		// }
		// }
		// mContactsGroups.add(noGroup);
		// 自定义一个group 放在最后一个
		myGroup = new ContactsGroup();
		myGroup.setId(-1);
		myGroup.setName("同时删除分组下所有成员");
		myGroup.setSelect(true);
		mContactsGroups.add(myGroup);

		myAdapter = new MyAdapter();
		listview.setAdapter(myAdapter);
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(this);
		btn_delete.setOnClickListener(this);

		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				for (int i = 0; i < mContactsGroups.size(); i++) {
					mContactsGroups.get(i).setSelect(false);
				}
				mContactsGroups.get(position).setSelect(true);
				// 更新deleteid
				delete_id = mContactsGroups.get(position).getId() + "";

				myAdapter.notifyDataSetChanged();
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
		try {
			SuccessMsg msg = new SuccessMsg();
			msg.parseJSON(jsonObject);

			// 更新本地数据f

			if (!contactsGroup.getName().equals("未分组")) {
				dbUtil.delete(contactsGroup);
				mContactsGroups.remove(myGroup);
			}

			// TODO
			// 为-1时
			if ("-1".equals(delete_id)) {
				dbUtil.deleteAll(contactsGroup.getmContacts());
			} else {
				ContactsGroup contactsGroup2 = mApplication.mContactsGroupMap
						.get(delete_id);
				if (contactsGroup.getmContacts() != null) {
					for (Contacts ct : contactsGroup.getmContacts()) {
						ct.setGroup_id(delete_id);
						contactsGroup2.getmContacts().add(ct);
					}
					dbUtil.saveOrUpdateAll(contactsGroup.getmContacts());
				}
			}

			mApplication.contactsObject.getmContactsGroups().remove(
					contactsGroup);
			// 注意 没有把phoneInfo信息删除 暂时感觉没有必要
			// 设置传递回去的数据 删除mygroup
			sendBroadcast(new Intent(AndroidConfig.ContactsRefresh));
			setResult(AndroidConfig.PacketManagementResultCode);
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
		ToastUtils.Errortoast(mContext, "操作失败，请重试");
	}

	private itemHolder itHolder = null;

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mContactsGroups.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mContactsGroups.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			final ContactsGroup contactsGroup = mContactsGroups.get(position);

			if (convertView == null) {
				itHolder = new itemHolder();
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.activity_packet_item, null);
				itHolder.item_select = (ImageView) convertView
						.findViewById(R.id.item_select);
				convertView.findViewById(R.id.addFriend).setVisibility(
						View.GONE);
				convertView.findViewById(R.id.changeNameBut).setVisibility(
						View.GONE);
				itHolder.group_name = (TextView) convertView
						.findViewById(R.id.group_name);
				convertView.setTag(itHolder);
			} else {
				itHolder = (itemHolder) convertView.getTag();
			}

			if (contactsGroup.isSelect()) {
				itHolder.item_select
						.setImageResource(R.drawable.icon_checkbox_select);
			} else {
				itHolder.item_select
						.setImageResource(R.drawable.icon_checkbox_noselect);
			}

			if (position == mContactsGroups.size() - 1) {
				convertView.setPadding(0, PixelUtil.dp2px(20), 0, 0);
				
				itHolder.group_name.setTextColor(Color.parseColor("#de3f3f"));
			} else {
				convertView.setPadding(0, 0, 0, 0);
				itHolder.group_name.setTextColor(Color.parseColor("#000000"));
			}
			itHolder.group_name.setText(contactsGroup.getName());

			return convertView;
		}

	}

	class itemHolder {
		ImageView item_select;
		TextView group_name;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 头部左侧点击
		case R.id.com_title_bar_left_bt:
		case R.id.com_title_bar_left_tv:
			onBackPressed();
			break;

		// 删除按钮
		case R.id.btn_delete:
			String target = delete_id.equals("-1") ? "0" : delete_id;
			if (CommonUtils.isNetworkAvailable(mContext))
				InteNetUtils.getInstance(mContext).DeletePacket(
						contactsGroup.getId() + "", target, mRequestCallBack);
			break;

		default:
			break;
		}

	}

	@Override
	public void onBackPressed() {
		mContactsGroups.remove(myGroup);
		if (mContactsGroups.size() > 0) {
			for (ContactsGroup cg : mContactsGroups) {
				cg.setSelect(false);
			}
		}
		AnimFinsh();
	}

}
