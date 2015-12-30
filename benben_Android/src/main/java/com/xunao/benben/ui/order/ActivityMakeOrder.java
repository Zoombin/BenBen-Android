package com.xunao.benben.ui.order;

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
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.account.ActivityAccountAddressManage;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.CircularImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.srain.cube.image.CubeImageView;

/**
 * Created by ltf on 2015/12/16.
 */
public class ActivityMakeOrder extends BaseActivity implements View.OnClickListener {
    private int promotionid;
    private Promotion promotion;
    private CubeImageView iv_poster;
    private TextView tv_title,tv_money,tv_origin_money;
    private TextView tv_total_price;
    private ImageView iv_minus,iv_plus;
    private EditText edt_num;
    private ListView lv_pay_method;
    private MyAdapter payMethodAdapter;
    private List<PayMethod> payMethods = new ArrayList<>();
    private LinearLayout ll_address;
    private TextView tv_pay_money,tv_pay_mail;
    private TextView tv_name,tv_phone,tv_address;
    private TextView tv_pag_msg;
    private Button btn_confirm;

    private int num=0;
    private double totalPrice=0;
    private String shipping_fee="";
    private int payType=0;
    private String payName="";
    private double payPrice=0;
    private AccountAddress accountAddress = new AccountAddress();
    private final int SELECT_ADDRESS=1;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_make_order);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("确认订单", "", "",
                R.drawable.icon_com_title_left, 0);
        iv_poster = (CubeImageView) findViewById(R.id.iv_poster);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_money = (TextView) findViewById(R.id.tv_money);
        tv_origin_money = (TextView) findViewById(R.id.tv_origin_money);
        tv_total_price = (TextView) findViewById(R.id.tv_total_price);
        iv_minus = (ImageView) findViewById(R.id.iv_minus);
        iv_minus.setOnClickListener(this);
        iv_minus.setClickable(false);
        iv_plus = (ImageView) findViewById(R.id.iv_plus);
        iv_plus.setOnClickListener(this);
        edt_num = (EditText) findViewById(R.id.edt_num);
        lv_pay_method = (ListView) findViewById(R.id.lv_pay_method);
        payMethodAdapter = new MyAdapter();
        lv_pay_method.setAdapter(payMethodAdapter);
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
        promotionid = getIntent().getIntExtra("promotionid",0);
        if(CommonUtils.isNetworkAvailable(mContext)){
            InteNetUtils.getInstance(mContext).GroupDetail(promotionid, user.getToken(), mRequestCallBack);
            InteNetUtils.getInstance(mContext).Defaultaddress(addressBack);
        }else{
            ToastUtils.Infotoast(mContext, "网络不可用");
        }
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

        edt_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = String.valueOf(editable).trim();
                if(str.equals("")){
                    num=0;
                }else{
                    num = Integer.parseInt(str);
                }
                if(num==0){
                    iv_minus.setClickable(false);
                    iv_minus.setImageResource(R.drawable.icon_num_minus_grey);
                }else{
                    iv_minus.setClickable(true);
                    iv_minus.setImageResource(R.drawable.icon_num_minus_green);
                }
                initPrice();



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
        try {
            JSONArray posterArray = jsonObject.getJSONArray("poster");
            if(posterArray!=null && posterArray.length()>0) {
                CommonUtils.startImageLoader(cubeimageLoader,posterArray.getJSONObject(0).optString("small_img"),iv_poster);
            }
            promotion = new Promotion();
            promotion.parseJSON(jsonObject);
            tv_title.setText(promotion.getName());
            tv_money.setText(promotion.getPromotion_price()+"");
            tv_origin_money.setText("原价:"+promotion.getOrigion_price()+"元");
            shipping_fee = promotion.getShipping_fee();
            JSONArray jsonArray = jsonObject.getJSONArray("pay_methods");
            if(jsonArray!=null && jsonArray.length()>0) {
                for(int i=0;i<jsonArray.length();i++) {
                    PayMethod payMethod = new PayMethod();
                    payMethod.parseJSON(jsonArray.getJSONObject(i));
                    payMethods.add(payMethod);
                }
                payMethodAdapter.notifyDataSetChanged();
            }
            btn_confirm.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NetRequestException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_plus:
                num++;
                edt_num.setText(num+"");
//                iv_minus.setClickable(true);
//                iv_minus.setImageResource(R.drawable.icon_num_minus_green);
//                initPrice();
                break;
            case R.id.iv_minus:
                num--;
                edt_num.setText(num+"");
//                if(num==0){
//                    iv_minus.setClickable(false);
//                    iv_minus.setImageResource(R.drawable.icon_num_minus_grey);
//                }
//                initPrice();
                break;
            case R.id.ll_address:
                Intent intent = new Intent(ActivityMakeOrder.this, ActivityAccountAddressManage.class);
                startActivityForResult(intent, SELECT_ADDRESS);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
            case R.id.btn_confirm:
                final MsgDialog msgDialog = new MsgDialog(ActivityMakeOrder.this, R.style.MyDialogStyle);
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
        if(num==0){
            ToastUtils.Infotoast(mContext,"请输入数量!");
            return;
        }
        if(payType==0){
            ToastUtils.Infotoast(mContext,"请选择交易方式!");
            return;
        }
        if(payType==1 && (accountAddress.getProvince().equals("0") || accountAddress.getProvince().equals(""))){
            ToastUtils.Infotoast(mContext,"请设置邮寄地址!");
            return;
        }
        if(CommonUtils.isNetworkAvailable(mContext)){
            if(payType==1) {
                InteNetUtils.getInstance(mContext).Addorder(promotionid, accountAddress.getConsignee(), accountAddress.getProvince(), accountAddress.getCity(),
                        accountAddress.getArea(), accountAddress.getStreet(), accountAddress.getAddress(), accountAddress.getMobile(), payType, payName,
                        totalPrice, shipping_fee, payPrice, num, 1,orderBack);
            }else{
                InteNetUtils.getInstance(mContext).Addorder(promotionid, "", "","","", "", "", "", payType, payName,
                        totalPrice, shipping_fee, payPrice, num, 1,orderBack);
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
                    ToastUtils.Infotoast(mContext,"下单成功!");
                    String order_id = jsonObject.optString("order_id");
                    String order_sn = jsonObject.optString("order_sn");
                    if(payType==3){
                        startAnimActivity2Obj(ActivityOrderPayResult.class,
                                "order_id", order_id);
                    }else{
                        Intent intent = new Intent(ActivityMakeOrder.this, ActivityOrderPayType.class);
                        intent.putExtra("payType", payType);
                        intent.putExtra("order_id", order_id);
                        intent.putExtra("order_sn", order_sn);
                        intent.putExtra("payPrice", payPrice+"");
                        intent.putExtra("shipping_fee", shipping_fee);
                        intent.putExtra("name", promotion.getName());
                        startActivity(intent);
                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    }

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
            ToastUtils.Infotoast(mContext,"下单失败!");
        }
    };


    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return payMethods.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return payMethods.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            final PayMethod payMethod = payMethods.get(position);
            final itemHolder itHolder;
            if (convertView == null) {
                itHolder = new itemHolder();
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_pay_method, null);
                itHolder.tv_name = (TextView) convertView
                        .findViewById(R.id.tv_name);
                itHolder.tv_mail_price = (TextView) convertView
                        .findViewById(R.id.tv_mail_price);

                itHolder.cb_item = (CheckBox) convertView
                        .findViewById(R.id.cb_item);
                convertView.setTag(itHolder);
            } else {
                itHolder = (itemHolder) convertView.getTag();
            }
            itHolder.tv_name.setText(payMethod.getPay_name());
            if(Integer.parseInt(payMethod.getPay_id())==payType){
                itHolder.cb_item.setChecked(true);
            }else{
                itHolder.cb_item.setChecked(false);
            }

            if(payMethod.getPay_id().equals("1")){
                itHolder.tv_mail_price.setVisibility(View.VISIBLE);
                itHolder.tv_mail_price.setText(shipping_fee);
            }else{
                itHolder.tv_mail_price.setVisibility(View.GONE);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeType(position);
                }
            });
            return convertView;
        }

        class itemHolder {
            TextView tv_name;
            TextView tv_mail_price;
            CheckBox cb_item;
        }

    }

    private void changeType(int position) {
        PayMethod payMethod = payMethods.get(position);
        if(payMethod.isChecked()){
            payType=0;
            payName = "";
        }else{
            payType = Integer.parseInt(payMethod.getPay_id());
            payName = payMethod.getPay_name();
        }
        initPrice();
        payMethodAdapter.notifyDataSetChanged();
    }

    private void initPrice() {
        totalPrice = promotion.getPromotion_price()*num;
        tv_total_price.setText(totalPrice+"元");
        if(payType==3){
            tv_pag_msg.setText("到店支付总金额:");
        }else{
            tv_pag_msg.setText("在线支付总金额:");
        }
        if(payType==1){
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
