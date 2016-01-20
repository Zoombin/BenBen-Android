package com.xunao.test.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xunao.test.R;
import com.xunao.test.utils.CommonUtils;

public class UpdateVersionDialog extends AbsDialog {

	private TextView dialog_info_content;
	private Button dialog_info_OK;
	private Button dialog_info_cancle;
	private ImageView dialog_iv_cancle;

	public UpdateVersionDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public UpdateVersionDialog(Context context, int theme) {
		super(context, theme);
	}

	public UpdateVersionDialog(Context context) {
		super(context, R.style.MyDialog1);
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
				CommonUtils.startUpdateAPK(context, url, pBar, handler);
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

	Context context;
	String url;
	ProgressDialog pBar;
	Handler handler;

	public void init(Context context, String url, ProgressDialog pBar,
			Handler handler) {
		this.context = context;
		this.url = url;
		this.pBar = pBar;
		this.handler = handler;
	}


}
