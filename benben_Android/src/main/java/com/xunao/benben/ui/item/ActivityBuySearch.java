package com.xunao.benben.ui.item;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.BaseActivity.TitleMode;
import com.xunao.benben.bean.BuyInfo;
import com.xunao.benben.bean.BuyInfolist;
import com.xunao.benben.bean.FriendData;
import com.xunao.benben.bean.FriendDataComment;
import com.xunao.benben.bean.Quote;
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
import com.xunao.benben.view.MyTextView;

public class ActivityBuySearch extends BaseActivity implements OnClickListener,
		OnRefreshListener<ListView>, OnLastItemVisibleListener,
		onSuccessLinstener {

	private PullToRefreshListView listview;
	private RelativeLayout nodota;
	private ArrayList<BuyInfo> mQuotes;
	private BuyInfoAdapter mQuoteAdapter;
	private BuyDialog buyDialog;
	private int curPosition;
	private EditText search_edittext;
	private ImageView iv_search_content_delect;
	private LinearLayout ll_seach_icon;
	private String searchKey = "";
	protected int pagerNum;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_buy_search);
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
		}, "我要买", 0);

		chanageTitle(mode);

		search_edittext = (EditText) findViewById(R.id.search_edittext);
		iv_search_content_delect = (ImageView) findViewById(R.id.iv_search_content_delect);
		ll_seach_icon = (LinearLayout) findViewById(R.id.ll_seach_icon);

		nodota = (RelativeLayout) findViewById(R.id.nodota);
		listview = (PullToRefreshListView) findViewById(R.id.listview);

		listview.setOnRefreshListener(this);
		listview.setOnLastItemVisibleListener(this);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				startAnimActivityForResult2(ActivityBuyInfoContent.class,
						AndroidConfig.writeFriendRequestCode, "ID", mQuotes
								.get(arg2 - 1).getId() + "");
			}
		});
		iv_search_content_delect.setOnClickListener(this);
	}

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

	// 加载本地数据
	private void initlockData() {
		try {
			List<BuyInfo> findAll = dbUtil.findAll(BuyInfo.class);
			if (findAll != null && findAll.size() > 0) {
				mQuotes = (ArrayList<BuyInfo>) findAll;
				for (BuyInfo b : mQuotes) {
					List<Quote> findAll2 = dbUtil.findAll(Selector
							.from(Quote.class));
					b.setmQuotes((ArrayList<Quote>) findAll2);
				}
				isloadLock = true;
				mQuoteAdapter = new BuyInfoAdapter();
				listview.setAdapter(mQuoteAdapter);
			} else {
				isloadLock = false;
			}
		} catch (DbException e) {
			isloadLock = false;
			e.printStackTrace();
		}

	}

	@Override
	public void initDate(Bundle savedInstanceState) {

	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		search_edittext.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				if (hasFocus) {
					InputMethodManager imm = (InputMethodManager) getApplicationContext()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					// 显示键盘
					imm.showSoftInput(search_edittext, 0);
				}
			}
		});

		search_edittext.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				if (arg0.length() > 0) {
					ll_seach_icon.setVisibility(View.GONE);
					iv_search_content_delect.setVisibility(View.VISIBLE);
					searchKey = search_edittext.getText().toString().trim();
				} else {
					ll_seach_icon.setVisibility(View.VISIBLE);
					iv_search_content_delect.setVisibility(View.GONE);
					searchKey = "";
				}
			}
		});

		search_edittext.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView arg0, int actionId,
					KeyEvent arg2) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					// 先隐藏键盘
					((InputMethodManager) search_edittext.getContext()
							.getSystemService(Context.INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(mContext.getCurrentFocus()
									.getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);

					// 更新关键字
					searchKey = search_edittext.getText().toString().trim();
					isLoadMore = false;
					enterNum = false;
					setShowLoding(!(mQuotes != null && mQuotes.size() > 0));
					if (CommonUtils.isNetworkAvailable(mContext))
						InteNetUtils.getInstance(mContext).buySearch("",
								searchKey, mRequestCallBack);
					return true;
				}
				return false;
			}
		});
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
		BuyInfolist buyInfolist = new BuyInfolist();
		try {
			buyInfolist.parseJSON(jsonObject);
			ArrayList<BuyInfo> getmQuotes = buyInfolist.getmQuotes();
			addData(getmQuotes);
			saveData(getmQuotes);
		} catch (NetRequestException e) {
			e.getError().print(mContext);
			e.printStackTrace();
		}

	}

	// 保存数据
	private void saveData(ArrayList<BuyInfo> getmQuotes) {
		// try {
		// dbUtil.deleteAll(BuyInfo.class);
		// dbUtil.deleteAll(Quote.class);
		// if (mQuotes != null) {
		// dbUtil.saveAll(mQuotes);
		// for (BuyInfo f : mQuotes) {
		// dbUtil.saveAll(f.getmQuotes());
		// }
		// }
		// } catch (DbException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	// 添加数据
	private void addData(ArrayList<BuyInfo> getmQuotes) {
		if (!isLoadMore) {
			if (getmQuotes != null && getmQuotes.size() > 0) {
				mQuotes = getmQuotes;
				nodota.setVisibility(View.GONE);
				if (mQuotes.size() < AndroidConfig.DataNUM) {
					isMoreData = false;
					enterNum = false;
				} else {
					isMoreData = true;
					enterNum = true;
				}
				if (mQuoteAdapter == null) {
					mQuoteAdapter = new BuyInfoAdapter();
					listview.setAdapter(mQuoteAdapter);
				} else {
					mQuoteAdapter.notifyDataSetChanged();
				}
			} else {
				mQuoteAdapter = null;
				listview.setAdapter(null);

				if (mQuotes != null) {
					mQuotes.clear();
				}
				nodota.setVisibility(View.VISIBLE);
			}
		} else {

			if (getmQuotes.size() < AndroidConfig.DataNUM) {
				isMoreData = false;
			} else {
				isMoreData = true;
			}
			enterNum = true;
			mQuotes.addAll(getmQuotes);
			mQuoteAdapter.notifyDataSetChanged();

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

	// 加载更多
	@Override
	public void onLastItemVisible() {

		if (isMoreData) {
			if (CommonUtils.isNetworkAvailable(mContext)) {
				isLoadMore = true;
				InteNetUtils.getInstance(mContext).buySearch(
						mQuotes.get(mQuotes.size() - 1).getCreatedTime() + "",
						searchKey, mRequestCallBack);
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

	// 刷新数据
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {

		if (CommonUtils.isNetworkAvailable(mContext)) {
			isLoadMore = false;
			String time = TimeUtil.getTime(new Date());
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后更新:" + time);
			setShowLoding(!(mQuotes != null && mQuotes.size() > 0));
			InteNetUtils.getInstance(mContext).buySearch("", searchKey,
					mRequestCallBack);
		} else {
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					listview.onRefreshComplete();
				}
			}, 200);
		}

	}

	@Override
	public void onClick(View v) {

		int id = v.getId();
		switch (id) {
		case R.id.iv_search_content_delect:// 删除搜索条件
			search_edittext.setText("");
			break;
		}

	}

	class BuyInfoAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mQuotes.size() + 1;
		}

		@Override
		public BuyInfo getItem(int arg0) {
			// TODO Auto-generated method stub
			return mQuotes.get(arg0);
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

			if (position >= mQuotes.size()) {
				return 1;
			}
			return 0;
		}

		@Override
		public View getView(final int arg0, View arg1, ViewGroup arg2) {
			int itemViewType = getItemViewType(arg0);

			if (arg1 == null) {
				if (itemViewType == 0) {
					arg1 = View.inflate(mContext, R.layout.item_buyinfo, null);
				} else {
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
				}
			}
			if (itemViewType == 0) {
				MyTextView item_buyinfo_title = ViewHolderUtil.get(arg1,
						R.id.item_buyinfo_title);
				MyTextView item_buyinfo_but = ViewHolderUtil.get(arg1,
						R.id.item_buyinfo_but);
				LinearLayout price_box = ViewHolderUtil.get(arg1,
						R.id.price_box);
				MyTextView item_buyinfo_num = ViewHolderUtil.get(arg1,
						R.id.item_buyinfo_num);
				MyTextView item_buyinfo_address = ViewHolderUtil.get(arg1,
						R.id.item_buyinfo_address);
				final MyTextView item_buyinfo_time = ViewHolderUtil.get(arg1,
						R.id.item_buyinfo_time);
				final BuyInfo item = getItem(arg0);

				item_buyinfo_title.setText(item.getTitle());
				long deadline = item.getDeadline();
				if (deadline == 0) {

					item_buyinfo_time.setText("已经结束");

				} else {
					long time = (deadline)
							- (System.currentTimeMillis() / 1000);

					String secToTime = secToTime(time);
					item_buyinfo_time.setText("  " + secToTime);
				}

				if (item.getMemberId() != user.getId() && deadline != 0) {

					item_buyinfo_but.setVisibility(View.VISIBLE);
					item_buyinfo_but.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// 报价
							if (user != null && user.getZhiId() != 0) {
								curPosition = arg0;
								buyDialog = new BuyDialog(mContext,
										R.style.BuyDialog);
								buyDialog
										.setSuccessLinstener(ActivityBuySearch.this);
								buyDialog.setBuyId(item.getId());
								buyDialog.show();
							} else {
								hint = new InfoMsgHint(mContext,
										R.style.MyDialog1);
								hint.setContent("需要先开通号码直通车", "", "去开通", "取消");
								hint.setCancleListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										hint.dismiss();
									}
								});
								hint.setOKListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										// 开通直通车

//										// 判断是不是完善了我的号码直通车信息
//										switch (mContext.user.getUserInfo()) {
//										case "0":
//											mContext.startAnimActivity(ActivityMyNumberTrianInfoPerfect.class);
//											break;
//										case "2":
//											mContext.startAnimActivity(ActivityMyNumberTrianInfoPerfect.class);
//											break;
//										default:
											InteNetUtils
													.getInstance(mContext)
													.getMyStore(
															new RequestCallBack<String>() {

																@Override
																public void onSuccess(
																		ResponseInfo<String> arg0) {
																	JSONObject jsonObject = null;
																	try {
																		jsonObject = new JSONObject(
																				arg0.result);
																	} catch (JSONException e) {
																		e.printStackTrace();
																	}
																	String ret_num = jsonObject
																			.optString("ret_num");

																	if ("123"
																			.equalsIgnoreCase(ret_num)) {
																		mContext.startAnimActivity2Obj(
																				ActivityMyNumberTrain.class,
																				"do",
																				"created");
																	} else {
																		mContext.startAnimActivity2Obj(
																				ActivityMyNumberTrain.class,
																				"do",
																				"update");
																	}
																}

																@Override
																public void onFailure(
																		HttpException arg0,
																		String arg1) {
																	ToastUtils
																			.Infotoast(
																					mContext,
																					"网络不可用!");
																}
															});

//										}
										hint.dismiss();
									}
								});
								hint.show();
							}
						}
					});

				} else {
					item_buyinfo_but.setVisibility(View.GONE);
				}

				ArrayList<Quote> getmQuotes = item.getmQuotes();
				if (getmQuotes != null && getmQuotes.size() > 0) {
					price_box.removeAllViews();
					price_box.setVisibility(View.VISIBLE);
					for (Quote q : getmQuotes) {
						View inflate = View.inflate(mContext,
								R.layout.item_quote, null);

						MyTextView item_name = ViewHolderUtil.get(inflate,
								R.id.item_name);
						MyTextView item_price = ViewHolderUtil.get(inflate,
								R.id.item_price);
						final MyTextView item_info = ViewHolderUtil.get(
								inflate, R.id.item_info);

						item_name.setText(" " + q.getName());
						item_price.setText(" 报价:" + q.getPrice());
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
				long time = (item.getDeadline())
						- (System.currentTimeMillis() / 1000);

				String secToTime = secToTime(time);
				item_buyinfo_time.setText("  " + secToTime);
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
		if (mQuotes != null) {
			mQuotes.get(curPosition)
					.getmQuotes()
					.add(0,
							new Quote(name, pricr, info, user
									.getId()));
			mQuoteAdapter.notifyDataSetChanged();
		}
	}
}
