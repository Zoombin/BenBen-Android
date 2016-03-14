package com.xunao.benben.ui.mybuy;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.AccountAddress;
import com.xunao.benben.bean.PayMethod;
import com.xunao.benben.bean.Promotion;
import com.xunao.benben.bean.QuoteContent;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.account.ActivityAccountAddressManage;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.image.CubeImageView;

/**
 * Created by ltf on 2016/3/11.
 */
public class ActivityAcceptPrice extends BaseActivity implements View.OnClickListener {

    private TextView tv_pay_name,tv_mail_price;
    private LinearLayout ll_address;
    private TextView tv_pay_money,tv_pay_mail;
    private TextView tv_name,tv_phone,tv_address;
    private TextView tv_pag_msg;
    private Button btn_confirm;

    private double totalPrice=0;
    private String shipping_fee="";
    private String payId="";
    private String payName="";
    private double payPrice=0;
    private AccountAddress accountAddress = new AccountAddress();
    private final int SELECT_ADDRESS=1;
    private String buyid;
    private QuoteContent quoteContent;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_accept_price);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("接受报价", "", "",
                R.drawable.icon_com_title_left, 0);
        tv_pay_name = (TextView) findViewById(R.id.tv_pay_name);
        tv_mail_price = (TextView) findViewById(R.id.tv_mail_price);
        ll_address = (LinearLayout) findViewById(R.id.ll_address);
        ll_address.setOnClickListener(this);
        tv_pay_money = (TextView) findViewById(R.id.tv_pay_money);
        tv_pay_mail = (TextView) findViewById(R.id.tv_pay_mail);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_pag_msg = (TextView) findViewById(R.id.tv_pag_msg);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        buyid = getIntent().getStringExtra("buyid");
        quoteContent = (QuoteContent) getIntent().getSerializableExtra("quoteContent");
        if(quoteContent!=null) {
            totalPrice = Double.parseDouble(quoteContent.getPrice());
            tv_pay_name.setText(quoteContent.getPay_name());
            payId = quoteContent.getPay_id();
            if(payId.equals("1")){
                tv_mail_price.setVisibility(View.VISIBLE);
                shipping_fee = quoteContent.getShipping_fee();
                tv_mail_price.setText("邮费:"+shipping_fee);
            }else{
                tv_mail_price.setVisibility(View.GONE);
            }
        }
        initPrice();
        if(CommonUtils.isNetworkAvailable(mContext)){
            InteNetUtils.getInstance(mContext).Defaultaddress(addressBack);
        }else{
            ToastUtils.Infotoast(mContext, "网络不可用");
        }
    }

    private void initPrice() {
        if(payId.equals("3")){
            tv_pag_msg.setText("到店支付总金额:");
        }else{
            tv_pag_msg.setText("在线支付总金额:");
        }
        if(payId.equals("1")){
            payPrice = totalPrice+Double.parseDouble(shipping_fee);
            tv_pay_mail.setText("含邮费("+shipping_fee+")元");
            ll_address.setVisibility(View.VISIBLE);
        }else{
            payPrice = totalPrice;
            tv_pay_mail.setText("");
            ll_address.setVisibility(View.GONE);
        }
        tv_pay_money.setText(payPrice+"元");
    }

    RequestCallBack<String> addressBack = new RequestCallBack<String>() {
        @Override
        public void onSuccess(ResponseInfo<String> stringResponseInfo) {
            try {
                JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                if(jsonObject.optInt("ret_num")==0){
                    accountAddress = new AccountAddress();
                    try {
                        accountAddress.parseJSON(jsonObject);
                        tv_name.setText(accountAddress.getConsignee());
                        tv_phone.setText(accountAddress.getMobile());
                        tv_address.setText(accountAddress.getAddress_name());
                    } catch (NetRequestException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(HttpException e, String s) {
            accountAddress = new AccountAddress();
            tv_name.setText(accountAddress.getConsignee());
            tv_phone.setText(accountAddress.getMobile());
            tv_address.setText(accountAddress.getAddress_name());
        }
    };

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
            case R.id.ll_address:
                Intent intent = new Intent(ActivityAcceptPrice.this, ActivityAccountAddressManage.class);
                startActivityForResult(intent, SELECT_ADDRESS);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
            case R.id.btn_confirm:
                final MsgDialog msgDialog = new MsgDialog(ActivityAcceptPrice.this, R.style.MyDialogStyle);
                msgDialog.setContent("确定下单吗？", "", "确定", "取消");
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
                        makeOrder();
                    }
                });
                msgDialog.show();


                break;
        }
    }

    private void makeOrder() {
        if(payId.equals("")){
            ToastUtils.Infotoast(mContext,"请选择交易方式!");
            return;
        }
        if(payId.equals("1") && (accountAddress.getProvince().equals("0") || accountAddress.getProvince().equals(""))){
            ToastUtils.Infotoast(mContext,"请设置邮寄地址!");
            return;
        }
        if(CommonUtils.isNetworkAvailable(mContext)){
            showLoding("");
            if(payId.equals("1")) {
                InteNetUtils.getInstance(mContext).Addorder(quoteContent.getId()+"", accountAddress.getConsignee(), accountAddress.getProvince(), accountAddress.getCity(),
                        accountAddress.getArea(), accountAddress.getStreet(), accountAddress.getAddress(), accountAddress.getMobile(), payId, payName,
                        totalPrice, shipping_fee, payPrice, 1, 2,orderBack);
            }else{
                InteNetUtils.getInstance(mContext).Addorder(quoteContent.getId()+"", "", "","","", "", "", "", payId, payName,
                        totalPrice, shipping_fee, payPrice, 1, 2,orderBack);
            }
        }else{
            ToastUtils.Infotoast(mContext, "网络不可用!");
        }
    }

    RequestCallBack<String> orderBack = new RequestCallBack<String>() {
        @Override
        public void onSuccess(ResponseInfo<String> stringResponseInfo) {
            try {
                JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                Log.d("ltf","jsonObject=============="+jsonObject);
                if(jsonObject.optInt("ret_num")==0){
                    String order_id = jsonObject.optString("order_id");
                    String order_sn = jsonObject.optString("order_sn");
                    InteNetUtils.getInstance(mContext).acceptPrice(buyid,quoteContent.getStore_id(),quoteContent.getId() + "",
                            new RequestCallBack<String>() {
                                @Override
                                public void onFailure(HttpException arg0,String arg1) {
                                    dissLoding();
                                }

                                @Override
                                public void onSuccess(ResponseInfo<String> arg0) {
                                    dissLoding();
                                    setResult(RESULT_OK,null);
                                    final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(
                                            mContext, R.style.MyDialog1);
                                    hint.setContent("已提交订单，请去订单列表确认或支付");
                                    hint.setBtnContent("知道了");
                                    hint.show();
                                    hint.setOKListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            hint.dismiss();
                                            AnimFinsh();

                                        }
                                    });
                                    hint.show();



                                }
                            });
                }else{
                    dissLoding();
                    ToastUtils.Infotoast(mContext,jsonObject.optString("ret_msg"));
                }

            } catch (JSONException e) {
                dissLoding();
                e.printStackTrace();
            }


        }

        @Override
        public void onFailure(HttpException e, String s) {
            dissLoding();
            ToastUtils.Infotoast(mContext, "下单失败!");
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SELECT_ADDRESS:
                if(resultCode==RESULT_OK && data!=null){
                    boolean isUpdate = data.getBooleanExtra("isUpdate",false);
                    accountAddress = (AccountAddress) data.getSerializableExtra("accountAddress");
                    if(accountAddress!=null) {
                        tv_name.setText(accountAddress.getConsignee());
                        tv_phone.setText(accountAddress.getMobile());
                        tv_address.setText(accountAddress.getAddress_name());
                    }else if(isUpdate){
                        InteNetUtils.getInstance(mContext).Defaultaddress(addressBack);
                    }
                }
                break;
        }
    }
}
