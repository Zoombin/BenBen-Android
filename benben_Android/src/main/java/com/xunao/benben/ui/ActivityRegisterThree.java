package com.xunao.benben.ui;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.baidu.location.f;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.widget.time.JudgeDate;
import com.widget.time.ScreenInfo;
import com.widget.time.WheelMain;
import com.xunao.benben.R;
import com.xunao.benben.activity.MainActivity;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.bean.News;
import com.xunao.benben.bean.PublicMessage;
import com.xunao.benben.bean.User;
import com.xunao.benben.dialog.BirthDialog;
import com.xunao.benben.dialog.InfoSimpleMsgHint;
import com.xunao.benben.dialog.MsgDialog;
import com.xunao.benben.exception.NetRequestException;
import com.xunao.benben.net.InteNetUtils;
import com.xunao.benben.utils.CommonUtils;
import com.xunao.benben.utils.PixelUtil;
import com.xunao.benben.utils.ToastUtils;
import com.xunao.benben.view.MyTextView;

public class ActivityRegisterThree extends BaseActivity implements
		OnClickListener {

	private String phone_num;
	private String phone_code;

	private EditText user_name;
	private MyTextView user_age;
	private EditText user_password_one;
	private EditText user_password_two;
	private RadioGroup radioGroup;

	private String age;

	private WheelMain wheelMain;
	private EditText txttime;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private String user_sex = "1";

	private InputMethodManager manager;
	private String user_password_one_text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		manager = (InputMethodManager) getApplicationContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	@Override
	public void loadLayout(Bundle savedInstanceState) {
		setContentView(R.layout.activity_register_three);
		setShowLoding(false);
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		initTitle_Right_Left_bar("注册", "", "完成",
				R.drawable.icon_com_title_left, 0);

		user_name = (EditText) findViewById(R.id.user_name);
		user_age = (MyTextView) findViewById(R.id.user_age);
		user_password_one = (EditText) findViewById(R.id.user_password_one);
		user_password_two = (EditText) findViewById(R.id.user_password_two);

		radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

	}

	@Override
	public void initDate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		phone_num = intent.getStringExtra("phone");
		phone_code = intent.getStringExtra("code");

	}

	@Override
	public void initLinstener(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setOnLeftClickLinester(this);
		setOnRightClickLinester(this);

		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// 获取变更后的选中项的ID
				int radioButtonId = arg0.getCheckedRadioButtonId();
				// 根据ID获取RadioButton的实例
				RadioButton rb = (RadioButton) findViewById(radioButtonId);
				// 更新文本内容，以符合选中项
				String rb_text = rb.getText().toString();
				if (rb_text.equals("男")) {
					user_sex = "1";
				} else {
					user_sex = "2";
				}
			}
		});

		user_age.setOnClickListener(this);
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
			User user = new User();
			user = user.parseJSON(jsonObject);
			user.setRegLogin(true);

			spUtil.setLastTimeLogin(true);
			// 保存用户信息
			if (dbUtil.tableIsExist(User.class)) {
				dbUtil.deleteAll(User.class);
			}

			dbUtil.save(user);
			String userAcount = mApplication.getSpUtil().getUserAcount();
			if (!user.getHuanxin_username().equals(userAcount)) {
				// 与上次登录用户不同删除上一个用户信息
				dbUtil.deleteAll(PublicMessage.class);
				CrashApplication.getInstance().getSpUtil().setLastTime(0);
				dbUtil.deleteAll(News.class);
			}
			spUtil.setUserAcount(user.getHuanxin_username());
			spUtil.setUseLoginrAcount(phone_num);
			spUtil.setUserPWD(user_password_one_text);
			// 存到sp中方便读取 注册的时候还没有进行操作
			spUtil.setHuanXinUserName(user.getHuanxin_username());
//			loginHuanXin(user);
//            ToastUtils.Infotoast(this,"欢迎来到本地化的私人订制平台——奔犇，您的奔犇号是"+user.getBenbenId()+",用奔犇号登录更方便。");
            final InfoSimpleMsgHint msgDialog = new InfoSimpleMsgHint(mContext, R.style.MyDialogStyle);
            msgDialog.setContent("","欢迎来到本地化的私人订制平台——奔犇，您的奔犇号是"+user.getBenbenId()+",用奔犇号登录更方便。", "确认", "取消");
            final User finalUser = user;
            msgDialog.setOKListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginHuanXin(finalUser);
                    msgDialog.dismiss();
                }
            });
            msgDialog.show();
            dissLoding();
			// startAnimActivity2Obj(MainActivity.class, "source", "register");
			// 销毁之前所有的activity
		} catch (NetRequestException e) {
			// TODO Auto-generated catch block
			e.getError().print(mContext);
		} catch (DbException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onFailure(HttpException exception, String strMsg) {
		// TODO Auto-generated method stub
		dissLoding();
		ToastUtils.Errortoast(mContext, strMsg);
	}

	private void loginHuanXin(final User user) {
		// 调用sdk登陆方法登陆聊天服务器
        showLoding("登录中");
		EMChatManager.getInstance().login(user.getHuanxin_username(),
				user.getHuanxin_password(), new EMCallBack() {
					@Override
					public void onSuccess() {
						mApplication.setUserName(user.getHuanxin_username());
						mApplication.setPassword(user.getHuanxin_password());
                        dissLoding();
						EMGroupManager.getInstance().loadAllGroups();
						EMChatManager.getInstance().loadAllConversations();
						mApplication.getSpUtil().setLastTimeLogin(true);
						dissLoding();
						startAnimActivity2Obj(MainActivity.class, "source",
								"register");
						AnimFinsh();
					}

					@Override
					public void onProgress(int progress, String status) {
					}

					@Override
					public void onError(int code, String message) {
						dissLoding();
						ActivityRegisterThree.this
								.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										dissLoding();
										startAnimActivity2Obj(
												MainActivity.class, "source",
												"register");
										AnimFinsh();
									}
								});
					}
				});
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		// 头部左侧点击
		case R.id.com_title_bar_left_bt:
		case R.id.com_title_bar_left_tv:
			AnimFinsh();
			break;

		// 头部右侧点击
		case R.id.com_title_bar_right_bt:
		case R.id.com_title_bar_right_tv:
			String user_name_text = user_name.getText().toString()
					.replaceAll("", "");
			String user_age_text = age;
			user_password_one_text = user_password_one.getText().toString()
					.trim();
			String user_password_two_text = user_password_two.getText()
					.toString().trim();

			if (TextUtils.isEmpty(user_name_text)) {
				ToastUtils.Errortoast(mContext, "请输入昵称!");
			}

			if (!CommonUtils.StringIsSurpass(user_name_text, 1, 12)) {
				ToastUtils.Infotoast(mContext, "昵称不可超过12字");
				return;
			}

			if (TextUtils.isEmpty(user_age_text)) {
				ToastUtils.Errortoast(mContext, "请选择年龄!");
			}

			// 验证信息
			if (user_password_one_text == null
					|| user_password_two_text == null
					|| user_password_one_text.length() <= 0
					|| user_password_two_text.length() <= 0) {
				ToastUtils.Errortoast(mContext, "请正确输入密码！");
			} else if (!user_password_one_text.equals(user_password_two_text)) {
				ToastUtils.Errortoast(mContext, "两次密码输入不一致！");
			} else if (!user_password_one_text.matches("^\\w{6,16}$")) {
				ToastUtils.Errortoast(mContext, "密码输入为6-16位");
			} else {
				showLoding("注册中...");
				String phone_mode = getPhoneInfo();
				InteNetUtils.getInstance(mContext).Register(user_name_text,
						phone_num, user_age_text, user_sex,
						user_password_one_text, user_password_two_text,
						phone_mode, phone_code, mRequestCallBack);
			}
			break;

		case R.id.user_age:
			choseDate();
			break;
		default:
			break;
		}
	}

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
					try {
						SimpleDateFormat myFormatter = new SimpleDateFormat(
								"yyyy-MM-dd");
						lastTime = dialog.getWheelMain();
						Date date = myFormatter.parse(dialog.getWheelMain());
						age = date.getTime() / 1000 + "";
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					if(CommonUtils.getAge2(mContext,
							dialog.getWheelMain()) != null){
						user_age.setText(CommonUtils.getAge2(mContext,
								dialog.getWheelMain())
								+ "岁");
						
					}
					dialog.dismiss();
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
