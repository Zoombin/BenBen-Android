package com.xunao.benben.ui.item;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.bean.AuthMessage;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityMyWeb;
import com.xunao.benben.ui.ActivityWeb;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.CutImageUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.ActionSheet;

import net.tsz.afinal.FinalBitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import in.srain.cube.image.CubeImageView;

/**
 * Created by ltf on 2015/11/28.
 */
public class ActivityNumberTrainPromotionIdentity extends BaseActivity implements View.OnClickListener, ActionSheet.ActionSheetListener {
    private RelativeLayout prerecord_tab_one;
    private RelativeLayout prerecord_tab_three;
    private LinearLayout ll_person,ll_merchant;
    private TextView tv_agreement;
    private CubeImageView iv_person_identityCode1,iv_person_identityCode2;
    private CubeImageView iv_merchant_identityCode1,iv_merchant_identityCode2,iv_merchant_license;
    private EditText edt_person_name,edt_person_identityCode;
    private EditText edt_merchant_name,edt_merchant_identityCode,edt_company;
    private CheckBox cb_agree;
    private Button btn_auth;
    private RelativeLayout rl_promotion_fail;
    private TextView tv_message;
    private int currentTable=1;

    private String imageName;
    private String imagePath;
    private Intent intent;
    private static final int PIC_REQUEST_CODE_SELECT_CAMERA = 1; // 从相机
    private static final int PIC_Select_CODE_ImageFromLoacal = 2; // 从相册
    private int imgType = 1;
    private File file1;
    private File file2;
    private File file3;
    private File file4;
    private File file5;
    private Bitmap bitMap;
    private AuthMessage authMessage;
    private FinalBitmap finalBitmap;
    private String from;

    @Override
    public void loadLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_number_train_promotion_identity);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        finalBitmap = FinalBitmap.create(mContext);
        initTitle_Right_Left_bar("商家认证", "", "",
                R.drawable.icon_com_title_left, 0);
        prerecord_tab_one = (RelativeLayout) findViewById(R.id.prerecord_tab_one);
        setChecked(prerecord_tab_one, true);
        prerecord_tab_three = (RelativeLayout) findViewById(R.id.prerecord_tab_three);
        prerecord_tab_one.setOnClickListener(this);
        prerecord_tab_three.setOnClickListener(this);
        ll_person =  (LinearLayout) findViewById(R.id.ll_person);
        ll_merchant =  (LinearLayout) findViewById(R.id.ll_merchant);
        tv_agreement = (TextView) findViewById(R.id.tv_agreement);
        tv_agreement.setText(Html.fromHtml("<u>号码直通车服务协议</u>"));
        tv_agreement.setOnClickListener(this);

        iv_person_identityCode1 = (CubeImageView) findViewById(R.id.iv_person_identityCode1);
        iv_person_identityCode2 = (CubeImageView) findViewById(R.id.iv_person_identityCode2);
        iv_merchant_identityCode1 = (CubeImageView) findViewById(R.id.iv_merchant_identityCode1);
        iv_merchant_identityCode2 = (CubeImageView) findViewById(R.id.iv_merchant_identityCode2);
        iv_merchant_license = (CubeImageView) findViewById(R.id.iv_merchant_license);
        iv_person_identityCode1.setOnClickListener(this);
        iv_person_identityCode2.setOnClickListener(this);
        iv_merchant_identityCode1.setOnClickListener(this);
        iv_merchant_identityCode2.setOnClickListener(this);
        iv_merchant_license.setOnClickListener(this);

        edt_person_name = (EditText) findViewById(R.id.edt_person_name);
        edt_company = (EditText) findViewById(R.id.edt_company);
        edt_person_identityCode = (EditText) findViewById(R.id.edt_person_identityCode);
        edt_merchant_name = (EditText) findViewById(R.id.edt_merchant_name);
        edt_merchant_identityCode = (EditText) findViewById(R.id.edt_merchant_identityCode);
        cb_agree = (CheckBox) findViewById(R.id.cb_agree);
        btn_auth = (Button) findViewById(R.id.btn_auth);
        btn_auth.setOnClickListener(this);
        rl_promotion_fail = (RelativeLayout) findViewById(R.id.rl_promotion_fail);
        tv_message = (TextView) findViewById(R.id.tv_message);
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        from = getIntent().getStringExtra("from");
        if (CommonUtils.isNetworkAvailable(mContext)) {
            showLoding("");
            InteNetUtils.getInstance(mContext).Getauth(user.getToken(), new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> arg0) {
                    dissLoding();
                    String result = arg0.result;
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if(jsonObject.optInt("ret_num")==0) {
                            JSONObject job = jsonObject.getJSONObject("info");
                            authMessage = new AuthMessage();
                            authMessage.parseJSON(job);
                            initMessage();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NetRequestException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    dissLoding();
                }
            });
        }else{
            ToastUtils.Infotoast(mContext, "网络不可用");
        }
    }

    private void initMessage() {
        if(authMessage.getType()==1){
            ll_person.setVisibility(View.VISIBLE);
            ll_merchant.setVisibility(View.GONE);
            setChecked(prerecord_tab_three, false);
            setChecked(prerecord_tab_one, true);
            currentTable=1;
            edt_person_name.setText(authMessage.getReal_name());
            edt_person_identityCode.setText(authMessage.getIdcard());
            finalBitmap.configLoadfailImage(R.drawable.img_legal_add);
            finalBitmap.display(iv_person_identityCode1,authMessage.getFront());
            finalBitmap.display(iv_person_identityCode2,authMessage.getBack());
//            CommonUtils.startImageLoader(cubeimageLoader, authMessage.getFront(), iv_person_identityCode1);
//            CommonUtils.startImageLoader(cubeimageLoader, authMessage.getBack(), iv_person_identityCode2);
        }else{
            ll_person.setVisibility(View.GONE);
            ll_merchant.setVisibility(View.VISIBLE);
            setChecked(prerecord_tab_three, true);
            setChecked(prerecord_tab_one, false);
            currentTable=2;
            edt_merchant_name.setText(authMessage.getReal_name());
            edt_company.setText(authMessage.getCompany());
            edt_merchant_identityCode.setText(authMessage.getIdcard());
            finalBitmap.configLoadfailImage(R.drawable.error);
            finalBitmap.display(iv_merchant_identityCode1,authMessage.getFront());
            finalBitmap.display(iv_merchant_identityCode2,authMessage.getBack());
            finalBitmap.display(iv_merchant_license,authMessage.getLicence());
//            CommonUtils.startImageLoader(cubeimageLoader, authMessage.getFront(), iv_merchant_identityCode1);
//            CommonUtils.startImageLoader(cubeimageLoader, authMessage.getBack(), iv_merchant_identityCode2);
//            CommonUtils.startImageLoader(cubeimageLoader, authMessage.getLicence(), iv_merchant_license);
        }
        if(authMessage.getReason()!=null && !authMessage.getReason().equals("")){
            rl_promotion_fail.setVisibility(View.VISIBLE);
            tv_message.setText("\u3000\u3000\u3000\u3000\u3000"+authMessage.getReason());
        }else{
            rl_promotion_fail.setVisibility(View.GONE);
        }

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
        ToastUtils.Infotoast(mContext,"提交审核成功！");
        setResult(RESULT_OK,null);

        Intent intent = new Intent(mContext, ActivityMyWeb.class);
        intent.putExtra("title", "奔犇商场");
        intent.putExtra("url", AndroidConfig.NETHOST4 + "/mobileService?token="+user.getToken());
        startActivity(intent);
//        if(from.equals("promotion")){
//            Intent intent = new Intent(mContext, ActivityMyWeb.class);
//            intent.putExtra("title", "促销");
//            intent.putExtra("url", AndroidConfig.NETHOST4 + "/mobileService/serviceDetail?type=0&token="+user.getToken());
//            startActivity(intent);
//
//        }else if(from.equals("groupbuy")){
//            Intent intent = new Intent(mContext, ActivityMyWeb.class);
//            intent.putExtra("title", "团购");
//            intent.putExtra("url", AndroidConfig.NETHOST4 + "/mobileService/serviceDetail?type=1&token="+user.getToken());
//            startActivity(intent);
//        }
        AnimFinsh();
    }

    @Override
    protected void onFailure(HttpException exception, String strMsg) {
        ToastUtils.Infotoast(mContext,"提交审核失败！");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.prerecord_tab_one:
                ll_person.setVisibility(View.VISIBLE);
                ll_merchant.setVisibility(View.GONE);
                setChecked(prerecord_tab_three, false);
                setChecked(prerecord_tab_one, true);
                currentTable=1;
                break;
            case R.id.prerecord_tab_three:
                ll_person.setVisibility(View.GONE);
                ll_merchant.setVisibility(View.VISIBLE);
                setChecked(prerecord_tab_three, true);
                setChecked(prerecord_tab_one, false);
                currentTable=2;
                break;
            case R.id.iv_person_identityCode1:
                imgType = 1;
                changeImage();
                break;
            case R.id.iv_person_identityCode2:
                imgType = 2;
                changeImage();
                break;
            case R.id.iv_merchant_identityCode1:
                imgType = 3;
                changeImage();
                break;
            case R.id.iv_merchant_identityCode2:
                imgType = 4;
                changeImage();
                break;
            case R.id.iv_merchant_license:
                imgType = 5;
                changeImage();
                break;
            case R.id.btn_auth:
                switch (currentTable){
                    case 1:
                        personAuth();
                        break;
                    case 2:
                        merchantAuth();
                        break;
                }

                break;
            case R.id.tv_agreement:
                Intent intent = new Intent(this, ActivityWeb.class);
                intent.putExtra("title", "服务协议");
                intent.putExtra("url", AndroidConfig.NETHOST  + "/store/declaration");

                startActivity(intent);

                break;
        }
    }

    private void personAuth() {
        String name = edt_person_name.getText().toString();
        String identityCode = String.valueOf(edt_person_identityCode.getText()).trim();

        if (TextUtils.isEmpty(name)) {
            ToastUtils.Errortoast(mContext, "姓名不可为空!");
            return;
        }
        if (identityCode.length()<18) {
            ToastUtils.Errortoast(mContext, "请正确填写身份证号!");
            return;
        }

        if(authMessage==null || authMessage.getAuthid()==0 || authMessage.getType()==2) {

            if (file1 == null) {
                ToastUtils.Errortoast(mContext, "请添加身份证正面照片!");
                return;
            }
            if (file2 == null) {
                ToastUtils.Errortoast(mContext, "请添加身份证反面照片!");
                return;
            }
        }
        if (!cb_agree.isChecked()) {
            ToastUtils.Errortoast(mContext, "请同意号码直通车服务协议!");
            return;
        }

        if (CommonUtils.isNetworkAvailable(mContext)) {
            InteNetUtils.getInstance(mContext).Setauth(1,name,identityCode,"",file1,file2,null,user.getToken(),mRequestCallBack);
        }else{
            ToastUtils.Infotoast(mContext,"网络不可用");
        }

    }

    private void merchantAuth() {
        String name = edt_merchant_name.getText().toString();
        //edt_company 企业名称
        String company = edt_company.getText().toString();
        String identityCode = String.valueOf(edt_merchant_identityCode.getText()).trim();

        if (TextUtils.isEmpty(name)) {
            ToastUtils.Errortoast(mContext, "姓名不可为空!");
            return;
        }
        if (TextUtils.isEmpty(company)) {
            ToastUtils.Errortoast(mContext, "企业名称不可为空!");
            return;
        }
        if (identityCode.length()<18) {
            ToastUtils.Errortoast(mContext, "请正确填写身份证号!");
            return;
        }
        if(authMessage==null || authMessage.getAuthid()==0 || authMessage.getType()==1) {
            if (file3 == null) {
                ToastUtils.Errortoast(mContext, "请添加身份证正面照片!");
                return;
            }
            if (file4 == null) {
                ToastUtils.Errortoast(mContext, "请添加身份证反面照片!");
                return;
            }
            if (file5 == null) {
                ToastUtils.Errortoast(mContext, "请添加营业执照照片!");
                return;
            }
        }
        if (!cb_agree.isChecked()) {
            ToastUtils.Errortoast(mContext, "请同意号码直通车服务协议!");
            return;
        }

        if (CommonUtils.isNetworkAvailable(mContext)) {
            InteNetUtils.getInstance(mContext).Setauth(2,name,identityCode,company,file3,file4,file5,user.getToken(),mRequestCallBack);
        }else{
            ToastUtils.Infotoast(mContext,"网络不可用");
        }

    }

    private void setChecked(RelativeLayout view, boolean isCheck) {
        RadioButton tab_RB = (RadioButton) view.findViewById(R.id.tab_RB);
        tab_RB.setChecked(isCheck);

    }

    // 显示拍照选照片 弹窗
    private void changeImage() {
        setTheme(R.style.ActionSheetStyleIOS7);
        showActionSheet();
    }

    public void showActionSheet() {
        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle("取消")
                .setOtherButtonTitles("拍摄新图片", "从相册选择")
                        // 设置颜色 必须一一对应
                .setOtherButtonTitlesColor("#1E82FF", "#1E82FF")
                .setCancelableOnTouchOutside(true).setListener(this).show();
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        switch (index) {
            case 0:
                imageName = getPhotoFileName();
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(CommonUtils.getImagePath(mContext, imageName)));
                startActivityForResult(intent, PIC_REQUEST_CODE_SELECT_CAMERA);
                break;
            case 1:
                intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*");
                startActivityForResult(intent, PIC_Select_CODE_ImageFromLoacal);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PIC_REQUEST_CODE_SELECT_CAMERA:
                if(resultCode==RESULT_OK) {
                    File temp = CommonUtils.getImagePath(mContext, imageName);
                    imagePath = temp.getAbsolutePath();
                    switch (imgType) {
                        case 1:
                            file1 = CommonUtils.getImagePath(mContext, imageName);
                            iv_person_identityCode1.setImageBitmap(getBitmap(imagePath));
                            break;
                        case 2:
                            file2 = CommonUtils.getImagePath(mContext, imageName);
                            iv_person_identityCode2.setImageBitmap(getBitmap(imagePath));
                            break;
                        case 3:
                            file3 = CommonUtils.getImagePath(mContext, imageName);
                            iv_merchant_identityCode1.setImageBitmap(getBitmap(imagePath));
                            break;
                        case 4:
                            file4 = CommonUtils.getImagePath(mContext, imageName);
                            iv_merchant_identityCode2.setImageBitmap(getBitmap(imagePath));
                            break;
                        case 5:
                            file5 = CommonUtils.getImagePath(mContext, imageName);
                            iv_merchant_license.setImageBitmap(getBitmap(imagePath));
                            break;
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

                                switch (imgType){
                                    case 1:
                                        file1 = new File(imagePath);
                                        iv_person_identityCode1.setImageBitmap(getBitmap(imagePath));
                                        break;
                                    case 2:
                                        file2 = new File(imagePath);
                                        iv_person_identityCode2.setImageBitmap(getBitmap(imagePath));
                                        break;
                                    case 3:
                                        file3 = new File(imagePath);
                                        iv_merchant_identityCode1.setImageBitmap(getBitmap(imagePath));
                                        break;
                                    case 4:
                                        file4 = new File(imagePath);
                                        iv_merchant_identityCode2.setImageBitmap(getBitmap(imagePath));
                                        break;
                                    case 5:
                                        file5 = new File(imagePath);
                                        iv_merchant_license.setImageBitmap(getBitmap(imagePath));
                                        break;
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
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    @Override
    protected void onDestroy() {
        if (bitMap != null) {
            bitMap.recycle();
        }
        super.onDestroy();
    }
}
