package com.xunao.benben.ui.order;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Order;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.hx.chatuidemo.activity.ChatActivity;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.item.ActivityNumberTrainDetail;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.utils.ViewHolderUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageTask;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

/**
 * Created by ltf on 2015/12/24.
 */
public class ActivityBusinessOrder extends BaseActivity implements View.OnClickListener,
        PullToRefreshBase.OnRefreshListener<ListView>,PullToRefreshBase.OnLastItemVisibleListener {
    private RelativeLayout order_tab_one;
    private RelativeLayout order_tab_two;
    private RelativeLayout order_tab_three;
    private RelativeLayout order_tab_four;
    private RelativeLayout order_tab_five;
    private RadioButton rb1,rb2,rb3,rb4,rb5;
    private RadioButton[] rbs = new RadioButton[5];
    private LinearLayout no_data;
    private PullToRefreshListView listView;

    private int currentPage=1;
    private int pagNum=1;
    private List<Order> orderList = new ArrayList<>();
    private MyAdapter adapter;
    private String status="";
    private static final int SDK_PAY_FLAG = 1;
    private String order_id;
    private String order_sn;
    private NewsBrocastReceiver brocastReceiver;


    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_business_oreder);
        brocastReceiver = new NewsBrocastReceiver();
        registerReceiver(brocastReceiver, new IntentFilter("businessOrderRefresh"));

        cubeimageLoader.setImageLoadHandler(new DefaultImageLoadHandler(
                mContext) {
            @Override
            public void onLoading(ImageTask imageTask,
                                  CubeImageView cubeImageView) {
                Boolean ispost = (Boolean) cubeImageView
                        .getTag(R.string.ispost);
                if (cubeImageView != null) {
                    if (ispost != null && ispost) {
                        cubeImageView.setImageResource(R.drawable.bg_buy_no_pic);
                    } else {
                        cubeImageView.setImageResource(R.drawable.loading);
                    }
                }

            }

            @Override
            public void onLoadFinish(ImageTask imageTask,
                                     CubeImageView cubeImageView, BitmapDrawable drawable) {
                if (cubeImageView != null) {
                    if (imageTask.getIdentityUrl().equalsIgnoreCase(
                            (String) cubeImageView.getTag())) {

                        cubeImageView.setVisibility(View.VISIBLE);
                        cubeImageView.setImageDrawable(drawable);

                    }

                }
            }

            @Override
            public void onLoadError(ImageTask imageTask,
                                    CubeImageView imageView, int errorCode) {
                if (imageView != null) {
                    Boolean ispost = (Boolean) imageView
                            .getTag(R.string.ispost);
                    if (ispost != null && ispost) {
                        imageView.setImageResource(R.drawable.bg_buy_no_pic);
                    } else {
                        imageView.setImageResource(R.drawable.bg_buy_no_pic);
                    }
                }
            }
        });
    }

    class NewsBrocastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            initOrder();
        }

    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("我是商家", "", "",
                R.drawable.icon_com_title_left, 0);
        order_tab_one = (RelativeLayout) findViewById(R.id.order_tab_one);
        rb1 = (RadioButton) order_tab_one.findViewById(R.id.tab_RB);
        order_tab_two = (RelativeLayout) findViewById(R.id.order_tab_two);
        rb2 = (RadioButton) order_tab_two.findViewById(R.id.tab_RB);
        order_tab_three = (RelativeLayout) findViewById(R.id.order_tab_three);
        rb3 = (RadioButton) order_tab_three.findViewById(R.id.tab_RB);
        order_tab_four = (RelativeLayout) findViewById(R.id.order_tab_four);
        rb4 = (RadioButton) order_tab_four.findViewById(R.id.tab_RB);
        order_tab_five = (RelativeLayout) findViewById(R.id.order_tab_five);
        rb5 = (RadioButton) order_tab_five.findViewById(R.id.tab_RB);
        rbs[0] = rb1;
        rbs[1] = rb2;
        rbs[2] = rb3;
        rbs[3] = rb4;
        rbs[4] = rb5;
        order_tab_one.setOnClickListener(this);
        order_tab_two.setOnClickListener(this);
        order_tab_three.setOnClickListener(this);
        order_tab_four.setOnClickListener(this);
        order_tab_five.setOnClickListener(this);
        order_tab_one.performClick();

        no_data = (LinearLayout) findViewById(R.id.no_data);
        listView = (PullToRefreshListView) findViewById(R.id.listView);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);

        listView.setOnRefreshListener(this);
        listView.setOnLastItemVisibleListener(this);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {

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
        listView.onRefreshComplete();
        isMoreData = false;
        if(!isLoadMore){
            orderList.clear();
        }
        if(jsonObject.optInt("ret_num")==0){
            Log.d("ltf","jsonObject================="+jsonObject);
            try {
                pagNum = jsonObject.optInt("page");
                JSONArray jsonArray = jsonObject.getJSONArray("orderinfo");
                if(jsonArray!=null && jsonArray.length()>0){
                    rb1.setText("全部\n("+jsonObject.optInt("all")+")");
                    rb2.setText("待发货\n("+jsonObject.optInt("confirm")+")");
                    rb3.setText("已发货\n("+jsonObject.optInt("wanna_get")+")");
                    rb4.setText("待评价\n("+jsonObject.optInt("ready_comment")+")");
                    rb5.setText("退款\n("+jsonObject.optInt("back")+")");
                    no_data.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    for(int i=0;i<jsonArray.length();i++){
                        Order order = new Order();
                        order.parseJSON(jsonArray.getJSONObject(i));
                        orderList.add(order);
                    }
                    adapter.notifyDataSetChanged();

                }else{
                    no_data.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NetRequestException e) {
                e.printStackTrace();
            }

        }else if(jsonObject.optInt("ret_num")==8844){
            no_data.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }else{
            ToastUtils.Infotoast(mContext,jsonObject.optString("ret_msg"));
        }

    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        listView.onRefreshComplete();
        ToastUtils.Infotoast(mContext,"获取订单失败");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.order_tab_one:
                setChecked(0);
                status="";
                initOrder();
                break;
            case R.id.order_tab_two:
                setChecked(1);
                status="0";
                initOrder();
                break;
            case R.id.order_tab_three:
                setChecked(2);
                status="1";
                initOrder();
                break;
            case R.id.order_tab_four:
                setChecked(3);
                status="2";
                initOrder();
                break;
            case R.id.order_tab_five:
                setChecked(4);
                status="4";
                initOrder();
                break;
        }
    }


    private void setChecked(int position) {
        for(int i=0;i<rbs.length;i++){
            if(i==position){
                rbs[i].setChecked(true);
            }else{
                rbs[i].setChecked(false);
            }
        }
    }

    private void initOrder(){
        setLoadMore(false);
        currentPage = 1;
        InteNetUtils.getInstance(mContext).Storderlist(currentPage, status, mRequestCallBack);
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        setLoadMore(false);
        currentPage = 1;
        listView.setOnLastItemVisibleListener(this);
        InteNetUtils.getInstance(mContext).Storderlist(currentPage, status, mRequestCallBack);
    }


    @Override
    public void onLastItemVisible() {
        if(currentPage>=pagNum){
//            ToastUtils.Infotoast(mContext,"无更多数据");
        }else{
            setLoadMore(true);
            currentPage++;
            InteNetUtils.getInstance(mContext).Storderlist(currentPage, status, mRequestCallBack);
        }
   }


    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return orderList.size() + 1;
        }

        @Override
        public Object getItem(int arg0) {
            return orderList.get(arg0);
        }

        @Override
        public int getItemViewType(int position) {

            return position <= orderList.size() - 1 ? 0 : 1;
        }

        @Override
        public int getViewTypeCount() {
            // TODO Auto-generated method stub
            return 2;
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            if (getItemViewType(position) == 0) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(
                            R.layout.item_business_order, null);
                }
                LinearLayout ll_user = (LinearLayout) convertView.findViewById(R.id.ll_user);
                RoundedImageView rv_poster = (RoundedImageView) convertView.findViewById(R.id.rv_poster);
                TextView tv_user_name = (TextView) convertView.findViewById(R.id.tv_user_name);
                TextView tv_title_right = (TextView) convertView.findViewById(R.id.tv_title_right);
                CubeImageView iv_promotion_post = (CubeImageView) convertView.findViewById(R.id.iv_promotion_post);
                ImageView iv_promotion_state = (ImageView) convertView.findViewById(R.id.iv_promotion_state);
                TextView tv_promotion_name = (TextView) convertView.findViewById(R.id.tv_promotion_name);
                TextView tv_goods_amount = (TextView) convertView.findViewById(R.id.tv_goods_amount);
                TextView tv_shipping_fee = (TextView) convertView.findViewById(R.id.tv_shipping_fee);
                TextView tv_goods_number = (TextView) convertView.findViewById(R.id.tv_goods_number);
                TextView tv_order_amount = (TextView) convertView.findViewById(R.id.tv_order_amount);
                TextView tv_order_fee = (TextView) convertView.findViewById(R.id.tv_order_fee);
                LinearLayout ll_order_operate = (LinearLayout) convertView.findViewById(R.id.ll_order_operate);
                TextView tv_operate1 = (TextView) convertView.findViewById(R.id.tv_operate1);
                final TextView tv_operate2 = (TextView) convertView.findViewById(R.id.tv_operate2);
                TextView tv_operate3 = (TextView) convertView.findViewById(R.id.tv_operate3);
                TextView tv_operate4 = (TextView) convertView.findViewById(R.id.tv_operate4);
                TextView tv_operate5 = (TextView) convertView.findViewById(R.id.tv_operate5);
                TextView tv_operate6 = (TextView) convertView.findViewById(R.id.tv_operate6);
                final Order order = orderList.get(position);

                iv_promotion_state.setVisibility(View.GONE);
                ll_order_operate.setVisibility(View.GONE);
                tv_operate1.setVisibility(View.GONE);
                tv_operate2.setVisibility(View.GONE);
                tv_operate3.setVisibility(View.GONE);
                tv_operate4.setVisibility(View.GONE);
                tv_operate5.setVisibility(View.GONE);
                tv_operate6.setVisibility(View.GONE);

                CommonUtils.startImageLoader(cubeimageLoader,order.getUser_poster(),rv_poster);
                tv_user_name.setText(order.getNick_name());
                CommonUtils.startImageLoader(cubeimageLoader,order.getPromotion_pic(),iv_promotion_post);
                if(order.getIs_close()==1){
                    iv_promotion_state.setImageResource(R.drawable.icon_off_line);
                    iv_promotion_state.setVisibility(View.VISIBLE);
                }else if(order.getIs_out()==1){
                    iv_promotion_state.setImageResource(R.drawable.icon_over_time);
                    iv_promotion_state.setVisibility(View.VISIBLE);
                }
                tv_promotion_name.setText(order.getGoods_name());
                tv_goods_amount.setText("￥:"+order.getGoods_amount());
                if(order.getPay_id()==1){
                    tv_shipping_fee.setText("运费:"+order.getShipping_fee()+"元");
                }else{
                    tv_shipping_fee.setText("");
                }
                tv_goods_number.setText("数量:x"+order.getGoods_number());

                if(order.getBack_status()==0 || order.getBack_status()==3) {
                    if(order.getBack_status()==3){
                        ll_order_operate.setVisibility(View.VISIBLE);
                        tv_operate5.setText("已拒绝退款");
                        tv_operate5.setVisibility(View.VISIBLE);
                    }

                    if (order.getShipping_status() == 0) {

                        if (order.getPay_status() == 2) {
                            tv_order_amount.setText("实收款:" + order.getOrder_amount() + "元");
                            if(order.getPay_id()==1) {
                                tv_title_right.setText("未发货");
                                tv_order_fee.setText("(含邮费" + order.getShipping_fee() + "元)");
                                ll_order_operate.setVisibility(View.VISIBLE);
                                tv_operate2.setText("手动发货");
                                tv_operate2.setVisibility(View.VISIBLE);
                            }else{
                                tv_title_right.setText(order.getPay_name());
                            }
                        } else{
                            tv_order_amount.setText("待收款:" + order.getOrder_amount() + "元");
                            if(order.getPay_id()==1) {
                                tv_title_right.setText("未发货");
                                tv_order_fee.setText("(含邮费" + order.getShipping_fee() + "元)");
                            }else{
                                tv_title_right.setText(order.getPay_name());
                            }
                            if (order.getOrder_status() == 2) {
                                tv_title_right.setText(order.getPay_name());
                                ll_order_operate.setVisibility(View.VISIBLE);
                                tv_operate5.setText("已取消");
                                tv_operate5.setVisibility(View.VISIBLE);
                            }
                        }
                    } else if (order.getShipping_status() == 1) {
                        tv_title_right.setText("待收货");
                        tv_order_amount.setText("实收款:" + order.getOrder_amount() + "元");
                        if(order.getPay_id()==1) {
                            tv_order_fee.setText("(含邮费" + order.getShipping_fee() + "元)");
                        }
                        ll_order_operate.setVisibility(View.VISIBLE);
                        tv_operate1.setVisibility(View.VISIBLE);
                    } else if (order.getShipping_status() == 2) {
                        tv_title_right.setText(order.getPay_name());
                        tv_order_amount.setText("实收款:" + order.getOrder_amount() + "元");
                        if(order.getPay_id()==1) {
                            tv_order_fee.setText("(含邮费" + order.getShipping_fee() + "元)");
                            tv_operate1.setVisibility(View.VISIBLE);
                        }
                        ll_order_operate.setVisibility(View.VISIBLE);
                        if (order.getExtension_code()!=2 &&(order.getOrder_status() == 5 || order.getOrder_status() == 6)) {
                            tv_operate2.setText("评价");
                            tv_operate2.setVisibility(View.VISIBLE);
                            tv_operate6.setVisibility(View.VISIBLE);
                        }
//                        if (order.getOrder_status() == 5) {
//                            tv_operate2.setText("评价");
//                        } else if (order.getOrder_status() == 6) {
//                            tv_operate2.setText("查看评价");
//                        }
//                        tv_operate2.setVisibility(View.VISIBLE);
                    }
                }else{
                    tv_title_right.setText(order.getPay_name());
                    tv_order_amount.setText("实收款:" + order.getOrder_amount() + "元");
                    if(order.getPay_id()==1) {
                        tv_order_fee.setText("(含邮费" + order.getShipping_fee() + "元)");
                    }
                    ll_order_operate.setVisibility(View.VISIBLE);
                    if(order.getShipping_status()==1){
                        tv_operate1.setVisibility(View.VISIBLE);
                        tv_title_right.setText("待收货");
                    }
                    if (order.getBack_status() == 1) {
                        tv_operate3.setVisibility(View.VISIBLE);
                        tv_operate4.setVisibility(View.VISIBLE);
                        if(order.getShipping_status()!=1 && order.getPay_id()==1){
                            tv_title_right.setText("未发货");
                            tv_operate2.setText("手动发货");
                            tv_operate2.setVisibility(View.VISIBLE);
                        }
                    } else if (order.getBack_status() == 2) {
                        tv_operate5.setText("已退款");
                        tv_operate5.setVisibility(View.VISIBLE);
                    } else if (order.getBack_status() == 4 || order.getBack_status() == 5) {
                        tv_operate5.setText("退款中");
                        tv_operate5.setVisibility(View.VISIBLE);
                    }
                }

                ll_user.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startAnimActivity2Obj(ChatActivity.class, "userId",
                                order.getHuanxin_username());
                    }
                });

                tv_operate1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final MsgDialog msgDialog = new MsgDialog(ActivityBusinessOrder.this, R.style.MyDialogStyle);
                        msgDialog.setContent("查看物流", "运单号:"+order.getShipping_sn(), "复制/查询", "取消");
                        msgDialog.setCancleListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                msgDialog.dismiss();
                            }
                        });
                        msgDialog.setOKListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                msgDialog.dismiss();
                                ClipboardManager mClipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                                ClipData mClipData  = ClipData.newPlainText("text", order.getShipping_sn());
                                mClipboardManager.setPrimaryClip(mClipData);
                                Uri uri = Uri.parse("http://m.kuaidi100.com");
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        });
                        msgDialog.show();
                    }
                });

                tv_operate2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(tv_operate2.getText().equals("手动发货")){
                            startAnimActivity2Obj(ActivityManualSend.class,"order_id",order.getOrder_id());
                        }else if(tv_operate2.getText().equals("评价")){
                            Intent intent = new Intent(ActivityBusinessOrder.this,ActivityBusinessOrderComment.class);
                            intent.putExtra("order", order);
                            startActivity(intent);
                            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                        }
//                        else if(tv_operate2.getText().equals("查看评价")){
//                            Intent intent = new Intent(ActivityBusinessOrder.this,ActivityOrderCommentDetail.class);
//                            intent.putExtra("order", order);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//                        }
                    }
                });

                tv_operate6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ActivityBusinessOrder.this,ActivityOrderCommentDetail.class);
                        intent.putExtra("order", order);
                        startActivity(intent);
                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

                    }
                });

                tv_operate3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final MsgDialog msgDialog = new MsgDialog(ActivityBusinessOrder.this, R.style.MyDialogStyle);
                        msgDialog.setContent("确定同意退款吗?", "", "确定", "取消");
                        msgDialog.setCancleListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                msgDialog.dismiss();
                            }
                        });
                        msgDialog.setOKListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                msgDialog.dismiss();
                                Uri uri = Uri.parse(AndroidConfig.NETHOST + AndroidConfig.Agreeback+"?key=android&order_id="+order.getOrder_id()+"&token="+user.getToken());
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivityForResult(intent,1);
//                                if (CommonUtils.isNetworkAvailable(mContext)) {
//                                    showLoding("");
//                                    InteNetUtils.getInstance(mContext).Agreeback(order.getOrder_id(), new RequestCallBack<String>() {
//                                        @Override
//                                        public void onSuccess(ResponseInfo<String> stringResponseInfo) {
//                                            dissLoding();
//
////                                            Uri uri = Uri.parse(stringResponseInfo.toString());
////                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
////                                            startActivity(intent);
//                                            try {
//                                                Log.d("ltf","jsonObject=============="+stringResponseInfo.result);
//                                                JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
//
//                                                if(jsonObject.optInt("ret_num")==0){
//                                                    ToastUtils.Infotoast(mContext, "同意退款成功");
//                                                    initOrder();
//                                                }else{
//                                                    ToastUtils.Infotoast(mContext, jsonObject.optString("ret_msg"));
//                                                }
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onFailure(HttpException e, String s) {
//                                            dissLoding();
//                                            ToastUtils.Infotoast(mContext, "同意退款失败");
//                                        }
//                                    });
//                                } else {
//                                    ToastUtils.Infotoast(mContext, "网络不可用");
//                                }
                            }
                        });
                        msgDialog.show();
                    }
                });

                tv_operate4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final MsgDialog msgDialog = new MsgDialog(ActivityBusinessOrder.this, R.style.MyDialogStyle);
                        msgDialog.setContent("确定拒绝退款吗?", "", "确定", "取消");
                        msgDialog.setCancleListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                msgDialog.dismiss();
                            }
                        });
                        msgDialog.setOKListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                msgDialog.dismiss();
                                if (CommonUtils.isNetworkAvailable(mContext)) {
                                    showLoding("");
                                    InteNetUtils.getInstance(mContext).Refuse(order.getOrder_id(), new RequestCallBack<String>() {
                                        @Override
                                        public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                                            dissLoding();
                                            try {
                                                JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                                                if(jsonObject.optInt("ret_num")==0){
                                                    ToastUtils.Infotoast(mContext, "拒绝退款成功");
                                                    initOrder();
                                                }else{
                                                    ToastUtils.Infotoast(mContext, jsonObject.optString("ret_msg"));
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onFailure(HttpException e, String s) {
                                            dissLoding();
                                            ToastUtils.Infotoast(mContext, "拒绝退款失败");
                                        }
                                    });
                                } else {
                                    ToastUtils.Infotoast(mContext, "网络不可用");
                                }
                            }
                        });
                        msgDialog.show();
                    }
                });

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startAnimActivity6Obj(ActivityBusinessOrderDetail.class,
                                "order_id", order.getOrder_id(),"pay_name",order.getPay_name(),"extension_code",order.getExtension_code()+"");
                    }
                });

            } else {
                if (isMoreData) {
                    convertView = getLayoutInflater().inflate(
                            R.layout.item_load_more, null);
                    LinearLayout load_more = ViewHolderUtil.get(convertView,
                            R.id.load_more);
                    if (enterNum) {
                        load_more.setVisibility(View.VISIBLE);
                    } else {
                        load_more.setVisibility(View.GONE);
                    }
                } else {
                    convertView = getLayoutInflater().inflate(
                            R.layout.item_no_load_more, null);
                    convertView.setVisibility(View.GONE);
                }
            }

            return convertView;
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                initOrder();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(brocastReceiver);
    }
}
