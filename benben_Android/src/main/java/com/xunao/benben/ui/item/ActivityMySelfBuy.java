package com.xunao.benben.ui.item;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.color;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.BaseActivity.TitleMode;
import com.xunao.benben.bean.BuyInfo;
import com.xunao.benben.bean.BuyInfolist;
import com.xunao.benben.bean.Contacts;
import com.xunao.benben.bean.FriendData;
import com.xunao.benben.bean.FriendDataComment;
import com.xunao.benben.bean.MyBuyInfo;
import com.xunao.benben.bean.MyBuyInfolist;
import com.xunao.benben.bean.MyQuote;
import com.xunao.benben.bean.Quote;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.bean.User;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.BuyDialog;
import com.xunao.benben.dialog.InfoMsgHint;
import com.xunao.benben.dialog.BuyDialog.onSuccessLinstener;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.TimeUtil;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.utils.ViewHolderUtil;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.ActionSheet.ActionSheetListener;
import com.xunao.benben.view.MyTextView;

public class ActivityMySelfBuy extends BaseActivity implements OnClickListener,
		OnRefreshListener<ListView>, OnLastItemVisibleListener,
		onSuccessLinstener {

	private PullToRefreshListView buy_listview;
	private RelativeLayout buy_nodota;
	private ArrayList<MyBuyInfo> buy_Quotes;
	private ArrayList<MyBuyInfo> price_Quotes;
	private BuyInfoAdapter mQuoteAdapter;

	private PullToRefreshListView price_listview;
	private RelativeLayout price_nodota;

	private BuyDialog buyDialog;
	private int curPosition;
	private ImageView public_buy;
	private RelativeLayout prerecord_tab_one;
	private RelativeLayout prerecord_tab_three;

	protected boolean price_isMoreData = true; // 是否有更多数据
	protected boolean price_enterNum = true;
	protected boolean price_isLoadMore;

	private View[] but;
	private final int BUY = 1;
	private final int PRICE = 2;
	private int dataType = 1;
	private RelativeLayout curTouchTab;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_myselfbuy);
		setShowLoding(false);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		setShowLoding(false);
		initTitleView();
		TitleMode mode = new TitleMode("#068cd9", "", 0, new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 搜索
				// startAnimActivity(ActivityBuySearch.class);
			}
		}, "", R.drawable.ic_back, new OnClickListener() {

			@Override
			public void onClick(View v) {

				mContext.AnimFinsh();
			}
		}, "我要买", 0);

		chanageTitle(mode);

		but = new View[2];
		prerecord_tab_one = (RelativeLayout) findViewById(R.id.prerecord_tab_one);
		prerecord_tab_three = (RelativeLayout) findViewById(R.id.prerecord_tab_three);
		public_buy = (ImageView) findViewById(R.id.public_buy);
		buy_nodota = (RelativeLayout) findViewById(R.id.buy_nodota);
		buy_listview = (PullToRefreshListView) findViewById(R.id.buy_listview);

		price_nodota = (RelativeLayout) findViewById(R.id.price_nodota);
		price_listview = (PullToRefreshListView) findViewById(R.id.price_listview);
		// findViewById(R.id.searchBox).setVisibility(View.GONE);

		buy_box = (RelativeLayout) findViewById(R.id.buy_box);
		price_box = (RelativeLayout) findViewById(R.id.price_box);

		but[0] = prerecord_tab_one;
		but[1] = prerecord_tab_three;

		price_listview.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (CommonUtils.isNetworkAvailable(mContext)) {
					price_isLoadMore = false;
					dataType = 2;
					String time = TimeUtil.getTime(new Date());
					refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
							"最后更新:" + time);
					InteNetUtils.getInstance(mContext).getMyBuyInfo("", "",
							dataType + "", priceComback);
				} else {
					mHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							price_listview.onRefreshComplete();
						}
					}, 200);
				}
			}
		});
		price_listview
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						if (price_isMoreData) {
							if (CommonUtils.isNetworkAvailable(mContext)) {
								price_isLoadMore = true;
								dataType = 2;
								InteNetUtils
										.getInstance(mContext)
										.getMyBuyInfo(
												price_Quotes.get(
														buy_Quotes.size() - 1)
														.getCreatedTime()
														+ "", "",
												dataType + "", mRequestCallBack);
							} else {
								mHandler.postDelayed(new Runnable() {

									@Override
									public void run() {
										price_listview.onRefreshComplete();
									}
								}, 200);
							}
						}
					}
				});

		buy_listview.setOnRefreshListener(this);
		buy_listview.setOnLastItemVisibleListener(this);

		// buy_listview.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		// long arg3) {
		// if (arg2 <= price_Quotes.size()) {
		// startAnimActivityForResult2(ActivityBuyInfoContent.class,
		// AndroidConfig.writeFriendRequestCode, "ID",
		// buy_Quotes.get(arg2 - 1).getId() + "");
		// }
		//
		// }
		// });
		//
		// price_listview.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		// long arg3) {
		// if (arg2 <= price_Quotes.size()) {
		// startAnimActivityForResult2(ActivityBuyInfoContent.class,
		// AndroidConfig.writeFriendRequestCode, "ID",
		// price_Quotes.get(arg2 - 1).getId() + "");
		//
		// }
		// }
		// });

		public_buy.setOnClickListener(this);
		prerecord_tab_one.setOnClickListener(this);
		prerecord_tab_three.setOnClickListener(this);
		prerecord_tab_one.performClick();

		boolean networkAvailable = CommonUtils.isNetworkAvailable(mContext);
		if (networkAvailable) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					// showLoding("请稍等...");
					buy_listview.setRefreshing(true);
				}
			}, 200);
		} else {
			if (isloadLock) {
				buy_nodota.setVisibility(View.GONE);
			} else {
				buy_nodota.setVisibility(View.VISIBLE);
			}
		}

	}

	RequestCallBack<String> priceComback = new RequestCallBack<String>() {

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {

			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(arg0.result);

				dissLoding();
				price_listview.onRefreshComplete();
				MyBuyInfolist buyInfolist = new MyBuyInfolist();

				try {
					buyInfolist.parseJSON(jsonObject);

					ArrayList<MyBuyInfo> getmQuotes = buyInfolist.getmQuotes();

					addData_price(getmQuotes);
					// saveData(getmQuotes);
				} catch (NetRequestException e) {
					e.printStackTrace();
					e.getError().print(mContext);
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			price_listview.onRefreshComplete();
			ToastUtils.Errortoast(mContext, "网络不可用");
			price_nodota.setVisibility(View.VISIBLE);
		}
	};
	private BuyInfoAdapter price_mQuoteAdapter;
	private RelativeLayout buy_box;
	private RelativeLayout price_box;

	//
	// // 加载本地数据
	// private void initlockData() {
	// try {
	// List<MyBuyInfo> findAll = dbUtil.findAll(MyBuyInfo.class);
	// if (findAll != null && findAll.size() > 0) {
	// mQuotes = (ArrayList<MyBuyInfo>) findAll;
	// for (MyBuyInfo b : mQuotes) {
	// List<MyQuote> findAll2 = dbUtil.findAll(Selector
	// .from(MyQuote.class));
	// b.setmQuotes((ArrayList<MyQuote>) findAll2);
	// }
	// isloadLock = true;
	// mQuoteAdapter = new BuyInfoAdapter();
	// listview.setAdapter(mQuoteAdapter);
	// } else {
	// isloadLock = false;
	// }
	// } catch (DbException e) {
	// isloadLock = false;
	// e.printStackTrace();
	// }

	@Override
	public void initDate(Bundle savedInstanceState) {

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
		dissLoding();
		buy_listview.onRefreshComplete();
		MyBuyInfolist buyInfolist = new MyBuyInfolist();

		try {
			buyInfolist.parseJSON(jsonObject);

			ArrayList<MyBuyInfo> getmQuotes = buyInfolist.getmQuotes();

			addData(getmQuotes);
			// saveData(getmQuotes);
		} catch (NetRequestException e) {
			e.printStackTrace();
			e.getError().print(mContext);
		}

	}

	// 保存数据
	private void saveData(ArrayList<MyBuyInfo> getmQuotes) {
		try {
			dbUtil.deleteAll(MyBuyInfo.class);
			dbUtil.deleteAll(MyQuote.class);
			if (buy_Quotes != null) {
				dbUtil.saveAll(buy_Quotes);
				for (MyBuyInfo f : buy_Quotes) {
					dbUtil.saveOrUpdateAll(f.getmQuotes());
				}
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addData_price(ArrayList<MyBuyInfo> getmQuotes) {
		if (!price_isLoadMore) {
			if (getmQuotes != null && getmQuotes.size() > 0) {
				price_Quotes = getmQuotes;
				price_nodota.setVisibility(View.GONE);
				if (price_Quotes.size() < AndroidConfig.DataNUM) {
					price_isMoreData = false;
					price_enterNum = false;
				} else {
					price_isMoreData = true;
					price_enterNum = true;
				}
				if (price_mQuoteAdapter == null) {
					price_mQuoteAdapter = new BuyInfoAdapter(2);
					price_listview.setAdapter(price_mQuoteAdapter);
				} else {
					price_mQuoteAdapter.notifyDataSetChanged();
				}
			} else {
				price_mQuoteAdapter = null;
				price_listview.setAdapter(null);

				if (price_Quotes != null) {
					price_Quotes.clear();
				}
				price_nodota.setVisibility(View.VISIBLE);
			}
		} else {

			if (getmQuotes.size() < AndroidConfig.DataNUM) {
				price_isMoreData = false;
			} else {
				price_isMoreData = true;
			}
			price_enterNum = true;
			price_Quotes.addAll(getmQuotes);
			price_mQuoteAdapter.notifyDataSetChanged();

		}

	}

	// 添加数据
	private void addData(ArrayList<MyBuyInfo> getmQuotes) {
		if (!isLoadMore) {
			if (getmQuotes != null && getmQuotes.size() > 0) {
				buy_Quotes = getmQuotes;
				buy_nodota.setVisibility(View.GONE);
				if (buy_Quotes.size() < AndroidConfig.DataNUM) {
					isMoreData = false;
					enterNum = false;
				} else {
					isMoreData = true;
					enterNum = true;
				}
				if (mQuoteAdapter == null) {
					mQuoteAdapter = new BuyInfoAdapter(1);
					buy_listview.setAdapter(mQuoteAdapter);
				} else {
					mQuoteAdapter.notifyDataSetChanged();
				}
			} else {
				mQuoteAdapter = null;
				buy_listview.setAdapter(null);

				if (buy_Quotes != null) {
					buy_Quotes.clear();
				}
				buy_nodota.setVisibility(View.VISIBLE);
			}
		} else {

			if (getmQuotes.size() < AndroidConfig.DataNUM) {
				isMoreData = false;
			} else {
				isMoreData = true;
			}
			enterNum = true;
			buy_Quotes.addAll(getmQuotes);
			mQuoteAdapter.notifyDataSetChanged();

		}

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		buy_listview.onRefreshComplete();
		ToastUtils.Errortoast(mContext, "网络不可用");
		if (isloadLock) {
			buy_nodota.setVisibility(View.GONE);
		} else {
			buy_nodota.setVisibility(View.VISIBLE);
		}
	}

	// 加载更多
	@Override
	public void onLastItemVisible() {

		if (isMoreData) {
			if (CommonUtils.isNetworkAvailable(mContext)) {
				isLoadMore = true;
				dataType = 1;
				InteNetUtils.getInstance(mContext).getMyBuyInfo(
						buy_Quotes.get(buy_Quotes.size() - 1).getCreatedTime()
								+ "", "", dataType + "", mRequestCallBack);
			} else {
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						buy_listview.onRefreshComplete();
					}
				}, 200);
			}
		}

	}

	// 刷新数据
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		if (CommonUtils.isNetworkAvailable(mContext)) {
			isLoadMore = false;
			dataType = 1;
			String time = TimeUtil.getTime(new Date());
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后更新:" + time);
			InteNetUtils.getInstance(mContext).getMyBuyInfo("", "",
					dataType + "", mRequestCallBack);
		} else {
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					buy_listview.onRefreshComplete();
				}
			}, 200);
		}
	}

	@Override
	public void onClick(View v) {

		int id = v.getId();
		switch (id) {
		case R.id.public_buy:// 发布我要买
			if ((Integer.parseInt(user.getUserInfo()) & 2) > 0) {
				startAnimActivityForResult(ActivityWriteBuyInfo.class,
						AndroidConfig.writeFriendRequestCode);
			} else {

				switch (user.getUserInfo()) {
				case "0":
					startAnimActivity2Obj(
							ActivityMyNumberTrianInfoPerfect.class, "buy",
							"buyInfo");
					break;
				case "2":
					startAnimActivity2Obj(
							ActivityMyNumberTrianInfoPerfect.class, "buy",
							"buyInfo");
					break;
				default:
					startAnimActivityForResult(ActivityWriteBuyInfo.class,
							AndroidConfig.writeFriendRequestCode);
				}
				break;
			}
			break;
		case R.id.prerecord_tab_one:// 我的我要买
			dataType = BUY;
			curTouchTab = prerecord_tab_one;
			setChecked(prerecord_tab_one, true);
			setChecked(prerecord_tab_three, false);

			buy_box.setVisibility(View.VISIBLE);
			price_box.setVisibility(View.GONE);

			break;
		case R.id.prerecord_tab_three:// 我的报价
			dataType = PRICE;
			curTouchTab = prerecord_tab_three;
			setChecked(prerecord_tab_one, false);
			setChecked(prerecord_tab_three, true);

			buy_box.setVisibility(View.GONE);
			price_box.setVisibility(View.VISIBLE);

			if (price_Quotes == null) {
				if (CommonUtils.isNetworkAvailable(mContext)) {
					mHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							price_listview.setRefreshing(true);
						}
					}, 200);

				} else {
					price_nodota.setVisibility(View.GONE);

				}
			}
			break;
		}
	}

	private void setChecked(RelativeLayout view, boolean isCheck) {
		RadioButton tab_RB = (RadioButton) view.findViewById(R.id.tab_RB);
		tab_RB.setChecked(isCheck);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (arg1 == AndroidConfig.writeFriendRefreshResultCode) {
			if (CommonUtils.isNetworkAvailable(mContext)) {
				buy_listview.setSelection(0);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						buy_listview.setRefreshing(true);
					}
				}, 200);
			}
		}

	}

	class BuyInfoAdapter extends BaseAdapter {

		int type;

		public BuyInfoAdapter(int type) {
			super();
			this.type = type;
		}

		@Override
		public int getCount() {
			if (type == 1) {
				return buy_Quotes.size() + 1;
			} else {
				return price_Quotes.size() + 1;
			}
		}

		@Override
		public MyBuyInfo getItem(int arg0) {
			// TODO Auto-generated method stub
			if (type == 1) {
				return buy_Quotes.get(arg0);
			} else {
				return price_Quotes.get(arg0);
			}
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
			if (type == 1) {
				if (position >= buy_Quotes.size()) {
					return 1;
				}
				return 0;
			} else {
				if (position >= price_Quotes.size()) {
					return 1;
				}
				return 0;
			}

		}

		@Override
		public View getView(final int arg0, View arg1, ViewGroup arg2) {
			int itemViewType = getItemViewType(arg0);
			String status = "";
			if (arg1 == null) {
				if (itemViewType == 0) {
					arg1 = View.inflate(mContext, R.layout.item_buyinfo, null);
				}
			}

			if (itemViewType == 1) {

				if (type == 1) {
					if (isMoreData) {
						arg1 = getLayoutInflater().inflate(
								R.layout.item_load_more, null);
						LinearLayout load_more = ViewHolderUtil.get(arg1,
								R.id.load_more);
						if (enterNum) {
							load_more.setVisibility(View.VISIBLE);
						} else {
							load_more.setVisibility(View.GONE);
						}
					} else {
						arg1 = getLayoutInflater().inflate(
								R.layout.item_no_load_more, null);
					}
					return arg1;
				} else {
					if (price_isMoreData) {
						arg1 = getLayoutInflater().inflate(
								R.layout.item_load_more, null);
						LinearLayout load_more = ViewHolderUtil.get(arg1,
								R.id.load_more);
						if (price_isMoreData) {
							load_more.setVisibility(View.VISIBLE);
						} else {
							load_more.setVisibility(View.GONE);
						}
					} else {
						arg1 = getLayoutInflater().inflate(
								R.layout.item_no_load_more, null);
					}
					return arg1;
				}
			}

			if (itemViewType == 0) {
				MyTextView item_buyinfo_title = ViewHolderUtil.get(arg1,
						R.id.item_buyinfo_title);
				MyTextView item_buyinfo_n = ViewHolderUtil.get(arg1,
						R.id.item_buyinfo_n);
				MyTextView item_buyinfo_but = ViewHolderUtil.get(arg1,
						R.id.item_buyinfo_but);
				LinearLayout price_box = ViewHolderUtil.get(arg1,
						R.id.price_box);
				MyTextView item_buyinfo_num = ViewHolderUtil.get(arg1,
						R.id.item_buyinfo_num);
				MyTextView item_buyinfo_address = ViewHolderUtil.get(arg1,
						R.id.item_buyinfo_address);
				
				LinearLayout ll_baojia = ViewHolderUtil.get(arg1, R.id.ll_baojia);
				LinearLayout ll_pinbi = ViewHolderUtil.get(arg1, R.id.ll_pinbi);
				final MyTextView item_buyinfo_time = ViewHolderUtil.get(arg1,
						R.id.item_buyinfo_time);
				final MyBuyInfo item = getItem(arg0);

				item_buyinfo_title.setText(item.getTitle());
				item_buyinfo_n.setText("x" + item.getAmount());
				long deadline = item.getDeadline();
				status = item.getStatus();
				if(item.getStatus().equals("1")){
					ll_baojia.setVisibility(View.GONE);
					ll_pinbi.setVisibility(View.VISIBLE);
					price_box.setVisibility(View.GONE);
				}else{
					ll_baojia.setVisibility(View.VISIBLE);
					ll_pinbi.setVisibility(View.GONE);
					price_box.setVisibility(View.VISIBLE);
				}

				arg1.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						String id = "1";
						if (type == 1) {
							id = buy_Quotes.get(arg0).getId() + "";

						} else {
							id = price_Quotes.get(arg0).getId() + "";
						}
						if(!item.getStatus().equals("1")){
							startAnimActivityForResult2(
									ActivityBuyInfoContent.class,
									AndroidConfig.writeFriendRequestCode, "ID", id);
						}
					}
				});

				if ((deadline) - (System.currentTimeMillis() / 1000) <= 0) {
					item_buyinfo_time.setText("  已关闭");
					item_buyinfo_but.setVisibility(View.INVISIBLE);
				} else {
					item_buyinfo_but.setVisibility(View.VISIBLE);
					long time = (deadline)
							- (System.currentTimeMillis() / 1000);

					String secToTime = secToTime(time);
					item_buyinfo_time.setText("  " + secToTime);

					item_buyinfo_but.setText("关闭交易");

					if (dataType == 1) {
						final String id = buy_Quotes.get(arg0).getId();
						item_buyinfo_but.setTextColor(Color.RED);
						item_buyinfo_but
								.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View arg0) {
										showActionSheet(id + "");
									}
								});

					} else {
						item_buyinfo_but.setVisibility(View.GONE);
					}
					// final String id = price_Quotes.get(arg0).getId();
					// item_buyinfo_but.setTextColor(Color.RED);
					// item_buyinfo_but
					// .setOnClickListener(new OnClickListener() {
					// @Override
					// public void onClick(View arg0) {
					// showActionSheet(id + "");
					// }
					// });
					//
					// arg1.setOnClickListener(new OnClickListener() {
					//
					// @Override
					// public void onClick(View v) {
					// startAnimActivityForResult2(
					// ActivityBuyInfoContent.class,
					// AndroidConfig.writeFriendRequestCode,
					// "ID", price_Quotes.get(arg0).getId() + "");
					// }
					// });
					// }
				}
				if(!status.equals("1")){
				ArrayList<MyQuote> getmQuotes = item.getmQuotes();
				if (getmQuotes != null && getmQuotes.size() > 0) {
					price_box.removeAllViews();
					price_box.setVisibility(View.VISIBLE);
					for (MyQuote q : getmQuotes) {

						int childCount = price_box.getChildCount();
						if (childCount >= 3) {
							break;
						}
						View inflate = View.inflate(mContext,
								R.layout.item_quote, null);

						MyTextView item_name = ViewHolderUtil.get(inflate,
								R.id.item_name);
						MyTextView item_price = ViewHolderUtil.get(inflate,
								R.id.item_price);
						final MyTextView item_info = ViewHolderUtil.get(
								inflate, R.id.item_info);

						item_name.setText(" " + q.getName());
						item_price.setText(" 报价:" + q.getPrice() + "元");
						item_info.setText(" " + q.getDescription());

						price_box.addView(inflate);

					}
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT, 1);
					layoutParams.leftMargin = PixelUtil.dp2px(10);
					layoutParams.rightMargin = PixelUtil.dp2px(10);
					layoutParams.topMargin = PixelUtil.dp2px(5);

					View view = new View(mContext);
					view.setBackgroundColor(Color.parseColor("#dfdfdf"));
					price_box.addView(view, layoutParams);

				} else {
					price_box.setVisibility(View.GONE);
				}

				item_buyinfo_num.setText(item.getQuotedNumber() + "人报价");
				item_buyinfo_address.setText(item.getPro_city());

			}
			
			
				
			}

			return arg1;
		}
	}

	public static String secToTime(long time) {
		String timeStr = null;
		long hour = 0;
		long minute = 0;
		long second = 0;
		long day = 0;
		if (time <= 0) {
			return "00:00:00";
		} else {
			minute = time / 60;
			if (minute < 60) {
				second = time % 60;
				timeStr = "00时" + unitFormat(minute) + "分" + unitFormat(second)
						+ "秒";
			} else {
				hour = minute / 60;
				if (hour < 48) {
					minute = minute % 60;
					second = time - hour * 3600 - minute * 60;
					timeStr = unitFormat(hour) + "时" + unitFormat(minute) + "分"
							+ unitFormat(second) + "秒";
				} else {
					day = hour / 24;
					hour = hour % 24;
					minute = minute % 60;
					second = time - day * 24 * 3600 - hour * 3600 - minute * 60;

					if (day > 99) {
						timeStr = unitFormat(day) + "天";
					} else {
						timeStr = unitFormat(day) + "天" + unitFormat(hour)
								+ "时" + unitFormat(minute) + "分";
					}
				}
			}
		}
		return timeStr;
	}

	public static String unitFormat(long i) {
		String retStr = null;
		if (i >= 0 && i < 10)
			retStr = "0" + Long.toString(i);
		else
			retStr = "" + i;
		return retStr;
	}

	// 报价成功回调
	@Override
	public void onSuccess(String name, String pricr, String info) {

		if (buy_Quotes != null) {
			buy_Quotes.get(curPosition).getmQuotes()
					.add(0, new MyQuote(name, pricr, info));
			mQuoteAdapter.notifyDataSetChanged();
		}
	}

	public void showActionSheet(final String id) {
		setTheme(R.style.ActionSheetStyleIOS7);
		ActionSheet.createBuilder(this, getSupportFragmentManager())
				.setCancelButtonTitle("取消")
				.setOtherButtonTitles("关闭交易")
				// 设置颜色 必须一一对应
				.setOtherButtonTitlesColor("#1E82FF")
				.setCancelableOnTouchOutside(true)
				.setListener(new ActionSheetListener() {
					@Override
					public void onOtherButtonClick(ActionSheet actionSheet,
							int index) {

						if (index == 0) {
							if (CommonUtils.isNetworkAvailable(mContext)) {
								InteNetUtils.getInstance(mContext).closeBuy(id,
										new RequestCallBack<String>() {
											@Override
											public void onFailure(
													HttpException arg0,
													String arg1) {
												ToastUtils.Errortoast(mContext,
														"关闭失败");
											}

											@Override
											public void onSuccess(
													ResponseInfo<String> arg0) {

												try {
													JSONObject jsonObj = new JSONObject(
															arg0.result);
													SuccessMsg msg = new SuccessMsg();
													msg.parseJSON(jsonObj);
													if (CommonUtils
															.isNetworkAvailable(mContext)) {
														buy_listview
																.setSelection(0);
														new Handler()
																.postDelayed(
																		new Runnable() {
																			@Override
																			public void run() {
																				buy_listview
																						.setRefreshing(true);
																			}
																		}, 200);
													}
												} catch (JSONException e) {
													ToastUtils.Errortoast(
															mContext, "关闭失败");
													e.printStackTrace();
												} catch (NetRequestException e) {
													e.printStackTrace();
													e.getError()
															.print(mContext);
												}
											}
										});
							}
						}

					}

					@Override
					public void onDismiss(ActionSheet actionSheet,
							boolean isCancel) {

					}
				}).show();
	}

}
