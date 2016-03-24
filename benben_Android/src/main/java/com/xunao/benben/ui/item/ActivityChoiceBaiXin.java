package com.xunao.benben.ui.item;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.AddressInfo;
import com.xunao.benben.bean.BaiXin;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ltf on 2016/3/17.
 */
public class ActivityChoiceBaiXin extends BaseActivity{
    private ListView list;
    private List<BaiXin> baiXins = new ArrayList<>();
    private MyAdapter adapter;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_choice_baixin);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("请选择虚拟网", "", "",
                R.drawable.icon_com_title_left, 0);
        list = (ListView) findViewById(R.id.list);
        adapter = new MyAdapter();
        list.setAdapter(adapter);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        if(CommonUtils.isNetworkAvailable(mContext)){
            InteNetUtils.getInstance(mContext).AllBx(mRequestCallBack);
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
        Log.d("ltf","jsonObject=============="+jsonObject);
        if(jsonObject.optInt("ret_num")==0){
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("bx");
                if(jsonArray!=null && jsonArray.length()>0){
                    for(int i=0;i<jsonArray.length();i++){
                        BaiXin baiXin = new BaiXin();
                        baiXin.parseJSON(jsonArray.getJSONObject(i));
                        baiXins.add(baiXin);
                    }
                }
                adapter.notifyDataSetChanged();
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
        ToastUtils.Infotoast(mContext,"获取百姓网失败");
    }


    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return baiXins.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View converView, ViewGroup arg2) {

            final BaiXin baiXin = baiXins.get(position);

            if (converView == null) {
                converView = LayoutInflater.from(mContext).inflate(
                        R.layout.activity_choice_address_item, null);
            }

            ((TextView) converView.findViewById(R.id.address_name))
                    .setText(baiXin.getName());

            converView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent();
                    intent.putExtra("enterprise_id", baiXin.getId());
                    intent.putExtra("name", baiXin.getName());
                    setResult(RESULT_OK,intent);
                    AnimFinsh();
                }
            });

            return converView;
        }

    }
}
