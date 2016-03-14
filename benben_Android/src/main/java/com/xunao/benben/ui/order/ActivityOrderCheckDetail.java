package com.xunao.benben.ui.order;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Order;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.srain.cube.image.CubeImageView;

/**
 * Created by ltf on 2015/12/24.
 */
public class ActivityOrderCheckDetail extends BaseActivity implements View.OnClickListener {
    private Order order;

    private TextView tv_order_sn;
    private CubeImageView iv_promotion_post;
    private TextView tv_promotion_name,tv_goods_amount,tv_goods_number,tv_status;
    private TextView tv_order_amount,tv_pay_name;
    private TextView tv_confirm,tv_cancel;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_order_check_detail);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("订单验证", "", "",
                R.drawable.icon_com_title_left, 0);
        tv_order_sn = (TextView) findViewById(R.id.tv_order_sn);
        iv_promotion_post = (CubeImageView) findViewById(R.id.iv_promotion_post);
        tv_promotion_name = (TextView) findViewById(R.id.tv_promotion_name);
        tv_goods_amount = (TextView) findViewById(R.id.tv_goods_amount);
        tv_goods_number = (TextView) findViewById(R.id.tv_goods_number);
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_order_amount = (TextView) findViewById(R.id.tv_order_amount);
        tv_pay_name = (TextView) findViewById(R.id.tv_pay_name);
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_confirm.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        order = (Order) getIntent().getSerializableExtra("order");
        initOrder();
//        if(CommonUtils.isNetworkAvailable(mContext)){
//            InteNetUtils.getInstance(mContext).Checkorder(order_sn, mRequestCallBack);
//        }else{
//            ToastUtils.Infotoast(mContext, "网络不可用");
//        }
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
                }else{
                    ToastUtils.Infotoast(mContext,"无订单信息");
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
        tv_order_sn.setText(order.getOrder_sn());
        CommonUtils.startImageLoader(cubeimageLoader,order.getPromotion_pic(),iv_promotion_post);
        tv_promotion_name.setText(order.getGoods_name());
        tv_goods_amount.setText(order.getGoods_amount());
        tv_goods_number.setText("x"+order.getGoods_number());
        int pay_status = order.getPay_status();
        if(pay_status==0){
            tv_status.setText("未付款");
        }else if(pay_status==1){
            tv_status.setText("付款中");
        }else if(pay_status==2){
            tv_status.setText("已付款");
        }
        tv_order_amount.setText(order.getOrder_amount());
        tv_pay_name.setText(order.getPay_name());
        if(order.getIs_consume()==0){
            tv_confirm.setVisibility(View.VISIBLE);
            tv_cancel.setVisibility(View.VISIBLE);
            tv_cancel.setText("取消消费");
        }else{
            tv_cancel.setVisibility(View.VISIBLE);
            tv_cancel.setText("已消费");
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_confirm:
                final MsgDialog msgDialog = new MsgDialog(ActivityOrderCheckDetail.this, R.style.MyDialogStyle);
                msgDialog.setContent("确定消费该订单吗?", "", "确定", "取消");
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
                            InteNetUtils.getInstance(mContext).Sureconsume(order.getOrder_id(), new RequestCallBack<String>() {
                                @Override
                                public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                                    dissLoding();
                                    try {
                                        JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                                        if(jsonObject.optInt("ret_num")==0){
                                            ToastUtils.Infotoast(mContext, "消费成功");
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
                                    ToastUtils.Infotoast(mContext, "消费失败");
                                }
                            });
                        } else {
                            ToastUtils.Infotoast(mContext, "网络不可用");
                        }
                    }
                });
                msgDialog.show();
                break;
            case R.id.tv_cancel:
                AnimFinsh();
                break;
        }
    }
}
