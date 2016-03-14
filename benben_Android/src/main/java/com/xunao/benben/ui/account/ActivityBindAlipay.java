package com.xunao.benben.ui.account;

import android.os.Bundle;
import android.view.View;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;

import org.json.JSONObject;

/**
 * Created by ltf on 2016/3/11.
 */
public class ActivityBindAlipay extends BaseActivity{
    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_bind_alipay);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("支付宝绑定", "", "",
                R.drawable.icon_com_title_left, 0);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {

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
}
