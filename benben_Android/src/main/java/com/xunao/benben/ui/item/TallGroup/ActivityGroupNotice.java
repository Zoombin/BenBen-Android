package com.xunao.benben.ui.item.TallGroup;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONObject;

/**
 * Created by LSD on 16/1/5.
 * 群公告
 */
public class ActivityGroupNotice extends BaseActivity implements
        View.OnClickListener {
    private EditText tvcontent;
    private TextView tv_txt_num;
    private String talk_groupid;
    private String content="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_groupnotice);
        setShowLoding(false);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("群公告", "", "保存", R.drawable.icon_com_title_left, 0);
        tvcontent = (EditText) findViewById(R.id.tv_content);
        tv_txt_num = (TextView) findViewById(R.id.tv_txt_num);
        tvcontent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String txt = editable.toString();
                tv_txt_num.setText(txt.length()+"/200");
            }
        });
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        talk_groupid = getIntent().getStringExtra("talk_groupid");
        content = getIntent().getStringExtra("content");
        tvcontent.setText(content);
    }

    @Override
    public void initLinstener(Bundle savedInstanceState) {
        setOnLeftClickLinester(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AnimFinsh();
            }
        });
        setOnRightClickLinester(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //保存
                String content = tvcontent.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    ToastUtils.Errortoast(mContext, "请输入公告内容");
                    return;
                }
                showLoding("正在保存……");
                InteNetUtils.getInstance(mContext).addbulletin(talk_groupid, content, user.getToken(), mRequestCallBack);
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
        ToastUtils.Infotoast(mContext, "保存成功 ！");
        sendBroadcast(new Intent(AndroidConfig.refreshGroupInfo));
        AnimFinsh();
    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        dissLoding();
        ToastUtils.Errortoast(mContext, "操作失败，请重试！");
    }

    @Override
    public void onClick(View view) {

    }
}
