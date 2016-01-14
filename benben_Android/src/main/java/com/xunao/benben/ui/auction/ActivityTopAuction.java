package com.xunao.benben.ui.auction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.test.UiThreadTest;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Auction;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.account.ActivityMoneyIncome;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ltf on 2016/1/7.
 */
public class ActivityTopAuction extends BaseActivity implements View.OnClickListener {
    private LinearLayout ll_bottom;
    private TextView tv_available_money,tv_freeze_money;
    private ListView lv_auction;
    private List<Auction> auctions = new ArrayList<>();
    private AuctionAdapter auctionAdapter;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日 HH:mm");
    private Timer timer;
    private String token="";

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_top_auction);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("置顶拍卖场", "", "记录",
                R.drawable.icon_com_title_left, 0);
        ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
        ll_bottom.setOnClickListener(this);
        tv_available_money = (TextView) findViewById(R.id.tv_available_money);
        tv_freeze_money = (TextView) findViewById(R.id.tv_freeze_money);
        lv_auction = (ListView) findViewById(R.id.lv_auction);
        auctionAdapter = new AuctionAdapter();
        lv_auction.setAdapter(auctionAdapter);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        if(CommonUtils.isNetworkAvailable(mContext)){
            InteNetUtils.getInstance(mContext).GetAuctionList(mRequestCallBack);
            InteNetUtils.getInstance(mContext).AuctionSet(new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                    try {
                        JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                        Log.d("ltf","jsonObject============="+jsonObject);
                        token = jsonObject.optString("token");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {

                }
            });

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

        setOnRightClickLinester(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAnimActivity(ActivityTopAuctionRecord.class);
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
        ll_bottom.setVisibility(View.VISIBLE);
        tv_available_money.setText(jsonObject.optString("fee")+"元");
        tv_freeze_money.setText(jsonObject.optString("guarantee")+"元");
        auctions.clear();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("info");
            if(jsonArray!=null && jsonArray.length()>0){
                for (int i=0;i<jsonArray.length();i++){
                    Auction auction = new Auction();
                    auction.parseJSON(jsonArray.getJSONObject(i));
                    auctions.add(auction);
                }
                auctionAdapter.notifyDataSetChanged();
            }else{
                ToastUtils.Infotoast(mContext,"暂无拍卖场");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NetRequestException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        ToastUtils.Infotoast(mContext,"获取拍卖场失败");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_bottom:
                Intent intent = new Intent(ActivityTopAuction.this, ActivityMoneyIncome.class);
                intent.putExtra("from","auction");
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;

        }
    }

    public class AuctionAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return auctions.size();
        }

        @Override
        public Object getItem(int i) {
            return auctions.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;

            LayoutInflater inflater = LayoutInflater.from(ActivityTopAuction.this);

            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.item_top_auction,null);
                holder.tv_start_price  = (TextView)view.findViewById(R.id.tv_start_price);
                holder.tv_time = (TextView)view.findViewById(R.id.tv_time);
                holder.tv_district = (TextView)view.findViewById(R.id.tv_district);
                holder.ll_industry  = (LinearLayout)view.findViewById(R.id.ll_industry);
                holder.tv_industry = (TextView)view.findViewById(R.id.tv_industry);
                holder.tv_top_period = (TextView)view.findViewById(R.id.tv_top_period);
                holder.tv_num = (TextView)view.findViewById(R.id.tv_num);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            final Auction auction = auctions.get(position);

            holder.tv_start_price.setText(auction.getStart_price()+"");
            holder.tv_district.setText(auction.getDistrict());
            if(auction.getIndustry().equals("")){
                holder.ll_industry.setVisibility(View.GONE);
            }else{
                holder.tv_industry.setText(auction.getIndustry());
                holder.ll_industry.setVisibility(View.VISIBLE);
            }
            holder.tv_top_period.setText(auction.getTop_period());
            holder.tv_num.setText(auction.getNum()+"人围观");

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

//            if(auction.getIs_close()==1){
//                holder.tv_time.setText("已关闭");
//                holder.tv_time.setTextColor(Color.parseColor("#E93434"));
//            }else if(auction.getIs_start()==0){
//                holder.tv_time.setText("开始时间:"+simpleDateFormat.format(auction.getStart_time()*1000));
//                holder.tv_time.setTextColor(Color.parseColor("#6ABC4D"));
//            }else{
//                long restTime = auction.getRest_time();
//
//                holder.tv_time.setText("剩余时间:"+auction.getRest_time());
//                holder.tv_time.setTextColor(Color.parseColor("#E93434"));
//            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ActivityTopAuction.this, ActivityTopAuctionDetail.class);
                    intent.putExtra("auction", auction);
                    intent.putExtra("token",token);
                    startActivityForResult(intent,2);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
            });

            return view;

        }

        private class ViewHolder {
            private TextView tv_start_price;
            private TextView tv_time;
            private TextView tv_district;
            private LinearLayout ll_industry;
            private TextView tv_industry;
            private TextView tv_top_period;
            private TextView tv_num;
        }
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer!=null){
            timer.cancel();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    InteNetUtils.getInstance(mContext).GetAuctionList(mRequestCallBack);
                }
                break;
            case 2:
                InteNetUtils.getInstance(mContext).GetAuctionList(mRequestCallBack);
                break;
        }
    }
}
