package com.xunao.benben.ui.item;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.User;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.dialog.LodingDialog;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityLogin;
import com.xunao.benben.ui.ActivityWeb;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.CutImageUtils;
import com.xunao.benben.utils.RegexUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.ActionSheet.ActionSheetListener;

public class ActivityApplyBaiXing extends BaseActivity implements
		OnClickListener, ActionSheetListener {
	private LinearLayout ll_add_card1;
	private LinearLayout ll_add_card2;
	private RelativeLayout rl_area;
	private TextView tv_area;
    private RelativeLayout rl_baixin;
    private TextView tv_baixin;
	private LinearLayout ll_iv1;
	private LinearLayout ll_iv2;
	private EditText tv_user_phone;
	private TextView register_point;
	private String[] addressId = {"0","0","0","0"};
	private EditText et_name;
	private EditText et_cardnum;
	private RoundedImageView iv_idcard1;
	private RoundedImageView iv_idcard2;
	private boolean idcardUpdate = false;
	private boolean checkImageBtn = false;
	private String imageName;
	private String imagePath;
	private Intent intent;
	private Bitmap bitMap;
	private File file1;
	private File file2;
	private LodingDialog lodingDialog;
	private static final int PIC_REQUEST_CODE_SELECT_CAMERA = 1; // 从相机
	private static final int PIC_Select_CODE_ImageFromLoacal = 2; // 从相册
	private static final int CHOCE_ADDRESS = 3;
    private static final int CHOCE_BAIXIN = 4;
	private View loding;
    private int enterprise_id=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setShowLoding(false);
		setContentView(R.layout.activity_apply_baixing);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("加入百姓网", "", "提交",
				R.drawable.icon_com_title_left, 0);
		tv_user_phone = (EditText) findViewById(R.id.tv_user_phone);
		et_name = (EditText) findViewById(R.id.et_name);
		loding = findViewById(R.id.loding);
		et_cardnum = (EditText) findViewById(R.id.et_cardnum);
		ll_add_card1 = (LinearLayout) findViewById(R.id.ll_add_card1);
		ll_add_card2 = (LinearLayout) findViewById(R.id.ll_add_card2);
		iv_idcard1 = (RoundedImageView) findViewById(R.id.iv_idcard1);
		iv_idcard2 = (RoundedImageView) findViewById(R.id.iv_idcard2);
		register_point = (TextView) findViewById(R.id.register_point);
		ll_iv1 = (LinearLayout) findViewById(R.id.ll_iv1);
		ll_iv2 = (LinearLayout) findViewById(R.id.ll_iv2);
		rl_area = (RelativeLayout) findViewById(R.id.rl_area);
		tv_area = (TextView) findViewById(R.id.tv_area);
        rl_baixin = (RelativeLayout) findViewById(R.id.rl_baixin);
        tv_baixin = (TextView) findViewById(R.id.tv_baixin);

		ll_add_card1.setOnClickListener(this);
		ll_add_card2.setOnClickListener(this);
		iv_idcard1.setOnClickListener(this);
		iv_idcard2.setOnClickListener(this);

		register_point.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startAnimActivity2Obj(ActivityWeb.class, "url",
						AndroidConfig.NETHOST3 + AndroidConfig.Setting
								+ "key/android/type/2", "title", "百姓网入网声明");
			}
		});
		loding.setVisibility(View.GONE);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		tv_user_phone.setText(user.getPhone());

		// 修改部分自体颜色
		SpannableStringBuilder builder = new SpannableStringBuilder(
				register_point.getText().toString());
		ForegroundColorSpan greenSpan = new ForegroundColorSpan(
				Color.parseColor("#3b96ca"));
		builder.setSpan(greenSpan, 11, 20, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		register_point.setText(builder);
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		rl_area.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startAnimActivityForResult2(ActivityChoiceAddress.class,
						CHOCE_ADDRESS, "level", "3");
			}
		});

        rl_baixin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startAnimActivityForResult(ActivityChoiceBaiXin.class,
                        CHOCE_BAIXIN);
            }
        });

		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AnimFinsh();
			}
		});

		setOnRightClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String phone = tv_user_phone.getText().toString().trim();
				String name = et_name.getText().toString().trim();
				String cardNum = et_cardnum.getText().toString().trim();
				String area = tv_area.getText().toString().trim();

				if (TextUtils.isEmpty(phone)) {
					ToastUtils.Infotoast(mContext, "手机号不能为空！");
					return;
				} else {
					if (!RegexUtils.checkMobile(phone)) {
						ToastUtils.Infotoast(mContext, "手机号格式不正确！");
						return;
					}
				}

				if (TextUtils.isEmpty(name)) {
					ToastUtils.Infotoast(mContext, "真实姓名不能为空！");
					return;
				}

				et_name.setText(Html.fromHtml(RegexUtils.minganciCheck2(et_name
						.getText().toString())));
				if (RegexUtils.minganciCheck3(et_name.getText().toString())) {
					et_name.setText(Html.fromHtml(RegexUtils
							.minganciCheck2(et_name.getText().toString())));
					ToastUtils.Errortoast(mContext, "请先修改红色非法文字");
					return;
				}

				if (TextUtils.isEmpty(cardNum)) {
					ToastUtils.Infotoast(mContext, "身份证号不能为空！");
					return;
				} else {
					if (!RegexUtils.checkIdCard(cardNum)) {
						ToastUtils.Infotoast(mContext, "身份证号格式不正确！");
						return;
					}
				}

//				if (area.equals("请选择所在地区")) {
//					ToastUtils.Infotoast(mContext, "请选择所在地区！");
//					return;
//				}

                if(enterprise_id==0){
                    ToastUtils.Infotoast(mContext, "请选择加入的百姓网！");
                    return;
                }

				if (file1 == null || file2 == null) {
					ToastUtils.Infotoast(mContext, "请上传身份证正反面照片");
					return;
				}
				if (CommonUtils.isNetworkAvailable(mContext))
					showLoding("请稍后...");


				InteNetUtils.getInstance(mContext).enterBaixing(name, phone,
						cardNum, file1, file2, addressId, enterprise_id+"",mRequestCallBack);

			}
		});

	}

	@Override
	protected void onHttpStart() {
		if (isShowLoding) {
			if (lodingDialog == null) {
				lodingDialog = new LodingDialog(mContext);
			}
			lodingDialog.show();
		}
	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		dissLoding();
		String ret_num = jsonObject.optString("ret_num");
		if ("0".equals(ret_num)) {
			final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(mContext,
					R.style.MyDialog1);
			hint.setContent("我们已收到您的申请，将会尽快处理!");
			hint.show();
			hint.setOKListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					hint.dismiss();
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							AnimFinsh();
						}
					}, 500);
				}
			});
			switch (user.getUserInfo()) {
			case "0":
				user.setUserInfo("2");
				break;
			case "1":
				user.setUserInfo("3");
				break;
			}
		} else {

			if (jsonObject.optString("ret_num").equals("2015")) {
				final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(mContext,
						R.style.MyDialog1);
				hint.setContent("奔犇账号在其他手机登录");
				hint.setBtnContent("确定");
				hint.show();
				hint.setOKListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						hint.dismiss();
					}
				});
				CrashApplication.getInstance().logout();
				startActivity(new Intent(mContext, ActivityLogin.class));
			} else {
				ToastUtils.Infotoast(mContext, jsonObject.optString("ret_msg"));
			}
		}

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		dissLoding();
		ToastUtils.Infotoast(mContext, "提交百姓网申请失败！");
		return;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.ll_add_card1:
			changeImage();
			break;
		case R.id.ll_add_card2:
			changeImage();
			break;
		case R.id.iv_idcard1:
			changeImage();
			idcardUpdate = false;
			break;
		case R.id.iv_idcard2:
			changeImage();
			break;
		}
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
			File temp = CommonUtils.getImagePath(mContext, imageName);

			imagePath = temp.getAbsolutePath();
			if (!idcardUpdate) {
				if (getBitmap(imagePath) != null) {
					file1 = CommonUtils.getImagePath(mContext, imageName);
					iv_idcard1.setImageBitmap(getBitmap(imagePath));
					iv_idcard1.setVisibility(View.VISIBLE);
					ll_iv1.setVisibility(View.VISIBLE);
					ll_add_card1.setVisibility(View.GONE);
					if (checkImageBtn) {
						ll_add_card2.setVisibility(View.GONE);
					} else {
						ll_add_card2.setVisibility(View.VISIBLE);
					}
					idcardUpdate = true;
				}
			} else {
				if (getBitmap(imagePath) != null) {
					file2 = CommonUtils.getImagePath(mContext, imageName);
					iv_idcard2.setImageBitmap(getBitmap(imagePath));
					iv_idcard2.setVisibility(View.VISIBLE);
					ll_iv2.setVisibility(View.VISIBLE);
					ll_add_card2.setVisibility(View.GONE);
					checkImageBtn = true;
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
							if (!idcardUpdate) {
								file1 = new File(imagePath);
								iv_idcard1.setImageBitmap(getBitmap(imagePath));
								iv_idcard1.setVisibility(View.VISIBLE);
								ll_add_card1.setVisibility(View.GONE);
								ll_iv1.setVisibility(View.VISIBLE);
								if (checkImageBtn) {
									ll_add_card2.setVisibility(View.GONE);
								} else {
									ll_add_card2.setVisibility(View.VISIBLE);
								}

								idcardUpdate = true;
							} else {
								file2 = new File(imagePath);
								iv_idcard2.setImageBitmap(getBitmap(imagePath));
								iv_idcard2.setVisibility(View.VISIBLE);
								ll_add_card2.setVisibility(View.GONE);
								ll_iv2.setVisibility(View.VISIBLE);
								checkImageBtn = true;
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
		case CHOCE_ADDRESS:
			if (data != null) {
				if (resultCode == AndroidConfig.ChoiceAddressResultCode) {
					String addressname = data.getStringExtra("address");
					addressId = data.getStringArrayExtra("addressId");
					tv_area.setText(addressname);
					tv_area.setTextColor(Color.BLACK);
				}
			}

			break;
            case CHOCE_BAIXIN:

                if (data != null) {
                    if (resultCode == RESULT_OK) {
                        String name = data.getStringExtra("name");
                        enterprise_id = data.getIntExtra("enterprise_id",0);
                        tv_baixin.setText(name);
                        tv_baixin.setTextColor(Color.BLACK);
                    }
                }
                break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		if (bitMap != null) {
			bitMap.recycle();
		}
		super.onDestroy();
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
