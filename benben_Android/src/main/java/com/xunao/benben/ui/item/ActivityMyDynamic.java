package com.xunao.benben.ui.item;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageTask;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.BufferType;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

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
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.FriendData;
import com.xunao.benben.bean.FriendDataComment;
import com.xunao.benben.bean.FriendDataList;
import com.xunao.benben.bean.MyDynamic;
import com.xunao.benben.bean.MyDynamicDataComment;
import com.xunao.benben.bean.MyDynamicDataList;
import com.xunao.benben.bean.SmallMakeData;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.bean.User;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.BuyDialog.onSuccessLinstener;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.hx.chatuidemo.activity.ChatActivity;
import com.xunao.benben.hx.chatuidemo.adapter.ExpressionAdapter;
import com.xunao.benben.hx.chatuidemo.adapter.ExpressionPagerAdapter;
import com.xunao.benben.hx.chatuidemo.utils.SmileUtils;
import com.xunao.benben.hx.chatuidemo.widget.ExpandGridView;
import com.xunao.benben.hx.chatuidemo.widget.PasteEditText;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.TimeUtil;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.utils.ViewHolderUtil;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.ContainsEmojiEditText;
import com.xunao.benben.view.MyTextView;
import com.xunao.benben.view.NoScrollGridView;
import com.xunao.benben.view.ActionSheet.ActionSheetListener;

public class ActivityMyDynamic extends BaseActivity implements OnClickListener,
		OnRefreshListener<ListView>, OnLastItemVisibleListener {

	private boolean isLoadMore;
	private String lastTime;
	private PullToRefreshListView listview;
	ArrayList<MyDynamic> myDynamics;
	private MyAdapter mFriendAdapter;
	private ContainsEmojiEditText mEditTextContent;
	private List<String> reslist;
	private ViewPager expressionViewpager;
	private View bar_bottom;
	private View more;
	// private ImageView iv_emoticons_normal;
	// private ImageView iv_emoticons_checked;
	private RelativeLayout edittext_layout;
	private LinearLayout emojiIconContainer;
	private View buttonSend;
	private InputMethodManager manager;
	private String curentID;
    private String replier;
	private int curentPosition;
	private int maxheight = PixelUtil.dp2px(150);
	private RelativeLayout nodota;

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);

		if (arg1 == AndroidConfig.writeFriendRefreshResultCode) {
			if (CommonUtils.isNetworkAvailable(mContext)) {
				listview.setSelection(0);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						listview.setRefreshing(true);
					}
				}, 200);
			}
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
						imageView.setImageResource(R.drawable.default_face);
					} else {
						imageView.setImageResource(R.drawable.error);
					}
				}
			}
		});
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_friend);

		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitleView();
		TitleMode mode = new TitleMode("#068cd9", "",
				R.drawable.ic_writefriend, new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 写朋友圈
						startAnimActivityForResult(ActivityWriteFriend.class,
								AndroidConfig.writeFriendRequestCode);

					}
				}, "", R.drawable.ic_back, new OnClickListener() {

					@Override
					public void onClick(View v) {

						mContext.AnimFinsh();
					}
				}, "我的朋友圈", 0);
		chanageTitle(mode);
		nodota = (RelativeLayout) findViewById(R.id.nodota);
		mEditTextContent = (ContainsEmojiEditText) findViewById(R.id.et_sendmessage);
		bar_bottom = findViewById(R.id.bar_bottom);
		edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
		emojiIconContainer = (LinearLayout) findViewById(R.id.ll_face_container);
		buttonSend = findViewById(R.id.btn_send);
		// iv_emoticons_normal = (ImageView)
		// findViewById(R.id.iv_emoticons_normal);
		// iv_emoticons_checked = (ImageView)
		// findViewById(R.id.iv_emoticons_checked);
		expressionViewpager = (ViewPager) findViewById(R.id.vPager);
		// iv_emoticons_normal.setVisibility(View.VISIBLE);
		// iv_emoticons_checked.setVisibility(View.INVISIBLE);

		// iv_emoticons_normal.setOnClickListener(this);
		// iv_emoticons_checked.setOnClickListener(this);

		mEditTextContent.setHint("说点什么吧...");
		buttonSend.setOnClickListener(this);

		more = findViewById(R.id.more);
		// 表情list
		reslist = getExpressionRes(35);
		// 初始化表情viewpager
		List<View> views = new ArrayList<View>();
		View gv1 = getGridChildView(1);
		View gv2 = getGridChildView(2);
		views.add(gv1);
		views.add(gv2);
		expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));

		mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// if (hasFocus) {
				// edittext_layout
				// .setBackgroundResource(R.drawable.input_bar_bg_active);
				// } else {
				// edittext_layout
				// .setBackgroundResource(R.drawable.input_bar_bg_normal);
				// }

			}
		});
		mEditTextContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// edittext_layout
				// .setBackgroundResource(R.drawable.input_bar_bg_active);
				// more.setVisibility(View.GONE);
				// iv_emoticons_normal.setVisibility(View.VISIBLE);
				// iv_emoticons_checked.setVisibility(View.INVISIBLE);
				emojiIconContainer.setVisibility(View.GONE);
			}
		});

		listview = (PullToRefreshListView) findViewById(R.id.listview);
		listview.setOnRefreshListener(this);
		listview.setOnLastItemVisibleListener(this);

		sethideSend(listview);

		initlockData();

		boolean networkAvailable = CommonUtils.isNetworkAvailable(mContext);
		if (networkAvailable) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					listview.setRefreshing(true);
				}
			}, 200);
		} else {
			if (isloadLock) {
				nodota.setVisibility(View.GONE);
			} else {
				nodota.setVisibility(View.VISIBLE);
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

	public List<String> getExpressionRes(int getSum) {
		List<String> reslist = new ArrayList<String>();
		for (int x = 1; x <= getSum; x++) {
			String filename = "ee_" + x;

			reslist.add(filename);

		}
		return reslist;

	}

	private void refreshData() {
		setShowLoding(false);
		isLoadMore = false;
		isMoreData = true;
		enterNum = false;
		lastTime = "0";
		InteNetUtils.getInstance(mContext).getMyDynamicData(lastTime,
				mRequestCallBack);
	}

	private void loadMoreData() {
		setShowLoding(false);
		isLoadMore = true;
		lastTime = myDynamics.get(myDynamics.size() - 1).getCreatedTime();
		InteNetUtils.getInstance(mContext).getMyDynamicData(lastTime,
				mRequestCallBack);
	}

	private void initlockData() {
		try {
			myDynamics = (ArrayList<MyDynamic>) dbUtil.findAll(MyDynamic.class);
			if (myDynamics != null && myDynamics.size() > 0) {
				isloadLock = true;

				for (MyDynamic f : myDynamics) {
					List<MyDynamicDataComment> findAll = dbUtil
							.findAll(Selector.from(MyDynamicDataComment.class)
									.where("circleId", "=", f.getId()));
					f.setmFriendDataComments((ArrayList) findAll);
				}

				if (myDynamics.size() < AndroidConfig.DataNUM) {
					isMoreData = false;
					enterNum = false;
				} else {
					isMoreData = true;
					enterNum = true;
				}

				mFriendAdapter = new MyAdapter();
				listview.setAdapter(mFriendAdapter);

			}

		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void saveData(ArrayList<MyDynamic> mFriendDatas) {
		try {
			dbUtil.deleteAll(FriendData.class);
			dbUtil.deleteAll(FriendDataComment.class);
			if (mFriendDatas != null) {
				dbUtil.saveAll(mFriendDatas);
				for (MyDynamic f : mFriendDatas) {
					dbUtil.saveAll(f.getmFriendDataComments());
				}
			}

		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void initDate(Bundle savedInstanceState) {

	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {

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

		listview.onRefreshComplete();
		MyDynamicDataList dataList = new MyDynamicDataList();
		try {
			dataList.parseJSON(jsonObject);
			ArrayList<MyDynamic> getmFriendDatas = dataList.getMyDynamics();
			addData(getmFriendDatas);
			saveData(getmFriendDatas);

		} catch (NetRequestException e) {
			e.getError().print(mContext);
			if (isloadLock) {
				nodota.setVisibility(View.GONE);
			} else {
				nodota.setVisibility(View.VISIBLE);
			}
		}

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		listview.onRefreshComplete();
		ToastUtils.Errortoast(mContext, "网络不可用");
		if (isloadLock) {
			nodota.setVisibility(View.GONE);
		} else {
			nodota.setVisibility(View.VISIBLE);
		}
	}

	private void addData(ArrayList<MyDynamic> getmFriendDatas) {

		if (!isLoadMore) {
			if (getmFriendDatas != null && getmFriendDatas.size() > 0) {
				myDynamics = getmFriendDatas;
				nodota.setVisibility(View.GONE);
				if (myDynamics.size() < AndroidConfig.DataNUM) {
					isMoreData = false;
					enterNum = false;
				} else {
					isMoreData = true;
					enterNum = true;
				}
				if (mFriendAdapter == null) {
					mFriendAdapter = new MyAdapter();
					listview.setAdapter(mFriendAdapter);
				} else {
					mFriendAdapter.notifyDataSetChanged();
				}
			} else {
				mFriendAdapter = null;
				listview.setAdapter(null);

				if (myDynamics != null) {
					myDynamics.clear();
				}
				nodota.setVisibility(View.VISIBLE);
			}
		} else {

			if (getmFriendDatas.size() < AndroidConfig.DataNUM) {
				isMoreData = false;
			} else {
				isMoreData = true;
			}
			enterNum = true;
			myDynamics.addAll(getmFriendDatas);
			mFriendAdapter.notifyDataSetChanged();

		}

	}

	private String member;
	private boolean isReply = false;

	private class MyAdapter extends BaseAdapter {

		private ViewHolderTextAImg holderTextAImg;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return myDynamics.size() + 1;
		}

		@Override
		public MyDynamic getItem(int arg0) {
			// TODO Auto-generated method stub
			return myDynamics.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 3;
		}

		@Override
		public int getItemViewType(int position) {

			if (position >= myDynamics.size()) {
				return 2;
			}
			return 0;
		}

		@Override
		public View getView(final int position, View contentView, ViewGroup arg2) {

			int itemViewType = getItemViewType(position);
			if (contentView == null) {
				switch (itemViewType) {
				case 0:
					holderTextAImg = new ViewHolderTextAImg();
					contentView = View.inflate(mContext,
							R.layout.item_myfriend_textimg, null);

					holderTextAImg.comment_box = (LinearLayout) contentView
							.findViewById(R.id.comment_box);
					holderTextAImg.item_more = (ImageView) contentView
							.findViewById(R.id.item_more);
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
					holderTextAImg.item_friend_singleImg = (CubeImageView) contentView
							.findViewById(R.id.item_friend_singleImg);
					holderTextAImg.item_friend_gridView = (NoScrollGridView) contentView
							.findViewById(R.id.item_friend_gridView);
					holderTextAImg.laud_list = (MyTextView) contentView
							.findViewById(R.id.laud_list);
					holderTextAImg.rl_views = (RelativeLayout) contentView.findViewById(R.id.rl_views);

					contentView.setTag(holderTextAImg);

					break;
				case 1:
					break;
				}
			}
			String status = "";
			switch (itemViewType) {
			case 0:
				final MyDynamic item = getItem(position);
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
					holderTextAImg.item_friend_gridView.setVisibility(View.GONE);
				}else{
					holderTextAImg.item_friend_content.setText(item
							.getDescription());
					holderTextAImg.rl_views.setVisibility(View.VISIBLE);
					holderTextAImg.laud_list.setVisibility(View.VISIBLE);
					holderTextAImg.comment_box.setVisibility(View.VISIBLE);
					holderTextAImg.item_friend_gridView.setVisibility(View.VISIBLE);
				}

				holderTextAImg.laud_list.setText(item.getLaudList());

				holderTextAImg.item_more
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								showActionSheet(new ActionSheetListener() {

									@Override
									public void onOtherButtonClick(
											ActionSheet actionSheet, int index) {

										switch (index) {
										case 0:// 删除帖子

											InteNetUtils
													.getInstance(mContext)
													.deleteFriend(
															item.getId(),
															new RequestCallBack<String>() {

																@Override
																public void onFailure(
																		HttpException arg0,
																		String arg1) {
																	ToastUtils
																			.Errortoast(
																					mContext,
																					"删除失败,请重试");
																}

																@Override
																public void onSuccess(
																		ResponseInfo<String> arg0) {
																	try {
																		JSONObject jsonObject = new JSONObject(
																				arg0.result);
																		SuccessMsg msg = new SuccessMsg();
																		try {
																			msg.parseJSON(jsonObject);
																			mApplication.isAttenRefresh = true;

																			myDynamics
																					.remove(item);

																			mFriendAdapter
																					.notifyDataSetChanged();

																		} catch (NetRequestException e) {
																			e.getError()
																					.print(mContext);
																		}
																	} catch (JSONException e) {
																		ToastUtils
																				.Errortoast(
																						mContext,
																						"删除失败,请重试");
																	}
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

				if (TextUtils.isEmpty(item.getLaudList())) {
					holderTextAImg.laud_list.setVisibility(View.GONE);
				} else {
					holderTextAImg.laud_list.setVisibility(View.VISIBLE);
				}

				ArrayList<MyDynamicDataComment> getmFriendDataComments = ((MyDynamic) item)
						.getmFriendDataComments();

				if (getmFriendDataComments != null
						&& getmFriendDataComments.size() > 0) {
					holderTextAImg.comment_box.setVisibility(View.VISIBLE);
					holderTextAImg.comment_box.removeAllViews();
					LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT);

					for (final MyDynamicDataComment fc : getmFriendDataComments) {
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
								showKeyBoard();
								curentPosition = position;
								member = fc.getNick_name();
								curentID = item.getId() + "";
                                replier = fc.getMemberId();
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
					holderTextAImg.item_iv.setTag(R.string.ispost, true);
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

				holderTextAImg.click_zan
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (item.getLaud() == 1) {
									if (CommonUtils
											.isNetworkAvailable(mContext)) {
										// 取消点赞
										InteNetUtils
												.getInstance(mContext)
												.cancelClickGood(
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
																		mFriendAdapter
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
																					"网络不可用,请重试");
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
																				"网络不可用,请重试");
															}
														});
									}

								} else {
									if (CommonUtils
											.isNetworkAvailable(mContext)) {
										// 点赞
										InteNetUtils
												.getInstance(mContext)
												.clickGood(
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
																		mFriendAdapter
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
																					"网络不可用,请重试");
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
																				"网络不可用,请重试");
															}
														});
									}
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
                                replier = item.getMemberId();
								curentPosition = position;
								bar_bottom.setVisibility(View.VISIBLE);
								mEditTextContent.setHint("说点什么吧...");
								showKeyBoard();
								isReply = false;

							}
						});
				holderTextAImg.item_friend_singleImg.setVisibility(View.GONE);
				holderTextAImg.item_friend_gridView.setVisibility(View.GONE);
				String images = item.getThumb();
				if (!TextUtils.isEmpty(images)) {
					String[] split = images.split("\\^");
					int length = split.length;
					if (length > 1) {
						// 多图用GridView
						holderTextAImg.item_friend_gridView
								.setVisibility(View.VISIBLE);
						MyGridViewAdapter adapter = new MyGridViewAdapter(split);
						holderTextAImg.item_friend_gridView.setAdapter(adapter);
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

						holderTextAImg.item_friend_singleImg.getLayoutParams().width = item
								.getSingImageW();
						holderTextAImg.item_friend_singleImg.getLayoutParams().height = item
								.getSingImageH();

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
										startActivity2StringAndPosition(
												ActivityContentPicSet.class,
												"IMAGES", item.getImages(), 0);
									}
								});

					}
				} else {
					holderTextAImg.item_friend_singleImg.setTag("");
				}

				break;
			case 1:

				break;
			case 2:
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
				holderTextAImg.item_friend_gridView.setVisibility(View.GONE);
				holderTextAImg.item_friend_singleImg.setVisibility(View.GONE);
			}
			
			
			return contentView;
		}
	}

	class ViewHolderTextAImg {
		LinearLayout comment_box;
		ImageView click_zan;
		MyTextView item_friend_looknum;
		MyTextView item_friend_content;
		MyTextView item_time;
		MyTextView item_name;
		RoundedImageView item_iv;
		ImageView comment;
		ImageView item_more;
		CubeImageView item_friend_singleImg;
		NoScrollGridView item_friend_gridView;
		MyTextView laud_list;
		RelativeLayout rl_views;
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
			String poster = getItem(position);
			if (!TextUtils.isEmpty(poster)) {
				CommonUtils.startImageLoader(cubeimageLoader,
						getItem(position), ((CubeImageView) convertView));
			} else {
				CommonUtils.startImageLoader(cubeimageLoader, "www.baidu.com",
						((CubeImageView) convertView));
			}
			return convertView;
		}

	}

	@Override
	public void onLastItemVisible() {
		if (isMoreData) {
			if (CommonUtils.isNetworkAvailable(mContext)) {
				loadMoreData();
			} else {
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						listview.onRefreshComplete();
					}
				}, 200);
			}
		}
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {

		if (CommonUtils.isNetworkAvailable(mContext)) {
			String time = TimeUtil.getTime(new Date());
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后更新:" + time);
			refreshData();
		} else {
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					listview.onRefreshComplete();
				}
			}, 200);
		}

	}

	public void startActivity2StringAndPosition(Class<?> cla, String key,
			String value, int position) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, value);
		intent.putExtra("POSITION", position);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.in_from_small2big_,
				R.anim.in_from_nochange);
	}

	@Override
	public void onBackPressed() {
		AnimFinsh();
	}

	/**
	 * 获取表情的gridview的子view
	 * 
	 * @param i
	 * @return
	 */
	private View getGridChildView(int i) {
		View view = View.inflate(this, R.layout.expression_gridview, null);
		ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
		List<String> list = new ArrayList<String>();
		if (i == 1) {
			List<String> list1 = reslist.subList(0, 20);
			list.addAll(list1);
		} else if (i == 2) {
			list.addAll(reslist.subList(20, reslist.size()));
		}
		list.add("delete_expression");
		final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this,
				1, list);
		gv.setAdapter(expressionAdapter);
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String filename = expressionAdapter.getItem(position);
				try {
					// 文字输入框可见时，才可输入表情
					// 按住说话可见，不让输入表情

					if (filename != "delete_expression") { // 不是删除键，显示表情
						// 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
						Class clz = Class
								.forName("com.xunao.benben.hx.chatuidemo.utils.SmileUtils");
						Field field = clz.getField(filename);
						mEditTextContent.append(SmileUtils.getSmiledText(
								mContext, (String) field.get(null)));
					} else { // 删除文字或者表情
						if (!TextUtils.isEmpty(mEditTextContent.getText())) {

							int selectionStart = mEditTextContent
									.getSelectionStart();// 获取光标的位置
							if (selectionStart > 0) {
								String body = mEditTextContent.getText()
										.toString();
								String tempStr = body.substring(0,
										selectionStart);
								int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
								if (i != -1) {
									CharSequence cs = tempStr.substring(i,
											selectionStart);
									if (SmileUtils.containsKey(cs.toString()))
										mEditTextContent.getEditableText()
												.delete(i, selectionStart);
									else
										mEditTextContent.getEditableText()
												.delete(selectionStart - 1,
														selectionStart);
								} else {
									mEditTextContent.getEditableText().delete(
											selectionStart - 1, selectionStart);
								}
							}
						}

					}
				} catch (Exception e) {
				}

			}
		});
		return view;
	}

	/**
	 * 点击文字输入框
	 * 
	 * @param v
	 */
	public void editClick(View v) {
		if (more.getVisibility() == View.VISIBLE) {
			more.setVisibility(View.GONE);
			// iv_emoticons_normal.setVisibility(View.VISIBLE);
			// iv_emoticons_checked.setVisibility(View.INVISIBLE);
		}

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.iv_emoticons_normal) { // 点击显示表情框
			more.setVisibility(View.VISIBLE);
			// iv_emoticons_normal.setVisibility(View.INVISIBLE);
			// iv_emoticons_checked.setVisibility(View.VISIBLE);
			emojiIconContainer.setVisibility(View.VISIBLE);
			hideKeyboard();
		} else if (id == R.id.iv_emoticons_checked) { // 点击隐藏表情框
			// iv_emoticons_normal.setVisibility(View.VISIBLE);
			// iv_emoticons_checked.setVisibility(View.INVISIBLE);
			emojiIconContainer.setVisibility(View.GONE);
			more.setVisibility(View.GONE);
		} else if (id == R.id.btn_send) { // 点击隐藏表情框
			String s = mEditTextContent.getText().toString();
			if (isReply) {
				s = "：" + s + " @" + member;
				isReply = false;
			}
			sendText(s, curentID);
		}
	}

	// 发送评论
	private void sendText(final String s, String curentID) {
		if (CommonUtils.isNetworkAvailable(mContext)) {
			hideKeyboard();
			showLoding("请稍等...");
			InteNetUtils.getInstance(mContext).publicComment(curentID, s,replier,
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

									MyDynamic friendData = myDynamics
											.get(curentPosition);
									MyDynamicDataComment friendDataComment = new MyDynamicDataComment();
									friendDataComment.setReview(s);
									friendDataComment.setNick_name(user
											.getUserNickname());
									friendDataComment.setMemberId(user.getId()
											+ "");
									friendData.getmFriendDataComments().add(
											friendDataComment);
									mEditTextContent.setText("");
									if (bar_bottom.getVisibility() == View.VISIBLE) {
										bar_bottom.setVisibility(View.GONE);
									}
									mFriendAdapter.notifyDataSetChanged();
								} catch (NetRequestException e) {
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

	private void showKeyBoard() {
		// mEditTextContent.setFocusable(true);
		mEditTextContent.requestFocus();
		InputMethodManager m = (InputMethodManager) mEditTextContent
				.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		// mEditTextContent.setFocusableInTouchMode(true);

	}
}
