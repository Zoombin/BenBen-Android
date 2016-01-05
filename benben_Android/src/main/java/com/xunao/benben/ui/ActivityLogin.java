package com.xunao.benben.ui;

import org.json.JSONObject;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.umeng.analytics.MobclickAgent;
import com.xunao.benben.R;
import com.xunao.benben.activity.MainActivity;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.LatelyLinkeMan;
import com.xunao.benben.bean.News;
import com.xunao.benben.bean.PublicMessage;
import com.xunao.benben.bean.User;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.SizeChangeLinearLayout;

public class ActivityLogin extends BaseActivity implements OnClickListener {

	private EditText login_name;
	private EditText login_passWord;
	private Button login_btn;

	private TextView login_register;
	private TextView login_forgetPassWord;
	private View logo_img;
	private User user;
	private String name;
	private String password;
    private ImageView iv_name_clear,iv_pwd_clear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		setShowLoding(false);
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_login);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		final SizeChangeLinearLayout sizechangeL = (SizeChangeLinearLayout) findViewById(R.id.sizechangeL);
		login_name = (EditText) findViewById(R.id.login_name);
		logo_img = findViewById(R.id.logo_img);
		login_passWord = (EditText) findViewById(R.id.login_passWord);
		login_btn = (Button) findViewById(R.id.login_btn);
        iv_name_clear = (ImageView) findViewById(R.id.iv_name_clear);
        iv_name_clear.setOnClickListener(this);
        iv_pwd_clear = (ImageView) findViewById(R.id.iv_pwd_clear);
        iv_pwd_clear.setOnClickListener(this);
		login_register = (TextView) findViewById(R.id.login_register);
		login_forgetPassWord = (TextView) findViewById(R.id.login_forgetPassWord);
		controlKeyboardLayout(sizechangeL, login_btn);

		login_name.setText(spUtil.getUserLoginAcount());
        if(spUtil.getUserLoginAcount().equals("")){
            iv_name_clear.setVisibility(View.GONE);
        }else{
            iv_name_clear.setVisibility(View.VISIBLE);
        }
		login_passWord.setText(spUtil.getUserPWD());
        if(spUtil.getUserPWD().equals("")){
            iv_pwd_clear.setVisibility(View.GONE);
        }else{
            iv_pwd_clear.setVisibility(View.VISIBLE);
        }

	}

	protected void onResume() {
		super.onResume();
		if(mApplication.isExit()){
			final InfoSimpleMsgHint hint2 = new InfoSimpleMsgHint(mContext,
					R.style.MyDialog1);
			hint2.setContent("奔犇账号在其他手机登录");
			hint2.setBtnContent("确定");
			hint2.show();
			hint2.setOKListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					hint2.dismiss();
				}
			});
			
			hint2.show();
			mApplication.setExit(false);
			MobclickAgent.onPageStart("login");
			MobclickAgent.onResume(mContext);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
//		final InfoSimpleMsgHint hint2 = new InfoSimpleMsgHint(
//				mContext, R.style.MyDialog1);
//		hint2.setContent("奔犇账号在其他手机登录");
//		hint2.setBtnContent("确定");
//		hint2.show();
//		hint2.setOKListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				hint2.dismiss();
//			}
//		});
//		
//		hint2.show();
		MobclickAgent.onPageEnd("login");
		MobclickAgent.onPause(mContext);
	}

	/**
	 * @param root
	 *            最外层布局，需要调整的布局
	 * @param scrollToView
	 *            被键盘遮挡的scrollToView，滚动root,使scrollToView在root可视区域的底部
	 */
	private void controlKeyboardLayout(final View root, final View scrollToView) {
		root.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						Rect rect = new Rect();
						// 获取root在窗体的可视区域
						root.getWindowVisibleDisplayFrame(rect);
						// 获取root在窗体的不可视区域高度(被其他View遮挡的区域高度)
						int rootInvisibleHeight = root.getRootView()
								.getHeight() - rect.bottom;
						// 若不可视区域高度大于100，则键盘显示
						if (rootInvisibleHeight > 100) {
							int[] location = new int[2];
							// 获取scrollToView在窗体的坐标
							scrollToView.getLocationInWindow(location);
							// 计算root滚动高度，使scrollToView在可见区域
							int srollHeight = (location[1]
									+ scrollToView.getHeight() + PixelUtil
										.dp2px(10)) - rect.bottom;
							root.scrollTo(0, srollHeight);
						} else {
							// 键盘隐藏
							root.scrollTo(0, 0);
						}
					}
				});
	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		login_btn.setOnClickListener(this);
		login_register.setOnClickListener(this);
		login_forgetPassWord.setOnClickListener(this);
		login_name.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				mApplication.getSpUtil().setLastTimeLogin(false);
				login_passWord.setText("");
				spUtil.setUserPWD("");
                if(s.length()==0){
                    iv_name_clear.setVisibility(View.GONE);
                }else{
                    iv_name_clear.setVisibility(View.VISIBLE);
                }
			}
		});

        login_passWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0){
                    iv_pwd_clear.setVisibility(View.GONE);
                }else{
                    iv_pwd_clear.setVisibility(View.VISIBLE);
                }
            }
        });
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
	protected void onSuccess(JSONObject jsonObject) {
		try {
			user = new User();
			user = user.parseJSON(jsonObject);
			// 保存用户信息
			if (dbUtil.tableIsExist(User.class)) {
				dbUtil.deleteAll(User.class);
			}
			String userAcount = mApplication.getSpUtil().getUserAcount();
			if (!user.getHuanxin_username().equals(userAcount)) {
				// 与上次登录用户不同删除上一个用户信息
				dbUtil.deleteAll(PublicMessage.class);
                dbUtil.deleteAll(LatelyLinkeMan.class);
				CrashApplication.getInstance().getSpUtil().setLastTime(0);
				dbUtil.deleteAll(News.class);

			}
			spUtil.setUserAcount(user.getHuanxin_username());
			spUtil.setUseLoginrAcount(name);
			spUtil.setUserPWD(password);

			dbUtil.save(user);
			// 存到sp中方便读取 注册的时候还没有进行操作
			spUtil.setHuanXinUserName(user.getHuanxin_username());
			loginHuanXin(user);
		} catch (NetRequestException e) {
			e.getError().print(mContext);
			mApplication.getSpUtil().setLastTimeLogin(false);
			dissLoding();
		} catch (DbException e) {
			mApplication.getSpUtil().setLastTimeLogin(false);
			e.printStackTrace();
			dissLoding();
		}

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		dissLoding();
		ToastUtils.Errortoast(mContext, "登录失败，请重试");
	}

	private void loginHuanXin(final User user) {
		Log.d("LSD",user.getHuanxin_username()  +"   "+user.getHuanxin_password());
        EMChatManager.getInstance().login(user.getHuanxin_username(), user.getHuanxin_password(), new EMCallBack() {

			@Override
			public void onSuccess() {
				mApplication.setUserName(user.getHuanxin_username());
				mApplication.setPassword(user.getHuanxin_password());

				EMGroupManager.getInstance().loadAllGroups();
				EMChatManager.getInstance().loadAllConversations();
				mApplication.getSpUtil().setLastTimeLogin(true);
				dissLoding();
				startAnimActivity2Obj(MainActivity.class, "source",
						"login");
				finish();
			}

			@Override
			public void onProgress(int progress, String status) {
			}

			@Override
			public void onError(final int code, final String message) {
				Log.d("ltf", "login=============失败=========");
				ActivityLogin.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						dissLoding();
						startAnimActivity2Obj(MainActivity.class,
								"source", "login");
						AnimFinsh();
					}
				});
            }
        });

    }
	boolean isLogin = false;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
            case R.id.login_btn:
                isLogin = true;
                name = login_name.getText().toString();
                password = login_passWord.getText().toString();
                if ("".equals(name)) {
                    ToastUtils.Errortoast(mContext, "用户名不能为空!");
                    return;
                }
                if ("".equals(password)) {
                    ToastUtils.Errortoast(mContext, "用户密码不能为空!");
                    return;
                }
                showLoding("正在登录...");
                if (CommonUtils.isNetworkAvailable(mContext)) {
                    CommonUtils.hideSoftInputFromWindow(mContext);
                    InteNetUtils.getInstance(mContext).Login(name, password,
                            getPhoneInfo(), mRequestCallBack);
                }
                break;
            case R.id.login_register:
                startAnimActivity(ActivityRegisterOne.class);
                break;

            case R.id.login_forgetPassWord:
                startAnimActivity(ActivityForgetPassWord.class);

                break;
            case R.id.iv_name_clear:
                login_name.setText("");
                break;
            case R.id.iv_pwd_clear:
                login_passWord.setText("");
                break;
            default:
                break;
		}
	}

	@Override
	public void onBackPressed() {
		mApplication.shutDown();
	}

}
