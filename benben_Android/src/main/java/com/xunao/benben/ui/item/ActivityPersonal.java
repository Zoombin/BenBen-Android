package com.xunao.benben.ui.item;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageTask;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.User;
import com.xunao.benben.config.AndroidConfig;
import com.xunao.benben.dialog.BirthDialog;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.dialog.LodingDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.ui.ActivityLogin;
import com.xunao.benben.ui.ActivityWeb;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.Core;
import com.xunao.benben.utils.CutImageUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.ActionSheet;
import com.xunao.benben.view.ActionSheet.ActionSheetListener;

public class ActivityPersonal extends BaseActivity implements OnClickListener,
		ActionSheetListener {
	private String imagePath;
	private String imageName;

	private static final int PIC_REQUEST_CODE_WITH_DATA = 1; // 标识获取图片数据
	private static final int PIC_REQUEST_CODE_SELECT_CAMERA = 2; // 标识请求照相功能的activity
	private static final int PIC_Select_CODE_ImageFromLoacal = 3;// 标识请求相册取图功能的activity
	private static final int UPDATE_NICK_NAME = 4; // 标识获取图片数据
	private static final int UPDATE_REAL_NAME = 5; // 标识获取图片数据
	private static final int CHOICE_ADDRESS = 6; // 标识获取图片数据
	private static final int UPDATE_DETAIL_ADDRESS = 7; // 标识获取图片数据

	private LodingDialog lodingDialog;
	protected boolean isShowLoding = true;

	private Uri uri;

	private RelativeLayout rl_sex;
	private RelativeLayout rl_nick_name;
	private RelativeLayout rl_area;
	private RelativeLayout rl_age;
	protected String age;

	private TextView tv_name;
	private RoundedImageView iv_poster;
	private TextView tv_nick_name;
	private TextView tv_benben_id;
	private TextView tv_phone;
	private RoundedImageView iv_two_code;
	private TextView tv_integral;
	private TextView tv_coin;
	private TextView tv_bxphone;
	private TextView tv_otherphone;
	private TextView tv_real_name;
	private TextView tv_sex;
	private TextView tv_age;
	private TextView tv_area;
	private TextView tv_address;
	private RelativeLayout rl_real_name;
	private RelativeLayout rl_address;
	private RelativeLayout rl_two_code;
    private RelativeLayout rl_integral;

	private int changeWhat = 1;
	private String[] addressId;
	private String addressname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		cubeimageLoader.setImageLoadHandler(new DefaultImageLoadHandler(
				mContext) {
			@Override
			public void onLoading(ImageTask imageTask,
					CubeImageView cubeImageView) {
				if (cubeImageView != null) {
					String tag = (String) cubeImageView.getTag(R.string.type);
					if ("group".equals(tag)) {
						cubeImageView
								.setImageResource(R.drawable.ic_group_poster);
					} else if ("twocode".equals(tag)) {
					} else {
						cubeImageView.setImageResource(R.drawable.default_face);
					}

				}

			}

			@Override
			public void onLoadFinish(ImageTask imageTask,
					CubeImageView cubeImageView, BitmapDrawable drawable) {
				if (cubeImageView != null) {
					if (imageTask.getIdentityUrl().equalsIgnoreCase(
							(String) cubeImageView.getTag())) {
						cubeImageView.setVisibility(View.VISIBLE);
						cubeImageView.setImageDrawable(drawable);
					}
				}
			}

			@Override
			public void onLoadError(ImageTask imageTask,
					CubeImageView imageView, int errorCode) {
				if (imageView != null) {
					String tag = (String) imageView.getTag(R.string.type);
					if ("group".equals(tag)) {
						imageView.setImageResource(R.drawable.ic_group_poster);
					} else if ("twocode".equals(tag)) {
					} else {
						imageView.setImageResource(R.drawable.default_face);
					}
				}
			}
		});

	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_personal_information);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("个人信息", "", "",
				R.drawable.icon_com_title_left, 0);
		// InteNetUtils.getInstance(mContext).memberInfo(mRequestCallBack);
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		rl_sex = (RelativeLayout) findViewById(R.id.rl_sex);
		rl_nick_name = (RelativeLayout) findViewById(R.id.rl_nick_name);

		// tv_name = (TextView) findViewById(R.id.tv_name);
		iv_poster = (RoundedImageView) findViewById(R.id.iv_poster);
		tv_nick_name = (TextView) findViewById(R.id.tv_nick_name);
		tv_benben_id = (TextView) findViewById(R.id.tv_benben_id);
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		iv_two_code = (RoundedImageView) findViewById(R.id.iv_two_code);
		tv_integral = (TextView) findViewById(R.id.tv_integral);
		tv_coin = (TextView) findViewById(R.id.tv_coin);
		tv_bxphone = (TextView) findViewById(R.id.tv_bxphone);
		tv_otherphone = (TextView) findViewById(R.id.tv_otherphone);
		tv_real_name = (TextView) findViewById(R.id.tv_real_name);
		tv_sex = (TextView) findViewById(R.id.tv_sex);
		tv_age = (TextView) findViewById(R.id.tv_age);
		tv_area = (TextView) findViewById(R.id.tv_area);
		tv_address = (TextView) findViewById(R.id.tv_address);
		rl_real_name = (RelativeLayout) findViewById(R.id.rl_real_name);
		rl_area = (RelativeLayout) findViewById(R.id.rl_area);
		rl_address = (RelativeLayout) findViewById(R.id.rl_address);
		rl_age = (RelativeLayout) findViewById(R.id.rl_age);
		rl_two_code = (RelativeLayout) findViewById(R.id.rl_two_code);

		iv_poster.setOnClickListener(this);
		rl_sex.setOnClickListener(this);
		rl_nick_name.setOnClickListener(this);
		rl_real_name.setOnClickListener(this);
		rl_area.setOnClickListener(this);
		rl_address.setOnClickListener(this);
		rl_age.setOnClickListener(this);
		rl_two_code.setOnClickListener(this);

		tv_nick_name.setText(user.getUserNickname());
		tv_benben_id.setText(user.getBenbenId());
		tv_phone.setText(hidePhone(user.getPhone()));
		tv_integral.setText(CommonUtils.isEmpty(user.getIntegral()) ? "0"
				: user.getIntegral());
		
		if(CommonUtils.isEmpty(user.getIntegral())){
			tv_coin.setText("0");
		}else{
			tv_coin.setText(Integer.parseInt(user.getIntegral())/100 +"");
		}
		
		tv_bxphone.setText(user.getBaiXing());
		tv_otherphone.setText(CommonUtils.isEmpty(user.getCornet()) ? "0"
				: user.getCornet());
		tv_real_name.setText(CommonUtils.isEmpty(user.getName()) ? "" : user
				.getName());
		try {
			tv_age.setText(CommonUtils.getAge(mContext,
					CommonUtils.changeLongDateToDate(user.getBirthday())));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		tv_sex.setText(user.getUserSex().equals("1") ? "男" : user.getUserSex()
				.equals("0") ? "未知" : "女");
		tv_area.setText(user.getProCity());
		tv_address.setText(user.getAddress());
		CommonUtils.startImageLoader(cubeimageLoader, user.getPoster(),
				iv_poster);
		iv_two_code.setTag(R.string.type, "twocode");
		CommonUtils.startImageLoader(cubeimageLoader, user.getUserQrCode(),
				iv_two_code);
		
		if(CommonUtils.isNetworkAvailable(mContext)){
			InteNetUtils.getInstance(mContext).memberInfo(mRequestCallBack);
		}
        rl_integral = (RelativeLayout) findViewById(R.id.rl_integral);
        rl_integral.setOnClickListener(this);
		
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

	@Override
	protected void onHttpStart() {

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		String ret_num = jsonObject.optString("ret_num");
		
		if("0".equals(ret_num)){
			User userInfo = new User();
			try {
				userInfo = userInfo.parseJSON(jsonObject);
				if(userInfo != null){
					tv_bxphone.setText(userInfo.getBaiXing());
					tv_address.setText(userInfo.getAddress());
					tv_area.setText(userInfo.getProCity());
				}
			} catch (NetRequestException e) {
				e.printStackTrace();
			}
		}else{
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
				ToastUtils.Errortoast(mContext, jsonObject.optString("ret_msg"));
				
			}
		}
		
	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Errortoast(mContext, "网络不可用");
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_poster:
			changeImage("拍摄新图片", "从相册选择");
			changeWhat = 1;
			break;
		case R.id.rl_age:
			changeWhat = 5;
			choseDate();
			break;
		case R.id.rl_sex:
			changeImage("男", "女");
			changeWhat = 2;
			break;
		case R.id.rl_nick_name:
			startAnimActivityForResult2(ActivityUpdatNickName.class,
					UPDATE_NICK_NAME, "nick_name", tv_nick_name.getText()
							.toString());
			break;
		case R.id.rl_real_name:
			startAnimActivityForResult2(ActivityUpdateRealName.class,
					UPDATE_REAL_NAME, "name", tv_real_name.getText().toString());
			break;
		case R.id.rl_area:
			startAnimActivityForResult2(ActivityChoiceAddress.class,
					CHOICE_ADDRESS, "level", "3");
			changeWhat = 3;
			break;
		case R.id.rl_address:
			startAnimActivityForResult2(ActivityUpdateAddress.class,
					UPDATE_DETAIL_ADDRESS, "address", tv_address.getText()
							.toString());
			break;
		case R.id.rl_two_code:
            InteNetUtils.getInstance(mContext).Qr12(user.getBenbenId(), new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                    try {
                        JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                        if(jsonObject.optInt("ret_num")==0){
                            String code = jsonObject.optString("UserQrcode");
                            CommonUtils.showQrCode(mContext, code,
                                    cubeimageLoader);
                        }else{
                            ToastUtils.Errortoast(mContext,jsonObject.optString("ret_msg"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    ToastUtils.Errortoast(mContext,"加载二维码失败!");
                }
            });
			break;
            case R.id.rl_integral:
                Intent intent = new Intent(this, ActivityWeb.class);
                intent.putExtra("title", "积分说明");
                intent.putExtra("url", AndroidConfig.NETHOST3 + AndroidConfig.Setting + "key/android/type/6");

                startActivity(intent);
                break;
		}
	}

	// 显示拍照选照片 弹窗
	private void changeImage(String name, String name2) {
		setTheme(R.style.ActionSheetStyleIOS7);
		showActionSheet(name, name2);
	}

	public void showActionSheet(String name, String name2) {
		ActionSheet.createBuilder(this, getSupportFragmentManager())
				.setCancelButtonTitle("取消")
				.setOtherButtonTitles(name, name2, "查看大头像")
				// 设置颜色 必须一一对应
				.setOtherButtonTitlesColor("#1E82FF", "#1E82FF")
				.setCancelableOnTouchOutside(true).setListener(this).show();
	}

	// 隐藏电话号码中间
	private String hidePhone(String phone) {
		String newPhone = phone.substring(0, 3) + "****"
				+ phone.substring(7, 11);
		return newPhone;
	}

	@Override
	public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
	}

	@Override
	public void onOtherButtonClick(ActionSheet actionSheet, int index) {

		if (changeWhat == 1) {
			switch (index) {
			case 0:
				imageName = getPhotoFileName();
				Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent2.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(CommonUtils.getImagePath(mContext,
								imageName)));
				startActivityForResult(intent2, PIC_REQUEST_CODE_SELECT_CAMERA);
				break;
			case 1:
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent, PIC_Select_CODE_ImageFromLoacal);
				break;
			case 2:
				CommonUtils.showPoster(mContext, user.getPoster(),
						cubeimageLoader);
				break;
			default:
				break;
			}
		} else if (changeWhat == 2) {
			switch (index) {
			case 0:
				if (user.getUserSex().equals("1")) {
					ToastUtils.Infotoast(mContext, "目前您的性别已是男性!");
					return;
				} else {
					if (CommonUtils.isNetworkAvailable(mContext))
						InteNetUtils.getInstance(mContext).updateMemberInfo(
								"1", "", "", "", requestCallBack);
				}
				break;
			case 1:
				if (user.getUserSex().equals("2")) {
					ToastUtils.Infotoast(mContext, "目前您的性别已是女性!");
					return;
				} else {
					if (CommonUtils.isNetworkAvailable(mContext))
						InteNetUtils.getInstance(mContext).updateMemberInfo(
								"2", "", "", "", requestCallBack);
				}
				break;
			default:
				if (CommonUtils.isNetworkAvailable(mContext))
					InteNetUtils.getInstance(mContext).updateMemberInfo("0",
							"", "", "", requestCallBack);
				break;
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case UPDATE_NICK_NAME:
			if (data != null) {
				String nick_name = data.getStringExtra("nick_name");
				tv_nick_name.setText(nick_name);
			}
			break;
		case UPDATE_REAL_NAME:
			if (data != null) {
				tv_real_name.setText(data.getStringExtra("name"));
			}
			break;
		case UPDATE_DETAIL_ADDRESS:
			if (data != null) {
				tv_address.setText(data.getStringExtra("address"));
			}
			break;
		case CHOICE_ADDRESS:
			if (resultCode == AndroidConfig.ChoiceAddressResultCode) {
				addressname = data.getStringExtra("address");
				addressId = null;
				addressId = data.getStringArrayExtra("addressId");
				// changeWhat = 3;
				InteNetUtils.getInstance(mContext).updateArea(addressId,
						requestCallBack);
			}
			break;
		case PIC_REQUEST_CODE_WITH_DATA:
			if (data != null) {
				uri = data.getData();
				String imageNames = data.getStringExtra("imageName");
				File files = CommonUtils.getImagePath(mContext, imageNames);

				InteNetUtils.getInstance(mContext).updateFace(files,
						requestCallBack);

			}
			break;
		case PIC_REQUEST_CODE_SELECT_CAMERA:
			File temp = CommonUtils.getImagePath(mContext, imageName);
			Intent intent = new Intent(this, ActivityCutImage.class);
			intent.putExtra("imagePath", temp.getAbsolutePath());
			startActivityForResult(intent, PIC_REQUEST_CODE_WITH_DATA);

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
						} else {
							ToastUtils.Infotoast(mContext, "请选择正确的图像资源");
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					Intent intent2 = new Intent(this, ActivityCutImage.class);
					intent2.putExtra("imagePath", imagePath);

					startActivityForResult(intent2, PIC_REQUEST_CODE_WITH_DATA);
				}
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private RequestCallBack<String> requestCallBack = new RequestCallBack<String>() {
		public void onStart() {
			if (isShowLoding) {
				if (lodingDialog == null) {
					lodingDialog = new LodingDialog(mContext);
				}
				lodingDialog.show();
			}
		};

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			if (lodingDialog != null && lodingDialog.isShowing()) {
				lodingDialog.dismiss();
			}

			switch (changeWhat) {
			case 1:
				ToastUtils.Infotoast(mContext, "修改头像失败!");
				break;
			case 2:
				ToastUtils.Infotoast(mContext, "修改性别失败!");
				break;
			case 3:
				ToastUtils.Infotoast(mContext, "修改所在地区失败!");
				break;
			case 5:
				ToastUtils.Infotoast(mContext, "修改年龄失败!");
				break;
			}

		}

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			if (lodingDialog != null && lodingDialog.isShowing()) {
				lodingDialog.dismiss();
			}

			String result = arg0.result;
			try {
				JSONObject jsonObject = new JSONObject(result);
				String ret_num = jsonObject.optString("ret_num");

				if ("0".equals(ret_num)) {
					switch (changeWhat) {
					case 1:
						ToastUtils.Infotoast(mContext, "修改头像成功!");
						BitmapFactory.Options options = new Options();
						options.inJustDecodeBounds = false;
						Bitmap bitmap = CutImageUtils.getBitMap(mContext, uri,
								options, iv_poster);
						iv_poster.setImageBitmap(bitmap);
						try {
							user.setPoster(user.parseJSONPoster(jsonObject));
							sendBroadcast(new Intent(
									AndroidConfig.refrashMyFragment));
						} catch (NetRequestException e1) {
							e1.printStackTrace();
						}
						break;
					case 2:
						User users;
						try {
							users = user.parseJSON(jsonObject);
							String sex = users.getUserSex();
							ToastUtils.Infotoast(mContext, "修改性别成功!");
							tv_sex.setText(sex.equals("1") ? "男" : sex
									.equals("0") ? "未知" : "女");
							user.setUserSex(sex);
						} catch (NetRequestException e) {
							e.printStackTrace();
							ToastUtils.Infotoast(mContext, e.getError()
									.toString());
						}
						break;
					case 3:
						ToastUtils.Infotoast(mContext, "修改所在地区成功!");
						tv_area.setText(addressname);
						tv_area.setTextColor(Color.parseColor("#000000"));
						break;
					case 5:
						try {
							ToastUtils.Infotoast(mContext, "修改年龄成功!");
							users = user.parseJSON(jsonObject);

							try {
								tv_age.setText(CommonUtils.getAge(mContext,
										CommonUtils.changeLongDateToDate(users
												.getBirthday() * 1000)));
								user.setBirthday(users.getBirthday());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						} catch (NetRequestException e) {
							e.printStackTrace();
						}

						break;
					}

				} else {
					switch (changeWhat) {
					case 1:
						ToastUtils.Infotoast(mContext, "修改头像成功!");
						break;
					case 2:
						ToastUtils.Infotoast(mContext, "修改性别成功!");
						break;
					case 3:
						ToastUtils.Infotoast(mContext, "修改所在地区成功!");
						break;
					case 5:
						ToastUtils.Infotoast(mContext, "修改年龄成功!");
						break;
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				switch (changeWhat) {
				case 1:
					ToastUtils.Infotoast(mContext, "修改头像失败");
					break;
				case 2:
					ToastUtils.Infotoast(mContext, "修改性别失败!");
					break;
				case 3:
					ToastUtils.Infotoast(mContext, "修改所在地区失败!");
					break;
				case 5:
					ToastUtils.Infotoast(mContext, "修改年龄失败!");
					break;
				}
			}

		}
	};

	private String lastTime;

	// 选择生日
	private void choseDate() {
		final BirthDialog dialog = new BirthDialog(mContext, lastTime);
		Window window = dialog.getWindow();
		window.setLayout(mScreenWidth - PixelUtil.dp2px(10),
				LayoutParams.WRAP_CONTENT);
		window.setGravity(Gravity.BOTTOM);

		dialog.setOKListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				switch (arg0.getId()) {
				case R.id.chose_position:
					if (CommonUtils.isNetworkAvailable(mContext)) {
						try {
							SimpleDateFormat myFormatter = new SimpleDateFormat(
									"yyyy-MM-dd");
							Date date = myFormatter.parse(dialog.getWheelMain());
							lastTime = dialog.getWheelMain();
							InteNetUtils.getInstance(mContext)
									.updateBirthday(date.getTime() / 1000 + "",
											requestCallBack);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						dialog.dismiss();
					}
					break;
				default:
					break;
				}
			}
		});

		dialog.setCancleListener(new OnClickListener() {
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

}
