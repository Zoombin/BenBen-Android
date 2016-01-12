package com.xunao.benben.ui.item;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.PublicMessage;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityLogin;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LSD on 16/1/7.
 * 赠送/转让详情
 */
public class ActivityTransferDetails extends BaseActivity {

    private RoundedImageView ivlogo;
    private TextView tvtitle;
    private TextView tvname;
    private TextView tvtipstxt;
    private TextView tvtipscontent;
    private TextView tvtipskeep;
    private Button btno;
    private Button btyes;
    private LinearLayout contentlayout;

    private PublicMessage publicMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_transfer_details);
        setShowLoding(false);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("转赠详情", "", "", R.drawable.icon_com_title_left, 0);

        ivlogo = (RoundedImageView) findViewById(R.id.iv_logo);
        tvtitle = (TextView) findViewById(R.id.tv_title);
        tvname = (TextView) findViewById(R.id.tv_name);
        tvtipstxt = (TextView) findViewById(R.id.tv_tips_txt);
        tvtipscontent = (TextView) findViewById(R.id.tv_tips_content);
        tvtipskeep = (TextView) findViewById(R.id.tv_tips_keep);
        btno = (Button) findViewById(R.id.bt_no);
        btyes = (Button) findViewById(R.id.bt_yes);
        contentlayout = (LinearLayout) findViewById(R.id.content_layout);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        publicMessage = (PublicMessage) getIntent().getSerializableExtra("publicMessage");

        String avstar = publicMessage.getPoster();
        String applay_name = publicMessage.getNick_name();
        String content = publicMessage.getReason();
        String vip_account = publicMessage.getVip_account();
        String store_name = publicMessage.getStore_name();

        CommonUtils.startImageLoader(cubeimageLoader, avstar, ivlogo);
        tvtitle.setText(store_name);
        tvname.setText("转赠人：" + applay_name);
        tvtipscontent.setText(content);
        tvtipskeep.setText("该直通车上还有"+vip_account+"元的会员账务");
    }

    @Override
    public void initLinstener(Bundle savedInstanceState) {
        setOnLeftClickLinester(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AnimFinsh();
            }
        });
        btno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoding("");
                String transfer_id=publicMessage.getNews_id();
                InteNetUtils.getInstance(mContext).storeRefuseTransfer(transfer_id, user.getToken(), new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        dissLoding();
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(responseInfo.result);
                            String ret_num = jsonObject.optString("ret_num");

                            if (ret_num.equals("0")) {
                                ToastUtils.Errortoast(mContext, "取消请求");
                                publicMessage.setStatus(PublicMessage.REFUSE);
                                try {
                                    dbUtil.saveOrUpdate(publicMessage);
                                } catch (DbException e1) {
                                    e1.printStackTrace();
                                }
                                setResult(AndroidConfig.writeFriendResultCode);
                                AnimFinsh();
                            } else {
                                ToastUtils.Errortoast(mContext, jsonObject.optString("ret_msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        ToastUtils.Errortoast(mContext, "操作失败，请重试！");
                        dissLoding();
                    }
                });
            }
        });
        btyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoding("");
                String transfer_id=publicMessage.getNews_id();
                InteNetUtils.getInstance(mContext).storeAgreeTransfer(transfer_id, user.getToken(), new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        dissLoding();
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(responseInfo.result);
                            String ret_num = jsonObject.optString("ret_num");

                            if (ret_num.equals("0")) {
                                ToastUtils.Errortoast(mContext, "接收请求");
                                publicMessage.setStatus(PublicMessage.AGREE);
                                try {
                                    dbUtil.saveOrUpdate(publicMessage);
                                } catch (DbException e1) {
                                    e1.printStackTrace();
                                }
                                setResult(AndroidConfig.writeFriendResultCode);
                                AnimFinsh();
                            } else {
                                ToastUtils.Errortoast(mContext, jsonObject.optString("ret_msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        ToastUtils.Errortoast(mContext, "操作失败，请重试！");
                        dissLoding();
                    }
                });
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

    private void hideSoftInput(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(ettips.getWindowToken(),0);
    }

}
