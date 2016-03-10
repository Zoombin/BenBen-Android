package com.xunao.benben.ui.account;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityMyWeb;
import com.xunao.benben.ui.auction.ActivityTopAuctionNotice;
import com.xunao.benben.ui.order.ActivityBusinessOrder;
import com.xunao.benben.ui.order.ActivityMyOrder;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONObject;

/**
 * Created by ltf on 2015/12/19.
 */
public class ActivityMyAccount extends BaseActivity implements View.OnClickListener {
    private RoundedImageView rv_poster;
    private TextView tv_name;
    private LinearLayout ll_rank_num;
    private TextView tv_level,tv_mean_rate;
    private LinearLayout ll_my_wallet;
    private TextView tv_fee;
    private LinearLayout ll_my_order,ll_collection;
    private TextView tv_my_order,tv_my_collection;
    private RelativeLayout rl_business_order,rl_mall;
    private int[] rankTypes = {R.drawable.icon_rating_select1,R.drawable.icon_rating_select2,R.drawable.icon_rating_select3,R.drawable.icon_rating_select4};
    private String fee="0.00";
    private RelativeLayout rl_auction;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_account);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("我的账户", "", "账单",
                R.drawable.icon_com_title_left, 0);
        rv_poster = (RoundedImageView) findViewById(R.id.rv_poster);
        tv_name = (TextView) findViewById(R.id.tv_name);
        ll_rank_num = (LinearLayout) findViewById(R.id.ll_rank_num);
        tv_level = (TextView) findViewById(R.id.tv_level);
        tv_mean_rate = (TextView) findViewById(R.id.tv_mean_rate);
        ll_my_wallet = (LinearLayout) findViewById(R.id.ll_my_wallet);
        ll_my_wallet.setOnClickListener(this);
        tv_fee = (TextView) findViewById(R.id.tv_fee);
        ll_my_order = (LinearLayout) findViewById(R.id.ll_my_order);
        ll_my_order.setOnClickListener(this);
        ll_collection = (LinearLayout) findViewById(R.id.ll_collection);
        ll_collection.setOnClickListener(this);
        tv_my_order = (TextView) findViewById(R.id.tv_my_order);
        tv_my_collection = (TextView) findViewById(R.id.tv_my_collection);
        rl_business_order = (RelativeLayout) findViewById(R.id.rl_business_order);
        rl_business_order.setOnClickListener(this);
        rl_auction = (RelativeLayout) findViewById(R.id.rl_auction);
        rl_auction.setOnClickListener(this);
        rl_mall = (RelativeLayout) findViewById(R.id.rl_mall);
        rl_mall.setOnClickListener(this);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        tv_name.setText(user.getUserNickname());
        CommonUtils.startImageLoader(cubeimageLoader,user.getPoster(), rv_poster);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(CommonUtils.isNetworkAvailable(mContext)){
            InteNetUtils.getInstance(mContext).MyAccount(mRequestCallBack);
        }else{
            ToastUtils.Infotoast(mContext,"网络不可用");
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

        setOnRightClickLinester(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAnimActivity(ActivityMyBill.class);
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
        if(jsonObject.optInt("ret_msg")==0){
            int rank=jsonObject.optInt("rank");
            if(rank==0){
                ll_rank_num.setVisibility(View.GONE);
            }else{
                ll_rank_num.setVisibility(View.VISIBLE);
                ll_rank_num.removeAllViews();
                int rankType = rank/5;
                int rankNum = rank%5;
                if(rankNum==0){
                    rankType--;
                    rankNum=5;
                }
                for(int i=0;i<rankNum;i++) {
                    ImageView iv_rank = new ImageView(mContext);
                    iv_rank.setImageResource(rankTypes[rankType]);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
                    params.rightMargin = 5;
                    ll_rank_num.addView(iv_rank, params);
                }
            }
            tv_level.setText("("+rank+"级)");
            tv_mean_rate.setText("好评率:"+jsonObject.optString("mean_rate"));
            fee = jsonObject.optString("fee");
            tv_fee.setText(fee);
            tv_my_order.setText("我的订单("+jsonObject.optString("order_num")+")");
            tv_my_collection.setText("我的收藏("+jsonObject.optString("collect_num")+")");
        }else{
            ToastUtils.Infotoast(mContext,jsonObject.optString("ret_msg"));
        }
    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        ToastUtils.Infotoast(mContext,"获取账户信息失败");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_my_wallet:
                startAnimActivity2Obj(ActivityMyWallet.class,"fee",fee);
                break;
            case R.id.ll_my_order:
                startAnimActivity(ActivityMyOrder.class);
                break;
            case R.id.rl_business_order:
                startAnimActivity(ActivityBusinessOrder.class);
                break;
            case R.id.ll_collection:
                startAnimActivity(ActivityMyPromotionCollection.class);
                break;
            case R.id.rl_auction:
                startAnimActivity(ActivityTopAuctionNotice.class);
                break;
            case R.id.rl_mall:
                Intent intent = new Intent(mContext, ActivityMyWeb.class);
                intent.putExtra("title", "奔犇商场");
                intent.putExtra("url", AndroidConfig.NETHOST4 + "/mobileService?token="+user.getToken());
                startActivity(intent);
                break;
        }
    }
}
