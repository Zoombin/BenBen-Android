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
 * 商家角度相册详情及操作，促销团购共用
 * Created by ltf on 2015/11/30.
 */
public class ActivityMyPromotionAlbumDetail extends BaseActivity implements View.OnClickListener {
    private RoundedImageView rv_album_cover;
    private LinearLayout ll_name;
    private EditText edt_name;
    private TextView tv_name,tv_time,tv_max_num,tv_num;
    private GridView noScrollgridview;
    private GridAdapter adapter;
    private boolean isCover = false;

    private int id=0;
    private int maxpic;
    private static final int TAKE_PICTURE = 0x000001;
    private static final int PIC_Select_CODE_ImageFromLoacal = 2; // 从相册
    private static final int Update_Name = 3; // 修改相册名称
    public static Bitmap bimap;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private File poster_cover;
    private Bitmap bitMap;
    private Album album;
    private ArrayList<AlbumPic> albumPics = new ArrayList<>();
    private ArrayList<AlbumPic> newAlbumPics = new ArrayList<>();
    private FinalBitmap finalBitmap;
    private String imagePath;
    private int changeWhat=1;

    private boolean isDelete = false;
    private LinearLayout ll_all;
    private CheckBox all_checkbox;
    private TextView tv_delete;


    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_promotion_album_detail);
        Res.init(this);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;
        CrashApplication.getInstance().addActivity(this);
        bimap = BitmapFactory.decodeResource(getResources(),
                R.drawable.icon_addpic_unfocused);
        PublicWay.activityList.add(this);
        finalBitmap = FinalBitmap.create(mContext);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("相册详情", "", "编辑",R.drawable.icon_com_title_left, 0);
        rv_album_cover = (RoundedImageView) findViewById(R.id.rv_album_cover);
        rv_album_cover.setOnClickListener(this);
        ll_name = (LinearLayout) findViewById(R.id.ll_name);
        ll_name.setOnClickListener(this);
        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_name.setVisibility(View.GONE);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_name.setVisibility(View.VISIBLE);
        tv_max_num = (TextView) findViewById(R.id.tv_max_num);
        tv_num = (TextView) findViewById(R.id.tv_num);
        tv_time = (TextView) findViewById(R.id.tv_time);
        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        adapter.update();
        noScrollgridview.setAdapter(adapter);
//        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//                                    long arg3) {
//                if(isDelete){
//                    if (arg2 != albumPics.size()) {
//                        if(albumPics.get(arg2).isChecked()){
//                            albumPics.get(arg2).setChecked(false);
//                            if(selectAlbumPics.contains(albumPics.get(arg2))){
//                                selectAlbumPics.remove(albumPics.get(arg2));
//                            }
//                        }else{
//                            albumPics.get(arg2).setChecked(true);
//                            if(!selectAlbumPics.contains(albumPics.get(arg2))){
//                                selectAlbumPics.add(albumPics.get(arg2));
//                            }
//                        }
//                        adapter.notifyDataSetChanged();
//                    }
//                }else {
//                    if (arg2 == albumPics.size()) {
//                        if (albumPics.size() < maxpic) {
//                            if ((maxpic - albumPics.size()) / 6 > 0) {
//                                PublicWay.num = 6;
//                            } else {
//                                PublicWay.num = (maxpic - albumPics.size()) % 6;
//                            }
//                            isCover = false;
//                            changeImage();
//                        }
//                    } else {
//                        String Images = null;
//                        for (int i = 0; i < albumPics.size(); i++) {
//                            if (Images != null) {
//                                Images += "^" + albumPics.get(i).getPoster();
//                            } else {
//                                Images = albumPics.get(i).getPoster();
//                            }
//                        }
//                        Intent intent = new Intent(ActivityPromotionAlbumDetail.this, ActivityContentPicSet.class);
//                        intent.putExtra("IMAGES", Images);
//                        intent.putExtra("POSITION", arg2);
//                        ActivityPromotionAlbumDetail.this.startActivity(intent);
//
//                    }
//                }
//            }
//        });
        ll_all = (LinearLayout)findViewById(R.id.ll_all);
        all_checkbox = (CheckBox) findViewById(R.id.all_checkbox);
        all_checkbox.setOnCheckedChangeListener(changeListener);
        tv_delete = (TextView) findViewById(R.id.tv_delete);
        tv_delete.setOnClickListener(this);
    }

    CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton arg0, boolean checked) {
            if (checked) {
                for (AlbumPic albumPic : albumPics) {
                    albumPic.setChecked(true);
                }
            } else {
                for (AlbumPic albumPic : albumPics) {
                    albumPic.setChecked(false);
                }
            }
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void initDate(Bundle savedInstanceState) {
        id  = getIntent().getIntExtra("id",0);
        maxpic = getIntent().getIntExtra("maxpic",0);
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

        setOnRightClickLinester(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isDelete) {
                    ll_all.setVisibility(View.GONE);
                    initTitle_Right_Left_bar("相册详情", "", "编辑",R.drawable.icon_com_title_left, 0);
                    isDelete = false;
                    adapter.notifyDataSetChanged();
                }else{
                    setTheme(R.style.ActionSheetStyleIOS7);
                    showMoreActionSheet();
                }


            }
        });
    }

    public void showMoreActionSheet() {
        ActionSheet
            .createBuilder(this, getSupportFragmentManager())
            .setCancelButtonTitle("取消")
            .setOtherButtonTitles("删除相册","删除图片")
                    // 设置颜色 必须一一对应
            .setOtherButtonTitlesColor("#1E82FF", "#1E82FF")
            .setCancelableOnTouchOutside(true)
            .setListener(new ActionSheet.ActionSheetListener() {

                @Override
                public void onOtherButtonClick(ActionSheet actionSheet,
                                               int index) {
                    switch (index) {
                        case 0:
                            final MsgDialog msgDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
                            msgDialog.setContent("确定删除吗?", "", "确认", "取消");
                            msgDialog.setCancleListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    msgDialog.dismiss();
                                }
                            });
                            msgDialog.setOKListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    msgDialog.dismiss();
                                    changeWhat = 3;
                                    showLoding("");
                                    InteNetUtils.getInstance(mContext).Delalbum(id, editAlbum);
                                }
                            });
                            msgDialog.show();
                            break;
                        case 1:
                            if(albumPics!=null && albumPics.size()>0) {
                                isDelete = true;
                                initTitle_Right_Left_bar("相册详情", "", "取消", R.drawable.icon_com_title_left,
                                        0);
                                ll_all.setVisibility(View.VISIBLE);
                                adapter.notifyDataSetChanged();
                            }else{
                                ToastUtils.Infotoast(mContext,"当前无照片删除!");
                            }
                            break;

                        default:
                            break;
                    }
                }

                @Override
                public void onDismiss(ActionSheet actionSheet,
                                      boolean isCancel) {
                    // TODO Auto-generated method stub

                }
            }).show();
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
            CommonUtils.startImageLoader(cubeimageLoader,
                    album.getSmall_poster_cover(), rv_album_cover);

            tv_name.setText(album.getTitle());
            tv_max_num.setText("(每次可上传6张,仅限"+maxpic+"张图片)");
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

    protected void onRestart() {
        int size = Bimp.tempSelectBitmap.size();
        if(size>0){
            String[] images = new String[size];
            for (int i = 0; i < size; i++) {
                images[i] = Bimp.tempSelectBitmap.get(i).getImagePath();
            }
            changeWhat = 2;
            showLoding("");
            InteNetUtils.getInstance(mContext).Addphoto(id, images,editAlbum);
            Bimp.tempSelectBitmap.clear();
        }
        super.onRestart();
    }

    // 显示拍照选照片 弹窗
    private void changeImage() {
        setTheme(R.style.ActionSheetStyleIOS7);
        showActionSheet();
    }

    public void showActionSheet() {
        ActionSheet
                .createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle("取消")
                .setOtherButtonTitles("拍摄新图片", "从相册选择")
                        // 设置颜色 必须一一对应
                .setOtherButtonTitlesColor("#1E82FF", "#1E82FF")
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet,
                                                   int index) {
                        switch (index) {
                            case 0:
                                photo();
                                break;
                            case 1:
                                if (isCover) {
                                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                            "image/*");
                                    startActivityForResult(intent, PIC_Select_CODE_ImageFromLoacal);
                                } else {
                                    Intent intent = new Intent(
                                            ActivityMyPromotionAlbumDetail.this,
                                            ImageFile.class);
                                    startActivity(intent);
                                    overridePendingTransition(
                                            R.anim.activity_translate_in,
                                            R.anim.activity_translate_out);
                                }
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onDismiss(ActionSheet actionSheet,
                                          boolean isCancel) {
                        // TODO Auto-generated method stub

                    }
                }).show();
    }

    public void photo() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rv_album_cover:
                isCover = true;
                changeImage();
                break;
            case R.id.ll_name:
                Intent intent = new Intent(ActivityMyPromotionAlbumDetail.this, ActivityUpdateAlbumName.class);
                intent.putExtra("name", tv_name.getText().toString());
                intent.putExtra("id", id);
                startActivityForResult(intent, Update_Name);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
            case R.id.tv_delete:
                String picid = "";
                for (AlbumPic albumPic : albumPics) {
                    if(albumPic.isChecked()){
                        picid += albumPic.getPicid()+",";
                    }
                }
                if(picid.equals("")){
                    ToastUtils.Infotoast(this,"请选择需要删除的图片！");
                }else {
                    this.showLoding("删除中...");
                    picid=picid.substring(0,picid.length()-1);
                    changeWhat=4;
                    InteNetUtils.getInstance(mContext).Delphoto(id,picid,editAlbum);
                }
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int selectedPosition = -1;
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
            if (albumPics.size() == maxpic) {
                return albumPics.size();
            }
            return (albumPics.size() + 1);
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
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
                holder.item_select = (CheckBox) convertView.findViewById(R.id.item_select);
                View v = convertView.findViewById(R.id.box);
                convertView.setTag(holder);

                v.setLayoutParams(new RelativeLayout.LayoutParams(mWidth, mWidth ));

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if(isDelete){
                holder.item_select.setVisibility(View.VISIBLE);
            }else{
                holder.item_select.setVisibility(View.GONE);
            }

            if (position == albumPics.size()) {
                holder.item_select.setVisibility(View.GONE);
                holder.image.setImageResource(R.drawable.icon_addpic_unfocused);
//				decodeFile = PhotoUtils.rotaingImageView(
//						PhotoUtils.readPictureDegree(image.getAbsolutePath()),
//						image.getAbsolutePath());

                if (position == maxpic) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                AlbumPic albumPic = albumPics.get(position);
                finalBitmap.display(holder.image,albumPic.getSmall_poster());
                if(albumPic.isChecked()){
                    holder.item_select.setChecked(true);
                }else{
                    holder.item_select.setChecked(false);
                }
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isDelete){
                        if (position != albumPics.size()) {
                            if(albumPics.get(position).isChecked()){
                                albumPics.get(position).setChecked(false);
                            }else{
                                albumPics.get(position).setChecked(true);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }else {
                        if (position == albumPics.size()) {
                            if (albumPics.size() < maxpic) {
                                if ((maxpic - albumPics.size()) / 6 > 0) {
                                    PublicWay.num = 6;
                                } else {
                                    PublicWay.num = (maxpic - albumPics.size()) % 6;
                                }
                                isCover = false;
                                changeImage();
                            }
                        } else {
                            String Images = null;
                            for (int i = 0; i < albumPics.size(); i++) {
                                if (Images != null) {
                                    Images += "^" + albumPics.get(i).getPoster();
                                } else {
                                    Images = albumPics.get(i).getPoster();
                                }
                            }
                            Intent intent = new Intent(ActivityMyPromotionAlbumDetail.this, ActivityContentPicSet.class);
                            intent.putExtra("IMAGES", Images);
                            intent.putExtra("POSITION", position);
                            ActivityMyPromotionAlbumDetail.this.startActivity(intent);

                        }
                    }
                }
            });

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
            public CheckBox item_select;
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        public void loading() {
            adapter.notifyDataSetChanged();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if(resultCode == RESULT_OK) {
                    String fileName = String.valueOf(System.currentTimeMillis());
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    String saveBitmap = FileUtils.saveBitmapPhoto(bm, fileName);
                    if (isCover) {
                        imagePath = saveBitmap;
                        poster_cover = new File(saveBitmap);
                        changeWhat = 1;
                        showLoding("");
                        InteNetUtils.getInstance(mContext).Editalbum(id, "", poster_cover, editAlbum);
                    } else{
//                        ImageItem takePhoto = new ImageItem();
//                        takePhoto.setBitmap(new SoftReference<Bitmap>(bm));
//                        takePhoto.setImagePath(saveBitmap);
//                        Bimp.tempSelectBitmap.add(takePhoto);
                        changeWhat = 2;
                        String[] images={saveBitmap};
                        showLoding("");
                        InteNetUtils.getInstance(mContext).Addphoto(id, images,editAlbum);
                    }
                }
                break;
            case PIC_Select_CODE_ImageFromLoacal:
                if (data != null) {
                    if (data.getData() != null) {
                        Uri uri = data.getData();
                        ContentResolver cr = getContentResolver();
                        try {
                            InputStream openInputStream = cr.openInputStream(uri);
                            if (openInputStream != null) {
                                imagePath = CutImageUtils.getImagePath(mContext,
                                        openInputStream);
                                poster_cover = new File(imagePath);
                                showLoding("");
                                changeWhat = 1;
                                InteNetUtils.getInstance(mContext).Editalbum(id, "", poster_cover, editAlbum);
                            } else {
                                ToastUtils.Infotoast(mContext, "请选择正确的图像资源");
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case Update_Name:
                if(resultCode == RESULT_OK && data!=null) {
                    String name = data.getStringExtra("name");
                    tv_name.setText(name);
                    setResult(RESULT_OK,null);
                }

                break;
        }
    }

    RequestCallBack editAlbum = new RequestCallBack() {
        @Override
        public void onSuccess(ResponseInfo responseInfo) {
            dissLoding();
            switch (changeWhat){
                case 1:
                    rv_album_cover.setImageBitmap(getBitmap(imagePath));
                    setResult(RESULT_OK, null);
                    break;
                case 2:
                    setResult(RESULT_OK, null);
                    InteNetUtils.getInstance(mContext).Albumdetail(id, mRequestCallBack);
                    break;
                case 3:
                    setResult(RESULT_OK, null);
                    AnimFinsh();
                    break;
                case 4:
                    setResult(RESULT_OK, null);
                    newAlbumPics.clear();
                    for (AlbumPic albumPic : albumPics) {
                        if(!albumPic.isChecked()){
                            newAlbumPics.add(albumPic);
                        }
                    }
                    ll_all.setVisibility(View.GONE);
                    initTitle_Right_Left_bar("相册详情", "", "编辑",R.drawable.icon_com_title_left, 0);
                    isDelete = false;
                    albumPics = newAlbumPics;
                    adapter.notifyDataSetChanged();
                    break;
            }

        }

        @Override
        public void onFailure(HttpException e, String s) {
            dissLoding();
            switch (changeWhat){
                case 1:
                    ToastUtils.Infotoast(mContext,"修改封面失败!");
                    break;
                case 2:
                    ToastUtils.Infotoast(mContext,"添加图片失败!");
                    break;
                case 3:
                    ToastUtils.Infotoast(mContext,"删除相册失败!");
                    break;
                case 4:
                    ToastUtils.Infotoast(mContext,"删除图片失败!");
                    break;
            }

        }
    };

    private Bitmap getBitmap(String path) {
        // 获取屏幕宽高
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeigh = dm.heightPixels;
        return bitMap = CutImageUtils.convertToBitmap(path, screenWidth,
                screenHeigh);
    }


    @Override
    public void onBackPressed() {
        this.finish();
        this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PublicWay.num = 6;
        Bimp.tempSelectBitmap.clear();
        CrashApplication.getInstance().removeActivity(this);
        if (bitMap != null) {
            bitMap.recycle();
        }
    }
}
