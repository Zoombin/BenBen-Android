package com.xunao.benben.ui.account;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Bill;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.utils.ViewHolderUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ltf on 2016/1/5.
 */
public class ActivityMyBill extends BaseActivity implements View.OnClickListener,
        PullToRefreshBase.OnRefreshListener<ListView>,PullToRefreshBase.OnLastItemVisibleListener {
    private TextView tv_message;
    private PullToRefreshListView lv_detail;
    private List<Bill> billList = new ArrayList<>();
    private DataAdapter adapter;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private int currentPage=1;
    private int pagNum=1;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_bill);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("我的账单", "", "",
                R.drawable.icon_com_title_left, 0);
        tv_message = (TextView) findViewById(R.id.tv_message);
        lv_detail = (PullToRefreshListView) findViewById(R.id.lv_detail);
        adapter = new DataAdapter();
        lv_detail.setAdapter(adapter);

        lv_detail.setOnRefreshListener(this);
        lv_detail.setOnLastItemVisibleListener(this);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        setLoadMore(false);
        currentPage = 1;
        InteNetUtils.getInstance(mContext).MyPayLog(currentPage, mRequestCallBack);
//        if(CommonUtils.isNetworkAvailable(mContext)){
//            InteNetUtils.getInstance(mContext).MyPayLog(mRequestCallBack);
//        }else {
//            ToastUtils.Infotoast(mContext,"网络不可用");
//        }
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
        Log.d("ltf","jsonObject============="+jsonObject);
        lv_detail.onRefreshComplete();
        isMoreData = false;
        if(!isLoadMore){
            billList.clear();
        }
        if(jsonObject.optInt("ret_num")==0){
            try {
                pagNum = jsonObject.optInt("ap");
                JSONArray jsonArray = jsonObject.getJSONArray("info");
                if(jsonArray!=null && jsonArray.length()>0){
                    tv_message.setVisibility(View.GONE);
                    lv_detail.setVisibility(View.VISIBLE);
                    for (int i=0;i<jsonArray.length();i++){
                        Bill bill = new Bill();
                        bill.parseJSON(jsonArray.getJSONObject(i));
                        billList.add(bill);
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    tv_message.setVisibility(View.VISIBLE);
                    lv_detail.setVisibility(View.GONE);
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
        lv_detail.onRefreshComplete();
        ToastUtils.Infotoast(mContext,"获取账单信息失败");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_address:
                startAnimActivity(ActivityAccountAddressManage.class);
                break;
        }
    }

    @Override
    public void onLastItemVisible() {
        if(currentPage>=pagNum){
//            ToastUtils.Infotoast(mContext,"无更多数据");
        }else{
            setLoadMore(true);
            currentPage++;
            InteNetUtils.getInstance(mContext).MyPayLog(currentPage, mRequestCallBack);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        setLoadMore(false);
        currentPage = 1;
        lv_detail.setOnLastItemVisibleListener(this);
        InteNetUtils.getInstance(mContext).MyPayLog(currentPage, mRequestCallBack);
    }

    class DataAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return billList.size() + 1;
        }

        @Override
        public Object getItem(int arg0) {
            return billList.get(arg0);
        }

        @Override
        public int getItemViewType(int position) {

            return position <= billList.size() - 1 ? 0 : 1;
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
                            R.layout.item_my_bill, null);
                }
                ImageView iv_poster = (ImageView) convertView.findViewById(R.id.iv_poster);
                TextView tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                TextView tv_money = (TextView) convertView.findViewById(R.id.tv_money);
                TextView tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                TextView tv_status = (TextView) convertView.findViewById(R.id.tv_status);

                Bill bill = billList.get(position);
                tv_content.setText(bill.getContent());
                tv_time.setText(simpleDateFormat.format(bill.getTime()*1000));
                String status = bill.getOrder_type();
                String typeMessage = "";
                if(status.equals("1")){
                    iv_poster.setImageResource(R.drawable.icon_outcome);
                    typeMessage = "-";
                    tv_status.setText("会员预付款支付");
                }else if(status.equals("2")){
                    iv_poster.setImageResource(R.drawable.icon_income);
                    typeMessage = "+";
                    tv_status.setText("退款");
                }else{
                    iv_poster.setImageResource(R.drawable.icon_outcome);
                    typeMessage = "-";
                    tv_status.setText("订单支付");
                }
                tv_money.setText(typeMessage+bill.getFee());

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

//    private class DataAdapter extends BaseAdapter {
//        public int getCount() {
//            if (billList != null) {
//                return billList.size();
//            }
//            return 0;
//        }
//
//        public Object getItem(int position) {
//            if (billList != null) {
//                return billList.get(position);
//            }
//            return null;
//        }
//
//        public long getItemId(int position) {
//            return position;
//        }
//
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            ViewHolder localViewHolder = null;
//
//            LayoutInflater inflater = LayoutInflater.from(ActivityMyBill.this);
//
//            if (convertView == null) {
//                localViewHolder = new ViewHolder();
//
//                convertView = inflater.inflate(R.layout.item_my_bill, null);
//                localViewHolder.iv_poster = (ImageView) convertView.findViewById(R.id.iv_poster);
//                localViewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
//                localViewHolder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
//                localViewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
//                localViewHolder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
//                convertView.setTag(localViewHolder);
//            } else {
//                localViewHolder = (ViewHolder) convertView.getTag();
//            }
//            Bill bill = billList.get(position);
//            localViewHolder.tv_content.setText(bill.getContent());
//            localViewHolder.tv_time.setText(simpleDateFormat.format(bill.getTime()*1000));
//            String status = bill.getOrder_type();
//            String typeMessage = "";
//            if(status.equals("1")){
//                localViewHolder.iv_poster.setImageResource(R.drawable.icon_outcome);
//                typeMessage = "-";
//                localViewHolder.tv_status.setText("会员预付款支付");
//            }else if(status.equals("2")){
//                localViewHolder.iv_poster.setImageResource(R.drawable.icon_income);
//                typeMessage = "+";
//                localViewHolder.tv_status.setText("退款");
//            }else{
//                localViewHolder.iv_poster.setImageResource(R.drawable.icon_outcome);
//                typeMessage = "-";
//                localViewHolder.tv_status.setText("订单支付");
//            }
//            localViewHolder.tv_money.setText(typeMessage+bill.getFee());
//
//
//            return convertView;
//        }
//
//        class ViewHolder {
//            private ImageView iv_poster;
//            private TextView tv_time;
//            private TextView tv_money;
//            private TextView tv_content;
//            private TextView tv_status;
//        }
//    }
}
