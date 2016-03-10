package com.xunao.benben.ui.order;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Order;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ltf on 2016/3/10.
 */
public class ActivityOrderCheckRecord extends BaseActivity{
    private ListView lv_record;
    private List<Order> orderList = new ArrayList<>();
    private myAdapter adapter;

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
        Order order = new Order();
        orderList.add(order);
        Order order1 = new Order();
        orderList.add(order1);
        Order order2 = new Order();
        orderList.add(order2);
        adapter.notifyDataSetChanged();
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

    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {

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


            return convertView;
        }
    }
}
