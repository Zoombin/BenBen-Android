package com.xunao.benben.ui.item.TallGroup;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageTask;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.TalkGroup;
import com.xunao.benben.bean.TalkGroupList;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.hx.chatuidemo.activity.ChatActivity;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.ActionSheet.ActionSheetListener;

public class ActivityTalkGroup extends BaseActivity implements OnClickListener,
		ActionSheetListener {

	private ListView listview;
	// 无数据时显示
	private LinearLayout no_talk_group;

	private ArrayList<TalkGroup> talkGroups;

	private MyAdapter myAdapter;
	private int groupNum = 0;

	// 记录是否有本地数据
	private boolean hasLocalData = false;
	private RefershBroadCast mRefershBroadCast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mRefershBroadCast = new RefershBroadCast();
		registerReceiver(mRefershBroadCast, new IntentFilter(
				AndroidConfig.RefreshTalkGroup));

	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_talk_group);

		cubeimageLoader.setImageLoadHandler(new DefaultImageLoadHandler(
				mContext) {
			@Override
			public void onLoading(ImageTask imageTask,
					CubeImageView cubeImageView) {
				Boolean ispost = (Boolean) cubeImageView
						.getTag(R.string.ispost);
				if (cubeImageView != null) {
					cubeImageView.setImageResource(R.drawable.ic_group_poster);
				}

			}

			@Override
			public void onLoadFinish(ImageTask imageTask,
					CubeImageView cubeImageView, BitmapDrawable drawable) {
				if (cubeImageView != null) {
					if (imageTask.getIdentityUrl().equalsIgnoreCase(
							(String) cubeImageView.getTag())) {
						cubeImageView.setVisibility(View.VISIBLE);
						cubeImageView.setImageDrawable(drawable);

					}

				}
			}

			@Override
			public void onLoadError(ImageTask imageTask,
					CubeImageView imageView, int errorCode) {
				if (imageView != null) {
					imageView.setImageResource(R.drawable.ic_group_poster);
				}
			}
		});
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("群组", "", "", R.drawable.icon_com_title_left,
				R.drawable.icon_com_title_more);
		setShowLoding(false);
		listview = (ListView) findViewById(R.id.listview);
		no_talk_group = (LinearLayout) findViewById(R.id.no_talk_group);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		showLoding("请稍后...");
		InteNetUtils.getInstance(mContext).getTalkGroup(mRequestCallBack);

	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(this);
		setOnRightClickLinester(this);
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
        Log.d("ltf","jsonObject========"+jsonObject);
        dissLoding();
		try {
			TalkGroupList groupList = new TalkGroupList();
			groupList = groupList.parseJSON(jsonObject);
			talkGroups = groupList.getTalkGroups();

			if (talkGroups != null && talkGroups.size() > 0) {
				dbUtil.deleteAll(TalkGroup.class);
				dbUtil.saveAll(talkGroups);
				// 改变mapplication里的map、
			} else {
				dbUtil.deleteAll(TalkGroup.class);
			}
		} catch (NetRequestException e) {
			e.printStackTrace();
		} catch (DbException e) {
			e.printStackTrace();
		}

		if (myAdapter == null) {
			myAdapter = new MyAdapter();
		}

		try {
			talkGroups = (ArrayList<TalkGroup>) dbUtil.findAll(TalkGroup.class);
		} catch (DbException e) {
			e.printStackTrace();
		}
		groupNum = 0;
		if (talkGroups != null && talkGroups.size() > 0) {
			mApplication.mTalkGroupMap.clear();
			for (TalkGroup tg : talkGroups) {
				mApplication.mTalkGroupMap.put(tg.getHuanxin_groupid(), tg);
				if (tg.getMember_id().equals(user.getId() + "")) {
					groupNum += 1;
				}
			}

			listview.setAdapter(myAdapter);
			no_talk_group.setVisibility(View.GONE);
			hasLocalData = true;
		} else {
			mApplication.mTalkGroupMap.clear();
			myAdapter.notifyDataSetChanged();
			no_talk_group.setVisibility(View.VISIBLE);
			hasLocalData = false;
		}

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		dissLoding();
		no_talk_group.setVisibility(View.GONE);
        ToastUtils.Infotoast(mContext,"当前无可用网络");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 头部左侧点击
		case R.id.com_title_bar_left_bt:
		case R.id.com_title_bar_left_tv:
			AnimFinsh();
			break;

		// 右侧
		case R.id.com_title_bar_right_bt:
		case R.id.com_title_bar_right_tv:
			setTheme(R.style.ActionSheetStyleIOS7);
			showActionSheet();
			break;

		default:
			break;
		}
	}

	public void showActionSheet() {
		ActionSheet.createBuilder(this, getSupportFragmentManager())
				.setCancelButtonTitle("取消").setOtherButtonTitles("创建群", "查找群")
				// 设置颜色 必须一一对应
				.setOtherButtonTitlesColor("#1E82FF", "#1E82FF")
				.setCancelableOnTouchOutside(true).setListener(this).show();
	}

	@Override
	public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onOtherButtonClick(ActionSheet actionSheet, int index) {

		switch (index) {
		case 0:
			if (groupNum >= 3) {
				ToastUtils.Errortoast(mContext, "一个人最多只能创建3个群组！");
			} else {
				startAnimActivityForResult(ActivityCreatedTallGroup.class, 11);
			}
			break;
		case 1:
			startAnimActivityForResult(ActivityFindTalkGroup.class, 12);
			break;
		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if (arg1 == AndroidConfig.AddGroupResultCode
				|| arg1 == AndroidConfig.writeFriendRefreshResultCode) {
			showLoding("请稍后...");
			InteNetUtils.getInstance(mContext).getTalkGroup(mRequestCallBack);
		}

	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return talkGroups.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View converView, ViewGroup arg2) {

			ItemHolder itemHolder;
			final TalkGroup tG = talkGroups.get(position);

			if (converView == null) {
				converView = LayoutInflater.from(mContext).inflate(
						R.layout.activity_talk_group_item, null);
				itemHolder = new ItemHolder();
				itemHolder.talk_group_poster = (CubeImageView) converView
						.findViewById(R.id.talk_group_poster);
				itemHolder.talk_group_name = (TextView) converView
						.findViewById(R.id.talk_group_name);
				itemHolder.talk_group_level = (TextView) converView
						.findViewById(R.id.talk_group_level);
				converView.setTag(itemHolder);
			} else {
				itemHolder = (ItemHolder) converView.getTag();
			}

			CommonUtils.startImageLoader(cubeimageLoader, tG.getPoster(),
					itemHolder.talk_group_poster);

			itemHolder.talk_group_name.setText(tG.getName() + " ( "
					+ tG.getNumber() + "人 ) ");
			itemHolder.talk_group_level.setText("LV" + tG.getLevel());

			converView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
					if(tG.getStatus().equals("1")){
						final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(mContext,
								R.style.MyDialog1);
						hint.setContent("该群组被屏蔽");
						hint.setBtnContent("确定");
						hint.show();
						hint.setOKListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								hint.dismiss();
							}
						});

						hint.show();
					}else{
						Intent intent = new Intent(mContext, ChatActivity.class);
						intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
						intent.putExtra("groupId", tG.getHuanxin_groupid());
						startActivityForResult(intent,
								AndroidConfig.writeFriendRequestCode);
					}
					
				}
			});

			return converView;
		}

	}

	class ItemHolder {
		CubeImageView talk_group_poster;
		TextView talk_group_name;
		TextView talk_group_level;
	}

	class RefershBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			InteNetUtils.getInstance(mContext).getTalkGroup(mRequestCallBack);
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mRefershBroadCast);
	}

}
