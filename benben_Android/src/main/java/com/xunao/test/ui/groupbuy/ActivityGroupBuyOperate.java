package com.xunao.test.ui.groupbuy;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.test.R;
import com.xunao.test.base.BaseActivity;
import com.xunao.test.base.IA.CrashApplication;
import com.xunao.test.bean.PayMethod;
import com.xunao.test.bean.Promotion;
import com.xunao.test.config.AndroidConfig;
import com.xunao.test.dialog.BirthDialog;
import com.xunao.test.dialog.MsgDialog;
import com.xunao.test.exception.NetRequestException;
import com.xunao.test.net.InteNetUtils;
import com.xunao.test.ui.item.ActivitySmallPublic;
import com.xunao.test.ui.item.ImageFile;
import com.xunao.test.utils.Bimp;
import com.xunao.test.utils.CommonUtils;
import com.xunao.test.utils.CutImageUtils;
import com.xunao.test.utils.FileUtils;
import com.xunao.test.utils.ImageItem;
import com.xunao.test.utils.PixelUtil;
import com.xunao.test.utils.PublicWay;
import com.xunao.test.utils.Res;
import com.xunao.test.utils.ToastUtils;
import com.xunao.test.view.ActionSheet;

import net.tsz.afinal.FinalBitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ltf on 2015/12/7.
 */
public class ActivityGroupBuyOperate extends BaseActivity implements View.OnClickListener {
    private GridView noScrollgridview;
    private GridAdapter adapter;
    private TextView tv_start_date,tv_end_date;
    private Button btn_confirm;
    private EditText edt_group_buy_name,edt_origin_price,edt_group_buy_price,edt_model,edt_description,edt_rule;
    private TextView tv_group_buy_name;
    private ListView lv_pay_method;
    private MyAdapter payMethodAdapter;
    private List<PayMethod> payMethods = new ArrayList<>();

    private int type=0;
    private int promotionid=0;
    private static final int TAKE_PICTURE = 0x000001;
    private static final int PIC_Select_CODE_ImageFromLoacal = 2; // 从相册
    public static Bitmap bimap;
    private String lastTime;
    private String startTime;
    private String endTime;
    private Promotion promotion;
    private List<Map<String,String>> picList = new ArrayList<>();
    private File[] files = new File[6];
    private long valid_left,valid_right;
    private FinalBitmap fb;
    private boolean isCover = false;
    private String imagePath="";
    private Bitmap bitMap;
    private String ids="";
    private int on=0;
    private int off=0;
    private int offline_restrict=0;
    private int online_restrict=0;
    private String shipping_fee="";
    private String pay_ids="";

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_group_buy_operate);
        Res.init(this);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;
        CrashApplication.getInstance().addActivity(this);
        bimap = BitmapFactory.decodeResource(getResources(),
                R.drawable.icon_addpic_unfocused);
        PublicWay.activityList.add(this);
        fb =FinalBitmap.create(mContext);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        type = getIntent().getIntExtra("type",0);
        if(type==0) {
            initTitle_Right_Left_bar("发布团购", "", "", R.drawable.icon_com_title_left, 0);
        }else{
            initTitle_Right_Left_bar("编辑团购", "", "", R.drawable.icon_com_title_left, 0);
        }
        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        adapter.update();
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                int size = Bimp.tempSelectBitmap.size()+picList.size();
                if (arg2 == size) {
                    if (size < 6) {
                        PublicWay.num = 6-picList.size();
                        isCover = false;
                        changeImage();
                    }
                } else {
                    if(arg2==0){
                        changeImage();
                        isCover = true;
                    }

//                    Intent intent = new Intent(ActivityPromotionOperate.this,
//                            GalleryActivity.class);
//                    intent.putExtra("position", "1");
//                    intent.putExtra("ID", arg2);
//                    startActivity(intent);
                }
            }
        });
        edt_group_buy_name = (EditText) findViewById(R.id.edt_group_buy_name);
        tv_group_buy_name = (TextView) findViewById(R.id.tv_group_buy_name);
        edt_origin_price = (EditText) findViewById(R.id.edt_origin_price);
        edt_group_buy_price = (EditText) findViewById(R.id.edt_group_buy_price);
        edt_model = (EditText) findViewById(R.id.edt_model);
        edt_description = (EditText) findViewById(R.id.edt_description);
        edt_rule = (EditText) findViewById(R.id.edt_rule);
        tv_start_date = (TextView) findViewById(R.id.tv_start_date);
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        startTime = myFormatter.format(date);
        valid_left = date.getTime();
        tv_start_date.setText(startTime);
//        tv_start_date.setOnClickListener(this);
        tv_end_date = (TextView) findViewById(R.id.tv_end_date);
        tv_end_date.setOnClickListener(this);

        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        if(type==0) {
            btn_confirm.setText("确认添加");
            btn_confirm.setVisibility(View.VISIBLE);
        }else{
            btn_confirm.setText("确认修改");
        }
        lv_pay_method = (ListView) findViewById(R.id.lv_pay_method);
        payMethodAdapter = new MyAdapter();
        lv_pay_method.setAdapter(payMethodAdapter);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        if(type==0){
            if(CommonUtils.isNetworkAvailable(mContext)){
                showLoding("");
                InteNetUtils.getInstance(mContext).Paymethods(user.getToken(), new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        dissLoding();
                        try {
                            JSONObject jsonObject = new JSONObject(arg0.result);
                            if(jsonObject.optInt("ret_num")==0) {
                                JSONArray jsonArray = jsonObject.getJSONArray("pay_methods");
                                if(jsonArray!=null && jsonArray.length()>0) {
                                    for(int i=0;i<jsonArray.length();i++) {
                                        PayMethod payMethod = new PayMethod();
                                        payMethod.parseJSON(jsonArray.getJSONObject(i));
                                        payMethods.add(payMethod);
                                    }
                                    payMethodAdapter.notifyDataSetChanged();
                                }
                            }else{
                                ToastUtils.Infotoast(mContext,jsonObject.optString("ret_msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NetRequestException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        ToastUtils.Infotoast(mContext,"获取支付方式失败");
                        dissLoding();
                    }
                });
            }else{
                ToastUtils.Infotoast(mContext, "网络不可用");
            }
        }else{


            tv_group_buy_name.setVisibility(View.VISIBLE);
            edt_group_buy_name.setVisibility(View.GONE);
            promotionid = getIntent().getIntExtra("promotionid", 0);
            on = getIntent().getIntExtra("on", 0);
            off = getIntent().getIntExtra("off", 0);
            offline_restrict = getIntent().getIntExtra("offline_restrict", 0);
            online_restrict = getIntent().getIntExtra("online_restrict", 0);

            if(CommonUtils.isNetworkAvailable(mContext)){
                InteNetUtils.getInstance(mContext).Getgroupbuy(promotionid, user.getToken(), mRequestCallBack);
            }else{
                ToastUtils.Infotoast(mContext, "网络不可用");
            }
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

    public void showMoreActionSheet() {
        String typeShow="";
        if(type==1){
            typeShow = "下架";
        }else if(type==2){
            typeShow = "上架";
        }

        ActionSheet
                .createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle("取消")
                .setOtherButtonTitles("删除", typeShow,"小喇叭")
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
                                        if(CommonUtils.isNetworkAvailable(mContext)){
                                            if(CommonUtils.isNetworkAvailable(mContext)){
                                                showLoding("");
                                                InteNetUtils.getInstance(mContext).Delgroupbuy(promotionid, user.getToken(), new RequestCallBack<String>() {
                                                    @Override
                                                    public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                                                        dissLoding();
                                                        ToastUtils.Infotoast(mContext, "删除成功");
                                                        setResult(RESULT_OK, null);
                                                        AnimFinsh();
                                                    }

                                                    @Override
                                                    public void onFailure(HttpException e, String s) {
                                                        ToastUtils.Infotoast(mContext, "删除失败!");
                                                    }
                                                });
                                            }else{
                                                ToastUtils.Infotoast(mContext, "网络不可用");
                                            }
                                        }else{
                                            ToastUtils.Infotoast(mContext, "网络不可用");
                                        }
                                    }
                                });
                                msgDialog.show();
                                break;
                            case 1:
                                if(type==1){
                                    if(off>=offline_restrict){
                                        ToastUtils.Infotoast(mContext,"下架数量已达上限!");
                                    }else{
                                        if(CommonUtils.isNetworkAvailable(mContext)){
                                            showLoding("");
                                            InteNetUtils.getInstance(mContext).Togglegroupbuy(promotionid, "1", user.getToken(), new RequestCallBack<String>() {
                                                @Override
                                                public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                                                    dissLoding();
                                                    ToastUtils.Infotoast(mContext, "下架成功");
                                                    setResult(RESULT_OK, null);
                                                    AnimFinsh();
                                                }

                                                @Override
                                                public void onFailure(HttpException e, String s) {
                                                    ToastUtils.Infotoast(mContext, "下架失败!");
                                                }
                                            });
                                        }else{
                                            ToastUtils.Infotoast(mContext, "网络不可用");
                                        }

                                    }

                                }else if(type==2){
                                    if(on>=online_restrict){
                                        ToastUtils.Infotoast(mContext,"上架数量已达上限!");
                                    }else{
                                        if(CommonUtils.isNetworkAvailable(mContext)){
                                            if(CommonUtils.isNetworkAvailable(mContext)){
                                                showLoding("");
                                                InteNetUtils.getInstance(mContext).Togglegroupbuy(promotionid, "0", user.getToken(), new RequestCallBack<String>() {
                                                    @Override
                                                    public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                                                        dissLoding();
                                                        ToastUtils.Infotoast(mContext, "上架成功");
                                                        setResult(RESULT_OK, null);
                                                        AnimFinsh();
                                                    }

                                                    @Override
                                                    public void onFailure(HttpException e, String s) {
                                                        ToastUtils.Infotoast(mContext, "上架失败!");
                                                    }
                                                });
                                            }else{
                                                ToastUtils.Infotoast(mContext, "网络不可用");
                                            }
                                        }else{
                                            ToastUtils.Infotoast(mContext, "网络不可用");
                                        }
                                    }
                                }

                                break;
                            case 2:
                                startAnimActivity2Obj(
                                        ActivitySmallPublic.class,
                                        "promotion", "我开通了新的团购,"+ AndroidConfig.NETHOST+"/promotion/promotiondetail/key/android?promotionid="+promotionid+",来给我捧捧场吧!");

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
        Log.d("ltf","jsonObject=============="+jsonObject);
        dissLoding();
        try {

            JSONArray posterArray = jsonObject.getJSONArray("poster");
            if(posterArray!=null && posterArray.length()>0) {
                for(int i=0;i<posterArray.length();i++) {
                    Map<String,String> map = new HashMap<>();
                    map.put("id",posterArray.getJSONObject(i).optString("img_id"));
                    map.put("path",posterArray.getJSONObject(i).optString("small_img"));
                    picList.add(map);
                }
                adapter.notifyDataSetChanged();
            }


            promotion = new Promotion();
            promotion.parseJSON(jsonObject);
            shipping_fee = promotion.getShipping_fee();
            initPromotion();
            JSONArray jsonArray = jsonObject.getJSONArray("paymethods");
            if(jsonArray!=null && jsonArray.length()>0) {
                for(int i=0;i<jsonArray.length();i++) {
                    PayMethod payMethod = new PayMethod();
                    payMethod.parseJSON(jsonArray.getJSONObject(i));
                    payMethods.add(payMethod);
                }
                payMethodAdapter.notifyDataSetChanged();
            }


        } catch (NetRequestException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        ToastUtils.Infotoast(mContext,"获取信息失败");
    }

    private void initPromotion() {
        edt_group_buy_name.setText(promotion.getName());
        tv_group_buy_name.setText(promotion.getName());
        edt_origin_price.setText(promotion.getOrigion_price()+"");
        edt_group_buy_price.setText(promotion.getPromotion_price()+"");
        edt_description.setText(promotion.getDescription());
        edt_model.setText(promotion.getModel());
        edt_rule.setText(promotion.getMustknow());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        valid_left = Long.parseLong(promotion.getValid_left())*1000;
        Date start=new Date(valid_left);
        startTime = format.format(start);
        tv_start_date.setText(startTime);
        valid_right = Long.parseLong(promotion.getValid_right())*1000;
        Date end = new Date(valid_right);
        endTime = format.format(end);
        tv_end_date.setText(endTime);

//        if(promotion.getSmall_poster_st()!=null && !promotion.getSmall_poster_st().equals("")){
//            Map<String,String> map = new HashMap<>();
//            map.put("id","1");
//            map.put("path",promotion.getSmall_poster_st());
//            picList.add(map);
//        }
//        if(promotion.getSmall_poster_nd()!=null && !promotion.getSmall_poster_nd().equals("")){
//            Map<String,String> map = new HashMap<>();
//            map.put("id","2");
//            map.put("path",promotion.getSmall_poster_nd());
//            picList.add(map);
//        }
//        if(promotion.getSmall_poster_rd()!=null && !promotion.getSmall_poster_rd().equals("")){
//            Map<String,String> map = new HashMap<>();
//            map.put("id","3");
//            map.put("path",promotion.getSmall_poster_rd());
//            picList.add(map);
//        }
//        adapter.notifyDataSetChanged();

        initTitle_Right_Left_bar("编辑团购", "", "更多", R.drawable.icon_com_title_left, 0);
        setOnRightClickLinester(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setTheme(R.style.ActionSheetStyleIOS7);
                    showMoreActionSheet();
                }
            });
        btn_confirm.setVisibility(View.VISIBLE);

    }


    protected void onRestart() {
        adapter.update();
        super.onRestart();
    }

    private void getNewFile(){
        files[1] = null;
        files[2] = null;
        files[3] = null;
        files[4] = null;
        files[5] = null;
        if(type==0){
            if(files[0]==null){
                for(int i=0;i<Bimp.tempSelectBitmap.size();i++){
                    files[i] = new File(Bimp.tempSelectBitmap.get(i).getImagePath());
                }
            }else{
                for(int i=1;i<Bimp.tempSelectBitmap.size();i++){
                    files[i] = new File(Bimp.tempSelectBitmap.get(i).getImagePath());
                }
            }
        }else{
            for(int i=0;i<Bimp.tempSelectBitmap.size();i++){
                files[i+1] = new File(Bimp.tempSelectBitmap.get(i).getImagePath());
            }
        }
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
                                            ActivityGroupBuyOperate.this,
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
            case R.id.tv_start_date:
                lastTime = startTime;
                choseDate(1);
                break;
            case R.id.tv_end_date:
                lastTime = endTime;
                choseDate(2);
                break;
            case R.id.btn_confirm:
                if(type==0){
                    addPromotion();
                }else{
                    updatePromotion();
                }
                break;
        }
    }

    private void addPromotion() {
        String name = String.valueOf(edt_group_buy_name.getText()).trim();
        if (TextUtils.isEmpty(name)) {
            ToastUtils.Errortoast(mContext, "团购品名称不可为空!");
            return;
        }
        if (!CommonUtils.StringIsSurpass2(name, 1, 12)) {
            ToastUtils.Errortoast(mContext, "团购品名称限制在12个字之内!");
            return;
        }
        getNewFile();
        if(files[0]==null){
            ToastUtils.Errortoast(mContext, "请添加团购图片!");
            return;
        }
        String origin_price = String.valueOf(edt_origin_price.getText()).trim();
        String promotion_price = String.valueOf(edt_group_buy_price.getText()).trim();
        if (TextUtils.isEmpty(origin_price)) {
            ToastUtils.Errortoast(mContext, "请输入原价!");
            return;
        }
        if (TextUtils.isEmpty(promotion_price)) {
            ToastUtils.Errortoast(mContext, "请输入团购价!");
            return;
        }
        if(Double.parseDouble(promotion_price) > Double.parseDouble(origin_price)){
            ToastUtils.Errortoast(mContext, "团购价不能高于原价!");
            return;
        }
        if (valid_left==0) {
            ToastUtils.Errortoast(mContext, "请设置起始时间!");
            return;
        }
        if (valid_right==0) {
            ToastUtils.Errortoast(mContext, "请设置结束时间!");
            return;
        }
        if(valid_left > valid_right){
            ToastUtils.Errortoast(mContext, "起始时间不能大于结束时间!");
            return;
        }
        pay_ids="";
        for(int i=0;i<payMethods.size();i++){
            if(payMethods.get(i).isChecked()){
                pay_ids += payMethods.get(i).getPay_id()+",";
            }
        }
        if(pay_ids.equals("")){
            ToastUtils.Errortoast(mContext, "请选择支付方式!");
            return;
        }
        String myFee = "";
        if(pay_ids.contains("1")){
            myFee = shipping_fee;
            if(myFee.equals("")) {
                ToastUtils.Errortoast(mContext, "请输入邮费!");
                return;
            }
        }

        String model = String.valueOf(edt_model.getText()).trim();
        if (!CommonUtils.StringIsSurpass2(model, 0, 20)) {
            ToastUtils.Errortoast(mContext, "产品参数限制在20个字之内!");
            return;
        }
        String description = String.valueOf(edt_description.getText()).trim();
        if (TextUtils.isEmpty(description)) {
            ToastUtils.Errortoast(mContext, "团购品介绍不可为空!");
            return;
        }
        if (!CommonUtils.StringIsSurpass2(description, 1, 100)) {
            ToastUtils.Errortoast(mContext, "团购品介绍限制在1至100个字之内!");
            return;
        }
        String mustknow = String.valueOf(edt_rule.getText()).trim();
        if (TextUtils.isEmpty(mustknow)) {
            ToastUtils.Errortoast(mContext, "购买须知不可为空!");
            return;
        }
        if (!CommonUtils.StringIsSurpass2(mustknow, 1, 240)) {
            ToastUtils.Errortoast(mContext, "购买须知限制在1至240个字之内!");
            return;
        }


        if(CommonUtils.isNetworkAvailable(mContext)){
            showLoding("");
            String payIds = pay_ids.substring(0,pay_ids.length()-1);
            InteNetUtils.getInstance(mContext).Addgroupbuy(name, origin_price, promotion_price, valid_left / 1000, valid_right / 1000, description
                    , files, payIds, myFee,mustknow,model ,user.getToken(), addCallBack);
        }else{
            ToastUtils.Infotoast(mContext, "网络不可用");
        }
    }

    public RequestCallBack<String> addCallBack = new RequestCallBack<String>() {

        @Override
        public void onSuccess(ResponseInfo<String> stringResponseInfo) {
            dissLoding();
            ToastUtils.Infotoast(mContext,"添加成功");
            setResult(RESULT_OK,null);
            AnimFinsh();
        }

        @Override
        public void onFailure(HttpException e, String s) {
            dissLoding();
            ToastUtils.Infotoast(mContext,"添加失败");
        }
    };

    private void updatePromotion() {
//        String name = String.valueOf(edt_group_buy_name.getText()).trim();
//        if (TextUtils.isEmpty(name)) {
//            ToastUtils.Errortoast(mContext, "团购品名称不可为空!");
//            return;
//        }
//        if (!CommonUtils.StringIsSurpass2(name, 1, 12)) {
//            ToastUtils.Errortoast(mContext, "团购品名称限制在12个字之内!");
//            return;
//        }

        String origin_price = String.valueOf(edt_origin_price.getText()).trim();
        String promotion_price = String.valueOf(edt_group_buy_price.getText()).trim();
        if (TextUtils.isEmpty(origin_price)) {
            ToastUtils.Errortoast(mContext, "请输入原价!");
            return;
        }
        if (TextUtils.isEmpty(promotion_price)) {
            ToastUtils.Errortoast(mContext, "请输入团购价!");
            return;
        }
        if(Double.parseDouble(promotion_price) > Double.parseDouble(origin_price)){
            ToastUtils.Errortoast(mContext, "团购价不能高于原价!");
            return;
        }
        if (valid_left==0) {
            ToastUtils.Errortoast(mContext, "请设置起始时间!");
            return;
        }
        if (valid_right==0) {
            ToastUtils.Errortoast(mContext, "请设置结束时间!");
            return;
        }
        if(valid_left > valid_right){
            ToastUtils.Errortoast(mContext, "起始时间不能大于结束时间!");
            return;
        }

        pay_ids="";
        for(int i=0;i<payMethods.size();i++){
            if(payMethods.get(i).isChecked()){
                pay_ids += payMethods.get(i).getPay_id()+",";
            }
        }
        if(pay_ids.equals("")){
            ToastUtils.Errortoast(mContext, "请选择支付方式!");
            return;
        }
        String myFee = "";
        if(pay_ids.contains("1")){
            myFee = shipping_fee;
            if(myFee.equals("")) {
                ToastUtils.Errortoast(mContext, "请输入邮费!");
                return;
            }
        }

        String model = String.valueOf(edt_model.getText()).trim();
        if (!CommonUtils.StringIsSurpass2(model, 0, 20)) {
            ToastUtils.Errortoast(mContext, "产品参数限制在20个字之内!");
            return;
        }
        String description = String.valueOf(edt_description.getText()).trim();
        if (TextUtils.isEmpty(description)) {
            ToastUtils.Errortoast(mContext, "团购品介绍不可为空!");
            return;
        }
        if (!CommonUtils.StringIsSurpass2(description, 1, 100)) {
            ToastUtils.Errortoast(mContext, "团购品介绍限制在1至100个字之内!");
            return;
        }
        String mustknow = String.valueOf(edt_rule.getText()).trim();
        if (TextUtils.isEmpty(mustknow)) {
            ToastUtils.Errortoast(mContext, "购买须知不可为空!");
            return;
        }
        if (!CommonUtils.StringIsSurpass2(mustknow, 1, 240)) {
            ToastUtils.Errortoast(mContext, "购买须知限制在1至240个字之内!");
            return;
        }

        if(CommonUtils.isNetworkAvailable(mContext)){
            showLoding("");
            getNewFile();
            String delIds="";
            if(ids.length()>0){
                delIds = ids.substring(0,ids.length()-1);
            }
            String payIds = pay_ids.substring(0,pay_ids.length()-1);
            InteNetUtils.getInstance(mContext).Editgroupbuy(promotionid, origin_price, promotion_price, valid_left / 1000, valid_right / 1000, description,
                    delIds , files, payIds, myFee,mustknow,model ,user.getToken(), updateCallBack);
        }else{
            ToastUtils.Infotoast(mContext, "网络不可用");
        }
    }

    public RequestCallBack<String> updateCallBack = new RequestCallBack<String>() {

        @Override
        public void onSuccess(ResponseInfo<String> stringResponseInfo) {
            dissLoding();
            ToastUtils.Infotoast(mContext,"修改成功");
            setResult(RESULT_OK,null);
            AnimFinsh();
        }

        @Override
        public void onFailure(HttpException e, String s) {
            dissLoding();
            ToastUtils.Infotoast(mContext,"修改失败");
        }
    };

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
            int size = Bimp.tempSelectBitmap.size()+picList.size();

            if (size == 6) {
                return 6;
            }
            return (size + 1);
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
                holder.tv_edittxt = (TextView) convertView
                        .findViewById(R.id.tv_edittxt);
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

            if (position == (Bimp.tempSelectBitmap.size()+picList.size())) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.icon_addpic_unfocused));

//				decodeFile = PhotoUtils.rotaingImageView(
//						PhotoUtils.readPictureDegree(image.getAbsolutePath()),
//						image.getAbsolutePath());

                holder.tv_edittxt.setVisibility(View.GONE);
                holder.delete.setVisibility(View.GONE);
                if (position == 6) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                if(position==0 && imagePath!=null && !imagePath.equals("")){
                    holder.image.setImageBitmap(getBitmap(imagePath));
                }else{
                    if(position<picList.size()){
                        Map<String,String> map = picList.get(position);
                        fb.display(holder.image,map.get("path"));
                    }else{
                        holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position-picList.size())
                                .getBitmap());
                    }
                }

                if(position==0){
                    holder.tv_edittxt.setVisibility(View.VISIBLE);
                    holder.delete.setVisibility(View.GONE);
                }else{
                    holder.tv_edittxt.setVisibility(View.GONE);
                    holder.delete.setVisibility(View.VISIBLE);
                    holder.delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(position<picList.size()){
                                ids += picList.get(position).get("id")+",";
                                picList.remove(position);
                                adapter.notifyDataSetChanged();
                            }else {
                                Bimp.tempSelectBitmap.remove(position-picList.size());
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                }



            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
            public ImageView delete;
            public TextView tv_edittxt;
        }

        public void loading() {
            adapter.notifyDataSetChanged();
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


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if(resultCode == RESULT_OK) {
                    String fileName = String.valueOf(System.currentTimeMillis());
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    String saveBitmap = FileUtils.saveBitmapPhoto(bm, fileName);
                    if (isCover) {
                        imagePath = saveBitmap;
                        files[0] = new File(imagePath);
                        adapter.notifyDataSetChanged();
                        if(type!=0){
                            ids = picList.get(0).get("id")+","+ids;
                        }

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
                                imagePath = CutImageUtils.getImagePath(mContext,
                                        openInputStream);
                                files[0] = new File(imagePath);
                                adapter.notifyDataSetChanged();
                                if(type!=0){
                                    ids = picList.get(0).get("id")+","+ids;
                                }
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

    private void choseDate(final int type) {
        final BirthDialog dialog = new BirthDialog(mContext, lastTime);
        Window window = dialog.getWindow();
        window.setLayout(mScreenWidth - PixelUtil.dp2px(10),
                ActionBar.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.BOTTOM);

        dialog.setOKListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                switch (arg0.getId()) {
                    case R.id.chose_position:
                        try {
                            SimpleDateFormat myFormatter = new SimpleDateFormat(
                                    "yyyy-MM-dd");
                            lastTime = dialog.getWheelMain();
                            Date date = myFormatter.parse(dialog.getWheelMain());
                            if(type==1) {
                                startTime = lastTime;
                                valid_left = date.getTime();
                                tv_start_date.setText(myFormatter.format(date));
                            }else if(type==2) {
                                endTime = lastTime;
                                valid_right = date.getTime();
                                tv_end_date.setText(myFormatter.format(date));
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });

        dialog.setCancleListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                switch (arg0.getId()) {
                    case R.id.chose_cancel:
                        dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });

        dialog.show();

    }

    @Override
    public void onBackPressed() {
        final MsgDialog msgDialog = new MsgDialog(ActivityGroupBuyOperate.this, R.style.MyDialogStyle);
        if(type==0){
            msgDialog.setContent("确定放弃新增团购吗？", "", "确定", "取消");
        }else {
            msgDialog.setContent("需要保存编辑信息吗？", "", "保存", "不保存");
        }
        msgDialog.setCancleListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type==0){
                    msgDialog.dismiss();
                }else {
                    msgDialog.dismiss();
                    AnimFinsh();
                }
            }
        });
        msgDialog.setOKListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type==0){
                    msgDialog.dismiss();
                    AnimFinsh();
                }else {
                    msgDialog.dismiss();
                    updatePromotion();
                }

            }

        });
        msgDialog.show();
    }

    @Override
    protected void onDestroy() {
        if (bitMap != null) {
            bitMap.recycle();
        }
        PublicWay.num = 6;
        Bimp.tempSelectBitmap.clear();
        CrashApplication.getInstance().removeActivity(this);
        super.onDestroy();
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
            if(payMethod.isChecked()){
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
                    if(payMethod.isChecked()){
                        payMethod.setChecked(false);
                    }else{
                        payMethod.setChecked(true);
                    }
                    payMethodAdapter.notifyDataSetChanged();
                }
            });
            return convertView;
        }

    }

    class itemHolder {
        TextView tv_name;
        EditText edt_mail_price;
        CheckBox cb_item;
    }

}
