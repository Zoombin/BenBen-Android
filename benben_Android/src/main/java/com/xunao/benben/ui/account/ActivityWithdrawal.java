package com.xunao.benben.ui.account;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ltf on 2016/3/15.
 */
public class ActivityWithdrawal extends BaseActivity implements View.OnClickListener {
    private TextView tv_account,tv_name;
    private EditText edt_money;
    private Button btn_withdrawals;


    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_withdrawals);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("现金提现", "", "解除绑定",
                R.drawable.icon_com_title_left, 0);
        tv_account = (TextView) findViewById(R.id.tv_account);
        tv_name = (TextView) findViewById(R.id.tv_name);
        edt_money = (EditText) findViewById(R.id.edt_money);
        btn_withdrawals = (Button) findViewById(R.id.btn_withdrawals);
        btn_withdrawals.setOnClickListener(this);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        String account = getIntent().getStringExtra("account");
        String[] strings = account.split(";");
        tv_account.setText(strings[0]);
        tv_name.setText(strings[1]);
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
                final MsgDialog msgDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
                msgDialog.setContent("确定解除绑定吗？", "", "确定", "取消");
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
                        if (CommonUtils.isNetworkAvailable(mContext)){
                            showLoding("");
                            InteNetUtils.getInstance(mContext).PayUnbind("1", new RequestCallBack<String>() {
                                @Override
                                public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                                    dissLoding();
                                    try {
                                        JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                                        if (jsonObject.optInt("ret_num") == 0) {
                                            ToastUtils.Infotoast(mContext, "解绑成功!");
                                            AnimFinsh();
                                        } else {
                                            ToastUtils.Infotoast(mContext, jsonObject.optString("ret_msg"));
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(HttpException e, String s) {
                                    dissLoding();
                                    ToastUtils.Infotoast(mContext, "解绑失败");
                                }
                            });
                        }
                    }

                });
                msgDialog.show();
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
        Log.d("ltf","jsonObject=========="+jsonObject);
        if(jsonObject.optInt("ret_num")==0){
            ToastUtils.Infotoast(mContext,"提现成功!");
            AnimFinsh();
        }else{
            ToastUtils.Infotoast(mContext,jsonObject.optString("ret_msg"));
        }
    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        ToastUtils.Infotoast(mContext,"提现失败");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_withdrawals:
                String moneyStr = String.valueOf(edt_money.getText()).trim();
                if(moneyStr.equals("")){
                    ToastUtils.Infotoast(this, "请输入提现金额");
                }else{
                    double money = Double.parseDouble(moneyStr);
                    if(money==0){
                        ToastUtils.Infotoast(this,"提现金额不能为0！");
                    }else{
                       if (CommonUtils.isNetworkAvailable(mContext)){
                           InteNetUtils.getInstance(mContext).PayCashOut(moneyStr,"1",mRequestCallBack);
                       }
                    }

                }
                break;
        }
    }
}
