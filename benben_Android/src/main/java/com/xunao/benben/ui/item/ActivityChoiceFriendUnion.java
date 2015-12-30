package com.xunao.benben.ui.item;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.FriendUnion;
import com.xunao.benben.bean.FriendUnionList;
import com.xunao.benben.bean.MyFriendToUnion;
import com.xunao.benben.bean.TrumpetArea;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

public class ActivityChoiceFriendUnion extends BaseActivity {
	private ListView listView;
	private myAdapter adapter;
	private LinearLayout no_data;
	private FriendUnionList friendUnionList;
	private ArrayList<FriendUnion> friendUnions = new ArrayList<FriendUnion>();
	private ArrayList<FriendUnion> friendUnions2 = new ArrayList<FriendUnion>();

	private final static int CHOICE_ADDRESS = 1;
	private final static int CHOICE_RESULT = 2;
	private String[] addressId;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_choice_friend_union);
		initdefaultImage(R.drawable.ic_group_poster);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("发送小喇叭", "", "完成",
				R.drawable.icon_com_title_left, 0);
		listView = (ListView) findViewById(R.id.listview);
		no_data = (LinearLayout) findViewById(R.id.no_data);

		adapter = new myAdapter();
		listView.setAdapter(adapter);

	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		if (CommonUtils.isNetworkAvailable(mContext))
			InteNetUtils.getInstance(mContext).getMyFriendUnion(
					mRequestCallBack);
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AnimFinsh();
			}
		});

		setOnRightClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (friendUnions2.size() <= 0) {
					ToastUtils.Infotoast(mContext, "请选择要发送的联盟!");
				} else {
					Intent intent = new Intent();
					intent.putExtra("trumpetArea", "");
					intent.putExtra("friendUnions", friendUnions2);
					setResult(CHOICE_RESULT, intent);
					AnimFinsh();
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
		try {
			friendUnionList = new FriendUnionList();
			friendUnionList = (FriendUnionList) friendUnionList
					.parseJSON(jsonObject);

			if (friendUnionList != null
					&& friendUnionList.getFriendUnions() != null) {
				friendUnions.addAll(friendUnionList.getFriendUnions());
			} else {
				ToastUtils.Infotoast(mContext, "您还没有好友联盟,请创建或加入!");
				AnimFinsh();
			}

			if (friendUnions.size() <= 0) {
				no_data.setVisibility(View.VISIBLE);
				listView.setVisibility(View.GONE);
			} else {
				no_data.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
			}

		} catch (NetRequestException e) {
			e.printStackTrace();
		}

		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Infotoast(mContext, "网络不可用,请重试!");
	}

	class myAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return friendUnions.size();
		}

		@Override
		public Object getItem(int arg0) {
			return friendUnions.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			final FriendUnion friendToUnion = friendUnions.get(position);
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.activity_my_friend_to_union_item, null);
			}

			TextView item_pinyin = (TextView) convertView
					.findViewById(R.id.item_pinyin);
			final CheckBox item_phone_checkbox = (CheckBox) convertView
					.findViewById(R.id.item_phone_checkbox);

			RoundedImageView item_phone_poster = (RoundedImageView) convertView
					.findViewById(R.id.item_phone_poster);

			TextView item_phone_name = (TextView) convertView
					.findViewById(R.id.item_phone_name);

			item_pinyin.setVisibility(View.GONE);
			item_phone_checkbox.setClickable(false);
			item_phone_checkbox.setVisibility(View.VISIBLE);

			item_phone_name.setText(friendToUnion.getNinkName());
			String poster = friendToUnion.getPoster();
			CommonUtils.startImageLoader(cubeimageLoader, poster,
					item_phone_poster);

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (item_phone_checkbox.isChecked()) {
						item_phone_checkbox.setChecked(false);
						friendUnions2.remove(friendToUnion);
					} else {
						item_phone_checkbox.setChecked(true);
						friendUnions2.add(friendToUnion);
					}
				}
			});

			return convertView;
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent data) {
		switch (arg0) {
		case CHOICE_ADDRESS:
			if (data != null) {
				ArrayList<TrumpetArea> trumpetAreas = new ArrayList<TrumpetArea>();
				trumpetAreas = (ArrayList<TrumpetArea>) data
						.getSerializableExtra("trumpetAreas");
				Intent intent = new Intent();
				intent.putExtra("trumpetArea", trumpetAreas);
				intent.putExtra("friendUnions", friendUnions2);
				setResult(CHOICE_RESULT, intent);
				AnimFinsh();
			}
			break;
		}
		super.onActivityResult(arg0, arg1, data);
	}
}
