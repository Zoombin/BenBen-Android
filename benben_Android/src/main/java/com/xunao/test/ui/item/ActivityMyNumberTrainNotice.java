package com.xunao.test.ui.item;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.test.R;
import com.xunao.test.base.BaseActivity;
import com.xunao.test.utils.CommonUtils;
import com.xunao.test.utils.ToastUtils;

import org.json.JSONObject;

/**
 * Created by ltf on 2015/12/11.
 */
public class ActivityMyNumberTrainNotice extends BaseActivity{
    private EditText edt_notice;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_number_train_notice);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("公告", "", "发表",
                R.drawable.icon_com_title_left, 0);
        edt_notice = (EditText) findViewById(R.id.edt_notice);
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
        setOnRightClickLinester(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String notice = String.valueOf(edt_notice.getText()).trim();
                if (TextUtils.isEmpty(notice)) {
                    ToastUtils.Infotoast(mContext, "请输入公告内容！");

                }else if(!CommonUtils.StringIsSurpass2(notice, 1, 150)){
                    ToastUtils.Infotoast(mContext, "公告内容限制在1-150个字之间");
                    return;
                }else{
                    AnimFinsh();
                }

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
}
