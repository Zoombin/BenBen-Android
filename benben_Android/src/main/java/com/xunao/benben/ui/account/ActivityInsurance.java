package com.xunao.benben.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityWeb;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 保证金页面及其相关操作
 * Created by ltf on 2016/3/11.
 */
public class ActivityInsurance extends BaseActivity implements View.OnClickListener {
    private TextView tv_insurance;
    private Button btn_release_insurance,btn_pay;
    private LinearLayout ll_pay;
    private TextView tv_rule;
    private LinearLayout ll_min_money,ll_money;
    private CheckBox cb_min_money,cb_money;
    private TextView tv_min_money;
    private EditText edt_money;
    private Button btn_go_pay;

    private int minMoney = 1000;
    private int payMoney=0;
    private int payType = 0;
    private String industry="";

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_insurance);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("保证金", "", "",
                R.drawable.icon_com_title_left, 0);
        tv_insurance = (TextView) findViewById(R.id.tv_insurance);
        btn_release_insurance = (Button) findViewById(R.id.btn_release_insurance);
        btn_release_insurance.setOnClickListener(this);
        btn_pay = (Button) findViewById(R.id.btn_pay);
        btn_pay.setOnClickListener(this);
        ll_pay = (LinearLayout) findViewById(R.id.ll_pay);
        tv_rule = (TextView) findViewById(R.id.tv_rule);
        tv_rule.setText(Html.fromHtml("<u>《商家保证金规则》</u>"));
        tv_rule.setOnClickListener(this);

        ll_min_money = (LinearLayout) findViewById(R.id.ll_min_money);
        ll_min_money.setOnClickListener(this);
        ll_money = (LinearLayout) findViewById(R.id.ll_money);
        ll_money.setOnClickListener(this);
        cb_min_money = (CheckBox) findViewById(R.id.cb_min_money);
        cb_money = (CheckBox) findViewById(R.id.cb_money);
        tv_min_money = (TextView) findViewById(R.id.tv_min_money);
        edt_money = (EditText) findViewById(R.id.edt_money);
        btn_go_pay = (Button) findViewById(R.id.btn_go_pay);
        btn_go_pay.setOnClickListener(this);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        if(CommonUtils.isNetworkAvailable(mContext)){
            InteNetUtils.getInstance(mContext).SearchGuarantee(user.getToken(), mRequestCallBack);
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
    }

    @Override
    protected void onHttpStart() {

    }

    @Override
    protected void onLoading(long count, long current, boolean isUploading) {

    }

    @Override
    protected void onSuccess(JSONObject jsonObject) {
        if (jsonObject.optInt("ret_num")==0){
            industry = jsonObject.optString("industry");
            tv_insurance.setText(jsonObject.optString("rest")+"元");
            minMoney = (int) Double.parseDouble(jsonObject.optString("guarantee"));
            tv_min_money.setText(minMoney+"元");
        }else{
            ToastUtils.Infotoast(mContext,jsonObject.optString("ret_msg"));
        }
    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        ToastUtils.Infotoast(mContext,"获取保证金失败");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_release_insurance:
                if(CommonUtils.isNetworkAvailable(mContext)){
                    showLoding("");
                    InteNetUtils.getInstance(mContext).ReleaseGuarantee(new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                            dissLoding();
                            try {
                                JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                                if(jsonObject.optInt("ret_num")==0){
                                    ToastUtils.Infotoast(mContext,"保证金将于24小时内解冻");
                                    AnimFinsh();
                                }else{
                                    ToastUtils.Infotoast(mContext,jsonObject.optString("ret_msg"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            dissLoding();
                            ToastUtils.Infotoast(mContext,"解冻失败");
                        }
                    });
                }
                break;
            case R.id.btn_pay:
                ll_pay.setVisibility(View.VISIBLE);
                btn_go_pay.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_rule:
                Intent intent = new Intent(this, ActivityWeb.class);
                intent.putExtra("title", "保证金规则");
                intent.putExtra("url", AndroidConfig.NETHOST  + "/guarantee/GuaranteeIntroduce");

                startActivity(intent);
                break;
            case R.id.ll_min_money:
                payType = 1;
                cb_min_money.setChecked(true);
                cb_money.setChecked(false);
                break;
            case R.id.ll_money:
                payType = 2;
                cb_min_money.setChecked(false);
                cb_money.setChecked(true);
                break;
            case R.id.btn_go_pay:
                if(payType==1){
                    payMoney = minMoney;
                }else if(payType==2){
                    String money = String.valueOf(edt_money.getText()).trim();
                    if(money.equals("")){
                        payMoney = 0;
                    }else{
                        payMoney = Integer.parseInt(money);
                    }
                }
                if(payMoney==0){
                    ToastUtils.Infotoast(mContext,"请输入额度");
                    return;
                }
                if(payType==2 && payMoney<minMoney){
                    ToastUtils.Infotoast(mContext,"支付额度不得低于最低额度");
                    return;
                }

                final MsgDialog msgDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
                msgDialog.setContent("确定支付吗？", "", "确定", "取消");
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
                            InteNetUtils.getInstance(mContext).GiveGuarantee(industry, payMoney+"", new RequestCallBack<String>() {
                                @Override
                                public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                                    dissLoding();
                                    try {
                                        JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                                        if(jsonObject.optInt("ret_num")==0){
                                            ToastUtils.Infotoast(mContext,"支付成功!");
                                            AnimFinsh();
                                        }else{
                                            ToastUtils.Infotoast(mContext,jsonObject.optString("ret_msg"));
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(HttpException e, String s) {
                                    dissLoding();
                                    ToastUtils.Infotoast(mContext,"支付失败");
                                }
                            });
                        }
                    }

                });
                msgDialog.show();

                break;
        }
    }
}
