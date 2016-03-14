package com.xunao.benben.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Order;
import com.xunao.benben.hx.chatuidemo.activity.ChatActivity;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.item.ActivityNumberTrainDetail;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONObject;

import in.srain.cube.image.CubeImageView;

/**
 * Created by ltf on 2015/12/25.
 */
public class ActivityBusinessOrderComment extends BaseActivity implements View.OnClickListener {
    private Order order;
    private LinearLayout ll_train;
    private RoundedImageView rv_poster;
    private TextView tv_short_name,tv_pay_name;
    private CubeImageView iv_promotion_post;
    private TextView tv_promotion_name,tv_goods_amount,tv_shipping_fee,tv_goods_number;
    private RadioButton rb_3,rb_2,rb_1;
    private EditText edt_content;
    private Button btn_comment;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_order_comment);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("评价", "", "",
                R.drawable.icon_com_title_left, 0);
        ll_train = (LinearLayout) findViewById(R.id.ll_train);
        ll_train.setOnClickListener(this);
        rv_poster = (RoundedImageView) findViewById(R.id.rv_poster);
        tv_short_name = (TextView) findViewById(R.id.tv_short_name);
        tv_pay_name = (TextView) findViewById(R.id.tv_pay_name);
        iv_promotion_post = (CubeImageView) findViewById(R.id.iv_promotion_post);
        tv_promotion_name = (TextView) findViewById(R.id.tv_promotion_name);
        tv_goods_amount = (TextView) findViewById(R.id.tv_goods_amount);
        tv_shipping_fee = (TextView) findViewById(R.id.tv_shipping_fee);
        tv_goods_number = (TextView) findViewById(R.id.tv_goods_number);
        rb_3 = (RadioButton) findViewById(R.id.rb_3);
        rb_2 = (RadioButton) findViewById(R.id.rb_2);
        rb_1 = (RadioButton) findViewById(R.id.rb_1);
        edt_content = (EditText) findViewById(R.id.edt_content);
        btn_comment = (Button) findViewById(R.id.btn_comment);
        btn_comment.setOnClickListener(this);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        order = (Order) getIntent().getSerializableExtra("order");
        CommonUtils.startImageLoader(cubeimageLoader, order.getUser_poster(), rv_poster);
        tv_short_name.setText(order.getNick_name());
        tv_pay_name.setText(order.getPay_name());
        CommonUtils.startImageLoader(cubeimageLoader,order.getPromotion_pic(),iv_promotion_post);
        tv_promotion_name.setText(order.getGoods_name());
        tv_goods_amount.setText("￥:"+order.getGoods_amount());
        if(order.getPay_id()==1){
            tv_shipping_fee.setText("运费:"+order.getShipping_fee()+"元");
        }
        tv_goods_number.setText("数量:x"+order.getGoods_number());
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
            ToastUtils.Infotoast(mContext,"评价成功!");
            sendBroadcast(new Intent("businessOrderRefresh"));
            AnimFinsh();
        }else{
            ToastUtils.Infotoast(mContext,jsonObject.optString("ret_msg"));
        }
    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        ToastUtils.Infotoast(mContext,"评价失败!");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_train:
                startAnimActivity2Obj(ChatActivity.class, "userId",
                        order.getHuanxin_username());
                break;
            case R.id.btn_comment:
                int comment_rank = getRank();
                if(comment_rank==0){
                    ToastUtils.Infotoast(mContext,"请选择评定等级!");
                    return;
                }
                String content = String.valueOf(edt_content.getText()).trim();
                if(content.equals("")){
                    ToastUtils.Infotoast(mContext,"请输入评价内容!");
                    return;
                }
                if(CommonUtils.isNetworkAvailable(mContext)){
                    InteNetUtils.getInstance(mContext).Stordercomment(order.getPromotion_id(),order.getOrder_id(),1,content,comment_rank, mRequestCallBack);
                }else{
                    ToastUtils.Infotoast(mContext, "网络不可用");
                }
                break;
        }
    }

    private int getRank() {
        int rank=0;
        if(rb_3.isChecked()){
            rank = 3;
            return rank;
        }
        if(rb_2.isChecked()){
            rank = 2;
            return rank;
        }
        if(rb_1.isChecked()){
            rank = 1;
            return rank;
        }

        return rank;

    }
}
