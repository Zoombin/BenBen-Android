package com.xunao.benben.ui.item.TallGroup;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.TalkGroup;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.TimeUtil;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LSD on 16/1/5.
 * 查看群公告详情
 */
public class ActivityGroupNoticeDetails extends BaseActivity implements
        View.OnClickListener {
    private TextView tvcontent,tv_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_groupnotice_details);
        setShowLoding(false);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("群公告", "", "", R.drawable.icon_com_title_left, 0);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tvcontent = (TextView) findViewById(R.id.tv_content);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        showLoding("");
        String hx_groupid = getIntent().getStringExtra("hx_groupid");
        InteNetUtils.getInstance(mContext).getSingleGroupInfo(hx_groupid,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        dissLoding();
                        try {
                            //改用gson
                            TalkGroup mTalkGroup = new TalkGroup();
                            JSONObject jsonObj = new JSONObject(arg0.result);
                            mTalkGroup.checkJson(jsonObj);
                            JSONObject optJSONObject = jsonObj
                                    .optJSONObject("group_info");
                            mTalkGroup.parseJSON(optJSONObject);
                            String content = mTalkGroup.getBulletin();
                            if(!TextUtils.isEmpty(content)){
                                long time = 0;
                                try {
                                    time = Long.parseLong(mTalkGroup.getCreated_time());
                                }catch (Exception e){}
                                if(time >0){
                                    tv_time.setText("发布时间:"+TimeUtil.getTimeString(time * 1000));
                                }else{
                                    tv_time.setText("发布时间:");
                                }
                                tvcontent.setText(content);
                            }else{
                                ToastUtils.Errortoast(mContext, "没有公告！");
                                AnimFinsh();
                            }



//                            InteNetUtils.getInstance(mContext).Getbulletin(mTalkGroup.getId(), user.getToken(), mRequestCallBack);
                        } catch (Exception e) {
                            dissLoding();
                            ToastUtils.Errortoast(mContext, "当前网络不可用");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        dissLoding();
                        ToastUtils.Errortoast(mContext, "当前网络不可用");
                    }
                });
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
        dissLoding();
        if("0".equals(jsonObject.optString("ret_num"))){
            String content = jsonObject.optString("bulletin");
            if(!TextUtils.isEmpty(content)){
                tvcontent.setText(content);
            }else{
                ToastUtils.Errortoast(mContext, "没有公告！");
                AnimFinsh();
            }
        }else{
            ToastUtils.Errortoast(mContext, jsonObject.optString("ret_msg"));
            AnimFinsh();
        }
    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        dissLoding();
        ToastUtils.Errortoast(mContext, "没有公告！");
        AnimFinsh();
    }

    @Override
    public void onClick(View view) {

    }
}
