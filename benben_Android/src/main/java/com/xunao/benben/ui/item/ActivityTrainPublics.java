package com.xunao.benben.ui.item;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.HttpException;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Bill;
import com.xunao.benben.bean.TrainPublic;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.groupbuy.ActivityGroupBuyDetail;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.utils.ViewHolderUtil;
import com.xunao.benben.view.NoScrollGridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.srain.cube.image.CubeImageView;

/**
 * 号码直通车大喇叭列表
 * Created by ltf on 2016/5/3.
 */
public class ActivityTrainPublics extends BaseActivity implements PullToRefreshBase.OnRefreshListener<ListView>,PullToRefreshBase.OnLastItemVisibleListener{
    private String train_id="";
    private PullToRefreshListView listview;
    private List<TrainPublic> trainPublics = new ArrayList<>();
    private DataAdapter adapter;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
    private int currentPage=1;
    private int pagNum=1;
    private BitmapUtils bitmapUtils;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_train_publics);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        bitmapUtils = new BitmapUtils(mContext);
        String title = getIntent().getStringExtra("title");
        initTitle_Right_Left_bar(title, "", "",
                R.drawable.icon_com_title_left, R.drawable.icon_com_title_more2);

        listview = (PullToRefreshListView) findViewById(R.id.listview);
        adapter = new DataAdapter();
        listview.setAdapter(adapter);

        listview.setOnRefreshListener(this);
        listview.setOnLastItemVisibleListener(this);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        train_id = getIntent().getStringExtra("ID");
        setLoadMore(false);
        currentPage = 1;
        InteNetUtils.getInstance(mContext).TrainNews(train_id, currentPage, mRequestCallBack);
    }

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
                startAnimActivity2Obj(ActivityNumberTrainDetail.class, "id", train_id);
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
        listview.onRefreshComplete();
        isMoreData = false;
        if(!isLoadMore){
            trainPublics.clear();
        }
        if(jsonObject.optInt("ret_num")==0){
            try {
                pagNum = jsonObject.optInt("ap");
                JSONArray jsonArray = jsonObject.getJSONArray("list");
                if(jsonArray!=null && jsonArray.length()>0){
                    for (int i=0;i<jsonArray.length();i++){
                        TrainPublic trainPublic = new TrainPublic();
                        trainPublic.parseJSON(jsonArray.getJSONObject(i));
                        trainPublics.add(trainPublic);
                    }
                    adapter.notifyDataSetChanged();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NetRequestException e) {
                e.printStackTrace();
            }
        }else{
            ToastUtils.Infotoast(mContext, jsonObject.optString("ret_msg"));
        }
    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        listview.onRefreshComplete();
        ToastUtils.Infotoast(mContext,"获取信息失败");
    }

    @Override
    public void onLastItemVisible() {
        if(currentPage>=pagNum){
//            ToastUtils.Infotoast(mContext,"无更多数据");
        }else{
            setLoadMore(true);
            currentPage++;
            InteNetUtils.getInstance(mContext).TrainNews(train_id, currentPage, mRequestCallBack);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        setLoadMore(false);
        currentPage = 1;
        listview.setOnLastItemVisibleListener(this);
        InteNetUtils.getInstance(mContext).TrainNews(train_id, currentPage, mRequestCallBack);
    }

    class DataAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return trainPublics.size() + 1;
        }

        @Override
        public Object getItem(int arg0) {
            return trainPublics.get(arg0);
        }

        @Override
        public int getItemViewType(int position) {

            return position <= trainPublics.size() - 1 ? 0 : 1;
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
                            R.layout.activity_train_public_item, null);
                }
                TextView tv_time = (TextView) convertView
                        .findViewById(R.id.tv_time);
                TextView tv_content = (TextView) convertView
                        .findViewById(R.id.tv_content);

                String time = trainPublics.get(position).getCreated_time();
                time  = simpleDateFormat.format(new Date(Long.parseLong(time)*1000));
                tv_time.setText(time);
                String content = trainPublics.get(position).getContent();
                tv_content.setText(content);
                NoScrollGridView item_gridView = (NoScrollGridView) convertView
                        .findViewById(R.id.item_gridView);
                final String images = trainPublics.get(position).getImages();
                if (!TextUtils.isEmpty(images)) {
                    String[] split = images.split("\\^");
                    item_gridView.setVisibility(View.VISIBLE);
                    MyGridViewAdapter gridAdapter = new MyGridViewAdapter(split);
                    item_gridView.setAdapter(gridAdapter);
                    item_gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(
                                AdapterView<?> arg0, View arg1,
                                int arg2, long arg3) {
                            startActivity2StringAndPosition(
                                    ActivityContentPicSet.class,
                                    "IMAGES", images,
                                    arg2);
                        }
                    });
                }

                final String ids = trainPublics.get(position).getIdentity1();
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ActivityGroupBuyDetail.class);
                        intent.putExtra("ids", ids);
                        intent.putExtra("position", 0);
                        startActivity(intent);
                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    }
                });


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

    private class MyGridViewAdapter extends BaseAdapter {

        String[] images;

        public MyGridViewAdapter(String[] images) {
            super();
            this.images = images;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return images.length;
        }

        @Override
        public String getItem(int position) {
            // TODO Auto-generated method stub
            return images[position];
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
                        R.layout.item_friend_gridview, null);
            }

            convertView
                    .setLayoutParams(new AbsListView.LayoutParams(
                            AbsListView.LayoutParams.MATCH_PARENT, PixelUtil
                            .dp2px(60)));
            String poster = getItem(position);
            if (!TextUtils.isEmpty(poster)) {
                bitmapUtils.display(((CubeImageView) convertView),getItem(position));
            } else {
                CommonUtils.startImageLoader(cubeimageLoader, "www.baidu.com",
                        ((CubeImageView) convertView));
            }
            return convertView;
        }

    }

    public void startActivity2StringAndPosition(Class<?> cla, String key,
                                                String value, int position) {
        Intent intent = new Intent(this, cla);
        intent.putExtra(key, value);
        intent.putExtra("POSITION", position);
        this.startActivity(intent);
    }
}
