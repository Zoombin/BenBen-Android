package com.xunao.benben.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
 * 手动发货
 * Created by ltf on 2015/12/25.
 */
public class ActivityManualSend extends BaseActivity implements View.OnClickListener {
    private EditText edt_sname,edt_sn;
    private Button btn_send;
    private String order_id;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_manual_send);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("手动发货", "", "",
                R.drawable.icon_com_title_left, 0);
        edt_sname = (EditText) findViewById(R.id.edt_sname);
        edt_sn = (EditText) findViewById(R.id.edt_sn);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        order_id = getIntent().getStringExtra("order_id");
    }

    @Override
    public void initLinstener(Bundle savedInstanceState) {

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
            case R.id.btn_send:
                final MsgDialog msgDialog = new MsgDialog(ActivityManualSend.this, R.style.MyDialogStyle);
                msgDialog.setContent("确定发货吗？", "", "确定", "取消");
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
                        String sname = String.valueOf(edt_sname.getText()).trim();
                        String sn= String.valueOf(edt_sn.getText()).trim();
                        if(sname.equals("")){
                            ToastUtils.Infotoast(mContext,"请输入物流公司名称");
                        }else if(sn.equals("")){
                            ToastUtils.Infotoast(mContext,"请输入运单号");
                        }else{
                            if (CommonUtils.isNetworkAvailable(mContext)) {
                                showLoding("");
                                InteNetUtils.getInstance(mContext).Manualsend(order_id, sname, sn, new RequestCallBack<String>() {
                                    @Override
                                    public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                                        dissLoding();
                                        try {
                                            JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                                            if(jsonObject.optInt("ret_num")==0){
                                                ToastUtils.Infotoast(mContext, "发货成功");
                                                sendBroadcast(new Intent("businessOrderRefresh"));
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
                                        ToastUtils.Infotoast(mContext, "发货失败");
                                    }
                                });
                            } else {
                                ToastUtils.Infotoast(mContext, "网络不可用");
                            }
                        }
                    }
                });
                msgDialog.show();



                break;
        }
    }
}
