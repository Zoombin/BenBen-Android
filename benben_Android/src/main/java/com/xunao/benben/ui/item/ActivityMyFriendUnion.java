/**
 * 我的好友联盟
 */

package com.xunao.benben.ui.item;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageTask;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.FriendUnion;
import com.xunao.benben.bean.FriendUnionList;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.CutImageUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.utils.ViewHolderUtil;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.ActionSheet.ActionSheetListener;

public class ActivityMyFriendUnion extends BaseActivity implements
		ActionSheetListener {
	private ListView listview;
	private myAdapter adapter;

	private FriendUnionList friendUnionList;
	private ArrayList<FriendUnion> friendUnions = new ArrayList<FriendUnion>();

	private static final int RESULT = 1;

	private Bitmap bitmap;
	private boolean result = false;
	private String imageName;
	private LinearLayout no_data;
	private myBroadcast broadcast;
	private myBroadCast2 broadCast2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		broadcast = new myBroadcast();
		broadCast2 = new myBroadCast2();
		registerReceiver(broadcast, new IntentFilter(
				AndroidConfig.refreshFriendUnion));
		registerReceiver(broadCast2, new IntentFilter(
				AndroidConfig.refreshFUBroadCasting));

	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_my_friend_union);

		initdefaultImage(R.drawable.ic_group_poster);

	}

	@Override
	public void initView(Bundle savedInstanceState) {

		if (user.getSysLeague() == 2) {
			initTitle_Right_Left_bar("好友联盟", "", "小喇叭",
					R.drawable.icon_com_title_left, 0);
		} else if (user.getSysLeague() == 1) {
			initTitle_Right_Left_bar("好友联盟", "", "",
					R.drawable.icon_com_title_left, 0);
			setOnRightClickLinester(null);
		} else {
			initTitle_Right_Left_bar("好友联盟", "", "",
					R.drawable.icon_com_title_left,
					R.drawable.icon_com_title_more);

		}

		listview = (ListView) findViewById(R.id.listview);
		no_data = (LinearLayout) findViewById(R.id.no_data);

		adapter = new myAdapter();
		listview.setAdapter(adapter);

	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		if (CommonUtils.isNetworkAvailable(mContext)) {
			InteNetUtils.getInstance(mContext).getMyFriendUnion(
					mRequestCallBack);
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

		setOnRightClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (user.getSysLeague() == 2) {
					startAnimActivity(ActivitySmallPublics.class);
				} else {
					showActionSheet();
				}

			}
		});
	}

	protected void showActionSheet() {
		setTheme(R.style.ActionSheetStyleIOS7);
		ActionSheet.createBuilder(this, getSupportFragmentManager())
				.setCancelButtonTitle("取消").setOtherButtonTitles("创建好友联盟")
				// 设置颜色 必须一一对应
				.setOtherButtonTitlesColor("#1E82FF")
				.setCancelableOnTouchOutside(true).setListener(this).show();
	}

	@Override
	protected void onHttpStart() {

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		lodingDialog.dismiss();
		friendUnionList = new FriendUnionList();
		try {
			friendUnionList = (FriendUnionList) friendUnionList
					.parseJSON(jsonObject);
			friendUnions.clear();
			if (friendUnionList.getFriendUnions() == null
					|| friendUnionList.getFriendUnions().size() <= 0) {
				no_data.setVisibility(View.VISIBLE);
				listview.setVisibility(View.GONE);
			} else {
				friendUnions.addAll(friendUnionList.getFriendUnions());
				no_data.setVisibility(View.GONE);
				listview.setVisibility(View.VISIBLE);
			}
			user.setLeague(0);

			if (friendUnions.size() > 0) {
				for (FriendUnion union : friendUnions) {
					
					
					if (union.getType().equals("0")) {
						user.setLeague(1);
						break;
					}
				}
			}
			dbUtil.saveOrUpdate(user);
		} catch (NetRequestException | DbException e) {
			e.printStackTrace();
			ToastUtils.Infotoast(mContext, "网络不可用,请重试!");
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Infotoast(mContext, "网络不可用,请重试!");
	}

	@Override
	public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
	}

	@Override
	public void onOtherButtonClick(ActionSheet actionSheet, int index) {
		switch (index) {
		case 0:
			startAnimActivityForResult(ActivityCreatedFriendUnion.class, RESULT);
			break;
		}
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
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.activity_friend_union_item, null);
			}

			TextView freind_union_name = ViewHolderUtil.get(convertView,
					R.id.tv_freind_union_name);

			TextView friend_union_number = ViewHolderUtil.get(convertView,
					R.id.tv_friend_union_number);

			TextView friend_union_description = ViewHolderUtil.get(convertView,
					R.id.tv_friend_union_description);

            TextView tv_union_type = ViewHolderUtil.get(convertView,
                    R.id.tv_union_type);

			RoundedImageView friend_union_poster = ViewHolderUtil.get(
					convertView, R.id.friend_union_poster);

			freind_union_name.setText(friendUnions.get(position).getName());
			friend_union_number.setText("("
					+ friendUnions.get(position).getNumber() + "人)");
			friend_union_description.setText(friendUnions.get(position)
					.getArea());

			CommonUtils
					.startImageLoader(cubeimageLoader,
							friendUnions.get(position).getPoster(),
							friend_union_poster);
            if(friendUnions.get(position).getCategory().equals("2")){
                tv_union_type.setText("英雄");
                tv_union_type.setTextColor(Color.rgb(33,207,213));
                tv_union_type.setBackgroundResource(R.drawable.textview_friend_union_2);
            }else{
                tv_union_type.setText("工作");
                tv_union_type.setTextColor(Color.rgb(233,81,135));
                tv_union_type.setBackgroundResource(R.drawable.textview_friend_union_1);
            }


			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (friendUnions.get(position).getStatus() == 1) {
						final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(mContext,
								R.style.MyDialog1);
						hint.setContent("该好友联盟已屏蔽");
						hint.setBtnContent("确定");
						hint.show();
						hint.setOKListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								hint.dismiss();
							}
						});

						hint.show();
						return;
					}
					startAnimActivity2Obj(ActivityFriendUnionDetail.class,
							"friendUnion", friendUnions.get(position));
				}
			});

			return convertView;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case RESULT:
			if (data != null) {
				friendUnions.clear();
				InteNetUtils.getInstance(mContext).getMyFriendUnion(
						mRequestCallBack);
			}
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		if (user.isUpdate()) {
			friendUnions.clear();
			InteNetUtils.getInstance(mContext).getMyFriendUnion(
					mRequestCallBack);
			user.setUpdate(false);
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcast);
	}

	class myBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if (CommonUtils.isNetworkAvailable(mContext)) {
				friendUnions.clear();
				lodingDialog.dismiss();
				InteNetUtils.getInstance(mContext).getMyFriendUnion(
						mRequestCallBack);
			}
		}
	}

	class myBroadCast2 extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if (user.getSysLeague() == 2) {
				initTitle_Right_Left_bar("好友联盟", "", "小喇叭",
						R.drawable.icon_com_title_left, 0);
			} else {
				initTitle_Right_Left_bar("好友联盟", "", "",
						R.drawable.icon_com_title_left,
						R.drawable.icon_com_title_more);

			}
		}

	}

}
