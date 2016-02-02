package com.xunao.benben.ui;

import java.util.ArrayList;

import javax.crypto.spec.IvParameterSpec;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.location.GpsStatus.NmeaListener;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.NumberTrainDetail;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.ui.MyOrientationListener.OnOrientationListener;
import com.xunao.benben.utils.ToastUtils;

public class ActivityNumberTrainDetailMap extends BaseActivity implements
		OnMarkerClickListener, OnClickListener {
	private NumberTrainDetail numberTrainDetail;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;

	MapView mMapView;
	BaiduMap mBaiduMap;
	boolean isFirstLoc = true;// 是否首次定位
	private boolean isDw = false;
	private Marker mMarkerA;
	private BDLocation bdLocation;
	MapStatusUpdate msu;
	// 初始化全局 bitmap 信息，不用时及时 recycle
	BitmapDescriptor bdA = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_marka);

	// 初始化全局 bitmap 信息，不用时及时 recycle
	private BitmapDescriptor bd;
	private LocationClient mLocClient;

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
	private TextView tv_name;
	private TextView tv_distance;
	private TextView tv_industry;
	private RelativeLayout rl_number_train_info;
	private ImageView icon_position;
	private TextView tv_info;
	private ImageView iv_choice;
	private TextView tv_detail;
	private ImageView iv_detail;
    private TextView tv_go;
	
	private RelativeLayout on_dingwei;
	private boolean gps;

	@Override
	protected void onStop() {
		super.onStop();
		if (mLocClient != null)
			mLocClient.stop();

		myOrientationListener.start();
	}

	@Override
	protected void onStart() {

		// 开启方向传感器
		myOrientationListener.start();
		super.onStart();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCurrentMode = LocationMode.NORMAL;
		bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();

		mBaiduMap.setMyLocationEnabled(true);
		initOverlay();
		
		LocationManager locationManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);
		gps = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		if(gps){
			
			mLocClient = new LocationClient(mApplication);
			LocationClientOption option = new LocationClientOption();
			option.setLocationMode(com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
			option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度，默认值gcj02
			option.setNeedDeviceDirect(true);
			option.setScanSpan(1000);// 设置发起定位请求的间隔时间为5000ms
			option.setIsNeedAddress(true);
			mLocClient.setLocOption(option);
			mLocClient.registerLocationListener(myListener);
			mLocClient.start();
		}

		
		on_dingwei = (RelativeLayout) findViewById(R.id.on_dingwei);
		
		
	
		// boolean network =
		// locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (gps) {
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
		
		
		

		mCurrentMarker = null;
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, mCurrentMarker));
		// mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
		// mCurrentMode, true, null));
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
		msu = MapStatusUpdateFactory.newLatLngZoom(
				new LatLng(numberTrainDetail.getNumberLat(), numberTrainDetail
						.getNumberLng()), 14.0f);
		mBaiduMap.setOnMarkerClickListener(this);
		mBaiduMap.setMapStatus(msu);
		initOritationListener();
	}

	private void initOverlay() {
		LatLng llA = new LatLng(numberTrainDetail.getNumberLat(),
				numberTrainDetail.getNumberLng());
		OverlayOptions oo = new MarkerOptions().position(llA).icon(bd)
				.zIndex(1).draggable(true);
		ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
		mMarkerA = (Marker) (mBaiduMap.addOverlay(oo));
		giflist.add(bd);
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_number_train_detail_map);
		rl_number_train_info = (RelativeLayout) findViewById(R.id.rl_number_train_info);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_distance = (TextView) findViewById(R.id.tv_distance);
		tv_industry = (TextView) findViewById(R.id.tv_industry);
		icon_position = (ImageView) findViewById(R.id.icon_position);
		iv_choice = (ImageView) findViewById(R.id.iv_choice);
		tv_info = (TextView) findViewById(R.id.tv_info);
		tv_detail = (TextView) findViewById(R.id.tv_detail);
		iv_detail = (ImageView) findViewById(R.id.iv_detail);
        tv_go = (TextView) findViewById(R.id.tv_go);
        tv_go.setOnClickListener(this);
		
		if(!gps){
			iv_choice.setVisibility(View.GONE);
			iv_choice.setOnClickListener(null);
		}
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		numberTrainDetail = (NumberTrainDetail) getIntent()
				.getSerializableExtra("numberTrain");

		initTitle_Right_Left_bar("商家位置", "", "",
				R.drawable.icon_com_title_left, 0);
		tv_name.setVisibility(View.VISIBLE);
		tv_industry.setVisibility(View.VISIBLE);
		tv_name.setText(numberTrainDetail.getShortName());
		tv_industry.setText(numberTrainDetail.getTag());
		tv_distance.setText(numberTrainDetail.getKil());
		tv_info.setTag(numberTrainDetail.getDescription());
		tv_distance.setVisibility(View.VISIBLE);
		// tv_phone.setVisibility(View.VISIBLE);
		icon_position.setVisibility(View.VISIBLE);
		tv_detail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AnimFinsh();
			}
		});
		iv_detail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AnimFinsh();
			}
		});

	}

	@Override
	public void initDate(Bundle savedInstanceState) {
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AnimFinsh();
			}
		});

		iv_choice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (isDw) {
					iv_choice.setImageResource(R.drawable.ic_number_train_dw);
					msu = MapStatusUpdateFactory.newLatLngZoom(
							new LatLng(numberTrainDetail.getNumberLat(),
									numberTrainDetail.getNumberLng()), 14.0f);
					isDw = false;
				} else {
					iv_choice.setImageResource(R.drawable.ic_number_train_sj);
					msu = MapStatusUpdateFactory.newLatLngZoom(
							new LatLng(bdLocation.getLatitude(), bdLocation
									.getLongitude()), 14.0f);
					isDw = true;

				}
				mBaiduMap.setMapStatus(msu);
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

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {

	}

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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_go:

                LatLng pt1 = new LatLng(mCurrentLantitude,mCurrentLongitude);
                LatLng pt2 = new LatLng(numberTrainDetail.getNumberLat(),numberTrainDetail.getNumberLng());

                NaviParaOption para = new NaviParaOption();
                para.startPoint(pt1);
                para.startName("从这里开始");
                para.endPoint(pt2);
                para.endName("到这里结束");

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
                            OpenClientUtil.getLatestBaiduMapApp(ActivityNumberTrainDetailMap.this);
//                            BaiduMapNavigation.
//                            BaiduMapNavigation.GetLatestBaiduMapApp(ActivityNumberTrainDetailMap.this);
                            msgDialog.dismiss();
                        }
                    });
                    msgDialog.show();
                }

                break;
        }
    }

    /**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

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
			bdLocation = location;
			mCurrentLantitude = location.getLatitude();
			mCurrentLongitude = location.getLongitude();
			// 设置自定义图标
			BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
					.fromResource(R.drawable.navi_map_gps_locked);
			MyLocationConfiguration config = new MyLocationConfiguration(
					mCurrentMode, true, mCurrentMarker);
			mBaiduMap.setMyLocationConfigeration(config);
			// 第一次定位时，将地图位置移动到当前位置
			// if (isFristLocation) {
			// isFristLocation = false;
			// LatLng ll = new LatLng(location.getLatitude(),
			// location.getLongitude());
			// MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			// mBaiduMap.animateMapStatus(u);
			// }

		}

	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		if(mLocClient != null){
			mLocClient.stop();
		}
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
		bd.recycle();
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
	public boolean onMarkerClick(Marker arg0) {
		rl_number_train_info.setVisibility(View.VISIBLE);
		return false;
	}

}
