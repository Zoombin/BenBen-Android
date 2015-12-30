package com.xunao.benben.ui;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.NumberTrain;
import com.xunao.benben.bean.NumberTrainList;
import com.xunao.benben.dialog.InfoMsgHint;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.MyOrientationListener.OnOrientationListener;
import com.xunao.benben.ui.item.ActivityMyNumberTrainDetail;
import com.xunao.benben.ui.item.ActivityNumberTrainDetail;

public class ActivityNumberTrainMap extends BaseActivity implements
		OnClickListener, OnGetGeoCoderResultListener {
	private MapView mMapView = null;
	private BaiduMap mBaiduMap;
	private Marker mMarker;

	private TextView tv_name;
	private TextView tv_distance;
	private TextView tv_industry;
	private TextView tv_phone;
	private ImageView icon_position;
	private TextView tv_detail;
	private ImageView iv_detail;

	private BDLocation bdLocation;
	private RelativeLayout rl_number_train_info;
	MapStatusUpdate msu;

	private String numberId;
    private String shop;
	private String searchKey = "";
	private EditText search_edittext;
	private LinearLayout ll_seach_icon;
    private String hxName;

	private LinearLayout ll_search_item;
	private boolean isShowSearch = false;
	private double latitude; // 维度
	private double longitude;// 经度
	private NumberTrainList numberTrainList;
	private ArrayList<NumberTrain> numberTrains = new ArrayList<NumberTrain>();
	private ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
	// 初始化全局 bitmap 信息，不用时及时 recycle
	private BitmapDescriptor bd;

	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private Marker curMarker;
	private TextView tv_info;

	private ImageView iv_choice;
	LocationClient mLocClient;
	private LocationMode mCurrentMode;
	private boolean isDw = false;
	public MyLocationListener myListener = new MyLocationListener();
	BitmapDescriptor mCurrentMarker;
	private LocationClientOption option;

	private String[] areas;
	private String address = "";
	
	private RelativeLayout on_dingwei;
    private int position;

	/***
	 * 是否是第一次定位
	 */
	private volatile boolean isFristLocation = true;
	/**
	 * 最新一次的经纬度
	 */
	private double mCurrentLantitude;
	private double mCurrentLongitude;
	/**
	 * 当前的精度
	 */
	private float mCurrentAccracy;
	/**
	 * 方向传感器的监听器
	 */
	private MyOrientationListener myOrientationListener;
	/**
	 * 方向传感器X方向的值
	 */
	private int mXDirection;

    private TextView tv_go;

	@Override
	public void loadLayout(Bundle savedInstanceState) {

		bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
		setContentView(R.layout.activity_number_train_map);

		mCurrentMode = LocationMode.NORMAL;

	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("号码直通车", "", "",
				R.drawable.icon_com_title_left, 0);

		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_distance = (TextView) findViewById(R.id.tv_distance);
		tv_industry = (TextView) findViewById(R.id.tv_industry);
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		icon_position = (ImageView) findViewById(R.id.icon_position);
		ll_search_item = (LinearLayout) findViewById(R.id.ll_search_item);
		rl_number_train_info = (RelativeLayout) findViewById(R.id.rl_number_train_info);
		tv_info = (TextView) findViewById(R.id.tv_info);

		iv_choice = (ImageView) findViewById(R.id.iv_choice);

		tv_phone.setVisibility(View.GONE);
		tv_industry.setVisibility(View.GONE);

		ll_search_item.setVisibility(View.VISIBLE);
        tv_go = (TextView) findViewById(R.id.tv_go);
        tv_go.setOnClickListener(this);


		search_edittext = (EditText) findViewById(R.id.search_edittext);
		((TextView) findViewById(R.id.searchName)).setText("商铺简称/服务项目/店铺号");
		ll_seach_icon = (LinearLayout) findViewById(R.id.ll_seach_icon);

		tv_detail = (TextView) findViewById(R.id.tv_detail);
		iv_detail = (ImageView) findViewById(R.id.iv_detail);
		

		on_dingwei = (RelativeLayout) findViewById(R.id.on_dingwei);
		
		on_dingwei = (RelativeLayout) findViewById(R.id.on_dingwei);
		
		LocationManager locationManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);
		gps  = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (gps ) {
			on_dingwei.setVisibility(View.GONE);	
		}else{
			on_dingwei.setVisibility(View.VISIBLE);	
		} 
		
		on_dingwei.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
		
		tv_detail.setOnClickListener(this);
		iv_detail.setOnClickListener(this);

		Intent intent = getIntent();
		numberTrains = (ArrayList<NumberTrain>) intent
				.getSerializableExtra("numberTrain");

		addressName  = intent.getStringExtra("addressName");

		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMyLocationEnabled(true);

		// 影藏放大缩小
		int childCount = mMapView.getChildCount();
		View zoom = null;
		for (int i = 0; i < childCount; i++) {
			View child = mMapView.getChildAt(i);
			if (child instanceof ZoomControls) {
				zoom = child;
				break;
			}
		}
		zoom.setVisibility(View.GONE);

		mBaiduMap = mMapView.getMap();

		// 设置地图中心点以及缩放级别
		String[] address = new String[4];
		if (addressName  != null) {
			areas = addressName .split(" ");
		}

		if (addressName  != null) {
			if (areas.length > 0) {
				if (areas.length == 1) {
					address[0] = areas[0];
					address[1] = areas[0];
					address[2] = areas[0];
					address[3] = areas[0];
				} else if (areas.length == 2) {
					address[0] = areas[0];
					address[1] = areas[1];
					address[2] = areas[0];
					address[3] = areas[1];
				} else if (areas.length == 3) {
					address[0] = areas[0];
					address[1] = areas[1];
					address[2] = areas[2];
					address[3] = areas[0];
				} else if (areas.length == 4) {
					address[0] = areas[0];
					address[1] = areas[1];
					address[2] = areas[2];
					address[3] = areas[3];
				}
				mSearch = GeoCoder.newInstance();
				mSearch.setOnGetGeoCodeResultListener(this);
				mSearch.geocode(new GeoCodeOption().city(address[0]).address(
						address[1] + address[2] + address[3]));
			} else {
				msu = MapStatusUpdateFactory.zoomTo(14.0f);
				mBaiduMap.setMapStatus(msu);
			}
		} else {
			msu = MapStatusUpdateFactory.zoomTo(14.0f);
			mBaiduMap.setMapStatus(msu);
		}

		initMyLocation();

		mCurrentMarker = null;
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, mCurrentMarker));
		

		iv_choice.setVisibility(View.GONE);

		rl_number_train_info.setVisibility(View.GONE);

		initOverlay();

		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				rl_number_train_info.setVisibility(View.VISIBLE);
				if (curMarker != null) {
					curMarker.setIcon(bd);
				}
				curMarker = marker;
				marker.setIcon(BitmapDescriptorFactory
						.fromResource(R.drawable.location_icon_blue));
				initGo(marker.getZIndex());
				return true;
			}
		});
		initOritationListener();
	}

	/**
	 * 初始化定位相关代码
	 */
	private void initMyLocation() {
		// 定位初始化
		mLocClient = new LocationClient(this);
		if(gps){
			mLocClient.registerLocationListener(myListener);
			// 设置定位的相关配置
			LocationClientOption option = new LocationClientOption();
			option.setOpenGps(true);// 打开gps
			option.setCoorType("bd09ll"); // 设置坐标类型
			option.setScanSpan(1);
			mLocClient.setLocOption(option);
		}else{
			mLocClient.unRegisterLocationListener(myListener);
		}
		
	}

	@Override
	protected void onStart() {
		// 开启图层定位
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocClient.isStarted()) {
			mLocClient.start();
		}
		// 开启方向传感器
		myOrientationListener.start();

		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mLocClient != null)
			mLocClient.stop();
		// 关闭图层定位
		mBaiduMap.setMyLocationEnabled(false);
		// 关闭方向传感器
		myOrientationListener.stop();
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
	}

	private void initGo(int item) {
        position = item;
		if (numberTrains.size() <= 0) {
			tv_name.setText("暂无数据");
			tv_distance.setVisibility(View.GONE);
			tv_phone.setVisibility(View.GONE);
			tv_industry.setVisibility(View.GONE);
			tv_detail.setVisibility(View.GONE);
			iv_detail.setVisibility(View.GONE);
			icon_position.setVisibility(View.GONE);
		} else {
			numberId = numberTrains.get(item).getId();
            shop = numberTrains.get(item).getShop();
			tv_name.setText(numberTrains.get(item).getShortName());
			tv_industry.setText(numberTrains.get(item).getTag());
			tv_distance
					.setText(numberTrains.get(item).getDistance_kilometers());
			tv_phone.setText(numberTrains.get(item).getPhone());
			tv_info.setTag(numberTrains.get(item).getDescription());
			tv_distance.setVisibility(View.VISIBLE);
			// tv_phone.setVisibility(View.VISIBLE);
			tv_industry.setVisibility(View.VISIBLE);
			tv_detail.setVisibility(View.VISIBLE);
			iv_detail.setVisibility(View.VISIBLE);
			icon_position.setVisibility(View.VISIBLE);
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
				isShowSearch = true;
				ll_search_item.setVisibility(View.VISIBLE);
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
					ll_seach_icon.setVisibility(View.GONE);
				} else {
					ll_seach_icon.setVisibility(View.VISIBLE);
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
					int pagerNum = 0;
					InteNetUtils.getInstance(mContext).getStoreList(pagerNum,
							searchKey, mCurrentLantitude, mCurrentLongitude,
							"", "", "","", mRequestCallBack);
					return true;
				}
				return false;
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
			rl_number_train_info.setVisibility(View.GONE);
			numberTrainList = new NumberTrainList();
			numberTrainList = (NumberTrainList) numberTrainList
					.parseJSON(jsonObject);
			if (numberTrainList == null) {
				numberTrains.clear();
				clearOverlay(mMapView);
			} else {
				numberTrains = numberTrainList.getNumberTrains();
				resetOverlay(mMapView);
			}
		} catch (NetRequestException e) {
			e.getError().print(mContext);
			e.printStackTrace();
			return;
		}
	}

	private float[] accelerometerValues = new float[3];
	private float[] magneticFieldValues = new float[3];
	private String addressName = "";
	private boolean gps = false;

	/**
	 * 初始化方向传感器
	 */
	private void initOritationListener() {
		myOrientationListener = new MyOrientationListener(
				getApplicationContext());
		myOrientationListener
				.setOnOrientationListener(new OnOrientationListener() {
					@Override
					public void onOrientationChanged(float x) {
						mXDirection = (int) x;

						// 构造定位数据
						MyLocationData locData = new MyLocationData.Builder()
								.accuracy(mCurrentAccracy)
								// 此处设置开发者获取到的方向信息，顺时针0-360
								.direction(mXDirection)
								.latitude(mCurrentLantitude)
								.longitude(mCurrentLongitude).build();
						// 设置定位数据
						mBaiduMap.setMyLocationData(locData);
						// 设置自定义图标
						BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
								.fromResource(R.drawable.navi_map_gps_locked);
						MyLocationConfiguration config = new MyLocationConfiguration(
								mCurrentMode, true, mCurrentMarker);
						mBaiduMap.setMyLocationConfigeration(config);

					}
				});
	}

	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {

			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			// 构造定位数据
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(mXDirection).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mCurrentAccracy = location.getRadius();
			// 设置定位数据
			mBaiduMap.setMyLocationData(locData);
			mCurrentLantitude = location.getLatitude();
			mCurrentLongitude = location.getLongitude();

			// 设置自定义图标
			BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
					.fromResource(R.drawable.navi_map_gps_locked);
			MyLocationConfiguration config = new MyLocationConfiguration(
					mCurrentMode, true, mCurrentMarker);
			mBaiduMap.setMyLocationConfigeration(config);
			// 第一次定位时，将地图位置移动到当前位置
//			if (isFristLocation) {
//				isFristLocation = false;
//				LatLng ll = new LatLng(location.getLatitude(),
//						location.getLongitude());
//				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
//				mBaiduMap.animateMapStatus(u);
//			}
			
//			if("".equals(addressName)){
			if(null == addressName){
				isFristLocation = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {

	}

	private void initOverlay() {
		if (numberTrains.size() > 0) {
			for (int i = 0; i < numberTrains.size(); i++) {
				LatLng ll = new LatLng(numberTrains.get(i).getLat(),
						numberTrains.get(i).getLng());
				OverlayOptions oo = new MarkerOptions().position(ll).icon(bd)
						.zIndex(i).draggable(true);
				mMarker = (Marker) (mBaiduMap.addOverlay(oo));
				giflist.add(bd);
			}
		}
	}

	@Override
	protected void onPause() {
		// MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
		mMapView.onDestroy();
		super.onDestroy();
		// 回收 bitmap 资源
		bd.recycle();

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.tv_detail:
		case R.id.iv_detail:
            if(shop.contains(user.getBenbenId())){
                Intent intent = new Intent(mContext,
                        ActivityMyNumberTrainDetail.class);
                intent.putExtra("id", numberId);
                intent.putExtra("Lan", mCurrentLantitude);
                intent.putExtra("long", mCurrentLongitude);
                this.startActivity(intent);
                this.overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
            }else{
                Intent intent = new Intent(mContext,
                        ActivityNumberTrainDetail.class);
                intent.putExtra("id", numberId);
                intent.putExtra("Lan", mCurrentLantitude);
                intent.putExtra("long", mCurrentLongitude);
                this.startActivity(intent);
                this.overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
            }



			break;
        case R.id.tv_go:

            LatLng pt1 = new LatLng(mCurrentLantitude,mCurrentLongitude);
            LatLng pt2 = new LatLng(numberTrains.get(position).getLat(),numberTrains.get(position).getLng());

            NaviParaOption para = new NaviParaOption();
            para.startPoint(pt1);
            para.startName("从这里开始");
            para.endPoint(pt2);
            para.endName("从这里开始");

            try {
//                    BaiduMapNavigation.setSupportWebNavi(false);
                BaiduMapNavigation.openBaiduMapNavi(para, this);

            } catch (BaiduMapAppNotSupportNaviException e) {
//                    e.printStackTrace();e.printStackTrace();
                final MsgDialog msgDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
                msgDialog.setContent("您尚未安装百度地图app或app版本过低，点击确认安装？", "", "确认", "取消");
                msgDialog.setCancleListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        msgDialog.dismiss();
                    }
                });
                msgDialog.setOKListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OpenClientUtil.getLatestBaiduMapApp(ActivityNumberTrainMap.this);
//                            BaiduMapNavigation.
//                            BaiduMapNavigation.GetLatestBaiduMapApp(ActivityNumberTrainDetailMap.this);
                        msgDialog.dismiss();
                    }
                });
                msgDialog.show();
            }

            break;


		default:
			break;
		}
	}

	/**
	 * 清除所有Overlay
	 * 
	 * @param view
	 */
	public void clearOverlay(View view) {
		mBaiduMap.clear();
	}

	/**
	 * 重新添加Overlay
	 * 
	 * @param view
	 */
	public void resetOverlay(View view) {
		clearOverlay(null);
		initOverlay();
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result != null) {
			msu = MapStatusUpdateFactory.newLatLngZoom(result.getLocation(),
					14.0f);
			mBaiduMap.setMapStatus(msu);
		}
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result != null) {
			msu = MapStatusUpdateFactory.newLatLngZoom(result.getLocation(),
					14.0f);
			mBaiduMap.setMapStatus(msu);
		}
	}

}
