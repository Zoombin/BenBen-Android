package com.xunao.test.ui.item;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.test.R;
import com.xunao.test.base.BaseActivity;
import com.xunao.test.base.IA.CrashApplication;
import com.xunao.test.bean.News;
import com.xunao.test.bean.NewsList;
import com.xunao.test.config.AndroidConfig;
import com.xunao.test.exception.NetRequestException;
import com.xunao.test.net.InteNetUtils;
import com.xunao.test.utils.CommonUtils;
import com.xunao.test.utils.PixelUtil;
import com.xunao.test.utils.TimeUtil;
import com.xunao.test.utils.ViewHolderUtil;
import com.xunao.test.view.MyTextView;

public class ActivityNews extends BaseActivity {

	private List<News> mNews = new ArrayList<>();
	private SwipeMenuListView listview;
	private RelativeLayout nodota;
	private NewsRefreshBroadCast mNewsRefreshBroadCast;
	private TextView wx_message;
	private FriendAdapter friendAdapter;
    Dialog dialog;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_news);
		mNewsRefreshBroadCast = new NewsRefreshBroadCast();
		registerReceiver(mNewsRefreshBroadCast, new IntentFilter(
				AndroidConfig.refreshNews));
        if(getHintState("help3")) {
            showHintDiaLog();
        }
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitleView();
		TitleMode mode = new TitleMode("#068cd9", "小喇叭", 0,
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 跳转发送小喇叭
						startAnimActivity(ActivitySmallPublics.class);
					}
				}, "", R.drawable.ic_back, new OnClickListener() {

					@Override
					public void onClick(View v) {
						mContext.AnimFinsh();
					}
				}, "消息中心", 0);
		chanageTitle(mode);

		listview = (SwipeMenuListView) findViewById(R.id.listview);
        mNews = new ArrayList<>();
        friendAdapter = new FriendAdapter();
        listview.setAdapter(friendAdapter);
		nodota = (RelativeLayout) findViewById(R.id.nodota);

		wx_message = (TextView) findViewById(R.id.wx_message);

		InteNetUtils.getInstanceNo(getApplicationContext()).getUpdateInfo(
				CrashApplication.getInstance().getSpUtil().getLastTime() + "",
				mRequestCallBack);

//		SharedPreferences preferences = getSharedPreferences("alert",
//				mContext.MODE_PRIVATE);
//
//		String isShowWx = preferences.getString("news", "");
//
//		if (isShowWx.equals("1")) {
//			wx_message.setVisibility(View.GONE);
//
//			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//					RelativeLayout.LayoutParams.WRAP_CONTENT,
//					RelativeLayout.LayoutParams.WRAP_CONTENT);
//			lp.bottomMargin = 0;
//			listview.setLayoutParams(lp);
//		} else {
//			SharedPreferences.Editor editor = preferences.edit();
//			editor.putString("news", "1");
//			editor.commit();
//		}

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				News news = mNews.get(position);
				if (news.getType() == 7) {

					InteNetUtils.getInstance(mContext).readNews(
							news.getId() + "", new RequestCallBack<String>() {

								@Override
								public void onFailure(HttpException arg0,
										String arg1) {

								}

								@Override
								public void onSuccess(ResponseInfo<String> arg0) {
								}

							});

					news.setStatus(1);
					try {
						dbUtil.saveOrUpdate(news);
					} catch (DbException e) {
						e.printStackTrace();
					}
					startAnimActivity2Obj(ActivityNumberTrainDetail.class,
							"id", news.getIdentity1() + "");
				} else if (news.getType() == 8) {
					InteNetUtils.getInstance(mContext).readNews(
							news.getId() + "", new RequestCallBack<String>() {

								@Override
								public void onFailure(HttpException arg0,
										String arg1) {

								}

								@Override
								public void onSuccess(ResponseInfo<String> arg0) {
								}

							});

					news.setStatus(1);
					try {
						dbUtil.saveOrUpdate(news);
					} catch (DbException e) {
						e.printStackTrace();
					}
					startAnimActivity2Obj(ActivityBuyInfoContent.class, "ID",
							news.getIdentity1() + "");
				} else {
					startAnimActivity2Obj(ActivityNewContent.class, "NEWS",
							mNews.get(position));
				}

			}
		});

		listview.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(final int position, SwipeMenu menu,
					int index) {
				// 删除消息
				final News news = mNews.get(position);

				int status = news.getStatus();

				if (status == 0) {
					InteNetUtils.getInstance(mContext).readNews(
							news.getId() + "", new RequestCallBack<String>() {

								@Override
								public void onFailure(HttpException arg0,
										String arg1) {
									try {
										dbUtil.delete(news);
									} catch (DbException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									mNews.remove(position);
									if (friendAdapter != null) {
										friendAdapter.notifyDataSetChanged();
									}
								}

								@Override
								public void onSuccess(ResponseInfo<String> arg0) {
									try {
										dbUtil.delete(news);
									} catch (DbException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									mNews.remove(position);
									if (friendAdapter != null) {
										friendAdapter.notifyDataSetChanged();
									}
								}
							});
				} else {
					try {
						dbUtil.delete(news);
					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mNews.remove(position);
					if (friendAdapter != null) {
						friendAdapter.notifyDataSetChanged();
					}
				}

				return false;
			}
		});

		initSwipeMenu();
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

		listview.setMenuCreator(creator);

	}

	@Override
	public void initDate(Bundle savedInstanceState) {

	}

	private void addData(List<News> mNews2) {
		nodota.setVisibility(View.GONE);
		if (friendAdapter == null) {
			friendAdapter = new FriendAdapter();
			listview.setAdapter(friendAdapter);
		} else {
			friendAdapter.notifyDataSetChanged();
		}
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
		// JSONObject jsonObject = new JSONObject(arg0.result);
		NewsList alllist = new NewsList();

		try {
			alllist.parseJSON(jsonObject);

			if (alllist != null && alllist.getmNewsList() != null) {
				News news = alllist.getmNewsList().get(0);
				CrashApplication.getInstance().getSpUtil()
						.setLastTime(news.getCreated_time());
				DbUtils db = CrashApplication.getInstance().getDb();
				try {
					for (News n : alllist.getmNewsList()) {
						db.save(n);
					}
				} catch (DbException e) {
					e.printStackTrace();
				}
				
				friendAdapter.notifyDataSetChanged();
			}
			sendBroadcast(new Intent(AndroidConfig.refreshNews));

		} catch (NetRequestException e) {
			e.printStackTrace();
		}

		

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {

	}

	class FriendAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
            if(mNews!=null) {
                return mNews.size();
            }else{
                return 0;
            }
		}

		@Override
		public News getItem(int position) {
			// TODO Auto-generated method stub
            if(mNews!=null) {
                return mNews.get(position);
            }else{
                return null;
            }

		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = View.inflate(mContext, R.layout.item_news, null);
			}
			final News item = getItem(position);
			RoundedImageView item_iv = ViewHolderUtil.get(convertView,
					R.id.item_iv);
			MyTextView item_name = ViewHolderUtil.get(convertView,
					R.id.item_name);
			MyTextView item_time = ViewHolderUtil.get(convertView,
					R.id.item_time);
			MyTextView item_friend_content = ViewHolderUtil.get(convertView,
					R.id.item_friend_content);
			TextView red_point = ViewHolderUtil
					.get(convertView, R.id.red_point);
			MyTextView item_smalltitle = ViewHolderUtil.get(convertView,
					R.id.item_smalltitle);

			item_time.setText(TimeUtil.getCurTime(new Date(item
					.getCreated_time() * 1000)));
			item_friend_content.setText(item.getContent());
			item_name.setText(item.getSender());

			red_point.setVisibility(item.getStatus() == 0 ? View.VISIBLE
					: View.GONE);

			switch (item.getType()) {
			case 1:// 公告
				item_iv.setImageResource(R.drawable.ic_public);
				item_smalltitle.setText("公告");
				item_smalltitle.setBackgroundResource(R.drawable.g_bg);
				break;
			case 2:// 通知
				item_smalltitle.setText("通知");
				item_smalltitle.setBackgroundResource(R.drawable.b_bg);
				item_iv.setImageResource(R.drawable.ic_notofication);
				break;
			case 3:// 小喇叭
				item_smalltitle.setBackgroundResource(R.drawable.o_bg);
				item_smalltitle.setText("小喇叭");
				CommonUtils.startImageLoader(cubeimageLoader, item.getPoster(),
						item_iv);
				break;
			case 4:// 政企
				item_smalltitle.setBackgroundResource(R.drawable.b_bg);
				item_smalltitle.setText("政企通讯录");
				CommonUtils.startImageLoader(cubeimageLoader, item.getPoster(),
						item_iv);
				break;
			case 5:// 好友联盟
				item_smalltitle.setBackgroundResource(R.drawable.b_bg);
				item_smalltitle.setText("好友联盟");
				CommonUtils.startImageLoader(cubeimageLoader, item.getPoster(),
						item_iv);
				break;
			case 6:// 微创组同步小喇叭
				item_smalltitle.setBackgroundResource(R.drawable.o_bg);
				item_smalltitle.setText("微创作");
				CommonUtils.startImageLoader(cubeimageLoader, item.getPoster(),
						item_iv);
				break;
			case 7:
				item_smalltitle.setBackgroundResource(R.drawable.o_bg);
				item_smalltitle.setText("号码直通车");
				CommonUtils.startImageLoader(cubeimageLoader, item.getPoster(),
						item_iv);
				break;
			case 8:
				item_smalltitle.setBackgroundResource(R.drawable.g_bg);
				item_smalltitle.setText("我要买");
				// CommonUtils.startImageLoader(cubeimageLoader,
				// item.getPoster(),
				// item_iv);
				item_iv.setImageResource(R.drawable.ic_public);
				break;
			}

			// convertView.setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// startAnimActivity2Obj(ActivityNewContent.class, "NEWS",
			// item);
			// }
			// });

			return convertView;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			mNews = dbUtil.findAll(Selector.from(News.class).orderBy(
					"created_time", true));
			if (mNews != null && mNews.size() > 0) {
				addData(mNews);
			} else {
				nodota.setVisibility(View.VISIBLE);
			}
		} catch (DbException e) {
			e.printStackTrace();
			nodota.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onBackPressed() {
		AnimFinsh();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mNewsRefreshBroadCast);
	}

	class NewsRefreshBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			try {
				mNews = dbUtil.findAll(Selector.from(News.class).orderBy(
						"created_time", true));
				if (mNews != null && mNews.size() > 0) {
					addData(mNews);
				} else {
					nodota.setVisibility(View.VISIBLE);
				}
			} catch (DbException e) {
				e.printStackTrace();
				nodota.setVisibility(View.VISIBLE);
			}
		}

	}

    private void showHintDiaLog(){
        updatHintState("help3",false);
        LayoutInflater inflater = LayoutInflater.from(this);
        View hintdialogView = inflater.inflate(R.layout.help_hint_dialog, null);
        ImageView iv_hint_help = (ImageView) hintdialogView.findViewById(R.id.iv_hint_help);
        iv_hint_help.setImageResource(R.drawable.img_help3);
        iv_hint_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog = new Dialog(this,R.style.hintDialog);
        // 设置它的ContentView
        dialog.setContentView(hintdialogView);
        Window dialogWindow = dialog.getWindow();
//        //设置在底部
//        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager m =getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        //设置对话框宽
        p.width = d.getWidth();
        p.height = d.getHeight();
        dialogWindow.setAttributes(p);
        dialog.show();
    }

    //获取帮助是否提示
    public boolean getHintState(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("benben", Context.MODE_WORLD_READABLE);
        if(sharedPreferences.contains(key)){
            return sharedPreferences.getBoolean(key, true);
        }else{
            return true;
        }
    }

    //修改帮助是否提示
    public void updatHintState(String key,boolean flag) {
        SharedPreferences sharedPreferences = getSharedPreferences("benben", Context.MODE_WORLD_READABLE);
        //创建数据编辑器
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        //传递需要保存的数据
        editor.putBoolean(key, flag);
        //保存数据
        editor.commit();
    }

}
