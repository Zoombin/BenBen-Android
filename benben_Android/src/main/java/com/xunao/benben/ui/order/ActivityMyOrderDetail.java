package com.xunao.benben.ui.order;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.NumberTrainDetail;
import com.xunao.benben.bean.Order;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.hx.chatuidemo.activity.ChatActivity;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityNumberTrainDetailMap;
import com.xunao.benben.ui.item.ActivityNumberTrainDetail;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import in.srain.cube.image.CubeImageView;

/**
 * Created by ltf on 2015/12/23.
 */
public class ActivityMyOrderDetail extends BaseActivity implements View.OnClickListener {
    private String order_id;
    private String pay_name;
    private Order order;

    private LinearLayout ll_address;
    private TextView tv_name,tv_phone,tv_address;
    private LinearLayout ll_train;
    private RoundedImageView rv_poster;
    private TextView tv_short_name;
    private CubeImageView iv_promotion_post;
    private TextView tv_promotion_name,tv_goods_amount,tv_shipping_fee,tv_goods_number;
    private TextView tv_order_amount,tv_order_fee;
    private LinearLayout ll_pay_text;
    private TextView tv_order_sn,tv_order_time;
    private RoundedImageView iv_order_code;
    private TextView tv_pay_order,tv_cancel_order,tv_apply_refund,tv_order_status;
    private LinearLayout ll_order_operate;
    private TextView tv_operate1,tv_operate2,tv_operate3;
    private LinearLayout ll_number_train,ll_contact;
    private LocationClient mLocClient;
    private MsgDialog msgDialog;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_order_detail);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        pay_name = getIntent().getStringExtra("pay_name");
        initTitle_Right_Left_bar(pay_name, "", "",
                R.drawable.icon_com_title_left, 0);

        ll_address = (LinearLayout) findViewById(R.id.ll_address);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_address = (TextView) findViewById(R.id.tv_address);
        ll_train = (LinearLayout) findViewById(R.id.ll_train);
        ll_train.setOnClickListener(this);
        rv_poster = (RoundedImageView) findViewById(R.id.rv_poster);
        tv_short_name = (TextView) findViewById(R.id.tv_short_name);
        iv_promotion_post = (CubeImageView) findViewById(R.id.iv_promotion_post);
        tv_promotion_name = (TextView) findViewById(R.id.tv_promotion_name);
        tv_goods_amount = (TextView) findViewById(R.id.tv_goods_amount);
        tv_shipping_fee = (TextView) findViewById(R.id.tv_shipping_fee);
        tv_goods_number = (TextView) findViewById(R.id.tv_goods_number);
        tv_order_amount = (TextView) findViewById(R.id.tv_order_amount);
        tv_order_fee = (TextView) findViewById(R.id.tv_order_fee);
        ll_pay_text = (LinearLayout) findViewById(R.id.ll_pay_text);
        tv_order_sn = (TextView) findViewById(R.id.tv_order_sn);
        tv_order_time = (TextView) findViewById(R.id.tv_order_time);
        iv_order_code = (RoundedImageView) findViewById(R.id.iv_order_code);
        iv_order_code.setOnClickListener(this);
        tv_pay_order = (TextView) findViewById(R.id.tv_pay_order);
        tv_pay_order.setOnClickListener(this);
        tv_cancel_order = (TextView) findViewById(R.id.tv_cancel_order);
        tv_cancel_order.setOnClickListener(this);
        tv_apply_refund = (TextView) findViewById(R.id.tv_apply_refund);
        tv_apply_refund.setOnClickListener(this);
        tv_order_status = (TextView) findViewById(R.id.tv_order_status);
        ll_order_operate = (LinearLayout) findViewById(R.id.ll_order_operate);
        tv_operate1 = (TextView) findViewById(R.id.tv_operate1);
        tv_operate1.setOnClickListener(this);
        tv_operate2 = (TextView) findViewById(R.id.tv_operate2);
        tv_operate2.setOnClickListener(this);
        tv_operate3 = (TextView) findViewById(R.id.tv_operate3);
        tv_operate3.setOnClickListener(this);
        ll_contact = (LinearLayout) findViewById(R.id.ll_contact);
        ll_contact.setOnClickListener(this);
        ll_number_train = (LinearLayout) findViewById(R.id.ll_number_train);
        ll_number_train.setOnClickListener(this);

        initMyLocation();
    }

    /**
     * 初始化定位相关代码
     */
    private void initMyLocation() {
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        // 设置定位的相关配置
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1);
        mLocClient.setLocOption(option);
    }

    private MyLocationListener myListener = new MyLocationListener();
    private double mCurrentLantitude;
    private double mCurrentLongitude;

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            // map view 销毁后不在处理新接收的位置
            if (location == null)
                return;

            mCurrentLantitude = location.getLatitude();
            mCurrentLongitude = location.getLongitude();
        }

    }

    @Override
    protected void onStart() {
        // 开启图层定位
        if (!mLocClient.isStarted()) {
            mLocClient.start();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mLocClient != null)
            mLocClient.stop();
    }
    @Override
    public void initDate(Bundle savedInstanceState) {
        order_id = getIntent().getStringExtra("order_id");
        if(CommonUtils.isNetworkAvailable(mContext)){
            InteNetUtils.getInstance(mContext).Orderdetail(order_id, mRequestCallBack);
        }else{
            ToastUtils.Infotoast(mContext, "网络不可用");
        }
    }

    @Override
    public void initLinstener(Bundle savedInstanceState) {
        setOnLeftClickLinester(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimFinsh();
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
        if(jsonObject.optInt("ret_num")==0){
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("info");
                if(jsonArray!=null && jsonArray.length()>0){
                    order = new Order();
                    order.parseJSON(jsonArray.getJSONObject(0));
                    initOrder();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NetRequestException e) {
                e.printStackTrace();
            }
        }else{
            ToastUtils.Infotoast(mContext,jsonObject.optString("ret_msg"));
        }
    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        ToastUtils.Infotoast(mContext,"获取订单信息失败");
    }

    private void initOrder() {
        ll_train.setVisibility(View.VISIBLE);
        CommonUtils.startImageLoader(cubeimageLoader,order.getStore_pic(),rv_poster);
        tv_short_name.setText(order.getShort_name());
        CommonUtils.startImageLoader(cubeimageLoader,order.getPromotion_pic(),iv_promotion_post);
        tv_promotion_name.setText(order.getGoods_name());
        tv_goods_amount.setText("￥:"+order.getGoods_amount());
        tv_goods_number.setText("数量:x"+order.getGoods_number());
        tv_order_sn.setText("订单号:"+order.getOrder_sn());
        ll_number_train.setVisibility(View.VISIBLE);
        ll_contact.setVisibility(View.VISIBLE);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm");

        int pay_id = order.getPay_id();
        if(pay_id==1){
            ll_address.setVisibility(View.VISIBLE);
            tv_name.setText(order.getConsignee());
            tv_phone.setText(order.getMobile());
            tv_address.setText(order.getAddress());
            tv_shipping_fee.setText("运费:"+order.getShipping_fee()+"元");
        }else if(pay_id==2){
            ll_pay_text.setVisibility(View.VISIBLE);
            iv_order_code.setVisibility(View.VISIBLE);
            CommonUtils.startImageLoader(cubeimageLoader,order.getQrcode(),iv_order_code);

        }else{
            iv_order_code.setVisibility(View.VISIBLE);
            CommonUtils.startImageLoader(cubeimageLoader, order.getQrcode(), iv_order_code);

        }

        if(order.getBack_status()==0) {
            if (order.getShipping_status() == 0) {
                if (order.getPay_status() == 2) {
                    Date date = new Date(order.getPay_time()*1000);
                    tv_order_time.setText("付款时间:"+simpleDateFormat.format(date));
                    tv_order_amount.setText("实付款:" + order.getOrder_amount() + "元");
                    if(pay_id==1) {
                        tv_order_fee.setText("(含邮费" + order.getShipping_fee() + "元)");
                    }
                    tv_apply_refund.setVisibility(View.VISIBLE);
                } else if (order.getPay_status() == 0) {
                    Date date = new Date(order.getAdd_time()*1000);
                    tv_order_time.setText("下单时间:"+simpleDateFormat.format(date));
                    if (pay_id == 3) {
                        tv_order_amount.setText("需到店付款:" + order.getOrder_amount());
                    } else {
                        tv_order_amount.setText("需在线付款:" + order.getOrder_amount());
                        if(pay_id==1) {
                            tv_order_fee.setText("(含邮费" + order.getShipping_fee() + "元)");
                        }
                    }
                    if (order.getOrder_status() == 2) {
                        tv_cancel_order.setText("已取消");
                    } else {
                        tv_cancel_order.setText("取消订单");
                        if (pay_id != 3) {
                            tv_pay_order.setVisibility(View.VISIBLE);
                        }
                    }
                    tv_cancel_order.setVisibility(View.VISIBLE);
                }else {
                    Date date = new Date(order.getPay_time()*1000);
                    tv_order_time.setText("付款时间:"+simpleDateFormat.format(date));
                    if (pay_id == 3) {
                        tv_order_amount.setText("需到店付款:" + order.getOrder_amount());
                    } else {
                        tv_order_amount.setText("需在线付款:" + order.getOrder_amount());
                        if(pay_id==1) {
                            tv_order_fee.setText("(含邮费" + order.getShipping_fee() + "元)");
                        }
                    }
                    tv_pay_order.setText("付款中");
                    tv_pay_order.setVisibility(View.VISIBLE);
                }
            } else if (order.getShipping_status() == 1) {
                Date date = new Date(order.getPay_time()*1000);
                tv_order_time.setText("付款时间:"+simpleDateFormat.format(date));
                tv_order_amount.setText("实付款:" + order.getOrder_amount() + "元");
                tv_order_fee.setText("(含邮费" + order.getShipping_fee() + "元)");
                tv_apply_refund.setVisibility(View.VISIBLE);
                ll_order_operate.setVisibility(View.VISIBLE);
                tv_operate1.setVisibility(View.VISIBLE);
                tv_operate2.setVisibility(View.VISIBLE);
                tv_operate3.setText("确认收货");
                tv_operate3.setVisibility(View.VISIBLE);
            } else if (order.getShipping_status() == 2) {
                Date date = new Date(order.getPay_time()*1000);
                tv_order_time.setText("付款时间:"+simpleDateFormat.format(date));
                ll_order_operate.setVisibility(View.VISIBLE);
                tv_order_amount.setText("实付款:" + order.getOrder_amount() + "元");
                if(pay_id==1) {
                    tv_order_fee.setText("(含邮费" + order.getShipping_fee() + "元)");
                    tv_operate2.setVisibility(View.VISIBLE);
                }
                tv_order_status.setVisibility(View.VISIBLE);
                tv_order_status.setText("交易成功");
                ll_order_operate.setVisibility(View.VISIBLE);
                if (order.getOrder_status() == 5) {
                    tv_operate3.setText("评价");
                } else if (order.getOrder_status() == 6) {
                    tv_operate3.setText("查看评价");
                }
                tv_operate3.setVisibility(View.VISIBLE);
            }
        }else{
            Date date = new Date(order.getPay_time()*1000);
            tv_order_time.setText("付款时间:"+simpleDateFormat.format(date));
            tv_order_amount.setText("实付款:" + order.getOrder_amount() + "元");
            if(pay_id==1) {
                tv_order_fee.setText("(含邮费" + order.getShipping_fee() + "元)");
            }
            if(order.getShipping_status()==1){
                ll_order_operate.setVisibility(View.VISIBLE);
                tv_operate1.setVisibility(View.VISIBLE);
                tv_operate2.setVisibility(View.VISIBLE);
                tv_operate3.setText("确认收货");
                tv_operate3.setVisibility(View.VISIBLE);
            }
            tv_order_status.setVisibility(View.VISIBLE);
            if (order.getBack_status() == 1) {
                tv_order_status.setText("退款申请中");
            } else if (order.getBack_status() == 2) {
                tv_order_status.setText("已退款");
            } else if (order.getBack_status() == 3) {
                tv_order_status.setText("退款申请失败");
            } else if (order.getBack_status() == 4) {
                tv_order_status.setText("退款中");
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_train:
                startAnimActivity2Obj(ActivityNumberTrainDetail.class,
                        "id", order.getTrain_id());
                break;
            case R.id.iv_order_code:
                CommonUtils.showQrCode(mContext, order.getQrcode(),
                        cubeimageLoader);
                break;
            case R.id.tv_cancel_order:
                if(order.getOrder_status()!=2) {
                    final MsgDialog msgDialog = new MsgDialog(ActivityMyOrderDetail.this, R.style.MyDialogStyle);
                    msgDialog.setContent("确定取消该订单吗？", "", "确定", "取消");
                    msgDialog.setCancleListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            msgDialog.dismiss();
                        }
                    });
                    msgDialog.setOKListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            msgDialog.dismiss();
                            if (CommonUtils.isNetworkAvailable(mContext)) {
                                showLoding("");
                                InteNetUtils.getInstance(mContext).Delorder(order.getOrder_id(), new RequestCallBack<String>() {
                                    @Override
                                    public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                                        dissLoding();
                                        try {
                                            JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                                            if(jsonObject.optInt("ret_num")==0){
                                                ToastUtils.Infotoast(mContext, "订单取消成功");
                                                sendBroadcast(new Intent("orderRefresh"));
                                                AnimFinsh();
                                            }else{
                                                ToastUtils.Infotoast(mContext, jsonObject.optString("ret_msg"));
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(HttpException e, String s) {
                                        dissLoding();
                                        ToastUtils.Infotoast(mContext, "订单取消失败");
                                    }
                                });
                            } else {
                                ToastUtils.Infotoast(mContext, "网络不可用");
                            }
                        }
                    });
                    msgDialog.show();
                }
                break;
            case R.id.tv_pay_order:
                if(order.getPay_status()==0) {
                    final MsgDialog msgDialog = new MsgDialog(ActivityMyOrderDetail.this, R.style.MyDialogStyle);
                    msgDialog.setContent("确定支付该订单吗？", "", "确定", "取消");
                    msgDialog.setCancleListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            msgDialog.dismiss();
                        }
                    });
                    msgDialog.setOKListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            msgDialog.dismiss();
                            order_id = order.getOrder_id();
                            Intent intent = new Intent(ActivityMyOrderDetail.this, ActivityOrderPayType.class);
                            intent.putExtra("payType", order.getPay_id());
                            intent.putExtra("order_id", order_id);
                            intent.putExtra("order_sn", order.getOrder_sn());
                            intent.putExtra("payPrice", order.getOrder_amount());
                            intent.putExtra("shipping_fee", order.getShipping_fee());
                            intent.putExtra("name", order.getGoods_name());
                            startActivity(intent);
                            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                            AnimFinsh();
                        }
                    });
                    msgDialog.show();
                }

                break;
            case R.id.tv_apply_refund:
                 msgDialog = new MsgDialog(ActivityMyOrderDetail.this, R.style.MyDialogStyle);
                msgDialog.setContent("确定申请退款吗？", "", "确定", "取消");
                msgDialog.setCancleListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        msgDialog.dismiss();
                    }
                });
                msgDialog.setOKListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        msgDialog.dismiss();
                        if (CommonUtils.isNetworkAvailable(mContext)) {
                            showLoding("");
                            InteNetUtils.getInstance(mContext).Backorder(order.getOrder_id(),order.getTrain_id(), new RequestCallBack<String>() {
                                @Override
                                public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                                    dissLoding();
                                    try {
                                        JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                                        if(jsonObject.optInt("ret_num")==0){
                                            ToastUtils.Infotoast(mContext, "申请退款成功");
                                            sendBroadcast(new Intent("orderRefresh"));
                                            AnimFinsh();
                                        }else{
                                            ToastUtils.Infotoast(mContext, jsonObject.optString("ret_msg"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(HttpException e, String s) {
                                    dissLoding();
                                    ToastUtils.Infotoast(mContext, "申请退款失败");
                                }
                            });
                        } else {
                            ToastUtils.Infotoast(mContext, "网络不可用");
                        }
                    }
                });
                msgDialog.show();
                break;
            case R.id.tv_operate1:
                msgDialog = new MsgDialog(ActivityMyOrderDetail.this, R.style.MyDialogStyle);
                msgDialog.setContent("确定延长收货吗？", "", "确定", "取消");
                msgDialog.setCancleListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        msgDialog.dismiss();
                    }
                });
                msgDialog.setOKListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        msgDialog.dismiss();
                        if (CommonUtils.isNetworkAvailable(mContext)) {
                            showLoding("");
                            InteNetUtils.getInstance(mContext).Extendtime(order.getOrder_id(), new RequestCallBack<String>() {
                                @Override
                                public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                                    dissLoding();
                                    try {
                                        JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                                        if(jsonObject.optInt("ret_num")==0){
                                            ToastUtils.Infotoast(mContext, "延长收货成功");
                                        }else{
                                            ToastUtils.Infotoast(mContext, jsonObject.optString("ret_msg"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(HttpException e, String s) {
                                    dissLoding();
                                    ToastUtils.Infotoast(mContext, "延长收货失败");
                                }
                            });
                        } else {
                            ToastUtils.Infotoast(mContext, "网络不可用");
                        }
                    }
                });
                msgDialog.show();
                break;
            case R.id.tv_operate2:
                msgDialog = new MsgDialog(ActivityMyOrderDetail.this, R.style.MyDialogStyle);
                msgDialog.setContent("查看物流", "运单号:"+order.getShipping_sn(), "复制/查询", "取消");
                msgDialog.setCancleListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        msgDialog.dismiss();
                    }
                });
                msgDialog.setOKListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        msgDialog.dismiss();
                        ClipboardManager mClipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                        ClipData mClipData  = ClipData.newPlainText("text", order.getShipping_sn());
                        mClipboardManager.setPrimaryClip(mClipData);
                        Uri uri = Uri.parse("http://m.kuaidi100.com");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
                msgDialog.show();
                break;
            case R.id.tv_operate3:
                if(tv_operate3.getText().equals("确认收货")){
                    final MsgDialog msgDialog = new MsgDialog(ActivityMyOrderDetail.this, R.style.MyDialogStyle);
                    msgDialog.setContent("确定收货吗？", "", "确定", "取消");
                    msgDialog.setCancleListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            msgDialog.dismiss();
                        }
                    });
                    msgDialog.setOKListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            msgDialog.dismiss();
                            if (CommonUtils.isNetworkAvailable(mContext)) {
                                showLoding("");
                                InteNetUtils.getInstance(mContext).Sureget(order.getOrder_id(), new RequestCallBack<String>() {
                                    @Override
                                    public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                                        dissLoding();
                                        try {
                                            JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                                            if(jsonObject.optInt("ret_num")==0){
                                                ToastUtils.Infotoast(mContext, "已确认收货");
                                                sendBroadcast(new Intent("orderRefresh"));
                                                AnimFinsh();
                                            }else{
                                                ToastUtils.Infotoast(mContext, jsonObject.optString("ret_msg"));
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(HttpException e, String s) {
                                        dissLoding();
                                        ToastUtils.Infotoast(mContext, "确认收货失败");
                                    }
                                });
                            } else {
                                ToastUtils.Infotoast(mContext, "网络不可用");
                            }
                        }
                    });
                    msgDialog.show();
                }else if(tv_operate3.getText().equals("评价")){
                    Intent intent = new Intent(ActivityMyOrderDetail.this,ActivityMyOrderComment.class);
                    intent.putExtra("order", order);
                    startActivity(intent);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    AnimFinsh();
                }else if(tv_operate3.getText().equals("查看评价")){
                    Intent intent = new Intent(ActivityMyOrderDetail.this,ActivityOrderCommentDetail.class);
                    intent.putExtra("order", order);
                    startActivity(intent);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
                break;
            case R.id.ll_number_train:
                showLoding("");
                InteNetUtils.getInstance(mContext).getStoreListDetail(order.getTrain_id(),
                        mCurrentLantitude + "", mCurrentLongitude + "",
                        new RequestCallBack<String>() {
                            @Override
                            public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                                dissLoding();
                                try {
                                    JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                                    if(jsonObject.optInt("ret_num")==0){
                                        NumberTrainDetail numberTrainDetail = new NumberTrainDetail();
                                        JSONObject object = jsonObject.optJSONObject("number_info");
                                        numberTrainDetail.parseJSONMyNumberTrain(object);
                                        startAnimActivity2Obj(ActivityNumberTrainDetailMap.class,
                                                "numberTrain", numberTrainDetail);
                                    }else{
                                        ToastUtils.Infotoast(mContext,jsonObject.optString("ret_msg"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (NetRequestException e) {
                                    e.printStackTrace();
                                }


                            }

                            @Override
                            public void onFailure(HttpException e, String s) {
                                dissLoding();
                                ToastUtils.Infotoast(mContext,"获取商家信息失败");
                            }
                        });
                break;
            case R.id.ll_contact:
                startAnimActivity2Obj(ChatActivity.class, "userId",
                        order.getHuanxin_username());

                break;
        }
    }
}
