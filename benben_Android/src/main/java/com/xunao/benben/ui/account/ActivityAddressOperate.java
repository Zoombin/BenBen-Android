package com.xunao.benben.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.AccountAddress;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.item.ActivityChoiceAddress;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ltf on 2015/12/16.
 */
public class ActivityAddressOperate extends BaseActivity implements View.OnClickListener {
    private LinearLayout ll_district;
    private static final int CHOCE_ADDRESS = 4;
    private TextView tv_district;
    // 记录了地区的id
    private String[] addressIds = new String[4];
    private Button btn_confirm;
    private EditText edt_consignee,edt_mobile,edt_address;
    private AccountAddress accountAddress;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_address_operate);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        accountAddress = (AccountAddress) getIntent()
                .getSerializableExtra("accountAddress");
        if(accountAddress==null) {
            initTitle_Right_Left_bar("新增地址", "", "",
                    R.drawable.icon_com_title_left, 0);
        }else{
            initTitle_Right_Left_bar("编辑地址", "", "",
                    R.drawable.icon_com_title_left, 0);
        }
        ll_district = (LinearLayout) findViewById(R.id.ll_district);
        ll_district.setOnClickListener(this);
        tv_district = (TextView) findViewById(R.id.tv_district);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        edt_consignee = (EditText) findViewById(R.id.edt_consignee);
        edt_mobile = (EditText) findViewById(R.id.edt_mobile);
        edt_address = (EditText) findViewById(R.id.edt_address);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        if(accountAddress!=null){
            edt_consignee.setText(accountAddress.getConsignee());
            edt_mobile.setText(accountAddress.getMobile());
            String district = accountAddress.getAddress_name();
            district = district.replace(accountAddress.getAddress(),"");
            tv_district.setText(district);
            edt_address.setText(accountAddress.getAddress());
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

    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_district:
                startAnimActivityForResult3(ActivityChoiceAddress.class,
                        CHOCE_ADDRESS, "trim", "trim", "level", "0");
                break;
            case R.id.btn_confirm:
                if(accountAddress!=null){
                    edtAddress();
                }else {
                    addAddress();
                }
                break;
        }
    }

    private void addAddress() {
        String consignee = String.valueOf(edt_consignee.getText()).trim();
        if (TextUtils.isEmpty(consignee)) {
            ToastUtils.Errortoast(mContext, "收货人姓名不可为空!");
            return;
        }
//        if (!CommonUtils.StringIsSurpass2(consignee, 1, 12)) {
//            ToastUtils.Errortoast(mContext, "收货人姓名限制在12个字之内!");
//            return;
//        }
        String mobile = String.valueOf(edt_mobile.getText()).trim();
        if (TextUtils.isEmpty(mobile)) {
            ToastUtils.Errortoast(mContext, "联系电话不可为空!");
            return;
        }
        if ((tv_district.getText().toString().trim())
                .equals("")) {
            ToastUtils.Infotoast(mContext, "请选择所在地区！");
            return;
        }
        String address = String.valueOf(edt_address.getText()).trim();
        if (TextUtils.isEmpty(address)) {
            ToastUtils.Errortoast(mContext, "详细地址不可为空!");
            return;
        }

        if(CommonUtils.isNetworkAvailable(mContext)){
            showLoding("");
            int is_default = 0;
            InteNetUtils.getInstance(mContext).Addaddress(consignee,addressIds , address,"",mobile,is_default ,user.getToken(), addCallBack);
        }else{
            ToastUtils.Infotoast(mContext, "网络不可用");
        }
    }

    public RequestCallBack<String> addCallBack = new RequestCallBack<String>() {

        @Override
        public void onSuccess(ResponseInfo<String> stringResponseInfo) {
            dissLoding();
            try {
                JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                if(jsonObject.optInt("ret_num")==0){
                    ToastUtils.Infotoast(mContext,"添加成功");
                    setResult(RESULT_OK,null);
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
            ToastUtils.Infotoast(mContext,"添加失败");
        }
    };

    private void edtAddress() {
        String consignee = String.valueOf(edt_consignee.getText()).trim();
        if (TextUtils.isEmpty(consignee)) {
            ToastUtils.Errortoast(mContext, "收货人姓名不可为空!");
            return;
        }
//        if (!CommonUtils.StringIsSurpass2(consignee, 1, 12)) {
//            ToastUtils.Errortoast(mContext, "收货人姓名限制在12个字之内!");
//            return;
//        }
        String mobile = String.valueOf(edt_mobile.getText()).trim();
        if (TextUtils.isEmpty(mobile)) {
            ToastUtils.Errortoast(mContext, "联系电话不可为空!");
            return;
        }
        if ((tv_district.getText().toString().trim())
                .equals("")) {
            ToastUtils.Infotoast(mContext, "请选择所在地区！");
            return;
        }
        String address = String.valueOf(edt_address.getText()).trim();
        if (TextUtils.isEmpty(address)) {
            ToastUtils.Errortoast(mContext, "详细地址不可为空!");
            return;
        }

        if(CommonUtils.isNetworkAvailable(mContext)){
            showLoding("");
            int is_default = 0;
            InteNetUtils.getInstance(mContext).Editaddress(accountAddress.getId(),consignee,addressIds , address,"",mobile,is_default ,user.getToken(), edtCallBack);
        }else{
            ToastUtils.Infotoast(mContext, "网络不可用");
        }
    }

    public RequestCallBack<String> edtCallBack = new RequestCallBack<String>() {

        @Override
        public void onSuccess(ResponseInfo<String> stringResponseInfo) {
            dissLoding();
            try {
                JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                if(jsonObject.optInt("ret_num")==0){
                    ToastUtils.Infotoast(mContext,"修改成功");
                    setResult(RESULT_OK,null);
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
            ToastUtils.Infotoast(mContext,"修改失败");
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CHOCE_ADDRESS:
                if (data != null) {
                    if (resultCode == AndroidConfig.ChoiceAddressResultCode) {
                        String addressname = data.getStringExtra("address");
                        addressIds = null;
                        addressIds = data.getStringArrayExtra("addressId");
                        tv_district.setText(addressname);

                    }
                }

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
