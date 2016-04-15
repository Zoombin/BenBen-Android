package com.xunao.benben.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.RSAUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 我的钱包
 * Created by ltf on 2016/1/4.
 */
public class ActivityMyWallet extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_address,rl_insurance;
    private TextView tv_fee;
    private TextView tv_income,tv_outcome;
    private boolean isAlipayBind = false;
    private String alipayAccount = "";

//    private String fee="0.00";

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
        tv_outcome = (TextView) findViewById(R.id.tv_outcome);
        tv_outcome.setOnClickListener(this);
        rl_address = (RelativeLayout) findViewById(R.id.rl_address);
        rl_address.setOnClickListener(this);
        rl_insurance = (RelativeLayout) findViewById(R.id.rl_insurance);
        rl_insurance.setOnClickListener(this);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
//        fee = getIntent().getStringExtra("fee");
//        tv_fee.setText(fee);
        if(CommonUtils.isNetworkAvailable(mContext)){
            InteNetUtils.getInstance(mContext).GetPayAccount(mRequestCallBack);
        }
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
        if(jsonObject.optInt("ret_num")==0){
            isAlipayBind = false;
            alipayAccount = "";
            tv_fee.setText(jsonObject.optString("allmoney"));
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                if(jsonArray!=null && jsonArray.length()>0){
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        int type = object.optInt("type");
                        if(type==1){
                            isAlipayBind = true;
                            alipayAccount = RSAUtils.decryptByPublic(object.optString("account"));
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            ToastUtils.Infotoast(mContext,jsonObject.optString("ret_msg"));
        }

    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        ToastUtils.Infotoast(mContext,"获取信息失败");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_income:
                startAnimActivity2Obj(ActivityMoneyIncome.class, "from", "wallet");
                break;
            case R.id.tv_outcome:
                if(isAlipayBind){
                    Intent intent = new Intent(this, ActivityWithdrawal.class);
                    intent.putExtra("account",alipayAccount);
                    startActivityForResult(intent, 1);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }else {
                    startAnimActivityForResult(ActivityBindAlipay.class, 1);
                }
                break;
            case R.id.rl_address:
                startAnimActivity(ActivityAccountAddressManage.class);
                break;
            case R.id.rl_insurance:
                startAnimActivityForResult(ActivityInsurance.class, 1);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(CommonUtils.isNetworkAvailable(mContext)){
                    InteNetUtils.getInstance(mContext).GetPayAccount(mRequestCallBack);
                }
                break;
        }
    }
}
