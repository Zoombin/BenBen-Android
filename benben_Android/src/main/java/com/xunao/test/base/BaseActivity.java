package com.xunao.test.base;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.image.ImageTask;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.Type;
import com.easemob.util.EasyUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xunao.test.R;
import com.xunao.test.activity.MainActivity;
import com.xunao.test.base.IA.CrashApplication;
import com.xunao.test.bean.User;
import com.xunao.test.dialog.InfoMsgHint;
import com.xunao.test.dialog.InfoSimpleMsgHint;
import com.xunao.test.dialog.LodingDialog;
import com.xunao.test.ui.ActivityLogin;
import com.xunao.test.utils.CommonUtils;
import com.xunao.test.utils.SharePreferenceUtil;
import com.xunao.test.utils.ToastUtils;
import com.xunao.test.utils.XunaoLog;
import com.xunao.test.view.MyTextView;

public abstract class BaseActivity extends FragmentActivity {

	private static final int notifiId = 11;
	protected NotificationManager notificationManager;

	private GestureDetector gestureDetector;

	protected CrashApplication mApplication;
	private InputMethodManager manager;
	protected BaseActivity mContext;
	private Bitmap myBitmap;
	private XunaoLog yeLog;
	public int mScreenWidth;
	public int mScreenHeight;
	protected SharePreferenceUtil spUtil;
	protected DbUtils dbUtil;
	public LodingDialog lodingDialog;
	private File mCurrentPhotoFile;
	protected boolean isShowLoding = true;

	protected boolean isMoreData = true; // 是否有更多数据
	protected boolean enterNum = true;
	protected Handler mHandler = new Handler();

	protected InfoMsgHint hint;
	protected boolean isLoadMore;
	protected boolean isloadLock = false;
	public static User user;
	

	public in.srain.cube.image.ImageLoader getCubeimageLoader() {
		return cubeimageLoader;
	}

	public boolean isLoadMore() {
		return isLoadMore;
	}

	public void setLoadMore(boolean isLoadMore) {
		this.isLoadMore = isLoadMore;
	}

	public boolean isShowLoding() {
		return isShowLoding;
	}

	public void setShowLoding(boolean isShowLoding) {
		this.isShowLoding = isShowLoding;
	}

	public void showLoding(String content) {
		if (lodingDialog != null && lodingDialog.isShowing()
				&& !this.isFinishing()) {
			lodingDialog.dismiss();
			lodingDialog = null;
		}
		lodingDialog = new LodingDialog(mContext);
		lodingDialog.setContent(content);
		if (!this.isFinishing())
			lodingDialog.show();
	}

	public void dissLoding() {
		if (lodingDialog != null && !this.isFinishing()) {
			lodingDialog.dismiss();
		}
	}

	public RequestCallBack<String> mRequestCallBack = new RequestCallBack<String>() {

		public void onStart() {
			if (isShowLoding && !isFinishing()) {
				lodingDialog = new LodingDialog(mContext);
				lodingDialog.show();
			}
			BaseActivity.this.onHttpStart();
		};

		@Override
		public void onSuccess(ResponseInfo<String> mResponseInfo) {
			Header firstHeader = mResponseInfo.getFirstHeader("set-cookie");
			if (lodingDialog != null && isShowLoding
					&& lodingDialog.isShowing()
					&& !BaseActivity.this.isFinishing()) {
				lodingDialog.dismiss();
			}
			try {
				JSONObject jsonObject = new JSONObject(mResponseInfo.result);
				
				if(jsonObject.optInt("ret_num") == 2015){
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
					
					
//					ToastUtils.InfotoastLong(context, "奔犇账号在其他手机登录");
					CrashApplication.getInstance().setExit(true);
					CrashApplication.getInstance().logout();
					startActivity(new Intent(mContext, ActivityLogin.class));
					return;
				}
				
				BaseActivity.this.onSuccess(jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		public void onLoading(long total, long current, boolean isUploading) {

			BaseActivity.this.onLoading(total, current, isUploading);

		};

		@Override
		public void onFailure(HttpException exception, String strMsg) {
			if (isShowLoding && lodingDialog != null
					&& lodingDialog.isShowing()) {
				lodingDialog.dismiss();
			}
			BaseActivity.this.onFailure(exception, strMsg);
		}
	};

	private ImageView com_title_bar_left_bt;

	private MyTextView com_title_bar_left_tv;

	private ImageView com_title_bar_right_bt;

	private MyTextView com_title_bar_right_tv;

	private MyTextView com_title_bar_content;

	private ImageView com_title_bai_content_img;

	private View com_title_bar_bg;

	private TextView tab_num;

	protected in.srain.cube.image.ImageLoader cubeimageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mApplication = (CrashApplication) CrashApplication.getInstance();
		spUtil = mApplication.getSpUtil();
		dbUtil = mApplication.getDb();
		if (user == null) {
			try {
				mApplication.user = user = dbUtil.findFirst(User.class);
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mApplication.mScreenWidth = mScreenWidth = metric.widthPixels;
		mApplication.mScreenHeight = mScreenHeight = metric.heightPixels;
		mContext = this;
		yeLog = XunaoLog.yLog();
		mApplication.addActivity(this);
		cubeimageLoader = ImageLoaderFactory.create(mContext);
		initdefaultImage(R.drawable.default_face);
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		loadLayout(savedInstanceState);
		initView(savedInstanceState);
		initLinstener(savedInstanceState);
		initDate(savedInstanceState);

	}

	protected void initdefaultImage(final int imgRes) {
		cubeimageLoader.setImageLoadHandler(new DefaultImageLoadHandler(
				mContext) {
			@Override
			public void onLoading(ImageTask imageTask,
					CubeImageView cubeImageView) {
				if (cubeImageView != null) {
					if (imgRes != 0) {
						cubeImageView.setImageResource(imgRes);
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
					if (imgRes != 0) {
						imageView.setImageResource(imgRes);
					}
				}
			}
		});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	protected void initTitleView() {
		tab_num = (TextView) findViewById(R.id.tab_num);
		com_title_bar_left_bt = (ImageView) findViewById(R.id.com_title_bar_left_bt);
		com_title_bar_left_tv = (MyTextView) findViewById(R.id.com_title_bar_left_tv);
		com_title_bar_right_bt = (ImageView) findViewById(R.id.com_title_bar_right_bt);
		com_title_bar_right_tv = (MyTextView) findViewById(R.id.com_title_bar_right_tv);
		com_title_bar_content = (MyTextView) findViewById(R.id.com_title_bar_content);
		com_title_bai_content_img = (ImageView) findViewById(R.id.com_title_bai_content_img);
		com_title_bar_bg = findViewById(R.id.com_title_bar_bg);
	}

	// 头部栏类型
	public enum TitleBarType {
		RightContent, SingleContent, LeftContent, RightAndLeft, SingleLeft, SingleRight
	}

	public static class TitleMode {
		String colorString;
		String rightString;
		int rightImgRes;
		String contentString;
		int contentImgRes;
		String leftString;
		int leftImgRes;
		OnClickListener leftListener;
		OnClickListener rightListener;

		public TitleMode(TitleBarType titleBarType, String colorString,
				String String, int ImgRes, String contentString,
				int contentImgRes, OnClickListener Listener) {
			super();
			this.colorString = colorString;
			this.contentString = contentString;
			this.contentImgRes = contentImgRes;

			switch (titleBarType) {
			case RightContent:
				this.rightString = String;
				this.rightImgRes = ImgRes;
				this.rightListener = Listener;
				break;
			case LeftContent:
				this.leftString = String;
				this.leftImgRes = ImgRes;
				this.leftListener = Listener;
				break;
			}
		}

		public TitleMode(TitleBarType titleBarType, String colorString,
				String String, int ImgRes, OnClickListener Listener) {
			super();
			this.colorString = colorString;
			switch (titleBarType) {
			case SingleRight:
				this.rightString = String;
				this.rightImgRes = ImgRes;
				this.rightListener = Listener;
				break;
			case SingleLeft:
				this.leftString = String;
				this.leftImgRes = ImgRes;
				this.leftListener = Listener;
				break;
			}
		}

		public TitleMode(String colorString, String contentString,
				int contentImgRes) {
			super();
			this.colorString = colorString;
			this.contentString = contentString;
			this.contentImgRes = contentImgRes;
		}

		public TitleMode(String colorString, String rightString,
				int rightImgRes, OnClickListener rightListener,
				String leftString, int leftImgRes,
				OnClickListener leftListener, String contentString,
				int contentImgRes) {
			super();
			this.colorString = colorString;
			this.contentString = contentString;
			this.contentImgRes = contentImgRes;
			this.rightString = rightString;
			this.rightImgRes = rightImgRes;
			this.leftString = leftString;
			this.leftImgRes = leftImgRes;
			this.rightListener = rightListener;
			this.leftListener = leftListener;
		}

	}

	public void chanageTitle(TitleMode titleMode) {
		com_title_bar_left_bt.setImageBitmap(null);
		com_title_bar_left_bt.setVisibility(View.GONE);

		com_title_bar_right_bt.setImageBitmap(null);
		com_title_bar_right_bt.setVisibility(View.GONE);

		com_title_bai_content_img.setImageBitmap(null);
		com_title_bai_content_img.setVisibility(View.GONE);

		com_title_bar_bg.setBackgroundColor(Color
				.parseColor(titleMode.colorString));

		com_title_bar_left_tv.setText(titleMode.leftString);
		com_title_bar_right_tv.setText(titleMode.rightString);
		com_title_bar_content.setText(titleMode.contentString);

		if (titleMode.rightImgRes != 0) {
			com_title_bar_right_bt.setImageResource(titleMode.rightImgRes);
			com_title_bar_right_bt.setVisibility(View.VISIBLE);
		}
		if (titleMode.leftImgRes != 0) {
			com_title_bar_left_bt.setImageResource(titleMode.leftImgRes);
			com_title_bar_left_bt.setVisibility(View.VISIBLE);
		}
		if (titleMode.contentImgRes != 0) {
			com_title_bai_content_img.setImageResource(titleMode.contentImgRes);
			com_title_bai_content_img.setVisibility(View.VISIBLE);
		}

		if (titleMode.rightListener != null) {
			com_title_bar_right_bt.setOnClickListener(titleMode.rightListener);
			com_title_bar_right_tv.setOnClickListener(titleMode.rightListener);
		}
		if (titleMode.leftListener != null) {
			com_title_bar_left_bt.setOnClickListener(titleMode.leftListener);
			com_title_bar_left_tv.setOnClickListener(titleMode.leftListener);
		}

	}

	public LodingDialog getLodingDialog() {
		return lodingDialog;
	}

	public void setLodingDialog(LodingDialog lodingDialog) {
		this.lodingDialog = lodingDialog;
	}

	/**
	 * 加载视图
	 */
	public abstract void loadLayout(Bundle savedInstanceState);

	/**
	 * 初始化控件
	 * 
	 */
	public abstract void initView(Bundle savedInstanceState);

	/**
	 * 获取数据
	 */
	public abstract void initDate(Bundle savedInstanceState);

	/**
	 * 监听设置
	 */
	public abstract void initLinstener(Bundle savedInstanceState);

	/**
	 * http连接开始
	 */
	protected abstract void onHttpStart();

	/**
	 * http连接中
	 */
	protected abstract void onLoading(long count, long current,
			boolean isUploading);

	/**
	 * http连接成功
	 */
	protected abstract void onSuccess(JSONObject jsonObject);

	/**
	 * http连接失败
	 */
	protected abstract void onFailure(HttpException exception, String strMsg);

	/**
	 * 获取手机型号
	 */
	public String getPhoneInfo() {
		TelephonyManager mTm = (TelephonyManager) this
				.getSystemService(TELEPHONY_SERVICE);
		// String imei = mTm.getDeviceId();
		// String imsi = mTm.getSubscriberId();
		String mtype = android.os.Build.MODEL; // 手机型号

		return mtype;
	}

	// 界面跳转方法

	public void addAnim() {
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivity(Class<?> cla) {
		this.startActivity(new Intent(this, cla));
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivity2Obj(Class<?> cla, String key, String value) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, value);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivity3Obj(Class<?> cla, String key, String value,
			String key2, String value2) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, value);
		intent.putExtra(key2, value2);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivity6Obj(Class<?> cla, String key, String value,
			String key2, String value2, String key3, String value3) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, value);
		intent.putExtra(key2, value2);
		intent.putExtra(key3, value3);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivity5Obj(Class<?> cla, String key,
			BaseBean<?> value, String key2, int value2) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, value);
		intent.putExtra(key2, value2);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivity4Obj(Class<?> cla, String key,
			ArrayList<?> value) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, value);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivityForResult(Class<?> cla, String key_1,
			BaseBean baseBean_1, String key_2, BaseBean baseBean_2,
			int requestCode) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key_1, baseBean_1);
		intent.putExtra(key_2, baseBean_2);
		this.startActivityForResult(intent, requestCode);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}
	
	public void startAnimActivityForResult(Class<?> cla, String key_1,
			String baseBean_1, String key_2, int baseBean_2,
			int requestCode) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key_1, baseBean_1);
		intent.putExtra(key_2, baseBean_2);
		this.startActivityForResult(intent, requestCode);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivityForResult(Class<?> cla, String key,
			ArrayList<?> value, int requestCode) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, value);
		this.startActivityForResult(intent, requestCode);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivityForResult(Class<?> cla, String key,
			String bean, String key_1, BaseBean<?> baseBean_1, String key_2,
			ArrayList<?> baseBean_2, int requestCode) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, bean);
		intent.putExtra(key_1, baseBean_1);
		intent.putExtra(key_2, baseBean_2);
		this.startActivityForResult(intent, requestCode);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivityForResult(Class<?> cla, String key,
			String bean, String key1, String bean1, String key_1,
			BaseBean<?> baseBean_1, String key_2, ArrayList<?> baseBean_2,
			int requestCode) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, bean);
		intent.putExtra(key1, bean1);
		intent.putExtra(key_1, baseBean_1);
		intent.putExtra(key_2, baseBean_2);
		this.startActivityForResult(intent, requestCode);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivityForResult(Class<?> cla, int requestCode) {
		Intent intent = new Intent(this, cla);
		this.startActivityForResult(intent, requestCode);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivityForResult(Class<?> cla, String key,
			String value, String key2, String value2, String key3,
			String value3, int requestCode) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, value);
		intent.putExtra(key2, value2);
		intent.putExtra(key3, value3);
		this.startActivityForResult(intent, requestCode);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivityForResult2(Class<?> cla, int requestCode,
			String key, String doWhat) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, doWhat);
		this.startActivityForResult(intent, requestCode);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivityForResult3(Class<?> cla, int requestCode,
			String key, int doWhat) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, doWhat);
		this.startActivityForResult(intent, requestCode);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivityForResult3(Class<?> cla, int requestCode,
			String key, int doWhat, String key1, String value) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, doWhat);
		intent.putExtra(key1, value);
		this.startActivityForResult(intent, requestCode);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivityForResult3(Class<?> cla, int requestCode,
			String key, String doWhat, String key1, String value) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, doWhat);
		intent.putExtra(key1, value);
		this.startActivityForResult(intent, requestCode);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivityForResult4(Class<?> cla, int requestCode,
			String key, String doWhat, String key2, double[] lat) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, doWhat);
		intent.putExtra(key2, lat);
		this.startActivityForResult(intent, requestCode);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivityForResult6(Class<?> cla, int requestCode,
			String key, BaseBean<?> baseBean, String key2,
			ArrayList<?> arrayList) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, baseBean);
		intent.putExtra(key2, arrayList);
		this.startActivityForResult(intent, requestCode);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivityForResult5(Class<?> cla, String key,
			String doWhat, String key2, ArrayList<?> arrayList, int requestCode) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, doWhat);
		intent.putExtra(key2, arrayList);
		this.startActivityForResult(intent, requestCode);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivityForResult51(Class<?> cla, String key,
			String doWhat, String key1, String value, String key2,
			ArrayList<?> arrayList, int requestCode) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, doWhat);
		intent.putExtra(key1, value);
		intent.putExtra(key2, arrayList);
		this.startActivityForResult(intent, requestCode);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivityForResult6(Class<?> cla, String key1,
			BaseBean baseBean, String key2, ArrayList<?> arrayList,
			String key3, ArrayList<?> arrayList2, int requestCode) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key1, baseBean);
		intent.putExtra(key2, arrayList);
		intent.putExtra(key3, arrayList2);
		this.startActivityForResult(intent, requestCode);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivityForResult(Class<?> cla, String key1,
			ArrayList<?> value, String key2, String value2, int requestCode) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key1, value);
		intent.putExtra(key2, value2);
		this.startActivityForResult(intent, requestCode);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivityForResult8(Class<?> cla, String key1,
			BaseBean baseBean, String key2, ArrayList<?> arrayList,
			String key3, ArrayList<?> arrayList2, String key4, String doWhat,
			int requestCode) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key1, baseBean);
		intent.putExtra(key2, arrayList);
		intent.putExtra(key3, arrayList2);
		intent.putExtra(key4, doWhat);
		this.startActivityForResult(intent, requestCode);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivityForResult7(Class<?> cla, String key,
			ArrayList<?> arrayList, String key2, int doWhat, String key3,
			String doWhat2, int requestCode) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, arrayList);
		intent.putExtra(key2, doWhat);
		intent.putExtra(key3, doWhat2);
		this.startActivityForResult(intent, requestCode);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivityForResult(Class<?> cla, String key,
			BaseBean baseBean, int requestCode) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, baseBean);
		this.startActivityForResult(intent, requestCode);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivity2Obj(Class<?> cla, String key,
			BaseBean baseBean) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, baseBean);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivity3Obj(Class<?> cla, String key,
			ArrayList<?> lists, String key2, double lat, String key3, double lng) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, lists);
		intent.putExtra(key2, lat);
		intent.putExtra(key3, lng);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivityNumberTrain(Class<?> cla, String key,
			ArrayList<?> lists, String key2, double lat, String key3,
			double lng, String key4, String value) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, lists);
		intent.putExtra(key2, lat);
		intent.putExtra(key3, lng);
		intent.putExtra(key4, value);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivity2Obj(Class<?> cla, String key_1,
			String value_1, String key_2, String value_2) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key_1, value_1);
		intent.putExtra(key_2, value_2);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivity2Obj(Class<?> cla, String key_1,
			String value_1, String key_2, BaseBean<?> baseBean) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key_1, value_1);
		intent.putExtra(key_2, baseBean);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void AnimFinsh() {
		this.finish();
		this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}

	// 头部
	public void initTitle_Right_Left_bar(String content, String leftContent,
			String rightContent, int left_img_id, int right_img_id) {
		com_title_bar_left_bt = (ImageView) findViewById(R.id.com_title_bar_left_bt);
		com_title_bar_content = (MyTextView) findViewById(R.id.com_title_bar_content);
		com_title_bar_right_bt = (ImageView) findViewById(R.id.com_title_bar_right_bt);

		com_title_bar_left_tv = (MyTextView) findViewById(R.id.com_title_bar_left_tv);
		com_title_bar_right_tv = (MyTextView) findViewById(R.id.com_title_bar_right_tv);

        com_title_bar_content.setText(content);

        com_title_bar_left_tv.setText(leftContent);
        if(leftContent.equals("")){
            com_title_bar_left_tv.setVisibility(View.GONE);
        }else{
            com_title_bar_left_tv.setVisibility(View.VISIBLE);
        }
		com_title_bar_right_tv.setText(rightContent);
        if(rightContent.equals("")){
            com_title_bar_right_tv.setVisibility(View.GONE);
        }else{
            com_title_bar_right_tv.setVisibility(View.VISIBLE);
        }
		com_title_bar_right_bt.setImageResource(right_img_id);
        if(right_img_id==0){
            com_title_bar_right_bt.setVisibility(View.GONE);
        }else{
            com_title_bar_right_bt.setVisibility(View.VISIBLE);
        }
		com_title_bar_left_bt.setImageResource(left_img_id);
        if(left_img_id==0){
            com_title_bar_left_bt.setVisibility(View.GONE);
        }else{
            com_title_bar_left_bt.setVisibility(View.VISIBLE);
        }
	}

	// 左边按钮的点击事件
	protected void setOnLeftClickLinester(OnClickListener clickListener) {
		com_title_bar_left_tv.setOnClickListener(clickListener);
		com_title_bar_left_bt.setOnClickListener(clickListener);
	}

	// 右边按钮的点击事件
	protected void setOnRightClickLinester(OnClickListener clickListener) {
		com_title_bar_right_tv.setOnClickListener(clickListener);
		com_title_bar_right_bt.setOnClickListener(clickListener);
	}

	// 改变头部的文字
	public void changeTitileContent(String titleContent) {
		com_title_bar_content.setText(titleContent);
	}

	// 用当前时间给取得的图片命名
	public String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}

	public static Intent getTakePickIntent(File f) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		return intent;
	}

	public Bitmap getBitMap(Uri data, BitmapFactory.Options opts, View v) {

		ContentResolver resolver = getContentResolver();
		try {
			byte[] mContent = readStream(resolver.openInputStream(Uri
					.parse(data.toString())));
			myBitmap = getPicFromBytes(mContent, opts, v);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return myBitmap;
	}

	public static byte[] readStream(InputStream inStream) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;

	}

	public String getBitmap(InputStream stream) {

		try {
			FileOutputStream fileOutputStream = null;
			Bitmap decodeFile = null;
			mCurrentPhotoFile = CommonUtils.getImagePath(mContext,
					getPhotoFileName());

			fileOutputStream = new FileOutputStream(mCurrentPhotoFile);

			int length = 0;
			byte[] buf = new byte[1024 * 1024];

			while ((length = stream.read(buf)) != -1) {
				fileOutputStream.write(buf);
			}
			fileOutputStream.flush();
			fileOutputStream.close();

		} catch (Exception e) {
			e.printStackTrace();
			ToastUtils.Errortoast(mContext, "获取图片异常，请重新尝试。");
		}

		return mCurrentPhotoFile.getAbsolutePath();

	}

	public static Bitmap getPicFromBytes(byte[] bytes,
			BitmapFactory.Options opts, View v) {
		if (bytes != null)
			if (opts != null) {
				opts.inJustDecodeBounds = true;
				BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
				int outWidth = opts.outWidth;
				int width = v.getWidth();
				if (outWidth > width) {
					opts.inSampleSize = outWidth / width;
				}
				opts.inJustDecodeBounds = false;
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
						opts);
			} else
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return null;
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (cubeimageLoader != null)
			cubeimageLoader.recoverWork();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (cubeimageLoader != null)
			cubeimageLoader.stopWork();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (cubeimageLoader != null)
			cubeimageLoader.resumeWork();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (cubeimageLoader != null)
			cubeimageLoader.pauseWork();
	}

	@Override
	protected void onDestroy() {
		mApplication.removeActivity(this);
		super.onDestroy();
		if (cubeimageLoader != null)
			cubeimageLoader.onDestroy();

	};

	/**
	 * 当应用在前台时，如果当前消息不是属于当前会话，在状态栏提示一下 如果不需要，注释掉即可
	 * 
	 * @param message
	 */
	protected void notifyNewMessage(EMMessage message) {
		// 如果是设置了不提醒只显示数目的群组(这个是app里保存这个数据的，demo里不做判断)
		// 以及设置了setShowNotificationInbackgroup:false(设为false后，后台时sdk也发送广播)
		if (!EasyUtils.isAppRunningForeground(this)) {
			return;
		}

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(getApplicationInfo().icon)
				.setWhen(System.currentTimeMillis()).setAutoCancel(true);

		String ticker = CommonUtils.getMessageDigest(message, this);
		String st = getResources().getString(R.string.expression);
		if (message.getType() == Type.TXT)
			ticker = ticker.replaceAll("\\[.{2,3}\\]", st);
		// 设置状态栏提示
		mBuilder.setTicker(message.getFrom() + ": " + ticker);

		// 必须设置pendingintent，否则在2.3的机器上会有bug
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, notifiId,
				intent, PendingIntent.FLAG_ONE_SHOT);
		mBuilder.setContentIntent(pendingIntent);

		Notification notification = mBuilder.build();
		notificationManager.notify(notifiId, notification);
		notificationManager.cancel(notifiId);
	}

	@Override
	public void onBackPressed() {
		AnimFinsh();
	}

	// 弹出输入法
	public void showKeyBoard(EditText view) {
		// mEditTextContent.setFocusable(true);
		view.setFocusable(true);
		view.setFocusableInTouchMode(true);
		view.requestFocus();
		InputMethodManager inputManager = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.showSoftInput(view, 0);
		// mEditTextContent.setFocusableInTouchMode(true);
	}

	/**
	 * 隐藏软键盘
	 */
	public void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

}
