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
 * 用户视角活动相册列表
 * Created by ltf on 2015/11/30.
 */
public class ActivityPromotionAlbum extends BaseActivity{
    private GridView gv_album;
    private GridAdapter adapter;
    private List<Album> albumList = new ArrayList<>();
    private LinearLayout no_data;
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
        no_data = (LinearLayout) findViewById(R.id.no_data);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        id  = getIntent().getStringExtra("id");
        if(CommonUtils.isNetworkAvailable(mContext)){
            InteNetUtils.getInstance(mContext).Activityalbum(id,user.getToken(), mRequestCallBack);
        }else{
            ToastUtils.Infotoast(mContext, "网络不可用");
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
        albumList.clear();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("album_info");
            if(jsonArray!=null && jsonArray.length()>0){
                for(int i=0;i<jsonArray.length();i++){
                    Album album = new Album();
                    album.parseJSON(jsonArray.getJSONObject(i));
                    albumList.add(album);
                }
            }
            adapter.notifyDataSetChanged();
            if(albumList!=null && albumList.size()>0){
                gv_album.setVisibility(View.VISIBLE);
                no_data.setVisibility(View.GONE);
            }else{
                gv_album.setVisibility(View.GONE);
                no_data.setVisibility(View.VISIBLE);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NetRequestException e) {
            e.printStackTrace();
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
            return albumList.size();
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

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

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


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                Intent intent = new Intent(ActivityPromotionAlbum.this, ActivityPromotionAlbumDetail.class);
                intent.putExtra("id",albumList.get(position).getId());
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
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


}
