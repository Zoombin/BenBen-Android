package com.xunao.benben.ui.item;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.BxApplyInfo;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityLogin;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.CutImageUtils;
import com.xunao.benben.utils.RegexUtils;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.ActionSheet.ActionSheetListener;
import com.xunao.benben.view.MyTextView;

public class ActivityApplyBaixingDetail extends BaseActivity implements
		OnClickListener, ActionSheetListener {
	private RelativeLayout rl_status;
	private LinearLayout ll_add_card1;
	private RelativeLayout rl_area,rl_baixin;
	private MyTextView register_point;
	private TextView tv_status;
	private EditText tv_user_phone;
	private EditText et_name;
	private EditText et_cardnum;
	private TextView tv_area;
    private TextView tv_baixin;
	private RoundedImageView iv_idcard1;
	private RoundedImageView iv_idcard2;
	private LinearLayout ll_iv1;
	private LinearLayout ll_iv2;

	private EditText tv_reason;

	private BxApplyInfo bxApplyInfo;
	private boolean isClick = false;
	private static final int PIC_REQUEST_CODE_SELECT_CAMERA = 1; // 从相机
	private static final int PIC_Select_CODE_ImageFromLoacal = 2; // 从相册
	private static final int CHOCE_ADDRESS = 3;
    private static final int CHOCE_BAIXIN = 4;
	private int uploadImage = 0;
	private String imageName;
	private String imagePath;
	private Intent intent;
	private Bitmap bitMap;
	private File file1;
	private File file2;
    private String[] addressId = {"0","0","0","0"};
	private RelativeLayout rl_reason;
	private View line;
	private int reasonNum = 0;
	private boolean mingan = false;
	private boolean isFirst = false;
    private int enterprise_id=0;

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setShowLoding(false);
		initdefaultImage(R.drawable.empty_photo);
		setContentView(R.layout.activity_apply_baixing);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("百姓网申请详情", "", "",
				R.drawable.icon_com_title_left, 0);
		phoneNum = getIntent().getStringExtra("PHONE");
		ID = getIntent().getStringExtra("ID");
		if (CommonUtils.isEmpty(phoneNum)) {
			phoneNum = user.getPhone();
		}
		if (CommonUtils.isEmpty(ID)) {
			InteNetUtils.getInstance(mContext).applyBxInfo(mRequestCallBack);
		} else {
			InteNetUtils.getInstance(mContext).applyBxInfowithId(ID,
					mRequestCallBack);
		}

	}

	@Override
	public void initDate(Bundle savedInstanceState) {

		loding = findViewById(R.id.loding);
		rl_status = (RelativeLayout) findViewById(R.id.rl_status);
		tv_status = (TextView) findViewById(R.id.tv_status);

		rl_shortPhone = (RelativeLayout) findViewById(R.id.rl_shortPhone);
		tv_shortPhone = (TextView) findViewById(R.id.tv_shortPhone);
		line_short = findViewById(R.id.line_short);

		tv_user_phone = (EditText) findViewById(R.id.tv_user_phone);
		et_name = (EditText) findViewById(R.id.et_name);
		et_cardnum = (EditText) findViewById(R.id.et_cardnum);
		cardid_rl = findViewById(R.id.cardid_rl);
		cardid_line = findViewById(R.id.cardid_line);
		tv_area = (TextView) findViewById(R.id.tv_area);
        tv_baixin = (TextView) findViewById(R.id.tv_baixin);
		iv_idcard1 = (RoundedImageView) findViewById(R.id.iv_idcard1);
		iv_idcard2 = (RoundedImageView) findViewById(R.id.iv_idcard2);
		ll_iv1 = (LinearLayout) findViewById(R.id.ll_iv1);
		ll_iv2 = (LinearLayout) findViewById(R.id.ll_iv2);
		rl_area = (RelativeLayout) findViewById(R.id.rl_area);
        rl_baixin = (RelativeLayout) findViewById(R.id.rl_baixin);
		rl_reason = (RelativeLayout) findViewById(R.id.rl_reason);
		line = findViewById(R.id.line);

		tv_reason = (EditText) findViewById(R.id.tv_reason);

		line.setVisibility(View.GONE);

		ll_add_card1 = (LinearLayout) findViewById(R.id.ll_add_card1);
		register_point = (MyTextView) findViewById(R.id.register_point);
		rl_status.setVisibility(View.VISIBLE);
		ll_add_card1.setVisibility(View.GONE);
		register_point.setVisibility(View.GONE);

		et_name.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
				| InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

		TextWatcher watcher = new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
//				if (isFirst) {
//					et_name.setText(Html.fromHtml(RegexUtils
//							.minganciCheck2(et_name.getText().toString())));
//				}
			}
		};
		et_name.addTextChangedListener(watcher);
	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AnimFinsh();
			}
		});

	}

	private RequestCallBack<String> requestCallBack = new RequestCallBack<String>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			dissLoding();
			ToastUtils.Infotoast(mContext, "申请加入百姓网失败!");
		}

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			dissLoding();
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(arg0.result);
				String ret_num = jsonObject.optString("ret_num");

				if (ret_num.equals("0")) {
					sendBroadcast(new Intent(
							AndroidConfig.ACTIVITYPROGRESSOFBXREFRESH));
					final InfoSimpleMsgHint hint = new InfoSimpleMsgHint(
							mContext, R.style.MyDialog1);
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
						ToastUtils.Infotoast(mContext,
								jsonObject.optString("ret_msg"));
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};
	private String phoneNum;
	private String ID;
	private View cardid_rl;
	private View cardid_line;
	private RelativeLayout rl_shortPhone;
	private TextView tv_shortPhone;
	private View line_short;
	private View loding;

	@Override
	protected void onHttpStart() {

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {

	}

	private MsgDialog inputDialog;

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		String ret_num = jsonObject.optString("ret_num");
		if (ret_num.equals("0")) {

			try {
				bxApplyInfo = new BxApplyInfo();
				JSONObject object = jsonObject.optJSONObject("info");
				bxApplyInfo.parseJSON(object);

				// 0为默认等待审核，1未通过，2退回重申，3已经通过
				switch (bxApplyInfo.getStatus()) {
				case 0:
					tv_status.setText("等待审核");
					if (CommonUtils.isEmpty(bxApplyInfo.getIdCard())) {
						cardid_rl.setVisibility(View.GONE);
						cardid_line.setVisibility(View.GONE);
					}
					break;
				case 1:
//					tv_user_phone.setFocusable(false);
//					et_name.setFocusable(false);
//					et_cardnum.setFocusable(false);
//					tv_status.setText("未通过");
//					if (CommonUtils.isEmpty(bxApplyInfo.getIdCard())) {
//						cardid_rl.setVisibility(View.GONE);
//						cardid_line.setVisibility(View.GONE);
//					}
                    reasonNum = 2;
                    tv_status.setText("未通过");
					break;
				case 2:
					reasonNum = 2;
					tv_status.setText("退回重申");

					break;
				case 3:
					tv_status.setText("已经通过");
					rl_shortPhone.setVisibility(View.VISIBLE);
					line_short.setVisibility(View.VISIBLE);
					tv_shortPhone.setText(bxApplyInfo.getShort_phone());
					break;
				case 4:
					tv_status.setText("已撤销");
					break;
				}

				rl_reason.setVisibility(View.VISIBLE);
				line.setVisibility(View.VISIBLE);

				tv_status.setTextColor(Color.BLACK);

				tv_user_phone.setText(bxApplyInfo.getPhone());
				et_name.setText(bxApplyInfo.getName());
				isFirst = true;
				et_cardnum.setText(bxApplyInfo.getIdCard());
				tv_area.setText(bxApplyInfo.getArea());
				tv_area.setTextColor(Color.BLACK);
                tv_baixin.setText(bxApplyInfo.getBx_name());
                tv_baixin.setTextColor(Color.BLACK);
                enterprise_id = bxApplyInfo.getEnterprise_id();

				if (!CommonUtils.isEmpty(bxApplyInfo.getReason())) {
					rl_reason.setVisibility(View.VISIBLE);
					tv_reason.setText(bxApplyInfo.getReason());
					tv_reason.setTextColor(Color.BLACK);
					line.setVisibility(View.VISIBLE);
				} else {
					line.setVisibility(View.GONE);
					rl_reason.setVisibility(View.GONE);
				}

				tv_user_phone.setKeyListener(null);

				if (bxApplyInfo.getStatus() != 2 && bxApplyInfo.getStatus() != 1) {
					et_name.setKeyListener(null);
					et_cardnum.setKeyListener(null);
					tv_area.setKeyListener(null);

					ll_iv1.setVisibility(View.GONE);
					ll_iv2.setVisibility(View.GONE);

					if (!user.getPhone().equals(phoneNum)) {
						cardid_rl.setVisibility(View.GONE);
						cardid_line.setVisibility(View.GONE);
                        rl_baixin.setVisibility(View.GONE);
					}

				} else {
					if (user.getPhone().equals(phoneNum)) {
						iv_idcard1.setVisibility(View.VISIBLE);
						iv_idcard2.setVisibility(View.VISIBLE);
						ll_iv1.setVisibility(View.VISIBLE);
						ll_iv2.setVisibility(View.VISIBLE);
						cardid_rl.setVisibility(View.VISIBLE);
						cardid_line.setVisibility(View.VISIBLE);
                        rl_baixin.setVisibility(View.VISIBLE);

                        if(bxApplyInfo.getPoster()!=null && !bxApplyInfo.getPoster().equals("")) {
                            CommonUtils.startImageLoader(cubeimageLoader,
                                    bxApplyInfo.getPoster(), iv_idcard1);
                        }
                        if(bxApplyInfo.getPoster2()!=null && !bxApplyInfo.getPoster2().equals("")) {
                            CommonUtils.startImageLoader(cubeimageLoader,
                                    bxApplyInfo.getPoster2(), iv_idcard2);
                        }

						iv_idcard1.setOnClickListener(this);
						iv_idcard2.setOnClickListener(this);
					} else {
						iv_idcard1.setVisibility(View.GONE);
						iv_idcard2.setVisibility(View.GONE);
						ll_iv1.setVisibility(View.GONE);
						ll_iv2.setVisibility(View.GONE);
						cardid_rl.setVisibility(View.GONE);
						cardid_line.setVisibility(View.GONE);
                        rl_baixin.setVisibility(View.GONE);
					}

					initTitle_Right_Left_bar("百姓网申请详情", "", "提交",
							R.drawable.icon_com_title_left, 0);
					isClick = true;

					rl_area.setOnClickListener(this);
                    rl_baixin.setOnClickListener(this);

					setOnRightClickLinester(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							final String phone = phoneNum;
							final String name = et_name.getText().toString()
									.trim();
							final String cardNum = et_cardnum.getText()
									.toString().trim();
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

							if (user.getPhone().equals(phoneNum)) {
								if (TextUtils.isEmpty(cardNum)) {
									ToastUtils.Infotoast(mContext, "身份证号不能为空！");
									return;
								} else {
									if (!RegexUtils.checkIdCard(cardNum)) {
										ToastUtils.Infotoast(mContext,
												"身份证号格式不正确！");
										return;
									}
								}
							}
//							if (area.equals("请选择所在地区")) {
//								ToastUtils.Infotoast(mContext, "请选择所在地区！");
//								return;
//							}

                            if(enterprise_id==0){
                                ToastUtils.Infotoast(mContext, "请选择加入的百姓网！");
                                return;
                            }
							
							if(reasonNum == 2){
								et_name.setText(Html.fromHtml(RegexUtils.minganciCheck2(et_name.getText().toString())));
								if(RegexUtils.minganciCheck3(et_name.getText().toString())){
									et_name.setText(Html.fromHtml(RegexUtils.minganciCheck2(et_name.getText().toString())));
									ToastUtils.Errortoast(mContext, "请先修改红色非法文字");
									return;
								}
							}
							
							if (CommonUtils.isNetworkAvailable(mContext))

								if (reasonNum == 2) {
									
									inputDialog = new MsgDialog(mContext,
											R.style.MyDialogStyle);
									inputDialog.setContent("已修改好有误内容，确定提交", "",
											"确认", "取消");
									inputDialog
											.setCancleListener(new OnClickListener() {
												@Override
												public void onClick(View v) {
													inputDialog.dismiss();
												}
											});
									inputDialog
											.setOKListener(new OnClickListener() {
												@Override
												public void onClick(View v) {
													showLoding("请稍后");
													InteNetUtils
															.getInstance(
																	mContext)
															.editBaixing(name,
																	phone,
																	cardNum,
																	file1,
																	file2,
																	addressId,
                                                                    enterprise_id+"",
																	requestCallBack);
													inputDialog.dismiss();
												}
											});
									inputDialog.show();
								} else {
									showLoding("请稍后");
									InteNetUtils.getInstance(mContext)
											.editBaixing(name, phone, cardNum,
													file1, file2, addressId,enterprise_id+"",
													requestCallBack);
								}

						}
					});

				}

			} catch (NetRequestException e) {
				e.printStackTrace();
			}

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
				ToastUtils.Infotoast(mContext, jsonObject.optString("ret_msg"));
			}
		}
		loding.setVisibility(View.GONE);
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Infotoast(mContext, "网络不可用，请重试!");
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.rl_area:
			startAnimActivityForResult2(ActivityChoiceAddress.class,
					CHOCE_ADDRESS, "level", "3");
			break;
        case R.id.rl_baixin:
            startAnimActivityForResult(ActivityChoiceBaiXin.class,
                    CHOCE_BAIXIN);
            break;
		case R.id.iv_idcard1:
			uploadImage = 1;
			changeImage();
			break;
		case R.id.iv_idcard2:
			uploadImage = 2;
			changeImage();
			break;
		default:
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
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PIC_REQUEST_CODE_SELECT_CAMERA:
			File temp = CommonUtils.getImagePath(mContext, imageName);
			imagePath = temp.getAbsolutePath();

			if (uploadImage == 1) {
				file1 = CommonUtils.getImagePath(mContext, imageName);
				Bitmap bitmap2 = getBitmap(imagePath);
				if (bitmap2 != null)
					iv_idcard1.setImageBitmap(bitmap2);
			} else if (uploadImage == 2) {
				file2 = CommonUtils.getImagePath(mContext, imageName);
				Bitmap bitmap2 = getBitmap(imagePath);
				if (bitmap2 != null)
					iv_idcard2.setImageBitmap(bitmap2);
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

							if (uploadImage == 1) {
								file1 = new File(imagePath);
								if (file1.exists())
									iv_idcard1
											.setImageBitmap(getBitmap(imagePath));
							} else if (uploadImage == 2) {
								file2 = new File(imagePath);
								if (file1.exists())
									iv_idcard2
											.setImageBitmap(getBitmap(imagePath));
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

}
