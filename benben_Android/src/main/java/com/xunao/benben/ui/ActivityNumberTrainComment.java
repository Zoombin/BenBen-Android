package com.xunao.benben.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.OrderComment;
import com.xunao.benben.bean.OrderCommentReply;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.TimeUtil;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.MyTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.srain.cube.image.CubeImageView;

/**
 * Created by ltf on 2015/12/7.
 */
public class ActivityNumberTrainComment extends BaseActivity{
    private ListView lv_comment;
    private MyAdapter myAdapter;
    private List<OrderComment> commentList = new ArrayList<>();
    private String store_id,mean_rate;
    private TextView tv_mean_rate;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_number_train_comment);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("店铺评价", "", "",
                R.drawable.icon_com_title_left, 0);
        tv_mean_rate = (TextView) findViewById(R.id.tv_mean_rate);
        lv_comment = (ListView) findViewById(R.id.lv_comment);
        myAdapter = new MyAdapter();
        lv_comment.setAdapter(myAdapter);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        store_id = getIntent().getStringExtra("store_id");
        mean_rate = getIntent().getStringExtra("mean_rate");
        tv_mean_rate.setText(mean_rate);
        if(CommonUtils.isNetworkAvailable(mContext)){
            InteNetUtils.getInstance(mContext).StoreCommentList(store_id, mRequestCallBack);
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
        if(jsonObject.optInt("ret_num")==0){

            try {
                JSONArray jsonArray = jsonObject.getJSONArray("info");
                if(jsonArray!=null && jsonArray.length()>0){
                    for(int i=0;i<jsonArray.length();i++){
                        OrderComment orderComment = new OrderComment();
                        orderComment.parseJSON(jsonArray.getJSONObject(i));
                        commentList.add(orderComment);
                    }
                    myAdapter.notifyDataSetChanged();
                }

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
        ToastUtils.Infotoast(mContext,"获取评价失败");
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return commentList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return commentList.get(position);
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
                        R.layout.item_number_train_comment, null);
            }
            OrderComment orderComment = commentList.get(position);
            CubeImageView iv_poster = (CubeImageView) convertView
                    .findViewById(R.id.iv_poster);
            TextView tv_name = (TextView) convertView .findViewById(R.id.tv_name);
            TextView tv_rank = (TextView) convertView .findViewById(R.id.tv_rank);
            TextView tv_content = (TextView) convertView .findViewById(R.id.tv_content);
            TextView tv_time = (TextView) convertView .findViewById(R.id.tv_time);
            LinearLayout comment_box = (LinearLayout) convertView.findViewById(R.id.comment_box);

            CommonUtils.startImageLoader(cubeimageLoader,orderComment.getPoster(),iv_poster);
            tv_name.setText(orderComment.getNick_name());
            if(orderComment.getComment_rank()==3){
                tv_rank.setText("综合评分:好评");
            }else if(orderComment.getComment_rank()==2){
                tv_rank.setText("综合评分:中评");
            }else if(orderComment.getComment_rank()==1){
                tv_rank.setText("综合评分:差评");
            }
            tv_content.setText(orderComment.getContent());
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            Date date = new Date(orderComment.getAdd_time()*1000);
//            tv_time.setText(simpleDateFormat.format(date));
            tv_time.setText(TimeUtil
                    .getDescriptionTimeFromTimestamp(orderComment.getAdd_time() * 1000));

            if (orderComment.getOrderCommentReplies() != null && orderComment.getOrderCommentReplies().size() > 0) {
                comment_box.setVisibility(View.VISIBLE);
                comment_box.removeAllViews();
                LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                for (OrderCommentReply reply : orderComment.getOrderCommentReplies()) {
                    MyTextView myTextView = new MyTextView(mContext);
                    String s = reply.getNick_name() + ": " + reply.getContent();
                    SpannableStringBuilder style = new SpannableStringBuilder(
                            s);
                    style.setSpan(
                            new ForegroundColorSpan(Color
                                    .parseColor("#0e7bba")), 0, reply.getNick_name().length() + 2,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    myTextView.setText(style, TextView.BufferType.SPANNABLE);
                    myTextView.setLineSpacing(PixelUtil.dp2px(3), 1);
                    if (comment_box.getChildCount() > 1) {
                        params.topMargin = PixelUtil.dp2px(5);
                    } else {
                        params.topMargin = PixelUtil.dp2px(0);
                    }
                    comment_box.addView(myTextView, params);
                }

            } else {
                comment_box.setVisibility(View.GONE);
            }

            return convertView;
        }

    }
}
