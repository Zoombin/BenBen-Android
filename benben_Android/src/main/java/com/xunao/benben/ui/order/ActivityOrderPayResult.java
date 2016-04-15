package com.xunao.benben.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Order;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
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
 * 订单支付结果
 * Created by ltf on 2015/12/22.
 */
public class ActivityOrderPayResult extends BaseActivity implements View.OnClickListener {
    private String order_id,extension_code;
    private Order order;

    private TextView tv_order_text1,tv_order_text2;
    private LinearLayout ll_address;
    private TextView tv_name,tv_phone,tv_address;
    private RoundedImageView rv_poster;
    private TextView tv_short_name,tv_title_right;
    private CubeImageView iv_promotion_post;
    private TextView tv_promotion_name,tv_goods_amount,tv_shipping_fee,tv_goods_number,tv_operate;
    private LinearLayout ll_mail_pay;
    private TextView tv_order_amount,tv_order_fee;
    private LinearLayout ll_pay_text;
    private TextView tv_order_sn,tv_order_time;
    private RoundedImageView iv_order_code;
    private Button btn_order,btn_train;
    private int pay_id;
    private LinearLayout ll_train;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_order_pay_result);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("支付结果", "", "",
                R.drawable.icon_com_title_left, 0);
        tv_order_text1 = (TextView) findViewById(R.id.tv_order_text1);
        tv_order_text2 = (TextView) findViewById(R.id.tv_order_text2);
        ll_address = (LinearLayout) findViewById(R.id.ll_address);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_address = (TextView) findViewById(R.id.tv_address);
        rv_poster = (RoundedImageView) findViewById(R.id.rv_poster);
        tv_short_name = (TextView) findViewById(R.id.tv_short_name);
        tv_title_right = (TextView) findViewById(R.id.tv_title_right);
        iv_promotion_post = (CubeImageView) findViewById(R.id.iv_promotion_post);
        tv_promotion_name = (TextView) findViewById(R.id.tv_promotion_name);
        tv_goods_amount = (TextView) findViewById(R.id.tv_goods_amount);
        tv_shipping_fee = (TextView) findViewById(R.id.tv_shipping_fee);
        tv_goods_number = (TextView) findViewById(R.id.tv_goods_number);
        tv_operate = (TextView) findViewById(R.id.tv_operate);
        ll_mail_pay = (LinearLayout) findViewById(R.id.ll_mail_pay);
        tv_order_amount = (TextView) findViewById(R.id.tv_order_amount);
        tv_order_fee = (TextView) findViewById(R.id.tv_order_fee);
        ll_pay_text = (LinearLayout) findViewById(R.id.ll_pay_text);
        tv_order_sn = (TextView) findViewById(R.id.tv_order_sn);
        tv_order_time = (TextView) findViewById(R.id.tv_order_time);
        iv_order_code = (RoundedImageView) findViewById(R.id.iv_order_code);
        ll_train = (LinearLayout) findViewById(R.id.ll_train);

        btn_order = (Button) findViewById(R.id.btn_order);
        btn_train = (Button) findViewById(R.id.btn_train);
        btn_order.setOnClickListener(this);
        btn_train.setOnClickListener(this);
        tv_operate.setOnClickListener(this);
        iv_order_code.setOnClickListener(this);

    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        order_id = getIntent().getStringExtra("order_id");
        extension_code = getIntent().getStringExtra("extension_code");
        if(CommonUtils.isNetworkAvailable(mContext)){
            InteNetUtils.getInstance(mContext).Orderdetail(order_id,extension_code, mRequestCallBack);
        }else{
            ToastUtils.Infotoast(mContext, "网络不可用");
        }
    }

    @Override
    public void initLinstener(Bundle savedInstanceState) {
        setOnLeftClickLinester(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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

    private void initOrder() {
        ll_train.setVisibility(View.VISIBLE);
        CommonUtils.startImageLoader(cubeimageLoader,order.getStore_pic(),rv_poster);
        tv_short_name.setText(order.getShort_name());
        CommonUtils.startImageLoader(cubeimageLoader,order.getPromotion_pic(),iv_promotion_post);
        tv_promotion_name.setText(order.getGoods_name());
        tv_title_right.setText(order.getPay_name());
        tv_goods_amount.setText("￥:"+order.getGoods_amount());
        tv_goods_number.setText("数量:x"+order.getGoods_number());
        tv_order_sn.setText("订单号:"+order.getOrder_sn());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm");
        Date date = new Date(order.getConfirm_time()*1000);
        tv_order_time.setText(simpleDateFormat.format(date));
        pay_id = order.getPay_id();
        if(pay_id==1){
            tv_order_text1.setText("您已成功支付"+order.getOrder_amount()+"元");
            tv_order_text2.setText("请耐心等待您的包裹");
            ll_address.setVisibility(View.VISIBLE);
            tv_name.setText(order.getConsignee());
            tv_phone.setText(order.getMobile());
            tv_address.setText(order.getAddress());
            tv_shipping_fee.setText("运费:"+order.getShipping_fee()+"元");
            tv_operate.setText("申请退款");
            ll_mail_pay.setVisibility(View.VISIBLE);
            tv_order_amount.setText(order.getOrder_amount()+"元");
            tv_order_fee.setText("(含邮费"+order.getShipping_fee()+"元)");
        }else if(pay_id==2){
            tv_order_text1.setText("您已成功支付"+order.getOrder_amount()+"元");
            tv_order_text2.setText("请尽快到店消费噢");
            tv_operate.setText("申请退款");
            ll_pay_text.setVisibility(View.VISIBLE);
            iv_order_code.setVisibility(View.VISIBLE);
            CommonUtils.startImageLoader(cubeimageLoader,order.getQrcode(),iv_order_code);
        }else{
            tv_order_text1.setText("您已成功下单");
            tv_order_text2.setText("请尽快到店消费噢");
            tv_operate.setText("取消订单");
            iv_order_code.setVisibility(View.VISIBLE);
            CommonUtils.startImageLoader(cubeimageLoader,order.getQrcode(),iv_order_code);
        }
        btn_order.setVisibility(View.VISIBLE);
        btn_train.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        ToastUtils.Infotoast(mContext,"获取订单信息失败");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_order:
                Intent orderintent = new Intent(this, ActivityMyOrder.class);
                orderintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(orderintent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                onBackPressed();

                break;
            case R.id.btn_train:
                Intent intent = new Intent(this, ActivityNumberTrainDetail.class);
                intent.putExtra("id", order.getTrain_id());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                onBackPressed();
                break;
            case R.id.tv_operate:
                if(pay_id==3){
                    cancelOrder();
                }else{
                    applyRefund();
                }

                break;
            case R.id.iv_order_code:
                CommonUtils.showQrCode(mContext, order.getQrcode(),
                        cubeimageLoader);
                break;
        }
    }

    private void applyRefund() {
        final MsgDialog msgDialog = new MsgDialog(ActivityOrderPayResult.this, R.style.MyDialogStyle);
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
                            ToastUtils.Infotoast(mContext, "申请退款成功");
                            onBackPressed();
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

    }

    private void cancelOrder() {
        final MsgDialog msgDialog = new MsgDialog(ActivityOrderPayResult.this, R.style.MyDialogStyle);
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
                            ToastUtils.Infotoast(mContext, "订单取消成功");
                            onBackPressed();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sendBroadcast(new Intent("orderRefresh"));
        AnimFinsh();
    }
}
