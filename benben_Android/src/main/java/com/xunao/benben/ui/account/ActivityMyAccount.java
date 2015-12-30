package com.xunao.benben.ui.account;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.ui.order.ActivityBusinessOrder;
import com.xunao.benben.ui.order.ActivityMyOrder;
import com.xunao.benben.utils.CommonUtils;

import org.json.JSONObject;

/**
 * Created by ltf on 2015/12/19.
 */
public class ActivityMyAccount extends BaseActivity implements View.OnClickListener {
    private RoundedImageView rv_poster;
    private TextView tv_name;
    private LinearLayout ll_my_order;
    private RelativeLayout rl_business_order;

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
        ll_my_order = (LinearLayout) findViewById(R.id.ll_my_order);
        ll_my_order.setOnClickListener(this);
        rl_business_order = (RelativeLayout) findViewById(R.id.rl_business_order);
        rl_business_order.setOnClickListener(this);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        tv_name.setText(user.getUserNickname());
        CommonUtils.startImageLoader(cubeimageLoader,user.getPoster(), rv_poster);
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

    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_my_order:
                startAnimActivity(ActivityMyOrder.class);
                break;
            case R.id.rl_business_order:
                startAnimActivity(ActivityBusinessOrder.class);
                break;
        }
    }
}
