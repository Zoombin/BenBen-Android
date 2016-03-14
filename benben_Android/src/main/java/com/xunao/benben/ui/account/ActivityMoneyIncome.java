package com.xunao.benben.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.User;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.PayResult;
import com.xunao.benben.utils.PayUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by ltf on 2016/1/7.
 */
public class ActivityMoneyIncome extends BaseActivity implements View.OnClickListener {
    private EditText edt_money;
    private TextView tv_money;
    private Button btn_confirm;

    private String from="wallet";
    private String fee="";
    private String order_id="";
    private String order_sn="";

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_money_income);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("转入", "", "",
                R.drawable.icon_com_title_left, 0);
        edt_money = (EditText) findViewById(R.id.edt_money);
        tv_money = (TextView) findViewById(R.id.tv_money);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        from = getIntent().getStringExtra("from");
        if(from.equals("order")){
            order_sn = getIntent().getStringExtra("order_sn");
            fee = getIntent().getStringExtra("payPrice");
            edt_money.setVisibility(View.GONE);
            tv_money.setText(fee);
            tv_money.setVisibility(View.VISIBLE);
        }else{
            edt_money.setVisibility(View.VISIBLE);
            tv_money.setVisibility(View.GONE);
        }
    }

    @Override
    public void initLinstener(Bundle savedInstanceState) {
        setOnLeftClickLinester(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                onBackPressed();
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
        order_id = jsonObject.optString("order_id");
        order_sn = jsonObject.optString("order_sn");
        payOrder();
    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        ToastUtils.Infotoast(mContext,"转入失败");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_confirm:
                if(from.equals("order")) {
                    payOrder();
                }else{
                    fee = String.valueOf(edt_money.getText()).trim();
                    if(fee.equals("")){
                        ToastUtils.Infotoast(mContext,"请输入支付金额");
                    }else{
                        InteNetUtils.getInstance(mContext).Addorder("40", "", "","","", "", "", "", "1", "快递发货",
                                Double.parseDouble(fee), "0",  Double.parseDouble(fee),  Integer.parseInt(fee), 4,mRequestCallBack);
                    }
                }
                break;
        }
    }

    private void payOrder() {
        // 订单
        String orderInfo = PayUtils.getOrderInfo("账户充值", "账户充值", fee, order_sn);
        // 对订单做RSA 签名
        String sign = PayUtils.sign(orderInfo);
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
                + PayUtils.getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(ActivityMoneyIncome.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);

                Message msg = new Message();
                msg.what = 1;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        setResult(RESULT_OK,null);
                        final InfoSimpleMsgHint msgDialog = new InfoSimpleMsgHint(mContext, R.style.MyDialogStyle);
                        msgDialog.setContent("","支付成功，实际到账时间可能会有一定延迟", "确认", "取消");
                        msgDialog.setOKListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                msgDialog.dismiss();
                                if(from.equals("wallet")) {
                                    Intent intent = new Intent(ActivityMoneyIncome.this, ActivityMyAccount.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                                }else{
                                    onBackPressed();
                                }
                            }
                        });
                        msgDialog.show();

                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            ToastUtils.Infotoast(mContext,"支付结果确认中");

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            ToastUtils.Infotoast(mContext,"支付失败");
                        }
                    }


                    break;
                }
//                case SDK_CHECK_FLAG: {
//                    Toast.makeText(PayDemoActivity.this, "检查结果为：" + msg.obj,
//                            Toast.LENGTH_SHORT).show();
//                    break;
//                }
                default:
                    break;
            }
        };
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sendBroadcast(new Intent("orderRefresh"));
        AnimFinsh();
    }
}
