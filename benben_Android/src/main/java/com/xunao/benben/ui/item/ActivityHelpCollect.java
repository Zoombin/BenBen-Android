package com.xunao.benben.ui.item;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONObject;

/**
 * Created by LSD on 15/12/31.
 * 小助手
 *
 */
public class ActivityHelpCollect extends BaseActivity implements View.OnClickListener {
    private LinearLayout content_layout;
    private TextView tvtitlerestnum;
    private Button btleftrestnum;
    private LinearLayout contentrestnum;
    private TextView tvtitleisfl;
    private Button btleftisfl;
    private LinearLayout contentisfl;
    private TextView tvtitleispo;
    private Button btleftispo;
    private LinearLayout contentispo;
    private TextView tvtitleisgb;
    private Button btleftisgb;
    private LinearLayout contentisgb;
    private TextView tvtitleisvip;
    private Button btleftisvip;
    private LinearLayout contentisvip;
    private LinearLayout contentlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_helpcollect);
        setShowLoding(false);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("收藏小助手", "", "", R.drawable.icon_com_title_left, 0);
        content_layout = (LinearLayout) findViewById(R.id.content_layout);

        tvtitlerestnum = (TextView) findViewById(R.id.tv_title_rest_num);
        btleftrestnum = (Button) findViewById(R.id.btleft_rest_num);
        contentrestnum = (LinearLayout) findViewById(R.id.content_rest_num);

        tvtitleisfl = (TextView) findViewById(R.id.tv_title_is_fl);
        btleftisfl = (Button) findViewById(R.id.btleft_is_fl);
        contentisfl = (LinearLayout) findViewById(R.id.content_is_fl);

        tvtitleispo = (TextView) findViewById(R.id.tv_title_is_po);
        btleftispo = (Button) findViewById(R.id.btleft_is_po);
        contentispo = (LinearLayout) findViewById(R.id.content_is_po);

        tvtitleisgb = (TextView) findViewById(R.id.tv_title_is_gb);
        btleftisgb = (Button) findViewById(R.id.btleft_is_gb);
        contentisgb = (LinearLayout) findViewById(R.id.content_is_gb);

        tvtitleisvip = (TextView) findViewById(R.id.tv_title_is_vip);
        btleftisvip = (Button) findViewById(R.id.btleft_is_vip);
        contentisvip = (LinearLayout) findViewById(R.id.content_is_vip);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        getIntent().getStringExtra("");
        InteNetUtils.getInstance(mContext).helpcollectDetails(user.getToken(),mRequestCallBack);
    }

    @Override
    public void initLinstener(Bundle savedInstanceState) {
        setOnLeftClickLinester(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AnimFinsh();
            }
        });
        btleftrestnum.setOnClickListener(this);
        btleftisfl.setOnClickListener(this);
        btleftispo.setOnClickListener(this);
        btleftisgb.setOnClickListener(this);
        btleftisvip.setOnClickListener(this);
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
        String optString = jsonObject.optString("ret_num");
        dissLoding();
        if (optString != null) {
            if (optString.equals("0")) {
                int rest_num  = jsonObject.optInt("rest_num");
                if(0 == rest_num){
                    contentrestnum.setVisibility(View.GONE);
                }else{
                    contentrestnum.setVisibility(View.VISIBLE);
                    tvtitlerestnum.setText(rest_num+"个赠送小喇叭");
                }

                int is_fl  = jsonObject.optInt("is_fl");
                btleftisfl.setTag(is_fl);
                if(1 == is_fl){
                    btleftisfl.setText("已开通");
                }else{
                    btleftisfl.setText("立即开通");
                }

                int is_po  = jsonObject.optInt("is_po");
                btleftispo.setTag(is_po);
                if(1 == is_po){
                    btleftispo.setText("已开通");
                }else{
                    btleftispo.setText("立即开通");
                }

                int is_gb  = jsonObject.optInt("is_gb");
                btleftisgb.setTag(is_gb);
                if(1 == is_gb){
                    btleftisgb.setText("已开通");
                }else{
                    btleftisgb.setText("立即开通");
                }

                int is_vip  = jsonObject.optInt("is_vip");
                btleftisvip.setTag(is_vip);
                if(1 == is_vip){
                    btleftisvip.setText("已开通");
                }else{
                    btleftisvip.setText("立即开通");
                }
            }
        }
    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        ToastUtils.Errortoast(mContext, "网络请求失败，请重试！");
        dissLoding();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btleft_is_fl:
                break;
            case R.id.btleft_is_gb:
                break;
            case R.id.btleft_is_po:
                break;
            case R.id.btleft_is_vip:
                break;
            case R.id.btleft_rest_num:
                startAnimActivity2Obj(
                        ActivitySmallPublic.class,
                        "numberTrain", "我开通了号码直通车来给我捧捧场吧!");
                break;
        }
        finish();
    }

}
