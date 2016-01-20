package com.xunao.test.activity;

import in.srain.cube.image.CubeImageView;

import org.json.JSONObject;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.xunao.test.R;
import com.xunao.test.base.BaseActivity;
import com.xunao.test.base.IA.CrashApplication;
import com.xunao.test.bean.User;
import com.xunao.test.config.AndroidConfig;
import com.xunao.test.exception.NetRequestException;
import com.xunao.test.net.InteNetUtils;
import com.xunao.test.ui.ActivityLogin;
import com.xunao.test.utils.CommonUtils;
import com.xunao.test.utils.ToastUtils;

/**
 * class desc: 启动画面 (1)判断是否是首次加载应用--采取读取SharedPreferences的方法
 * (2)是，则进入GuideActivity；否，则进入MainActivity (3)3s后执行(2)操作
 */
public class SplashActivity extends BaseActivity {

	boolean isFirstIn = false;
	// 应用版本号
	private int version;

	private static final int GO_HOME = 1000;
	private static final int GO_GUIDE = 1001;
	private static final int GO_MAIN = 1002;

	// 延迟3秒
	private static final long SPLASH_DELAY_MILLIS = 2000;

	private ImageView welcome_img;
	private Animation animation;

	private String loginType;

	/**
	 * Handler:跳转到不同界面
	 */
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GO_HOME:
				goLogin();
				break;
			case GO_GUIDE:
				goGuide();
				break;
			case GO_MAIN:
				goMain();
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_splash);
		init();
//		sendBroadcast(new Intent(AndroidConfig.RefreshTalkGroup));
	}

	private void init() {
		final CubeImageView img = (CubeImageView) findViewById(R.id.img);
		String path = spUtil.getPath();
		initdefaultImage(0);
		if (CommonUtils.isEmpty(path)) {
			img.setImageResource(R.drawable.benben);
		} else {
			CommonUtils.startImageLoader(cubeimageLoader, path, img);
		}

		setShowLoding(false);
		version = CommonUtils.getVersionCode(this);
		isFirstIn = spUtil.isFristComing();
		// 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
		// if (isFirstIn || !version.equals(spUtil.getVersion())) {
		// // 使用Handler的postDelayed方法，2秒后执行跳转到MainActivity
		// mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
		// } else {
		if (spUtil.getLastTimeLogin() && user != null) {
			if (CommonUtils.isNetworkAvailable(mContext)) {
				loginType = AndroidConfig.AUTOLOGIN;
				InteNetUtils.getInstance(mContext).autoLogin(user.getToken(),
						mRequestCallBack);

			} else {
				mHandler.sendEmptyMessageDelayed(GO_MAIN, SPLASH_DELAY_MILLIS);
			}
		} else {
			mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
		}
		// }

	}

	private void goLogin() {
		startAnimActivity(ActivityLogin.class);
		finish();
	}

	private void goGuide() {

		// Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
		// SplashActivity.this.startActivity(intent);
		// SplashActivity.this.finish();
	}

	protected void goMain() {
		startAnimActivity2Obj(MainActivity.class, "source", "login");
		finish();
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onHttpStart() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onLoading(long count, long current, boolean isUploading) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		ToastUtils.Errortoast(mContext, "登录失败,请重新登录");
		spUtil.setLastTimeLogin(false);
		mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		try {
		//	user = new User();
			CrashApplication.getInstance().user = user.parseJSON(jsonObject);
			user = user.parseJSON(jsonObject);
			// spUtil.setUserName(usernmae);
			// 保存用户信息
			if (dbUtil.tableIsExist(User.class)) {
				dbUtil.deleteAll(User.class);
			}
			dbUtil.save(user);
			// 存到sp中方便读取 注册的时候还没有进行操作
			spUtil.setHuanXinUserName(user.getHuanxin_username());
			loginHuanXin(user);
		} catch (NetRequestException e) {
			ToastUtils.Errortoast(mContext, "登录失败,请重新登录");
			mApplication.getSpUtil().setLastTimeLogin(false);
			mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
		} catch (DbException e) {
			mApplication.getSpUtil().setLastTimeLogin(false);
			e.printStackTrace();
			ToastUtils.Errortoast(mContext, "登录失败,请重新登录");
			mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
		}

	}

	private void loginHuanXin(final User user) {
		// 调用sdk登陆方法登陆聊天服务器
		EMChatManager.getInstance().login(user.getHuanxin_username(),
				user.getHuanxin_password(), new EMCallBack() {
					@Override
					public void onSuccess() {
						mApplication.setUserName(user.getHuanxin_username());
						mApplication.setPassword(user.getHuanxin_password());

						mApplication.getSpUtil().setLastTimeLogin(true);
						EMGroupManager.getInstance().loadAllGroups();
						EMChatManager.getInstance().loadAllConversations();
						mApplication.getSpUtil().setLastTimeLogin(true);
						startAnimActivity2Obj(MainActivity.class, "source",
								loginType);
						finish();
					}

					@Override
					public void onProgress(int progress, String status) {
					}

					@Override
					public void onError(int code, String message) {
						SplashActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								startAnimActivity2Obj(MainActivity.class,
										"source", loginType);
								finish();
							}
						});
					}
				});
	}

	// InteNetUtils.getInstance(mContext)
	// .downloadSplashImg(path,
	// new RequestCallBack<File>() {
	//
	// @Override
	// public void onSuccess(
	// ResponseInfo<File> arg0) {
	// File imagePath = CommonUtils
	// .getImagePath(
	// mContext,
	// CommonUtils
	// .md5(path));
	// FileInputStream fis = null;
	// FileOutputStream fos = null;
	// try {
	// fis = new FileInputStream(
	// arg0.result);
	// fos = new FileOutputStream(
	// imagePath);
	//
	// byte[] buff = new byte[1024];
	//
	// int read = -1;
	// while ((read = fis
	// .read(buff)) != -1) {
	// fos.write(buff);
	// }
	// fos.flush();
	//
	// } catch (FileNotFoundException e) {
	// // TODO
	// // Auto-generated
	// // catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO
	// // Auto-generated
	// // catch block
	// e.printStackTrace();
	// } finally {
	// try {
	// if (fis != null) {
	// fis.close();
	// fis = null;
	// }
	// if (fos != null) {
	// fos.close();
	// fos = null;
	// }
	// } catch (IOException e) {
	// // TODO
	// // Auto-generated
	// // catch block
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// @Override
	// public void onFailure(
	// HttpException arg0,
	// String arg1) {
	//
	// }
	// });
}
