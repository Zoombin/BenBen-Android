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
import android.widget.ListView;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.BaseActivity.TitleMode;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.hx.chatuidemo.activity.ChatActivity;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ViewHolderUtil;
import com.xunao.benben.view.MyTextView;

public class ActivityBenBenFriend extends BaseActivity {

	private ListView listview;
	private View nodota;
	private ArrayList<Contacts> contactsList;
	private LayoutInflater inflater;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_benben_friend);
		initdefaultImage(R.drawable.default_face);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitleView();
		TitleMode mode = new TitleMode("#068cd9", "", 0, null, "",
				R.drawable.ic_back, new OnClickListener() {
					@Override
					public void onClick(View v) {
						mContext.AnimFinsh();
					}
				}, "好友聊天", 0);
		chanageTitle(mode);
		inflater = LayoutInflater.from(mContext);

		listview = (ListView) findViewById(R.id.listview);
		nodota = findViewById(R.id.nodota);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				Contacts contacts = contactsList.get(arg2);
				Intent intent = new Intent(mContext, ChatActivity.class);
				intent.putExtra("userId", contacts.getHuanxin_username());
				intent.putExtra("contacts", contacts);
				startActivity(intent);

			}
		});
	}

	@Override
	public void initDate(Bundle savedInstanceState) {

		try {
			List<Contacts> findAll = dbUtil.findAll(Selector.from(
                    Contacts.class).where("is_benben", "!=", "0").and("group_id","!=","10000"));
			contactsList = (ArrayList<Contacts>) findAll;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (contactsList != null && contactsList.size() > 0) {
			nodota.setVisibility(View.GONE);

			ContactsAdapter adapter = new ContactsAdapter();

			listview.setAdapter(adapter);

		} else {
			nodota.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		// TODO Auto-generated method stub

	}

	class ContactsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return contactsList.size();
		}

		@Override
		public Contacts getItem(int position) {
			// TODO Auto-generated method stub
			return contactsList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Contacts item = getItem(position);
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_benben_friend,
						null);
			}
			RoundedImageView item_poster = ViewHolderUtil.get(convertView,
					R.id.item_poster);
			MyTextView item_name = ViewHolderUtil.get(convertView,
					R.id.item_name);

			CommonUtils.startImageLoader(cubeimageLoader, item.getPoster(),
					item_poster);

			item_name.setText(item.getName());

			return convertView;
		}

	}

}
