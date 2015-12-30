package com.xunao.benben.ui.promotion;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Promotion;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.NoScrollGridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.image.CubeImageView;

/**
 * Created by ltf on 2015/11/30.
 */
public class ActivityPromotionManage extends BaseActivity implements View.OnClickListener {
    private NoScrollGridView gv_promotion_off_shelf,gv_promotion_showing;
    private TextView tv_add_promotion;

    private int on=0;
    private int off=0;
    private int offline_restrict=0;
    private int online_restrict=0;
    private List<Promotion> onLinePromotions = new ArrayList<>();
    private List<Promotion> offLinePromotions = new ArrayList<>();
    private ShowingPromotionAdapter showingPromotionAdapter;
    private OffShelfPromotionAdapter offShelfPromotionAdapter;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_promotion_manage);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("促销管理", "", "",
                R.drawable.icon_com_title_left, 0);
        gv_promotion_off_shelf = (NoScrollGridView) findViewById(R.id.gv_promotion_off_shelf);
        gv_promotion_showing = (NoScrollGridView) findViewById(R.id.gv_promotion_showing);
        tv_add_promotion = (TextView) findViewById(R.id.tv_add_promotion);
        tv_add_promotion.setOnClickListener(this);

        gv_promotion_showing.setVisibility(View.VISIBLE);
        showingPromotionAdapter = new ShowingPromotionAdapter();
        gv_promotion_showing.setAdapter(showingPromotionAdapter);

        gv_promotion_off_shelf.setVisibility(View.VISIBLE);
        offShelfPromotionAdapter = new OffShelfPromotionAdapter();
        gv_promotion_off_shelf.setAdapter(offShelfPromotionAdapter);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        if(CommonUtils.isNetworkAvailable(mContext)){
            InteNetUtils.getInstance(mContext).Promotionmanage(user.getToken(), mRequestCallBack);
        }else{
            ToastUtils.Infotoast(mContext,"网络不可用");
        }

//
//        String[] split = {"http://112.124.101.177:81/uploads/images/201511/small1447838070391727710.",
//                "http://112.124.101.177:81/uploads/images/201511/small1447838070391727710.",
//                "http://112.124.101.177:81/uploads/images/201511/small1447838070391727710.",
//                "http://112.124.101.177:81/uploads/images/201511/small1447838070391727710.",
//                "http://112.124.101.177:81/uploads/images/201511/small1447838070391727710."};
//
//        gv_promotion_showing.setVisibility(View.VISIBLE);
//        ShowingPromotionAdapter showingPromotionAdapter = new ShowingPromotionAdapter(split);
//        gv_promotion_showing.setAdapter(showingPromotionAdapter);
//
//        String[] split1 = {"http://112.124.101.177:81/uploads/images/201511/small1447838070391727710.",
//                "http://112.124.101.177:81/uploads/images/201511/small1447838070391727710.",
//                "http://112.124.101.177:81/uploads/images/201511/small1447838070391727710.",};
//
//        gv_promotion_off_shelf.setVisibility(View.VISIBLE);
//        OffShelfPromotionAdapter offShelfPromotionAdapter = new OffShelfPromotionAdapter(split1);
//        gv_promotion_off_shelf.setAdapter(offShelfPromotionAdapter);
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
        onLinePromotions.clear();
        offLinePromotions.clear();
        tv_add_promotion.setVisibility(View.VISIBLE);
        on = jsonObject.optInt("on");
        off = jsonObject.optInt("off");
        offline_restrict = jsonObject.optInt("offline_restrict");
        online_restrict = jsonObject.optInt("online_restrict");
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("promotion_info");
            if(jsonArray!=null && jsonArray.length()>0){
                for(int i=0;i<jsonArray.length();i++){
                    Promotion promotion = new Promotion();
                    promotion.parseJSON(jsonArray.optJSONObject(i));
                    if(promotion.getIs_close()==0){
                        onLinePromotions.add(promotion);
                    }else{
                        offLinePromotions.add(promotion);
                    }
                }
            }
            showingPromotionAdapter.notifyDataSetChanged();
            offShelfPromotionAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NetRequestException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        ToastUtils.Infotoast(mContext,"获取信息失败");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_add_promotion:
                if(on>=online_restrict){
                    ToastUtils.Infotoast(mContext,"上架数量已达上限！");
                }else {
                    Intent intent = new Intent(ActivityPromotionManage.this, ActivityPromotionOperate.class);
                    intent.putExtra("type", 0);
                    startActivityForResult(intent, 1);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
                break;
        }
    }

    private class ShowingPromotionAdapter extends BaseAdapter {



        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return onLinePromotions.size();
        }

        @Override
        public Promotion getItem(int position) {
            // TODO Auto-generated method stub
            return onLinePromotions.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mContext,
                        R.layout.item_promotion_gridview, null);
            }

            final Promotion promotion = onLinePromotions.get(position);
            TextView tv_title = (TextView) convertView
                    .findViewById(R.id.tv_title);
            tv_title.setText(promotion.getName());
            TextView tv_edittxt = (TextView) convertView
                    .findViewById(R.id.tv_edittxt);
            tv_edittxt.setVisibility(View.VISIBLE);
            RoundedImageView iv_promotion = (RoundedImageView) convertView
                    .findViewById(R.id.iv_promotion);
//            iv_promotion
//                    .setLayoutParams(new LinearLayout.LayoutParams(
//                            LinearLayout.LayoutParams.MATCH_PARENT, PixelUtil
//                            .dp2px(60)));
            iv_promotion.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, (mScreenWidth-PixelUtil.dp2px(30))/3));
            String poster = promotion.getPoster_st();
            if (!TextUtils.isEmpty(poster)) {
                CommonUtils.startImageLoader(cubeimageLoader, poster, iv_promotion);
            } else {
                CommonUtils.startImageLoader(cubeimageLoader, "www.baidu.com",iv_promotion);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ActivityPromotionManage.this,ActivityPromotionOperate.class);
                    intent.putExtra("type",1);
                    intent.putExtra("on",on);
                    intent.putExtra("off",off);
                    intent.putExtra("offline_restrict",offline_restrict);
                    intent.putExtra("online_restrict",online_restrict);
                    intent.putExtra("promotionid",promotion.getPromotionid());
                    startActivityForResult(intent, 1);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
            });

            return convertView;
        }

    }

    private class OffShelfPromotionAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return offLinePromotions.size();
        }

        @Override
        public Promotion getItem(int position) {
            // TODO Auto-generated method stub
            return offLinePromotions.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mContext,
                        R.layout.item_promotion_gridview, null);
            }
            final Promotion promotion = offLinePromotions.get(position);
            TextView tv_title = (TextView) convertView
                    .findViewById(R.id.tv_title);
            TextView tv_edittxt = (TextView) convertView
                    .findViewById(R.id.tv_edittxt);
            tv_edittxt.setVisibility(View.VISIBLE);
            tv_title.setText(promotion.getName());
            RoundedImageView iv_promotion = (RoundedImageView) convertView
                    .findViewById(R.id.iv_promotion);

//            iv_promotion
//                    .setLayoutParams(new LinearLayout.LayoutParams(
//                            LinearLayout.LayoutParams.MATCH_PARENT, PixelUtil
//                            .dp2px(60)));
            iv_promotion.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, (mScreenWidth-PixelUtil.dp2px(30))/3));
            String poster = promotion.getPoster_st();
            if (!TextUtils.isEmpty(poster)) {
                CommonUtils.startImageLoader(cubeimageLoader, poster, iv_promotion);
            } else {
                CommonUtils.startImageLoader(cubeimageLoader, "www.baidu.com",iv_promotion);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ActivityPromotionManage.this,ActivityPromotionOperate.class);
                    intent.putExtra("type",2);
                    intent.putExtra("on",on);
                    intent.putExtra("off",off);
                    intent.putExtra("offline_restrict",offline_restrict);
                    intent.putExtra("online_restrict",online_restrict);
                    intent.putExtra("promotionid",promotion.getPromotionid());
                    startActivityForResult(intent, 1);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
            });
            return convertView;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    if(CommonUtils.isNetworkAvailable(mContext)){
                        InteNetUtils.getInstance(mContext).Promotionmanage(user.getToken(), mRequestCallBack);
                    }else{
                        ToastUtils.Infotoast(mContext,"网络不可用");
                    }
                    setResult(RESULT_OK,null);
                }
                break;
        }
    }
}
