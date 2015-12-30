package com.xunao.benben.ui.item.TallGroup;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageTask;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.BaseActivity.TitleMode;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.bean.TalkGroup;
import com.xunao.benben.bean.talkgroup.GroupMember;
import com.xunao.benben.bean.talkgroup.GroupMemberList;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.item.ActivityContactsInfo;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.utils.ViewHolderUtil;
import com.xunao.benben.view.MyTextView;

public class ActivityTalkGroupMember extends BaseActivity {

	private TalkGroup mTalkGroup;
	private ArrayList<GroupMember> getmGroupMembers;
	private SwipeMenuListView listView;
	private GroupMember master;
	private CubeImageView heard_item_iv;
	private MyTextView heard_item_name;
	private ImageView heard_groupmaster;
	private MemberAdapter adapter;
	private TextView wx_message;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_talkgroupmember);
		initdefaultImage(R.drawable.ic_group_df);

        cubeimageLoader.setImageLoadHandler(new DefaultImageLoadHandler(
                mContext) {
            @Override
            public void onLoading(ImageTask imageTask,
                                  CubeImageView cubeImageView) {
                Boolean ispost = (Boolean) cubeImageView
                        .getTag(R.string.ispost);
                if (cubeImageView != null) {
                    if (ispost != null && ispost) {
                        cubeImageView.setImageResource(R.drawable.ic_group_df);
                    } else {
                        cubeImageView.setImageResource(R.drawable.loading);
                    }
                }

            }

            @Override
            public void onLoadFinish(ImageTask imageTask,
                                     CubeImageView cubeImageView, BitmapDrawable drawable) {
                if (cubeImageView != null) {
                    if (imageTask.getIdentityUrl().equalsIgnoreCase(
                            (String) cubeImageView.getTag())) {

                        // Boolean issuofang = (Boolean) cubeImageView
                        // .getTag(R.string.issuofang);
                        // if (issuofang != null && issuofang) {
                        // Bitmap bitmap = drawable.getBitmap();
                        // int width = bitmap.getWidth();
                        // int height = bitmap.getHeight();
                        // float scal = 1;
                        // if (width > height) {
                        // scal = (width * 1.0f / height * 1.0f);
                        //
                        // if (width > mScreenWidth - PixelUtil.dp2px(75)) {
                        // width = mScreenWidth - PixelUtil.dp2px(75);
                        // height = (int) (width / scal);
                        // } else if (height > maxheight) {
                        // height = maxheight;
                        // width = (int) (height * scal);
                        // } else {
                        // height = (int) (width / scal);
                        // }
                        //
                        // } else {
                        // scal = (height * 1.0f / width * 1.0f);
                        //
                        // if (width > mScreenWidth - PixelUtil.dp2px(75)) {
                        // width = mScreenWidth - PixelUtil.dp2px(75);
                        // height = (int) (width * scal);
                        // } else if (height > maxheight) {
                        // height = maxheight;
                        // width = (int) (height / scal);
                        // } else {
                        // width = (int) (height / scal);
                        // }
                        // }
                        // cubeImageView.getLayoutParams().width = width;
                        // cubeImageView.getLayoutParams().height = height;
                        // }

                        cubeImageView.setVisibility(View.VISIBLE);
                        cubeImageView.setImageDrawable(drawable);

                    }

                }
            }

            @Override
            public void onLoadError(ImageTask imageTask,
                                    CubeImageView imageView, int errorCode) {
                if (imageView != null) {
                    Boolean ispost = (Boolean) imageView
                            .getTag(R.string.ispost);
                    if (ispost != null && ispost) {
                        imageView.setImageResource(R.drawable.ic_group_df);
                    } else {
                        imageView.setImageResource(R.drawable.ic_group_df);
                    }
                }
            }
        });
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitleView();
		TitleMode mode = new TitleMode("#068cd9", "", 0, new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		}, "", R.drawable.ic_back, new OnClickListener() {

			@Override
			public void onClick(View v) {
				mContext.AnimFinsh();
			}
		}, "成员列表", 0);
		chanageTitle(mode);

		listView = (SwipeMenuListView) findViewById(R.id.listView);
		View heard = View.inflate(mContext, R.layout.item_talkmember, null);
		heard_item_iv = ViewHolderUtil.get(heard, R.id.item_iv);
		heard_item_name = ViewHolderUtil.get(heard, R.id.item_name);
		heard_groupmaster = ViewHolderUtil.get(heard, R.id.groupmaster);
		wx_message = (TextView) findViewById(R.id.wx_message);
		listView.addHeaderView(heard);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				GroupMember groupMember = getmGroupMembers.get(arg2 - 1);
				Intent intent = new Intent(mContext,
                        ActivityContactsInfo.class);
				intent.putExtra("username", groupMember.getHuanxin_username());
				startActivity(intent);
				mContext.overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
		});

		heard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext,
                        ActivityContactsInfo.class);
				intent.putExtra("username", master.getHuanxin_username());
				startActivity(intent);
				mContext.overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
		});
		
//		SharedPreferences preferences = getSharedPreferences("alert",
//				mContext.MODE_PRIVATE);
//
//		String isShowWx = preferences.getString("group", "");

		//if (isShowWx.equals("1")) {
//			wx_message.setVisibility(View.GONE);
//
//			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//					RelativeLayout.LayoutParams.WRAP_CONTENT,
//					RelativeLayout.LayoutParams.WRAP_CONTENT);
//			lp.bottomMargin = 0;
//			listView.setLayoutParams(lp);
//		} else {
//			SharedPreferences.Editor editor = preferences.edit();
//			editor.putString("group", "1");
//			editor.commit();
	//	}

	}

	private void initSwipeMenu() {
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(mContext);
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color
						.parseColor("#ec5d57")));
				// set item width
				deleteItem.setWidth(PixelUtil.dp2px(70));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};

		listView.setMenuCreator(creator);

	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		mTalkGroup = (TalkGroup) getIntent().getSerializableExtra("TG");

//		if (Integer.parseInt(mTalkGroup.getIs_admin()) == 1) {
			initSwipeMenu();
			listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(final int position,
						SwipeMenu menu, int index) {
					switch (index) {
					case 0: // 删除群成员
						InteNetUtils.getInstance(mContext).quitTalkGroup(
								mTalkGroup.getId() + "",
								getmGroupMembers.get(position).getId() + "",
								new RequestCallBack<String>() {

									@Override
									public void onFailure(HttpException arg0,
											String arg1) {
										ToastUtils.Errortoast(mContext, "删除失败");
									}

									@Override
									public void onSuccess(
											ResponseInfo<String> arg0) {

										try {
											JSONObject jsonObject = new JSONObject(
													arg0.result);

											SuccessMsg successMsg = new SuccessMsg();
											successMsg.checkJson(jsonObject);
											getmGroupMembers.remove(position);
											adapter.notifyDataSetChanged();
											sendBroadcast(new Intent(
													AndroidConfig.refreshGroupInfo));
											sendBroadcast(new Intent(
													AndroidConfig.RefreshTalkGroup));
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (NetRequestException e) {
											e.printStackTrace();
											e.getError().print(mContext);
										}

									}
								});
						break;
					}
					return false;
				}
			});
//		}else{
//			wx_message.setVisibility(View.GONE);
//
//			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//					RelativeLayout.LayoutParams.WRAP_CONTENT,
//					RelativeLayout.LayoutParams.WRAP_CONTENT);
//			lp.bottomMargin = 0;
//			listView.setLayoutParams(lp);
//		}
		InteNetUtils.getInstance(mContext).getTalkGroupMember(
				mTalkGroup.getId(), mRequestCallBack);
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {

	}

	@Override
	protected void onHttpStart() {

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {

		GroupMemberList groupMemberList = new GroupMemberList();
		try {
			groupMemberList.parseJSON(jsonObject);
			getmGroupMembers = groupMemberList.getmGroupMembers();

			for (int i = 0; i < getmGroupMembers.size(); i++) {
				if (getmGroupMembers.get(i).getIsAdmin() == 1) {
					master = getmGroupMembers.remove(i);
					break;
				}
			}
			addData();
		} catch (NetRequestException e) {
			e.getError().print(mContext);
			e.printStackTrace();
		}

	}

	private void addData() {

		if (master.getIsAdmin() == 0) {
			heard_groupmaster.setVisibility(View.GONE);
		} else {
			heard_groupmaster.setVisibility(View.VISIBLE);
		}

		CommonUtils.startImageLoader(cubeimageLoader, master.getPoster(),
				heard_item_iv);
		String group_nick_name = master.getGroup_nick_name();
		if (TextUtils.isEmpty(group_nick_name)) {
			heard_item_name.setText(master.getNick_name());
		} else {
			heard_item_name.setText(group_nick_name);
		}

		adapter = new MemberAdapter();
		listView.setAdapter(adapter);

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Errortoast(mContext, "网络不可用");
	}

	@Override
	public void onBackPressed() {
		AnimFinsh();
	}

	class MemberAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return getmGroupMembers.size();
		}

		@Override
		public GroupMember getItem(int arg0) {
			// TODO Auto-generated method stub
			return getmGroupMembers.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {

			if (arg1 == null) {
				arg1 = View.inflate(mContext, R.layout.item_talkmember, null);
			}
			GroupMember item = getItem(arg0);
			CubeImageView item_iv = ViewHolderUtil.get(arg1, R.id.item_iv);
			MyTextView item_name = ViewHolderUtil.get(arg1, R.id.item_name);
			ImageView groupmaster = ViewHolderUtil.get(arg1, R.id.groupmaster);

			if (item.getIsAdmin() == 0) {
				groupmaster.setVisibility(View.GONE);
			} else {
				groupmaster.setVisibility(View.VISIBLE);
			}

			CommonUtils.startImageLoader(cubeimageLoader, item.getPoster(),
					item_iv);
			String group_nick_name = item.getGroup_nick_name();
			if (TextUtils.isEmpty(group_nick_name)) {
				item_name.setText(item.getNick_name());
			} else {
				item_name.setText(group_nick_name);
			}
			return arg1;
		}
	}

}
