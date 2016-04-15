package com.xunao.benben.ui.order;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
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
import com.xunao.benben.bean.Order;
import com.xunao.benben.bean.OrderComment;
import com.xunao.benben.bean.OrderCommentReply;
import com.xunao.benben.bean.SmallMakeDataComment;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.hx.chatuidemo.utils.SmileUtils;
import com.xunao.benben.hx.chatuidemo.widget.PasteEditText;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.MyTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.srain.cube.image.CubeImageView;

/**
 * 订单评价详细
 * Created by ltf on 2015/12/25.
 */
public class ActivityOrderCommentDetail extends BaseActivity implements View.OnClickListener {
    private Order order;
    private CubeImageView iv_promotion_post;
    private TextView tv_promotion_name;
    private RelativeLayout prerecord_tab_one;
    private RelativeLayout prerecord_tab_three;
    private int is_seller = 0;
    private List<OrderComment> orderComments = new ArrayList<>();
    private List<OrderComment> personOrderComments = new ArrayList<>();
    private List<OrderComment> businessOrderComments = new ArrayList<>();
    private ListView lv_comment;
    private MyAdapter myAdapter;
    private int selectPosition=0;
    private int selectCommentId=0;
    private View bar_bottom;
    private PasteEditText mEditTextContent;
    private View buttonSend;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_order_comment_detail);

    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("查看评价", "", "",
                R.drawable.icon_com_title_left, 0);
        iv_promotion_post = (CubeImageView) findViewById(R.id.iv_promotion_post);
        tv_promotion_name = (TextView) findViewById(R.id.tv_promotion_name);
        bar_bottom = findViewById(R.id.bar_bottom);
        mEditTextContent = (PasteEditText) findViewById(R.id.et_sendmessage);
        buttonSend = findViewById(R.id.btn_send);
        buttonSend.setOnClickListener(this);
        mEditTextContent.setHint("说点什么吧...");
        prerecord_tab_one = (RelativeLayout) findViewById(R.id.prerecord_tab_one);
        prerecord_tab_three = (RelativeLayout) findViewById(R.id.prerecord_tab_three);
        prerecord_tab_one.setOnClickListener(this);
        prerecord_tab_three.setOnClickListener(this);
        prerecord_tab_one.performClick();
        setChecked(prerecord_tab_one,true);
        lv_comment = (ListView) findViewById(R.id.lv_comment);
        myAdapter = new MyAdapter();
        lv_comment.setAdapter(myAdapter);

        sethideSend(lv_comment);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        order = (Order) getIntent().getSerializableExtra("order");
        CommonUtils.startImageLoader(cubeimageLoader, order.getPromotion_pic(), iv_promotion_post);
        tv_promotion_name.setText(order.getGoods_name());
        if(CommonUtils.isNetworkAvailable(mContext)){
            InteNetUtils.getInstance(mContext).OrderCommentList(order.getOrder_id(), mRequestCallBack);
        }else{
            ToastUtils.Infotoast(mContext, "网络不可用");
        }
    }

    @Override
    public void initLinstener(Bundle savedInstanceState) {
        setOnLeftClickLinester(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    personOrderComments.clear();
                    businessOrderComments.clear();
                    for(int i=0;i<jsonArray.length();i++){
                        OrderComment orderComment = new OrderComment();
                        orderComment.parseJSON(jsonArray.getJSONObject(i));
                        if(orderComment.getIs_seller()==0){
                            personOrderComments.add(orderComment);
                        }else if(orderComment.getIs_seller()==1){
                            businessOrderComments.add(orderComment);
                        }
                    }
                    if(is_seller==0){
                        orderComments = personOrderComments;
                    }else if(is_seller==1){
                        orderComments = businessOrderComments;
                    }
                    myAdapter.notifyDataSetChanged();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NetRequestException e) {
                e.printStackTrace();
            }


        }else{
            ToastUtils.Infotoast(mContext, jsonObject.optString("ret_msg"));
        }
    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        ToastUtils.Infotoast(mContext, "获取评价信息失败");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.prerecord_tab_one:
                if (bar_bottom.getVisibility() == View.VISIBLE) {
                    bar_bottom.setVisibility(View.GONE);
                    mEditTextContent.setText("");
                }
                if(is_seller!=0) {
                    is_seller=0;
                    setChecked(prerecord_tab_one, true);
                    setChecked(prerecord_tab_three, false);
                    orderComments = personOrderComments;
                    myAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.prerecord_tab_three:
                if (bar_bottom.getVisibility() == View.VISIBLE) {
                    bar_bottom.setVisibility(View.GONE);
                    mEditTextContent.setText("");
                }
                if(is_seller!=1) {
                    is_seller = 1;
                    setChecked(prerecord_tab_one, false);
                    setChecked(prerecord_tab_three, true);
                    orderComments = businessOrderComments;
                    myAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.btn_send:
                String s = mEditTextContent.getText().toString();
                if (TextUtils.isEmpty(s.trim())) {
                    ToastUtils.Errortoast(mContext, "评论不可为空");
                }else if (!CommonUtils.StringIsSurpass2(s.trim(), 1, 200)) {
                    ToastUtils.Errortoast(mContext, "评论限制在1-200个字之间!");
                }else {
                    if (CommonUtils.isNetworkAvailable(mContext)) {
                        hideKeyboard();
                        showLoding("请稍等...");
                        InteNetUtils.getInstance(mContext).Reply(order.getOrder_id(), selectCommentId, 1, s, order.getPromotion_id(),
                                new RequestCallBack<String>() {
                                    @Override
                                    public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                                        dissLoding();
                                        try {
                                            JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                                            if(jsonObject.optInt("ret_num")==0){
                                                mEditTextContent.setText("");
                                                if(CommonUtils.isNetworkAvailable(mContext)){
                                                    InteNetUtils.getInstance(mContext).OrderCommentList(order.getOrder_id(), mRequestCallBack);
                                                }else{
                                                    ToastUtils.Infotoast(mContext, "网络不可用");
                                                }
                                            }else{
                                                ToastUtils.Errortoast(mContext, jsonObject.optString("ret_msg"));
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(HttpException e, String s) {
                                        dissLoding();
                                        ToastUtils.Errortoast(mContext, "回复失败");
                                    }
                                });

                    }else{
                        ToastUtils.Errortoast(mContext, "网络不可用");
                    }
                }
                break;
        }
    }

    private void setChecked(RelativeLayout view, boolean isCheck) {
        RadioButton tab_RB = (RadioButton) view.findViewById(R.id.tab_RB);
        tab_RB.setChecked(isCheck);

    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return orderComments.size();
        }

        @Override
        public OrderComment getItem(int position) {
            // TODO Auto-generated method stub
            return orderComments.get(position);
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
                        R.layout.item_order_comment, null);
            }
            final OrderComment orderComment = orderComments.get(position);
            TextView tv_name = (TextView) convertView .findViewById(R.id.tv_name);
            TextView tv_rank = (TextView) convertView .findViewById(R.id.tv_rank);
            TextView tv_content = (TextView) convertView .findViewById(R.id.tv_content);
            TextView tv_time = (TextView) convertView .findViewById(R.id.tv_time);
            TextView tv_reply = (TextView) convertView .findViewById(R.id.tv_reply);
            LinearLayout comment_box = (LinearLayout) convertView.findViewById(R.id.comment_box);
            if(orderComment.getIs_seller()==0) {
                tv_name.setText("个人评价");
            }else if(orderComment.getIs_seller()==1) {
                tv_name.setText("商家评价");
            }
            if(orderComment.getComment_rank()==3){
                tv_rank.setText("综合评分:好评");
            }else if(orderComment.getComment_rank()==2){
                tv_rank.setText("综合评分:中评");
            }else if(orderComment.getComment_rank()==1){
                tv_rank.setText("综合评分:差评");
            }
            tv_content.setText(orderComment.getContent());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
            Date date = new Date(orderComment.getAdd_time()*1000);
            tv_time.setText(simpleDateFormat.format(date));

            if (orderComment.getOrderCommentReplies() != null && orderComment.getOrderCommentReplies().size() > 0) {
                comment_box.setVisibility(View.VISIBLE);
                comment_box.removeAllViews();
                LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                for (OrderCommentReply reply : orderComment.getOrderCommentReplies()) {
                    MyTextView myTextView = new MyTextView(mContext);
                    String name = "";
                    if(reply.getIs_seller()==0){
                        name = "个人";
                    }else{
                        name = "商家";
                    }


                    String s = name + ": " + reply.getContent();
                    SpannableStringBuilder style = new SpannableStringBuilder(
                            s);
                    style.setSpan(
                            new ForegroundColorSpan(Color
                                    .parseColor("#000000")), 0, name.length() + 2,
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

            tv_reply.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    selectPosition = position;
                    selectCommentId = orderComment.getComment_id();
                    bar_bottom.setVisibility(View.VISIBLE);
                    showKeyBoard();
                }
            });

            return convertView;
        }

    }

    private void showKeyBoard() {
        // mEditTextContent.setFocusable(true);
        mEditTextContent.requestFocus();
        InputMethodManager m = (InputMethodManager) mEditTextContent
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        // mEditTextContent.setFocusableInTouchMode(true);
    }

    private void sethideSend(ListView listview) {
        listview.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (bar_bottom.getVisibility() == View.VISIBLE) {
                    bar_bottom.setVisibility(View.GONE);
                    // mEditTextContent.setText("");
                }
                hideKeyboard();
                return false;
            }
        });
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (bar_bottom.getVisibility() == View.VISIBLE) {
                    bar_bottom.setVisibility(View.GONE);
                    // mEditTextContent.setText("");
                }
                hideKeyboard();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });
    }
}
