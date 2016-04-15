package com.xunao.benben.ui.promotion;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.Album;
import com.xunao.benben.bean.AlbumPic;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.item.ActivityContentPicSet;
import com.xunao.benben.ui.item.ImageFile;
import com.xunao.benben.utils.Bimp;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.CutImageUtils;
import com.xunao.benben.utils.FileUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.PublicWay;
import com.xunao.benben.utils.Res;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.ActionSheet;

import net.tsz.afinal.FinalBitmap;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 用户视角活动相册详情
 * Created by ltf on 2015/11/30.
 */
public class ActivityPromotionAlbumDetail extends BaseActivity{
    private TextView tv_time,tv_num;
    private GridView noScrollgridview;
    private GridAdapter adapter;
    private int id=0;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Album album;
    private ArrayList<AlbumPic> albumPics = new ArrayList<>();
    private FinalBitmap finalBitmap;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_promotion_album_detail);
        finalBitmap = FinalBitmap.create(mContext);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("相册详情", "", "",R.drawable.icon_com_title_left, 0);
        tv_num = (TextView) findViewById(R.id.tv_num);
        tv_time = (TextView) findViewById(R.id.tv_time);
        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        adapter.update();
        noScrollgridview.setAdapter(adapter);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        id  = getIntent().getIntExtra("id",0);
        if(CommonUtils.isNetworkAvailable(mContext)){
            InteNetUtils.getInstance(mContext).Albumdetail(id, mRequestCallBack);
        }else{
            ToastUtils.Infotoast(mContext, "网络不可用");
        }
    }

    @Override
    public void initLinstener(Bundle savedInstanceState) {
        setOnLeftClickLinester(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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
        try {
            album = new Album();
            album.parseJSON(jsonObject);
            tv_num.setText("("+album.getPoster_num()+"张)");
            long time = album.getTime();
            Date date = new Date(time*1000);
            tv_time.setText(simpleDateFormat.format(date));
            albumPics = album.getAlbumPics();
            adapter.notifyDataSetChanged();
        } catch (NetRequestException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        ToastUtils.Infotoast(mContext,"获取信息失败");
    }


    @SuppressLint("HandlerLeak")
    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private boolean shape;
        private int mWidth = (mScreenWidth - PixelUtil.dp2px(35)) / 4;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        public int getCount() {
            return albumPics.size();
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
                convertView = inflater.inflate(R.layout.item_album_detail,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                View v = convertView.findViewById(R.id.box);
                convertView.setTag(holder);

                v.setLayoutParams(new RelativeLayout.LayoutParams(mWidth, mWidth ));

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            AlbumPic albumPic = albumPics.get(position);
            finalBitmap.display(holder.image,albumPic.getSmall_poster());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        String Images = null;
                        for (int i = 0; i < albumPics.size(); i++) {
                            if (Images != null) {
                                Images += "^" + albumPics.get(i).getPoster();
                            } else {
                                Images = albumPics.get(i).getPoster();
                            }
                        }
                        Intent intent = new Intent(ActivityPromotionAlbumDetail.this, ActivityContentPicSet.class);
                        intent.putExtra("IMAGES", Images);
                        intent.putExtra("POSITION", position);
                        ActivityPromotionAlbumDetail.this.startActivity(intent);
                 }
            });

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

        public void loading() {
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onBackPressed() {
        this.finish();
        this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
