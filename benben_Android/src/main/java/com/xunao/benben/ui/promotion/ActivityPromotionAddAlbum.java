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
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.item.ImageFile;
import com.xunao.benben.utils.Bimp;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.CutImageUtils;
import com.xunao.benben.utils.FileUtils;
import com.xunao.benben.utils.ImageItem;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.PublicWay;
import com.xunao.benben.utils.Res;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.ActionSheet;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 商家活动相册添加
 * Created by ltf on 2015/11/30.
 */
public class ActivityPromotionAddAlbum extends BaseActivity implements View.OnClickListener {
    private RoundedImageView rv_album_cover;
    private EditText edt_name;
    private TextView tv_time,tv_max_num,tv_num;
    private GridView noScrollgridview;
    private GridAdapter adapter;
    private boolean isCover = false;

    private static final int TAKE_PICTURE = 0x000001;
    private static final int PIC_Select_CODE_ImageFromLoacal = 2; // 从相册
    public static Bitmap bimap;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private File poster_cover;
    private Bitmap bitMap;


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
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initTitle_Right_Left_bar("添加相册", "", "保存",R.drawable.icon_com_title_left, 0);
        rv_album_cover = (RoundedImageView) findViewById(R.id.rv_album_cover);
        rv_album_cover.setOnClickListener(this);
        edt_name = (EditText) findViewById(R.id.edt_name);
        tv_num = (TextView) findViewById(R.id.tv_num);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_max_num = (TextView) findViewById(R.id.tv_max_num);
        tv_max_num.setText("(每次可上传6张,仅限20张图片)");
        tv_time.setText(simpleDateFormat.format(new Date()));
        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        adapter.update();
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == Bimp.tempSelectBitmap.size()) {
                    if (Bimp.tempSelectBitmap.size() < 6) {
                        isCover = false;
                        changeImage();
                    }
                }
            }
        });
    }

    @Override
    public void initDate(Bundle savedInstanceState) {

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
                saveAlbum();
            }
        });
    }

    private void saveAlbum() {
        String name = String.valueOf(edt_name.getText()).trim();
        if(poster_cover==null){
            ToastUtils.Infotoast(mContext,"请设置相册封面！");
        }else if (TextUtils.isEmpty(name)) {
            ToastUtils.Errortoast(mContext, "相册名称不可为空!");
            return;
        }else if (!CommonUtils.StringIsSurpass2(name, 1, 8)) {
            ToastUtils.Errortoast(mContext, "相册名称限制在8个字之内!");
            return;
        }else{
            if(CommonUtils.isNetworkAvailable(mContext)){
                int size = Bimp.tempSelectBitmap.size();
                String[] images = new String[size];
                for (int i = 0; i < size; i++) {
                    images[i] = Bimp.tempSelectBitmap.get(i).getImagePath();
                }
                setOnRightClickLinester(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                InteNetUtils.getInstance(mContext).Addalbum(name, poster_cover, images,mRequestCallBack);
            }else{
                ToastUtils.Infotoast(mContext, "网络不可用");
            }
        }

    }


    @Override
    protected void onHttpStart() {

    }

    @Override
    protected void onLoading(long count, long current, boolean isUploading) {

    }

    @Override
    protected void onSuccess(JSONObject jsonObject) {
        ToastUtils.Infotoast(mContext,"添加成功");
        setResult(RESULT_OK,null);
        AnimFinsh();
    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        ToastUtils.Infotoast(mContext,"添加失败");
        setOnRightClickLinester(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAlbum();
            }
        });
    }

    protected void onRestart() {
        adapter.update();
        tv_num.setText("("+Bimp.tempSelectBitmap.size()+"张)");
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
                                            ActivityPromotionAddAlbum.this,
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
        }
    }

    @SuppressLint("HandlerLeak")
    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int selectedPosition = -1;
        private boolean shape;
        private int mWidth = (mScreenWidth - PixelUtil.dp2px(100)) / 4;

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
            if (Bimp.tempSelectBitmap.size() == 6) {
                return 6;
            }
            return (Bimp.tempSelectBitmap.size() + 1);
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
                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                View v = convertView.findViewById(R.id.box);
                holder.delete = (ImageView) convertView
                        .findViewById(R.id.delete);
                convertView.setTag(holder);

                v.setLayoutParams(new RelativeLayout.LayoutParams(mWidth
                        + PixelUtil.dp2px(10), mWidth + PixelUtil.dp2px(10)));
                holder.image.getLayoutParams().width = mWidth;
                holder.image.getLayoutParams().height = mWidth;

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == Bimp.tempSelectBitmap.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.icon_addpic_unfocused));
                holder.delete.setVisibility(View.GONE);
                if (position == 6) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position)
                        .getBitmap());
                holder.delete.setVisibility(View.VISIBLE);
                holder.delete.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Bimp.tempSelectBitmap.remove(position);
                        tv_num.setText("("+Bimp.tempSelectBitmap.size()+"张)");
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
            public ImageView delete;
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
                        poster_cover = new File(saveBitmap);
                        rv_album_cover.setImageBitmap(getBitmap(saveBitmap));
                    } else if (Bimp.tempSelectBitmap.size() < 9) {
                        ImageItem takePhoto = new ImageItem();
                        takePhoto.setBitmap(new SoftReference<Bitmap>(bm));
                        takePhoto.setImagePath(saveBitmap);

                        Bimp.tempSelectBitmap.add(takePhoto);
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
                                String imagePath = CutImageUtils.getImagePath(mContext,
                                        openInputStream);
                                poster_cover = new File(imagePath);
                                rv_album_cover.setImageBitmap(getBitmap(imagePath));
                            } else {
                                ToastUtils.Infotoast(mContext, "请选择正确的图像资源");
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        final MsgDialog msgDialog = new MsgDialog(ActivityPromotionAddAlbum.this, R.style.MyDialogStyle);
        msgDialog.setContent("确定放弃新增相册吗？", "", "确定", "取消");
        msgDialog.setCancleListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgDialog.dismiss();
            }
        });
        msgDialog.setOKListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgDialog.dismiss();
                AnimFinsh();
            }

        });
        msgDialog.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Bimp.tempSelectBitmap.clear();
        CrashApplication.getInstance().removeActivity(this);
        if (bitMap != null) {
            bitMap.recycle();
        }
    }

    private Bitmap getBitmap(String path) {
        // 获取屏幕宽高
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeigh = dm.heightPixels;
        return bitMap = CutImageUtils.convertToBitmap(path, screenWidth,
                screenHeigh);
    }
}
