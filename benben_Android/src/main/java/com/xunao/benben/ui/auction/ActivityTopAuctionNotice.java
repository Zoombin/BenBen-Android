package com.xunao.benben.ui.auction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.ui.account.ActivityAccountAddressManage;
import com.xunao.benben.ui.account.ActivityMoneyIncome;

import org.json.JSONObject;

/**
 * Created by ltf on 2016/1/7.
 */
public class ActivityTopAuctionNotice extends BaseActivity implements View.OnClickListener {
    private Button btn_cancel,btn_confirm;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_top_auction_notice);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("置顶拍卖场", "", "",
                R.drawable.icon_com_title_left, 0);

        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_cancel.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {

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
            case R.id.btn_cancel:
                AnimFinsh();
                break;
            case R.id.btn_confirm:
                startAnimActivity(ActivityTopAuction.class);
                AnimFinsh();
                break;
        }
    }
}
