package com.xunao.benben.ui.item;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.PublicMessage;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.srain.cube.image.CubeImageView;

/**
 * Created by ltf on 2015/11/10.
 */
public class ActivityContactsUnionInfo extends BaseActivity implements View.OnClickListener {
    private CubeImageView group_poster;
    private TextView tv_group_name,tv_group_address;
    private TextView tv_group_message;
    private TextView union_member_num,union_member_type,union_member_introduce;
    private String legid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_new_union_info);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("联盟详情", "", "",
                R.drawable.icon_com_title_left, 0);
        group_poster = (CubeImageView) findViewById(R.id.group_poster);
        tv_group_name = (TextView) findViewById(R.id.tv_group_name);
        tv_group_address = (TextView) findViewById(R.id.tv_group_address);
        tv_group_message = (TextView) findViewById(R.id.tv_group_message);
        union_member_num = (TextView) findViewById(R.id.union_member_num);
        union_member_type = (TextView) findViewById(R.id.union_member_type);
        union_member_introduce = (TextView) findViewById(R.id.union_member_introduce);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        legid = getIntent().getStringExtra("legid");
        InteNetUtils.getInstance(mContext).leagueDetail(legid, mRequestCallBack);
    }

    @Override
    public void initLinstener(Bundle savedInstanceState) {
        setOnLeftClickLinester(this);
    }

    @Override
    protected void onHttpStart() {

    }

    @Override
    protected void onLoading(long count, long current, boolean isUploading) {

    }

    @Override
    protected void onSuccess(JSONObject jsonObject) {
        if (jsonObject.optString("ret_num").equals("0")) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("enterprise_list");
                JSONObject object = jsonArray.getJSONObject(0);
                CommonUtils.startImageLoader(cubeimageLoader, object.optString("poster"), group_poster);
                tv_group_name.setText(object.optString("name"));
                tv_group_address.setText(object.optString("full_area"));
                tv_group_message.setText("联盟公告："+object.optString("announcement"));
                union_member_num.setText(object.optString("number"));
                if (object.optString("category").equals("1")) {
                    union_member_type.setText("工作联盟");
                } else if (object.optString("category").equals("2")) {
                    union_member_type.setText("英雄联盟");
                }
                union_member_introduce.setText(object.optString("description"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            ToastUtils.Errortoast(this,"获取信息失败");
        }
    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        ToastUtils.Errortoast(this,"获取信息失败");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            // 头部左侧点击
            case R.id.com_title_bar_left_bt:
            case R.id.com_title_bar_left_tv:
                AnimFinsh();
                break;
        }
    }
}
