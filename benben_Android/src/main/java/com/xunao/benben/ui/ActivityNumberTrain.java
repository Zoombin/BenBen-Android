/**
 * 号码直通车
 */
package com.xunao.benben.ui;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.NumberTrain;
import com.xunao.benben.bean.NumberTrainList;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoMsgHint;
import com.xunao.benben.dialog.LodingDialog;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.item.ActivityChoiceAddress;
import com.xunao.benben.ui.item.ActivityChoiceIndusrty;
import com.xunao.benben.ui.item.ActivityMyNumberTrainDetail;
import com.xunao.benben.ui.item.ActivityNumberTrainDetail;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PhoneUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.utils.ViewHolderUtil;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.ActionSheet.ActionSheetListener;

public class ActivityNumberTrain extends BaseActivity implements
		OnRefreshListener<ListView>, OnLastItemVisibleListener,
		BDLocationListener, OnClickListener {
	private String curNmae;
	private PullToRefreshListView listView;
	private LinearLayout ll_seach_icon;
	private LinearLayout ll_search_item;
	private LinearLayout no_data;
	private TextView tv_search_number;
	private String searchKey = "";
	private myAdapter adapter;
	private int pagerNum = 0;
	private boolean isMoreData = true;
	private boolean enterNum = false;
	private Button btn_search_range;
	private NumberTrainList numberTrainList;
	private ImageView iv_search_content_delect;
	private ArrayList<NumberTrain> numberTrains = new ArrayList<NumberTrain>();

	private RelativeLayout on_dingwei;

	private double latitude; // 维度
	private double longitude;// 经度
	private EditText search_edittext;
	// 记录了地区的id
	private String[] addressId = { "", "", "", "" };
	private static final int CHOCE_ADDRESS = 1;
    private static final int CHOCE_INDUSTRY = 2;
	private LodingDialog lodingDialog;

	private boolean isDelete = false;

	private String phone;
	private boolean isSearch = false;

	private View view;
	private LinearLayout ll_range;
	private TextView tv_range;
	protected InfoMsgHint hint;
    private String from="";

//    private LinearLayout ll_search_range,ll_search_industry;
    private RelativeLayout rl_search_area;
    private PopupWindow popupWindow;
    private TextView tv_search_range,tv_search_industry;
    private LinearLayout ll_industry;
    private TextView tv_industry;
    private String industryId="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		initlocalData();
		mLocationClient = new LocationClient(mApplication);
		// 设置定位条件
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度，默认值gcj02
		option.setNeedDeviceDirect(true);
		option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
		mLocationClient.registerLocationListener(this);
		mLocationClient.start();

	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mLocationClient != null) {
			mLocationClient.stop();
		}

	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_number_train);
		initdefaultImage(R.drawable.ic_group_poster);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
        from = getIntent().getStringExtra("from");
        if(from!=null && from.equals("chat")){
            initTitle_Right_Left_bar("号码直通车", "", "",
                    R.drawable.icon_com_title_left, 0);
        }else{
            initTitle_Right_Left_bar("号码直通车", "", "",
                    R.drawable.icon_com_title_left, R.drawable.icon_position_3);
        }



		search_edittext = (EditText) findViewById(R.id.search_edittext);
		((TextView) findViewById(R.id.searchName)).setText("商铺简称/服务项目/店铺号");
        rl_search_area = (RelativeLayout) findViewById(R.id.rl_search_area);
        rl_search_area.setOnClickListener(this);
		iv_search_content_delect = (ImageView) findViewById(R.id.iv_search_content_delect);
		ll_seach_icon = (LinearLayout) findViewById(R.id.ll_seach_icon);
		listView = (PullToRefreshListView) findViewById(R.id.listView);
		btn_search_range = (Button) findViewById(R.id.btn_search_range);
		no_data = (LinearLayout) findViewById(R.id.no_data);
		ll_search_item = (LinearLayout) findViewById(R.id.ll_search_item);
		tv_search_number = (TextView) findViewById(R.id.tv_search_number);

		on_dingwei = (RelativeLayout) findViewById(R.id.on_dingwei);

		LocationManager locationManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);
		boolean gps = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		PackageManager pm = getPackageManager();
		boolean permission = (PackageManager.PERMISSION_GRANTED == pm
				.checkPermission("android.permission.ACCESS_MOCK_LOCATION",
						"com.xunao.benben"));

		boolean isShowDialog = false;

		if (!permission) {
			isShowDialog = true;

			on_dingwei.setVisibility(view.VISIBLE);
			hint = new InfoMsgHint(mContext, R.style.MyDialog1);
			hint.setContent("定位服务未开启", "请在手机设置中开启定位服务以看到附近直通车", "开启定位", "知道了");
			hint.setOKListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent();
					intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					hint.dismiss();
				}
			});
			hint.setCancleListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					hint.dismiss();
				}
			});
			hint.show();
		}
		if (gps) {
			on_dingwei.setVisibility(view.GONE);
		} else {

			if (!isShowDialog) {

				on_dingwei.setVisibility(view.VISIBLE);
				hint = new InfoMsgHint(mContext, R.style.MyDialog1);
				hint.setContent("定位服务未开启", "请在手机设置中开启定位服务以看到附近直通车", "开启定位",
						"知道了");
				hint.setOKListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent();
						intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
						hint.dismiss();
					}
				});
				hint.setCancleListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						hint.dismiss();
					}
				});
				hint.show();
				isShowDialog = false;
			}
		}

		// PackageManager pm = getPackageManager();
		// boolean permission = (PackageManager.PERMISSION_GRANTED ==
		// pm.checkPermission("android.permission.RECORD_AUDIO",
		// "packageName"));
		//
		// if(!permission){
		// on_dingwei.setVisibility(view.VISIBLE);
		// hint = new InfoMsgHint(mContext, R.style.MyDialog1);
		// hint.setContent("定位服务未开启", "请在手机设置中开启定位服务以看到附近直通车", "开启定位", "知道了");
		// hint.setOKListener(new OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// Intent intent = new Intent();
		// intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		// startActivity(intent);
		// hint.dismiss();
		// }
		// });
		// hint.setCancleListener(new OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// hint.dismiss();
		// }
		// });
		// hint.show();
		// }

		on_dingwei.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});

		view = findViewById(R.id.view);
		ll_range = (LinearLayout) findViewById(R.id.ll_range);
		tv_range = (TextView) findViewById(R.id.tv_range);

		iv_search_content_delect.setOnClickListener(this);
		btn_search_range.setOnClickListener(this);
        btn_search_range.setText("搜索");

		listView.setOnRefreshListener(this);
		listView.setOnLastItemVisibleListener(this);

		boolean networkAvailable = CommonUtils.isNetworkAvailable(mContext);
		if (!networkAvailable) {
			initlocalData();
		}

		adapter = new myAdapter();
		listView.setAdapter(adapter);

//        ll_search_range = (LinearLayout) findViewById(R.id.ll_search_range);
//        ll_search_range.setOnClickListener(this);
//        ll_search_industry = (LinearLayout) findViewById(R.id.ll_search_industry);
//        ll_search_industry.setOnClickListener(this);
        ll_industry = (LinearLayout) findViewById(R.id.ll_industry);
        tv_industry = (TextView) findViewById(R.id.tv_industry);

        initPopWindow();
	}

	// 初始化本地数据
	private void initlocalData() {
		try {
			ArrayList<NumberTrain> arrayList = (ArrayList<NumberTrain>) dbUtil
					.findAll(NumberTrain.class);
			if (arrayList != null) {
				numberTrains = arrayList;
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		if (isShowLoding) {
			if (lodingDialog == null) {
				lodingDialog = new LodingDialog(mContext);
			}
			lodingDialog.show();
		}
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (isSearch || isDelete) {
					pagerNum = 0;
					enterNum = false;
					search_edittext.setText("");
					searchKey = "";
					addressId[1] = addressId[2] = addressId[0] = "";
					addressname = null;
                    industryId = "";
					InteNetUtils.getInstance(mContext).getStoreList(pagerNum,
							"", latitude, longitude, addressId[0],
							addressId[1], addressId[2], addressId[3],industryId,
							requestCallBack);
					isSearch = false;
					isDelete = false;
				} else {
					finish();
				}
			}
		});

		setOnRightClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startAnimActivityNumberTrain(ActivityNumberTrainMap.class,
						"numberTrain", numberTrains, "latitude", latitude,
						"longitude", longitude, "addressName", addressname);
			}
		});

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
					isSearch = true;
					ll_seach_icon.setVisibility(View.GONE);
					iv_search_content_delect.setVisibility(View.VISIBLE);
				} else {
					isSearch = false;
					ll_seach_icon.setVisibility(View.VISIBLE);
					iv_search_content_delect.setVisibility(View.GONE);
					searchKey = "";
					// new Handler().postDelayed(new Runnable() {
					// @Override
					// public void run() {
					// listView.setRefreshing(true);
					// }
					// }, 200);
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
					pagerNum = 0;
					enterNum = false;
					if (CommonUtils.isEmpty(searchKey)) {
						isSearch = false;
					} else {
						isSearch = true;
					}

					InteNetUtils.getInstance(mContext).getStoreList(pagerNum,
							searchKey, latitude, longitude, addressId[0],
							addressId[1], addressId[2], addressId[3],industryId,
							requestCallBack);
					return true;
				}
				return false;
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int position, long arg3) {
				if (position <= numberTrains.size()) {
					String id = numberTrains.get(position - 1).getId();
					String kil = numberTrains.get(position - 1)
							.getDistance_kilometers();

                    if(from!=null && from.equals("chat")){
                        final MsgDialog  msgDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
                        msgDialog.setContent("确定发送该直通车", "", "确认", "取消");
                        msgDialog.setCancleListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                msgDialog.dismiss();
                            }
                        });
                        msgDialog.setOKListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                msgDialog.dismiss();
                                Intent intent = new Intent();
                                intent.putExtra("numberTrain",numberTrains.get(position - 1));
                                setResult(RESULT_OK,intent);
                                AnimFinsh();
                            }
                        });
                        msgDialog.show();

                    }else {
                        String shop = numberTrains.get(position - 1).getShop();
                        if(shop.contains(user.getBenbenId())){
                            startAnimActivity3Obj(ActivityMyNumberTrainDetail.class,
                                    "id", id, "kil", kil);
                        }else {
                            startAnimActivity3Obj(ActivityNumberTrainDetail.class,
                                    "id", id, "kil", kil);
                        }
                    }
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
		listView.onRefreshComplete();
		try {
			numberTrainList = new NumberTrainList();
			numberTrainList = (NumberTrainList) numberTrainList
					.parseJSON(jsonObject);

			if (numberTrainList == null) {

			} else {
				if (isLoadMore) {
					numberTrains.addAll(numberTrainList.getNumberTrains());
				} else {
					numberTrains = numberTrainList.getNumberTrains();
				}

				// 保存数据到本地
				saveLocalData(numberTrains);

			}
		} catch (NetRequestException e) {

			e.printStackTrace();
		}
		adapter.notifyDataSetChanged();
	}

	// 保存数据到本地
	private void saveLocalData(ArrayList<NumberTrain> numberTrains) {
		try {
			dbUtil.deleteAll(NumberTrain.class);
			if (numberTrains != null) {
				dbUtil.saveAll(numberTrains);
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {

	}

	class myAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return numberTrains.size() + 1;
		}

		@Override
		public Object getItem(int arg0) {
			return numberTrains.get(arg0);
		}

		@Override
		public int getItemViewType(int position) {

			return position <= numberTrains.size() - 1 ? 0 : 1;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			if (getItemViewType(position) == 0) {
				if (convertView == null) {
					convertView = getLayoutInflater().inflate(
							R.layout.activity_number_train_item, null);
				}

				TextView number_train_title = ViewHolderUtil.get(convertView,
						R.id.number_train_title);
				RoundedImageView iv_face = ViewHolderUtil.get(convertView,
						R.id.iv_face);
				TextView tv_distance = ViewHolderUtil.get(convertView,
						R.id.tv_distance);
				TextView tv_tag = ViewHolderUtil.get(convertView, R.id.tv_tag);

				TextView tv_istop = ViewHolderUtil.get(convertView,
						R.id.tv_istop);

				ImageView iv_call_phone = ViewHolderUtil.get(convertView,
						R.id.iv_call_phone);
                ImageView iv_corner = ViewHolderUtil.get(convertView,
                        R.id.iv_corner);
                ImageView iv_top = ViewHolderUtil.get(convertView,
                        R.id.iv_top);


				final NumberTrain numberTrain = numberTrains.get(position);


				iv_call_phone.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						setTheme(R.style.ActionSheetStyleIOS7);
						phone = numberTrain.getPhone();
						String telPhone = numberTrain.getTelePhone();
						String telPhones = phone + "," + telPhone;
						String[] phones = telPhones.split(",");

						curNmae = numberTrain.getName();

                        int cid = Integer.parseInt(numberTrain.getId());
						showActionSheet(cid,phones);

					}
				});

				number_train_title.setText(numberTrain.getShortName());

//				if (!numberTrain.getIsTop().equals("0")) {
//					iv_top.setVisibility(View.VISIBLE);
//				} else {
//                    iv_top.setVisibility(View.GONE);
//				}
                int auth_grade=numberTrain.getAuth_grade();
                if(auth_grade==0 || numberTrain.getIs_valid()==0){
                    iv_corner.setVisibility(View.GONE);
                }else{
                    iv_corner.setVisibility(View.VISIBLE);
                    if(numberTrain.getVip_store()==1){
                        iv_corner.setImageResource(R.drawable.icon_corner_hui);
                    }else {

                        if (auth_grade == 1) {
                            iv_corner.setImageResource(R.drawable.icon_corner_cu);
                        } else if (auth_grade == 2) {
                            iv_corner.setImageResource(R.drawable.icon_corner_tuan);
                        }
                    }
                }

                if(numberTrain.getPlace()>0 && numberTrain.getPlace()!=100){
                    iv_top.setVisibility(View.VISIBLE);
                }else{
                    iv_top.setVisibility(View.GONE);
                }


				tv_tag.setText(numberTrain.getTag());

				CommonUtils.startImageLoader(cubeimageLoader,
						numberTrain.getPoster(), iv_face);
				if (!CommonUtils.isEmpty(numberTrain.getDistance_kilometers())
						&& tv_distance != null && numberTrain != null)
					tv_distance.setText(numberTrain.getDistance_kilometers());

			} else {
				if (isMoreData) {
					convertView = getLayoutInflater().inflate(
							R.layout.item_load_more, null);
					LinearLayout load_more = ViewHolderUtil.get(convertView,
							R.id.load_more);
					if (enterNum) {
						load_more.setVisibility(View.VISIBLE);
					} else {
						load_more.setVisibility(View.GONE);
					}
				} else {
					convertView = getLayoutInflater().inflate(
							R.layout.item_no_load_more, null);
					convertView.setVisibility(View.GONE);
				}
			}
			return convertView;
		}

	}

	public void showActionSheet(final int cid,final String[] phone) {
		ActionSheet.createBuilder(this, getSupportFragmentManager())
				.setCancelButtonTitle("取消")
				.setOtherButtonTitles(phone)
				// 设置颜色 必须一一对应
				.setOtherButtonTitlesColor("#1E82FF")
				.setCancelableOnTouchOutside(true)
				.setListener(new ActionSheetListener() {
					@Override
					public void onOtherButtonClick(ActionSheet actionSheet,
							int index) {
						switch (index) {
						case 0:
							PhoneUtils.makeCall(cid,curNmae, phone[0], mContext);
							break;
						case 1:
							PhoneUtils.makeCall(cid,curNmae, phone[1], mContext);
							break;
						}
					}

					@Override
					public void onDismiss(ActionSheet actionSheet,
							boolean isCancel) {

					}
				}).show();
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		setLoadMore(false);
		pagerNum = 0;
		listView.setOnLastItemVisibleListener(this);

		InteNetUtils.getInstance(mContext).getStoreList(pagerNum, searchKey,
				latitude, longitude, addressId[0], addressId[1], addressId[2],
				addressId[3],industryId, requestCallBack);
	}

	@Override
	public void onLastItemVisible() {
		setLoadMore(true);
		pagerNum++;
		InteNetUtils.getInstance(mContext).getStoreList(pagerNum, searchKey,
				latitude, longitude, addressId[0], addressId[1], addressId[2],
				addressId[3],industryId, requestCallBack);
	}

	private RequestCallBack<String> requestCallBack = new RequestCallBack<String>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			if (lodingDialog != null && lodingDialog.isShowing()) {
				lodingDialog.dismiss();
			}
			listView.onRefreshComplete();
//			ToastUtils.Errortoast(mContext, arg1);
		}

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			if (lodingDialog != null && lodingDialog.isShowing()) {
				lodingDialog.dismiss();
			}
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(arg0.result);
			} catch (JSONException e) {
				e.printStackTrace();
				ToastUtils.Infotoast(ActivityNumberTrain.this, "网络不可用,请重试");

			}

			listView.onRefreshComplete();

			numberTrainList = new NumberTrainList();
			try {
				numberTrainList.parseJSON(jsonObject);
				if (numberTrainList == null || numberTrainList.getNumberTrains()==null || numberTrainList.getNumberTrains().size()==0) {
					numberTrains.clear();
				} else {
					if (isLoadMore) {
						if (numberTrainList.getNumberTrains().size() < AndroidConfig.zhitongcheDataNUM) {
							isMoreData = false;
						} else {

							isMoreData = true;
						}
						enterNum = true;
						numberTrains.addAll(numberTrainList.getNumberTrains());
					} else {
						if (numberTrainList.getNumberTrains().size() < AndroidConfig.zhitongcheDataNUM) {
							enterNum = false;
							isMoreData = false;
						} else {
							enterNum = true;
							isMoreData = true;
						}

						if (numberTrainList.getNumberTrains().size() == 0) {
							// listView.setVisibility(View.GONE);
							no_data.setVisibility(View.VISIBLE);
						} else {
							listView.setVisibility(View.VISIBLE);
							no_data.setVisibility(View.GONE);
						}

						numberTrains = numberTrainList.getNumberTrains();
					}

					if (isSearch) {

						if (!searchKey.equals("")) {
							tv_search_number.setVisibility(View.VISIBLE);
							tv_search_number.setText("搜索到关于“" + searchKey
									+ "”的结果共" + numberTrains.size() + "个");
						}
					} else {
						view.setVisibility(view.GONE);
						ll_range.setVisibility(view.GONE);
                        ll_industry.setVisibility(view.GONE);
						tv_range.setText("");
						tv_search_number.setVisibility(View.GONE);
					}
				}

			} catch (NetRequestException e) {
				e.getError().print(mContext);
                if (!isLoadMore) {
                    numberTrains.clear();
                }
			}
			adapter.notifyDataSetChanged();
		}

	};
	private LocationClient mLocationClient;
	private String addressname;

	private boolean isFristRefresh = true;

	// 百度定位
	@Override
	public void onReceiveLocation(BDLocation location) {
		if (location == null)
			return;
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		if (isFristRefresh) {
			isFristRefresh = false;
			setLoadMore(false);
			pagerNum = 0;
			InteNetUtils.getInstance(mContext).getStoreList(pagerNum,
					searchKey, location.getLatitude(), location.getLongitude(),
					addressId[0], addressId[1], addressId[2], addressId[3],industryId,
					requestCallBack);
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		// 搜索删除内容
		case R.id.iv_search_content_delect:
			isDelete = true;
			iv_search_content_delect.setVisibility(View.GONE);
			searchKey = "";
			ll_seach_icon.setVisibility(View.VISIBLE);
			search_edittext.setText("");
			// 影藏键盘
			((InputMethodManager) search_edittext.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(mContext.getCurrentFocus()
							.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);

			break;

		// 选择搜索范围
		case R.id.btn_search_range:
            // 更新关键字
            searchKey = search_edittext.getText().toString().trim();
            isLoadMore = false;
            pagerNum = 0;
            enterNum = false;
            if (CommonUtils.isEmpty(searchKey)) {
                isSearch = false;
            } else {
                isSearch = true;
            }

            InteNetUtils.getInstance(mContext).getStoreList(pagerNum,
                    searchKey, latitude, longitude, addressId[0],
                    addressId[1], addressId[2], addressId[3],industryId,
                    requestCallBack);
//			isSearch = true;
//			startAnimActivityForResult2(ActivityChoiceAddress.class,
//					CHOCE_ADDRESS, "level", "0");
			break;
//        case R.id.ll_search_range:
//            isSearch = true;
//            startAnimActivityForResult3(ActivityChoiceAddress.class,
//                    CHOCE_ADDRESS, "level", "0","from","train");
//            break;
//        case R.id.ll_search_industry:
//            isSearch = true;
//            Intent intent = new Intent(this, ActivityChoiceIndusrty.class);
//            intent.putExtra("from","train");
//            startActivityForResult(intent, CHOCE_INDUSTRY);
//            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//            break;
            case R.id.rl_search_area:
                popupWindow.showAsDropDown(rl_search_area, -PixelUtil.dp2px(45), 0);
                break;
            case R.id.tv_search_range:
                isSearch = true;
                startAnimActivityForResult3(ActivityChoiceAddress.class,
                        CHOCE_ADDRESS, "level", "0","from","train");
                popupWindow.dismiss();
                break;
            case R.id.tv_search_industry:
                isSearch = true;
                Intent intent = new Intent(this, ActivityChoiceIndusrty.class);
                intent.putExtra("level","1");
                startActivityForResult(intent, CHOCE_INDUSTRY);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                popupWindow.dismiss();
            default:
                break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case CHOCE_ADDRESS:
			if (data != null) {
				if (resultCode == AndroidConfig.ChoiceAddressResultCode) {
					addressname = data.getStringExtra("address");
					addressId = null;
					addressId = data.getStringArrayExtra("addressId");

					if (addressname.length() > 0) {
						view.setVisibility(view.VISIBLE);
						ll_range.setVisibility(view.VISIBLE);
						tv_range.setText(addressname);
					} else {
						view.setVisibility(view.GONE);
						ll_range.setVisibility(view.GONE);
						tv_range.setText("");
					}

					// if (addressname.length() >= 2) {
					// btn_search_range.setText(addressname.substring(0, 2));
					// } else {
					// btn_search_range.setText("范围");
					// }
					isLoadMore = false;
					pagerNum = 0;
					InteNetUtils.getInstance(mContext).getStoreList(pagerNum,
							searchKey, latitude, longitude, addressId[0],
							addressId[1], addressId[2], addressId[3],industryId,
							requestCallBack);
				}
			}
			break;
        case CHOCE_INDUSTRY:
            if (data != null) {
                ll_industry.setVisibility(View.VISIBLE);
                tv_industry.setText("行业:"+data.getStringExtra("industry"));
                industryId = data.getStringExtra("industryId");
            }else{
                ll_industry.setVisibility(View.GONE);
                industryId = "";
            }
            isLoadMore = false;
            pagerNum = 0;
            InteNetUtils.getInstance(mContext).getStoreList(pagerNum,
                    searchKey, latitude, longitude, addressId[0],
                    addressId[1], addressId[2], addressId[3],industryId,
                    requestCallBack);
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		initlocalData();
		super.onResume();
	}

    /**
     * 创建PopupWindow
     */
    protected void initPopWindow() {
        // TODO Auto-generated method stub
        View popupWindow_view = getLayoutInflater().inflate(R.layout.search_pop_window, null,
                false);
        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popupWindow = new PopupWindow(popupWindow_view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
//        // 设置动画效果
//        popupWindow.setAnimationStyle(R.style.AnimationFade);
        // 点击其他地方消失
        popupWindow_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                return false;
            }
        });
        tv_search_range = (TextView) popupWindow_view.findViewById(R.id.tv_search_range);
        tv_search_industry = (TextView) popupWindow_view.findViewById(R.id.tv_search_industry);
        tv_search_range.setOnClickListener(this);
        tv_search_industry.setOnClickListener(this);
    }
}
