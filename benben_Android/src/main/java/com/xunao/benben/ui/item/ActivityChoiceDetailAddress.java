package com.xunao.benben.ui.item;

import javax.microedition.khronos.opengles.GL10;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.android.bbalbs.common.a.a;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapDrawFrameCallback;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMapTouchListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.utils.ToastUtils;

public class ActivityChoiceDetailAddress extends BaseActivity implements
		OnGetGeoCoderResultListener, OnMapClickListener, OnMarkerDragListener,
		OnMarkerClickListener, OnMapStatusChangeListener {
	private String area;

	private MapView mMapView;
	private BaiduMap mBaiduMap;
	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private boolean isShow = true;
	private InfoWindow mInfoWindow;
	private String address;
	private double latitude;
	private double longitude;
	private double[] lats;
	private MapStatusUpdate msu;
	private boolean isTouch;

	private static final int CHOSE_ADDRESS = 1;

	private Marker addOverlay;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_choice_detail_address);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("选择位置", "", "完成",
				R.drawable.icon_com_title_left, 0);
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();

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

		Intent intent = getIntent();
		area = intent.getStringExtra("address");
		String[] areas = area.split(" ");

		lats = intent.getDoubleArrayExtra("lats");

		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		if (lats[0] != 0.0) {
			msu = MapStatusUpdateFactory.newLatLngZoom(new LatLng(lats[0],
					lats[1]), 14.0f);
		} else {
			msu = MapStatusUpdateFactory.zoomTo(14.0f);
		}

		mBaiduMap.setMapStatus(msu);

		if (lats[0] != 0.0) {
			LatLng ptCenter = new LatLng(lats[0], lats[1]);
			mSearch.reverseGeoCode(new ReverseGeoCodeOption()
					.location(ptCenter));
			isShow = true;
		} else {
			mSearch.geocode(new GeoCodeOption().city(areas[1])
					.address(areas[2]));
		}

		mBaiduMap.setOnMapClickListener(this);
		mBaiduMap.setOnMarkerClickListener(this);
		mBaiduMap.setOnMarkerDragListener(this);
		mBaiduMap.setOnMapStatusChangeListener(this);

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

		setOnRightClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (TextUtils.isEmpty(address)) {
					ToastUtils.Infotoast(mContext, "请选择详细地址！");
				} else {
					Intent intent = new Intent();
					intent.putExtra("address", address);
					intent.putExtra("latitude", latitude);
					intent.putExtra("longitude", longitude);
					setResult(CHOSE_ADDRESS, intent);
					AnimFinsh();
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

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {

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
		mMapView.onDestroy();
		mSearch.destroy();
		super.onDestroy();
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
        Log.d("ltf","result============"+result);
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			ToastUtils.Infotoast(mContext, "抱歉，未能找到结果");
			return;
		}
		mBaiduMap.clear();

		if (isShow) {
			addOverlay = (Marker) mBaiduMap.addOverlay(new MarkerOptions()
					.position(result.getLocation())
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.icon_marker))
					.visible(isShow).draggable(true));
		} else {
			addOverlay = (Marker) mBaiduMap.addOverlay(new MarkerOptions()
					.position(result.getLocation())
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.icon_marker))
					.visible(isShow).draggable(true));

		}
        Log.d("ltf","result====1========"+result);
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
				.getLocation()));
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			ToastUtils.Infotoast(mContext, "抱歉，未能找到结果");
			return;
		}
		mBaiduMap.clear();

		if (isShow) {
			addOverlay = (Marker) mBaiduMap.addOverlay(new MarkerOptions()
					.position(result.getLocation())
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.icon_marker))
					.visible(isShow).draggable(true));
			address = result.getAddress();
			// ToastUtils.Infotoast(mContext, result.getAddress());
		} else {
			addOverlay = (Marker) mBaiduMap.addOverlay(new MarkerOptions()
					.position(result.getLocation())
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.icon_marker))
					.visible(isShow).draggable(true));
		}
        latitude = result.getLocation().latitude;
        longitude = result.getLocation().longitude;

		// ToastUtils.Infotoast(mContext, result.getAddress());
	}

	@Override
	public void onMapClick(LatLng latLng) {

		mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
		isShow = true;

		latitude = latLng.latitude;
		longitude = latLng.longitude;
	}

	@Override
	public boolean onMapPoiClick(MapPoi poi) {
		// ToastUtils.Infotoast(mContext, poi.getName());
		return true;
	}

	@Override
	public void onMarkerDrag(Marker arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMarkerDragEnd(Marker marker) {

		mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(marker
				.getPosition()));
		isShow = true;

		latitude = marker.getPosition().latitude;
		longitude = marker.getPosition().longitude;
	}

	@Override
	public void onMarkerDragStart(Marker arg0) {

	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		// Button button = new Button(mContext);
		// button.setBackgroundResource(R.drawable.popup);
		// button.setText("拖动更改位置");
		// OnInfoWindowClickListener listener = null;
		// listener = new OnInfoWindowClickListener() {
		// public void onInfoWindowClick() {
		// mBaiduMap.hideInfoWindow();
		// }
		// };
		// LatLng ll = new LatLng(latitude, longitude);
		// mInfoWindow = new
		// InfoWindow(BitmapDescriptorFactory.fromView(button),
		// ll, -47, listener);
		// mBaiduMap.showInfoWindow(mInfoWindow);
		return false;
	}

	@Override
	public void onMapStatusChange(MapStatus arg0) {
        if(addOverlay!=null) {
            addOverlay.setPosition(arg0.target);
        }
		isShow = true;
	}

	@Override
	public void onMapStatusChangeFinish(MapStatus arg0) {
		mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(arg0.target));
		latitude = arg0.target.latitude;
		longitude = arg0.target.longitude;
	}

	@Override
	public void onMapStatusChangeStart(MapStatus arg0) {
	}

}
