package com.xunao.benben.ui.trainvip;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 号码直通车会员
 * Created by ltf on 2016/4/18.
 */
public class ActivityTrainVip extends BaseActivity{
    private TextView tv_num;
    private ListView list;
    private DataAdapter adapter;
    private List<Map<String,String>> mapList = new ArrayList<>();
    private Button btn_confirm;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_train_vip);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("会员号", "", "账单",
                R.drawable.icon_com_title_left, 0);

        tv_num = (TextView) findViewById(R.id.tv_num);
        list = (ListView) findViewById(R.id.list);
        adapter = new DataAdapter();
        list.setAdapter(adapter);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        Map<String,String> map1 = new HashMap<>();
        map1.put("level","普通会员");
        map1.put("introduce","关注即可");
        map1.put("discount","全场商品全部9.5折");
        mapList.add(map1);
        Map<String,String> map2 = new HashMap<>();
        map2.put("level","VIP1");
        map2.put("introduce","消费满500元");
        map2.put("discount","全场商品全部9折");
        mapList.add(map2);
        Map<String,String> map3 = new HashMap<>();
        map3.put("level","VIP2");
        map3.put("introduce","消费满1000元");
        map3.put("discount","全场商品全部8.5折");
        mapList.add(map3);
        Map<String,String> map4 = new HashMap<>();
        map4.put("level","VIP3");
        map4.put("introduce","消费满2000元");
        map4.put("discount","全场商品全部8折");
        mapList.add(map4);
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

        setOnRightClickLinester(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAnimActivity(ActivityTrainVipBill.class);
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

    class DataAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mapList.size();
        }

        @Override
        public Object getItem(int position) {
            return mapList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.item_train_vip, null);

                viewHolder.tv_level = (TextView) convertView.findViewById(R.id.tv_level);
                viewHolder.tv_introduce = (TextView) convertView.findViewById(R.id.tv_introduce);
                viewHolder.tv_discount = (TextView) convertView.findViewById(R.id.tv_discount);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Map<String,String> map = mapList.get(position);
            viewHolder.tv_level.setText(map.get("level"));
            viewHolder.tv_introduce.setText(map.get("introduce"));
            viewHolder.tv_discount.setText(map.get("discount"));


            return convertView;
        }


        class ViewHolder{
            private TextView tv_level;
            private TextView tv_introduce;
            private TextView tv_discount;
        }
    }
}
