package com.xunao.benben.ui.item;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityMyWeb;
import com.xunao.benben.ui.ActivityWeb;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    private TextView tv_vip_num;

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
        tv_vip_num = (TextView) findViewById(R.id.tv_vip_num);
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
                    contentispo.setVisibility(View.GONE);
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
//                tv_vip_num.setText("(已开通"+jsonObject.optString("vip_num")+"人)");
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
                if (CrashApplication.getInstance().user.getLeague_disable() == 0) {
                    startAnimActivity(ActivityMyFriendUnion.class);
                } else if (CrashApplication.getInstance().user.getLeague_disable() == 1) {
                    final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(ActivityHelpCollect.this,
                            R.style.MyDialog1);
                    hint.setContent("功能已被永久禁用");
                    hint.setBtnContent("确定");
                    hint.show();
                    hint.setOKListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            hint.dismiss();
                        }
                    });

                    hint.show();
                } else {
                    String beginDate = CrashApplication.getInstance().user
                            .getLeague_disable() + "000";
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String sd = sdf.format(new Date(Long.parseLong(beginDate)));

                    final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(ActivityHelpCollect.this,
                            R.style.MyDialog1);
                    hint.setContent("功能被禁用,将于" + sd + "解禁");
                    hint.setBtnContent("确定");
                    hint.show();
                    hint.setOKListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            hint.dismiss();
                        }
                    });

                    hint.show();
                }
                break;
            case R.id.btleft_is_gb:
                if(btleftisgb.getTag()==1){

                }else{
                    Intent intent = new Intent(mContext, ActivityMyWeb.class);
                    intent.putExtra("title", "团购");
                    intent.putExtra("url", AndroidConfig.NETHOST4 + "/mobileService/serviceDetail?type=1&token="+user.getToken());
                    startActivity(intent);
                }
                break;
            case R.id.btleft_is_po:
                if(btleftispo.getTag()==1){

                }else{
                    Intent intent = new Intent(mContext, ActivityMyWeb.class);
                    intent.putExtra("title", "促销");
                    intent.putExtra("url", AndroidConfig.NETHOST4 + "/mobileService/serviceDetail?type=0&token="+user.getToken());
                    startActivity(intent);
                }


                break;
            case R.id.btleft_is_vip:
                if(btleftisvip.getTag()==1){

                }else{
                    Intent intent = new Intent(mContext, ActivityMyWeb.class);
                    intent.putExtra("title", "会员号");
                    intent.putExtra("url", AndroidConfig.NETHOST4 + "/mobileService/serviceDetail?type=11&token="+user.getToken());
                    startActivity(intent);
                }
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
