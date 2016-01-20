/***
 * 我的微创作
 **/
package com.xunao.test.ui.item;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageTask;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView.BufferType;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.test.R;
import com.xunao.test.base.BaseActivity;
import com.xunao.test.bean.MySmallCreationComment;
import com.xunao.test.bean.MySmallCreationData;
import com.xunao.test.bean.MySmallCreationList;
import com.xunao.test.bean.SmallMakeDataComment;
import com.xunao.test.bean.SmallSelfDataList;
import com.xunao.test.bean.SmallSelfMakeData;
import com.xunao.test.bean.SuccessMsg;
import com.xunao.test.bean.User;
import com.xunao.test.config.AndroidConfig;
import com.xunao.test.exception.NetRequestException;
import com.xunao.test.hx.chatuidemo.utils.SmileUtils;
import com.xunao.test.hx.chatuidemo.widget.PasteEditText;
import com.xunao.test.net.InteNetUtils;
import com.xunao.test.utils.CommonUtils;
import com.xunao.test.utils.PixelUtil;
import com.xunao.test.utils.TimeUtil;
import com.xunao.test.utils.ToastUtils;
import com.xunao.test.utils.ViewHolderUtil;
import com.xunao.test.utils.click.VoicelistMyPlayClickListener;
import com.xunao.test.utils.click.VoicelistSelfPlayClickListener;
import com.xunao.test.view.ActionSheet;
import com.xunao.test.view.ActionSheet.ActionSheetListener;
import com.xunao.test.view.MyTextView;
import com.xunao.test.view.NoScrollGridView;

public class ActivityMySmallMake extends BaseActivity implements
		OnClickListener {
	private RelativeLayout curTouchTab;
	private RelativeLayout prerecord_tab_one;
	private RelativeLayout prerecord_tab_three;
	private View[] but;
	private PullToRefreshListView listview_all;
	private PullToRefreshListView listview_myself;

	private boolean isLoadMoreALL;
	private boolean isLoadMoreSelf;
	private ArrayList<MySmallCreationData> mySmallCreationDatas;
	private ArrayList<SmallSelfMakeData> mSmallMakeDataSelf;
	MyAdapterAll mALLAdapter;
	MyAdapterSelf mSelfAdapter;
	private String curentID;
	private int curentPosition;
	private View bar_bottom;
	private LinearLayout ll_title;
	private PasteEditText mEditTextContent;
	private View v_vertical;
	private View buttonSend;
	private RelativeLayout edittext_layout;
	private LinearLayout emojiIconContainer;
	private InputMethodManager manager;
	private int maxheight = PixelUtil.dp2px(150);

	public String playMsgId;

	public static final int MYSELF = 1;
	public static final int ALL = 0;

	private boolean initAllLockData;
	private boolean initLockData;

	@Override
	protected void onPause() {
		super.onPause();
		stopCurVoice();
	}

	// 停止当前播放音频
	private void stopCurVoice() {
		if (VoicelistMyPlayClickListener.isPlaying
				&& VoicelistMyPlayClickListener.currentPlayListener != null) {
			// 停止语音播放
			VoicelistMyPlayClickListener.currentPlayListener.stopPlayVoice();
		}
		if (VoicelistSelfPlayClickListener.isPlaying
				&& VoicelistSelfPlayClickListener.currentPlayListener != null) {
			// 停止语音播放
			VoicelistSelfPlayClickListener.currentPlayListener.stopPlayVoice();
		}
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_small_make);

		cubeimageLoader.setImageLoadHandler(new DefaultImageLoadHandler(
				mContext) {
			@Override
			public void onLoading(ImageTask imageTask,
					CubeImageView cubeImageView) {

				Boolean ispost = (Boolean) cubeImageView
						.getTag(R.string.ispost);
				if (cubeImageView != null) {
					if (ispost != null && ispost) {
						cubeImageView.setImageResource(R.drawable.default_face);
					} else {
						cubeImageView.setImageResource(R.drawable.loading);
					}
				}

			}

			@Override
			public void onLoadFinish(ImageTask imageTask,
					CubeImageView cubeImageView, BitmapDrawable drawable) {
				if (cubeImageView != null
						&& imageTask.getIdentityUrl().equalsIgnoreCase(
								(String) cubeImageView.getTag())) {

					Boolean issuofang = (Boolean) cubeImageView
							.getTag(R.string.issuofang);

					String urlToFileFormat = CommonUtils
							.UrlToFileFormat(imageTask.getOriginUrl());
					if (issuofang != null && issuofang) {
						Bitmap bitmap = drawable.getBitmap();
						int width = bitmap.getWidth();
						int height = bitmap.getHeight();
						float scal = 1;
						if (width > height) {
							scal = (width * 1.0f / height * 1.0f);

							if (width > mScreenWidth - PixelUtil.dp2px(75)) {
								width = mScreenWidth - PixelUtil.dp2px(75);
								height = (int) (width / scal);
							} else if (height > maxheight) {
								height = maxheight;
								width = (int) (height * scal);
							} else {
								height = (int) (width / scal);
							}

						} else {
							scal = (height * 1.0f / width * 1.0f);

							if (width > mScreenWidth - PixelUtil.dp2px(75)) {
								width = mScreenWidth - PixelUtil.dp2px(75);
								height = (int) (width * scal);
							} else if (height > maxheight) {
								height = maxheight;
								width = (int) (height / scal);
							} else {
								width = (int) (height / scal);
							}

						}
						cubeImageView.getLayoutParams().width = width;
						cubeImageView.getLayoutParams().height = height;
					}

					cubeImageView.setVisibility(View.VISIBLE);
					cubeImageView.setImageDrawable(drawable);

				}
			}

			@Override
			public void onLoadError(ImageTask imageTask,
					CubeImageView imageView, int errorCode) {
				if (imageView != null) {
					Boolean ispost = (Boolean) imageView
							.getTag(R.string.ispost);
					if (ispost != null && ispost) {
						imageView.setImageResource(R.drawable.default_face);
					} else {
						imageView.setImageResource(R.drawable.error);
					}
				}
			}
		});
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		setShowLoding(false);
		initTitleView();
		TitleMode mode = new TitleMode("#068cd9", "", 0, null, "",
				R.drawable.ic_back, new OnClickListener() {
					@Override
					public void onClick(View v) {
						mContext.AnimFinsh();
					}
				}, "我的微创作", 0);
		chanageTitle(mode);

		but = new View[2];
		all_box = (RelativeLayout) findViewById(R.id.all_box);
		all_nodota = (RelativeLayout) findViewById(R.id.all_nodota);
		myself_box = (RelativeLayout) findViewById(R.id.myself_box);
		myself_nodota = (RelativeLayout) findViewById(R.id.myself_nodota);
		prerecord_tab_one = (RelativeLayout) findViewById(R.id.prerecord_tab_one);
		prerecord_tab_three = (RelativeLayout) findViewById(R.id.prerecord_tab_three);
		listview_all = (PullToRefreshListView) findViewById(R.id.listview_all);
		listview_myself = (PullToRefreshListView) findViewById(R.id.listview_myself);
		public_smallmake = (ImageView) findViewById(R.id.public_smallmake);
		v_vertical = findViewById(R.id.v_vertical);
		ll_title = (LinearLayout) findViewById(R.id.ll_title);
		but[0] = prerecord_tab_one;
		but[1] = prerecord_tab_three;

		mEditTextContent = (PasteEditText) findViewById(R.id.et_sendmessage);
		bar_bottom = findViewById(R.id.bar_bottom);
		edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
		emojiIconContainer = (LinearLayout) findViewById(R.id.ll_face_container);
		buttonSend = findViewById(R.id.btn_send);
		buttonSend.setOnClickListener(this);

		mEditTextContent.setHint("说点什么吧...");

		public_smallmake.setOnClickListener(this);
		prerecord_tab_one.setOnClickListener(this);
		prerecord_tab_three.setOnClickListener(this);
		prerecord_tab_one.performClick();
		curTouchTab = prerecord_tab_one;
		sethideSend(listview_myself);
		sethideSend(listview_all);

		// 隐藏头部
		ll_title.setVisibility(View.GONE);
		prerecord_tab_one.setVisibility(View.GONE);
		prerecord_tab_three.setVisibility(View.GONE);
		v_vertical.setVisibility(View.GONE);

		// 全部微创作刷新
		listview_all.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (CommonUtils.isNetworkAvailable(mContext)) {
					String time = TimeUtil.getTime(new Date());
					refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
							"最后更新:" + time);
					refreshData(ALL);
				} else {
					mHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							listview_all.onRefreshComplete();

						}
					}, 200);

				}
			}
		});
		// 全部微创作加载
		listview_all
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						if (mALLAdapter.isMoreData) {
							if (CommonUtils.isNetworkAvailable(mContext)) {
								loadMore(ALL);
							} else {
								mHandler.postDelayed(new Runnable() {

									@Override
									public void run() {
										listview_all.onRefreshComplete();

									}
								}, 200);
							}
						}
					}
				});

		// 我关注微创作刷新
		listview_myself.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (CommonUtils.isNetworkAvailable(mContext)) {
					String time = TimeUtil.getTime(new Date());
					refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
							"最后更新:" + time);
					refreshData(MYSELF);
				} else {
					mHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							listview_all.onRefreshComplete();

						}
					}, 200);

				}
			}
		});
		// 我关注微创作加载
		listview_myself
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						if (mSelfAdapter.isMoreData) {
							if (CommonUtils.isNetworkAvailable(mContext)) {
								loadMore(MYSELF);
							} else {
								mHandler.postDelayed(new Runnable() {

									@Override
									public void run() {
										listview_all.onRefreshComplete();

									}
								}, 200);
							}
						}
					}
				});

		initlockData(ALL);
		if (CommonUtils.isNetworkAvailable(mContext)) {

			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					listview_all.setRefreshing(true);
				}
			}, 200);

		} else {
			if (initAllLockData) {
				all_nodota.setVisibility(View.GONE);
			} else {
				all_nodota.setVisibility(View.VISIBLE);
			}

		}

	}

	private void sethideSend(PullToRefreshListView listview) {
		listview.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (bar_bottom.getVisibility() == View.VISIBLE) {
					bar_bottom.setVisibility(View.GONE);
					// mEditTextContent.setText("");
				}
				hideKeyboard();
				return false;
			}
		});
		listview.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (bar_bottom.getVisibility() == View.VISIBLE) {
					bar_bottom.setVisibility(View.GONE);
					// mEditTextContent.setText("");
				}
				hideKeyboard();
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
	}

	/***
	 * 根据参数刷新数据
	 * 
	 * @param type
	 */
	private void refreshData(int type) {
		switch (type) {
		case MYSELF:// 刷新我的关注
			isLoadMoreSelf = false;
			InteNetUtils.getInstance(mContext).getMyselfSmallMakeData("",
					requestCallBack);
			break;
		case ALL:// 刷新全部
			isLoadMoreALL = false;
			InteNetUtils.getInstance(mContext).getMySmallMakeData("",
					mRequestCallBack);
			break;
		}

	}

	/***
	 * 根据参数加载更多数据
	 * 
	 * @param type
	 */
	private void loadMore(int type) {
		switch (type) {
		case MYSELF:// 刷新我的关注
			isLoadMoreSelf = true;
			InteNetUtils.getInstance(mContext).getMyselfSmallMakeData(
					mSmallMakeDataSelf.get(mSmallMakeDataSelf.size() - 1)
							.getCreatedTime(), requestCallBack);
			break;
		case ALL:// 刷新全部
			isLoadMoreALL = true;
			InteNetUtils.getInstance(mContext).getMySmallMakeData(
					mySmallCreationDatas.get(mySmallCreationDatas.size() - 1)
							.getCreatedTime(), mRequestCallBack);
			break;
		}

	}

	/***
	 * 根据参数加载更多数据
	 * 
	 * @param type
	 */
	private void initlockData(int type) {
		switch (type) {
		case MYSELF:
			try {
				List<SmallSelfMakeData> findAll = dbUtil.findAll(Selector
						.from(SmallSelfMakeData.class));

				if (findAll != null && findAll.size() > 0) {
					mSmallMakeDataSelf = (ArrayList<SmallSelfMakeData>) findAll;
					mSelfAdapter = new MyAdapterSelf(MYSELF);
					listview_myself.setAdapter(mSelfAdapter);
					initLockData = true;
				} else {
					initLockData = false;
				}
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case ALL:
			try {
				List<MySmallCreationData> findAll = dbUtil.findAll(Selector
						.from(MySmallCreationData.class));

				if (findAll != null && findAll.size() > 0) {
					mySmallCreationDatas = ((ArrayList<MySmallCreationData>) findAll);
					mALLAdapter = new MyAdapterAll(ALL);
					listview_all.setAdapter(mALLAdapter);
					initAllLockData = true;
				} else {
					initAllLockData = false;
				}
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		}

	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		try {
			user = dbUtil.findFirst(User.class);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	// 全部创作
	@Override
	protected void onSuccess(JSONObject jsonObject) {
		listview_all.onRefreshComplete();
		MySmallCreationList dataList = new MySmallCreationList();
		try {
			dataList.parseJSON(jsonObject);
			ArrayList<MySmallCreationData> getmSmallMakeData = dataList
					.getmCreationDatas();

			all_nodota.setVisibility(View.GONE);
			addDataAll(getmSmallMakeData);
			saveData(getmSmallMakeData);
		} catch (NetRequestException e) {
			if (initAllLockData) {
				all_nodota.setVisibility(View.GONE);
			} else {
				all_nodota.setVisibility(View.VISIBLE);
			}
			e.getError().print(mContext);
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		listview_all.onRefreshComplete();
		ToastUtils.Errortoast(mContext, "网络不可用");
		if (initAllLockData) {
			all_nodota.setVisibility(View.GONE);
		} else {
			all_nodota.setVisibility(View.VISIBLE);
		}

	}

	// 关注创作
	RequestCallBack<String> requestCallBack = new RequestCallBack<String>() {

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {

			try {
				JSONObject jsonObject = new JSONObject(arg0.result);
				listview_myself.onRefreshComplete();
				SmallSelfDataList dataList = new SmallSelfDataList();
				try {
					dataList.parseJSON(jsonObject);
					ArrayList<SmallSelfMakeData> getmSmallMakeData = dataList
							.getmSmallMakeData();

					addDataSelf(getmSmallMakeData);
					saveSelfData(getmSmallMakeData);
				} catch (NetRequestException e) {
					if (initLockData) {
						myself_nodota.setVisibility(View.GONE);
					} else {
						myself_nodota.setVisibility(View.VISIBLE);
					}
					e.getError().print(mContext);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			listview_myself.onRefreshComplete();
			ToastUtils.Errortoast(mContext, "网络不可用");
			if (initLockData) {
				myself_nodota.setVisibility(View.GONE);
			} else {
				myself_nodota.setVisibility(View.VISIBLE);
			}
		}
	};
	private ImageView public_smallmake;
	private Drawable drawable;
	private RelativeLayout all_box;
	private RelativeLayout all_nodota;
	private RelativeLayout myself_box;
	private RelativeLayout myself_nodota;

	private void saveData(ArrayList<MySmallCreationData> getmSmallMakeData) {
//
//		try {
//			dbUtil.deleteAll(MySmallCreationData.class);
//			if (getmSmallMakeData != null && getmSmallMakeData.size() > 0) {
//				dbUtil.saveOrUpdateAll(getmSmallMakeData);
//				for (MySmallCreationData s : getmSmallMakeData) {
//					dbUtil.saveOrUpdateAll(s.getMySmallMakeCreationComments());
//				}
//			}
//
//		} catch (DbException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

	private void saveSelfData(ArrayList<SmallSelfMakeData> getmSmallMakeData) {

		try {
			dbUtil.deleteAll(SmallSelfMakeData.class);
			if (getmSmallMakeData != null && getmSmallMakeData.size() > 0) {
				dbUtil.saveOrUpdateAll(getmSmallMakeData);
				for (SmallSelfMakeData s : getmSmallMakeData) {
					dbUtil.saveOrUpdateAll(s.getmFriendDataComments());
				}
			}

		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void addDataSelf(ArrayList<SmallSelfMakeData> getmSmallMakeData) {
		if (!isLoadMoreSelf) {
			if (getmSmallMakeData != null && getmSmallMakeData.size() > 0) {
				myself_nodota.setVisibility(View.GONE);
				mSmallMakeDataSelf = getmSmallMakeData;

				if (mSelfAdapter == null) {
					mSelfAdapter = new MyAdapterSelf(MYSELF);
					listview_myself.setAdapter(mSelfAdapter);
				} else {
					if (mSmallMakeDataSelf.size() < AndroidConfig.DataNUM) {
						mSelfAdapter.isMoreData = false;
						mSelfAdapter.enterNum = false;
					} else {
						mSelfAdapter.isMoreData = true;
						mSelfAdapter.enterNum = true;
					}
					mSelfAdapter.notifyDataSetChanged();
				}
				if (mSmallMakeDataSelf.size() < AndroidConfig.DataNUM) {
					mSelfAdapter.isMoreData = false;
					mSelfAdapter.enterNum = false;
				} else {
					mSelfAdapter.isMoreData = true;
					mSelfAdapter.enterNum = true;
				}
			} else {
				listview_myself.setAdapter(null);
				if (mSmallMakeDataSelf != null)
					mSmallMakeDataSelf.clear();
				mSelfAdapter = null;
				myself_nodota.setVisibility(View.VISIBLE);
			}
		} else {
			if (getmSmallMakeData.size() < AndroidConfig.DataNUM) {
				mSelfAdapter.isMoreData = false;
			} else {
				mSelfAdapter.isMoreData = true;
			}
			mSelfAdapter.enterNum = true;
			mSmallMakeDataSelf.addAll(getmSmallMakeData);
			mSelfAdapter.notifyDataSetChanged();

		}

	}

	private void addDataAll(ArrayList<MySmallCreationData> getmSmallMakeData) {
		if (!isLoadMoreALL) {
			if (getmSmallMakeData != null && getmSmallMakeData.size() > 0) {
				mySmallCreationDatas = getmSmallMakeData;
				all_nodota.setVisibility(View.GONE);
				if (mALLAdapter == null) {
					mALLAdapter = new MyAdapterAll(ALL);

					listview_all.setAdapter(mALLAdapter);
				} else {
					mALLAdapter.notifyDataSetChanged();
					if (mySmallCreationDatas.size() < AndroidConfig.DataNUM) {
						mALLAdapter.isMoreData = false;
						mALLAdapter.enterNum = false;
					} else {
						mALLAdapter.isMoreData = true;
						mALLAdapter.enterNum = true;
					}
				}

				if (mySmallCreationDatas.size() < AndroidConfig.DataNUM) {
					mALLAdapter.isMoreData = false;
					mALLAdapter.enterNum = false;
				} else {
					mALLAdapter.isMoreData = true;
					mALLAdapter.enterNum = true;
				}
			} else {
				mALLAdapter = null;
				listview_all.setAdapter(null);
				if (mySmallCreationDatas != null)
					mySmallCreationDatas.clear();
				all_nodota.setVisibility(View.VISIBLE);
			}
		} else {
			if (getmSmallMakeData.size() < AndroidConfig.DataNUM) {
				mALLAdapter.isMoreData = false;
			} else {
				mALLAdapter.isMoreData = true;
			}
			mALLAdapter.enterNum = true;
			mySmallCreationDatas.addAll(getmSmallMakeData);
			mALLAdapter.notifyDataSetChanged();

		}

	}

	private int curType = ALL;
	private User user;

	@Override
	public void onClick(View v) {
		int id = v.getId();

		switch (id) {
		case R.id.prerecord_tab_one:// 广场
			if (bar_bottom.getVisibility() == View.VISIBLE) {
				bar_bottom.setVisibility(View.GONE);
				mEditTextContent.setText("");
			}
			curType = ALL;
			stopCurVoice();
			setChecked(prerecord_tab_one, false);
			setChecked(prerecord_tab_three, false);
			curTouchTab = prerecord_tab_one;
			setChecked(prerecord_tab_one, true);
			all_box.setVisibility(View.VISIBLE);
			myself_box.setVisibility(View.GONE);

			break;
		case R.id.prerecord_tab_three:// 关注
			if (bar_bottom.getVisibility() == View.VISIBLE) {
				bar_bottom.setVisibility(View.GONE);
				mEditTextContent.setText("");
			}
			curType = MYSELF;
			stopCurVoice();
			setChecked(prerecord_tab_one, false);
			setChecked(prerecord_tab_three, false);
			curTouchTab = prerecord_tab_three;
			setChecked(prerecord_tab_three, true);
			all_box.setVisibility(View.GONE);
			myself_box.setVisibility(View.VISIBLE);

			if (mSmallMakeDataSelf == null || mApplication.isAttenRefresh) {
				mApplication.isAttenRefresh = false;
				initlockData(MYSELF);
				if (CommonUtils.isNetworkAvailable(mContext)) {
					mHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							listview_myself.setRefreshing(true);
						}
					}, 200);

				} else {
					if (initLockData) {
						myself_nodota.setVisibility(View.GONE);
					} else {
						myself_nodota.setVisibility(View.VISIBLE);
					}

				}

			}

			break;
		case R.id.btn_send:// 发送评论
			String s = mEditTextContent.getText().toString();
			if (isReply) {
				s = "：" + s + " @" + member;
				isReply = false;
			}
			if (!CommonUtils.isEmpty(s)) {
				sendText(s, curentID, curType);
			} else {
				ToastUtils.Infotoast(mContext, "评论不可为空");
			}
			break;
		case R.id.public_smallmake:// 发布微创作
			startAnimActivity(ActivityWriteSmallMake.class);
			break;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mApplication.isAttenRefresh) {
			mApplication.isAttenRefresh = false;
			if (CommonUtils.isNetworkAvailable(mContext)) {
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						if (curType == ALL) {
							listview_all.setSelection(0);
							listview_all.setRefreshing(true);
						} else {
							listview_myself.setSelection(0);
							listview_myself.setRefreshing(true);
						}
					}
				}, 200);

			}

		}
	}

	// 发送评论
	private void sendText(final String s, String curentID, final int type) {
		if (CommonUtils.isNetworkAvailable(mContext)) {
			hideKeyboard();
			showLoding("请稍等...");
			InteNetUtils.getInstance(mContext).publicSmallComment(curentID, s,
					new RequestCallBack<String>() {

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							// 评论成功
							try {
								dissLoding();
								JSONObject jsonObject = new JSONObject(
										arg0.result);
								SuccessMsg msg = new SuccessMsg();
								try {
									msg.parseJSON(jsonObject);

									MySmallCreationComment comment = new MySmallCreationComment();
									comment.setReview(s);
									comment.setNick_name(user.getUserNickname());
									comment.setMemberId(user.getId() + "");
									mEditTextContent.setText("");
									switch (type) {
									case ALL:
										MySmallCreationData smallMakeData = mySmallCreationDatas
												.get(curentPosition);
										smallMakeData
												.getMySmallMakeCreationComments()
												.add(comment);
										mALLAdapter.notifyDataSetChanged();
										break;
									case MYSELF:
										SmallSelfMakeData smallSelfMakeData = mSmallMakeDataSelf
												.get(curentPosition);
										// smallSelfMakeData.getmFriendDataComments()
										// .add(comment);
										mSelfAdapter.notifyDataSetChanged();
										break;
									}
									if (bar_bottom.getVisibility() == View.VISIBLE)
										bar_bottom.setVisibility(View.GONE);

								} catch (NetRequestException e) {
									// TODO Auto-generated
									// catch block
									e.getError().print(mContext);
								}
							} catch (JSONException e) {
								dissLoding();
								ToastUtils.Errortoast(mContext, "网络不可用,请重试");
							}

						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							// 评论失败
							dissLoding();
							ToastUtils.Errortoast(mContext, "网络不可用,请重试");
						}
					});
		}

	}

	private void setChecked(RelativeLayout view, boolean isCheck) {
		RadioButton tab_RB = (RadioButton) view.findViewById(R.id.tab_RB);
		tab_RB.setChecked(isCheck);

	}

	private String member;
	private boolean isReply = false;

	private class MyAdapterAll extends BaseAdapter {

		private boolean isMoreData = true; // 是否有更多数据
		private boolean enterNum = true;
		private int type;

		public MyAdapterAll(int type) {
			super();
			this.type = type;
		}

		private ViewHolderTextAImg holderTextAImg;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mySmallCreationDatas.size() + 1;
		}

		@Override
		public MySmallCreationData getItem(int arg0) {
			// TODO Auto-generated method stub
			return mySmallCreationDatas.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public int getItemViewType(int position) {

			if (position >= (type == ALL ? mySmallCreationDatas.size()
					: mSmallMakeDataSelf.size())) {
				return 1;
			} else {
				return 0;

			}

		}

		@Override
		public View getView(final int position, View contentView, ViewGroup arg2) {

			int itemViewType = getItemViewType(position);
			if (contentView == null) {
				switch (itemViewType) {
				case 0:
					holderTextAImg = new ViewHolderTextAImg();
					contentView = View.inflate(mContext,
							R.layout.item_mysmallmake, null);

					holderTextAImg.comment_box = (LinearLayout) contentView
							.findViewById(R.id.comment_box);
					holderTextAImg.item_friend_voice_loding = contentView
							.findViewById(R.id.item_friend_voice_loding);
					holderTextAImg.item_friend_voice_error = contentView
							.findViewById(R.id.item_friend_voice_error);
					holderTextAImg.click_zan = (ImageView) contentView
							.findViewById(R.id.click_zan);
					holderTextAImg.item_more = (ImageView) contentView
							.findViewById(R.id.item_more);
					holderTextAImg.item_friend_looknum = (MyTextView) contentView
							.findViewById(R.id.item_friend_looknum);
					holderTextAImg.item_friend_content = (MyTextView) contentView
							.findViewById(R.id.item_friend_content);
					holderTextAImg.item_time = (MyTextView) contentView
							.findViewById(R.id.item_time);
					holderTextAImg.item_name = (MyTextView) contentView
							.findViewById(R.id.item_name);
					holderTextAImg.item_iv = (RoundedImageView) contentView
							.findViewById(R.id.item_iv);
					holderTextAImg.comment = (ImageView) contentView
							.findViewById(R.id.comment);
					holderTextAImg.item_friend_voice = (ImageView) contentView
							.findViewById(R.id.item_friend_voice);
					holderTextAImg.item_friend_voice_box = contentView
							.findViewById(R.id.item_friend_voice_box);
					holderTextAImg.item_friend_singleImg = (CubeImageView) contentView
							.findViewById(R.id.item_friend_singleImg);
					holderTextAImg.item_friend_gridView = (NoScrollGridView) contentView
							.findViewById(R.id.item_friend_gridView);

					holderTextAImg.rl_views = (RelativeLayout) contentView.findViewById(R.id.rl_views);
					holderTextAImg.laud_list = (MyTextView) contentView
							.findViewById(R.id.laud_list);

					contentView.setTag(holderTextAImg);

					break;
				}
			}
			
			String status = "";
			
			switch (itemViewType) {
			case 0:
				final MySmallCreationData item = getItem(position);
				holderTextAImg = (ViewHolderTextAImg) contentView.getTag();

				holderTextAImg.item_name.setText(item.getName());
				holderTextAImg.item_time.setText(TimeUtil
						.getDescriptionTimeFromTimestamp(Long.parseLong(item
								.getCreatedTime()) * 1000));
				holderTextAImg.item_friend_looknum.setText("浏览:"
						+ item.getViews() + "次");
				status = item.getStatus();
				if(item.getStatus().equals("1")){
					holderTextAImg.item_friend_content.setText("该帖子已被管理员屏蔽");
					holderTextAImg.rl_views.setVisibility(View.GONE);
					holderTextAImg.laud_list.setVisibility(View.GONE);
					holderTextAImg.comment_box.setVisibility(View.GONE);
					holderTextAImg.item_friend_voice_box.setVisibility(View.GONE);
					holderTextAImg.item_friend_gridView.setVisibility(View.GONE);
					holderTextAImg.item_friend_singleImg.setVisibility(View.GONE);
				}else{
					holderTextAImg.item_friend_content.setText(item
							.getDescription());
					holderTextAImg.rl_views.setVisibility(View.VISIBLE);
					holderTextAImg.laud_list.setVisibility(View.VISIBLE);
					holderTextAImg.comment_box.setVisibility(View.VISIBLE);
					holderTextAImg.item_friend_voice_box.setVisibility(View.VISIBLE);
					holderTextAImg.item_friend_gridView.setVisibility(View.VISIBLE);
					holderTextAImg.item_friend_singleImg.setVisibility(View.VISIBLE);
				}
				ArrayList<MySmallCreationComment> getmFriendDataComments = item
						.getMySmallMakeCreationComments();

				holderTextAImg.laud_list = (MyTextView) contentView
						.findViewById(R.id.laud_list);

				holderTextAImg.laud_list.setText(item.getLaudList());

				if (TextUtils.isEmpty(item.getLaudList())) {
					holderTextAImg.laud_list.setVisibility(View.GONE);
				} else {
					holderTextAImg.laud_list.setVisibility(View.VISIBLE);
				}

				if (getmFriendDataComments != null
						&& getmFriendDataComments.size() > 0) {
					holderTextAImg.comment_box.setVisibility(View.VISIBLE);
					holderTextAImg.comment_box.removeAllViews();
					LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT);

					for (final MySmallCreationComment fc : getmFriendDataComments) {
						MyTextView myTextView = new MyTextView(mContext);

						Spannable span = SmileUtils.getSmiledText(mContext,
								fc.getReview());

						String s = fc.getNick_name() + ": " + span;
						SpannableStringBuilder style = new SpannableStringBuilder(
								s);
						style.setSpan(
								new ForegroundColorSpan(Color
										.parseColor("#0e7bba")), 0, fc
										.getNick_name().length() + 2,
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						myTextView.setText(style, BufferType.SPANNABLE);
						myTextView.setLineSpacing(PixelUtil.dp2px(3), 1);
						if (holderTextAImg.comment_box.getChildCount() > 1) {
							params.topMargin = PixelUtil.dp2px(5);
						} else {
							params.topMargin = PixelUtil.dp2px(0);
						}
						holderTextAImg.comment_box.addView(myTextView, params);

						myTextView.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								bar_bottom.setVisibility(View.VISIBLE);
								if (!isReply
										|| item.getId() != Integer
												.parseInt(curentID)) {
									mEditTextContent.setHint("@"
											+ fc.getNick_name());
									mEditTextContent.setText("");
								}
								mEditTextContent.setHint("@"
										+ fc.getNick_name());
								member = fc.getNick_name();
								curentID = item.getId() + "";
								showKeyBoard();
								isReply = true;
							}
						});

					}

				} else {
					holderTextAImg.comment_box.setVisibility(View.GONE);
					mEditTextContent.setText("");
					mEditTextContent.setHint("说点什么吧...");
					isReply = false;

				}

				String poster = item.getPoster();
				if (!TextUtils.isEmpty(poster)) {
					holderTextAImg.item_iv.setTag(R.string.ispost, true);
					CommonUtils.startImageLoader(cubeimageLoader, poster,
							holderTextAImg.item_iv);
				} else {
					CommonUtils.startImageLoader(cubeimageLoader,
							"www.baidu.com", holderTextAImg.item_iv);
				}

				if (item.getLaud() == 1) {
					holderTextAImg.click_zan
							.setImageResource(R.drawable.ic_click_zaned);
				} else {
					holderTextAImg.click_zan
							.setImageResource(R.drawable.ic_click_zan);
				}

				holderTextAImg.item_more.setVisibility(View.VISIBLE);
				holderTextAImg.item_more
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// 删除
								showActionSheet(new ActionSheetListener() {
									@Override
									public void onOtherButtonClick(
											ActionSheet actionSheet, int index) {
										switch (0) {
										case 0:
											// 删除帖子
											InteNetUtils
													.getInstance(mContext)
													.deleteSmallMake(
															item.getId(),
															new RequestCallBack<String>() {

																@Override
																public void onSuccess(
																		ResponseInfo<String> arg0) {
																	try {
																		JSONObject jsonObject = new JSONObject(
																				arg0.result);
																		SuccessMsg msg = new SuccessMsg();
																		try {
																			msg.parseJSON(jsonObject);

																			mySmallCreationDatas
																					.remove(item);

																			mALLAdapter
																					.notifyDataSetChanged();
																		} catch (NetRequestException e) {
																			e.getError()
																					.print(mContext);
																		}
																	} catch (JSONException e) {
																		ToastUtils
																				.Errortoast(
																						mContext,
																						"删除帖子,请重试");
																	}

																}

																@Override
																public void onFailure(
																		HttpException arg0,
																		String arg1) {
																	ToastUtils
																			.Errortoast(
																					mContext,
																					"删除帖子,请重试");
																}
															});

											break;
										}
									}

									@Override
									public void onDismiss(
											ActionSheet actionSheet,
											boolean isCancel) {

									}
								});
							}
						});
				holderTextAImg.click_zan.setVisibility(View.GONE);
				holderTextAImg.click_zan
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (item.getLaud() == 1) {
									// 取消点赞
									InteNetUtils
											.getInstance(mContext)
											.cancelSmallClickGood(
													item.getId() + "",
													new RequestCallBack<String>() {

														@Override
														public void onSuccess(
																ResponseInfo<String> arg0) {
															// 点赞成功
															try {
																JSONObject jsonObject = new JSONObject(
																		arg0.result);
																SuccessMsg msg = new SuccessMsg();
																try {
																	msg.parseJSON(jsonObject);
																	item.setLaud(0);
																	item.setLaudList(jsonObject
																			.optString("msg"));
																	MyAdapterAll.this
																			.notifyDataSetChanged();

																} catch (NetRequestException e) {
																	e.getError()
																			.print(mContext);
																}
															} catch (JSONException e) {
																ToastUtils
																		.Errortoast(
																				mContext,
																				"点赞失败,请重试");
															}

														}

														@Override
														public void onFailure(
																HttpException arg0,
																String arg1) {
															// 点赞失败
															ToastUtils
																	.Errortoast(
																			mContext,
																			"点赞失败,请重试");
														}
													});

								} else {
									// 点赞
									InteNetUtils
											.getInstance(mContext)
											.clickSmallGood(
													item.getId() + "",
													new RequestCallBack<String>() {

														@Override
														public void onSuccess(
																ResponseInfo<String> arg0) {
															// 点赞成功
															try {
																JSONObject jsonObject = new JSONObject(
																		arg0.result);
																SuccessMsg msg = new SuccessMsg();
																try {
																	msg.parseJSON(jsonObject);
																	item.setLaud(1);
																	item.setLaudList(jsonObject
																			.optString("msg"));
																	MyAdapterAll.this
																			.notifyDataSetChanged();
																} catch (NetRequestException e) {
																	// TODO
																	// Auto-generated
																	// catch
																	// block
																	e.getError()
																			.print(mContext);
																}
															} catch (JSONException e) {
																ToastUtils
																		.Errortoast(
																				mContext,
																				"取消失败,请重试");
															}

														}

														@Override
														public void onFailure(
																HttpException arg0,
																String arg1) {
															// 点赞失败
															ToastUtils
																	.Errortoast(
																			mContext,
																			"取消失败,请重试");
														}
													});
								}
							}
						});

				holderTextAImg.comment
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (isReply) {
									mEditTextContent.setText("");
								}
								curentID = item.getId() + "";
								curentPosition = position;
								bar_bottom.setVisibility(View.VISIBLE);
								mEditTextContent.setHint("说点什么吧...");
								showKeyBoard();
								isReply = false;
							}
						});
				holderTextAImg.item_friend_singleImg.setVisibility(View.GONE);
				holderTextAImg.item_friend_gridView.setVisibility(View.GONE);
				holderTextAImg.item_friend_voice_box.setVisibility(View.GONE);
				String images = item.getThumb();
				if (item.getType() == 0) {// 图文
					if (!TextUtils.isEmpty(images)) {
						String[] split = images.split("\\^");
						int length = split.length;
						if (length > 1) {
							// 多图用GridView
							holderTextAImg.item_friend_gridView
									.setVisibility(View.VISIBLE);
							MyGridViewAdapter adapter = new MyGridViewAdapter(
									split);
							holderTextAImg.item_friend_gridView
									.setAdapter(adapter);
							holderTextAImg.item_friend_gridView
									.setOnItemClickListener(new OnItemClickListener() {

										@Override
										public void onItemClick(
												AdapterView<?> arg0, View arg1,
												int arg2, long arg3) {
											startActivity2StringAndPosition(
													ActivityContentPicSet.class,
													"IMAGES", item.getImages(),
													arg2);
										}
									});
						} else {
							// 单图
							holderTextAImg.item_friend_singleImg
									.setVisibility(View.VISIBLE);

							holderTextAImg.item_friend_singleImg.setTag(
									R.string.issuofang, true);

							CommonUtils.startImageLoader(cubeimageLoader,
									split[0],
									holderTextAImg.item_friend_singleImg);
							holderTextAImg.item_friend_singleImg
									.getLayoutParams().width = item
									.getSingImageW();
							holderTextAImg.item_friend_singleImg
									.getLayoutParams().height = item
									.getSingImageH();

							holderTextAImg.item_friend_singleImg
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											startActivity2StringAndPosition(
													ActivityContentPicSet.class,
													"IMAGES", item.getImages(),
													0);
										}
									});

						}
					}
				} else {// 音频
					holderTextAImg.item_friend_voice_box
							.setVisibility(View.VISIBLE);
					if (images != null) {

						AnimationDrawable voiceAnimation = (AnimationDrawable) holderTextAImg.item_friend_voice
								.getDrawable();
						voiceAnimation.stop();
						voiceAnimation.selectDrawable(0);

						holderTextAImg.item_friend_voice_box
								.setOnClickListener(new VoicelistMyPlayClickListener(
										item,
										holderTextAImg.item_friend_voice,
										holderTextAImg.item_friend_voice_error,
										holderTextAImg.item_friend_voice_loding,
										mContext));
					}
				}

				break;
			case 1:
				if (isMoreData) {
					contentView = getLayoutInflater().inflate(
							R.layout.item_load_more, null);
					LinearLayout load_more = ViewHolderUtil.get(contentView,
							R.id.load_more);
					if (enterNum) {
						load_more.setVisibility(View.VISIBLE);
					} else {
						load_more.setVisibility(View.GONE);
					}
				} else {
					contentView = getLayoutInflater().inflate(
							R.layout.item_no_load_more, null);
				}
				break;
			}

			if(status.equals("1")){
//				holderTextAImg.item_friend_content.setText("该帖子已被管理员屏蔽");
//				holderTextAImg.rl_views.setVisibility(View.GONE);
//				holderTextAImg.laud_list.setVisibility(View.GONE);
//				holderTextAImg.comment_box.setVisibility(View.GONE);
//				holderTextAImg.item_friend_voice_box.setVisibility(View.GONE);
				holderTextAImg.item_friend_gridView.setVisibility(View.GONE);
				holderTextAImg.item_friend_singleImg.setVisibility(View.GONE);
				holderTextAImg.item_friend_voice_box.setVisibility(View.GONE);
			}
			
			
			return contentView;
		}
	}

	private class MyAdapterSelf extends BaseAdapter {

		private boolean isMoreData = true; // 是否有更多数据
		private boolean enterNum = true;
		private int type;

		public MyAdapterSelf(int type) {
			super();
			this.type = type;
		}

		private ViewHolderTextAImg holderTextAImg;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mSmallMakeDataSelf.size() + 1;
		}

		@Override
		public SmallSelfMakeData getItem(int arg0) {
			// TODO Auto-generated method stub
			return mSmallMakeDataSelf.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public int getItemViewType(int position) {

			if (position >= (type == ALL ? mySmallCreationDatas.size()
					: mSmallMakeDataSelf.size())) {
				return 1;
			} else {
				return 0;

			}

		}

		@Override
		public View getView(final int position, View contentView, ViewGroup arg2) {

			int itemViewType = getItemViewType(position);
			if (contentView == null) {
				switch (itemViewType) {
				case 0:
					holderTextAImg = new ViewHolderTextAImg();
					contentView = View.inflate(mContext,
							R.layout.item_smallmake, null);

					holderTextAImg.comment_box = (LinearLayout) contentView
							.findViewById(R.id.comment_box);
					holderTextAImg.item_friend_voice_loding = contentView
							.findViewById(R.id.item_friend_voice_loding);
					holderTextAImg.item_friend_voice_error = contentView
							.findViewById(R.id.item_friend_voice_error);
					holderTextAImg.click_zan = (ImageView) contentView
							.findViewById(R.id.click_zan);
					holderTextAImg.item_friend_looknum = (MyTextView) contentView
							.findViewById(R.id.item_friend_looknum);
					holderTextAImg.item_friend_content = (MyTextView) contentView
							.findViewById(R.id.item_friend_content);
					holderTextAImg.item_time = (MyTextView) contentView
							.findViewById(R.id.item_time);
					holderTextAImg.item_name = (MyTextView) contentView
							.findViewById(R.id.item_name);
					holderTextAImg.item_iv = (RoundedImageView) contentView
							.findViewById(R.id.item_iv);
					holderTextAImg.comment = (ImageView) contentView
							.findViewById(R.id.comment);
					holderTextAImg.item_friend_voice = (ImageView) contentView
							.findViewById(R.id.item_friend_voice);
					holderTextAImg.item_friend_voice_box = contentView
							.findViewById(R.id.item_friend_voice_box);
					holderTextAImg.item_friend_singleImg = (CubeImageView) contentView
							.findViewById(R.id.item_friend_singleImg);
					holderTextAImg.item_friend_gridView = (NoScrollGridView) contentView
							.findViewById(R.id.item_friend_gridView);

					contentView.setTag(holderTextAImg);

					break;
				}
			}
			switch (itemViewType) {
			case 0:
				final SmallSelfMakeData item = getItem(position);
				holderTextAImg = (ViewHolderTextAImg) contentView.getTag();

				holderTextAImg.item_name.setText(item.getName());
				holderTextAImg.item_time.setText(TimeUtil
						.getDescriptionTimeFromTimestamp(Long.parseLong(item
								.getCreatedTime()) * 1000));
				holderTextAImg.item_friend_looknum.setText("浏览:"
						+ item.getViews() + "次");
//				holderTextAImg.item_friend_content.setText(item
//						.getDescription());
				
				if(item.getStatus().equals("1")){
					holderTextAImg.item_friend_content.setText("该帖子已被管理员屏蔽");
				}else{
					holderTextAImg.item_friend_content.setText(item
							.getDescription());
				}
				ArrayList<SmallMakeDataComment> getmFriendDataComments = item
						.getmFriendDataComments();

				if (getmFriendDataComments != null
						&& getmFriendDataComments.size() > 0) {
					holderTextAImg.comment_box.setVisibility(View.VISIBLE);
					holderTextAImg.comment_box.removeAllViews();
					LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT);

					for (SmallMakeDataComment fc : getmFriendDataComments) {
						MyTextView myTextView = new MyTextView(mContext);

						Spannable span = SmileUtils.getSmiledText(mContext,
								fc.getReview());

						String s = fc.getNick_name() + ": " + span;
						SpannableStringBuilder style = new SpannableStringBuilder(
								s);
						style.setSpan(
								new ForegroundColorSpan(Color
										.parseColor("#0e7bba")), 0, fc
										.getNick_name().length() + 2,
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						myTextView.setText(style, BufferType.SPANNABLE);
						myTextView.setLineSpacing(PixelUtil.dp2px(3), 1);
						if (holderTextAImg.comment_box.getChildCount() > 1) {
							params.topMargin = PixelUtil.dp2px(5);
						} else {
							params.topMargin = PixelUtil.dp2px(0);
						}
						holderTextAImg.comment_box.addView(myTextView, params);

					}

				} else {
					holderTextAImg.comment_box.setVisibility(View.GONE);
				}

				if (!TextUtils.isEmpty(item.getPoster())) {
					holderTextAImg.item_iv.setTag(R.string.ispost, true);
					CommonUtils.startImageLoader(cubeimageLoader,
							item.getPoster(), holderTextAImg.item_iv);
				} else {
					holderTextAImg.item_iv.setTag(R.string.ispost, true);
					CommonUtils.startImageLoader(cubeimageLoader,
							"www.baidu.com",
							holderTextAImg.item_friend_singleImg);
				}

				if (item.getLaud() == 1) {
					holderTextAImg.click_zan
							.setImageResource(R.drawable.ic_click_zaned);
				} else {
					holderTextAImg.click_zan
							.setImageResource(R.drawable.ic_click_zan);
				}

				holderTextAImg.click_zan.setVisibility(View.GONE);
				holderTextAImg.click_zan
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (item.getLaud() == 1) {
									// 取消点赞
									InteNetUtils
											.getInstance(mContext)
											.cancelSmallClickGood(
													item.getId() + "",
													new RequestCallBack<String>() {

														@Override
														public void onSuccess(
																ResponseInfo<String> arg0) {
															// 点赞成功
															try {
																JSONObject jsonObject = new JSONObject(
																		arg0.result);
																SuccessMsg msg = new SuccessMsg();
																try {
																	msg.parseJSON(jsonObject);
																	item.setLaud(0);
																	MyAdapterSelf.this
																			.notifyDataSetChanged();

																} catch (NetRequestException e) {
																	e.getError()
																			.print(mContext);
																}
															} catch (JSONException e) {
																ToastUtils
																		.Errortoast(
																				mContext,
																				"点赞失败,请重试");
															}

														}

														@Override
														public void onFailure(
																HttpException arg0,
																String arg1) {
															// 点赞失败
															ToastUtils
																	.Errortoast(
																			mContext,
																			"点赞失败,请重试");
														}
													});

								} else {
									// 点赞
									InteNetUtils
											.getInstance(mContext)
											.clickSmallGood(
													item.getId() + "",
													new RequestCallBack<String>() {

														@Override
														public void onSuccess(
																ResponseInfo<String> arg0) {
															// 点赞成功
															try {
																JSONObject jsonObject = new JSONObject(
																		arg0.result);
																SuccessMsg msg = new SuccessMsg();
																try {
																	msg.parseJSON(jsonObject);
																	item.setLaud(1);
																	MyAdapterSelf.this
																			.notifyDataSetChanged();
																} catch (NetRequestException e) {
																	// TODO
																	// Auto-generated
																	// catch
																	// block
																	e.getError()
																			.print(mContext);
																}
															} catch (JSONException e) {
																ToastUtils
																		.Errortoast(
																				mContext,
																				"取消失败,请重试");
															}

														}

														@Override
														public void onFailure(
																HttpException arg0,
																String arg1) {
															// 点赞失败
															ToastUtils
																	.Errortoast(
																			mContext,
																			"取消失败,请重试");
														}
													});
								}
							}
						});

				holderTextAImg.comment
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								curentID = item.getId() + "";
								curentPosition = position;
								bar_bottom.setVisibility(View.VISIBLE);
							}
						});
				holderTextAImg.item_friend_singleImg.setVisibility(View.GONE);
				holderTextAImg.item_friend_gridView.setVisibility(View.GONE);
				holderTextAImg.item_friend_voice_box.setVisibility(View.GONE);
				String images = item.getImages();
				if (item.getType() == 0) {// 图文
					if (!TextUtils.isEmpty(images)) {
						String[] split = images.split("\\^");
						int length = split.length;
						if (length > 1) {
							// 多图用GridView
							holderTextAImg.item_friend_gridView
									.setVisibility(View.VISIBLE);
							MyGridViewAdapter adapter = new MyGridViewAdapter(
									split);
							holderTextAImg.item_friend_gridView
									.setAdapter(adapter);
							holderTextAImg.item_friend_gridView
									.setOnItemClickListener(new OnItemClickListener() {

										@Override
										public void onItemClick(
												AdapterView<?> arg0, View arg1,
												int arg2, long arg3) {
											// startActivity2StringAndPosition(
											// ActivityContentPicSet.class,
											// "IMAGES", item.getImages(),
											// arg2);
										}
									});
						} else {
							// 单图
							holderTextAImg.item_friend_singleImg
									.setVisibility(View.VISIBLE);

							holderTextAImg.item_friend_singleImg.setTag(
									R.string.issuofang, true);

							if (!TextUtils.isEmpty(split[0])) {
								CommonUtils.startImageLoader(cubeimageLoader,
										split[0],
										holderTextAImg.item_friend_singleImg);
							} else {
								CommonUtils.startImageLoader(cubeimageLoader,
										"www.baidu.com",
										holderTextAImg.item_friend_singleImg);
							}

							holderTextAImg.item_friend_singleImg
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											// startActivity2StringAndPosition(
											// ActivityContentPicSet.class,
											// "IMAGES", item.getImages(), 0);
										}
									});

						}
					}
				} else {// 音频
					holderTextAImg.item_friend_voice_box
							.setVisibility(View.VISIBLE);
					if (images != null) {
						holderTextAImg.item_friend_voice_box
								.setOnClickListener(new VoicelistSelfPlayClickListener(
										item,
										holderTextAImg.item_friend_voice,
										holderTextAImg.item_friend_voice_error,
										holderTextAImg.item_friend_voice_loding,
										mContext));
					}
				}

				break;
			case 1:
				if (this.isMoreData) {
					contentView = getLayoutInflater().inflate(
							R.layout.item_load_more, null);
					LinearLayout load_more = ViewHolderUtil.get(contentView,
							R.id.load_more);
					if (this.enterNum) {
						load_more.setVisibility(View.VISIBLE);
					} else {
						load_more.setVisibility(View.GONE);
					}
				} else {
					contentView = getLayoutInflater().inflate(
							R.layout.item_no_load_more, null);
				}
				break;
			}

			return contentView;
		}
	}

	class ViewHolderTextAImg {
		View item_friend_voice_error;
		View item_friend_voice_loding;
		LinearLayout comment_box;
		ImageView click_zan;
		MyTextView item_friend_looknum;
		MyTextView item_friend_content;
		MyTextView item_time;
		MyTextView item_name;
		RoundedImageView item_iv;
		ImageView item_more;
		ImageView comment;
		CubeImageView item_friend_singleImg;
		NoScrollGridView item_friend_gridView;
		RelativeLayout rl_views;
		View item_friend_voice_box;
		ImageView item_friend_voice;
		MyTextView laud_list;
	}

	private class MyGridViewAdapter extends BaseAdapter {

		String[] images;

		public MyGridViewAdapter(String[] images) {
			super();
			this.images = images;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return images.length;
		}

		@Override
		public String getItem(int position) {
			// TODO Auto-generated method stub
			return images[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(mContext,
						R.layout.item_friend_gridview, null);
			}

			convertView
					.setLayoutParams(new AbsListView.LayoutParams(
							AbsListView.LayoutParams.MATCH_PARENT, PixelUtil
									.dp2px(60)));

			if (!TextUtils.isEmpty(getItem(position))) {
				CommonUtils.startImageLoader(cubeimageLoader,
						getItem(position), ((CubeImageView) convertView));
			} else {
				CommonUtils.startImageLoader(cubeimageLoader, "www.baidu.com",
						((CubeImageView) convertView));
			}

			return convertView;
		}

	}

	public void showActionSheet(ActionSheetListener clickListener) {
		setTheme(R.style.ActionSheetStyleIOS7);
		ActionSheet.createBuilder(this, getSupportFragmentManager())
				.setCancelButtonTitle("取消")
				.setOtherButtonTitles("删除帖子")
				// 设置颜色 必须一一对应
				.setOtherButtonTitlesColor("#1E82FF", "#1E82FF")
				.setCancelableOnTouchOutside(true).setListener(clickListener)
				.show();
	}

	public void startActivity2StringAndPosition(Class<?> cla, String key,
			String value, int position) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, value);
		intent.putExtra("POSITION", position);
		this.startActivity(intent);
	}

	private void showKeyBoard() {
		// mEditTextContent.setFocusable(true);
		mEditTextContent.requestFocus();
		InputMethodManager m = (InputMethodManager) mEditTextContent
				.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		// mEditTextContent.setFocusableInTouchMode(true);
	}

}
