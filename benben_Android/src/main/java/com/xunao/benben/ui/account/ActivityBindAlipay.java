package com.xunao.benben.ui.account;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.RSAUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONObject;

/**
 * 支付宝绑定
 * Created by ltf on 2016/3/11.
 */
public class ActivityBindAlipay extends BaseActivity implements View.OnClickListener {
    private EditText edt_account,edt_name;
    private Button btn_bind;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_bind_alipay);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("支付宝绑定", "", "",
                R.drawable.icon_com_title_left, 0);
        edt_account = (EditText) findViewById(R.id.edt_account);
        edt_name = (EditText) findViewById(R.id.edt_name);
        btn_bind = (Button) findViewById(R.id.btn_bind);
        btn_bind.setOnClickListener(this);
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
        if(jsonObject.optInt("ret_num")==0){
            ToastUtils.Infotoast(mContext,"绑定成功");
            AnimFinsh();
        }else{
            ToastUtils.Infotoast(mContext,jsonObject.optString("ret_msg"));
        }
    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        ToastUtils.Infotoast(mContext,"绑定失败");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_bind:
                String account = String.valueOf(edt_account.getText()).trim();
                String name = String.valueOf(edt_name.getText()).trim();
                if(account.equals("") || name.equals("")){
                    ToastUtils.Infotoast(mContext, "请输入账号及姓名！");
                }else {
                    String afterencrypt = RSAUtils.encryptByPublic(account + ";" + name);
                    if(CommonUtils.isNetworkAvailable(mContext)){
                        InteNetUtils.getInstance(mContext).PayBind(afterencrypt,"1",mRequestCallBack);
                    }
                }

                break;
        }
    }
}
