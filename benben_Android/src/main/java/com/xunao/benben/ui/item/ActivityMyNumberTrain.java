package com.xunao.benben.ui.item;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ta.utdid2.android.utils.NetworkUtils;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.IndustryInfo;
import com.xunao.benben.bean.NumberTrainDetail;
import com.xunao.benben.bean.NumberTrainPoster;
import com.xunao.benben.bean.SuccessMsg;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoMsgHint;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.dialog.TransferMyNumberTrainDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityLogin;
import com.xunao.benben.ui.item.TallGroup.ActivityGroupNotice;
import com.xunao.benben.utils.Bimp;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.FileUtils;
import com.xunao.benben.utils.ImageItem;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.PublicWay;
import com.xunao.benben.utils.RegexUtils;
import com.xunao.benben.utils.Res;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.ActionSheet.ActionSheetListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ActivityMyNumberTrain extends BaseActivity implements
		OnClickListener, ActionSheetListener {
//	private RoundedImageView iv_upload_face;
    private GridView noScrollgridview;
    private GridAdapter adapter;
    private ArrayList<NumberTrainPoster> picList = new ArrayList<>();
	private boolean isCreate;
	private Bitmap myBitmap = null;
	private byte[] mContent;
	private EditText et_name;
	private EditText et_short_name;
	private EditText et_phone;
	private RelativeLayout rl_chose_address;
	private TextView tv_chose_address;
	private EditText et_description;
	private EditText et_numPhone;
	private EditText et_address;
	private EditText et_tag1,et_tag2,et_tag3;
	private TextView et_industry;
	private RelativeLayout rl_set_detailaddress;
	private TextView tv_set_detailaddress;
	private RelativeLayout rl_industry;
	// 记录了地区的id
	private String[] addressId = new String[4];
	private String imagePath;
	private String imageName;
	private static final int PIC_REQUEST_CODE_WITH_DATA = 1; // 标识获取图片数据
	private static final int PIC_REQUEST_CODE_SELECT_CAMERA = 2; // 标识请求照相功能的activity
//	private static final int PIC_Select_CODE_ImageFromLoacal = 3;// 标识请求相册取图功能的activity
	private static final int CHOCE_ADDRESS = 4;
	private static final int CHOCE_DETAIL_ADDRESS = 5;
	private static final int CHOCE_INDUSTRY = 6;
	private NumberTrainDetail numberTrainDetail;
	private double latitude;
	private double longitude;
	private String industryId;
	private String shortName;

	private RelativeLayout rl_broad_cast;
	private ImageView iv_broad_cast;

//	private File files;

	private myBoradCast boradCast;
    private MsgDialog msgDialog;

    public static Bitmap bimap;
//    private FinalBitmap fb;
    private String ids="";
    private boolean isRemoveOriginPoster = false;

	class myBoradCast extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			InteNetUtils.getInstance(mContext).getMyStore(mRequestCallBack);
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initdefaultImage(R.drawable.icon_upload_face);
		boradCast = new myBoradCast();
		registerReceiver(boradCast, new IntentFilter(
				AndroidConfig.refreshNumberTrain));

        Res.init(this);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;
        CrashApplication.getInstance().addActivity(this);
        bimap = BitmapFactory.decodeResource(getResources(),
                R.drawable.icon_addpic_unfocused);
        PublicWay.activityList.add(this);
//        fb = FinalBitmap.create(mContext);
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		if (getIntent().getStringExtra("do") == null) {
			setContentView(R.layout.activity_created_number_train);
		} else {
			if (getIntent().getStringExtra("do").equals("update")) {
				setContentView(R.layout.activity_my_number_train_update);
			} else {
				setContentView(R.layout.activity_created_number_train);
			}
		}
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("我的号码直通车", "", "完成",
				R.drawable.icon_com_title_left, 0);

//		iv_upload_face = (RoundedImageView) findViewById(R.id.iv_upload_face);
		et_name = (EditText) findViewById(R.id.et_name);
		et_short_name = (EditText) findViewById(R.id.et_short_name);
		et_phone = (EditText) findViewById(R.id.et_phone);
		rl_chose_address = (RelativeLayout) findViewById(R.id.rl_chose_address);
		tv_chose_address = (TextView) findViewById(R.id.tv_chose_address);
		et_description = (EditText) findViewById(R.id.et_description);
		et_address = (EditText) findViewById(R.id.et_address);
		et_tag1 = (EditText) findViewById(R.id.et_tag1);
        et_tag2 = (EditText) findViewById(R.id.et_tag2);
        et_tag3 = (EditText) findViewById(R.id.et_tag3);
		et_numPhone = (EditText) findViewById(R.id.et_numPhone);
		et_industry = (TextView) findViewById(R.id.et_industry);
		send_message_red = (TextView) findViewById(R.id.send_message_red);
		rl_industry = (RelativeLayout) findViewById(R.id.rl_industry);
		rl_set_detailaddress = (RelativeLayout) findViewById(R.id.rl_set_detailaddress);
		tv_set_detailaddress = (TextView) findViewById(R.id.tv_set_detailaddress);
		rl_broad_cast = (RelativeLayout) findViewById(R.id.rl_broad_cast);
		iv_broad_cast = (ImageView) findViewById(R.id.iv_broad_cast);

//		iv_upload_face.setOnClickListener(this);
		rl_chose_address.setOnClickListener(this);
		rl_set_detailaddress.setOnClickListener(this);
		tv_set_detailaddress.setOnClickListener(this);
		rl_industry.setOnClickListener(this);

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
                        changeImage();
                    }
                }
            }
        });
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		if (CommonUtils.isNetworkAvailable(mContext)) {
			InteNetUtils.getInstance(mContext).getMyStore(mRequestCallBack);
		}
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                onBackPressed();

            }
        });

		setOnRightClickLinester(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                saveMessage();
            }
        });

        et_tag1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = String.valueOf(editable);
                if(text.contains(" ")){
                    text = text.replace(" ","");
                    et_tag1.setText(text);
                    et_tag1.setSelection(text.length());
                }

            }
        });

        et_tag2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = String.valueOf(editable);
                if(text.contains(" ")){
                    text = text.replace(" ", "");
                    et_tag2.setText(text);
                    et_tag2.setSelection(text.length());
                }

            }
        });

        et_tag3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = String.valueOf(editable);
                if(text.contains(" ")){
                    text = text.replace(" ", "");
                    et_tag3.setText(text);
                    et_tag3.setSelection(text.length());
                }

            }
        });


	}

    @Override
    public void onBackPressed() {
        if (getIntent().getStringExtra("do").equals("update")) {
            msgDialog = new MsgDialog(ActivityMyNumberTrain.this, R.style.MyDialogStyle);
            msgDialog.setContent("需要保存修改信息吗？", "", "保存", "不保存");
            msgDialog.setCancleListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    msgDialog.dismiss();
                    AnimFinsh();
                }
            });
            msgDialog.setOKListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveMessage();

                }

            });
            msgDialog.show();
        }else{
            AnimFinsh();
        }
    }

    private void saveMessage() {
        if (numberTrainDetail == null) {
            if(Bimp.tempSelectBitmap.size()==0){
                ToastUtils.Infotoast(mContext, "请上传头像！");
                return;
            }
        }else{
            if(picList.size()==0 && Bimp.tempSelectBitmap.size()==0){
                ToastUtils.Infotoast(mContext, "请上传头像！");
                return;
            }
        }

        if (TextUtils.isEmpty(et_name.getText().toString().trim())) {
            ToastUtils.Infotoast(mContext, "请输入直通车名称！");
            return;
        }

        if(!CommonUtils.StringIsSurpass2(et_name.getText().toString().trim(), 2, 20)){
            ToastUtils.Infotoast(mContext, "直通车名称限制在2-20个字之间");
            return;
        }
        if (CommonUtils.isEmpty(et_short_name.getText().toString())) {
            ToastUtils.Infotoast(mContext, "直通车简称不可为空");
            return;
        }

        if (!CommonUtils.StringIsSurpass(et_short_name.getText()
                .toString().trim(), 1, 10)) {
            ToastUtils.Errortoast(mContext, "直通车简称最多为10个字");
            return;
        }

        // if (et_short_name.getText().toString().trim().length() > 6) {
        // ToastUtils.Infotoast(mContext, "直通车简称最多为6个字！");
        // return;
        // }
//        if (CommonUtils.isEmpty(et_short_name.getText().toString())) {
//            ToastUtils.Infotoast(mContext, "直通车简称不可为空");
//            return;
//        }
//
//        if (!CommonUtils.StringIsSurpass(et_short_name.getText()
//                .toString(), 1, 20)) {
//            ToastUtils.Errortoast(mContext, "直通车简称不能超过20个字");
//            return;
//        }

        if (!RegexUtils.checkMobile(et_numPhone.getText().toString()
                .trim())) {
            ToastUtils.Infotoast(mContext, "请输入正确的手机号！");
            return;
        }

        if (!RegexUtils.checkMobile(et_numPhone.getText().toString()
                .trim())) {
            ToastUtils.Infotoast(mContext, "手机格式不正确！");
            return;
        }

        if ((et_industry.getText().toString().trim()).equals("选择行业")) {
            ToastUtils.Infotoast(mContext, "请选择行业！");
            return;
        }

        if ((tv_chose_address.getText().toString().trim())
                .equals("选择所在地区")) {
            ToastUtils.Infotoast(mContext, "请选择所在地区！");
            return;
        }

        if ((tv_set_detailaddress.getText().toString().trim())
                .equals("去地图设置详细地址")) {
            ToastUtils.Infotoast(mContext, "请去地图设置详细地址！");
            return;
        }

        if (TextUtils.isEmpty(et_address.getText().toString().trim())) {
            ToastUtils.Infotoast(mContext, "请填写详细地址！");
            return;
        }

        if (!CommonUtils.StringIsSurpass(et_address.getText()
                .toString().trim(), 1, 20)) {
            ToastUtils.Errortoast(mContext, "详细地址不能超过20个字");
            return;
        }

        String newTags = "";
        String tag1 = String.valueOf(et_tag1.getText()).trim();
        if(tag1.contains(" ")){
            ToastUtils.Errortoast(mContext, "服务项目请勿输入空格！");
            return;
        }
        newTags += tag1;
        String tag2 = String.valueOf(et_tag2.getText()).trim();
        if(tag2.contains(" ")){
            ToastUtils.Errortoast(mContext, "服务项目请勿输入空格！");
            return;
        }
        if(!newTags.equals("")){
            newTags += " "+tag2;
        }else{
            newTags += tag2;
        }
        String tag3 = String.valueOf(et_tag3.getText()).trim();
        if(tag3.contains(" ")){
            ToastUtils.Errortoast(mContext, "服务项目请勿输入空格！");
            return;
        }
        if(!newTags.equals("")){
            newTags += " "+tag3;
        }else{
            newTags += tag3;
        }
        if (!TextUtils.isEmpty(newTags)) {
            String[] tags = newTags.split("\\s+");
            if (tags.length > 3) {
                ToastUtils.Infotoast(mContext, " 最多填写三个服务项目！");
                return;
            }

            for (String string : tags) {
                if (!"".equals(string)) {

                    int orignLen = string.length();
                    int resultLen = 0;
                    String temp = null;
                    for (int i = 0; i < orignLen; i++) {
                        temp = string.substring(i, i + 1);
                        try {// 3 bytes to indicate chinese word,1 byte
                            // to indicate english
                            // word ,in utf-8 encode
                            if (temp.getBytes("utf-8").length == 3) {
                                resultLen += 2;
                            } else {
                                resultLen++;
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    if (resultLen <= 1 || 13 <= resultLen) {
                        isLong = true;

                        break;
                    }
                }
            }

            if(isLong){
                isLong = false;
                ToastUtils.Errortoast(mContext, "服务项目不能超过6个字");
                return;
            }

        } else {
            ToastUtils.Infotoast(mContext, " 请填写至少一个服务项目！");
            return;
        }

        if (et_description.getText().toString().length() > 200) {
            ToastUtils.Infotoast(mContext, "业务介绍字数最多200字！");
            return;
        }
        if (CommonUtils.isEmpty(et_description.getText().toString())) {
            ToastUtils.Infotoast(mContext, "业务介绍不可为空");
            return;
        }

        String names = et_name.getText().toString();
        shortName = et_short_name.getText().toString();
        String phones = et_phone.getText().toString();
        String address = et_address.getText().toString();
        String descriptions = et_description.getText().toString();
        String numPhone = et_numPhone.getText().toString();
        String deleteIds="";
        if(ids.length()>0){
            deleteIds = ids.substring(0,ids.length()-1);
        }
        int size = Bimp.tempSelectBitmap.size();
        String[] images = new String[size];
        for (int i = 0; i < size; i++) {
            images[i] = Bimp.tempSelectBitmap.get(i).getImagePath();
        }

        if (CommonUtils.isNetworkAvailable(mContext)){
            setOnRightClickLinester(null);
            showLoding("");
            if(isRemoveOriginPoster){
                InteNetUtils.getInstance(mContext).updateMyNumberTrain(
                        names, shortName, phones, numPhone, industryId,
                        address, newTags, descriptions, addressId, latitude,
                        longitude, images,"1",deleteIds,user.getToken(), requestCallBack);
            }else{
                InteNetUtils.getInstance(mContext).updateMyNumberTrain(
                        names, shortName, phones, numPhone, industryId,
                        address, newTags, descriptions, addressId, latitude,
                        longitude, images,"",deleteIds,user.getToken(), requestCallBack);
            }
        }


    }

    private boolean isLong = false;

	@Override
	protected void onHttpStart() {

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		String reg_num = jsonObject.optString("ret_num");

		if (reg_num != null) {
			if (!reg_num.equals("0")) {
				String errorMsg = jsonObject.optString("ret_msg");
				
				if ("123".equalsIgnoreCase(reg_num)) {
					isCreate = true;
					initTitle_Right_Left_bar("我的号码直通车", "", "创建",
							R.drawable.icon_com_title_left, 0);
				} else {
					
					if(jsonObject.optString("ret_num").equals("2015") ){
						final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(
								mContext, R.style.MyDialog1);
						hint.setContent("奔犇账号在其他手机登录");
						hint.setBtnContent("确定");
						hint.show();
						hint.setOKListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								hint.dismiss();
							}
						});

						hint.show();
						CrashApplication.getInstance().logout();
						startActivity(new Intent(mContext, ActivityLogin.class));
					}else{
						ToastUtils.Infotoast(mContext, errorMsg);
					}
				}
				return;
			}
			try {
				numberTrainDetail = new NumberTrainDetail();
				numberTrainDetail.checkJson(jsonObject);
				JSONObject object = jsonObject.optJSONObject("number_info");
				numberTrainDetail.parseJSONMyNumberTrain(object);

				String poster = numberTrainDetail.getPoster();
				String name = numberTrainDetail.getName();
				String industry = numberTrainDetail.getIndustry();
				String address = numberTrainDetail.getAddress();
				String description = numberTrainDetail.getDescription();
				String area = numberTrainDetail.getArea();
				String tag = numberTrainDetail.getTag();
				String phone = numberTrainDetail.getPhone();
				String shortName = numberTrainDetail.getShortName();
				String telPhone = numberTrainDetail.getTelNumber();

				String province = numberTrainDetail.getNumberProvince();
				String city = numberTrainDetail.getNumberCity();
				String area2 = numberTrainDetail.getNumberArea();
				String street = numberTrainDetail.getNumberStreet();

				latitude = numberTrainDetail.getNumberLat();
				longitude = numberTrainDetail.getNumberLng();

				if (!numberTrainDetail.getHaveRight().equals("0")) {
					rl_broad_cast.setVisibility(View.VISIBLE);
				} else {
					rl_broad_cast.setVisibility(View.GONE);
				}

				if (numberTrainDetail.getIs_close() == 0) {
					send_message_red
							.setBackgroundResource(R.drawable.btn_sendmessage_red);
					send_message_red.setText("关闭直通车");
				} else {
					send_message_red
							.setBackgroundResource(R.drawable.btn_sendmessage_blue);
					send_message_red.setText("打开直通车");
				}

				send_message_red.setOnClickListener(this);
                findViewById(R.id.transfer_mynumber_train).setOnClickListener(this);

				if (!numberTrainDetail.getHaveRight().equals("0")) {
					rl_broad_cast.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(
									ActivityMyNumberTrain.this,
									R.style.MyDialog1);
							hint.setContent("该只有"
									+ numberTrainDetail.getHaveRight()
									+ "条，确定发送");
							hint.setBtnContent("知道了");
							hint.show();
							hint.setOKListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									startAnimActivity2Obj(
											ActivitySmallPublic.class,
											"numberTrain", "我开通了号码直通车来给我捧捧场吧!");
									hint.dismiss();
								}
							});

							hint.show();
						}
					});
				}

				formatLat(latitude, longitude);

				addressId[0] = province;
				addressId[1] = city;
				addressId[2] = area2;
				addressId[3] = street;

				industryId = numberTrainDetail.getIndustryId();
                try {
                    IndustryInfo industryInfo = dbUtil.findFirst(Selector.from(IndustryInfo.class).where(
                            "id", "=", industryId));
                    if(industryInfo!=null) {
                        industryInfo = dbUtil.findFirst(Selector.from(IndustryInfo.class).where(
                                "id", "=", industryInfo.getParentId()));
                        if (industryInfo.getLevel().equals("1")) {
                            industry = industryInfo.getName() + " " + industry;
                        } else {
                            industryInfo = dbUtil.findFirst(Selector.from(IndustryInfo.class).where(
                                    "id", "=", industryInfo.getParentId()));
                            industry = industryInfo.getName() + " " + industry;
                        }
                    }
                    et_industry.setText(industry);
                    et_industry.setTextColor(Color.BLACK);

                } catch (DbException e) {
                    e.printStackTrace();
                }
                picList = numberTrainDetail.getNumberTrainPosters();
                if(picList==null || picList.size()==0){
                    if (!TextUtils.isEmpty(poster)) {
                        NumberTrainPoster numberTrainPoster = new NumberTrainPoster();
                        numberTrainPoster.setPid(0);
                        numberTrainPoster.setPoster(poster);
                        picList.add(numberTrainPoster);
                    }
                }

                adapter.update();

//                if (TextUtils.isEmpty(poster)) {
//                    iv_upload_face.setImageResource(R.drawable.icon_upload_face);
//				} else {
//					CommonUtils.startImageLoader(cubeimageLoader, poster,
//							iv_upload_face);
//				}

				et_name.setText(name);
				et_description.setText(description);
				et_phone.setText(telPhone);
				et_numPhone.setText(phone);
				et_address.setText(address);
				et_short_name.setText(shortName);

				String[] split = tag.split("\\s+");
//				String etag = "";
//				for (int i = 0; i < split.length; i++) {
//					etag += split[i] + " ";
//				}
                if(split.length==1){
                    et_tag1.setText(split[0]);
                    et_tag2.setText("");
                    et_tag3.setText("");
                }else if(split.length==2){
                    et_tag1.setText(split[0]);
                    et_tag2.setText(split[1]);
                    et_tag3.setText("");
                }else{
                    et_tag1.setText(split[0]);
                    et_tag2.setText(split[1]);
                    et_tag3.setText(split[2]);
                }
				tv_chose_address.setText(area);
				tv_chose_address.setTextColor(Color.BLACK);
				et_short_name.setText(shortName);


			} catch (NetRequestException e) {
				e.printStackTrace();
				e.getError().print(mContext);
			}
		}
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Infotoast(mContext, "网络不可用");
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
//		case R.id.iv_upload_face:
//			changeImage();
//			break;
		case R.id.send_message_red:

			final InfoMsgHint hint = new InfoMsgHint(mContext,
					R.style.MyDialog1);

			switch (numberTrainDetail.getIs_close()) {
			case 0:// 关闭
				hint.setContent("关闭之后直通车将从其他人的收藏中移除,是否关闭直通车?", "", "关闭直通车",
						"取消");
				break;
			case 1:// 打开
				hint.setContent("是否要打开直通车?", "", "打开直通车", "取消");
				break;
			}

			hint.setCancleListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					hint.dismiss();
				}
			});
			hint.setOKListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					hint.dismiss();
					InteNetUtils.getInstance(mContext).storeClose(
							numberTrainDetail.getId(),
							new RequestCallBack<String>() {

								@Override
								public void onFailure(HttpException arg0,
										String arg1) {

									switch (numberTrainDetail.getIs_close()) {
									case 0:// 关闭
										ToastUtils.Infotoast(mContext, "关闭失败");
										break;
									case 1:// 打开
										ToastUtils.Infotoast(mContext, "打开失败");
										break;
									}

								}

								@Override
								public void onSuccess(ResponseInfo<String> arg0) {

									try {
										JSONObject jsonObject = new JSONObject(
												arg0.result);

										SuccessMsg msg = new SuccessMsg();

										try {
											msg.checkJson(jsonObject);
											int optInt = jsonObject
													.optInt("is_close");

											numberTrainDetail
													.setIs_close(optInt);
											if (optInt == 0) {
												send_message_red
														.setBackgroundResource(R.drawable.btn_sendmessage_red);
												send_message_red
														.setText("关闭直通车");
											} else {
												send_message_red
														.setBackgroundResource(R.drawable.btn_sendmessage_blue);
												send_message_red
														.setText("打开直通车");
											}

										} catch (NetRequestException e) {
											e.printStackTrace();
											e.getError().print(mContext);
										}

									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}

							});

				}
			});
			hint.show();

			break;
        case R.id.transfer_mynumber_train:
            //转让号码直通车
            startActivity(new Intent(mContext, ActivityTransferMyNumberTrain.class));
             break;
		case R.id.rl_chose_address:
			startAnimActivityForResult3(ActivityChoiceAddress.class,
					CHOCE_ADDRESS, "trim", "trim", "level", "0");
			break;
		case R.id.tv_set_detailaddress:
			if ((tv_chose_address.getText().toString().trim()).equals("选择所在地区")) {
				ToastUtils.Infotoast(mContext, "请选择所在地区！");
			} else {
				double[] lats = new double[2];
				if (!TextUtils.isEmpty(latitude + "")) {
					lats[0] = latitude;
					lats[1] = longitude;
				}
				startAnimActivityForResult4(ActivityChoiceDetailAddress.class,
						CHOCE_DETAIL_ADDRESS, "address", tv_chose_address
								.getText().toString().trim(), "lats", lats);
			}

			break;
		case R.id.rl_industry:
			startAnimActivityForResult(ActivityChoiceIndusrty.class,
					CHOCE_INDUSTRY);
		default:
			break;
		}
	}

    //处理号码直通车转让
    private void dealTransferMyNumberTrain(){
        TransferMyNumberTrainDialog dialog = new TransferMyNumberTrainDialog(mContext);
        dialog.setDialogListener(new TransferMyNumberTrainDialog.DialogViewListener() {
            @Override
            public void onConfirm() {
            }
        });
        dialog.show();
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
//			imageName = getPhotoFileName();
//			Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//			intent2.putExtra(MediaStore.EXTRA_OUTPUT,
//					Uri.fromFile(CommonUtils.getImagePath(mContext, imageName)));
//			startActivityForResult(intent2, PIC_REQUEST_CODE_SELECT_CAMERA);
            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(openCameraIntent, PIC_REQUEST_CODE_SELECT_CAMERA);
			break;
		case 1:
//			Intent intent = new Intent(Intent.ACTION_PICK, null);
//			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//					"image/*");
//			startActivityForResult(intent, PIC_Select_CODE_ImageFromLoacal);

            Intent intent = new Intent(
                    ActivityMyNumberTrain.this,
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

    protected void onRestart() {
        adapter.update();
        super.onRestart();
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
//		case PIC_REQUEST_CODE_WITH_DATA:
//			if (data != null) {
//				Uri uri = data.getData();
//				BitmapFactory.Options options = new Options();
//				String imageNames = data.getStringExtra("imageName");
//				files = CommonUtils.getImagePath(mContext, imageNames);
//
//				options.inJustDecodeBounds = false;
////				Bitmap bitmap = CutImageUtils.getBitMap(mContext, uri, options,
////						iv_upload_face);
////				iv_upload_face.setImageBitmap(bitmap);
//			}
//			break;
		case PIC_REQUEST_CODE_SELECT_CAMERA:

//			File temp = CommonUtils.getImagePath(mContext, imageName);
//			if (temp.length() > 0) {
//				Intent intent = new Intent(this, ActivityCutImage.class);
//				intent.putExtra("imagePath", temp.getAbsolutePath());
//				startActivityForResult(intent, PIC_REQUEST_CODE_WITH_DATA);
//			}
            if(resultCode==RESULT_OK) {
                String fileName = String.valueOf(System.currentTimeMillis());
                Bitmap bm = (Bitmap) data.getExtras().get("data");
                String saveBitmap = FileUtils.saveBitmapPhoto(bm, fileName);
                ImageItem takePhoto = new ImageItem();
                takePhoto.setBitmap(new SoftReference<Bitmap>(bm));
                takePhoto.setImagePath(saveBitmap);
                Bimp.tempSelectBitmap.add(takePhoto);
            }
			break;
//		case PIC_Select_CODE_ImageFromLoacal:
//			if (data != null) {
//				if (data.getData() != null) {
//					Uri uri = data.getData();
//					boolean isSDCard = true;
//					ContentResolver cr = getContentResolver();
//					try {
//						InputStream openInputStream = cr.openInputStream(uri);
//						if (openInputStream != null) {
//							imagePath = CutImageUtils.getImagePath(mContext,
//									openInputStream);
//						} else {
//							ToastUtils.Infotoast(mContext, "请选择正确的图像资源");
//						}
//					} catch (FileNotFoundException e) {
//						e.printStackTrace();
//					}
//					Intent intent2 = new Intent(this, ActivityCutImage.class);
//					intent2.putExtra("imagePath", imagePath);
//					startActivityForResult(intent2, PIC_REQUEST_CODE_WITH_DATA);
//				}
//			}
//			break;
		case CHOCE_ADDRESS:
			if (data != null) {
				if (resultCode == AndroidConfig.ChoiceAddressResultCode) {
					String addressname = data.getStringExtra("address");
					addressId = null;
					addressId = data.getStringArrayExtra("addressId");
					tv_chose_address.setText(addressname);
					tv_chose_address.setTextColor(Color.BLACK);
					
					
					latitude = 0;
					longitude = 0;
					tv_set_detailaddress.setText("");
					
				}
			}

			break;
		case CHOCE_DETAIL_ADDRESS:
			if (data != null) {
				if (resultCode == 1) {
					String address = data.getStringExtra("address");
					// et_address.setText(address);
					latitude = data.getDoubleExtra("latitude", 0.0);
					longitude = data.getDoubleExtra("longitude", 0.0);
					formatLat(latitude, longitude);

					tv_chose_address.setTextColor(Color.BLACK);
				}
			}
			break;
		case CHOCE_INDUSTRY:
			if (data != null) {
				et_industry.setText(data.getStringExtra("industry"));
				et_industry.setTextColor(Color.BLACK);
				industryId = data.getStringExtra("industryId");
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private RequestCallBack<String> requestCallBack = new RequestCallBack<String>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
            dissLoding();
            setOnRightClickLinester(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    saveMessage();
                }
            });
			ToastUtils.Infotoast(mContext, "修改我的直通车信息失败！");
		}

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
            dissLoding();

			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(arg0.result);
				NumberTrainDetail detail = new NumberTrainDetail();
				detail.checkJson(jsonObject);

				JSONObject optJSONObject = jsonObject
						.optJSONObject("number_info");

				user.setZhiId(optJSONObject.optInt("NumberId"));
				user.setZhiName(optJSONObject.optString("NumberName"));
				user.setZhiShortName(shortName);
				try {
					dbUtil.saveOrUpdate(user);
				} catch (DbException e) {
					e.printStackTrace();
				}
				ToastUtils.Infotoast(mContext, isCreate ? "我的直通车创建成功！"
						: "修改我的直通车信息成功！");
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
				AnimFinsh();
			} catch (NetRequestException e1) {
				e1.getError().print(mContext);
				e1.printStackTrace();
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
            setOnRightClickLinester(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    saveMessage();
                }
            });
		}
	};
	private TextView send_message_red;

	private void formatLat(double latitude, double longitude) {
		DecimalFormat df = new DecimalFormat("#.00");

		tv_set_detailaddress.setText("经度:" + df.format(longitude) + "  "
				+ "纬度:" + df.format(latitude));
		tv_set_detailaddress.setTextColor(Color.BLACK);
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
//            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_number_train_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (RoundedImageView) convertView
                        .findViewById(R.id.item_grida_image);
                View v = convertView.findViewById(R.id.box);
                holder.delete = (ImageView) convertView
                        .findViewById(R.id.delete);
                convertView.setTag(holder);

                v.setLayoutParams(new RelativeLayout.LayoutParams(mWidth
                        + PixelUtil.dp2px(10), mWidth + PixelUtil.dp2px(10)));
                holder.image.getLayoutParams().width = mWidth;
                holder.image.getLayoutParams().height = mWidth;

//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }

            if (position == (Bimp.tempSelectBitmap.size()+picList.size())) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.icon_addpic_unfocused));
                holder.delete.setVisibility(View.GONE);
            } else {

                if(position<picList.size()){
                    NumberTrainPoster numberTrainPoster = picList.get(position);
//                    fb.display(holder.image,numberTrainPoster.getPoster());
                    CommonUtils.startImageLoader(cubeimageLoader,
                            numberTrainPoster.getPoster(), holder.image);
                }else{
                    holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position-picList.size())
                            .getBitmap());
                }

                holder.delete.setVisibility(View.VISIBLE);
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(position<picList.size()){
                            int id = picList.get(position).getPid();
                            if(id==0){
                                isRemoveOriginPoster = true;
                            }else{
                                ids += id+",";
                            }
                            picList.remove(position);
                        }else {
                            Bimp.tempSelectBitmap.remove(position-picList.size());
                        }
                        adapter.update();
                    }
                });




            }

            return convertView;
        }

        public class ViewHolder {
            public RoundedImageView image;
            public ImageView delete;
        }


        public void loading() {
            int size = picList.size()+Bimp.tempSelectBitmap.size();
            int mWidth = 0;
            if(size==0){
                mWidth = PixelUtil.dp2px(100);
            }else if(size==6){
                mWidth = PixelUtil.dp2px(80)*size+PixelUtil.dp2px(20)+PixelUtil.dp2px(5)*(size-1);
            }else{
                mWidth = PixelUtil.dp2px(80)*(size+1)+PixelUtil.dp2px(20)+PixelUtil.dp2px(5)*size;
            }
            noScrollgridview.setLayoutParams(new LinearLayout.LayoutParams(mWidth, PixelUtil.dp2px(100)));
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        PublicWay.num = 6;
        Bimp.tempSelectBitmap.clear();
        CrashApplication.getInstance().removeActivity(this);
        super.onDestroy();
    }

}
