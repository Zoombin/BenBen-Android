package com.xunao.benben.ui.account;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Promotion;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.groupbuy.ActivityGroupBuyDetail;
import com.xunao.benben.ui.groupbuy.ActivityGroupBuyManage;
import com.xunao.benben.ui.promotion.ActivityPromotionDetail;
import com.xunao.benben.ui.promotion.ActivityPromotionManage;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.NoScrollGridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.srain.cube.image.CubeImageView;

/**
 * Created by ltf on 2016/1/4.
 */
public class ActivityMyPromotionCollection extends BaseActivity implements View.OnClickListener {
    private RelativeLayout prerecord_tab_one;
    private RelativeLayout prerecord_tab_three;
    private RadioButton rb_one,rb_three;
    private int currentTab=1;

    private GridView item_gridView;
    private ListView item_group_buy;
    private MyGridViewAdapter adapter;
    private GroupBuyAdapter buyAdapter;
    private List<Promotion> gbLists = new ArrayList<>();
    private List<Promotion> cpLists = new ArrayList<>();
    private List<Promotion> promotions = new ArrayList<>();

    private boolean isDelete = false;
    private LinearLayout ll_all;
    private TextView tv_delete;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_promotion_collection);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("我的收藏", "", "编辑",
                R.drawable.icon_com_title_left, 0);
        prerecord_tab_one = (RelativeLayout) findViewById(R.id.prerecord_tab_one);
        rb_one = (RadioButton) prerecord_tab_one.findViewById(R.id.tab_RB);
        prerecord_tab_three = (RelativeLayout) findViewById(R.id.prerecord_tab_three);
        rb_three = (RadioButton) prerecord_tab_three.findViewById(R.id.tab_RB);
        prerecord_tab_one.setOnClickListener(this);
        prerecord_tab_three.setOnClickListener(this);
        prerecord_tab_one.performClick();
        setChecked(rb_one, true);

        item_gridView = (GridView) findViewById(R.id.item_gridView);
        adapter = new MyGridViewAdapter();
        item_gridView.setAdapter(adapter);
        item_group_buy = (ListView) findViewById(R.id.item_group_buy);
        buyAdapter = new GroupBuyAdapter();
        item_group_buy.setAdapter(buyAdapter);
        item_group_buy.setVisibility(View.VISIBLE);
        ll_all = (LinearLayout)findViewById(R.id.ll_all);
        tv_delete = (TextView) findViewById(R.id.tv_delete);
        tv_delete.setOnClickListener(this);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        if(CommonUtils.isNetworkAvailable(mContext)){
            InteNetUtils.getInstance(mContext).CollectGoodsList(mRequestCallBack);
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

        setOnRightClickLinester(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isDelete) {
                    ll_all.setVisibility(View.GONE);
                    initTitle_Right_Left_bar("我的收藏", "", "编辑",R.drawable.icon_com_title_left, 0);
                    isDelete = false;
                    adapter.notifyDataSetChanged();
                    buyAdapter.notifyDataSetChanged();
                }else{
                    ll_all.setVisibility(View.VISIBLE);
                    initTitle_Right_Left_bar("我的收藏", "", "取消",R.drawable.icon_com_title_left, 0);
                    isDelete = true;
                    adapter.notifyDataSetChanged();
                    buyAdapter.notifyDataSetChanged();
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
        Log.d("ltf","jsonObject============="+jsonObject);
        if(jsonObject.optInt("ret_num")==0){
            try {
                JSONArray gbJsonArray = jsonObject.getJSONArray("gb");
                if(gbJsonArray!=null && gbJsonArray.length()>0){
                    for(int i=0;i<gbJsonArray.length();i++){
                        Promotion promotion = new Promotion();
                        promotion.parseJSONCollection(gbJsonArray.getJSONObject(i));
                        gbLists.add(promotion);
                    }
                }
                rb_one.setText("团购("+gbLists.size()+")");
                buyAdapter.notifyDataSetChanged();
                JSONArray cpJsonArray = jsonObject.getJSONArray("cp");
                if(cpJsonArray!=null && cpJsonArray.length()>0){
                    for(int i=0;i<cpJsonArray.length();i++){
                        Promotion promotion = new Promotion();
                        promotion.parseJSONCollection(cpJsonArray.getJSONObject(i));
                        cpLists.add(promotion);
                    }
                }
                rb_three.setText("促销("+cpLists.size()+")");
                adapter.notifyDataSetChanged();
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
        ToastUtils.Infotoast(mContext,"获取收藏失败");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.prerecord_tab_one:
                if(currentTab==0){
                    currentTab=1;
                    setChecked(rb_one, true);
                    setChecked(rb_three, false);
                    item_group_buy.setVisibility(View.VISIBLE);
                    item_gridView.setVisibility(View.GONE);
                }
                break;
            case R.id.prerecord_tab_three:
                if(currentTab==1){
                    currentTab=0;
                    setChecked(rb_one, false);
                    setChecked(rb_three, true);
                    item_group_buy.setVisibility(View.GONE);
                    item_gridView.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_delete:
                String ids = "";
                promotions.clear();
                if(currentTab==1){
                    promotions.addAll(gbLists);
                }else{
                    promotions.addAll(cpLists);
                }
                for (Promotion promotion : promotions) {
                    if(promotion.isChecked()){
                        ids += promotion.getPromotionid()+",";
                    }
                }
                if(ids.equals("")){
                    ToastUtils.Infotoast(this,"请选择需要取消的收藏！");
                }else {
                    this.showLoding("删除中...");
                    ids=ids.substring(0,ids.length()-1);
                    Log.d("ltf","ids======="+ids+"==="+currentTab);
                    InteNetUtils.getInstance(mContext).DelCollect(ids, currentTab+"", new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                            dissLoding();
                            try {
                                JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                                if(jsonObject.optInt("ret_mag")==0){
                                    if(currentTab==1){
                                        for (Promotion promotion : gbLists) {
                                            if(promotion.isChecked()){
                                                promotions.remove(promotion);
                                            }
                                        }
                                        gbLists.clear();
                                        gbLists.addAll(promotions);
                                        rb_one.setText("团购("+gbLists.size()+")");
                                        buyAdapter.notifyDataSetChanged();
                                    }else{
                                        for (Promotion promotion : cpLists) {
                                            if(promotion.isChecked()){
                                                promotions.remove(promotion);
                                            }
                                        }
                                        cpLists.clear();
                                        cpLists.addAll(promotions);
                                        rb_three.setText("促销("+cpLists.size()+")");
                                        adapter.notifyDataSetChanged();
                                    }
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
                            ToastUtils.Infotoast(mContext,"取消收藏失败");
                        }
                    });
                }
                break;
        }
    }

    private void setChecked(RadioButton view, boolean isCheck) {
        view.setChecked(isCheck);

    }

    private class MyGridViewAdapter extends BaseAdapter {

        public MyGridViewAdapter() {
            super();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return cpLists.size();
        }

        @Override
        public Promotion getItem(int position) {
            // TODO Auto-generated method stub
            return cpLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mContext,
                        R.layout.item_promotion_gridview, null);
            }
            Promotion promotion = cpLists.get(position);
            TextView tv_title = (TextView) convertView
                    .findViewById(R.id.tv_title);
            ImageView iv_over_time = (ImageView) convertView.findViewById(R.id.iv_over_time);
            TextView tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            CheckBox cb_item = (CheckBox) convertView.findViewById(R.id.cb_item);
            if(isDelete){
                cb_item.setVisibility(View.VISIBLE);
                if(promotion.isChecked()){
                    cb_item.setChecked(true);
                }else{
                    cb_item.setChecked(false);
                }
            }else{
                cb_item.setVisibility(View.GONE);
            }

            if(promotion.getIs_close()==1) {
                iv_over_time.setImageResource(R.drawable.icon_off_line);
                iv_over_time.setVisibility(View.VISIBLE);
            }else if(Long.parseLong(promotion.getValid_right())*1000 < new Date().getTime()){
                iv_over_time.setImageResource(R.drawable.icon_over_time);
                iv_over_time.setVisibility(View.VISIBLE);
            }else{
                iv_over_time.setVisibility(View.GONE);
            }

            tv_title.setText(promotion.getName());
            CubeImageView iv_promotion = (CubeImageView) convertView
                    .findViewById(R.id.iv_promotion);
            tv_price.setText(promotion.getPrice());
//
//            iv_promotion.setLayoutParams(new LinearLayout.LayoutParams(
//                            LinearLayout.LayoutParams.MATCH_PARENT, PixelUtil
//                            .dp2px(60)));

            iv_promotion.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, (mScreenWidth- PixelUtil.dp2px(30))/2));
            if (!TextUtils.isEmpty(promotion.getPoster())) {
                CommonUtils.startImageLoader(cubeimageLoader,promotion.getPoster(), iv_promotion);
            } else {
                CommonUtils.startImageLoader(cubeimageLoader, "www.baidu.com",iv_promotion);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isDelete){
                        if(cpLists.get(position).isChecked()){
                            cpLists.get(position).setChecked(false);
                        }else{
                            cpLists.get(position).setChecked(true);
                        }
                        adapter.notifyDataSetChanged();
                    }else{
                        Intent intent = new Intent(mContext, ActivityPromotionDetail.class);
                        intent.putExtra("ids", cpLists.get(position).getPromotionid()+"");
                        intent.putExtra("position", 0);
                        startActivity(intent);
                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    }
                }
            });
            return convertView;
        }

    }

    private class GroupBuyAdapter extends BaseAdapter {

        public GroupBuyAdapter() {
            super();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return gbLists.size();
        }

        @Override
        public Promotion getItem(int position) {
            // TODO Auto-generated method stub
            return gbLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mContext,
                        R.layout.item_group_buy_listview, null);
            }
            Promotion promotion = gbLists.get(position);
            CubeImageView iv_promotion = (CubeImageView) convertView
                    .findViewById(R.id.iv_group_buy);
            TextView tv_title = (TextView) convertView
                    .findViewById(R.id.tv_title);
            tv_title.setText(promotion.getName());
            ImageView iv_over_time = (ImageView) convertView.findViewById(R.id.iv_over_time);
            CheckBox cb_item = (CheckBox) convertView.findViewById(R.id.cb_item);
            if(isDelete){
                cb_item.setVisibility(View.VISIBLE);
                if(promotion.isChecked()){
                    cb_item.setChecked(true);
                }else{
                    cb_item.setChecked(false);
                }
            }else{
                cb_item.setVisibility(View.GONE);
            }
            if(promotion.getIs_close()==1) {
                iv_over_time.setImageResource(R.drawable.icon_off_line);
                iv_over_time.setVisibility(View.VISIBLE);
            }else if(Long.parseLong(promotion.getValid_right())*1000  < new Date().getTime()){
                iv_over_time.setImageResource(R.drawable.icon_over_time);
                iv_over_time.setVisibility(View.VISIBLE);
            }else{
                iv_over_time.setVisibility(View.GONE);
            }

            TextView tv_new_price = (TextView) convertView.findViewById(R.id.tv_new_price);
            tv_new_price.setText(promotion.getPromotion_price()+"");
            TextView tv_origin_price = (TextView) convertView.findViewById(R.id.tv_origin_price);
            tv_origin_price.setText("原价:"+promotion.getOrigion_price()+"元");
            tv_origin_price.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
            TextView tv_num = (TextView) convertView.findViewById(R.id.tv_num);
            tv_num.setText("已售:"+promotion.getSellcount());


            if (!TextUtils.isEmpty(promotion.getPoster())) {
                CommonUtils.startImageLoader(cubeimageLoader,promotion.getPoster(), iv_promotion);
            } else {
                CommonUtils.startImageLoader(cubeimageLoader, "www.baidu.com",iv_promotion);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isDelete){
                        if(gbLists.get(position).isChecked()){
                            gbLists.get(position).setChecked(false);
                        }else{
                            gbLists.get(position).setChecked(true);
                        }
                        buyAdapter.notifyDataSetChanged();
                    }else{
                        Intent intent = new Intent(mContext, ActivityGroupBuyDetail.class);
                        intent.putExtra("ids",  gbLists.get(position).getPromotionid()+"");
                        intent.putExtra("position", position);
                        startActivity(intent);
                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    }
                }
            });
            return convertView;
        }

    }
}
