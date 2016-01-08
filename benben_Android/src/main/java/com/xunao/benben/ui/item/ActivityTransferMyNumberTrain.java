package com.xunao.benben.ui.item;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONObject;

/**
 * Created by LSD on 16/1/7.
 * 赠送我的号码直通车
 */
public class ActivityTransferMyNumberTrain extends BaseActivity implements View.OnClickListener {
    private EditText etnumbenben;
    private EditText ettips;
    private Button btnconfirm;
    private LinearLayout contentlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_transfer_mynumber_train);
        setShowLoding(false);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("转让", "", "", R.drawable.icon_com_title_left, 0);

        etnumbenben = (EditText) findViewById(R.id.et_num_benben);
        ettips = (EditText) findViewById(R.id.et_tips);
        btnconfirm = (Button) findViewById(R.id.btn_confirm);
        contentlayout = (LinearLayout) findViewById(R.id.content_layout);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {

    }

    @Override
    public void initLinstener(Bundle savedInstanceState) {
        setOnLeftClickLinester(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AnimFinsh();
            }
        });

        btnconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String benben_id_other = etnumbenben.getText().toString();
                String tips = ettips.getText().toString();
                if(TextUtils.isEmpty(tips)){
                    ToastUtils.Errortoast(mContext,"请输入对方奔犇号");
                    return;
                }
                hideSoftInput();
                InteNetUtils.getInstance(mContext).applyStoreTransfer(benben_id_other, tips, user.getToken(), mRequestCallBack);
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
        dissLoding();
        String code = jsonObject.optString("ret_num");
        if ("0".equals(code)) {
            ToastUtils.Infotoast(mContext, "操作成功");
            AnimFinsh();
        }else{
            ToastUtils.Errortoast(mContext, jsonObject.optString("ret_msg"));
        }
    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        ToastUtils.Errortoast(mContext, "操作失败，请重试！");
        dissLoding();
    }

    @Override
    public void onClick(View view) {

    }

    private void hideSoftInput(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(ettips.getWindowToken(),0);
    }

}
