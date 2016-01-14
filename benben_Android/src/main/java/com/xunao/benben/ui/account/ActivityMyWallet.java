package com.xunao.benben.ui.account;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;

import org.json.JSONObject;

/**
 * Created by ltf on 2016/1/4.
 */
public class ActivityMyWallet extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_address;
    private TextView tv_fee;
    private TextView tv_income;

    private String fee="0.00";

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_wallet);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("我的钱包", "", "",
                R.drawable.icon_com_title_left, 0);
        tv_fee = (TextView) findViewById(R.id.tv_fee);
        tv_income = (TextView) findViewById(R.id.tv_income);
        tv_income.setOnClickListener(this);
        rl_address = (RelativeLayout) findViewById(R.id.rl_address);
        rl_address.setOnClickListener(this);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        fee = getIntent().getStringExtra("fee");
        tv_fee.setText(fee);
    }

    @Override
    public void initLinstener(Bundle savedInstanceState) {
        setOnLeftClickLinester(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
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
            case R.id.tv_income:
                startAnimActivity2Obj(ActivityMoneyIncome.class,"from","wallet");
                break;
            case R.id.rl_address:
                startAnimActivity(ActivityAccountAddressManage.class);
                break;
        }
    }
}
