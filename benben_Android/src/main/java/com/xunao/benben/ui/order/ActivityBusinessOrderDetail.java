package com.xunao.benben.ui.order;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Order;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.hx.chatuidemo.activity.ChatActivity;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.groupbuy.ActivityGroupBuyManage;
import com.xunao.benben.ui.promotion.ActivityPromotionManage;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import in.srain.cube.image.CubeImageView;

/**
 * Created by ltf on 2015/12/25.
 */
public class ActivityBusinessOrderDetail extends BaseActivity implements View.OnClickListener {
    private String order_id;
    private String pay_name;
    private Order order;

    private LinearLayout ll_address;
    private TextView tv_name,tv_phone,tv_address;
    private LinearLayout ll_user;
    private RoundedImageView rv_poster;
    private TextView tv_user_name;
    private CubeImageView iv_promotion_post;
    private TextView tv_promotion_name,tv_goods_amount,tv_shipping_fee,tv_goods_number;
    private TextView tv_order_amount,tv_order_fee;
    private TextView tv_order_sn,tv_order_time;
    private LinearLayout ll_order_operate;
    private TextView tv_operate1,tv_operate2,tv_operate3,tv_operate4,tv_operate5,tv_operate6;
    private NewsBrocastReceiver brocastReceiver;
    private MsgDialog msgDialog;
    private LinearLayout ll_promotion;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_business_order_detail);
        brocastReceiver = new NewsBrocastReceiver();
        registerReceiver(brocastReceiver, new IntentFilter("businessOrderRefresh"));
    }

    class NewsBrocastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(CommonUtils.isNetworkAvailable(mContext)){
                InteNetUtils.getInstance(mContext).Storderdetail(order_id, mRequestCallBack);
            }else{
                ToastUtils.Infotoast(mContext, "网络不可用");
            }
        }

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
        ll_user = (LinearLayout) findViewById(R.id.ll_user);
        ll_user.setOnClickListener(this);
        rv_poster = (RoundedImageView) findViewById(R.id.rv_poster);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        ll_promotion = (LinearLayout) findViewById(R.id.ll_promotion);
        ll_promotion.setOnClickListener(this);
        iv_promotion_post = (CubeImageView) findViewById(R.id.iv_promotion_post);
        tv_promotion_name = (TextView) findViewById(R.id.tv_promotion_name);
        tv_goods_amount = (TextView) findViewById(R.id.tv_goods_amount);
        tv_shipping_fee = (TextView) findViewById(R.id.tv_shipping_fee);
        tv_goods_number = (TextView) findViewById(R.id.tv_goods_number);
        tv_order_amount = (TextView) findViewById(R.id.tv_order_amount);
        tv_order_fee = (TextView) findViewById(R.id.tv_order_fee);
        tv_order_sn = (TextView) findViewById(R.id.tv_order_sn);
        tv_order_time = (TextView) findViewById(R.id.tv_order_time);
        ll_order_operate = (LinearLayout) findViewById(R.id.ll_order_operate);
        tv_operate1 = (TextView) findViewById(R.id.tv_operate1);
        tv_operate1.setOnClickListener(this);
        tv_operate2 = (TextView) findViewById(R.id.tv_operate2);
        tv_operate2.setOnClickListener(this);
        tv_operate3 = (TextView) findViewById(R.id.tv_operate3);
        tv_operate3.setOnClickListener(this);
        tv_operate4 = (TextView) findViewById(R.id.tv_operate4);
        tv_operate4.setOnClickListener(this);
        tv_operate5 = (TextView) findViewById(R.id.tv_operate5);
        tv_operate6 = (TextView) findViewById(R.id.tv_operate6);
        tv_operate6.setOnClickListener(this);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        order_id = getIntent().getStringExtra("order_id");
        if(CommonUtils.isNetworkAvailable(mContext)){
            InteNetUtils.getInstance(mContext).Storderdetail(order_id, mRequestCallBack);
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
        ll_user.setVisibility(View.VISIBLE);
        CommonUtils.startImageLoader(cubeimageLoader,order.getUser_poster(),rv_poster);
        tv_user_name.setText(order.getNick_name());
        CommonUtils.startImageLoader(cubeimageLoader,order.getPromotion_pic(),iv_promotion_post);
        tv_promotion_name.setText(order.getGoods_name());
        tv_goods_amount.setText("￥:"+order.getGoods_amount());
        tv_goods_number.setText("数量:x"+order.getGoods_number());
        tv_order_sn.setText("订单号:"+order.getOrder_sn());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm");
        int pay_id = order.getPay_id();
        if(pay_id==1){
            ll_address.setVisibility(View.VISIBLE);
            tv_name.setText(order.getConsignee());
            tv_phone.setText(order.getMobile());
            tv_address.setText(order.getAddress());
            tv_shipping_fee.setText("运费:"+order.getShipping_fee()+"元");
        }

        if(order.getBack_status()==0 || order.getBack_status()==3) {
            if(order.getBack_status()==3){
                ll_order_operate.setVisibility(View.VISIBLE);
                tv_operate5.setText("已拒绝退款");
                tv_operate5.setVisibility(View.VISIBLE);
            }
            if (order.getShipping_status() == 0) {
                if (order.getPay_status() == 2) {
                    Date date = new Date(order.getPay_time()*1000);
                    tv_order_time.setText("付款时间:"+simpleDateFormat.format(date));
                    tv_order_amount.setText("实付款:" + order.getOrder_amount() + "元");
                    if(pay_id==1) {
                        tv_order_fee.setText("(含邮费" + order.getShipping_fee() + "元)");
                        ll_order_operate.setVisibility(View.VISIBLE);
                        tv_operate2.setText("手动发货");
                        tv_operate2.setVisibility(View.VISIBLE);
                    }
                } else{
                    Date date = new Date(order.getAdd_time()*1000);
                    tv_order_time.setText("下单时间:"+simpleDateFormat.format(date));
                    tv_order_amount.setText("待付款:" + order.getOrder_amount());
                    if(pay_id==1) {
                        tv_order_fee.setText("(含邮费" + order.getShipping_fee() + "元)");
                    }
                    if (order.getOrder_status() == 2) {
                        ll_order_operate.setVisibility(View.VISIBLE);
                        tv_operate5.setText("已取消");
                        tv_operate5.setVisibility(View.VISIBLE);
                    }
                }
            } else if (order.getShipping_status() == 1) {
                Date date = new Date(order.getPay_time()*1000);
                tv_order_time.setText("付款时间:"+simpleDateFormat.format(date));
                tv_order_amount.setText("实付款:" + order.getOrder_amount() + "元");
                tv_order_fee.setText("(含邮费" + order.getShipping_fee() + "元)");
                ll_order_operate.setVisibility(View.VISIBLE);
                tv_operate1.setVisibility(View.VISIBLE);
            } else if (order.getShipping_status() == 2) {
                Date date = new Date(order.getPay_time()*1000);
                tv_order_time.setText("付款时间:"+simpleDateFormat.format(date));
                ll_order_operate.setVisibility(View.VISIBLE);
                tv_order_amount.setText("实付款:" + order.getOrder_amount() + "元");
                if(pay_id==1) {
                    tv_order_fee.setText("(含邮费" + order.getShipping_fee() + "元)");
                    tv_operate1.setVisibility(View.VISIBLE);
                }
                ll_order_operate.setVisibility(View.VISIBLE);
                if (order.getOrder_status() == 5 || order.getOrder_status() == 6) {
                    tv_operate2.setText("评价");
                    tv_operate2.setVisibility(View.VISIBLE);
                    tv_operate6.setVisibility(View.VISIBLE);
                }
//                if (order.getOrder_status() == 5) {
//                    tv_operate2.setText("评价");
//                } else if (order.getOrder_status() == 6) {
//                    tv_operate2.setText("查看评价");
//                }
//                tv_operate2.setVisibility(View.VISIBLE);

            }
        }else{
            Date date = new Date(order.getPay_time()*1000);
            tv_order_time.setText("付款时间:"+simpleDateFormat.format(date));
            tv_order_amount.setText("实付款:" + order.getOrder_amount() + "元");
            if(pay_id==1) {
                tv_order_fee.setText("(含邮费" + order.getShipping_fee() + "元)");
            }
            ll_order_operate.setVisibility(View.VISIBLE);
            if(order.getShipping_status()==1){
                tv_operate1.setVisibility(View.VISIBLE);
            }
            if (order.getBack_status() == 1) {
                tv_operate3.setVisibility(View.VISIBLE);
                tv_operate4.setVisibility(View.VISIBLE);
                if(order.getShipping_status()!=1 && pay_id==1){
                    tv_operate2.setText("手动发货");
                    tv_operate2.setVisibility(View.VISIBLE);
                }
            } else if (order.getBack_status() == 2) {
                tv_operate5.setText("已退款");
                tv_operate5.setVisibility(View.VISIBLE);
            } else if (order.getBack_status() == 4 || order.getBack_status() == 5) {
                tv_operate5.setText("退款中");
                tv_operate5.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_user:
                startAnimActivity2Obj(ChatActivity.class, "userId",
                        order.getHuanxin_username());
                break;
            case R.id.ll_promotion:
                if(order.getIs_close()==1){
                    ToastUtils.Infotoast(mContext,"该商品已下架");
                }else if(order.getIs_out()==1){
                    ToastUtils.Infotoast(mContext,"该商品已过期");
                }else{
                    Intent intent = new Intent(ActivityBusinessOrderDetail.this, ActivityGroupBuyManage.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
                break;
            case R.id.tv_operate1:
                msgDialog = new MsgDialog(ActivityBusinessOrderDetail.this, R.style.MyDialogStyle);
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
            case R.id.tv_operate2:
                if(tv_operate2.getText().equals("手动发货")){
                    startAnimActivity2Obj(ActivityManualSend.class,"order_id",order.getOrder_id());
                }else if(tv_operate2.getText().equals("评价")){
                    Intent intent = new Intent(ActivityBusinessOrderDetail.this,ActivityBusinessOrderComment.class);
                    intent.putExtra("order", order);
                    startActivity(intent);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
//                else if(tv_operate2.getText().equals("查看评价")){
//                    Intent intent = new Intent(ActivityBusinessOrderDetail.this,ActivityOrderCommentDetail.class);
//                    intent.putExtra("order", order);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//                }
                break;
            case R.id.tv_operate3:
                msgDialog = new MsgDialog(ActivityBusinessOrderDetail.this, R.style.MyDialogStyle);
                msgDialog.setContent("确定同意退款吗?", "", "确定", "取消");
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
                        Uri uri = Uri.parse(AndroidConfig.NETHOST + AndroidConfig.Agreeback+"?key=android&order_id="+order.getOrder_id()+"&token="+user.getToken());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivityForResult(intent,1);

//                        if (CommonUtils.isNetworkAvailable(mContext)) {
//                            showLoding("");
//
//                            InteNetUtils.getInstance(mContext).Agreeback(order.getOrder_id(), new RequestCallBack<String>() {
//                                @Override
//                                public void onSuccess(ResponseInfo<String> stringResponseInfo) {
//                                    dissLoding();
////                                    Uri uri = Uri.parse(stringResponseInfo.toString());
////                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
////                                    startActivity(intent);
////                                    try {
////                                        JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
////                                        if(jsonObject.optInt("ret_num")==0){
////                                            ToastUtils.Infotoast(mContext, "同意退款成功");
////                                            sendBroadcast(new Intent("businessOrderRefresh"));
////                                            AnimFinsh();
////                                        }else{
////                                            ToastUtils.Infotoast(mContext, jsonObject.optString("ret_msg"));
////                                        }
////                                    } catch (JSONException e) {
////                                        e.printStackTrace();
////                                    }
//                                }
//
//                                @Override
//                                public void onFailure(HttpException e, String s) {
//                                    dissLoding();
//                                    ToastUtils.Infotoast(mContext, "同意退款失败");
//                                }
//                            });
//                        } else {
//                            ToastUtils.Infotoast(mContext, "网络不可用");
//                        }
                    }
                });
                msgDialog.show();
                break;
            case R.id.tv_operate4:
                msgDialog = new MsgDialog(ActivityBusinessOrderDetail.this, R.style.MyDialogStyle);
                msgDialog.setContent("确定拒绝退款吗?", "", "确定", "取消");
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
                            InteNetUtils.getInstance(mContext).Refuse(order.getOrder_id(), new RequestCallBack<String>() {
                                @Override
                                public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                                    dissLoding();
                                    try {
                                        JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                                        if(jsonObject.optInt("ret_num")==0){
                                            ToastUtils.Infotoast(mContext, "拒绝退款成功");
                                            sendBroadcast(new Intent("businessOrderRefresh"));
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
                                    ToastUtils.Infotoast(mContext, "拒绝退款失败");
                                }
                            });
                        } else {
                            ToastUtils.Infotoast(mContext, "网络不可用");
                        }
                    }
                });
                msgDialog.show();
                break;
            case R.id.tv_operate6:
                Intent intent = new Intent(ActivityBusinessOrderDetail.this,ActivityOrderCommentDetail.class);
                intent.putExtra("order", order);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(CommonUtils.isNetworkAvailable(mContext)){
                    InteNetUtils.getInstance(mContext).Storderdetail(order_id, mRequestCallBack);
                    sendBroadcast(new Intent("businessOrderRefresh"));
                }else{
                    ToastUtils.Infotoast(mContext, "网络不可用");
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(brocastReceiver);
    }
}
