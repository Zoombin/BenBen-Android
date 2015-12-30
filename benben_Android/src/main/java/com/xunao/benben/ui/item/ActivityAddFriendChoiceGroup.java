package com.xunao.benben.ui.item;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
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

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.ContactsGroup;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.MyTextView;

public class ActivityAddFriendChoiceGroup extends BaseActivity {
	private String groupId;
	private ListView listview;
	private myAdapter adapter;
	private String targetGroupId = "";
	private String memberId;
	private Contacts contacts;

	private ArrayList<ContactsGroup> contactsGroups = new ArrayList<ContactsGroup>();

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_change_contacts_group);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("选择分组", "", "",
				R.drawable.icon_com_title_left, 0);

		listview = (ListView) findViewById(R.id.listview);
		adapter = new myAdapter();
		listview.setAdapter(adapter);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
	
		initData();
	}

	private void initData() {
		try {
			List<ContactsGroup> list = dbUtil.findAll(Selector.from(
					ContactsGroup.class).where(
					WhereBuilder.b("id", "!=", "10000")));
			if (list.size() > 0) {
				contactsGroups = (ArrayList<ContactsGroup>) list;
				adapter.notifyDataSetChanged();
			} else {
				ToastUtils.Infotoast(mContext, "您还没有创建分组!");
				AnimFinsh();
			}

			// contacts = dbUtil.findById(Contacts.class, memberId);
		} catch (DbException e) {
			e.printStackTrace();
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

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int positon, long arg3) {
				targetGroupId = contactsGroups.get(positon).getId()+"";
				String groupName = contactsGroups.get(positon).getName();
				Intent intent = new Intent();
				intent.putExtra("groupId", targetGroupId);
				intent.putExtra("groupName", groupName);
				setResult(10010, intent);
				AnimFinsh();
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
		ToastUtils.Infotoast(mContext, "网络不可用!");
	}

	class myAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return contactsGroups.size();
		}

		@Override
		public Object getItem(int arg0) {
			return contactsGroups.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View converView, ViewGroup parents) {
			ContactsGroup group = contactsGroups.get(position);
			if (converView == null) {
				converView = LayoutInflater.from(mContext).inflate(
						R.layout.item_edit_contacts_unshort, null);
			}

			ImageView delete_but = (ImageView) converView
					.findViewById(R.id.delete_but);
			MyTextView item_phone_name = (MyTextView) converView
					.findViewById(R.id.item_phone_name);
			item_phone_name.setText(group.getName());
			delete_but.setVisibility(View.GONE);
			return converView;
		}

	}

}
