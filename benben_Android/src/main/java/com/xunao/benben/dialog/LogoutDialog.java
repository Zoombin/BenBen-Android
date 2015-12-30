package com.xunao.benben.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xunao.benben.R;
import com.xunao.benben.base.BaseActivity;
import com.xunao.benben.base.IA.CrashApplication;
import com.xunao.benben.ui.ActivityLogin;
import com.xunao.benben.utils.CommonUtils;

public class LogoutDialog extends AbsDialog {

	private TextView dialog_info_content;
	private Button dialog_info_OK;
	private Button dialog_info_cancle;
	private ImageView dialog_iv_cancle;
	private Context context;

	public LogoutDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public LogoutDialog(Context context, int theme) {
		super(context, theme);
	}

	public LogoutDialog(Context context) {
		super(context, R.style.MyDialog1);
		this.context = context;
	}

	@Override
	protected void initView() {

		dialog_info_content = (TextView) findViewById(R.id.dialog_info_content);
		dialog_info_OK = (Button) findViewById(R.id.dialog_info_OK);
		dialog_info_cancle = (Button) findViewById(R.id.dialog_info_cancle);
		dialog_iv_cancle = (ImageView) findViewById(R.id.dialog_iv_cancle);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	public void setContent(String content) {
		dialog_info_content.setText(content);
	}

	@Override
	protected void setListener() {
		dialog_info_OK.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				application.logout();
				((BaseActivity) context).startAnimActivity(ActivityLogin.class);
			}
		});

		dialog_info_cancle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		dialog_iv_cancle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

	}

	CrashApplication application;

	public void init(CrashApplication application) {
		this.application = application;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

}
