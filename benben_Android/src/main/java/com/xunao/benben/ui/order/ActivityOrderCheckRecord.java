package com.xunao.benben.ui.order;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Order;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ltf on 2016/3/10.
 */
public class ActivityOrderCheckRecord extends BaseActivity{
    private ListView lv_record;
    private List<Order> orderList = new ArrayList<>();
    private myAdapter adapter;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_order_check_record);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("记录", "", "",
                R.drawable.icon_com_title_left, 0);
        lv_record = (ListView)findViewById(R.id.lv_record);
        adapter = new myAdapter();
        lv_record.setAdapter(adapter);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        if(CommonUtils.isNetworkAvailable(mContext)){
            InteNetUtils.getInstance(mContext).ConsumeRecords(user.getToken(), mRequestCallBack);
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
        Log.d("ltf","jsonObject============="+jsonObject);
        if(jsonObject.optInt("ret_num")==0){
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("list");
                if(jsonArray!=null && jsonArray.length()>0){
                    for(int i=0;i<jsonArray.length();i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        Order order = new Order();
                        order.parseJSON1(object);
                        orderList.add(order);
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    ToastUtils.Infotoast(mContext,"暂无记录");
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
        ToastUtils.Infotoast(mContext,"获取信息失败");
    }


    class myAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return orderList.size();
        }

        @Override
        public Object getItem(int arg0) {
            return orderList.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(
                        R.layout.item_order_check_record, null);

            }
            TextView tv_order_sn = (TextView) convertView.findViewById(R.id.tv_order_sn);
            TextView tv_order_time = (TextView) convertView.findViewById(R.id.tv_order_time);
            TextView tv_order_name = (TextView) convertView.findViewById(R.id.tv_order_name);

            Order order = orderList.get(position);
            tv_order_sn.setText("订单号:"+order.getOrder_sn());
            long consume_time = order.getConsume_time();
            tv_order_time.setText(simpleDateFormat.format(new Date(consume_time*1000)));
            tv_order_name.setText("标题:"+order.getGoods_name());

            return convertView;
        }
    }
}
