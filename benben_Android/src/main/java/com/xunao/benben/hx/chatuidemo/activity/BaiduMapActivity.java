/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xunao.benben.hx.chatuidemo.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.xunao.benben.R;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.ui.ActivityFindContacts;
import com.xunao.benben.ui.ActivityFindMapAddress;

public class BaiduMapActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = "map";
    static MapView mMapView = null;
    FrameLayout mMapViewContainer = null;
    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    public NotifyLister mNotifyer = null;

    Button sendButton = null;

    EditText indexText = null;
    int index = 0;
    // LocationData locData = null;
//    static BDLocation lastLocation = null;
    public static BaiduMapActivity instance = null;
    ProgressDialog progressDialog;
    private BaiduMap mBaiduMap;

    private LocationMode mCurrentMode;
    private TextView tvaddress;
    private Button btn_go;
//    private LatLng showConvertLatLng;
    private BDLocation startLocation = null;
    private NowLocationListener nowListener = new NowLocationListener();

    private GeoCoder mSearch;
    private LatLng mLatLng = null;
    private String address="";
    private boolean isFirstLoc = true;
    private LinearLayout ll_search;
    private EditText search_edittext;
    private String city="";
    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class BaiduSDKReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            String st1 = getResources().getString(R.string.Network_error);
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {

                String st2 = getResources().getString(R.string.please_check);
                Toast.makeText(instance, st2, Toast.LENGTH_SHORT).show();
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                Toast.makeText(instance, st1, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private BaiduSDKReceiver mBaiduReceiver;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_baidumap);
        tvaddress = (TextView) findViewById(R.id.tvaddress);
        btn_go = (Button) findViewById(R.id.btn_go);
        btn_go.setOnClickListener(this);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapView.showZoomControls(false);
        sendButton = (Button) findViewById(R.id.btn_location_send);
        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("latitude", 0);
        mCurrentMode = LocationMode.NORMAL;
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
        initMapView();
        ll_search = (LinearLayout) findViewById(R.id.ll_search);
        if (latitude == 0) {
            ll_search.setVisibility(View.VISIBLE);
            mMapView = new MapView(this, new BaiduMapOptions());
            mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                    mCurrentMode, true, null));
            showMapWithLocationClient();
        } else {
            ll_search.setVisibility(View.GONE);
            double longtitude = intent.getDoubleExtra("longitude", 0);
            String address = intent.getStringExtra("address");
            tvaddress.setText(address);
            LatLng p = new LatLng(latitude, longtitude);
            mMapView = new MapView(this,
                    new BaiduMapOptions().mapStatus(new MapStatus.Builder()
                            .target(p).build()));
            showMap(latitude, longtitude, address);
        }
        // 注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mBaiduReceiver = new BaiduSDKReceiver();
        registerReceiver(mBaiduReceiver, iFilter);
        search_edittext = (EditText) findViewById(R.id.search_edittext);
        search_edittext.setFocusable(false);
        search_edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(BaiduMapActivity.this, ActivityFindMapAddress.class);
                intent.putExtra("city",city);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                startActivityForResult(intent, 1);

            }
        });
    }

    private void showMap(double latitude, double longtitude, String address) {
        sendButton.setVisibility(View.GONE);
        mLatLng = new LatLng(latitude, longtitude);
//        CoordinateConverter converter= new CoordinateConverter();
//        converter.coord(llA);
//        converter.from(CoordinateConverter.CoordType.COMMON);
//        showConvertLatLng = converter.convert();
        OverlayOptions ooA = new MarkerOptions().position(mLatLng).icon(BitmapDescriptorFactory
                .fromResource(R.drawable.icon_marka))
                .zIndex(4).draggable(true);
        mBaiduMap.addOverlay(ooA);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(mLatLng, 17.0f);
        mBaiduMap.animateMapStatus(u);
        myLocation();
    }

    private void myLocation() {
        String str1 = getResources().getString(R.string.Making_sure_your_location);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(str1);

        progressDialog.setOnCancelListener(new OnCancelListener() {

            public void onCancel(DialogInterface arg0) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Log.d("map", "cancel retrieve location");
                finish();
            }
        });

        progressDialog.show();
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(nowListener);

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); //设置坐标类型
        // Johnson change to use gcj02 coordination. chinese national standard
        // so need to conver to bd09 everytime when draw on baidu map
//        option.setCoorType("gcj02");
        option.setScanSpan(30000);
        option.setAddrType("all");
        mLocClient.setLocOption(option);
    }

    private void showMapWithLocationClient() {
        String str1 = getResources().getString(R.string.Making_sure_your_location);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(str1);

        progressDialog.setOnCancelListener(new OnCancelListener() {

            public void onCancel(DialogInterface arg0) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Log.d("map", "cancel retrieve location");
                finish();
            }
        });

        progressDialog.show();
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        // option.setCoorType("gcj02"); //设置坐标类型
        // Johnson change to use gcj02 coordination. chinese national standard
        // so need to conver to bd09 everytime when draw on baidu map
        option.setCoorType("bd09ll");
        option.setScanSpan(30000);
        option.setAddrType("all");
        mLocClient.setLocOption(option);

        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(listener);
        mBaiduMap.setOnMapStatusChangeListener( new BaiduMap.OnMapStatusChangeListener() {
            /**
             * 手势操作地图，设置地图状态等操作导致地图状态开始改变。
             * @param status 地图状态改变开始时的地图状态
             */
            public void onMapStatusChangeStart(MapStatus status){
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                }
            }
            /**
             * 地图状态变化中
             * @param status 当前地图状态
             */
            public void onMapStatusChange(MapStatus status){
            }
            /**
             * 地图状态改变结束
             * @param status 地图状态改变结束后的地图状态
             */
            public void onMapStatusChangeFinish(MapStatus status){

                mLatLng = status.target;
                mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(mLatLng));

            }
        });
    }

    OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
        public void onGetGeoCodeResult(GeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有检索到结果
            }
            //获取地理编码结果
        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有找到检索结果
            }else{ //获取反向地理编码结果
                sendButton.setEnabled(true);
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                mBaiduMap.clear();
                OverlayOptions ooA = new MarkerOptions().position(mLatLng).icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_marka))
                        .zIndex(4).draggable(true);
                mBaiduMap.addOverlay(ooA);
                address = result.getAddress();
                tvaddress.setText(address);
            }

        }
    };

    @Override
    protected void onPause() {
        mMapView.onPause();
        if (mLocClient != null) {
            mLocClient.stop();
        }
        super.onPause();

    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        if (mLocClient != null) {
            mLocClient.start();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (mLocClient != null)
            mLocClient.stop();
        mMapView.onDestroy();
        if (mSearch != null)
            mSearch.destroy();
        unregisterReceiver(mBaiduReceiver);
        super.onDestroy();
    }
    private void initMapView() {
        mMapView.setLongClickable(true);
    }

    /**
     * 监听函数，有新位置的时候，格式化成字符串，输出到屏幕中
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }

//            sendButton.setEnabled(true);
//            if (progressDialog != null) {
//                progressDialog.dismiss();
//            }
//
//            if (lastLocation != null) {
//                if (lastLocation.getLatitude() == location.getLatitude() && lastLocation.getLongitude() == location.getLongitude()) {
//                    Log.d("map", "same location, skip refresh");
//                    // mMapView.refresh(); //need this refresh?
//                    return;
//                }
//            }
//            Log.d("map", "same location, ok");
//            lastLocation = location;
//            mBaiduMap.clear();
            LatLng llA = new LatLng(location.getLatitude(), location.getLongitude());
//            CoordinateConverter converter= new CoordinateConverter();
//            converter.coord(llA);
//            converter.from(CoordinateConverter.CoordType.COMMON);
//            LatLng convertLatLng = converter.convert();
//            OverlayOptions ooA = new MarkerOptions().position(convertLatLng).icon(BitmapDescriptorFactory
//                    .fromResource(R.drawable.icon_marka))
//                    .zIndex(4).draggable(true);
//            mBaiduMap.addOverlay(ooA);

//            MyLocationData locData = new MyLocationData.Builder()
//                    .accuracy(location.getRadius())
//                            // 此处设置开发者获取到的方向信息，顺时针0-360
//                    .direction(100).latitude(location.getLatitude())
//                    .longitude(location.getLongitude()).build();
//            mBaiduMap.setMyLocationData(locData);
//            // 设置自定义图标
//            BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
//                    .fromResource(R.drawable.);
//            MyLocationConfiguration config = new MyLocationConfiguration( MyLocationConfiguration.LocationMode.NORMAL, true, mCurrentMarker);
//            mBaiduMap.setMyLocationConfigeration(config);
            city = location.getCity();
            if (isFirstLoc) {
                isFirstLoc = false;
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(llA, 17.0f);
                mBaiduMap.animateMapStatus(u);
            }
//            tvaddress.setText(lastLocation.getAddrStr());
        }

        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null) {
                return;
            }
        }
    }

    /**
     * 监听函数，有新位置的时候，格式化成字符串，输出到屏幕中
     */
    public class NowLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            btn_go.setVisibility(View.VISIBLE);
            sendButton.setEnabled(true);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            startLocation = location;

        }

        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null) {
                return;
            }
        }
    }

    public class NotifyLister extends BDNotifyListener {
        public void onNotify(BDLocation mlocation, float distance) {
        }
    }

    public void back(View v) {
        finish();
    }

    public void sendLocation(View view) {
        Intent intent = this.getIntent();
        intent.putExtra("latitude", mLatLng.latitude);
        intent.putExtra("longitude", mLatLng.longitude);
        intent.putExtra("address", address);
        this.setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_go:
                LatLng pt1 = new LatLng(startLocation.getLatitude(),startLocation.getLongitude());

                LatLng pt2 = new LatLng(mLatLng.latitude,mLatLng.longitude);

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
                    final MsgDialog msgDialog = new MsgDialog(BaiduMapActivity.this, R.style.MyDialogStyle);
                    msgDialog.setContent("您尚未安装百度地图app或app版本过低，点击确认安装？", "", "确认", "取消");
                    msgDialog.setCancleListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            msgDialog.dismiss();
                        }
                    });
                    msgDialog.setOKListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            OpenClientUtil.getLatestBaiduMapApp(BaiduMapActivity.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(resultCode==RESULT_OK && data!=null){
                    double latitude = data.getDoubleExtra("latitude",0);
                    double longitude = data.getDoubleExtra("longitude",0);
                    LatLng llA = new LatLng(latitude,longitude);
                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(llA, 17.0f);
                    mBaiduMap.animateMapStatus(u);
                }
                break;
        }
    }
}
