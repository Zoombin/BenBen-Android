package com.xunao.benben.ui.mybuy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.PayMethod;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.bean.User;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.item.GalleryActivity;
import com.xunao.benben.ui.item.ImageFile;
import com.xunao.benben.utils.Bimp;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.FileUtils;
import com.xunao.benben.utils.ImageItem;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.PublicWay;
import com.xunao.benben.utils.Res;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.MyTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ltf on 2016/1/22.
 */
public class ActivityBuyGivePrice extends BaseActivity implements View.OnClickListener {
    private int buyId=0;

    private MyTextView name;
    private EditText price;
    private EditText info;
    private Button submit;

    private GridView noScrollgridview;
    private GridAdapter adapter;
    public static Bitmap bimap;
    private static final int TAKE_PICTURE = 0x000001;

    private ListView lv_pay_method;
    private MyAdapter payMethodAdapter;
    private List<PayMethod> payMethods = new ArrayList<>();
    private int payType=0;
    private String payName="";
    private String shipping_fee="";

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_buy_give_price);

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
        initTitle_Right_Left_bar("我的报价", "", "",R.drawable.icon_com_title_left, 0);
        name = (MyTextView) findViewById(R.id.name);
        price = (EditText) findViewById(R.id.price);
        info = (EditText) findViewById(R.id.info);
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(this);

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
                        changeImage();
                    }
                } else {
                    Intent intent = new Intent(ActivityBuyGivePrice.this,
                            GalleryActivity.class);
                    intent.putExtra("position", "1");
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }

            }

        });

        lv_pay_method = (ListView) findViewById(R.id.lv_pay_method);
        payMethodAdapter = new MyAdapter();
        lv_pay_method.setAdapter(payMethodAdapter);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        buyId = getIntent().getIntExtra("buyId",0);
        try {
            User findFirst = CrashApplication.getInstance().getDb()
                    .findFirst(User.class);
            name.setText(findFirst.getZhiShortName());
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        payMethods = (ArrayList<PayMethod>) getIntent().getSerializableExtra("payMethods");
        adapter.notifyDataSetChanged();
    }

    @Override
    public void initLinstener(Bundle savedInstanceState) {
        setOnLeftClickLinester(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimFinsh();
            }
        });

        price.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);

                        price.setText(s);
                        price.setSelection(s.length());
                    }

                    if(s.toString().indexOf(".") > 8){
                        //	ToastUtils.Errortoast(context, s.toString().indexOf(".") +"");
                        s = s.toString().subSequence(1,
                                s.toString().length());
                        price.setText(s);
                        //price.setSelection(s.length());
                    }
                }else{
                    if(s.length() > 8){
                        s = s.toString().subSequence(0,
                                8);
                        price.setText(s);
                        price.setSelection(s.length());
                    }
//                    charSequence = s;

                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });

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
                                Intent intent = new Intent(
                                        ActivityBuyGivePrice.this,
                                        ImageFile.class);
                                startActivity(intent);
                                overridePendingTransition(
                                        R.anim.activity_translate_in,
                                        R.anim.activity_translate_out);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.submit:
                final String sName = name.getText().toString();
                final String sPrice = price.getText().toString();
                final String sInfo = info.getText().toString();

                if (TextUtils.isEmpty(sPrice)) {
                    ToastUtils.Errortoast(mContext, "报价不可为空");
                    return;
                }
                if (!CommonUtils.StringIsSurpass2(sInfo, 0, 200)) {
                    ToastUtils.Errortoast(mContext, "说明限制在200个字之间!");
                    return;
                }
                if(payType == 0){
                    ToastUtils.Errortoast(mContext, "请选择支付方式!");
                    return;
                }

                final MsgDialog msgDialog = new MsgDialog(mContext, R.style.MyDialogStyle);
                msgDialog.setContent("确定报价吗？", "", "确定", "取消");
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
                        if (CommonUtils.isNetworkAvailable(mContext)) {
                            int size = Bimp.tempSelectBitmap.size();
                            String[] images = new String[size];
                            for (int i = 0; i < size; i++) {
                                images[i] = Bimp.tempSelectBitmap.get(i).getImagePath();
                            }

                            InteNetUtils.getInstance(mContext).submitBuyPrice(
                                    buyId, sPrice, sInfo,images,payType+"",shipping_fee,
                                    new RequestCallBack<String>() {
                                        @Override
                                        public void onFailure(HttpException arg0,
                                                              String arg1) {
                                            ToastUtils.Errortoast(mContext,
                                                    "网络提交失败");
                                        }

                                        @Override
                                        public void onSuccess(ResponseInfo<String> arg0) {
                                            JSONObject jsonObject;
                                            try {
                                                jsonObject = new JSONObject(arg0.result);
                                                Log.d("ltf","jsonObject=============="+jsonObject);
                                                SuccessMsg msg = new SuccessMsg();
                                                msg.parseJSON(jsonObject);
                                                ToastUtils.Infotoast(mContext,
                                                        "报价成功");
                                                setResult(RESULT_OK,null);
                                                AnimFinsh();
                                            } catch (NetRequestException e) {
                                                e.getError().print(mContext);
                                                e.printStackTrace();
                                            } catch (JSONException e) {
                                                ToastUtils.Errortoast(mContext,
                                                        "网络提交失败");
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                        }

                    }

                });
                msgDialog.show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {
                    String fileName = String.valueOf(System.currentTimeMillis());
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    String saveBitmap = FileUtils.saveBitmapPhoto(bm, fileName);
                    ImageItem takePhoto = new ImageItem();
                    takePhoto.setBitmap(new SoftReference<Bitmap>(bm));
                    takePhoto.setImagePath(saveBitmap);

                    Bimp.tempSelectBitmap.add(takePhoto);
                }
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
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

    protected void onRestart() {
        adapter.update();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Bimp.tempSelectBitmap.clear();
        CrashApplication.getInstance().removeActivity(this);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return payMethods.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return payMethods.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            final PayMethod payMethod = payMethods.get(position);
            final itemHolder itHolder;
            if (convertView == null) {
                itHolder = new itemHolder();
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_pay_method, null);
                itHolder.tv_name = (TextView) convertView
                        .findViewById(R.id.tv_name);
                itHolder.edt_mail_price = (EditText) convertView
                        .findViewById(R.id.edt_mail_price);

                itHolder.cb_item = (CheckBox) convertView
                        .findViewById(R.id.cb_item);
                convertView.setTag(itHolder);
            } else {
                itHolder = (itemHolder) convertView.getTag();
            }
            itHolder.tv_name.setText(payMethod.getPay_name());
            if(Integer.parseInt(payMethod.getPay_id())==payType){
                itHolder.cb_item.setChecked(true);
            }else{
                itHolder.cb_item.setChecked(false);
            }

            if(payMethod.getPay_id().equals("1")){
                itHolder.edt_mail_price.setVisibility(View.VISIBLE);
                itHolder.edt_mail_price.setText(shipping_fee);
            }else{
                itHolder.edt_mail_price.setVisibility(View.GONE);
            }

            itHolder.edt_mail_price.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    shipping_fee = editable.toString();
                }
            });
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeType(position);
                }
            });
            return convertView;
        }

        class itemHolder {
            TextView tv_name;
            EditText edt_mail_price;
            CheckBox cb_item;
        }

    }

    private void changeType(int position) {
        PayMethod payMethod = payMethods.get(position);
        if(payMethod.isChecked()){
            payType=0;
            payName = "";
        }else{
            payType = Integer.parseInt(payMethod.getPay_id());
            payName = payMethod.getPay_name();
        }
        payMethodAdapter.notifyDataSetChanged();
    }
}
