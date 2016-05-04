package com.xunao.benben.ui.trainvip;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 号码直通车会员
 * Created by ltf on 2016/4/18.
 */
public class ActivityTrainVip extends BaseActivity implements View.OnClickListener {
    private TextView tv_num;
    private ListView list;
    private DataAdapter adapter;
    private List<Map<String,String>> mapList = new ArrayList<>();
    private Button btn_confirm;
    private String shop="";
    private String cumulation_type = "0";

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_train_vip);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("会员号", "", "",
                R.drawable.icon_com_title_left, 0);

        tv_num = (TextView) findViewById(R.id.tv_num);
        list = (ListView) findViewById(R.id.list);
        adapter = new DataAdapter();
        list.setAdapter(adapter);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        shop = getIntent().getStringExtra("shop");
        if(CommonUtils.isNetworkAvailable(mContext)){
            InteNetUtils.getInstance(mContext).GetLevel(shop,mRequestCallBack);
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

        setOnRightClickLinester(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAnimActivity3Obj(ActivityTrainVipBill.class,"shop",shop,"cumulation_type",cumulation_type);
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
        Log.d("ltf","jsonObject============"+jsonObject);
        if(jsonObject.optInt("ret_num")==0){
            tv_num.setText("关注人数:"+jsonObject.optString("collects")+"人");
            cumulation_type = jsonObject.optString("cumulation_type");
            if(cumulation_type.equals("0")){
                ToastUtils.Infotoast(mContext,"该号码直通车还未设置会员规则!");
            }else{
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("vip_level");
                    if(jsonArray!=null && jsonArray.length()>0){
                        int is_vip = jsonObject.optInt("is_vip");
                        if(is_vip == 1){
                            btn_confirm.setVisibility(View.GONE);
                            initTitle_Right_Left_bar("会员号", "", "账单",
                                    R.drawable.icon_com_title_left, 0);
                        }else{
                            btn_confirm.setVisibility(View.VISIBLE);
                        }
                        for(int i=0;i<jsonArray.length();i++){
                            Map<String,String> map = new HashMap<>();
                            map.put("feedback",jsonArray.getJSONObject(i).optString("feedback"));
                            map.put("level",jsonArray.getJSONObject(i).optString("level"));
                            map.put("type",jsonArray.getJSONObject(i).optString("type"));
                            map.put("cumulation",jsonArray.getJSONObject(i).optString("cumulation"));
                            mapList.add(map);
                        }
                        adapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }else{
            ToastUtils.Infotoast(mContext,jsonObject.optString("ret_msg"));
        }

    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        ToastUtils.Infotoast(mContext,"获取会员信息失败");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_confirm:
                if(CommonUtils.isNetworkAvailable(mContext)){
                    InteNetUtils.getInstance(mContext).BecomeVip(shop, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                            try {
                                JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                                if(jsonObject.optInt("ret_num")==0){
                                    btn_confirm.setVisibility(View.GONE);
                                    ToastUtils.Infotoast(mContext,"您已成功成为会员!");
                                    initTitle_Right_Left_bar("会员号", "", "账单",
                                            R.drawable.icon_com_title_left, 0);
                                }else{
                                    ToastUtils.Infotoast(mContext,jsonObject.optString("ret_msg"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            ToastUtils.Infotoast(mContext,"操作失败!");
                        }
                    });
                }

                break;
        }
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
            if(map.get("level").equals("1")){
                viewHolder.tv_level.setText("普通会员");
            }else if(map.get("level").equals("2")){
                viewHolder.tv_level.setText("高级会员");
            }else if(map.get("level").equals("3")){
                viewHolder.tv_level.setText("尊享会员");
            }

            if(map.get("type").equals("1")){
                viewHolder.tv_introduce.setText("充值满"+map.get("cumulation"));
                viewHolder.tv_discount.setText("赠送"+map.get("feedback"));
            }else if(map.get("type").equals("2")){
                viewHolder.tv_introduce.setText("消费满"+map.get("cumulation"));
                viewHolder.tv_discount.setText("全场商品全部"+map.get("feedback")+"折");
            }


           return convertView;
        }


        class ViewHolder{
            private TextView tv_level;
            private TextView tv_introduce;
            private TextView tv_discount;
        }
    }
}
