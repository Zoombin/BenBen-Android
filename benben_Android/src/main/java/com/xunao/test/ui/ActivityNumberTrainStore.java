package com.xunao.test.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.test.R;
import com.xunao.test.base.BaseActivity;
import com.xunao.test.bean.TrainStore;
import com.xunao.test.exception.NetRequestException;
import com.xunao.test.net.InteNetUtils;
import com.xunao.test.utils.CommonUtils;
import com.xunao.test.utils.PhoneUtils;
import com.xunao.test.utils.ToastUtils;
import com.xunao.test.view.ActionSheet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ltf on 2015/12/7.
 */
public class ActivityNumberTrainStore extends BaseActivity{
    private TextView tv_num;
    private ListView lv_store;
    private MyAdapter myAdapter;
    private List<TrainStore> trainStores = new ArrayList<>();
    private String trainid;
    private double longitude;
    private double latitude;


    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_number_train_store);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("适用门店", "", "",
                R.drawable.icon_com_title_left, 0);
        tv_num = (TextView) findViewById(R.id.tv_num);
        lv_store = (ListView) findViewById(R.id.lv_store);
        myAdapter = new MyAdapter();
        lv_store.setAdapter(myAdapter);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        myAdapter.notifyDataSetChanged();
        trainid = getIntent().getStringExtra("trainid");
        longitude = getIntent().getDoubleExtra("longitude", 0);
        latitude = getIntent().getDoubleExtra("latitude",0);
        if(CommonUtils.isNetworkAvailable(mContext)){
            InteNetUtils.getInstance(mContext).Alldeparts(trainid, longitude + "", latitude + "", user.getToken(), mRequestCallBack);
        }else{
            ToastUtils.Infotoast(mContext, "网络不可用");
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
        Log.d("ltf","jsonObject=================="+jsonObject);
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("shops");
            if(jsonArray!=null && jsonArray.length()>0){
                for(int i=0;i<jsonArray.length();i++){
                    TrainStore trainStore = new TrainStore();
                    trainStore.parseJSON(jsonArray.getJSONObject(i));
                    trainStores.add(trainStore);
                }
            }
            tv_num.setText("共"+trainStores.size()+"家门店");
            myAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NetRequestException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        ToastUtils.Infotoast(mContext, "获取门店失败");
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return trainStores.size();
        }

        @Override
        public TrainStore getItem(int position) {
            // TODO Auto-generated method stub
            return trainStores.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mContext,R.layout.item_number_train_store, null);
            }
            final TrainStore trainStore = trainStores.get(position);
            TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            TextView tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            TextView tv_distance = (TextView) convertView.findViewById(R.id.tv_distance);
            ImageView iv_phone = (ImageView) convertView.findViewById(R.id.iv_phone);

            tv_name.setText(trainStore.getShort_name());
            tv_address.setText(trainStore.getArea());
            tv_distance.setText(trainStore.getDistance());
            iv_phone.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    setTheme(R.style.ActionSheetStyleIOS7);
                    String phone = trainStore.getPhone();
                    String telPhone = trainStore.getTelephone();
                    String telPhones = phone + "," + telPhone;
                    String[] phones = telPhones.split(",");

                    String curNmae = trainStore.getShort_name();

                    int cid = Integer.parseInt(trainid);
                    showActionSheet(cid,phones,curNmae);

                }
            });



            return convertView;
        }

    }

    public void showActionSheet(final int cid,final String[] phone,final String curNmae) {
        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle("取消")
                .setOtherButtonTitles(phone)
                        // 设置颜色 必须一一对应
                .setOtherButtonTitlesColor("#1E82FF")
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet,
                                                   int index) {
                        switch (index) {
                            case 0:
                                PhoneUtils.makeCall(cid, curNmae, phone[0], mContext);
                                break;
                            case 1:
                                PhoneUtils.makeCall(cid,curNmae, phone[1], mContext);
                                break;
                        }
                    }

                    @Override
                    public void onDismiss(ActionSheet actionSheet,
                                          boolean isCancel) {

                    }
                }).show();
    }
}
