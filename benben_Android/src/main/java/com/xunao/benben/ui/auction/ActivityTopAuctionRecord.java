package com.xunao.benben.ui.auction;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.AuctionRecord;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ltf on 2016/1/8.
 */
public class ActivityTopAuctionRecord extends BaseActivity{
    private ListView lv_record;
    private List<AuctionRecord> auctionRecords = new ArrayList<>();
    private AuctionRecordAdapter adapter;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_top_auction_record);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("历史记录", "", "",
                R.drawable.icon_com_title_left, 0);
        lv_record = (ListView) findViewById(R.id.lv_record);
        adapter = new AuctionRecordAdapter();
        lv_record.setAdapter(adapter);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        InteNetUtils.getInstance(mContext).GetLog(mRequestCallBack);
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
        Log.d("ltf","jsonObject==========="+jsonObject);
        if(jsonObject.optInt("ret_num")==0){
            auctionRecords.clear();
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("info");
                if(jsonArray!=null && jsonArray.length()>0){
                    for (int i=0;i<jsonArray.length();i++){
                        AuctionRecord auctionRecord = new AuctionRecord();
                        auctionRecord.parseJSON(jsonArray.getJSONObject(i));
                        auctionRecords.add(auctionRecord);
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    ToastUtils.Infotoast(mContext,"暂无历史记录");
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
        ToastUtils.Infotoast(mContext,"获取历史记录失败");
    }


    public class AuctionRecordAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return auctionRecords.size();
        }

        @Override
        public Object getItem(int i) {
            return auctionRecords.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;

            LayoutInflater inflater = LayoutInflater.from(ActivityTopAuctionRecord.this);

            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.item_top_auction_record,null);
                holder.tv_place = (TextView)view.findViewById(R.id.tv_place);
                holder.tv_price  = (TextView)view.findViewById(R.id.tv_price);
                holder.tv_district = (TextView)view.findViewById(R.id.tv_district);
                holder.ll_industry  = (LinearLayout)view.findViewById(R.id.ll_industry);
                holder.tv_industry = (TextView)view.findViewById(R.id.tv_industry);
                holder.tv_top_period = (TextView)view.findViewById(R.id.tv_top_period);
                holder.tv_time = (TextView)view.findViewById(R.id.tv_time);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            AuctionRecord auctionRecord = auctionRecords.get(position);
            if(auctionRecord.getPlace()==1){
                holder.tv_place.setText("置顶第一位");
                holder.tv_place.setBackgroundColor(Color.parseColor("#FFA247"));
            }else if(auctionRecord.getPlace()==2){
                holder.tv_place.setText("置顶第二位");
                holder.tv_place.setBackgroundColor(Color.parseColor("#2895FB"));
            }if(auctionRecord.getPlace()==3){
                holder.tv_place.setText("置顶第三位");
                holder.tv_place.setBackgroundColor(Color.parseColor("#42D4E1"));
            }
            holder.tv_price.setText("中标价:"+auctionRecord.getAuction_price()+"元");
            holder.tv_district.setText(auctionRecord.getDistrict());
            if(auctionRecord.getIndustry().equals("")){
                holder.ll_industry.setVisibility(View.GONE);
            }else{
                holder.tv_industry.setText(auctionRecord.getIndustry());
                holder.ll_industry.setVisibility(View.VISIBLE);
            }
            holder.tv_top_period.setText(auctionRecord.getTop_period());
            Date date = new Date(auctionRecord.getAuction_time()*1000);
            holder.tv_time.setText(format.format(date));

            return view;

        }

        private class ViewHolder {
            private TextView tv_place;
            private TextView tv_price;
            private TextView tv_district;
            private LinearLayout ll_industry;
            private TextView tv_industry;
            private TextView tv_top_period;
            private TextView tv_time;

        }
    }
}
