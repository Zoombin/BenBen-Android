package com.xunao.benben.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.utils.CommonUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.srain.cube.image.CubeImageView;

/**
 * Created by ltf on 2015/12/7.
 */
public class ActivityNumberTrainComment extends BaseActivity{
    private ListView lv_comment;
    private MyAdapter myAdapter;
    private List<Map<String,String>> commentList = new ArrayList<>();

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_number_train_comment);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("评价", "", "",
                R.drawable.icon_com_title_left, 0);
        lv_comment = (ListView) findViewById(R.id.lv_comment);
        myAdapter = new MyAdapter();
        lv_comment.setAdapter(myAdapter);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        Map<String,String> map = new HashMap<>();
        commentList.add(map);
        Map<String,String> map1 = new HashMap<>();
        commentList.add(map1);
        Map<String,String> map2 = new HashMap<>();
        commentList.add(map2);
        Map<String,String> map3 = new HashMap<>();
        commentList.add(map3);
        Map<String,String> map4 = new HashMap<>();
        commentList.add(map4);
        myAdapter.notifyDataSetChanged();
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

    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {

    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return commentList.size();
        }

        @Override
        public Map<String,String> getItem(int position) {
            // TODO Auto-generated method stub
            return commentList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mContext,
                        R.layout.item_number_train_comment, null);
            }

            CubeImageView iv_promotion = (CubeImageView) convertView
                    .findViewById(R.id.iv_group_buy);


            return convertView;
        }

    }
}
