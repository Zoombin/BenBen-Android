package com.xunao.benben.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.utils.PayResult;
import com.xunao.benben.utils.PayUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import in.srain.cube.image.CubeImageView;

/**
 * Created by ltf on 2015/12/23.
 */
public class ActivityOrderPayType extends BaseActivity implements View.OnClickListener {
    private int payType;
    private String order_id,order_sn,shipping_fee,name;
    private String payPrice;
    private int extension_code=0;

    private TextView tv_pay_price,tv_pay_fee;
    private Button btn_confirm;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_order_pay_type);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("支付订单", "", "",
                R.drawable.icon_com_title_left, 0);

        tv_pay_price = (TextView) findViewById(R.id.tv_pay_price);
        tv_pay_fee = (TextView) findViewById(R.id.tv_pay_fee);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        extension_code = getIntent().getIntExtra("extension_code",0);
        payType = getIntent().getIntExtra("payType",0);
        order_id = getIntent().getStringExtra("order_id");
        order_sn = getIntent().getStringExtra("order_sn");
        shipping_fee = getIntent().getStringExtra("shipping_fee");
        payPrice = getIntent().getStringExtra("payPrice");
        name = getIntent().getStringExtra("name");
        tv_pay_price.setText(payPrice+"元");
        if(payType==1){
            tv_pay_fee.setText("(含邮费"+shipping_fee+"元)");
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

    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_confirm:
                final MsgDialog msgDialog = new MsgDialog(ActivityOrderPayType.this, R.style.MyDialogStyle);
                msgDialog.setContent("确定支付该订单吗？", "", "确定", "取消");
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
                        payOrder();
                    }
                });
                msgDialog.show();
                break;
        }
    }

    private void payOrder() {
        // 订单
        String orderInfo = PayUtils.getOrderInfo(name, name, payPrice, order_sn);
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
                PayTask alipay = new PayTask(ActivityOrderPayType.this);
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
                        ToastUtils.Infotoast(mContext, "支付成功");
                        if(extension_code==3 || extension_code==5){
                            sendBroadcast(new Intent("orderRefresh"));
                        }else {
                            startAnimActivity2Obj(ActivityOrderPayResult.class,
                                    "order_id", order_id);
                        }
                        AnimFinsh();
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
}
