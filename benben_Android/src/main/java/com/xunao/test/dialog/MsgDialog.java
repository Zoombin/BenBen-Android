package com.xunao.test.dialog;

import com.xunao.test.R;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;

public class MsgDialog extends Dialog {
	private TextView dialog_info_content;
	private TextView dialog_info_content_;
	private Button dialog_info_ok;
	private Button dialog_info_cancle;

	public MsgDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		init();
	}

	protected MsgDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
		init();
	}

	public MsgDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public void init() {
		setContentView(R.layout.msg_dialog);
		initView();
		initData();
		setListener();
	}

	private void initView() {
		dialog_info_content = (TextView) findViewById(R.id.dialog_info_content);
		dialog_info_content_ = (TextView) findViewById(R.id.dialog_info_content_);
		dialog_info_ok = (Button) findViewById(R.id.dialog_info_OK);
		dialog_info_cancle = (Button) findViewById(R.id.dialog_info_cancle);
	}

	private void initData() {
		// TODO Auto-generated method stub

	}

	private void setListener() {
		// TODO Auto-generated method stub

	}

	public void setContent(String content_title, String content,
			String OKString, String cancleString) {
		dialog_info_content.setText(content_title);
		dialog_info_content_.setText(content);
		dialog_info_cancle.setText(cancleString);
		dialog_info_ok.setText(OKString);

	}

	public void setEditContent(String content) {

	}

	public void setOKListener(android.view.View.OnClickListener clickListener) {
		dialog_info_ok.setOnClickListener(clickListener);
	}

	public void setCancleListener(
			android.view.View.OnClickListener clickListener) {
		dialog_info_cancle.setOnClickListener(clickListener);
	}

}
