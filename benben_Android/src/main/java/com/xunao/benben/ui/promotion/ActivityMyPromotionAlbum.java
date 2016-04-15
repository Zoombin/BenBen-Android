package com.xunao.benben.ui.promotion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.Album;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 商家角度活动现场相册，促销团购共用
 * Created by ltf on 2015/11/30.
 */
public class ActivityMyPromotionAlbum extends BaseActivity{
    private GridView gv_album;
    private GridAdapter adapter;
    private List<Album> albumList = new ArrayList<>();
    private int maxalbun=6;
    private int maxpic=20;
    private String id;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_promotion_album);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("活动现场相册", "", "",R.drawable.icon_com_title_left, 0);
        gv_album = (GridView) findViewById(R.id.gv_album);
        gv_album.setVisibility(View.GONE);
        adapter = new GridAdapter(this);
        gv_album.setAdapter(adapter);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        id  = getIntent().getStringExtra("id");
        if(CommonUtils.isNetworkAvailable(mContext)){
            InteNetUtils.getInstance(mContext).Activityalbum(id,user.getToken(), mRequestCallBack);
        }else{
            ToastUtils.Infotoast(mContext, "网络不可用");
        }

//        Map<String ,String> map1 = new HashMap<>();
//        map1.put("pic","http://112.124.101.177:81/uploads/images/201511/small1447838070391727710.");
//        map1.put("title","我的");
//        map1.put("time","2015-11-30");
//        map1.put("num","(9张)");
//        albumList.add(map1);
//        Map<String ,String> map2 = new HashMap<>();
//        map2.put("pic","http://112.124.101.177:81/uploads/images/201511/small1447838070391727710.");
//        map2.put("title","我的方式方法是史蒂夫收费手动");
//        map2.put("time","2015-11-30");
//        map2.put("num","(19张)");
//        albumList.add(map2);
//        Map<String ,String> map3 = new HashMap<>();
//        map3.put("pic","http://112.124.101.177:81/uploads/images/201511/small1447838070391727710.");
//        map3.put("title","打工的21312");
//        map3.put("time","2015-11-30");
//        map3.put("num","(1张)");
//        albumList.add(map3);
//        Map<String ,String> map4 = new HashMap<>();
//        map4.put("pic","http://112.124.101.177:81/uploads/images/201511/small1447838070391727710.");
//        map4.put("title","我的");
//        map4.put("time","2015-11-30");
//        map4.put("num","(9张)");
//        albumList.add(map4);
//        Map<String ,String> map5 = new HashMap<>();
//        map5.put("pic","http://112.124.101.177:81/uploads/images/201511/small1447838070391727710.");
//        map5.put("title","我的");
//        map5.put("time","2015-11-30");
//        map5.put("num","(9张)");
////        albumList.add(map5);
//        Map<String ,String> map6 = new HashMap<>();
//        map6.put("pic","http://112.124.101.177:81/uploads/images/201511/small1447838070391727710.");
//        map6.put("title","sdfssffs");
//        map6.put("time","2015-12-01");
//        map6.put("num","(15张)");
//        albumList.add(map6);
//        adapter.notifyDataSetChanged();
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
        if(jsonObject.optInt("ret_num")==0) {
            albumList.clear();
            maxalbun = jsonObject.optInt("maxalbun");
            maxpic = jsonObject.optInt("maxpic");
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("album_info");
                if (jsonArray != null && jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Album album = new Album();
                        album.parseJSON(jsonArray.getJSONObject(i));
                        albumList.add(album);
                    }
                }
                adapter.notifyDataSetChanged();
                gv_album.setVisibility(View.VISIBLE);
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

    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int mWidth = (mScreenWidth - PixelUtil.dp2px(40)) / 2;


        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public int getCount() {
            if (albumList.size() == maxalbun) {
                return maxalbun;
            }
            return (albumList.size() + 1);
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }


        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_promotion_album,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (RoundedImageView) convertView.findViewById(R.id.item_grida_image);
                holder.ll_message = (LinearLayout) convertView.findViewById(R.id.ll_message);
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
                holder.iv_add = (ImageView) convertView.findViewById(R.id.iv_add);
                View v = convertView.findViewById(R.id.box);
                convertView.setTag(holder);
                v.setLayoutParams(new RelativeLayout.LayoutParams(mWidth,mWidth*360/280));
                holder.image.setLayoutParams(new RelativeLayout.LayoutParams(mWidth, mWidth));
//                holder.image.getLayoutParams().width = mWidth;
//                holder.image.getLayoutParams().height = mWidth;

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

//            if (position == albumList.size()) {
//                if (position == maxalbun) {
//                    holder.iv_add.setVisibility(View.GONE);
//                }
//            } else {
            if (position != albumList.size()) {
                holder.image.setVisibility(View.VISIBLE);
                holder.ll_message.setVisibility(View.VISIBLE);
                holder.iv_add.setVisibility(View.GONE);
                CommonUtils.startImageLoader(cubeimageLoader, albumList.get(position).getPoster_cover(), holder.image);
                holder.tv_title.setText(albumList.get(position).getTitle());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                long time = albumList.get(position).getTime();
                Date date = new Date(time*1000);
                holder.tv_time.setText(simpleDateFormat.format(date));
                holder.tv_num.setText("("+albumList.get(position).getPoster_num()+"张)");
            }else{
                holder.image.setVisibility(View.GONE);
                holder.ll_message.setVisibility(View.GONE);
                holder.iv_add.setVisibility(View.VISIBLE);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position != albumList.size()) {
                        Intent intent = new Intent(ActivityMyPromotionAlbum.this, ActivityMyPromotionAlbumDetail.class);
                        intent.putExtra("id",albumList.get(position).getId());
                        intent.putExtra("maxpic",maxpic);
                        startActivityForResult(intent, 1);
                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    }else{
                        Intent intent = new Intent(ActivityMyPromotionAlbum.this, ActivityPromotionAddAlbum.class);
                        startActivityForResult(intent, 1);
                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    }

                }
            });

            return convertView;
        }

        public class ViewHolder {
            private RoundedImageView image;
            private LinearLayout ll_message;
            private TextView tv_title;
            private TextView tv_time;
            private TextView tv_num;
            private ImageView iv_add;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    if(CommonUtils.isNetworkAvailable(mContext)){
                        InteNetUtils.getInstance(mContext).Activityalbum(id,user.getToken(), mRequestCallBack);
                    }else{
                        ToastUtils.Infotoast(mContext, "网络不可用");
                    }
                }
                break;
        }
    }
}
