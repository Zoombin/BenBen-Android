package com.xunao.benben.ui.auction;

import android.app.Dialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Auction;
import com.xunao.benben.bean.AuctionDetail;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.account.ActivityMoneyIncome;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by ltf on 2016/1/8.
 */
public class ActivityTopAuctionDetail extends BaseActivity implements View.OnClickListener {
    private LinearLayout ll_bottom;
    private TextView tv_district;
    private LinearLayout ll_industry;
    private TextView tv_industry;
    private TextView tv_top_period;
    private ListView lv_auction;
    private List<AuctionDetail> auctionDetails = new ArrayList<>();
    private AuctionAdapter auctionAdapter;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日 HH:mm");
    private Timer timer;
    private Dialog dialog;
    private TextView tv_dialog_place,tv_dialog_cancel,tv_dialog_add_step,tv_dialog_confirm;
    private EditText edt_dialog_price;

    private Auction auction;
    private int selectId=0;
    private int selectPosition=0;
    private NewsBrocastReceiver brocastReceiver;

    private Socket mSocket;
    private String token;


    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_top_auction_detail);
        brocastReceiver = new NewsBrocastReceiver();
        registerReceiver(brocastReceiver, new IntentFilter("auction"));
    }

    class NewsBrocastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            InteNetUtils.getInstance(mContext).AuctionDetail(auction.getAuction_id(),mRequestCallBack);
        }

    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("置顶拍卖场", "", "",
                R.drawable.icon_com_title_left, 0);
        ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tv_district = (TextView) findViewById(R.id.tv_district);
        ll_industry  = (LinearLayout) findViewById(R.id.ll_industry);
        tv_industry = (TextView) findViewById(R.id.tv_industry);
        tv_top_period = (TextView) findViewById(R.id.tv_top_period);
        lv_auction = (ListView) findViewById(R.id.lv_auction);
        auctionAdapter = new AuctionAdapter();
        lv_auction.setAdapter(auctionAdapter);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        auction = (Auction) getIntent().getSerializableExtra("auction");
        token = getIntent().getStringExtra("token");

        if(CommonUtils.isNetworkAvailable(mContext)){
            InteNetUtils.getInstance(mContext).AuctionDetail(auction.getAuction_id(),mRequestCallBack);
        }else{
            ToastUtils.Infotoast(mContext,"网络不可用");
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long time = new Date().getTime()/1000;
//                if(time%60==1){
                    Message message = new Message();
                    message.what=1;
                    handler.sendMessage(message);
//                }

            }
        },new Date(),1000);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case 1:
                    auctionAdapter.notifyDataSetChanged();
                    break;

            }

        }
    };

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
            ll_bottom.setVisibility(View.VISIBLE);
            tv_district.setText(auction.getDistrict());
            if(auction.getIndustry().equals("")){
                ll_industry.setVisibility(View.GONE);
            }else{
                tv_industry.setText(auction.getIndustry());
                ll_industry.setVisibility(View.VISIBLE);
            }
            tv_top_period.setText(auction.getTop_period());

            auctionDetails.clear();
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("info");
                if(jsonArray!=null && jsonArray.length()>0){
                    String auction_id="";
                    for (int i=0;i<jsonArray.length();i++){
                        AuctionDetail auctionDetail = new AuctionDetail();
                        auctionDetail.parseJSON(jsonArray.getJSONObject(i));
                        auction_id += auctionDetail.getAuction_id()+",";
                        auctionDetails.add(auctionDetail);

                    }
                    auctionAdapter.notifyDataSetChanged();
                    auction_id = auction_id.substring(0,auction_id.length()-1);
                    String ip = "http://112.124.101.177:8080/?user_id="+user.getId()+"&auction_id="+auction_id+"&token="+token;
                    try {
                        if(mSocket == null) {
                            mSocket = IO.socket(ip);
                            mSocket.on("givePrice", onNewMessage);
                            mSocket.on("connect", onConnect);
                            mSocket.connect();
                        }
                    } catch (URISyntaxException e) {
                    }

                }else{
                    ToastUtils.Infotoast(mContext,"暂无拍卖详情");
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
        ToastUtils.Infotoast(mContext,"获取拍卖场详情失败");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){


        }
    }

    public class AuctionAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return auctionDetails.size();
        }

        @Override
        public Object getItem(int i) {
            return auctionDetails.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;

            LayoutInflater inflater = LayoutInflater.from(ActivityTopAuctionDetail.this);

            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.item_top_auction_detail,null);
                holder.tv_start_price  = (TextView)view.findViewById(R.id.tv_start_price);
                holder.tv_time = (TextView)view.findViewById(R.id.tv_time);
                holder.tv_place = (TextView)view.findViewById(R.id.tv_place);
                holder.btn_give_price  = (Button)view.findViewById(R.id.btn_give_price);
                holder.tv_from = (TextView)view.findViewById(R.id.tv_from);
                holder.tv_num = (TextView)view.findViewById(R.id.tv_num);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            final AuctionDetail auctionDetail = auctionDetails.get(position);
            if(auctionDetail.getPlace()==1){
                holder.tv_place.setText("置顶第一位");
                holder.tv_place.setBackgroundColor(Color.parseColor("#FFA247"));
            }else if(auctionDetail.getPlace()==2){
                holder.tv_place.setText("置顶第二位");
                holder.tv_place.setBackgroundColor(Color.parseColor("#2895FB"));
            }if(auctionDetail.getPlace()==3){
                holder.tv_place.setText("置顶第三位");
                holder.tv_place.setBackgroundColor(Color.parseColor("#42D4E1"));
            }
            if(auctionDetail.getNow_price().equals("")){
                holder.tv_start_price.setText(auction.getStart_price() + "");
                holder.tv_from.setText("暂无");
            }else {
                holder.tv_start_price.setText(auctionDetail.getNow_price());
                holder.tv_from.setText(auctionDetail.getFrom());
            }
            holder.tv_num.setText(auctionDetail.getNum()+"人出价");

            long current_time = new Date().getTime()/1000;
            long start_time = auction.getStart_time();
            long end_time = auction.getEnd_time();
            if(current_time<start_time){
                holder.tv_time.setText("开始时间:"+simpleDateFormat.format(start_time*1000));
                holder.tv_time.setTextColor(Color.parseColor("#6ABC4D"));
            }else if(current_time>end_time){
                holder.tv_time.setText("已关闭");
                holder.tv_time.setTextColor(Color.parseColor("#E93434"));
            }else{
                String time = secToTime(end_time-current_time);
                holder.tv_time.setText("剩余时间:"+time);
                holder.tv_time.setTextColor(Color.parseColor("#E93434"));
            }

            holder.btn_give_price.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectPosition = position;
                    showPriceDialog(auctionDetail);

                }
            });

            return view;

        }

        private class ViewHolder {
            private TextView tv_start_price;
            private TextView tv_time;
            private TextView tv_place;
            private Button btn_give_price;
            private TextView tv_from;
            private TextView tv_num;
        }
    }

    private void showPriceDialog(final AuctionDetail auctionDetail) {
        selectId = auctionDetail.getAuction_id();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        dialog = new Dialog(ActivityTopAuctionDetail.this,R.style.myDialog);
        View dialogView = LayoutInflater.from(ActivityTopAuctionDetail.this).inflate(R.layout.dialog_give_price, null);
        edt_dialog_price = (EditText) dialogView.findViewById(R.id.edt_dialog_price);
        tv_dialog_place = (TextView) dialogView.findViewById(R.id.tv_dialog_place);
        tv_dialog_cancel = (TextView) dialogView.findViewById(R.id.tv_dialog_cancel);
        tv_dialog_add_step = (TextView) dialogView.findViewById(R.id.tv_dialog_add_step);
        tv_dialog_confirm = (TextView) dialogView.findViewById(R.id.tv_dialog_confirm);
        dialog.setContentView(dialogView);
        if(auctionDetail.getPlace()==1){
            tv_dialog_place.setText("置顶第一位");
        }else if(auctionDetail.getPlace()==2){
            tv_dialog_place.setText("置顶第二位");
        }if(auctionDetail.getPlace()==3){
            tv_dialog_place.setText("置顶第三位");
        }
        tv_dialog_add_step.setText("加价幅度:"+auction.getAdd_step()+"元");
        tv_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        tv_dialog_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String priceStr = String.valueOf(edt_dialog_price.getText()).trim();
                if(priceStr.equals("")){
                    ToastUtils.Infotoast(mContext,"请输入出价金额");
                }else{
                    double price = Double.parseDouble(priceStr);
                    double nowPrice = auction.getStart_price();
                    if(!auctionDetail.getNow_price().equals("")){
                        nowPrice = Double.parseDouble(auctionDetail.getNow_price());
                    }
                    BigDecimal a = new BigDecimal(Double.toString(price));

                    BigDecimal b = new BigDecimal(Double.toString(nowPrice));

                    double addPrice = a.subtract(b).doubleValue();
                    Log.d("ltf","price==========="+price+"==="+nowPrice+"==="+addPrice+"==="+auction.getAdd_step());
                    if(addPrice<auction.getAdd_step()){
                        ToastUtils.Infotoast(mContext,"出价金额不得小于加价幅度");
                    }else{
                        dialog.dismiss();
                        showLoding("");
                        InteNetUtils.getInstance(mContext).GivePrice(selectId,priceStr,givePriceBack);
                    }
                }
            }
        });

        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialogstyle);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) dialogView.getLayoutParams();
        params.width = dm.widthPixels;
        dialogView.setLayoutParams(params);
        dialog.setCancelable(true);
        dialog.show();


    }

    RequestCallBack<String> givePriceBack = new RequestCallBack<String>() {
        @Override
        public void onSuccess(ResponseInfo<String> stringResponseInfo) {
            dissLoding();
            try {
                JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                if(jsonObject.optInt("ret_num")==0){
                    String top_price = jsonObject.optString("top_price");
                    String top_person = jsonObject.optString("top_person");
                    auctionDetails.get(selectPosition).setNow_price(top_price);
                    auctionDetails.get(selectPosition).setFrom(top_person);
                    auctionAdapter.notifyDataSetChanged();
                }else if(jsonObject.optInt("ret_num")==116){
                    final MsgDialog msgDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
                    msgDialog.setContent("您还未交保证金，无法出价?", "确认缴纳保证金"+auction.getGuarantee()+"元吗?", "确认", "取消");
                    msgDialog.setCancleListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            msgDialog.dismiss();
                        }
                    });
                    msgDialog.setOKListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            msgDialog.dismiss();

                            if(CommonUtils.isNetworkAvailable(mContext)){
                                showLoding("");
                                InteNetUtils.getInstance(mContext).PayGuarantee(selectId, new RequestCallBack<String>() {
                                    @Override
                                    public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                                        dissLoding();
                                        try {
                                            JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                                            if(jsonObject.optInt("ret_num")==0){
                                                ToastUtils.Infotoast(mContext,"您已成功缴纳保证金,可进行出价!");
                                            }else if(jsonObject.optInt("ret_num")==2116){
                                                final MsgDialog msgDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
                                                msgDialog.setContent("账户余额不足", "", "充值", "取消");
                                                msgDialog.setCancleListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        msgDialog.dismiss();
                                                    }
                                                });
                                                msgDialog.setOKListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        msgDialog.dismiss();
                                                        Intent intent = new Intent(ActivityTopAuctionDetail.this, ActivityMoneyIncome.class);
                                                        intent.putExtra("from","auction");
                                                        startActivity(intent);
                                                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                                                    }

                                                });
                                                msgDialog.show();
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
                                        ToastUtils.Infotoast(mContext, "缴纳失败!");
                                    }
                                });
                            }else{
                                ToastUtils.Infotoast(mContext, "网络不可用");
                            }

                        }
                    });
                    msgDialog.show();

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
            ToastUtils.Infotoast(mContext,"出价失败");
        }
    };

    public static String secToTime(long time) {
        String timeStr = null;
        int day = 0;
        int hour = 0;
        int minute = 0;
        int second = 0;

        day = (int) (time / 86400);
        hour = (int)(time % 86400) / 3600;
        minute = (int)((time % 86400) % 3600) / 60;
        second =  (int)((time % 86400) % 3600) % 60;
        timeStr = unitFormat(day)+"天"+unitFormat(hour)+"小时"+unitFormat(minute) + "分"
                + unitFormat(second)+"秒";
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }



    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object[] args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(args!=null && args.length>0) {
                        JSONObject data = (JSONObject) args[0];
                        Log.d("ltf", "data===========" + data);
                        for(int i=0;i<auctionDetails.size();i++){
                            AuctionDetail auctionDetail = auctionDetails.get(i);
                            if(auctionDetail.getAuction_id()==data.optInt("auction_id")){
                                auctionDetail.setFrom(data.optString("from"));
                                auctionDetail.setNow_price(data.optString("price"));
                                break;
                            }
                        }
                        auctionAdapter.notifyDataSetChanged();

                    }

                    // add the message to view
//                    addMessage(username, message);
                }
            });
        }


    };

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object[] args) {
//            JSONObject data = (JSONObject) args[0];

            Log.d("ltf","conn=========="+mSocket.connected());
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("givePrice", onNewMessage);
        mSocket.off("connect", onConnect);
        mSocket=null;
        if(timer!=null){
            timer.cancel();
        }
        unregisterReceiver(brocastReceiver);
    }


}
