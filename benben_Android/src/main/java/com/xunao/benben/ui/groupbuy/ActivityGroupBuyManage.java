package com.xunao.benben.ui.groupbuy;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Promotion;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.image.CubeImageView;

/**
 * Created by ltf on 2015/12/7.
 */
public class ActivityGroupBuyManage extends BaseActivity implements View.OnClickListener {
    private ListView lv_group_buy_showing,lv_group_buy_off_shelf;
    private TextView tv_add_group_buy;

    private int on=0;
    private int off=0;
    private int offline_restrict=0;
    private int online_restrict=0;
    private List<Promotion> onLinePromotions = new ArrayList<>();
    private List<Promotion> offLinePromotions = new ArrayList<>();
    private ShowingGroupBuyAdapter showingGroupBuyAdapter;
    private OffShelfGroupBuyAdapter offShelfGroupBuyAdapter;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_group_buy_manage);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("团购管理", "", "",
                R.drawable.icon_com_title_left, 0);
        lv_group_buy_showing = (ListView) findViewById(R.id.lv_group_buy_showing);
        lv_group_buy_off_shelf = (ListView) findViewById(R.id.lv_group_buy_off_shelf);
        tv_add_group_buy = (TextView) findViewById(R.id.tv_add_group_buy);
        tv_add_group_buy.setOnClickListener(this);

        lv_group_buy_showing.setVisibility(View.VISIBLE);
        showingGroupBuyAdapter = new ShowingGroupBuyAdapter();
        lv_group_buy_showing.setAdapter(showingGroupBuyAdapter);

        lv_group_buy_off_shelf.setVisibility(View.VISIBLE);
        offShelfGroupBuyAdapter = new OffShelfGroupBuyAdapter();
        lv_group_buy_off_shelf.setAdapter(offShelfGroupBuyAdapter);

    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        if(CommonUtils.isNetworkAvailable(mContext)){
            InteNetUtils.getInstance(mContext).GroupManage(user.getToken(), mRequestCallBack);
        }else{
            ToastUtils.Infotoast(mContext,"网络不可用");
        }
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
        if(jsonObject.optInt("ret_num")==0) {

            onLinePromotions.clear();
            offLinePromotions.clear();
            tv_add_group_buy.setVisibility(View.VISIBLE);
            on = jsonObject.optInt("on");
            off = jsonObject.optInt("off");
            offline_restrict = jsonObject.optInt("offline_restrict");
            online_restrict = jsonObject.optInt("online_restrict");
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("promotion_info");
                if (jsonArray != null && jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Promotion promotion = new Promotion();
                        promotion.parseJSON(jsonArray.optJSONObject(i));
                        if (promotion.getIs_close() == 0) {
                            onLinePromotions.add(promotion);
                        } else {
                            offLinePromotions.add(promotion);
                        }
                    }
                }
                showingGroupBuyAdapter.notifyDataSetChanged();
                offShelfGroupBuyAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NetRequestException e) {
                e.printStackTrace();
            }
        }else{
            ToastUtils.Infotoast(mContext,jsonObject.optString("ret_msg"));
        }
    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        ToastUtils.Infotoast(mContext,"获取信息失败");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_add_group_buy:
                if(on>=online_restrict){
                    ToastUtils.Infotoast(mContext,"上架数量已达上限！");
                }else {
                    Intent intent = new Intent(ActivityGroupBuyManage.this, ActivityGroupBuyOperate.class);
                    intent.putExtra("type", 0);
                    startActivityForResult(intent, 1);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
                break;
        }
    }

    private class ShowingGroupBuyAdapter extends BaseAdapter {



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
                        R.layout.item_group_buy_listview, null);
            }

            final Promotion promotion = onLinePromotions.get(position);
            CubeImageView iv_promotion = (CubeImageView) convertView
                    .findViewById(R.id.iv_group_buy);
            TextView tv_title = (TextView) convertView
                    .findViewById(R.id.tv_title);
            tv_title.setText(promotion.getName());
            TextView tv_new_price = (TextView) convertView.findViewById(R.id.tv_new_price);
            tv_new_price.setText(promotion.getPromotion_price()+"");
            TextView tv_origin_price = (TextView) convertView.findViewById(R.id.tv_origin_price);
            tv_origin_price.setText("原价:"+promotion.getOrigion_price()+"元");
            tv_origin_price.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
            TextView tv_num = (TextView) convertView.findViewById(R.id.tv_num);
            tv_num.setText("已售:"+promotion.getSellcount());
            String poster = promotion.getPoster_st();
            if (!TextUtils.isEmpty(poster)) {
                CommonUtils.startImageLoader(cubeimageLoader, poster, iv_promotion);
            } else {
                CommonUtils.startImageLoader(cubeimageLoader, "www.baidu.com",iv_promotion);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ActivityGroupBuyManage.this,ActivityGroupBuyOperate.class);
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

    private class OffShelfGroupBuyAdapter extends BaseAdapter {

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
                        R.layout.item_group_buy_listview, null);
            }
            final Promotion promotion = offLinePromotions.get(position);
            CubeImageView iv_promotion = (CubeImageView) convertView
                    .findViewById(R.id.iv_group_buy);
            TextView tv_title = (TextView) convertView
                    .findViewById(R.id.tv_title);
            tv_title.setText(promotion.getName());
            TextView tv_new_price = (TextView) convertView.findViewById(R.id.tv_new_price);
            tv_new_price.setText(promotion.getPromotion_price()+"");
            TextView tv_origin_price = (TextView) convertView.findViewById(R.id.tv_origin_price);
            tv_origin_price.setText("原价:"+promotion.getOrigion_price()+"元");
            tv_origin_price.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
            TextView tv_num = (TextView) convertView.findViewById(R.id.tv_num);
            tv_num.setText("已售:"+promotion.getSellcount());

            String poster = promotion.getPoster_st();
            if (!TextUtils.isEmpty(poster)) {
                CommonUtils.startImageLoader(cubeimageLoader, poster, iv_promotion);
            } else {
                CommonUtils.startImageLoader(cubeimageLoader, "www.baidu.com",iv_promotion);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ActivityGroupBuyManage.this,ActivityGroupBuyOperate.class);
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
                        InteNetUtils.getInstance(mContext).GroupManage(user.getToken(), mRequestCallBack);
                    }else{
                        ToastUtils.Infotoast(mContext,"网络不可用");
                    }
                    setResult(RESULT_OK,null);
                }
                break;
        }
    }
}
